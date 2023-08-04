package org.meveo.apiv2.dunning.impl;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.meveo.apiv2.dunning.ImmutableDunningSettings;
import org.meveo.apiv2.dunning.resource.DunningSettingResource;
import org.meveo.apiv2.dunning.service.DunningSettingsApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningTemplateService;

public class DunningSettingsResourceImpl implements DunningSettingResource {

	@Inject
	private DunningSettingsApiService dunningSettingsApiService;

	@Inject
	private DunningTemplateService dunningTemplateService;

	@Inject
	private DunningLevelService dunningLevelService;

	private DunningSettingsMapper mapper = new DunningSettingsMapper();

	@Transactional
	@Override
	public Response create(org.meveo.apiv2.dunning.DunningSettings dunningSettings) {
		var entity = mapper.toEntity(dunningSettings);
		var savedDunning = dunningSettingsApiService.create(entity);

		//Update DunningTemplate after creating a new DunningSettings
		dunningTemplateService.updateDunningTemplateByDunningMode(dunningSettings.getDunningMode());

		//Activate and Deactivate DunningLevel by DunningSettings
		dunningLevelService.updateDunningLevelAfterCreatingOrUpdatingDunningSetting(dunningSettings.getDunningMode());

		return Response.created(LinkGenerator.getUriBuilderFromResource(DunningSettingResource.class, savedDunning.getId()).build())
				.entity(toResourceOrderWithLink(mapper.toResource(savedDunning)))
				.build();
	}

	@Transactional
	@Override
	public Response update(org.meveo.apiv2.dunning.DunningSettings dunningSettings, Long dunningId) {
		var updated = dunningSettingsApiService.update(dunningId, mapper.toEntity(dunningSettings)).get();

		//Update DunningTemplate after creating a new DunningSettings
		dunningTemplateService.updateDunningTemplateByDunningMode(dunningSettings.getDunningMode());

		//Activate and Deactivate DunningLevel by DunningSettings
		dunningLevelService.updateDunningLevelAfterCreatingOrUpdatingDunningSetting(dunningSettings.getDunningMode());

		return Response.status(Status.ACCEPTED).entity(toResourceOrderWithLink(mapper.toResource(updated))).build();
	}
	
	private org.meveo.apiv2.dunning.DunningSettings toResourceOrderWithLink(org.meveo.apiv2.dunning.DunningSettings dunningSettings) {
		return ImmutableDunningSettings.copyOf(dunningSettings)
				.withLinks(
						new LinkGenerator.SelfLinkGenerator(DunningSettingResource.class)
											.withId(dunningSettings.getId())
				                            .withGetAction().withPostAction().withPutAction().withPatchAction().withDeleteAction()
				                            .build()
						);
	}

	@Override
	public Response delete(Long dunningId) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningSettingsApiService.delete(dunningId).get()))).build();
	}

	@Transactional
	@Override
	public Response findByCode(String dunningCode) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningSettingsApiService.findByCode(dunningCode).get()))).build();
	}

	@Transactional
	@Override
	public Response duplicate(String dunningCode) {
		return Response.ok(toResourceOrderWithLink(mapper.toResource(dunningSettingsApiService.duplicate(dunningCode)))).build();
	}

}
