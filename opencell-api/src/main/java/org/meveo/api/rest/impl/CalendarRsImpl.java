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

package org.meveo.api.rest.impl;

import org.meveo.api.CalendarApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.CalendarsDto;
import org.meveo.api.dto.response.BankingDateStatusResponse;
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.CalendarRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Date;

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
    public ListCalendarResponse listGetAll() {

        ListCalendarResponse result = new ListCalendarResponse();

        try {
            result = calendarApi.list(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
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

    @Override
    public BankingDateStatusResponse getBankingDateStatus(Date date) {
        
        BankingDateStatusResponse result = new BankingDateStatusResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
            result.setBankingDateStatus(calendarApi.getBankingDateStatus(date));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}