package org.meveo.api.restful.filter;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import jakarta.servlet..Filter;
import jakarta.servlet..FilterChain;
import jakarta.servlet..ServletException;
import jakarta.servlet..ServletRequest;
import jakarta.servlet..ServletResponse;
import jakarta.servlet..annotation.WebFilter;
import jakarta.servlet..http.HttpServletRequest;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
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
        httpClient = new ResteasyClientBuilder().build();
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
