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
package org.meveo.admin.action.catalog;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.DiscriminatorValue;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
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
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.HourInDay;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DayInYearService;
import org.meveo.service.catalog.impl.HourInDayService;
import org.omnifaces.cdi.Param;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class CalendarBean extends BaseBean<Calendar> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Calendar} service. Extends {@link PersistenceService}. */
    @Inject
    private CalendarService calendarService;

    @Inject
    private DayInYearService dayInYearService;

    private DualListModel<DayInYear> dayInYearListModel;

    @Inject
    private HourInDayService hourInDayService;

    @Inject
    @Param
    private String classType;

    @Inject
    private ResourceBundle resourceMessages;

    private String timeToAdd;

    private Integer weekdayIntervalFrom;
    private Integer weekdayIntervalTo;

    private String holidayToAdd;

    private Integer weekendFrom;
    private Integer weekendTo;

    public static String FIXED_DATE_FORMAT = "dd/MM/yyyy HH:mm";

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CalendarBean() {
        super(Calendar.class);
    }

    public Calendar getInstance() throws InstantiationException, IllegalAccessException {

        Calendar calendar = CalendarYearly.class.newInstance();
        calendar.setCalendarType(CalendarYearly.class.getAnnotation(DiscriminatorValue.class).value());

        return calendar;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Calendar> getPersistenceService() {
        return calendarService;
    }

    public DualListModel<DayInYear> getDayInYearModel() {

        if (dayInYearListModel == null && getEntity() instanceof CalendarYearly) {
            List<DayInYear> perksSource = dayInYearService.list();
            List<DayInYear> perksTarget = new ArrayList<DayInYear>();
            if (((CalendarYearly) getEntity()).getDays() != null) {
                perksTarget.addAll(((CalendarYearly) getEntity()).getDays());
            }
            perksSource.removeAll(perksTarget);
            dayInYearListModel = new DualListModel<DayInYear>(perksSource, perksTarget);
        }
        return dayInYearListModel;
    }

    public String getTimeToAdd() {
        return timeToAdd;
    }

    public void setTimeToAdd(String timeToAdd) {
        this.timeToAdd = timeToAdd;
    }

    public Integer getWeekdayIntervalFrom() {
        return weekdayIntervalFrom;
    }

    public void setWeekdayIntervalFrom(Integer weekdayIntervalFrom) {
        this.weekdayIntervalFrom = weekdayIntervalFrom;
    }

    public Integer getWeekdayIntervalTo() {
        return weekdayIntervalTo;
    }

    public void setWeekdayIntervalTo(Integer weekdayIntervalTo) {
        this.weekdayIntervalTo = weekdayIntervalTo;
    }

    public String getHolidayToAdd() {
        return holidayToAdd;
    }

    public void setHolidayToAdd(String holidayToAdd) {
        this.holidayToAdd = holidayToAdd;
    }

    public Integer getWeekendFrom() {
        return weekendFrom;
    }

    public void setWeekendFrom(Integer weekendFrom) {
        this.weekendFrom = weekendFrom;
    }

    public Integer getWeekendTo() {
        return weekendTo;
    }

    public void setWeekendTo(Integer weekendTo) {
        this.weekendTo = weekendTo;
    }

    public void setDayInYearModel(DualListModel<DayInYear> perks) {
        this.dayInYearListModel = perks;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public Map<String, String> getCalendarTypes() {
        Map<String, String> values = new HashMap<String, String>();

        values.put("FIXED", resourceMessages.getString("calendar.calendarType.FIXED"));
        values.put("DAILY", resourceMessages.getString("calendar.calendarType.DAILY"));
        values.put("YEARLY", resourceMessages.getString("calendar.calendarType.YEARLY"));
        values.put("PERIOD", resourceMessages.getString("calendar.calendarType.PERIOD"));
        values.put("INTERVAL", resourceMessages.getString("calendar.calendarType.INTERVAL"));
        values.put("JOIN", resourceMessages.getString("calendar.calendarType.JOIN"));
        values.put("BANKING", resourceMessages.getString("calendar.calendarType.BANKING"));

        return values;
    }

    public Map<String, String> getPeriodTypes() {
        Map<String, String> values = new HashMap<String, String>();

        values.put(Integer.toString(java.util.Calendar.MONTH), resourceMessages.getString("calendar.periodUnit.2"));
        values.put(Integer.toString(java.util.Calendar.DAY_OF_MONTH), resourceMessages.getString("calendar.periodUnit.5"));
        values.put(Integer.toString(java.util.Calendar.HOUR_OF_DAY), resourceMessages.getString("calendar.periodUnit.11"));
        values.put(Integer.toString(java.util.Calendar.MINUTE), resourceMessages.getString("calendar.periodUnit.12"));
        values.put(Integer.toString(java.util.Calendar.SECOND), resourceMessages.getString("calendar.periodUnit.13"));

        return values;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void changeCalendarType(AjaxBehaviorEvent event) {

        String newType = (String) ((EditableValueHolder) event.getComponent()).getValue();

        Class[] classes = { CalendarYearly.class, CalendarFixed.class, CalendarDaily.class, CalendarPeriod.class, CalendarInterval.class, CalendarJoin.class, CalendarBanking.class };
        for (Class clazz : classes) {

            if (newType.equalsIgnoreCase(((DiscriminatorValue) clazz.getAnnotation(DiscriminatorValue.class)).value())) {

                try {
                    Calendar calendar = (Calendar) clazz.newInstance();

                    calendar.setCalendarType(((DiscriminatorValue) clazz.getAnnotation(DiscriminatorValue.class)).value());
                    setEntity(calendar);
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("Failed to instantiate a calendar", e);
                }
                return;
            }
        }
    }

    public void changeIntervalType(AjaxBehaviorEvent event) {

        CalendarIntervalTypeEnum newType = (CalendarIntervalTypeEnum) ((EditableValueHolder) event.getComponent()).getValue();

        ((CalendarInterval) entity).setIntervalType(newType);
        if (((CalendarInterval) entity).getIntervals() != null) {
            ((CalendarInterval) entity).getIntervals().clear();
        }
    }

    public void addTime() throws BusinessException {

        if (timeToAdd == null || timeToAdd.compareTo("23:59") > 0) {
            return;
        }

        String[] hourMin = timeToAdd.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int minute = Integer.parseInt(hourMin[1]);

        HourInDay hourInDay = hourInDayService.findByHourAndMin(hour, minute);
        if (hourInDay == null) {
            hourInDay = new HourInDay(hour, minute);
            // hourInDayService.create(hourInDay);
        }

        timeToAdd = null;
        if (((CalendarDaily) entity).getHours() != null && ((CalendarDaily) entity).getHours().contains(hourInDay)) {
            return;
        }

        if (((CalendarDaily) entity).getHours() == null) {
            ((CalendarDaily) entity).setHours(new ArrayList<HourInDay>());
        }
        ((CalendarDaily) entity).getHours().add(hourInDay);
        Collections.sort(((CalendarDaily) entity).getHours());

    }

    public void removeTime(HourInDay hourInDay) {
        ((CalendarDaily) entity).getHours().remove(hourInDay);
    }

    public void addInterval() throws BusinessException {

        CalendarDateInterval intervalToAdd = null;

        // Process hour interval expressed as hh:mm - hh:mm
        if (((CalendarInterval) entity).getIntervalType() == CalendarIntervalTypeEnum.HOUR) {
            if (timeToAdd == null) {
                return;
            }
            String[] hourMins = timeToAdd.split(" - ");

            if (hourMins[0].compareTo("23:59") > 0 || hourMins[1].compareTo("23:59") > 0) {
                return;
            }

            hourMins[0] = hourMins[0].replace(":", "");
            hourMins[1] = hourMins[1].replace(":", "");
            intervalToAdd = new CalendarDateInterval((CalendarInterval) entity, Integer.parseInt(hourMins[0]), Integer.parseInt(hourMins[1]));

            // Process hour interval expressed as mm/dd - mm/dd
        } else if (((CalendarInterval) entity).getIntervalType() == CalendarIntervalTypeEnum.DAY) {
            if (timeToAdd == null) {
                return;
            }

            String[] monthDays = timeToAdd.split(" - ");
            if (!isValidMonthDays(monthDays[0].substring(0, 2), monthDays[0].substring(3)) || !isValidMonthDays(monthDays[1].substring(0, 2), monthDays[1].substring(3))) {
                return;
            }

            monthDays[0] = monthDays[0].replace("/", "");
            monthDays[1] = monthDays[1].replace("/", "");
            intervalToAdd = new CalendarDateInterval((CalendarInterval) entity, Integer.parseInt(monthDays[0]), Integer.parseInt(monthDays[1]));

            // Process weekday interval
        } else if (((CalendarInterval) entity).getIntervalType() == CalendarIntervalTypeEnum.WDAY) {
            if (weekdayIntervalFrom == null || weekdayIntervalTo == null) {
                return;
            }

            intervalToAdd = new CalendarDateInterval((CalendarInterval) entity, weekdayIntervalFrom, weekdayIntervalTo);

        }

        timeToAdd = null;
        weekdayIntervalFrom = null;
        weekdayIntervalTo = null;

        // Check if not duplicate
        if (((CalendarInterval) entity).getIntervals() != null && ((CalendarInterval) entity).getIntervals().contains(intervalToAdd)) {
            return;
        }
        if (((CalendarInterval) entity).getIntervals() == null) {
            ((CalendarInterval) entity).setIntervals(new ArrayList<CalendarDateInterval>());
        }
        ((CalendarInterval) entity).getIntervals().add(intervalToAdd);
        Collections.sort(((CalendarInterval) entity).getIntervals());
    }

    public void removeInterval(CalendarDateInterval interval) {
        ((CalendarInterval) entity).getIntervals().remove(interval);
    }

    public List<CalendarIntervalTypeEnum> getCalendarIntervalTypeEnumValues() {
        return Arrays.asList(CalendarIntervalTypeEnum.values());
    }

    public void addHoliday() throws BusinessException {

        CalendarHoliday holiday = null;

        if (holidayToAdd == null) {
            return;
        }

        String[] monthDays = holidayToAdd.split(" - ");

        if (!isValidMonthDays(monthDays[0].substring(0, 2), monthDays[0].substring(3)) || !isValidMonthDays(monthDays[1].substring(0, 2), monthDays[1].substring(3))) {
            return;
        }

        monthDays[0] = monthDays[0].replace("/", "");
        monthDays[1] = monthDays[1].replace("/", "");
        holiday = new CalendarHoliday((CalendarBanking) entity, Integer.parseInt(monthDays[0]), Integer.parseInt(monthDays[1]));

        holidayToAdd = null;

        // Check if not duplicate
        if (((CalendarBanking) entity).getHolidays() != null && ((CalendarBanking) entity).getHolidays().contains(holiday)) {
            return;
        }
        if (((CalendarBanking) entity).getHolidays() == null) {
            ((CalendarBanking) entity).setHolidays(new ArrayList<CalendarHoliday>());
        }
        ((CalendarBanking) entity).getHolidays().add(holiday);
        Collections.sort(((CalendarBanking) entity).getHolidays());
    }

    public void removeHoliday(CalendarHoliday holiday) {
        ((CalendarBanking) entity).getHolidays().remove(holiday);
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (entity instanceof CalendarYearly) {
            if (((CalendarYearly) getEntity()).getDays() == null) {
                ((CalendarYearly) getEntity()).setDays(new ArrayList<DayInYear>());
            } else {
                ((CalendarYearly) getEntity()).getDays().clear();
            }
            ((CalendarYearly) getEntity()).getDays().addAll(dayInYearService.refreshOrRetrieve(dayInYearListModel.getTarget()));
        }

        return super.saveOrUpdate(killConversation);
    }

    /**
     * Checks if is valid month days.
     *
     * @param month the month
     * @param day the day
     * @return true, if is valid month days
     */
    private boolean isValidMonthDays(String month, String day) {
        if (StringUtils.isBlank(month) || StringUtils.isBlank(day)) {
            return false;
        }
        int iMonth = Integer.parseInt(month);
        int iDay = Integer.parseInt(day);
        switch (iMonth) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            return iDay <= 31;
        case 4:
        case 6:
        case 9:
        case 11:
            return iDay <= 30;
        case 2:
            return iDay <= 29;
        default:
            break;
        }
        return false;
    }
    public void addFixedDate() throws BusinessException {
        CalendarFixed calendarFixed = (CalendarFixed) entity;
        Date fromDate = null;
        Date toDate = null;
        if (timeToAdd == null || timeToAdd.split("-").length != 2) {
            return;
        }
        try {
            LocalDateTime.parse(timeToAdd.split("-")[0].trim(), DateTimeFormatter.ofPattern(FIXED_DATE_FORMAT));
            LocalDateTime.parse(timeToAdd.split("-")[1].trim(), DateTimeFormatter.ofPattern(FIXED_DATE_FORMAT));

            fromDate = new SimpleDateFormat(FIXED_DATE_FORMAT).parse(timeToAdd.split("-")[0].trim());
            toDate = new SimpleDateFormat(FIXED_DATE_FORMAT).parse(timeToAdd.split("-")[1].trim());
            
            if(! calendarFixed.isValidFixedDate(new DatePeriod(fromDate, toDate))) {
                return;
            }
            calendarFixed.addFixedDate(new DatePeriod(fromDate, toDate));
        } catch (Exception ex) {
            return;
        }     
        timeToAdd = null;
    }

    public void removeFixedDate(DatePeriod datePeriod) {
        ((CalendarFixed) entity).removeFixedDate(datePeriod); 
    }

    public String getFixedDate(DatePeriod datePeriod) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(FIXED_DATE_FORMAT);
            return formatter.format(datePeriod.getFrom()) + " - " + formatter.format(datePeriod.getTo());
        } catch (Exception e) {
            return "";
        }
    }
}