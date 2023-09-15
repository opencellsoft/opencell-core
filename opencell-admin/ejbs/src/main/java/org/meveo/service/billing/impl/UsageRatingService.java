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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.CommunicateToRemoteInstanceException;
import org.meveo.admin.exception.CounterInstantiationException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.NoChargeException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.DeducedCounter;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRRejectReasonEnum;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.medina.impl.CDRService;

/**
 * Usage charge related rating
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @author Mounir BAHIJE
 */
@Stateless
public class UsageRatingService extends RatingService implements Serializable {

    private static final long serialVersionUID = 1411446109227299227L;

    @Inject
    private EdrService edrService;

    @Inject
    private CDRService cdrService;

    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;
    
    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    @Rejected
    private Event<Serializable> rejectedEdrProducer;
        
    @Inject
    private UserAccountService userAccountService;

    /**
     * Decrease a usage charge counter by EDR quantity. A new counter period matching EDR event date will be instantiated if does not exist yet.
     *
     * @param edr EDR to process
     * @param usageChargeInstance Usage charge instance definition
     * @param reservation Is charge event part of reservation
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @return Deducted EDR quantity. NOTE: counter and EDR units might differ - translation is performed.
     * @throws CounterInstantiationException Failure to create a new counter period
     * @throws ValidationException Can not convert quantity units to rating units
     */
    private DeducedCounter deduceCounter(EDR edr, UsageChargeInstance usageChargeInstance, Reservation reservation, boolean isVirtual) throws CounterInstantiationException, ValidationException {

        if (usageChargeInstance.getCounter() == null) {
            return new DeducedCounter();
        }

        BigDecimal deducedQuantityInEDRUnit = BigDecimal.ZERO;
        UsageChargeTemplate chargeTemplate = null;
        if (usageChargeInstance.getChargeTemplate() instanceof UsageChargeTemplate) {
            chargeTemplate = (UsageChargeTemplate) usageChargeInstance.getChargeTemplate();
        } else {
            chargeTemplate = getEntityManager().find(UsageChargeTemplate.class, usageChargeInstance.getChargeTemplate().getId());
        }

        BigDecimal quantityToDeduce = usageChargeTemplateService.evaluateRatingQuantity(chargeTemplate, edr.getQuantityLeftToRate());
        log.trace("Attempt to deduce counter instance {} for {} by  {} * {} = {} ", isVirtual ? usageChargeInstance.getCounter().getCode() : usageChargeInstance.getCounter().getId(), edr.getEventDate(),
            edr.getQuantityLeftToRate(), chargeTemplate.getUnitMultiplicator(), quantityToDeduce);

        CounterValueChangeInfo counterValueChangeInfo = counterInstanceService.deduceCounterValue(usageChargeInstance.getCounter(), edr.getEventDate(), usageChargeInstance.getServiceInstance().getSubscriptionDate(),
            usageChargeInstance, quantityToDeduce, isVirtual);

        // Quantity is not tracked in counter (no initial/new value)
        if (counterValueChangeInfo.getNewValue() == null) {
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

            // Remember what counter was consumed, so in case of reservation cancellation counter value could be restored.
            if (reservation != null && !counterValueChangeInfo.isAccumulator()) {
                reservation.getCounterPeriodValues().put(counterValueChangeInfo.getCounterPeriodId(), counterValueChangeInfo.getDeltaValue());
            }
        }

        log.trace("In original EDR units EDR {} deduced by {}", edr.getId(), deducedQuantityInEDRUnit);

        return new DeducedCounter(counterValueChangeInfo, deducedQuantityInEDRUnit);
    }

