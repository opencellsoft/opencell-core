package org.meveo.apiv2.article.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

@Stateless
public class ArticleMappingService implements ApiService<ArticleMapping> {

    @Inject
    private org.meveo.service.billing.impl.article.ArticleMappingService articleMappingService;
    @Inject
    private ScriptInstanceService scriptInstanceService;

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
            ScriptInstance scriptInstance = scriptInstanceService.findById(articleMapping.getMappingScript().getId());
            if(scriptInstance == null)
                throw new BadRequestException("No script instance found with id: " + articleMapping.getMappingScript().getId());
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
}
