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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.DatePeriod;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RatingStatus;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.script.revenue.RevenueRecognitionScriptService;

/**
 * RecurringChargeInstanceService
 *
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @author khalid HORRI
 * @lastModifiedVersion 6.1
 */
@Stateless
public class RecurringChargeInstanceService extends BusinessService<RecurringChargeInstance> {

    @Inject
    private WalletService walletService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RevenueRecognitionScriptService revenueRecognitionScriptService;

    @Inject
    @Rejected
    Event<Serializable> rejectededChargeProducer;

    @Inject
    private CounterInstanceService counterInstanceService;

    @EJB
    private RecurringChargeInstanceService recurringChargeInstanceServiceNewTx;

    public RecurringChargeInstance findByCodeAndService(String code, Long serviceInstanceId) {
        RecurringChargeInstance chargeInstance = null;
        try {

            QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
            qb.addCriterion("c.code", "=", code, true);
            qb.addCriterion("c.serviceInstance.id", "=", serviceInstanceId, true);
            chargeInstance = (RecurringChargeInstance) qb.getQuery(getEntityManager()).getSingleResult();

        } catch (NoResultException nre) {
            log.trace("No recurring charges by code {} on service instance {} found", code, serviceInstanceId);
        }
        return chargeInstance;
    }

    public List<Long> findIdsByStatusAndSubscriptionId(InstanceStatusEnum status, Date maxChargeDate, Long subscriptionId) {

        QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
        qb.addCriterionEnum("c.status", status);
        qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate, false, false);
        qb.addValueIsEqualToField("c.subscription.id", subscriptionId, false,  false);

        List<Long> ids = qb.getIdQuery(getEntityManager()).getResultList();
        log.trace("Found recurring charges by status {} and subscriptionId {} . Result size found={}.", status, subscriptionId, (ids != null ? ids.size() : "NULL"));

