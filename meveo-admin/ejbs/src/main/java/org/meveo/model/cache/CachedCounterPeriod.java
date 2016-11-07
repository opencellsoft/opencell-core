package org.meveo.model.cache;

import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.billing.CounterPeriod;

public class CachedCounterPeriod {

    private Long counterPeriodId;
    private Date startDate;
    private Date endDate;
    private BigDecimal value;
    private BigDecimal level;
    private boolean dbDirty;
    private CachedCounterInstance counterInstance;

    public Long getCounterPeriodId() {
        return counterPeriodId;
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

    public CachedCounterInstance getCounterInstance() {
        return counterInstance;
    }

    public CachedCounterPeriod() {

    }

    public CachedCounterPeriod(CounterPeriod counterPeriod, CachedCounterInstance counterInstance) {
        this.counterPeriodId = counterPeriod.getId();
        this.endDate = counterPeriod.getPeriodEndDate();
        this.level = counterPeriod.getLevel();
        this.startDate = counterPeriod.getPeriodStartDate();
        this.value = counterPeriod.getValue();
        this.counterInstance = counterInstance;
    }

    @Override
    public String toString() {
        return String.format("CachedCounterPeriod [counterPeriodId=%s, startDate=%s, endDate=%s, value=%s, level=%s, dbDirty=%s]", counterPeriodId, startDate, endDate, value,
            level, dbDirty);
    }
}