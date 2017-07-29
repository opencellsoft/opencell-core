package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;

@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionRenewalDto implements Serializable {

    private static final long serialVersionUID = 4718234196806858689L;

    /**
     * The initial period for which the subscription will be active - value
     */
    private Integer initialyActiveFor;

    /**
     * The initial period for which the subscription will be active - unit
     */
    private RenewalPeriodUnitEnum initialyActiveForUnit;

    /**
     * Should subscription be renewed automatically
     */
    private boolean autoRenew;

    /**
     * Number of days before the end of term to trigger notification event
     */
    private Integer daysNotifyRenewal;

    /**
     * Whether the Subscription should be suspended or terminated if not renewed
     */
    private EndOfTermActionEnum endOfTermAction;

    /**
     * TerminationReason used when terminating subscription if endOfTermAction is to terminate
     */
    private String terminationReasonCode;

    /**
     * The period to renew subscription for - value
     */
    private Integer renewFor;

    /**
     * The period to renew subscription for - units
     */
    private RenewalPeriodUnitEnum renewForUnit;

    /**
     * Whether end of agreement date should be matched to the active till date
     */
    private boolean extendAgreementPeriodToSubscribedTillDate;

    public SubscriptionRenewalDto() {

    }

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

    public Integer getInitialyActiveFor() {
        return initialyActiveFor;
    }

    public void setInitialyActiveFor(Integer initialyActiveFor) {
        this.initialyActiveFor = initialyActiveFor;
    }

    public RenewalPeriodUnitEnum getInitialyActiveForUnit() {
        return initialyActiveForUnit;
    }

    public void setInitialyActiveForUnit(RenewalPeriodUnitEnum initialyActiveForUnit) {
        this.initialyActiveForUnit = initialyActiveForUnit;
    }

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

    public String getTerminationReasonCode() {
        return terminationReasonCode;
    }

    public void setTerminationReasonCode(String terminationReasonCode) {
        this.terminationReasonCode = terminationReasonCode;
    }

    public Integer getRenewFor() {
        return renewFor;
    }

    public void setRenewFor(Integer renewFor) {
        this.renewFor = renewFor;
    }

    public RenewalPeriodUnitEnum getRenewForUnit() {
        return renewForUnit;
    }

    public void setRenewForUnit(RenewalPeriodUnitEnum renewForUnit) {
        this.renewForUnit = renewForUnit;
    }

    public boolean isExtendAgreementPeriodToSubscribedTillDate() {
        return extendAgreementPeriodToSubscribedTillDate;
    }

    public void setExtendAgreementPeriodToSubscribedTillDate(boolean extendAgreementPeriodToSubscribedTillDate) {
        this.extendAgreementPeriodToSubscribedTillDate = extendAgreementPeriodToSubscribedTillDate;
    }
}