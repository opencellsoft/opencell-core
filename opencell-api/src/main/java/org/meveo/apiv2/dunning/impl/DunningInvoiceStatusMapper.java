package org.meveo.apiv2.dunning.impl;


import org.meveo.apiv2.dunning.ImmutableDunningInvoiceStatus;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningSettings;

public class DunningInvoiceStatusMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningInvoiceStatus, DunningInvoiceStatus> {

	@Override
	protected org.meveo.apiv2.dunning.DunningInvoiceStatus toResource(DunningInvoiceStatus entity) {
        return ImmutableDunningInvoiceStatus.builder().id(entity.getId()).context(entity.getContext()).status(entity.getStatus())
                .dunningSettings(createResource((AuditableEntity) entity.getDunningSettings())).build();
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
        entity.setStatus(resource.getStatus());
        entity.setContext(resource.getContext());
        return entity;
    }
}
