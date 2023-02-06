package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import java.util.ArrayList;
import java.util.List;

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

    public List<BillingRule> findAllBillingRulesByBillingAccount(BillingAccount billingAccount) {
        try {
            if (billingAccount.getCustomerAccount().getCustomer().getSeller() != null) {
                return getEntityManager().createNamedQuery("BillingRule.findByAccounts")
                        .setParameter("billingAccountId", billingAccount.getId())
                        .setParameter("customerAccountId", billingAccount.getCustomerAccount().getId())
                        .setParameter("customerId", billingAccount.getCustomerAccount().getCustomer().getId())
                        .setParameter("sellerId", billingAccount.getCustomerAccount().getCustomer().getSeller().getId())
                        .getResultList();
            } else {
                return getEntityManager().createNamedQuery("BillingRule.findByAccountsWithSellerNull")
                        .setParameter("billingAccountId", billingAccount.getId())
                        .setParameter("customerAccountId", billingAccount.getCustomerAccount().getId())
                        .setParameter("customerId", billingAccount.getCustomerAccount().getCustomer().getId())
                        .getResultList();
            }
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List<BillingRule> findAllBillingRulesByBillingAccountAndContract(BillingAccount billingAccount, Contract contract) {
        try {
            if (billingAccount.getCustomerAccount().getCustomer().getSeller() != null) {
                return getEntityManager().createNamedQuery("BillingRule.findByAccountsAndContract")
                        .setParameter("contractId", contract.getId())
                        .setParameter("billingAccountId", billingAccount.getId())
                        .setParameter("customerAccountId", billingAccount.getCustomerAccount().getId())
                        .setParameter("customerId", billingAccount.getCustomerAccount().getCustomer().getId())
                        .setParameter("sellerId", billingAccount.getCustomerAccount().getCustomer().getSeller().getId())
                        .getResultList();
            } else {
                return getEntityManager().createNamedQuery("BillingRule.findByAccountsAndContractWithSellerNull")
                        .setParameter("contractId", contract.getId())
                        .setParameter("billingAccountId", billingAccount.getId())
                        .setParameter("customerAccountId", billingAccount.getCustomerAccount().getId())
                        .setParameter("customerId", billingAccount.getCustomerAccount().getCustomer().getId())
                        .getResultList();
            }
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }


}
