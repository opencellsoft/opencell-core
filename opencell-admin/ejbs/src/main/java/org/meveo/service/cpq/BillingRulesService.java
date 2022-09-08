package org.meveo.service.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.service.base.PersistenceService;

@Stateless
public class BillingRulesService extends PersistenceService<BillingRule>  {   
    
    public List<BillingRule> findAllBillingRulesByBillingAccount(BillingAccount billingAccount) {
        try {
            if(billingAccount.getCustomerAccount().getCustomer().getSeller() != null) {
                return getEntityManager().createNamedQuery("BillingRule.findByAccounts")
                        .setParameter("billingAccountId", billingAccount.getId())
                        .setParameter("customerAccountId", billingAccount.getCustomerAccount().getId())
                        .setParameter("customerId", billingAccount.getCustomerAccount().getCustomer().getId())
                        .setParameter("sellerId", billingAccount.getCustomerAccount().getCustomer().getSeller().getId())
                        .getResultList();
            }
            else {
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
            if(billingAccount.getCustomerAccount().getCustomer().getSeller() != null) {
                return getEntityManager().createNamedQuery("BillingRule.findByAccountsAndContract")
                        .setParameter("contractId", contract.getId())
                        .setParameter("billingAccountId", billingAccount.getId())
                        .setParameter("customerAccountId", billingAccount.getCustomerAccount().getId())
                        .setParameter("customerId", billingAccount.getCustomerAccount().getCustomer().getId())
                        .setParameter("sellerId", billingAccount.getCustomerAccount().getCustomer().getSeller().getId())
                        .getResultList();
            }
            else {
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