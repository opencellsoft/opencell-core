package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningPauseReasons;
import org.meveo.apiv2.dunning.resource.DunningPauseReasonResource;
import org.meveo.apiv2.dunning.service.DunningPauseReasonApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class DunningPauseReasonsResourceImpl implements DunningPauseReasonResource {

	@Inject
	private DunningPauseReasonApiService dunningPauseReasonApiService;
	private DunningPauseReasonsMapper mapper = new DunningPauseReasonsMapper();
	
	@Override
	public Response create(org.meveo.apiv2.dunning.DunningPauseReasons dunningPauseReasons) {
		var entity = mapper.toEntity(dunningPauseReasons);
		var savedEntity = dunningPauseReasonApiService.create(entity);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningPauseReasonResource.class, savedEntity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedEntity)))
				.build();
	}

	@Override
	public Response update(org.meveo.apiv2.dunning.DunningPauseReasons entityDto, String dunningSettingsCode, Long id) {
		var updatedEntity = dunningPauseReasonApiService.update(id, mapper.toEntity(entityDto)).get();
		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updatedEntity))).build();
	}
	
	private org.meveo.apiv2.dunning.DunningPauseReasons toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningPauseReasons entityDto) {
		return ImmutableDunningPauseReasons.copyOf(entityDto)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningPauseReasonResource.class)
											.withId(entityDto.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

	@Override
	public Response delete(String dunningSettingsCode, Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningPauseReasonApiService.delete(id).get()))).build();
	}


}
