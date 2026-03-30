package servlet;

import dao.UserDAO;
import entity.User;
import utils.AuthUtil;
import utils.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/auth/login", "/auth/logout"})
public class AuthServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/auth/logout")) {
            AuthUtil.clear(request);
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Hiển thị trang đăng nhập
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = ParamUtil.getString(request, "email", "");
        String password = ParamUtil.getString(request, "password", "");

        // Tìm user theo email
        User user = userDAO.findByEmail(email);

        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            // Đăng nhập thành công - lưu session
            AuthUtil.setUser(request, user);

            // Redirect về trang trước đó hoặc trang mặc định
            String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
            if (redirectUri != null) {
                request.getSession().removeAttribute("redirect_uri");
                response.sendRedirect(redirectUri);
            } else if (user.isRole()) {
                response.sendRedirect(request.getContextPath() + "/manager/categories");
            } else {
                response.sendRedirect(request.getContextPath() + "/employee/pos");
            }
        } else {
            // Đăng nhập thất bại
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
