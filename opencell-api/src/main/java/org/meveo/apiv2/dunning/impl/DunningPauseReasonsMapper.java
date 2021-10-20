package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningPauseReasons;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.dunning.DunningPauseReasons;
import org.meveo.model.dunning.DunningSettings;

public class DunningPauseReasonsMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningPauseReasons, DunningPauseReasons> {

	@Override
	protected org.meveo.apiv2.dunning.DunningPauseReasons toResource(DunningPauseReasons entity) {
		return ImmutableDunningPauseReasons.builder()
				.id(entity.getId())
				.language(entity.getLanguage())
				.pauseReason(entity.getPauseReason())
				.description(entity.getDescription())
				.dunningSettings(createResource((entity.getDunningSettings())))
                .build();
    }

    @Override
    protected DunningPauseReasons toEntity(org.meveo.apiv2.dunning.DunningPauseReasons resource) {
        var entity = new DunningPauseReasons();
        entity.setId(resource.getId());
        if (resource.getDunningSettings() != null) {
            var dunningSettings = new DunningSettings();
            dunningSettings.setId(resource.getDunningSettings().getId());
            dunningSettings.setCode(resource.getDunningSettings().getCode());
            entity.setDunningSettings(dunningSettings);
        }
        entity.setLanguage(resource.getLanguage());
        entity.setDescription(resource.getDescription());
        entity.setPauseReason(resource.getPauseReason());
        return entity;
    }
}
