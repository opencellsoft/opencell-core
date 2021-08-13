package org.meveo.service.cpq;

import static org.meveo.model.catalog.ColumnTypeEnum.Double;
import static org.meveo.model.catalog.ColumnTypeEnum.String;
import static org.meveo.model.catalog.ColumnTypeEnum.Range_Date;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.service.base.PersistenceService;

import java.util.regex.Pattern;

public abstract class AttributeValueService<T extends AttributeValue> extends PersistenceService<T> {

    public void validateValue(AttributeValue attributeValue) throws BusinessException {
        if (attributeValue.getAttribute().getAttributeType().getColumnType(false).equals(Double)) {
            if(!validate(attributeValue.getAttribute().getValidationType(),
                    attributeValue.getAttribute().getValidationPattern(), attributeValue.getDoubleValue())) {
                throw new BusinessException("Double value does not match the validation pattern");
            }
        }
        if (attributeValue.getAttribute().getAttributeType().getColumnType(false).equals(String)) {
            if(!validate(attributeValue.getAttribute().getValidationType(),
                    attributeValue.getAttribute().getValidationPattern(), attributeValue.getStringValue())) {
                throw new BusinessException("String value does not match the validation pattern");
            }
        }
        if (attributeValue.getAttribute().getAttributeType().getColumnType(false).equals(Range_Date)) {
            if(!validate(attributeValue.getAttribute().getValidationType(),
                    attributeValue.getAttribute().getValidationPattern(), attributeValue.getDateValue())){
                throw new BusinessException("Date value does not match the validation pattern");
            }
        }
    }

    private boolean validate(AttributeValidationType validationType, String validationPattern, Object value) {
        if (validationType.equals(AttributeValidationType.EL)) {
            return evaluateExpression(validationPattern, Boolean.class, value);
        } else {
            return Pattern.compile(validationPattern).matcher(value.toString()).find();
        }
    }
}