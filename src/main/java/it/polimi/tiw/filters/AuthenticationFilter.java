package it.polimi.tiw.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import it.polimi.tiw.beans.User;

public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
        chain.doFilter(request, response);
    }
}