/**
 * 
 */
package org.meveo.service.cpq;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.regex.Pattern;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.service.base.PersistenceService;

/**
 * @author Rachid.AITYAAZZA
 *
 */

@Stateless
public class ProductVersionAttributeService extends PersistenceService<ProductVersionAttribute>{

    public void checkValidationPattern(ProductVersionAttribute attribute) throws BusinessException {
        if (attribute.getValidationPattern() != null
                && attribute.getDefaultValue() != null && !validateDefaultValue(attribute)) {
            throw new BusinessException(createErrorMessage(attribute));
        }
        super.create(attribute);
    }


    public boolean validateDefaultValue(ProductVersionAttribute attribute) {
        if (attribute.getValidationType().equals(AttributeValidationType.EL)) {
            return evaluateExpression(attribute.getValidationPattern(), Boolean.class, attribute.getDefaultValue());
        } else {
            return Pattern.compile(attribute.getValidationPattern()).matcher(attribute.getDefaultValue()).find();
        }
    }

    public String createErrorMessage(ProductVersionAttribute attribute) {
        String value = attribute.getDefaultValue().length() <= 30
                ? attribute.getDefaultValue() : attribute.getDefaultValue().substring(0, 27) + "...";
        StringBuilder errorMessage = new StringBuilder("Value ")
                .append(value)
                .append(" for attribute ")
                .append(attribute.getAttribute().getCode())
                .append(" does not match validation pattern ")
                .append(attribute.getValidationPattern());
        return errorMessage.toString();
    }
}