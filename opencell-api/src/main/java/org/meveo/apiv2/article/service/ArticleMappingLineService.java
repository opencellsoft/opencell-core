package org.meveo.apiv2.article.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

@Stateless
public class ArticleMappingLineService implements ApiService<ArticleMappingLine> {

    @Inject
    private AccountingArticleService accountingArticleService;
    @Inject
    private ArticleMappingService articleMappingService;
    @Inject
    private org.meveo.service.billing.impl.article.ArticleMappingLineService articleMappingLineService;

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
        Optional<AccountingArticle> accountingArticle = accountingArticleService.findById(articleMappingLine.getAccountingArticle().getId());
        if(!accountingArticle.isPresent())
            throw new BadRequestException("No accounting article found with id: " + articleMappingLine.getAccountingArticle().getId());
        Optional<ArticleMapping> articleMapping = articleMappingService.findById(articleMappingLine.getArticleMapping().getId());
        if(!articleMapping.isPresent())
            throw new BadRequestException("No article mapping found with id: " + articleMappingLine.getArticleMapping().getId());
        articleMappingLine.setAccountingArticle(accountingArticle.get());
        articleMappingLine.setArticleMapping(articleMapping.get());
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
