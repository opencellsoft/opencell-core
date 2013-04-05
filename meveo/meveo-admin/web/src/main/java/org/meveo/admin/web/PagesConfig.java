package org.meveo.admin.web;

import org.jboss.seam.faces.security.AccessDeniedView;
import org.jboss.seam.faces.security.LoginView;
import org.jboss.seam.faces.view.config.ViewConfig;
import org.jboss.seam.faces.view.config.ViewPattern;
import org.jboss.seam.security.annotations.LoggedIn;

@ViewConfig
public interface PagesConfig {

    static enum Pages1 {

        @ViewPattern("/*")
        @LoginView("/loginInternal.xhtml?faces-redirect=true")
        @AccessDeniedView("/errors/403.xhtml")
        ALL,

        @ViewPattern("/errors/*")
        // @Admin(restrictAtPhase=RESTORE_VIEW)
        ERRORS,

        @ViewPattern("/home.xhtml")
        @LoggedIn()
        // @Admin(restrictAtPhase=RESTORE_VIEW)
        HOME,

        @ViewPattern("/pages/*")
        @LoggedIn()
        // @Admin(restrictAtPhase=RESTORE_VIEW)
        PAGES
    }

}
