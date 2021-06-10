package org.meveo.service.quote;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;

@Stateless
public class QuoteOfferService extends PersistenceService<QuoteOffer> {

	public QuoteOffer findByTemplateAndQuoteVersion(String offerTemplateCode, String CpqQuoteCode, int quoteVersion ) {
		try {
			return (QuoteOffer) this.getEntityManager().createNamedQuery("QuoteOffer.findByTemplateAndQuoteVersion")
											.setParameter("offerTemplateCode", offerTemplateCode)
												.setParameter("cpqQuoteCode", CpqQuoteCode)
													.setParameter("quoteVersion", quoteVersion)
														.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<QuoteOffer> findByQuoteVersion(QuoteVersion quoteVersion) {
		QueryBuilder queryBuilder = new QueryBuilder(QuoteOffer.class, "qo", Arrays.asList("quoteVersion"));
		 queryBuilder.addCriterion("qo.quoteVersion.id", "=", quoteVersion.getId(), false);
		 Query query = queryBuilder.getQuery(getEntityManager());
		 return query.getResultList();
	}
	
	
	public QuoteOffer findByQuoteVersionAndOffer(Long quoteVersionId,String offerCode) {
		try {
			Query query=getEntityManager().createNamedQuery("QuoteOffer.findQuoteAttribute");
			if(quoteVersionId!=null) {
				query=query.setParameter("quoteVersionId", quoteVersionId);
			}
			if(offerCode!=null) {
				query=query.setParameter("offerCode",offerCode);
			}										 
			return (QuoteOffer)query.getResultList().get(0);
		}catch(NoResultException e ) {
			log.error("cant find QuoteProduct with quoteVersion and : {} offer : {} and  product : {}", quoteVersionId, offerCode);
			return null;
		}
	}
}
