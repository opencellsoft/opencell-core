package org.meveo.apiv2.article.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.article.AccountingArticles;
import org.meveo.apiv2.article.ImmutableAccountingArticle;
import org.meveo.apiv2.article.ImmutableAccountingArticles;
import org.meveo.apiv2.article.resource.AccountingArticleResource;
import org.meveo.apiv2.article.service.AccountingArticleApiService;
import org.meveo.apiv2.article.service.AccountingArticleBaseApi;
import org.meveo.apiv2.ordering.common.LinkGenerator;
//import org.meveo.apiv2.ordering.resource.order.ImmutableOrder;
import org.meveo.model.article.AccountingArticle;
import org.meveo.service.api.EntityToDtoConverter;

public class AccountingArticleResourceImpl implements AccountingArticleResource {

    @Inject
    private AccountingArticleApiService accountingArticleApiService;
    @Inject private AccountingArticleBaseApi accountingArticleBaseApi;
    private AccountingArticleMapper mapper = new AccountingArticleMapper();


    @Override
    public Response createAccountingArticle(org.meveo.apiv2.article.AccountingArticle accountingArticle) {

        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);
        accountingArticleBaseApi.populateCustomFieldsForGenericApi(accountingArticle.getCustomFields(), accountingArticleEntity, true);
        accountingArticleEntity = accountingArticleApiService.create(accountingArticleEntity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accountingArticleEntity)))
                .build();
    }

	@Override
	public Response updateAccountingArticle(Long id, org.meveo.apiv2.article.AccountingArticle accountingArticle) {
        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);
        accountingArticleBaseApi.populateCustomFieldsForGenericApi(accountingArticle.getCustomFields(), accountingArticleEntity, false);
        Optional<AccountingArticle> accoutningUpdated = accountingArticleApiService.update(id, accountingArticleEntity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accoutningUpdated.orElseThrow(NotFoundException::new))))
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

	public Response findByAccountingCode(String accountingCode, Request request) {
		List<AccountingArticle> accountingArticles = accountingArticleApiService.findByAccountingCode(accountingCode);
		return mapToAccountingArticlesResponse(null, null, null, request, accountingArticles);
	}

	@Override
	public Response delete(String accountingArticleCode, Request request) {
		ActionStatus result = new ActionStatus();
		result.setJson(null);
		try {
			accountingArticleApiService.delete(accountingArticleCode)
					.map(accountingArticle -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(accountingArticle))).build())
					.orElse(Response.status(Response.Status.NOT_FOUND).entity(result).build());
			return Response.ok(result).build();
		}catch(BadRequestException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			return Response.status(Response.Status.NOT_FOUND).entity(result).build();
		}
	}

	public Response deleteByAccountingCode(String accountingArticleCode, Request request) {
    	ActionStatus result = new ActionStatus();
    	result.setJson(null);
    	try {
			List<AccountingArticle> accountingArticles = accountingArticleApiService.deleteByAccountingCode(accountingArticleCode);
			return mapToAccountingArticlesResponse(null, null, null, request, accountingArticles);
    	}catch(BadRequestException e) {
    		result.setStatus(ActionStatusEnum.FAIL);
    		result.setMessage(e.getMessage());
    		return Response.status(Response.Status.NOT_FOUND).entity(result).build();
    	}
	}

	public Response list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter, Request request) {
        List<AccountingArticle> accoutnigArticleEntities = accountingArticleApiService.list(offset, limit, sort, orderBy, filter);
		return mapToAccountingArticlesResponse(offset, limit, filter, request, accoutnigArticleEntities);
	}

	private Response mapToAccountingArticlesResponse(Long offset, Long limit, Map<String, Object> filter, Request request, List<AccountingArticle> accoutnigArticleEntities) {
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
