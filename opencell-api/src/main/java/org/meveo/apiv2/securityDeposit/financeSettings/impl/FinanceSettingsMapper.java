package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableFinanceSettings;
import org.meveo.apiv2.securityDeposit.ImmutableHugeEntity;
import org.meveo.apiv2.settings.openOrderSetting.impl.OpenOrderSettingMapper;
import org.meveo.model.securityDeposit.AuxiliaryAccounting;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.HugeEntity;

public class FinanceSettingsMapper  extends ResourceMapper<org.meveo.apiv2.securityDeposit.FinanceSettings, FinanceSettings> {

    private OpenOrderSettingMapper openOrderSettingMapper = new OpenOrderSettingMapper();

    protected static final String AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL =
            "#{gca.code.substring(0, 3)}#{ca.isCompany ? ca.description : ca.name.lastName}";
    protected static final String AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL =
            "#{ca.isCompany ? ca.description : ca.name.firstName.concat(' ').concat(ca.name.lastName)}";

	@Override
	protected org.meveo.apiv2.securityDeposit.FinanceSettings toResource(FinanceSettings entity) {
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
                .billingRunProcessWarning(entity.isBillingRunProcessWarning())
                .nbPartitionsToKeep(entity.getNbPartitionsToKeep())
                .woPartitionPeriod(entity.getWoPartitionPeriod())
                .rtPartitionPeriod(entity.getRtPartitionPeriod())
                .edrPartitionPeriod(entity.getEdrPartitionPeriod())
                .handleAccountingPeriods(entity.isHandleAccountingPeriods())
                .handleFrameworkAgreement(entity.isHandleFrameworkAgreement())
                .handleInvoicingPlans(entity.isHandleInvoicingPlans());
        
        if (entity.getAuxiliaryAccounting() != null) {
            builder.useAuxiliaryAccounting(entity.getAuxiliaryAccounting().isUseAuxiliaryAccounting())
                    .auxiliaryAccountCodeEl(entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl())
                    .auxiliaryAccountLabelEl(entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl());
        }

        if (entity.getOpenOrderSetting() != null) {
            builder.openOrderSetting(openOrderSettingMapper.toResource(entity.getOpenOrderSetting()));
        }

        // Set the entitiesWithHugeVolume field
        Map<String, org.meveo.apiv2.securityDeposit.HugeEntity> entitiesWithHugeVolume = entity.getEntitiesWithHugeVolume()
                                                                                               .entrySet()
                                                                                               .stream()
                                                                                               .map(e -> Map.entry(e.getKey(), toHugeEntityResource(e.getValue())))
                                                                                               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        builder.entitiesWithHugeVolume(entitiesWithHugeVolume);
        return builder.build();
    }

	@Override
	protected FinanceSettings toEntity(org.meveo.apiv2.securityDeposit.FinanceSettings resource) {
		return toEntity(new FinanceSettings(), resource);
	}

