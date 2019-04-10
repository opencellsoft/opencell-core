package org.meveo.model.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.CustomDateSerializer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Embeddable set of renewal fields. Use in ServiceTemplate and Subscription.
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Mounir BAHIJE
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
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
	 * The default subscription renewal term type. A subscription can be
	 * automatically renewed on a period basis or on a fixed date.
	 */
    public enum InitialTermTypeEnum {
        /**
         * Uses RenewalPeriodUnitEnum.
         */
        RECURRING,
        
        /**
         * Uses date picker.
         */
        FIXED;

        public String getLabel() {
            return this.getClass().getSimpleName() + "." + this.name();
        }
    }

    /**
     * Should subscription be renewed automatically
     */
    @Type(type = "numeric_boolean")
    @Column(name = "auto_renew")
    private boolean autoRenew;

    /**
     * Linked to auto_renew
     *
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "auto_renew_date")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date autoRenewDate;
    
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
    @JsonUnwrapped()
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

    /**
     * The instance of InitialTermTypeEnum for this subscription.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "initial_term_type")
    private InitialTermTypeEnum initialTermType = InitialTermTypeEnum.RECURRING;

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

    public void setDefaultInitialyActiveForUnit() {
        if (initialyActiveFor != null && initialyActiveForUnit == null) {
            initialyActiveForUnit = RenewalPeriodUnitEnum.MONTH;
        } else if (initialyActiveFor == null) {
            initialyActiveForUnit = null;
        }
    }

    public void setDefaultRenewForUnit() {
        if (renewFor != null && renewForUnit == null) {
            renewForUnit = RenewalPeriodUnitEnum.MONTH;
        } else if (renewFor == null) {
            renewForUnit = null;
        }
    }

    public InitialTermTypeEnum getInitialTermType() {
        return initialTermType;
    }

    public void setInitialTermType(InitialTermTypeEnum initialTermType) {
        this.initialTermType = initialTermType;
    }

    /**
     * AutoRenewDate getter
     * @return
     */
    public Date getAutoRenewDate() {
        return autoRenewDate;
    }

    /**
     * AutoRenewDate setter
     * @param autoRenewDate
     */
    public void setAutoRenewDate(Date autoRenewDate) {
        this.autoRenewDate = autoRenewDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionRenewal that = (SubscriptionRenewal) o;
        return autoRenew == that.autoRenew &&
                extendAgreementPeriodToSubscribedTillDate == that.extendAgreementPeriodToSubscribedTillDate &&
                Objects.equals(autoRenewDate, that.autoRenewDate) &&
                endOfTermAction == that.endOfTermAction &&
                initialyActiveForUnit == that.initialyActiveForUnit &&
                Objects.equals(initialyActiveFor, that.initialyActiveFor) &&
                renewForUnit == that.renewForUnit &&
                Objects.equals(renewFor, that.renewFor) &&
                initialTermType == that.initialTermType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(autoRenew, autoRenewDate, endOfTermAction, initialyActiveForUnit, initialyActiveFor, extendAgreementPeriodToSubscribedTillDate,
                renewForUnit, renewFor, initialTermType);
    }
}