/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.catalog;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Length;
import org.meveo.commons.utils.DateUtils;
import org.meveo.model.AuditableEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "CAT_CALENDAR", uniqueConstraints = @UniqueConstraint(columnNames={"NAME", "PROVIDER_ID"}))
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_CALENDAR_SEQ")
public class Calendar extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", length = 20)
    @Length(max = 20)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CAT_CALENDAR_DAYS", joinColumns = @JoinColumn(name = "CALENDAR_ID"), inverseJoinColumns = @JoinColumn(name = "DAY_ID"))
    @BatchSize(size = 365)
    private List<DayInYear> days;

    @Enumerated(EnumType.STRING)
    @Column(name = "CALENDAR_TYPE", length = 20)
    private CalendarTypeEnum type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DayInYear> getDays() {
        return days;
    }

    public void setDays(List<DayInYear> days) {
        this.days = days;
    }

    public CalendarTypeEnum getType() {
        return type;
    }

    public void setType(CalendarTypeEnum type) {
        this.type = type;
    }

    /**
     * Checks for next calendar date. If not found in this year checks next
     * years dates. Calendar has list of days (month/day), so if calendar has at
     * least one date it will be found in this or next year. For example today
     * is 2010.12.06. Calendar has only one day - 12.05. So nextCalendarDate
     * will be found for 2011.12.05.
     * 
     * @param date
     *            Current date.
     * @return Next calendar date.
     */
    public Date nextCalendarDate(Date date) {
        Date future = DateUtils.newDate(3000, 0, 1, 0, 0, 0);
        Date result = future;
        int currentYear = DateUtils.getYearFromDate(date);
        for (DayInYear dayInYear : days) {
            Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
            if (d.after(date) && d.before(result)) {
                result = d;
            }
        }
        if (result == future) { // if result did not change
            currentYear++; // check for date in next year
            for (DayInYear dayInYear : days) {
                Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
                if (d.after(date) && d.before(result)) {
                    result = d;
                }
            }
        }
        if (result == future) {
            throw new IllegalStateException("Next calendar date could not be found!");
        }
        return result;
    }

    /**
     * Checks for previous calendar date. If not found in this year checks
     * previous years dates. Calendar has list of days (month/day), so if
     * calendar has at least one date it will be found in this or next year. For
     * example today is 2010.12.06. Calendar has only one day - 12.07. So
     * previousCalendarDate will be found for 2009.12.07.
     * 
     * @param date
     *            Current date.
     * @return Next calendar date.
     */
    public Date previousCalendarDate(Date date) {
        Date past = DateUtils.newDate(1970, 0, 1, 0, 0, 0);
        Date result = past;
        int currentYear = DateUtils.getYearFromDate(date);
        for (DayInYear dayInYear : days) {
            Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
            if (d.before(date) && d.after(result)) {
                result = d;
            }
        }
        if (result == past) { // if result did not change
            currentYear--; // check for date in previous year
            for (DayInYear dayInYear : days) {
                Date d = DateUtils.newDate(currentYear, dayInYear.getMonth().getId() - 1, dayInYear.getDay(), 0, 0, 0);
                if (d.before(date) && d.after(result)) {
                    result = d;
                }
            }
        }
        if (result == past) {
            throw new IllegalStateException("Previous calendar date could not be found!");
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;

        Calendar other = (Calendar) obj;
        if (other.getId() == getId())
            return true;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
