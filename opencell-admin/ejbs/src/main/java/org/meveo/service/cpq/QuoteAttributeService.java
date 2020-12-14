package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.service.base.PersistenceService;


/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteAttributeService extends PersistenceService<QuoteAttribute> {

	public QuoteAttribute findByAttributeAndQuoteProduct(Long attributeId, Long quoteProductId) {
		try {
			return (QuoteAttribute) this.getEntityManager().createNamedQuery("QuoteAttribute.findByAttributeAndQuoteProduct")
																.setParameter("attributeId", attributeId)
																.setParameter("quoteProductId", quoteProductId)
																.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
}