    /**
     * This method rates the EDR against the charge and its counter. If counter is used, and the quantity left in counter if less then quantity in EDR, EDR is updated with a left over quantity and the remaining quantity
     * will be covered by a next charge or EDR will be marked as rejected. If any additional EDRs are triggered, they will be instantiated. Note: No Wallet operations nor triggered EDRs are persisted.
     * 
     * @param edr EDR to rate
     * @param usageChargeInstance Charge instance to apply
     * @param reservation - Reservation the wallet operation is tied to
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB. Applies here to counter periods only.
     *
     * @return returns an RatedEDRResult object, the RatedEDRResult.EDRfullyRated is true if the charge has been fully rated (either because it has no counter or because the counter can be fully decremented with the EDR
     *         content)
     * @throws ValidationException - Failure to rate due to data or EL validation exceptions
     * @throws RatingException - Failure to rate charge due to lack of funds, inconsistency or other rating related failure
     * @throws CommunicateToRemoteInstanceException - Failed to communicate to a remote Opencell instance when sending Triggered EDRs
     * @throws CounterInstantiationException Failure to create a new counter period
     */
    private RatingResult rateEDRonChargeAndCounterAndInstantiateTriggeredEDRs(EDR edr, UsageChargeInstance usageChargeInstance, Reservation reservation, boolean isVirtual)
            throws CounterInstantiationException, ValidationException, RatingException, CommunicateToRemoteInstanceException {

        BigDecimal quantityToCharge = edr.getQuantityLeftToRate();
        DeducedCounter deducedCounter = null;

        boolean fullyRated = usageChargeInstance.getCounter() == null;

        if (usageChargeInstance.getCounter() != null) {
            // if the charge is associated to a counter, we decrement it. If decremented by the full quantity, rating is finished.
            // If decremented partially or none - will proceed with another charge
            deducedCounter = deduceCounter(edr, usageChargeInstance, null, isVirtual);
            BigDecimal deducedQuantity = deducedCounter.getDeducedQuantity();

            if (deducedQuantity != null && deducedQuantity.compareTo(BigDecimal.ZERO) == 0) {
                // we continue the rating to have a WO that its needed in pricePlan.script
                return new RatingResult();
            }
            if (edr.getQuantityLeftToRate().compareTo(deducedQuantity) == 0) {
                fullyRated = true;
            }
            edr.deduceQuantityLeftToRate(deducedQuantity);
            quantityToCharge = deducedQuantity;
        }

        try {
            RatingResult ratingResult = null;
            if (reservation == null) {
                ratingResult = rateChargeAndInstantiateTriggeredEDRs(usageChargeInstance, edr.getEventDate(), quantityToCharge, null, null, null, null, null, null, edr, reservation, false, isVirtual);
            } else {
                ratingResult = rateCharge(usageChargeInstance, edr.getEventDate(), quantityToCharge, null, null, null, null, null, null, edr, reservation, isVirtual);
            }
            ratingResult.setFullyRated(fullyRated);
            if (deducedCounter != null) {
                ratingResult.addCounterChange(deducedCounter.getCounterValueChangeInfo().getCounterPeriodId(), deducedCounter.getCounterValueChangeInfo().getDeltaValue());
            }

            return ratingResult;

        } catch (ValidationException | RatingException | CommunicateToRemoteInstanceException e) {
            // Counter was deduced, but rating failed, so counter value must be restored
            if (deducedCounter != null) {
                log.error("Failed to rate charge {}, will revert the applied counter {} by {}", usageChargeInstance.getId(), deducedCounter.getCounterValueChangeInfo().getCounterPeriodId(),
                    deducedCounter.getCounterValueChangeInfo().getDeltaValue());
                counterInstanceService.incrementCounterValue(deducedCounter.getCounterValueChangeInfo().getCounterPeriodId(), deducedCounter.getCounterValueChangeInfo().getDeltaValue());
            }
            throw e;
        }
    }

