package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class BillingRuleService extends PersistenceService<BillingRule> {

    public static final String BILLING_ACCOUNT_ID = "billingAccountId";
    public static final String CUSTOMER_ACCOUNT_ID = "customerAccountId";
    public static final String CUSTOMER_ID = "customerId";
    @Inject
    private FinanceSettingsService financeSettingsService;

    public void checkBillingRedirectionRulesConfiguration(ContractDto contractDto) {
        if (contractDto.getBillingRules() != null && !contractDto.getBillingRules().isEmpty() && !isBillingRedirectionRulesEnabled()) {
            throw new ActionForbiddenException("Feature disabled in application settings");
        }
    }

    public boolean isBillingRedirectionRulesEnabled() {
        return financeSettingsService.isBillingRedirectionRulesEnabled();
    }

    public List<BillingRule> findAllBillingRulesByBillingAccount(BillingAccount billingAccount) {
        try {
            if (billingAccount.getCustomerAccount().getCustomer().getSeller() != null) {
                return getEntityManager().createNamedQuery("BillingRule.findByAccounts")
                        .setParameter(BILLING_ACCOUNT_ID, billingAccount.getId())
                        .setParameter(CUSTOMER_ACCOUNT_ID, billingAccount.getCustomerAccount().getId())
                        .setParameter(CUSTOMER_ID, billingAccount.getCustomerAccount().getCustomer().getId())
                        .setParameter("sellerId", billingAccount.getCustomerAccount().getCustomer().getSeller().getId())
                        .getResultList();
            } else {
                return getEntityManager().createNamedQuery("BillingRule.findByAccountsWithSellerNull")
                        .setParameter(BILLING_ACCOUNT_ID, billingAccount.getId())
                        .setParameter(CUSTOMER_ACCOUNT_ID, billingAccount.getCustomerAccount().getId())
                        .setParameter(CUSTOMER_ID, billingAccount.getCustomerAccount().getCustomer().getId())
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
                        .setParameter(BILLING_ACCOUNT_ID, billingAccount.getId())
                        .setParameter(CUSTOMER_ACCOUNT_ID, billingAccount.getCustomerAccount().getId())
                        .setParameter(CUSTOMER_ID, billingAccount.getCustomerAccount().getCustomer().getId())
                        .setParameter("sellerId", billingAccount.getCustomerAccount().getCustomer().getSeller().getId())
                        .getResultList();
            } else {
                return getEntityManager().createNamedQuery("BillingRule.findByAccountsAndContractWithSellerNull")
                        .setParameter("contractId", contract.getId())
                        .setParameter(BILLING_ACCOUNT_ID, billingAccount.getId())
                        .setParameter(CUSTOMER_ACCOUNT_ID, billingAccount.getCustomerAccount().getId())
                        .setParameter(CUSTOMER_ID, billingAccount.getCustomerAccount().getCustomer().getId())
                        .getResultList();
            }
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }


}
