package servlet;

import dao.BillDAO;
import dao.BillDetailDAO;
import dao.CardDAO;
import dao.DrinkDAO;
import entity.Bill;
import entity.BillDetail;
import entity.Card;
import entity.Drink;
import entity.User;
import utils.AuthUtil;
import utils.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/employee/pos",
        "/employee/pos/init",
        "/employee/pos/update-quantity",
        "/employee/pos/checkout",
        "/employee/pos/cancel"
})
public class PosServlet extends HttpServlet {

    private BillDAO       billDAO;
    private BillDetailDAO billDetailDAO;
    private DrinkDAO      drinkDAO;
    private CardDAO       cardDAO;

    @Override
    public void init() throws ServletException {
        billDAO       = new BillDAO();
        billDetailDAO = new BillDetailDAO();
        drinkDAO      = new DrinkDAO();
        cardDAO       = new CardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = AuthUtil.getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        request.setAttribute("drinks", drinkDAO.findBySql("SELECT * FROM drinks WHERE active = 1"));
        request.setAttribute("cards",  cardDAO.findAll());

        int billId = ParamUtil.getInt(request, "billId", 0);
        if (billId == 0) {
            Bill waiting = billDAO.findWaitingBill(user.getId());
            if (waiting != null) billId = waiting.getId();
        }

        if (billId > 0) {
            Bill bill = billDAO.findById(billId);
            if (bill != null && bill.getUserId() == user.getId()) {
                // Only show the bill if it belongs to this user
                List<BillDetail> details = billDetailDAO.findByBillId(billId);
                request.setAttribute("bill",    bill);
                request.setAttribute("details", details);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/employee/pos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = AuthUtil.getUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        String path = request.getServletPath();
        switch (path) {
            case "/employee/pos/init":            initOrder(request, response, user);  break;
            case "/employee/pos/update-quantity": updateQuantity(request, response, user); break;
            case "/employee/pos/checkout":        checkout(request, response, user);   break;
            case "/employee/pos/cancel":          cancel(request, response, user);     break;
            default: response.sendRedirect(request.getContextPath() + "/employee/pos");
        }
    }

    /** Add a drink to the current (or new) order. */
    private void initOrder(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int drinkId = ParamUtil.getInt(request, "drinkId", 0);
        if (drinkId <= 0) {
            request.getSession().setAttribute("error", "Đồ uống không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        // Verify the drink exists and is active
        Drink drink = drinkDAO.findById(drinkId);
        if (drink == null || !drink.isActive()) {
            request.getSession().setAttribute("error", "Đồ uống không tồn tại hoặc đã ngừng bán!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        double price = 0;
        try {
            price = Double.parseDouble(ParamUtil.getString(request, "price", "0"));
        } catch (NumberFormatException e) {
            price = drink.getPrice();
        }
        String size = ParamUtil.getString(request, "size", "M");
        String note = ParamUtil.getString(request, "note", "");

        Bill bill  = billDAO.findWaitingBill(user.getId());
        int billId = (bill == null) ? billDAO.createAndGetId(user.getId()) : bill.getId();

        if (billId <= 0) {
            request.getSession().setAttribute("error", "Không thể tạo đơn hàng, vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        billDetailDAO.addDrinkToBill(billId, drinkId, price, size, note);
        billDAO.updateTotal(billId);
        response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
    }

    /** Increase / decrease / remove a line item. */
    private void updateQuantity(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int    detailId = ParamUtil.getInt(request, "detailId", 0);
        int    billId   = ParamUtil.getInt(request, "billId",   0);
        String action   = ParamUtil.getString(request, "quantityAction", "");

        if (billId <= 0 || detailId <= 0) {
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        // Security: confirm bill belongs to this user
        Bill bill = billDAO.findById(billId);
        if (bill == null || bill.getUserId() != user.getId()) {
            request.getSession().setAttribute("error", "Không có quyền chỉnh sửa đơn hàng này!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        BillDetail detail = billDetailDAO.findById(detailId);
        if (detail != null) {
            switch (action) {
                case "increase":
                    billDetailDAO.updateQuantity(detailId, detail.getQuantity() + 1);
                    break;
                case "decrease":
                    if (detail.getQuantity() <= 1) {
                        billDetailDAO.delete(detailId);
                    } else {
                        billDetailDAO.updateQuantity(detailId, detail.getQuantity() - 1);
                    }
                    break;
                case "remove":
                    billDetailDAO.delete(detailId);
                    break;
            }
            billDAO.updateTotal(billId);
        }
        response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
    }

    /** Finalise payment — record payment method, apply loyalty card. */
    private void checkout(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int billId = ParamUtil.getInt(request, "billId", 0);
        if (billId <= 0) {
            request.getSession().setAttribute("error", "ID đơn hàng không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        Bill bill = billDAO.findById(billId);
        if (bill == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đơn hàng!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }
        if (bill.getUserId() != user.getId()) {
            request.getSession().setAttribute("error", "Không có quyền thanh toán đơn hàng này!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }
        if (!"waiting".equals(bill.getStatus())) {
            request.getSession().setAttribute("error", "Đơn hàng này không thể thanh toán (trạng thái: " + bill.getStatus() + ")!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        // Check order has at least one item
        List<BillDetail> details = billDetailDAO.findByBillId(billId);
        if (details == null || details.isEmpty()) {
            request.getSession().setAttribute("error", "Không thể thanh toán đơn hàng trống!");
            response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
            return;
        }

        String paymentMethod = ParamUtil.getString(request, "paymentMethod", "cash");
        String cardCode      = ParamUtil.getString(request, "cardCode", "");

        // Attach loyalty card if code provided
        if (!cardCode.isEmpty()) {
            try {
                Card card = cardDAO.findByCode(cardCode);
                if (card != null && card.isStatus()) {
                    bill.setCardId(card.getId());
                    billDAO.update(bill);
                } else if (card != null) {
                    request.getSession().setAttribute("cardWarning", "Thẻ thành viên không hoạt động, thanh toán không áp dụng thẻ.");
                } else {
                    request.getSession().setAttribute("cardWarning", "Mã thẻ không tồn tại, thanh toán không áp dụng thẻ.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Card lookup failed — continue checkout without it
            }
        }

        int result = billDAO.updateStatusAndPayment(billId, "finish", paymentMethod);
        if (result <= 0) {
            request.getSession().setAttribute("error", "Thanh toán thất bại, vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
            return;
        }

        request.getSession().setAttribute("message",    "Thanh toán thành công!");
        request.getSession().setAttribute("lastBillId", billId);
        response.sendRedirect(request.getContextPath() + "/employee/pos");
    }

    /** Cancel the current order. */
    private void cancel(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int billId = ParamUtil.getInt(request, "billId", 0);
        if (billId <= 0) {
            request.getSession().setAttribute("error", "ID đơn hàng không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        Bill bill = billDAO.findById(billId);
        if (bill == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đơn hàng!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }
        if (bill.getUserId() != user.getId()) {
            request.getSession().setAttribute("error", "Không có quyền hủy đơn hàng này!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }
        if ("finish".equals(bill.getStatus())) {
            request.getSession().setAttribute("error", "Không thể hủy đơn hàng đã thanh toán!");
            response.sendRedirect(request.getContextPath() + "/employee/pos");
            return;
        }

        int result = billDAO.updateStatus(billId, "cancel");
        if (result <= 0) {
            request.getSession().setAttribute("error", "Hủy đơn thất bại, vui lòng thử lại!");
        } else {
            request.getSession().setAttribute("message", "Đã hủy đơn hàng!");
        }
        response.sendRedirect(request.getContextPath() + "/employee/pos");
    }
}