        return ids;
    }

    /**
     * Find recurring charge instances to rate
     * 
     * @param status Status to match
     * @param maxChargeDate Date to rate to. chargeInstance.nextChargeDate must be less than a given date.
     * @param billingCycles Limit to accounts with a given billing cycles
     * @return A list of recurring charge instance IDs to rate
     */
    @SuppressWarnings("unchecked")
    public List<Long> findRecurringChargeInstancesToRate(InstanceStatusEnum status, Date maxChargeDate, List<BillingCycle> billingCycles) {

        List<Long> ids = null;
        if (billingCycles == null || billingCycles.isEmpty()) {
            ids = getEntityManager().createNamedQuery("RecurringChargeInstance.listToRateByStatusAndDate").setParameter("status", status).setParameter("maxNextChargeDate", DateUtils.truncateTime(maxChargeDate))
                .getResultList();
            log.trace("Found {} recurring charges of status {} to rate up to {}.", (ids != null ? ids.size() : "0"), status, DateUtils.formatAsDate(maxChargeDate));

        } else {
            ids = getEntityManager().createNamedQuery("RecurringChargeInstance.listToRateByStatusBCAndDate").setParameter("status", status).setParameter("maxNextChargeDate", DateUtils.truncateTime(maxChargeDate))
                .setParameter("billingCycles", billingCycles).getResultList();
            log.trace("Found {} recurring charges of status {} and billing cycles {} to rate up to {}.", (ids != null ? ids.size() : "0"), status, billingCycles, DateUtils.formatAsDate(maxChargeDate));
        }

        return ids;
    }

    @SuppressWarnings("unchecked")
    public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(Long subscriptionId) {
        QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c", Arrays.asList("chargeTemplate"));
        qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
        return qb.getQuery(getEntityManager()).getResultList();
    }

    public RecurringChargeInstance recurringChargeInstanciation(ServiceInstance serviceInstance, ServiceChargeTemplateRecurring serviceChargeTemplateRecurring, boolean isVirtual) throws BusinessException {

        if (serviceInstance == null) {
            throw new BusinessException("service instance does not exist.");
        }

        if (serviceInstance.getStatus() == InstanceStatusEnum.CANCELED || serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED || serviceInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
            throw new BusinessException("service instance is " + serviceInstance.getStatus() + ". code=" + serviceInstance.getCode());
        }

        if (serviceChargeTemplateRecurring == null) {
            throw new BusinessException("service charge template recurring does not exist.");
        }

        RecurringChargeTemplate recurringChargeTemplate = serviceChargeTemplateRecurring.getChargeTemplate();
        String chargeCode = recurringChargeTemplate.getCode();

        if (!isVirtual) {
            RecurringChargeInstance recurringChargeInstance = (RecurringChargeInstance) findByCodeAndService(chargeCode, serviceInstance.getId());
            if (recurringChargeInstance != null) {
                throw new BusinessException("charge instance code already exists. code=" + chargeCode + " service instance id " + serviceInstance.getId());
            }
        }

        log.debug("Instanciate a recurring charge for code {} on subscription {}", chargeCode, serviceInstance.getSubscription().getCode());

        RecurringChargeInstance chargeInstance = new RecurringChargeInstance(null, null, recurringChargeTemplate, serviceInstance, InstanceStatusEnum.INACTIVE, recurringChargeTemplate.getCalendar(),
            recurringChargeTemplate.getApplyInAdvance());

        ServiceChargeTemplateRecurring recChTmplServ = serviceInstance.getServiceTemplate().getServiceRecurringChargeByChargeCode(chargeCode);
        // getEntityManager().merge(recChTmplServ); - does not make sence as
        // merge result is what shoudl be used
        List<WalletTemplate> walletTemplates = recChTmplServ.getWalletTemplates();

        if (walletTemplates != null && walletTemplates.size() > 0) {
            for (WalletTemplate walletTemplate : walletTemplates) {
                if (walletTemplate == null) {
                    continue;
                }
                if (walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                    // One walletTemplate is prepaid, we set the chargeInstance as being prepaid
                    chargeInstance.setPrepaid(true);
                }

                WalletInstance walletInstance = walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(), walletTemplate, isVirtual);
                log.debug("Added the wallet instance {} to the chargeInstance {}", walletInstance.getId(), chargeInstance.getId());
                chargeInstance.getWalletInstances().add(walletInstance);
            }
        } else {
            chargeInstance.setPrepaid(false);
            chargeInstance.getWalletInstances().add(serviceInstance.getSubscription().getUserAccount().getWallet());
        }

        if (!isVirtual) {
            create(chargeInstance);
        }

        if ((serviceChargeTemplateRecurring.getAccumulatorCounterTemplates() != null && !serviceChargeTemplateRecurring.getAccumulatorCounterTemplates().isEmpty())
                || serviceChargeTemplateRecurring.getCounterTemplate() != null) {
            for (CounterTemplate counterTemplate : serviceChargeTemplateRecurring.getAccumulatorCounterTemplates()) {
                CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, counterTemplate, isVirtual);
                log.debug("Accumulator counter instance {} will be added to charge instance {}", counterInstance, chargeInstance);
                chargeInstance.addCounterInstance(counterInstance);
            }
            CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, serviceChargeTemplateRecurring.getCounterTemplate(), isVirtual);
            log.debug("Counter instance {} will be added to charge instance {}", counterInstance, chargeInstance);
            chargeInstance.setCounter(counterInstance);
            if (!isVirtual) {
                update(chargeInstance);
            }
        }

        return chargeInstance;
    }

    public void recurringChargeDeactivation(long recurringChargeInstanId, Date terminationDate) throws BusinessException {

        RecurringChargeInstance recurringChargeInstance = findById(recurringChargeInstanId, true);

        log.debug("recurringChargeDeactivation : recurringChargeInstanceId={}", recurringChargeInstance.getId());

        recurringChargeInstance.setStatus(InstanceStatusEnum.TERMINATED);

        // chargeApplicationService.cancelChargeApplications(recurringChargeInstanId,
        // null);

        update(recurringChargeInstance);

    }

    public void recurringChargeSuspension(long recurringChargeInstanId, Date terminationDate) throws BusinessException {

        RecurringChargeInstance recurringChargeInstance = findById(recurringChargeInstanId, true);

        log.debug("recurringChargeSuspension : recurringChargeInstanceId={}", recurringChargeInstance.getId());

        recurringChargeInstance.setStatus(InstanceStatusEnum.SUSPENDED);
        update(recurringChargeInstance);

    }

    public void recurringChargeReactivation(ServiceInstance serviceInst, Subscription subscription, Date subscriptionDate) throws BusinessException {
        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new BusinessException("subscription is " + subscription.getStatus());
        }
        if (serviceInst.getStatus() == InstanceStatusEnum.TERMINATED || serviceInst.getStatus() == InstanceStatusEnum.CANCELED || serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
            throw new BusinessException("service instance is " + subscription.getStatus() + ". service Code=" + serviceInst.getCode() + ",subscription Code" + subscription.getCode());
        }
        for (RecurringChargeInstance recurringChargeInstance : serviceInst.getRecurringChargeInstances()) {
            recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            // recurringChargeInstance.setSubscriptionDate(subscriptionDate);
            recurringChargeInstance.setTerminationDate(null);
            recurringChargeInstance.setChargeDate(subscriptionDate);
            update(recurringChargeInstance);
        }

    }

    /**
     * Rate recurring charge up to a given date
     * 
     * @param recurringChargeInstance Recurring charge instance
     * @param maxDate Date to rate until. Only full periods will be considered.
     * @param isMaxDateInclusive If true, <maxDate check is applied for iteration. If false <=maxDate is applied
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param applicationMode Charge application mode. Optional. Defaults to Subscription if not specified.
     * @return A list of wallet operations created
     * @throws BusinessException General business exception
     */
    public List<WalletOperation> applyRecurringCharge(RecurringChargeInstance recurringChargeInstance, Date maxDate, boolean isMaxDateInclusive, boolean isVirtual, ChargeApplicationModeEnum applicationMode)
            throws BusinessException {

        if (applicationMode == null) {
            applicationMode = ChargeApplicationModeEnum.SUBSCRIPTION;
        }

        List<WalletOperation> walletOperations = new ArrayList<WalletOperation>();
        boolean chargeWasUpdated = false;
        boolean isApplyInAdvance = walletOperationService.isApplyInAdvance(recurringChargeInstance);

        // MIGRATE for pre-v.10.0.0 cases when chargedToDate is not set yet
        // Determine a date until which a charge was rated.
        // For Apply in advance cases it is the nextChargeDate field. For rating at the end of recurring period it is chargeDate field
        if (recurringChargeInstance.getChargeDate() != null && recurringChargeInstance.getChargedToDate() == null) {// This check will consider old charges only
            recurringChargeInstance.setChargedToDate(isApplyInAdvance ? recurringChargeInstance.getNextChargeDate() : recurringChargeInstance.getChargeDate());
            chargeWasUpdated = true;
        }

        try {
            // Need to initialize charge if it is the first time that recurring charge is being applied
            // It is considered as first time application when both dates are null. NextChargeDate check is not sufficient as if recurring calendar is period based, end date will
            // be calculated as null when calendar periods reach the end
            if (recurringChargeInstance.getChargeDate() == null && recurringChargeInstance.getNextChargeDate() == null) {

                DatePeriod period = walletOperationService.getRecurringPeriod(recurringChargeInstance, recurringChargeInstance.getSubscriptionDate());

                Date nextChargeDate = period.getTo();
                if (nextChargeDate == null) {
                    throw new BusinessException(
                        "Can not determine a next charge period for charge instance " + recurringChargeInstance.getId() + " and subscription date " + recurringChargeInstance.getSubscriptionDate());
                }
                recurringChargeInstance.setChargeDate(recurringChargeInstance.getSubscriptionDate());
                recurringChargeInstance.setNextChargeDate(isApplyInAdvance && !recurringChargeInstance.getSubscriptionDate().after(period.getFrom()) ? recurringChargeInstance.getSubscriptionDate() : nextChargeDate);
                chargeWasUpdated = true;

                log.debug("Initializing recurring charge nextChargeDate to {} for chargeInstance id {} chargeCode {}, quantity {}, subscriptionDate {}", recurringChargeInstance.getNextChargeDate(),
                    recurringChargeInstance.getId(), recurringChargeInstance.getCode(), recurringChargeInstance.getQuantity(), recurringChargeInstance.getSubscriptionDate());
            }

            int maxRecurringRatingHistory = Integer.parseInt(paramBeanFactory.getInstance().getProperty("rating.recurringMaxRetry", "100"));

            // If we recognize revenue we first delete all SCHEDULED wallet operations
            if (!isVirtual && appProvider.isRecognizeRevenue()) {

                try {
                    log.debug("Delete scheduled charge applications on chargeInstance {}", recurringChargeInstance.getId());
                    getEntityManager().createNamedQuery("WalletOperation.deleteScheduled").setParameter("chargeInstance", recurringChargeInstance).executeUpdate();
                } catch (Exception e) {
                    log.error("Failed to delete scheduled charges applications on chargeInstance {}", recurringChargeInstance.getId(), e);
                }
            }

            Date nextChargeToDate = isApplyInAdvance ? recurringChargeInstance.getChargeDate() : recurringChargeInstance.getNextChargeDate();

            log.debug("Will apply recurring charge {} for missing periods {} - {} {}", recurringChargeInstance.getId(), nextChargeToDate, maxDate, isMaxDateInclusive ? "inclusive" : "exclusive");
            int i = 0;
            while (nextChargeToDate != null && i < maxRecurringRatingHistory
                    && ((nextChargeToDate.getTime() <= maxDate.getTime() && isMaxDateInclusive) || (nextChargeToDate.getTime() < maxDate.getTime() && !isMaxDateInclusive))) {

                List<WalletOperation> wos = walletOperationService.applyReccuringCharge(recurringChargeInstance, applicationMode, false, null, null, isVirtual);
                walletOperations.addAll(wos);

                nextChargeToDate = recurringChargeInstance.getNextChargeDate();
                i++;

            }
            if (!isVirtual && (chargeWasUpdated || !walletOperations.isEmpty())) {
                recurringChargeInstance = updateNoCheck(recurringChargeInstance);
            }

            // If we recognize revenue we create SCHEDULED wallet op until the
            // end of the contract
            if (!isVirtual && appProvider.isRecognizeRevenue() && !recurringChargeInstance.getPrepaid()) {
                Date endContractDate = recurringChargeInstance.getSubscription().getEndAgreementDate();
                log.debug("apply scheduled charges until {}", endContractDate);
                if (endContractDate == null) {
                    log.error("error while trying to schedule revenue for chargeInstance {}," + " the subscription has no end agreeement date", recurringChargeInstance.getId());
                } else {
                    Date chargeDate = recurringChargeInstance.getChargeDate();
                    Date nextChargeDate = recurringChargeInstance.getNextChargeDate();
                    while (nextChargeToDate != null && nextChargeToDate.getTime() <= endContractDate.getTime()) {
                        log.debug("Schedule applicationDate={}", nextChargeToDate);
                        nextChargeToDate = DateUtils.setTimeToZero(nextChargeToDate);
                        walletOperationService.applyReccuringCharge(recurringChargeInstance, ChargeApplicationModeEnum.SUBSCRIPTION, true, null, null, false);

                        log.debug("chargeDate {},nextChargeDate {}", recurringChargeInstance.getChargeDate(), recurringChargeInstance.getNextChargeDate());
                        nextChargeToDate = recurringChargeInstance.getNextChargeDate();

                    }
                    // restore back the values
                    recurringChargeInstance.setChargeDate(chargeDate);
                    recurringChargeInstance.setNextChargeDate(nextChargeDate);
                }
                if (recurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule() != null && recurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule().getScript() != null) {
                    revenueRecognitionScriptService.createRevenueSchedule(recurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule().getScript().getCode(), recurringChargeInstance);
                }
            }

            return walletOperations;

        } catch (Exception e) {
            rejectededChargeProducer.fire("RecurringCharge rejected " + recurringChargeInstance.getId());
            throw e;
        }

    }

    /**
     * Rate recurring charge up to a given date
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param maxDate Date to rate until. Only full periods will be considered.
     * @param isMaxDateInclusive If true, <maxDate check is applied for iteration. If false <=maxDate is applied
     * @return Rating status summary
     * @throws BusinessException General business exception
     */
    public RatingStatus applyRecurringCharge(Long chargeInstanceId, Date maxDate, boolean isMaxDateInclusive) throws BusinessException {

        RecurringChargeInstance chargeInstance = findById(chargeInstanceId);
        List<WalletOperation> wos = applyRecurringCharge(chargeInstance, maxDate, isMaxDateInclusive, false, null);

        RatingStatus ratingStatus = new RatingStatus();
        if (!wos.isEmpty()) {
            ratingStatus.setNbRating(1);
        }
        return ratingStatus;
    }

    /**
     * Rate recurring charge up to a given date
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param maxDate Date to rate until. Only full periods will be considered.
     * @param isMaxDateInclusive If true, <maxDate check is applied for iteration. If false <=maxDate is applied
     * @param applicationMode Charge application mode. Optional. Defaults to Subscription if not specified.
     * @return Rating status summary
     * @throws BusinessException General business exception
     */
    public RatingStatus applyRecurringCharge(Long chargeInstanceId, Date maxDate, boolean isMaxDateInclusive, ChargeApplicationModeEnum applicationMode) throws BusinessException {

        RecurringChargeInstance chargeInstance = findById(chargeInstanceId);
        List<WalletOperation> wos = applyRecurringCharge(chargeInstance, maxDate, isMaxDateInclusive, false, applicationMode);

        RatingStatus ratingStatus = new RatingStatus();
        if (!wos.isEmpty()) {
            ratingStatus.setNbRating(1);
        }
        return ratingStatus;
    }

    /**
     * Rate recurring charge up to a given date in a new transaction
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param maxDate Date to rate until. Only full periods will be considered.
     * @param isMaxDateInclusive If true, <maxDate check is applied for iteration. If false <=maxDate is applied
     * @return Rating status summary
     * @throws BusinessException General business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RatingStatus applyRecurringChargeInNewTx(Long chargeInstanceId, Date maxDate, boolean isMaxDateInclusive, ChargeApplicationModeEnum applicationMode) throws BusinessException {

        return applyRecurringCharge(chargeInstanceId, maxDate, isMaxDateInclusive, applicationMode);
    }

    /**
     * Reimburse already applied recurring charges
     * 
     * @param chargeInstance Recurring charge instance
     * @param orderNumber Order number
     * @return Updated Recurring charge instance
     * @throws BusinessException Business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RecurringChargeInstance reimburseRecuringCharges(RecurringChargeInstance chargeInstance, String orderNumber) throws BusinessException, RatingException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        boolean chargeWasUpdated = true;

        // MIGRATE for pre-v.10.0.0 cases when chargedToDate is not set yet
        // Determine a date until which a charge was rated.
        // For Apply in advance cases it is the nextChargeDate field. For rating at the end of recurring period it is chargeDate field
        if (chargeInstance.getChargeDate() != null && chargeInstance.getChargedToDate() == null) {// This check will consider old charges only
            boolean isApplyInAdvance = walletOperationService.isApplyInAdvance(chargeInstance);
            chargeInstance.setChargedToDate(isApplyInAdvance ? chargeInstance.getNextChargeDate() : chargeInstance.getChargeDate());
            chargeWasUpdated = true;
        }

        log.debug("Will apply reimbursment for charge {} for period {} - {}", chargeInstance.getId(), chargeInstance.getChargeToDateOnTermination(), chargeInstance.getChargedToDate());

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.REIMBURSMENT, false, null, null, false);

        if (chargeWasUpdated || !wos.isEmpty()) {
            chargeInstance = updateNoCheck(chargeInstance);
        }

        return chargeInstance;
    }

    /**
     * Apply missing recurring charges from the last charge date to the end agreement date
     *
     * @param chargeInstance charge Instance
     * @param recurringChargeTemplate recurringCharge Template
     * @param endAgreementDate end agreement date
     * @throws BusinessException Business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RecurringChargeInstance applyRecuringChargeToEndAgreementDate(RecurringChargeInstance chargeInstance, Date endAgreementDate) throws BusinessException, RatingException {

        boolean chargeWasUpdated = true;

        // MIGRATE for pre-v.10.0.0 cases when chargedToDate is not set yet
        // Determine a date until which a charge was rated.
        // For Apply in advance cases it is the nextChargeDate field. For rating at the end of recurring period it is chargeDate field
        if (chargeInstance.getChargeDate() != null && chargeInstance.getChargedToDate() == null) {// This check will consider old charges only
            boolean isApplyInAdvance = walletOperationService.isApplyInAdvance(chargeInstance);
            chargeInstance.setChargedToDate(isApplyInAdvance ? chargeInstance.getNextChargeDate() : chargeInstance.getChargeDate());
            chargeWasUpdated = true;
        }

        log.debug("Will apply recurring charge {} to supplement charge agreement for {} - {}", chargeInstance.getId(), chargeInstance.getChargedToDate(), endAgreementDate);

        List<WalletOperation> wos = walletOperationService.applyReccuringCharge(chargeInstance, ChargeApplicationModeEnum.AGREEMENT, false, endAgreementDate, null, false);

        if (chargeWasUpdated || !wos.isEmpty()) {
            chargeInstance = updateNoCheck(chargeInstance);
        }

        return chargeInstance;
    }

    /**
     * Reset recurring charge to a given date - cancel any unbilled wallet operations and related rated transactions. Set recurring charge's chargedTo date to fromDate
     * 
     * @param chargeInstanceId Charge instance id
     * @param fromDate Date to reset recurring charge to (chargedToDate value)
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     * @return Number of wallet operations canceled or reopended in case of rated transaction aggregation
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int resetRecurringCharge(Long chargeInstanceId, Date fromDate, boolean resetInvoiced) {

        List<Long> invoicedWoIds = null;

        // Cancel not invoiced wallet operations
        int resetNr = 0;
        List<Long> notInvoicedWoIds = getEntityManager().createNamedQuery("WalletOperation.findNotInvoicedByChargeIdFromStartDate", Long.class).setParameter("chargeInstanceId", chargeInstanceId)
            .setParameter("from", fromDate).getResultList();
        if (!notInvoicedWoIds.isEmpty()) {
            resetNr = walletOperationService.cancelWalletOperations(notInvoicedWoIds);
        }

        // Refund invoiced wallet operations if requested
        if (resetInvoiced) {
            invoicedWoIds = getEntityManager().createNamedQuery("WalletOperation.findInvoicedByChargeIdFromStartDate", Long.class).setParameter("chargeInstanceId", chargeInstanceId).setParameter("from", fromDate)
                .getResultList();
            if (invoicedWoIds.isEmpty()) {
                invoicedWoIds = null;
            } else {
                walletOperationService.refundWalletOperations(invoicedWoIds);
            }
        }

        if (invoicedWoIds == null && resetNr == 0) {
            log.warn("No wallet operations found to rerate for recurring charge {} from date {}. Rerating will be skipped.", chargeInstanceId, DateUtils.formatAsDate(fromDate));
            return 0;
        }

        RecurringChargeInstance chargeInstance = findById(chargeInstanceId);

        try {
            Date minStartDate = null;
            if (invoicedWoIds != null && resetNr > 0) {
                minStartDate = getEntityManager().createNamedQuery("WalletOperation.getMinStartDateOfResetRecurringChargesIncludingInvoiced", Date.class).setParameter("notInvoicedIds", notInvoicedWoIds)
                    .setParameter("invoicedIds", invoicedWoIds).getSingleResult();

            } else if (resetNr > 0) {
                minStartDate = getEntityManager().createNamedQuery("WalletOperation.getMinStartDateOfResetRecurringCharges", Date.class).setParameter("notInvoicedIds", notInvoicedWoIds).getSingleResult();

            } else {
                minStartDate = getEntityManager().createNamedQuery("WalletOperation.getMinStartDateOfResetRecurringChargesJustInvoiced", Date.class).setParameter("invoicedIds", invoicedWoIds).getSingleResult();
            }

            log.info("Will reset recurring charge {} from charge/next/chargedTo:{}/{}/{} to {}", chargeInstance, DateUtils.formatAsDate(chargeInstance.getChargeDate()),
                DateUtils.formatAsDate(chargeInstance.getNextChargeDate()), DateUtils.formatAsDate(chargeInstance.getChargedToDate()), DateUtils.formatAsDate(minStartDate));

            chargeInstance.setChargeDate(minStartDate);
            chargeInstance.setChargedToDate(minStartDate);
            chargeInstance.setNextChargeDate(minStartDate);

        } catch (NoResultException e) {
            log.info("Will NOT reset recurring charge {} from charge/next/chargedTo:{}/{}/{} to {}. No unbilled wallet operations were found.", chargeInstance, DateUtils.formatAsDate(chargeInstance.getChargeDate()),
                DateUtils.formatAsDate(chargeInstance.getNextChargeDate()), DateUtils.formatAsDate(chargeInstance.getChargedToDate()), DateUtils.formatAsDate(fromDate));

        }

        return resetNr;
    }

    /**
     * Re-rate recurring charge up to a given date. Existing Wallet operations and Rated transactions will be marked as canceled. Other Wallet operations related through RT
     * aggregation will be marked as open. Already invoiced wallet operations will be refunded if rerateInvoiced=true.
     * 
     * Recurring charge will be reset to a given date and existing wallet operations canceled independently of new wallet operations recreated
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param fromDate Date to reset recurring charge to (chargedToDate value)
     * @param toDate Date to rate until. Partial periods might be considered if charge was terminated and rerating up to termination date.
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     * @throws BusinessException General business exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER) // This is set to NEVER, so two inner methods are executed in new transactions
    public void rerateRecurringChargeInNewTx(Long chargeInstanceId, Date fromDate, Date toDate, boolean rerateInvoiced) {

        int nrWOCanceled = recurringChargeInstanceServiceNewTx.resetRecurringCharge(chargeInstanceId, fromDate, rerateInvoiced);

        if (nrWOCanceled > 0) {
            RecurringChargeInstance chargeInstance = findById(chargeInstanceId);
            Date chargeToDateOnTermination = chargeInstance.getChargeToDateOnTermination();

            // Reimbursement is applied directly as application end date is fixed and can not be changed
            if (chargeToDateOnTermination != null && chargeToDateOnTermination.before(chargeInstance.getChargedToDate())) {
                walletOperationService.applyReccuringChargeInNewTx(chargeInstanceId, ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, toDate, null, false);

                // Regular re-rating goes though applyRecurringCharge() as recurring calendar might change in mid rating and end date is not a strict date, but rather a period
                // indicator
            } else {
                recurringChargeInstanceServiceNewTx.applyRecurringChargeInNewTx(chargeInstanceId, toDate, false, ChargeApplicationModeEnum.RERATING);
            }
        }
    }

    /**
     * Re-rate recurring charge up to a given date. Existing Wallet operations and Rated transactions will be marked as canceled. Other Wallet operations related through RT
     * aggregation will be marked as open. Already invoiced wallet operations will be refunded if rerateInvoiced=true.
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param fromDate Date to reset recurring charge to (chargedToDate value)
     * @param toDate Date to rate until. Partial periods might be considered if charge was terminated and rerating up to termination date.
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     * @throws BusinessException General business exception
     */
    public void rerateRecurringCharge(Long chargeInstanceId, Date fromDate, Date toDate, boolean rerateInvoiced) {

        int nrWOCanceled = resetRecurringCharge(chargeInstanceId, fromDate, rerateInvoiced);

        if (nrWOCanceled > 0) {

            RecurringChargeInstance chargeInstance = findById(chargeInstanceId);
            Date chargeToDateOnTermination = chargeInstance.getChargeToDateOnTermination();

            // Reimbursement is applied directly as application end date is fixed and can not be changed.
            // On rerating, invoiced operations dont count unless the are canceled based on rerateInvoiced flag.
            // As in resetRecurringCharge() chargedToDate will be reset to a fromDate or a later date (depending on rerateInvoiced flag), need to reimburse not from
            // chargeToDateOnTermination, but from what chargedToDate was reset to.
            if (chargeToDateOnTermination != null && chargeToDateOnTermination.before(chargeInstance.getChargedToDate())) {
                walletOperationService.applyReccuringCharge(findById(chargeInstanceId), ChargeApplicationModeEnum.RERATING_REIMBURSEMENT, false, toDate, null, false);

                // Regular re-rating goes though applyRecurringCharge() as recurring calendar might change in mid rating and end date is not a strict date, but rather a period
                // indicator
            } else {
                applyRecurringCharge(chargeInstanceId, toDate, false, ChargeApplicationModeEnum.RERATING);
            }
        }
    }
}