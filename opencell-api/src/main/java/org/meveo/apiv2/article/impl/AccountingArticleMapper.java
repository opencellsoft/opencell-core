package org.meveo.apiv2.article.impl;

import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.apiv2.article.ImmutableAccountingArticle;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleFamily;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.api.EntityToDtoConverter;

public class AccountingArticleMapper extends ResourceMapper<org.meveo.apiv2.article.AccountingArticle, AccountingArticle> {

    private EntityToDtoConverter entityToDtoConverter = (EntityToDtoConverter) EjbUtils.getServiceInterface(EntityToDtoConverter.class.getSimpleName());;

    @Override
    protected org.meveo.apiv2.article.AccountingArticle toResource(AccountingArticle entity) {
        return ImmutableAccountingArticle.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .taxClass(ImmutableResource.builder().id(entity.getTaxClass().getId()).code(entity.getTaxClass().getCode()).build())
                .invoiceSubCategory(ImmutableResource.builder().id(entity.getInvoiceSubCategory().getId()).code(entity.getInvoiceSubCategory().getCode()).build())
                .accountingCode(entity.getAccountingCode() != null ? ImmutableResource.builder().id(entity.getAccountingCode().getId()).code(entity.getAccountingCode().getCode()).build() : null)
                .articleFamily(entity.getArticleFamily() != null ? ImmutableResource.builder().id(entity.getArticleFamily().getId()).code(entity.getArticleFamily().getCode()).build() : null)
                .analyticCode1(entity.getAnalyticCode1()).analyticCode2(entity.getAnalyticCode2()).analyticCode3(entity.getAnalyticCode3()).unitPrice(entity.getUnitPrice())
                .languageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(entity.getDescriptionI18n()))
                .customFields(entityToDtoConverter.getCustomFieldsDTO(entity))
                .invoiceType(entity.getInvoiceType() != null ? ImmutableResource.builder().id(entity.getInvoiceType().getId()).code(entity.getInvoiceType().getCode()).build(): null)
                .invoiceTypeEl(entity.getInvoiceTypeEl())
                .accountingCodeEl(entity.getAccountingCodeEl())
                .columCriteriaEL(entity.getColumnCriteriaEL())
                .build();
    }

    @Override
    protected AccountingArticle toEntity(org.meveo.apiv2.article.AccountingArticle resource) {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(resource.getTaxClass().getId());
        taxClass.setCode(resource.getTaxClass().getCode());
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setId(resource.getInvoiceSubCategory().getId());
        invoiceSubCategory.setCode(resource.getInvoiceSubCategory().getCode());
        AccountingArticle accountingArticleEntity = new AccountingArticle(resource.getCode(), resource.getDescription(), taxClass, invoiceSubCategory);
        if (resource.getAccountingCode() != null) {
            AccountingCode accountingCode = new AccountingCode();
            accountingCode.setId(resource.getAccountingCode().getId());
            accountingCode.setCode(resource.getAccountingCode().getCode());
            accountingArticleEntity.setAccountingCode(accountingCode);
        }
        if (resource.getArticleFamily() != null) {
            final ArticleFamily articleFamily = new ArticleFamily(resource.getArticleFamily().getId());
            articleFamily.setCode(resource.getArticleFamily().getCode());
            accountingArticleEntity.setArticleFamily(articleFamily);
        }
        accountingArticleEntity.setAnalyticCode1(resource.getAnalyticCode1());
        accountingArticleEntity.setAnalyticCode2(resource.getAnalyticCode2());
        accountingArticleEntity.setAnalyticCode3(resource.getAnalyticCode3());
        accountingArticleEntity.setUnitPrice(resource.getUnitPrice());
        if (resource.getLanguageDescriptions() != null && !resource.getLanguageDescriptions().isEmpty()) {
            for (LanguageDescriptionDto languageDescription : resource.getLanguageDescriptions()) {
                accountingArticleEntity.getDescriptionI18n().put(languageDescription.getLanguageCode(), languageDescription.getDescription());
            }
        }

        if (resource.getInvoiceType() != null) {
            final InvoiceType invoiceType = new InvoiceType();
            invoiceType.setId(resource.getInvoiceType().getId());
            invoiceType.setCode(resource.getInvoiceType().getCode());
            accountingArticleEntity.setInvoiceType(invoiceType);
        }
        accountingArticleEntity.setColumnCriteriaEL(resource.getColumCriteriaEL());
        accountingArticleEntity.setAccountingCodeEl(resource.getAccountingCodeEl());
        accountingArticleEntity.setInvoiceTypeEl(resource.getInvoiceTypeEl());
        return accountingArticleEntity;
    }
}
