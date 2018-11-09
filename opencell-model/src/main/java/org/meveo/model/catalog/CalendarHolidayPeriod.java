package org.meveo.model.catalog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
 * Represents a holiday period to be used by a banking calendar that excludes holidays. The period is expressed in months / days
 * 
 * @author hznibar
 * 
 */
@Entity
@Cacheable
@ExportIdentifier({ "calendar.code", "holidayBegin", "holidayEnd" })
@Table(name = "cat_calendar_holiday_period")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_calendar_holiday_period_seq"), })
public class CalendarHolidayPeriod extends BaseEntity implements Comparable<CalendarHolidayPeriod> {

    private static final long serialVersionUID = -8419267880869260329L;

    /**
     * Specified holiday start.
     */
    @Column(name = "holiday_begin", nullable = false)
    @NotNull
    private int holidayBegin;

    /**
     * Specified holiday end. 
     */
    @Column(name = "holiday_end", nullable = false)
    @NotNull
    private int holidayEnd;

    /**
     * Holiday based type calendar
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calendar_id")
    @NotNull
    private CalendarBanking calendar;

    public CalendarHolidayPeriod() {
        super();
    }

    public CalendarHolidayPeriod(CalendarBanking calendar, int holidayBegin, int holidayEnd) {
        super();
        this.calendar = calendar;
        this.holidayBegin = holidayBegin;
        this.holidayEnd = holidayEnd;
    }

    public CalendarBanking getCalendar() {
        return calendar;
    }

    public void setCalendar(CalendarBanking calendar) {
        this.calendar = calendar;
    }

    public int getHolidayBegin() {
        return holidayBegin;
    }

    public void setHolidayBegin(Integer holidayBegin) {
        this.holidayBegin = holidayBegin;
    }

    public int getHolidayEnd() {
        return holidayEnd;
    }

    public void setHolidayEnd(int holidayEnd) {
        this.holidayEnd = holidayEnd;
    }

    public boolean isCrossBoundry() {
        return holidayEnd < holidayBegin;
    }
    
	public boolean isHolidayDate(Date d) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + ""
				+ (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
				+ calendar.get(java.util.Calendar.DAY_OF_MONTH));
		int monthDayAdjusted = monthDay;
		if (isCrossBoundry() && monthDay < getHolidayBegin()) {
			monthDayAdjusted = monthDay + 1200;
		}
		return monthDayAdjusted >= getHolidayBegin() && monthDayAdjusted <= getHolidayEndAdjusted();
	}

	/**
	 * To handle special case when holiday spans to another year (e.g. 12/15 to
	 * 01/25), holiday's end date is adjusted accordingly:
	 * 
	 * when holiday spans to another year (e.g. 12/15 to 01/25), the holiday end
	 * value is adjusted by 12 month.
	 * 
	 * @return Adjusted end holiday value
	 */
	public int getHolidayEndAdjusted() {
		if (holidayEnd < holidayBegin) {
			return holidayEnd + 1200;
		}
		return holidayEnd;
	}

	public String getHolidayAsString() {
		String separator = "/";
		return StringUtils.leftPad(Integer.toString(holidayBegin / 100), 2, '0') + separator
				+ StringUtils.leftPad(Integer.toString(holidayBegin % 100), 2, '0') + " - "
				+ StringUtils.leftPad(Integer.toString(holidayEnd / 100), 2, '0') + separator
				+ StringUtils.leftPad(Integer.toString(holidayEnd % 100), 2, '0');
	}

    @Override
    public String toString() {
        return getHolidayAsString();
    }

    @Override
    public int compareTo(CalendarHolidayPeriod other) {
        return holidayBegin - other.getHolidayBegin();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CalendarHolidayPeriod)) {
            return false;
        }

        CalendarHolidayPeriod other = (CalendarHolidayPeriod) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        return holidayBegin == other.getHolidayBegin() && holidayEnd == other.getHolidayEnd();
    }

    /**
     * get the next day after holiday period. 
     * @param date
     * @return the same date if it is not a holiday period,  the next date after holiday period if it's a holiday date.
     */
	public Date getDateAfterHoliday(Date date) {
		// if the date is not a holiday, we return the same date
		if (!isHolidayDate(date)) {
			return date;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + ""
				+ (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
				+ calendar.get(java.util.Calendar.DAY_OF_MONTH));

		int endValue = getHolidayEnd();
		// Advance to another year if holiday end value was adjusted by 12 month
		if (isCrossBoundry() && monthDay > getHolidayEnd()) {
			calendar.add(java.util.Calendar.YEAR, 1);
		}
		calendar.set(java.util.Calendar.MONTH, endValue / 100 - 1);
		calendar.set(java.util.Calendar.DAY_OF_MONTH, (endValue % 100));
		calendar.add(Calendar.DATE, 1); // the next date after holiday period
		return calendar.getTime();
	}

	/**
     * get the last day before holiday period. 
     * @param date
     * @return the same date if it is not a holiday period,  the last date before holiday period if it's a holiday date.
     */
	public Date getDateBeforeHoliday(Date date) {
		// if the date is not a holiday, we return the same date
		if (!isHolidayDate(date)) {
			return date;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int monthDay = Integer.parseInt((calendar.get(java.util.Calendar.MONTH) + 1) + ""
				+ (calendar.get(java.util.Calendar.DAY_OF_MONTH) < 10 ? "0" : "")
				+ calendar.get(java.util.Calendar.DAY_OF_MONTH));

		int beginValue = getHolidayBegin();
		// Advance to another year if holiday end value was adjusted by 12 month
		if (isCrossBoundry() && monthDay < getHolidayBegin()) {
			calendar.add(java.util.Calendar.YEAR, -1);
		}
		calendar.set(java.util.Calendar.MONTH, beginValue / 100 - 1);
		calendar.set(java.util.Calendar.DAY_OF_MONTH, (beginValue % 100));
		calendar.add(Calendar.DATE, -1); // the last date before holiday period
		return calendar.getTime();
	}
}