package org.meveo.service.cpq;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.payments.impl.CustomDDRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AttributeValueService<T extends AttributeValue> extends PersistenceService<T> {

    
    public static void validateValue(AttributeValue attributeValue, CpqQuote cpqQuote, QuoteVersion quoteVersion,
                                     CommercialOrder commercialOrder, ServiceInstance serviceInstance) throws BusinessException {
        if (!validate(attributeValue.getAttribute().getValidationType(),
                attributeValue.getAttribute().getValidationPattern(), attributeValue, cpqQuote, quoteVersion,
                commercialOrder, serviceInstance)) {
            throw new BusinessException(createErrorMessage(attributeValue, quoteVersion, commercialOrder, serviceInstance));
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
            return !StringUtils.isBlank(value)? Pattern.compile(validationPattern).matcher(value.toString()).find() : true;
        }
    }

    private static String createErrorMessage(AttributeValue attributeValue, QuoteVersion quoteVersion, CommercialOrder commercialOrder, ServiceInstance serviceInstance) {
        if(StringUtils.isNotBlank(attributeValue.getAttribute().getValidationLabel())){
            return evaluateExpression(attributeValue.getAttribute().getValidationLabel(), String.class, attributeValue, quoteVersion, commercialOrder, serviceInstance);
        }
        AttributeTypeEnum attributeType = attributeValue.getAttribute().getAttributeType();
        String value;
        if (AttributeTypeEnum.NUMERIC.equals(attributeType)) {
            value = attributeValue.getDoubleValue() != null ? attributeValue.getDoubleValue().toString() : attributeValue.getStringValue();
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
    
    @SuppressWarnings("rawtypes")
	public AttributeValue getAttributeValue(AttributeValue attributeInstance, Object... parameters) throws BusinessException{
    	try {
        	if(AttributeTypeEnum.EXPRESSION_LANGUAGE.equals(attributeInstance.getAttribute().getAttributeType())) {
        		if(!StringUtils.isBlank(attributeInstance.getStringValue())) {

        			Object value=ValueExpressionWrapper.evaluateExpression(attributeInstance.getStringValue(), Object.class, parameters);
    	    			if(value!=null) {
    	    				AttributeValue<AttributeValue> attributeValue= (AttributeValue) BeanUtils.cloneBean(attributeInstance);
    	    				attributeValue.setId(null);
    	       			 if(NumberUtils.isCreatable(value.toString().trim())) {
    	       					attributeValue.setDoubleValue(Double.valueOf(value.toString().trim()));
    	       			 }else {
    	       				attributeValue.setStringValue((String)value);
    	       			}
    	       			log.debug("getAttributeValue value={}, String={},double={}",value,attributeValue.getStringValue(),attributeValue.getDoubleValue());
    	       			
    	       			return attributeValue;
        			}
        			
        		  }
        		}
		} catch (Exception e) {
			log.error("Error when trying to get AttributeValue : ", e);
			throw new BusinessException(e.getMessage());
		}
    	AttributeValue attributeValue=(AttributeValue)attributeInstance;
    	setDefaultAttributeValue(attributeValue);
    	return attributeValue;
    	}
    @SuppressWarnings("rawtypes")
    private void setDefaultAttributeValue(AttributeValue attributeValue) {
    	//set default value if value is null
    	Attribute attribute=attributeValue.getAttribute();
		if(!StringUtils.isBlank(attribute.getDefaultValue())){
			switch (attribute.getAttributeType()) {
			case BOOLEAN:
				if(attributeValue.getStringValue()==null)
					attributeValue.setStringValue(attribute.getDefaultValue());
				break;	
			case TOTAL :
			case COUNT :
			case NUMERIC :
			case INTEGER:
				if(attributeValue.getDoubleValue()==null)
					attributeValue.setDoubleValue(Double.valueOf(attribute.getDefaultValue()));
				break;
			case LIST_MULTIPLE_TEXT:
			case LIST_TEXT:
			case EXPRESSION_LANGUAGE :
			case TEXT:
				if(attributeValue.getStringValue()==null && attributeValue.getDoubleValue()==null)
					attributeValue.setStringValue(attribute.getDefaultValue());
				break;
			default:
				if(attributeValue.getStringValue()==null)
					attributeValue.setStringValue(attribute.getDefaultValue());
				break;
			}
		}
    }
}