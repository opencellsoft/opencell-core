package org.meveo.service.billing.impl.article;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

	

	@SuppressWarnings("unchecked")
	public List<ArticleMappingLine> findByProductCode(Product product) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", Arrays.asList("product"));
		queryBuilder.addCriterionEntity("am.product.code", product.getCode());
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}
}
