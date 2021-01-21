package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.service.base.PersistenceService;
import org.w3c.dom.Attr;


/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteAttributeService extends PersistenceService<QuoteAttribute> {

	public AttributeValue findByAttributeAndQuoteProduct(Long attributeId, Long quoteProductId) {
		try {
			return (AttributeValue) this.getEntityManager().createNamedQuery("QuoteAttribute.findByAttributeAndQuoteProduct")
																.setParameter("attributeId", attributeId)
																.setParameter("quoteProductId", quoteProductId)
																.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
}
