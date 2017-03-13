package org.meveo.security.keycloak;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;

@Named
public class AuthenticationProvider {

    @Inject
    private HttpServletRequest httpRequest;

    public String logout() throws ServletException {

        return KeycloakUriBuilder.fromUri("/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
            .queryParam("redirect_uri", httpRequest.getRequestURL().substring(0, httpRequest.getRequestURL().indexOf(httpRequest.getContextPath())) + httpRequest.getContextPath())
            .build("master").toString();
    }
}