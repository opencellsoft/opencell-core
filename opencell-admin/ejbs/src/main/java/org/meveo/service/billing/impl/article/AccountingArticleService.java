package org.meveo.service.billing.impl.article;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.accountingScheme.AccountingCodeMapping;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;

@Stateless
public class AccountingArticleService extends BusinessService<AccountingArticle> {

	@Inject 
	private ArticleMappingLineService articleMappingLineService;
	@Inject 
	private AttributeService attributeService;
	@Inject
	private AccountingCodeService accountingCodeService;
    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;
    
    @Inject
    private OfferTemplateService offerTemplateService;
    
    @Inject
    private ServiceInstanceService serviceInstanceService;

	 private String multiValuesAttributeSeparator = ";";
	    
	 @PostConstruct
	 private void init() {
		 multiValuesAttributeSeparator = paramBeanFactory.getInstance().getProperty("attribute.multivalues.separator", ";");
	 }
	 
	public Optional<AccountingArticle> getAccountingArticle(Product product, Map<String, Object> attributes) throws BusinessException {
		return getAccountingArticle(product, null, attributes, null);
	}

	public Optional<AccountingArticle> getAccountingArticle(Product product, ChargeTemplate chargeTemplate, OfferTemplate offer,
															Map<String, Object> attributes, WalletOperation walletOperation) throws InvalidELException, ValidationException {
		List<ArticleMappingLine> articleMappingLines = null;
		String param1 = ofNullable(walletOperation).map(WalletOperation::getParameter1).orElse(null);
		String param2 = ofNullable(walletOperation).map(WalletOperation::getParameter2).orElse(null);
		String param3 = ofNullable(walletOperation).map(WalletOperation::getParameter3).orElse(null);
		articleMappingLines = articleMappingLineService.findByProductAndCharge(product, chargeTemplate, offer, null, null, null);
		if(articleMappingLines.isEmpty() && chargeTemplate != null && product != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(product, chargeTemplate, null, null, null, null);
		}
		if(articleMappingLines.isEmpty() && chargeTemplate != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(null, chargeTemplate, null, null, null, null);
		}
		if(articleMappingLines.isEmpty() && offer != null && product != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(product, null, offer, null, null, null);
		}
		if(articleMappingLines.isEmpty() && product != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(product, null, null, null, null, null);
		}
		if(articleMappingLines.isEmpty() && offer != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(null, null, offer, null, null, null);
		}
		if(articleMappingLines.isEmpty() && walletOperation != null && walletOperation.getParameter1() != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(null, null, null, param1, null, null);
		}
		if(articleMappingLines.isEmpty() && walletOperation != null && walletOperation.getParameter2() != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(null, null, null, null, param2, null);
		}
		if(articleMappingLines.isEmpty() && walletOperation != null && walletOperation.getParameter3() != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(null, null, null, null, null, param3);
		}
		if(walletOperation != null && !StringUtils.isBlank(walletOperation.getParameter1())) {
			articleMappingLines = articleMappingLines.stream()
					.filter(articleMappingLine -> StringUtils.isBlank(articleMappingLine.getParameter1())
							|| walletOperation.getParameter1().equals(articleMappingLine.getParameter1()))
					.collect(toList());
		}
		if(walletOperation != null && !StringUtils.isBlank(walletOperation.getParameter2())) {
			articleMappingLines = articleMappingLines.stream()
					.filter(articleMappingLine -> StringUtils.isBlank(articleMappingLine.getParameter2())
							|| walletOperation.getParameter2().equals(articleMappingLine.getParameter2()))
					.collect(toList());
		}
		if(walletOperation != null && !StringUtils.isBlank(walletOperation.getParameter3())) {
			articleMappingLines = articleMappingLines.stream()
					.filter(articleMappingLine -> StringUtils.isBlank(articleMappingLine.getParameter3())
							|| walletOperation.getParameter3().equals(articleMappingLine.getParameter3()))
					.collect(toList());
		}
		if(articleMappingLines != null) {
			articleMappingLines = articleMappingLines
					.stream()
					.filter(articleMappingLine -> filterMappingLines(walletOperation, articleMappingLine.getMappingKeyEL()))
					.collect(toList());
		}
		AttributeMappingLineMatch attributeMappingLineMatch = new AttributeMappingLineMatch();
		articleMappingLines.forEach(aml -> {
			List<AttributeMapping> matchedAttributesMapping = new ArrayList<>();
			List<AttributeMapping> attributesMapping = aml.getAttributesMapping();
				AtomicBoolean continueProcess = new AtomicBoolean(true);
				if (OperatorEnum.AND == aml.getAttributeOperator()) {
					attributesMapping.forEach(attributeMapping -> {
						if (continueProcess.get()) {
							if (checkAttribute(product, walletOperation, attributes, attributeMapping)) {
								matchedAttributesMapping.add(attributeMapping);
							} else {
								// for AND operator, if at least we have 1 unmatchedAttributs (else), all previous matchedAttribut shall not taken into account
								matchedAttributesMapping.clear();
								continueProcess.set(false);
							}
						}
					});
				} else if (OperatorEnum.OR == aml.getAttributeOperator()) {
					attributesMapping.forEach(attributeMapping -> {
						if (continueProcess.get()) {
							if (checkAttribute(product, walletOperation, attributes, attributeMapping)) {
								matchedAttributesMapping.add(attributeMapping);
								continueProcess.set(false);
							}
						}
					});
				}

				Set<Attribute> matchedAttributes = matchedAttributesMapping.stream().map(AttributeMapping::getAttribute).collect(Collectors.toSet());
				//fullMatch
				if (attributesMapping.size() >= matchedAttributesMapping.size() && (matchedAttributes.size() == attributes.keySet().size())) {
					attributeMappingLineMatch.addFullMatch(aml);
				} else {
					if (!(attributesMapping.size() > 0 && matchedAttributesMapping.size() == 0)) {
						attributeMappingLineMatch.addPartialMatch(aml, matchedAttributesMapping.size());
					}
				}
		});
		if (attributeMappingLineMatch.getFullMatchsArticle().size() > 1) {
			throw new RatingException("More than one accounting article found for product " + product.getId() + " and charge template " + chargeTemplate.getId());
		}
		AccountingArticle result = null;
		if(attributeMappingLineMatch.getFullMatchsArticle().size() == 1) {
			result = attributeMappingLineMatch.getFullMatchsArticle().iterator().next();
		} else {
			ParamBean paramBean = ParamBean.getInstance();
			String defaultArticle = paramBean.getProperty("default.article", "ART-STD");
			ArticleMappingLine bestMatch = attributeMappingLineMatch.getBestMatch();
			result = bestMatch != null ? bestMatch.getAccountingArticle() : findByCode(defaultArticle, Arrays.asList("taxClass"));
		}
		if(result != null) {
			Hibernate.initialize(result);
			detach(result);
		}
		return  result != null ? Optional.of(result) : Optional.empty();
	}

