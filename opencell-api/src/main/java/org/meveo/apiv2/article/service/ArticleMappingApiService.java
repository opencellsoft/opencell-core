package org.meveo.apiv2.article.service;

import java.util.Optional;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.api.exception.EntityAlreadyExistsException;
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
    	var exitedArticleMapping = articleMappingService.findByCode(articleMapping.getCode());
    	if(exitedArticleMapping != null)
    		throw new EntityAlreadyExistsException(ArticleMapping.class, articleMapping.getCode());
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
