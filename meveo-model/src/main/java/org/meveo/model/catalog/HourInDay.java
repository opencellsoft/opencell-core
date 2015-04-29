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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseProviderlessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "hour", "minute" })
@Table(name = "CAT_HOUR_IN_DAY", uniqueConstraints = @UniqueConstraint(columnNames = { "HOUR", "MIN" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_HOUR_IN_DAY_SEQ")
public class HourInDay extends BaseProviderlessEntity implements Comparable<HourInDay> {

    private static final long serialVersionUID = 1L;

    public HourInDay() {
        super();
    }

    public HourInDay(Integer hour, Integer minute) {
        super();
        this.hour = hour;
        this.minute = minute;
    }

    @Column(name = "HOUR")
    private Integer hour = 0;

    @Column(name = "MIN")
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
        int result = super.hashCode();
        result = prime * result + ((hour == null) ? 0 : hour.hashCode());
        result = prime * result + ((minute == null) ? 0 : minute.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HourInDay other = (HourInDay) obj;
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
