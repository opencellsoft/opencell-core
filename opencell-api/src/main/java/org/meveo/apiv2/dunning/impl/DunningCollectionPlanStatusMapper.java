package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningCollectionPlanStatus;
import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlanStatus;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningCollectionPlanStatuses;
import org.meveo.model.dunning.DunningSettings;

public class DunningCollectionPlanStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningCollectionPlanStatus, DunningCollectionPlanStatuses> {

	@Override
	protected DunningCollectionPlanStatus toResource(DunningCollectionPlanStatuses entity) {
		return ImmutableDunningCollectionPlanStatus.builder()
				.id(entity.getId())
				.dunningSettings(createResource(entity.getDunningSettings()))
				.context(entity.getContext())
				.status(entity.getStatus())
				.build();
		
	}

	@Override
	protected DunningCollectionPlanStatuses toEntity(DunningCollectionPlanStatus resource) {
		var entity = new DunningCollectionPlanStatuses();
		resource.getDunningSettings();
		var dunningSetting = new DunningSettings();
		dunningSetting.setId(resource.getDunningSettings().getId());
		dunningSetting.setCode(resource.getDunningSettings().getCode());
		entity.setDunningSettings(dunningSetting);
		entity.setId(resource.getId());
		entity.setContext(resource.getContext());
		entity.setStatus(resource.getStatus());
		return entity;
	}

}
