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
package org.meveo.admin.action.admin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.security.Identity;
import org.meveo.admin.exception.BusinessException;
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
@Named
// TODO: Conversation. @Scope(ScopeType.CONVERSATION)
public class ChangePasswordAction {
	protected Logger log;

	@Inject
	private Identity identity;

	@Inject
	private UserService userService;

	@Inject
	protected Messages messages;

	private User currentUser;
	private String currentPassword;

	/*
	 * //TODO: Conversation. @Begin(join = true)
	 * 
	 * @Create
	 */
	/*
	 * public void init() { if (currentUser != null) user =
	 * userService.findById(currentUser.getId()); else user = new User();
	 * 
	 * currentPassword = ""; }
	 */

	@PostConstruct
	private void init() {
		currentUser = ((MeveoUser) identity.getUser()).getUser();
	}

	public String update() {
		String result = null;
		
		if (currentUser == null) {
			User tempUser = userService.findByUsernameAndPassword(currentUser.getUserName(),
					currentPassword);
			if (tempUser != null) {
				tempUser.setNewPassword(currentUser.getNewPassword());
				tempUser.setNewPasswordConfirmation(currentUser.getNewPasswordConfirmation());
				currentUser = tempUser;
			} else {
				messages.error(new BundleKey("messages", "changePassword.err.badUsernameOrPassword"));
				return null;
			}
		}
		
		if (validate()) {
			try {
				userService.changePassword(currentUser, currentUser.getNewPassword());
				messages.error(new BundleKey("messages", "changePassword.err.badUsernameOrPassword"));
			} catch (BusinessException e) {
				log.error("Error when update the password of #{currentUser.username} with password="
						+ currentPassword);
			}
			result = "success";
		}
		currentPassword = "";

		return result;
	}

	private boolean validate() {
		if (!Sha1Encrypt.encodePassword(currentPassword).equals(currentUser.getPassword())) {
			messages.error(new BundleKey("messages", "changePassword.err.currentPasswordIncorrect"));
			return false;
		}

		if (Sha1Encrypt.encodePassword(currentUser.getNewPassword()).equals(
				currentUser.getPassword())) {
			messages.error(new BundleKey("messages", "changePassword.err.passwordMustBeDifferent"));
			return false;
		}

		if (!currentUser.getNewPassword().equals(currentUser.getNewPasswordConfirmation())) {
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
}
