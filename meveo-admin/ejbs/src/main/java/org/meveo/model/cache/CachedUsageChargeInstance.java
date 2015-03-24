package org.meveo.model.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.meveo.model.BaseEntity;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;

public class CachedUsageChargeInstance implements Comparable<CachedUsageChargeInstance> {

    private Long id;
    private Provider provider;
    private Long currencyId;
    private Date lastUpdate;
    private BigDecimal unityMultiplicator = BigDecimal.ONE;
    private int unityNbDecimal = 2;
    int roundingUnityNbDecimal = 2;
    int roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
    private CachedCounterInstance counter;
    private Date chargeDate;
    private Date subscriptionDate;
    private Date terminationDate;
    private CachedUsageChargeTemplate templateCache;

    public Long getId() {
        return id;
    }

    public Provider getProvider() {
        return provider;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public BigDecimal getUnityMultiplicator() {
        return unityMultiplicator;
    }

    public int getUnityNbDecimal() {
        return unityNbDecimal;
    }

    private void computeRoundingValues() {
        try {
            if (unityNbDecimal >= BaseEntity.NB_DECIMALS) {
                roundingUnityNbDecimal = BaseEntity.NB_DECIMALS;
            } else {
                roundingUnityNbDecimal = unityNbDecimal;
                roundingEdrNbDecimal = (int) Math.round(unityNbDecimal + Math.floor(Math.log10(unityMultiplicator.doubleValue())));
                if (roundingEdrNbDecimal > BaseEntity.NB_DECIMALS) {
                    roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
                }
            }
        } catch (Exception e) {
        }
    }

    public CachedCounterInstance getCounter() {
        return counter;
    }

    public void setCounter(CachedCounterInstance counter) {
        this.counter = counter;
    }

    public Date getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(Date chargeDate) {
        this.chargeDate = chargeDate;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public CachedUsageChargeTemplate getTemplateCache() {
        return templateCache;
    }

    public void setTemplateCache(CachedUsageChargeTemplate templateCache) {
        this.templateCache = templateCache;
    }

    @Override
    public int compareTo(CachedUsageChargeInstance o) {
        return this.getTemplateCache().getPriority() - o.getTemplateCache().getPriority();
    }

    public BigDecimal getInChargeUnit(BigDecimal edrUnitValue) {
        if (unityMultiplicator == null)
            unityMultiplicator = BigDecimal.ONE;
        BigDecimal result = edrUnitValue.multiply(unityMultiplicator);

        if (unityNbDecimal > 0) {
            result = result.setScale(roundingUnityNbDecimal, RoundingMode.HALF_UP);
        }

        return result;
    }

    public BigDecimal getInEDRUnit(BigDecimal chargeUnitValue) {
        return chargeUnitValue.divide(unityMultiplicator, roundingEdrNbDecimal, RoundingMode.HALF_UP);
    }

    public void populateFromUsageChargeInstance(UsageChargeInstance usageChargeInstance, UsageChargeTemplate usageChargeTemplate, CachedUsageChargeTemplate cachedTemplate,
            CachedCounterInstance counterCacheValue) {

        subscriptionDate = usageChargeInstance.getServiceInstance().getSubscriptionDate();
        chargeDate = usageChargeInstance.getChargeDate();
        id = usageChargeInstance.getId();
        usageChargeInstance.getProvider().getCode();
        provider = usageChargeInstance.getProvider();
        currencyId = usageChargeInstance.getCurrency().getId();
        counter = counterCacheValue;
        terminationDate = usageChargeInstance.getTerminationDate();
        templateCache = cachedTemplate;
        unityMultiplicator = usageChargeTemplate.getUnityMultiplicator();
        unityNbDecimal = usageChargeTemplate.getUnityNbDecimal();
        lastUpdate = new Date();
        computeRoundingValues();
    }

    @Override
    public String toString() {
        return String
            .format(
                "CachedUsageChargeInstance [id=%s, provider=%s, currencyId=%s, lastUpdate=%s, unityMultiplicator=%s, unityNbDecimal=%s, roundingUnityNbDecimal=%s, roundingEdrNbDecimal=%s, counter=%s, chargeDate=%s, subscriptionDate=%s, terminationDate=%s, templateCache=%s]",
                id, provider, currencyId, lastUpdate, unityMultiplicator, unityNbDecimal, roundingUnityNbDecimal, roundingEdrNbDecimal, counter, chargeDate, subscriptionDate,
                terminationDate, templateCache);
    }
}