/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.jsf.validator;

import java.math.BigDecimal;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

import org.meveo.admin.util.ResourceBundle;

/**
 * @author Gediminas Ubartas
 * @since Jan 31, 2011
 * 
 */
@FacesValidator(value = "ribValidator", managed = true)
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
