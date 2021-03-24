/**
 * 
 */
package org.meveo.service.cpq;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

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
	public <T> T  evaluteElExpressionAttribute(String expression,Product product, OfferTemplate offer, CpqQuote quote, Class<T> resultType) {
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
		 T res = params.isEmpty() ? null : ValueExpressionWrapper.evaluateExpression(expression, params, resultType);
        try {
           return  res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
	}
 
}
