/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action;

import java.security.Principal;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.NoRoleException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.service.admin.local.UserServiceLocal;
import org.slf4j.Logger;

@Named
public class Authenticator {

	private static final long serialVersionUID = 7629475040801773331L;

	private Logger log;

	@Inject
	private UserServiceLocal userService;

	@Inject
	private Identity identity;

	@Inject
	private Credentials credentials;

	@Produces
	@Named("currentUser")
	private User currentUser;

	@Produces
	@Named("currentProvider")
	private Provider currentProvider;

	@Produces
	@Named("homeMessage")
	private String homeMessage;

	@Inject
	private FacesMessage facesMessages;

	@Inject
	// TODO: private LocaleSelector localeSelector;
	/* Authentication errors */
	private boolean noLoginError, inactiveUserError, noRoleError, passwordExpired;

	public boolean internalAuthenticate(Principal principal, List<String> roles) {
		/* Authentication check */
		currentUser = userService.findByUsername("meveo.admin");
		try {
			userService.login(currentUser);
		} catch (LoginException e) {
			log.info("Login failed for the user #" + currentUser.getId(), e);
			if (e instanceof InactiveUserException)
				inactiveUserError = true;
			else if (e instanceof NoRoleException)
				noRoleError = true;
			else if (e instanceof PasswordExpiredException)
				passwordExpired = true;
			return false;
		}

		homeMessage = "application.home.message";

		// TODO: identity.acceptExternallyAuthenticatedPrincipal(principal);

		// Roles
		for (Role role : currentUser.getRoles()) {
			// TODO: identity.addRole(role.getName());
			log.info("Role added #0", role.getName());
		}

		return true;
	}

	public String localLogout() {
		// TODO: Identity.instance().logout();
		return "loggedOut";
	}

	public boolean authenticate() {

		//TODO: log.info("authenticating {0} - {1}", credentials.getUsername(), credentials.getPassword());

		try {
			noLoginError = false;
			inactiveUserError = false;
			noRoleError = false;
			passwordExpired = false;

			/* Authentication check */
			//TODO: currentUser = userService.findByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());

			log.info("End of select");

			if (currentUser == null) {
				log.info("login failed with username=#{credentials.username} and password=#{credentials.password}");
				noLoginError = true;
				return false;
			}

			userService.login(currentUser);

			homeMessage = "application.home.message";

			// Roles
			for (Role role : currentUser.getRoles()) {
				// TODO: identity.addRole(role.getName());
				log.info("Role added #0", role.getName());
			}

			log.info("End of authenticating");
			return true;

		} catch (LoginException e) {
			log.info("Login failed for the user {0} for reason {1} {2}" + currentUser.getId(), e
					.getClass().getName(), e.getMessage());
			if (e instanceof InactiveUserException) {
				inactiveUserError = true;
			} else if (e instanceof NoRoleException) {
				noRoleError = true;
			} else if (e instanceof PasswordExpiredException) {
				passwordExpired = true;
			}
			return false;

		} catch (Exception other) {
			log.error("Authenticator : error thrown when trying to login", other);
			throw new RuntimeException(other);
		}

	}

	//TODO: @Observer("org.jboss.seam.security.loginFailed")
	public void loginFailed() {
		if (noLoginError) {
			//TODO: facesMessages.addFromResourceBundle(Severity.ERROR, "user.error.login");
			return;
		}
		if (inactiveUserError) {
			//TODO: facesMessages.addFromResourceBundle(Severity.ERROR, "user.error.inactive");
			return;
		}
		if (noRoleError) {
			//TODO: facesMessages.addFromResourceBundle(Severity.ERROR, "user.error.noRole");
			return;
		}
		if (passwordExpired) {
			//TODO: facesMessages.addFromResourceBundle(Severity.ERROR, "user.password.expired");
			return;
		}
	}

	//TODO: @Observer("org.jboss.seam.security.loginSuccessful")
	public void loginSuccessful() {
		// If user has only one provider, set it automatically, instead of
		// asking user to pick it
		if (currentUser.isOnlyOneProvider()) {
			currentProvider = currentUser.getProviders().get(0);
		}
	}

	public void setLocale(String language) {
		//TODO: localeSelector.selectLanguage(language);

	}
}
