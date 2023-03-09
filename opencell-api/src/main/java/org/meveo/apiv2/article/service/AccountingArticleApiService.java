package org.meveo.apiv2.article.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.cpq.Product;
import org.meveo.model.tax.TaxClass;
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

    Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void initService() {
        fetchFields = Arrays.asList("taxClass", "invoiceSubCategory", "articleFamily", "accountingCode");
    }

    @Override
    public AccountingArticle create(AccountingArticle accountingArticle) {
        AccountingArticle accou = accountingArticleService.findByCode(accountingArticle.getCode());
        if (accou != null) {
            throw new EntityAlreadyExistsException(AccountingArticle.class, accountingArticle.getCode());
        }
        InvoiceSubCategory invoiceSubCategory = (InvoiceSubCategory) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getInvoiceSubCategory());
        if (invoiceSubCategory == null)
            throw new BadRequestException("No invoice sub category found with id: " + accountingArticle.getInvoiceSubCategory().getId());
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);

        TaxClass taxClass = (TaxClass) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getTaxClass());
        if (taxClass == null)
            throw new BadRequestException("No taxClass found for id : " + accountingArticle.getTaxClass().getId());
        accountingArticle.setTaxClass(taxClass);

        if (accountingArticle.getAccountingCode() != null) {
            AccountingCode accountingCode = (AccountingCode) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getAccountingCode());
            if (accountingCode == null)
                throw new BadRequestException("No accountingCode found");
            accountingArticle.setAccountingCode(accountingCode);
        }

        if (accountingArticle.getArticleFamily() != null) {
            ArticleFamily articleFamily = (ArticleFamily) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getArticleFamily());
            if (articleFamily == null)
                throw new BadRequestException("No articleFamily found");
            accountingArticle.setArticleFamily(articleFamily);
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
        return Optional.ofNullable(accountingArticleService.findById(id, fetchFields));
    }

    @Override
    public Optional<AccountingArticle> update(Long id, AccountingArticle baseEntity) {
        Optional<AccountingArticle> accountingArticleOptional = findById(id);
        if (accountingArticleOptional.isEmpty()) {
            return Optional.empty();
        }
        AccountingArticle accountingArticle = accountingArticleOptional.get();

        if (baseEntity.getTaxClass() != null && baseEntity.getTaxClass().getId() != null) {
            TaxClass taxClass = taxClassService.findById(baseEntity.getTaxClass().getId());
            if (taxClass == null)
                throw new BadRequestException("No taxClass found for id : " + baseEntity.getTaxClass().getId());
            accountingArticle.setTaxClass(taxClass);
        }
        
        if (baseEntity.getTaxClass() != null && baseEntity.getTaxClass().getCode() != null) {
            TaxClass taxClass = taxClassService.findByCode(baseEntity.getTaxClass().getCode());
            if (taxClass == null)
                throw new BadRequestException("No taxClass found for code : " + baseEntity.getTaxClass().getCode());
            accountingArticle.setTaxClass(taxClass);
        }

        if (baseEntity.getInvoiceSubCategory() != null && baseEntity.getInvoiceSubCategory().getId() != null) {
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(baseEntity.getInvoiceSubCategory().getId());
            if (invoiceSubCategory == null)
                throw new BadRequestException("No invoiceSubCategory found for id : " + baseEntity.getInvoiceSubCategory().getId());
            accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        }

        if (baseEntity.getAccountingCode() != null) {
            AccountingCode accountingCode = (AccountingCode) accountingArticleService.tryToFindByCodeOrId(baseEntity.getAccountingCode());
            if (accountingCode == null)
                throw new BadRequestException("No accountingCode found");
            accountingArticle.setAccountingCode(accountingCode);
        }

        if (baseEntity.getArticleFamily() != null) {
            ArticleFamily articleFamily = (ArticleFamily) accountingArticleService.tryToFindByCodeOrId(baseEntity.getArticleFamily());
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

        if (baseEntity.getCfValues() != null) {
            accountingArticle.setCfValues(baseEntity.getCfValues());
        }

        accountingArticleService.update(accountingArticle);

        return Optional.ofNullable(accountingArticle);
    }

    @Override
    public Optional<AccountingArticle> findByCode(String code) {
        AccountingArticle accountingArticle = accountingArticleService.findByCode(code);
        if (accountingArticle == null)
            throw new BadRequestException("No Account Article class found with code: " + code);
        return Optional.ofNullable(accountingArticle);
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
        Optional<AccountingArticle> article = accountingArticleService.getAccountingArticle(product, attributes);
        return article;
    }
}
