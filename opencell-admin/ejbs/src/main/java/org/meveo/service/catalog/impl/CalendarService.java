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
package org.meveo.service.catalog.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.Calendar;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

/**
 * Calendar service implementation.
 */
@Stateless
@Named
public class CalendarService extends PersistenceService<Calendar> {

    public Calendar findByCode(String code) {
        QueryBuilder qb = new QueryBuilder(Calendar.class, "c", null);
        qb.addCriterion("code", "=", code, true);

        try {
            return (Calendar) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Initialize calendar with a starting date.<br/>
     * If date, to initialize the calendar with, was not specified as EL expression, a default initialize date will be applied
     * 
     * @param calendar Calendar to initialize
     * @param defaultInitDate Default date to initialize with
     * @return Calendar initialized with a starting date
     */
    public static Calendar initializeCalendar(Calendar calendar, Date defaultInitDate, Object... elParameters) {

        if (calendar == null) {
            return null;
        }
        if (!calendar.isInitializationRequired()) {
            return calendar;
        }

        Date initDate = null;
        if (calendar.getInitDateEL() != null) {
            initDate = ValueExpressionWrapper.evaluateExpression(calendar.getInitDateEL(), Date.class, elParameters);
        }
        if (initDate == null) {
            initDate = defaultInitDate;
        }
        calendar.setInitDate(initDate);

        return calendar;
    }

}