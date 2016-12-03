package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.CounterTypeEnum;

@Entity
@ObservableEntity
@Table(name = "BILLING_COUNTER_PERIOD", uniqueConstraints = @UniqueConstraint(columnNames = { "COUNTER_INSTANCE_ID",
		"PERIOD_START_DATE" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_COUNTER_PERIOD_SEQ")
@NamedQueries({ @NamedQuery(name = "CounterPeriod.findByPeriodDate", query = "SELECT cp FROM CounterPeriod cp WHERE cp.counterInstance=:counterInstance AND cp.periodStartDate<=:date AND cp.periodEndDate>:date"), })
public class CounterPeriod extends BusinessEntity {
    private static final long serialVersionUID = -4924601467998738157L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTER_INSTANCE_ID")
    private CounterInstance counterInstance;

    @Enumerated(EnumType.STRING)
    @Column(name = "COUNTER_TYPE")
    private CounterTypeEnum counterType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_START_DATE")
    private Date periodStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PERIOD_END_DATE")
    private Date periodEndDate;

    @Column(name = "LEVEL_NUM", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal level;

    @Column(name = "VALUE", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal value;

    @Column(name = "NOTIFICATION_LEVELS", length = 70)
    @Size(max = 70)
    private String notificationLevels;

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
     * Get notification levels converted to a list of big decimal values
     * 
     * @return A list of big decimal values
     */
    public List<BigDecimal> getNotificationLevelsAsList() {

        List<BigDecimal> bdLevels = new ArrayList<>();

        if (StringUtils.isBlank(notificationLevels)) {
            return null;
        }

        String[] levels = notificationLevels.split(",");
        for (String level : levels) {
            level = level.trim();
            if (StringUtils.isBlank(level)) {
                continue;
            }
            try {
                bdLevels.add(new BigDecimal(level));

            } catch (Exception e) {
            }
        }

        if (bdLevels.isEmpty()) {
            return null;
        }

        return bdLevels;
    }

    /**
     * Set notification levels with percentage values converted to real values based on a given initial value
     * 
     * @param notificationLevels Notification values
     * @param initialValue Initial counter value
     */
    public void setNotificationLevels(String notificationLevels, BigDecimal initialValue) {

        List<BigDecimal> convertedLevels = new ArrayList<>();

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
                        convertedLevels.add(initialValue.multiply(bdLevel).divide(new BigDecimal(100)).setScale(2));
                    }

                } else if (!level.endsWith("%")) {
                    bdLevel = new BigDecimal(level);
                    if (initialValue.compareTo(bdLevel) > 0) {
                        convertedLevels.add(bdLevel);
                    }
                }
            } catch (Exception e) {
            }
        }

        Collections.sort(convertedLevels);

        this.notificationLevels = org.meveo.commons.utils.StringUtils.concatenate(",", convertedLevels);
    }
}