package org.meveo.model.billing;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

@Embeddable
public class SubscriptionRenewal implements Serializable {

    private static final long serialVersionUID = 7391688555444183997L;

    /**
     * End of subscription term action to be taken
     */
    public enum EndOfTermActionEnum {
        /**
         * Suspend subscription
         */
        SUSPEND,

        /**
         * Terminate subscription
         */
        TERMINATE;

        public String getLabel() {
            return this.getClass().getSimpleName() + "." + this.name();
        }
    }

    /**
     * Subscription renewal period unit
     */
    public enum RenewalPeriodUnitEnum {
        /**
         * Month
         */
        MONTH(Calendar.MONTH),

        /**
         * Day
         */
        DAY(Calendar.DAY_OF_MONTH);

        int calendarField;

        RenewalPeriodUnitEnum(int calendarField) {
            this.calendarField = calendarField;
        }

        public String getLabel() {
            return this.getClass().getSimpleName() + "." + this.name();
        }

        public int getCalendarField() {
            return calendarField;
        }
    }

    /**
     * Should subscription be renewed automatically
     */
    @Type(type = "numeric_boolean")
    @Column(name = "auto_renew")
    private boolean autoRenew;

    /**
     * Number of days before the end of term to trigger notification event
     */
    @Column(name = "days_notify_renew")
    private Integer daysNotifyRenewal;

    /**
     * Whether the Subscription should be suspended or terminated if not renewed
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "end_of_term_action", length = 10)
    private EndOfTermActionEnum endOfTermAction;

    /**
     * TerminationReason used when terminating subscription if endOfTermAction is to terminate
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_termin_reason_id")
    private SubscriptionTerminationReason terminationReason;

    /**
     * The initial period for which the subscription will be active - unit
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "init_active_unit", length = 5)
    private RenewalPeriodUnitEnum initialyActiveForUnit;

    /**
     * The initial period for which the subscription will be active - value
     */
    @Column(name = "init_active")
    private Integer initialyActiveFor;

    /**
     * Whether end of agreement date should be matched to the active till date
     */
    @Type(type = "numeric_boolean")
    @Column(name = "match_end_aggr_date")
    private boolean extendAgreementPeriodToSubscribedTillDate;

    /**
     * The period to renew subscription for - units
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "renew_for_unit", length = 5)
    private RenewalPeriodUnitEnum renewForUnit;

    /**
     * The period to renew subscription for - value
     */
    @Column(name = "renew_for")
    private Integer renewFor;

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public Integer getDaysNotifyRenewal() {
        return daysNotifyRenewal;
    }

    public void setDaysNotifyRenewal(Integer daysNotifyRenewal) {
        this.daysNotifyRenewal = daysNotifyRenewal;
    }

    public EndOfTermActionEnum getEndOfTermAction() {
        return endOfTermAction;
    }

    public void setEndOfTermAction(EndOfTermActionEnum endOfTermAction) {
        this.endOfTermAction = endOfTermAction;
    }

    public SubscriptionTerminationReason getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
        this.terminationReason = terminationReason;
    }

    public RenewalPeriodUnitEnum getInitialyActiveForUnit() {
        return initialyActiveForUnit;
    }

    public void setInitialyActiveForUnit(RenewalPeriodUnitEnum initialyActiveForUnit) {
        this.initialyActiveForUnit = initialyActiveForUnit;
    }

    public Integer getInitialyActiveFor() {
        return initialyActiveFor;
    }

    public void setInitialyActiveFor(Integer initialyActiveFor) {
        this.initialyActiveFor = initialyActiveFor;
    }

    public boolean isExtendAgreementPeriodToSubscribedTillDate() {
        return extendAgreementPeriodToSubscribedTillDate;
    }

    public void setExtendAgreementPeriodToSubscribedTillDate(boolean extendAgreementPeriodToSubscribedTillDate) {
        this.extendAgreementPeriodToSubscribedTillDate = extendAgreementPeriodToSubscribedTillDate;
    }

    public RenewalPeriodUnitEnum getRenewForUnit() {
        return renewForUnit;
    }

    public void setRenewForUnit(RenewalPeriodUnitEnum renewForUnit) {
        this.renewForUnit = renewForUnit;
    }

    public Integer getRenewFor() {
        return renewFor;
    }

    public void setRenewFor(Integer renewFor) {
        this.renewFor = renewFor;
    }
}