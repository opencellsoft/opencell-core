package org.meveo.apiv2.securityDeposit.securityDepositSettings.impl;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositSettings;
import org.meveo.model.securityDeposit.SecurityDepositSettings;

public class SecurityDepositSettingsMapper  extends ResourceMapper<org.meveo.apiv2.securityDeposit.SecurityDepositSettings, SecurityDepositSettings> {

    @Override protected org.meveo.apiv2.securityDeposit.SecurityDepositSettings toResource(SecurityDepositSettings entity) {
        return ImmutableSecurityDepositSettings.builder()
                .id(entity.getId())
                .useSecurityDeposit(entity.isUseSecurityDeposit())
                .maxAmountPerSecurityDeposit(entity.getMaxAmountPerSecurityDeposit())
                .maxAmountPerCustomer(entity.getMaxAmountPerCustomer())
                .autoRefund(entity.isAutoRefund())
                .allowRenew(entity.isAllowRenew())
                .allowTransfer(entity.isAllowTransfer())
                .build();
    }

    @Override protected SecurityDepositSettings toEntity(org.meveo.apiv2.securityDeposit.SecurityDepositSettings resource) {
        return toEntity(new SecurityDepositSettings(), resource);
    }

     protected SecurityDepositSettings toEntity(SecurityDepositSettings securityDepositSettings, org.meveo.apiv2.securityDeposit.SecurityDepositSettings resource) {
        securityDepositSettings.setUseSecurityDeposit(resource.getUseSecurityDeposit());
        securityDepositSettings.setMaxAmountPerSecurityDeposit(resource.getMaxAmountPerSecurityDeposit());
        securityDepositSettings.setMaxAmountPerCustomer(resource.getMaxAmountPerCustomer());
        securityDepositSettings.setAutoRefund(resource.getAutoRefund());
        securityDepositSettings.setAllowRenew(resource.getAllowRenew());
        securityDepositSettings.setAllowTransfer(resource.getAllowTransfer());
        return securityDepositSettings;
    }
}
