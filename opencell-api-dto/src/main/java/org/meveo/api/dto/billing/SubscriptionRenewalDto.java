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

package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.CalendarDto;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class SubscriptionRenewalDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionRenewalDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4718234196806858689L;

    @Schema(description = "intial term type", example = "RECURRING, CALENDAR, FIXED")
    private SubscriptionRenewal.InitialTermTypeEnum initialTermType;

    @Schema(description = "renewal term type", example = "possible value are : RECURRING, CALENDAR")
    private SubscriptionRenewal.RenewalTermTypeEnum renewalTermType;

    /** The initial period for which the subscription will be active - value. */
    @Schema(description = "The initial period for which the subscription will be active")
    private Integer initialyActiveFor;

    /** The initial period for which the subscription will be active - unit. */
    @Schema(description = "The initial period for which the subscription will be active", example = "possible value are : MONTH, DAY")
    private RenewalPeriodUnitEnum initialyActiveForUnit;

    @Schema(description = "calendar initial for subscription")
    private CalendarDto calendarInitialyActiveFor;

    /** Should subscription be renewed automatically. */
    @Schema(description = "Should subscription be renewed automatically")
    private boolean autoRenew;

    /** Number of days before the end of term to trigger notification event. */
    @Schema(description = "Number of days before the end of term to trigger notification event")
    private Integer daysNotifyRenewal;

    /** Whether the Subscription should be suspended or terminated if not renewed. */
    @Schema(description = "Whether the Subscription should be suspended or terminated if not renewed",example = "possible value are : SUSPEND, TERMINATE")
    private EndOfTermActionEnum endOfTermAction;

    /** TerminationReason used when terminating subscription if endOfTermAction is to terminate. */
    @Schema(description = "terminating subscription if endOfTermAction is to terminate")
    private String terminationReasonCode;

    /** The period to renew subscription for - value. */
    @Schema(description = "The period to renew subscription for")
    private Integer renewFor;

    @Schema(description = "calendar associated to subscription renewal")
    private CalendarDto calendarRenewFor;

    /** The period to renew subscription for - units. */
    @Schema(description = "he period to renew subscription for", example = "possible value are : MONTH, DAY")
    private RenewalPeriodUnitEnum renewForUnit;

    /** Whether end of agreement date should be matched to the active till date. */
    @Schema(description = "Whether end of agreement date should be matched to the active till date")
    private boolean extendAgreementPeriodToSubscribedTillDate;

    /**
     * Instantiates a new subscription renewal dto.
     */
    public SubscriptionRenewalDto() {

    }

    /**
     * Instantiates a new subscription renewal dto.
     *
     * @param renewalInfo the renewal info
     */
    public SubscriptionRenewalDto(SubscriptionRenewal renewalInfo) {
        if (renewalInfo == null) {
            return;
        }

        autoRenew = renewalInfo.isAutoRenew();
        daysNotifyRenewal = renewalInfo.getDaysNotifyRenewal();
        endOfTermAction = renewalInfo.getEndOfTermAction();
        extendAgreementPeriodToSubscribedTillDate = renewalInfo.isExtendAgreementPeriodToSubscribedTillDate();
        initialTermType = renewalInfo.getInitialTermType();
        initialyActiveFor = renewalInfo.getInitialyActiveFor();
        initialyActiveForUnit = renewalInfo.getInitialyActiveForUnit();
        if(renewalInfo.getCalendarInitialyActiveFor() != null) {
            calendarInitialyActiveFor = new CalendarDto(renewalInfo.getCalendarInitialyActiveFor());
        }
        renewFor = renewalInfo.getRenewFor();
        renewForUnit = renewalInfo.getRenewForUnit();
        if(renewalInfo.getCalendarRenewFor() != null) {
            calendarRenewFor = new CalendarDto(renewalInfo.getCalendarRenewFor());
        }
        if (renewalInfo.getTerminationReason() != null) {
            terminationReasonCode = renewalInfo.getTerminationReason().getCode();
        }
    }

    /**
     * Gets the initialy active for.
     *
     * @return the initialy active for
     */
    public Integer getInitialyActiveFor() {
        return initialyActiveFor;
    }

    /**
     * Sets the initialy active for.
     *
     * @param initialyActiveFor the new initialy active for
     */
    public void setInitialyActiveFor(Integer initialyActiveFor) {
        this.initialyActiveFor = initialyActiveFor;
    }

    /**
     * Gets the initialy active for unit.
     *
     * @return the initialy active for unit
     */
    public RenewalPeriodUnitEnum getInitialyActiveForUnit() {
        return initialyActiveForUnit;
    }

    /**
     * Sets the initialy active for unit.
     *
     * @param initialyActiveForUnit the new initialy active for unit
     */
    public void setInitialyActiveForUnit(RenewalPeriodUnitEnum initialyActiveForUnit) {
        this.initialyActiveForUnit = initialyActiveForUnit;
    }

    /**
     * Checks if is auto renew.
     *
     * @return true, if is auto renew
     */
    public boolean isAutoRenew() {
        return autoRenew;
    }

    /**
     * Sets the auto renew.
     *
     * @param autoRenew the new auto renew
     */
    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    /**
     * Gets the days notify renewal.
     *
     * @return the days notify renewal
     */
    public Integer getDaysNotifyRenewal() {
        return daysNotifyRenewal;
    }

    /**
     * Sets the days notify renewal.
     *
     * @param daysNotifyRenewal the new days notify renewal
     */
    public void setDaysNotifyRenewal(Integer daysNotifyRenewal) {
        this.daysNotifyRenewal = daysNotifyRenewal;
    }

    /**
     * Gets the end of term action.
     *
     * @return the end of term action
     */
    public EndOfTermActionEnum getEndOfTermAction() {
        return endOfTermAction;
    }

    /**
     * Sets the end of term action.
     *
     * @param endOfTermAction the new end of term action
     */
    public void setEndOfTermAction(EndOfTermActionEnum endOfTermAction) {
        this.endOfTermAction = endOfTermAction;
    }

    /**
     * Gets the termination reason code.
     *
     * @return the termination reason code
     */
    public String getTerminationReasonCode() {
        return terminationReasonCode;
    }

    /**
     * Sets the termination reason code.
     *
     * @param terminationReasonCode the new termination reason code
     */
    public void setTerminationReasonCode(String terminationReasonCode) {
        this.terminationReasonCode = terminationReasonCode;
    }

    /**
     * Gets the renew for.
     *
     * @return the renew for
     */
    public Integer getRenewFor() {
        return renewFor;
    }

    /**
     * Sets the renew for.
     *
     * @param renewFor the new renew for
     */
    public void setRenewFor(Integer renewFor) {
        this.renewFor = renewFor;
    }

    /**
     * Gets the renew for unit.
     *
     * @return the renew for unit
     */
    public RenewalPeriodUnitEnum getRenewForUnit() {
        return renewForUnit;
    }

    /**
     * Sets the renew for unit.
     *
     * @param renewForUnit the new renew for unit
     */
    public void setRenewForUnit(RenewalPeriodUnitEnum renewForUnit) {
        this.renewForUnit = renewForUnit;
    }

    /**
     * Checks if is extend agreement period to subscribed till date.
     *
     * @return true, if is extend agreement period to subscribed till date
     */
    public boolean isExtendAgreementPeriodToSubscribedTillDate() {
        return extendAgreementPeriodToSubscribedTillDate;
    }

    /**
     * Sets the extend agreement period to subscribed till date.
     *
     * @param extendAgreementPeriodToSubscribedTillDate the new extend agreement period to subscribed till date
     */
    public void setExtendAgreementPeriodToSubscribedTillDate(boolean extendAgreementPeriodToSubscribedTillDate) {
        this.extendAgreementPeriodToSubscribedTillDate = extendAgreementPeriodToSubscribedTillDate;
    }

    public CalendarDto getCalendarInitialyActiveFor() {
        return calendarInitialyActiveFor;
    }

    public void setCalendarInitialyActiveFor(CalendarDto calendarInitialyActiveFor) {
        this.calendarInitialyActiveFor = calendarInitialyActiveFor;
    }

    public SubscriptionRenewal.InitialTermTypeEnum getInitialTermType() {
        return initialTermType;
    }

    public void setInitialTermType(SubscriptionRenewal.InitialTermTypeEnum initialTermType) {
        this.initialTermType = initialTermType;
    }

    public SubscriptionRenewal.RenewalTermTypeEnum getRenewalTermType() {
        return renewalTermType;
    }

    public void setRenewalTermType(SubscriptionRenewal.RenewalTermTypeEnum renewalTermType) {
        this.renewalTermType = renewalTermType;
    }

    public CalendarDto getCalendarRenewFor() {
        return calendarRenewFor;
    }

    public void setCalendarRenewFor(CalendarDto calendarRenewFor) {
        this.calendarRenewFor = calendarRenewFor;
    }
}