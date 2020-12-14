package org.meveo.apiv2.article.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.service.billing.impl.article.AccountingArticleService1;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.meveo.service.billing.impl.article.ArticleMappingService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

public class ArticleMappingLineApiService implements ApiService<ArticleMappingLine> {

    @Inject
    private AccountingArticleService1 accountingArticleApiService;
    @Inject
    private ArticleMappingService articleMappingApiService;
    @Inject
    private ArticleMappingLineService articleMappingLineService;

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
