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
package org.meveo.model.catalog;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Days in year entry for yearly calendar
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@ExportIdentifier({ "day", "month" })
@Table(name = "cat_day_in_year", uniqueConstraints = @UniqueConstraint(columnNames = { "day", "month" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_day_in_year_seq"), })
public class DayInYear extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Day in month
     */
    @Column(name = "day")
    private Integer day;

    /**
     * Month
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "month", length = 20)
    private MonthEnum month;

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public MonthEnum getMonth() {
        return month;
    }

    public void setMonth(MonthEnum month) {
        this.month = month;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = "DayInYear".hashCode();
        result = prime * result + ((day == null) ? 0 : day.hashCode());
        result = prime * result + ((month == null) ? 0 : month.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof DayInYear)) {
            return false;
        }

        DayInYear other = (DayInYear) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (day == null) {
            if (other.day != null)
                return false;
        } else if (!day.equals(other.day))
            return false;
        if (month == null) {
            if (other.month != null)
                return false;
        } else if (!month.equals(other.month))
            return false;
        return true;
    }

}
