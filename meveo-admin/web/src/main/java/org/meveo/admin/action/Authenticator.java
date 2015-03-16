/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.NoRoleException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.admin.exception.UnknownUserException;
import org.meveo.event.qualifier.LoggedIn;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model
public class Authenticator extends BaseAuthenticator {

	@Inject
	private UserService userService;

	@Inject
	private Credentials credentials;

	@Inject @LoggedIn
	protected Event<User> userEventProducer;
	
	private static final Logger log = LoggerFactory.getLogger(Authenticator.class);

	//
	// @Produces
	// @Named("homeMessage")
	// private String homeMessage;

	@Inject
	private Messages messages;

	// @Inject
	// TODO: private LocaleSelector localeSelector;

	/* Authentication errors */
	private boolean noLoginError;
	private boolean inactiveUserError;
	private boolean noRoleError;
	private boolean passwordExpired;

	// public User internalAuthenticate(Principal principal, List<String> roles)
	// {
	//
	// User user = null;
	//
	// try {
	// user = userService.loginChecks("meveo.admin", null);
	//
	// } catch (LoginException e) {
	// log.info("Login failed for the user #" + user.getId(), e);
	// if (e instanceof InactiveUserException) {
	// inactiveUserError = true;
	//
	// } else if (e instanceof NoRoleException) {
	// noRoleError = true;
	//
	// } else if (e instanceof PasswordExpiredException) {
	// passwordExpired = true;
	//
	// } else if (e instanceof UnknownUserException) {
	// noLoginError = true;
	// }
	// }
	//
	// homeMessage = "application.home.message";
	//
	// if (user == null) {
	// setStatus(AuthenticationStatus.FAILURE);
	// } else {
	//
	// homeMessage = "application.home.message";
	//
	// setStatus(AuthenticationStatus.SUCCESS);
	// setUser(new MeveoUser(user));
	//
	// // TODO needed to overcome lazy loading issue. Remove once solved
	// for (Role role : user.getRoles()) {
	// for (org.meveo.model.security.Permission permission :
	// role.getPermissions()) {
	// permission.getName();
	// }
	// }
	// }
	// return user;
	// }

	public String localLogout() {
		return "loggedOut";
	}

	public void authenticate() {

		noLoginError = false;
		inactiveUserError = false;
		noRoleError = false;
		passwordExpired = false;

		User user = null;
		try {

			/* Authentication check */
			user = userService.loginChecks(credentials.getUsername(),
					((PasswordCredential) credentials.getCredential()).getValue());

		} catch (LoginException e) {
			log.debug("Login failed for the user {} for reason {} {}", credentials.getUsername(), e
					.getClass().getName(), e.getMessage());
			if (e instanceof InactiveUserException) {
				inactiveUserError = true;
				log.error("login failed with username=" + credentials.getUsername()
						+ " and password="
						+ ((PasswordCredential) credentials.getCredential()).getValue()
						+ " : cause user is not active");
				messages.info(new BundleKey("messages", "user.error.inactive"));

			} else if (e instanceof NoRoleException) {
				noRoleError = true;
				log.error("The password of user " + credentials.getUsername() + " has expired.");
				messages.info(new BundleKey("messages", "user.error.noRole"));

			} else if (e instanceof PasswordExpiredException) {
				passwordExpired = true;
				log.error("The password of user " + credentials.getUsername() + " has expired.");
				messages.info(new BundleKey("messages", "user.password.expired"));

			} else if (e instanceof UnknownUserException) {
				noLoginError = true;
				log.debug("login failed with username={} and password={}",
						credentials.getUsername(),
						((PasswordCredential) credentials.getCredential()).getValue());
				messages.info(new BundleKey("messages", "user.error.login"));
			}
		}

		if (user == null) {
			setStatus(AuthenticationStatus.FAILURE);
		} else {

            // homeMessage = "application.home.message";

            setStatus(AuthenticationStatus.SUCCESS);
            MeveoUser meveoUser = new MeveoUser(user);
            
            // Choose the first provider as a current provider
            Provider currentProvider = null;

            if (user.getProviders().isEmpty()) {
                currentProvider = user.getProvider();
            } else {
                currentProvider = user.getProviders().iterator().next();
            }
            if(currentProvider.getLanguage()!=null){
            	currentProvider.getLanguage().getLanguageCode(); // Lazy loading issue
            }
            meveoUser.setCurrentProvider(currentProvider);

            setUser(meveoUser);
            userEventProducer.fire(user);
            log.debug("End of authenticating");
		}
	}

	public void setLocale(String language) {
		// TODO: localeSelector.selectLanguage(language);

	}

	public boolean isNoLoginError() {
		return noLoginError;
	}

	public void setNoLoginError(boolean noLoginError) {
		this.noLoginError = noLoginError;
	}

	public boolean isInactiveUserError() {
		return inactiveUserError;
	}

	public void setInactiveUserError(boolean inactiveUserError) {
		this.inactiveUserError = inactiveUserError;
	}

	public boolean isNoRoleError() {
		return noRoleError;
	}

	public void setNoRoleError(boolean noRoleError) {
		this.noRoleError = noRoleError;
	}

	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	public void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}
}
