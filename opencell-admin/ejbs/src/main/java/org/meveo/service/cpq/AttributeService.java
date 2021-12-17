/**
 * 
 */
package org.meveo.service.cpq;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Rachid.AITYAAZZA
 *
 */

@Stateless
public class AttributeService extends BusinessService<Attribute>{
	
	/**
     * Update parent attribute
     */
    public void updateParentAttribute(Long id) {
        Query q=getEntityManager().createNamedQuery("Attribute.updateParentAttribute").setParameter("id", id);
        q.executeUpdate();
    }

	@SuppressWarnings("unchecked")
	public <T> T  evaluateElExpressionAttribute(String expression,Product product, OfferTemplate offer,
                                               CpqQuote quote, Class<T> resultType) {
		Map<Object, Object> params = new HashMap<>();
		if(Strings.isBlank(expression)) {
			return null;
		}
		if(expression.indexOf(ValueExpressionWrapper.VAR_PRODUCT) >= 0 && product != null) {
			params.put(ValueExpressionWrapper.VAR_PRODUCT, product);
		}
		if(expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0 && offer != null) {
			params.put(ValueExpressionWrapper.VAR_OFFER, offer);
		}
		if(expression.indexOf(ValueExpressionWrapper.VAR_CPQ_QUOTE) >= 0 && quote != null) {
			params.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
		}
		if(resultType == null) {
			resultType = (Class<T>) String.class;
		}
		 T res = params.isEmpty() ? null : evaluateExpression(expression, params, resultType);
        try {
           return  res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
	}

    @Override
    public void create(Attribute attribute) throws BusinessException {
        if (attribute.getValidationPattern() != null
                && attribute.getDefaultValue() != null && !validateDefaultValue(attribute)) {
            throw new BusinessException(createErrorMessage(attribute));
        }
        super.create(attribute);
    }

    @Override
    public Attribute update(Attribute attribute) throws BusinessException {
        if (attribute.getValidationPattern() != null
                && attribute.getDefaultValue() != null && !validateDefaultValue(attribute)) {
            throw new BusinessException(createErrorMessage(attribute));
        }
        return super.updateNoCheck(attribute);
    }

    public boolean validateDefaultValue(Attribute attribute) {
        if (attribute.getValidationType().equals(AttributeValidationType.EL)) {
            return evaluateExpression(attribute.getValidationPattern(), Boolean.class, attribute.getDefaultValue());
        } else {
            return Pattern.compile(attribute.getValidationPattern()).matcher(attribute.getDefaultValue()).find();
        }
    }

    private String createErrorMessage(Attribute attribute) {
    	 if(StringUtils.isNotBlank(attribute.getValidationLabel())){
             return evaluateExpression(attribute.getValidationLabel(), String.class, attribute);
         }
        String value = attribute.getDefaultValue().length() <= 30
                ? attribute.getDefaultValue() : attribute.getDefaultValue().substring(0, 27) + "...";
        StringBuilder errorMessage = new StringBuilder("Value ")
                .append(value)
                .append(" for attribute ")
                .append(attribute.getCode())
                .append(" does not match validation pattern ")
                .append(attribute.getValidationPattern());
        return errorMessage.toString();
    }
}