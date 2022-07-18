package org.meveo.service.cpq;

import java.util.List;

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
	
	public QuoteProduct findByQuoteAndOfferAndProduct(Long quoteVersionId, String quoteOfferCode,String productCode) {
		try {
			return (QuoteProduct) this.getEntityManager().createNamedQuery("QuoteProduct.findByQuoteVersionAndQuoteOffer")
														.setParameter("quoteVersionId", quoteVersionId)
														.setParameter("quoteOfferCode", quoteOfferCode)
														.setParameter("productCode",productCode)
															.getSingleResult();
		}catch(NoResultException e ) {
			log.warn("cant find QuoteProduct with  quote version: {} and product version : {}", quoteVersionId, quoteOfferCode);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<QuoteProduct> findByQuoteVersion(Long quoteVersionId) {
		Query query = this.getEntityManager().createNamedQuery("QuoteProduct.findByQuoteVersionId").setParameter("id", quoteVersionId);
		return query.getResultList();
	}

}
