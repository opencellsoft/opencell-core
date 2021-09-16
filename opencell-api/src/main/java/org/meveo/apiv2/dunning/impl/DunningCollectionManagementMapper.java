package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.ImmutableDunningCollectionManagement;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.dunning.DunningCollectionManagement;
import org.meveo.model.dunning.DunningSettings;

public class DunningCollectionManagementMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningCollectionManagement, DunningCollectionManagement> {

	@Override
	protected org.meveo.apiv2.dunning.DunningCollectionManagement toResource(DunningCollectionManagement entity) {
		return ImmutableDunningCollectionManagement.builder()
				.id(entity.getId())
				.agentEmailItem(entity.getAgentEmailItem())
				.emailCollectionAgency(entity.getEmailCollectionAgency())
				.agentLastNameItem(entity.getAgentLastNameItem())
				.agentFirstNameItem(entity.getAgentFirstNameItem())
				.dunningSettings(createResource(entity.getDunningSettings()))
				.includeCollectionAgency(entity.isIncludeCollectionAgency())
				.build();
		
	}

	@Override
	protected DunningCollectionManagement toEntity(org.meveo.apiv2.dunning.DunningCollectionManagement resource) {
		var entity = new DunningCollectionManagement();
		if(resource.getDunningSettings() != null) {
			var dunnigSetting = new DunningSettings();
			dunnigSetting.setId(resource.getDunningSettings().getId());
			dunnigSetting.setCode(resource.getDunningSettings().getCode());
			entity.setDunningSettings(dunnigSetting);
		}
		entity.setAgentEmailItem(resource.getAgentEmailItem());
		entity.setEmailCollectionAgency(resource.getEmailCollectionAgency());
		entity.setAgentLastNameItem(resource.getAgentLastNameItem());
		entity.setAgentFirstNameItem(resource.getAgentFirstNameItem());
		entity.setIncludeCollectionAgency(resource.getIncludeCollectionAgency());
		entity.setId(resource.getId());
		return entity;
	}

}
