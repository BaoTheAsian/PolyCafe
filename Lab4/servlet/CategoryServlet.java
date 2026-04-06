package servlet;

import dao.CategoryDAO;
import entity.Category;
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

@WebServlet(urlPatterns = "/manager/categories")
public class CategoryServlet extends HttpServlet {

    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "list");

        switch (action) {
            case "edit":
                edit(request, response);
                break;
            case "delete":
                delete(request, response);
                break;
            case "create":
                request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                        .forward(request, response);
                break;
            default:
                list(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "");

        switch (action) {
            case "create":
                create(request, response);
                break;
            case "update":
                update(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manager/categories");
                break;
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryDAO.findAll();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/manager/category/list.jsp")
                .forward(request, response);
    }

    private void edit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        Category category = categoryDAO.findById(id);

        if (category == null) {
            response.sendRedirect(request.getContextPath() + "/manager/categories");
            return;
        }

        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                .forward(request, response);
    }

    private void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy dữ liệu từ form
        String name = ParamUtil.getString(request, "name", "");
        boolean active = request.getParameter("active") != null;

        // Validate
        Map<String, String> errors = new HashMap<>();
        if (name.isEmpty()) {
            errors.put("name", "Tên loại đồ uống không được để trống!");
        }

        if (!errors.isEmpty()) {
            Category category = new Category(0, name, active);
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                    .forward(request, response);
            return;
        }

        // Lưu vào database
        Category category = new Category(0, name, active);
        int result = categoryDAO.create(category);
        if (result == -1) {
            errors.put("name", "Tên loại đồ uống \"" + name + "\" đã tồn tại!");
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                    .forward(request, response);
            return;
        }
        if (result <= 0) {
            errors.put("name", "Lỗi hệ thống, vui lòng thử lại!");
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                    .forward(request, response);
            return;
        }
        request.getSession().setAttribute("message", "Thêm loại đồ uống thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/categories");
    }

    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        String name = ParamUtil.getString(request, "name", "");
        boolean active = request.getParameter("active") != null;

        Map<String, String> errors = new HashMap<>();
        if (name.isEmpty()) {
            errors.put("name", "Tên loại đồ uống không được để trống!");
        }

        if (!errors.isEmpty()) {
            Category category = new Category(id, name, active);
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                    .forward(request, response);
            return;
        }

        Category category = new Category(id, name, active);
        int result = categoryDAO.update(category);
        if (result == -1) {
            errors.put("name", "Tên loại đồ uống \"" + name + "\" đã tồn tại!");
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                    .forward(request, response);
            return;
        }
        if (result <= 0) {
            errors.put("name", "Lỗi hệ thống, vui lòng thử lại!");
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/category/form.jsp")
                    .forward(request, response);
            return;
        }
        request.getSession().setAttribute("message", "Cập nhật loại đồ uống thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/categories");
    }

    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        if (id <= 0) {
            request.getSession().setAttribute("error", "ID không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/manager/categories");
            return;
        }
        int result = categoryDAO.delete(id);
        if (result == -2) {
            request.getSession().setAttribute("error", "Không thể xóa loại đồ uống vì có đồ uống thuộc loại này!");
        } else if (result <= 0) {
            request.getSession().setAttribute("error", "Xóa thất bại, vui lòng thử lại!");
        } else {
            request.getSession().setAttribute("message", "Xóa loại đồ uống thành công!");
        }
        response.sendRedirect(request.getContextPath() + "/manager/categories");
    }
}
