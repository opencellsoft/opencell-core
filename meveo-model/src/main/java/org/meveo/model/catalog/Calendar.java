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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "CAT_CALENDAR", uniqueConstraints = @UniqueConstraint(columnNames = { "NAME",
		"PROVIDER_ID" }))
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CAL_TYPE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_CALENDAR_SEQ")
public abstract class Calendar extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "NAME", length = 20)
	@Size(max = 20)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "CALENDAR_TYPE", length = 20)
	private CalendarTypeEnum type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CalendarTypeEnum getType() {
		return type;
	}

	public void setType(CalendarTypeEnum type) {
		this.type = type;
	}

	/**
	 * @param date
	 *            Current date.
	 * @return Next calendar date.
	 */
	public abstract Date nextCalendarDate(Date date);
	
	/**
	 * @param date
	 *            Current date.
	 * @return Next calendar date.
	 */
	public abstract Date previousCalendarDate(Date date);


	/**
	 * 
	 * @return true if the dates used in meveo should have time set to 00:00:00 with this calendar
	 */
	public abstract boolean truncDateTime();
	
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
