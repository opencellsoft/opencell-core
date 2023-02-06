package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

@Stateless
public class BillingRuleService extends PersistenceService<BillingRule> {

    @Inject
    private FinanceSettingsService financeSettingsService;
    public void checkBillingRedirectionRulesConfiguration(ContractDto contractDto) {
        if (contractDto.getBillingRules() != null && !isBillingRedirectionRulesEnabled()) {
            throw new BusinessException("Feature disabled in application settings");
        }
    }
    public boolean isBillingRedirectionRulesEnabled() {
        return financeSettingsService.isBillingRedirectionRulesEnabled();
    }


}
