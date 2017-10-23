package org.meveo.cache;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.meveo.model.billing.CounterPeriod;

public class CachedCounterPeriod implements Serializable {

    private static final long serialVersionUID = 5267933089763181401L;

    private Long id;
    private Date startDate;
    private Date endDate;
    private BigDecimal value;
    private BigDecimal level;
    private Map<String, BigDecimal> notificationLevels;

    public Long getId() {
        return id;
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

    protected void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getLevel() {
        return level;
    }

    public void setLevel(BigDecimal level) {
        this.level = level;
    }

    public boolean isCorrespondsToPeriod(Date dateToCheck) {

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK period match {} {} to {} {} {}", startDate, endDate, dateToCheck, !dateToCheck.before(startDate), !dateToCheck.after(endDate));
        return !dateToCheck.before(startDate) && !dateToCheck.after(endDate);
    }

    public CachedCounterPeriod() {

    }

    public CachedCounterPeriod(CounterPeriod counterPeriod) {
        this.id = counterPeriod.getId();
        this.endDate = counterPeriod.getPeriodEndDate();
        this.level = counterPeriod.getLevel();
        this.startDate = counterPeriod.getPeriodStartDate();
        this.value = counterPeriod.getValue();
        this.notificationLevels = counterPeriod.getNotificationLevelsAsMap();
    }

    /**
     * Get a list of counter values for which notification should fire given the counter value change from (exclusive)/to (inclusive) value
     * 
     * @param fromValue Counter changed from value
     * @param toValue Counter changed to value
     * @return A list of counter values that match notification levels
     */
    public List<Entry<String, BigDecimal>> getMatchedNotificationLevels(BigDecimal fromValue, BigDecimal toValue) {
        if (notificationLevels == null) {
            return null;
        }

        List<Entry<String, BigDecimal>> matchedLevels = new ArrayList<>();
        for (Entry<String, BigDecimal> notifValue : notificationLevels.entrySet()) {
            if (fromValue.compareTo(notifValue.getValue()) > 0 && notifValue.getValue().compareTo(toValue) >= 0) {
                matchedLevels.add(notifValue);
            }
        }
        return matchedLevels;
    }

    @Override
    public String toString() {
        return String.format("CachedCounterPeriod [counterPeriodId=%s, startDate=%s, endDate=%s, value=%s, level=%s, notificationLevels=%s]", id, startDate, endDate, value, level,
            notificationLevels);
    }
}