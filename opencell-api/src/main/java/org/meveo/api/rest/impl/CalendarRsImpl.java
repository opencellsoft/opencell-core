package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.CalendarApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.CalendarsDto;
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CalendarRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CalendarRsImpl extends BaseRs implements CalendarRs {

    @Inject
    private CalendarApi calendarApi;

    @Override
    public ActionStatus create(CalendarDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CalendarDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCalendarResponse find(String calendarCode) {
        GetCalendarResponse result = new GetCalendarResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCalendar(calendarApi.find(calendarCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ListCalendarResponse list() {
        ListCalendarResponse result = new ListCalendarResponse();
        CalendarsDto calendarsDto = new CalendarsDto();

        try {
            calendarsDto.setCalendar(calendarApi.list());
            result.setCalendars(calendarsDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String calendarCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.remove(calendarCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(CalendarDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}