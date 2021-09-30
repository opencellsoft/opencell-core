package org.meveo.apiv2.article.service;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.article.ArticleMapping;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.article.ArticleMappingService;

@Stateless
public class ArticleMappingApiService  {

    @Inject
    private ArticleMappingService articleMappingService;


    public Optional<ArticleMapping> findById(Long id) {
        return Optional.of(articleMappingService.findById(id));
    }

    public ArticleMapping create(ArticleMapping articleMapping) {
        if(articleMapping.getMappingScript() != null){
            ScriptInstance scriptInstance = (ScriptInstance)articleMappingService.tryToFindByCodeOrId(articleMapping.getMappingScript());
            articleMapping.setMappingScript(scriptInstance);
        }

        articleMappingService.create(articleMapping);
        return articleMapping;
    }


    public Optional<ArticleMapping> findByCode(String code) {
        return Optional.ofNullable(articleMappingService.findByCode(code));
    }
}
