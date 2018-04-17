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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidEntityStatusException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.TerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.AccountService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * A service class to manage CRUD operations on BillingAccount entity
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

    @Inject
    private UserAccountService userAccountService;

    @EJB
    private BillingRunService billingRunService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject 
    private InvoiceSubCategoryService invoiceSubCategoryService;    

    /**
     * Initialize if empty SubscriptionDate, NextInvoiceDate fields to a current date and status to Active before creating a Billing account
     */
    @Override
    public void create(BillingAccount billingAccount) throws BusinessException {

        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        if (billingAccount.getSubscriptionDate() == null) {
            billingAccount.setSubscriptionDate(new Date());
        }

        if (billingAccount.getNextInvoiceDate() == null) {
            billingAccount.setNextInvoiceDate(new Date());
        }

        super.create(billingAccount);
    }

    public BillingAccount updateElectronicBilling(BillingAccount billingAccount, Boolean electronicBilling) throws BusinessException {
        billingAccount.setElectronicBilling(electronicBilling);
        return update(billingAccount);
    }

    public BillingAccount updateBillingAccountDiscount(BillingAccount billingAccount, BigDecimal ratedDiscount) throws BusinessException {
        billingAccount.setDiscountRate(ratedDiscount);
        return update(billingAccount);
    }

    /**
     * Terminate Billing account. Status will be changed to Terminated. Action will also terminate related User accounts and Subscriptions.
     * 
     * @param billingAccount Billing account
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @return Updated Billing account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public BillingAccount terminateBillingAccount(BillingAccount billingAccount, Date terminationDate, TerminationReason terminationReason) throws BusinessException {

        if (billingAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            return billingAccount;
        }
        log.debug("Will terminate Billing account " + billingAccount.getCode());

        if (terminationDate == null) {
            terminationDate = new Date();
        }

        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : userAccounts) {
            userAccountService.terminateUserAccount(userAccount, terminationDate, terminationReason);
        }
        billingAccount.setTerminationReason(terminationReason);
        billingAccount.setTerminationDate(terminationDate);
        billingAccount.setStatus(AccountStatusEnum.TERMINATED);

        billingAccount = update(billingAccount);

        log.info("Billing account " + billingAccount.getCode() + " was terminated");

        return billingAccount;
    }

    /**
     * Cancel Billing account. Status will be changed to Canceled. Action will also cancel related User accounts and Subscriptions.
     * 
     * @param billingAccount Billing account
     * @param cancellationDate Cancellation date
     * @return Updated Billing account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public BillingAccount cancelBillingAccount(BillingAccount billingAccount, Date cancellationDate) throws BusinessException {

        if (billingAccount.getStatus() != AccountStatusEnum.ACTIVE) {
            return billingAccount;
        }

        log.debug("Will cancel Billing account " + billingAccount.getCode());

        if (cancellationDate == null) {
            cancellationDate = new Date();
        }
        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : userAccounts) {
            userAccountService.cancelUserAccount(userAccount, cancellationDate);
        }
        billingAccount.setTerminationDate(cancellationDate);
        billingAccount.setStatus(AccountStatusEnum.CANCELED);

        billingAccount = update(billingAccount);

        log.info("Billing account " + billingAccount.getCode() + " was canceled");

        return billingAccount;
    }

    /**
     * Activate previously canceled or terminated Billing account. Status will be changed to Active.
     * 
     * @param billingAccount Billing account
     * @param activationDate Activation date
     * @return Updated Billing account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public BillingAccount reactivateBillingAccount(BillingAccount billingAccount, Date activationDate) throws BusinessException {

        if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED && billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new InvalidEntityStatusException(BillingAccount.class, billingAccount.getCode(), "reactivate", billingAccount.getStatus(), AccountStatusEnum.TERMINATED,
                AccountStatusEnum.CANCELED);
        }

        log.debug("Will reactivate Billing account " + billingAccount.getCode());

        if (activationDate == null) {
            activationDate = new Date();
        }
        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        billingAccount.setTerminationDate(null);
        billingAccount.setTerminationReason(null);

        billingAccount = update(billingAccount);

        log.info("Billing account " + billingAccount.getCode() + " was reactivated");

        return billingAccount;
    }

    /**
     * Close previously canceled or terminated Billing account. Status will be changed to Closed.
     * 
     * @param billingAccount Billing account
     * @return Updated Billing account entity
     * @throws BusinessException Business exception
     */
    @MeveoAudit
    public BillingAccount closeBillingAccount(BillingAccount billingAccount) throws BusinessException {

        if (billingAccount.getStatus() == AccountStatusEnum.CLOSED) {
            return billingAccount;
        }

        // @Todo : ajouter la condition : l'encours de facturation est vide :

        if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED && billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new InvalidEntityStatusException(BillingAccount.class, billingAccount.getCode(), "close", billingAccount.getStatus(), AccountStatusEnum.TERMINATED,
                AccountStatusEnum.CANCELED);
        }
        log.debug("Will close Billing account " + billingAccount.getCode());

        billingAccount.setStatus(AccountStatusEnum.CLOSED);

        billingAccount = update(billingAccount);

        log.info("Billing account " + billingAccount.getCode() + " was closed");

        return billingAccount;
    }

    public List<Invoice> invoiceList(BillingAccount billingAccount) throws BusinessException {
        List<Invoice> invoices = billingAccount.getInvoices();
        Collections.sort(invoices, new Comparator<Invoice>() {
            public int compare(Invoice c0, Invoice c1) {

                return c1.getInvoiceDate().compareTo(c0.getInvoiceDate());
            }
        });
        return invoices;
    }

    @SuppressWarnings("unchecked")
    public List<BillingAccount> findBillingAccounts(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b", null);
            qb.addCriterionEntity("b.billingCycle", billingCycle);

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate);
            }

            return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    public List<Long> findBillingAccountIds(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b", null);
            qb.addCriterionEntity("b.billingCycle", billingCycle);

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate);
            }

            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean updateBillingAccountTotalAmounts(Long billingAccountId, BillingRun billingRun) throws BusinessException {
        log.debug("updateBillingAccountTotalAmounts  billingAccount:" + billingAccountId);
        BillingAccount billingAccount = findById(billingAccountId);
        BigDecimal invoiceAmount = createMinAmountsRT(billingAccount,  billingRun.getLastTransactionDate());

        if (invoiceAmount != null) {
            BillingCycle billingCycle = billingRun.getBillingCycle();
            BigDecimal invoicingThreshold = billingCycle == null ? null : billingCycle.getInvoicingThreshold();

            if (invoicingThreshold != null) {
                if (invoicingThreshold.compareTo(invoiceAmount) > 0) {
                    log.debug("updateBillingAccountTotalAmounts  invoicingThreshold( stop invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}",
                        billingAccount.getCode(), invoiceAmount, invoicingThreshold);
                    return false;
                } else {
                    log.debug("updateBillingAccountTotalAmounts  invoicingThreshold(out continue invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}",
                        billingAccount.getCode(), invoiceAmount, invoicingThreshold);
                }
            } else {
                log.debug("updateBillingAccountTotalAmounts no invoicingThreshold to apply");
            }
            billingAccount.setBrAmountWithoutTax(invoiceAmount);

            log.debug("set brAmount {} in BA {}", invoiceAmount, billingAccount.getId());
        }

        billingAccount.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));

        updateNoCheck(billingAccount);

        return true;
    }

    /**
     * Compute the invoice amount for billingAccount.
     * 
     * @param billingAccount billing account
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @return computed billing account's invoice amount.
     */
    public BigDecimal computeBaInvoiceAmount(BillingAccount billingAccount, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumBillingAccount").setParameter("billingAccount", billingAccount)
                .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        BigDecimal sumAmountWithouttax = (BigDecimal) q.getSingleResult();
        if (sumAmountWithouttax == null) {
            sumAmountWithouttax = BigDecimal.ZERO;
        }

        return sumAmountWithouttax;
    }

    @SuppressWarnings("unchecked")
    public List<BillingAccount> listByCustomerAccount(CustomerAccount customerAccount) {
        QueryBuilder qb = new QueryBuilder(BillingAccount.class, "c");
        qb.addCriterionEntity("customerAccount", customerAccount);
        qb.addOrderCriterionAsIs("c.id", true);
        try {
            return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list by customer account", e);
            return null;
        }
    }

    /**
     * Determine if billing account is exonerated from taxes - check either a flag or EL expressions in customer's customerCategory
     * 
     * @param ba The BillingAccount
     * @return True if billing account is exonerated from taxes
     */
    public boolean isExonerated(BillingAccount ba) {

        boolean isExonerated = false;
        if (ba == null) {
            return false;
        }
        CustomerCategory customerCategory = ba.getCustomerAccount().getCustomer().getCustomerCategory();
        if (customerCategory == null) {
            return false;
        }
        if (customerCategory.getExoneratedFromTaxes()) {
            return true;
        }

        if (!StringUtils.isBlank(customerCategory.getExonerationTaxEl())) {

            Map<Object, Object> userMap = new HashMap<Object, Object>();
            if (customerCategory.getExonerationTaxEl().indexOf("ba.") > -1) {
                userMap.put("ba", ba);
            }

            isExonerated =  ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(customerCategory.getExonerationTaxEl(), userMap);
        }
        return isExonerated;
    }

    /**
     * Compute the invoice amount by charge.
     * 
     * @param chargeInstance chargeInstance
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @return computed invoice amount by charge.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> computeChargeInvoiceAmount(ChargeInstance chargeInstance, Date firstTransactionDate, Date lastTransactionDate, BillingAccount billingAccount) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByCharge").setParameter("chargeInstance", chargeInstance)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("billingAccount", billingAccount);
        return (List<Object[]>) q.getResultList();
    }

    /**
     * @param expression EL expression
     * @param billingAccount billingAccount
     * @return userMap userMap
     */    
    private Map<Object, Object> constructElContext(String expression, BillingAccount ba) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", ba);
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", ba.getCustomerAccount());
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", ba.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        return userMap;
    }

    /**
     * @param expression EL expression
     * @param subscription subscription
     * @return userMap userMap
     */    
    private Map<Object, Object> constructElContext(String expression, Subscription subscription) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        UserAccount userAccount = subscription.getUserAccount();
        BillingAccount billingAccount = userAccount.getBillingAccount();

        if (expression.indexOf("offer") >= 0) {
            OfferTemplate offer = subscription.getOffer();
            userMap.put("offer", offer);
        }
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", userAccount);
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        return userMap;
    }

    /**
     * @param expression EL expression
     * @param serviceInstance serviceInstance
     * @return userMap userMap
     */    
    private Map<Object, Object> constructElContext(String expression, ServiceInstance serviceInstance) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        Subscription subscription = serviceInstance.getSubscription();
        UserAccount userAccount = subscription.getUserAccount();
        BillingAccount billingAccount = userAccount.getBillingAccount();

        if (expression.indexOf("serviceInstance") >= 0) {
            userMap.put("serviceInstance", serviceInstance);
        }
        if (expression.indexOf("offer") >= 0) {
            OfferTemplate offer = serviceInstance.getSubscription().getOffer();
            userMap.put("offer", offer);
        }
        if (expression.indexOf("ua") >= 0) {
            userMap.put("ua", userAccount);
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        return userMap;
    }

    /**
     * @param expression EL expression
     * @param ba billing account
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    public Double evaluateDoubleExpression(String expression, BillingAccount ba) throws BusinessException {
        Double result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, ba);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        try {
            result = (Double) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param serviceInstance   serviceInstance
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private Double evaluateDoubleExpression(String expression, ServiceInstance serviceInstance) throws BusinessException {
        Double result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, serviceInstance);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        try {
            result = (Double) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param subscription   subscription
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private Double evaluateDoubleExpression(String expression, Subscription subscription) throws BusinessException {
        Double result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, subscription);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Double.class);
        try {
            result = (Double) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to double but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param ba billing account
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    public String evaluateStringExpression(String expression, BillingAccount ba) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, ba);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param subscription subscription
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateStringExpression(String expression, Subscription subscription) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, subscription);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param serviceInstance serviceInstance
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateStringExpression(String expression, ServiceInstance serviceInstance) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, serviceInstance);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
        }
        return result;
    }

    /**
     * Create min amounts rated transactions
     * 
     * @param billingAccount the billing account
     * @param lastTransactionDate last transaction date
     * @return invoice amount
     */
    public BigDecimal createMinAmountsRT(BillingAccount billingAccount, Date lastTransactionDate) throws BusinessException {

        Date minRatingDate = new Date();

        Map<InvoiceSubCategory, Map<String, BigDecimal>> billingAccountAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

        for(UserAccount userAccount : billingAccount.getUsersAccounts()) {
            if(userAccount.getStatus().equals(AccountStatusEnum.ACTIVE)) {

                for(Subscription subscription : userAccount.getSubscriptions()) {
                    if(subscription.getStatus().equals(SubscriptionStatusEnum.ACTIVE)) {

                        Map<InvoiceSubCategory, Map<String, BigDecimal>> subscriptionAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

                        for(ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                            if(serviceInstance.getStatus().equals(InstanceStatusEnum.ACTIVE)) {

                                Map<InvoiceSubCategory, Map<String, BigDecimal>> serviceAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

                                List<RecurringChargeInstance> recurringChargeInstanceList = serviceInstance.getRecurringChargeInstances();
                                for(RecurringChargeInstance recurringChargeInstance : recurringChargeInstanceList) {
                                    List<Object[]> amountsList = computeChargeInvoiceAmount(recurringChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                                    for(Object[] amounts : amountsList) {
                                        BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                                        BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                                        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long)amounts[3]);

                                        if(chargeAmountWithoutTax != null) {
                                            if(serviceAmountMap.get(invoiceSubCategory) != null) {
                                                Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                                serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                                serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            } else {
                                                Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                                serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                                serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            }
                                        }
                                    }
                                }

                                List<UsageChargeInstance> usageChargeInstanceList = serviceInstance.getUsageChargeInstances();
                                for(UsageChargeInstance usageChargeInstance : usageChargeInstanceList) {
                                    List<Object[]> amountsList = computeChargeInvoiceAmount(usageChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                                    for(Object[] amounts : amountsList) {
                                        BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                                        BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                                        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long)amounts[3]);

                                        if(chargeAmountWithoutTax != null) {
                                            if(serviceAmountMap.get(invoiceSubCategory) != null) {
                                                Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                                serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                                serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            } else {
                                                Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                                serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                                serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            }
                                        }
                                    }
                                }

                                List<OneShotChargeInstance> subscriptionChargeInstanceList = serviceInstance.getSubscriptionChargeInstances();
                                for(OneShotChargeInstance subscriptionChargeInstance : subscriptionChargeInstanceList) {
                                    List<Object[]> amountsList = computeChargeInvoiceAmount(subscriptionChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                                    for(Object[] amounts : amountsList) {
                                        BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                                        BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                                        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long)amounts[3]);

                                        if(chargeAmountWithoutTax != null) {
                                            if(serviceAmountMap.get(invoiceSubCategory) != null) {
                                                Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                                serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                                serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            } else {
                                                Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                                serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                                serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            }
                                        }
                                    }
                                }

                                List<OneShotChargeInstance> terminationChargeInstanceList = serviceInstance.getTerminationChargeInstances();
                                for(OneShotChargeInstance terminationChargeInstance : terminationChargeInstanceList) {
                                    List<Object[]> amountsList = computeChargeInvoiceAmount(terminationChargeInstance, new Date(0), lastTransactionDate, billingAccount);

                                    for(Object[] amounts : amountsList) {
                                        BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                                        BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                                        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long)amounts[3]);

                                        if(chargeAmountWithoutTax != null) {
                                            if(serviceAmountMap.get(invoiceSubCategory) != null) {
                                                Map<String, BigDecimal> serviceAmount = serviceAmountMap.get(invoiceSubCategory);
                                                serviceAmount.put("serviceAmountWithoutTax", serviceAmount.get("serviceAmountWithoutTax").add(chargeAmountWithoutTax));
                                                serviceAmount.put("serviceAmountWithTax", serviceAmount.get("serviceAmountWithTax").add(chargeAmountWithTax));
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            } else {
                                                Map<String, BigDecimal> serviceAmount = new HashMap<String, BigDecimal>();
                                                serviceAmount.put("serviceAmountWithoutTax", chargeAmountWithoutTax);
                                                serviceAmount.put("serviceAmountWithTax", chargeAmountWithTax);
                                                serviceAmountMap.put(invoiceSubCategory, serviceAmount);
                                            }
                                        }
                                    }
                                }

                                BigDecimal totalServiceAmountWithoutTax = BigDecimal.ZERO;
                                BigDecimal totalServiceAmountWithTax = BigDecimal.ZERO;

                                for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : serviceAmountMap.entrySet()) {
                                    totalServiceAmountWithoutTax = totalServiceAmountWithoutTax.add(entry.getValue().get("serviceAmountWithoutTax"));
                                    totalServiceAmountWithTax = totalServiceAmountWithTax.add(entry.getValue().get("serviceAmountWithTax"));
                                }   

                                for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : serviceAmountMap.entrySet()) {

                                    BigDecimal serviceAmountWithoutTax = entry.getValue().get("serviceAmountWithoutTax");
                                    BigDecimal serviceAmountWithTax = entry.getValue().get("serviceAmountWithTax");
                                    InvoiceSubCategory invoiceSubCategory = entry.getKey();

                                    String serviceMinAmountEL = StringUtils.isBlank(serviceInstance.getMinimumAmountEl())
                                            ? serviceInstance.getServiceTemplate().getMinimumAmountEl() : serviceInstance.getMinimumAmountEl();
                                    String serviceMinLabelEL = StringUtils.isBlank(serviceInstance.getMinimumLabelEl()) ? serviceInstance.getServiceTemplate().getMinimumLabelEl()
                                            : serviceInstance.getMinimumLabelEl();
                                    if(!StringUtils.isBlank(serviceMinAmountEL)) {

                                        BigDecimal serviceMinAmount = new BigDecimal(evaluateDoubleExpression(serviceMinAmountEL, serviceInstance));
                                        String serviceMinLabel = evaluateStringExpression(serviceMinLabelEL, serviceInstance);

                                        BigDecimal ratio = BigDecimal.ZERO;
                                        BigDecimal diff = null;
                                        if(appProvider.isEntreprise()) {
                                            diff = serviceMinAmount.subtract(totalServiceAmountWithoutTax);
                                            if(totalServiceAmountWithoutTax != BigDecimal.ZERO) {
                                                ratio = serviceAmountWithoutTax.divide(totalServiceAmountWithoutTax, 2, RoundingMode.HALF_UP);  
                                            }
                                        } else {
                                            diff = serviceMinAmount.subtract(totalServiceAmountWithTax);
                                            if(totalServiceAmountWithTax != BigDecimal.ZERO) {
                                                ratio = serviceAmountWithTax.divide(totalServiceAmountWithTax, 2, RoundingMode.HALF_UP);  
                                            }
                                        }

                                        if(diff.doubleValue() > 0) {
                                            BigDecimal taxPercent = BigDecimal.ZERO;
                                            BigDecimal rtMinAmount = diff.multiply(ratio);
                                            for(InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                                                if(invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                                    taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                                    break;
                                                }
                                            }

                                            BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ? rtMinAmount
                                                    : rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                                            BigDecimal unitAmountWithTax = appProvider.isEntreprise()
                                                    ? rtMinAmount.add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) : rtMinAmount;
                                            BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                                            BigDecimal amountWithoutTax = unitAmountWithoutTax;
                                            BigDecimal amountWithTax = unitAmountWithTax;
                                            BigDecimal amountTax = unitAmountTax;

                                            RatedTransaction ratedTransaction = new RatedTransaction(null, minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax,
                                                BigDecimal.ONE, amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount,
                                                invoiceSubCategory, "", "", "", "", null, "", "", null, "NO_OFFER", null,
                                                RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(), serviceMinLabel);
                                            ratedTransactionService.create(ratedTransaction);
                                            ratedTransactionService.commit();

                                            serviceAmountWithoutTax = serviceAmountWithoutTax.add(amountWithoutTax);
                                            serviceAmountWithTax = serviceAmountWithTax.add(amountWithTax);

                                        }
                                    }

                                    if(subscriptionAmountMap.get(invoiceSubCategory) != null) {
                                        Map<String, BigDecimal> subscriptionAmount = subscriptionAmountMap.get(invoiceSubCategory);
                                        subscriptionAmount.put("subscriptionAmountWithoutTax", subscriptionAmount.get("subscriptionAmountWithoutTax").add(serviceAmountWithoutTax));
                                        subscriptionAmount.put("subscriptionAmountWithTax", subscriptionAmount.get("subscriptionAmountWithTax").add(serviceAmountWithTax));
                                        subscriptionAmountMap.put(invoiceSubCategory, subscriptionAmount);
                                    } else {
                                        Map<String, BigDecimal> subscriptionAmount = new HashMap<String, BigDecimal>();
                                        subscriptionAmount.put("subscriptionAmountWithoutTax", serviceAmountWithoutTax);
                                        subscriptionAmount.put("subscriptionAmountWithTax", serviceAmountWithTax);
                                        subscriptionAmountMap.put(invoiceSubCategory, subscriptionAmount);
                                    }

                                }
                            }
                        }

                        BigDecimal totalSubscriptionAmountWithoutTax = BigDecimal.ZERO;
                        BigDecimal totalSubscriptionAmountWithTax = BigDecimal.ZERO;

                        for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : subscriptionAmountMap.entrySet()) {
                            totalSubscriptionAmountWithoutTax = totalSubscriptionAmountWithoutTax.add(entry.getValue().get("subscriptionAmountWithoutTax"));
                            totalSubscriptionAmountWithTax = totalSubscriptionAmountWithTax.add(entry.getValue().get("subscriptionAmountWithTax"));
                        }   

                        for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : subscriptionAmountMap.entrySet()) {

                            BigDecimal subscriptionAmountWithoutTax = entry.getValue().get("subscriptionAmountWithoutTax");
                            BigDecimal subscriptionAmountWithTax = entry.getValue().get("subscriptionAmountWithTax");
                            InvoiceSubCategory invoiceSubCategory = entry.getKey();

                            String subscriptionMinAmountEL = StringUtils.isBlank(subscription.getMinimumAmountEl()) ? subscription.getOffer().getMinimumAmountEl()
                                    : subscription.getMinimumAmountEl();
                            String subscriptionMinLabelEL = StringUtils.isBlank(subscription.getMinimumLabelEl()) ? subscription.getOffer().getMinimumLabelEl()
                                    : subscription.getMinimumLabelEl();

                            if(!StringUtils.isBlank(subscriptionMinAmountEL)) {
                                BigDecimal subscriptionMinAmount = new BigDecimal(evaluateDoubleExpression(subscriptionMinAmountEL, subscription));
                                String subscriptionMinLabel = evaluateStringExpression(subscriptionMinLabelEL, subscription);

                                BigDecimal ratio = BigDecimal.ZERO;
                                BigDecimal diff = null;
                                if(appProvider.isEntreprise()) {
                                    diff = subscriptionMinAmount.subtract(totalSubscriptionAmountWithoutTax);
                                    if(totalSubscriptionAmountWithoutTax != BigDecimal.ZERO) {
                                        ratio = subscriptionAmountWithoutTax.divide(totalSubscriptionAmountWithoutTax, 2, RoundingMode.HALF_UP);  
                                    }
                                } else {
                                    diff = subscriptionMinAmount.subtract(totalSubscriptionAmountWithTax);
                                    if(totalSubscriptionAmountWithTax != BigDecimal.ZERO) {
                                        ratio = subscriptionAmountWithTax.divide(totalSubscriptionAmountWithTax, 2, RoundingMode.HALF_UP);  
                                    }
                                }

                                if(diff.doubleValue() > 0) {

                                    BigDecimal taxPercent = BigDecimal.ZERO;
                                    BigDecimal rtMinAmount = diff.multiply(ratio);
                                    for(InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                                        if(invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                            taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                            break;
                                        }
                                    }

                                    BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ? rtMinAmount
                                            : rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                                    BigDecimal unitAmountWithTax = appProvider.isEntreprise()
                                            ? rtMinAmount.add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) : rtMinAmount;
                                    BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                                    BigDecimal amountWithoutTax = unitAmountWithoutTax;
                                    BigDecimal amountWithTax = unitAmountWithTax;
                                    BigDecimal amountTax = unitAmountTax;

                                    RatedTransaction ratedTransaction = new RatedTransaction(null, minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax,
                                        BigDecimal.ONE, amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, invoiceSubCategory, "",
                                        "", "", "", null, "", "", null, "NO_OFFER", null,
                                        RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode() + "_" + subscription.getCode(), subscriptionMinLabel);
                                    ratedTransactionService.create(ratedTransaction);
                                    ratedTransactionService.commit();

                                    subscriptionAmountWithoutTax = subscriptionAmountWithoutTax.add(amountWithoutTax);
                                    subscriptionAmountWithTax = subscriptionAmountWithTax.add(amountWithTax);

                                }
                            }

                            if(billingAccountAmountMap.get(invoiceSubCategory) != null) {
                                Map<String, BigDecimal> billingAccountAmount = billingAccountAmountMap.get(invoiceSubCategory);
                                billingAccountAmount.put("billingAccountAmountWithoutTax",
                                    billingAccountAmount.get("billingAccountAmountWithoutTax").add(subscriptionAmountWithoutTax));
                                billingAccountAmount.put("billingAccountAmountWithTax", billingAccountAmount.get("billingAccountAmountWithTax").add(subscriptionAmountWithTax));
                                billingAccountAmountMap.put(invoiceSubCategory, billingAccountAmount);
                            } else {
                                Map<String, BigDecimal> billingAccountAmount = new HashMap<String, BigDecimal>();
                                billingAccountAmount.put("billingAccountAmountWithoutTax", subscriptionAmountWithoutTax);
                                billingAccountAmount.put("billingAccountAmountWithTax", subscriptionAmountWithTax);
                                billingAccountAmountMap.put(invoiceSubCategory, billingAccountAmount);
                            }

                        }
                    }
                }
            }
        }

        BigDecimal totalInvoiceAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalBillingAccountAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalBillingAccountAmountWithTax = BigDecimal.ZERO;

        for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : billingAccountAmountMap.entrySet()) {
            totalBillingAccountAmountWithoutTax = totalBillingAccountAmountWithoutTax.add(entry.getValue().get("billingAccountAmountWithoutTax"));
            totalBillingAccountAmountWithTax = totalBillingAccountAmountWithTax.add(entry.getValue().get("billingAccountAmountWithTax"));
        }   

        for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : billingAccountAmountMap.entrySet()) {

            BigDecimal billingAccountAmountWithoutTax = entry.getValue().get("billingAccountAmountWithoutTax");
            BigDecimal billingAccountAmountWithTax = entry.getValue().get("billingAccountAmountWithTax");
            InvoiceSubCategory invoiceSubCategory = entry.getKey();

            if(!StringUtils.isBlank(billingAccount.getMinimumAmountEl()) && billingAccountAmountWithoutTax != null && billingAccountAmountWithoutTax != BigDecimal.ZERO) {
                BigDecimal billingAccountMinAmount = new BigDecimal(evaluateDoubleExpression(billingAccount.getMinimumAmountEl(), billingAccount));
                String billingAccountMinLabel = evaluateStringExpression(billingAccount.getMinimumLabelEl(), billingAccount);

                BigDecimal ratio = BigDecimal.ZERO;
                BigDecimal diff = null;
                if(appProvider.isEntreprise()) {
                    diff = billingAccountMinAmount.subtract(totalBillingAccountAmountWithoutTax);
                    if(totalBillingAccountAmountWithoutTax != BigDecimal.ZERO) {
                        ratio = billingAccountAmountWithoutTax.divide(totalBillingAccountAmountWithoutTax, 2, RoundingMode.HALF_UP);  
                    }
                } else {
                    diff = billingAccountMinAmount.subtract(totalBillingAccountAmountWithTax);
                    if(totalBillingAccountAmountWithTax != BigDecimal.ZERO) {
                        ratio = billingAccountAmountWithTax.divide(totalBillingAccountAmountWithTax, 2, RoundingMode.HALF_UP);  
                    }
                }

                if(diff.doubleValue() > 0) {

                    BigDecimal taxPercent = BigDecimal.ZERO;
                    BigDecimal rtMinAmount = diff.multiply(ratio);
                    for(InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                        if(invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                            taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                            break;
                        }
                    }

                    BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ? rtMinAmount
                            : rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                    BigDecimal unitAmountWithTax = appProvider.isEntreprise()
                            ? rtMinAmount.add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) : rtMinAmount;
                    BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                    BigDecimal amountWithoutTax = unitAmountWithoutTax;
                    BigDecimal amountWithTax = unitAmountWithTax;
                    BigDecimal amountTax = unitAmountTax;

                    RatedTransaction ratedTransaction = new RatedTransaction(null, minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax, BigDecimal.ONE,
                        amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, invoiceSubCategory, "", "", "", "", null, "", "", null,
                        "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_BA.getCode() + "_" + billingAccount.getCode(), billingAccountMinLabel);
                    ratedTransactionService.create(ratedTransaction);
                    ratedTransactionService.commit();

                    billingAccountAmountWithoutTax = billingAccountAmountWithoutTax.add(amountWithoutTax);
                    billingAccountAmountWithTax = billingAccountAmountWithTax.add(amountWithTax);

                }
            }   
            totalInvoiceAmountWithoutTax = totalInvoiceAmountWithoutTax.add(billingAccountAmountWithoutTax);
        }

        return totalInvoiceAmountWithoutTax;
    }

}