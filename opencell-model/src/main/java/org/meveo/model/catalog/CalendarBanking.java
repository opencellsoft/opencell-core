package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents a calendar from which weekend and holidays can be excluded.
 *
 * @author hznibar
 * 
 */
@Entity
@DiscriminatorValue("BANKING")
public class CalendarBanking extends Calendar {

    private static final long serialVersionUID = 1L;
    
    private static Logger log = LoggerFactory.getLogger(CalendarBanking.class);
    
    /** The start date. */
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date startDate;

    /** The end date. */
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date endDate;
    
    /**
     * Specified weekend start.
     */
    @Column(name = "weekend_begin", nullable = false)
    @NotNull
    private Integer weekendBegin;

    /**
     * Specified weekend end. 
     */
    @Column(name = "weekend_end", nullable = false)
    @NotNull
    private Integer weekendEnd;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("holidayBegin")
    private List<CalendarHoliday> holidays;

    /**
     * Gets the holidays.
     *
     * @return the holidays
     */
    public List<CalendarHoliday> getHolidays() {
        if(holidays == null) {
            holidays =  new ArrayList<CalendarHoliday>();
        }
        return holidays;
    }

    /**
     * Sets the holidays.
     *
     * @param holidays the new holidays
     */
    public void setHolidays(List<CalendarHoliday> holidays) {
        this.holidays = holidays;
    }
    
    /**
     * Gets the weekend begin.
     *
     * @return the weekend begin
     */
    public Integer getWeekendBegin() {
		return weekendBegin;
	}

	/**
	 * Sets the weekend begin.
	 *
	 * @param weekendBegin the new weekend begin
	 */
	public void setWeekendBegin(Integer weekendBegin) {
		this.weekendBegin = weekendBegin;
	}

	public Integer getWeekendEnd() {
		return weekendEnd;
	}

	/**
	 * Sets the weekend end.
	 *
	 * @param weekendEnd the new weekend end
	 */
	public void setWeekendEnd(Integer weekendEnd) {
		this.weekendEnd = weekendEnd;
	}
	
	

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Determines a next calendar date, if it's a holiday, the next date will be the holiday end period + 1 day.
     * 
     * 
     * @param date Date to check
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {
    	if(date == null) {
    		return null;
    	}   
    	if(!DateUtils.isWithinDate(date, startDate, endDate)) {
            log.warn("The given date: " +date +" is not in period [startDate,endDate] of banking Calendar: "+ code);
            return date;
        }
    	Date nextCalendarDate = date;
        int iteration = 0;
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(nextCalendarDate);
        int lastYear;
        int newYear;
        do {
        	lastYear = calendar.get(java.util.Calendar.YEAR);
        	iteration++;
        	if(isWeekend(nextCalendarDate)) {            		
        		nextCalendarDate = getDateAfterWeekend(nextCalendarDate);
        	}
        	for (CalendarHoliday holidayPeriod : getHolidays()) {
            	if(holidayPeriod.isHolidayDate(nextCalendarDate)) {
            		nextCalendarDate = holidayPeriod.getDateAfterHoliday(nextCalendarDate);
            		if(isWeekend(nextCalendarDate)) {            		
                		nextCalendarDate = getDateAfterWeekend(nextCalendarDate);
                	}
            	}            	
            }
        	calendar.setTime(nextCalendarDate);
        	newYear = calendar.get(java.util.Calendar.YEAR);
        } while((lastYear != newYear) && iteration < 2); // we do two iterations in case of change of year, because the nextCalendarDate can be a holiday or weekend of next year.
        
        if((lastYear != newYear) && iteration == 2) { // all possibles next dates are a holiday
        	throw new IllegalStateException("Next calendar date could not be found!");
        }
        
        return nextCalendarDate;
    }
    
