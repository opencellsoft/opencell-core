package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.quote.QuoteProduct;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteProductService extends PersistenceService<QuoteProduct> {

	private final static Logger LOGGER = LoggerFactory.getLogger(QuoteProductService.class);
	

	
	public QuoteProduct addNewQuoteProduct(QuoteProduct quoteProduct){
		this.create(quoteProduct);
		return quoteProduct;
	}
	
	public QuoteProduct findByQuoteVersionAndQuoteOffer(Long quoteVersionId, Long quoteOfferId) {
		try {
			return (QuoteProduct) this.getEntityManager().createNamedQuery("QuoteProduct.findByQuoteVersionAndQuoteOffer")
														.setParameter("quoteVersionId", quoteVersionId)
														.setParameter("quoteOfferId", quoteOfferId)
															.getSingleResult();
		}catch(NoResultException e ) {
			LOGGER.warn("cant find QuoteProduct with  quote version: {} and product version : {}", quoteVersionId, quoteOfferId);
			return null;
		}
	}
}
