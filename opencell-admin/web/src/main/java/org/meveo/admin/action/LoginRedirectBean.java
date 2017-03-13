package org.meveo.admin.action;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Named
public class LoginRedirectBean {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private Logger log;

    public void redirect() throws IOException {
        String redirectUrl = "login.jsf";

        try {
            if (currentUser.hasRole("marketingCatalogManager") || currentUser.hasRole("marketingCatalogVisualization")) {
                redirectUrl = "mm_index.jsf";
            } else {
                redirectUrl = "home.jsf";
            }
        } catch (NullPointerException e) {
            log.error("no role?={}", e.getMessage());
        }

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(redirectUrl);
    }
}