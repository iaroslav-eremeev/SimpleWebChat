package filter;

import DAO.DAO;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*@WebFilter(urlPatterns = { "/*" }, asyncSupported = true)
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("hash")) {
                    value = cookie.getValue();
                }
            }
        }

        System.out.println(request.getRequestURI());

        //URL request/redirect to the login Servlet
        String loginURI = request.getContextPath() + "/login";
        String registerURI = request.getContextPath() + "/registration";
        //If a session has been previously created
        boolean loginRequest = request.getRequestURI().contains(loginURI);
        boolean registerRequest = request.getRequestURI().contains(registerURI);
        //If the request came from the login page or the session is not empty, allow to proceed
        //Otherwise, redirect to the login page
        if (request.getRequestURI().endsWith("js") || loginRequest || registerRequest
                || value != null && DAO.getObjectByParam("hash", value, User.class) != null) {
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect(loginURI + ".html");
        }
    }

    @Override
    public void destroy() {
    }
}*/
