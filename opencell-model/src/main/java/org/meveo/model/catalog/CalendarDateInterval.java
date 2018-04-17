package org.meveo.model.catalog;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Represents a single interval of time for Interval based calendar. Time can be specified as weekday, hour/minute, month/day
 * 
 * @author Andrius Karpavicius
 * 
 */
@Entity
@Cacheable
@ExportIdentifier({  "calendar.code", "intervalBegin", "intervalEnd" })
@Table(name = "cat_calendar_interval")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "cat_calendar_interval_seq"), })
public class CalendarDateInterval extends BaseEntity implements Comparable<CalendarDateInterval> {

    private static final long serialVersionUID = -8419267880869260329L;

    /**
     * Specified interval start. Depending on calendar interval type (calendar.intervalType) specifies:
     * 
     * a weekday (1=monday ... 7=sunday)
     * a month (january = 1, december = 12) and day as 3 or 4 digits in a format month without leading zero day with leading zero
     * a hour and minute as 3 or 4 digits in a format hour without leading zero minute with leading zero
     */
    @Column(name = "interval_begin", nullable = false)
    @NotNull
    private int intervalBegin;

    /**
     * Specified interval end. Depending on calendar interval type (calendar.intervalType) specifies:
     * 
     * a weekday (1=monday ... 7=sunday)
     * a month (january = 1, december = 12) and day as 3 or 4 digits in a format  &lt;month without leading zero&gt; &lt;day with leading zero&gt;
     * a hour and minute as 3 or 4 digits in a format &lt;hour without leading zero&gt;&lt;minute with leading zero if hour specified&gt;
     */
    @Column(name = "interval_end", nullable = false)
    @NotNull
    private int intervalEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calendar_id")
    @NotNull
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
     * For weekday type interval when interval spans to another week (e.g. thursday to monday), the interval end value is adjusted by 7 days.
     * For day type interval when interval spans to another year (e.g. 12/15 to 01/25), the interval end value is adjusted by 12 month.
     * For hour type interval when interval spans to another day (e.g. 23:15 to 00:45), the interval end value is adjusted by 24 hours.
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
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CalendarDateInterval)) {
            return false;
        }
        
        CalendarDateInterval other = (CalendarDateInterval) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        return intervalBegin == other.getIntervalBegin() && intervalEnd == other.getIntervalEnd();
    }
}