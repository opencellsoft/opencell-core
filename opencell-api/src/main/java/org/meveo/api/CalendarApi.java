package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CalendarDateIntervalDto;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.CalendarTypeEnum;
import org.meveo.api.dto.DayInYearDto;
import org.meveo.api.dto.HourInDayDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarDateInterval;
import org.meveo.model.catalog.CalendarInterval;
import org.meveo.model.catalog.CalendarJoin;
import org.meveo.model.catalog.CalendarJoin.CalendarJoinTypeEnum;
import org.meveo.model.catalog.CalendarPeriod;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.HourInDay;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DayInYearService;
import org.meveo.service.catalog.impl.HourInDayService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CalendarApi extends BaseApi {

    @Inject
    private CalendarService calendarService;

    @Inject
    private DayInYearService dayInYearService;

    @Inject
    private HourInDayService hourInDayService;

    public void create(CalendarDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendarType())) {
            missingParameters.add("calendarType");
        }

        handleMissingParametersAndValidate(postData);
        

        

        if (calendarService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Calendar.class, postData.getCode());
        }

        if (postData.getCalendarType() == CalendarTypeEnum.YEARLY) {

            CalendarYearly calendar = new CalendarYearly();
            calendar.setCode(postData.getCode());
            calendar.setDescription(postData.getDescription());
            if (postData.getDays() != null && postData.getDays().size() > 0) {
                List<DayInYear> days = new ArrayList<DayInYear>();
                for (DayInYearDto d : postData.getDays()) {
                    DayInYear dayInYear = dayInYearService.findByMonthAndDay(d.getMonth(), d.getDay());
                    if (dayInYear != null) {
                        days.add(dayInYear);
                    }
                }

                calendar.setDays(days);
            }

            calendarService.create(calendar);

        } else if (postData.getCalendarType() == CalendarTypeEnum.DAILY) {

            CalendarDaily calendar = new CalendarDaily();
            calendar.setCode(postData.getCode());
            calendar.setDescription(postData.getDescription());

            if (postData.getHours() != null && postData.getHours().size() > 0) {
                List<HourInDay> hours = new ArrayList<HourInDay>();
                for (HourInDayDto d : postData.getHours()) {
                    HourInDay hourInDay = hourInDayService.findByHourAndMin(d.getHour(), d.getMin());
                    if (hourInDay == null) {
                        hourInDay = new HourInDay(d.getHour(), d.getMin());
                    }
                    hours.add(hourInDay);
                }

                calendar.setHours(hours);
            }

            calendarService.create(calendar);

        } else if (postData.getCalendarType() == CalendarTypeEnum.PERIOD) {

            if (StringUtils.isBlank(postData.getPeriodUnit())) {
                missingParameters.add("periodUnit");
                handleMissingParameters();
            }

            CalendarPeriod calendar = new CalendarPeriod();
            calendar.setCode(postData.getCode());
            calendar.setDescription(postData.getDescription());
            calendar.setPeriodLength(postData.getPeriodLength());
            calendar.setNbPeriods(postData.getNbPeriods());
            calendar.setPeriodUnit(postData.getPeriodUnit().getUnitValue());

            calendarService.create(calendar);

        } else if (postData.getCalendarType() == CalendarTypeEnum.INTERVAL) {

            CalendarInterval calendar = new CalendarInterval();
            calendar.setCode(postData.getCode());
            calendar.setDescription(postData.getDescription());
            calendar.setIntervalType(postData.getIntervalType());

            if (postData.getIntervals() != null && postData.getIntervals().size() > 0) {
                List<CalendarDateInterval> intervals = new ArrayList<CalendarDateInterval>();
                for (CalendarDateIntervalDto interval : postData.getIntervals()) {
                    intervals.add(new CalendarDateInterval(calendar, interval.getIntervalBegin(), interval.getIntervalEnd()));
                }

                calendar.setIntervals(intervals);
            }

            calendarService.create(calendar);

        } else if (postData.getCalendarType().isJoin()) {

            if (StringUtils.isBlank(postData.getJoinCalendar1Code())) {
                missingParameters.add("joinCalendar1Code");
            }
            if (StringUtils.isBlank(postData.getJoinCalendar2Code())) {
                missingParameters.add("joinCalendar2Code");
            }

            handleMissingParameters();
            

            Calendar cal1 = calendarService.findByCode(postData.getJoinCalendar1Code());
            Calendar cal2 = calendarService.findByCode(postData.getJoinCalendar2Code());

            if (cal1 == null) {
                throw new InvalidParameterException("joinCalendar1Code", postData.getJoinCalendar1Code());
            }
            if (cal2 == null) {
                throw new InvalidParameterException("joinCalendar2Code", postData.getJoinCalendar2Code());
            }

            CalendarJoin calendar = new CalendarJoin();
            calendar.setCode(postData.getCode());
            calendar.setDescription(postData.getDescription());
            calendar.setJoinType(CalendarJoinTypeEnum.valueOf(postData.getCalendarType().name())); // Join type is expressed as Calendar type in DTO
            calendar.setJoinCalendar1(cal1);
            calendar.setJoinCalendar2(cal2);

            calendarService.create(calendar);
        } else {
            throw new BusinessApiException("invalid calendar type, possible values YEARLY, DAILY, PERIOD, INTERVAL, JOIN");
        }

    }

    public void update(CalendarDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCalendarType())) {
            missingParameters.add("calendarType");
        }

        handleMissingParametersAndValidate(postData);
        

        

        Calendar calendar = calendarService.findByCode(postData.getCode());
        if (calendar == null) {
            throw new EntityDoesNotExistsException(Calendar.class, postData.getCode());
        }
        calendar.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        calendar.setDescription(postData.getDescription());

        if (calendar instanceof CalendarYearly) {
            if (postData.getDays() != null && postData.getDays().size() > 0) {
                List<DayInYear> days = new ArrayList<DayInYear>();
                for (DayInYearDto d : postData.getDays()) {
                    DayInYear dayInYear = dayInYearService.findByMonthAndDay(d.getMonth(), d.getDay());
                    if (dayInYear != null) {
                        days.add(dayInYear);
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
            if (!StringUtils.isBlank(postData.getPeriodUnit())) {
                ((CalendarPeriod) calendar).setPeriodUnit(postData.getPeriodUnit().getUnitValue());
            }

        } else if (calendar instanceof CalendarInterval) {

            CalendarInterval calendarInterval = (CalendarInterval) calendar;
            calendarInterval.setIntervalType(postData.getIntervalType());

            calendarInterval.getIntervals().clear();

            if (postData.getIntervals() != null && postData.getIntervals().size() > 0) {
                for (CalendarDateIntervalDto interval : postData.getIntervals()) {
                    calendarInterval.getIntervals().add(new CalendarDateInterval(calendarInterval, interval.getIntervalBegin(), interval.getIntervalEnd()));
                }
            }

        } else if (calendar instanceof CalendarJoin) {

            if (StringUtils.isBlank(postData.getJoinCalendar1Code())) {
                missingParameters.add("joinCalendar1Code");
            }
            if (StringUtils.isBlank(postData.getJoinCalendar2Code())) {
                missingParameters.add("joinCalendar2Code");
            }

            handleMissingParameters();
            

            Calendar cal1 = calendarService.findByCode(postData.getJoinCalendar1Code());
            Calendar cal2 = calendarService.findByCode(postData.getJoinCalendar2Code());

            if (cal1 == null) {
                throw new InvalidParameterException("joinCalendar1Code", postData.getJoinCalendar1Code());
            }
            if (cal2 == null) {
                throw new InvalidParameterException("joinCalendar2Code", postData.getJoinCalendar2Code());
            }

            CalendarJoin calendarJoin = (CalendarJoin) calendar;
            calendarJoin.setJoinType(CalendarJoinTypeEnum.valueOf(postData.getCalendarType().name()));// Join type is expressed as Calendar type in DTO
            calendarJoin.setJoinCalendar1(cal1);
            calendarJoin.setJoinCalendar2(cal2);
        }

        calendarService.update(calendar);
    }

    public CalendarDto find(String calendarCode) throws MeveoApiException {
        CalendarDto result = new CalendarDto();

        if (!StringUtils.isBlank(calendarCode)) {
            Calendar calendar = calendarService.findByCode(calendarCode);
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, calendarCode);
            }

            result = new CalendarDto(calendar);
        } else {
            if (StringUtils.isBlank(calendarCode)) {
                missingParameters.add("calendarCode");
            }

            handleMissingParameters();
        }

        return result;
    }
    
    public List<CalendarDto> list() throws MeveoApiException{
    	List<CalendarDto> result = new ArrayList<CalendarDto>();
		for(Calendar calendar : calendarService.list()){
    		result.add(new CalendarDto(calendar));
    	}
    	return result;
    }

    public void remove(String calendarCode) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(calendarCode)) {
            Calendar calendar = calendarService.findByCode(calendarCode);
            if (calendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, calendarCode);
            }

            calendarService.remove(calendar);
        } else {
            if (StringUtils.isBlank(calendarCode)) {
                missingParameters.add("calendarCode");
            }

            handleMissingParameters();
        }
    }

    public void createOrUpdate(CalendarDto postData) throws MeveoApiException, BusinessException {
        Calendar calendar = calendarService.findByCode(postData.getCode());
        if (calendar == null) {
            // create
            create(postData);
        } else {
            // update
            update(postData);
        }
    }

}
