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
                log.error("Error when update the password of #{currentUser.username} with password=" + currentPassword);
                messages.error(new BundleKey("messages", "changePassword.err.badUsernameOrPassword"));
                return null;
            }
            messages.info(new BundleKey("messages", "changePassword.msg.passwordChanged"));
            return "/home.xhtml?faces-redirect=true";

        } else {
            return null;
        }

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