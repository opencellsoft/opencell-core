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

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoChargeException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.RatingException;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.DeducedCounter;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletReservation;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRRejectReasonEnum;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.rating.RatingResult;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * UsageRatingService
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 * @lastModifiedVersion 7.0
 */
@Stateless
public class UsageRatingService implements Serializable {

    private static final long serialVersionUID = 1411446109227299227L;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    protected Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private RatingService ratingService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private Event<CounterPeriodEvent> counterPeriodEvent;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private ReservationService reservationService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @EJB
    private UsageRatingService usageRatingServiceNewTX;

    @Inject
    @Rejected
    private Event<Serializable> rejectedEdrProducer;

    // @PreDestroy
    // accessing Entity manager in predestroy is bugged in jboss7.1.3
    /*
     * void saveCounters() { for (Long key : MeveoCacheContainerProvider.getCounterCache().keySet()) { CounterInstanceCache counterInstanceCache =
     * MeveoCacheContainerProvider.getCounterCache().get(key); if (counterInstanceCache.getCounterPeriods() != null) { for (CounterPeriodCache itemPeriodCache :
     * counterInstanceCache .getCounterPeriods()) { if (itemPeriodCache.isDbDirty()) { CounterPeriod counterPeriod = em.find( CounterPeriod.class,
     * itemPeriodCache.getCounterPeriodId()); counterPeriod.setValue(itemPeriodCache.getValue()); counterPeriod.getAuditable().setUpdated(new Date()); em.merge(counterPeriod);
     * log.debug("save counter with id={}, new value={}", itemPeriodCache.getCounterPeriodId(), itemPeriodCache.getValue()); // calling ejb in this predestroy method just fail...
     * // counterInstanceService .updatePeriodValue(itemPeriodCache.getCounterPeriodId (),itemPeriodCache.getValue()); } } } } }
     */

    /**
     * This method first look if there is a counter and a counter period for an event date.
     *
     * @param edr EDR to process
     * @param usageChargeInstance Usage charge instance definition
     * @param reservation Is charge event part of reservation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return if EDR quantity fits partially in the counter, returns the remaining quantity. NOTE: counter and EDR units might differ - translation is performed.
     * @throws BusinessException Business exception
     */
    private DeducedCounter deduceCounter(EDR edr, UsageChargeInstance usageChargeInstance, Reservation reservation, boolean isVirtual) throws BusinessException {

        CounterPeriod counterPeriod;
        BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;

        // In case of virtual operation only instantiate a counter period, don't create it
        if (isVirtual) {
            counterPeriod = counterInstanceService.instantiateCounterPeriod(usageChargeInstance.getCounter().getCounterTemplate(), edr.getEventDate(), usageChargeInstance.getServiceInstance().getSubscriptionDate(),
                usageChargeInstance, usageChargeInstance.getServiceInstance());

        } else {
            counterPeriod = counterInstanceService.getOrCreateCounterPeriod(usageChargeInstance.getCounter(), edr.getEventDate(), usageChargeInstance.getServiceInstance().getSubscriptionDate(), usageChargeInstance,
                usageChargeInstance.getServiceInstance());
        }

        if (counterPeriod == null) {
            return new DeducedCounter();
        }

        CounterValueChangeInfo counterValueChangeInfo = null;

        UsageChargeTemplate chargeTemplate = null;
        if (usageChargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = getEntityManager().find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplate().getId());
        }

        BigDecimal quantityToDeduce = usageChargeTemplateService.evaluateRatingQuantity(chargeTemplate, edr.getQuantityLeftToRate());
        log.trace("Deduce counter instance {} current value {} by  {} * {} = {} ", isVirtual ? usageChargeInstance.getCounter().getCode() : usageChargeInstance.getCounter().getId(), counterPeriod.getValue(),
            edr.getQuantityLeftToRate(), chargeTemplate.getUnitMultiplicator(), quantityToDeduce);

