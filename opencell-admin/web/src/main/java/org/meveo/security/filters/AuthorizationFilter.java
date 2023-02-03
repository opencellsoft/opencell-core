package org.meveo.security.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.meveo.security.keycloak.CurrentUserProvider;

@WebFilter(urlPatterns = "/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String url = httpRequest.getRequestURL().toString();
        String contextPath = httpRequest.getContextPath();

        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        boolean[] isPermited = CurrentUserProvider.isLinkAccesible(httpRequest, httpRequest.getMethod(), url);

        if (!isPermited[0]) {
            ((HttpServletResponse) response).setStatus(403);
            if (!url.startsWith("/api")) {
                String page = "/errors/403.jsf";
                RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(page);
                dispatcher.forward(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}