     protected FinanceSettings toEntity(FinanceSettings financeSettings, org.meveo.apiv2.securityDeposit.FinanceSettings resource) {
         if (resource.getMaxAmountPerSecurityDeposit() != null) {
        	financeSettings.setMaxAmountPerSecurityDeposit(resource.getMaxAmountPerSecurityDeposit());
         }
         if (resource.getMaxAmountPerCustomer() != null) {
        	financeSettings.setMaxAmountPerCustomer(resource.getMaxAmountPerCustomer());
         }
         financeSettings.setUseSecurityDeposit(resource.getUseSecurityDeposit());
         financeSettings.setAutoRefund(resource.getAutoRefund());
         AuxiliaryAccounting auxiliaryAccounting = new AuxiliaryAccounting();
         auxiliaryAccounting.setUseAuxiliaryAccounting(resource.getUseAuxiliaryAccounting());
         auxiliaryAccounting.setAuxiliaryAccountCodeEl(resource.getAuxiliaryAccountCodeEl());
         auxiliaryAccounting.setAuxiliaryAccountLabelEl(resource.getAuxiliaryAccountLabelEl());
         if (auxiliaryAccounting.isUseAuxiliaryAccounting()
                 && (auxiliaryAccounting.getAuxiliaryAccountCodeEl() == null
                 || auxiliaryAccounting.getAuxiliaryAccountCodeEl().isBlank())) {
             auxiliaryAccounting.setAuxiliaryAccountCodeEl(AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL);
         }
         if (auxiliaryAccounting.isUseAuxiliaryAccounting()
                 && (auxiliaryAccounting.getAuxiliaryAccountLabelEl() == null
                 || auxiliaryAccounting.getAuxiliaryAccountLabelEl().isBlank())) {
             auxiliaryAccounting.setAuxiliaryAccountLabelEl(AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL);
         }
         financeSettings.setAuxiliaryAccounting(auxiliaryAccounting);
         financeSettings.setActivateDunning(resource.getActivateDunning());
         financeSettings.setEnableBillingRedirectionRules(resource.getEnableBillingRedirectionRules());
         financeSettings.setDiscountAdvancedMode(resource.getDiscountAdvancedMode());
         financeSettings.setEnablePriceList(resource.getEnablePriceList());
	     if (resource.getArticleSelectionMode() != null) {
		     financeSettings.setArticleSelectionMode(resource.getArticleSelectionMode());
	     }
	     // Set the entitiesWithHugeVolume field
	     if (resource.getEntitiesWithHugeVolume() != null) {
		    Map<String, org.meveo.apiv2.securityDeposit.HugeEntity> entitiesWithHugeVolume = resource.getEntitiesWithHugeVolume();
	        Map<String, HugeEntity> hugeEntitiesSettings = entitiesWithHugeVolume.entrySet()
	                                                                             .stream()
	                                                                             .map(e -> Map.entry(e.getKey(), toHugeEntity(e.getValue())))
	                                                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	        financeSettings.setEntitiesWithHugeVolume(hugeEntitiesSettings);
	     }
         financeSettings.setBillingRunProcessWarning(resource.getBillingRunProcessWarning());

         if (resource.getNbPartitionsToKeep() != null) {
        	 financeSettings.setNbPartitionsToKeep(resource.getNbPartitionsToKeep());
         }
         
         if (resource.getSynchronousMassActionLimit() != null) {
        	 financeSettings.setSynchronousMassActionLimit(resource.getSynchronousMassActionLimit());
         } else if (financeSettings.getId() == null) {
        	 financeSettings.setSynchronousMassActionLimit(10000);
         }
                  
         financeSettings.setWoPartitionPeriod(resource.getWoPartitionPeriod());
         if(financeSettings.getWoPartitionPeriod() != null && financeSettings.getWoPartitionPeriod() == 0) {
             financeSettings.setWoPartitionPeriod(null);
         }
         
         financeSettings.setRtPartitionPeriod(resource.getRtPartitionPeriod());
         if(financeSettings.getRtPartitionPeriod() != null && financeSettings.getRtPartitionPeriod() == 0) {
             financeSettings.setRtPartitionPeriod(null);
         }
         
         financeSettings.setEdrPartitionPeriod(resource.getEdrPartitionPeriod());
         if(financeSettings.getEdrPartitionPeriod() != null && financeSettings.getEdrPartitionPeriod() == 0) {
             financeSettings.setEdrPartitionPeriod(null);
         }

         financeSettings.setHandleAccountingPeriods(resource.getHandleAccountingPeriods());
         financeSettings.setHandleFrameworkAgreement(resource.getHandleFrameworkAgreement());
         financeSettings.setHandleInvoicingPlans(resource.getHandleInvoicingPlans());
         
         return financeSettings;
    }

    private org.meveo.apiv2.securityDeposit.HugeEntity toHugeEntityResource(HugeEntity entity) {
        return ImmutableHugeEntity.builder()
                                  .entityClass(entity.getEntityClass())
                                  .hugeLists(entity.getHugeLists())
                                  .mandatoryFilterFields(entity.getMandatoryFilterFields())
                                  .build();
    }

    private HugeEntity toHugeEntity(org.meveo.apiv2.securityDeposit.HugeEntity resource) {
        return new HugeEntity().setEntityClass(resource.getEntityClass())
                               .setHugeLists(resource.getHugeLists())
                               .setMandatoryFilterFields(resource.getMandatoryFilterFields());
    }
}