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

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
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
		var matchResult = new ArrayList<ArticleMappingLine>();
		articleMappingLines.forEach(aml -> {
			aml.getAttributesMapping().size();
			var finalResult = aml.getAttributesMapping().stream().filter(am -> {
				final Attribute attribute = am.getAttribute();
				if(attributes.get(attribute.getCode()) != null) {
					Object value = attributes.get(am.getAttribute().getCode());
					switch (attribute.getAttributeType()) {
						case TEXT:
						case LIST_TEXT :
						case LIST_NUMERIC :
							return value.toString().contentEquals(am.getAttributeValue());
						case TOTAL:
						case COUNT:
						case NUMERIC:
							return Double.valueOf(value.toString()).doubleValue() == Double.valueOf(am.getAttributeValue()).doubleValue();
						case LIST_MULTIPLE_TEXT :
						case LIST_MULTIPLE_NUMERIC :
							List<String> source = Arrays.asList(am.getAttributeValue().split(";"));
							List<String> input = Arrays.asList(value.toString().split(";"));
							Optional<String> valExist = input.stream().filter( val -> {
								if(source.contains(val))
									return true;
								return false;
							}).findFirst();
							return valExist.isPresent();
						case EXPRESSION_LANGUAGE :
							 String result = attributeService.evaluteElExpressionAttribute(value.toString(), product, null, null, String.class);
							 return am.getAttributeValue().contentEquals(result);
						default:
							return value.toString().contentEquals(am.getAttributeValue());
					}
				}
				return false;
			}).collect(Collectors.toList());

			if(aml.getAttributesMapping().size() >= finalResult.size() && (finalResult.size() == attributes.keySet().size())) {
				matchResult.add(aml);
			}
			
		});
		if(matchResult.size() > 1) 
			throw new BadRequestException("More than one article found");
		AccountingArticle result = null;
		if(matchResult.size() == 1) {
			result = matchResult.get(0).getAccountingArticle();
			detach(result);
		}
		if(result==null) {
			result=findByCode("ART-STD"); 
		}
		return  result != null ? Optional.of(result) : Optional.empty();
	}

	public List<AccountingArticle> findByAccountingCode(String accountingCode) {
		return getEntityManager().createNamedQuery("AccountingArticle.findByAccountingCode", AccountingArticle.class)
				.setParameter("accountingCode", accountingCode)
				.getResultList();
	}
}
