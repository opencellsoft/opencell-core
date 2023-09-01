package org.meveo.apiv2.article.impl;

import static javax.ws.rs.core.Response.Status.NOT_IMPLEMENTED;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.article.ArticleMapping;
import org.meveo.apiv2.article.resource.ArticleMappingResource;
import org.meveo.apiv2.article.service.ArticleMappingApiService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Interceptors({ WsRestApiInterceptor.class })
public class ArticleMappingResourceImpl implements ArticleMappingResource {

    @Inject
    private ArticleMappingApiService articleMappingApiService;

    @Override
    @Deprecated
    public Response createArticleMappingLine(ArticleMapping articleMapping) {
        return Response
                .status(NOT_IMPLEMENTED)
                .entity("{\"actionStatus\":{\"status\":\"NOT IMPLEMENTED\",\"message\":\"API NOT IMPLEMENTED\"}}")
                .build();
    }

    @Override
    public Response find(String code, Request request) {
        org.meveo.model.article.ArticleMapping articleMapping = articleMappingApiService
                .findByCode(code)
                .orElseThrow(NoSuchFieldError::new);
        return Response.ok().entity(articleMapping).build();
    }
}