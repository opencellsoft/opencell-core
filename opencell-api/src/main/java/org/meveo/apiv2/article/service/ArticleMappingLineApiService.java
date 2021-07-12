package org.meveo.apiv2.article.service;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.article.AttributeMapping;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;

public class ArticleMappingLineApiService implements ApiService<ArticleMappingLine> {

    @Inject
    private ArticleMappingLineService articleMappingLineService;

    private List<String> fields =
            asList("accountingArticle", "articleMapping", "offerTemplate", "product", "chargeTemplate");

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
        return ofNullable(articleMappingLineService.findById(id, fields, true));
    }

    @Override
    public ArticleMappingLine create(ArticleMappingLine articleMappingLine) {
        AccountingArticle accountingArticle = (AccountingArticle) articleMappingLineService.tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
        if(articleMappingLine.getArticleMapping()!=null) {
        	ArticleMapping articleMapping = (ArticleMapping) articleMappingLineService.tryToFindByCodeOrId(articleMappingLine.getArticleMapping());
        	articleMappingLine.setArticleMapping(articleMapping);
        }
        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
                    .stream()
                    .map(am -> {
                        Attribute attribute = (Attribute) articleMappingLineService.tryToFindByCodeOrId(am.getAttribute());
                        AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue());
                        attributeMapping.setArticleMappingLine(articleMappingLine);
                        return attributeMapping;
                    })
                    .collect(Collectors.toList());
            articleMappingLine.setAttributesMapping(attributesMapping);
        }
        populateArticleMappingLine(articleMappingLine);
        articleMappingLine.setAccountingArticle(accountingArticle);
        articleMappingLineService.create(articleMappingLine);
        return articleMappingLine;
    }

    @Override
    public Optional<ArticleMappingLine> update(Long id, ArticleMappingLine articleMappingLine) {
        ArticleMappingLine articleMappingLineUpdated = articleMappingLineService.findById(id, true);
        if(articleMappingLineUpdated == null) return Optional.empty();
        AccountingArticle accountingArticle = (AccountingArticle) articleMappingLineService.tryToFindByCodeOrId(articleMappingLine.getAccountingArticle());
		articleMappingLine.setAccountingArticle(accountingArticle);
		if(articleMappingLine.getArticleMapping() != null) {
			ArticleMapping articleMapping = (ArticleMapping) articleMappingLineService.tryToFindByCodeOrId(articleMappingLine.getArticleMapping());
			articleMappingLineUpdated.setArticleMapping(articleMapping);
		} else {
        	articleMappingLineUpdated.setArticleMapping(null);
        }
        populateArticleMappingLine(articleMappingLine);
        
        articleMappingLineUpdated.setParameter1(articleMappingLine.getParameter1());
        articleMappingLineUpdated.setParameter2(articleMappingLine.getParameter2());
        articleMappingLineUpdated.setParameter3(articleMappingLine.getParameter3());
        
//        articleMappingLineUpdated.getAttributesMapping().forEach(am -> AttributeMappingService.remove(am));
        articleMappingLineUpdated.getAttributesMapping().clear();
        if(articleMappingLine.getAttributesMapping() != null && !articleMappingLine.getAttributesMapping().isEmpty()){
            List<AttributeMapping> attributesMapping = articleMappingLine.getAttributesMapping()
                    .stream()
                    .map(am -> {
                        Attribute attribute = (Attribute) articleMappingLineService.tryToFindByCodeOrId(am.getAttribute());

                        AttributeMapping attributeMapping = new AttributeMapping(attribute, am.getAttributeValue());
                        attributeMapping.setArticleMappingLine(articleMappingLineUpdated);
                        return attributeMapping;
                    })
                    .collect(Collectors.toList());
            articleMappingLineUpdated.getAttributesMapping().addAll(attributesMapping);
        }
        articleMappingLineService.update(articleMappingLineUpdated);
        return Optional.of(articleMappingLineUpdated);
    }
    
    private void populateArticleMappingLine(ArticleMappingLine articleMappingLine) {
    	if(articleMappingLine.getOfferTemplate() != null){
            OfferTemplate offerTemplate = (OfferTemplate) articleMappingLineService.tryToFindByCodeOrId(articleMappingLine.getOfferTemplate());
            articleMappingLine.setOfferTemplate(offerTemplate);
        }
        if(articleMappingLine.getProduct() != null){
        	Product product = (Product) articleMappingLineService.tryToFindByCodeOrId(articleMappingLine.getProduct());
        	articleMappingLine.setProduct(product);
        }
        if(articleMappingLine.getChargeTemplate() != null){
            ChargeTemplate chargeTemplate = (ChargeTemplate) articleMappingLineService.tryToFindByEntityClassAndCodeOrId(ChargeTemplate.class, articleMappingLine.getChargeTemplate().getCode(), articleMappingLine.getChargeTemplate().getId());
            articleMappingLine.setChargeTemplate(chargeTemplate);
        }
    }
    
   
    @Override
    public Optional<ArticleMappingLine> patch(Long id, ArticleMappingLine baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMappingLine> delete(Long id) {
    	Optional<ArticleMappingLine> articleMappingLine = findById(id);
    	if(articleMappingLine.isPresent()) {
    		ArticleMappingLine current = articleMappingLine.get();
    		articleMappingLineService.remove(current);
    		return Optional.of(current);
    	}
         return Optional.empty();
    }

    @Override
    public Optional<ArticleMappingLine> findByCode(String code) {
        return ofNullable(articleMappingLineService.findByCode(code, fields));
    }
}
