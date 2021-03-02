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
import org.meveo.model.cpq.Product;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.meveo.service.billing.impl.article.ArticleMappingService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ProductService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArticleMappingLineApiService implements ApiService<ArticleMappingLine> {

    @Inject
    private AccountingArticleService accountingArticleApiService;
    @Inject
    private ArticleMappingService articleMappingApiService;
    @Inject
    private ArticleMappingLineService articleMappingLineService;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;
    @Inject
    private AttributeService attributeService;
    @Inject
    private ProductService productService;

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
        if(articleMappingLine.getProduct() != null){
            Product product = productService.findById(articleMappingLine.getProduct().getId());
            if(product == null)
                throw new BadRequestException("No product template found with id: " + articleMappingLine.getProduct().getId());
            articleMappingLine.setProduct(product);
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
                        Attribute attribute = attributeService.findById(am.getAttribute().getId());
                        if (attribute == null)
                            throw new BadRequestException("No attribute found with Id: " + attribute.getId());
                        AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue());
                        attributeMapping.setArticleMappingLine(articleMappingLine);
                        return attributeMapping;
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
    public Optional<ArticleMappingLine> update(Long id, ArticleMappingLine articleMappingLine) {
        ArticleMapping articleMapping = Optional.ofNullable(articleMappingApiService.findById(id)).get();
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
