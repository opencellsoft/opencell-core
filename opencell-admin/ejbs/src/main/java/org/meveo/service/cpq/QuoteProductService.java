package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.model.quote.QuoteProduct;
import org.meveo.service.base.PersistenceService;


/**
 * @author Khairi
 * @version 10.0
 */
@Stateless
public class QuoteProductService extends PersistenceService<QuoteProduct> { 
	
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
			log.warn("cant find QuoteProduct with  quote version: {} and product version : {}", quoteVersionId, quoteOfferId);
			return null;
		}
	}
	
	
	public QuoteProduct findByQuoteAndOfferAndProduct(Long quoteVersionId,String offerCode,String productCode) {
		try {
			Query query=getEntityManager().createNamedQuery("QuoteProduct.findQuoteAttribute");
			if(quoteVersionId!=null) {
				query=query.setParameter("quoteVersionId", quoteVersionId);
			}
			if(offerCode!=null) {
				query=query.setParameter("offerCode",offerCode);
			}
			query.setParameter("productCode",productCode);
														 
			return (QuoteProduct) query.getResultList().get(0);
		}catch(NoResultException e ) {
			log.error("cant find QuoteProduct with quoteVersion and : {} offer : {} and  product : {}", quoteVersionId, offerCode, productCode);
			return null;
		}
	}
}
