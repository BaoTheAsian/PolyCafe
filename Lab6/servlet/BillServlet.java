package servlet;

import dao.BillDAO;
import dao.BillDetailDAO;
import entity.Bill;
import entity.BillDetail;
import utils.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/manager/bills")
public class BillServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private BillDAO billDAO;
    private BillDetailDAO billDetailDAO;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAO();
        billDetailDAO = new BillDetailDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "list");

        switch (action) {
            case "detail":
                detail(request, response);
                break;
            case "cancel":
                cancel(request, response);
                break;
            default:
                list(request, response);
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = ParamUtil.getInt(request, "page", 1);

        List<Bill> bills = billDAO.findAllPaged(page, PAGE_SIZE);
        int totalItems = billDAO.countAll();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        request.setAttribute("bills", bills);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/manager/bill/list.jsp")
                .forward(request, response);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        Bill bill = billDAO.findById(id);

        if (bill == null) {
            response.sendRedirect(request.getContextPath() + "/manager/bills");
            return;
        }

        List<BillDetail> details = billDetailDAO.findByBillId(id);
        request.setAttribute("bill", bill);
        request.setAttribute("details", details);

        request.getRequestDispatcher("/WEB-INF/views/manager/bill/detail.jsp")
                .forward(request, response);
    }

    private void cancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        billDAO.updateStatus(id, "cancel");
        request.getSession().setAttribute("message", "Đã hủy hóa đơn!");
        response.sendRedirect(request.getContextPath() + "/manager/bills");
    }
}
