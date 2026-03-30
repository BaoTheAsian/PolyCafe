package servlet;

import dao.BillDAO;
import dao.BillDetailDAO;
import dao.DrinkDAO;
import entity.Bill;
import entity.BillDetail;
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

    private BillDAO billDAO;
    private BillDetailDAO billDetailDAO;
    private DrinkDAO drinkDAO;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAO();
        billDetailDAO = new BillDetailDAO();
        drinkDAO = new DrinkDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang POS
        User user = AuthUtil.getUser(request);
        request.setAttribute("drinks", drinkDAO.findBySql("SELECT * FROM drinks WHERE active = 1"));

        // Nếu có billId → hiển thị chi tiết đơn hàng
        int billId = ParamUtil.getInt(request, "billId", 0);
        if (billId == 0) {
            // Tìm đơn hàng đang chờ
            Bill waitingBill = billDAO.findWaitingBill(user.getId());
            if (waitingBill != null) billId = waitingBill.getId();
        }

        if (billId > 0) {
            Bill bill = billDAO.findById(billId);
            List<BillDetail> details = billDetailDAO.findByBillId(billId);
            request.setAttribute("bill", bill);
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
            case "/employee/pos/init":
                initOrder(request, response, user);
                break;
            case "/employee/pos/update-quantity":
                updateQuantity(request, response);
                break;
            case "/employee/pos/checkout":
                checkout(request, response);
                break;
            case "/employee/pos/cancel":
                cancel(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/employee/pos");
        }
    }

    /**
     * POST /employee/pos/init - Thêm thức uống vào đơn
     */
    private void initOrder(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int drinkId = ParamUtil.getInt(request, "drinkId", 0);
        double price = Double.parseDouble(ParamUtil.getString(request, "price", "0"));

        // Tìm đơn hàng đang chờ
        Bill bill = billDAO.findWaitingBill(user.getId());
        int billId;

        if (bill == null) {
            // Tạo đơn hàng mới
            billId = billDAO.createAndGetId(user.getId());
        } else {
            billId = bill.getId();
        }

        // Thêm thức uống vào đơn
        billDetailDAO.addDrinkToBill(billId, drinkId, price);

        // Cập nhật tổng tiền
        billDAO.updateTotal(billId);

        response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
    }

    /**
     * POST /employee/pos/update-quantity - Tăng/Giảm/Xóa số lượng
     */
    private void updateQuantity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int detailId = ParamUtil.getInt(request, "detailId", 0);
        int billId = ParamUtil.getInt(request, "billId", 0);
        String action = ParamUtil.getString(request, "quantityAction", "");

        BillDetail detail = billDetailDAO.findById(detailId);
        if (detail != null) {
            switch (action) {
                case "increase":
                    billDetailDAO.updateQuantity(detailId, detail.getQuantity() + 1);
                    break;
                case "decrease":
                    billDetailDAO.updateQuantity(detailId, detail.getQuantity() - 1);
                    break;
                case "remove":
                    billDetailDAO.delete(detailId);
                    break;
            }
            billDAO.updateTotal(billId);
        }

        response.sendRedirect(request.getContextPath() + "/employee/pos?billId=" + billId);
    }

    /**
     * POST /employee/pos/checkout - Thanh toán đơn hàng
     */
    private void checkout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int billId = ParamUtil.getInt(request, "billId", 0);
        billDAO.updateStatus(billId, "finish");
        request.getSession().setAttribute("message", "Thanh toán thành công!");
        response.sendRedirect(request.getContextPath() + "/employee/pos");
    }

    /**
     * POST /employee/pos/cancel - Hủy đơn hàng
     */
    private void cancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int billId = ParamUtil.getInt(request, "billId", 0);
        billDAO.updateStatus(billId, "cancel");
        request.getSession().setAttribute("message", "Đã hủy đơn hàng!");
        response.sendRedirect(request.getContextPath() + "/employee/pos");
    }
}