    /**
     * Rate an EDR using counters if they apply. EDR status will be updated to Rejected even if exception is thrown
     * 
     * @param edrId EDR id to rate
     * @throws BusinessException business exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void ratePostpaidUsage(Long edrId) throws BusinessException, RatingException {

        try {
            methodCallingUtils.callMethodInNewTx(()->rateUsage(edrId, false, 0, 0));

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edrId, e.getRejectionReason());
            methodCallingUtils.callMethodInNewTx(()->rejectEDR(edrId, e));
            throw e;

        } catch (Exception e) {
            log.error("Failed to rate EDR {}: {}", edrId, e.getMessage(), e);
            methodCallingUtils.callMethodInNewTx(()->rejectEDR(edrId, e));
            throw e;
        }
    }

    /**
     * Rate a a list of EDRs a a batch. Counters are used if they apply. All EDRs MUST succeed. EDR status will NOT be updated to Rejected even if exception is thrown - any changes will simply be rolledback.
     * 
     * @param edrIds A list of EDR ids to rate
     * @throws BusinessException business exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void ratePostpaidUsage(List<Long> edrIds) throws BusinessException, RatingException {
        try {
            List<EDR> edrs = findEdrsListByIdsandSubscription(edrIds);

            for (EDR edr : edrs) {
                rateUsage(edr, false, false, 0, 0, null, false);
            }

        } catch (RatingException e) {
            log.trace("Failed to rate EDRs {}: {}", edrIds, e.getRejectionReason());
            throw e;

        } catch (Exception e) {
            log.error("Failed to rate EDRs {}: {}", edrIds, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Find an EDR with its id and fetch subscription.
     *
     * @author Mohamed Ali Hammal
     * @param edrid an EDR id
     * @return A single EDR
     */
    public EDR findEdrByIdandSubscription(Long edrid) {
        EntityManager em = getEntityManager();
        EDR edr = em.createNamedQuery("EDR.findByIdWithSubscription", EDR.class).setParameter("id", edrid).getSingleResult();
        return edr;
    }

    /**
     * Find a list of EDRs with a list of corresponding ids and fetch subscriptions.
     *
     * @author Mohamed Ali Hammal
     * @param edrIds EDR list of ids
     * @return A list of EDRs
     */
    public List<EDR> findEdrsListByIdsandSubscription(List<Long> edrIds) {
        EntityManager em = getEntityManager();
        List<EDR> edrsList = em.createNamedQuery("EDR.findByIdsWithSubscription", EDR.class).setParameter("ids", edrIds).getResultList();
        return edrsList;
    }

