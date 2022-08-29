package org.meveo.service.billing.impl.article;

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

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.meveo.service.base.BusinessService;

@Stateless
public class ArticleMappingLineService extends BusinessService<ArticleMappingLine> {

	@Inject 
	private ArticleMappingLineService articleMappingLineService;

	@SuppressWarnings("unchecked")
	public List<ArticleMappingLine> findByProductAndCharge(Product product, ChargeTemplate chargeTemplate) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", Arrays.asList("product", "chargeTemplate"));
		if(product != null)
			queryBuilder.addCriterionEntity("am.product.code", product.getCode());
		if(chargeTemplate != null)
			queryBuilder.addCriterionEntity("am.chargeTemplate.code", chargeTemplate.getCode());
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

	/**
	 * @param id
	 * @param articleMappingLine
	 * @return
	 */
	public Optional<ArticleMappingLine> update(Long id, ArticleMappingLine articleMappingLine) {
		 ArticleMappingLine articleMappingLineUpdated = findById(id, true);
	        if(articleMappingLineUpdated == null) return Optional.empty();

	        AccountingArticle accountingArticle = (AccountingArticle) tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
	        if(articleMappingLine.getArticleMapping()!=null) {
	        	ArticleMapping articleMapping = (ArticleMapping) tryToFindByCodeOrId(articleMappingLine.getArticleMapping());
	        	articleMappingLineUpdated.setArticleMapping(articleMapping);
	        }else {
	        	articleMappingLineUpdated.setArticleMapping(null);
	        }
	        articleMappingLineUpdated.setAccountingArticle(accountingArticle);
	        populateArticleMappingLine(articleMappingLine);
	        
	        articleMappingLineUpdated.setParameter1(articleMappingLine.getParameter1());
	        articleMappingLineUpdated.setParameter2(articleMappingLine.getParameter2());
	        articleMappingLineUpdated.setParameter3(articleMappingLine.getParameter3());
	        
	        articleMappingLineUpdated.getAttributesMapping().clear();
	        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
	            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
	                    .stream()
	                    .map(am -> {
	                        Attribute attribute = (Attribute) tryToFindByCodeOrId(am.getAttribute());

							AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue(), am.getOperator());
							// Check if attributeType is en phase with le RuleOperator. For example : we cannot have greatherThenOrEquals for Text attribute
							isValidOperator(attribute, am.getOperator());
	                        attributeMapping.setArticleMappingLine(articleMappingLineUpdated);
	                        return attributeMapping;
	                    })
	                    .collect(Collectors.toList());
	            articleMappingLineUpdated.getAttributesMapping().addAll(attributesMapping);
	        }
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
	 * @return
	 */
	 public ArticleMappingLine valdiateAndCreate(ArticleMappingLine articleMappingLine) {

        AccountingArticle accountingArticle = (AccountingArticle) tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
        if(articleMappingLine.getArticleMapping()!=null) {
        	ArticleMapping articleMapping = (ArticleMapping) tryToFindByCodeOrId(articleMappingLine.getArticleMapping());
        	articleMappingLine.setArticleMapping(articleMapping);
        }
        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
                    .stream()
                    .map(am -> {
                        Attribute attribute = (Attribute) tryToFindByCodeOrId(am.getAttribute());
						AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue(), am.getOperator());
						// Check if attributeType is en phase with le RuleOperator. For example : we cannot have greatherThenOrEquals for Text attribute
						isValidOperator(attribute, am.getOperator());
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

	private void isValidOperator(Attribute attribute, RuleOperatorEnum givenOperator) {
		switch (attribute.getAttributeType()) {
			case BOOLEAN:
			case PHONE:
			case EMAIL:
			case TEXT:
				if (isNotOneOfOperator(givenOperator, RuleOperatorEnum.EQUAL, RuleOperatorEnum.NOT_EQUAL)) {
					throw new BusinessException(attribute.getAttributeType() + " Atttribut type cannot have operation : " + givenOperator);
				}
			case TOTAL:
			case COUNT:
			case NUMERIC:
			case INTEGER:
			case DATE:
			case CALENDAR:
				if (isNotOneOfOperator(givenOperator, RuleOperatorEnum.EQUAL, RuleOperatorEnum.NOT_EQUAL,
						RuleOperatorEnum.GREATER_THAN, RuleOperatorEnum.GREATER_THAN_OR_EQUAL,
						RuleOperatorEnum.LESS_THAN, RuleOperatorEnum.LESS_THAN_OR_EQUAL)) {
					throw new BusinessException(attribute.getAttributeType() + " Atttribut type cannot have operation : " + givenOperator);
				}
			case LIST_TEXT:
			case LIST_NUMERIC:
			case LIST_MULTIPLE_TEXT:
			case LIST_MULTIPLE_NUMERIC:
				if (isNotOneOfOperator(givenOperator, RuleOperatorEnum.EQUAL, RuleOperatorEnum.NOT_EQUAL, RuleOperatorEnum.EXISTS)) {
					throw new BusinessException(attribute.getAttributeType() + " Atttribut type cannot have operation : " + givenOperator);
				}
			case EXPRESSION_LANGUAGE:
			case INFO:
			default:
		}

	}

	private boolean isNotOneOfOperator(RuleOperatorEnum operator, RuleOperatorEnum... operators) {
		for (RuleOperatorEnum op : operators) {
			if (op == operator) {
				return false;
			}
		}
		return true;
	}
    
}