	public Optional<AccountingArticle> getAccountingArticle(Product product, ChargeTemplate chargeTemplate,
															Map<String, Object> attributes, WalletOperation walletOperation) throws InvalidELException, ValidationException {
		return getAccountingArticle(product, chargeTemplate,  ofNullable(walletOperation).map(WalletOperation::getOfferTemplate).orElse(null), attributes, walletOperation);

	}

	private void getBestMatchedArticleMappingLines(Product product,
												   ChargeTemplate chargeTemplate,
												   OfferTemplate offer,
												   String param1, String param2, String param3,
												   List<ArticleMappingLine> articleMappingLines) {
		List<ArticleMappingLine> mappings = articleMappingLineService.findAll();
		Map<Long, Integer> matchingScore = new HashMap<>();
		mappings.forEach(map -> {

			int offerScore = (map.getOfferTemplate() == null && offer == null) || (map.getOfferTemplate() != null && offer == null) || (map.getOfferTemplate() == null && offer != null) ? 0
					: (map.getOfferTemplate() != null && offer != null) && map.getOfferTemplate().getId().equals(offer.getId())	? 1 : -10;
			int productScore = (map.getProduct() == null && product == null) || (map.getProduct() != null && product == null) || (map.getProduct() == null && product != null) ? 0
					: (map.getProduct() != null && product != null) && map.getProduct().getId().equals(product.getId())	? 1 : -10;
			int chargeScore = (map.getChargeTemplate() == null && chargeTemplate == null) || (map.getChargeTemplate() != null && chargeTemplate == null) || (map.getProduct() == null && chargeTemplate != null) ? 0
					: (map.getChargeTemplate() != null && chargeTemplate != null) && map.getChargeTemplate().getId().equals(chargeTemplate.getId())	? 1 : -10;
			int param1Score = (StringUtils.isBlank(map.getParameter1()) && StringUtils.isBlank(param1)) ||  (StringUtils.isNotBlank(map.getParameter1()) && StringUtils.isBlank(param1)) || (StringUtils.isBlank(map.getParameter1()) && StringUtils.isNotBlank(param1)) ? 0
					: (StringUtils.isNotBlank(map.getParameter1()) && StringUtils.isNotBlank(param1)) && map.getParameter1().equals(param1)	? 1 : -10;
			int param2Score = (StringUtils.isBlank(map.getParameter2()) && StringUtils.isBlank(param2)) ||  (StringUtils.isNotBlank(map.getParameter2()) && StringUtils.isBlank(param2)) || (StringUtils.isBlank(map.getParameter2()) && StringUtils.isNotBlank(param2)) ? 0
					: (StringUtils.isNotBlank(map.getParameter2()) && StringUtils.isNotBlank(param2)) && map.getParameter2().equals(param2)	? 1 : -10;
			int param3Score = (StringUtils.isBlank(map.getParameter3()) && StringUtils.isBlank(param3)) ||  (StringUtils.isNotBlank(map.getParameter3()) && StringUtils.isBlank(param3)) || (StringUtils.isBlank(map.getParameter3()) && StringUtils.isNotBlank(param3)) ? 0
					: (StringUtils.isNotBlank(map.getParameter3()) && StringUtils.isNotBlank(param3)) && map.getParameter3().equals(param3)	? 1 : -10;

			int mappingScore = offerScore + productScore + chargeScore + param1Score + param2Score + param3Score;
			if (mappingScore > 0) {
				matchingScore.put(map.getId(), mappingScore);
			}

		});

		if (matchingScore.size() > 0) {
			List<Integer> results = matchingScore.entrySet()
					.stream()
					.sorted(Map.Entry.comparingByValue())
					.map(Map.Entry::getValue).collect(Collectors.toList());

			Collections.reverse(results);


			Integer highScore = results.get(0);
			List<ArticleMappingLine> matchedArticleMappingLine = new ArrayList<>();
			// Keep only the highest score matched mapping lines
			matchingScore.forEach((mappingId, integer) -> {
				if (highScore.equals(integer)) {
					matchedArticleMappingLine.addAll(mappings.stream().filter(map -> mappingId.equals(map.getId())).collect(Collectors.toList()));
				}
			});

			articleMappingLines.addAll(matchedArticleMappingLine);
		}
	}

