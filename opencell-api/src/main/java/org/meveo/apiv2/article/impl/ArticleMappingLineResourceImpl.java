package org.meveo.apiv2.article.impl;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.article.ArticleMappingLine;
import org.meveo.apiv2.article.ImmutableArticleMappingLine;
import org.meveo.apiv2.article.resource.ArticleMappingLineResource;
import org.meveo.apiv2.article.service.ArticleMappingLineApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;

public class ArticleMappingLineResourceImpl implements ArticleMappingLineResource {

    @Inject
    private ArticleMappingLineApiService articleMappingLineApiService;

    private ArticleMappingLineMapper mapper = new ArticleMappingLineMapper();

    @Override
    public Response createArticleMappingLine(ArticleMappingLine articleMappingLine) {
        org.meveo.model.article.ArticleMappingLine articleMappingLineEntity = articleMappingLineApiService.create(mapper.toEntity(articleMappingLine));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ArticleMappingLineResource.class, articleMappingLineEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(articleMappingLineEntity)))
                .build();
    }

	@Override
	public Response updateArticleMappingLine(Long id, ArticleMappingLine articleMappingLine) {
        org.meveo.model.article.ArticleMappingLine articleMappingLineEntity = 
        					articleMappingLineApiService.update(id, mapper.toEntity(articleMappingLine)).orElseThrow(NotFoundException::new);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ArticleMappingLineResource.class, articleMappingLineEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(articleMappingLineEntity)))
                .build();
	}

	@Override
	public Response findById(Long id, Request request) {
		return articleMappingLineApiService.findById(id)
				.map(aml -> {
					 EntityTag etag = new EntityTag(Integer.toString(aml.hashCode()));
	                    CacheControl cc = new CacheControl();
	                    cc.setMaxAge(1000);
	                    Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
	                    if (builder != null) {
	                        builder.cacheControl(cc);
	                        return builder.build();
	                    }
	                    return Response.ok().cacheControl(cc).tag(etag)
	                            .entity(toResourceOrderWithLink(mapper.toResource(aml))).build();
				})
				 .orElseThrow(NotFoundException::new);
	}

	@Override
	public Response deleteById(Long id) {
		return articleMappingLineApiService.delete(id)
				.map(aml -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(aml))).build())
                .orElseThrow(NotFoundException::new);
	}
	
    private org.meveo.apiv2.article.ArticleMappingLine toResourceOrderWithLink(org.meveo.apiv2.article.ArticleMappingLine articleMappingLineResource) {
        return ImmutableArticleMappingLine.copyOf(articleMappingLineResource)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(ArticleMappingLineResource.class)
                                .withId(articleMappingLineResource.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }


}
