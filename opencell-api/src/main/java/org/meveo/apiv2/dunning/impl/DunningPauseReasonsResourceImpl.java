package org.meveo.apiv2.dunning.impl;


import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningPauseReason;
import org.meveo.apiv2.dunning.ImmutableDunningPauseReason;
import org.meveo.apiv2.dunning.resource.DunningPauseReasonResource;
import org.meveo.apiv2.dunning.service.DunningPauseReasonApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningPauseReasonsResourceImpl implements DunningPauseReasonResource {

	@Inject
	private DunningPauseReasonApiService dunningPauseReasonApiService;
	private DunningPauseReasonsMapper mapper = new DunningPauseReasonsMapper();

	@Override
	public Response create(DunningPauseReason dunningPauseReason) {
		var entity = mapper.toEntity(dunningPauseReason);
		var savedEntity = dunningPauseReasonApiService.create(entity);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningPauseReasonResource.class, savedEntity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedEntity))).build();
	}

	@Override
	public Response update(DunningPauseReason entityDto, Long id) {
		var updatedEntity = dunningPauseReasonApiService.update(id, mapper.toEntity(entityDto)).get();
		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updatedEntity))).build();
	}

	private DunningPauseReason toResourceOrderWithLink(DunningPauseReason entityDto) {
		return ImmutableDunningPauseReason.copyOf(entityDto).withLinks(
				new LinkGenerator.SelfLinkGenerator(DunningPauseReasonResource.class).withId(entityDto.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
						.withDeleteAction().build());
	}

	@Override
	public Response delete(Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningPauseReasonApiService.delete(id).get()))).build();
	}


}
