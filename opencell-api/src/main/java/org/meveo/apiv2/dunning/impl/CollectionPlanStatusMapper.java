package org.meveo.apiv2.dunning.impl;

import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.apiv2.dunning.ImmutableCollectionPlanStatus;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.model.dunning.DunningSettings;

public class CollectionPlanStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.CollectionPlanStatus, CollectionPlanStatus> {

	@Override
	protected org.meveo.apiv2.dunning.CollectionPlanStatus toResource(CollectionPlanStatus entity) {
		return ImmutableCollectionPlanStatus.builder()
				.id(entity.getId())
				.dunningSettings(createResource(entity.getDunningSettings()))
				.context(entity.getContext())
				.language(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(entity.getLanguage()))
				.status(entity.getStatus())
				.build();
		
	}

	@Override
	protected CollectionPlanStatus toEntity(org.meveo.apiv2.dunning.CollectionPlanStatus resource) {
		var entity = new CollectionPlanStatus();
		if(resource.getDunningSettings() != null) {
			var dunningSetting = new DunningSettings();
			dunningSetting.setId(resource.getDunningSettings().getId());
			dunningSetting.setCode(resource.getDunningSettings().getCode());
			entity.setDunningSettings(dunningSetting);
		}
		entity.setId(resource.getId());
		entity.setContext(resource.getContext());
        if( resource.getLanguage() !=null && ! resource.getLanguage().isEmpty()) {
        for(LanguageDescriptionDto languageDescription : resource.getLanguage()) {
        	entity.getLanguage().put(languageDescription.getLanguageCode(), languageDescription.getDescription());
        }
        }
		entity.setStatus(resource.getStatus());
		return entity;
	}

}
