package org.meveo.security.keycloak;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

@Named
public class AuthenticationProvider {

    @Inject
    private HttpServletRequest httpRequest;

    @Inject
    private Logger log;

    public String logout() {
        try {
            httpRequest.logout();
        } catch (ServletException e) {
            log.error("Failed to logout", e);
        }
        return "indexPage";
    }
}