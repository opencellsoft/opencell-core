package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.ImmutableDunningCollectionPlanStatus;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.AuditableEntity;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningSettings;

public class DunningCollectionPlanStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningCollectionPlanStatus, DunningCollectionPlanStatus> {

	@Override
	protected org.meveo.apiv2.dunning.DunningCollectionPlanStatus toResource(DunningCollectionPlanStatus entity) {
		return ImmutableDunningCollectionPlanStatus.builder().id(entity.getId()).dunningSettings(createResource((AuditableEntity) entity.getDunningSettings()))
				.context(entity.getContext()).status(entity.getStatus()).build();

	}

	@Override
	protected DunningCollectionPlanStatus toEntity(org.meveo.apiv2.dunning.DunningCollectionPlanStatus resource) {
		var entity = new DunningCollectionPlanStatus();
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
