package org.meveo.apiv2.generic;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = { "/*" })
public class AuthenticationFilter implements Filter {

    static ResteasyClient httpClient;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        httpClient = new ResteasyClientBuilder().build();
        if ( servletRequest != null ) {
            if ( servletRequest instanceof HttpServletRequest) {
                HttpServletRequest aHttpServletRequest = (HttpServletRequest) servletRequest;
                if ( aHttpServletRequest.getHeader("Authorization") != null ) {
                    String base64Credentials = aHttpServletRequest.getHeader("Authorization").substring("Basic".length()).trim();
                    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                    // credentials = username:password
                    final String[] values = credentials.split(":", 2);

                    BasicAuthentication basicAuthentication = new BasicAuthentication( values[0], values[1] );
                    httpClient.register(basicAuthentication);
                }

                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