        // synchronized (this) {// cachedCounterPeriod) { TODO how to ensure one at a time update?
        counterValueChangeInfo = counterInstanceService.deduceCounterValue(counterPeriod, quantityToDeduce, isVirtual);
        // }
        // Quantity is not tracked in counter (no initial value)
        if (counterValueChangeInfo == null) {
            deducedQuantityInEDRUnit = edr.getQuantityLeftToRate();

        } else if (counterValueChangeInfo.getDeltaValue().compareTo(BigDecimal.ZERO) != 0) {

            BigDecimal deducedQuantity = counterValueChangeInfo.getDeltaValue();

            // Not everything was deduced
            if (deducedQuantity.compareTo(quantityToDeduce) < 0) {
                deducedQuantityInEDRUnit = chargeTemplate.getInEDRUnit(deducedQuantity);
                // Everything was deduced
            } else {
                deducedQuantityInEDRUnit = edr.getQuantityLeftToRate();
            }
            if (reservation != null && (counterPeriod.getAccumulator() == null || !counterPeriod.getAccumulator())) {
                reservation.getCounterPeriodValues().put(counterPeriod.getId(), deducedQuantity);
            }
        }

        log.trace("In original EDR units EDR {} deduced by {}", edr.getId(), deducedQuantityInEDRUnit);

        // Fire notifications if counter value matches trigger value and counter value is tracked
        if (counterValueChangeInfo != null && counterPeriod.getNotificationLevels() != null) {
            // Need to refresh counterPeriod as it is stale object if it was updated in counterInstanceService.deduceCounterValue()
            counterPeriod = getEntityManager().find(CounterPeriod.class, counterPeriod.getId());
            List<Entry<String, BigDecimal>> counterPeriodEventLevels = counterPeriod.getMatchedNotificationLevels(counterValueChangeInfo.getPreviousValue(), counterValueChangeInfo.getNewValue());

            if (counterPeriodEventLevels != null && !counterPeriodEventLevels.isEmpty()) {
                triggerCounterPeriodEvent(counterPeriod, counterPeriodEventLevels);
            }
        }