	private boolean filterMappingLines(WalletOperation walletOperation, String mappingExpressionEl) {
		if (!StringUtils.isBlank(mappingExpressionEl)) {
			Map<Object, Object> context = new HashMap<>();
			context.put("walletOperation",walletOperation);
			Object result = evaluateExpression(mappingExpressionEl, context, Boolean.class);
			try {
				return (Boolean) result;
			} catch (Exception exception) {
				throw new BusinessException("Expression " + mappingExpressionEl + " do not evaluate to boolean");
			}

		} else {
			return true;
		}
	}

	public List<AccountingArticle> findByAccountingCode(String accountingCode) {
		return getEntityManager().createNamedQuery("AccountingArticle.findByAccountingCode", AccountingArticle.class)
				.setParameter("accountingCode", accountingCode)
				.getResultList();
	}	
	
    public AccountingArticle getAccountingArticleByChargeInstance(ChargeInstance chargeInstance) throws InvalidELException, ValidationException {
		return getAccountingArticleByChargeInstance(chargeInstance, null);
	}

	@SuppressWarnings("rawtypes")
    public AccountingArticle getAccountingArticleByChargeInstance(ChargeInstance chargeInstance, WalletOperation walletOperation) throws InvalidELException, ValidationException {
        if (chargeInstance == null) {
            return null;
        }
        ServiceInstance serviceInstance = chargeInstance.getServiceInstance();
        Map<String, Object> attributes = extractAttributes(serviceInstance);
        Optional<AccountingArticle> accountingArticle;
		accountingArticle = getAccountingArticle(serviceInstance != null && serviceInstance.getProductVersion()!=null ? serviceInstance.getProductVersion().getProduct() : null,
				chargeInstance.getChargeTemplate(),
				chargeInstance.getSubscription().getOffer(),
				attributes,
				walletOperation);

        return accountingArticle.isPresent() ? accountingArticle.get() : null;
    }
	
	public AccountingArticle getAccountingArticle(ServiceInstance serviceInstance, ChargeTemplate chargeTemplate, OfferTemplate offer, WalletOperation walletOperation) {
		Optional<AccountingArticle> accountingArticle = getAccountingArticle(serviceInstance != null && serviceInstance.getProductVersion()!=null ? serviceInstance.getProductVersion().getProduct() : null, chargeTemplate, offer, extractAttributes(serviceInstance), walletOperation);
		return accountingArticle.isPresent() ? accountingArticle.get() : null;
	}

