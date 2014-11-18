package org.meveo.api.rest;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.meveo.admin.exception.LoginException;
import org.meveo.api.rest.security.WSUser;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class DefaultUserProducer {

	@Inject
	private Logger log;

	@Inject
	private UserService userService;

	private User currentUser;

	@PostConstruct
	public void init() {
		try {
			currentUser = userService.loginChecks("meveo.admin", "meveo.admin",
					false);
		} catch (LoginException e) {
			log.error("Failed to login. {}", e.getMessage());
		}
	}

	@Produces
	@WSUser
	public User getCurrentUser() {
		return currentUser;
	}

}
