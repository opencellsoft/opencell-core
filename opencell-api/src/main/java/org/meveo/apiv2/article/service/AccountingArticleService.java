package org.meveo.apiv2.article.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.article.ArticleFamilyService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.tax.TaxClassService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

@Stateless
public class AccountingArticleService implements ApiService<AccountingArticle> {

    @Inject
    private org.meveo.service.billing.impl.article.AccountingArticleService accountingArticleService;
    @Inject
    private AccountingCodeService accountingCodeService;
    @Inject
    private TaxClassService taxClassService;
    @Inject
    private ArticleFamilyService articleFamilyService;
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Override
    public AccountingArticle create(AccountingArticle accountingArticle) {
        TaxClass taxClass = taxClassService.findById(accountingArticle.getTaxClass().getId());
        if(taxClass == null)
            throw new BadRequestException("No tax class found with id: " + accountingArticle.getTaxClass().getId());
        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById(accountingArticle.getInvoiceSubCategory().getId());
        if(invoiceSubCategory == null)
            throw new BadRequestException("No invoice sub category found with id: " + accountingArticle.getInvoiceSubCategory().getId());
        accountingArticle.setTaxClass(taxClass);
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        if(accountingArticle.getAccountingCode() != null){
            AccountingCode accountingCode = accountingCodeService.findById(accountingArticle.getAccountingCode().getId());
            if(accountingCode == null)
                throw new BadRequestException("No accounting code found with id: " + accountingArticle.getAccountingCode().getId());
            accountingArticle.setAccountingCode(accountingCode);
        }
        if(accountingArticle.getArticleFamily() != null){
            ArticleFamily articleFamily = articleFamilyService.findById(accountingArticle.getArticleFamily().getId());
            if(articleFamily == null)
                throw new BadRequestException("No article family found with id: " + accountingArticle.getArticleFamily().getId());
        }

        accountingArticleService.create(accountingArticle);
        return accountingArticle;
    }

    @Override
    public List<AccountingArticle> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<AccountingArticle> findById(Long id) {
        return Optional.of(accountingArticleService.findById(id));
    }

    @Override
    public Optional<AccountingArticle> update(Long id, AccountingArticle baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<AccountingArticle> patch(Long id, AccountingArticle baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<AccountingArticle> delete(Long id) {
        return Optional.empty();
    }
}
