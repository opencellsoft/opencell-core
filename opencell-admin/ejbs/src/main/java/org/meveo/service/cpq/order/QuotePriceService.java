package org.meveo.service.cpq.order;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.quote.QuotePrice;
import org.meveo.service.base.BusinessService;

/**
 * @author Tarik FAKHOURI
 * @version 11.0
 * @dateCreation 21/01/2021
 *
 */
@Stateless
public class QuotePriceService extends BusinessService<QuotePrice> {

	
	@SuppressWarnings("unchecked")
	public List<QuotePrice> findByQuoteArticleLineIdandQuoteVersionId(Long QuoteArticleLineId, Long quoteVersionId) {
		QueryBuilder queryBuilder = new QueryBuilder(QuotePrice.class, "qp", Arrays.asList("quoteArticleLine", "quoteVersion"));
		queryBuilder.addCriterion("qp.quoteArticleLine.id", "=", QuoteArticleLineId, false);
		queryBuilder.addCriterion("qp.quoteVersion.id", "=", quoteVersionId, false);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}
}
