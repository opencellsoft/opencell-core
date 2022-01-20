package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableFinanceSettings;
import org.meveo.model.securityDeposit.FinanceSettings;

public class FinanceSettingsMapper  extends ResourceMapper<org.meveo.apiv2.securityDeposit.FinanceSettings, FinanceSettings> {

    @Override protected org.meveo.apiv2.securityDeposit.FinanceSettings toResource(FinanceSettings entity) {
        return ImmutableFinanceSettings.builder()
                .id(entity.getId())
                .useSecurityDeposit(entity.isUseSecurityDeposit())
                .maxAmountPerSecurityDeposit(entity.getMaxAmountPerSecurityDeposit())
                .maxAmountPerCustomer(entity.getMaxAmountPerCustomer())
                .autoRefund(entity.isAutoRefund())
                .build();
    }

    @Override protected FinanceSettings toEntity(org.meveo.apiv2.securityDeposit.FinanceSettings resource) {
        return toEntity(new FinanceSettings(), resource);
    }

     protected FinanceSettings toEntity(FinanceSettings financeSettings, org.meveo.apiv2.securityDeposit.FinanceSettings resource) {
        financeSettings.setUseSecurityDeposit(resource.getUseSecurityDeposit());
        financeSettings.setMaxAmountPerSecurityDeposit(resource.getMaxAmountPerSecurityDeposit());
        financeSettings.setMaxAmountPerCustomer(resource.getMaxAmountPerCustomer());
        financeSettings.setAutoRefund(resource.getAutoRefund());
        return financeSettings;
    }
}
