/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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