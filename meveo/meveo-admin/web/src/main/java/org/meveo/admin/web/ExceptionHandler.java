package org.meveo.admin.web;

import java.io.IOException;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.seam.security.AuthorizationException;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.servlet.http.ContextPath;

@HandlesExceptions
public class ExceptionHandler {

    // @Inject
    // private Instance<HttpConversationContext> contextInstance;

    @Inject
    @ContextPath
    private String contextPath;

    public void handleAuthorizationException(@Handles CaughtException<AuthorizationException> evt) {

        evt.handled();

        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/errors/403.xhtml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleInvalidConversationException(@Handles CaughtException<NonexistentConversationException> evt) {

        evt.handled();

        try {
        	FacesContext.getCurrentInstance().getExternalContext().redirect("/errors/sessionExpired.jsf");
            // facesContext.getExternalContext().redirect(contextPath + "/errors/sessionExpired.jsf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleViewExpiredException(@Handles CaughtException<javax.faces.application.ViewExpiredException> evt) {

        evt.handled();

        try {
        	FacesContext.getCurrentInstance().getExternalContext().redirect("/errors/sessionExpired.jsf");
			//FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/errors/sessionExpired.jsf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void handleIllegalStateException(@Handles CaughtException<IllegalStateException> evt) {
    //
    // evt.handled();
    //
    // try {
    // facesContext.getExternalContext().redirect(contextPath + "/errors/sessionExpired.jsf");
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

}