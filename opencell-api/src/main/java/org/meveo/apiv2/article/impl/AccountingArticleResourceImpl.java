package org.meveo.apiv2.article.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.article.AccountingArticles;
import org.meveo.apiv2.article.ImmutableAccountingArticle;
import org.meveo.apiv2.article.ImmutableAccountingArticles;
import org.meveo.apiv2.article.resource.AccountingArticleResource;
import org.meveo.apiv2.article.service.AccountingArticleApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;
//import org.meveo.apiv2.ordering.resource.order.ImmutableOrder;
import org.meveo.model.article.AccountingArticle;

public class AccountingArticleResourceImpl implements AccountingArticleResource {

    @Inject
    private AccountingArticleApiService accountingArticleApiService;
    private AccountingArticleMapper mapper = new AccountingArticleMapper();


    @Override
    public Response createAccountingArticle(org.meveo.apiv2.article.AccountingArticle accountingArticle) {

        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);
        accountingArticleEntity = accountingArticleApiService.create(accountingArticleEntity);

        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accountingArticleEntity)))
                .build();
    }

	@Override
	public Response updateAccountingArticle(Long id, org.meveo.apiv2.article.AccountingArticle accountingArticle) {
        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);
        Optional<AccountingArticle> accoutningUpdated = accountingArticleApiService.update(id, accountingArticleEntity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accoutningUpdated.get())))
                .build();
	}

    private org.meveo.apiv2.article.AccountingArticle toResourceOrderWithLink(org.meveo.apiv2.article.AccountingArticle accountingResource) {
        return ImmutableAccountingArticle.copyOf(accountingResource)
                .withLinks(
                        new LinkGenerator.SelfLinkGenerator(AccountingArticleResource.class)
                                .withId(accountingResource.getId())
                                .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
                                .build()
                );
    }

    @Override
	public Response find(String accountingArticleCode, Request request) {
		return accountingArticleApiService.findByCode(accountingArticleCode)
					.map(accounting -> {
						EntityTag etag = new EntityTag(Integer.toString(accounting.hashCode()));
	                    CacheControl cc = new CacheControl();
	                    cc.setMaxAge(1000);
	                    Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
	                    if (builder != null) {
	                        builder.cacheControl(cc);
	                        return builder.build();
	                    }
	                    return Response.ok().cacheControl(cc).tag(etag)
	                            .entity(toResourceOrderWithLink(mapper.toResource(accounting))).build();
					}).orElseThrow(NotFoundException::new);
	}

    @Override
	public Response delete(String accountingArticleCode, Request request) {
		return accountingArticleApiService.delete(accountingArticleCode)
											.map(accountingArticle -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(accountingArticle))).build())
												.orElseThrow(NotFoundException::new);
	}

	public Response list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter, Request request) {
        List<AccountingArticle> accoutnigArticleEntities = accountingArticleApiService.list(offset, limit, sort, orderBy, filter);
        EntityTag etag = new EntityTag(Integer.toString(accoutnigArticleEntities.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableAccountingArticle[] accountingList = accoutnigArticleEntities
                .stream()
                .map(accoutingArticle -> toResourceOrderWithLink(mapper.toResource(accoutingArticle)))
                .toArray(ImmutableAccountingArticle[]::new);
        Long orderCount = accountingArticleApiService.getCount(filter);
        AccountingArticles articles = ImmutableAccountingArticles.builder().addData(accountingList).offset(offset).limit(limit).total(orderCount)
                .build().withLinks(new LinkGenerator.PaginationLinkGenerator(AccountingArticleResource.class)
                        .offset(offset).limit(limit).total(orderCount).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(articles).build();
	}

	@Override
	public Response getAccountingArticles(String productCode, Map<String, Object> attribues, Request request) {
		return accountingArticleApiService.getAccountingArticles(productCode, attribues)
											.map(accountingArticle -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(accountingArticle))).build())
											.orElseThrow(NotFoundException::new);
	}

}
