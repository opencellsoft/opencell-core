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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.EnableEntity;

/**
 * Zonning Plan.
 */
@Entity
@Table(name = "MEDINA_ZONNING_PLAN")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_ZONNING_PLAN_SEQ")
public class ZonningPlan extends EnableEntity {

	private static final long serialVersionUID = 1L;

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

	@Column(name = "ORIGIN_PREFIX", length = 50)
	private String originPrefix;

	// output
	@Column(name = "ZONE_CODE", length = 50)
	private String zoneCode;

	// output
	@Column(name = "SUB_ZONE_CODE", length = 50)
	private String subZoneCode;

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

	public String getOriginPrefix() {
		return originPrefix;
	}

	public void setOriginPrefix(String originPrefix) {
		this.originPrefix = originPrefix;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public String getSubZoneCode() {
		return subZoneCode;
	}

	public void setSubZoneCode(String subZoneCode) {
		this.subZoneCode = subZoneCode;
	}

}
