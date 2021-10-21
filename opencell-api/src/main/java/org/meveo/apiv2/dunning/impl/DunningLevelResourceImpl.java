package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.ImmutableDunningLevel;
import org.meveo.apiv2.dunning.resource.DunningLevelResource;
import org.meveo.apiv2.dunning.service.DunningLevelApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

public class DunningLevelResourceImpl implements DunningLevelResource {

	@Inject
	private DunningLevelApiService dunningLevelApiService;
	private DunningLevelMapper mapper = new DunningLevelMapper();

	@Override
	public Response create(org.meveo.apiv2.dunning.DunningLevel dunningLevel) {
		var entity = mapper.toEntity(dunningLevel);
		var savedDunningLevel = dunningLevelApiService.create(entity);
		return Response.ok(LinkGenerator.getUriBuilderFromResource(DunningLevelResource.class, savedDunningLevel.getId()).build())
			.entity(toResourceOrderWithLink(mapper.toResource(savedDunningLevel))).build();
	}

	private org.meveo.apiv2.dunning.DunningLevel toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningLevel dunningLevel) {
		return ImmutableDunningLevel.copyOf(dunningLevel).withLinks(new LinkGenerator.SelfLinkGenerator(DunningLevelResource.class).withId(dunningLevel.getId()).withGetAction()
			.withPostAction().withPutAction().withPatchAction().withDeleteAction().build());
	}
}
