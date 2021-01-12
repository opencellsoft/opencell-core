package org.meveo.service.billing.impl.article;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;

@Stateless
public class AccountingArticleService1 extends BusinessService<AccountingArticle> {
	
	@Inject private ArticleMappingLineService articleMappingLineService;
	
	public Optional<AccountingArticle> getAccountingArticle(Product product, Map<String, Object> attributes) {
		List<ArticleMappingLine> articles = articleMappingLineService.findByProductCode(product);
		var matchResult = new ArrayList<ArticleMappingLine>();
		articles.forEach(aml -> {
			aml.getAttributesMapping().size();
			var finalResult = aml.getAttributesMapping().stream().filter(am -> {
				final Attribute attribute = am.getAttribute();
				if(attributes.get(attribute.getCode()) != null) {
					Object value = attributes.get(am.getAttribute().getCode());
					if(value.toString().contentEquals(am.getAttributeValue())) return true;
					/*switch (attribute.getAttributeType()) {
					case TEXT: 
					case LIST_TEXT :
					case LIST_MULTIPLE_TEXT :
					case NUMERIC: 
					case LIST_NUMERIC :
					default:
						if(value.toString().contentEquals(am.getAttributeValue())) return true;
					}*/
				}
				return false;
			}).collect(Collectors.toList());
			
			if(aml.getAttributesMapping().size() == finalResult.size()) {
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
		return  result != null ? Optional.of(result) : Optional.empty();
	}
}
