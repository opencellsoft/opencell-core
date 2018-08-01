package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class OfferPricePlanDto.
 *
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 */
@XmlRootElement(name = "OfferPricePlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferPricePlanDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3592282981490299021L;

    /** The offer id. */
    private String offerId;

    /** The organization id. */
    private String organizationId;

    /** The tax id. */
    private String taxId;

    /** The subscription prorata. */
    private Boolean subscriptionProrata;

    /** The termination prorata. */
    private Boolean terminationProrata;

    /** The apply in advance. */
    private Boolean applyInAdvance;

    /** The param 1. */
    private String param1;

    /** The param 2. */
    private String param2;

    /** The param 3. */
    private String param3;

    /** The billing period. */
    private String billingPeriod;

    /** The recurring charges. */
    private List<RecurringChargeDto> recurringCharges;

    /** The usage unit. */
    private String usageUnit;

    /** The usage charges. */
    private List<UsageChargeDto> usageCharges;

    /** The subscription fees. */
    private List<SubscriptionFeeDto> subscriptionFees;

    /** The termination fees. */
    private List<TerminationFeeDto> terminationFees;

    /**
     * Gets the offer id.
     *
     * @return the offer id
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * Sets the offer id.
     *
     * @param offerId the new offer id
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * Gets the organization id.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the organization id.
     *
     * @param organizationId the new organization id
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Gets the tax id.
     *
     * @return the tax id
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * Sets the tax id.
     *
     * @param taxId the new tax id
     */
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    /**
     * Gets the subscription prorata.
     *
     * @return the subscription prorata
     */
    public Boolean getSubscriptionProrata() {
        return subscriptionProrata;
    }

    /**
     * Sets the subscription prorata.
     *
     * @param subscriptionProrata the new subscription prorata
     */
    public void setSubscriptionProrata(Boolean subscriptionProrata) {
        this.subscriptionProrata = subscriptionProrata;
    }

    /**
     * Gets the termination prorata.
     *
     * @return the termination prorata
     */
    public Boolean getTerminationProrata() {
        return terminationProrata;
    }

    /**
     * Sets the termination prorata.
     *
     * @param terminationProrata the new termination prorata
     */
    public void setTerminationProrata(Boolean terminationProrata) {
        this.terminationProrata = terminationProrata;
    }

    /**
     * Gets the apply in advance.
     *
     * @return the apply in advance
     */
    public Boolean getApplyInAdvance() {
        return applyInAdvance;
    }

    /**
     * Sets the apply in advance.
     *
     * @param applyInAdvance the new apply in advance
     */
    public void setApplyInAdvance(Boolean applyInAdvance) {
        this.applyInAdvance = applyInAdvance;
    }

    /**
     * Gets the param 1.
     *
     * @return the param 1
     */
    public String getParam1() {
        return param1;
    }

    /**
     * Sets the param 1.
     *
     * @param param1 the new param 1
     */
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    /**
     * Gets the param 2.
     *
     * @return the param 2
     */
    public String getParam2() {
        return param2;
    }

    /**
     * Sets the param 2.
     *
     * @param param2 the new param 2
     */
    public void setParam2(String param2) {
        this.param2 = param2;
    }

    /**
     * Gets the param 3.
     *
     * @return the param 3
     */
    public String getParam3() {
        return param3;
    }

    /**
     * Sets the param 3.
     *
     * @param param3 the new param 3
     */
    public void setParam3(String param3) {
        this.param3 = param3;
    }

    /**
     * Gets the billing period.
     *
     * @return the billing period
     */
    public String getBillingPeriod() {
        return billingPeriod;
    }

    /**
     * Sets the billing period.
     *
     * @param billingPeriod the new billing period
     */
    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    /**
     * Gets the recurring charges.
     *
     * @return the recurring charges
     */
    public List<RecurringChargeDto> getRecurringCharges() {
        return recurringCharges;
    }

    /**
     * Sets the recurring charges.
     *
     * @param recurringCharges the new recurring charges
     */
    public void setRecurringCharges(List<RecurringChargeDto> recurringCharges) {
        this.recurringCharges = recurringCharges;
    }

    /**
     * Gets the usage unit.
     *
     * @return the usage unit
     */
    public String getUsageUnit() {
        return usageUnit;
    }

    /**
     * Sets the usage unit.
     *
     * @param usageUnit the new usage unit
     */
    public void setUsageUnit(String usageUnit) {
        this.usageUnit = usageUnit;
    }

    /**
     * Gets the usage charges.
     *
     * @return the usage charges
     */
    public List<UsageChargeDto> getUsageCharges() {
        return usageCharges;
    }

    /**
     * Sets the usage charges.
     *
     * @param usageCharges the new usage charges
     */
    public void setUsageCharges(List<UsageChargeDto> usageCharges) {
        this.usageCharges = usageCharges;
    }

    /**
     * Gets the subscription fees.
     *
     * @return the subscription fees
     */
    public List<SubscriptionFeeDto> getSubscriptionFees() {
        return subscriptionFees;
    }

    /**
     * Sets the subscription fees.
     *
     * @param subscriptionFees the new subscription fees
     */
    public void setSubscriptionFees(List<SubscriptionFeeDto> subscriptionFees) {
        this.subscriptionFees = subscriptionFees;
    }

    /**
     * Gets the termination fees.
     *
     * @return the termination fees
     */
    public List<TerminationFeeDto> getTerminationFees() {
        return terminationFees;
    }

    /**
     * Sets the termination fees.
     *
     * @param terminationFees the new termination fees
     */
    public void setTerminationFees(List<TerminationFeeDto> terminationFees) {
        this.terminationFees = terminationFees;
    }

}
