package org.meveo.service.billing.impl.article;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.enums.RuleOperatorEnum;
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
		if(articleMappingLines.isEmpty() && chargeTemplate!=null) {
			articleMappingLines=articleMappingLineService.findByProductAndCharge(product, null);
		}else if(chargeTemplate==null) {
			productCharges.addAll(product.getProductCharges().stream()
					.map(pc -> pc.getChargeTemplate())
					.collect(toList()));
			articleMappingLines = articleMappingLines.stream()
					.filter(aml -> aml.getChargeTemplate() == null || productCharges.contains(aml.getChargeTemplate()))
					.collect(toList());;
		}

		AttributeMappingLineMatch attributeMappingLineMatch = new AttributeMappingLineMatch();
		articleMappingLines.forEach(aml -> {
			List<AttributeMapping> matchedAttributesMapping = new ArrayList<>();
			aml.getAttributesMapping().size();
			AtomicBoolean continueProcess = new AtomicBoolean(true);
			if (OperatorEnum.AND == aml.getAttributeOperator()) {
				aml.getAttributesMapping().forEach(attributeMapping -> {
					if (continueProcess.get()) {
						if (checkAttribute(product, attributes, attributeMapping)) {
							matchedAttributesMapping.add(attributeMapping);
						} else {
							// for AND operator, if at least we have 1 unmatchedAttributs (else), all previous matchedAttribut shall not taken into account
							matchedAttributesMapping.clear();
							continueProcess.set(false);
						}
					}
				});
			} else if (OperatorEnum.OR == aml.getAttributeOperator()) {
				aml.getAttributesMapping().forEach(attributeMapping -> {
					if (continueProcess.get()) {
						if (checkAttribute(product, attributes, attributeMapping)) {
							matchedAttributesMapping.add(attributeMapping);
							continueProcess.set(false);
						}
					}
				});
			}
			//fullMatch
			if(aml.getAttributesMapping().size() >= matchedAttributesMapping.size() && (matchedAttributesMapping.size() == attributes.keySet().size())) {
				attributeMappingLineMatch.addFullMatch(aml);
			}else{
				if (!(aml.getAttributesMapping().size() > 0 && matchedAttributesMapping.size() == 0)) {
					attributeMappingLineMatch.addPartialMatch(aml, matchedAttributesMapping.size());
				}
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

	private boolean checkAttribute(Product product, Map<String, Object> attributes, AttributeMapping attributeMapping) {
		final Attribute attribute = attributeMapping.getAttribute();
		if (attributes.get(attribute.getCode()) != null) {
			isValidOperator(attributeMapping.getAttribute(), attributeMapping.getOperator());
			Object value = attributes.get(attributeMapping.getAttribute().getCode());
			switch (attribute.getAttributeType()) {
				case TEXT:
				case NUMERIC:
					return valueCompare(attributeMapping.getOperator(), attributeMapping.getAttributeValue(), value);
				case LIST_TEXT:
				case LIST_NUMERIC:
				case LIST_MULTIPLE_TEXT:
				case LIST_MULTIPLE_NUMERIC:
					List<String> source = Arrays.asList(attributeMapping.getAttributeValue().split(";"));
					List<Object> input;
					if (value instanceof Collection) {
						input = (List) value;
					} else {
						input = Arrays.asList(value.toString().split(";"));
					}
					return valueCompareCollection(attributeMapping.getOperator(), source, input);
				case EXPRESSION_LANGUAGE:
					Object result = attributeService.evaluateElExpressionAttribute(value.toString(), product, null, null, Object.class);
					if (value instanceof Collection) {
						List<String> sourceEL = Arrays.asList(attributeMapping.getAttributeValue().split(";"));
						List<Object> inputEL = (List) value;
						return valueCompareCollection(attributeMapping.getOperator(), sourceEL, inputEL);
					}
					return valueCompare(attributeMapping.getOperator(), attributeMapping.getAttributeValue(), result);
				case TOTAL:
				case COUNT:
				default:
					return valueCompare(attributeMapping.getOperator(), attributeMapping.getAttributeValue(), value);
			}
		}
		return false;
	}

	private boolean valueCompareCollection(RuleOperatorEnum operator, List<String> source, List<Object> input) {
		if (CollectionUtils.isEmpty(source) && CollectionUtils.isEmpty(input)) {
			return true;
		}

		if ((CollectionUtils.isEmpty(source) && CollectionUtils.isNotEmpty(input)) ||
				(CollectionUtils.isNotEmpty(source) && CollectionUtils.isEmpty(input))) {
			return false;
		}

		List<Object> contains = new ArrayList<>();
		for (Object o : source) {
			if (input.contains(o)) {
				contains.add(o);
			}
		}

		switch (operator) {
			case EQUAL:
				return contains.size() == input.size();
			case NOT_EQUAL:
				return contains.size() == 0;
			case EXISTS:
				return contains.size() > 1;
			default:
				return false;
		}
	}

	private boolean valueCompare(RuleOperatorEnum operator, String sourceAttributeValue, Object convertedValue) {
		if (convertedValue == null && StringUtils.isBlank(sourceAttributeValue)) {
			return true;
		}

		if (sourceAttributeValue != null && operator != null) {
			String convertedValueStr = convertedValue != null ? String.valueOf(convertedValue) : null;
			switch (operator) {
				case EQUAL:
					if (convertedValueStr != null && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr).compareTo(Double.valueOf(sourceAttributeValue)) == 0) {
							return true;
						}
					}
					if (sourceAttributeValue.equals(convertedValueStr))
						return true;
					break;
				case NOT_EQUAL:
					if (convertedValueStr != null && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr).compareTo(Double.valueOf(sourceAttributeValue)) != 0) {
							return true;
						}
					}
					if (!sourceAttributeValue.equals(convertedValueStr))
						return true;
					break;
				case LESS_THAN:
					if (convertedValueStr != null && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) < Double.valueOf(sourceAttributeValue))
							return true;
					}
					break;
				case LESS_THAN_OR_EQUAL:
					if (convertedValueStr != null && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) <= Double.valueOf(sourceAttributeValue))
							return true;
					}
					break;
				case GREATER_THAN:
					if (convertedValueStr != null && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) > Double.valueOf(sourceAttributeValue))
							return true;
					}
					break;
				case GREATER_THAN_OR_EQUAL:
					if (convertedValueStr != null && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) >= Double.valueOf(sourceAttributeValue))
							return true;
					}
			}
		}
		return false;
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
