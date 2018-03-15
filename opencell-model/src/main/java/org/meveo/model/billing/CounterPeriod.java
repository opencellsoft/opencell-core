package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.CounterTypeEnum;

@Entity
@ObservableEntity
@Table(name = "billing_counter_period", uniqueConstraints = @UniqueConstraint(columnNames = { "counter_instance_id", "period_start_date" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_counter_period_seq"), })
@NamedQueries({
        @NamedQuery(name = "CounterPeriod.findByPeriodDate", query = "SELECT cp FROM CounterPeriod cp WHERE cp.counterInstance=:counterInstance AND cp.periodStartDate<=:date AND cp.periodEndDate>:date"), })
public class CounterPeriod extends BusinessEntity {
    private static final long serialVersionUID = -4924601467998738157L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_instance_id")
    private CounterInstance counterInstance;

    @Enumerated(EnumType.STRING)
    @Column(name = "counter_type")
    private CounterTypeEnum counterType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "period_start_date")
    private Date periodStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "period_end_date")
    private Date periodEndDate;

    @Column(name = "level_num", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal level;

    @Column(name = "value", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal value;

    @Column(name = "notification_levels", length = 100)
    @Size(max = 100)
    private String notificationLevels;

    @Transient
    private Map<String, BigDecimal> notificationLevelsAsMap;

    public CounterInstance getCounterInstance() {
        return counterInstance;
    }

    public void setCounterInstance(CounterInstance counterInstance) {
        this.counterInstance = counterInstance;
    }

    public CounterTypeEnum getCounterType() {
        return counterType;
    }

    public void setCounterType(CounterTypeEnum counterType) {
        this.counterType = counterType;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public BigDecimal getLevel() {
        return level;
    }

    public void setLevel(BigDecimal level) {
        this.level = level;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getNotificationLevels() {
        return notificationLevels;
    }

    public void setNotificationLevels(String notificationLevels) {
        this.notificationLevels = notificationLevels;
    }

    /**
     * Get notification levels converted to a map of big decimal values with key being an original threshold value (that could have been entered as % or a number)
     * 
     * @return A map of big decimal values with original threshold values as a key
     */
    @SuppressWarnings("unchecked")
    public Map<String, BigDecimal> getNotificationLevelsAsMap() {

        if (StringUtils.isBlank(notificationLevels)) {
            return null;

        } else if (notificationLevelsAsMap != null) {
            return notificationLevelsAsMap;
        }

        Map<String, BigDecimal> bdLevelMap = new LinkedHashMap<>();

        Map<String, ?> bdLevelMapObj = JsonUtils.toObject(notificationLevels, LinkedHashMap.class);

        for (Entry<String, ?> mapItem : bdLevelMapObj.entrySet()) {

            if (mapItem.getValue() instanceof String) {
                bdLevelMap.put(mapItem.getKey(), new BigDecimal((String) mapItem.getValue()));
            } else if (mapItem.getValue() instanceof Double) {
                bdLevelMap.put(mapItem.getKey(), new BigDecimal((Double) mapItem.getValue()));
            } else if (mapItem.getValue() instanceof Integer) {
                bdLevelMap.put(mapItem.getKey(), new BigDecimal((Integer) mapItem.getValue()));
            } else if (mapItem.getValue() instanceof Long) {
                bdLevelMap.put(mapItem.getKey(), new BigDecimal((Long) mapItem.getValue()));
            }
        }

        if (bdLevelMap.isEmpty()) {
            return null;
        }

        notificationLevelsAsMap = bdLevelMap;
        return bdLevelMap;
    }

    /**
     * Set notification levels with percentage values converted to real values based on a given initial value
     * 
     * @param notificationLevels Notification values
     * @param initialValue Initial counter value
     */
    public void setNotificationLevels(String notificationLevels, BigDecimal initialValue) {

        Map<String, BigDecimal> convertedLevels = new HashMap<>();

        if (StringUtils.isBlank(notificationLevels)) {
            this.notificationLevels = null;
            return;
        }

        String[] levels = notificationLevels.split(",");
        for (String level : levels) {
            level = level.trim();
            if (StringUtils.isBlank(level)) {
                continue;
            }
            BigDecimal bdLevel = null;
            try {
                if (level.endsWith("%") && level.length() > 1) {
                    bdLevel = new BigDecimal(level.substring(0, level.length() - 1));
                    if (bdLevel.compareTo(new BigDecimal(100)) < 0) {
                        convertedLevels.put(level, initialValue.multiply(bdLevel).divide(new BigDecimal(100)).setScale(2));
                    }

                } else if (!level.endsWith("%")) {
                    bdLevel = new BigDecimal(level);
                    if (initialValue.compareTo(bdLevel) > 0) {
                        convertedLevels.put(level, bdLevel);
                    }
                }
            } catch (Exception e) {
            }
        }

        convertedLevels = ListUtils.sortMapByValue(convertedLevels);

        this.notificationLevels = JsonUtils.toJson(convertedLevels, false);
    }

    public boolean isCorrespondsToPeriod(Date dateToCheck) {
        return !dateToCheck.before(periodStartDate) && !dateToCheck.after(periodEndDate);
    }

    /**
     * Get a list of counter values for which notification should fire given the counter value change from (exclusive)/to (inclusive) value (NOTE : as TO value is lower, it is
     * inclusive)
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
        for (Entry<String, BigDecimal> notifValue : getNotificationLevelsAsMap().entrySet()) {
            if (fromValue.compareTo(notifValue.getValue()) > 0 && notifValue.getValue().compareTo(toValue) >= 0) {
                matchedLevels.add(notifValue);
            }
        }
        return matchedLevels;
    }
}