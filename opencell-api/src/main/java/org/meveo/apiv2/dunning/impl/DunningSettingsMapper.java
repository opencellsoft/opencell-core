package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.ImmutableDunningSettings;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.payments.CustomerBalance;
import org.meveo.model.dunning.DunningSettings;

public class DunningSettingsMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningSettings, DunningSettings> {

	@Override
	protected org.meveo.apiv2.dunning.DunningSettings toResource(DunningSettings entity) {
		return ImmutableDunningSettings.builder()
				.id(entity.getId())
				.accountingArticle(createResource(entity.getAccountingArticle()))
				.code(entity.getCode())
				.isAllowDunningCharges(entity.isAllowDunningCharges())
				.isAllowInterestForDelay(entity.isAllowInterestForDelay())
				.isApplyDunningChargeFxExchangeRate(entity.isApplyDunningChargeFxExchangeRate())
				.dunningMode(entity.getDunningMode())
				.interestForDelayRate(entity.getInterestForDelayRate())
				.maxDaysOutstanding(entity.getMaxDaysOutstanding())
				.maxDunningLevels(entity.getMaxDunningLevels())
				.customerBalance(createResource(entity.getCustomerBalance()))
				.build();
    }

    @Override
    protected DunningSettings toEntity(org.meveo.apiv2.dunning.DunningSettings resource) {
        var entity = new DunningSettings();
        
        if (resource.getAccountingArticle() != null) {
            var accountingArticle = new AccountingArticle();
            accountingArticle.setId(resource.getAccountingArticle().getId());
            accountingArticle.setCode(resource.getAccountingArticle().getCode());
            entity.setAccountingArticle(accountingArticle);
        }
        entity.setCode(resource.getCode());
        
        if (resource.isAllowInterestForDelay() != null) {
            entity.setAllowInterestForDelay(resource.isAllowInterestForDelay());
        }
        
        if (resource.isAllowDunningCharges() != null) {
            entity.setAllowDunningCharges(resource.isAllowDunningCharges());
        }
        
        if (resource.isApplyDunningChargeFxExchangeRate() != null) {
            entity.setApplyDunningChargeFxExchangeRate(resource.isApplyDunningChargeFxExchangeRate());
        }
        
        if(resource.getCustomerBalance() != null && resource.getCustomerBalance().getId() != null) {
        	var customerBalance = new CustomerBalance();
        	customerBalance.setId(resource.getCustomerBalance().getId());
        	entity.setCustomerBalance(customerBalance);
        }
        
        entity.setDunningMode(resource.getDunningMode());
        entity.setId(resource.getId());
        entity.setInterestForDelayRate(resource.getInterestForDelayRate());
        entity.setMaxDaysOutstanding(resource.getMaxDaysOutstanding());
        entity.setMaxDunningLevels(resource.getMaxDunningLevels());
        return entity;
    }
}
