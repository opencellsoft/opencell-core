package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningCollectionPlanStatus;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlanStatus;
import org.meveo.apiv2.dunning.resource.CollectionPlanStatusResource;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.dunning.service.CollectionPlanStatusApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

@Interceptors({ WsRestApiInterceptor.class })
public class CollectionPlanStatusResourceImpl implements CollectionPlanStatusResource {

	private DunningCollectionPlanStatusMapper mapper = new DunningCollectionPlanStatusMapper();
	@Inject
	private CollectionPlanStatusApiService collectionPlanStatusApiService;
	
	@Override
	public Response create(DunningCollectionPlanStatus collectionPlanStatus) {
		var entity = collectionPlanStatusApiService.create(mapper.toEntity(collectionPlanStatus));
		return Response.created(LinkGenerator.getUriBuilderFromResource(CollectionPlanStatusResource.class, entity.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(entity)))
				.build();
	}
	
	@Override
	public Response update(DunningCollectionPlanStatus collectionPlanStatus, Long id) {
		var entity = collectionPlanStatusApiService.update(id, mapper.toEntity(collectionPlanStatus)).get();
		return Response.status(Response.Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(entity))).build();
	}

	@Override
	public Response delete(Long id) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(collectionPlanStatusApiService.delete(id).get()))).build();
	}

	
	

	private DunningCollectionPlanStatus toResourceOrderWithLink(DunningCollectionPlanStatus dunningCollectionPlanStatus) {
		return ImmutableDunningCollectionPlanStatus.copyOf(dunningCollectionPlanStatus)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningSettingResource.class)
											.withId(dunningCollectionPlanStatus.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}
}
