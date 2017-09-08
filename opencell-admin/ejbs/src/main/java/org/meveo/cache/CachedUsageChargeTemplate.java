package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.NumberUtil;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;

public class CachedUsageChargeTemplate implements Serializable {

    private static final long serialVersionUID = 5720248298194275643L;

    private Long id;
    private String code;
    private String description;
    private Map<String, String> descriptionI18n;
    private Date lastUpdate;
    private int priority;
    private String filterExpression;
    private String filter1;
    private String filter2;
    private String filter3;
    private String filter4;
    private BigDecimal unitMultiplicator = BigDecimal.ONE;
    private int unitNbDecimal = 2;
    private RoundingModeEnum roundingMode;
    private Set<CachedTriggeredEDR> edrTemplates = new HashSet<CachedTriggeredEDR>();
    private Set<Long> subscriptionIds = new HashSet<Long>();
    private String ratingUnitDescription;
    private String inputUnitDescription;
    private String invoiceSubCategoryCode;

    // Calculated values
    private int roundingUnityNbDecimal = 2;
    private int roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public int getPriority() {
        return priority;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public String getFilter1() {
        return filter1;
    }

    public String getFilter2() {
        return filter2;
    }

    public String getFilter3() {
        return filter3;
    }

    public String getFilter4() {
        return filter4;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public String getInputUnitDescription() {
        return inputUnitDescription;
    }

    public Set<CachedTriggeredEDR> getEdrTemplates() {
        return edrTemplates;
    }

    public Set<Long> getSubscriptionIds() {
        return subscriptionIds;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public String getDescriptionOrCode() {
        if (!StringUtils.isBlank(description)) {
            return description;
        } else {
            return code;
        }
    }
    
    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }
    
    public BigDecimal getUnitMultiplicator() {
        return unitMultiplicator;
    }

    public int getUnitNbDecimal() {
        return unitNbDecimal;
    }

    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    public String getInvoiceSubCategoryCode() {
        return invoiceSubCategoryCode;
    }

    public CachedUsageChargeTemplate() {
    }

    public CachedUsageChargeTemplate(UsageChargeTemplate usageChargeTemplate) {
        populateFromUsageChargeTemplate(usageChargeTemplate);
    }

    @Override
    public String toString() {
        return String.format(
            "CachedUsageChargeTemplate [id=%s, code=%s, lastUpdate=%s, priority=%s, filterExpression=%s, filter1=%s, filter2=%s, filter3=%s, filter4=%s, unityMultiplicator=%s, unityNbDecimal=%s, roundingModeEnum=%s, edrTemplates=%s, subscriptionIds=%s]",
            id, code, lastUpdate, priority, filterExpression, filter1, filter2, filter3, filter4, unitMultiplicator, unitNbDecimal, roundingMode, edrTemplates, subscriptionIds);
    }

    public void populateFromUsageChargeTemplate(UsageChargeTemplate usageChargeTemplate) {

        id = usageChargeTemplate.getId();
        code = usageChargeTemplate.getCode();
        description = usageChargeTemplate.getDescription();
        descriptionI18n = usageChargeTemplate.getDescriptionI18n();
        filterExpression = StringUtils.stripToNull(usageChargeTemplate.getFilterExpression());
        filter1 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam1());
        filter2 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam2());
        filter3 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam3());
        filter4 = StringUtils.stripToNull(usageChargeTemplate.getFilterParam4());
        unitNbDecimal = usageChargeTemplate.getUnitNbDecimal();
        roundingMode = usageChargeTemplate.getRoundingMode();
        unitMultiplicator = usageChargeTemplate.getUnitMultiplicator();
        ratingUnitDescription = usageChargeTemplate.getRatingUnitDescription();
        inputUnitDescription = usageChargeTemplate.getInputUnitDescription();
        invoiceSubCategoryCode = usageChargeTemplate.getInvoiceSubCategory().getCode();

        if (unitMultiplicator == null) {
            unitMultiplicator = BigDecimal.ONE;
        }

        edrTemplates = new HashSet<CachedTriggeredEDR>();
        if (usageChargeTemplate.getEdrTemplates() != null && usageChargeTemplate.getEdrTemplates().size() > 0) {
            for (TriggeredEDRTemplate edrTemplate : usageChargeTemplate.getEdrTemplates()) {
                CachedTriggeredEDR trigerredEDRCache = new CachedTriggeredEDR(edrTemplate);
                edrTemplates.add(trigerredEDRCache);
            }
        }
        if (getPriority() != usageChargeTemplate.getPriority()) {
            priority = usageChargeTemplate.getPriority();
        }

        computeRoundingValues();
    }

    public BigDecimal getInChargeUnit(BigDecimal edrUnitValue) throws BusinessException {

        if (edrUnitValue == null) {
            throw new BusinessException("Cant get countedValue with null quantity");
        }
        BigDecimal result = NumberUtil.getInChargeUnit(edrUnitValue, unitMultiplicator, unitNbDecimal, roundingMode);
        return result;
    }

    private void computeRoundingValues() {
        try {
            if (unitNbDecimal >= BaseEntity.NB_DECIMALS) {
                roundingUnityNbDecimal = BaseEntity.NB_DECIMALS;
            } else {
                roundingUnityNbDecimal = unitNbDecimal;
                roundingEdrNbDecimal = (int) Math.round(roundingUnityNbDecimal + Math.floor(Math.log10(unitMultiplicator.doubleValue())));
                if (roundingEdrNbDecimal > BaseEntity.NB_DECIMALS) {
                    roundingEdrNbDecimal = BaseEntity.NB_DECIMALS;
                }
            }
        } catch (Exception e) {
        }
    }

    public BigDecimal getInEDRUnit(BigDecimal chargeUnitValue) {
        return chargeUnitValue.divide(unitMultiplicator, roundingEdrNbDecimal, RoundingMode.HALF_UP);
    }
}