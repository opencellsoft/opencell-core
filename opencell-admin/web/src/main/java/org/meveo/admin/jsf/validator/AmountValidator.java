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
import org.meveo.model.catalog.ChargeTemplate;

/**
 * @author Gediminas Ubartas
 * @since Jul 28, 2011
 * 
 */
@FacesValidator(value = "amountValidator", managed = true)
public class AmountValidator implements Validator {
    private static String amountWithoutTaxID = "amountWithoutTax";
    private static String chargeTemplateID = "chargeTemplate";

    @Inject
    ResourceBundle resourceMessages;

    public boolean validateOneShotChargeInstanceAmount(ChargeTemplate chargeTemplate, BigDecimal amountWithoutTax, BigDecimal amount2) {
        // If fields are blank
        if (amountWithoutTax == null && amount2 == null)
            return true;
        // If there are values
      /*  if (chargeTemplate != null & amountWithoutTax != null & amount2 != null) {
            amount2.setScale(2, RoundingMode.HALF_UP);
            Tax tax = chargeTemplate.getInvoiceSubCategory().getTax();
            BigDecimal calculatedAmount = amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100)).add(amountWithoutTax).setScale(2, RoundingMode.HALF_UP);
            if (calculatedAmount.compareTo(amount2) == 0) {
                return true;
            }
        }*/

        return false;
    }

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        /*
         * TODO: ModelValidator modelValidator = new ModelValidator(); modelValidator.validate(context, component, value);
         */

        UIInput accountNumberField = (UIInput) context.getViewRoot().findComponent("#{rich:clientId('amountWithoutTax')}");
        

        BigDecimal amountWithoutTax = (BigDecimal) component.getAttributes().get(amountWithoutTaxID);
        BigDecimal amount2 = (BigDecimal) value;
        ChargeTemplate chargeTemplate = (ChargeTemplate) component.getAttributes().get(chargeTemplateID);
        if (!validateOneShotChargeInstanceAmount(chargeTemplate, amountWithoutTax, amount2)) {
            FacesMessage facesMessage = new FacesMessage();
            String message = resourceMessages.getString("commons.checkAmountHTandTTC");
            facesMessage.setDetail(message);
            facesMessage.setSummary(message);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }
}
