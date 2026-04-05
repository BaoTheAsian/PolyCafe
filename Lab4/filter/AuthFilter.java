package filter;

import utils.AuthUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter(urlPatterns = {"/employee/*", "/manager/*"})
public class AuthFilter implements Filter {

    /**
     * Manager-only paths where employees are blocked.
     * /manager/dashboard is intentionally NOT in this list —
     * both roles can view the dashboard (employee sees a simplified version).
     */
    private static final Set<String> MANAGER_ONLY_PREFIXES = Set.of(
            "/manager/categories",
            "/manager/drinks",
            "/manager/staffs",
            "/manager/bills",
            "/manager/statistics"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();

        // Must be logged in
        if (!AuthUtil.isAuthenticated(request)) {
            request.getSession().setAttribute("redirect_uri", uri);
            response.sendRedirect(ctx + "/auth/login");
            return;
        }

        // Manager-only pages — block employees
        if (uri.contains("/manager/")) {
            boolean restricted = MANAGER_ONLY_PREFIXES.stream()
                    .anyMatch(p -> uri.contains(p));
            if (restricted && !AuthUtil.isManager(request)) {
                response.sendRedirect(ctx + "/employee/pos");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
