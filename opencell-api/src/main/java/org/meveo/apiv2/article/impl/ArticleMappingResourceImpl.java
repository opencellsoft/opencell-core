package org.meveo.apiv2.article.impl;

import org.meveo.apiv2.article.ArticleMapping;
import org.meveo.apiv2.article.ImmutableArticleMapping;
import org.meveo.apiv2.article.resource.ArticleMappingResource;
import org.meveo.apiv2.article.service.ArticleMappingApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;

import javax.inject.Inject;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class ArticleMappingResourceImpl implements ArticleMappingResource {

    @Inject
    private ArticleMappingApiService articleMappingApiService;
    private ArticleMappingMapper mapper = new ArticleMappingMapper();

    @Override
    public Response createArticleMappingLine(ArticleMapping articleMapping) {
        org.meveo.model.article.ArticleMapping articleMappingEntity = articleMappingApiService.create(mapper.toEntity(articleMapping));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ArticleMappingResource.class, articleMappingEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(articleMappingEntity)))
                .build();
    }

    @Override
    public Response find(String code, Request request) {
        org.meveo.model.article.ArticleMapping articleMapping = articleMappingApiService
                .findByCode(code)
                .orElseThrow(NoSuchFieldError::new);
        return Response.ok().entity(articleMapping).build();
    }

    private org.meveo.apiv2.article.ArticleMapping toResourceOrderWithLink(org.meveo.apiv2.article.ArticleMapping articleMappingResource) {
        return ImmutableArticleMapping.copyOf(articleMappingResource)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(ArticleMappingResource.class)
                                .withId(articleMappingResource.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }


}
