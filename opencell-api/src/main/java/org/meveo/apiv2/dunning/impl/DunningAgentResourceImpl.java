package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.dunning.DunningAgentInput;
import org.meveo.apiv2.dunning.ImmutableDunningAgentInput;
import org.meveo.apiv2.dunning.resource.DunningAgentResource;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.dunning.service.DunningAgentApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

@Interceptors({ WsRestApiInterceptor.class })
public class DunningAgentResourceImpl implements DunningAgentResource {

	@Inject
	private DunningAgentApiService dunningAgentApiService;

	private final DunningAgentMapper mapper = new DunningAgentMapper();
	
	@Override
	public Response create(DunningAgentInput dunningAgentInput) {
		var savedDunningAgent = dunningAgentApiService.create(mapper.toEntity(dunningAgentInput));
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningAgentResource.class, savedDunningAgent.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedDunningAgent)))
				.build();
	}

	@Override
	public Response update(DunningAgentInput dunningAgentInput, String dunningSettingsCode,
			String agentEmailItem) {
		var updatedDunningAgent = dunningAgentApiService.update(dunningSettingsCode, agentEmailItem, mapper.toEntity(dunningAgentInput));
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningAgentResource.class, updatedDunningAgent.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(updatedDunningAgent)))
				.build();
	}

	@Override
	public Response delete(String dunningSettingsCode, String agentEmailItem) {
		var deletedDunningAgent = dunningAgentApiService.delete(dunningSettingsCode, agentEmailItem);
		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningAgentResource.class, deletedDunningAgent.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(deletedDunningAgent)))
				.build();
	}

	

	private org.meveo.apiv2.dunning.DunningAgentInput toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningAgentInput dunningSettings) {
		return ImmutableDunningAgentInput.copyOf(dunningSettings)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningSettingResource.class)
											.withId(dunningSettings.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

	

}
