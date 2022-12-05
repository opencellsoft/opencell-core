package org.meveo.apiv2.article.impl;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

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
    	try {
    		org.meveo.model.article.ArticleMappingLine articleMappingLineEntity = articleMappingLineApiService.create(mapper.toEntity(articleMappingLine));
            return Response
                    .created(LinkGenerator.getUriBuilderFromResource(ArticleMappingLineResource.class, articleMappingLineEntity.getId()).build())
                    .entity(toResourceOrderWithLink(mapper.toResource(articleMappingLineEntity)))
                    .build();
		} catch (Exception e) {
			return toError(e, Status.BAD_REQUEST);
        }
    }
    
    private Response toError(Exception e, Status s) {
        ResponseBuilder rb = Response.status(s);
        rb.entity(e.getMessage());
        return rb.build();   
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

	@Override
	public Response find(String code, Request request) {
		return articleMappingLineApiService.findByCode(code)
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