    /**
     * Rate a virtual EDR. Data will not be persisted
     * 
     * @param edr EDR to rate
     * @return A list of wallet operations
     * @throws BusinessException A general exception
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateVirtualEDR(EDR edr) throws BusinessException, RatingException {
        return rateUsage(edr, true, false, 0, 0, null, false);
    }

    /**
     * Rate EDR in a new transaction.
     * 
     * @param edrId EDR id
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @return A list of wallet operations corresponding to rated EDR
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateUsage(Long edrId, boolean rateTriggeredEdr, int maxDeep, int currentRatingDepth) throws BusinessException, RatingException {

        EDR edr = findEdrByIdandSubscription(edrId);
        return rateUsage(edr, false, rateTriggeredEdr, maxDeep, currentRatingDepth, null, false);
    }

    /**
     * Rate EDR. Change status to RATED if successfully rated or REJECTED with rejection reason specified.
     * 
     * @param edr EDR
     * @param isVirtual Is this a virtual operation and no real counters or wallets should be affected (applies to quotes)
     * @param rateTriggeredEdr check whether the rating for triggered EDR is enabled or not.
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @param reservation - Reservation the rating is for
     * @param failSilently If true, any error will be reported and returned in the rating result instead of throwing an exception
     * @return Rating result containing rated wallet operations (persisted when currentRatingDepth is 0) and triggered EDRs (persisted when currentRatingDepth is 0)
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public RatingResult rateUsage(EDR edr, boolean isVirtual, boolean rateTriggeredEdr, Integer maxDeep, Integer currentRatingDepth, Reservation reservation, boolean failSilently) {

        log.trace("Rating EDR={}", edr);

        RatingResult ratingResult = new RatingResult();

        try {
            if (edr.getQuantity() == null) {
                throw new RatingException(EDRRejectReasonEnum.QUANTITY_IS_NULL);
            }

            if (edr.getSubscription() == null) {
                throw new RatingException(EDRRejectReasonEnum.SUBSCRIPTION_IS_NULL);
            }

            List<UsageChargeInstance> usageChargeInstances = null;
            maxDeep = maxDeep != null ? maxDeep : 0;
            currentRatingDepth = currentRatingDepth != null ? currentRatingDepth : 0;
            // Charges should be already ordered by priority and id (why id??)
            Long subscriptionId = edr.getSubscription().getId();
            if (subscriptionId != null) {
                
                boolean isSubscriptionInitialized = Hibernate.isInitialized(edr.getSubscription());
                
                usageChargeInstances = usageChargeInstanceService.getUsageChargeInstancesValidForDateBySubscriptionId(edr.getSubscription(), edr.getEventDate());
                if (usageChargeInstances == null || usageChargeInstances.isEmpty()) {
                    throw new NoChargeException("No active usage charges are associated with subscription " + subscriptionId);
                }

                // Just to load all subscription service instances with their attributes to avoid querying service instances and their attributes one by one. 
                // Done once per subscription (in same tx) - a fix when EDR batch to rate is based on same subscription, or when EDR triggers other EDRs 
                if (!isSubscriptionInitialized) {
                    List<ServiceInstance> subscriptionServices = getEntityManager().createNamedQuery("ServiceInstance.findBySubscriptionIdLoadAttributes", ServiceInstance.class).setParameter("subscriptionId", subscriptionId)
                        .getResultList();
                    UserAccount userAccount = userAccountService.findById(edr.getSubscription().getUserAccount().getId(), Arrays.asList("wallet"));
                }
                    
                // This covers a virtual rating case when estimating usage from a quote. Subscription in that case was not persisted.
            } else if (edr.getSubscription().getServiceInstances() != null) {
                usageChargeInstances = edr.getSubscription().getServiceInstances().stream().flatMap(si -> si.getUsageChargeInstances().stream()).collect(toList());
                if (usageChargeInstances.isEmpty()) {
                    throw new NoChargeException("No usage charges are associated with subscription " + subscriptionId);
                }
            }

            boolean fullyRated = false;
            
            // Find the first matching charge and rate it
            for (UsageChargeInstance usageChargeInstance : usageChargeInstances) {
          
                log.trace("Try to rate EDR {} with charge {}", edr.getId(), usageChargeInstance.getCode());
                if (!isChargeMatch(usageChargeInstance, edr)) {
                    continue;
                }

                log.debug("Will apply matching charge instance id={} for EDR {}", usageChargeInstance.getId(), edr.getId());

                RatingResult localRatedEDRResult = rateEDRonChargeAndCounterAndInstantiateTriggeredEDRs(edr, usageChargeInstance, reservation, isVirtual);
                ratingResult.add(localRatedEDRResult);

                if (localRatedEDRResult.getWalletOperations() != null) {

                    if (rateTriggeredEdr && localRatedEDRResult.getTriggeredEDRs() != null && !localRatedEDRResult.getTriggeredEDRs().isEmpty()) {
                        RatingResult triggeredEDRRatedResult = rateTriggeredEDRs(isVirtual, maxDeep, currentRatingDepth, localRatedEDRResult.getTriggeredEDRs(), reservation);
                        ratingResult.add(triggeredEDRRatedResult);
                    }

                    if (localRatedEDRResult.isFullyRated()) {
                        fullyRated = true;

                        UsageChargeTemplate usageChargeTemplate = usageChargeInstance.getUsageChargeTemplate();

                        boolean triggerNextCharge = usageChargeTemplate.getTriggerNextCharge();

                        if (!StringUtils.isBlank(usageChargeTemplate.getTriggerNextChargeEL())) {
                            triggerNextCharge = evaluateBooleanExpression(usageChargeTemplate.getTriggerNextChargeEL(), edr, localRatedEDRResult.getWalletOperations().get(0));
                        }
                        if (!triggerNextCharge) {
                            break;
                        }
                    }
                }
            }

            if (!fullyRated) {
                throw new NoChargeException(EDRRejectReasonEnum.NO_MATCHING_CHARGE, "No charge matched to fully rate EDR " + edr.getId());
            }

            // Apply accumulator counters
            incrementAccumulatorCounterValues(ratingResult.getWalletOperations(), ratingResult, isVirtual);

            edr.changeStatus(EDRStatusEnum.RATED);
            edr.setRejectReason(null);
            
            WalletOperation walletOperation = ratingResult.getWalletOperations().stream().filter(e -> e.getEdr().equals(edr)).findFirst().orElse(null);
            if (walletOperation != null) {
            	edr.setBusinessKey(walletOperation.getBusinessKey());
            }

            // If not virtual, persist triggered EDRs and created Wallet operations
            if (!isVirtual && currentRatingDepth == 0) {
                if (ratingResult.getTriggeredEDRs() != null) {
                    for (EDR triggeredEdr : ratingResult.getTriggeredEDRs()) {
                        edrService.create(triggeredEdr);
                    }
                }

                for (WalletOperation wo : ratingResult.getWalletOperations()) {
	                checkDiscountedWalletOpertion(wo, ratingResult.getWalletOperations());
                    walletOperationService.chargeWalletOperation(wo);
                }
            }

        } catch (EJBTransactionRolledbackException e) {
            revertCounterChanges(ratingResult.getCounterChanges());
            throw e;

        } catch (Exception e) {
            rejectEDR(edr, e, currentRatingDepth > 0, currentRatingDepth > 0);
            revertCounterChanges(ratingResult.getCounterChanges());

            if (failSilently) {
                return new RatingResult(e);
            } else {
                throw e;
            }
        }

        return ratingResult;
    }

    /**
     * Rate Triggered EDR. Note: does not persist EDRs nor Wallet operations. Any rating errors will mark a particular EDR as rejected with a rejection reason specified. No errors are thrown.
     *
     * @param isVirtual rate EDR virtually (no persisting counters in DB)
     * @param maxDeep The max level of triggered EDR rating depth
     * @param currentRatingDepth Tracks the current triggered EDR rating depth
     * @param triggeredEDRs EDRs that were triggered by a charge processing
     * @param reservation - Reservation the rating is for
     * @return RatingResult Rating result
     */
    private RatingResult rateTriggeredEDRs(boolean isVirtual, int maxDeep, int currentRatingDepth, List<EDR> triggeredEDRs, Reservation reservation) {

        RatingResult ratingResult = new RatingResult();

        if (currentRatingDepth <= maxDeep) {
            for (EDR edr : triggeredEDRs) {
                // Do not throw the errors, but mark EDR as rejected with and error
                try {
                    RatingResult localRatingResult = rateUsage(edr, isVirtual, true, maxDeep, currentRatingDepth + 1, reservation, true);
                    ratingResult.add(localRatingResult);

                } catch (Exception e) {
                    rejectEDR(edr, e, false, false);
                }
            }
        }
        return ratingResult;
    }

