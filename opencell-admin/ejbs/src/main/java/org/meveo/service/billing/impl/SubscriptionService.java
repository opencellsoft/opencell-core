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

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotResiliatedOrCanceledException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditableFieldNameEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.Renewal;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.mediation.Access;
import org.meveo.model.order.OrderItemActionEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderHistoryService;
import org.meveo.service.script.offer.OfferModelScriptService;
import org.primefaces.model.SortOrder;


/**
 * @author Edward P. Legaspi
 * @author khalid HORRI
 * @author Mounir BAHIJE
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class SubscriptionService extends BusinessService<Subscription> {

    @Inject
    private OfferModelScriptService offerModelScriptService;

    @EJB
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private AccessService accessService;

    @Inject
    private OrderHistoryService orderHistoryService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    @Inject
    private AuditableFieldService auditableFieldService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @MeveoAudit
    @Override
    public void create(Subscription subscription) throws BusinessException {
        checkSubscriptionPaymentMethod(subscription, subscription.getUserAccount().getBillingAccount().getCustomerAccount().getPaymentMethods());
        updateSubscribedTillAndRenewalNotifyDates(subscription);

        subscription.createAutoRenewDate();

        super.create(subscription);

        // Status audit (to trace the passage from before creation "" to creation "CREATED") need for lifecycle
        auditableFieldService.createFieldHistory(subscription, AuditableFieldNameEnum.STATUS.getFieldName(), AuditChangeTypeEnum.STATUS, "", String.valueOf(subscription.getStatus()));

        // execute subscription script
        OfferTemplate offerTemplate = offerTemplateService.retrieveIfNotManaged(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.subscribe(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode());
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }
    }

    @MeveoAudit
    @Override
    public Subscription update(Subscription subscription) throws BusinessException {

        checkSubscriptionPaymentMethod(subscription, subscription.getUserAccount().getBillingAccount().getCustomerAccount().getPaymentMethods());
        updateSubscribedTillAndRenewalNotifyDates(subscription);

        Subscription subscriptionOld = this.findById(subscription.getId());
        subscription.updateAutoRenewDate(subscriptionOld);

        return super.update(subscription);
    }

    private void checkSubscriptionPaymentMethod(Subscription subscription, List<PaymentMethod> paymentMethods) {
        if (Objects.nonNull(subscription.getPaymentMethod()) && (paymentMethods.isEmpty() || paymentMethods.stream()
                .filter(PaymentMethod::isActive)
                .noneMatch(paymentMethod -> paymentMethod.getId().equals(subscription.getPaymentMethod().getId())))) {
            log.error("the payment method should be reference to an active PaymentMethod defined on the CustomerAccount");
            throw new BusinessException("the payment method should be reference to an active PaymentMethod defined on the CustomerAccount");
        }
    }

    @MeveoAudit
    public Subscription subscriptionCancellation(Subscription subscription, Date cancelationDate) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (cancelationDate == null) {
            cancelationDate = new Date();
        }
        /*
         * List<ServiceInstance> serviceInstances = subscription .getServiceInstances(); for (ServiceInstance serviceInstance : serviceInstances) { if
         * (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) { serviceInstanceService.serviceCancellation(serviceInstance, terminationDate); } }
         */
        subscription.setTerminationDate(cancelationDate);
        subscription.setStatus(SubscriptionStatusEnum.CANCELED);
        subscription = update(subscription);

        return subscription;
    }

    @MeveoAudit
    public Subscription subscriptionSuspension(Subscription subscription, Date suspensionDate) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
        if (suspensionDate == null) {
            suspensionDate = new Date();
        }

        OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.suspendSubscription(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode(), suspensionDate);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus())) {
                serviceInstanceService.serviceSuspension(serviceInstance, suspensionDate);
            }
        }

        subscription.setTerminationDate(suspensionDate);
        subscription.setStatus(SubscriptionStatusEnum.SUSPENDED);
        subscription = update(subscription);
        for (Access access : subscription.getAccessPoints()) {
            accessService.disable(access);
        }

        return subscription;
    }

    @MeveoAudit
    public Subscription subscriptionReactivation(Subscription subscription, Date reactivationDate)
            throws IncorrectSusbcriptionException, ElementNotResiliatedOrCanceledException, IncorrectServiceInstanceException, BusinessException {

        if (reactivationDate == null) {
            reactivationDate = new Date();
        }

        if (subscription.getStatus() != SubscriptionStatusEnum.RESILIATED && subscription.getStatus() != SubscriptionStatusEnum.CANCELED && subscription.getStatus() != SubscriptionStatusEnum.SUSPENDED) {
            throw new ElementNotResiliatedOrCanceledException("subscription", subscription.getCode());
        }

        subscription.setTerminationDate(null);
        subscription.setSubscriptionTerminationReason(null);
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (InstanceStatusEnum.SUSPENDED.equals(serviceInstance.getStatus())) {
                serviceInstanceService.serviceReactivation(serviceInstance, reactivationDate, true, false);
            }
        }

        subscription = update(subscription);

        for (Access access : subscription.getAccessPoints()) {
            accessService.enable(access);
        }

        OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            try {
                offerModelScriptService.reactivateSubscription(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode(), reactivationDate);
            } catch (BusinessException e) {
                log.error("Failed to execute a script {}", offerTemplate.getBusinessOfferModel().getScript().getCode(), e);
            }
        }

        return subscription;
    }

    /**
     * Terminate subscription. If termination date is not provided, a current date will be used. If termination date is a future date, subscription's subscriptionRenewal will be
     * updated with a termination date and a reason.
     *
     * @param subscription Subscription to terminate
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param orderNumber Order number that requested subscription termination
     * @return Updated subscription entity
     * @throws BusinessException General business exception
     */
    @MeveoAudit
    public Subscription terminateSubscription(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber) throws BusinessException {
        return terminateSubscription(subscription, terminationDate, terminationReason, orderNumber, null, null);
    }

    /**
     * Terminate subscription. If termination date is not provided, a current date will be used. If termination date is a future date, subscription's subscriptionRenewal will be
     * updated with a termination date and a reason.
     *
     * @param subscription Subscription to terminate
     * @param terminationDate Termination date
     * @param terminationReason Termination reason
     * @param orderNumber Order number that requested subscription termination
     * @param orderItemId Order item's identifier in the order that requested subscription termination
     * @param orderItemAction Order item's action that requested subscription termination
     * @return Updated subscription entity
     * @throws BusinessException General business exception
     */
    @MeveoAudit
    public Subscription terminateSubscription(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber, Long orderItemId, OrderItemActionEnum orderItemAction)
            throws BusinessException {

        if (terminationDate == null) {
            terminationDate = new Date();
        }
        // point termination date to the end of the day
        Date terminationDateTime = DateUtils.setDateToEndOfDay(terminationDate);

        if (terminationReason == null) {
            throw new ValidationException("Termination reason not provided", "subscription.error.noTerminationReason");

        } else if (subscription.getSubscriptionDate().after(terminationDateTime)) {
            throw new ValidationException("Termination date can not be before the subscription date", "subscription.error.terminationDateBeforeSubscriptionDate");
        }

        // checks if termination date is > now (do not ignore time, as subscription time is time sensitive)
        Date now = new Date();
        if (terminationDateTime.compareTo(now) <= 0) {
            return terminateSubscriptionWithPastDate(subscription, terminationDate, terminationReason, orderNumber, orderItemId, orderItemAction);
        } else {
            // if future date/time set subscription termination
            return terminateSubscriptionWithFutureDate(subscription, terminationDate, terminationReason);
        }
    }

    private Subscription terminateSubscriptionWithFutureDate(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason) throws BusinessException {

        SubscriptionRenewal subscriptionRenewal = subscription.getSubscriptionRenewal();
        subscriptionRenewal.setTerminationReason(PersistenceUtils.initializeAndUnproxy(subscriptionRenewal.getTerminationReason()));
        Renewal renewal = new Renewal(subscriptionRenewal, subscription.getSubscribedTillDate());
        subscription.setInitialSubscriptionRenewal(JacksonUtil.toString(renewal));

        subscription.setSubscribedTillDate(terminationDate);
        subscription.setToValidity(terminationDate);
        subscriptionRenewal.setTerminationReason(terminationReason);
        subscriptionRenewal.setInitialTermType(SubscriptionRenewal.InitialTermTypeEnum.FIXED);
        subscriptionRenewal.setAutoRenew(false);
        subscriptionRenewal.setEndOfTermAction(SubscriptionRenewal.EndOfTermActionEnum.TERMINATE);

        subscription = update(subscription);

        return subscription;
    }

    @MeveoAudit
    private Subscription terminateSubscriptionWithPastDate(Subscription subscription, Date terminationDate, SubscriptionTerminationReason terminationReason, String orderNumber, Long orderItemId,
                                                           OrderItemActionEnum orderItemAction) throws BusinessException {

        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (InstanceStatusEnum.ACTIVE.equals(serviceInstance.getStatus()) || InstanceStatusEnum.SUSPENDED.equals(serviceInstance.getStatus())) {
                serviceInstanceService.terminateService(serviceInstance, terminationDate, terminationReason, orderNumber);
                orderHistoryService.create(orderNumber, orderItemId, serviceInstance, orderItemAction);
            }
        }
        // Apply oneshot charge of type=Other refunding
        if (terminationReason.isReimburseOneshots()) {
            List<OneShotChargeInstance> oneShotChargeInstances = oneShotChargeInstanceService.findOneShotChargeInstancesBySubscriptionId(subscription.getId());
            for (OneShotChargeInstance oneShotChargeInstance : oneShotChargeInstances) {
                if (oneShotChargeInstance.getChargeDate() != null && terminationDate.compareTo(oneShotChargeInstance.getChargeDate()) <= 0) {
                    OneShotChargeTemplate chargeTemplate = (OneShotChargeTemplate) PersistenceUtils.initializeAndUnproxy(oneShotChargeInstance.getChargeTemplate());
                    if (chargeTemplate == null || chargeTemplate.getOneShotChargeTemplateType() == null || !chargeTemplate.getOneShotChargeTemplateType().equals(OneShotChargeTemplateTypeEnum.OTHER)) {
                        continue;
                    }
                    oneShotChargeInstanceService.oneShotChargeApplication(oneShotChargeInstance, terminationDate, oneShotChargeInstance.getQuantity().negate(), orderNumber, ChargeApplicationModeEnum.REIMBURSMENT);
                    oneShotChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);
                    oneShotChargeInstanceService.update(oneShotChargeInstance);
                }

            }

        }


        subscription.setToValidity(terminationDate);
        subscription.setSubscriptionTerminationReason(terminationReason);
        subscription.setTerminationDate(terminationDate);
        subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
        subscription = update(subscription);

        for (Access access : subscription.getAccessPoints()) {
            access.setEndDate(terminationDate);
            accessService.update(access);
        }

        // execute termination script
        OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(subscription.getOffer());
        if (offerTemplate.getBusinessOfferModel() != null && offerTemplate.getBusinessOfferModel().getScript() != null) {
            offerModelScriptService.terminateSubscription(subscription, offerTemplate.getBusinessOfferModel().getScript().getCode(), terminationDate, terminationReason);
        }

        return subscription;
    }

    public boolean hasSubscriptions(OfferTemplate offerTemplate) {
        try {
            QueryBuilder qb = new QueryBuilder(Subscription.class, "s");
            qb.addCriterionEntity("offer", offerTemplate);

            return ((Long) qb.getCountQuery(getEntityManager()).getSingleResult()).longValue() > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    public List<Subscription> listByUserAccount(UserAccount userAccount) {
        return listByUserAccount(userAccount, "code", SortOrder.ASCENDING);
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> listByUserAccount(UserAccount userAccount, String sortBy, SortOrder sortOrder) {
        QueryBuilder qb = new QueryBuilder(Subscription.class, "c");
        qb.addCriterionEntity("userAccount", userAccount);
        boolean ascending = true;
        if (sortOrder != null) {
            ascending = sortOrder.equals(SortOrder.ASCENDING);
        }
        qb.addOrderCriterion(sortBy, ascending);

        try {
            return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list subscription by user account", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> listByCustomer(Customer customer) {
        try {
            return getEntityManager().createNamedQuery("Subscription.listByCustomer").setParameter("customer", customer).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting list subscription by customer", e);
            return null;
        }
    }

    /**
     * Get a list of subscription ids that are about to expire or have expired already
     *
     * @return A list of subscription ids
     */
    public List<Long> getSubscriptionsToRenewOrNotify() {

        return getSubscriptionsToRenewOrNotify(new Date());
    }

    /**
     * Get a list of subscription ids that are about to expire or have expired already
     *
     * @param untillDate the subscription till date
     * @return A list of subscription ids
     */
    public List<Long> getSubscriptionsToRenewOrNotify(Date untillDate) {

        List<Long> ids = getEntityManager().createNamedQuery("Subscription.getExpired", Long.class).setParameter("date", untillDate)
                .setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED)).getResultList();
        ids.addAll(getEntityManager().createNamedQuery("Subscription.getToNotifyExpiration", Long.class).setParameter("date", untillDate)
                .setParameter("statuses", Arrays.asList(SubscriptionStatusEnum.ACTIVE, SubscriptionStatusEnum.CREATED)).getResultList());

        return ids;
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInstance> listBySubscription(Subscription subscription) {
        QueryBuilder qb = new QueryBuilder(ServiceInstance.class, "c");
        qb.addCriterionEntity("subscription", subscription);

        try {
            return (List<ServiceInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("error while getting user account list by billing account", e);
            return null;
        }
    }

    public void activateInstantiatedService(Subscription sub) throws BusinessException {
        // using a new ArrayList (cloning the original one) to avoid ConcurrentModificationException
        for (ServiceInstance si : new ArrayList<>(emptyIfNull(sub.getServiceInstances()))) {
            if (si.getStatus().equals(InstanceStatusEnum.INACTIVE)) {
                serviceInstanceService.serviceActivation(si);
            }
        }
    }

    /**
     * Return all subscriptions with status not equal to CREATED or ACTIVE and initialAgreement date more than n years old
     *
     * @param nYear age of the subscription
     * @return Filtered list of subscriptions
     */
    @SuppressWarnings("unchecked")
    public List<Subscription> listInactiveSubscriptions(int nYear) {
        QueryBuilder qb = new QueryBuilder(Subscription.class, "e");
        Date higherBound = DateUtils.addYearsToDate(new Date(), -1 * nYear);

        qb.addCriterionDateRangeToTruncatedToDay("subscriptionDate", higherBound, true, false);
        qb.addCriterionEnum("status", SubscriptionStatusEnum.CREATED, "<>", false);
        qb.addCriterionEnum("status", SubscriptionStatusEnum.ACTIVE, "<>", false);

        return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
    }

    public void cancelSubscriptionRenewal(Subscription entity) throws BusinessException {
        entity.setSubscribedTillDate(null);
        entity.setSubscriptionTerminationReason(null);
        entity.getSubscriptionRenewal().setInitialyActiveFor(null);
        entity.setSubscriptionRenewal(new SubscriptionRenewal());
    }

    /**
     * Subscription balance due.
     *
     * @param subscription the Subscription
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal subscriptionBalanceDue(Subscription subscription, Date to) throws BusinessException {
        return computeBalance(subscription, to, true, MatchingStatusEnum.O, MatchingStatusEnum.P, MatchingStatusEnum.I);
    }

    /**
     * Subscription balance exigible without litigation.
     *
     * @param subscription the Subscription
     * @param to the to
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    public BigDecimal subscriptionBalanceExigibleWithoutLitigation(Subscription subscription, Date to) throws BusinessException {
        return computeBalance(subscription, to, true, MatchingStatusEnum.O, MatchingStatusEnum.P);
    }

    /**
     * Compute balance.
     *
     * @param subscription the Subscription
     * @param to the to
     * @param isDue the is due
     * @param status the status
     * @return the big decimal
     * @throws BusinessException the business exception
     */
    private BigDecimal computeBalance(Subscription subscription, Date to, boolean isDue, MatchingStatusEnum... status) throws BusinessException {
        return computeBalance(subscription, to, false, isDue, status);
    }

    /**
     * Computes a balance given a subscription. to and isDue parameters are ignored when isFuture is true.
     *
     * @param subscription of the customer
     * @param to compare the invoice due or transaction date here
     * @param isFuture includes the future due or transaction date
     * @param isDue if true filter via dueDate else transactionDate
     * @param status can be a list of MatchingStatusEnum
     * @return the computed balance
     * @throws BusinessException when an error in computation is encoutered
     */
    private BigDecimal computeBalance(Subscription subscription, Date to, boolean isFuture, boolean isDue, MatchingStatusEnum... status) throws BusinessException {
        log.trace("start computeBalance subscription:{}, toDate:{}, isDue:{}", (subscription == null ? "null" : subscription.getCode()), to, isDue);
        if (subscription == null) {
            log.warn("Error when subscription is null!");
            throw new BusinessException("subscription is null");
        }
        if (!isFuture && to == null) {
            log.warn("Error when toDate is null!");
            throw new BusinessException("toDate is null");
        }
        BigDecimal balance = null, balanceDebit = null, balanceCredit = null;
        try {
            balanceDebit = computeOccAmount(subscription, OperationCategoryEnum.DEBIT, isFuture, isDue, to, status);
            balanceCredit = computeOccAmount(subscription, OperationCategoryEnum.CREDIT, isFuture, isDue, to, status);
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
            log.debug("computeBalance subscription code:{} , balance:{}", subscription.getCode(), balance);
        } catch (Exception e) {
            throw new BusinessException("Internal error");
        }
        return balance;

    }

    /**
     * Compute occ amount.
     *
     * @param subscription the Subscription
     * @param operationCategoryEnum the operation category enum
     * @param isFuture the is future
     * @param isDue the is due
     * @param to the to
     * @param status the status
     * @return the big decimal
     * @throws Exception the exception
     */
    private BigDecimal computeOccAmount(Subscription subscription, OperationCategoryEnum operationCategoryEnum, boolean isFuture, boolean isDue, Date to, MatchingStatusEnum... status) throws Exception {
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

        queryBuilder.addCriterionEntity("subscription", subscription);
        if (status.length == 1) {
            queryBuilder.addCriterionEnum("matchingStatus", status[0]);
        } else {
            queryBuilder.startOrClause();
            for (MatchingStatusEnum st : status) {
                queryBuilder.addCriterionEnum("matchingStatus", st);
            }
            queryBuilder.endOrClause();
        }
        Query query = queryBuilder.getQuery(getEntityManager());
        balance = (BigDecimal) query.getSingleResult();
        return balance;
    }

    /**
     * Returns all subscriptions to the given offer by code
     *
     * @param offerCode code of the Offer to search
     * @param sortBy sort criteria
     * @param sortOrder sort order
     * @return list of Subscription
     */
    @SuppressWarnings("unchecked")
    public List<Subscription> listByOffer(String offerCode, String sortBy, SortOrder sortOrder) {
        QueryBuilder qb = new QueryBuilder(Subscription.class, "c");
        qb.addCriterionEntity("offer.code", offerCode);

        boolean ascending = true;
        if (sortOrder != null) {
            ascending = sortOrder.equals(SortOrder.ASCENDING);
        }
        qb.addOrderCriterion(sortBy, ascending);

        try {
            return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.trace("No subscription found for offer code " + offerCode, e);
            return null;
        }
    }

    public Subscription instantiateDiscountPlan(Subscription entity, DiscountPlan dp) throws BusinessException {
        if (!entity.getOffer().getAllowedDiscountPlans().contains(dp)) {
            throw new BusinessException("DiscountPlan " + dp.getCode() + " is not allowed in this offer.");
        }
        BillingAccount billingAccount = entity.getUserAccount().getBillingAccount();
        for (DiscountPlanInstance discountPlanInstance : billingAccount.getDiscountPlanInstances()) {
            if (dp.getCode().equals(discountPlanInstance.getDiscountPlan().getCode())) {
                throw new BusinessException("DiscountPlan " + dp.getCode() + " is already instantiated in Billing Account " + billingAccount.getCode() + ".");
            }
        }
        return (Subscription) discountPlanInstanceService.instantiateDiscountPlan(entity, dp, null);
    }

    public void terminateDiscountPlan(Subscription entity, DiscountPlanInstance dpi) throws BusinessException {
        discountPlanInstanceService.terminateDiscountPlan(entity, dpi);
    }

    /**
     * check if the subscription will be terminated in future
     *
     * @param subscription the subscription
     * @return true is the subscription will be terminated in future.
     */
    public boolean willBeTerminatedInFuture(Subscription subscription) {
        SubscriptionRenewal subscriptionRenewal = subscription != null ? subscription.getSubscriptionRenewal() : null;
        return (subscription != null && (subscription.getStatus() == SubscriptionStatusEnum.CREATED || subscription.getStatus() == SubscriptionStatusEnum.ACTIVE) && subscription.getSubscribedTillDate() != null
                && subscription.getSubscribedTillDate().compareTo(new Date()) > 0 && subscriptionRenewal != null && !subscriptionRenewal.isAutoRenew() && subscriptionRenewal.getTerminationReason() != null
                && subscriptionRenewal.getEndOfTermAction() == SubscriptionRenewal.EndOfTermActionEnum.TERMINATE);
    }

    /**
     * cancel subscription termination
     *
     * @param subscription the subscription
     * @throws BusinessException business exception
     */
    public void cancelSubscriptionTermination(Subscription subscription) throws BusinessException {
        SubscriptionRenewal subscriptionRenewal = null;
        Date subscribedTillDate = null;

        String initialRenewal = subscription.getInitialSubscriptionRenewal();
        if (!StringUtils.isBlank(initialRenewal)) {

            Renewal renewal = JacksonUtil.fromString(initialRenewal, Renewal.class);
            subscriptionRenewal = renewal.getValue();
            subscriptionRenewal.setTerminationReason(subscriptionRenewal.getTerminationReason() != null && subscriptionRenewal.getTerminationReason().getId() != null ? subscriptionRenewal.getTerminationReason() : null);
            subscribedTillDate = renewal.getSubscribedTillDate();

        }
        subscription.setSubscriptionRenewal(subscriptionRenewal);
        subscription.setSubscribedTillDate(subscribedTillDate);
        update(subscription);
    }

    /**
     * check compatibility of services before instantiation
     *
     * @param subscription
     * @param selectedItemsAsList
     * @throws BusinessException
     */
    public void checkCompatibilityOfferServices(Subscription subscription, List<ServiceTemplate> selectedItemsAsList) throws BusinessException {

        if (subscription == null) {
            throw new BusinessException("subscription is Null in checkCompatibilityOfferServices ");
        }
        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        OfferTemplate offerTemplate = subscription.getOffer();

        // loop in selected Available services for subscription
        for (ServiceTemplate serviceTemplate : selectedItemsAsList) {
            OfferServiceTemplate offerServiceTemplate = getOfferServiceTemplate(serviceTemplate.getCode(), offerTemplate);
            if (offerServiceTemplate == null) {
                throw new BusinessException("No offerServiceTemplate corresponds to " + serviceTemplate.getCode());
            }

            // list of incompatible services of an element of current Available services selected
            List<ServiceTemplate> serviceTemplateIncompatibles = offerServiceTemplate.getIncompatibleServices();

            // check if other selected Available services are part of incompatible services
            for (ServiceTemplate serviceTemplateOther : selectedItemsAsList) {
                if (!serviceTemplateOther.getCode().equals(serviceTemplate.getCode())) {
                    for (ServiceTemplate serviceTemplateIncompatible : serviceTemplateIncompatibles) {
                        if (serviceTemplateOther.getCode().equals(serviceTemplateIncompatible.getCode())) {
                            throw new BusinessException("Services Incompatibility between " + serviceTemplateIncompatible.getCode() + " and " + serviceTemplate.getCode());
                        }
                    }
                }
            }

            // check if subscribed service's are part of incompatible services of selected available services
            for (ServiceInstance subscribedService : serviceInstances) {
                for (ServiceTemplate serviceTemplateIncompatible : serviceTemplateIncompatibles) {
                    if (subscribedService.getCode().equals(serviceTemplateIncompatible.getCode())) {
                        throw new BusinessException("Services Incompatibility between " + serviceTemplateIncompatible.getCode() + " and " + serviceTemplate.getCode());
                    }
                }
            }
        }

        // check if selected available services are part of incompatible services of subscribed service's
        for (ServiceInstance subscribedService : serviceInstances) {
            OfferServiceTemplate offerServiceTemplateSubscribedService = getOfferServiceTemplate(subscribedService.getServiceTemplate().getCode(), offerTemplate);
            // list of incompatible services of an element of current subscribed service's
            List<ServiceTemplate> serviceTemplateSubscribedServiceIncompatibles = offerServiceTemplateSubscribedService.getIncompatibleServices();

            for (ServiceTemplate serviceTemplateSelectedItem : selectedItemsAsList) {
                for (ServiceTemplate serviceTemplateSubscribedServiceIncompatible : serviceTemplateSubscribedServiceIncompatibles) {
                    if (serviceTemplateSelectedItem.getCode().equals(serviceTemplateSubscribedServiceIncompatible.getCode())) {
                        throw new BusinessException("Services Incompatibility between " + serviceTemplateSelectedItem.getCode() + " and " + subscribedService.getCode());
                    }
                }
            }
        }
    }

    /**
     * Get OfferServiceTemplate which corresponds to serviceCode and offerTemplate
     *
     * @param serviceCode
     * @param offerTemplate
     * @return offerServiceTemplate
     */
    public OfferServiceTemplate getOfferServiceTemplate(String serviceCode, OfferTemplate offerTemplate) {
        OfferServiceTemplate offerServiceTemplateResult = null;
        List<OfferServiceTemplate> offerServiceTemplates = offerTemplate.getOfferServiceTemplates();
        for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
            List<ServiceTemplate> serviceTemplates = offerServiceTemplate.getIncompatibleServices();
            if (serviceCode.equals(offerServiceTemplate.getServiceTemplate().getCode())) {
                offerServiceTemplateResult = offerServiceTemplate;
            }
        }
        return offerServiceTemplateResult;
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> findSubscriptions(BillingCycle billingCycle, Date startdate, Date endDate) {
        try {
            QueryBuilder qb = new QueryBuilder(Subscription.class, "s", null);
            qb.addCriterionEntity("s.billingCycle.id", billingCycle.getId());
            qb.addOrderCriterionAsIs("id", true);

            return (List<Subscription>) qb.getQuery(getEntityManager()).getResultList();
        } catch (Exception ex) {
            log.error("failed to find subscriptions", ex);
        }

        return null;
    }

    /**
     * List subscriptions that are associated with a given billing run
     *
     * @param billingRun Billing run
     * @return A list of Subscriptions
     */
    public List<Subscription> findSubscriptions(BillingRun billingRun) {
        return getEntityManager().createNamedQuery("Subscription.listByBillingRun", Subscription.class).setParameter("billingRunId", billingRun.getId()).getResultList();
    }


    /**
     * Update subscribedTillDate field in subscription while it was not renewed yet. Also calculate Notify of renewal date
     */
    public void updateSubscribedTillAndRenewalNotifyDates(Subscription subscription) {
        if (subscription.isRenewed()) {
            return;
        }
        if (subscription.getSubscriptionRenewal().getInitialTermType().equals(SubscriptionRenewal.InitialTermTypeEnum.RECURRING)) {
            if (subscription.getSubscriptionDate() != null && subscription.getSubscriptionRenewal() != null && subscription.getSubscriptionRenewal().getInitialyActiveFor() != null) {
                if (subscription.getSubscriptionRenewal().getInitialyActiveForUnit() == null) {
                    subscription.getSubscriptionRenewal().setInitialyActiveForUnit(RenewalPeriodUnitEnum.MONTH);
                }
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(subscription.getSubscriptionDate());
                calendar.add(subscription.getSubscriptionRenewal().getInitialyActiveForUnit().getCalendarField(), subscription.getSubscriptionRenewal().getInitialyActiveFor());
                subscription.setSubscribedTillDate(calendar.getTime());

            } else {
                subscription.setSubscribedTillDate(null);
            }
        } else if (subscription.getSubscriptionRenewal().getInitialTermType().equals(SubscriptionRenewal.InitialTermTypeEnum.CALENDAR)) {
            if (subscription.getSubscriptionDate() != null && subscription.getSubscriptionRenewal() != null && subscription.getSubscriptionRenewal().getCalendarInitialyActiveFor() != null) {
                org.meveo.model.catalog.Calendar calendar = CalendarService.initializeCalendar(subscription.getSubscriptionRenewal().getCalendarInitialyActiveFor(), subscription.getSubscriptionDate(), subscription);
                Date date = calendar.nextCalendarDate(subscription.getSubscriptionDate());
                subscription.setSubscribedTillDate(date);
            } else {
                subscription.setSubscribedTillDate(null);
            }
        }

        if (subscription.getSubscribedTillDate() != null && subscription.getSubscriptionRenewal().isAutoRenew() && subscription.getSubscriptionRenewal().getDaysNotifyRenewal() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(subscription.getSubscribedTillDate());
            calendar.add(Calendar.DAY_OF_MONTH, subscription.getSubscriptionRenewal().getDaysNotifyRenewal() * (-1));
            subscription.setNotifyOfRenewalDate(calendar.getTime());
        } else {
            subscription.setNotifyOfRenewalDate(null);
        }
        subscription.autoUpdateEndOfEngagementDate();
    }

    public Subscription findByCodeAndValidityDate(String subscriptionCode, Date date) {
        if(date == null)
            return findByCode(subscriptionCode);

        List<Subscription> subscriptions = getEntityManager().createNamedQuery("Subscription.findByValidity", Subscription.class)
                .setParameter("code", subscriptionCode.toLowerCase())
                .setParameter("validityDate", date)
                .getResultList();

        return getActiveOrLastUpdated(subscriptions);
    }

    private Subscription getActiveOrLastUpdated(List<Subscription> subscriptions) {
        if(subscriptions.isEmpty())
            return null;

        Optional<Subscription> activeSubscription = subscriptions.stream()
                .filter(s -> SubscriptionStatusEnum.ACTIVE.equals(s.getStatus()))
                .findFirst();

        return activeSubscription.orElseGet(() -> subscriptions.stream()
                .sorted(Comparator.comparing(this::getUpdated).reversed())
                .collect(Collectors.toList())
                .get(0));
    }

    private Date getUpdated(Subscription subscription) {
        return subscription.getAuditable().getUpdated() != null ? subscription.getAuditable().getUpdated() : subscription.getAuditable().getCreated();
    }

    @Override
    public Subscription findByCode(String code) {
        List<Subscription> subscriptions = findListByCode(code);
        return getActiveOrLastUpdated(subscriptions);
    }

    public List<Subscription> findListByCode(String code) {
        TypedQuery<Subscription> query = getEntityManager().createQuery("select be from " + entityClass.getSimpleName() + " be where lower(code)=:code", entityClass)
                .setParameter("code", code.toLowerCase());
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.debug("No {} of code {} found", getEntityClass().getSimpleName(), code);
            return new ArrayList<>();
        }


    }
}