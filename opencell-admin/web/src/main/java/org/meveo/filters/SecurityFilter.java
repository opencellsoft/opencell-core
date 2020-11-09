package org.meveo.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

public class SecurityFilter implements Filter {
    
    private static final String POLICY = "frame-src 'self'";
    private static final String XFRAME_SAMEORIGIN = "SAMEORIGIN";
    private static final String XCONTENT_NOSNIFF = "nosniff";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String SAME_SITE_ATTRIBUTE_VALUES = ";HttpOnly;Secure;SameSite=None";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        
        res.setHeader("Content-Security-Policy", SecurityFilter.POLICY);
        res.setHeader("X-Frame-Options", SecurityFilter.XFRAME_SAMEORIGIN);
        res.setHeader("X-Content-Type-Options", SecurityFilter.XCONTENT_NOSNIFF);
        
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            List<Cookie> cookieList = Arrays.asList(cookies);
            Cookie sessionCookie = cookieList.stream().filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName())).findFirst().orElse(null);
            if (sessionCookie != null) {
                res.setHeader(HttpHeaders.SET_COOKIE, sessionCookie.getName() + "=" + sessionCookie.getValue() + SAME_SITE_ATTRIBUTE_VALUES);
            }
        }
        
        chain.doFilter(request, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

}