package org.meveo.service.quote;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.service.base.PersistenceService;

@Stateless
public class QuoteOfferService extends PersistenceService<QuoteOffer> {

	public QuoteOffer findByTemplateAndQuoteVersion(String offerTemplateCode, String CpqQuoteCode, int quoteVersion ) {
		try {
			return (QuoteOffer) this.getEntityManager().createNamedQuery("")
											.setParameter("offerTemplateCode", offerTemplateCode)
												.setParameter("cpqQuoteCode", CpqQuoteCode)
													.setParameter("quoteVersion", quoteVersion)
														.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
	}
}
