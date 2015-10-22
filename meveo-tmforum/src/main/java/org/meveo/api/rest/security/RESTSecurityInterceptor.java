package org.meveo.api.rest.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.util.Base64;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.admin.exception.UnknownUserException;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * 
 *         http://java.dzone.com/articles/java-ee-7-and-jax-rs-20
 **/
@RSSecured
@Provider
public class RESTSecurityInterceptor implements ContainerRequestFilter, ExceptionMapper<Exception> {

	@Inject
	private Logger log;

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401,
			new Headers<Object>());
	private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403,
			new Headers<Object>());
	private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500,
			new Headers<Object>());

	@Inject
	private UserService userService;

	private User currentUser;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		log.debug("REST security filter");
		ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext
				.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
		Method method = methodInvoker.getMethod();
		// Access allowed for all
		if (!method.isAnnotationPresent(PermitAll.class)) {
			// Access denied for all
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(ACCESS_FORBIDDEN);
				return;
			}

			// Get request headers
			final MultivaluedMap<String, String> headers = requestContext.getHeaders();

			// Fetch authorization header
			final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

			// If no authorization information present; block access
			if (authorization == null || authorization.isEmpty()) {
				requestContext.abortWith(ACCESS_DENIED);
				return;
			}

			// Get encoded username and password
			final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

			// Decode username and password
			String usernameAndPassword = null;
			try {
				usernameAndPassword = new String(Base64.decode(encodedUserPassword));
			} catch (IOException e) {
				requestContext.abortWith(SERVER_ERROR);
				return;
			}

			// Split username and password tokens
			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			String username = tokenizer.nextToken();
			String password = tokenizer.nextToken();

			log.debug("REST call basic authentication. Username={}", username);

			try {
				currentUser = userService.loginChecks(username, password, false);
				log.debug("REST login successfull with username={}", username);

			} catch (LoginException e) {
				log.error("Login failed for the user {} for reason {} {}", new Object[] { username,
						e.getClass().getName(), e.getMessage() });
				if (e instanceof InactiveUserException) {
					log.error("login failed with username=" + username + " and password=" + password
							+ " : cause Business Account or user is not active");
				} else if (e instanceof UnknownUserException) {
					log.error("login failed with username=" + username + " and password=" + password
							+ " : cause unknown username/password");
				} else if (e instanceof PasswordExpiredException) {
					log.error("The password of user " + username + " has expired.");
				}

				currentUser = null;

				// requestContext.abortWith(new
				// ServerResponse("Access denied for this resource. " +
				// e.getMessage(), 401,
				// new Headers<Object>()));
			}

			boolean isAllowed = false;
			
			if (currentUser == null) {
				requestContext.abortWith(ACCESS_DENIED);
			} else {
				// check if user has permission
				isAllowed = currentUser.hasPermission("user", "apiAccess");
			}
			

			if (!isAllowed) {
				requestContext.abortWith(ACCESS_DENIED);
			}

		}
	}

	@Override
	public Response toResponse(Exception exception) {
		return null;
	}

	@Produces
	@RSUser
	public User getCurrentUser() {
		return currentUser;
	}
}