package org.meveo.apiv2.dunning.impl;


import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningStopReason;
import org.meveo.apiv2.dunning.ImmutableDunningStopReason;
import org.meveo.apiv2.dunning.resource.DunningStopReasonResource;
import org.meveo.apiv2.dunning.service.DunningStopReasonApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningStopReasonsResourceImpl implements DunningStopReasonResource {

	@Inject
	private DunningStopReasonApiService dunningStopReasonApiService;
	private DunningStopReasonsMapper mapper = new DunningStopReasonsMapper();

	@Override
	public Response create(DunningStopReason dunningStopReason) {
		var entity = mapper.toEntity(dunningStopReason);
		var savedEntity = dunningStopReasonApiService.create(entity);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningStopReasonResource.class, savedEntity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedEntity))).build();
	}

	@Override
	public Response update(DunningStopReason entityDto, Long id) {
		var updatedEntity = dunningStopReasonApiService.update(id, mapper.toEntity(entityDto)).get();
		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updatedEntity))).build();
	}

	private DunningStopReason toResourceOrderWithLink(DunningStopReason entityDto) {
		return ImmutableDunningStopReason.copyOf(entityDto).withLinks(
				new LinkGenerator.SelfLinkGenerator(DunningStopReasonResource.class).withId(entityDto.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
						.withDeleteAction().build());
	}

	@Override
	public Response delete(Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningStopReasonApiService.delete(id).get()))).build();
	}


}
