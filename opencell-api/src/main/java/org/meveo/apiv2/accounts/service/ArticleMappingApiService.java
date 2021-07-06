package org.meveo.apiv2.accounts.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.article.ArticleMappingService;

public class ArticleMappingApiService implements ApiService<ArticleMapping> {

    @Inject
    private ArticleMappingService articleMappingService;

    @Override
    public List<ArticleMapping> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<ArticleMapping> findById(Long id) {
        return Optional.of(articleMappingService.findById(id));
    }

    @Override
    public ArticleMapping create(ArticleMapping articleMapping) {
        if(articleMapping.getMappingScript() != null){
            ScriptInstance scriptInstance = (ScriptInstance)articleMappingService.tryToFindByCodeOrId(articleMapping.getMappingScript());
            articleMapping.setMappingScript(scriptInstance);
        }

        articleMappingService.create(articleMapping);
        return articleMapping;
    }

    @Override
    public Optional<ArticleMapping> update(Long id, ArticleMapping baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMapping> patch(Long id, ArticleMapping baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMapping> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<ArticleMapping> findByCode(String code) {
        return Optional.ofNullable(articleMappingService.findByCode(code));
    }
}
