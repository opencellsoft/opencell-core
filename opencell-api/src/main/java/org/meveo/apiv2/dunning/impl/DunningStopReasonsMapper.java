package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningStopReasons;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningStopReasons;

public class DunningStopReasonsMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningStopReasons, DunningStopReasons> {

	@Override
	protected org.meveo.apiv2.dunning.DunningStopReasons toResource(DunningStopReasons entity) {
		return ImmutableDunningStopReasons.builder()
				.id(entity.getId())
				.language(createResource(entity.getLanguage()))
				.stopReason(entity.getStopReason())
				.description(entity.getDescription())
				.dunningSettings(createResource((entity.getDunningSettings())))
                .build();
    }

    @Override
    protected DunningStopReasons toEntity(org.meveo.apiv2.dunning.DunningStopReasons resource) {
        var entity = new DunningStopReasons();
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
        entity.setStopReason(resource.getStopReason());
        return entity;
    }
}
