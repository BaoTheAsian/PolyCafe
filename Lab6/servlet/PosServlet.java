package servlet;

import dao.BillDAO;
import dao.BillDetailDAO;
import dao.CardDAO;
import dao.DrinkDAO;
import entity.Bill;
import entity.BillDetail;
import entity.Card;
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
        request.setAttribute("drinks", drinkDAO.findBySql("SELECT * FROM drinks WHERE active = 1"));
        request.setAttribute("cards",  cardDAO.findAll());

        int billId = ParamUtil.getInt(request, "billId", 0);
        if (billId == 0) {
            Bill waiting = billDAO.findWaitingBill(user.getId());
            if (waiting != null) billId = waiting.getId();
        }

        if (billId > 0) {
            Bill bill = billDAO.findById(billId);
            // findByBillId() does a JOIN — drink names are populated
            List<BillDetail> details = billDetailDAO.findByBillId(billId);
            request.setAttribute("bill",    bill);
            request.setAttribute("details", details);
        }

        request.getRequestDispatcher("/WEB-INF/views/employee/pos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        User user = AuthUtil.getUser(request);

        switch (path) {
            case "/employee/pos/init":           initOrder(request, response, user);  break;
            case "/employee/pos/update-quantity": updateQuantity(request, response);  break;
            case "/employee/pos/checkout":        checkout(request, response);        break;
            case "/employee/pos/cancel":          cancel(request, response);          break;
            default: response.sendRedirect(request.getContextPath() + "/employee/pos");
        }
    }

    /** Add a drink to the current (or new) order. */
    private void initOrder(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int    drinkId = ParamUtil.getInt(request, "drinkId", 0);
        double price   = Double.parseDouble(ParamUtil.getString(request, "price", "0"));
        String size    = ParamUtil.getString(request, "size", "M");
        String note    = ParamUtil.getString(request, "note", "");

        Bill bill = billDAO.findWaitingBill(user.getId());
        int billId = (bill == null) ? billDAO.createAndGetId(user.getId()) : bill.getId();

        billDetailDAO.addDrinkToBill(billId, drinkId, price, size, note);
        billDAO.updateTotal(billId);

        response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
    }

    /** Increase / decrease / remove a line item. */
    private void updateQuantity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int    detailId = ParamUtil.getInt(request, "detailId", 0);
        int    billId   = ParamUtil.getInt(request, "billId",   0);
        String action   = ParamUtil.getString(request, "quantityAction", "");

        BillDetail detail = billDetailDAO.findById(detailId);
        if (detail != null) {
            switch (action) {
                case "increase": billDetailDAO.updateQuantity(detailId, detail.getQuantity() + 1); break;
                case "decrease": billDetailDAO.updateQuantity(detailId, detail.getQuantity() - 1); break;
                case "remove":   billDetailDAO.delete(detailId); break;
            }
            billDAO.updateTotal(billId);
        }
        response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
    }

    /** Finalise payment — record payment method, apply loyalty card. */
    private void checkout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int    billId        = ParamUtil.getInt(request, "billId", 0);
        String paymentMethod = ParamUtil.getString(request, "paymentMethod", "cash");
        String cardCode      = ParamUtil.getString(request, "cardCode", "");

        // Attach loyalty card if code provided
        if (!cardCode.isEmpty()) {
            Card card = cardDAO.findByCode(cardCode);
            if (card != null && card.isStatus()) {
                Bill b = billDAO.findById(billId);
                if (b != null) {
                    b.setCardId(card.getId());
                    billDAO.update(b);
                }
            }
        }

        billDAO.updateStatusAndPayment(billId, "finish", paymentMethod);
        request.getSession().setAttribute("message", "Thanh toán thành công!");
        request.getSession().setAttribute("lastBillId", billId);
        response.sendRedirect(request.getContextPath() + "/employee/pos");
    }

    /** Cancel the current order. */
    private void cancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int billId = ParamUtil.getInt(request, "billId", 0);
        billDAO.updateStatus(billId, "cancel");
        request.getSession().setAttribute("message", "Đã hủy đơn hàng!");
        response.sendRedirect(request.getContextPath() + "/employee/pos");
    }
}
