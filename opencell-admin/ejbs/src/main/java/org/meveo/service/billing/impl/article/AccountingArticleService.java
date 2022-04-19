package org.meveo.service.billing.impl.article;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.accountingScheme.AccountingCodeMapping;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.cpq.AttributeService;

@Stateless
public class AccountingArticleService extends BusinessService<AccountingArticle> {
	
	@Inject private ArticleMappingLineService articleMappingLineService;
	@Inject private AttributeService attributeService;

	public Optional<AccountingArticle> getAccountingArticle(Product product, Map<String, Object> attributes) throws BusinessException {
		return getAccountingArticle(product, null, attributes, null, null, null);
	}

	public Optional<AccountingArticle> getAccountingArticle(Product product, ChargeTemplate chargeTemplate,
															Map<String, Object> attributes, String param1, String param2, String param3) throws InvalidELException, ValidationException {
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
		if(!StringUtils.isBlank(param1)) {
			articleMappingLines = articleMappingLines.stream()
					.filter(articleMappingLine ->param1.equals(articleMappingLine.getParameter1()))
					.collect(toList());
		}
		if(!StringUtils.isBlank(param2)) {
			articleMappingLines = articleMappingLines.stream()
					.filter(articleMappingLine ->param2.equals(articleMappingLine.getParameter2()))
					.collect(toList());
		}
		if(!StringUtils.isBlank(param3)) {
			articleMappingLines = articleMappingLines.stream()
					.filter(articleMappingLine ->param3.equals(articleMappingLine.getParameter3()))
					.collect(toList());
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
        if (attributeMappingLineMatch.getFullMatchsArticle().size() > 1) {
            throw new ValidationException("More than one accounting article found for product " + product.getId() + " and charge template " + chargeTemplate.getId());
        }
		AccountingArticle result = null;
		if(attributeMappingLineMatch.getFullMatchsArticle().size() == 1) {
			result = attributeMappingLineMatch.getFullMatchsArticle().iterator().next();
		} else {
			ArticleMappingLine bestMatch = attributeMappingLineMatch.getBestMatch();
			result = bestMatch != null ? bestMatch.getAccountingArticle() : findByCode("ART-STD", Arrays.asList("taxClass"));
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
    public AccountingArticle getAccountingArticleByChargeInstance(ChargeInstance chargeInstance) throws InvalidELException, ValidationException {
		return getAccountingArticleByChargeInstance(chargeInstance,null,null,null);
	}

	@SuppressWarnings("rawtypes")
    public AccountingArticle getAccountingArticleByChargeInstance(ChargeInstance chargeInstance,String parameter1,String parameter2,String parameter3) throws InvalidELException, ValidationException {
        if (chargeInstance == null) {
            return null;
        }
        ServiceInstance serviceInstance = chargeInstance.getServiceInstance();
        Map<String, Object> attributes = new HashMap<>();
        List<AttributeValue> attributeValues = serviceInstance != null ? serviceInstance.getAttributeInstances().stream().map(ai -> (AttributeValue) ai).collect(toList()) : new ArrayList<>();
        for (AttributeValue attributeValue : attributeValues) {
            Attribute attribute = attributeValue.getAttribute();
            Object value = attribute.getAttributeType().getValue(attributeValue);
            if (value != null) {
                attributes.put(attributeValue.getAttribute().getCode(), value);
            }
        }
        Optional<AccountingArticle> accountingArticle = Optional.empty();
        accountingArticle = getAccountingArticle(serviceInstance != null && serviceInstance.getProductVersion()!=null ? serviceInstance.getProductVersion().getProduct() : null, chargeInstance.getChargeTemplate(), attributes, parameter1, parameter2, parameter3);

        return accountingArticle.isPresent() ? accountingArticle.get() : null;
    }
	
	public List<AccountingArticle> findByTaxClassAndSubCategory(TaxClass taxClass, InvoiceSubCategory invoiceSubCategory) {
		return getEntityManager().createNamedQuery("AccountingArticle.findByTaxClassAndSubCategory", AccountingArticle.class)
				.setParameter("taxClass", taxClass)
				.setParameter("invoiceSubCategory", invoiceSubCategory)
				.getResultList();
	}

	public AccountingCode getArticleAccountingCode(InvoiceLine invoiceLine, AccountingArticle accountingArticle) {
		// **1** if accountingCodeEL is filled then return the evaluated accountingCode
		if (StringUtils.isNotBlank(accountingArticle.getAccountingCodeEl())) {
			AccountingCode result = evaluateAccountingCodeArticleEl(accountingArticle.getAccountingCodeEl(),
					accountingArticle, invoiceLine.getInvoice(), AccountingCode.class);

			if (result == null) {
				throw new BusinessException(".::TODO FROM US SPEC::.");
			}

			return result;
		}
		// **2** if not, if accountingCodeMapping table contains related lines get the best matched line

		// Find related AccountingCodeMappping
		List<AccountingCodeMapping> codeMappings = getEntityManager().createNamedQuery("AccountingCodeMapping.findByAccountingArticle")
				.setParameter("ACCOUNTING_ARTICLE_ID", accountingArticle.getId()).getResultList();

		if (codeMappings == null || !codeMappings.isEmpty()) {
			AccountingCode accountingCode = accountingCodeMappingMatching(codeMappings, invoiceLine.getInvoice(), accountingArticle);

			if (accountingCode == null) {
				throw new BusinessException(".::TODO FROM US SPEC::.");
			}

			return accountingCode;

		}

		// **3** if no line is matched, return accounting code single value (accountingArticle.accountingCode field)
		return accountingArticle.getAccountingCode();

	}

	private <T> T evaluateAccountingCodeArticleEl(String expression,
												  AccountingArticle accountingArticle,
												  Invoice invoice,
												  Class<T> type) throws InvalidELException {
		if (StringUtils.isNotBlank(expression)) {
			// EL will have access to the following variables: article / billingAccount / seller
			BillingAccount billingAccount = invoice.getBillingAccount();
			Seller seller = invoice.getSeller();

			Map<Object, Object> contextMap = new HashMap<>();
			contextMap.put("article", accountingArticle);
			contextMap.put("billingAccount", billingAccount);
			contextMap.put("seller", seller);
			contextMap.put("ratedTransaction", null); // always null in this case

			try {
				return ValueExpressionWrapper.evaluateExpression(expression, contextMap, type);
			} catch (Exception e) {
				log.warn("Error when evaluate accountingCodeEl for AccountingArticle id={}", accountingArticle.getId());
				return null;
			}
		}

		return null;
	}

	private AccountingCode accountingCodeMappingMatching(List<AccountingCodeMapping> codeMappings, Invoice invoice,
														 AccountingArticle accountingArticle) {
		// Prepare vars
		BillingAccount billingAccount = invoice.getBillingAccount();
		Seller seller = invoice.getSeller();
		TradingCountry sellerCurrency = seller.getTradingCountry();
		TradingCurrency billingCurrency = billingAccount.getTradingCurrency();
		TradingCountry billingCountry = billingAccount.getTradingCountry();
		String columCriteriaEL = evaluateAccountingCodeArticleEl(accountingArticle.getColumCriteriaEL(),
				accountingArticle, invoice, String.class);

		Map<String, AccountingCodeMapping> map = new HashMap<>(); // TODO exact matching, waiting for Rachid response
		codeMappings.forEach(acm -> {
			map.put(buildKey(Optional.ofNullable(acm.getAccountingArticle()).map(BaseEntity::getId),
					Optional.ofNullable(acm.getBillingCountry()).map(BaseEntity::getId),
					Optional.ofNullable(acm.getBillingCurrency()).map(BaseEntity::getId),
					Optional.ofNullable(acm.getSellerCountry()).map(BaseEntity::getId),
					Optional.ofNullable(acm.getSeller()).map(BaseEntity::getId),
					acm.getCriteriaElValue()), acm);
		});

		// Get matched AccountingCodeMapping
		AccountingCodeMapping matched = map.get(buildKey(Optional.of(accountingArticle).map(BaseEntity::getId),
				Optional.ofNullable(billingCountry).map(BaseEntity::getId),
				Optional.ofNullable(billingCurrency).map(BaseEntity::getId),
				Optional.ofNullable(sellerCurrency).map(BaseEntity::getId),
				Optional.of(seller).map(BaseEntity::getId),
				columCriteriaEL));

		if (matched == null) {
			throw new BusinessException(".::TODO FROM US SPEC::.");
		}

		return matched.getAccountingCode();
	}

	private String buildKey(Object... o) {
		StringBuilder key = new StringBuilder();
		for (Object value : o) {
			key.append(value == null ? "" : value.toString());
		}

		return key.toString();
	}

}
