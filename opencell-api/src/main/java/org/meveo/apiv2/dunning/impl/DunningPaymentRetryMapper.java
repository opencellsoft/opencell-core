package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.ImmutableDunningPaymentRetry;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningPaymentRetry;

public class DunningPaymentRetryMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningPaymentRetry, DunningPaymentRetry> {

    @Override
    protected org.meveo.apiv2.dunning.DunningPaymentRetry toResource(DunningPaymentRetry entity) {
        return ImmutableDunningPaymentRetry.builder().id(entity.getId()).paymentMethod(entity.getPaymentMethod()).psp(entity.getPsp()).numPayRetries(entity.getNumPayRetries())
                .payRetryFrequencyUnit(entity.getPayRetryFrequencyUnit()).payRetryFrequency(entity.getPayRetryFrequency())
                .dunningSettings(createResource((AuditableEntity) entity.getDunningSettings())).build();
    }

    @Override
    protected DunningPaymentRetry toEntity(org.meveo.apiv2.dunning.DunningPaymentRetry resource) {
        var entity = new DunningPaymentRetry();
        entity.setId(resource.getId());
        entity.setPaymentMethod(resource.getPaymentMethod());
        entity.setPsp(resource.getPsp());
        entity.setNumPayRetries(resource.getNumPayRetries());
        entity.setPayRetryFrequencyUnit(resource.getPayRetryFrequencyUnit());
        entity.setPayRetryFrequency(resource.getPayRetryFrequency());
        var dunningSettings = new DunningSettings();
        dunningSettings.setId(resource.getDunningSettings().getId());
        dunningSettings.setCode(resource.getDunningSettings().getCode());
        entity.setDunningSettings(dunningSettings);
        return entity;
    }
}
