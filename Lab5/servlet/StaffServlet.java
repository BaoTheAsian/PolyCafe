package servlet;

import dao.UserDAO;
import entity.User;
import utils.EmailUtil;
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
import java.util.Random;

@WebServlet(urlPatterns = "/manager/staffs")
public class StaffServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException { userDAO = new UserDAO(); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "list");
        switch (action) {
            case "create":          request.getRequestDispatcher("/WEB-INF/views/manager/staff/form.jsp").forward(request, response); break;
            case "edit":            editForm(request, response);      break;
            case "toggle-active":   toggleActive(request, response);  break;
            case "reset-password":  resetPassword(request, response); break;
            default:                list(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "");
        switch (action) {
            case "create": create(request, response); break;
            case "update": update(request, response); break;
            default: response.sendRedirect(request.getContextPath() + "/manager/staffs");
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = ParamUtil.getString(request, "keyword", "");
        String email   = ParamUtil.getString(request, "email",   "");
        String role    = ParamUtil.getString(request, "role",    "");
        int    active  = ParamUtil.getInt(request, "active", -1);
        int    page    = ParamUtil.getInt(request, "page", 1);

        List<User> staffs  = userDAO.searchStaff(keyword, email, role, active, page, PAGE_SIZE);
        int totalItems     = userDAO.countStaff(keyword, email, role, active);
        int totalPages     = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        request.setAttribute("staffs",      staffs);
        request.setAttribute("keyword",     keyword);
        request.setAttribute("email",       email);
        request.setAttribute("role",        role);
        request.setAttribute("active",      active);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages",  totalPages);

        request.getRequestDispatcher("/WEB-INF/views/manager/staff/list.jsp").forward(request, response);
    }

    private void editForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        User user = userDAO.findById(id);
        if (user == null) { response.sendRedirect(request.getContextPath() + "/manager/staffs"); return; }
        request.setAttribute("staff", user);
        request.getRequestDispatcher("/WEB-INF/views/manager/staff/form.jsp").forward(request, response);
    }

    private void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getUserFromForm(request);
        Map<String, String> errors = validate(user, true);
        if (!errors.isEmpty()) {
            request.setAttribute("staff", user); request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/staff/form.jsp").forward(request, response);
            return;
        }
        userDAO.create(user);
        request.getSession().setAttribute("message", "Thêm nhân viên thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/staffs");
    }

    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        User user = getUserFromForm(request);
        user.setId(id);
        String newPassword = ParamUtil.getString(request, "password", "");
        if (newPassword.isEmpty()) {
            User old = userDAO.findById(id);
            if (old != null) user.setPassword(old.getPassword());
        }
        Map<String, String> errors = validate(user, false);
        if (!errors.isEmpty()) {
            request.setAttribute("staff", user); request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/manager/staff/form.jsp").forward(request, response);
            return;
        }
        userDAO.update(user);
        request.getSession().setAttribute("message", "Cập nhật nhân viên thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/staffs");
    }

    private void toggleActive(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        User user = userDAO.findById(id);
        if (user != null) userDAO.updateActive(id, !user.isActive());
        request.getSession().setAttribute("message", "Cập nhật trạng thái thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/staffs");
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = ParamUtil.getInt(request, "id", 0);
        User user = userDAO.findById(id);
        if (user != null) {
            String newPassword = randomPassword(8);
            userDAO.updatePassword(id, newPassword);
            String body = "Xin chào " + user.getFullName() + ",\n\n"
                    + "Mật khẩu mới của bạn là: " + newPassword + "\n\n"
                    + "Vui lòng đăng nhập và đổi mật khẩu.";
            EmailUtil.send(user.getEmail(), "PolyCoffee - Mật khẩu mới", body);
            request.getSession().setAttribute("message",
                    "Đã cấp mật khẩu mới và gửi email cho " + user.getEmail());
        }
        response.sendRedirect(request.getContextPath() + "/manager/staffs");
    }

    private User getUserFromForm(HttpServletRequest request) {
        User u = new User();
        u.setEmail(    ParamUtil.getString(request, "email",    ""));
        u.setPassword( ParamUtil.getString(request, "password", ""));
        u.setFullName( ParamUtil.getString(request, "fullName", ""));
        u.setPhone(    ParamUtil.getString(request, "phone",    ""));
        u.setRole(     ParamUtil.getString(request, "role",     "staff"));
        u.setActive(   request.getParameter("active") != null);
        return u;
    }

    private Map<String, String> validate(User user, boolean isNew) {
        Map<String, String> errors = new HashMap<>();
        if (user.getFullName().isEmpty())              errors.put("fullName", "Họ tên không được để trống!");
        if (user.getEmail().isEmpty())                 errors.put("email",    "Email không được để trống!");
        if (isNew && user.getPassword().isEmpty())     errors.put("password", "Mật khẩu không được để trống!");
        if (!user.getEmail().isEmpty()) {
            User existing = userDAO.findByEmail(user.getEmail());
            if (existing != null && existing.getId() != user.getId())
                errors.put("email", "Email đã tồn tại!");
        }
        return errors;
    }

    private String randomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }
}
