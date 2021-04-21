package org.meveo.apiv2.article.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.article.ImmutableAccountingArticle;
import org.meveo.apiv2.article.ImmutableArticleMappingLine;
import org.meveo.apiv2.billing.ImmutableTax;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;

public class ArticleMappingLineMapperTest {

    private ArticleMappingLineMapper articleMappingLineMapper;

    @Before
    public void setUp() {
        articleMappingLineMapper = new ArticleMappingLineMapper();
    }

    @Test
    public void test_map_entity_to_resource() {
        ArticleMappingLine entity = buildArticleMappingLineEntity();

        org.meveo.apiv2.article.ArticleMappingLine resource = articleMappingLineMapper.toResource(entity);

        assertNotNull(resource);
        assertTrue(resource instanceof org.meveo.apiv2.article.ArticleMappingLine);
        assertNotNull(resource.getAccountingArticle());
        assertEquals(resource.getParameter1(), "param01");
        assertEquals(resource.getMappingKeyEL(), "Mapping EL");
        assertEquals(resource.getAccountingArticle().getId(), (Long) 1L);
    }

    private ArticleMappingLine buildArticleMappingLineEntity() {
        ArticleMappingLine articleMappingLine = new ArticleMappingLine();
        articleMappingLine.setId(1L);
        articleMappingLine.setParameter1("param01");
        articleMappingLine.setParameter2("param02");
        articleMappingLine.setParameter3("param03");

        ArticleMapping articleMapping = new ArticleMapping();
        articleMapping.setCode("article_Mapping");
        articleMapping.setId(1L);

        AccountingArticle accountingArticle = new AccountingArticle();
        accountingArticle.setId(1L);
        accountingArticle.setCode("accounting_article");
        articleMappingLine.setAccountingArticle(accountingArticle);

        articleMappingLine.setArticleMapping(articleMapping);
        articleMappingLine.setMappingKelEL("Mapping EL");

        OfferTemplate offerTemplate = new OfferTemplate();
        offerTemplate.setId(1L);
        articleMappingLine.setOfferTemplate(offerTemplate);

        Product product = new Product();
        product.setId(1L);
        articleMappingLine.setProduct(product);

        return articleMappingLine;
    }

    @Test
    public void test_map_resource_to_entity() {
        org.meveo.apiv2.article.ArticleMappingLine articleMappingLine = buildArticleMappingLineResource();

        ArticleMappingLine articleMappingLineEntity = articleMappingLineMapper.toEntity(articleMappingLine);

        assertNotNull(articleMappingLineEntity);
        assertTrue(articleMappingLineEntity instanceof ArticleMappingLine);
        assertEquals(articleMappingLineEntity.getParameter2(), "param02");
        assertEquals(articleMappingLineEntity.getParameter3(), "param03");
        assertEquals(articleMappingLineEntity.getMappingKelEL(), "Mapping EL");
    }

    private org.meveo.apiv2.article.ArticleMappingLine buildArticleMappingLineResource() {
        ImmutableTax tax = ImmutableTax.builder().id(1L).build();
        ImmutableAccountingArticle accountingArticle = ImmutableAccountingArticle.builder()
                .id(1L)
                .code("accounting_article").description("description")
                .taxClass(tax)
                .invoiceSubCategory(ImmutableResource.builder().id(1L).build())
                .build();
        ImmutableArticleMappingLine resource = ImmutableArticleMappingLine.builder()
                .id(1L)
                .mappingKeyEL("Mapping EL")
                .parameter1("param01")
                .parameter2("param02")
                .parameter3("param03")
                .articleMapping(ImmutableResource.builder().id(1L).build())
                .accountingArticle(accountingArticle)
                .build();

        return resource;
    }
}