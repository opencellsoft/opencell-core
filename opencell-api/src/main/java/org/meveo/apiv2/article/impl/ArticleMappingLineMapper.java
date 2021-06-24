package org.meveo.apiv2.article.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.meveo.apiv2.article.ImmutableArticleMappingLine;
import org.meveo.apiv2.article.ImmutableAttributeMapping;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;

public class ArticleMappingLineMapper extends ResourceMapper<org.meveo.apiv2.article.ArticleMappingLine, ArticleMappingLine> {
    @Override
    protected org.meveo.apiv2.article.ArticleMappingLine toResource(ArticleMappingLine entity) {
        return ImmutableArticleMappingLine.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .parameter1(entity.getParameter1())
                .parameter2(entity.getParameter2())
                .parameter3(entity.getParameter3())
                .mappingKeyEL(entity.getMappingKelEL())
                .articleMapping(createResource(entity.getArticleMapping()))
                .accountingArticle(createResource(entity.getAccountingArticle()))
                .attributesMapping(getAttributesMappingResources(entity.getAttributesMapping()))
                .offer(createResource(entity.getOfferTemplate()))
                .product(createResource(entity.getProduct()))
                .charge(createResource(entity.getChargeTemplate()))
                .build();
    }

    @Override
    protected ArticleMappingLine toEntity(org.meveo.apiv2.article.ArticleMappingLine resource) {
        ArticleMappingLine articleMappingLine = new ArticleMappingLine();
        final ArticleMapping articleMapping = new ArticleMapping(resource.getArticleMapping().getId());
        articleMapping.setCode(resource.getArticleMapping().getCode());
		articleMappingLine.setArticleMapping(articleMapping);
        final AccountingArticle accountingArticle = new AccountingArticle(resource.getAccountingArticle().getId());
        accountingArticle.setCode(resource.getAccountingArticle().getCode());
		articleMappingLine.setAccountingArticle(accountingArticle);
        articleMappingLine.setParameter1(resource.getParameter1());
        articleMappingLine.setParameter2(resource.getParameter2());
        articleMappingLine.setParameter3(resource.getParameter3());
        articleMappingLine.setMappingKelEL(resource.getMappingKeyEL());
        if(resource.getAttributesMapping() != null){
            List<AttributeMapping> attributesMapping = resource.getAttributesMapping()
                    .stream()
                    .map(attributeMapping -> {
                        Attribute attribute = new Attribute(attributeMapping.getAttribute().getId());
                        attribute.setCode(attributeMapping.getAttribute().getCode());
						return new AttributeMapping(attribute, attributeMapping.getAttributeValue());
                    })
                    .collect(Collectors.toList());
            articleMappingLine.setAttributesMapping(attributesMapping);
        }
        if(resource.getOffer() != null){
            OfferTemplate offerTemplate = new OfferTemplate();
            offerTemplate.setId(resource.getOffer().getId());
            offerTemplate.setCode(resource.getOffer().getCode());
            articleMappingLine.setOfferTemplate(offerTemplate);
        }
        if(resource.getCharge() != null){
            ChargeTemplate chargeTemplate = new RecurringChargeTemplate();
            chargeTemplate.setId(resource.getCharge().getId());
            chargeTemplate.setCode(resource.getCharge().getCode());
            articleMappingLine.setChargeTemplate(chargeTemplate);
        }
        if(resource.getProduct() != null){
            Product product = new Product();
            product.setId(resource.getProduct().getId());
            product.setCode(resource.getProduct().getCode());
            articleMappingLine.setProduct(product);
        }
        return articleMappingLine;
    }

    private Iterable<? extends org.meveo.apiv2.article.AttributeMapping> getAttributesMappingResources(List<AttributeMapping> attributesMapping) {
        return attributesMapping != null ?
                attributesMapping.stream()
                        .map(am -> ImmutableAttributeMapping.builder().attribute(ImmutableResource.builder().id(am.getAttribute().getId()).code(am.getAttribute().getCode()).build()).attributeValue(am.getAttributeValue()).build())
                        .collect(Collectors.toList())
                : null;
    }

    private ImmutableResource createResource(BusinessEntity baseEntity) {
        return baseEntity != null ? ImmutableResource.builder().id(baseEntity.getId()).code(baseEntity.getCode()).build() : null;
    }
}
