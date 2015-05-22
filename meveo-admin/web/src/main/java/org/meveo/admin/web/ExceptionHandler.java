package org.meveo.admin.web;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.AuthorizationException;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.meveo.admin.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HandlesExceptions
public class ExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

	public void handleAuthorizationException(
			@Handles CaughtException<AuthorizationException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught in handleAuthorizationException exception={}",evt.getException()!=null?evt.getException().getMessage():null, evt.getException());

		try {
			if(!response.isCommitted()){
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/403.jsf"));
			}
		} catch (Exception e) {
			log.error("failed to redirect in handleAuthorizationException exception={}",e.getMessage(),e);
		}
	}

	public void handleInvalidConversationException(
			@Handles CaughtException<NonexistentConversationException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught in handleInvalidConversationException exception={}", evt.getException()!=null?evt.getException().getMessage():null,evt.getException());

		try {
			if(!response.isCommitted()){
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/expired.jsf"));
			}
		} catch (Exception e) {
		    log.error("failed to redirect in handleInvalidConversationException exception {}",e.getMessage(),e);
		}
	}

	public void handleViewExpiredException(
			@Handles CaughtException<javax.faces.application.ViewExpiredException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught in handleViewExpiredException exception={}", evt.getException()!=null?evt.getException().getMessage():null,evt.getException());

		try {
			if(!response.isCommitted()){
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/expired.jsf"));
			}
		} catch (Exception e) {
			log.error("failed to redirect in handleViewExpiredException exception={}",e.getMessage(),e);
		}
	}

	public void handleSqlException(
			@Handles CaughtException<java.sql.SQLException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught in handleSqlException exception={}", evt.getException()!=null?evt.getException().getMessage():null,evt.getException());

		try {
			if(!response.isCommitted()){
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/database.jsf"));
			}
		} catch (Exception e) {
			log.error("failed to redirect in handleSqlException exception={}",e.getMessage(),e);
		}
	}

	public void handleRuntimeException(
			@Handles CaughtException<java.lang.RuntimeException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught in handleRuntimeException exception={}", evt.getException()!=null?evt.getException().getMessage():null,evt.getException());

		try {
			if(!response.isCommitted()){
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/bug.jsf"));
			}
		} catch (Exception e) {
			log.error("failed to redirect in handleRuntimeException exception={}",e.getMessage(),e);
		}
	}

	public void handleBusinessException(
			@Handles CaughtException<BusinessException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught in handleBusinessException exception={}", evt.getException()!=null?evt.getException().getMessage():null,evt.getException());

		try {
			if(!response.isCommitted()){
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/bug.jsf"));
			}
		} catch (Exception e) {
			log.error("failed to redirect in handleBusinessException exception={}",e.getMessage(),e);
		}
	}

}