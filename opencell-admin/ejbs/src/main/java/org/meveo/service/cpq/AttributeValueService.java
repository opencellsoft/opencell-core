package org.meveo.service.cpq;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

@SuppressWarnings("rawtypes")
public abstract class AttributeValueService<T extends AttributeValue> extends PersistenceService<T> {

    public static void validate(AttributeValidationType validationType, String validationPattern, String validationLabel,
                                AttributeValue attributeValue, CpqQuote cpqQuote, QuoteVersion quoteVersion,
                                CommercialOrder commercialOrder, ServiceInstance serviceInstance) {
        boolean validate = true;
        if (validationType.equals(AttributeValidationType.EL)) {
            validate = evaluateExpression(validationPattern,
                    Boolean.class, attributeValue, cpqQuote, quoteVersion, commercialOrder, serviceInstance);
        } else {
            Object value = attributeValue.getAttribute().getAttributeType().getValue(attributeValue);
            validate = value != null ? Pattern.compile(validationPattern).matcher(value.toString()).find() : true;
        }
        if(!validate) {
            throw new BusinessException(createErrorMessage(attributeValue, validationPattern, validationLabel, quoteVersion, commercialOrder, serviceInstance));
        }
    }

    private static String createErrorMessage(AttributeValue attributeValue, String validationPattern,String validationLabel, QuoteVersion quoteVersion, CommercialOrder commercialOrder, ServiceInstance serviceInstance) {
        if(StringUtils.isNotBlank(validationLabel)){
            return evaluateExpression(validationLabel, String.class, attributeValue, quoteVersion, commercialOrder, serviceInstance);
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
                .append(validationPattern);
        return errorMessage.toString();

    }

    public static void evaluateMandatoryEl(AttributeValidationType validationType, String validationPattern, String validationLabel, AttributeValue attributeValue, String mandatoryEl, CpqQuote cpqQuote, QuoteVersion quoteVersion,
                                           CommercialOrder commercialOrder, ServiceInstance serviceInstance) {
        if(!Strings.isEmpty(mandatoryEl)){
            Object value = attributeValue.getAttribute().getAttributeType().getValue(attributeValue);
            var isMandatory = evaluateExpression(mandatoryEl,
                    Boolean.class, cpqQuote, quoteVersion, commercialOrder, serviceInstance);
            if(isMandatory && value == null ) {
                throw new BusinessException("Attribute code : " +  attributeValue.getAttribute().getCode() + " is mandatory");
            }
        }
        if(!Strings.isEmpty(validationPattern) && validationType != null) {
            validate(validationType, validationPattern, validationLabel ,attributeValue, cpqQuote, quoteVersion, commercialOrder, serviceInstance);
        }
    }

    public static Optional<ProductVersionAttribute> findMandatoryByProductVersion(AttributeValue attributeValue, ProductVersion productVersion) {
        var mandatoryEl = attributeValue.getAttribute().getProductVersionAttributes()
                .stream()
                .filter(pva ->
                        pva.getAttribute().getCode().equalsIgnoreCase(attributeValue.getAttribute().getCode()) &&
                                pva.getProductVersion().getId() == productVersion.getId()
                )
                .findFirst();
        return mandatoryEl;
    }

    public static Optional<OfferTemplateAttribute> findMandatoryByOfferTemplate(AttributeValue attributeValue, OfferTemplate offerTemplate) {
        var mandatoryEl = attributeValue.getAttribute().getOfferTemplateAttribute()
                .stream()
                .filter(pva ->
                        pva.getAttribute().getCode().equalsIgnoreCase(attributeValue.getAttribute().getCode()) &&
                                pva.getOfferTemplate().getId() == offerTemplate.getId()
                )
                .findFirst();
        return mandatoryEl;
    }

    public AttributeValue getAttributeValue(AttributeValue attributeInstance, Object... parameters) throws BusinessException {

        try {

            if (AttributeTypeEnum.EXPRESSION_LANGUAGE.equals(attributeInstance.getAttribute().getAttributeType())) {

                if (!StringUtils.isBlank(attributeInstance.getStringValue())) {

                    Object value = ValueExpressionWrapper.evaluateExpression(attributeInstance.getStringValue(), Object.class, parameters);

                    if (value != null) {

                        AttributeValue<AttributeValue> attributeValue = (AttributeValue) BeanUtils.cloneBean(attributeInstance);

                        attributeValue.setId(null);

                        if (value instanceof Boolean) {

                            attributeValue.setBooleanValue((Boolean) value);

                        } else if (NumberUtils.isCreatable(value.toString().trim()) && !shouldEvaluateNumericValueAsString(attributeValue)) {

                            attributeValue.setDoubleValue(Double.valueOf(value.toString().trim()));

                        } else {

                            attributeValue.setStringValue((String) value);

                        }

                        log.debug("getAttributeValue value={}, String={},boolean={},double={}", value, attributeValue.getStringValue(), attributeValue.getBooleanValue(), attributeValue.getDoubleValue());

                        return attributeValue;

                    }
                }
            }

        } catch (Exception e) {

            log.error("Error when trying to get AttributeValue : ", e);

            throw new BusinessException(e.getMessage());

        }

        return (AttributeValue) attributeInstance;
    }

    private boolean shouldEvaluateNumericValueAsString(AttributeValue<AttributeValue> attributeValue) {
        return attributeValue.getStringValue().contains("serviceInstance.code");
    }
    
    public void PopulateAttributeValue(AttributeValue attributeValue) { 
    	Attribute  attribute =attributeValue.getAttribute();
    	if(attribute == null)
    		throw new EntityDoesNotExistsException(Attribute.class, attributeValue.getId());

     
    	if(attribute.getAttributeType()!=null) {
    		switch (attribute.getAttributeType()) {
			case TOTAL :
			case COUNT :
			case NUMERIC :
			case INTEGER:
				if(attributeValue.getDoubleValue()==null && attributeValue.getStringValue()!=null ) {
					if(org.apache.commons.lang3.math.NumberUtils.isCreatable(attributeValue.getStringValue().trim())) {
						attributeValue.setDoubleValue(Double.valueOf(attributeValue.getStringValue()));
					}
				}
				break;
			case BOOLEAN:
				if(attributeValue.getBooleanValue()==null && attributeValue.getStringValue()!=null ) {
					if(StringUtils.isBoolean(attributeValue.getStringValue()) ) {
						attributeValue.setBooleanValue(Boolean.valueOf(attributeValue.getStringValue()));
					}
				}
				break;
			default:
				break;  
			}
    		}
    }
}