package org.meveo.service.cpq;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.regex.Pattern;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;

public abstract class AttributeValueService<T extends AttributeValue> extends PersistenceService<T> {

    public static void validateValue(AttributeValue attributeValue, CpqQuote cpqQuote, QuoteVersion quoteVersion,
                                     CommercialOrder commercialOrder, ServiceInstance serviceInstance) throws BusinessException {
        if (!validate(attributeValue.getAttribute().getValidationType(),
                attributeValue.getAttribute().getValidationPattern(), attributeValue, cpqQuote, quoteVersion,
                commercialOrder, serviceInstance)) {
            throw new BusinessException(createErrorMessage(attributeValue));
        }

    }

    private static boolean validate(AttributeValidationType validationType, String validationPattern,
                                    AttributeValue attributeValue, CpqQuote cpqQuote, QuoteVersion quoteVersion,
                                    CommercialOrder commercialOrder, ServiceInstance serviceInstance) {
        if (validationType.equals(AttributeValidationType.EL)) {
            return evaluateExpression(validationPattern,
                    Boolean.class, attributeValue, cpqQuote, quoteVersion, commercialOrder, serviceInstance);
        } else {
            Object value = attributeValue.getAttribute().getAttributeType().getValue(attributeValue);
            return value != null ? Pattern.compile(validationPattern).matcher(value.toString()).find() : true;
        }
    }

    private static String createErrorMessage(AttributeValue attributeValue) {
        AttributeTypeEnum attributeType = attributeValue.getAttribute().getAttributeType();
        String value;
        if (AttributeTypeEnum.NUMERIC.equals(attributeType)) {
            value = attributeValue.getDoubleValue().toString();
        } else {
            if (AttributeTypeEnum.DATE.equals(attributeType)) {
                value = attributeValue.getDateValue().toString();
            } else {
                value = attributeValue.getStringValue();
            }
        }
        StringBuilder errorMessage = new StringBuilder("Value ")
                .append((value.length() > 30 ? (value.substring(0, 27) + "...") : value))
                .append(" for attribute ")
                .append(attributeValue.getAttribute().getCode())
                .append(" does not match validation pattern ")
                .append(attributeValue.getAttribute().getValidationPattern());
        return errorMessage.toString();
    }
    
    public static boolean evaluateMandatoryEl(String mandatoryEl, CpqQuote cpqQuote, QuoteVersion quoteVersion,
                                     CommercialOrder commercialOrder, ServiceInstance serviceInstance) {
    	 return evaluateExpression(mandatoryEl,
                 Boolean.class, cpqQuote, quoteVersion, commercialOrder, serviceInstance);
    }
}