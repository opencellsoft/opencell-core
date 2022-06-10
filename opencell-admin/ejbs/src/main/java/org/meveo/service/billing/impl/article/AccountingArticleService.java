package org.meveo.service.billing.impl.article;

import static java.util.stream.Collectors.toList;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.*;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.accountingScheme.AccountingCodeMapping;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.cpq.AttributeService;

@Stateless
public class AccountingArticleService extends BusinessService<AccountingArticle> {

	@Inject private ArticleMappingLineService articleMappingLineService;
	@Inject private AttributeService attributeService;
	@Inject
	private AccountingCodeService accountingCodeService;

	public Optional<AccountingArticle> getAccountingArticle(Product product, Map<String, Object> attributes) throws BusinessException {
		return getAccountingArticle(product, null, attributes, null);
	}

	public Optional<AccountingArticle> getAccountingArticle(Product product, ChargeTemplate chargeTemplate,
															Map<String, Object> attributes, WalletOperation walletOperation) throws InvalidELException, ValidationException {
		List<ArticleMappingLine> articleMappingLines = null;
		articleMappingLines = articleMappingLineService.findByProductAndCharge(product, chargeTemplate);
		if(articleMappingLines.isEmpty() && chargeTemplate!=null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(null, chargeTemplate);
		}
		if(articleMappingLines.isEmpty() && product != null) {
			articleMappingLines = articleMappingLineService.findByProductAndCharge(product, null);
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

	private boolean filterMappingLines(WalletOperation walletOperation, String mappingExpressionEl) {
		if (!StringUtils.isBlank(mappingExpressionEl)) {
			Object result = evaluateExpression(mappingExpressionEl,
					Map.of("walletOperation", walletOperation), Boolean.class);
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
        Map<String, Object> attributes = new HashMap<>();
        List<AttributeValue> attributeValues = serviceInstance != null ? serviceInstance.getAttributeInstances().stream().map(ai -> (AttributeValue) ai).collect(toList()) : new ArrayList<>();
        for (AttributeValue attributeValue : attributeValues) {
            Attribute attribute = attributeValue.getAttribute();
            Object value = attribute.getAttributeType().getValue(attributeValue);
            if (value != null) {
                attributes.put(attributeValue.getAttribute().getCode(), value);
            }
        }
        Optional<AccountingArticle> accountingArticle;
        accountingArticle = getAccountingArticle(serviceInstance != null && serviceInstance.getProductVersion()!=null ? serviceInstance.getProductVersion().getProduct() : null, chargeInstance.getChargeTemplate(), attributes, walletOperation);

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

		if (codeMappings == null || !codeMappings.isEmpty()) {
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

			int mappingScore = 0;
			mappingScore += ((map.getBillingCountry() == null && billingCountry == null) || (map.getBillingCountry() != null && billingCountry == null)) ? 0 : (map.getBillingCountry() != null && billingCountry != null) && map.getBillingCountry().getId().equals(billingCountry.getId()) ? 1000 : -1000;
			mappingScore += ((map.getBillingCurrency() == null && billingCurrency == null) || (map.getBillingCurrency() != null && billingCurrency == null))  ? 0 : (map.getBillingCurrency() != null && billingCurrency != null) && map.getBillingCurrency().getId().equals(billingCurrency.getId()) ? 500 : -500;
			mappingScore += ((map.getSellerCountry() == null && sellerCountry == null) || (map.getSellerCountry() != null && sellerCountry == null))  ? 0 : (map.getSellerCountry() != null && sellerCountry != null) && map.getSellerCountry().getId().equals(sellerCountry.getId()) ? 250 : -250;
			mappingScore += ((map.getSeller() == null && seller == null) || (map.getSeller() != null && seller == null))  ? 0 : (map.getSeller() != null && seller != null) && map.getSeller().getId().equals(seller.getId()) ? 150 : -150;
			mappingScore += ((map.getCriteriaElValue() == null && StringUtils.isBlank(columCriteriaEL) || map.getCriteriaElValue() != null && StringUtils.isBlank(columCriteriaEL))) ? 0 : (map.getCriteriaElValue() != null && map.getCriteriaElValue().equals(columCriteriaEL)) ? 50 : -50;

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

}