	private Map<String, Object> extractAttributes(ServiceInstance serviceInstance) {
		Map<String, Object> attributes = new HashMap<>();
        List<AttributeValue> attributeValues = serviceInstance != null ? serviceInstance.getAttributeInstances().stream().map(ai -> (AttributeValue) ai).collect(toList()) : new ArrayList<>();
        for (AttributeValue attributeValue : attributeValues) {
            Attribute attribute = attributeValue.getAttribute();
            Object value = attribute.getAttributeType().getValue(attributeValue);
            if (value != null) {
                attributes.put(attributeValue.getAttribute().getCode(), value);
            }
        }
		return attributes;
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
			String resultEl = evaluateAccountingCodeArticleEl(accountingArticle.getAccountingCodeEl(),
					accountingArticle, invoiceLine.getInvoice(), String.class);

			if (StringUtils.isBlank(resultEl)) {
				throw new BusinessException("No accounting code found for EL=" + accountingArticle.getAccountingCodeEl());
			}

			AccountingCode result = accountingCodeService.findByCode(resultEl);

			if (result == null) {
				throw new BusinessException("No accounting code found for code=" + resultEl);
			}

			return result;
		}
		// **2** if not, if accountingCodeMapping table contains related lines get the best matched line

		// Find related AccountingCodeMappping
		List<AccountingCodeMapping> codeMappings = getEntityManager().createNamedQuery("AccountingCodeMapping.findByAccountingArticle")
				.setParameter("ACCOUNTING_ARTICLE_ID", accountingArticle.getId()).getResultList();

