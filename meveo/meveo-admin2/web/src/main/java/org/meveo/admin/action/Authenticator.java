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
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.NoRoleException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.model.admin.Role;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.local.UserServiceLocal;

@Name("authenticator")
public class Authenticator {

    private static final long serialVersionUID = 7629475040801773331L;

    @Logger
	private Log log;

	@In
	private UserServiceLocal userService;

	@In
	private Identity identity;

	@In
	private Credentials credentials;

	@Out(required = false, scope = ScopeType.SESSION)
	private User currentUser;

	@SuppressWarnings("unused")
	@Out(required = false, scope = ScopeType.SESSION)
	private Provider currentProvider;

	@SuppressWarnings("unused")
	@Out(required = false, scope = ScopeType.SESSION)
	private String homeMessage;

	@In
	private FacesMessages facesMessages;
	
	@In
	private LocaleSelector localeSelector;
	
	

	/* Authentication errors */
	private boolean noLoginError, inactiveUserError, noRoleError,
			passwordExpired;

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

		identity.acceptExternallyAuthenticatedPrincipal(principal);

		// Roles
		for (Role role : currentUser.getRoles()) {
			identity.addRole(role.getName());
			log.info("Role added #0", role.getName());
		}

		return true;
	}


	public String localLogout() {
		Identity.instance().logout();
		return "loggedOut";
	}

    public boolean authenticate() {

        log.info("authenticating {0} - {1}", credentials.getUsername(), credentials.getPassword());

        try {
            noLoginError = false;
            inactiveUserError = false;
            noRoleError = false;
            passwordExpired = false;

            /* Authentication check */
            currentUser = userService.findByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());

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
                identity.addRole(role.getName());
                log.info("Role added #0", role.getName());
            }

            log.info("End of authenticating");
            return true;
            
        } catch (LoginException e) {
            log.info("Login failed for the user {0} for reason {1} {2}" + currentUser.getId(), e.getClass().getName(), e.getMessage());
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

	@Observer("org.jboss.seam.security.loginFailed")
	public void loginFailed() {
		if (noLoginError) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"user.error.login");
			return;
		}
		if (inactiveUserError) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"user.error.inactive");
			return;
		}
		if (noRoleError) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"user.error.noRole");
			return;
		}
		if (passwordExpired) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"user.password.expired");
			return;
		}
	}

	@Observer("org.jboss.seam.security.loginSuccessful")
	public void loginSuccessful() {
	    // If user has only one provider, set it automatically, instead of asking user to pick it
        if (currentUser.isOnlyOneProvider()) {
            currentProvider=currentUser.getProviders().get(0);
        }
	}
	
	public void setLocale(String language){
		localeSelector.selectLanguage(language);
		
	}
}
