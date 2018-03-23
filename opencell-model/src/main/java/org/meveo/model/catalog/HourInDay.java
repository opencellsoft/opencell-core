/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@Cacheable
@ExportIdentifier({ "hour", "minute" })
@Table(name = "cat_hour_in_day", uniqueConstraints = @UniqueConstraint(columnNames = { "hour", "min" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_hour_in_day_seq"), })
public class HourInDay extends BaseEntity implements Comparable<HourInDay> {

    private static final long serialVersionUID = 1L;

    public HourInDay() {
        super();
    }

    public HourInDay(Integer hour, Integer minute) {
        super();
        this.hour = hour;
        this.minute = minute;
    }

    @Column(name = "hour")
    private Integer hour = 0;

    @Column(name = "min")
    private Integer minute = 0;

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = "HourInDay".hashCode();
        result = prime * result + ((hour == null) ? 0 : hour.hashCode());
        result = prime * result + ((minute == null) ? 0 : minute.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof HourInDay)) {
            return false;
        }

        HourInDay other = (HourInDay) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (hour == null) {
            if (other.hour != null) {
                return false;
            }
        } else if (!hour.equals(other.hour)) {
            return false;
        }
        if (minute == null) {
            if (other.minute != null) {
                return false;
            }
        } else if (!minute.equals(other.minute)) {
            return false;
        }
        return true;
    }

    public String getHourAsString() {
        return (hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute;
    }

    @Override
    public String toString() {
        return getHourAsString();
    }

    @Override
    public int compareTo(HourInDay other) {
        return getHourAsString().compareTo(other.getHourAsString());
    }
}
