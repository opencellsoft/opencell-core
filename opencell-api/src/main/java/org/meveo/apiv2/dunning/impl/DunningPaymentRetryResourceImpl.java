package org.meveo.apiv2.dunning.impl;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningPaymentRetry;
import org.meveo.apiv2.dunning.ImmutableDunningPaymentRetry;
import org.meveo.apiv2.dunning.resource.DunningPaymentRetryResource;
import org.meveo.apiv2.dunning.service.DunningPaymentRetryApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningPaymentRetryResourceImpl implements DunningPaymentRetryResource {

	@Inject
	private DunningPaymentRetryApiService dunningPaymentRetryApiService;
	private DunningPaymentRetryMapper mapper = new DunningPaymentRetryMapper();

	@Override
	public Response create(DunningPaymentRetry dunningStopReasons) {
		var entity = mapper.toEntity(dunningStopReasons);
		var savedEntity = dunningPaymentRetryApiService.create(entity);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningPaymentRetryResource.class, savedEntity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedEntity))).build();
	}

	@Override
	public Response update(DunningPaymentRetry entityDto, Long id) {
		var updatedEntity = dunningPaymentRetryApiService.update(id, mapper.toEntity(entityDto)).get();
		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updatedEntity))).build();
	}

	private DunningPaymentRetry toResourceOrderWithLink(DunningPaymentRetry entityDto) {
		return ImmutableDunningPaymentRetry.copyOf(entityDto).withLinks(
				new LinkGenerator.SelfLinkGenerator(DunningPaymentRetryResource.class).withId(entityDto.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
						.withDeleteAction().build());
	}

	@Override
	public Response delete(Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningPaymentRetryApiService.delete(id).get()))).build();
	}

}