    /**
     * Check if charge filter parameters match those of EDR.
     * 
     * @param chargeInstance Charge instance to match against EDR
     * @param edr EDR to check
     * @return true if charge is matched
     * @throws InvalidELException Failed to evaluate EL expression
     */
    private boolean isChargeMatch(UsageChargeInstance chargeInstance, EDR edr) throws InvalidELException {

        UsageChargeTemplate chargeTemplate = chargeInstance.getUsageChargeTemplate();
        
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

                            // Check if there is any attribute with value FALSE, indicating that service instance is not active
                            if (anyFalseAttributeMatch(chargeInstance)) {
                                return false;
                            }
                            return true;
                        }
                    }
                }
            }
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

        log.trace("Reserve EDR={}", edr);

        Reservation reservation = null;

        long time = System.currentTimeMillis();
        log.debug("Reserving EDR={}, we override the event date with the current date", edr);
        edr.setEventDate(new Date(time));

        reservation = new Reservation();
        reservation.setReservationDate(edr.getEventDate());
        reservation.setExpiryDate(new Date(time + appProvider.getPrepaidReservationExpirationDelayinMillisec()));
        reservation.setStatus(ReservationStatus.OPEN);
        reservation.updateAudit(currentUser);
        reservation.setOriginEdr(edr);
        reservation.setQuantity(edr.getQuantity());

        reservationService.create(reservation);

        RatingResult ratingResult = rateUsage(edr, false, false, 0, 0, reservation, false);

        for (WalletOperation wo : ratingResult.getWalletOperations()) {
            reservation.setAmountWithoutTax(reservation.getAmountWithoutTax().add(wo.getAmountWithoutTax()));
            reservation.setAmountWithTax(reservation.getAmountWithoutTax().add(wo.getAmountWithTax()));
        }

        return reservation;
    }

    private boolean matchExpression(UsageChargeInstance ci, String expression, EDR edr) throws InvalidELException {
        Map<Object, Object> userMap = new HashMap<>();
        userMap.put("edr", edr);
        userMap.put("ci", ci);
        return ValueExpressionWrapper.evaluateToBoolean(expression, userMap);
    }

    public boolean evaluateBooleanExpression(String expression, EDR edr, WalletOperation walletOperation) throws InvalidELException {
        boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<>();
        userMap.put("edr", edr);
        userMap.put("op", walletOperation);
        if (expression.indexOf(ValueExpressionWrapper.VAR_USER_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_USER_ACCOUNT, walletOperation.getWallet().getUserAccount());
        }
        if (expression.indexOf("serviceInstance") >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, walletOperation.getServiceInstance());
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CPQ_QUOTE) >= 0) {
            ServiceInstance service = walletOperation.getServiceInstance();
            if (service != null) {
                CpqQuote quote = service.getQuoteProduct() != null ? service.getQuoteProduct().getQuote() : null;
                if (quote != null) {
                    userMap.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
                }

            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_QUOTE_VERSION) >= 0) {
            ServiceInstance service = walletOperation.getServiceInstance();
            if (service != null) {
                QuoteVersion quoteVersion = service.getQuoteProduct() != null ? service.getQuoteProduct().getQuoteVersion() : null;
                if (quoteVersion != null) {
                    userMap.put(ValueExpressionWrapper.VAR_QUOTE_VERSION, quoteVersion);
                }

            }
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    public void rejectEDR(Long edrId, Exception e) {
        EDR edr = edrService.findById(edrId);
        rejectEDR(edr, e, true, true);
    }

    /**
     * Mark EDR as rejected or canceled when no active charges were found
     * 
     * @param edr EDR
     * @param e Exception
     * @param markCanceledIfNoActiveCharges Should EDR be marked as CANCELED instead of REJECTED and CDR status be updated to TO_REPROCESS when no active charges were found
     * @param fireRejectEvent Shall EDR rejected event be fired. Note: no event will be fired for rating failure when no active charges were found
     */
    private void rejectEDR(EDR edr, Exception e, boolean markCanceledIfNoActiveCharges, boolean fireRejectEvent) {

        if (e instanceof RatingException) {
            log.error("Failed to rate EDR {}: {}", edr, ((RatingException) e).getRejectionReason(), e);
        } else {
            log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
        }

        String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage(), 255, true);
        edr.changeStatus(EDRStatusEnum.REJECTED);
        edr.setRejectReason(rejectReason);

        if (rejectReason != null && rejectReason.contains("No active usage")) {
            if (markCanceledIfNoActiveCharges) {
                edr.setRejectReason(rejectReason + " - CDR will be reprocessed");
                edr.changeStatus(EDRStatusEnum.CANCELLED);
                CDR cdr = cdrService.findByEdr(edr);
                if (cdr != null) {
                    cdr.setStatus(CDRStatusEnum.TO_REPROCESS);
                }
            }
        } else if (fireRejectEvent) {
            rejectedEdrProducer.fire(edr);
        }
    }
}