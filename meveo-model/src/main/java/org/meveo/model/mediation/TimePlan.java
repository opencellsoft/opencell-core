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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.EnableEntity;

/**
 * Time Plan.
 */
@Entity
@Table(name = "MEDINA_TIME_PLAN")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_TIME_PLAN_SEQ")
public class TimePlan extends EnableEntity {

	private static final long serialVersionUID = 3308464990654625792L;

	// input
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "PRIORITY")
	private Integer priority;

	@Column(name = "DISCRIMINATOR_CODE", length = 50)
	private String discriminatorCode;

	@Enumerated(EnumType.STRING)
	TimePlanDaysEnum days;

	@Column(name = "TIME_FROM")
	Long from;

	@Column(name = "TIME_TO")
	Long to;

	// output
	@Column(name = "TIME_CODE")
	String timeCode;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getDiscriminatorCode() {
		return discriminatorCode;
	}

	public void setDiscriminatorCode(String discriminatorCode) {
		this.discriminatorCode = discriminatorCode;
	}

	public TimePlanDaysEnum getDays() {
		return days;
	}

	public void setDays(TimePlanDaysEnum days) {
		this.days = days;
	}

	public Long getFrom() {
		return from;
	}

	public void setFrom(Long from) {
		this.from = from;
	}

	public Long getTo() {
		return to;
	}

	public void setTo(Long to) {
		this.to = to;
	}

	public String getTimeCode() {
		return timeCode;
	}

	public void setTimeCode(String timeCode) {
		this.timeCode = timeCode;
	}

}
