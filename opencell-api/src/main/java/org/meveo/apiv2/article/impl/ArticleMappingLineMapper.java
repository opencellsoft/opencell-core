package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ImmutableArticleMappingLine;
import org.meveo.apiv2.article.ImmutableAttributeMapping;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class ArticleMappingLineMapper extends ResourceMapper<org.meveo.apiv2.article.ArticleMappingLine, ArticleMappingLine> {
    @Override
    protected org.meveo.apiv2.article.ArticleMappingLine toResource(ArticleMappingLine entity) {
        return ImmutableArticleMappingLine.builder()
                .id(entity.getId())
                .parameter1(entity.getParameter1())
                .parameter2(entity.getParameter2())
                .parameter3(entity.getParameter3())
                .mappingKeyEL(entity.getMappingKelEL())
                .articleMapping(createResource(entity.getArticleMapping().getId()))
                .accountingArticle(createResource(entity.getAccountingArticle().getId()))
                .attributesMapping(getAttributesMappingResources(entity.getAttributesMapping())
                        )
                .build();
    }

    @Override
    protected ArticleMappingLine toEntity(org.meveo.apiv2.article.ArticleMappingLine resource) {
        ArticleMappingLine articleMappingLine = new ArticleMappingLine();
        articleMappingLine.setArticleMapping(new ArticleMapping(resource.getArticleMapping().getId()));
        articleMappingLine.setAccountingArticle(new AccountingArticle(resource.getAccountingArticle().getId()));
        articleMappingLine.setParameter1(resource.getParameter1());
        articleMappingLine.setParameter2(resource.getParameter2());
        articleMappingLine.setParameter3(resource.getParameter3());
        articleMappingLine.setMappingKelEL(resource.getMappingKeyEL());
        if(resource.getAttributesMapping() != null){
            List<AttributeMapping> attributesMapping = resource.getAttributesMapping()
                    .stream()
                    .map(attributeMapping -> new AttributeMapping(attributeMapping.getAttribute(), attributeMapping.getAttributeValue()))
                    .collect(Collectors.toList());
            articleMappingLine.setAttributesMapping(attributesMapping);
        }
        if(resource.getOffer() != null){
            OfferTemplate offerTemplate = new OfferTemplate();
            offerTemplate.setId(resource.getOffer().getId());
            articleMappingLine.setOfferTemplate(offerTemplate);
        }
        if(resource.getCharge() != null){
            ChargeTemplate chargeTemplate = new RecurringChargeTemplate();
            chargeTemplate.setId(resource.getCharge().getId());
            articleMappingLine.setChargeTemplate(chargeTemplate);
        }
        if(resource.getProduct() != null){
            ProductTemplate product = new ProductTemplate();
            product.setId(product.getId());
            articleMappingLine.setProductTemplate(product);
        }
        return articleMappingLine;
    }

    private Iterable<? extends org.meveo.apiv2.article.AttributeMapping> getAttributesMappingResources(List<AttributeMapping> attributesMapping) {
        return attributesMapping != null ?
                attributesMapping.stream()
                        .map(am -> ImmutableAttributeMapping.builder().attribute(am.getAttribute()).attributeValue(am.getAttributeValue()).build())
                        .collect(Collectors.toList())
                : null;
    }

    private ImmutableResource createResource(Long id) {
        return ImmutableResource.builder().id(id).build();
    }
}
