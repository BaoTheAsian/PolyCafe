package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Ensures every request and response uses UTF-8 encoding.
 * Must run BEFORE any servlet calls getParameter(), otherwise
 * setCharacterEncoding() is silently ignored by the container
 * and Vietnamese characters get corrupted (mojibake).
 */
@WebFilter("/*")
public class Utf8Filter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Force UTF-8 BEFORE any getParameter() call so the container
        // decodes POST body bytes correctly.
        request.setCharacterEncoding("UTF-8");
        // Force UTF-8 on the response too; individual JSPs may refine
        // content-type further via their own page directive.
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
