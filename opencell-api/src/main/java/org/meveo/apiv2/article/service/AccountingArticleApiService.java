package org.meveo.apiv2.article.service;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.ws.rs.*;

import org.meveo.admin.exception.*;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.article.*;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.accountingScheme.AccountingCodeMapping;
import org.meveo.model.admin.*;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.*;
import org.meveo.model.cpq.Product;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.accountingscheme.*;
import org.meveo.service.admin.impl.*;
import org.meveo.service.billing.impl.*;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.tax.TaxClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingArticleApiService implements AccountingArticleServiceBase {

    private List<String> fetchFields;

    @Inject
    private AccountingArticleService accountingArticleService;
    
    @Inject
    private TaxClassService taxClassService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;
    
    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private AccountingCodeMappingService accountingCodeMappingService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private SellerService sellerService;

    @Inject
    private AccountingCodeService accountingCodeService;

    Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void initService() {
        fetchFields = asList("taxClass", "invoiceSubCategory",
                "articleFamily", "accountingCode", "accountingCodeMappings", "invoiceType", "allowanceCode");
    }

    @Override
    public AccountingArticle create(AccountingArticle accountingArticle) {
        AccountingArticle entity = accountingArticleService.findByCode(accountingArticle.getCode());
        if (entity != null) {
            throw new EntityAlreadyExistsException(AccountingArticle.class, accountingArticle.getCode());
        }
        InvoiceSubCategory invoiceSubCategory = accountingArticleService.tryToFindByCodeOrId(accountingArticle.getInvoiceSubCategory());
        if (invoiceSubCategory == null)
            throw new BadRequestException("No invoice sub category found with id: " + accountingArticle.getInvoiceSubCategory().getId());
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);

        TaxClass taxClass = accountingArticleService.tryToFindByCodeOrId(accountingArticle.getTaxClass());
        if (taxClass == null)
            throw new BadRequestException("No taxClass found for id : " + accountingArticle.getTaxClass().getId());
        accountingArticle.setTaxClass(taxClass);

        if (accountingArticle.getAccountingCode() != null) {
            AccountingCode accountingCode = accountingArticleService.tryToFindByCodeOrId(accountingArticle.getAccountingCode());
            if (accountingCode == null)
                throw new BadRequestException("No accountingCode found");
            accountingArticle.setAccountingCode(accountingCode);
        }

        if (accountingArticle.getArticleFamily() != null) {
            ArticleFamily articleFamily = accountingArticleService.tryToFindByCodeOrId(accountingArticle.getArticleFamily());
            if (articleFamily == null)
                throw new BadRequestException("No articleFamily found");
            accountingArticle.setArticleFamily(articleFamily);
        }
        
        if (accountingArticle.getInvoiceType() != null) {
            InvoiceType invoiceType = invoiceTypeService.tryToFindByCodeOrId(accountingArticle.getInvoiceType());
            if (invoiceType == null)
                throw new BadRequestException("No invoiceType found");
            accountingArticle.setInvoiceType(invoiceType);
        }

        accountingArticleService.create(accountingArticle);
        return accountingArticle;
    }

    @Override
    public List<AccountingArticle> list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), filter, null, fetchFields, null, null);
        return accountingArticleService.list(paginationConfiguration);
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, fetchFields, null, null);
        return accountingArticleService.count(paginationConfiguration);
    }

    @Override
    public Long getCount(Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, filter, null, fetchFields, null, null);
        return accountingArticleService.count(paginationConfiguration);
    }

    @Override
    public Optional<AccountingArticle> findById(Long id) {
        return ofNullable(accountingArticleService.findById(id, fetchFields));
    }

    @Override
    public Optional<AccountingArticle> update(Long id, AccountingArticle baseEntity) {
        Optional<AccountingArticle> accountingArticleOptional = findById(id);
        if (accountingArticleOptional.isEmpty()) {
            return Optional.empty();
        }
        AccountingArticle accountingArticle = accountingArticleOptional.get();

        if (baseEntity.getTaxClass() != null) {
            accountingArticle.setTaxClass(taxClassService.tryToFindByCodeOrId(baseEntity.getTaxClass()));
        }

        if (baseEntity.getInvoiceSubCategory() != null) {
            accountingArticle.setInvoiceSubCategory(invoiceSubCategoryService
                    .tryToFindByCodeOrId(baseEntity.getInvoiceSubCategory()));
        }

        if (baseEntity.getAccountingCode() != null) {
            AccountingCode accountingCode = accountingArticleService.tryToFindByCodeOrId(baseEntity.getAccountingCode());
            if (accountingCode == null)
                throw new BadRequestException("No accountingCode found");
            accountingArticle.setAccountingCode(accountingCode);
        }

        if (baseEntity.getArticleFamily() != null) {
            ArticleFamily articleFamily = accountingArticleService.tryToFindByCodeOrId(baseEntity.getArticleFamily());
            if (articleFamily == null)
                throw new BadRequestException("No articleFamily found");
            accountingArticle.setArticleFamily(articleFamily);
        }

        if (!StringUtils.isBlank(baseEntity.getDescription())) {
            accountingArticle.setDescription(baseEntity.getDescription());
        }

        if (baseEntity.getDescriptionI18n() != null && !baseEntity.getDescriptionI18n().isEmpty()) {
            accountingArticle.getDescriptionI18n().clear();
            accountingArticle.getDescriptionI18n().putAll((baseEntity.getDescriptionI18n()));
        }

        if (!StringUtils.isBlank(baseEntity.getAnalyticCode1())) {
            accountingArticle.setAnalyticCode1(baseEntity.getAnalyticCode1());
        }

        if (!StringUtils.isBlank(baseEntity.getAnalyticCode2())) {
            accountingArticle.setAnalyticCode2(baseEntity.getAnalyticCode2());
        }

        if (!StringUtils.isBlank(baseEntity.getAnalyticCode3())) {
            accountingArticle.setAnalyticCode3(baseEntity.getAnalyticCode3());
        }
        
        if (baseEntity.getUnitPrice() != null) {
        	accountingArticle.setUnitPrice(baseEntity.getUnitPrice());
        }

        if (baseEntity.getCfValues() != null) {
            accountingArticle.setCfValues(baseEntity.getCfValues());
        }
        
        if (baseEntity.getInvoiceType() != null) {
            InvoiceType invoiceType = invoiceTypeService.tryToFindByCodeOrId(baseEntity.getInvoiceType());
            if (invoiceType == null)
                throw new BadRequestException("No invoiceType found");
            accountingArticle.setInvoiceType(invoiceType);
        }
        
        if (baseEntity.getInvoiceTypeEl() != null) {
            accountingArticle.setInvoiceTypeEl(baseEntity.getInvoiceTypeEl());
        }

        if(baseEntity.getAccountingCodeEl() != null) {
            accountingArticle.setAccountingCodeEl(baseEntity.getAccountingCodeEl());
        }

        if(baseEntity.getColumnCriteriaEL() != null) {
            accountingArticle.setColumnCriteriaEL(baseEntity.getColumnCriteriaEL());
        }
        
        if(baseEntity.getAllowanceCode() != null) {
            accountingArticle.setAllowanceCode(baseEntity.getAllowanceCode());
        }
        
        accountingArticle.setIgnoreAggregation(baseEntity.isIgnoreAggregation());

        accountingArticleService.update(accountingArticle);

        return ofNullable(accountingArticle);
    }

    @Override
    public Optional<AccountingArticle> findByCode(String code) {
        AccountingArticle accountingArticle = accountingArticleService.findByCode(code, fetchFields);
        if (accountingArticle == null)
            throw new BadRequestException("No Account Article class found with code: " + code);
        return ofNullable(accountingArticle);
    }

    public List<AccountingArticle> findByAccountingCode(String code) {
        List<AccountingArticle> accountingArticles = accountingArticleService.findByAccountingCode(code);
        if (accountingArticles.isEmpty())
            throw new BadRequestException("No Account Article class found with code: " + code);
        return accountingArticles;
    }

    public List<AccountingArticle> deleteByAccountingCode(String accountingCode) {
        List<AccountingArticle> accountingArticles = accountingArticleService.findByAccountingCode(accountingCode);
        if (accountingArticles.isEmpty())
            throw new BadRequestException("No accounting articles existe with code: " + accountingCode);
        accountingArticles.stream()
                .forEach(a -> accountingArticleService.remove(a));
        return accountingArticles;
    }

    @Override
    public Optional<AccountingArticle> delete(Long id) {
        Optional<AccountingArticle> accountingArticle = findById(id);
        if (accountingArticle.isPresent()) {
            try {
                accountingArticleService.remove(accountingArticle.get());
            } catch (Exception e) {
            	if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
            		throw new DeleteReferencedEntityException(AccountingArticle.class, id);
    			}
                throw new BadRequestException(e);
            }
        }
        return accountingArticle;
    }

    @Override
    public Optional<AccountingArticle> delete(String code) {
        Optional<AccountingArticle> accountingArticle = findByCode(code);
        if (accountingArticle.isPresent()) {
            try {
                accountingArticleService.remove(accountingArticle.get());
                accountingArticleService.commit();
            } catch (Exception e) {
            	if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
            		throw new DeleteReferencedEntityException(AccountingArticle.class, code);
    			}
                throw new BadRequestException(e);
            }
        }
        return accountingArticle;
    }

    @Inject
    @MeveoJpa
    private EntityManagerWrapper entityManagerWrapper;

    @Override
    public Optional<AccountingArticle> getAccountingArticles(String productCode, Map<String, Object> attributes) {
        var sqlProduct = "select distinct  p from Product p join fetch p.productCharges pp inner join fetch pp.chargeTemplate where p.code=:code";
		Product product = (Product) entityManagerWrapper.getEntityManager()
									.createQuery(sqlProduct)
									.setParameter("code", productCode)
									.setFlushMode(FlushModeType.COMMIT).getSingleResult();
        if (product == null)
            throw new BadRequestException("No Product found with code: " + productCode);
        return accountingArticleService.getAccountingArticle(product, attributes);
    }

    public List<AccountingCodeMapping> createAccountingCodeMappings(AccountingCodeMappingInput accountingCodeMappingInput) {
        List<AccountingCodeMapping> accountingCodeMappings = new ArrayList<>();
        AccountingArticle accountingArticle = null;
        if(accountingCodeMappingInput.getAccountingArticleCode() != null
                && !accountingCodeMappingInput.getAccountingArticleCode().isBlank()) {
            accountingArticle = accountingArticleService.findByCode(accountingCodeMappingInput.getAccountingArticleCode());
            if(accountingArticle == null) {
                throw new NotFoundException("Accounting article with code "
                        + accountingCodeMappingInput.getAccountingArticleCode() + " does not exits");
            }
        }
        for (org.meveo.apiv2.article.AccountingCodeMapping accountingCodeMapping
                : accountingCodeMappingInput.getAccountingCodeMappings()) {
            accountingCodeMappings.add(createAccountingCodeMapping(accountingCodeMapping, accountingArticle));
        }
        return accountingCodeMappings;
    }

    public AccountingCodeMapping createAccountingCodeMapping(org.meveo.apiv2.article.AccountingCodeMapping resource,
                                                             AccountingArticle accountingArticle) {
        AccountingCodeMapping entity = new AccountingCodeMapping();
        ofNullable(accountingArticle).ifPresent(entity::setAccountingArticle);
        if(resource.getBillingCountryCode() != null
                && !resource.getBillingCountryCode().isBlank()) {
            entity.setBillingCountry(getTradingCountry(resource.getBillingCountryCode(), "Billing"));
        }
        if(resource.getBillingCurrencyCode() != null && !resource.getBillingCurrencyCode().isBlank()) {
            TradingCurrency billingCurrency =
                    ofNullable(tradingCurrencyService.findByTradingCurrencyCode(resource.getBillingCurrencyCode()))
                            .orElseThrow(() -> new NotFoundException("Trading currency with code "
                                    + resource.getBillingCurrencyCode() + " does not exits"));
            entity.setBillingCurrency(billingCurrency);
        }
        if(resource.getSellerCountryCode() != null
                && !resource.getSellerCountryCode().isBlank()) {
            entity.setSellerCountry(getTradingCountry(resource.getSellerCountryCode(), "Seller"));
        }
        if(resource.getSellerCode() != null && !resource.getSellerCode().isBlank()) {
            Seller seller = ofNullable(sellerService.findByCode(resource.getSellerCode()))
                    .orElseThrow(() -> new NotFoundException("Seller with code " + resource.getSellerCode() + " does not exits"));
            entity.setSeller(seller);
        }
        if(resource.getAccountingCode() != null && !resource.getAccountingCode().isBlank()) {
            AccountingCode accountingCode = ofNullable(accountingCodeService.findByCode(resource.getAccountingCode()))
                    .orElseThrow(() -> new NotFoundException("Accounting code " + resource.getSellerCode() + " does not exits"));
            entity.setAccountingCode(accountingCode);
        }
        entity.setCriteriaElValue(resource.getCriteriaElValue());
        try {
            accountingCodeMappingService.create(entity);
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
        return entity;
    }

    private TradingCountry getTradingCountry(String countryCode, String prefix) {
        TradingCountry country = tradingCountryService.findByCode(countryCode);
        if(country == null) {
            throw new NotFoundException(prefix + " country with code " + countryCode + " does not exits");
        } else {
            return country;
        }
    }

    public AccountingArticle updateAccountingCodeMapping(String accountingArticleCode,
                                            AccountingCodeMappingInput accountingCodeMappingInput) {
        AccountingArticle accountingArticle = ofNullable(accountingArticleService.findByCode(accountingArticleCode, fetchFields))
                .orElseThrow(() -> new NotFoundException("Accounting article with code "
                        + accountingCodeMappingInput.getAccountingArticleCode() + " does not exits"));
        if((accountingCodeMappingInput.getAccountingCodeMappings() == null
                || accountingCodeMappingInput.getAccountingCodeMappings().isEmpty()) &&
                (accountingArticle.getAccountingCodeMappings() == null || accountingArticle.getAccountingCodeMappings().isEmpty())) {
            throw new BadRequestException("Accounting article " + accountingArticleCode + " does not have an accounting code mapping");
        }
        accountingArticle.getAccountingCodeMappings().clear();
        if(accountingCodeMappingInput.getAccountingCodeMappings() != null) {
            List<AccountingCodeMapping> accountingCodeMappings = new ArrayList<>();
            for (org.meveo.apiv2.article.AccountingCodeMapping accountingCodeMapping
                    : accountingCodeMappingInput.getAccountingCodeMappings()) {
                accountingCodeMappings.add(createAccountingCodeMapping(accountingCodeMapping, accountingArticle));
            }
            accountingArticle.setAccountingCodeMappings(accountingCodeMappings);
        }
        try {
            accountingArticleService.update(accountingArticle);
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
        return accountingArticle;
    }
}