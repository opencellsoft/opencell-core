package org.meveo.service.cpq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.PersistenceService;

/**
 * @author Rachid.AITYAAZZA
 * @version 11.0
 * @dateCreation 22/01/2021
 *
 */
@Stateless
public class QuoteArticleLineService extends PersistenceService<QuoteArticleLine> {

	@SuppressWarnings("unchecked")

	public Map<String, QuoteArticleLine> findByQuoteVersion(QuoteVersion quoteVersion){
		if(quoteVersion == null || quoteVersion.getId() == null)
			throw new BusinessException("Quote version for quote artline line is mandatory");
		var result = new HashMap<String, QuoteArticleLine>();
		QueryBuilder builder = new QueryBuilder(QuoteArticleLine.class, "q", Arrays.asList("quoteVersion"));
		builder.addCriterion("q.quoteVersion.id", "=", quoteVersion.getId(), false);
		var query = builder.getQuery(getEntityManager());
		((Stream<QuoteArticleLine>)query.getResultStream()).forEach(quoteArticleLine -> {
			if(quoteArticleLine.getAccountingArticle() != null)
				result.put(quoteArticleLine.getAccountingArticle().getCode(), quoteArticleLine);
		});
		return result;
	}
}
