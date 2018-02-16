package org.meveo.security.keycloak;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class AuthenticationProvider {

    @Inject
    private HttpServletRequest httpRequest;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public String logout() {
        try {
            httpRequest.logout();
        } catch (ServletException e) {
            log.error("Failed to logout", e);
        }
        return "indexPage";
    }
    
	public String getAccountUrl() {
		String kcUrl = System.getProperty("opencell.keycloak.url");
		String kcRealm = System.getProperty("opencell.keycloak.realm");
		String acctUri = KeycloakUriBuilder.fromUri(kcUrl).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH).build(kcRealm)
				.toString();
		return acctUri + "?faces-redirect=true";
	}

	public String getAuthPath() {
		String kcUrl = System.getProperty("opencell.keycloak.url");
		String kcRealm = System.getProperty("opencell.keycloak.realm");
		String uri = KeycloakUriBuilder.fromUri(kcUrl).path(ServiceUrlConstants.AUTH_PATH).build(kcRealm).toString();
		return uri + "?faces-redirect=true";
	}
}