		if (codeMappings != null && !codeMappings.isEmpty()) {
			AccountingCode accountingCode = accountingCodeMappingMatching(codeMappings, invoiceLine.getInvoice(), accountingArticle);

			if (accountingCode != null) {
				return accountingCode;
			}

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
				throw new BusinessException("Error during evaluate EL for AccountingArticle id=" + accountingArticle.getId());
			}
		}

		return null;
	}

	private AccountingCode accountingCodeMappingMatching(List<AccountingCodeMapping> mappings, Invoice invoice,
														 AccountingArticle accountingArticle) {
		// Prepare vars
		TradingCountry billingCountry = invoice.getBillingAccount().getTradingCountry();
		TradingCurrency billingCurrency = invoice.getBillingAccount().getTradingCurrency();
		TradingCountry sellerCountry = invoice.getSeller().getTradingCountry();
		Seller seller = invoice.getSeller();
		String columCriteriaEL = evaluateAccountingCodeArticleEl(accountingArticle.getColumnCriteriaEL(),
				accountingArticle, invoice, String.class);

		Map<Long, Integer> matchingScore = new HashMap<>();

		mappings.forEach(map -> {

			int billCountryScore = map.getBillingCountry() == null && billingCountry == null ? 0
					: map.getBillingCountry() != null && billingCountry == null ? -1000
					: (map.getBillingCountry() != null && billingCountry != null) && map.getBillingCountry().getId().equals(billingCountry.getId())	? 1000
					: map.getBillingCountry() == null && billingCountry != null ? -1000
					: -9999;
			int billCurrencyScore = map.getBillingCurrency() == null && billingCurrency == null ? 0
					: map.getBillingCurrency() != null && billingCurrency == null ? -500
					: (map.getBillingCurrency() != null && billingCurrency != null) && map.getBillingCurrency().getId().equals(billingCurrency.getId())	? 500
					: map.getBillingCurrency() == null && billingCurrency != null ? -500
					: -9999;
			int sellerCountryScore = map.getSellerCountry() == null && sellerCountry == null ? 0
					: map.getSellerCountry() != null && sellerCountry == null ? -250
					: (map.getSellerCountry() != null && sellerCountry != null) && map.getSellerCountry().getId().equals(sellerCountry.getId())	? 250
					: map.getSellerCountry() == null && sellerCountry != null ? -250
					: -9999;
			int sellerScore = map.getSeller() == null && seller == null ? 0
					: map.getSeller() != null && seller == null ? -150
					: (map.getSeller() != null && seller != null) && map.getSeller().getId().equals(seller.getId())	? 150
					: map.getSeller() == null && seller != null ? -150
					: -9999;
			int columCriteriaELScore = StringUtils.isBlank(map.getCriteriaElValue()) && StringUtils.isBlank(columCriteriaEL) ? 0
					: StringUtils.isNotBlank(map.getCriteriaElValue()) && StringUtils.isBlank(columCriteriaEL) ? 0 // If no "Column criteria EL" is set, the "Criteria EL value" column is ignored
					: (StringUtils.isNotBlank(map.getCriteriaElValue()) && StringUtils.isNotBlank(columCriteriaEL)) && map.getCriteriaElValue().equals(columCriteriaEL)	? 50
					: StringUtils.isBlank(map.getCriteriaElValue()) && StringUtils.isNotBlank(columCriteriaEL) ? -50
					: -9999;

			int mappingScore = billCountryScore + billCurrencyScore + sellerCountryScore + sellerScore + columCriteriaELScore;
			if (mappingScore > 0) {
				matchingScore.put(map.getId(), mappingScore);
			}

		});

		if (matchingScore.size() == 0) {
			return null;
		}

		List<Integer> results = matchingScore.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getValue).collect(Collectors.toList());

		Collections.reverse(results);

		if (results.size() > 1 && results.get(0).equals(results.get(1))) {
			throw new BusinessException("More than one AccountingCode found during matching with AccountingCodeMapping of AccountingArticle id="
					+ accountingArticle.getId()
					+ (invoice.getBillingAccount() == null ? "" : " for BillingAccount code=" + invoice.getBillingAccount().getCode())
					+ (seller == null ? "" : " and Seller code=" + seller.getCode()));
		}

		AtomicReference<AccountingCode> result = new AtomicReference<>();

		matchingScore.forEach((mappingId, integer) -> {
			if (results.get(0).equals(integer)) {
				mappings.stream().filter(map -> mappingId.equals(map.getId()))
						.findAny().ifPresent(mapping -> result.set(mapping.getAccountingCode()));
			}
		});

		return result.get();

	}

	private boolean checkAttribute(Product product, WalletOperation walletOperation, Map<String, Object> attributes, AttributeMapping attributeMapping) {
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
					List<String> source = Arrays.asList(attributeMapping.getAttributeValue().split(multiValuesAttributeSeparator));
					List<Object> input;
					if (value instanceof Collection) {
						input = (List) value;
					} else {
						input = Arrays.asList(value.toString().split(multiValuesAttributeSeparator));
					}

					return valueCompareCollection(attributeMapping.getOperator(), source, input);
				case EXPRESSION_LANGUAGE:
					Object result = attributeService.evaluateElExpressionAttribute(value.toString(), product, null, null, walletOperation, Object.class);
					if (value instanceof Collection) {
						List<String> sourceEL = Arrays.asList(attributeMapping.getAttributeValue().split(multiValuesAttributeSeparator));
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
					if (StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr).compareTo(Double.valueOf(sourceAttributeValue)) == 0) {
							return true;
						}
					}
					if (sourceAttributeValue.equals(convertedValueStr))
						return true;
					break;
				case NOT_EQUAL:
					if (StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr).compareTo(Double.valueOf(sourceAttributeValue)) != 0) {
							return true;
						}
					}
					if (!sourceAttributeValue.equals(convertedValueStr))
						return true;
					break;
				case LESS_THAN:
					if (StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) < Double.valueOf(sourceAttributeValue))
							return true;
					}
					break;
				case LESS_THAN_OR_EQUAL:
					if (StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) <= Double.valueOf(sourceAttributeValue))
							return true;
					}
					break;
				case GREATER_THAN:
					if (StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
						if (Double.valueOf(convertedValueStr) > Double.valueOf(sourceAttributeValue))
							return true;
					}
					break;

				case GREATER_THAN_OR_EQUAL:
					if (StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
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
	

    public AccountingArticle getDefaultAccountingArticle() {
        String articleCode = ParamBean.getInstance().getProperty("accountingArticle.advancePayment.defautl.code", "ADV-STD");

        AccountingArticle accountingArticle = findByCode(articleCode);
        if (accountingArticle == null)
            throw new EntityDoesNotExistsException(AccountingArticle.class, articleCode);
        return accountingArticle;
    }

	public AccountingArticle getAccountingArticle(Long serviceInstanceId, Long chargeTemplateId, Long offerTemplateId) {
		return getAccountingArticle(serviceInstanceService.findFetchProductById(serviceInstanceId), chargeTemplateService.findById(chargeTemplateId), offerTemplateService.findById(offerTemplateId), null);
	}

}
