package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.DunningStopReasons;
import org.meveo.apiv2.dunning.ImmutableDunningStopReasons;
import org.meveo.apiv2.dunning.resource.DunningStopReasonResource;
import org.meveo.apiv2.dunning.service.DunningStopReasonApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class DunningStopReasonsResourceImpl implements DunningStopReasonResource {

	@Inject
	private DunningStopReasonApiService dunningStopReasonApiService;
	private DunningStopReasonsMapper mapper = new DunningStopReasonsMapper();
	
	@Override
	public Response create(org.meveo.apiv2.dunning.DunningStopReasons dunningStopReasons) {
		var entity = mapper.toEntity(dunningStopReasons);
		var savedEntity = dunningStopReasonApiService.create(entity);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningStopReasonResource.class, savedEntity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedEntity)))
				.build();
	}


	@Override
	public Response update(org.meveo.apiv2.dunning.DunningStopReasons entityDto, Long id) {
		var updatedEntity = dunningStopReasonApiService.update(id, mapper.toEntity(entityDto)).get();
		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updatedEntity))).build();
	}
	
	private org.meveo.apiv2.dunning.DunningStopReasons toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningStopReasons entityDto) {
		return ImmutableDunningStopReasons.copyOf(entityDto)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningStopReasonResource.class)
											.withId(entityDto.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

	@Override
	public Response delete(Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningStopReasonApiService.delete(id).get()))).build();
	}


}
