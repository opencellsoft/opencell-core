package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableFinanceSettings;
import org.meveo.apiv2.settings.openOrderSetting.impl.OpenOrderSettingMapper;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;

public class FinanceSettingsMapper  extends ResourceMapper<org.meveo.apiv2.securityDeposit.FinanceSettings, FinanceSettings> {

    private OpenOrderSettingMapper openOrderSettingMapper = new OpenOrderSettingMapper();
    @Override protected org.meveo.apiv2.securityDeposit.FinanceSettings toResource(FinanceSettings entity) {
        ImmutableFinanceSettings.Builder builder = ImmutableFinanceSettings.builder()
                .id(entity.getId())
                .useSecurityDeposit(entity.isUseSecurityDeposit())
                .maxAmountPerSecurityDeposit(entity.getMaxAmountPerSecurityDeposit())
                .maxAmountPerCustomer(entity.getMaxAmountPerCustomer())
                .autoRefund(entity.isAutoRefund());
        if(entity.getAuxiliaryAccounting() != null) {
            builder.useAuxiliaryAccounting(entity.getAuxiliaryAccounting().isUseAuxiliaryAccounting())
                    .auxiliaryAccountCodeEl(entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl())
                    .auxiliaryAccountLabelEl(entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl());
        }

         if(entity.getOpenOrderSetting() != null) {
            builder.openOrderSetting(openOrderSettingMapper.toResource(entity.getOpenOrderSetting()));
        }
        return builder.build();
    }

    @Override protected FinanceSettings toEntity(org.meveo.apiv2.securityDeposit.FinanceSettings resource) {
        return toEntity(new FinanceSettings(), resource);
    }

     protected FinanceSettings toEntity(FinanceSettings financeSettings, org.meveo.apiv2.securityDeposit.FinanceSettings resource) {
         financeSettings.setUseSecurityDeposit(resource.getUseSecurityDeposit());
         financeSettings.setMaxAmountPerSecurityDeposit(resource.getMaxAmountPerSecurityDeposit());
         financeSettings.setMaxAmountPerCustomer(resource.getMaxAmountPerCustomer());
         financeSettings.setAutoRefund(resource.getAutoRefund());
         AuxiliaryAccounting auxiliaryAccounting = new AuxiliaryAccounting();
         auxiliaryAccounting.setUseAuxiliaryAccounting(resource.getUseAuxiliaryAccounting());
         auxiliaryAccounting.setAuxiliaryAccountCodeEl(resource.getAuxiliaryAccountCodeEl());
         auxiliaryAccounting.setAuxiliaryAccountLabelEl(resource.getAuxiliaryAccountLabelEl());
         financeSettings.setAuxiliaryAccounting(auxiliaryAccounting);
         return financeSettings;
    }
}
