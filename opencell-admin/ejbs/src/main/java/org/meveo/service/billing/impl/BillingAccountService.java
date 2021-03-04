/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.AccountService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * The Class BillingAccountService.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Mounir Bahije
 * @author anasseh
 * @lastModifiedVersion 10.0
 */
@Stateless
public class BillingAccountService extends AccountService<BillingAccount> {

    /** The user account service. */
    @Inject
    private UserAccountService userAccountService;

    /** The billing run service. */
    @EJB
    private BillingRunService billingRunService;

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

    @Override
    public void create(BillingAccount entity) throws BusinessException {
        checkBillingAccountPaymentMethod(entity, entity.getCustomerAccount().getPaymentMethods());
        super.create(entity);
    }

    @Override
    public BillingAccount update(BillingAccount entity) throws BusinessException {
        checkBillingAccountPaymentMethod(entity, entity.getCustomerAccount().getPaymentMethods());
        return super.update(entity);
    }

    private void checkBillingAccountPaymentMethod(BillingAccount billingAccount, List<PaymentMethod> paymentMethods) {
        if(Objects.nonNull(billingAccount.getPaymentMethod()) && (paymentMethods.isEmpty() || paymentMethods.stream()
                .filter(PaymentMethod::isActive)
                .noneMatch(paymentMethod -> paymentMethod.getId().equals(billingAccount.getPaymentMethod().getId())))){
            log.error("the payment method should be reference to an active PaymentMethod defined on the CustomerAccount");
            throw new BusinessException("the payment method should be reference to an active PaymentMethod defined on the CustomerAccount");
        }
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
            qb.addCriterionEntity("b.billingCycle.id", billingCycle.getId());

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                boolean inclusive = Boolean.parseBoolean(paramBeanFactory.getInstance().getProperty("invoicing.includeEndDate", "false"));
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, inclusive, false);
            }

            qb.addOrderCriterionAsIs("id", true);

            return (List<BillingAccount>) qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
    }

    /**
     * Find a list of not processed billing accounts by a billing run
     * 
     * @param billingRun Billing run
     * @return A list of Billing Account identifiers
     */
    @SuppressWarnings("unchecked")
    public List<Long> findNotProcessedBillingAccounts(BillingRun billingRun) {

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();

        if (endDate == null) {
            endDate = new Date();
        }

        Date maxNextDate = DateUtils.truncateTime(endDate);
        Query query = null;
        if (startDate == null) {
            query = getEntityManager().createNamedQuery("BillingAccount.getUnbilledByBC").setParameter("billingCycle", billingRun.getBillingCycle()).setParameter("billingRun", billingRun)
                .setParameter("maxNextInvoiceDate", maxNextDate);
        } else {
            startDate = DateUtils.truncateTime(startDate);
            query = getEntityManager().createNamedQuery("BillingAccount.getUnbilledByBCWithStartDate").setParameter("billingCycle", billingRun.getBillingCycle()).setParameter("billingRun", billingRun)
                .setParameter("maxNextInvoiceDate", maxNextDate).setParameter("minNextInvoiceDate", startDate);
        }

        return query.getResultList();
    }

    /**
     * List billing accounts that are associated with a given billing run
     * 
     * @param billingRun Billing run
     * @return A list of Billing accounts
     */
    public List<BillingAccount> findBillingAccounts(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("BillingAccount.listByBillingRun", BillingAccount.class).setParameter("billingRunId", billingRun.getId()).getResultList();
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
            qb.addCriterionEntity("b.billingCycle.id", billingCycle.getId());

            if (startdate != null) {
                qb.addCriterionDateRangeFromTruncatedToDay("nextInvoiceDate", startdate);
            }

            if (endDate != null) {
                qb.addCriterionDateRangeToTruncatedToDay("nextInvoiceDate", endDate, false, false);
            }
            qb.addOrderCriterionAsIs("id", true);
            return qb.getIdQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find billing accounts", ex);
        }

        return null;
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
            isExonerated = ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(customerCategory.getExonerationTaxEl(), "ba", ba);
        }
        return isExonerated;
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
            instantiateDiscountPlan(entity, dp);
        }

        if (!toAdd.isEmpty()) {
            entity.getAllDiscountPlanInstances().addAll(toAdd);
        }

        return entity;
    }

    public void terminateDiscountPlans(BillingAccount entity, List<DiscountPlanInstance> dpis) throws BusinessException {
        if (dpis == null) {
            return;
        }

        for (DiscountPlanInstance dpi : dpis) {
            terminateDiscountPlan(entity, dpi);
        }
    }

    public BillingAccount instantiateDiscountPlan(BillingAccount entity, DiscountPlan dp) throws BusinessException {
        for (UserAccount userAccount : entity.getUsersAccounts()) {
            UserAccount userAccountById = userAccountService.findById(userAccount.getId());
            for (Subscription subscription : userAccountById.getSubscriptions()) {
                for (DiscountPlanInstance discountPlanInstance : subscription.getDiscountPlanInstances()) {
                    if (dp.getCode().equals(discountPlanInstance.getDiscountPlan().getCode())) {
                        throw new BusinessException("DiscountPlan " + dp.getCode() + " is already instantiated in subscription " + subscription.getCode() + ".");
                    }
                }
            }
        }
        return (BillingAccount) discountPlanInstanceService.instantiateDiscountPlan(entity, dp, null);
    }

    public void terminateDiscountPlan(BillingAccount entity, DiscountPlanInstance dpi) throws BusinessException {
        discountPlanInstanceService.terminateDiscountPlan(entity, dpi);
    }

    /**
     * Get a count of billing accounts by a parent customer account
     * 
     * @param parent Parent customer account
     * @return A number of child billing accounts
     */
    public long getCountByParent(CustomerAccount parent) {

        return getEntityManager().createNamedQuery("BillingAccount.getCountByParent", Long.class).setParameter("parent", parent).getSingleResult();
    }
}