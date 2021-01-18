/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.billing;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.AccumulatorCounterTypeEnum;
import org.meveo.model.catalog.CounterTypeEnum;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Counter values for a given period
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@Table(name = "billing_counter_period", uniqueConstraints = @UniqueConstraint(columnNames = { "counter_instance_id", "period_start_date" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_counter_period_seq"), })
@NamedQueries({
        @NamedQuery(name = "CounterPeriod.findByPeriodDate", query = "SELECT cp FROM CounterPeriod cp WHERE cp.counterInstance=:counterInstance AND cp.periodStartDate<=:date AND cp.periodEndDate>:date"),
        @NamedQuery(name = "CounterPeriod.countPeriodsToPurgeByDate", query = "select count(*) FROM CounterPeriod cp WHERE cp.periodEndDate<=:date"),
        @NamedQuery(name = "CounterPeriod.purgePeriodsByDate", query = "delete CounterPeriod cp WHERE cp.periodEndDate<=:date"),
        @NamedQuery(name = "CounterPeriod.findByCounterEntityAndPeriodDate", query = "SELECT cp FROM CounterPeriod cp "
                + "WHERE (cp.counterInstance.serviceInstance=:serviceInstance OR cp.counterInstance.subscription=:subscription OR cp.counterInstance.billingAccount=:billingAccount OR cp.counterInstance.userAccount=:userAccount OR cp.counterInstance.customerAccount=:customerAccount OR cp.counterInstance.customer=:customer) "
                + "AND cp.periodStartDate<=:date AND cp.periodEndDate>:date AND cp.counterInstance.code=:counterCode")

})
public class CounterPeriod extends BusinessEntity {
    private static final long serialVersionUID = -4924601467998738157L;

    /**
     * Counter instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_instance_id")
    private CounterInstance counterInstance;

    /**
     * Counter type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "counter_type")
    private CounterTypeEnum counterType;

    /**
     * Period start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "period_start_date")
    private Date periodStartDate;

    /**
     * Period end date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "period_end_date")
    private Date periodEndDate;

    /**
     * Initial counter value
     */
    @Column(name = "level_num", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal level;

    /**
     * Current counter value
     */
    @Column(name = "value", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal value;

    /**
     * Notification levels, upon reaching which, a notification will be fired
     */
    @Column(name = "notification_levels", length = 100)
    @Size(max = 100)
    private String notificationLevels;

    /**
     * Check if is it an accumulator account.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_accumulator")
    private Boolean accumulator = Boolean.FALSE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="billing_counter_period_values")
    @MapKeyColumn(name = "counter_key")
    @Column(name = "counter_value")
    private Map<String, BigDecimal> accumulatedValues;

    /**
     * The type field can be "Multi-value" if the accumulator is true
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "accumulator_type")
    private AccumulatorCounterTypeEnum accumulatorType;

    /**
     * Notification levels mapped by a value. Used for entry in GUI.
     */
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

    public Boolean getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(Boolean accumulator) {
        this.accumulator = accumulator;
    }

    public Map<String, BigDecimal> getAccumulatedValues() {
        return accumulatedValues;
    }

    public void setAccumulatedValues(Map<String, BigDecimal> accumulatedValues) {
        this.accumulatedValues = accumulatedValues;
    }

    /**
     * Gets the accumulator type multiple or single
     * @return an accumulator counter type enum.
     */
    public AccumulatorCounterTypeEnum getAccumulatorType() {
        return accumulatorType;
    }

    /**
     * Sets the accumulator counter type.
     * @param accumulatorType AccumulatorCounterTypeEnum
     */
    public void setAccumulatorType(AccumulatorCounterTypeEnum accumulatorType) {
        this.accumulatorType = accumulatorType;
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
        if(getNotificationLevelsAsMap() != null) {
            for (Entry<String, BigDecimal> notifValue : getNotificationLevelsAsMap().entrySet()) {
                if (fromValue.compareTo(notifValue.getValue()) > 0 && notifValue.getValue().compareTo(toValue) >= 0) {
                    matchedLevels.add(notifValue);
                }
            }
        }
        return matchedLevels;
    }
}