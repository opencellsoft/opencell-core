package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;

/**
 * The Class SubscriptionRenewalDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionRenewalDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4718234196806858689L;

    /** The initial period for which the subscription will be active - value. */
    private Integer initialyActiveFor;

    /** The initial period for which the subscription will be active - unit. */
    private RenewalPeriodUnitEnum initialyActiveForUnit;

    /** Should subscription be renewed automatically. */
    private boolean autoRenew;

    /** Number of days before the end of term to trigger notification event. */
    private Integer daysNotifyRenewal;

    /** Whether the Subscription should be suspended or terminated if not renewed. */
    private EndOfTermActionEnum endOfTermAction;

    /** TerminationReason used when terminating subscription if endOfTermAction is to terminate. */
    private String terminationReasonCode;

    /** The period to renew subscription for - value. */
    private Integer renewFor;

    /** The period to renew subscription for - units. */
    private RenewalPeriodUnitEnum renewForUnit;

    /** Whether end of agreement date should be matched to the active till date. */
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
        initialyActiveFor = renewalInfo.getInitialyActiveFor();
        initialyActiveForUnit = renewalInfo.getInitialyActiveForUnit();
        renewFor = renewalInfo.getRenewFor();
        renewForUnit = renewalInfo.getRenewForUnit();
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
}