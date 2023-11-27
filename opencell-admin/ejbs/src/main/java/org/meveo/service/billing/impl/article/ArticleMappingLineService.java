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
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

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
	private ArticleMappingService articleMappingService;

	private static final String DEFAULT_ARTICLE_MAPPING_CODE = "DEFAULT_ARTICLE_MAPPING";

	@SuppressWarnings("unchecked")
	public List<ArticleMappingLine> findByProductAndCharge(Product product, ChargeTemplate chargeTemplate,
														   OfferTemplate offer, String parameter1,
														   String parameter2, String parameter3) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", Arrays.asList("attributesMapping"));
		if(product != null)
			queryBuilder.addCriterionEntity("am.product.id", product.getId());
		if(chargeTemplate != null)
			queryBuilder.addCriterionEntity("am.chargeTemplate.id", chargeTemplate.getId());
		if(product == null) {
			queryBuilder.addSql("am.product is null ");
		}
		if(chargeTemplate == null) {
			queryBuilder.addSql("am.chargeTemplate is null ");
		}
		if(offer != null) {
			queryBuilder.addCriterionEntity("am.offerTemplate.id", offer.getId());
		}
		if(offer == null) {
			queryBuilder.addSql("am.offerTemplate is null ");
		}
		if(parameter1 != null) {
			queryBuilder.addCriterionEntity("am.parameter1", parameter1);
		}
		if(parameter2 != null) {
			queryBuilder.addCriterionEntity("am.parameter2", parameter2);
		}
		if(parameter3 != null) {
			queryBuilder.addCriterionEntity("am.parameter3", parameter3);
		}
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.setFlushMode(FlushModeType.COMMIT).setHint("org.hibernate.cacheable", Boolean.TRUE).setHint("org.hibernate.readOnly", Boolean.TRUE).getResultList();
    }

	@SuppressWarnings("unchecked")
	public void deleteByProductCode(Product product) {
		QueryBuilder queryBuilder = new QueryBuilder(ArticleMappingLine.class, "am", asList("product"));
		queryBuilder.addCriterionEntity("am.product.code", product.getCode());
		Query query = queryBuilder.getQuery(getEntityManager());
		List<ArticleMappingLine> lists = query.getResultList();
		Set<Long> idsMapping = lists.stream().map(aml -> aml.getAttributesMapping()).flatMap(Collection::stream).map(AttributeMapping::getId).collect(Collectors.toSet());
		Set<Long> ids =  new HashSet<>(lists).stream().map(ArticleMappingLine::getId).collect(Collectors.toSet());
		if(!idsMapping.isEmpty())
			remove(idsMapping);
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

		AccountingArticle accountingArticle = tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
		if(articleMappingLineUpdated.getArticleMapping() == null) {
			articleMappingLineUpdated.setArticleMapping(getArticleMappingFromMappingLine(articleMappingLine));
		}
		articleMappingLineUpdated.setAccountingArticle(accountingArticle);
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
						Attribute attribute = tryToFindByCodeOrId(am.getAttribute());

						AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue(), am.getOperator());
						// Check if attributeType is en phase with le RuleOperator. For example : we cannot have greatherThenOrEquals for Text attribute
						isValidOperator(attribute, am.getOperator());
						attributeMapping.setArticleMappingLine(articleMappingLineUpdated);
						return attributeMapping;
					})
					.collect(Collectors.toList());
			articleMappingLineUpdated.getAttributesMapping().addAll(attributesMapping);
		}
		articleMappingLineUpdated.setAttributeOperator(articleMappingLine.getAttributeOperator());
		articleMappingLineUpdated.setMappingKeyEL(articleMappingLine.getMappingKeyEL());
		articleMappingLineUpdated.setDescription(articleMappingLine.getDescription());
		update(articleMappingLineUpdated);
		return Optional.of(articleMappingLineUpdated);
	}

	private void populateArticleMappingLine(ArticleMappingLine articleMappingLine) {
		if (articleMappingLine.getOfferTemplate() != null) {
			OfferTemplate offerTemplate = tryToFindByCodeOrId(articleMappingLine.getOfferTemplate());
			articleMappingLine.setOfferTemplate(offerTemplate);
		}
		if (articleMappingLine.getProduct() != null) {
			Product product = tryToFindByCodeOrId(articleMappingLine.getProduct());
			articleMappingLine.setProduct(product);
		}
		if (articleMappingLine.getChargeTemplate() != null) {
			ChargeTemplate chargeTemplate = (ChargeTemplate) tryToFindByEntityClassAndCodeOrId(ChargeTemplate.class, articleMappingLine.getChargeTemplate().getCode(), articleMappingLine.getChargeTemplate().getId());
			articleMappingLine.setChargeTemplate(chargeTemplate);
		}
	}

	/**
	 * @param articleMappingLine
	 * @return ArticleMappingLine
	 */
	 public ArticleMappingLine validateAndCreate(ArticleMappingLine articleMappingLine) {
        AccountingArticle accountingArticle = tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
        articleMappingLine.setArticleMapping(getArticleMappingFromMappingLine(articleMappingLine));
        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
                    .stream()
                    .map(am -> {
                        Attribute attribute = tryToFindByCodeOrId(am.getAttribute());
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

    private ArticleMapping getArticleMappingFromMappingLine(ArticleMappingLine articleMappingLine) {
		ArticleMapping articleMapping = null;
	 	if(articleMappingLine.getArticleMapping() != null) {
			try {
				articleMapping = tryToFindByCodeOrId(articleMappingLine.getArticleMapping());
			} catch (Exception exception) {
				log.debug("Default article mapping line will be used");
			}
		}
	 	if(articleMapping == null) {
			articleMapping = articleMappingService.findByCode(DEFAULT_ARTICLE_MAPPING_CODE);
			if(articleMapping == null) {
				throw new NotFoundException("Default article mapping not found");
			}
		}
	 	return articleMapping;
	}

    public List<ArticleMappingLine> findAll() {
        return getEntityManager().createNamedQuery("ArticleMappingLine.findAll").getResultList();
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
				break;
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
				break;
			case LIST_TEXT:
			case LIST_NUMERIC:
			case LIST_MULTIPLE_TEXT:
			case LIST_MULTIPLE_NUMERIC:
				if (isNotOneOfOperator(givenOperator, RuleOperatorEnum.EQUAL, RuleOperatorEnum.NOT_EQUAL, RuleOperatorEnum.EXISTS)) {
					throw new BusinessException(attribute.getAttributeType() + " Atttribut type cannot have operation : " + givenOperator);
				}
				break;
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