package org.meveo.api.restful.filter;


import jakarta.servlet..*;
import jakarta.servlet..annotation.WebFilter;
import jakarta.servlet..http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "RestCorsResponseFilter", urlPatterns = { "/api/rest/v1/*" })
public class RestCorsResponseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
