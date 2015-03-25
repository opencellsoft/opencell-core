package org.meveo.model.cache;

import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;

public class CachedCounterPeriod {

    private Long counterPeriodId;
    private CounterTypeEnum counterType;
    private Date startDate;
    private Date endDate;
    private BigDecimal value;
    private BigDecimal level;
    private boolean dbDirty;

    public Long getCounterPeriodId() {
        return counterPeriodId;
    }

    public CounterTypeEnum getCounterType() {
        return counterType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getLevel() {
        return level;
    }

    public boolean isDbDirty() {
        return dbDirty;
    }

    public CachedCounterPeriod() {

    }

    public CachedCounterPeriod(CounterPeriod counterPeriod, CounterTemplate template) {
        counterPeriodId = counterPeriod.getId();
        counterType = template.getCounterType();
        endDate = counterPeriod.getPeriodEndDate();
        level = template.getLevel();
        startDate = counterPeriod.getPeriodStartDate();
        value = counterPeriod.getValue();
    }

    @Override
    public String toString() {
        return String.format("CachedCounterPeriod [counterPeriodId=%s, counterType=%s, startDate=%s, endDate=%s, value=%s, level=%s, dbDirty=%s]", counterPeriodId, counterType,
            startDate, endDate, value, level, dbDirty);
    }
}