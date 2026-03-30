package filter;

import utils.AuthUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/employee/*", "/manager/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getRequestURI();

        // B2: Kiểm tra đăng nhập
        if (!AuthUtil.isAuthenticated(request)) {
            request.getSession().setAttribute("redirect_uri", uri);
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // B3: Kiểm tra truy cập trang quản lý
        if (uri.contains("/manager/")) {
            // B4: Kiểm tra vai trò quản lý
            if (!AuthUtil.isManager(request)) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
        }

        // Cho phép truy cập
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
