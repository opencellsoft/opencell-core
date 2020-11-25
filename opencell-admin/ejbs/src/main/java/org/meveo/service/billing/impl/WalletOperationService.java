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

import static java.util.Collections.emptyList;
import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.BusinessException.ErrorContextAttributeEnum;
import org.meveo.admin.exception.IncorrectChargeInstanceException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.CounterValueChangeInfo;
import org.meveo.model.DatePeriod;
import org.meveo.model.IBillableEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.rating.RatingResult;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.transformer.AliasToAggregatedWalletOperationResultTransformer;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.filter.FilterService;

/**
 * Service class for WalletOperation entity
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Phung tien lan
 * @author anasseh
 * @author Abdellatif BARI
 * @author Mbarek-Ay
 * @lastModifiedVersion 7.0
 */
@Stateless
public class WalletOperationService extends PersistenceService<WalletOperation> {

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private RatingService ratingService;

    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private WalletService walletService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private SellerService sellerService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private TaxService taxService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private FilterService filterService;

    @EJB
    private RecurringChargeInstanceService recurringChargeInstanceService;

    public WalletOperation applyOneShotWalletOperation(Subscription subscription, OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Date applicationDate, boolean isVirtual,
            String orderNumberOverride) throws BusinessException, RatingException {

        if (chargeInstance == null) {
            throw new IncorrectChargeInstanceException("charge instance is null");
        }

        if (applicationDate == null) {
            applicationDate = new Date();
        }

        log.debug("WalletOperationService.oneShotWalletOperation subscriptionCode={}, quantity={}, applicationDate={}, chargeInstance.id={}, chargeInstance.desc={}",
            new Object[] { subscription.getId(), quantityInChargeUnits, applicationDate, chargeInstance.getId(), chargeInstance.getDescription() });

        RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(chargeInstance, applicationDate, inputQuantity, quantityInChargeUnits, orderNumberOverride, null, null, null,
            ChargeApplicationModeEnum.SUBSCRIPTION, null, false, isVirtual);

        WalletOperation walletOperation = ratingResult.getWalletOperation();

        if (isVirtual) {
            return walletOperation;
        }

        chargeWalletOperation(walletOperation);

        OneShotChargeTemplate oneShotChargeTemplate = null;

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

        if (chargeTemplate instanceof OneShotChargeTemplate) {
            oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
        } else {
            oneShotChargeTemplate = oneShotChargeTemplateService.findById(chargeTemplate.getId());
        }

        Boolean immediateInvoicing = (oneShotChargeTemplate != null && oneShotChargeTemplate.getImmediateInvoicing() != null) ? oneShotChargeTemplate.getImmediateInvoicing() : false;

        if (immediateInvoicing != null && immediateInvoicing) {
            BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
            int delay = billingAccount.getBillingCycle().getInvoiceDateDelayEL() == null ? 0
                    : InvoiceService.resolveImmediateInvoiceDateDelay(billingAccount.getBillingCycle().getInvoiceDateDelayEL(), walletOperation, billingAccount);
            Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(), -delay);
            nextInvoiceDate = DateUtils.setTimeToZero(nextInvoiceDate);
            applicationDate = DateUtils.setTimeToZero(applicationDate);

            if (nextInvoiceDate == null || applicationDate.after(nextInvoiceDate)) {
                billingAccount.setNextInvoiceDate(applicationDate);
                billingAccountService.update(billingAccount);
            }
        }
        applyAccumulatorCounter(chargeInstance, Collections.singletonList(walletOperation), isVirtual);
        return walletOperation;
    }

    /**
     * Determine recurring period start date
     *
     * @param chargeInstance Charge instance
     * @param date Date to calculate period for
     * @return Recurring period start date
     */
    public Date getRecurringPeriodStartDate(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
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
     */
    public Date getRecurringPeriodEndDate(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
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
     */
    public DatePeriod getRecurringPeriod(RecurringChargeInstance chargeInstance, Date date) {

        Calendar cal = resolveCalendar(chargeInstance);
        if (cal == null) {
            throw new BusinessException("Recurring charge instance has no calendar: id=" + chargeInstance.getId());
        }

        cal = CalendarService.initializeCalendar(cal, chargeInstance.getSubscriptionDate(), chargeInstance);

        Date startPeriodDate = cal.previousCalendarDate(cal.truncateDateTime(date));
        Date endPeriodDate = cal.nextCalendarDate(cal.truncateDateTime(date));

        return new DatePeriod(startPeriodDate, endPeriodDate);
    }

    private Calendar resolveCalendar(RecurringChargeInstance chargeInstance) {
        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        Calendar cal = chargeInstance.getCalendar();
        if (!StringUtils.isBlank(recurringChargeTemplate.getCalendarCodeEl())) {
            cal = recurringChargeTemplateService.getCalendarFromEl(recurringChargeTemplate.getCalendarCodeEl(), chargeInstance.getServiceInstance(), null, recurringChargeTemplate, chargeInstance);
        }
        return cal;
    }

    /**
     * Determine if charge should be applied in advance
     * 
     * @param recurringChargeInstance Recurring charge instance
     * @return True if charge is applied in advance
     */
    public boolean isApplyInAdvance(RecurringChargeInstance recurringChargeInstance) {
        boolean isApplyInAdvance = (recurringChargeInstance.getApplyInAdvance() == null) ? false : recurringChargeInstance.getApplyInAdvance();
        if (!StringUtils.isBlank(recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl())) {
            isApplyInAdvance = recurringChargeTemplateService.matchExpression(recurringChargeInstance.getRecurringChargeTemplate().getApplyInAdvanceEl(), recurringChargeInstance.getServiceInstance(), null, null,
                recurringChargeInstance);
        }

        return isApplyInAdvance;
    }

    private boolean shouldNotIncludePPM(Date applyChargeOnDate, Date nextChargeDate, Date from, Date to) {
        return (to != null && (to.before(applyChargeOnDate) || to.getTime() == applyChargeOnDate.getTime())) || (from != null && (from.after(nextChargeDate) || from.getTime() == nextChargeDate.getTime()));
    }

    public boolean isChargeMatch(ChargeInstance chargeInstance, String filterExpression) throws BusinessException {
        if (StringUtils.isBlank(filterExpression)) {
            return true;
        }

        return ValueExpressionWrapper.evaluateToBooleanOneVariable(filterExpression, "ci", chargeInstance);
    }

    /**
     * Apply a recurring charge.<br>
     * Identical to applyRecurringCharge() but occurs in a new transaction
     * 
     * @param chargeInstanceId Recurring charge instance id
     * @param chargeMode Charge application mode
     * @param forSchedule Is this a scheduled charge
     * @param chargeToDate An explicit date to charge to. Assumption - to reach end agreement date. Optional. If provided, will charge up to a given date, pro-rating, if needed,
     *        the last period.
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param orderNumberToOverride Order number to assign to Wallet operation. Optional. If provided, will override a value from chargeInstance.
     * @return List of created wallet operations
     * @throws BusinessException General business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyReccuringChargeInNewTx(Long chargeInstanceId, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date chargeToDate, String orderNumberToOverride, boolean isVirtual)
            throws BusinessException, RatingException {
        return applyReccuringCharge(recurringChargeInstanceService.findById(chargeInstanceId), chargeMode, forSchedule, chargeToDate, orderNumberToOverride, isVirtual);
    }

    /**
     * Apply a recurring charge.<br>
     * Quantity might be prorated for first time charge (identified by chargeInstance.chargeDate=null) if subscription prorata is enabled on charge template. Will update charge
     * instance with a new charge and next charge dates. <br>
     * <br>
     * Recurring charge can be of two scenarious, depending on chargeInstance.applyInAdvance flag of its EL expression:
     * <ul>
     * <li>Apply charge that is applied at the end of calendar period - for charge instance with appliedInAdvance = false</li>
     * <li>Apply the recurring charge in advance of calendar period - for charge instance with appliedInAdvance = true</li>
     * </ul>
     *
     * <b>Apply charge that is applied at the end of calendar period</b> applyInAdvance = false:<br>
     * <br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.nextChargeDate, wo.startDate = chargeInstance.chargeDate and
     * wo.endDate=chargeInstance.nextChargeDate.<br>
     * <br>
     * <b>Apply the recurring charge in advance of calendar period</b> applyInAdvance = true:<br>
     * <br>
     * Will create a WalletOperation with wo.operationDate = chargeInstance.chargeDate, wo.startDate = chargeInstance.chargeDate and wo.endDate=chargeInstance.nextChargeDate <br>
     * ---<br>
     * For non-reimbursement it will charge only the next calendar period cycle unless an explicit chargeToDate is provided. In such case last period might be prorated.<br>
     * For reimbursement need to reimburse earlier applied recurring charges starting from termination date to the last date charged. Thus it might span multiple calendar periods
     * with first period being .<br>
     * ---<br>
     * It will also update chargeInstance.chargeDate = chargeInstance.nextChargeDate and chargeInstance.nextChargeDate = nextCalendarDate(chargeInstance.nextChargeDate)
     * 
     *
     * @param chargeInstance Charge instance
     * @param chargeMode Charge application mode
     * @param forSchedule Is this a scheduled charge
     * @param chargeToDate An explicit date to charge to. Assumption - to reach end agreement date. Optional. If provided, will charge up to a given date, pro-rating, if needed,
     *        the last period.
     * @param isVirtual Is charge event a virtual operation? If so, no entities should be created/updated/persisted in DB
     * @param orderNumberToOverride Order number to assign to Wallet operation. Optional. If provided, will override a value from chargeInstance.
     * @return List of created wallet operations
     * @throws BusinessException General business exception
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public List<WalletOperation> applyReccuringCharge(RecurringChargeInstance chargeInstance, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date chargeToDate, String orderNumberToOverride, boolean isVirtual)
            throws BusinessException, RatingException {

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();

        Date applyChargeFromDate = null;
        Date applyChargeToDate = null;

        boolean prorateFirstPeriod = false;
        Date prorateFirstPeriodFromDate = null;

        CounterPeriod firstChargeCounterPeriod = null;

        // -- Determine charge period, prorating for the first termination period and prorating of first subscription period

        // For reimbursement need to reimburse earlier applied recurring charges starting from "apply charge to upon termination" date to the last date charged (chargedToDate).
        // This might span multiple calendar periods and might require a first period proration. Here none of WOs are created yet and chargedToDate reflects the real last charged
        // to date.
        if (chargeMode == ChargeApplicationModeEnum.REIMBURSMENT) {

            if (chargeInstance.getChargedToDate() == null) {
                // Trying to reimburse something that was not charged yet
                log.error("Trying to reimburse a charge {} that was not charged yet. Will skip.", chargeInstance.getId());
                return new ArrayList<>();
            }

            applyChargeFromDate = chargeInstance.getChargeToDateOnTermination();
            applyChargeToDate = chargeInstance.getChargedToDate();

            // Take care of the first charge period that termination date falls into
            // Determine if first period proration is needed and is requested
            prorateFirstPeriodFromDate = getRecurringPeriodStartDate(chargeInstance, applyChargeFromDate);
            if (prorateFirstPeriodFromDate.before(applyChargeFromDate)) {
                prorateFirstPeriod = prorateTerminationCharges(chargeInstance);

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
                return new ArrayList<>();
            }

            applyChargeFromDate = chargeInstance.getChargedToDate();

            DatePeriod period = getRecurringPeriod(chargeInstance, applyChargeFromDate);
            applyChargeToDate = period.getTo();

            // Take care of the first charge period that termination date falls into
            // Determine if first period proration is needed and is requested
            if (period.getFrom().before(applyChargeFromDate)) {
                prorateFirstPeriod = prorateTerminationCharges(chargeInstance);
                prorateFirstPeriodFromDate = period.getFrom();

            } else {
                prorateFirstPeriod = false;
                prorateFirstPeriodFromDate = null;
            }

            applyChargeToDate = chargeToDate != null ? chargeToDate : applyChargeToDate;

            // For non-reimbursement it will cover only one calendar period cycle unless an explicit chargeToDate is specified.
            // In such case this might span multiple calendar periods and might require a last period proration
            // Initialize charge and determine prorata ratio if applying a charge for the first time
        } else {

            applyChargeFromDate = chargeInstance.getChargedToDate();

            boolean isFirstCharge = false;

            // First time charge
            if (applyChargeFromDate == null) {
                applyChargeFromDate = chargeInstance.getSubscriptionDate();
                isFirstCharge = true;
            } else {
                isFirstCharge = applyChargeFromDate.equals(chargeInstance.getSubscriptionDate());
            }

            DatePeriod period = getRecurringPeriod(chargeInstance, applyChargeFromDate);
            applyChargeToDate = period.getTo();

            // When charging first time, need to determine if counter is available and prorata ratio if subscription charge proration is enabled
            if (isFirstCharge) {

                CounterInstance counterInstance = chargeInstance.getCounter();
                if (!isVirtual && counterInstance != null) {
                    boolean isApplyInAdvance = isApplyInAdvance(chargeInstance);
                    // get the counter period of recurring charge instance
                    CounterPeriod counterPeriod = counterInstanceService.getCounterPeriod(counterInstance, chargeInstance.getChargeDate());

                    // If the counter is equal to 0, then the charge is not applied (but next activation date is updated).
                    if (counterPeriod != null && BigDecimal.ZERO.equals(counterPeriod.getValue())) {
                        chargeInstance.advanceChargeDates(applyChargeFromDate, applyChargeToDate, isApplyInAdvance ? applyChargeToDate : applyChargeFromDate);
                        return new ArrayList<>();

                    } else if (counterPeriod == null) {
                        counterPeriod = counterInstanceService.getOrCreateCounterPeriod(counterInstance, chargeInstance.getChargeDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance,
                            chargeInstance.getServiceInstance());
                    }

                    firstChargeCounterPeriod = counterPeriod;
                }

                // Determine if subscription charge should be prorated
                prorateFirstPeriodFromDate = period.getFrom();
                if (prorateFirstPeriodFromDate.before(applyChargeFromDate)) {

                    boolean prorateSubscription = recurringChargeTemplate.getSubscriptionProrata() == null ? false : recurringChargeTemplate.getSubscriptionProrata();
                    if (!StringUtils.isBlank(recurringChargeTemplate.getSubscriptionProrataEl())) {
                        prorateSubscription = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getSubscriptionProrataEl(), chargeInstance.getServiceInstance(), null, recurringChargeTemplate,
                            chargeInstance);
                    }

                    prorateFirstPeriod = prorateSubscription;

                } else {
                    prorateFirstPeriod = false;
                    prorateFirstPeriodFromDate = null;
                }
            }

            applyChargeToDate = chargeToDate != null ? chargeToDate : applyChargeToDate;
        }

        if (applyChargeFromDate == null) {
            throw new IncorrectChargeInstanceException("nextChargeDate is null.");
        }

        log.debug("Will apply {} recuring charges for charge {}/{} for period(s) {} - {}.",
            chargeMode == ChargeApplicationModeEnum.REIMBURSMENT || chargeMode == ChargeApplicationModeEnum.RERATING_REIMBURSEMENT ? "reimbursement" : "", chargeInstance.getId(), chargeInstance.getCode(),
            applyChargeFromDate, applyChargeToDate);

        // -- Divide a period to charge into periods (or partial periods) and create WOs

        List<WalletOperation> walletOperations = new ArrayList<>();
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
                    prorate = true && prorateFirstPeriod;
                }

                // Take care of the last charge period that termination date falls into
                // Check if prorating is needed on last period (this really should happen in Apply end agreement mode)
                Date currentPeriodToDate = getRecurringPeriodEndDate(chargeInstance, currentPeriodFromDate);
                effectiveChargeToDate = currentPeriodToDate;
                if (chargeToDate != null && currentPeriodToDate.after(chargeToDate)) {
                    effectiveChargeToDate = chargeToDate;

                    if (chargeInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                        prorate = prorate || prorateTerminationCharges(chargeInstance);
                    }
                }

                // Handle a case of re-rating of recurring charge - existing WOs have been canceled and new ones are re-generated up to termination date only if it falls within the
                // rating period. chargeToDate is not passed in rerating cases.
                if ((chargeMode == ChargeApplicationModeEnum.RERATING || chargeMode == ChargeApplicationModeEnum.AGREEMENT) && chargeInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
                    Date chargeToDateOnTermination = chargeInstance.getChargeToDateOnTermination();
                    // Termination date is not charged, its like TO value in a range of dates - e.g. if termination date is on friday, friday will not be charged.
                    if (chargeToDateOnTermination != null && currentPeriodFromDate.compareTo(chargeToDateOnTermination) < 0 && chargeToDateOnTermination.compareTo(currentPeriodToDate) <= 0) {
                        effectiveChargeToDate = effectiveChargeToDate.before(chargeToDateOnTermination) ? effectiveChargeToDate : chargeToDateOnTermination;

                        if (chargeToDateOnTermination.compareTo(currentPeriodToDate) < 0) {
                            prorate = prorate || prorateTerminationCharges(chargeInstance);
                        }
                    }
                }

                boolean chargeDatesAlreadyAdvanced = false;

                boolean filterExpression = isChargeMatch(chargeInstance, chargeInstance.getRecurringChargeTemplate().getFilterExpression());
                List<WalletOperation> woList = chargeInstance.getWalletOperations();

                // If charge is not applicable for current period, skip it
                if ((!filterExpression && woList.isEmpty()) || (filterExpression && woList.isEmpty() && chargeMode.isReimbursement())) {
                    log.debug("IPIEL: not rating chargeInstance with id={}, chargeApplication mode={}", chargeInstance.getId(), chargeMode.name());

                } else {

                    BigDecimal inputQuantity = chargeMode.isReimbursement() ? chargeInstance.getQuantity().negate() : chargeInstance.getQuantity();

                    // Apply prorating if needed
                    if (prorate) {
                        BigDecimal prorata = DateUtils.calculateProrataRatio(effectiveChargeFromDate, effectiveChargeToDate, currentPeriodFromDate, currentPeriodToDate, false);
                        if (prorata == null) {
                            throw new BusinessException("Failed to calculate prorating for charge id=" + chargeInstance.getId() + " : periodFrom=" + currentPeriodFromDate + ", periodTo=" + currentPeriodToDate
                                    + ", proratedFrom=" + effectiveChargeFromDate + ", proratedTo=" + effectiveChargeToDate);
                        }

                        inputQuantity = inputQuantity.multiply(prorata);
                    }

                    log.debug("Applying {} recurring charge {} for period {} - {}, quantity {}", chargeMode.isReimbursement() ? "reimbursement" : isApplyInAdvance ? "start of period" : "end of period",
                        chargeInstance.getId(), effectiveChargeFromDate, effectiveChargeToDate, inputQuantity);

                    if (recurringChargeTemplate.isProrataOnPriceChange()) {
                        walletOperations.addAll(generateWalletOperationsByPricePlan(chargeInstance, chargeMode, forSchedule, effectiveChargeFromDate, effectiveChargeToDate,
                            prorate ? new DatePeriod(currentPeriodFromDate, currentPeriodToDate) : null, inputQuantity, orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(),
                            isApplyInAdvance, isVirtual));

                    } else {

                        Date oldChargedToDate = chargeInstance.getChargedToDate();

                        Date operationDate = isApplyInAdvance ? effectiveChargeFromDate : effectiveChargeToDate;
                        // Any operation past the termination date is invoiced with termination date
                        if (chargeInstance.getTerminationDate() != null && operationDate.after(chargeInstance.getTerminationDate())) {
                            operationDate = chargeInstance.getTerminationDate();
                        }

                        RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(chargeInstance, operationDate, inputQuantity, null,
                            orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), effectiveChargeFromDate, effectiveChargeToDate,
                            prorate ? new DatePeriod(currentPeriodFromDate, currentPeriodToDate) : null, chargeMode, null, forSchedule, false);

                        WalletOperation walletOperation = ratingResult.getWalletOperation();

                        // Check if rating script modified a chargedTo date
                        chargeDatesAlreadyAdvanced = DateUtils.compare(oldChargedToDate, chargeInstance.getChargedToDate()) != 0;

                        if (forSchedule) {
                            walletOperation.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                        }

                        if (isVirtual) {
                            walletOperations.add(walletOperation);
                        } else {
                            List<WalletOperation> operations = chargeWalletOperation(walletOperation);
                            walletOperations.addAll(operations);
                        }
                    }
                    if (!isVirtual && !chargeMode.isReimbursement()) {
                        applyAccumulatorCounter(chargeInstance, walletOperations, false);
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

                currentPeriodFromDate = chargeInstance.getChargedToDate(); // currentPeriodToDate;
                periodIndex++;

                // Handle a case of infinite loop when chargeToDate is null (regular charging), but period was shortened (e.g. rating up to the termination/end aggreement date only
                // for
                // terminated charges)
                if (effectiveChargeToDate.compareTo(currentPeriodToDate) != 0) {
                    break;
                }
            }

        } catch (Exception e) {

            if (e instanceof BusinessException) {
                ((BusinessException) e).addErrorContext(ErrorContextAttributeEnum.RATING_PERIOD, new DatePeriod(effectiveChargeFromDate, effectiveChargeToDate));
            }

            throw e;
        }

        // The counter will be decremented by charge quantity
        if (!isVirtual && firstChargeCounterPeriod != null)

        {
            CounterValueChangeInfo counterValueChangeInfo = counterInstanceService.deduceCounterValue(firstChargeCounterPeriod, chargeInstance.getQuantity(), false);
            counterInstanceService.triggerCounterPeriodEvent(counterValueChangeInfo, firstChargeCounterPeriod);
        }

        return walletOperations;
    }

    /**
     * Shall termination or reimbursement charges be prorated
     *
     * @param chargeInstance Recurring charge instance
     * @return True if termination charges should be prorated
     */
    private boolean prorateTerminationCharges(RecurringChargeInstance chargeInstance) {

        RecurringChargeTemplate recurringChargeTemplate = chargeInstance.getRecurringChargeTemplate();
        boolean isTerminationProrata = recurringChargeTemplate.getTerminationProrata() == null ? false : recurringChargeTemplate.getTerminationProrata();
        if (!StringUtils.isBlank(recurringChargeTemplate.getTerminationProrataEl())) {
            isTerminationProrata = recurringChargeTemplateService.matchExpression(recurringChargeTemplate.getTerminationProrataEl(), chargeInstance.getServiceInstance(), null, null, chargeInstance);
        }
        boolean prorate = chargeInstance.getServiceInstance().getSubscriptionTerminationReason() == null ? isTerminationProrata
                : chargeInstance.getServiceInstance().getSubscriptionTerminationReason().getOverrideProrata().isToProrate(isTerminationProrata);

        return prorate;
    }

    // TODO AKK what if rateCharge would return multiple WOs as alternative to this method??
    private List<WalletOperation> generateWalletOperationsByPricePlan(RecurringChargeInstance chargeInstance, ChargeApplicationModeEnum chargeMode, boolean forSchedule, Date periodStartDate, Date periodEndDate,
            DatePeriod fullRatingPeriod, BigDecimal inputQuantity, String orderNumberToOverride, boolean isApplyInAdvance, boolean isVirtual) {

        String recurringChargeTemplateCode = chargeInstance.getRecurringChargeTemplate().getCode();

        return ratingService.getActivePricePlansByChargeCode(recurringChargeTemplateCode).stream()
            .filter(ppm -> !shouldNotIncludePPM(periodStartDate, periodEndDate, ppm.getValidityFrom(), ppm.getValidityDate()) && (ppm.getValidityFrom() != null && periodEndDate.after(ppm.getValidityFrom())))
            .map(pricePlanMatrix -> {

                Date computedApplyChargeOnDate = pricePlanMatrix.getValidityFrom().after(periodStartDate) ? pricePlanMatrix.getValidityFrom() : periodStartDate;
                Date computedNextChargeDate = pricePlanMatrix.getValidityDate() != null && pricePlanMatrix.getValidityDate().before(periodEndDate) ? pricePlanMatrix.getValidityDate() : periodEndDate;
                double ratio = computeQuantityRatio(computedApplyChargeOnDate, computedNextChargeDate);
                BigDecimal computedInputQuantityHolder = inputQuantity.multiply(new BigDecimal(ratio + ""));

                RatingResult ratingResult = ratingService.rateChargeAndTriggerEDRs(chargeInstance, isApplyInAdvance ? computedApplyChargeOnDate : computedNextChargeDate, computedInputQuantityHolder, null,
                    orderNumberToOverride != null ? orderNumberToOverride : chargeInstance.getOrderNumber(), computedApplyChargeOnDate, computedNextChargeDate, fullRatingPeriod, chargeMode, null, forSchedule, false);

                WalletOperation walletOperation = ratingResult.getWalletOperation();
                walletOperation.setSubscriptionDate(chargeInstance.getSubscriptionDate());

                if (forSchedule) {
                    walletOperation.changeStatus(WalletOperationStatusEnum.SCHEDULED);
                }
                if (isVirtual) {
                    return Arrays.asList(walletOperation);

                } else {
                    return chargeWalletOperation(walletOperation);
                }

            }).flatMap(List::stream).collect(Collectors.toList());
    }

    public void applyAccumulatorCounter(ChargeInstance chargeInstance, List<WalletOperation> walletOperations, boolean isVirtual) {

        for (CounterInstance counterInstance : chargeInstance.getCounterInstances()) {
            CounterPeriod counterPeriod = null;
            if (counterInstance != null) {
                // get the counter period of charge instance
                log.debug("Get accumulator counter period for counter instance {}", counterInstance);

                for (WalletOperation wo : walletOperations) {
                    counterPeriod = counterInstanceService.getCounterPeriod(counterInstance, wo.getOperationDate());
                    if (counterPeriod == null || counterPeriod.getValue() == null || !counterPeriod.getValue().equals(BigDecimal.ZERO)) {
                        // The counter will be incremented by charge quantity
                        if (counterPeriod == null) {
                            counterPeriod = counterInstanceService.getOrCreateCounterPeriod(counterInstance, wo.getOperationDate(), chargeInstance.getServiceInstance().getSubscriptionDate(), chargeInstance,
                                chargeInstance.getServiceInstance());
                        }
                    }

                    log.debug("Increment accumulator counter period value {} by the WO's amount {} or quantity {} ", counterPeriod, wo.getAmountWithoutTax(), wo.getQuantity());
                    counterInstanceService.accumulatorCounterPeriodValue(counterPeriod, wo, null, isVirtual);
                }
            }
        }
    }

    private double computeQuantityRatio(Date computedApplyChargeOnDate, Date computedNextChargeDate) {
        int lengthOfMonth = 30;
        double days = DateUtils.daysBetween(computedApplyChargeOnDate, computedNextChargeDate);
        return days > lengthOfMonth ? 1 : days / lengthOfMonth;
    }

    /**
     *
     * @param ids
     * @return list of walletOperations by ids
     */
    public List<WalletOperation> listByIds(List<Long> ids) {
        if (ids.isEmpty())
            return emptyList();
        return getEntityManager().createNamedQuery("WalletOperation.listByIds", WalletOperation.class).setParameter("idList", ids).getResultList();
    }

    /**
     * Get a list of wallet operations to rate up to a given date. WalletOperation.invoiceDate< date
     *
     * @param entityToInvoice Entity to invoice
     * @param invoicingDate Invoicing date
     * @return A list of wallet operations
     */
    public List<WalletOperation> listToRate(IBillableEntity entityToInvoice, Date invoicingDate) {

        if (entityToInvoice instanceof BillingAccount) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByBA", WalletOperation.class).setParameter("invoicingDate", invoicingDate).setParameter("billingAccount", entityToInvoice)
                .getResultList();

        } else if (entityToInvoice instanceof Subscription) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateBySubscription", WalletOperation.class).setParameter("invoicingDate", invoicingDate).setParameter("subscription", entityToInvoice)
                .getResultList();

        } else if (entityToInvoice instanceof Order) {
            return getEntityManager().createNamedQuery("WalletOperation.listToRateByOrderNumber", WalletOperation.class).setParameter("invoicingDate", invoicingDate)
                .setParameter("orderNumber", ((Order) entityToInvoice).getOrderNumber()).getResultList();
        }

        return new ArrayList<>();
    }

    /**
     * Get a list of wallet operations to be invoiced/converted to rated transactions up to a given date. WalletOperation.invoiceDate< date
     * 
     * @param invoicingDate Invoicing date
     * @param nbToRetrieve Number of items to retrieve for processing
     * @return A list of Wallet operation ids
     */
    public List<Long> listToRate(Date invoicingDate, int nbToRetrieve) {
        return getEntityManager().createNamedQuery("WalletOperation.listToRateIds", Long.class).setParameter("invoicingDate", invoicingDate).setMaxResults(nbToRetrieve).getResultList();
    }

    public WalletOperation findByUserAccountAndCode(String code, UserAccount userAccount) {

        try {
            return getEntityManager().createNamedQuery("WalletOperation.findByUAAndCode", WalletOperation.class).setParameter("userAccount", userAccount).setParameter("code", code).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Charge wallet operation on prepaid wallets
     * 
     * @param chargeInstance Charge instance
     * @param op Wallet operation
     * @return A list of wallet operations containing a single original wallet operation or multiple wallet operations if had to be split among various wallets
     * @throws BusinessException General business exception
     * @throws InsufficientBalanceException Balance is insufficient in the wallet
     */
    private List<WalletOperation> chargeOnPrepaidWallets(ChargeInstance chargeInstance, WalletOperation op) throws BusinessException, InsufficientBalanceException {

        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        List<WalletOperation> result = new ArrayList<>();
        Map<Long, BigDecimal> walletLimits = walletService.getWalletIds(chargeInstance);

        // Handles negative amounts (recharge) - apply recharge to the first wallet
        if (op.getAmountWithTax().compareTo(BigDecimal.ZERO) <= 0) {

            Long walletId = walletLimits.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);
            return result;
        }

        log.debug("chargeWalletOperation chargeInstanceId found with {} wallet ids", walletLimits.size());

        Map<Long, BigDecimal> balances = walletService.getWalletReservedBalances(walletLimits.keySet());

        Map<Long, BigDecimal> woAmounts = new HashMap<>();

        BigDecimal remainingAmountToCharge = op.getAmountWithTax();

        // First iterate over balances that have credit
        for (Long walletId : balances.keySet()) {

            BigDecimal balance = balances.get(walletId);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal negatedBalance = balance.negate();
                // Case when amount to deduct (5) is less than or equal to a negated balance amount -(-10)
                if (remainingAmountToCharge.compareTo(negatedBalance) <= 0) {
                    woAmounts.put(walletId, remainingAmountToCharge);
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Case when amount to deduct (10) is more tan a negated balance amount -(-5)
                } else {
                    woAmounts.put(walletId, negatedBalance);
                    balances.put(walletId, BigDecimal.ZERO);
                    remainingAmountToCharge = remainingAmountToCharge.add(balance);
                }
            }
        }

        // If not all the amount was deducted, then iterate again checking if any of the balances can be reduced pass the Zero up to a rejection limit as defined in a wallet.
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {

            for (Long walletId : balances.keySet()) {

                BigDecimal balance = balances.get(walletId);
                BigDecimal rejectLimit = walletLimits.get(walletId);

                // There is no limit upon which further consumption should be rejected
                if (rejectLimit == null) {
                    if (woAmounts.containsKey(walletId)) {
                        woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                    } else {
                        woAmounts.put(walletId, remainingAmountToCharge);
                    }
                    balances.put(walletId, balance.add(remainingAmountToCharge));
                    remainingAmountToCharge = BigDecimal.ZERO;
                    break;

                    // Limit is not exceeded yet
                } else if (rejectLimit.compareTo(balance) > 0) {

                    BigDecimal remainingLimit = rejectLimit.subtract(balance);

                    // Case when amount to deduct (5) is less than or equal to a remaining limit (10)
                    if (remainingAmountToCharge.compareTo(remainingLimit) <= 0) {
                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingAmountToCharge));
                        } else {
                            woAmounts.put(walletId, remainingAmountToCharge);
                        }

                        balances.put(walletId, balance.add(remainingAmountToCharge));
                        remainingAmountToCharge = BigDecimal.ZERO;
                        break;

                        // Case when amount to deduct (10) is more tan a remaining limit (5)
                    } else {

                        if (woAmounts.containsKey(walletId)) {
                            woAmounts.put(walletId, woAmounts.get(walletId).add(remainingLimit));
                        } else {
                            woAmounts.put(walletId, remainingLimit);
                        }

                        balances.put(walletId, rejectLimit);
                        remainingAmountToCharge = remainingAmountToCharge.subtract(remainingLimit);
                    }
                }
            }
        }

        // Not possible to deduct all WO amount, so throw an Insufficient balance error
        if (remainingAmountToCharge.compareTo(BigDecimal.ZERO) > 0) {
            throw new InsufficientBalanceException("Insuficient balance when charging " + op.getAmountWithTax() + " for wallet operation " + op.getId());
        }

        // All charge was over one wallet
        if (woAmounts.size() == 1) {
            Long walletId = woAmounts.keySet().iterator().next();
            op.setWallet(getEntityManager().find(WalletInstance.class, walletId));
            log.debug("prepaid walletoperation fit in walletInstance {}", walletId);
            create(op);
            result.add(op);
            walletCacheContainerProvider.updateBalance(op);

            // Charge was over multiple wallets
        } else {

            for (Entry<Long, BigDecimal> amountInfo : woAmounts.entrySet()) {
                Long walletId = amountInfo.getKey();
                BigDecimal walletAmount = amountInfo.getValue();

                BigDecimal newOverOldCoeff = walletAmount.divide(op.getAmountWithTax(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                BigDecimal newOpAmountWithTax = walletAmount;
                BigDecimal newOpAmountWithoutTax = op.getAmountWithoutTax().multiply(newOverOldCoeff);

                newOpAmountWithoutTax = round(newOpAmountWithoutTax, rounding, roundingMode);
                newOpAmountWithTax = round(newOpAmountWithTax, rounding, roundingMode);
                BigDecimal newOpAmountTax = newOpAmountWithTax.subtract(newOpAmountWithoutTax);
                BigDecimal newOpQuantity = op.getQuantity().multiply(newOverOldCoeff);

                WalletOperation newOp = op.getUnratedClone();
                newOp.setWallet(getEntityManager().find(WalletInstance.class, walletId));
                newOp.setAmountWithTax(newOpAmountWithTax);
                newOp.setAmountTax(newOpAmountTax);
                newOp.setAmountWithoutTax(newOpAmountWithoutTax);
                newOp.setQuantity(newOpQuantity);
                log.debug("prepaid walletoperation partially fit in walletInstance {}, we charge {} of  ", newOp.getWallet(), newOpAmountTax, op.getAmountWithTax());
                create(newOp);
                result.add(newOp);
                walletCacheContainerProvider.updateBalance(newOp);
            }
        }
        return result;
    }

    public List<WalletOperation> chargeWalletOperation(WalletOperation op) throws BusinessException, InsufficientBalanceException {

        List<WalletOperation> result = new ArrayList<>();
        ChargeInstance chargeInstance = op.getChargeInstance();
        Long chargeInstanceId = chargeInstance.getId();
        // case of scheduled operation (for revenue recognition)
        UserAccount userAccount = chargeInstance.getUserAccount();

        ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
        if (chargeTemplate != null) {
            if (op.getInputUnitDescription() == null) {
                op.setInputUnitDescription(chargeTemplate.getInputUnitDescription());
            }
            if (op.getRatingUnitDescription() == null) {
                op.setRatingUnitDescription(chargeTemplate.getRatingUnitDescription());
            }
            if (op.getInvoiceSubCategory() == null) {
                op.setInvoiceSubCategory(chargeTemplate.getInvoiceSubCategory());
            }
        }

        if (chargeInstanceId == null) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);

            // Balance and reserved balance deals with prepaid wallets. If charge instance does not contain any prepaid wallet, then it is a postpaid charge and dont need to deal
            // with wallet cache at all
        } else if (!chargeInstance.getPrepaid()) {
            op.setWallet(userAccount.getWallet());
            result.add(op);
            create(op);

            // Prepaid charges only
        } else {
            result = chargeOnPrepaidWallets(chargeInstance, op);
        }
        return result;
    }

    /**
     * Rerate existing wallet operation. Executed in new transaction. <br/>
     * <br/>
     *
     * <b>When rerateInvoiced = false:</b><br/>
     *
     * Update Wallet operations to status TO_RERATE and cancel related RTs. Only unbilled wallet operations will be considered. In case of Wallet operation aggregation to a single
     * Rated transaction, all related wallet operations through the same Rated transaction, will be marked for re-rating as well. Note, that a number of Wallet operation ids passed
     * and a number of Wallet operations marked for re-rating might not match if aggregation was used, or Wallet operation status were invalid.
     * <p/>
     * <b>When rerateInvoiced = true:</b> <br/>
     *
     * Billed wallet operations will be refunded and new wallet operations with status TO_RERATE will be created. For the unbilled wallet operations the logic of
     * includeinvoiced=false applies.
     *
     * @param walletOperationIds A list of Wallet operation ids to mark for re-rating
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded.
     * @return Number of wallet operations marked for re-rating.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int markToRerateInNewTx(List<Long> walletOperationIds, boolean rerateInvoiced) {
        return markToRerate(walletOperationIds, rerateInvoiced);
    }

    /**
     * Rerate existing wallet operation. <br/>
     * <br/>
     *
     * <b>When rerateInvoiced = false:</b><br/>
     *
     * Update Wallet operations to status TO_RERATE and cancel related RTs. Only unbilled wallet operations will be considered. In case of Wallet operation aggregation to a single
     * Rated transaction, all related wallet operations through the same Rated transaction, will be marked for re-rating as well. Note, that a number of Wallet operation ids passed
     * and a number of Wallet operations marked for re-rating might not match if aggregation was used, or Wallet operation status were invalid.
     * <p/>
     * <b>When includeInvoiced = true:</b> <br/>
     *
     * Billed wallet operations will be refunded and new wallet operations with status TO_RERATE will be created. For the unbilled wallet operations the logic of
     * rerateInvoiced=false applies.
     *
     * @param walletOperationIds A list of Wallet operation ids to mark for re-rating
     * @param rerateInvoiced Re-rate already invoiced wallet operations if true. In such case invoiced wallet operations will be refunded
     * @return Number of wallet operations marked for re-rating.
     */
    public int markToRerate(List<Long> walletOperationIds, boolean rerateInvoiced) {

        if (walletOperationIds.isEmpty()) {
            return 0;
        }

        int nrOfWosToRerate = 0;

        // Ignore Rated transactions that were billed already
        List<Long> walletOperationsBilled = getEntityManager().createNamedQuery("WalletOperation.getWalletOperationsBilled", Long.class).setParameter("walletIdList", walletOperationIds).getResultList();
        walletOperationIds.removeAll(walletOperationsBilled);

        // Handle invoiced wallet operations if requested: Refund invoiced operations and create identical one (with negated amounts) with status TO_RERATE
        if (rerateInvoiced && !walletOperationsBilled.isEmpty()) {

            List<WalletOperation> refundedWOs = refundWalletOperations(walletOperationsBilled);
            Date today = new Date();
            for (WalletOperation refundedWO : refundedWOs) {

                // A refund WO
                WalletOperation wo = refundedWO.getClone();
                wo.setAmountTax(wo.getAmountTax().negate());
                wo.setAmountWithoutTax(wo.getAmountWithoutTax().negate());
                wo.setAmountWithTax(wo.getAmountWithTax().negate());
                wo.setStatus(WalletOperationStatusEnum.TO_RERATE);
                wo.setCreated(today);
                wo.setUpdated(null);
                create(wo);
            }
        }

        // Cancelled related RTS and change WO status to re-rate. Note: in case of aggregation, it will re-rate all WOs that are linked through the related RTs
        if (!walletOperationIds.isEmpty()) {
            getEntityManager().createNamedQuery("RatedTransaction.cancelByWOIds").setParameter("woIds", walletOperationIds).setParameter("now", new Date()).executeUpdate();

            nrOfWosToRerate = nrOfWosToRerate + getEntityManager().createNamedQuery("WalletOperation.setStatusToToRerate").setParameter("now", new Date()).setParameter("woIds", walletOperationIds).executeUpdate();

            log.info("{} out of {} requested Wallet operations are marked for rerating", nrOfWosToRerate, walletOperationIds.size());
        }
        return nrOfWosToRerate;
    }

    public List<Long> listToRerate() {
        return (List<Long>) getEntityManager().createNamedQuery("WalletOperation.listToRerate", Long.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<WalletOperation> openWalletOperationsBySubCat(WalletInstance walletInstance, InvoiceSubCategory invoiceSubCategory, Date from, Date to) {
        QueryBuilder qb = new QueryBuilder("Select op from WalletOperation op", "op");
        if (invoiceSubCategory != null) {
            qb.addCriterionEntity("op.chargeInstance.chargeTemplate.invoiceSubCategory", invoiceSubCategory);
        }
        qb.addCriterionEntity("op.wallet", walletInstance);
        qb.addSql(" op.status = 'OPEN'");
        if (from != null) {
            qb.addCriterion("operationDate", ">=", from, false);
        }
        if (to != null) {
            qb.addCriterion("operationDate", "<=", to, false);
        }

        try {
            return (List<WalletOperation>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> openWalletOperationsByCharge(WalletInstance walletInstance) {

        try {
            // todo ejbQL and make namedQuery
            List<Object[]> resultList = getEntityManager().createNativeQuery("select op.description ,sum(op.quantity) QT, sum(op.amount_without_tax) MT ,op.input_unit_description from "
                    + "billing_wallet_operation op , cat_charge_template ct, billing_charge_instance ci " + "where op.wallet_id = " + walletInstance.getId()
                    + " and  op.status = 'OPEN'  and op.charge_instance_id = ci.id and ci.charge_template_id = ct.id and ct.id in (select id from cat_charge_template where charge_type 'U') "
                    + "group by op.description, op.input_unit_description")
                .getResultList();

            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long countNonTreatedWOByBA(BillingAccount billingAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByBA").setParameter("billingAccount", billingAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by BA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByUA(UserAccount userAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByUA").setParameter("userAccount", userAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by UA", e);
            return null;
        }
    }

    public Long countNonTreatedWOByCA(CustomerAccount customerAccount) {
        try {
            return (Long) getEntityManager().createNamedQuery("WalletOperation.countNotTreatedByCA").setParameter("customerAccount", customerAccount).getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to countNonTreated WO by CA", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrWalletsOperationByStatus() {
        try {
            return (List<Object[]>) getEntityManager().createNamedQuery("WalletOperation.countNbrWalletsOperationByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrWalletsOperationByStatus", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getNbrEdrByStatus() {
        try {
            return (List<Object[]>) getEntityManager().createNamedQuery("EDR.countNbrEdrByStatus").getResultList();
        } catch (NoResultException e) {
            log.warn("failed to countNbrEdrByStatus", e);
            return null;
        }
    }

    public List<AggregatedWalletOperation> listToInvoiceIdsWithGrouping(Date invoicingDate, WalletOperationAggregationSettings aggregationSettings) {

        updateWalletOperationPeriodView(aggregationSettings);

        WalletOperationAggregatorQueryBuilder woa = new WalletOperationAggregatorQueryBuilder(aggregationSettings, customFieldTemplateService, filterService);

        String strQuery = woa.getGroupQuery();
        log.debug("aggregated query={}", strQuery);

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("invoicingDate", invoicingDate);
        // get the aggregated data
        @SuppressWarnings("unchecked")
        List result = query.unwrap(org.hibernate.query.Query.class).setResultTransformer(new AliasToAggregatedWalletOperationResultTransformer(AggregatedWalletOperation.class)).getResultList();

        return result;
    }

    private void updateWalletOperationPeriodView(WalletOperationAggregationSettings aggregationSettings) {
        String queryTemplate = "CREATE OR REPLACE VIEW billing_wallet_operation_period AS select o.*, SUM(o.flag) over (partition by o.seller_id order by o.charge_instance_id {{ADDITIONAL_ORDER_BY}}) as period "
                + " from (select o.*, (case when (DATE(lag(o.end_Date) over (partition by o.seller_id order by o.charge_instance_id {{ADDITIONAL_ORDER_BY}})) {{PERIOD_END_DATE_INCLUDED}}= DATE(o.start_date)) then 0 else 1 end) as flag "
                + " FROM billing_wallet_operation o WHERE o.status='OPEN' ) o ";
        Map<String, String> parameters = new HashMap<>();
        if (aggregationSettings.isPeriodEndDateIncluded()) {
            parameters.put("{{PERIOD_END_DATE_INCLUDED}}", "+ interval '1' day");
        } else {
            parameters.put("{{PERIOD_END_DATE_INCLUDED}}", "");
        }
        if (!StringUtils.isBlank(aggregationSettings.getAdditionalOrderBy())) {
            String orderByClause = "";
            String[] orderByFields = aggregationSettings.getAdditionalOrderBy().split(",");
            for (String orderByField : orderByFields) {
                orderByClause += ", o." + orderByField;
            }
            parameters.put("{{ADDITIONAL_ORDER_BY}}", orderByClause);
        } else {
            parameters.put("{{ADDITIONAL_ORDER_BY}}", "");
        }

        for (String key : parameters.keySet()) {
            queryTemplate = queryTemplate.replace(key, parameters.get(key));
        }
        Query q = getEntityManager().createNativeQuery(queryTemplate);
        q.executeUpdate();

    }

    public List<WalletOperation> listByRatedTransactionId(Long ratedTransactionId) {
        return getEntityManager().createNamedQuery("WalletOperation.listByRatedTransactionId", WalletOperation.class).setParameter("ratedTransactionId", ratedTransactionId).getResultList();
    }

    /**
     * Return a list of open Wallet operation between two date.
     *
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @param lastId a last id for pagination
     * @param max a max rows
     * @return a list of Wallet Operation
     */
    public List<WalletOperation> getNotOpenedWalletOperationBetweenTwoDates(Date firstTransactionDate, Date lastTransactionDate, Long lastId, int max) {
        return getEntityManager().createNamedQuery("WalletOperation.listNotOpenedWObetweenTwoDates", WalletOperation.class).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).setParameter("lastId", lastId).setMaxResults(max).getResultList();
    }

    /**
     * Remove all not open Wallet operation between two date
     * 
     * @param firstTransactionDate first operation date
     * @param lastTransactionDate last operation date
     * @return the number of deleted entities
     */
    public long purge(Date firstTransactionDate, Date lastTransactionDate) {

        return getEntityManager().createNamedQuery("WalletOperation.deleteNotOpenWObetweenTwoDates").setParameter("firstTransactionDate", firstTransactionDate).setParameter("lastTransactionDate", lastTransactionDate)
            .executeUpdate();
    }

    /**
     * Import wallet operations.
     * 
     * @param walletOperations Wallet Operations DTO list
     * @throws BusinessException
     */
    public void importWalletOperation(List<WalletOperationDto> walletOperations) throws BusinessException {

        for (WalletOperationDto dto : walletOperations) {
            Tax tax = null;
            ChargeInstance chargeInstance = null;

            if (dto.getTaxCode() != null) {
                tax = taxService.findByCode(dto.getTaxCode());
            } else if (dto.getTaxPercent() != null) {
                tax = taxService.findTaxByPercent(dto.getTaxPercent());
            }
            if (tax == null) {
                log.warn("No tax matched for wallet operation by code {} nor tax percent {}", dto.getTaxCode(), dto.getTaxPercent());
                continue;
            }

            if (dto.getChargeInstance() != null) {
                chargeInstance = (ChargeInstance) chargeInstanceService.findByCode(dto.getChargeInstance());
            }

            WalletOperation wo = null;
            if (chargeInstance != null) {
                BigDecimal ratingQuantity = chargeTemplateService.evaluateRatingQuantity(chargeInstance.getChargeTemplate(), dto.getQuantity());

                Date invoicingDate = null;
                if (chargeInstance.getInvoicingCalendar() != null) {

                    Date defaultInitDate = null;
                    if (chargeInstance instanceof RecurringChargeInstance && ((RecurringChargeInstance) chargeInstance).getSubscriptionDate() != null) {
                        defaultInitDate = ((RecurringChargeInstance) chargeInstance).getSubscriptionDate();
                    } else if (chargeInstance.getServiceInstance() != null) {
                        defaultInitDate = chargeInstance.getServiceInstance().getSubscriptionDate();
                    } else if (chargeInstance != null && chargeInstance.getSubscription() != null) {
                        defaultInitDate = chargeInstance.getSubscription().getSubscriptionDate();
                    }

                    Calendar invoicingCalendar = CalendarService.initializeCalendar(chargeInstance.getInvoicingCalendar(), defaultInitDate, chargeInstance);
                    invoicingDate = invoicingCalendar.nextCalendarDate(dto.getOperationDate());
                }

                wo = new WalletOperation(chargeInstance, dto.getQuantity(), ratingQuantity, dto.getOperationDate(), dto.getOrderNumber(), dto.getParameter1(), dto.getParameter2(), dto.getParameter3(),
                    dto.getParameterExtra(), tax, dto.getStartDate(), dto.getEndDate(), null, invoicingDate);

            } else {
                Seller seller = null;
                WalletInstance wallet = null;
                Currency currency = null;
                OfferTemplate offer = null;

                if (dto.getOfferCode() != null) {
                    offer = offerTemplateService.findByCode(dto.getOfferCode());
                }

                if (dto.getSeller() != null) {
                    seller = sellerService.findByCode(dto.getSeller());
                }
                if (dto.getWalletId() != null) {
                    wallet = walletService.findById(dto.getWalletId());
                }
                if (dto.getCurrency() != null) {
                    currency = currencyService.findByCode(dto.getCurrency());
                }
                wo = new WalletOperation(dto.getCode(), "description", wallet, dto.getOperationDate(), null, dto.getType(), currency, tax, dto.getUnitAmountWithoutTax(), dto.getUnitAmountWithTax(),
                    dto.getUnitAmountTax(), dto.getQuantity(), dto.getAmountWithoutTax(), dto.getAmountWithTax(), dto.getAmountTax(), dto.getParameter1(), dto.getParameter2(), dto.getParameter3(),
                    dto.getParameterExtra(), dto.getStartDate(), dto.getEndDate(), dto.getSubscriptionDate(), offer, seller, null, dto.getRatingUnitDescription(), null, null, null, null, dto.getStatus());
            }
            Integer sortIndex = RatingService.getSortIndex(wo);
            wo.setSortIndex(sortIndex);
            create(wo);
        }
    }

    /**
     * Mark wallet operations, that were invoiced by a given billing run, to be rerated
     * 
     * @param billingRun Billing run that invoiced wallet operations
     */
    public void markToRerateByBR(BillingRun billingRun) {

        List<WalletOperation> walletOperations = getEntityManager().createNamedQuery("WalletOperation.listByBRId", WalletOperation.class).setParameter("brId", billingRun.getId()).getResultList();

        for (WalletOperation walletOperation : walletOperations) {
            walletOperation.changeStatus(WalletOperationStatusEnum.TO_RERATE);
        }
    }

    /**
     * @param firstDate
     * @param lastDate
     * @param lastId
     * @param maxResult
     * @param formattedStatus
     * @return
     */
    public List<WalletOperation> getWalletOperationBetweenTwoDatesByStatus(Date firstDate, Date lastDate, Long lastId, int maxResult, List<WalletOperationStatusEnum> formattedStatus) {
        return getEntityManager().createNamedQuery("WalletOperation.listWObetweenTwoDatesByStatus", WalletOperation.class).setParameter("firstTransactionDate", firstDate).setParameter("lastTransactionDate", lastDate)
            .setParameter("lastId", lastId).setParameter("status", formattedStatus).setMaxResults(maxResult).getResultList();
    }

    public long purge(Date firstTransactionDate, Date lastTransactionDate, List<WalletOperationStatusEnum> targetStatusList) {
        return getEntityManager().createNamedQuery("WalletOperation.deleteWObetweenTwoDatesByStatus").setParameter("status", targetStatusList).setParameter("firstTransactionDate", firstTransactionDate)
            .setParameter("lastTransactionDate", lastTransactionDate).executeUpdate();
    }

    /**
     * Remove wallet operation rated 0 and chargeTemplate.dropZeroWo=true.
     */
    public void removeZeroWalletOperation() {
        getEntityManager().createNamedQuery("WalletOperation.deleteZeroWO").executeUpdate();
    }

    /**
     * Mark Wallet operation as failed to re-rate
     *
     * @param id Wallet operation identifier
     * @param e Exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void markAsFailedToRerateInNewTx(Long id, Exception e) {

        String message = e instanceof NullPointerException ? "NPE" : e.getMessage();
        getEntityManager().createNamedQuery("WalletOperation.setStatusFailedToRerate").setParameter("now", new Date()).setParameter("rejectReason", message).setParameter("id", id).executeUpdate();
    }

    /**
     * Update Wallet operations to status Canceled and cancel related RTs. Only unbilled wallet operations will be considered. In case of Wallet operation aggregation to a single
     * Rated transaction, all OTHER related wallet operations to the same Rated transaction, will be marked as Open (only the related ones).
     *
     * Note, that a number of Wallet operation ids passed and a number of Wallet operations marked as Canceled and Opened might not match if aggregation was used, or Wallet
     * operation status were invalid.
     *
     * @param walletOperationIds A list of Wallet operation ids to mark as Canceled
     * @return Number of wallet operations marked for Canceled
     */
    public int cancelWalletOperations(List<Long> walletOperationIds) {

        if (walletOperationIds.isEmpty()) {
            return 0;
        }

        int nrOfWosUpdated = 0;

        // Ignore Rated transactions that were billed already
        List<Long> walletOperationsBilled = getEntityManager().createNamedQuery("WalletOperation.getWalletOperationsBilled", Long.class).setParameter("walletIdList", walletOperationIds).getResultList();
        walletOperationIds.removeAll(walletOperationsBilled);

        // Cancel related RTS and change WO status to Canceled. Note: in case of aggregation, WOs that were aggregated under same RT will be marked as Open
        if (!walletOperationIds.isEmpty()) {
            // Cancel related RTS
            int nrRtsCanceled = getEntityManager().createNamedQuery("RatedTransaction.cancelByWOIds").setParameter("woIds", walletOperationIds).setParameter("now", new Date()).executeUpdate();

            // Change WO status to Canceled
            nrOfWosUpdated = getEntityManager().createNamedQuery("WalletOperation.setStatusToCanceledById").setParameter("now", new Date()).setParameter("woIds", walletOperationIds).executeUpdate();

            // In case of aggregation, WOs that were aggregated under same RT will be marked as Open
            if (nrRtsCanceled > 0) {
                nrOfWosUpdated = nrOfWosUpdated
                        + getEntityManager().createNamedQuery("WalletOperation.setStatusToOpenForWosThatAreRelatedByRTsById").setParameter("now", new Date()).setParameter("woIds", walletOperationIds).executeUpdate();
            }

            log.info("{} out of {} requested Wallet operations are canceled/marked for rerating", nrOfWosUpdated, walletOperationIds.size());
        }
        return nrOfWosUpdated;
    }

    /**
     * Refund already billed wallet operations by creating an identical wallet operation with a negated amount and status OPEN
     *
     * @param ids A list of wallet operation identifiers to refund
     * @return A list of newly created wallet operations with a negated amount and status OPEN
     */
    public List<WalletOperation> refundWalletOperations(List<Long> ids) {

        List<WalletOperation> refundedWOs = new ArrayList<WalletOperation>();
        List<WalletOperation> invoicedWos = findByIds(ids);
        Date today = new Date();
        for (WalletOperation invoicedWo : invoicedWos) {

            // A refund WO
            WalletOperation wo = invoicedWo.getClone();
            wo.setAmountTax(wo.getAmountTax().negate());
            wo.setAmountWithoutTax(wo.getAmountWithoutTax().negate());
            wo.setAmountWithTax(wo.getAmountWithTax().negate());
            wo.setStatus(WalletOperationStatusEnum.OPEN);
            wo.setCreated(today);
            wo.setUpdated(null);
            create(wo);

            refundedWOs.add(wo);
        }
        return refundedWOs;
    }
}