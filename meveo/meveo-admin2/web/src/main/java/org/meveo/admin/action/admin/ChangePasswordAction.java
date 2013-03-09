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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.model.admin.User;
import org.meveo.service.admin.local.UserServiceLocal;

/**
 * 
 * @author Gediminas Ubartas
 * @created 2010.12.08
 */
@Name("changePasswordAction")
@Scope(ScopeType.CONVERSATION)
public class ChangePasswordAction {
    @Logger
    protected Log log;

    @In(required = false, value = "currentUser")
    private User currentUser;

    @Out
    private User user;

    @In
    private UserServiceLocal userService;

    private String currentPassword;

    @Begin(join = true)
    @Create
    public void init() {
        if (currentUser != null)
            user = userService.findById(currentUser.getId());
        else
            user = new User();

        currentPassword = "";
    }

    public String update() {
        String result = null;
        if (currentUser == null) {
            User tempUser = userService.findByUsernameAndPassword(user.getUserName(), currentPassword);
            if (tempUser != null) {
                tempUser.setNewPassword(user.getNewPassword());
                tempUser.setNewPasswordConfirmation(user.getNewPasswordConfirmation());
                user = tempUser;
            } else {
                FacesMessages.instance().addFromResourceBundle("changePassword.err.badUsernameOrPassword");
                return null;
            }
        }
        if (validate()) {
            try {
                userService.changePassword(user, user.getNewPassword());
                FacesMessages.instance().addFromResourceBundle("changePassword.msg.passwordChanged", user.getUserName());
            } catch (BusinessException e) {
                log.error("Error when update the password of #{currentUser.username} with password=" + currentPassword);
            }
            result = "success";
        }
        currentPassword = "";

        return result;
    }

    private boolean validate() {
        FacesMessages facesMessages = FacesMessages.instance();

        if (!Sha1Encrypt.encodePassword(currentPassword).equals(user.getPassword())) {
            facesMessages.addToControlFromResourceBundle("currentPassword", "changePassword.err.currentPasswordIncorrect");
            return false;
        }

        if (Sha1Encrypt.encodePassword(user.getNewPassword()).equals(user.getPassword())) {
            facesMessages.addToControlFromResourceBundle("newPassword", "changePassword.err.passwordMustBeDifferent");
            return false;
        }

        if (!user.getNewPassword().equals(user.getNewPasswordConfirmation())) {
            facesMessages.addToControlFromResourceBundle("newPassword", "changePassword.err.confirmationFailed");
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
