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
package org.meveo.admin.jsf.validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.ChargeTemplate;

/**
 * @author Gediminas Ubartas
 * @created Jul 28, 2011
 * 
 */
@FacesValidator("amountValidator")
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
        if (chargeTemplate != null & amountWithoutTax != null & amount2 != null) {
            amount2.setScale(2, RoundingMode.HALF_UP);
            Tax tax = chargeTemplate.getInvoiceSubCategory().getTax();
            BigDecimal calculatedAmount = amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100)).add(amountWithoutTax).setScale(2, RoundingMode.HALF_UP);
            System.out.println("CALCULATED" + calculatedAmount + "aaa" + calculatedAmount.compareTo(amount2));
            if (calculatedAmount.compareTo(amount2) == 0) {
                return true;
            }
        }

        return false;
    }

    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        /*
         * TODO: ModelValidator modelValidator = new ModelValidator(); modelValidator.validate(context, component, value);
         */

        UIInput accountNumberField = (UIInput) context.getViewRoot().findComponent("#{rich:clientId('amountWithoutTax')}");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa" + accountNumberField);

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
