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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.BusinessException.ErrorContextAttributeEnum;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.RatingException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.Rejected;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.DatePeriod;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;

import static java.util.Arrays.asList;

@Stateless
public class RecurringRatingService extends RatingService implements Serializable {

    private static final long serialVersionUID = -9015227381576471689L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    /** The calendar service. */
    @Inject
    private CalendarService calendarService;

    @Inject
    @Rejected
    Event<Serializable> rejectededChargeProducer;

    @Inject
    private EdrService edrService;

    /**
     * Apply a recurring charge. Same as rateRecurringCharge, but in new transaction<br>
     * 
     * @param chargeInstance Charge instance
     * @param chargeMode Charge application mode
     * @param forSchedule Is this a scheduled charge
     * @param prorateLastPeriodToDate An explicit date to charge to. Assumption - to reach end agreement date. Optional. If provided, the last period will be pro-rated up to a given date if date does not match the period
     *        end date.
     * @param orderNumberToOverride Order number to assign to Wallet operation. Optional. If provided, will override a value from chargeInstance.
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param allowIncompleteRating When rating spans multiple periods and a failure to rate (e.g. price not found) occurs, should rating throw an exception or consider the last successfully rated period as "good enough"
     *        for rating purpose
     * @return Rating result
     * @throws BusinessException General business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RatingResult rateReccuringChargeInNewTx(Long chargeInstanceId, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date prorateLastPeriodToDate, String orderNumberToOverride, boolean isVirtual,
            boolean allowIncompleteRating) throws BusinessException, RatingException {

        RecurringChargeInstance chargeInstance = getEntityManager().find(RecurringChargeInstance.class, chargeInstanceId);
        return rateReccuringCharge(chargeInstance, chargeMode, forSchedule, prorateLastPeriodToDate, orderNumberToOverride, isVirtual, allowIncompleteRating);
    }

    /**
     * Apply a recurring charge.<br>
     * Quantity might be prorated for first time charge (identified by chargeInstance.chargeDate=null) if subscription prorata is enabled on charge template. Will update charge instance with a new charge and next charge
     * dates. <br>
     * <br>
     * Recurring charge can be of two scenarious, depending on chargeInstance.applyInAdvance flag of its EL expression:
     * <ul>
     * <li>Apply charge that is applied at the end of calendar period - for charge instance with appliedInAdvance = false</li>
     * <li>Apply the recurring charge in advance of calendar period - for charge instance with appliedInAdvance = true</li>
     * </ul>
     *
     * <b>Apply charge that is applied at the end of calendar period</b> applyInAdvance = false:<br>
     * <br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.nextChargeDate, wo.startDate = chargeInstance.chargeDate and wo.endDate=chargeInstance.nextChargeDate.<br>
     * <br>
     * <b>Apply the recurring charge in advance of calendar period</b> applyInAdvance = true:<br>
     * <br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.chargeDate, wo.startDate = chargeInstance.chargeDate and wo.endDate=chargeInstance.nextChargeDate <br>
     * ---<br>
     * For non-reimbursement it will charge only the next calendar period cycle unless an explicit chargeToDate is provided. In such case last period might be prorated.<br>
     * For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged. Thus it might span multiple calendar periods with first period being .<br>
     * ---<br>
     * It will also update chargeInstance.chargeDate = chargeInstance.nextChargeDate and chargeInstance.nextChargeDate = nextCalendarDate(chargeInstance.nextChargeDate)
     * 
     *
     * @param chargeInstance Charge instance
     * @param chargeMode Charge application mode
     * @param forSchedule Is this a scheduled charge
     * @param prorateLastPeriodToDate An explicit date to charge to the last period. Assumption - to reach end agreement date. Optional. If provided, the last period will be pro-rated up to a given date if date does not
     *        match the period end date.
     * @param orderNumberToOverride Order number to assign to Wallet operation. Optional. If provided, will override a value from chargeInstance.
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param allowIncompleteRating When rating spans multiple periods and a failure to rate (e.g. price not found) occurs, should rating throw an exception or consider the last successfully rated period as "good enough"
     *        for rating purpose
     * @return Rating result containing a rated wallet operation (persisted) and triggered EDRs (persisted)
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateReccuringCharge(RecurringChargeInstance chargeInstance, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date prorateLastPeriodToDate, String orderNumberToOverride,
            boolean isVirtual, boolean allowIncompleteRating) throws BusinessException, RatingException {

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

        Date applyChargeFromDate;
        Date applyChargeToDate;

        boolean prorateFirstPeriod = false;
        boolean prorateLastPeriod = false;
        Date prorateFirstPeriodFromDate = null;

        RatingResult ratingResult = new RatingResult();
        CounterValueChangeInfo firstChargeCounterChange = null;
        List<DatePeriod> periods = new ArrayList<>();

        // Check if there is any attribute with value FALSE, indicating that service instance is not active
        if (anyFalseAttributeMatch(chargeInstance)) {
            return new RatingResult();
        }

        // -- Determine charge period, prorating for the first termination period and prorating of first subscription period

        // For reimbursement need to reimburse earlier applied recurring charges starting from "apply charge to upon termination" date to the last date charged (chargedToDate).
        // This might span multiple calendar periods and might require a first period proration. Here none of WOs are created yet and chargedToDate reflects the real last charged
        // to date.
        if (chargeMode == ChargeApplicationModeEnum.REIMBURSMENT) {

            if (chargeInstance.getChargedToDate() == null) {
                // Trying to reimburse something that was not charged yet
                log.error("Trying to reimburse a charge {} that was not charged yet. Will skip.", chargeInstance.getId());
                return new RatingResult();
            }

            applyChargeFromDate = chargeInstance.getChargeToDateOnTermination();
            applyChargeToDate = chargeInstance.getChargedToDate();

            // Take care of the first charge period that termination date falls into
            // Determine if first period proration is needed and is requested
            prorateFirstPeriodFromDate = getRecurringPeriodStartDate(chargeInstance, applyChargeFromDate);
            if (prorateFirstPeriodFromDate.before(applyChargeFromDate)) {
                prorateFirstPeriod = isProrateTerminationCharges(chargeInstance);

            } else {
                prorateFirstPeriod = false;
                prorateFirstPeriodFromDate = null;
            }

            // For rerating reimbursement need to reimburse earlier applied recurring charges starting from the last date charged (chargedToDate) to the end of the period or the
            // chargeToDate.
            // This might span multiple calendar periods and might require a first period proration.
            // As in RecurringChargeInstanceService.resetRecurringCharge() chargedToDate will be reset to a fromDate or a later date (depending on rerateInvoiced
            // flag), need to reimburse not from chargeToDateOnTermination, but from what chargedToDate was reset to.
            // And chargeToDate (not chargedToDate) is expected as to know what date to reimburse charges to
        } else if (chargeMode == ChargeApplicationModeEnum.RERATING_REIMBURSEMENT) {

            if (chargeInstance.getChargedToDate() == null) {
                // Trying to reimburse something that was not charged yet
                log.error("Trying to reimburse a charge {} that was not charged yet. Will skip.", chargeInstance.getId());
                return new RatingResult();
            }

            applyChargeFromDate = chargeInstance.getChargedToDate();

            DatePeriod period = getRecurringPeriod(chargeInstance, applyChargeFromDate);
            applyChargeToDate = period.getTo();

            if (prorateLastPeriodToDate != null) {
                applyChargeToDate = prorateLastPeriodToDate;
            }

            // Take care of the first charge period that termination date falls into
            // Determine if first period proration is needed and is requested
            if (period.getFrom().before(applyChargeFromDate)) {
                prorateFirstPeriod = isProrateTerminationCharges(chargeInstance);
                prorateFirstPeriodFromDate = period.getFrom();

            } else {
                prorateFirstPeriod = false;
                prorateFirstPeriodFromDate = null;
            }

            // For non-reimbursement it will cover only one calendar period cycle unless an explicit chargeToDate is specified.
            // In such case this might span multiple calendar periods and might require a last period proration
            // Initialize charge and determine prorata ratio if applying a charge for the first time
        } else {

            applyChargeFromDate = chargeInstance.getChargedToDate();

            boolean isFirstCharge = false;

            // First time charge
            if (chargeInstance.getChargedToDate() == null) {
                applyChargeFromDate = chargeInstance.getSubscriptionDate();
                isFirstCharge = true;
            } else {
                isFirstCharge = chargeInstance.getChargedToDate().equals(chargeInstance.getSubscriptionDate());
            }

            DatePeriod period = getRecurringPeriod(chargeInstance, applyChargeFromDate);
            applyChargeToDate = period.getTo();
            if (prorateLastPeriodToDate != null) {
                applyChargeToDate = prorateLastPeriodToDate;
            }

            if(chargeInstance.isAnticipateEndOfSubscription()) {

                Date prorateLastPeriodDate = Stream.of(period.getTo(), chargeInstance.getSubscription().getSubscribedTillDate(),
                        chargeInstance.getServiceInstance().getSubscribedTillDate())
                        .filter(Objects::nonNull).min(Date::compareTo).orElse(null);
                if(prorateLastPeriodDate != null && prorateLastPeriodDate.compareTo(period.getTo()) != 0) {
                    applyChargeFromDate = period.getFrom();
                    applyChargeToDate = chargeInstance.getTerminationDate() != null ? chargeInstance.getTerminationDate() : prorateLastPeriodDate;
                    prorateLastPeriod = true;
                }
                if(chargeInstance.getSubscription().getSubscribedTillDate() != null) {
                    Date end = getRecurringPeriodEndDate(chargeInstance, chargeInstance.getSubscription().getSubscribedTillDate());
                    if (end != null && chargeInstance.getTerminationDate() != null && chargeInstance.getTerminationDate().after(end)) {
                        Date startDate = chargeInstance.getSubscription().getSubscribedTillDate();
                        while (chargeInstance.getTerminationDate().after(end)) {
                            DatePeriod datePeriod = new DatePeriod(startDate, end);
                            periods.add(datePeriod);
                            startDate = end;
                            end = getRecurringPeriodEndDate(chargeInstance, startDate);
                        }
                    }
                }
            }
            // When charging first time, need to determine if counter is available and prorata ratio if subscription charge proration is enabled
            if (isFirstCharge) {

                // The counter will be decremented by charge quantity
                CounterInstance counterInstance = chargeInstance.getCounter();
                if (!isVirtual && counterInstance != null) {
                	
                	CounterValueChangeInfo counterValueChangeInfo = counterInstanceService.deduceCounterValue_noLock(counterInstance, chargeInstance.getChargeDate(), chargeInstance.getServiceInstance().getSubscriptionDate(),
                				chargeInstance, chargeInstance.getQuantity(), isVirtual);
                	

                    boolean isApplyInAdvance = isApplyInAdvance(chargeInstance);

                    // If the counter was not deducted, then the charge is not applied (but next activation date is updated).
                    if (counterValueChangeInfo.getDeltaValue().equals(BigDecimal.ZERO)) {
                        chargeInstance.advanceChargeDates(applyChargeFromDate, applyChargeToDate, isApplyInAdvance ? applyChargeToDate : applyChargeFromDate);
                        return new RatingResult();
                    }
                    ratingResult.addCounterChange(counterValueChangeInfo.getCounterPeriodId(), counterValueChangeInfo.getDeltaValue());
                    firstChargeCounterChange = counterValueChangeInfo;
                }

                // Determine if subscription charge should be prorated
                prorateFirstPeriodFromDate = period.getFrom();
                if (period.getFrom().before(applyChargeFromDate)) {

                    boolean prorateSubscription = recurringChargeTemplate.getSubscriptionProrata() != null && recurringChargeTemplate.getSubscriptionProrata();
                    if (!StringUtils.isBlank(recurringChargeTemplate.getSubscriptionProrataEl())) {
                        prorateSubscription = matchExpression(recurringChargeTemplate.getSubscriptionProrataEl(), chargeInstance.getServiceInstance(), null, recurringChargeTemplate, chargeInstance);
                    }

                    prorateFirstPeriod = prorateSubscription;

                } else {
                    prorateFirstPeriod = false;
                    prorateFirstPeriodFromDate = null;
                }

                // If it is not a first time charge, it might still be need to prorate first period when rerating.
                // e.g. initial charge WO was 01/10 to 01/11. A change came in on 22/10 and two WOs were created instead: 01/10-22/10 and 22/10-01/11.
                // A second change came in on 27/10 and three WOs were created instead: 01/10-22/10, 22/10-27/10 and 27/10-01/11.
                // When processing a second change and rerating a period 22/10-01/11, it should be aware that proration shall be applied as initial full period was 01/10-01/11
            } else if (chargeMode == ChargeApplicationModeEnum.RERATING && period.getFrom().before(applyChargeFromDate)) {
                prorateFirstPeriod = true;
                prorateFirstPeriodFromDate = period.getFrom();
            }
        }

        if (applyChargeFromDate == null) {
            throw new IncorrectChargeInstanceException("nextChargeDate is null.");
        }

        log.debug("Will apply {} recuring charges for charge {}/{} for period(s) {} - {}.",
            chargeMode == ChargeApplicationModeEnum.REIMBURSMENT || chargeMode == ChargeApplicationModeEnum.RERATING_REIMBURSEMENT ? "reimbursement" : "", chargeInstance.getId(), chargeInstance.getCode(),
            applyChargeFromDate, applyChargeToDate);

        // -- Divide a period to charge into periods (or partial periods) and create WOs

        Date currentPeriodFromDate = applyChargeFromDate;
        int periodIndex = 0;

        Date effectiveChargeFromDate = null;
        Date effectiveChargeToDate = null;

        try {
            while (applyChargeToDate != null && currentPeriodFromDate.getTime() < applyChargeToDate.getTime()) {

                boolean isApplyInAdvance = isApplyInAdvance(chargeInstance);
                boolean prorate = false;

                // Check if prorating is needed on first period of termination reimbursement or on subscription
                effectiveChargeFromDate = currentPeriodFromDate;
                if (periodIndex == 0 && prorateFirstPeriodFromDate != null) {
                    currentPeriodFromDate = prorateFirstPeriodFromDate;
                    prorate = prorateFirstPeriod;
                }

                // Take care of the last charge period that termination date falls into
                // Check if prorating is needed on last period (this really should happen in Apply end agreement mode)
                Date currentPeriodToDate = getRecurringPeriodEndDate(chargeInstance, currentPeriodFromDate);
                // Handle date not in banking calendar period to avoid infinite loop
                Calendar cal = resolveRecurrenceCalendar(chargeInstance);
                if (cal == null) {
                    throw new IncorrectChargeTemplateException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
                }
                if(cal.getCalendarType() != null && cal.getCalendarType().equals("BANKING") && currentPeriodToDate != null && currentPeriodToDate.compareTo(currentPeriodFromDate) == 0) {
                	throw new IllegalStateException("The given date: " +currentPeriodFromDate +" is not in period [startDate,endDate] of banking Calendar: "+ cal.getCode());
                }
                
                effectiveChargeToDate = currentPeriodToDate.before(applyChargeToDate) ? currentPeriodToDate : applyChargeToDate;
                if (prorateLastPeriodToDate != null && currentPeriodToDate.after(prorateLastPeriodToDate)) {
                    effectiveChargeToDate = prorateLastPeriodToDate;

                    if (chargeInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                        prorate = prorate || isProrateTerminationCharges(chargeInstance);
                    }
                }

                // Handle a case of re-rating of recurring charge - existing WOs have been canceled and new ones are re-generated up to termination date only if it falls within the
                // rating period. prorateLastPeriodToDate is not passed in rerating cases.
                if ((chargeMode == ChargeApplicationModeEnum.RERATING || chargeMode == ChargeApplicationModeEnum.AGREEMENT) && chargeInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                    Date chargeToDateOnTermination = chargeInstance.getChargeToDateOnTermination();
                    // Termination date is not charged, its similar to a TO value in a range of dates - e.g. if termination date is on friday, friday will not be charged.
                    if (chargeToDateOnTermination != null && currentPeriodFromDate.compareTo(chargeToDateOnTermination) < 0 && chargeToDateOnTermination.compareTo(currentPeriodToDate) <= 0) {
                        effectiveChargeToDate = effectiveChargeToDate.before(chargeToDateOnTermination) ? effectiveChargeToDate : chargeToDateOnTermination;

                        if (chargeToDateOnTermination.compareTo(currentPeriodToDate) < 0) {
                            prorate = prorate || isProrateTerminationCharges(chargeInstance);
                        }
                    }
                }

                boolean chargeDatesAlreadyAdvanced = false;

                // If charge is not applicable for current period, skip it
                if (!RatingService.isORChargeMatch(chargeInstance)) {
                    log.debug("Not rating recurring chargeInstance {}/{}, filter expression or service attributes evaluated to FALSE", chargeInstance.getId(), chargeInstance.getCode());

                } else {

                    boolean alreadyInvoiced = false;
                    BigDecimal inputQuantity = chargeMode.isReimbursement() ? chargeInstance.getQuantity().negate() : chargeInstance.getQuantity();

                    if (chargeInstance != null && chargeInstance.getSubscription() != null
                            && chargeInstance.getSubscription().getSubscribedTillDate() != null
                            && chargeInstance.getRecurringChargeTemplate().getTerminationProrata()) {
                        prorate = true;
                        if(chargeInstance.getWalletOperations() != null && !chargeInstance.getWalletOperations().isEmpty()) {
                            final int lastIndex = chargeInstance.getWalletOperations().size() - 1;
                            List<WalletOperation> walletOperations = new ArrayList<>();
                            if(chargeInstance.getChargeToDateOnTermination().before(chargeInstance.getSubscription().getSubscribedTillDate())) {
                                walletOperations = chargeInstance.getWalletOperations()
                                        .stream()
                                        .filter(walletOperation -> walletOperation.getEndDate().after(chargeInstance.getChargeToDateOnTermination()))
                                        .collect(Collectors.toList());
                            } else {
                                walletOperations.add(chargeInstance.getWalletOperations().get(lastIndex));
                            }
                            alreadyInvoiced = walletOperations.stream().allMatch(this::isAlreadyInvoiced);
                            if(!alreadyInvoiced) {
                                walletOperationService.cancelWalletOperations(walletOperations.stream().map(WalletOperation::getId).collect(Collectors.toList()));
                                inputQuantity = inputQuantity.abs();
                                effectiveChargeFromDate =  chargeInstance.getCalendar().previousCalendarDate(chargeInstance.getChargeToDateOnTermination());
                            } else {
                                effectiveChargeFromDate = chargeInstance.getSubscription().getSubscribedTillDate();
                                if(chargeInstance.getSubscription().getSubscribedTillDate().before(chargeInstance.getChargeToDateOnTermination()) && alreadyInvoiced && !periods.isEmpty()) {
                                    effectiveChargeFromDate = getRecurringPeriodStartDate(chargeInstance, chargeInstance.getChargeToDateOnTermination());
                                }
                            }
                            effectiveChargeToDate = chargeInstance.getChargeToDateOnTermination();
                            if(effectiveChargeFromDate.after(effectiveChargeToDate)) {
                                effectiveChargeFromDate = chargeInstance.getChargeToDateOnTermination();
                                effectiveChargeToDate = chargeInstance.getSubscription().getSubscribedTillDate();
                            }
                            if(effectiveChargeFromDate.compareTo(effectiveChargeToDate) == 0 && chargeInstance.getSubscription() != null) {
                                effectiveChargeFromDate = getRecurringPeriodStartDate(chargeInstance, chargeInstance.getSubscription().getSubscribedTillDate());
                            }
                        }
                    }
                    // Apply prorating if needed
                    if (prorate || prorateLastPeriod) {
                        BigDecimal prorata = DateUtils.calculateProrataRatio(effectiveChargeFromDate, effectiveChargeToDate, currentPeriodFromDate, currentPeriodToDate, false);
                        if (prorata == null) {
                            throw new RatingException("Failed to calculate prorating for charge id=" + chargeInstance.getId() + " : periodFrom=" + currentPeriodFromDate + ", periodTo=" + currentPeriodToDate
                                    + ", proratedFrom=" + effectiveChargeFromDate + ", proratedTo=" + effectiveChargeToDate);
                        }
                        if(effectiveChargeFromDate.after(effectiveChargeToDate)) {
                            inputQuantity = inputQuantity.negate();
                        }
                    }
                    if((!prorate && !prorateLastPeriod) && chargeInstance.getTerminationDate() != null && chargeInstance.getTerminationDate().compareTo(new Date()) < 0) {
                        effectiveChargeToDate = chargeInstance.getTerminationDate();
                        effectiveChargeFromDate = getRecurringPeriodStartDate(chargeInstance, chargeInstance.getChargeToDateOnTermination());
                        inputQuantity = computeProrate(chargeInstance, effectiveChargeFromDate,
                                effectiveChargeToDate, currentPeriodFromDate, currentPeriodToDate, inputQuantity);
                    }

                    if (chargeMode.isReimbursement()) {
                        log.debug("Applying {} recurring charge {} for period {} - {}, quantity {}", "reimbursement", chargeInstance.getId(), effectiveChargeFromDate, effectiveChargeToDate, inputQuantity);
                    } else {
                        log.debug("Applying {} recurring charge {} for period {} - {}, quantity {}", isApplyInAdvance ? "start of period" : "end of period", chargeInstance.getId(), effectiveChargeFromDate,
                            effectiveChargeToDate, inputQuantity);
                    }

                    if (recurringChargeTemplate.isProrataOnPriceChange()) {
                        RatingResult localRatingResult = generateWalletOperationsByPricePlan(chargeInstance, chargeMode, forSchedule, effectiveChargeFromDate, effectiveChargeToDate,
                            prorate ? new DatePeriod(currentPeriodFromDate, currentPeriodToDate) : null, inputQuantity, orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(),
                            isApplyInAdvance, isVirtual);

                        ratingResult.add(localRatingResult);

                    } else {

                        Date oldChargedToDate = chargeInstance.getChargedToDate();

                        Date operationDate = isApplyInAdvance ? effectiveChargeFromDate : effectiveChargeToDate;
                        // Any operation past the termination date is invoiced with termination date
                        if (chargeInstance.getTerminationDate() != null && operationDate.after(chargeInstance.getTerminationDate())) {
                            operationDate = chargeInstance.getTerminationDate();
                        }

                        if(chargeInstance != null
                                && chargeInstance.getSubscription() != null

                                && chargeInstance.getSubscription().getSubscribedTillDate() != null
                                && ( chargeInstance.getSubscription().getSubscribedTillDate().before(effectiveChargeToDate) || chargeInstance.getSubscription().getSubscribedTillDate().compareTo(effectiveChargeToDate) == 0)
                                && inputQuantity.compareTo(BigDecimal.ONE) == 0) {
	                        effectiveChargeFromDate = operationDate;
                            inputQuantity = computeProrate(chargeInstance, effectiveChargeFromDate,
                                    effectiveChargeToDate, currentPeriodFromDate, currentPeriodToDate, chargeInstance.getQuantity());
                        }

                        RatingResult localRatingResult = rateChargeAndInstantiateTriggeredEDRs(chargeInstance, operationDate, inputQuantity, null,
                            orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), effectiveChargeFromDate, effectiveChargeToDate,
                            prorate ? new DatePeriod(currentPeriodFromDate, currentPeriodToDate) : null, chargeMode, null, null, forSchedule, isVirtual);
                        ratingResult.add(localRatingResult);
                        for (DatePeriod datePeriod : periods) {
                            BigDecimal prorata = DateUtils.calculateProrataRatio(datePeriod.getFrom(), datePeriod.getTo(),  currentPeriodFromDate, currentPeriodToDate, false);
                            localRatingResult = rateChargeAndInstantiateTriggeredEDRs(chargeInstance, operationDate, BigDecimal.ONE.multiply(prorata), null,
                                    orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), datePeriod.getFrom(), datePeriod.getTo(),
                                    prorate ? new DatePeriod(currentPeriodFromDate, currentPeriodToDate) : null, chargeMode, null, null, forSchedule, isVirtual);
                            ratingResult.add(localRatingResult);
                        }

                        // Check if rating script modified a chargedTo date
                        chargeDatesAlreadyAdvanced = DateUtils.compare(oldChargedToDate, chargeInstance.getChargedToDate()) != 0;

                    }

                }

                // Update charge, nextCharge and chargedToDates if not advanced already in some rating script
                if (!chargeDatesAlreadyAdvanced) {
                    if (isApplyInAdvance) {
                        chargeInstance.advanceChargeDates(effectiveChargeFromDate, effectiveChargeToDate, effectiveChargeToDate);
                    } else {
                        chargeInstance.advanceChargeDates(effectiveChargeToDate, getRecurringPeriodEndDate(chargeInstance, effectiveChargeToDate), effectiveChargeToDate);
                    }
                }

                currentPeriodFromDate = chargeInstance.getChargedToDate();
                periodIndex++;

                // Handle a case of infinite loop when chargeToDate is null (regular charging), but period was shortened (e.g. rating up to the termination/end aggreement date only
                // for terminated charges)
                if (effectiveChargeToDate.compareTo(currentPeriodToDate) != 0) {
                    break;
                }
            }
            
        } catch (EJBTransactionRolledbackException e) {
            revertCounterChanges(ratingResult.getCounterChanges());
            throw e;

        } catch (Exception e) {

            // Failed to rate the first period. Revert counter changes
            if (periodIndex == 0 && firstChargeCounterChange != null) {
                log.error("Failed to rate charge {}, will revert the applied counter {} by {}", chargeInstance.getId(), firstChargeCounterChange.getCounterPeriodId(), firstChargeCounterChange.getDeltaValue());
                counterInstanceService.incrementCounterValue(firstChargeCounterChange.getCounterPeriodId(), firstChargeCounterChange.getDeltaValue());
            }

            if (e instanceof BusinessException) {
                ((BusinessException) e).addErrorContext(ErrorContextAttributeEnum.RATING_PERIOD, new DatePeriod(effectiveChargeFromDate, effectiveChargeToDate));
            }

            if (!allowIncompleteRating) {
                throw e;
            }
        }

        incrementAccumulatorCounterValues(ratingResult.getWalletOperations(), ratingResult, isVirtual);

        // If not virtual, persist triggered EDRs and created Wallet operations
        if (!isVirtual) {
            if (ratingResult.getTriggeredEDRs() != null) {
                for (EDR triggeredEdr : ratingResult.getTriggeredEDRs()) {
                    edrService.create(triggeredEdr);
                }
            }

            for (WalletOperation wo : ratingResult.getWalletOperations()) {
                if(forSchedule){
                    wo.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                }
	            checkDiscountedWalletOpertion(wo, ratingResult.getWalletOperations());
                walletOperationService.chargeWalletOperation(wo);
            }
        }

        return ratingResult;
    }

    private BigDecimal computeProrate(RecurringChargeInstance chargeInstance,
                                      Date effectiveChargeFromDate, Date effectiveChargeToDate,
                                      Date currentPeriodFromDate, Date currentPeriodToDate, BigDecimal inputQuantity) {
        BigDecimal prorata = DateUtils.calculateProrataRatio(effectiveChargeFromDate,
                effectiveChargeToDate, currentPeriodFromDate, currentPeriodToDate, false);
        if (prorata == null) {
            throw new RatingException("Failed to calculate prorating for charge id=" + chargeInstance.getId()
                    + " : periodFrom=" + currentPeriodFromDate + ", periodTo=" + currentPeriodToDate
                    + ", proratedFrom=" + effectiveChargeFromDate + ", proratedTo=" + effectiveChargeToDate);
        }
        inputQuantity = inputQuantity.multiply(prorata)
                .setScale(appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        if(effectiveChargeFromDate.after(effectiveChargeToDate)) {
            inputQuantity = inputQuantity.negate();
        }
        return inputQuantity;
    }

    private boolean isAlreadyInvoiced(WalletOperation walletOperation) {
        return walletOperation.getRatedTransaction() != null
                && walletOperation.getRatedTransaction().getStatus().equals(RatedTransactionStatusEnum.BILLED)
                && (walletOperation.getRatedTransaction().getInvoiceLine() != null
                && walletOperation.getRatedTransaction().getInvoiceLine().getStatus().equals(InvoiceLineStatusEnum.BILLED));
    }

    /**
     * Determine recurring period start date
     *
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period start date
     * @throws IncorrectChargeTemplateException Unable to determine or no recurring calendar is specified for a charge
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws ElementNotFoundException Calendar, as resolved from EL expression was not found
     */
    public Date getRecurringPeriodStartDate(RecurringChargeInstance chargeInstance, Date date) throws IncorrectChargeTemplateException, ElementNotFoundException, InvalidELException {

        Calendar cal = resolveRecurrenceCalendar(chargeInstance);
        if (cal == null) {
            throw new IncorrectChargeTemplateException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }

        cal = CalendarService.initializeCalendar(cal, chargeInstance.getSubscriptionDate(), chargeInstance);

        Date previousChargeDate = cal.previousCalendarDate(cal.truncateDateTime(date));
        return previousChargeDate;
    }

