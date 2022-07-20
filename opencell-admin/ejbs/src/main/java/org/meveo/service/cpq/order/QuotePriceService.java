package org.meveo.service.cpq.order;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;

/**
 * @author Tarik FAKHOURI
 * @version 11.0
 * @dateCreation 21/01/2021
 *
 */
@Stateless
public class QuotePriceService extends PersistenceService<QuotePrice> {

	
	@SuppressWarnings("unchecked")
	public List<QuotePrice> findByQuoteArticleLineIdandQuoteVersionId(Long QuoteArticleLineId, Long quoteVersionId) {
		QueryBuilder queryBuilder = new QueryBuilder(QuotePrice.class, "qp", Arrays.asList("quoteArticleLine", "quoteVersion"));
		queryBuilder.addCriterion("qp.quoteArticleLine.id", "=", QuoteArticleLineId, false);
		queryBuilder.addCriterion("qp.quoteVersion.id", "=", quoteVersionId, false);
		queryBuilder.addOrderCriterionAsIs("qp.unitPriceWithoutTax", false);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}
	
	public void removeByQuoteVersionAndPriceLevel(QuoteVersion quoteVersion, PriceLevelEnum priceLevel) {
		getEntityManager().createNamedQuery("QuotePrice.removeByQuoteVersionAndPriceLevel")
				.setParameter("quoteVersion", quoteVersion)
				.setParameter("priceLevelEnum", priceLevel)
				.executeUpdate();
	}
	
	public void removeByQuoteOfferAndPriceLevel(QuoteOffer quoteOffer, PriceLevelEnum priceLevel) {
		getEntityManager().createNamedQuery("QuotePrice.removeByQuoteOfferAndPriceLevel")
				.setParameter("quoteOfferId", quoteOffer.getId())
				.setParameter("priceLevelEnum", priceLevel)
				.executeUpdate();
	}

	public List<QuotePrice> loadByQuoteOfferAndArticleCodeAndPriceLevel(Long quoteOfferId, String accountingArticleCode){
		return getEntityManager().createNamedQuery("QuotePrice.loadByQuoteOfferAndArticleCodeAndPriceLevel", QuotePrice.class)
				.setParameter("quoteOfferId", quoteOfferId)
				.setParameter("accountingArticleCode", accountingArticleCode)
				.setParameter("priceLevelEnum", PriceLevelEnum.PRODUCT)
				.getResultList();
	}
	
	public List<QuotePrice> loadByQuoteVersionAndPriceLevel(QuoteVersion quoteVersion, PriceLevelEnum priceLevel){
		return getEntityManager().createNamedQuery("QuotePrice.loadByQuoteVersionAndPriceLevel", QuotePrice.class)
				.setParameter("quoteVersion", quoteVersion)
				.setParameter("priceLevelEnum", priceLevel)
				.getResultList();
	}
	
	public QuotePrice findByUuid(String uuid) {
		if(uuid == null) return null;
		try {
			return getEntityManager().createQuery("from QuotePrice q where q.uuid=:uuid", QuotePrice.class)
					.setParameter("uuid", uuid)
					.getSingleResult();
		}catch(NonUniqueResultException e) {
			return null;
		}catch (NoResultException e) {
		return null;
	}
	}
}
