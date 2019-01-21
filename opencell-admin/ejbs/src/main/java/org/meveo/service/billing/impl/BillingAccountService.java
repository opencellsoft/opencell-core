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
import java.util.ArrayList;
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
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
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
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.AccountService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.order.OrderService;

/**
 * The Class BillingAccountService.
 * 
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Mounir Bahije
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

    /** The user account service. */
    @Inject
    private UserAccountService userAccountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OrderService orderService;

    /** The billing run service. */
    @EJB
    private BillingRunService billingRunService;


    /** The invoice sub category service. */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;
    
    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    /**
     * Inits the billing account.
     *
     * @param billingAccount the billing account
     */
    public void initBillingAccount(BillingAccount billingAccount) {
        billingAccount.setStatus(AccountStatusEnum.ACTIVE);
        if (billingAccount.getSubscriptionDate() == null) {
            billingAccount.setSubscriptionDate(new Date());
        }

        if (billingAccount.getNextInvoiceDate() == null) {
            billingAccount.setNextInvoiceDate(new Date());
        }
    }

    /**
     * Creates the billing account.
     *
     * @param billingAccount the billing account
     * @throws BusinessException the business exception
     */
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

    /**
     * Update electronic billing.
     *
     * @param billingAccount the billing account
     * @param electronicBilling the electronic billing
     * @return the billing account
     * @throws BusinessException the business exception
     */
    public BillingAccount updateElectronicBilling(BillingAccount billingAccount, Boolean electronicBilling) throws BusinessException {
        billingAccount.setElectronicBilling(electronicBilling);
        return update(billingAccount);
    }

    /**
     * Update billing account discount.
     *
     * @param billingAccount the billing account
     * @param ratedDiscount the rated discount
     * @return the billing account
     * @throws BusinessException the business exception
     */
    public BillingAccount updateBillingAccountDiscount(BillingAccount billingAccount, BigDecimal ratedDiscount) throws BusinessException {
        billingAccount.setDiscountRate(ratedDiscount);
        return update(billingAccount);
    }

    /**
     * Billing account termination.
     *
     * @param billingAccount the billing account
     * @param terminationDate the termination date
     * @param terminationReason the termination reason
     * @return the billing account
     * @throws BusinessException the business exception
     */
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

    /**
     * Billing account cancellation.
     *
     * @param billingAccount the billing account
     * @param terminationDate the termination date
     * @return the billing account
     * @throws BusinessException the business exception
     */
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

    /**
     * Billing account reactivation.
     *
     * @param billingAccount the billing account
     * @param activationDate the activation date
     * @return the billing account
     * @throws BusinessException the business exception
     */
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

    /**
     * Close billing account.
     *
     * @param billingAccount the billing account
     * @return the billing account
     * @throws BusinessException the business exception
     */
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

    /**
     * Invoice list.
     *
     * @param billingAccount the billing account
     * @return the list
     * @throws BusinessException the business exception
     */
    public List<Invoice> invoiceList(BillingAccount billingAccount) throws BusinessException {
        List<Invoice> invoices = billingAccount.getInvoices();
        Collections.sort(invoices, new Comparator<Invoice>() {
            public int compare(Invoice c0, Invoice c1) {

                return c1.getInvoiceDate().compareTo(c0.getInvoiceDate());
            }
        });
        return invoices;
    }

    /**
     * Invoice detail.
     *
     * @param invoiceReference the invoice reference
     * @return the invoice
     */
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

    /**
     * Invoice sub category detail.
     *
     * @param invoiceReference the invoice reference
     * @param invoiceSubCategoryCode the invoice sub category code
     * @return the invoice sub category
     */
    public InvoiceSubCategory invoiceSubCategoryDetail(String invoiceReference, String invoiceSubCategoryCode) {
        // TODO : need to be more clarified
        return null;
    }

    /**
     * Find billing accounts.
     *
     * @param billingCycle the billing cycle
     * @param startdate the startdate
     * @param endDate the end date
     * @return the list
     */
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

    @SuppressWarnings("unchecked")
    public List<Subscription> findSubscriptions(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(Subscription.class, "s", null);
            qb.addCriterionEntity("s.billingCycle", billingCycle);

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, false);
            }

            return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Order> findOrders(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(Order.class, "o", null);
            qb.addCriterionEntity("o.billingCycle", billingCycle);

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, false);
            }

            return (List<Order>) qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    /**
     * Find billing account ids.
     *
     * @param billingCycle the billing cycle
     * @param startdate the startdate
     * @param endDate the end date
     * @return the list
     */
    public List<Long> findBillingAccountIds(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(BillingAccount.class, "b", null);
            qb.addCriterionEntity("b.billingCycle", billingCycle);

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, false);
            }

            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }
    
    /**
     * Update billing account total amounts.
     *
     * @param entity entity
     * @param billingRun the billing run
     * @return Updated entity
     * @throws BusinessException the business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBillableEntity updateEntityTotalAmounts(IBillableEntity entity, BillingRun billingRun) throws BusinessException {
        log.debug("updateEntityTotalAmounts  entity:" + entity.getId());

        BillingAccount billingAccount = null;
        if (entity instanceof BillingAccount) {
            entity = findByCode(((BillingAccount) entity).getCode());
            billingAccount = (BillingAccount) entity;
        }

        if (entity instanceof Subscription) {
            entity = subscriptionService.findByCode(((Subscription) entity).getCode());
            billingAccount = ((Subscription) entity).getUserAccount() != null ? ((Subscription) entity).getUserAccount().getBillingAccount() : null;
        }

        if (entity instanceof Order) {
            entity = orderService.findByCode(((Order) entity).getCode());
            if (((Order) entity).getUserAccounts() != null && !((Order) entity).getUserAccounts().isEmpty()) {
                billingAccount = ((Order) entity).getUserAccounts().stream().findFirst().get() != null ?
                        (((Order) entity).getUserAccounts().stream().findFirst().get()).getBillingAccount() : null;
            }
        }

        entity = calculateInvoicing(entity, null, billingRun.getLastTransactionDate(), billingRun);

        BigDecimal invoiceAmount = entity.getTotalInvoicingAmountWithoutTax();
        if (invoiceAmount != null) {
            BigDecimal invoicingThreshold = null;
            if (billingAccount != null) {
                invoicingThreshold = billingAccount.getInvoicingThreshold();
            }
            if (invoicingThreshold == null && billingRun.getBillingCycle() != null) {
                invoicingThreshold = billingRun.getBillingCycle().getInvoicingThreshold();
            }

            if (invoicingThreshold != null) {
                if (invoicingThreshold.compareTo(invoiceAmount) > 0) {
                    log.debug("updateEntityTotalAmounts  invoicingThreshold( stop invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}",
                            entity.getCode(), invoiceAmount, invoicingThreshold);
                    return null;
                } else {
                    log.debug("updateEntityTotalAmounts  invoicingThreshold(out continue invoicing)  baCode:{}, amountWithoutTax:{} ,invoicingThreshold:{}",
                            entity.getCode(), invoiceAmount, invoicingThreshold);
                }
            } else {
                log.debug("updateBillingAccountTotalAmounts no invoicingThreshold to apply");
            }

            log.debug("set brAmount {} in BA {}", invoiceAmount, entity.getId());
        }

        entity.setBillingRun(getEntityManager().getReference(BillingRun.class, billingRun.getId()));

        if (entity instanceof BillingAccount) {
            ((BillingAccount) entity).setBrAmountWithoutTax(invoiceAmount);
            updateNoCheck((BillingAccount) entity);
        }
        if (entity instanceof Order) {
            orderService.updateNoCheck((Order) entity);
        }
        if (entity instanceof Subscription) {
            subscriptionService.updateNoCheck((Subscription) entity);
        }

        return entity;
    }

    /**
     * Compute the invoice amount for order.
     * 
     * @param order order
     * @param firstTransactionDate first transaction date.
     * @param lastTransactionDate last transaction date
     * @return computed order's invoice amount.
     */
    public Object[] computeOrderInvoiceAmount(Order order, Date firstTransactionDate, Date lastTransactionDate) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByOrderNumber").setParameter("orderNumber", order.getOrderNumber())
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate);
        Object[] amounts = (Object[]) q.getSingleResult();
        return amounts;
    }

    /**
     * List by customer account.
     *
     * @param customerAccount the customer account
     * @return the list
     */
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
     * Determine if billing account is exonerated from taxes - check either a flag or EL expressions in customer's customerCategory.
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

            isExonerated = ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(customerCategory.getExonerationTaxEl(), userMap);
        }
        return isExonerated;
    }

    /**
     * Compute the invoice amount by charge.
     *
     * @param chargeInstance Charge instance
     * @param firstTransactionDate First transaction date.
     * @param lastTransactionDate Last transaction date
     * @param billingAccount Billing account
     * @param subscription The subscription
     * @return Computed invoice amount by charge.
     */
    public List<Object[]> computeChargeInvoiceAmount(ChargeInstance chargeInstance, Date firstTransactionDate, Date lastTransactionDate, BillingAccount billingAccount,
            Subscription subscription) {
        Query q = getEntityManager().createNamedQuery("RatedTransaction.sumByCharge").setParameter("chargeInstance", chargeInstance)
            .setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate).setParameter("billingAccount", billingAccount);
        if (chargeInstance == null) {
            q = getEntityManager().createNamedQuery("RatedTransaction.sumByOneShotCharge").setParameter("firstTransactionDate", firstTransactionDate)
                    .setParameter("lastTransactionDate", lastTransactionDate).setParameter("billingAccount", billingAccount).setParameter("subscription", subscription);
        }
        return (List<Object[]>) q.getResultList();
    }

    /**
     * Construct el context.
     *
     * @param expression EL expression
     * @param ba the ba
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
     * Construct el context.
     *
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
     * Construct el context.
     *
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
     * Evaluate double expression.
     *
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
     * Evaluate double expression.
     *
     * @param expression EL expression
     * @param serviceInstance serviceInstance
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
     * Evaluate double expression.
     *
     * @param expression EL expression
     * @param subscription subscription
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
     * Evaluate string expression.
     *
     * @param expression EL expression
     * @param ba billing account
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    public String evaluateStringExpression(String expression, BillingAccount ba) throws BusinessException {
        String result = "";
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
     * Evaluate string expression.
     *
     * @param expression EL expression
     * @param subscription subscription
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateStringExpression(String expression, Subscription subscription) throws BusinessException {
        String result = "";
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
     * Evaluate string expression.
     *
     * @param expression EL expression
     * @param serviceInstance serviceInstance
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    private String evaluateStringExpression(String expression, ServiceInstance serviceInstance) throws BusinessException {
        String result = "";
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
     * Create min amounts rated transactions.
     *
     * @param billableEntity The billable entity
     * @param lastTransactionDate Last transaction date
     * @return Invoice amount
     * @throws BusinessException General business exception
     */
    public IBillableEntity calculateInvoicing(IBillableEntity billableEntity, Date firstTransactionDate, Date lastTransactionDate, BillingRun billingRun) throws BusinessException {

        List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();

        Date minRatingDate = DateUtils.addDaysToDate(lastTransactionDate, -1);

        BillingAccount billingAccount = null;
        List<Subscription> subscriptionsToProcess = new ArrayList<Subscription>();
        if(billableEntity instanceof Subscription) {
            subscriptionsToProcess.add((Subscription) billableEntity);
            billingAccount = ((Subscription) billableEntity).getUserAccount().getBillingAccount();
        }

        if(billableEntity instanceof BillingAccount) {
            billingAccount = (BillingAccount) billableEntity;
            for (UserAccount userAccount : ((BillingAccount) billableEntity).getUsersAccounts()) {
                for (Subscription subscription : userAccount.getSubscriptions()) {
                        subscriptionsToProcess.add(subscription);
                }
            }
        }

        Map<InvoiceSubCategory, Map<String, BigDecimal>> billingAccountAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

        for (Subscription subscription : subscriptionsToProcess) {

            Map<InvoiceSubCategory, Map<String, BigDecimal>> subscriptionAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();
            Boolean isOneShotOtherCalculated = false;
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                if (serviceInstance.getStatus().equals(InstanceStatusEnum.ACTIVE)) {

                    Map<InvoiceSubCategory, Map<String, BigDecimal>> serviceAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();

                    List<RecurringChargeInstance> recurringChargeInstanceList = serviceInstance.getRecurringChargeInstances();
                    serviceAmountMap.putAll(getCharegInstanceAmounts(lastTransactionDate, billingAccount, recurringChargeInstanceList, subscription));

                    List<UsageChargeInstance> usageChargeInstanceList = serviceInstance.getUsageChargeInstances();
                    serviceAmountMap.putAll(getCharegInstanceAmounts(lastTransactionDate, billingAccount, usageChargeInstanceList, subscription));

                    List<OneShotChargeInstance> subscriptionChargeInstanceList = serviceInstance.getSubscriptionChargeInstances();
                    serviceAmountMap.putAll(getCharegInstanceAmounts(lastTransactionDate, billingAccount, subscriptionChargeInstanceList, subscription));

                    List<OneShotChargeInstance> terminationChargeInstanceList = serviceInstance.getTerminationChargeInstances();
                    serviceAmountMap.putAll(getCharegInstanceAmounts(lastTransactionDate, billingAccount, terminationChargeInstanceList, subscription));

                    // One Shot other
                    if (!isOneShotOtherCalculated) {
                        List<Object[]> amountsList = computeChargeInvoiceAmount(null, new Date(0), lastTransactionDate, billingAccount, subscription);

                        for (Object[] amounts : amountsList) {
                            serviceAmountMap.putAll(setAmounts(amounts));
                            isOneShotOtherCalculated = true;
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

                        String serviceMinAmountEL = StringUtils.isBlank(serviceInstance.getMinimumAmountEl()) ?
                                serviceInstance.getServiceTemplate().getMinimumAmountEl() :
                                serviceInstance.getMinimumAmountEl();
                        String serviceMinLabelEL = StringUtils.isBlank(serviceInstance.getMinimumLabelEl()) ?
                                serviceInstance.getServiceTemplate().getMinimumLabelEl() :
                                serviceInstance.getMinimumLabelEl();
                        if (!StringUtils.isBlank(serviceMinAmountEL)) {

                            BigDecimal serviceMinAmount = new BigDecimal(evaluateDoubleExpression(serviceMinAmountEL, serviceInstance));
                            String serviceMinLabel = evaluateStringExpression(serviceMinLabelEL, serviceInstance);

                            BigDecimal ratio = BigDecimal.ZERO;
                            BigDecimal diff = null;
                            if (appProvider.isEntreprise()) {
                                diff = serviceMinAmount.subtract(totalServiceAmountWithoutTax);
                                if (totalServiceAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                                    ratio = serviceAmountWithoutTax.divide(totalServiceAmountWithoutTax, 2, RoundingMode.HALF_UP);
                                } else {
                                    ratio = BigDecimal.ONE;
                                }
                            } else {
                                diff = serviceMinAmount.subtract(totalServiceAmountWithTax);
                                if (totalServiceAmountWithTax.compareTo(BigDecimal.ZERO) != 0) {
                                    ratio = serviceAmountWithTax.divide(totalServiceAmountWithTax, 2, RoundingMode.HALF_UP);
                                } else {
                                    ratio = BigDecimal.ONE;
                                }
                            }

                            if (diff.doubleValue() > 0) {
                                BigDecimal taxPercent = BigDecimal.ZERO;
                                BigDecimal rtMinAmount = diff.multiply(ratio);
                                for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                                    if (invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                        taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                        if (!StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL()) && (
                                                invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null, billingAccount, null) != null)) {
                                            taxPercent = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null, billingAccount, null)
                                                    .getPercent();
                                        }
                                        break;
                                    }
                                }
                                BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ?
                                        rtMinAmount :
                                        rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                                BigDecimal unitAmountWithTax = appProvider.isEntreprise() ?
                                        rtMinAmount.add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) :
                                        rtMinAmount;
                                BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                                BigDecimal amountWithoutTax = unitAmountWithoutTax;
                                BigDecimal amountWithTax = unitAmountWithTax;
                                BigDecimal amountTax = unitAmountTax;

                                RatedTransaction ratedTransaction = new RatedTransaction(null, minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax,

                                        BigDecimal.ONE, amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, invoiceSubCategory, "",
                                        "", "", "", null, null, "", "", null, "NO_OFFER", null,
                                        RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SE.getCode() + "_" + serviceInstance.getCode(), serviceMinLabel, null, null, subscription.getSeller());
                                ratedTransaction.setBillingRun(billingRun);
                                minAmountTransactions.add(ratedTransaction);

                                serviceAmountWithoutTax = serviceAmountWithoutTax.add(amountWithoutTax);
                                serviceAmountWithTax = serviceAmountWithTax.add(amountWithTax);
                            }
                        }

                        if (subscriptionAmountMap.get(invoiceSubCategory) != null) {
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

                String subscriptionMinAmountEL = StringUtils.isBlank(subscription.getMinimumAmountEl()) ?
                        subscription.getOffer().getMinimumAmountEl() :
                        subscription.getMinimumAmountEl();
                String subscriptionMinLabelEL = StringUtils.isBlank(subscription.getMinimumLabelEl()) ?
                        subscription.getOffer().getMinimumLabelEl() :
                        subscription.getMinimumLabelEl();

                if (!StringUtils.isBlank(subscriptionMinAmountEL)) {
                    BigDecimal subscriptionMinAmount = new BigDecimal(evaluateDoubleExpression(subscriptionMinAmountEL, subscription));
                    String subscriptionMinLabel = evaluateStringExpression(subscriptionMinLabelEL, subscription);

                    BigDecimal ratio = BigDecimal.ZERO;
                    BigDecimal diff = null;
                    if (appProvider.isEntreprise()) {
                        diff = subscriptionMinAmount.subtract(totalSubscriptionAmountWithoutTax);
                        if (totalSubscriptionAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = subscriptionAmountWithoutTax.divide(totalSubscriptionAmountWithoutTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    } else {
                        diff = subscriptionMinAmount.subtract(totalSubscriptionAmountWithTax);
                        if (totalSubscriptionAmountWithTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = subscriptionAmountWithTax.divide(totalSubscriptionAmountWithTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    }

                    if (diff.doubleValue() > 0) {

                        BigDecimal taxPercent = BigDecimal.ZERO;
                        BigDecimal rtMinAmount = diff.multiply(ratio);
                        for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                            if (invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                if (!StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL()) && (
                                        invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null, billingAccount, null) != null)) {
                                    taxPercent = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null, billingAccount, null).getPercent();
                                }
                                break;
                            }
                        }

                        BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ?
                                rtMinAmount :
                                rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        BigDecimal unitAmountWithTax = appProvider.isEntreprise() ?
                                rtMinAmount.add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) :
                                rtMinAmount;
                        BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                        BigDecimal amountWithoutTax = unitAmountWithoutTax;
                        BigDecimal amountWithTax = unitAmountWithTax;
                        BigDecimal amountTax = unitAmountTax;

                        RatedTransaction ratedTransaction = new RatedTransaction(null, minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax, BigDecimal.ONE,
                                amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, invoiceSubCategory, "", "", "", "", null,
                                subscription, "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_SU.getCode() + "_" + subscription.getCode(),
                                subscriptionMinLabel, null, null, subscription.getSeller());
                        ratedTransaction.setBillingRun(billingRun);
                        minAmountTransactions.add(ratedTransaction);

                        subscriptionAmountWithoutTax = subscriptionAmountWithoutTax.add(amountWithoutTax);
                        subscriptionAmountWithTax = subscriptionAmountWithTax.add(amountWithTax);

                    }
                }

                if (billingAccountAmountMap.get(invoiceSubCategory) != null) {
                    Map<String, BigDecimal> billingAccountAmount = billingAccountAmountMap.get(invoiceSubCategory);
                    billingAccountAmount.put("billingAccountAmountWithoutTax", billingAccountAmount.get("billingAccountAmountWithoutTax").add(subscriptionAmountWithoutTax));
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

        BigDecimal totalInvoiceAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountWithTax = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmountTax = BigDecimal.ZERO;
        BigDecimal totalBillingAccountAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalBillingAccountAmountWithTax = BigDecimal.ZERO;

        for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : billingAccountAmountMap.entrySet()) {
            totalBillingAccountAmountWithoutTax = totalBillingAccountAmountWithoutTax.add(entry.getValue().get("billingAccountAmountWithoutTax"));
            totalBillingAccountAmountWithTax = totalBillingAccountAmountWithTax.add(entry.getValue().get("billingAccountAmountWithTax"));
        }

        if(billableEntity instanceof Subscription) {
            totalInvoiceAmountWithoutTax = totalBillingAccountAmountWithoutTax;
            totalInvoiceAmountWithTax = totalBillingAccountAmountWithTax;
            totalInvoiceAmountTax = totalBillingAccountAmountWithTax.subtract(totalBillingAccountAmountWithoutTax);
        }

        if(billableEntity instanceof Order) {
            Object[] amounts = computeOrderInvoiceAmount((Order) billableEntity, new Date(0), lastTransactionDate);
            totalInvoiceAmountWithoutTax = (BigDecimal) amounts[0];
            totalInvoiceAmountWithTax = (BigDecimal) amounts[1];
            totalInvoiceAmountTax = (BigDecimal) amounts[2];
        }

        if(billableEntity instanceof BillingAccount) {

            for (Map.Entry<InvoiceSubCategory, Map<String, BigDecimal>> entry : billingAccountAmountMap.entrySet()) {

                BigDecimal billingAccountAmountWithoutTax = entry.getValue().get("billingAccountAmountWithoutTax");
                BigDecimal billingAccountAmountWithTax = entry.getValue().get("billingAccountAmountWithTax");
                InvoiceSubCategory invoiceSubCategory = entry.getKey();

                if (!StringUtils.isBlank(billingAccount.getMinimumAmountEl()) && billingAccountAmountWithoutTax != null
                        && billingAccountAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal billingAccountMinAmount = new BigDecimal(evaluateDoubleExpression(billingAccount.getMinimumAmountEl(), billingAccount));
                    String billingAccountMinLabel = evaluateStringExpression(billingAccount.getMinimumLabelEl(), billingAccount);

                    BigDecimal ratio = BigDecimal.ZERO;
                    BigDecimal diff = null;
                    if (appProvider.isEntreprise()) {
                        diff = billingAccountMinAmount.subtract(totalBillingAccountAmountWithoutTax);
                        if (totalBillingAccountAmountWithoutTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = billingAccountAmountWithoutTax.divide(totalBillingAccountAmountWithoutTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    } else {
                        diff = billingAccountMinAmount.subtract(totalBillingAccountAmountWithTax);
                        if (totalBillingAccountAmountWithTax.compareTo(BigDecimal.ZERO) != 0) {
                            ratio = billingAccountAmountWithTax.divide(totalBillingAccountAmountWithTax, 2, RoundingMode.HALF_UP);
                        } else {
                            ratio = BigDecimal.ONE;
                        }
                    }

                    if (diff.doubleValue() > 0) {

                        BigDecimal taxPercent = BigDecimal.ZERO;
                        BigDecimal rtMinAmount = diff.multiply(ratio);
                        for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                            if (invoiceSubcategoryCountry.getTradingCountry() == billingAccount.getTradingCountry()) {
                                taxPercent = invoiceSubcategoryCountry.getTax().getPercent();
                                if (!StringUtils.isBlank(invoiceSubcategoryCountry.getTaxCodeEL()) && (invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null,  billingAccount, null) != null)) {
                                    taxPercent = invoiceSubCategoryService.evaluateTaxCodeEL(invoiceSubcategoryCountry.getTaxCodeEL(), null,  billingAccount, null).getPercent();
                                }
                                break;
                            }
                        }

                        BigDecimal unitAmountWithoutTax = appProvider.isEntreprise() ?
                                rtMinAmount :
                                rtMinAmount.subtract(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        BigDecimal unitAmountWithTax = appProvider.isEntreprise() ?
                                rtMinAmount.add(rtMinAmount.multiply(taxPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) :
                                rtMinAmount;
                        BigDecimal unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
                        BigDecimal amountWithoutTax = unitAmountWithoutTax;
                        BigDecimal amountWithTax = unitAmountWithTax;
                        BigDecimal amountTax = unitAmountTax;

                        RatedTransaction ratedTransaction = new RatedTransaction(null, minRatingDate, unitAmountWithoutTax, unitAmountWithTax, unitAmountTax, BigDecimal.ONE,
                                amountWithoutTax, amountWithTax, amountTax, RatedTransactionStatusEnum.OPEN, null, billingAccount, invoiceSubCategory, "", "", "", "", null, null,
                                "", "", null, "NO_OFFER", null, RatedTransactionMinAmountTypeEnum.RT_MIN_AMOUNT_BA.getCode() + "_" + billingAccount.getCode(),
                                billingAccountMinLabel, null, null, null);
                        ratedTransaction.setBillingRun(billingRun);
                        minAmountTransactions.add(ratedTransaction);

                        billingAccountAmountWithoutTax = billingAccountAmountWithoutTax.add(amountWithoutTax);
                        billingAccountAmountWithTax = billingAccountAmountWithTax.add(amountWithTax);

                    }
                }
                totalInvoiceAmountWithoutTax = totalInvoiceAmountWithoutTax.add(billingAccountAmountWithoutTax);
                totalInvoiceAmountWithTax = totalInvoiceAmountWithTax.add(billingAccountAmountWithTax);
            }

            totalInvoiceAmountTax = totalInvoiceAmountWithTax.subtract(totalInvoiceAmountWithoutTax);
        }

        billableEntity.setMinRatedTransactions(minAmountTransactions);
        billableEntity.setTotalInvoicingAmountWithoutTax(totalInvoiceAmountWithoutTax);
        billableEntity.setTotalInvoicingAmountWithTax(totalInvoiceAmountWithTax);
        billableEntity.setTotalInvoicingAmountTax(totalInvoiceAmountTax);

        return billableEntity;
    }

    private Map<InvoiceSubCategory, Map<String, BigDecimal>> getCharegInstanceAmounts(Date lastTransactionDate, BillingAccount billingAccount,
            List<? extends ChargeInstance> chargeInstanceList, Subscription subscription) {
        Map<InvoiceSubCategory, Map<String, BigDecimal>> serviceAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();
        for (ChargeInstance chargeInstance : chargeInstanceList) {
            List<Object[]> amountsList = computeChargeInvoiceAmount(chargeInstance, new Date(0), lastTransactionDate, billingAccount, subscription);

            for (Object[] amounts : amountsList) {
                serviceAmountMap.putAll(setAmounts(amounts));
            }
        }
        return serviceAmountMap;
    }

    private Map<InvoiceSubCategory, Map<String, BigDecimal>> setAmounts(Object[] amounts) {
        Map<InvoiceSubCategory, Map<String, BigDecimal>> serviceAmountMap = new HashMap<InvoiceSubCategory, Map<String, BigDecimal>>();
        BigDecimal chargeAmountWithoutTax = (BigDecimal) amounts[0];
        BigDecimal chargeAmountWithTax = (BigDecimal) amounts[1];
        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findById((Long) amounts[3]);

        if (chargeAmountWithoutTax != null) {
            if (serviceAmountMap.get(invoiceSubCategory) != null) {
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
        return serviceAmountMap;
    }

    /**
     * Find billing accounts by billing run.
     *
     * @param billingRunId Billing run id
     * @return A list of billing account identifiers
     */
    public List<Long> findBillingAccountIdsByBillingRun(Long billingRunId) {
        return getEntityManager().createNamedQuery("BillingAccount.listIdsByBillingRunId", Long.class).setParameter("billingRunId", billingRunId).getResultList();
    }
    
    public BillingAccount instantiateDiscountPlans(BillingAccount entity, List<DiscountPlan> discountPlans) throws BusinessException {
		List<DiscountPlanInstance> toAdd = new ArrayList<>();
		for (DiscountPlan dp : discountPlans) {
			instantiateDiscountPlan(entity, dp, toAdd);
		}
		
		if (!toAdd.isEmpty()) {
			entity.getDiscountPlanInstances().addAll(toAdd);
		}
		
		return entity;
	}
	
    public BillingAccount instantiateDiscountPlan(BillingAccount entity, DiscountPlan dp, List<DiscountPlanInstance> toAdd, Date startDate) throws BusinessException {
        if (entity.getDiscountPlanInstances() == null || entity.getDiscountPlanInstances().isEmpty()) {
            // add
            entity.setDiscountPlanInstances(new ArrayList<>());
            DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
            discountPlanInstance.setBillingAccount(entity);
            discountPlanInstance.setDiscountPlan(dp);
            discountPlanInstance.copyEffectivityDates(dp);
            if (startDate != null) {
                discountPlanInstance.setStartDate(startDate);  
            }
            discountPlanInstanceService.create(discountPlanInstance, dp);
            entity.getDiscountPlanInstances().add(discountPlanInstance);
            
        } else {
            boolean found = false;
            DiscountPlanInstance dpiMatched = null;
            for (DiscountPlanInstance dpi : entity.getDiscountPlanInstances()) {
                if (dp.equals(dpi.getDiscountPlan())) {
                    found = true;
                    dpiMatched = dpi;
                    break;
                }
            }
            
            if (found && dpiMatched != null) {
                // update effectivity dates
                dpiMatched.copyEffectivityDates(dp);
                if (startDate != null) {
                    dpiMatched.setStartDate(startDate);  
                }
                discountPlanInstanceService.update(dpiMatched, dp);
                
            } else {
                // add
                DiscountPlanInstance discountPlanInstance = new DiscountPlanInstance();
                discountPlanInstance.setBillingAccount(entity);
                discountPlanInstance.setDiscountPlan(dp);
                discountPlanInstance.copyEffectivityDates(dp);
                if (startDate != null) {
                    discountPlanInstance.setStartDate(startDate);  
                }
                discountPlanInstanceService.create(discountPlanInstance, dp);
                if (toAdd != null) {
                    toAdd.add(discountPlanInstance);
                } else {
                    entity.getDiscountPlanInstances().add(discountPlanInstance);
                }
            }
        }
        return entity;
    }
    
	public BillingAccount instantiateDiscountPlan(BillingAccount entity, DiscountPlan dp, List<DiscountPlanInstance> toAdd) throws BusinessException {
	    return instantiateDiscountPlan(entity, dp, toAdd, null);
	}
	
	public void terminateDiscountPlans(BillingAccount entity, List<DiscountPlanInstance> dpis)
			throws BusinessException {
		if (dpis == null) {
			return;
		}

		for (DiscountPlanInstance dpi : dpis) {
			terminateDiscountPlan(entity, dpi);
		}
	}

	public void terminateDiscountPlan(BillingAccount entity, DiscountPlanInstance dpi) throws BusinessException {
		discountPlanInstanceService.remove(dpi);
		entity.getDiscountPlanInstances().remove(dpi);
	}
}