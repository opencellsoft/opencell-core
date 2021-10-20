package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningInvoiceStatus;
import org.meveo.apiv2.dunning.ImmutableDunningStopReasons;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningStopReasons;

public class DunningInvoiceStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningInvoiceStatus, DunningInvoiceStatus> {

	@Override
	protected org.meveo.apiv2.dunning.DunningInvoiceStatus toResource(DunningInvoiceStatus entity) {
		return ImmutableDunningInvoiceStatus.builder()
				.id(entity.getId())
				.language(entity.getLanguage())
				.context(entity.getContext())
				.status(entity.getStatus())
				.dunningSettings(createResource((entity.getDunningSettings())))
                .build();
    }

    @Override
    protected DunningInvoiceStatus toEntity(org.meveo.apiv2.dunning.DunningInvoiceStatus resource) {
        var entity = new DunningInvoiceStatus();
        entity.setId(resource.getId());
        if (resource.getDunningSettings() != null) {
            var dunningSettings = new DunningSettings();
            dunningSettings.setId(resource.getDunningSettings().getId());
            dunningSettings.setCode(resource.getDunningSettings().getCode());
            entity.setDunningSettings(dunningSettings);
        }
        entity.setLanguage(resource.getLanguage());
        entity.setStatus(resource.getStatus());
        entity.setContext(resource.getContext());
        return entity;
    }
}
