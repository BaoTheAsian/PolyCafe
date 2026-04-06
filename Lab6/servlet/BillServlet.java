package servlet;

import dao.BillDAO;
import dao.BillDetailDAO;
import dao.UserDAO;
import entity.Bill;
import entity.BillDetail;
import entity.User;
import utils.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/manager/bills")
public class BillServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private BillDAO       billDAO;
    private BillDetailDAO billDetailDAO;
    private UserDAO       userDAO;

    @Override
    public void init() throws ServletException {
        billDAO       = new BillDAO();
        billDetailDAO = new BillDetailDAO();
        userDAO       = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "list");
        switch (action) {
            case "detail": detail(request, response); break;
            case "cancel": cancel(request, response); break;
            default:       list(request, response);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int    page     = Math.max(1, ParamUtil.getInt(request, "page", 1));
        String fromDate = ParamUtil.getString(request, "fromDate", "");
        String toDate   = ParamUtil.getString(request, "toDate",   "");
        String status   = ParamUtil.getString(request, "status",   "");
        String keyword  = ParamUtil.getString(request, "keyword",  "");

        List<Bill> bills = billDAO.searchPaged(fromDate, toDate, status, keyword, page, PAGE_SIZE);
        int totalItems   = billDAO.countSearch(fromDate, toDate, status, keyword);
        int totalPages   = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        Map<Integer, String> staffNames = new HashMap<>();
        for (Bill b : bills) {
            staffNames.computeIfAbsent(b.getUserId(), id -> {
                User u = userDAO.findById(id);
                return u != null ? u.getFullName() : "—";
            });
        }

        request.setAttribute("bills",       bills);
        request.setAttribute("staffNames",  staffNames);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages",  totalPages);
        request.setAttribute("fromDate",    fromDate);
        request.setAttribute("toDate",      toDate);
        request.setAttribute("status",      status);
        request.setAttribute("keyword",     keyword);
        request.getRequestDispatcher("/WEB-INF/views/manager/bill/list.jsp").forward(request, response);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        if (id <= 0) {
            request.getSession().setAttribute("error", "ID hóa đơn không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }
        Bill bill = billDAO.findById(id);
        if (bill == null) {
            request.getSession().setAttribute("error", "Không tìm thấy hóa đơn #" + id + "!");
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }

        List<BillDetail> details = billDetailDAO.findByBillId(id);
        User staffUser   = userDAO.findById(bill.getUserId());
        String staffName = staffUser != null ? staffUser.getFullName() : "—";

        request.setAttribute("bill",      bill);
        request.setAttribute("details",   details);
        request.setAttribute("staffName", staffName);
        request.getRequestDispatcher("/WEB-INF/views/manager/bill/detail.jsp").forward(request, response);
    }

    private void cancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        if (id <= 0) {
            request.getSession().setAttribute("error", "ID hóa đơn không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }
        Bill bill = billDAO.findById(id);
        if (bill == null) {
            request.getSession().setAttribute("error", "Không tìm thấy hóa đơn!");
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }
        if ("finish".equals(bill.getStatus())) {
            request.getSession().setAttribute("error", "Không thể hủy hóa đơn đã hoàn thành!");
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }
        if ("cancel".equals(bill.getStatus())) {
            request.getSession().setAttribute("error", "Hóa đơn này đã được hủy trước đó!");
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }
        int result = billDAO.updateStatus(id, "cancel");
        if (result <= 0) {
            request.getSession().setAttribute("error", "Hủy hóa đơn thất bại, vui lòng thử lại!");
        } else {
            request.getSession().setAttribute("message", "Đã hủy hóa đơn #" + id + "!");
        }
        response.sendRedirect(request.getContextPath() + "/manager/bills");
    }
}
