package org.meveo.admin.action;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Identity;
import org.meveo.model.admin.User;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Named
public class LoginRedirectBean {

	@Inject
	private Identity identity;

	@Inject
	private Logger log;

	public void redirect() throws IOException {
		String redirectUrl = "login.jsf";
		if (identity.isLoggedIn()) {
			try {
				User user = ((MeveoUser) identity.getUser()).getUser();
				if (user.hasPermission("marketing", "marketingCatalogManager") || user.hasPermission("marketing", "marketingCatalogVisualization")) {
					redirectUrl = "mm_index.jsf";				
				} else {					
					redirectUrl = "home.jsf";
				}
			} catch (NullPointerException e) {
				log.error("no role?={}", e.getMessage());
			}
		} else {
			log.error("isNotLoggedIn, redirect to login.");
		}

		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		context.redirect(redirectUrl);
	}
}
