package org.meveo.service.billing.impl.article;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;
import org.meveo.service.cpq.AttributeService;

@Stateless
public class AccountingArticleService extends BusinessService<AccountingArticle> {
	
	@Inject private ArticleMappingLineService articleMappingLineService;
	@Inject private AttributeService attributeService;
	
	public Optional<AccountingArticle> getAccountingArticle(Product product, Map<String, Object> attributes) {
		List<ArticleMappingLine> articleMappingLines = articleMappingLineService.findByProductCode(product);
		AttributeMappingLineMatch attributeMappingLineMatch = new AttributeMappingLineMatch();
		articleMappingLines.forEach(aml -> {
			aml.getAttributesMapping().size();
			List<AttributeMapping> matchedAttributesMapping = aml.getAttributesMapping().stream().filter(attributeMapping -> {
				final Attribute attribute = attributeMapping.getAttribute();
				if (attributes.get(attribute.getCode()) != null) {
					Object value = attributes.get(attributeMapping.getAttribute().getCode());
					switch (attribute.getAttributeType()) {
						case TEXT:
						case LIST_TEXT:
						case LIST_NUMERIC:
							return value.toString().contentEquals(attributeMapping.getAttributeValue());
						case TOTAL:
						case COUNT:
						case NUMERIC:
							return Double.valueOf(value.toString()).doubleValue() == Double.valueOf(attributeMapping.getAttributeValue()).doubleValue();
						case LIST_MULTIPLE_TEXT:
						case LIST_MULTIPLE_NUMERIC:
							List<String> source = Arrays.asList(attributeMapping.getAttributeValue().split(";"));
							List<String> input = Arrays.asList(value.toString().split(";"));
							Optional<String> valExist = input.stream().filter(val -> {
								if (source.contains(val))
									return true;
								return false;
							}).findFirst();
							return valExist.isPresent();
						case EXPRESSION_LANGUAGE:
							String result = attributeService.evaluteElExpressionAttribute(value.toString(), product, null, null, String.class);
							return attributeMapping.getAttributeValue().contentEquals(result);
						default:
							return value.toString().contentEquals(attributeMapping.getAttributeValue());
					}
				}
				return false;
			}).collect(Collectors.toList());

			//fullMatch
			if(aml.getAttributesMapping().size() >= matchedAttributesMapping.size() && (matchedAttributesMapping.size() == attributes.keySet().size())) {
				attributeMappingLineMatch.addFullMatch(aml);
			}else{
				attributeMappingLineMatch.addPartialMatch(aml, matchedAttributesMapping.size());
			}
			
		});
		if(attributeMappingLineMatch.getFullMatchs().size() > 1)
			throw new BadRequestException("More than one article found");
		AccountingArticle result = null;
		if(attributeMappingLineMatch.getFullMatchs().size() == 1) {
			result = attributeMappingLineMatch.getFullMatchs().get(0).getAccountingArticle();
		} else {
			ArticleMappingLine bestMatch = attributeMappingLineMatch.getBestMatch();
			result = bestMatch != null ? bestMatch.getAccountingArticle() : findByCode("ART-STD");
		}
		detach(result);
		return  result != null ? Optional.of(result) : Optional.empty();
	}

	public List<AccountingArticle> findByAccountingCode(String accountingCode) {
		return getEntityManager().createNamedQuery("AccountingArticle.findByAccountingCode", AccountingArticle.class)
				.setParameter("accountingCode", accountingCode)
				.getResultList();
	}
}
