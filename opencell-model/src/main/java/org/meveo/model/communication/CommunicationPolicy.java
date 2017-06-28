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
package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommunicationPolicy {

	@Column(name="delay_min")
	private Long delayMinBetween2messages;
	
	@Column(name="nb_max_day")
	private Long NbMaxMessagePerDay;
	
	@Column(name="nb_max_week")
	private Long NbMaxMessagePerWeek;
	
	@Column(name="nb_max_month")
	private Long NbMaxMessagePerMonth;

	public Long getDelayMinBetween2messages() {
		return delayMinBetween2messages;
	}

	public void setDelayMinBetween2messages(Long delayMinBetween2messages) {
		this.delayMinBetween2messages = delayMinBetween2messages;
	}

	public Long getNbMaxMessagePerDay() {
		return NbMaxMessagePerDay;
	}

	public void setNbMaxMessagePerDay(Long nbMaxMessagePerDay) {
		NbMaxMessagePerDay = nbMaxMessagePerDay;
	}

	public Long getNbMaxMessagePerWeek() {
		return NbMaxMessagePerWeek;
	}

	public void setNbMaxMessagePerWeek(Long nbMaxMessagePerWeek) {
		NbMaxMessagePerWeek = nbMaxMessagePerWeek;
	}

	public Long getNbMaxMessagePerMonth() {
		return NbMaxMessagePerMonth;
	}

	public void setNbMaxMessagePerMonth(Long nbMaxMessagePerMonth) {
		NbMaxMessagePerMonth = nbMaxMessagePerMonth;
	}
	
	
}
