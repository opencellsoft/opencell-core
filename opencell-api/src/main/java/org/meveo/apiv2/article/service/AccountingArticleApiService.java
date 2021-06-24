package org.meveo.apiv2.article.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.cpq.Product;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingArticleApiService implements AccountingArticleServiceBase{

    private List<String> fetchFields;
    @Inject
    private AccountingArticleService accountingArticleService;
    
    Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void initService(){
        fetchFields = Arrays.asList("taxClass", "invoiceSubCategory", "articleFamily", "accountingCode");
    }
    
    @Override
    public AccountingArticle create(AccountingArticle accountingArticle) {
        TaxClass taxClass = (TaxClass) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getTaxClass());
        InvoiceSubCategory invoiceSubCategory = (InvoiceSubCategory) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getInvoiceSubCategory());
        AccountingArticle accou = accountingArticleService.findByCode(accountingArticle.getCode());
        if(accou != null){
            throw new EntityAlreadyExistsException(AccountingArticle.class, accountingArticle.getCode());
        }
        if(invoiceSubCategory == null)
            throw new BadRequestException("No invoice sub category found with id: " + accountingArticle.getInvoiceSubCategory().getId());
        accountingArticle.setTaxClass(taxClass);
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        if(accountingArticle.getAccountingCode() != null){
            AccountingCode accountingCode =  (AccountingCode) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getAccountingCode());
            accountingArticle.setAccountingCode(accountingCode);
        }
        if(accountingArticle.getArticleFamily() != null){
            ArticleFamily articleFamily = (ArticleFamily) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getArticleFamily());
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
        return Optional.ofNullable(accountingArticleService.findById(id));
    }

    @Override
    public Optional<AccountingArticle> update(Long id, AccountingArticle baseEntity) {
        Optional<AccountingArticle> accountingArticleOtional = findById(id);
        if(!accountingArticleOtional.isPresent()) {
            return Optional.empty();
        }
        AccountingArticle accountingArticle = accountingArticleOtional.get();
        if(baseEntity.getTaxClass() != null && baseEntity.getTaxClass().getId() != null) {
	        TaxClass taxClass = (TaxClass) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getTaxClass());
	        accountingArticle.setTaxClass(taxClass);
        }
        if(baseEntity.getInvoiceSubCategory() != null && baseEntity.getInvoiceSubCategory().getId() != null) {
	        InvoiceSubCategory invoiceSubCategory = (InvoiceSubCategory) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getInvoiceSubCategory());
	        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        }
        if(baseEntity.getAccountingCode() != null){
            AccountingCode accountingCode = (AccountingCode) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getAccountingCode());

            accountingArticle.setAccountingCode(accountingCode);
        }
        if(baseEntity.getArticleFamily() != null){
            ArticleFamily articleFamily = (ArticleFamily) accountingArticleService.tryToFindByCodeOrId(accountingArticle.getArticleFamily());
        }
        if(!Strings.isEmpty(baseEntity.getDescription())) {
        	accountingArticle.setDescription(baseEntity.getDescription());
        }
        if(baseEntity.getDescriptionI18n()!=null && !baseEntity.getDescriptionI18n().isEmpty() ) {
        	accountingArticle.getDescriptionI18n().clear();
        	accountingArticle.getDescriptionI18n().putAll((baseEntity.getDescriptionI18n()));
        }
        
        if(!Strings.isEmpty(baseEntity.getAnalyticCode1())) {
        	accountingArticle.setAnalyticCode1(baseEntity.getAnalyticCode1());
        }
        
        if(!Strings.isEmpty(baseEntity.getAnalyticCode2())) {
        	accountingArticle.setAnalyticCode2(baseEntity.getAnalyticCode2());
        }
        
        if(!Strings.isEmpty(baseEntity.getAnalyticCode3())) {
        	accountingArticle.setAnalyticCode3(baseEntity.getAnalyticCode3());
        }
        
        if(baseEntity.getCfValues() != null) {
        	accountingArticle.setCfValues(baseEntity.getCfValues());
        }
        accountingArticleService.update(accountingArticle);
        
        return accountingArticleOtional;
    }

    @Override
    public Optional<AccountingArticle> findByCode(String code) {
        AccountingArticle accountingArticle = accountingArticleService.findByCode(code);
        if(accountingArticle == null)
            throw new BadRequestException("No Account Article class found with code: " + code);
        return Optional.ofNullable(accountingArticle);
    }

    public List<AccountingArticle> findByAccountingCode(String code) {
        List<AccountingArticle> accountingArticles = accountingArticleService.findByAccountingCode(code);
        if(accountingArticles.isEmpty())
            throw new BadRequestException("No Account Article class found with code: " + code);
        return accountingArticles;
    }

    public List<AccountingArticle> deleteByAccountingCode(String accountingCode) {
        List<AccountingArticle> accountingArticles = accountingArticleService.findByAccountingCode(accountingCode);
        if(accountingArticles.isEmpty())
            throw new BadRequestException("No accounting articles existe with code: " + accountingCode);
        accountingArticles.stream()
                .forEach(a -> accountingArticleService.remove(a));
        return accountingArticles;
    }

    @Override
    public Optional<AccountingArticle> delete(Long id) {
        Optional<AccountingArticle> accountingArticle = findById(id);
        if(accountingArticle.isPresent()) {
            try {
                accountingArticleService.remove(accountingArticle.get());
            } catch (Exception e) {
                throw new BadRequestException(e);
            }
        }
        return accountingArticle;
    }

    @Override
    public Optional<AccountingArticle> delete(String code) {
        Optional<AccountingArticle> accountingArticle = findByCode(code);
        if(accountingArticle.isPresent()) {
            try {
                accountingArticleService.remove(accountingArticle.get());
            } catch (Exception e) {
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
		if(product == null)
            throw new BadRequestException("No Product found with code: " + productCode);
		Optional<AccountingArticle> article = accountingArticleService.getAccountingArticle(product, attributes);
		return article;
	}
}
