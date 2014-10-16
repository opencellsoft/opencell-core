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
package org.meveo.admin.action.admin;

import java.io.Serializable;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.model.admin.User;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;

/**
 * 
 * @author Gediminas Ubartas
 * @created 2010.12.08
 */
@Model
public class ChangePasswordAction implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Logger log;

	@Inject
	private Identity identity;

	@Inject
	private UserService userService;

	@Inject
	protected Messages messages;

	private String username;

	private String currentPassword;

	private String newPassword;

	private String newPasswordConfirmation;

	public String update() {

		User currentUser = null;
		if (identity.isLoggedIn()) {
			currentUser = userService.findById(((MeveoUser) identity.getUser()).getUser().getId());

		} else {
			try {
				currentUser = userService.loginChecks(username, currentPassword, true);

			} catch (LoginException e) {
				messages.error(new BundleKey("messages", "changePassword.err.badUsernameOrPassword"));
				return null;
			}
		}

		if (validate(currentUser)) {
			try {
				userService.changePassword(currentUser, newPassword);

			} catch (BusinessException e) {
				log.error("Error when update the password of #{currentUser.username} with password="
						+ currentPassword);
				messages.error(new BundleKey("messages", "changePassword.err.badUsernameOrPassword"));
				return null;
			}
			messages.info(new BundleKey("messages", "changePassword.msg.passwordChanged"));
			return "/home.xhtml?faces-redirect=true";
		}
		return null;
	}

	private boolean validate(User currentUser) {
		if (!Sha1Encrypt.encodePassword(currentPassword).equals(currentUser.getPassword())) {
			messages.error(new BundleKey("messages", "changePassword.err.currentPasswordIncorrect"));
			return false;
		}

		if (Sha1Encrypt.encodePassword(newPassword).equals(currentUser.getPassword())) {
			messages.error(new BundleKey("messages", "changePassword.err.passwordMustBeDifferent"));
			return false;
		}

		if (!StringUtils.equals(newPassword, newPasswordConfirmation)) {
			messages.error(new BundleKey("messages", "changePassword.err.confirmationFailed"));
			return false;
		}

		return true;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}