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

package org.meveo.api.dto;

import static org.meveo.api.dto.LanguageDescriptionDto.convertMultiLanguageFromMapOfValues;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarBanking;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarDateInterval;
import org.meveo.model.catalog.CalendarFixed;
import org.meveo.model.catalog.CalendarHoliday;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarIntervalTypeEnum;
import org.meveo.model.catalog.CalendarJoin;
import org.meveo.model.catalog.CalendarPeriod;
import org.meveo.model.catalog.CalendarPeriodUnitEnum;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.FixedDate;
import org.meveo.model.catalog.HourInDay;
import org.meveo.model.shared.DateUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class CalendarDto.
 *
 * @author Edward P. Legaspi
 * @author hznibar
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class CalendarDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8269245242022483636L;

    /** Calendar type. */
    @XmlElement(required = true)
    @Schema(description = "calendar type", example = "possible value are : YEARLY, DAILY, PERIOD, INTERVAL, INTERSECT, UNION, APPEND, BANKING, FIXED")
    private CalendarTypeEnum calendarType;

    /** Fixed Dates. */
    @Schema(description = "list of fixed date")
    private List<String> fixedDates;

    /** Days. */
    @Schema(description = "list of the day")
    private List<DayInYearDto> days;

    /** Hours. */
    @Schema(description = "list of the hour")
    private List<HourInDayDto> hours;

    /** Period length. */
    @Schema(description = "Period length")
    private Integer periodLength;

    /** Period measurement unit. */
    @Schema(description = "Period measurement unit", example = "MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE, SECOND")
    private CalendarPeriodUnitEnum periodUnit;

    /** Number of periods. */
    @Schema(description = "Number of periods")
    private Integer nbPeriods;

    /** Code of the first calendar to intersect/union. */
    @Schema(description = "Code of the first calendar to intersect/union")
    private String joinCalendar1Code;

    /** Code of the second calendar to intersect/union. */
    @Schema(description = "Code of the second calendar to intersect/union")
    private String joinCalendar2Code;

    /** Interval type. */
    @Schema(description = "Interval type", example = "possible value are : DAY, HOUR, WDAY")
    private CalendarIntervalTypeEnum intervalType;

    /** List of intervals. */
    @Schema(description = "List of intervals")
    private List<CalendarDateIntervalDto> intervals;

    /** The weekend begin. */
    @Schema(description = "The weekend begin")
    private Integer weekendBegin;

    /** The weekend end. */
    @Schema(description = "")
    private Integer weekendEnd;

    /** The end date. */
    @Schema(description = "The end dat")
    private Date endDate;

    /** The start date. */
    @Schema(description = "The start date")
    private Date startDate;

    /**
     * Calendar initialization date - expression to determine a value for calendar initialization date
     */
    @Size(max = 2000)
    @Schema(description = "Calendar initialization date - expression to determine a value for calendar initialization date")
    private String initDateEL;

    /**
     * Calendar initialization date - expression to determine a value for calendar initialization date for Spark
     */
    @Size(max = 2000)
    @Schema(description = "Calendar initialization date - expression to determine a value for calendar initialization date for Spark")
    private String initDateELSpark;

    @Schema(description = "list of the days of holiday")
    private List<CalendarHolidayDto> holidays;

    @Schema(description = "lsit of language description")
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new calendar dto.
     */
    public CalendarDto() {
    }

    /**
     * Instantiates a new calendar dto.
     *
     * @param calendarEntity the Calendar entity
     */
    public CalendarDto(Calendar calendarEntity) {
        super(calendarEntity);
        languageDescriptions = convertMultiLanguageFromMapOfValues(calendarEntity.getDescriptionI18n());
        calendarType = CalendarTypeEnum.valueOf(calendarEntity.getCalendarTypeWSubtypes());

        if (calendarEntity instanceof CalendarYearly) {
            CalendarYearly calendar = (CalendarYearly) calendarEntity;
            if (calendar.getDays() != null && calendar.getDays().size() > 0) {
                days = new ArrayList<DayInYearDto>();
                for (DayInYear d : calendar.getDays()) {
                    days.add(new DayInYearDto(d));
                }
            }
        } else if (calendarEntity instanceof CalendarDaily) {
            CalendarDaily calendar = (CalendarDaily) calendarEntity;
            if (calendar.getHours() != null && calendar.getHours().size() > 0) {
                hours = new ArrayList<HourInDayDto>();
                for (HourInDay d : calendar.getHours()) {
                    hours.add(new HourInDayDto(d));
                }
            }

        } else if (calendarEntity instanceof CalendarFixed) {
            CalendarFixed calendar = (CalendarFixed) calendarEntity;
            fixedDates = new ArrayList<>();
            for (FixedDate fixedDate : calendar.getFixedDates()) {
                fixedDates.add(DateUtils.formatDateWithPattern(fixedDate.getDatePeriod().getFrom(), "dd/MM/yyyy HH:mm") + "-" + DateUtils.formatDateWithPattern(fixedDate.getDatePeriod().getTo(), "dd/MM/yyyy HH:mm"));
            }
        } else if (calendarEntity instanceof CalendarPeriod) {
            CalendarPeriod calendar = (CalendarPeriod) calendarEntity;
            periodLength = calendar.getPeriodLength();
            periodUnit = CalendarPeriodUnitEnum.getValueByUnit(calendar.getPeriodUnit());
            nbPeriods = calendar.getNbPeriods();

        } else if (calendarEntity instanceof CalendarInterval) {
            CalendarInterval calendar = (CalendarInterval) calendarEntity;
            intervalType = calendar.getIntervalType();

            if (calendar.getIntervals() != null && calendar.getIntervals().size() > 0) {
                intervals = new ArrayList<CalendarDateIntervalDto>();
                for (CalendarDateInterval d : calendar.getIntervals()) {
                    intervals.add(new CalendarDateIntervalDto(d));
                }
            }

        } else if (calendarEntity instanceof CalendarJoin) {
            CalendarJoin calendar = (CalendarJoin) calendarEntity;

            joinCalendar1Code = calendar.getJoinCalendar1().getCode();
            joinCalendar2Code = calendar.getJoinCalendar2().getCode();
        } else if (calendarEntity instanceof CalendarBanking) {
            CalendarBanking calendar = (CalendarBanking) calendarEntity;
            startDate = calendar.getStartDate();
            endDate = calendar.getEndDate();
            weekendBegin = calendar.getWeekendBegin();
            weekendEnd = calendar.getWeekendEnd();

            if (calendar.getHolidays() != null && calendar.getHolidays().size() > 0) {
                holidays = new ArrayList<CalendarHolidayDto>();
                for (CalendarHoliday d : calendar.getHolidays()) {
                    holidays.add(new CalendarHolidayDto(d));
                }
            }
        }
    }

    /**
     * Gets the calendar type.
     *
     * @return the calendar type
     */
    public CalendarTypeEnum getCalendarType() {
        return calendarType;
    }

    /**
     * Sets the calendar type.
     *
     * @param calendarType the new calendar type
     */
    public void setCalendarType(CalendarTypeEnum calendarType) {
        this.calendarType = calendarType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final int maxLen = 10;
        return "CalendarDto [code=" + getCode() + ", description=" + getDescription() + ", calendarType=" + calendarType + ", days=" + (days != null ? days.subList(0, Math.min(days.size(), maxLen)) : null) + ", hours="
                + (hours != null ? hours.subList(0, Math.min(hours.size(), maxLen)) : null) + ", periodLength=" + periodLength + ", periodUnit=" + periodUnit + ", nbPeriods=" + nbPeriods + ", joinCalendar1Code="
                + joinCalendar1Code + ", joinCalendar2Code=" + joinCalendar2Code + ", intervalType=" + intervalType + ", intervals=" + (intervals != null ? intervals.subList(0, Math.min(intervals.size(), maxLen)) : null)
                + ", startDate=" + startDate + ", endDate=" + endDate + ", weekendBegin=" + weekendBegin + ", weekendEnd=" + weekendEnd + ", holidays="
                + (holidays != null ? holidays.subList(0, Math.min(holidays.size(), maxLen)) : null) + "]";
    }

    /**
     * Gets the days.
     *
     * @return the days
     */
    public List<DayInYearDto> getDays() {
        return days;
    }

    /**
     * Sets the days.
     *
     * @param days the new days
     */
    public void setDays(List<DayInYearDto> days) {
        this.days = days;
    }

    /**
     * Gets the hours.
     *
     * @return the hours
     */
    public List<HourInDayDto> getHours() {
        return hours;
    }

    /**
     * Sets the hours.
     *
     * @param hours the new hours
     */
    public void setHours(List<HourInDayDto> hours) {
        this.hours = hours;
    }

    /**
     * Gets the period length.
     *
     * @return the period length
     */
    public Integer getPeriodLength() {
        return periodLength;
    }

    /**
     * Sets the period length.
     *
     * @param periodLength the new period length
     */
    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    /**
     * Gets the period unit.
     *
     * @return the period unit
     */
    public CalendarPeriodUnitEnum getPeriodUnit() {
        return periodUnit;
    }

    /**
     * Sets the period unit.
     *
     * @param periodUnit the new period unit
     */
    public void setPeriodUnit(CalendarPeriodUnitEnum periodUnit) {
        this.periodUnit = periodUnit;
    }

    /**
     * Gets the nb periods.
     *
     * @return the nb periods
     */
    public Integer getNbPeriods() {
        return nbPeriods;
    }

    /**
     * Sets the nb periods.
     *
     * @param nbPeriods the new nb periods
     */
    public void setNbPeriods(Integer nbPeriods) {
        this.nbPeriods = nbPeriods;
    }

    /**
     * Gets the join calendar 1 code.
     *
     * @return the join calendar 1 code
     */
    public String getJoinCalendar1Code() {
        return joinCalendar1Code;
    }

    /**
     * Sets the join calendar 1 code.
     *
     * @param joinCalendar1Code the new join calendar 1 code
     */
    public void setJoinCalendar1Code(String joinCalendar1Code) {
        this.joinCalendar1Code = joinCalendar1Code;
    }

    /**
     * Gets the join calendar 2 code.
     *
     * @return the join calendar 2 code
     */
    public String getJoinCalendar2Code() {
        return joinCalendar2Code;
    }

    /**
     * Sets the join calendar 2 code.
     *
     * @param joinCalendar2Code the new join calendar 2 code
     */
    public void setJoinCalendar2Code(String joinCalendar2Code) {
        this.joinCalendar2Code = joinCalendar2Code;
    }

    /**
     * Gets the interval type.
     *
     * @return the interval type
     */
    public CalendarIntervalTypeEnum getIntervalType() {
        return intervalType;
    }

    /**
     * Sets the interval type.
     *
     * @param intervalType the new interval type
     */
    public void setIntervalType(CalendarIntervalTypeEnum intervalType) {
        this.intervalType = intervalType;
    }

    /**
     * Gets the intervals.
     *
     * @return the intervals
     */
    public List<CalendarDateIntervalDto> getIntervals() {
        return intervals;
    }

    /**
     * Sets the intervals.
     *
     * @param intervals the new intervals
     */
    public void setIntervals(List<CalendarDateIntervalDto> intervals) {
        this.intervals = intervals;
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

    /**
     * Gets the weekend end.
     *
     * @return the weekend end
     */
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
     * Gets the holidays.
     *
     * @return the holidays
     */
    public List<CalendarHolidayDto> getHolidays() {
        return holidays;
    }

    /**
     * Sets the holidays.
     *
     * @param holidays the new holidays
     */
    public void setHolidays(List<CalendarHolidayDto> holidays) {
        this.holidays = holidays;
    }

    /**
     * get Fixed Dates
     * 
     * @return list of fixed dates
     */
    public List<String> getFixedDates() {
        return fixedDates;
    }

    /**
     * set Fixed Dates
     * 
     * @param fixedDates fixed dates
     */
    public void setFixedDates(List<String> fixedDates) {
        this.fixedDates = fixedDates;
    }

    /**
     * @return Calendar initialization date - expression to determine a value for calendar initialization date
     */
    public String getInitDateEL() {
        return initDateEL;
    }

    /**
     * @param initDateEL Calendar initialization date - expression to determine a value for calendar initialization date
     */
    public void setInitDateEL(String initDateEL) {
        this.initDateEL = initDateEL;
    }

    /**
     * @return Calendar initialization date - expression to determine a value for calendar initialization date for Spark
     */
    public String getInitDateELSpark() {
        return initDateELSpark;
    }

    /**
     * @param initDateELSpark Calendar initialization date - expression to determine a value for calendar initialization date for Spark
     */
    public void setInitDateELSpark(String initDateELSpark) {
        this.initDateELSpark = initDateELSpark;
    }

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}