        return new DeducedCounter(counterPeriod, deducedQuantityInEDRUnit);
    }

    /**
     * @param counterPeriod
     * @param counterPeriodEventLevels
     */
    private void triggerCounterPeriodEvent(CounterPeriod counterPeriod, List<Entry<String, BigDecimal>> counterPeriodEventLevels) {
        for (Entry<String, BigDecimal> counterValue : counterPeriodEventLevels) {
            try {
                CounterPeriodEvent event = new CounterPeriodEvent(counterPeriod, counterValue.getValue(), counterValue.getKey());
                event.setCounterPeriod(counterPeriod);
                counterPeriodEvent.fire(event);
            } catch (Exception e) {
                log.error("Failed to executing trigger counterPeriodEvent", e);
            }
        }
    }

    /**
     * This method evaluate the EDR against the charge and its counter. If counter is present, it will be decremented by EDR content
     * 
     * @param edr EDR to rate
     * @param usageChargeInstance Charge instance to apply
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     *
     * @return returns an RatedEDRResult object, the RatedEDRResult.eDRfullyRated is true if the charge has been fully rated (either because it has no counter or because the
     *         counter can be fully decremented with the EDR content)
     * @throws BusinessException Business exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private RatingResult rateEDRonChargeAndCounters(EDR edr, UsageChargeInstance usageChargeInstance, boolean isVirtual) throws BusinessException, RatingException {

        BigDecimal deducedQuantity = null;
        DeducedCounter deducedCounter = null;

        boolean fullyRated = false;
        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedCounter = deduceCounter(edr, usageChargeInstance, null, isVirtual);
            deducedQuantity = deducedCounter.getDeducedQuantity();
            if (edr.getQuantityLeftToRate().compareTo(deducedQuantity) == 0) {
                fullyRated = true;
            }

            if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
                // we continue the rating to have a WO that its needed in pricePlan.script
                return new RatingResult();
            }
        } else {
            fullyRated = true;
        }

        BigDecimal quantityToCharge = null;
        if (useFullQuantity(deducedCounter)) {
            quantityToCharge = edr.getQuantityLeftToRate();

        } else {
            edr.deduceQuantityLeftToRate(deducedQuantity);
            quantityToCharge = deducedQuantity;
        }

        RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(usageChargeInstance, edr.getEventDate(), quantityToCharge, null, null, null, null, null, null, edr, false, isVirtual);
        ratingResult.setFullyRated(fullyRated);

        if (!isVirtual) {
            walletOperationService.chargeWalletOperation(ratingResult.getWalletOperation());
        }

        return ratingResult;
    }

    private boolean useFullQuantity(DeducedCounter deducedCounter) {
        if (deducedCounter == null) {
            return true;
        }
        if (deducedCounter.getDeducedQuantity() == null) {
            return true;
        }
        if (deducedCounter.getCounterPeriod() != null && deducedCounter.getCounterPeriod().getAccumulator() != null && deducedCounter.getCounterPeriod().getAccumulator()) {
            return true;
        }
        return false;
    }

    /**
     * Rate EDR and create wallet operation for reservation. If counter is used, and the quantity left in counter if less then quantity in EDR, EDR is updated with a left over
     * quantity and the remaining quantity will be covered by a next charge or EDR will be marked as rejected.
     * 
     * @param reservation Reservation
     * @param edr EDR to reserve
     * @param usageChargeInstance Associated charge
     * @return True EDR was rated fully - either no counter used, or quantity remaining in a counter was greater or equal to the quantity to rate
     * @throws BusinessException Business exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    private boolean reserveEDRonChargeAndCounters(Reservation reservation, EDR edr, UsageChargeInstance usageChargeInstance) throws BusinessException, RatingException {
        boolean stopEDRRating = false;
        BigDecimal deducedQuantity = null;
        DeducedCounter deducedCounter = null;

        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - proceed with another charge
            deducedCounter = deduceCounter(edr, usageChargeInstance, reservation, false);
            deducedQuantity = deducedCounter.getDeducedQuantity();
            if (edr.getQuantityLeftToRate().compareTo(deducedQuantity) == 0) {
                stopEDRRating = true;
            }
        } else {
            stopEDRRating = true;
        }

        if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return stopEDRRating;
        }

        BigDecimal quantityToCharge;
        if (deducedQuantity == null) {
            quantityToCharge = edr.getQuantityLeftToRate();
        } else {
            edr.deduceQuantityLeftToRate(deducedQuantity);
            quantityToCharge = deducedQuantity;
        }

        RatingResult ratingResult = ratingService.rateCharge(usageChargeInstance, edr.getEventDate(), quantityToCharge, null, null, null, null, null, null, edr, true, false);

        WalletReservation walletReservation = (WalletReservation) ratingResult.getWalletOperation();

        walletReservation.setReservation(reservation);

        // Set the amount instead of quantity if the counter is an accumulator.
        if (deducedCounter != null && deducedCounter.getCounterPeriod() != null) {
            counterInstanceService.accumulatorCounterPeriodValue(deducedCounter.getCounterPeriod(), walletReservation, reservation, false);
        }
        reservation.setAmountWithoutTax(reservation.getAmountWithoutTax().add(walletReservation.getAmountWithoutTax()));
        reservation.setAmountWithTax(reservation.getAmountWithoutTax().add(walletReservation.getAmountWithTax()));

        walletOperationService.chargeWalletOperation(walletReservation);

        return stopEDRRating;
    }

    /**
     * Rate an EDR using counters if they apply. EDR status will be updated to Rejected even if exception is thrown
     * 
     * @param edr EDR to rate
     * @throws BusinessException business exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void ratePostpaidUsage(Long edrId) throws BusinessException, RatingException {

        try {
            usageRatingServiceNewTX.rateUsageInNewTransaction(edrId, false, 0, 0);

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edrId, e.getRejectionReason());
            usageRatingServiceNewTX.rejectEDR(edrId, e);
            throw e;

        } catch (BusinessException e) {
            log.error("Failed to rate EDR {}: {}", edrId, e.getMessage(), e);
            usageRatingServiceNewTX.rejectEDR(edrId, e);
            throw e;
        }
    }

    /**
     * Rate a virtual EDR. Data will not be persisted
     * 
     * @param edr EDR to rate
     * @return A list of wallet operations
     * @throws BusinessException A general exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> rateVirtualEDR(EDR edr) throws BusinessException, RatingException {
        return rateUsageWithinTransaction(edr, true, false, 0, 0);
    }

    /**
     * Rate EDR in a new transaction
     * 
     * @param edrId EDR id
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @return A list of wallet operations corresponding to rated EDR
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<WalletOperation> rateUsageInNewTransaction(Long edrId, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth) throws BusinessException, RatingException {

        EDR edr = getEntityManager().find(EDR.class, edrId);
        return rateUsageWithinTransaction(edr, false, rateTriggeredEdr, maxDeep, currentRatingDepth);
    }

    /**
     * Rate EDR
     * 
     * @param edr EDR
     * @param isVirtual Is this a virtual operation and no real counters or wallets should be affected (applies to quotes)
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @return A list of wallet operations corresponding to rated EDR
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<WalletOperation> rateUsageWithinTransaction(EDR edr, boolean isVirtual, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth) throws BusinessException, RatingException {

        log.debug("Rating EDR={}", edr);
        List<WalletOperation> walletOperations = new ArrayList<>();

        try {
            if (edr.getQuantity() == null) {
                throw new RatingException(EDRRejectReasonEnum.QUANTITY_IS_NULL);
            }

            if (edr.getSubscription() == null) {
                throw new RatingException(EDRRejectReasonEnum.SUBSCRIPTION_IS_NULL);
            }

            RatingResult ratedEDRResult = new RatingResult();

            List<UsageChargeInstance> usageChargeInstances = null;

            Long SubId = edr.getSubscription().getId();
            if (SubId != null) {
                usageChargeInstances = usageChargeInstanceService.getUsageChargeInstancesValidForDateBySubscriptionId(SubId, edr.getEventDate());
                if (usageChargeInstances == null || usageChargeInstances.isEmpty()) {
                    throw new NoChargeException("No active usage charges are associated with subscription " + SubId);
                }
            } else if(edr.getSubscription().getServiceInstances() != null) {
                usageChargeInstances = edr.getSubscription().getServiceInstances()
                                            .stream()
                                            .flatMap(si -> si.getUsageChargeInstances().stream())
                                            .collect(toList());
                if (usageChargeInstances == null || usageChargeInstances.isEmpty()) {
                    throw new NoChargeException("No usage charges are associated with subscription " + SubId);
                }
            }


            boolean foundPricePlan = true;

            // Find the first matching charge and rate it
            for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {

                log.trace("Try to rate EDR {} with charge {}", edr.getId(), usageChargeInstance.getCode());
                try {

                    if (!isChargeMatch(usageChargeInstance, edr, true)) {
                        continue;
                    }

                } catch (NoPricePlanException e) {
                    log.debug("Charge {} was matched for EDR {} but does not contain a priceplan", usageChargeInstance.getCode(), edr.getId());
                    foundPricePlan = false;
                    continue;
                }

                log.debug("Will apply matching charge instance id={} for EDR {}", usageChargeInstance.getId(), edr.getId());

                ratedEDRResult = rateEDRonChargeAndCounters(edr, usageChargeInstance, isVirtual);
                if (ratedEDRResult.getWalletOperation() != null) {
                    walletOperations.add(ratedEDRResult.getWalletOperation());
                    walletOperationService.applyAccumulatorCounter(usageChargeInstance, Collections.singletonList(ratedEDRResult.getWalletOperation()), isVirtual);
                }

                if (rateTriggeredEdr && ratedEDRResult.getTriggeredEDRs() != null && !ratedEDRResult.getTriggeredEDRs().isEmpty()) {
                    walletOperations.addAll(rateTriggeredEDRs(isVirtual, rateTriggeredEdr, maxDeep, currentRatingDepth, ratedEDRResult.getTriggeredEDRs()));
                }

                if (ratedEDRResult.isFullyRated()) {
                    UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService.findById(usageChargeInstance.getChargeTemplate().getId());

                    boolean triggerNextCharge = usageChargeTemplate.getTriggerNextCharge();

                    if (!StringUtils.isBlank(usageChargeTemplate.getTriggerNextChargeEL())) {
                        triggerNextCharge = evaluateBooleanExpression(usageChargeTemplate.getTriggerNextChargeEL(), edr, ratedEDRResult.getWalletOperation());
                    }
                    if (!triggerNextCharge) {
                        break;
                    }
                }
            }

            if (ratedEDRResult.isFullyRated()) {
                edr.changeStatus(EDRStatusEnum.RATED);

            } else if (!foundPricePlan) {
                throw new NoPricePlanException("At least one charge was matched but did not contain an applicable price plan for EDR " + (edr.getId() != null ? edr.getId() : edr));

            } else {
                throw new NoChargeException(EDRRejectReasonEnum.NO_MATCHING_CHARGE, "No charge matched for EDR " + edr.getId());
            }

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
            throw e;

        } catch (BusinessException e) {
            log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
            throw e;
        }
        return walletOperations;
    }

    /**
     * Rate Triggered EDR.
     *
     * @param isVirtual rate EDR virtually (no persisting in DB)
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @param triggeredEDRs EDRs that were triggered by a charge processing
     * @return a list of WalletOperation
     * @throws BusinessException General business exception
     */
    private List<WalletOperation> rateTriggeredEDRs(boolean isVirtual, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth, List<EDR> triggeredEDRs) throws BusinessException {
        List<WalletOperation> triggeredWOs = new ArrayList<>();
        if (rateTriggeredEdr && currentRatingDepth < maxDeep) {
            for (EDR edr : triggeredEDRs) {
                // Ignore errors, as triggered EDR was saved already, and will be rated again later ?? AKK? Is this valid assumption?
                try {
                    triggeredWOs.addAll(rateUsageWithinTransaction(edr, isVirtual, true, maxDeep, currentRatingDepth + 1));
                } catch (RatingException e) {
                    log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());

                } catch (BusinessException e) {
                    log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
                }
            }
        }
        return triggeredWOs;
    }

    /**
     * Check if charge filter parameters match those of EDR. Optionally checks if charge has a priceplan associated
     * 
     * @param chargeInstance Charge instance to match against EDR
     * @param edr EDR to check
     * @param requirePP Require that charge has a priceplan associated
     * @return true if charge is matched
     * @throws BusinessException business exception.
     * @throws ChargeWitoutPricePlanException If charge has no price plan associated
     */
    private boolean isChargeMatch(UsageChargeInstance chargeInstance, EDR edr, boolean requirePP) throws BusinessException, NoPricePlanException {

        UsageChargeTemplate chargeTemplate;
        if (chargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) chargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = getEntityManager().find(UsageChargeTemplate.class, chargeInstance.getChargeTemplate().getId());
        }

        String filter1 = chargeTemplate.getFilterParam1();

        if (filter1 == null || filter1.equals(edr.getParameter1())) {
            String filter2 = chargeTemplate.getFilterParam2();
            if (filter2 == null || filter2.equals(edr.getParameter2())) {
                String filter3 = chargeTemplate.getFilterParam3();
                if (filter3 == null || filter3.equals(edr.getParameter3())) {
                    String filter4 = chargeTemplate.getFilterParam4();
                    if (filter4 == null || filter4.equals(edr.getParameter4())) {
                        String filterExpression = chargeTemplate.getFilterExpression();
                        if (filterExpression == null || matchExpression(chargeInstance, filterExpression, edr)) {

                            if (requirePP) {
                                String chargeCode = chargeTemplate.getCode();
                                List<PricePlanMatrix> chargePricePlans = pricePlanMatrixService.getActivePricePlansByChargeCode(chargeCode);
                                if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                                    throw new NoPricePlanException("Charge " + chargeCode + " has no price plan defined");
                                }
                            }
                            return true;
                        }
                    } else {
                        log.trace("filter 4 not matched");
                    }
                } else {
                    log.trace("filter 3 not matched");
                }
            } else {
                log.trace("filter 2 not matched");
            }
        } else {
            log.trace("filter 1 not matched");
        }
        return false;
    }

    /**
     * Rate EDR as reservation.
     * 
     * @param edr EDR to rate
     * @return Reservation
     * @throws BusinessException business exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Reservation reserveUsageWithinTransaction(EDR edr) throws BusinessException, RatingException {

        Reservation reservation = null;

        long time = System.currentTimeMillis();
        log.debug("Reserving EDR={}, we override the event date with the current date", edr);
        edr.setEventDate(new Date(time));

        if (edr.getSubscription() == null) {
            edr.setRatingRejectionReason(EDRRejectReasonEnum.SUBSCRIPTION_IS_NULL.getCode());
            return null;
        }

        boolean edrIsRated = false;

        // Charges are ordered by priority and id
        List<UsageChargeInstance> charges = usageChargeInstanceService.getActiveUsageChargeInstancesBySubscriptionId(edr.getSubscription().getId());
        if (charges == null || charges.isEmpty()) {
            edr.setRatingRejectionReason(EDRRejectReasonEnum.SUBSCRIPTION_HAS_NO_CHARGE.getCode());
        }
        reservation = new Reservation();
        reservation.setReservationDate(edr.getEventDate());
        reservation.setExpiryDate(new Date(time + appProvider.getPrepaidReservationExpirationDelayinMillisec()));
        reservation.setStatus(ReservationStatus.OPEN);
        reservation.updateAudit(currentUser);
        reservation.setOriginEdr(edr);
        reservation.setQuantity(edr.getQuantity());

        reservationService.create(reservation);

        UsageChargeTemplate chargeTemplate;
        for (UsageChargeInstance usageChargeInstance : charges) {

        	chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
            log.trace("Try  templateCache {}", chargeTemplate.getCode());
            try {
                if (isChargeMatch(usageChargeInstance, edr, true)) {

                    log.debug("found matching charge inst : id {}", usageChargeInstance.getId());
                    edrIsRated = reserveEDRonChargeAndCounters(reservation, edr, usageChargeInstance);
                    if (edrIsRated) {
                        edr.changeStatus(EDRStatusEnum.RATED);
                        break;
                    }
                }
            } catch (NoPricePlanException e) {
                continue;
            }
        }

        if (!edrIsRated) {
            edr.setRatingRejectionReason(EDRRejectReasonEnum.NO_MATCHING_CHARGE.getCode());
        }

        return reservation;
    }

    private boolean matchExpression(UsageChargeInstance ci, String expression, EDR edr) throws BusinessException {
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("ci", ci);
        return ValueExpressionWrapper.evaluateToBoolean(expression, userMap);
    }

    private boolean evaluateBooleanExpression(String expression, EDR edr, WalletOperation walletOperation) throws BusinessException {
        boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf(ValueExpressionWrapper.VAR_USER_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_USER_ACCOUNT, walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, walletOperation.getServiceInstance());
        }

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    /**
     * @return Get entity manager
     */
    private EntityManager getEntityManager() {
        return emWrapper.getEntityManager();
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void rejectEDR(Long edrId, Exception e) {
        String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);

        EDR edr = edrService.findById(edrId);
        edr.changeStatus(EDRStatusEnum.REJECTED);
        edr.setRejectReason(rejectReason);

        rejectedEdrProducer.fire(edr);
    }
}