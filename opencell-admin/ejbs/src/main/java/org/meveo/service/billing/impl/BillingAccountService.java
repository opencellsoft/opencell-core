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
import java.util.Calendar;
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
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionMinAmountTypeEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.AccountService;
import org.meveo.service.base.ValueExpressionWrapper;

@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

    @Inject
    private UserAccountService userAccountService;

    @EJB
    private BillingRunService billingRunService;
    
    @Inject
    private RatedTransactionService ratedTransactionService;

    public void initBillingAccount(BillingAccount billingAccount) {
        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        if (billingAccount.getSubscriptionDate() == null) {
            billingAccount.setSubscriptionDate(new Date());
        }

        if (billingAccount.getNextInvoiceDate() == null) {
            billingAccount.setNextInvoiceDate(new Date());
        }
    }

    public void createBillingAccount(BillingAccount billingAccount) throws BusinessException {

        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        if (billingAccount.getSubscriptionDate() == null) {
            billingAccount.setSubscriptionDate(new Date());
        }

        if (billingAccount.getNextInvoiceDate() == null) {
            billingAccount.setNextInvoiceDate(new Date());
        }

        create(billingAccount);
    }

    public BillingAccount updateElectronicBilling(BillingAccount billingAccount, Boolean electronicBilling) throws BusinessException {
        billingAccount.setElectronicBilling(electronicBilling);
        return update(billingAccount);
    }

    public BillingAccount updateBillingAccountDiscount(BillingAccount billingAccount, BigDecimal ratedDiscount) throws BusinessException {
        billingAccount.setDiscountRate(ratedDiscount);
        return update(billingAccount);
    }

    @MeveoAudit
    public BillingAccount billingAccountTermination(BillingAccount billingAccount, Date terminationDate, SubscriptionTerminationReason terminationReason) throws BusinessException {
        if (terminationDate == null) {
            terminationDate = new Date();
        }

        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : userAccounts) {
            userAccountService.userAccountTermination(userAccount, terminationDate, terminationReason);
        }
        billingAccount.setTerminationReason(terminationReason);
        billingAccount.setTerminationDate(terminationDate);
        billingAccount.setStatus(AccountStatusEnum.TERMINATED);
        return update(billingAccount);
    }

    @MeveoAudit
    public BillingAccount billingAccountCancellation(BillingAccount billingAccount, Date terminationDate) throws BusinessException {
        if (terminationDate == null) {
            terminationDate = new Date();
        }
        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        for (UserAccount userAccount : userAccounts) {
            userAccountService.userAccountCancellation(userAccount, terminationDate);
        }
        billingAccount.setTerminationDate(terminationDate);
        billingAccount.setStatus(AccountStatusEnum.CANCELED);
        return update(billingAccount);
    }

    @MeveoAudit
    public BillingAccount billingAccountReactivation(BillingAccount billingAccount, Date activationDate) throws BusinessException {
        if (activationDate == null) {
            activationDate = new Date();
        }
        if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED && billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new ElementNotResiliatedOrCanceledException("billing account", billingAccount.getCode());
        }

        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        return update(billingAccount);
    }

    @MeveoAudit
    public BillingAccount closeBillingAccount(BillingAccount billingAccount) throws BusinessException {

        /**
         * *
         * 
         * @Todo : ajouter la condition : l'encours de facturation est vide :
         */
        if (billingAccount.getStatus() != AccountStatusEnum.TERMINATED && billingAccount.getStatus() != AccountStatusEnum.CANCELED) {
            throw new ElementNotResiliatedOrCanceledException("billing account", billingAccount.getCode());
        }
        billingAccount.setStatus(AccountStatusEnum.CLOSED);
        return update(billingAccount);
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

    public Invoice InvoiceDetail(String invoiceReference) {
        try {
            QueryBuilder qb = new QueryBuilder(Invoice.class, "i");
            qb.addCriterion("i.invoiceNumber", "=", invoiceReference, true);
            return (Invoice) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ex) {
            log.debug("invoice search returns no result for reference={}.", invoiceReference);
        }
        return null;
    }

    public InvoiceSubCategory invoiceSubCategoryDetail(String invoiceReference, String invoiceSubCategoryCode) {
        // TODO : need to be more clarified
        return null;
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
        BillingAccount billingAccount = findById(billingAccountId, true);
        Object[] objects = computeBaInvoiceAmount(billingAccount, new Date(0), billingRun.getLastTransactionDate());
        BigDecimal invoiceAmount = (BigDecimal)objects[0];
        
        if (invoiceAmount != null) {
            BillingCycle billingCycle = billingRun.getBillingCycle();
			BigDecimal invoicingThreshold = billingCycle == null ? null : billingCycle.getInvoicingThreshold();

            log.debug("updateBillingAccountTotalAmounts invoicingThreshold is {}", invoicingThreshold);
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
    
    public BigDecimal createMinAmountsRT(BillingAccount billingAccount, Date lastTransactionDate) throws BusinessException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date rtDate = cal.getTime();
        
        for(UserAccount userAccount : billingAccount.getUsersAccounts()) {
            for(Subscription subscription : userAccount.getSubscriptions()) {
                BigDecimal totalSubscriptionBilledAmountWithTax = BigDecimal.ZERO;
                BigDecimal totalSubscriptionBilledAmountWithoutTax = BigDecimal.ZERO;
                BigDecimal totalSubscriptionBilledAmountTax = BigDecimal.ZERO;

                for(ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                    
                    if(!StringUtils.isBlank(serviceInstance.getMinimumAmountEl())) {
                        
                        BigDecimal serviceMinAmount = new BigDecimal(evaluateDoubleExpression(serviceInstance.getMinimumAmountEl(), serviceInstance));
                        String serviceMinLabel = evaluateStringExpression(serviceInstance.getMinimumLabelEl(), serviceInstance);

                        BigDecimal serviceAmountWithTax = BigDecimal.ZERO;
                        BigDecimal serviceAmountWithoutTax = BigDecimal.ZERO;
                        BigDecimal serviceAmountTax = BigDecimal.ZERO;
                        
                        List<RecurringChargeInstance> recurringChargeInstanceList = serviceInstance.getRecurringChargeInstances();
                        for(RecurringChargeInstance recurringChargeInstance : recurringChargeInstanceList) {
                            Object[] amounts = computeChargeInvoiceAmount(recurringChargeInstance, new Date(0), lastTransactionDate, billingAccount);
                            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                            BigDecimal chargeAmountTax = (BigDecimal) amounts[2];
                            
                            if(chargeAmountWithoutTax != null) {
                                serviceAmountWithoutTax = serviceAmountWithoutTax.add(chargeAmountWithoutTax);
                                serviceAmountWithTax = serviceAmountWithTax.add(chargeAmountWithTax);
                                serviceAmountTax = serviceAmountTax.add(chargeAmountTax);
                            }
                        }
                        
                        List<UsageChargeInstance> usageChargeInstanceList = serviceInstance.getUsageChargeInstances();
                        for(UsageChargeInstance usageChargeInstance : usageChargeInstanceList) {
                            Object[] amounts = computeChargeInvoiceAmount(usageChargeInstance, new Date(0), lastTransactionDate, billingAccount);
                            BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
                            BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
                            BigDecimal chargeAmountTax = (BigDecimal) amounts[2];

                            if(chargeAmountWithoutTax != null) {
                                serviceAmountWithoutTax = serviceAmountWithoutTax.add(chargeAmountWithoutTax);
                                serviceAmountWithTax = serviceAmountWithTax.add(chargeAmountWithTax);
                                serviceAmountTax = serviceAmountTax.add(chargeAmountTax);
                            }
                        }
                        
                        if(serviceAmountWithoutTax != BigDecimal.ZERO) {
                            BigDecimal diff = null;
                            if(appProvider.isEntreprise()) {
                                diff = serviceMinAmount.subtract(serviceAmountWithoutTax);
                            } else {
                                diff = serviceMinAmount.subtract(serviceAmountWithTax);
                            }
                            
                            if(diff.doubleValue() > 0) {
                                BigDecimal percent = BigDecimal.ZERO;
                                if(serviceAmountWithTax != BigDecimal.ZERO) {
                                    percent = serviceAmountTax.divide(serviceAmountWithTax, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                                }
    
                                BigDecimal unitAmountWithoutTax = appProvider.isEntreprise()?diff:diff.subtract(diff.multiply(percent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                                BigDecimal unitAmountWithTax = appProvider.isEntreprise()?diff.add(diff.multiply(percent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)):diff;
                                BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                                BigDecimal amountWithoutTax = unitAmountWithoutTax;
                                BigDecimal amountWithTax = unitAmountWithTax;
                                BigDecimal amountTax = unitAmountTax;
                                
                                RatedTransaction ratedTransaction = new RatedTransaction(null, rtDate, unitAmountWithoutTax, unitAmountWithTax,
                                    unitAmountTax,  BigDecimal.ONE, amountWithoutTax, amountWithTax,  amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount,
                                    null, "", "", "", "", null, "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_SERVICE_AMOUNT.getCode()+"_"+serviceInstance.getCode(), serviceMinLabel);
                                ratedTransactionService.create(ratedTransaction);
                                ratedTransactionService.commit();
                                
                                if(appProvider.isEntreprise()) {
                                    serviceAmountWithTax = serviceMinAmount;
                                    serviceAmountWithoutTax = serviceAmountWithoutTax.add(amountWithoutTax);
                                } else {
                                    serviceAmountWithTax = serviceAmountWithTax.add(amountWithTax);
                                    serviceAmountWithoutTax = serviceMinAmount;
                                }
                            }
                    
                            totalSubscriptionBilledAmountWithTax = totalSubscriptionBilledAmountWithTax.add(serviceAmountWithTax);
                            totalSubscriptionBilledAmountWithoutTax = totalSubscriptionBilledAmountWithoutTax.add(serviceAmountWithoutTax);
                            totalSubscriptionBilledAmountTax = totalSubscriptionBilledAmountWithTax.subtract(totalSubscriptionBilledAmountWithoutTax);
                        }
                    }
                }
                
                if(!StringUtils.isBlank(subscription.getMinimumAmountEl()) && totalSubscriptionBilledAmountWithoutTax != BigDecimal.ZERO) {
                    BigDecimal subscriptionMinAmount = new BigDecimal(evaluateDoubleExpression(subscription.getMinimumAmountEl(), subscription));
                    String subscriptionMinLabel = evaluateStringExpression(subscription.getMinimumLabelEl(), subscription);
                    BigDecimal percent = BigDecimal.ZERO;
                    if(totalSubscriptionBilledAmountWithTax != BigDecimal.ZERO) {
                        percent = totalSubscriptionBilledAmountTax.divide(totalSubscriptionBilledAmountWithTax, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                    }
                    
                    BigDecimal diff = null;
                    if(appProvider.isEntreprise()) {
                        diff = subscriptionMinAmount.subtract(totalSubscriptionBilledAmountWithoutTax);
                    } else {
                        diff = subscriptionMinAmount.subtract(totalSubscriptionBilledAmountWithTax);
                    }
                    
                    if(diff.doubleValue() > 0) {
                        
                        BigDecimal unitAmountWithoutTax = appProvider.isEntreprise()?diff:diff.add(diff.multiply(percent));
                        BigDecimal unitAmountWithTax = appProvider.isEntreprise()?diff.subtract(diff.multiply(percent)):diff;
                        BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                        BigDecimal amountWithoutTax = unitAmountWithoutTax;
                        BigDecimal amountWithTax = unitAmountWithTax;
                        BigDecimal amountTax = unitAmountTax;
                        
                        RatedTransaction ratedTransaction = new RatedTransaction(null, rtDate, unitAmountWithoutTax, unitAmountWithTax,
                            unitAmountTax, BigDecimal.ONE, amountWithoutTax, amountWithTax,  amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount,
                            null, "", "", "", "", null, "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_SUBSCRIPTION_AMOUNT.getCode()+"_"+subscription.getCode(), subscriptionMinLabel);
                        ratedTransactionService.create(ratedTransaction);
                        ratedTransactionService.commit();
                    }
                }
            }
        }
        
        Object[] amounts = computeBaInvoiceAmount(billingAccount, new Date(0), lastTransactionDate);
        BigDecimal invoiceAmountWithoutTax = (BigDecimal) amounts[0];
        BigDecimal invoiceAmountWithTax = (BigDecimal) amounts[1];
        BigDecimal invoiceAmountTax = (BigDecimal) amounts[2];
        
        if(!StringUtils.isBlank(billingAccount.getMinimumAmountEl()) && invoiceAmountWithoutTax != null && invoiceAmountWithoutTax != BigDecimal.ZERO) {
            BigDecimal billingAccountMinAmount = new BigDecimal(evaluateDoubleExpression(billingAccount.getMinimumAmountEl(), billingAccount));
            String billingAccountMinLabel = evaluateStringExpression(billingAccount.getMinimumLabelEl(), billingAccount);
            BigDecimal percent = BigDecimal.ZERO;
            if(invoiceAmountWithTax != null && invoiceAmountWithTax != BigDecimal.ZERO) {
                percent = invoiceAmountTax.divide(invoiceAmountWithTax, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
                    
            BigDecimal diff = null;
            if(appProvider.isEntreprise()) {
                diff = billingAccountMinAmount.subtract(invoiceAmountWithoutTax);
            } else {
                diff = billingAccountMinAmount.subtract(invoiceAmountWithTax);
            }
            
            if(diff.doubleValue() > 0) {
                
                BigDecimal unitAmountWithoutTax = appProvider.isEntreprise()?diff:diff.subtract(diff.multiply(percent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                BigDecimal unitAmountWithTax = appProvider.isEntreprise()?diff.add(diff.multiply(percent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)):diff;
                BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                BigDecimal amountWithoutTax = unitAmountWithoutTax;
                BigDecimal amountWithTax = unitAmountWithTax;
                BigDecimal amountTax = unitAmountTax;
                
                RatedTransaction ratedTransaction = new RatedTransaction(null, rtDate, unitAmountWithoutTax, unitAmountWithTax,
                    unitAmountTax,  BigDecimal.ONE, amountWithoutTax, amountWithTax,  amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount,
                    null, "", "", "", "", null, "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_BA_AMOUNT.getCode()+"_"+billingAccount.getCode(), billingAccountMinLabel);
                
                ratedTransactionService.create(ratedTransaction);
                ratedTransactionService.commit();
                return amountWithoutTax.add(invoiceAmountWithoutTax);
            }
        }   
        
        return invoiceAmountWithoutTax;
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean updateBillingAccountTotalAmountsAndCreateMinBilledRT(Long billingAccountId, BillingRun billingRun) throws BusinessException {
        log.debug("updateBillingAccountTotalAmountsAndCreateMinBilledRT  billingAccount:" + billingAccountId);
        BillingAccount billingAccount = findById(billingAccountId, true);
        
        BigDecimal invoiceAmount = createMinAmountsRT(billingAccount,  billingRun.getLastTransactionDate());
        
        if (invoiceAmount != null) {
            BillingCycle billingCycle = billingRun.getBillingCycle();
            BigDecimal invoicingThreshold = billingCycle == null ? null : billingCycle.getInvoicingThreshold();

            log.debug("updateBillingAccountTotalAmounts invoicingThreshold is {}", invoicingThreshold);
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
	public Object[] computeBaInvoiceAmount(BillingAccount billingAccount, Date firstTransactionDate,
			Date lastTransactionDate) {
		Query q = getEntityManager().createNamedQuery("RatedTransaction.sumBillingAccount")
				.setParameter("billingAccount", billingAccount)
				.setParameter("firstTransactionDate", firstTransactionDate)
				.setParameter("lastTransactionDate", lastTransactionDate);
		return (Object[]) q.getSingleResult();
	}
	
	/**
     * Compute the invoice amount by charge.
     * 
     * @param chargeInstance chargeInstance
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @return computed invoice amount by charge.
     */
	public Object[] computeChargeInvoiceAmount(ChargeInstance chargeInstance, Date firstTransactionDate,
            Date lastTransactionDate, BillingAccount billingAccount) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByCharge")
                .setParameter("chargeInstance", chargeInstance)
                .setParameter("firstTransactionDate", firstTransactionDate)
                .setParameter("lastTransactionDate", lastTransactionDate)
                .setParameter("billingAccount", billingAccount);
        return (Object[]) q.getSingleResult();
    }
	
	
	

    @SuppressWarnings("unchecked")
    public List<BillingAccount> listByCustomerAccount(CustomerAccount customerAccount) {
        QueryBuilder qb = new QueryBuilder(BillingAccount.class, "c");
        qb.addCriterionEntity("customerAccount", customerAccount);
        qb.addOrderCriterion("c.id", true);
        try {
            return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list by customer account", e);
            return null;
        }
    }

    /**
     * Evatuate the exoneration Taxes EL.
     * 
     * @param ba The BillingAccount
     * @return true if it is exonerated.
     */
    public boolean isExonerated(BillingAccount ba) {
        boolean isExonerated = false;
        CustomerCategory customerCategory = null;
        if (ba == null || ba.getCustomerAccount().getCustomer().getCustomerCategory() == null) {
            return false;
        }
        customerCategory = ba.getCustomerAccount().getCustomer().getCustomerCategory();
        if (customerCategory.getExoneratedFromTaxes()) {
            return true;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        if (!StringUtils.isBlank(customerCategory.getExonerationTaxEl())) {
            if (customerCategory.getExonerationTaxEl().indexOf("ba.") > -1) {
                userMap.put("ba", ba);
            }
            Boolean isExon = Boolean.FALSE;
            try {
                isExon = (Boolean) ValueExpressionWrapper.evaluateExpression(customerCategory.getExonerationTaxEl(), userMap, Boolean.class);
            } catch (BusinessException e) {
                log.error("Error evaluateExpression Exoneration taxes:", e);
                e.printStackTrace();
            }
            isExonerated = (isExon == null ? false : isExon);
        }
        return isExonerated;
    }

}
