package org.meveo.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.catalog.CounterTypeEnum;

/**
 * 
 * A View of CounterInstance stored in Data Grid with its entity counterpart stored in Database.
 * 
 */
public class CachedCounterInstance implements Serializable {

    private static final long serialVersionUID = 4237398308951202639L;

    private Long id;
    private String code;
    private List<CachedCounterPeriod> counterPeriods;

    private CounterTypeEnum counterType;

    public CachedCounterInstance() {

    }

    protected List<CachedCounterPeriod> getCounterPeriods() {
        return counterPeriods;
    }

    protected void setCounterPeriods(List<CachedCounterPeriod> counterPeriods) {
        this.counterPeriods = counterPeriods;
    }

    /**
     * Create a cache view of a counter entity it will store all the periods in a ascending order by startDate for a fast selection during rating.
     * 
     * @param counterInstance counter instance.
     */
    public CachedCounterInstance(CounterInstance counterInstance) {

        id = counterInstance.getId();
        code = counterInstance.getCode();
        counterType = counterInstance.getCounterTemplate().getCounterType();
        if (counterInstance.getCounterPeriods() != null && counterInstance.getCounterPeriods().size() > 0) {
            counterPeriods = new ArrayList<CachedCounterPeriod>(counterInstance.getCounterPeriods().size());
            for (CounterPeriod counterPeriod : counterInstance.getCounterPeriods()) {
                addCounterPeriod(counterPeriod);
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public CounterTypeEnum getCounterType() {
        return counterType;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("CachedCounterInstance [id=%s, code=%s, counterPeriods=%s]", id, code,
            counterPeriods != null ? counterPeriods.subList(0, Math.min(counterPeriods.size(), maxLen)) : null);
    }

    /**
     * Get a matching period for a given date.
     * 
     * @param date Date to match
     * @return Counter period matched
     */
    public CachedCounterPeriod getCounterPeriod(Date date) {

        if (counterPeriods == null) {
            counterPeriods = new ArrayList<CachedCounterPeriod>();
            return null;
        }

        for (CachedCounterPeriod period : counterPeriods) {
            if (period.isCorrespondsToPeriod(date)) {
                return period;
            }
        }
        return null;
    }

    /**
     * Get a matching period for a given date.
     * 
     * @param id id of counter period/
     * @return Counter period matched
     */
    public CachedCounterPeriod getCounterPeriod(Long id) {
        for (CachedCounterPeriod period : counterPeriods) {
            if (period.getId().equals(id)) {
                return period;
            }
        }

        return null;
    }

    protected CachedCounterPeriod addCounterPeriod(CounterPeriod counterPeriod) {
        CachedCounterPeriod periodCache = new CachedCounterPeriod(counterPeriod);
        boolean added = false;
        for (int i = 0; i < counterPeriods.size(); i++) {
            if (counterPeriods.get(i).getStartDate().after(periodCache.getStartDate())) {
                counterPeriods.add(i, periodCache);
                added = true;
                break;
            }
        }
        if (!added) {
            counterPeriods.add(periodCache);
        }

        return periodCache;
    }
}