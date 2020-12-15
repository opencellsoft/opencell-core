package org.meveo.apiv2.article.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.service.billing.impl.article.AccountingArticleService1;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.meveo.service.billing.impl.article.ArticleMappingService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.cpq.AttributeService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArticleMappingLineApiService implements ApiService<ArticleMappingLine> {

    @Inject
    private AccountingArticleService1 accountingArticleApiService;
    @Inject
    private ArticleMappingService articleMappingApiService;
    @Inject
    private ArticleMappingLineService articleMappingLineService;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private ProductTemplateService productTemplateService;
    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;
    @Inject
    private AttributeService attributeService;

    @Override
    public List<ArticleMappingLine> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<ArticleMappingLine> findById(Long id) {
        return Optional.of(articleMappingLineService.findById(id));
    }

    @Override
    public ArticleMappingLine create(ArticleMappingLine articleMappingLine) {
        AccountingArticle accountingArticle = accountingArticleApiService.findById(articleMappingLine.getAccountingArticle().getId());
        if(accountingArticle == null)
            throw new BadRequestException("No accounting article found with id: " + articleMappingLine.getAccountingArticle().getId());
        ArticleMapping articleMapping = articleMappingApiService.findById(articleMappingLine.getArticleMapping().getId());
        if(articleMapping == null)
            throw new BadRequestException("No article mapping found with id: " + articleMappingLine.getArticleMapping().getId());
        if(articleMappingLine.getOfferTemplate() != null){
            OfferTemplate offerTemplate = offerTemplateService.findById(articleMappingLine.getOfferTemplate().getId());
            if(offerTemplate == null)
                throw new BadRequestException("No offer template found with id: " + articleMappingLine.getOfferTemplate().getId());
            articleMappingLine.setOfferTemplate(offerTemplate);
        }
        if(articleMappingLine.getProductTemplate() != null){
            ProductTemplate productTemplate = productTemplateService.findById(articleMappingLine.getProductTemplate().getId());
            if(productTemplate == null)
                throw new BadRequestException("No product template found with id: " + articleMappingLine.getProductTemplate().getId());
            articleMappingLine.setProductTemplate(productTemplate);
        }
        if(articleMappingLine.getChargeTemplate() != null){
            ChargeTemplate chargeTemplate = chargeTemplateService.findById(articleMappingLine.getChargeTemplate().getId());
            if(chargeTemplate == null)
                throw new BadRequestException("No charge template found with id: " + articleMappingLine.getChargeTemplate().getId());
            articleMappingLine.setChargeTemplate(chargeTemplate);
        }
        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
                    .stream()
                    .map(am -> {
                        Attribute attribute = attributeService.findById(am.getId());
                        if (attribute == null)
                            throw new BadRequestException("No attribute found with Id: " + attribute.getId());
                        return new AttributeMapping(attribute, am.getAttributeValue());
                    })
                    .collect(Collectors.toList());
            articleMappingLine.setAttributesMapping(attributesMapping);
        }
        articleMappingLine.setAccountingArticle(accountingArticle);
        articleMappingLine.setArticleMapping(articleMapping);
        articleMappingLineService.create(articleMappingLine);
        return articleMappingLine;
    }

    @Override
    public Optional<ArticleMappingLine> update(Long id, ArticleMappingLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMappingLine> patch(Long id, ArticleMappingLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMappingLine> delete(Long id) {
        return Optional.empty();
    }
}
