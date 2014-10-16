/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.model.bi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "BI_JOB_HISTORY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "HISTORY_TYPE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BI_JOB_HISTORY_SEQ")
public class JobHistory extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "HISTORY_TYPE", insertable = false, updatable = false)
	private String type;

	@Column(name = "EXECUTION_DATE")
	private Date executionDate;

	@Column(name = "LINES_READ")
	private Integer linesRead;

	@Column(name = "LINES_INSERTED")
	private Integer linesInserted;

	@Column(name = "LINES_REJECTED")
	private Integer linesRejected;

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public Integer getLinesRead() {
		return linesRead;
	}

	public void setLinesRead(Integer linesRead) {
		this.linesRead = linesRead;
	}

	public Integer getLinesInserted() {
		return linesInserted;
	}

	public void setLinesInserted(Integer linesInserted) {
		this.linesInserted = linesInserted;
	}

	public Integer getLinesRejected() {
		return linesRejected;
	}

	public void setLinesRejected(Integer linesRejected) {
		this.linesRejected = linesRejected;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((executionDate == null) ? 0 : executionDate.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobHistory other = (JobHistory) obj;
		if (executionDate == null) {
			if (other.executionDate != null)
				return false;
		} else if (!executionDate.equals(other.executionDate))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
