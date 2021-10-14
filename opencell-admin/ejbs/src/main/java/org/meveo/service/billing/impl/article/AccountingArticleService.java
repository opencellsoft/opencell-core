package org.meveo.service.billing.impl.article;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.BusinessService;
import org.meveo.service.cpq.AttributeService;

import static java.util.stream.Collectors.toList;

@Stateless
public class AccountingArticleService extends BusinessService<AccountingArticle> {
	
	@Inject private ArticleMappingLineService articleMappingLineService;
	@Inject private AttributeService attributeService;

	public Optional<AccountingArticle> getAccountingArticle(Product product, Map<String, Object> attributes) throws BusinessException {
		return getAccountingArticle(product, null, attributes);
	}

	public Optional<AccountingArticle> getAccountingArticle(Product product, ChargeTemplate chargeTemplate, Map<String, Object> attributes) throws BusinessException {
		List<ChargeTemplate> productCharges=new ArrayList<ChargeTemplate>();
		List<ArticleMappingLine> articleMappingLines = null;
		articleMappingLines = articleMappingLineService.findByProductAndCharge(product, chargeTemplate);
		if(chargeTemplate==null) {
			productCharges.addAll(product.getProductCharges().stream()
					.map(pc -> pc.getChargeTemplate())
					.collect(toList()));
			articleMappingLines = articleMappingLines.stream()
					.filter(aml -> aml.getChargeTemplate() == null || productCharges.contains(aml.getChargeTemplate()))
					.collect(toList());;
		}

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
							String result = attributeService.evaluateElExpressionAttribute(value.toString(), product, null, null, String.class);
							return attributeMapping.getAttributeValue().contentEquals(result);
						default:
							return value.toString().contentEquals(attributeMapping.getAttributeValue());
					}
				}
				return false;
			}).collect(toList());

			//fullMatch
			if(aml.getAttributesMapping().size() >= matchedAttributesMapping.size() && (matchedAttributesMapping.size() == attributes.keySet().size())) {
				attributeMappingLineMatch.addFullMatch(aml);
			}else{
				attributeMappingLineMatch.addPartialMatch(aml, matchedAttributesMapping.size());
			}
			
		});
		if(attributeMappingLineMatch.getFullMatchsArticle().size() > 1)
			throw new BusinessException("More than one article found");
		AccountingArticle result = null;
		if(attributeMappingLineMatch.getFullMatchsArticle().size() == 1) {
			result = attributeMappingLineMatch.getFullMatchsArticle().iterator().next();
		} else {
			ArticleMappingLine bestMatch = attributeMappingLineMatch.getBestMatch();
			result = bestMatch != null ? bestMatch.getAccountingArticle() : findByCode("ART-STD");
		}
		if(result != null) {
			Hibernate.initialize(result);
			detach(result);
		}
		return  result != null ? Optional.of(result) : Optional.empty();
	}

	public List<AccountingArticle> findByAccountingCode(String accountingCode) {
		return getEntityManager().createNamedQuery("AccountingArticle.findByAccountingCode", AccountingArticle.class)
				.setParameter("accountingCode", accountingCode)
				.getResultList();
	}

	@SuppressWarnings("rawtypes")
	public AccountingArticle getAccountingArticleByChargeInstance(ChargeInstance chargeInstance) {
		if(chargeInstance==null) {
			return null;
		}
		ServiceInstance serviceInstance=chargeInstance.getServiceInstance();
		   Map<String, Object> attributes = new HashMap<>();
		  List<AttributeValue> attributeValues = serviceInstance != null ?
				  serviceInstance.getAttributeInstances().stream().map(ai -> (AttributeValue)ai).collect(toList())
				  : new ArrayList<>();
	       for (AttributeValue attributeValue : attributeValues) {
               Attribute attribute = attributeValue.getAttribute();
               Object value = attribute.getAttributeType().getValue(attributeValue);
               if (value != null) {
                   attributes.put(attributeValue.getAttribute().getCode(), value);
               }
           }
           Optional<AccountingArticle> accountingArticle = Optional.empty();
           try {
        	   accountingArticle = getAccountingArticle(serviceInstance != null ? serviceInstance.getProductVersion().getProduct() : null, chargeInstance.getChargeTemplate(),attributes);
           }catch(BusinessException e) {
           	throw new MeveoApiException(e.getMessage());
           }
           return accountingArticle.isPresent() ? accountingArticle.get() : null;
	}
	
	public List<AccountingArticle> findByTaxClassAndSubCategory(TaxClass taxClass, InvoiceSubCategory invoiceSubCategory) {
		return getEntityManager().createNamedQuery("AccountingArticle.findByTaxClassAndSubCategory", AccountingArticle.class)
				.setParameter("taxClass", taxClass)
				.setParameter("invoiceSubCategory", invoiceSubCategory)
				.getResultList();
	}
}
