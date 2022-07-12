package org.meveo.service.billing.impl.article;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

	@Inject 
	private ArticleMappingLineService articleMappingLineService;

	@Inject
	private ArticleMappingService articleMappingService;

	private static final String DEFAULT_ARTICLE_MAPPING_CODE = "DEFAULT_ARTICLE_MAPPING";

	@SuppressWarnings("unchecked")
	public List<ArticleMappingLine> findByProductAndCharge(Product product, ChargeTemplate chargeTemplate,
														   OfferTemplate offer, String parameter1,
														   String parameter2, String parameter3) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", asList("product", "chargeTemplate"));
		if(product != null)
			queryBuilder.addCriterionEntity("am.product.code", product.getCode());
		if(chargeTemplate != null)
			queryBuilder.addCriterionEntity("am.chargeTemplate.code", chargeTemplate.getCode());
		if(product == null) {
			queryBuilder.addSql("am.product is null ");
		}
		if(chargeTemplate == null) {
			queryBuilder.addSql("am.chargeTemplate is null ");
		}
		if(offer != null) {
			queryBuilder.addCriterionEntity("am.offerTemplate.code", offer.getCode());
		}
		if(offer == null) {
			queryBuilder.addSql("am.offerTemplate is null ");
		}
		if(parameter1 != null) {
			queryBuilder.addCriterionEntity("am.parameter1", parameter1);
		}
		if(parameter1 == null) {
			queryBuilder.addSql("am.parameter1 is null ");
		}
		if(parameter2 != null) {
			queryBuilder.addCriterionEntity("am.parameter2", parameter2);
		}
		if(parameter2 == null) {
			queryBuilder.addSql("am.parameter2 is null ");
		}
		if(parameter3 != null) {
			queryBuilder.addCriterionEntity("am.parameter3", parameter3);
		}
		if(parameter3 == null) {
			queryBuilder.addSql("am.parameter3 is null ");
		}
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
		Set<Long> ids =  new HashSet<>(lists).stream().map(ArticleMappingLine::getId).collect(Collectors.toSet());
		if(!idsMapping.isEmpty())
			articleMappingLineService.remove(idsMapping);
		if(!ids.isEmpty())
			remove(ids);
	}

	/**
	 * @param id
	 * @param articleMappingLine
	 * @return
	 */
	public Optional<ArticleMappingLine> update(Long id, ArticleMappingLine articleMappingLine) {
		ArticleMappingLine articleMappingLineUpdated = findById(id, true);
		if (articleMappingLineUpdated == null) return Optional.empty();

		AccountingArticle accountingArticle = (AccountingArticle) tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
		articleMappingLineUpdated.setArticleMapping(getArticleMappingFromMappingLine(articleMappingLine));
		articleMappingLine.setAccountingArticle(accountingArticle);
		populateArticleMappingLine(articleMappingLine);

		articleMappingLineUpdated.setParameter1(articleMappingLine.getParameter1());
		articleMappingLineUpdated.setParameter2(articleMappingLine.getParameter2());
		articleMappingLineUpdated.setParameter3(articleMappingLine.getParameter3());

		articleMappingLineUpdated.setOfferTemplate(articleMappingLine.getOfferTemplate());
		articleMappingLineUpdated.setChargeTemplate(articleMappingLine.getChargeTemplate());
		articleMappingLineUpdated.setProduct(articleMappingLine.getProduct());

		articleMappingLineUpdated.getAttributesMapping().clear();
		if (articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()) {
			List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
					.stream()
					.map(am -> {
						Attribute attribute = (Attribute) tryToFindByCodeOrId(am.getAttribute());

						AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue());
						attributeMapping.setArticleMappingLine(articleMappingLineUpdated);
						return attributeMapping;
					})
					.collect(Collectors.toList());
			articleMappingLineUpdated.getAttributesMapping().addAll(attributesMapping);
		}
		articleMappingLineUpdated.setMappingKeyEL(articleMappingLine.getMappingKeyEL());
		update(articleMappingLineUpdated);
		return Optional.of(articleMappingLineUpdated);
	}
	
	private void populateArticleMappingLine(ArticleMappingLine articleMappingLine) {
    	if(articleMappingLine.getOfferTemplate() != null){
            OfferTemplate offerTemplate = (OfferTemplate) tryToFindByCodeOrId(articleMappingLine.getOfferTemplate());
            articleMappingLine.setOfferTemplate(offerTemplate);
        }
        if(articleMappingLine.getProduct() != null){
        	Product product = (Product) tryToFindByCodeOrId(articleMappingLine.getProduct());
        	articleMappingLine.setProduct(product);
        }
        if(articleMappingLine.getChargeTemplate() != null){
            ChargeTemplate chargeTemplate = (ChargeTemplate) tryToFindByEntityClassAndCodeOrId(ChargeTemplate.class, articleMappingLine.getChargeTemplate().getCode(), articleMappingLine.getChargeTemplate().getId());
            articleMappingLine.setChargeTemplate(chargeTemplate);
        }
    }

	/**
	 * @param articleMappingLine
	 * @return ArticleMappingLine
	 */
	 public ArticleMappingLine validateAndCreate(ArticleMappingLine articleMappingLine) {
        AccountingArticle accountingArticle = (AccountingArticle) tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
        articleMappingLine.setArticleMapping(getArticleMappingFromMappingLine(articleMappingLine));
        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
                    .stream()
                    .map(am -> {
                        Attribute attribute = (Attribute) tryToFindByCodeOrId(am.getAttribute());
                        AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue());
                        attributeMapping.setArticleMappingLine(articleMappingLine);
                        return attributeMapping;
                    })
                    .collect(Collectors.toList());
            articleMappingLine.setAttributesMapping(attributesMapping);
        }
        populateArticleMappingLine(articleMappingLine);
        articleMappingLine.setAccountingArticle(accountingArticle);
        create(articleMappingLine);
        return articleMappingLine;
    }

    private ArticleMapping getArticleMappingFromMappingLine(ArticleMappingLine articleMappingLine) {
		ArticleMapping articleMapping = null;
	 	if(articleMappingLine.getArticleMapping() != null) {
			try {
				articleMapping = tryToFindByCodeOrId(articleMappingLine.getArticleMapping());
			} catch (Exception exception) { }
		}
	 	if(articleMapping == null) {
			articleMapping = articleMappingService.findByCode(DEFAULT_ARTICLE_MAPPING_CODE);
			if(articleMapping == null) {
				throw new NotFoundException("Default article mapping not found");
			}
		}
	 	return articleMapping;
	}
}