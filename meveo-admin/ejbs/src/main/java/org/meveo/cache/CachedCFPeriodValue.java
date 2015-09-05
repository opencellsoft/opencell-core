package org.meveo.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.shared.DateUtils;

public class CachedCFPeriodValue implements Serializable {

    private static final long serialVersionUID = -6850614096852110306L;

    private Date periodStartDate;

    private Date periodEndDate;

    private Object value;

    private boolean versioned;

    private int priority;

    private int maxKeyLength;

    @SuppressWarnings("unchecked")
    public CachedCFPeriodValue(Object value) {
        super();
        this.value = value;

        // If it is a map, calculate a maxKey length, to avoid checking irrelevant characters later in getClosestMatchValue() method
        if (value != null && value instanceof Map) {
            for (String key : ((Map<String, Object>) value).keySet()) {
                if (maxKeyLength < key.length()) {
                    maxKeyLength = key.length();
                }
            }
        }
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

    // public Date getPeriodStartDate() {
    // return periodStartDate;
    // }
    //
    // public Date getPeriodEndDate() {
    // return periodEndDate;
    // }

    public Object getValue() {
        return value;
    }

    public boolean isVersioned() {
        return versioned;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Match as close as possible map's key to the key provided and return a map value. Match is performed by matching a full string and then reducing one by one symbol untill a
     * match is found.
     * 
     * TODO can be an issue with lower/upper case mismatch
     * 
     * @param keyToMatch Key to match
     * @return Map value that closely matches map key
     */
    @SuppressWarnings("unchecked")
    public Object getClosestMatchValue(String keyToMatch) {
        if (value == null || !(value instanceof Map) || StringUtils.isEmpty(keyToMatch)) {
            return null;
        }

        Object valueFound = null;
        Map<String, Object> mapValue = (Map<String, Object>) value;
        for (int i = Math.min(keyToMatch.length(), maxKeyLength); i > 0; i--) {
            valueFound = mapValue.get(keyToMatch.substring(0, i));
            if (valueFound != null) {
                return valueFound;
            }
        }

        return null;

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
}