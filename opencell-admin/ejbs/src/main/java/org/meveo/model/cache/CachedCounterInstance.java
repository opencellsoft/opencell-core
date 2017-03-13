package org.meveo.model.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.catalog.CounterTypeEnum;

/**
 * 
 * A View of CounterInstance stored in Data Grid with its entity counterpart stored in Database
 * 
 */
public class CachedCounterInstance implements Serializable {

    private static final long serialVersionUID = 4237398308951202639L;

    private Long id;
    private String code;
    private Long counterInstanceId;
    private List<CachedCounterPeriod> counterPeriods;

    private CounterTypeEnum counterType;

    public CachedCounterInstance() {

    }

    public List<CachedCounterPeriod> getCounterPeriods() {
        return counterPeriods;
    }

    public void setCounterPeriods(List<CachedCounterPeriod> counterPeriods) {
        this.counterPeriods = counterPeriods;
    }

    /**
     * Create a cache view of a counter entity it will store all the periods in a ascending order by startDate for a fast selection during rating
     * 
     * @param counter
     * @return
     */
    public CachedCounterInstance(CounterInstance counter) {

        id = counter.getId();
        code = counter.getCode();
        counterInstanceId = counter.getId();
        counterType = counter.getCounterTemplate().getCounterType();
        if (counter.getCounterPeriods() != null && counter.getCounterPeriods().size() > 0) {
            counterPeriods = new ArrayList<CachedCounterPeriod>(counter.getCounterPeriods().size());
            for (CounterPeriod counterPeriod : counter.getCounterPeriods()) {
                CachedCounterPeriod periodCache = new CachedCounterPeriod(counterPeriod, this);
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
            }
        }
    }

    public Long getCounterInstanceId() {
        return counterInstanceId;
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
        return String.format("CachedCounterInstance [id=%s, code=%s, counterInstanceId=%s, counterPeriods=%s]", id, code, counterInstanceId,
            counterPeriods != null ? counterPeriods.subList(0, Math.min(counterPeriods.size(), maxLen)) : null);
    }

    /**
     * Get a matching period for a given date
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
            if ((period.getStartDate().before(date) || period.getStartDate().equals(date)) && period.getEndDate().after(date)) {
                return period;
            }
        }
        return null;
    }
}