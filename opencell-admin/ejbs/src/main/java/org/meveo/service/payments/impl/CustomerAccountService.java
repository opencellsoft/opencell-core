/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.AccountService;

/**
 * Customer Account service implementation.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
@Stateless
public class CustomerAccountService extends AccountService<CustomerAccount> {

    /** The credit category service. */
    @Inject
    private CreditCategoryService creditCategoryService;

    /** The other credit and charge service. */
    @Inject
    private OtherCreditAndChargeService otherCreditAndChargeService;

    /** The recource messages. */
    @Inject
    private ResourceBundle recourceMessages;

    /** The payment method service. */
    @Inject
    private PaymentMethodService paymentMethodService;

    /**
     * Checks if is customer account with id exists.
     *
     * @param id id of customer to be checking
     * @return true if customer is found.
     */
    public boolean isCustomerAccountWithIdExists(Long id) {
        Query query = getEntityManager().createQuery("select count(*) from CustomerAccount a where a.id = :id");
        query.setParameter("id", id);
        Long count = (Long) query.getSingleResult();
        if (count == null) {
            return false;
        }
        return count.longValue() > 0;
    }

    /**
     * Gets the all billing keywords.
     *
     * @return the all billing keywords
     */
    @SuppressWarnings("unchecked")
    public List<String> getAllBillingKeywords() {
        Query query = getEntityManager().createQuery("select distinct(billingKeyword) from CustomerAccount");
        return query.getResultList();
    }

    /**
     * Import customer accounts.
     *
     * @param customerAccountsToImport the customer accounts to import
     * @return the list
     */
    public List<CustomerAccount> importCustomerAccounts(List<CustomerAccount> customerAccountsToImport) {
        List<CustomerAccount> failedImports = new ArrayList<CustomerAccount>();
        return failedImports;
    }
    
    /**
     * Compute occ amount.
     *
     * @param customerAccount the customer account
     * @param operationCategoryEnum the operation category enum
     * @param isDue the is due
     * @param to the to
     * @param status the status
     * @return the big decimal
     * @throws Exception the exception
     */
    private BigDecimal computeOccAmount(CustomerAccount customerAccount, OperationCategoryEnum operationCategoryEnum, boolean isDue, Date to, MatchingStatusEnum... status) throws Exception {
    	return computeOccAmount(customerAccount, operationCategoryEnum, false, isDue, to, status);
    }

    /**
     * Compute occ amount.
     *
     * @param customerAccount the customer account
     * @param operationCategoryEnum the operation category enum
     * @param isFuture the is future
     * @param isDue the is due
     * @param to the to
     * @param status the status
     * @return the big decimal
     * @throws Exception the exception
     */
    private BigDecimal computeOccAmount(CustomerAccount customerAccount, OperationCategoryEnum operationCategoryEnum, boolean isFuture, boolean isDue, Date to, MatchingStatusEnum... status) throws Exception {
        BigDecimal balance = null;
        QueryBuilder queryBuilder = new QueryBuilder("select sum(unMatchingAmount) from AccountOperation");
        queryBuilder.addCriterionEnum("transactionCategory", operationCategoryEnum);
        
		if (!isFuture) {
			if (isDue) {
				queryBuilder.addCriterion("dueDate", "<=", to, false);
				
			} else {
				queryBuilder.addCriterion("transactionDate", "<=", to, false);
			}
		}
		
        queryBuilder.addCriterionEntity("customerAccount", customerAccount);
        if (status.length == 1) {
            queryBuilder.addCriterionEnum("matchingStatus", status[0]);
        } else {
            queryBuilder.startOrClause();
            for (MatchingStatusEnum st : status) {
                queryBuilder.addCriterionEnum("matchingStatus", st);
            }
            queryBuilder.endOrClause();
        }
        log.debug("query={}", queryBuilder.getSqlString());
        Query query = queryBuilder.getQuery(getEntityManager());
        balance = (BigDecimal) query.getSingleResult();
        return balance;
    }
    
    /**
     * Compute balance.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @param isDue the is due
     * @param status the status
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    private BigDecimal computeBalance(CustomerAccount customerAccount, Date to, boolean isDue, MatchingStatusEnum... status) throws BusinessException {
    	return computeBalance(customerAccount, to, false, isDue, status);
    }

    /**
     * Computes a balance given a customerAccount. 
     * to and isDue parameters are ignored when isFuture is true.
     * 
     * @param customerAccount account of the customer
     * @param to compare the invoice due or transaction date here
     * @param isFuture includes the future due or transaction date
     * @param isDue if true filter via dueDate else transactionDate
     * @param status can be a list of MatchingStatusEnum
     * @return the computed balance
     * @throws BusinessException when an error in computation is encoutered
     */
    private BigDecimal computeBalance(CustomerAccount customerAccount, Date to, boolean isFuture, boolean isDue, MatchingStatusEnum... status) throws BusinessException {
        log.trace("start computeBalance customerAccount:{}, toDate:{}, isDue:{}", (customerAccount == null ? "null" : customerAccount.getCode()), to, isDue);
        if (customerAccount == null) {
            log.warn("Error when customerAccount is null!");
            throw new BusinessException("customerAccount is null");
        }
        if (!isFuture && to == null) {
            log.warn("Error when toDate is null!");
            throw new BusinessException("toDate is null");
        }
        BigDecimal balance = null, balanceDebit = null, balanceCredit = null;
        try {
            balanceDebit = computeOccAmount(customerAccount, OperationCategoryEnum.DEBIT, isFuture, isDue, to, status);
            balanceCredit = computeOccAmount(customerAccount, OperationCategoryEnum.CREDIT, isFuture, isDue, to, status);
            if (balanceDebit == null) {
                balanceDebit = BigDecimal.ZERO;
            }
            if (balanceCredit == null) {
                balanceCredit = BigDecimal.ZERO;
            }
            balance = balanceDebit.subtract(balanceCredit);
            ParamBean param = paramBeanFactory.getInstance();
            int balanceFlag = Integer.parseInt(param.getProperty("balance.multiplier", "1"));
            balance = balance.multiply(new BigDecimal(balanceFlag));
            log.debug("end computeBalance customerAccount code:{} , balance:{}", customerAccount.getCode(), balance);
        } catch (Exception e) {
            throw new BusinessException("Internal error",e);
        }
        return balance;

    }

    /**
     * Customer account balance due.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceDue(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceDue  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, true, MatchingStatusEnum.O, MatchingStatusEnum.P, MatchingStatusEnum.I);
    }

    /**
     * Customer account balance due.
     *
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param to until date
     * @return customer account balance due
     * @throws BusinessException business exception.
     */
    public BigDecimal customerAccountBalanceDue(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException {
        log.info("start customerAccountBalanceDue with id:" + customerAccountId + ",code:" + customerAccountCode + ",toDate:" + to);
        return customerAccountBalanceDue(findCustomerAccount(customerAccountId, customerAccountCode), to);
    }

    /**
     * Customer account balance due without litigation.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceDueWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceDueWithoutLitigation  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, true, MatchingStatusEnum.O, MatchingStatusEnum.P);
    }
    
    /**
     * Customer account balance current without litigation.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceCurrentWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceDueWithoutLitigation  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, false, MatchingStatusEnum.O, MatchingStatusEnum.P);
    }

    /**
     * Customer account balance due without litigation.
     *
     * @param customerAccountId the customer account id
     * @param customerAccountCode the customer account code
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceDueWithoutLitigation(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException {
        log.info("customerAccountBalanceDueWithoutLitigation with id" + customerAccountId + ",code:" + customerAccountCode + ",toDate:" + to);
        return customerAccountBalanceDueWithoutLitigation(findCustomerAccount(customerAccountId, customerAccountCode), to);
    }

    /**
     * Customer account balance.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalance(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceDue  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, false, MatchingStatusEnum.O, MatchingStatusEnum.P);
    }

    /**
     * Customer account balance exigible.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceExigible(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceExigible  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, true, MatchingStatusEnum.O, MatchingStatusEnum.P, MatchingStatusEnum.I);

    }
    
    /**
     * Customer account balance currentexigible.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceCurrentExigible(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceExigible  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, false, MatchingStatusEnum.O, MatchingStatusEnum.P, MatchingStatusEnum.I);

    }


    /**
     * Customer account balance exigible without litigation.
     *
     * @param customerAccountId the customer account id
     * @param customerAccountCode the customer account code
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceExigibleWithoutLitigation(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException {
        log.info("customerAccountBalanceExigibleWithoutLitigation with id:{},code:{},toDate:{}", customerAccountId, customerAccountCode, to);
        return customerAccountBalanceExigibleWithoutLitigation(findCustomerAccount(customerAccountId, customerAccountCode), to);
    }

    /**
     * Customer account balance exigible without litigation.
     *
     * @param customerAccount the customer account
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal customerAccountBalanceExigibleWithoutLitigation(CustomerAccount customerAccount, Date to) throws BusinessException {
        log.info("customerAccountBalanceExigibleWithoutLitigation  customerAccount:" + (customerAccount == null ? "null" : customerAccount.getCode()) + " toDate:" + to);
        return computeBalance(customerAccount, to, true, MatchingStatusEnum.O, MatchingStatusEnum.P);
    }

    /**
     * Customer account balance exigible.
     *
     * @param customerAccountId customer account id
     * @param customerAccountCode customer account code
     * @param to until date
     * @return customer account balance exigible
     * @throws BusinessException business exception.
     */
    public BigDecimal customerAccountBalanceExigible(Long customerAccountId, String customerAccountCode, Date to) throws BusinessException {
        log.info("customerAccountBalanceExligible with id:" + customerAccountId + ",code:" + customerAccountCode + ",toDate:" + to);
        return customerAccountBalanceExigible(findCustomerAccount(customerAccountId, customerAccountCode), to);
    }

    /**
     * Close customer account.
     *
     * @param customerAccount the customer account
     * @throws BusinessException the business exception
     */
    @MeveoAudit
    public void closeCustomerAccount(CustomerAccount customerAccount) throws BusinessException {
        log.info("closeCustomerAccount customerAccount {}", (customerAccount == null ? "null" : customerAccount.getCode()));

        if (customerAccount == null) {
            log.warn("closeCustomerAccount customerAccount is null");
            throw new BusinessException("customerAccount is null");
        }
        if (customerAccount.getStatus() == CustomerAccountStatusEnum.CLOSE) {
            log.warn("closeCustomerAccount customerAccount already closed");
            throw new BusinessException("customerAccount already closed");
        }
        try {
            log.debug("closeCustomerAccount  update customerAccount ok");
            ParamBean param = paramBeanFactory.getInstance();
            String codeOCCTemplate = param.getProperty("occ.codeOccCloseAccount", "CLOSE_ACC");
            BigDecimal balanceDue = customerAccountBalanceDue(customerAccount, new Date());
            if (balanceDue == null) {
                log.warn("closeCustomerAccount balanceDue is null");
                throw new BusinessException("balanceDue is null");
            }
            log.debug("closeCustomerAccount  balanceDue:" + balanceDue);
            if (balanceDue.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(recourceMessages.getString("closeCustomerAccount.balanceDueNegatif"));
            }
            if (balanceDue.compareTo(BigDecimal.ZERO) > 0) {
                otherCreditAndChargeService.addOCC(codeOCCTemplate, null, customerAccount, balanceDue, new Date());
                log.debug("closeCustomerAccount  add occ ok");
            }
            customerAccount.setStatus(CustomerAccountStatusEnum.CLOSE);
            customerAccount.setDateStatus(new Date());
            update(customerAccount);
            log.info("closeCustomerAccount customerAccountCode:" + customerAccount.getCode() + " closed successfully");
        } catch (BusinessException be) {
            throw be;
        }
    }

    /**
     * Close customer account.
     *
     * @param customerAccountId the customer account id
     * @param customerAccountCode the customer account code
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    public void closeCustomerAccount(Long customerAccountId, String customerAccountCode) throws BusinessException, Exception {
        log.info("closeCustomerAccount customerAccountCode {}, customerAccountID {}", customerAccountCode, customerAccountId);
        closeCustomerAccount(findCustomerAccount(customerAccountId, customerAccountCode));
    }

    /**
     * Transfer account.
     *
     * @param fromCustomerAccount the from customer account
     * @param toCustomerAccount the to customer account
     * @param amount the amount
     * @throws BusinessException the business exception
     * @throws Exception the exception
     */
    @MeveoAudit
    public void transferAccount(CustomerAccount fromCustomerAccount, CustomerAccount toCustomerAccount, BigDecimal amount) throws BusinessException, Exception {
        log.info("transfertAccount fromCustomerAccount {} toCustomerAccount {} amount {}", (fromCustomerAccount == null ? "null" : fromCustomerAccount.getCode()),
            (toCustomerAccount == null ? "null" : toCustomerAccount.getCode()), amount);

        if (fromCustomerAccount == null) {
            log.warn("transfertAccount fromCustomerAccount is null");
            throw new BusinessException("fromCustomerAccount is null");
        }
        if (toCustomerAccount == null) {
            log.warn("transfertAccount toCustomerAccount is null");
            throw new BusinessException("toCustomerAccount is null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Error in transfertAccount amount is null");
            throw new BusinessException("amount is null");
        }
        try {
            ParamBean paramBean = paramBeanFactory.getInstance();
            String occTransferAccountCredit = paramBean.getProperty("occ.templateTransferAccountCredit", null);
            String occTransferAccountDebit = paramBean.getProperty("occ.templateTransferAccountDebit", null);
            String descTransfertFrom = paramBean.getProperty("occ.descTransfertFrom", "transfer from");
            String descTransfertTo = paramBean.getProperty("occ.descTransfertTo", "transfer to");

            otherCreditAndChargeService.addOCC(occTransferAccountDebit, descTransfertFrom + " " + toCustomerAccount.getCode(), fromCustomerAccount, amount, new Date());
            otherCreditAndChargeService.addOCC(occTransferAccountCredit, descTransfertTo + " " + fromCustomerAccount.getCode(), toCustomerAccount, amount, new Date());
            log.info("Successful transfertAccount fromCustomerAccountCode:" + fromCustomerAccount.getCode() + " toCustomerAccountCode:" + toCustomerAccount.getCode());

        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Transfer amount from a customer account to an other.
     * 
     * @param fromCustomerAccountId customer account id
     * @param fromCustomerAccountCode customer account code
     * @param toCustomerAccountId customer account of transfer's destination
     * @param toCustomerAccountCode customer account code of transfer's destination
     * @param amount transfer's amount
     * @throws BusinessException business exception
     * @throws Exception general exception.
     */
    public void transferAccount(Long fromCustomerAccountId, String fromCustomerAccountCode, Long toCustomerAccountId, String toCustomerAccountCode, BigDecimal amount)
            throws BusinessException, Exception {
        log.info("transfertAccount fromCustomerAccountCode {} fromCustomerAccountId {} toCustomerAccountCode {} toCustomerAccountId {}, amount {}", fromCustomerAccountCode,
            fromCustomerAccountId, toCustomerAccountCode, +toCustomerAccountId, amount);
        transferAccount(findCustomerAccount(fromCustomerAccountId, fromCustomerAccountCode), findCustomerAccount(toCustomerAccountId, toCustomerAccountCode), amount);
    }

    /**
     * Consult customer account.
     *
     * @param id the id
     * @param code the code
     * @return the customer account
     * @throws BusinessException the business exception
     */
    public CustomerAccount consultCustomerAccount(Long id, String code) throws BusinessException {
        return findCustomerAccount(id, code);
    }

    /**
     * Update Credit Category for a customer account.
     * @param id id of customer account
     * @param code code of customer account
     * @param creditCategory credit category
     * @throws BusinessException business exception.
     */
    public void updateCreditCategory(Long id, String code, String creditCategory) throws BusinessException {
        log.info("start updateCreditCategory with id:" + id + ",code:" + code);
        if (creditCategory == null) {
            log.warn("Error when required creditCategory is null!");
            throw new BusinessException("Error when required creditCategory is null");
        }
        CustomerAccount customerAccount = findCustomerAccount(id, code);
        if (!StringUtils.isBlank(creditCategory)) {
            customerAccount.setCreditCategory(creditCategoryService.findByCode(creditCategory));
        }

        update(customerAccount);
        log.info("successfully end updateCreditCategory!");
    }

    /**
     * update dunningLevel for one existed customer account by id or code.
     *
     * @param id id of customer account
     * @param code code of customer account
     * @param dunningLevel dunning level
     * @throws BusinessException business exception.
     */
    @MeveoAudit
    public void updateDunningLevel(Long id, String code, DunningLevelEnum dunningLevel) throws BusinessException {
        log.info("start updateDunningLevel with id:" + id + ",code:" + code);
        if (dunningLevel == null) {
            log.warn("Error when required dunningLevel is null!");
            throw new BusinessException("Error when required dunningLevel is null");
        }
        CustomerAccount customerAccount = findCustomerAccount(id, code);
        customerAccount.setDunningLevel(dunningLevel);
        customerAccount.setDateDunningLevel(new Date());
        update(customerAccount);
        log.info("successfully end updateDunningLevel!");
    }

    /**
     * get operations from one existed customerAccount by id or code.
     * 
     * @param id customer account
     * @param code customer account code
     * @param from date from
     * @param to until date
     * @return list of account operation.
     * @throws BusinessException business exception.
     */
    public List<AccountOperation> consultOperations(Long id, String code, Date from, Date to) throws BusinessException {
        log.info("start consultOperations with id:" + id + ",code:" + code + "from:" + from + ",to:" + to);
        CustomerAccount customerAccount = findCustomerAccount(id, code);
        List<AccountOperation> operations = customerAccount.getAccountOperations();
        log.info("found accountOperation size:" + (operations != null ? operations.size() : 0) + " from customerAccount code:" + code + ",id:" + id);
        if (to == null) {
            to = new Date();
        }
        if (operations != null) {
            Iterator<AccountOperation> it = operations.iterator();
            while (it.hasNext()) {
                Date transactionDate = it.next().getTransactionDate();
                if (transactionDate == null) {
                    continue;
                }
                if (from == null) {
                    if (transactionDate.after(to)) {
                        it.remove();
                    }
                } else if (transactionDate.before(from) || transactionDate.after(to)) {
                    it.remove();
                }
            }
        }
        log.info("found effective operations size:" + (operations != null ? operations.size() : 0) + " from customerAccount code:" + code + ",id:" + id);
        log.info("successfully end consultOperations");
        return operations;
    }

    /**
     * Find customer account.
     *
     * @param id the id
     * @param code the code
     * @return the customer account
     * @throws BusinessException the business exception
     */
    public CustomerAccount findCustomerAccount(Long id, String code) throws BusinessException {

        log.info("findCustomerAccount with code:" + code + ",id:" + id);

        if ((code == null || code.equals("")) && (id == null || id == 0)) {
            log.warn("Error: require code and id are null!");
            throw new BusinessException("Error: required code and ID are null!");
        }

        CustomerAccount customerAccount = null;
        try {
            customerAccount = (CustomerAccount) getEntityManager().createQuery("from CustomerAccount where id=:id or code=:code").setParameter("id", id).setParameter("code", code)
                .getSingleResult();
        } catch (Exception e) {
            log.warn("failed to fin customer account ", e);
        }

        if (customerAccount == null) {
            log.warn("Error when find nonexisted customer account ");
            throw new BusinessException("Error when find nonexisted customer account code:" + code + " , id:" + id);
        }

        return customerAccount;
    }

    /**
     * Checks if is all service instances terminated.
     *
     * @param customerAccount the customer account
     * @return true, if is all service instances terminated
     */
    public boolean isAllServiceInstancesTerminated(CustomerAccount customerAccount) {
        // FIXME : just count inside the query
        Query billingQuery = getEntityManager().createQuery(
            "select si from ServiceInstance si join si.subscription s join s.userAccount ua join ua.billingAccount ba join ba.customerAccount ca where ca.id = :customerAccountId");
        billingQuery.setParameter("customerAccountId", customerAccount.getId());
        @SuppressWarnings("unchecked")
        List<ServiceInstance> services = (List<ServiceInstance>) billingQuery.getResultList();
        for (ServiceInstance service : services) {
            boolean serviceActive = service.getStatus() == InstanceStatusEnum.ACTIVE;
            if (serviceActive) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the customer accounts.
     *
     * @param creditCategory the credit category
     * @param paymentMethod the payment method
     * @return the customer accounts
     */
    @SuppressWarnings("unchecked")
    public List<CustomerAccount> getCustomerAccounts(String creditCategory, PaymentMethodEnum paymentMethod) {
        List<CustomerAccount> customerAccounts = getEntityManager()
            .createQuery("from " + CustomerAccount.class.getSimpleName() + " where paymentMethod=:paymentMethod and creditCategory.code=:creditCategoryCode and status=:status ")
            .setParameter("paymentMethod", paymentMethod).setParameter("creditCategoryCode", creditCategory).setParameter("status", CustomerAccountStatusEnum.ACTIVE)
            .getResultList();
        return customerAccounts;
    }

    /**
     * List by customer.
     *
     * @param customer the customer
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<CustomerAccount> listByCustomer(Customer customer) {
        QueryBuilder qb = new QueryBuilder(CustomerAccount.class, "c");
        qb.addCriterionEntity("customer", customer);

        try {
            return (List<CustomerAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("failed to get customerAccount list by customer", e);
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.meveo.service.base.PersistenceService#create(org.meveo.model.IEntity)
     */
    @Override
    public void create(CustomerAccount entity) throws BusinessException {

        if (entity.getPreferredPaymentMethod() == null) {
            throw new BusinessException("CustomerAccount does not have a preferred payment method");
        }
        for (PaymentMethod pm : entity.getPaymentMethods()) {
            pm.updateAudit(currentUser);
        }
        // Register card payment methods in payment gateway and obtain a token id
        for (CardPaymentMethod cardPaymentMethod : entity.getCardPaymentMethods(true)) {
            paymentMethodService.obtainAndSetCardToken(cardPaymentMethod, cardPaymentMethod.getCustomerAccount());
        }

        entity.ensureOnePreferredPaymentMethod();
        super.create(entity);
    }

    /* (non-Javadoc)
     * @see org.meveo.service.base.PersistenceService#update(org.meveo.model.IEntity)
     */
    @Override
    public CustomerAccount update(CustomerAccount entity) throws BusinessException {

        if (entity.getPreferredPaymentMethod() == null) {
            throw new BusinessException("CustomerAccount does not have a preferred payment method");
        }
        for (PaymentMethod pm : entity.getPaymentMethods()) {
            pm.updateAudit(currentUser);
        }
        // Register card payment methods in payment gateway and obtain a token id
        for (CardPaymentMethod cardPaymentMethod : entity.getCardPaymentMethods(true)) {
            paymentMethodService.obtainAndSetCardToken(cardPaymentMethod, cardPaymentMethod.getCustomerAccount());
        }

        entity.ensureOnePreferredPaymentMethod();
        return super.update(entity);
    }

    /**
     * Gets the preferred payment method.
     *
     * @param customerAccountId the customer account id
     * @return the preferred payment method
     */
    public PaymentMethod getPreferredPaymentMethod(Long customerAccountId) {
        try {
            TypedQuery<PaymentMethod> query = this.getEntityManager().createNamedQuery("PaymentMethod.getPreferredPaymentMethodForCA", PaymentMethod.class).setMaxResults(1)
                .setParameter("caId", customerAccountId);

            PaymentMethod paymentMethod = query.getSingleResult();
            return paymentMethod;

        } catch (NoResultException e) {
            log.warn("Customer account {} has no preferred payment method", customerAccountId, e);
            return null;
        }
    }

    /**
     * Gets the payment methods.
     *
     * @param billingAccount the billing account
     * @return the payment methods
     */
    @SuppressWarnings("unchecked")
    public List<PaymentMethod> getPaymentMethods(BillingAccount billingAccount) {

        Query query = this.getEntityManager()
            .createQuery("select m from PaymentMethod m where m.customerAccount.id in (select b.customerAccount.id from BillingAccount b where b.id=:id)", PaymentMethod.class);
        query.setParameter("id", billingAccount.getId());
        try {
            List<PaymentMethod> resultList = (List<PaymentMethod>) (query.getResultList());
            return resultList;

        } catch (NoResultException e) {
            log.warn("error while getting user account list by billing account", e);
            return null;
        }

    }
    
    /**
     *  Compute  credit balnce.
     *  
     * @param customerAccount the customer account 
     * @param isDue if true will compare with the due date else operationn date
     * @param to include AOs until this date
     * @param dunningExclusion if true the litigation AOs will not be included
     * @return Calculated balnce
     * @throws BusinessException Business Exception
     */
    public BigDecimal computeCreditBalance(CustomerAccount customerAccount, boolean isDue, Date to, boolean dunningExclusion) throws BusinessException {
        BigDecimal result = new BigDecimal(0);
        try {
            if (dunningExclusion) {
                result = computeOccAmount(customerAccount, OperationCategoryEnum.CREDIT, isDue, to, MatchingStatusEnum.O, MatchingStatusEnum.P);
            } else {
                result = computeOccAmount(customerAccount, OperationCategoryEnum.CREDIT, isDue, to, MatchingStatusEnum.O, MatchingStatusEnum.P, MatchingStatusEnum.I);
            }

            result = result == null ? new BigDecimal(0) : result;
            ParamBean param = paramBeanFactory.getInstance();
            int balanceFlag = Integer.parseInt(param.getProperty("balance.multiplier", "1"));
            balanceFlag = Math.negateExact(balanceFlag);
            result = result.multiply(new BigDecimal(balanceFlag));

        } catch (Exception e) {
            log.error("Error on computeCreditBalance:", e);
            throw new BusinessException(e.getMessage());
        }

        return result;
    }

    /**
     * Computes the future dueBalance or the dueBalance at the invoice due date.
     * The total due is a snapshot at invoice generation time of the due balance (not exigible) before invoice calculation+invoice amount. 
     * 
     * @param customerAccount Account of the Customer
     * @return computed due balance of a customer account
     * @throws BusinessException when an error occurred in computation
     */
	public BigDecimal customerAccountFutureBalanceExigibleWithoutLitigation(CustomerAccount customerAccount) throws BusinessException {
		return computeBalance(customerAccount, null, true, true, MatchingStatusEnum.O, MatchingStatusEnum.P);
	}

    
    /**
     * Return list customerAccount ids for payment.
     *
     * @param paymentMethodEnum payment method.
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @return list of customerAccount ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> getCAidsForPayment(PaymentMethodEnum paymentMethodEnum,Date fromDueDate,Date toDueDate) {
        try {
            return (List<Long>) getEntityManager().createNamedQuery("CustomerAccount.listCAIdsForPayment").setParameter("paymentMethodIN", paymentMethodEnum)
                    .setParameter("fromDueDateIN", fromDueDate).setParameter("toDueDateIN", toDueDate).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    /**
     * Return list customerAccount ids for refund.
     *
     * @param paymentMethodEnum payment method.
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @return list of customerAccount ids.
     */
    @SuppressWarnings("unchecked")
    public List<Long> getCAidsForRefund(PaymentMethodEnum paymentMethodEnum,Date fromDueDate,Date toDueDate) {
        try {
            return (List<Long>) getEntityManager().createNamedQuery("CustomerAccount.listCAIdsForRefund").setParameter("paymentMethodIN", paymentMethodEnum)
                    .setParameter("fromDueDateIN", fromDueDate).setParameter("toDueDateIN", toDueDate).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

}