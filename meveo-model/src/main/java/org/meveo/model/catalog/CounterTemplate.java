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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "CAT_COUNTER_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_COUNTER_TEMPLATE_SEQ")
public class CounterTemplate extends BusinessEntity {

	private static final long serialVersionUID = -1246995971618884001L;

	@Enumerated(EnumType.STRING)
	@Column(name = "COUNTER_TYPE")
	private CounterTypeEnum counterType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CALENDAR_ID")
	private Calendar calendar;

	@Column(name = "LEVEL_NUM", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	private BigDecimal level;

	@Column(name = "UNITY_DESCRIPTION", length = 20)
	@Size(min = 0, max = 20)
	private String unityDescription;

	@Enumerated(EnumType.STRING)
	@Column(name = "COUNTER_LEVEL")
	private CounterTemplateLevel counterLevel;

	public CounterTypeEnum getCounterType() {
		return counterType;
	}

	public void setCounterType(CounterTypeEnum counterType) {
		this.counterType = counterType;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public BigDecimal getLevel() {
		return level;
	}

	public void setLevel(BigDecimal level) {
		this.level = level;
	}

	public String getUnityDescription() {
		return unityDescription;
	}

	public void setUnityDescription(String unityDescription) {
		this.unityDescription = unityDescription;
	}

	public CounterTemplateLevel getCounterLevel() {
		return counterLevel;
	}

	public void setCounterLevel(CounterTemplateLevel counterLevel) {
		this.counterLevel = counterLevel;
	}

}
