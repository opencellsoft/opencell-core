package org.meveo.admin.web;

import javax.enterprise.context.NonexistentConversationException;
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

	private Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

	public void handleAuthorizationException(
			@Handles CaughtException<AuthorizationException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught exception={}", evt.getException());

		try {
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/403.jsf"));
		} catch (Exception e) {
			log.error("Caught exception {}",e.getMessage());
		}
	}

	public void handleInvalidConversationException(
			@Handles CaughtException<NonexistentConversationException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught exception={}", evt.getException());

		try {
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/expired.jsf"));
		} catch (Exception e) {
		    log.error("Caught exception {}",e.getMessage());
		}
	}

	public void handleViewExpiredException(
			@Handles CaughtException<javax.faces.application.ViewExpiredException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught exception={}", evt.getException());

		try {
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/expired.jsf"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void handleSqlException(
			@Handles CaughtException<java.sql.SQLException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught exception={}", evt.getException());

		try {
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/database.jsf"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void handleRuntimeException(
			@Handles CaughtException<java.lang.RuntimeException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught exception={}", evt.getException());

		try {
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/bug.jsf"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public void handleBusinessException(
			@Handles CaughtException<BusinessException> evt,
			final HttpServletRequest request, final HttpServletResponse response) {

		evt.handled();
		
		log.error("Caught exception={}", evt.getException());

		try {
			response.sendRedirect(response.encodeRedirectURL(request
					.getContextPath() + "/errors/bug.jsf"));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}