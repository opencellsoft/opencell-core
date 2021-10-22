package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningPauseReasons;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningPauseReasons;
import org.meveo.model.dunning.DunningSettings;

public class DunningPauseReasonsMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningPauseReasons, DunningPauseReasons> {

	@Override
	protected org.meveo.apiv2.dunning.DunningPauseReasons toResource(DunningPauseReasons entity) {
		return ImmutableDunningPauseReasons.builder()
				.id(entity.getId())
				.language(createResource(entity.getLanguage()))
				.pauseReason(entity.getPauseReason())
				.description(entity.getDescription())
				.dunningSettings(createResource((entity.getDunningSettings())))
                .build();
    }

    @Override
    protected DunningPauseReasons toEntity(org.meveo.apiv2.dunning.DunningPauseReasons resource) {
        var entity = new DunningPauseReasons();
        entity.setId(resource.getId());
        resource.getDunningSettings();
        var dunningSettings = new DunningSettings();
        dunningSettings.setId(resource.getDunningSettings().getId());
        dunningSettings.setCode(resource.getDunningSettings().getCode());
        entity.setDunningSettings(dunningSettings);
        resource.getLanguage();
        var tradingLanguage = new TradingLanguage();
        tradingLanguage.setId(resource.getLanguage().getId());
        entity.setLanguage(tradingLanguage);
        entity.setDescription(resource.getDescription());
        entity.setPauseReason(resource.getPauseReason());
        return entity;
    }
}
