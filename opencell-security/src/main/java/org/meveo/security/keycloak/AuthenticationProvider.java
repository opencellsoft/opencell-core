package org.meveo.security.keycloak;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.meveo.security.KeyCloackConstants;
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
	
	public String getNewUserUrl() {
		String kcUrl = System.getProperty("opencell.keycloak.url");
		String kcRealm = System.getProperty("opencell.keycloak.realm");
		String newUserUrl =  KeycloakUriBuilder.fromUri(kcUrl).path(KeyCloackConstants.NEW_USER_PATH).build(kcRealm).toString();
		log.debug(" newUserUrl = {} ", newUserUrl);
		try {
			return URLDecoder.decode(newUserUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.debug(" Error decoding newUserUrl {} ", newUserUrl, e);
			return newUserUrl + "?faces-redirect=true";
		}
	}


	public String getAuthPath() {
		String kcUrl = System.getProperty("opencell.keycloak.url");
		String kcRealm = System.getProperty("opencell.keycloak.realm");
		String uri = KeycloakUriBuilder.fromUri(kcUrl).path(ServiceUrlConstants.AUTH_PATH).build(kcRealm).toString();
		return uri + "?faces-redirect=true";
	}
}