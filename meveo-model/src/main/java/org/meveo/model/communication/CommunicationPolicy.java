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
package org.meveo.model.communication;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CommunicationPolicy {

	@Column(name="DELAY_MIN")
	private Long delayMinBetween2messages;
	
	@Column(name="NB_MAX_DAY")
	private Long NbMaxMessagePerDay;
	
	@Column(name="NB_MAX_WEEK")
	private Long NbMaxMessagePerWeek;
	
	@Column(name="NB_MAX_MONTH")
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
