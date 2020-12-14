package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ImmutableArticleMapping;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.scripts.ScriptInstance;

public class ArticleMappingMapper extends ResourceMapper<org.meveo.apiv2.article.ArticleMapping, ArticleMapping> {

    @Override
    protected org.meveo.apiv2.article.ArticleMapping toResource(ArticleMapping entity) {
        return ImmutableArticleMapping.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .mappingScript(ImmutableResource.builder().id(entity.getMappingScript() != null ? entity.getMappingScript().getId() : null).build() )
                .build();
    }

    @Override
    protected ArticleMapping toEntity(org.meveo.apiv2.article.ArticleMapping resource) {
        ArticleMapping articleMapping = new ArticleMapping();
        articleMapping.setId(resource.getId());
        articleMapping.setCode(resource.getCode());
        articleMapping.setDescription(resource.getDescription());
        if(resource.getMappingScript() != null){
            ScriptInstance scriptInstance = new ScriptInstance();
            scriptInstance.setId(resource.getMappingScript().getId());
            articleMapping.setMappingScript(scriptInstance);
        }
        return articleMapping;
    }
}
