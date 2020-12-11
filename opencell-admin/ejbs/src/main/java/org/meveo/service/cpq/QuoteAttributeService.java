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

	public QuoteAttribute findByAttributeAndQuoteProduct(String attributeCode, Long quoteProductId) {
		try {
			return (QuoteAttribute) this.getEntityManager().createNamedQuery("QuoteAttribute.findByAttributeAndQuoteProduct")
																.setParameter("attributeCode", attributeCode)
																.setParameter("quoteProductId", quoteProductId)
																.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}
}
