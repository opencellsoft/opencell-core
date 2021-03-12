package org.meveo.service.billing.impl.article;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

	@Inject private ArticleMappingLineService articleMappingLineService;

	@SuppressWarnings("unchecked")
	public List<ArticleMappingLine> findByProductCode(Product product) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", Arrays.asList("product"));
		queryBuilder.addCriterionEntity("am.product.code", product.getCode());
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public void deleteByProductCode(Product product) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", Arrays.asList("product"));
		queryBuilder.addCriterionEntity("am.product.code", product.getCode());
		Query query = queryBuilder.getQuery(getEntityManager());
		List<ArticleMappingLine> lists = query.getResultList();
		Set<Long> idsMapping = lists.stream().map(aml -> aml.getAttributesMapping()).flatMap(Collection::stream).map(AttributeMapping::getId).collect(Collectors.toSet());
		Set<Long> ids =  new HashSet<ArticleMappingLine>(lists).stream().map(ArticleMappingLine::getId).collect(Collectors.toSet());
		if(!idsMapping.isEmpty())
			articleMappingLineService.remove(idsMapping);
		if(!ids.isEmpty())
			remove(ids);
	}
}
