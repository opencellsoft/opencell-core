package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.meveo.model.BaseProviderlessEntity;
import org.meveo.model.catalog.CalendarInterval.CalendarIntervalTypeEnum;

/**
 * Represents a single interval of time for Interval based calendar. Time can be specified as weekday, hour/minute, month/day
 * 
 * @author Andrius Karpavicius
 * 
 */
@Entity
@Table(name = "CAT_CALENDAR_INTERVAL")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_CALENDAR_INTERVAL_SEQ")
public class CalendarDateInterval extends BaseProviderlessEntity implements Comparable<CalendarDateInterval> {

    private static final long serialVersionUID = -8419267880869260329L;

    /**
     * Specified interval start. Depending on calendar interval type (calendar.intervalType) specifies:
     * 
     * a weekday (1=monday ... 7=sunday)<br/>
     * a month (january = 1, december = 12) and day as 3 or 4 digits in a format <month without leading zero><day with leading zero>, <br/>
     * a hour and minute as 3 or 4 digits in a format <hour without leading zero><minute with leading zero>
     */
    @Column(name = "INTERVAL_BEGIN", nullable = false)
    @NotNull
    private int intervalBegin;

    /**
     * Specified interval end. Depending on calendar interval type (calendar.intervalType) specifies:
     * 
     * a weekday (1=monday ... 7=sunday)<br/>
     * a month (january = 1, december = 12) and day as 3 or 4 digits in a format <month without leading zero><day with leading zero>, <br/>
     * a hour and minute as 3 or 4 digits in a format <hour without leading zero><minute with leading zero if hour specified>
     */
    @Column(name = "INTERVAL_END", nullable = false)
    @NotNull
    private int intervalEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CALENDAR_ID")
    private CalendarInterval calendar;

    public CalendarDateInterval() {
        super();
    }

    public CalendarDateInterval(CalendarInterval calendar, int intervalBegin, int intervalEnd) {
        super();
        this.calendar = calendar;
        this.intervalBegin = intervalBegin;
        this.intervalEnd = intervalEnd;
    }

    public CalendarInterval getCalendar() {
        return calendar;
    }

    public void setCalendar(CalendarInterval calendar) {
        this.calendar = calendar;
    }

    public int getIntervalBegin() {
        return intervalBegin;
    }

    public void setIntervalBegin(Integer intervalBegin) {
        this.intervalBegin = intervalBegin;
    }

    public int getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(int intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public boolean isCrossBoundry() {
        return intervalEnd <= intervalBegin;
    }

    /**
     * To handle special case when interval spans to another week (e.g. thursday to monday), another day (e.g. 23:15 to 00:45), or another year (e.g. 12/15 to 01/25), interval's
     * end date is adjusted accordingly:
     * 
     * For weekday type interval when interval spans to another week (e.g. thursday to monday), the interval end value is adjusted by 7 days.<br/>
     * For day type interval when interval spans to another year (e.g. 12/15 to 01/25), the interval end value is adjusted by 12 month. <br/>
     * For hour type interval when interval spans to another day (e.g. 23:15 to 00:45), the interval end value is adjusted by 24 hours.<br/>
     * 
     * @return Adjusted end interval value
     */
    public int getIntervalEndAdjusted() {
        if (intervalEnd <= intervalBegin) {
            if (calendar.getIntervalType() == CalendarIntervalTypeEnum.WDAY) {
                return intervalEnd + 7;

            } else if (calendar.getIntervalType() == CalendarIntervalTypeEnum.DAY) {
                return intervalEnd + 1200;

            } else if (calendar.getIntervalType() == CalendarIntervalTypeEnum.HOUR) {
                return intervalEnd + 2400;
            }
        }
        return intervalEnd;
    }

    public String getIntervalAsString() {
        String intervalBeginAsString = Integer.toString(intervalBegin);
        String intervalEndAsString = Integer.toString(intervalEnd);

        if (calendar.getIntervalType() == CalendarIntervalTypeEnum.DAY || calendar.getIntervalType() == CalendarIntervalTypeEnum.HOUR) {
            String separator = calendar.getIntervalType() == CalendarIntervalTypeEnum.HOUR ? ":" : "/";

            return StringUtils.leftPad(Integer.toString(intervalBegin / 100), 2, '0') + separator + StringUtils.leftPad(Integer.toString(intervalBegin % 100), 2, '0') + " - "
                    + StringUtils.leftPad(Integer.toString(intervalEnd / 100), 2, '0') + separator + StringUtils.leftPad(Integer.toString(intervalEnd % 100), 2, '0');

            // Weekdays
        } else {
            return intervalBeginAsString + " - " + intervalEndAsString;
        }

    }

    @Override
    public String toString() {
        return getIntervalAsString();
    }

    @Override
    public int compareTo(CalendarDateInterval other) {
        return intervalBegin - other.getIntervalBegin();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CalendarDateInterval other = (CalendarDateInterval) obj;
        return intervalBegin == other.getIntervalBegin() && intervalEnd == other.getIntervalEnd();
    }
}