    /**
     * Determines a previous calendar date, if it's a holiday, the previous date will be the holiday begin period -1 day.
     * 
     * 
     * @param date Date to check
     * @return Previous calendar date.
     */
	public Date previousCalendarDate(Date date) {

		if (date == null) {
			return null;
		}
		if(!DateUtils.isWithinDate(date, startDate, endDate)) {
		    log.warn("The given date: " +date +" is not in period [startDate,endDate] of banking Calendar: "+ code);
		    return date;
		}
		Date previousCalendarDate = date;
        int iteration = 0;
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(previousCalendarDate);
        int lastYear;
        int newYear;
		do {
			lastYear = calendar.get(java.util.Calendar.YEAR);
			iteration++;
			if(isWeekend(previousCalendarDate)) {            		
				previousCalendarDate = getDateBeforeWeekend(previousCalendarDate);
        	}
			for (CalendarHoliday holidayPeriod : getHolidays()) {
				if (holidayPeriod.isHolidayDate(previousCalendarDate)) {
					previousCalendarDate = holidayPeriod.getDateBeforeHoliday(previousCalendarDate);
					if(isWeekend(previousCalendarDate)) {            		
						previousCalendarDate = getDateBeforeWeekend(previousCalendarDate);
		        	}
				}
			}
			calendar.setTime(previousCalendarDate);
        	newYear = calendar.get(java.util.Calendar.YEAR);
		} while ((lastYear != newYear) && iteration < 2); // we do two iterations in case of change of year, because the previousCalendarDate can be a holiday of last year.

		if ((lastYear != newYear) && iteration < 2) { // all possibles previous dates are a holiday
			throw new IllegalStateException("Next calendar date could not be found!");
		}

		return previousCalendarDate;

	}

    private Date getDateBeforeWeekend(Date date) {
		// if the date is not a weekend, we return the same date
		if (!isWeekend(date)) {
			return date;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        if (weekday == 0) {
            weekday = 7;
        }
        
        int weekdayAdjusted = weekday;
        if (isWeekendCrossBoundry() && weekday < getWeekendBegin()) {
            weekdayAdjusted = weekday + 7;
        }
        calendar.add(java.util.Calendar.DATE, (getWeekendBegin() - weekdayAdjusted)-1);
		return calendar.getTime();
	}

	private Date getDateAfterWeekend(Date date) {
		// if the date is not a weekend, we return the same date
		if (!isWeekend(date)) {
			return date;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        if (weekday == 0) {
            weekday = 7;
        }
        
        int weekdayAdjusted = weekday;
        if (isWeekendCrossBoundry() && weekday < getWeekendBegin()) {
            weekdayAdjusted = weekday + 7;
        }
        calendar.add(java.util.Calendar.DATE, (getWeekendEndAdjusted() - weekdayAdjusted)+1);
		return calendar.getTime();
	}

	/**
     * check if the date d is a weekend date
     * @param d the date to check
     * @return true if the checked date is a weekend, false otherwise
     */
	private boolean isWeekend(Date d) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(d);

		int weekday = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
		if (weekday == 0) {
			weekday = 7;
		}
		// Adjust given date's weekday if interval crosses to another week - and weekday
		// corresponds to another week. E.g checking monday for friday-tuesday period
		int weekdayAdjusted = weekday;
		if (isWeekendCrossBoundry() && weekday < getWeekendBegin()) {
			weekdayAdjusted = weekday + 7;
		}
		return getWeekendBegin() <= weekdayAdjusted && weekdayAdjusted <= getWeekendEndAdjusted();
	}

	/**
	 * When weekend period spans to another week (e.g. Thursday to Monday), the interval end value is adjusted by 7 days.
	 * @return Adjusted end weekend End value
	 */
	private int getWeekendEndAdjusted() {
		if (weekendEnd < weekendBegin) {
			return weekendEnd + 7;
		}
		return weekendEnd;
	}

	private boolean isWeekendCrossBoundry() {
		return weekendEnd < weekendBegin;
	}

	@Override
	public Date previousPeriodEndDate(Date date) {
		return null;
	}

	@Override
	public Date nextPeriodStartDate(Date date) {
		return null;
	}
}