package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningCollectionManagement;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionManagement;
import org.meveo.apiv2.dunning.resource.DunningCollectionManagementResource;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.dunning.service.DunningCollectionManagementApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

public class DunningCollectionManagementResourceImpl implements DunningCollectionManagementResource {

	@Inject
	private DunningCollectionManagementApiService dunningCollectionManagementApiService;
	private final DunningCollectionManagementMapper mapper = new DunningCollectionManagementMapper();
	
	@Override
	public Response create(DunningCollectionManagement dunningCollectionManagement) {
		var savedDunningCollection = dunningCollectionManagementApiService.create(mapper.toEntity(dunningCollectionManagement));
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningCollectionManagementResource.class, savedDunningCollection.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedDunningCollection)))
				.build();
	}

	@Override
	public Response update(DunningCollectionManagement dunningCollectionManagement, String dunningSettingsCode,
			String agentEmailItem) {
		var updatedDunningCollection = dunningCollectionManagementApiService.update(dunningSettingsCode, agentEmailItem, mapper.toEntity(dunningCollectionManagement));
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningCollectionManagementResource.class, updatedDunningCollection.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(updatedDunningCollection)))
				.build();
	}

	@Override
	public Response delete(String dunningSettingsCode, String agentEmailItem) {
		var deletedDunningCollection = dunningCollectionManagementApiService.delete(dunningSettingsCode, agentEmailItem);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningCollectionManagementResource.class, deletedDunningCollection.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(deletedDunningCollection)))
				.build();
	}

	

	private org.meveo.apiv2.dunning.DunningCollectionManagement toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningCollectionManagement dunningSettings) {
		return ImmutableDunningCollectionManagement.copyOf(dunningSettings)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningSettingResource.class)
											.withId(dunningSettings.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

	

}
