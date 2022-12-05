package org.meveo.filters;

import java.io.IOException;

import jakarta.servlet..Filter;
import jakarta.servlet..FilterChain;
import jakarta.servlet..FilterConfig;
import jakarta.servlet..ServletException;
import jakarta.servlet..ServletRequest;
import jakarta.servlet..ServletResponse;
import jakarta.servlet..http.Cookie;
import jakarta.servlet..http.HttpServletRequest;
import jakarta.servlet..http.HttpServletResponse;

public class SecurityFilter implements Filter {
    
    private static final String POLICY = "frame-src 'self'";
    private static final String XFRAME_SAMEORIGIN = "SAMEORIGIN";
    private static final String XCONTENT_NOSNIFF = "nosniff";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSION_ID = "JSESSIONID=";
    private static final String HTTP_ONLY = ";Secure;HttpOnly";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        
        res.setHeader("Content-Security-Policy", SecurityFilter.POLICY);
        res.setHeader("X-Frame-Options", SecurityFilter.XFRAME_SAMEORIGIN);
        res.setHeader("X-Content-Type-Options", SecurityFilter.XCONTENT_NOSNIFF);
        
        /*
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for(Cookie cookie :cookies){
                if("JSESSIONID".equals(cookie.getName())) {
                    cookie.setValue(req.getSession().getId() + HTTP_ONLY);
                    cookie.setSecure(true);
                }
            }
        }
        res.setHeader(SET_COOKIE, JSESSION_ID + req.getSession().getId() + HTTP_ONLY);
        */
        chain.doFilter(request, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

}