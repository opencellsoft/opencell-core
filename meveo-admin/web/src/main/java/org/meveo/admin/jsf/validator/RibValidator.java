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
package org.meveo.admin.jsf.validator;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.meveo.admin.util.ResourceBundle;

/**
 * @author Gediminas Ubartas
 * @created Jan 31, 2011
 * 
 */
@FacesValidator("ribValidator")
public class RibValidator implements Validator {
    
    @Inject
    private ResourceBundle resourceMessages;
    
	public static boolean checkRib(String rib) {
		StringBuilder extendedRib = new StringBuilder(rib.length());
		for (char currentChar : rib.toCharArray()) {
			// Works on base 36
			int currentCharValue = Character.digit(currentChar, Character.MAX_RADIX);
			if (currentCharValue == -1)
				return false;
			// Convert character to simple digit
			extendedRib
					.append(currentCharValue < 10 ? currentCharValue
							: (currentCharValue + (int) StrictMath.pow(2,
									(currentCharValue - 10) / 9)) % 10);
		}

		return new BigDecimal(extendedRib.toString()).remainder(new BigDecimal(97)).intValue() == 0;
	}

	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		/*
		 * TODO: ModelValidator modelValidator = new ModelValidator();
		 * modelValidator.validate(context, component, value);
		 */

		String bankCodeId = (String) component.getAttributes().get("bankCodeId");
		UIInput bankCodeField = (UIInput) context.getViewRoot().findComponent(bankCodeId);

		String branchCodeId = (String) component.getAttributes().get("branchCodeId");
		UIInput branchCodeField = (UIInput) context.getViewRoot().findComponent(branchCodeId);

		String accountNumberId = (String) component.getAttributes().get("accountNumberId");
		UIInput accountNumberField = (UIInput) context.getViewRoot().findComponent(accountNumberId);

		String keyId = (String) component.getAttributes().get("keyId");
		UIInput keyField = (UIInput) context.getViewRoot().findComponent(keyId);

		StringBuilder rib = new StringBuilder();
		rib.append(bankCodeField.getSubmittedValue());
		rib.append(branchCodeField.getSubmittedValue());
		rib.append(accountNumberField.getSubmittedValue());
		rib.append(keyField.getSubmittedValue());

		if (!checkRib(rib.toString())) {
			FacesMessage facesMessage = new FacesMessage();
			String message = resourceMessages.getString("commons.ribValidation");
			facesMessage.setDetail(message);
			facesMessage.setSummary(message);
			facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}
	}
}
