package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.CollectionPlanStatus;
import org.meveo.apiv2.dunning.ImmutableCollectionPlanStatus;
import org.meveo.apiv2.dunning.resource.CollectionPlanStatusResource;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.dunning.service.CollectionPlanStatusApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

public class CollectionPlanStatusResourceImpl implements CollectionPlanStatusResource {

	private CollectionPlanStatusMapper mapper = new CollectionPlanStatusMapper();
	@Inject
	private CollectionPlanStatusApiService collectionPlanStatusApiService;
	
	@Override
	public Response create(CollectionPlanStatus collectionPlanStatus) {
		var entity = collectionPlanStatusApiService.create(mapper.toEntity(collectionPlanStatus));
		return Response.created(LinkGenerator.getUriBuilderFromResource(CollectionPlanStatusResource.class, entity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(entity)))
				.build();
	}
	
	@Override
	public Response update(CollectionPlanStatus collectionPlanStatus, String dunningSettingsCode, String status) {
		var entity = collectionPlanStatusApiService.update(dunningSettingsCode, status, mapper.toEntity(collectionPlanStatus));
		return Response.created(LinkGenerator.getUriBuilderFromResource(CollectionPlanStatusResource.class, entity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(entity)))
				.build();
	}
	
	@Override
	public Response delete(String dunningSettingsCode, String status) {
		var entity = collectionPlanStatusApiService.delete(dunningSettingsCode, status);
		return Response.created(LinkGenerator.getUriBuilderFromResource(CollectionPlanStatusResource.class, entity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(entity)))
				.build();
	}

	
	

	private org.meveo.apiv2.dunning.CollectionPlanStatus toResourceOrderWithLink(org.meveo.apiv2.dunning.CollectionPlanStatus collectionPlanStatus) {
		return ImmutableCollectionPlanStatus.copyOf(collectionPlanStatus)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningSettingResource.class)
											.withId(collectionPlanStatus.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}


}