    /**
     * Determine recurring period end date
     *
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period end date
     * @throws IncorrectChargeTemplateException Unable to determine or no recurring calendar is specified for a charge
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws ElementNotFoundException Calendar, as resolved from EL expression was not found
     */
    public Date getRecurringPeriodEndDate(RecurringChargeInstance chargeInstance, Date date) throws IncorrectChargeTemplateException, ElementNotFoundException, InvalidELException {

        Calendar cal = resolveRecurrenceCalendar(chargeInstance);
        if (cal == null) {
            throw new IncorrectChargeTemplateException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }

        cal = CalendarService.initializeCalendar(cal, chargeInstance.getSubscriptionDate(), chargeInstance);

        Date nextChargeDate = cal.nextCalendarDate(cal.truncateDateTime(date));
        return nextChargeDate;
    }

    /**
     * Determine recurring period start and end dates
     * 
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period
     * @throws IncorrectChargeTemplateException Unable to determine or no recurring calendar is specified for a charge
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws ElementNotFoundException Calendar, as resolved from EL expression was not found
     */
    public DatePeriod getRecurringPeriod(RecurringChargeInstance chargeInstance, Date date) throws IncorrectChargeTemplateException, ElementNotFoundException, InvalidELException {

        Calendar cal = resolveRecurrenceCalendar(chargeInstance);
        if (cal == null) {
            throw new IncorrectChargeTemplateException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }

        cal = CalendarService.initializeCalendar(cal, chargeInstance.getSubscriptionDate(), chargeInstance);

        Date startPeriodDate = cal.previousCalendarDate(cal.truncateDateTime(date));
        Date endPeriodDate = cal.nextCalendarDate(cal.truncateDateTime(date));

        return new DatePeriod(startPeriodDate, endPeriodDate);
    }

