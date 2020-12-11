package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ImmutableAccountingArticle;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.tax.TaxClass;

public class AccountingArticleMapper extends ResourceMapper<org.meveo.apiv2.article.AccountingArticle, AccountingArticle> {


    @Override
    protected org.meveo.apiv2.article.AccountingArticle toResource(AccountingArticle entity) {
        return ImmutableAccountingArticle.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .taxClass(ImmutableResource.builder().id(entity.getTaxClass().getId()).build())
                .invoiceSubCategory(ImmutableResource.builder().id(entity.getInvoiceSubCategory().getId()).build())
                .accountingCode(entity.getAccountingCode() != null ? ImmutableResource.builder().id(entity.getAccountingCode().getId()).build() : null)
                .articleFamily(entity.getArticleFamily() != null ? ImmutableResource.builder().id(entity.getArticleFamily().getId()).build() : null)
                .analyticCode1(entity.getAnalyticCode1())
                .analyticCode2(entity.getAnalyticCode2())
                .analyticCode3(entity.getAnalyticCode3())
                .build();
    }

    @Override
    protected AccountingArticle toEntity(org.meveo.apiv2.article.AccountingArticle resource) {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(resource.getTaxClass().getId());
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setId(resource.getInvoiceSubCategory().getId());
        AccountingArticle accountingArticleEntity = new AccountingArticle(resource.getCode(), resource.getDescription(), taxClass, invoiceSubCategory);
        if(resource.getAccountingCode() != null) {
            AccountingCode accountingCode = new AccountingCode();
            accountingCode.setId(resource.getId());
            accountingArticleEntity.setAccountingCode(accountingCode);
        }
        if(resource.getArticleFamily() != null){
            accountingArticleEntity.setArticleFamily(new ArticleFamily(resource.getArticleFamily().getId()));
        }
        accountingArticleEntity.setAnalyticCode1(resource.getAnalyticCode1());
        accountingArticleEntity.setAnalyticCode2(resource.getAnalyticCode2());
        accountingArticleEntity.setAnalyticCode3(resource.getAnalyticCode3());
        return accountingArticleEntity;
    }
}
