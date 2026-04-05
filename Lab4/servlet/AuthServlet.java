package servlet;

import dao.UserDAO;
import entity.User;
import utils.AuthUtil;
import utils.CsrfUtil;
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
        if ("/auth/logout".equals(request.getServletPath())) {
            AuthUtil.clear(request);
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        // Seed CSRF token into session for the login form
        CsrfUtil.getOrCreateToken(request);
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // CSRF check (even on login — prevents pre-auth CSRF token fixation)
        if (!CsrfUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token");
            return;
        }

        String email    = ParamUtil.getString(request, "email",    "");
        String password = ParamUtil.getString(request, "password", "");

        User user = userDAO.findByEmail(email);

        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            // Regenerate session to prevent session fixation
            request.getSession().invalidate();
            request.getSession(true);
            AuthUtil.setUser(request, user);
            CsrfUtil.getOrCreateToken(request); // fresh token for new session

            String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
            if (redirectUri != null) {
                request.getSession().removeAttribute("redirect_uri");
                response.sendRedirect(redirectUri);
            } else if (user.isManager()) {
                response.sendRedirect(request.getContextPath() + "/manager/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/manager/dashboard");
            }
        } else {
            CsrfUtil.getOrCreateToken(request);
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
