package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.UsageChargeTemplate;

public class CachedUsageChargeInstance implements Comparable<CachedUsageChargeInstance>, Serializable {

    private static final long serialVersionUID = 2291426584953781851L;

    private Long id;
    private Long currencyId;
    private Date lastUpdate;
    private Date chargeDate;
    private Date subscriptionDate;
    private Date terminationDate;
    private String ratingUnitDescription;
    private Long chargeTemplateId;
    private Long counterInstanceId;
    private String description;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;

    // Properties copied from chargeTemplate;
    private int priority;

    public Long getId() {
        return id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Long getCounterInstanceId() {
        return counterInstanceId;
    }

    public Date getChargeDate() {
        return chargeDate;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public String getDescription() {
        return description;
    }

    public Long getChargeTemplateId() {
        return chargeTemplateId;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    @Override
    public int compareTo(CachedUsageChargeInstance o) {
        return priority - o.priority;
    }

    /**
     * Populate with info from charge instance and charge template
     * 
     * @param usageChargeInstance Charge instance
     * @param usageChargeTemplate Charge template
     * @param cachedCounterInstance Counter instance
     */
    public void populateFromUsageChargeInstance(UsageChargeInstance usageChargeInstance, UsageChargeTemplate usageChargeTemplate, CachedCounterInstance cachedCounterInstance) {

        subscriptionDate = usageChargeInstance.getServiceInstance().getSubscriptionDate();
        chargeDate = usageChargeInstance.getChargeDate();
        id = usageChargeInstance.getId();
        currencyId = usageChargeInstance.getCurrency().getId();
        terminationDate = usageChargeInstance.getTerminationDate();
        ratingUnitDescription = usageChargeInstance.getRatingUnitDescription();
        description = usageChargeInstance.getDescription();
        amountWithoutTax = usageChargeInstance.getAmountWithoutTax();
        amountWithTax = usageChargeInstance.getAmountWithTax();

        if (cachedCounterInstance != null) {
            counterInstanceId = cachedCounterInstance.getId();
        }
        chargeTemplateId = usageChargeTemplate.getId();
        lastUpdate = new Date();

        // Copy values from charge template
        priority = usageChargeTemplate.getPriority();
    }

    /**
     * Update stored charge template information. Safe to call on any charge instance - a match for charge template will be done first
     * 
     * @param chargeTemplate Charge template
     * @return True, if charge template ID matched and charge template info was updated
     */
    public boolean updateChargeTemplateInfo(UsageChargeTemplate chargeTemplate) {

        if (chargeTemplateId.equals(chargeTemplate.getId())) {

            // Copy values from charge template
            priority = chargeTemplate.getPriority();
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "CachedUsageChargeInstance [id=" + id + ", currencyId=" + currencyId + ", lastUpdate=" + lastUpdate + ", chargeDate=" + chargeDate + ", subscriptionDate="
                + subscriptionDate + ", terminationDate=" + terminationDate + ", ratingUnitDescription=" + ratingUnitDescription + ", chargeTemplateId=" + chargeTemplateId
                + ", counterInstanceId=" + counterInstanceId + ", description=" + description + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax
                + ", priority=" + priority + "]";
    }
}