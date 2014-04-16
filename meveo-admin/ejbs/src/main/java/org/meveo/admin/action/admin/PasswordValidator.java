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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.security.PasswordCheck;

/**
 * 
 * @author Gediminas Ubartas
 * @created 2010.12.08
 */
@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    @Inject
    private ResourceBundle resourceMessages;

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = (String) value;
        PasswordCheck pwdCheck = new PasswordCheck();
        if (!pwdCheck.checkPasswordStrength(password)) {

            throw new ValidatorException(new FacesMessage(resourceMessages.getString("changePassword.err.passwordWeak")));
        }
    }
}