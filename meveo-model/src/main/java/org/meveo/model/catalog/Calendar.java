/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "CAT_CALENDAR", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CAL_TYPE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_CALENDAR_SEQ")
public abstract class Calendar extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "CAL_TYPE", insertable = false, updatable = false)
    private String calendarType;

    @Transient
    private Date initDate;
    
    /**
     * Get the period end date for a given date
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public abstract Date nextCalendarDate(Date date);

    /**
     * Get the period start date for a given date
     * 
     * @param date Current date.
     * @return Next calendar date.
     */
    public abstract Date previousCalendarDate(Date date);

    /**
     * Get the previous period end date
     * 
     * @param date Current date
     * @return The previous period end date
     */
    public abstract Date previousPeriodEndDate(Date date);

    /**
     * Get the next period start date
     * 
     * @param date Current date
     * @return The next period start date
     */
    public abstract Date nextPeriodStartDate(Date date);

    /**
     * 
     * @return true if the dates used in meveo should have time set to 00:00:00 with this calendar
     */
    public boolean truncDateTime() {
        return true;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date startDate) {
        this.initDate = startDate;
    }
}