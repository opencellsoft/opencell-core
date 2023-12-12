package org.meveo.apiv2.article.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.article.*;
import org.meveo.apiv2.article.resource.AccountingArticleResource;
import org.meveo.apiv2.article.service.AccountingArticleApiService;
import org.meveo.apiv2.article.service.AccountingArticleBaseApi;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.UntdidAllowanceCode;
import org.meveo.service.billing.impl.UntdidAllowanceCodeService;

@Interceptors({ WsRestApiInterceptor.class })
public class AccountingArticleResourceImpl implements AccountingArticleResource {

    @Inject private AccountingArticleApiService accountingArticleApiService;
    @Inject private AccountingArticleBaseApi accountingArticleBaseApi;
    @Inject private UntdidAllowanceCodeService untdidAllowanceCodeService;

	@Inject
	private GenericPagingAndFilteringUtils genericPagingAndFilteringUtils;
    
    private AccountingArticleMapper mapper = new AccountingArticleMapper();

    @Override
    public Response createAccountingArticle(org.meveo.apiv2.article.AccountingArticle accountingArticle) {

        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);
		accountingArticleEntity.
				setAllowanceCode(untdidAllowanceCodeService.getByCode(accountingArticle.getAllowanceCode()));
        accountingArticleBaseApi.populateCustomFieldsForGenericApi(accountingArticle.getCustomFields(),
				accountingArticleEntity, true);
        accountingArticleEntity = accountingArticleApiService.create(accountingArticleEntity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class,
						accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accountingArticleEntity)))
                .build();
    }

	@Override
	public Response updateAccountingArticle(Long id, org.meveo.apiv2.article.AccountingArticle accountingArticle) {
        AccountingArticle accountingArticleEntity = mapper.toEntity(accountingArticle);        
        accountingArticleEntity.setAllowanceCode(untdidAllowanceCodeService.getByCode(accountingArticle.getAllowanceCode()));
        accountingArticleBaseApi.populateCustomFieldsForGenericApi(accountingArticle.getCustomFields(), accountingArticleEntity, false);
        Optional<AccountingArticle> accountingUpdated = accountingArticleApiService.update(id, accountingArticleEntity);
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticleEntity.getId()).build())
                .entity(toResourceOrderWithLink(mapper.toResource(accountingUpdated.orElseThrow(NotFoundException::new))))
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
	@Transactional
	public Response delete(String accountingArticleCode, Request request) {
		ActionStatus result = new ActionStatus();
		result.setJson(null);
		try {
			accountingArticleApiService.delete(accountingArticleCode)
					.map(accountingArticle -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(accountingArticle))).build())
					.orElse(Response.status(Response.Status.NOT_FOUND).entity(result).build());
			return Response.ok(result).build();
		} catch(BadRequestException e) {
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
		long apiLimit = genericPagingAndFilteringUtils.getLimit(limit != null ? limit.intValue() : null);
        List<AccountingArticle> accountingArticleEntities = accountingArticleApiService.list(offset, apiLimit, sort, orderBy, filter);
		return mapToAccountingArticlesResponse(offset, apiLimit, filter, request, accountingArticleEntities);
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
				.map(accountingArticle -> toResourceOrderWithLink(mapper.toResource(accountingArticle)))
				.toArray(ImmutableAccountingArticle[]::new);
		Long orderCount = accountingArticleApiService.getCount(filter);
		AccountingArticles articles = ImmutableAccountingArticles.builder().addData(accountingList).offset(offset).limit(limit).total(orderCount)
				.build().withLinks(new LinkGenerator.PaginationLinkGenerator(AccountingArticleResource.class)
						.offset(offset).limit(limit).total(orderCount).build());
		return Response.ok().cacheControl(cc).tag(etag).entity(articles).build();
	}

	@Override
	public Response getAccountingArticles(String productCode, Map<String, Object> attributes, Request request) {
		return accountingArticleApiService.getAccountingArticles(productCode, attributes)
											.map(accountingArticle -> Response.ok().entity(toResourceOrderWithLink(mapper.toResource(accountingArticle))).build())
											.orElseThrow(NotFoundException::new);
	}

	@Override
	public Response createAccountingCodeMapping(AccountingCodeMappingInput accountingCodeMapping) {
		accountingArticleApiService.createAccountingCodeMappings(accountingCodeMapping);
		return Response
				.ok()
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Accounting code mapping successfully created\"}}")
				.build();
	}

	@Override
	public Response updateAccountingCodeMapping(String accountingArticleCode,
												AccountingCodeMappingInput accountingCodeMappingInput) {
		AccountingArticle accountingArticle =
				accountingArticleApiService.updateAccountingCodeMapping(accountingArticleCode, accountingCodeMappingInput);
		return Response
				.ok(LinkGenerator.getUriBuilderFromResource(AccountingArticleResource.class, accountingArticle.getId()).build())
				.entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"Accounting code mapping successfully updated\"}}")
				.build();
	}
}
