package org.meveo.service.cpq;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteAttributeService extends AttributeValueService<QuoteAttribute> {

    public AttributeValue findByAttributeAndQuoteProduct(Long attributeId, Long quoteProductId) {
        try {
            return (AttributeValue) this.getEntityManager()
					.createNamedQuery("QuoteAttribute.findByAttributeAndQuoteProduct")
                    .setParameter("attributeId", attributeId)
                    .setParameter("quoteProductId", quoteProductId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void create(QuoteAttribute quoteAttribute) throws BusinessException {
        if(quoteAttribute.getAttribute() != null
                && quoteAttribute.getAttribute().getValidationPattern() != null) {
            super.validateValue(quoteAttribute);
        }
        super.create(quoteAttribute);
    }

    @Override
    public QuoteAttribute update(QuoteAttribute quoteAttribute) throws BusinessException {
        if(quoteAttribute.getAttribute() != null
                && quoteAttribute.getAttribute().getValidationPattern() != null) {
            super.validateValue(quoteAttribute);
        }
        return super.update(quoteAttribute);
    }
}