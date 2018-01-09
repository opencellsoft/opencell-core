/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * @since 2010.12.08
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