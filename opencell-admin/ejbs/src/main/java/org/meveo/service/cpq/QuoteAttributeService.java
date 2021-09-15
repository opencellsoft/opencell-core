package org.meveo.service.cpq;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.quote.QuoteVersion;

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
        	QuoteVersion quoteVersion=quoteAttribute.getQuoteProduct()!=null?
        			quoteAttribute.getQuoteProduct().getQuoteVersion():quoteAttribute.getQuoteOffer()!=null?quoteAttribute.getQuoteOffer().getQuoteVersion():null;
            super.validateValue(quoteAttribute, quoteVersion.getQuote(), quoteVersion, null, null);
        }
        super.create(quoteAttribute);
    }

    @Override
    public QuoteAttribute update(QuoteAttribute quoteAttribute) throws BusinessException {
        if(quoteAttribute.getAttribute() != null
                && quoteAttribute.getAttribute().getValidationPattern() != null) {
        	QuoteVersion quoteVersion=quoteAttribute.getQuoteProduct()!=null?
        			quoteAttribute.getQuoteProduct().getQuoteVersion():quoteAttribute.getQuoteOffer()!=null?quoteAttribute.getQuoteOffer().getQuoteVersion():null;
        			super.validateValue(quoteAttribute, quoteVersion.getQuote(), quoteVersion, null, null);
        }
        return super.update(quoteAttribute);
    }
}