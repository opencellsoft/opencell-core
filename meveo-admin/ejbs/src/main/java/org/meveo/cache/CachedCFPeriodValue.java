package org.meveo.cache;

import java.io.Serializable;
import java.util.Date;

import org.meveo.model.shared.DateUtils;

public class CachedCFPeriodValue implements Serializable {

    private static final long serialVersionUID = -6850614096852110306L;

    private Date periodStartDate;

    private Date periodEndDate;

    private Object value;

    private boolean versioned;

    private int priority;

    // private int maxKeyLength;

    public CachedCFPeriodValue(Object value) {
        super();
        this.value = value;

        // // If it is a map, calculate a maxKey length, to avoid checking irrelevant characters later in getClosestMatchValue() method
        // if (value != null && value instanceof Map) {
        // for (String key : ((Map<String, Object>) value).keySet()) {
        // if (maxKeyLength < key.length()) {
        // maxKeyLength = key.length();
        // }
        // }
        // }
    }

    public CachedCFPeriodValue(Object value, int priority, Date periodStartDate, Date periodEndDate) {
        this(value);
        this.priority = priority;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        versioned = true;

    }

    public boolean isCorrespondsToPeriod(Date date) {
        return (periodStartDate == null || date.compareTo(periodStartDate) >= 0) && (periodEndDate == null || date.before(periodEndDate));
    }

    protected Date getPeriodStartDate() {
        return periodStartDate;
    }

    protected Date getPeriodEndDate() {
        return periodEndDate;
    }

    public Object getValue() {
        return value;
    }

    public boolean isVersioned() {
        return versioned;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {

        if (versioned) {
            return String.format("CachedCFPeriodValue [priority=%s, periodStartDate=%s, periodEndDate=%s, value=%s, versioned=%s]", priority,
                DateUtils.formatDateWithPattern(periodStartDate, "yyyy-MM-dd"), DateUtils.formatDateWithPattern(periodEndDate, "yyyy-MM-dd"), value, versioned);
        } else {
            return String.format("CachedCFPeriodValue [value=%s]", value);
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;

        } else if (!(obj instanceof CachedCFPeriodValue)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CachedCFPeriodValue other = (CachedCFPeriodValue) obj;

        boolean match = (other.getPeriodStartDate() == null && periodStartDate == null)
                || (other.getPeriodStartDate() != null && periodStartDate != null && other.getPeriodStartDate().equals(periodStartDate));
        match = match
                && ((other.getPeriodEndDate() == null && periodEndDate == null) || (other.getPeriodEndDate() != null && periodEndDate != null && other.getPeriodEndDate().equals(
                    periodEndDate)));
        return match;
    }
}