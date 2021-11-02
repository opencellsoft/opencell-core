package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.DunningAgentInput;
import org.meveo.apiv2.dunning.ImmutableDunningAgentInput;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.dunning.DunningAgent;
import org.meveo.model.dunning.DunningSettings;

public class DunningAgentMapper extends ResourceMapper<DunningAgentInput, DunningAgent> {

	@Override
	protected DunningAgentInput toResource(DunningAgent entity) {
		return ImmutableDunningAgentInput.builder()
				.id(entity.getId())
				.agentEmailItem(entity.getAgentEmailItem())
				.collectionAgency(entity.getCollectionAgency())
				.agentLastNameItem(entity.getAgentLastNameItem())
				.agentFirstNameItem(entity.getAgentFirstNameItem())
				.dunningSettings(createResource(entity.getDunningSettings()))
				.external(entity.isExternal())
				.build();
		
	}

	@Override
	protected DunningAgent toEntity(DunningAgentInput resource) {
		var entity = new DunningAgent();
		if(resource.getDunningSettings() != null) {
			var dunnigSetting = new DunningSettings();
			dunnigSetting.setId(resource.getDunningSettings().getId());
			dunnigSetting.setCode(resource.getDunningSettings().getCode());
			entity.setDunningSettings(dunnigSetting);
		}
		entity.setAgentEmailItem(resource.getAgentEmailItem());
		entity.setCollectionAgency(resource.getCollectionAgency());
		entity.setAgentLastNameItem(resource.getAgentLastNameItem());
		entity.setAgentFirstNameItem(resource.getAgentFirstNameItem());
		entity.setExternal(resource.getExternal());
		entity.setId(resource.getId());
		return entity;
	}

}
