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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RatingStatus;
import org.meveo.model.billing.RatingStatusEnum;
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
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
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
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    @Rejected
    Event<Serializable> rejectededChargeProducer;

    @Inject
    private CounterInstanceService counterInstanceService;

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

    public List<Long> findIdsByStatusAndSubscriptionCode(InstanceStatusEnum status, Date maxChargeDate, Long subscriptionId, boolean truncateToDay) {

        QueryBuilder qb = queryIdsByStatus(status, maxChargeDate, truncateToDay);
        qb.addCriterion("c.subscription.id", "=", subscriptionId, true);

        List<Long> ids = qb.getIdQuery(getEntityManager()).getResultList();
        log.trace("Found recurring charges by status {} and subscriptionId {} . Result size found={}.", status, subscriptionId, (ids != null ? ids.size() : "NULL"));

        return ids;
    }

    public List<Long> findIdsByStatus(InstanceStatusEnum status, Date maxChargeDate, boolean truncateToDay) {

        QueryBuilder qb = queryIdsByStatus(status, maxChargeDate, truncateToDay);
        List<Long> ids = qb.getIdQuery(getEntityManager()).getResultList();
        log.trace("Found recurring charges by status (status={}). Result size found={}.", status, (ids != null ? ids.size() : "NULL"));

        return ids;
    }

    /**
     * Query ids by status.
     *
     * @param status the status
     * @param maxChargeDate the max charge date
     * @param truncateToDay the truncate to day
     * @return the query builder
     */
    private QueryBuilder queryIdsByStatus(InstanceStatusEnum status, Date maxChargeDate, boolean truncateToDay) {
        QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c");
        qb.addCriterionEnum("c.status", status);

        if (truncateToDay) {
            qb.addCriterionDateRangeToTruncatedToDay("c.nextChargeDate", maxChargeDate);
        } else {
            qb.addCriterion("c.nextChargeDate", "<", maxChargeDate, false);
        }
        return qb;
    }

    @SuppressWarnings("unchecked")
    public List<RecurringChargeInstance> findRecurringChargeInstanceBySubscriptionId(Long subscriptionId) {
        QueryBuilder qb = new QueryBuilder(RecurringChargeInstance.class, "c", Arrays.asList("chargeTemplate"));
        qb.addCriterion("c.subscription.id", "=", subscriptionId, true);
        return qb.getQuery(getEntityManager()).getResultList();
    }

    public RecurringChargeInstance recurringChargeInstanciation(ServiceInstance serviceInstance, ServiceChargeTemplateRecurring serviceChargeTemplateRecurring, boolean isVirtual)
            throws BusinessException {

        if (serviceInstance == null) {
            throw new BusinessException("service instance does not exist.");
        }

        if (serviceInstance.getStatus() == InstanceStatusEnum.CANCELED || serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED
                || serviceInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
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

        log.debug("create chargeInstance for charge {}", chargeCode);
        RecurringChargeInstance chargeInstance = new RecurringChargeInstance(null, null, recurringChargeTemplate, serviceInstance, InstanceStatusEnum.INACTIVE, recurringChargeTemplate.getCalendar(), recurringChargeTemplate.getApplyInAdvance());

        ServiceChargeTemplateRecurring recChTmplServ = serviceInstance.getServiceTemplate().getServiceRecurringChargeByChargeCode(chargeCode);
        // getEntityManager().merge(recChTmplServ); - does not make sence as
        // merge result is what shoudl be used
        List<WalletTemplate> walletTemplates = recChTmplServ.getWalletTemplates();

        if (walletTemplates != null && walletTemplates.size() > 0) {
            log.debug("associate {} walletsInstance", walletTemplates.size());
            for (WalletTemplate walletTemplate : walletTemplates) {
                if (walletTemplate == null) {
                    log.debug("walletTemplate is null, we continue");
                    continue;
                }
                if (walletTemplate.getWalletType() == BillingWalletTypeEnum.PREPAID) {
                    log.debug("one walletTemplate is prepaid, we set the chargeInstance as being prepaid");
                    chargeInstance.setPrepaid(true);
                }

                WalletInstance walletInstance = walletService.getWalletInstance(serviceInstance.getSubscription().getUserAccount(), walletTemplate, isVirtual);
                log.debug("add the wallet instance {} to the chargeInstance {}", walletInstance.getId(), chargeInstance.getId());
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
            log.debug("Usage charge has {} accumulator counter templates", serviceChargeTemplateRecurring.getAccumulatorCounterTemplates().size());
            for (CounterTemplate counterTemplate : serviceChargeTemplateRecurring.getAccumulatorCounterTemplates()) {
                log.debug("Accumulator counter template {}", counterTemplate);
                CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, counterTemplate, isVirtual);
                log.debug("Accumulator counter instance {} will be add to charge instance {}", counterInstance, chargeInstance);
                chargeInstance.addCounterInstance(counterInstance);
            }
            log.debug("Counter template {}", serviceChargeTemplateRecurring.getCounterTemplate());
            CounterInstance counterInstance = counterInstanceService.counterInstanciation(serviceInstance, serviceChargeTemplateRecurring.getCounterTemplate(), isVirtual);
            log.debug("Counter instance {} will be add to charge instance {}", counterInstance, chargeInstance);
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
        if (serviceInst.getStatus() == InstanceStatusEnum.TERMINATED || serviceInst.getStatus() == InstanceStatusEnum.CANCELED
                || serviceInst.getStatus() == InstanceStatusEnum.SUSPENDED) {
            throw new BusinessException(
                "service instance is " + subscription.getStatus() + ". service Code=" + serviceInst.getCode() + ",subscription Code" + subscription.getCode());
        }
        for (RecurringChargeInstance recurringChargeInstance : serviceInst.getRecurringChargeInstances()) {
            recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
            // recurringChargeInstance.setSubscriptionDate(subscriptionDate);
            recurringChargeInstance.setTerminationDate(null);
            recurringChargeInstance.setChargeDate(subscriptionDate);
            update(recurringChargeInstance);
        }

    }

    public RatingStatus applyRecurringCharge(Long chargeInstanceId, Date maxDate, boolean isStrictlyBeforeMaxDate) throws BusinessException {

        int maxRecurringRatingHistory = Integer.parseInt(paramBeanFactory.getInstance().getProperty("rating.recurringMaxRetry", "100"));
        RatingStatus ratingStatus = new RatingStatus();

        try {
            RecurringChargeInstance activeRecurringChargeInstance = findById(chargeInstanceId);
            Date applyChargeFromDate = null;
            applyChargeFromDate = activeRecurringChargeInstance.getNextChargeDate();

            if (!walletOperationService.isChargeMatch(activeRecurringChargeInstance, activeRecurringChargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
                log.debug("not rating chargeInstance with code={}, filter expression  evaluated to false", activeRecurringChargeInstance.getCode());
                while (applyChargeFromDate != null && ratingStatus.getNbRating() < maxRecurringRatingHistory
                        && ((applyChargeFromDate.getTime() <= maxDate.getTime() && !isStrictlyBeforeMaxDate)
                                || (applyChargeFromDate.getTime() < maxDate.getTime() && isStrictlyBeforeMaxDate))) {
                    walletOperationService.updateChargeDate(activeRecurringChargeInstance);
                    applyChargeFromDate = activeRecurringChargeInstance.getNextChargeDate();
                }
                ratingStatus.setStatus(RatingStatusEnum.NOT_RATED_FALSE_FILTER);
                return ratingStatus;
            }

            RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance.getRecurringChargeTemplate();
            if (recurringChargeTemplate.getCalendar() == null) {
                // FIXME : should not stop the method execution
                rejectededChargeProducer.fire(recurringChargeTemplate);
                log.error("Recurring charge template has no calendar: code=" + recurringChargeTemplate.getCode());
                throw new BusinessException("Recurring charge template has no calendar: code=" + recurringChargeTemplate.getCode());
            }

            // if (recurringChargeTemplate.getApplyInAdvance()) {
            applyChargeFromDate = activeRecurringChargeInstance.getNextChargeDate();
            // } else {
            // applyChargeFromDate = activeRecurringChargeInstance.getChargeDate();
            // }

            // If we recognize revenue we first delete all SCHEDULED wallet
            // operations
            if (appProvider.isRecognizeRevenue()) {

                try {
                    log.debug("delete scheduled charges applications on chargeInstance {}", chargeInstanceId);
                    getEntityManager().createNamedQuery("WalletOperation.deleteScheduled").setParameter("chargeInstance", activeRecurringChargeInstance).executeUpdate();
                } catch (Exception e) {
                    log.error("error while trying to delete scheduled charges applications on chargeInstance {}", chargeInstanceId, e);
                }

            }

            log.debug("Will apply recurring charge {} for missing periods {} - {} {}", activeRecurringChargeInstance.getId(), applyChargeFromDate, maxDate,
                isStrictlyBeforeMaxDate ? "exclusive" : "inclusive");

            while (applyChargeFromDate != null && ratingStatus.getNbRating() < maxRecurringRatingHistory
                    && ((applyChargeFromDate.getTime() <= maxDate.getTime() && !isStrictlyBeforeMaxDate)
                            || (applyChargeFromDate.getTime() < maxDate.getTime() && isStrictlyBeforeMaxDate))) {

                ratingStatus.setNbRating(ratingStatus.getNbRating() + 1);
                log.debug("Applying recurring charge {} for {}", activeRecurringChargeInstance.getId(), applyChargeFromDate);

                List<WalletOperation> wos = null;
                boolean isApplyInAdvance = (activeRecurringChargeInstance.getApplyInAdvance() == null) ? false : activeRecurringChargeInstance.getApplyInAdvance();
                if (!StringUtils.isBlank(recurringChargeTemplate.getApplyInAdvanceEl())) {
                    isApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getApplyInAdvanceEl(),
                        activeRecurringChargeInstance.getServiceInstance(), recurringChargeTemplate);
                }
                if (isApplyInAdvance) {
                    wos = walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false, recurringChargeTemplate, false);
                } else {
                    wos = walletOperationService.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance, false, recurringChargeTemplate);
                }

                log.debug("Recurring charge {} applied for {} - {}, produced {} wallet operations", activeRecurringChargeInstance.getId(),
                    activeRecurringChargeInstance.getChargeDate(), activeRecurringChargeInstance.getNextChargeDate(), wos.size());
                // if (recurringChargeTemplate.getApplyInAdvance()) {
                applyChargeFromDate = activeRecurringChargeInstance.getNextChargeDate();
                // } else {
                // applicationDate =
                // activeRecurringChargeInstance.getChargeDate();
                // }
            }
            if (ratingStatus.getNbRating() > 0) {
                updateNoCheck(activeRecurringChargeInstance);
            }
            // If we recognize revenue we create SCHEDULED wallet op until the
            // end of the contract
            if (appProvider.isRecognizeRevenue() && !activeRecurringChargeInstance.getPrepaid()) {
                Date endContractDate = activeRecurringChargeInstance.getSubscription().getEndAgreementDate();
                log.debug("apply scheduled charges until {}", endContractDate);
                if (endContractDate == null) {
                    log.error("error while trying to schedule revenue for chargeInstance {}," + " the subscription has no end agreeement date", chargeInstanceId);
                } else {
                    Date chargeDate = activeRecurringChargeInstance.getChargeDate();
                    Date nextChargeDate = activeRecurringChargeInstance.getNextChargeDate();
                    while (applyChargeFromDate != null && applyChargeFromDate.getTime() <= endContractDate.getTime()) {
                        log.debug("Schedule applicationDate={}", applyChargeFromDate);
                        applyChargeFromDate = DateUtils.setTimeToZero(applyChargeFromDate);
                        if (!activeRecurringChargeInstance.getApplyInAdvance()) {
                            walletOperationService.applyNotAppliedinAdvanceReccuringCharge(activeRecurringChargeInstance, false, recurringChargeTemplate);
                        } else {
                            walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false, recurringChargeTemplate, true);
                        }
                        log.debug("chargeDate {},nextChargeDate {}", activeRecurringChargeInstance.getChargeDate(), activeRecurringChargeInstance.getNextChargeDate());
                        applyChargeFromDate = activeRecurringChargeInstance.getNextChargeDate();

                    }
                    activeRecurringChargeInstance.setChargeDate(chargeDate);
                    activeRecurringChargeInstance.setNextChargeDate(nextChargeDate);
                }
                if (activeRecurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule() != null
                        && activeRecurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule().getScript() != null) {
                    revenueRecognitionScriptService.createRevenueSchedule(activeRecurringChargeInstance.getChargeTemplate().getRevenueRecognitionRule().getScript().getCode(),
                        activeRecurringChargeInstance);
                }

            }
        } catch (Exception e) {
            rejectededChargeProducer.fire("RecurringCharge " + chargeInstanceId);
            throw new BusinessException(e);
        }
        return ratingStatus;

    }

    public RatingStatus applyRecurringCharge(Long chargeInstanceId, Date maxDate) throws BusinessException {
        // RecurringChargeInstance recurringChargeInstance = findById(chargeInstanceId);
        return applyRecurringCharge(chargeInstanceId, maxDate, false);
    }

    /**
     * Apply recurring charges between given dates to a user account for a Virtual operation. Does not create/update/persist any entity.
     *
     * @param chargeInstance Recurring charge instance
     * @param fromDate Recurring charge application start
     * @param toDate Recurring charge application end
     * @return list of wallet operations
     * @throws BusinessException business exception.
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyRecurringChargeVirtual(RecurringChargeInstance chargeInstance, Date fromDate, Date toDate) throws BusinessException, RatingException {

        log.debug("Apply recuring charges on Virtual operation. User account {}, offer {}, charge {}, quantity {}, date range {}-{}", chargeInstance.getUserAccount().getCode(),
            chargeInstance.getServiceInstance().getSubscription().getOffer().getCode(), chargeInstance.getRecurringChargeTemplate().getCode(), chargeInstance.getQuantity(),
            fromDate, toDate);

        if (!walletOperationService.isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression())) {
            log.debug("not rating chargeInstance with code={}, filter expression not evaluated to true", chargeInstance.getCode());
            return null;
        }

        return walletOperationService.applyReccuringChargeVirtual(chargeInstance, fromDate, toDate);

    }
}