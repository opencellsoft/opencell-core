package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningInvoiceStatus;
import org.meveo.apiv2.dunning.ImmutableDunningStopReasons;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningStopReasons;

public class DunningInvoiceStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningInvoiceStatus, DunningInvoiceStatus> {

	@Override
	protected org.meveo.apiv2.dunning.DunningInvoiceStatus toResource(DunningInvoiceStatus entity) {
		return ImmutableDunningInvoiceStatus.builder()
				.id(entity.getId())
				.language(createResource(entity.getLanguage()))
				.context(entity.getContext())
				.status(entity.getStatus())
				.dunningSettings(createResource((entity.getDunningSettings())))
                .build();
    }

    @Override
    protected DunningInvoiceStatus toEntity(org.meveo.apiv2.dunning.DunningInvoiceStatus resource) {
        var entity = new DunningInvoiceStatus();
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
        entity.setStatus(resource.getStatus());
        entity.setContext(resource.getContext());
        return entity;
    }
}
