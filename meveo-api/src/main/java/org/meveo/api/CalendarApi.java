package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CalendarDateIntervalDto;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.DayInYearDto;
import org.meveo.api.dto.HourInDayDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarDateInterval;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarInterval.CalendarIntervalTypeEnum;
import org.meveo.model.catalog.CalendarJoin;
import org.meveo.model.catalog.CalendarJoin.CalendarJoinTypeEnum;
import org.meveo.model.catalog.CalendarPeriod;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.HourInDay;
import org.meveo.model.catalog.MonthEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DayInYearService;
import org.meveo.service.catalog.impl.HourInDayService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CalendarApi extends BaseApi {

    @Inject
    private Logger log;

    @Inject
    private CalendarService calendarService;

    @Inject
    private DayInYearService dayInYearService;

    @Inject
    private HourInDayService hourInDayService;

    public void create(CalendarDto postData, User currentUser) throws MeveoApiException {
        if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getCalendarType())) {
            Provider provider = currentUser.getProvider();

            if (calendarService.findByCode(postData.getCode(), provider) != null) {
                throw new EntityAlreadyExistsException(Calendar.class, postData.getCode());
            }

            if ("YEARLY".equalsIgnoreCase(postData.getCalendarType())) {

                CalendarYearly calendar = new CalendarYearly();
                calendar.setCode(postData.getCode());
                calendar.setDescription(postData.getDescription());
                if (postData.getDays() != null && postData.getDays().size() > 0) {
                    List<DayInYear> days = new ArrayList<DayInYear>();
                    for (DayInYearDto d : postData.getDays()) {
                        try {
                            DayInYear dayInYear = dayInYearService.findByMonthAndDay(MonthEnum.valueOf(d.getMonth()), d.getDay());
                            if (dayInYear != null) {
                                days.add(dayInYear);
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("month={}", e.getMessage());
                        }
                    }

                    calendar.setDays(days);
                }

                calendarService.create(calendar, currentUser, provider);

            } else if ("DAILY".equalsIgnoreCase(postData.getCalendarType())) {

                CalendarDaily calendar = new CalendarDaily();
                calendar.setCode(postData.getCode());
                calendar.setDescription(postData.getDescription());

                if (postData.getHours() != null && postData.getHours().size() > 0) {
                    List<HourInDay> hours = new ArrayList<HourInDay>();
                    for (HourInDayDto d : postData.getHours()) {
                        HourInDay hourInDay = hourInDayService.findByHourAndMin(d.getHour(), d.getMin());
                        if (hourInDay != null) {
                            hours.add(hourInDay);
                        }
                    }

                    calendar.setHours(hours);
                }

                calendarService.create(calendar, currentUser, provider);

            } else if ("PERIOD".equalsIgnoreCase(postData.getCalendarType())) {

                CalendarPeriod calendar = new CalendarPeriod();
                calendar.setCode(postData.getCode());
                calendar.setDescription(postData.getDescription());
                calendar.setPeriodLength(postData.getPeriodLength());
                calendar.setNbPeriods(postData.getNbPeriods());

                calendarService.create(calendar, currentUser, provider);
                            
            } else if ("INTERVAL".equalsIgnoreCase(postData.getCalendarType())) {

                CalendarInterval calendar = new CalendarInterval();
                calendar.setCode(postData.getCode());
                calendar.setDescription(postData.getDescription());
                calendar.setIntervalType(CalendarIntervalTypeEnum.valueOf(postData.getIntervalType()));

                if (postData.getIntervals() != null && postData.getIntervals().size() > 0) {
                    List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
                    for (CalendarDateIntervalDto interval : postData.getIntervals()) {
                            intervals.add(new CalendarDateInterval(calendar, interval.getIntervalBegin(), interval.getIntervalEnd()));
                    }

                    calendar.setIntervals(intervals);
                }

                calendarService.create(calendar, currentUser, provider);

            } else if ("JOIN".equalsIgnoreCase(postData.getCalendarType())) {

                Calendar cal1 = calendarService.findByCode(postData.getJoinCalendar1Code(), provider);
                Calendar cal2 = calendarService.findByCode(postData.getJoinCalendar2Code(), provider);

                CalendarJoin calendar = new CalendarJoin();
                calendar.setCode(postData.getCode());
                calendar.setDescription(postData.getDescription());
                calendar.setJoinType(CalendarJoinTypeEnum.valueOf(postData.getJoinType()));
                calendar.setJoinCalendar1(cal1);
                calendar.setJoinCalendar2(cal2);

                calendarService.create(calendar, currentUser, provider);
            }
            

        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getCalendarType())) {
                missingParameters.add("calendarType");
            }
        }
    }

    public void update(CalendarDto postData, User currentUser) throws MeveoApiException {
        if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getCalendarType())) {
            Provider provider = currentUser.getProvider();

            Calendar calendar = calendarService.findByCode(postData.getCode(), provider);
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getCode());
            }

            calendar.setDescription(postData.getDescription());

            if (calendar instanceof CalendarYearly) {
                if (postData.getDays() != null && postData.getDays().size() > 0) {
                    List<DayInYear> days = new ArrayList<DayInYear>();
                    for (DayInYearDto d : postData.getDays()) {
                        try {
                            DayInYear dayInYear = dayInYearService.findByMonthAndDay(MonthEnum.valueOf(d.getMonth()), d.getDay());
                            if (dayInYear != null) {
                                days.add(dayInYear);
                            }
                        } catch (IllegalArgumentException e) {
                            log.warn("month={}", e.getMessage());
                        }
                    }

                    ((CalendarYearly) calendar).setDays(days);
                }

            } else if (calendar instanceof CalendarYearly) {
                if (postData.getHours() != null && postData.getHours().size() > 0) {
                    List<HourInDay> hours = new ArrayList<HourInDay>();
                    for (HourInDayDto d : postData.getHours()) {
                        HourInDay hourInDay = hourInDayService.findByHourAndMin(d.getHour(), d.getMin());
                        if (hourInDay != null) {
                            hours.add(hourInDay);
                        }
                    }

                    ((CalendarDaily) calendar).setHours(hours);
                }

            } else if (calendar instanceof CalendarPeriod) {

                ((CalendarPeriod) calendar).setPeriodLength(postData.getPeriodLength());
                ((CalendarPeriod) calendar).setNbPeriods(postData.getNbPeriods());
            
            } else if (calendar instanceof CalendarInterval) {

                CalendarInterval calendarInterval = (CalendarInterval) calendar;
                calendarInterval.setIntervalType(CalendarIntervalTypeEnum.valueOf(postData.getIntervalType()));

                calendarInterval.getIntervals().clear();
                
                if (postData.getIntervals() != null && postData.getIntervals().size() > 0) {
                    List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
                    for (CalendarDateIntervalDto interval : postData.getIntervals()) {
                        calendarInterval.getIntervals().add(new CalendarDateInterval(calendarInterval, interval.getIntervalBegin(), interval.getIntervalEnd()));
                    }
                }

            } else if (calendar instanceof CalendarJoin) {

                Calendar cal1 = calendarService.findByCode(postData.getJoinCalendar1Code(), provider);
                Calendar cal2 = calendarService.findByCode(postData.getJoinCalendar2Code(), provider);

                CalendarJoin calendarJoin = (CalendarJoin) calendar;
                calendarJoin.setJoinType(CalendarJoinTypeEnum.valueOf(postData.getJoinType()));
                calendarJoin.setJoinCalendar1(cal1);
                calendarJoin.setJoinCalendar2(cal2);
            }

            calendarService.update(calendar, currentUser);

        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getCalendarType())) {
                missingParameters.add("calendarType");
            }
        }
    }

    public CalendarDto find(String calendarCode, Provider provider) throws MeveoApiException {
        CalendarDto result = new CalendarDto();

        if (!StringUtils.isBlank(calendarCode)) {
            Calendar calendar = calendarService.findByCode(calendarCode, provider);
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, calendarCode);
            }

            result = new CalendarDto(calendar);
        } else {
            if (StringUtils.isBlank(calendarCode)) {
                missingParameters.add("calendarCode");
            }

            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        return result;
    }

    public void remove(String calendarCode, Provider provider) throws MeveoApiException {
        if (!StringUtils.isBlank(calendarCode)) {
            Calendar calendar = calendarService.findByCode(calendarCode, provider);
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, calendarCode);
            }

            calendarService.remove(calendar);
        } else {
            if (StringUtils.isBlank(calendarCode)) {
                missingParameters.add("calendarCode");
            }

            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }
    }

}
