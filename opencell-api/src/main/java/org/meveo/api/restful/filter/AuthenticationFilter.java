package org.meveo.api.restful.filter;

import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = { "/api/rest/v1/*" })
public class AuthenticationFilter implements Filter {

    public static ResteasyClient httpClient;

    private static final String BASIC_AUTH = "Basic";
    private static final String OAUTH2 = "Bearer";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        httpClient = new ResteasyClientBuilderImpl().build();
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        if ( httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION) != null ) {
            final String authorizationHeaderValue = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if ( authorizationHeaderValue.startsWith(BASIC_AUTH) ) {
                String base64Credentials = authorizationHeaderValue.substring(BASIC_AUTH.length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                // credentials = username:password
                final String[] values = credentials.split(":", 2);

                BasicAuthentication basicAuthentication = new BasicAuthentication( values[0], values[1] );
                httpClient.register(basicAuthentication);
            }
            else if ( authorizationHeaderValue.startsWith(OAUTH2) ) {
                String token = authorizationHeaderValue.substring(OAUTH2.length()).trim();

                httpClient.register((ClientRequestFilter) requestContext ->
                        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, OAUTH2 + " " + token));
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
