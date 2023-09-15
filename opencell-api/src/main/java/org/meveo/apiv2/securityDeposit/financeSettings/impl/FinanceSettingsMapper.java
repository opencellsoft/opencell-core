package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import java.util.List;
import java.util.Map;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableFinanceSettings;
import org.meveo.apiv2.settings.openOrderSetting.impl.OpenOrderSettingMapper;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;

public class FinanceSettingsMapper  extends ResourceMapper<org.meveo.apiv2.securityDeposit.FinanceSettings, FinanceSettings> {

    private OpenOrderSettingMapper openOrderSettingMapper = new OpenOrderSettingMapper();

    protected static final String AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL =
            "#{gca.code.substring(0, 3)}#{ca.isCompany ? ca.description : ca.name.lastName}";
    protected static final String AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL =
            "#{ca.isCompany ? ca.description : ca.name.firstName.concat(' ').concat(ca.name.lastName)}";
    @Override protected org.meveo.apiv2.securityDeposit.FinanceSettings toResource(FinanceSettings entity) {
        ImmutableFinanceSettings.Builder builder = ImmutableFinanceSettings.builder()
                .id(entity.getId())
                .useSecurityDeposit(entity.isUseSecurityDeposit())
                .maxAmountPerSecurityDeposit(entity.getMaxAmountPerSecurityDeposit())
                .maxAmountPerCustomer(entity.getMaxAmountPerCustomer())
                .autoRefund(entity.isAutoRefund())
                .activateDunning(entity.isActivateDunning())
                .enableBillingRedirectionRules(entity.isEnableBillingRedirectionRules())
                .discountAdvancedMode(entity.isDiscountAdvancedMode())
                .enablePriceList(entity.isEnablePriceList())
				.articleSelectionMode(entity.getArticleSelectionMode())
                .billingRunProcessWarning(entity.isBillingRunProcessWarning());
        if(entity.getAuxiliaryAccounting() != null) {
            builder.useAuxiliaryAccounting(entity.getAuxiliaryAccounting().isUseAuxiliaryAccounting())
                    .auxiliaryAccountCodeEl(entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl())
                    .auxiliaryAccountLabelEl(entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl());
        }

         if(entity.getOpenOrderSetting() != null) {
            builder.openOrderSetting(openOrderSettingMapper.toResource(entity.getOpenOrderSetting()));
        }
         
        // Set the entitiesWithHugeVolume field
        Map<String, List<String>> entitiesWithHugeVolume = entity.getEntitiesWithHugeVolume();
        builder.entitiesWithHugeVolume(entitiesWithHugeVolume);
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
         if(auxiliaryAccounting.isUseAuxiliaryAccounting()
                 && (auxiliaryAccounting.getAuxiliaryAccountCodeEl() == null
                 || auxiliaryAccounting.getAuxiliaryAccountCodeEl().isBlank())) {
             auxiliaryAccounting.setAuxiliaryAccountCodeEl(AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL);
         }
         if(auxiliaryAccounting.isUseAuxiliaryAccounting()
                 && (auxiliaryAccounting.getAuxiliaryAccountLabelEl() == null
                 || auxiliaryAccounting.getAuxiliaryAccountLabelEl().isBlank())) {
             auxiliaryAccounting.setAuxiliaryAccountLabelEl(AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL);
         }
         financeSettings.setAuxiliaryAccounting(auxiliaryAccounting);
         financeSettings.setActivateDunning(resource.getActivateDunning());
         financeSettings.setEnableBillingRedirectionRules(resource.getEnableBillingRedirectionRules());
         financeSettings.setDiscountAdvancedMode(resource.getDiscountAdvancedMode());
         financeSettings.setEnablePriceList(resource.getEnablePriceList());
	     if(resource.getArticleSelectionMode() != null) {
		     financeSettings.setArticleSelectionMode(resource.getArticleSelectionMode());
	     }
	     // Set the entitiesWithHugeVolume field
	     Map<String, List<String>> entitiesWithHugeVolume = resource.getEntitiesWithHugeVolume();
	     financeSettings.setEntitiesWithHugeVolume(entitiesWithHugeVolume);
         financeSettings.setBillingRunProcessWarning(resource.getBillingRunProcessWarning());
         return financeSettings;
    }
}