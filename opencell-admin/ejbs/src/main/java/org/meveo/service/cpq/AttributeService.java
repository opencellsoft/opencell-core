/**
 * 
 */
package org.meveo.service.cpq;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
    public <T> T evaluateElExpressionAttribute(String expression, Product product, OfferTemplate offer, CpqQuote quote, Class<T> resultType) throws BusinessException {
        Map<Object, Object> params = new HashMap<>();
        if (Strings.isBlank(expression)) {
            return null;
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_PRODUCT) >= 0 && product != null) {
            params.put(ValueExpressionWrapper.VAR_PRODUCT, product);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0 && offer != null) {
            params.put(ValueExpressionWrapper.VAR_OFFER, offer);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CPQ_QUOTE) >= 0 && quote != null) {
            params.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
        }
        if (resultType == null) {
            resultType = (Class<T>) String.class;
        }
        T res = evaluateExpression(expression, params, resultType);
        return res;
    }

    public Attribute findByDescription(String description) {

        TypedQuery<Attribute> query = getEntityManager().createQuery("select a from Attribute a where lower(a.description)=:code", entityClass)
            .setParameter("code", description.toLowerCase()).setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of description {} found", entityClass.getSimpleName(), description);
            return null;
        }
    }
}