    /**
     * Get a recurrence calendar of a recurring charge
     * 
     * @param chargeInstance Charge instance
     * @return Calendar
     * 
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws ElementNotFoundException Calendar, as resolved from EL expression was not found
     */
    private Calendar resolveRecurrenceCalendar(RecurringChargeInstance chargeInstance) throws ElementNotFoundException, InvalidELException {
        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        Calendar cal = null;
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), null, recurringChargeTemplate, chargeInstance);
        }
        if (cal == null) {
            cal = chargeInstance.getCalendar();
        }
        return cal;
    }

    /**
     * Determine if charge should be applied in advance
     * 
     * @param recurringChargeInstance Recurring charge instance
     * @return True if charge is applied in advance
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public boolean isApplyInAdvance(RecurringChargeInstance recurringChargeInstance) throws InvalidELException {
        boolean isApplyInAdvance = recurringChargeInstance.getApplyInAdvance() != null && recurringChargeInstance.getApplyInAdvance();
        if (!StringUtils.isBlank(recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl())) {
            isApplyInAdvance = matchExpression(recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl(), recurringChargeInstance.getServiceInstance(), null, null, recurringChargeInstance);
        }

        return isApplyInAdvance;
    }

    /**
     * Shall termination or reimbursement charges be prorated
     *
     * @param chargeInstance Recurring charge instance
     * @return True if termination charges should be prorated
     * @throws InvalidELException Failed to evaluate EL expression
     */
    private boolean isProrateTerminationCharges(RecurringChargeInstance chargeInstance) throws InvalidELException {

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        boolean isTerminationProrata = recurringChargeTemplate.getTerminationProrata() != null && recurringChargeTemplate.getTerminationProrata();
        if (!StringUtils.isBlank(recurringChargeTemplate.getTerminationProrataEl())) {
            isTerminationProrata = matchExpression(recurringChargeTemplate.getTerminationProrataEl(), chargeInstance.getServiceInstance(), null, null, chargeInstance);
        }
        boolean prorate;
        if (chargeInstance.getServiceInstance().getSubscriptionTerminationReason() == null) {
            prorate = isTerminationProrata;
        } else {
            prorate = chargeInstance.getServiceInstance().getSubscriptionTerminationReason().getOverrideProrata().isToProrate(isTerminationProrata);
        }
        return prorate;
    }

    // TODO AKK what if rateCharge would return multiple WOs as alternative to this method??
    private RatingResult generateWalletOperationsByPricePlan(RecurringChargeInstance chargeInstance, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date periodStartDate, Date periodEndDate,
            DatePeriod fullRatingPeriod, BigDecimal inputQuantity, String orderNumberToOverride, boolean isApplyInAdvance, boolean isVirtual) {

        String recurringChargeTemplateCode = chargeInstance.getRecurringChargeTemplate().getCode();

        RatingResult ratingResult = new RatingResult();

        pricePlanMatrixService.getActivePricePlansByChargeCode(recurringChargeTemplateCode).stream()
            .filter(ppm -> !shouldNotIncludePPM(periodStartDate, periodEndDate, ppm.getValidityFrom(), ppm.getValidityDate()) && (ppm.getValidityFrom() != null && periodEndDate.after(ppm.getValidityFrom())))
            .forEach(pricePlanMatrix -> {

                Date computedApplyChargeOnDate = pricePlanMatrix.getValidityFrom().after(periodStartDate) ? pricePlanMatrix.getValidityFrom() : periodStartDate;
                Date computedNextChargeDate = pricePlanMatrix.getValidityDate() != null && pricePlanMatrix.getValidityDate().before(periodEndDate) ? pricePlanMatrix.getValidityDate() : periodEndDate;
                double ratio = computeQuantityRatio(computedApplyChargeOnDate, computedNextChargeDate);
                BigDecimal computedInputQuantityHolder = inputQuantity.multiply(new BigDecimal(ratio + ""));

                RatingResult localRatingResult = rateChargeAndInstantiateTriggeredEDRs(chargeInstance, isApplyInAdvance ? computedApplyChargeOnDate : computedNextChargeDate, computedInputQuantityHolder, null,
                    orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), computedApplyChargeOnDate, computedNextChargeDate, fullRatingPeriod, chargeMode, null, null, forSchedule,
                    false);

                ratingResult.add(localRatingResult);

            });

        return ratingResult;
    }

    private boolean shouldNotIncludePPM(Date applyChargeOnDate, Date nextChargeDate, Date from, Date to) {
        return (to != null && (to.before(applyChargeOnDate) || to.getTime() == applyChargeOnDate.getTime())) || (from != null && (from.after(nextChargeDate) || from.getTime() == nextChargeDate.getTime()));
    }

    private double computeQuantityRatio(Date computedApplyChargeOnDate, Date computedNextChargeDate) {
        int lengthOfMonth = 30;
        double days = DateUtils.daysBetween(computedApplyChargeOnDate, computedNextChargeDate);
        return days > lengthOfMonth ? 1 : days / lengthOfMonth;
    }

    /**
     * Match expression.
     *
     * @param expression Expression to evaluate
     * @param serviceInstance Service instance
     * @param serviceTemplate Service template
     * @param recurringChargeTemplate Recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return true, if successful
     * @throws InvalidELException Failed to evaluate EL expression
     */
    public boolean matchExpression(String expression, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate, RecurringChargeInstance recurringChargeInstance)
            throws InvalidELException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, serviceInstance, serviceTemplate, recurringChargeTemplate, recurringChargeInstance);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
    }

    /**
     * Evaluate string expression.
     *
     * @param expression the expression
     * @param serviceInstance the service instance
     * @param serviceTemplate the service template
     * @param recurringChargeTemplate the recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return the evaluated string
     * @throws InvalidELException Failed to evaluate EL expression
     */
    private String evaluateStringExpression(String expression, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate,
            RecurringChargeInstance recurringChargeInstance) throws InvalidELException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, serviceInstance, serviceTemplate, recurringChargeTemplate, recurringChargeInstance);

        return ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }

    /**
     * Resolve the calendar from el.
     *
     * @param calendarCodeEl the calendar code el
     * @param serviceInstance the service instance
     * @param serviceTemplate the service template
     * @param recurringChargeTemplate the recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return the calendar from calendar code el
     * @throws InvalidELException Failed to evaluate EL expression
     * @throws ElementNotFoundException Calendar, as resolved from EL expression was not found
     */
    public Calendar getCalendarFromEl(String calendarCodeEl, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate,
            RecurringChargeInstance recurringChargeInstance) throws ElementNotFoundException, InvalidELException {

        String calendarCode = evaluateStringExpression(calendarCodeEl, serviceInstance, serviceTemplate, recurringChargeTemplate, recurringChargeInstance);
        if (calendarCode != null) {
            Calendar calendar = calendarService.findByCode(calendarCode);
            if (calendar == null) {
                throw new ElementNotFoundException(calendarCode + " from EL '" + calendarCodeEl + "'", "Calendar");
            }
            return calendar;
        }
        return null;
    }

    /**
     * Construct el context.
     *
     * @param expression Expression to evaluate
     * @param serviceInstance Service instance
     * @param serviceTemplate Service template
     * @param recurringChargeTemplate Recurring charge template
     * @param recurringChargeInstance Recurring charge instance
     * @return the context el map
     */
    private Map<Object, Object> constructElContext(String expression, ServiceInstance serviceInstance, ServiceTemplate serviceTemplate, RecurringChargeTemplate recurringChargeTemplate,
            RecurringChargeInstance recurringChargeInstance) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_INSTANCE) >= 0) {
            if (serviceInstance == null && recurringChargeInstance != null) {
                serviceInstance = recurringChargeInstance.getServiceInstance();
            }
            userMap.put(ValueExpressionWrapper.VAR_SERVICE_INSTANCE, serviceInstance);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CPQ_QUOTE) >= 0) {
            ServiceInstance service = recurringChargeInstance.getServiceInstance();
            if (service != null) {
                CpqQuote quote = service.getQuoteProduct() != null ? service.getQuoteProduct().getQuote() : null;
                if (quote != null) {
                    userMap.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
                }

            }
        }

        if (expression.indexOf(ValueExpressionWrapper.VAR_QUOTE_VERSION) >= 0) {
            ServiceInstance service = recurringChargeInstance.getServiceInstance();
            if (service != null) {
                QuoteVersion quoteVersion = service.getQuoteProduct() != null ? service.getQuoteProduct().getQuoteVersion() : null;
                if (quoteVersion != null) {
                    userMap.put(ValueExpressionWrapper.VAR_QUOTE_VERSION, quoteVersion);
                }

            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_SERVICE_TEMPLATE) >= 0) {
            if (serviceTemplate != null) {
                userMap.put(ValueExpressionWrapper.VAR_SERVICE_TEMPLATE, serviceTemplate);
            } else if (serviceInstance != null) {
                userMap.put(ValueExpressionWrapper.VAR_SERVICE_TEMPLATE, serviceInstance.getServiceTemplate());
            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE) >= 0 || expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT) >= 0) {

            if (recurringChargeTemplate != null) {
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT, recurringChargeTemplate);
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE, recurringChargeTemplate);
            } else if (recurringChargeInstance != null) {
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE_SHORT, recurringChargeInstance.getRecurringChargeTemplate());
                userMap.put(ValueExpressionWrapper.VAR_CHARGE_TEMPLATE, recurringChargeInstance.getRecurringChargeTemplate());

            }
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CHARGE_INSTANCE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_CHARGE_INSTANCE, recurringChargeInstance);
        }

        if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
        }

        return userMap;
    }
}