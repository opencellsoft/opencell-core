package org.meveo.model.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.NumberUtil;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.catalog.UsageChargeTemplate;

public class CachedUsageChargeInstance implements Comparable<CachedUsageChargeInstance> {

    private Long id;
    private Long currencyId;
    private Date lastUpdate;
    int roundingUnityNbDecimal = 2;
    int roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
    private Date chargeDate;
    private Date subscriptionDate;
    private Date terminationDate;
    private String ratingUnitDescription;
    private CachedUsageChargeTemplate chargeTemplate;
    private CachedCounterInstance counter;
    private String description;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;

    public Long getId() {
        return id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    private void computeRoundingValues() {
        try {
            if (chargeTemplate.getUnitNbDecimal() >= BaseEntity.NB_DECIMALS) {
                roundingUnityNbDecimal = BaseEntity.NB_DECIMALS;
            } else {
                roundingUnityNbDecimal = chargeTemplate.getUnitNbDecimal();
                roundingEdrNbDecimal = (int) Math.round(roundingUnityNbDecimal + Math.floor(Math.log10(chargeTemplate.getUnitMultiplicator().doubleValue())));
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

    public CachedUsageChargeTemplate getChargeTemplate() {
        return chargeTemplate;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    @Override
    public int compareTo(CachedUsageChargeInstance o) {
        return this.getChargeTemplate().getPriority() - o.getChargeTemplate().getPriority();
    }

    public BigDecimal getInChargeUnit(BigDecimal edrUnitValue) throws BusinessException {

        if (edrUnitValue == null) {
            throw new BusinessException("Cant get countedValue with null quantity");
        }
        BigDecimal result = NumberUtil.getInChargeUnit(edrUnitValue, chargeTemplate.getUnitMultiplicator(), chargeTemplate.getUnitNbDecimal(), chargeTemplate.getRoundingMode());
        return result;
    }

    public BigDecimal getInEDRUnit(BigDecimal chargeUnitValue) {
        return chargeUnitValue.divide(chargeTemplate.getUnitMultiplicator(), roundingEdrNbDecimal, RoundingMode.HALF_UP);
    }

    public void populateFromUsageChargeInstance(UsageChargeInstance usageChargeInstance, UsageChargeTemplate usageChargeTemplate, CachedUsageChargeTemplate cachedTemplate,
            CachedCounterInstance counterCacheValue) {

        subscriptionDate = usageChargeInstance.getServiceInstance().getSubscriptionDate();
        chargeDate = usageChargeInstance.getChargeDate();
        id = usageChargeInstance.getId();
        currencyId = usageChargeInstance.getCurrency().getId();
        terminationDate = usageChargeInstance.getTerminationDate();
        ratingUnitDescription = usageChargeInstance.getRatingUnitDescription();
        description = usageChargeInstance.getDescription();
        amountWithoutTax = usageChargeInstance.getAmountWithoutTax();
        amountWithTax = usageChargeInstance.getAmountWithTax();

        counter = counterCacheValue;
        chargeTemplate = cachedTemplate;
        lastUpdate = new Date();
        computeRoundingValues();
    }

    @Override
    public String toString() {
        return String
            .format(
                "CachedUsageChargeInstance [id=%s, currencyId=%s, lastUpdate=%s, roundingUnityNbDecimal=%s, roundingEdrNbDecimal=%s, chargeDate=%s, subscriptionDate=%s, terminationDate=%s, ratingUnitDescription=%s, description=%s, amountWithoutTax=%s, amountWithTax=%s]",
                id, currencyId, lastUpdate, roundingUnityNbDecimal, roundingEdrNbDecimal, chargeDate, subscriptionDate, terminationDate, ratingUnitDescription,
                description, amountWithoutTax, amountWithTax);
    }
}