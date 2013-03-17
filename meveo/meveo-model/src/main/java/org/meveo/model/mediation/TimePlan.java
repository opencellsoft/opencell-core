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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.EnableEntity;

/**
 * Time Plan.
 * 
 * @author seb
 * @created Aug 6, 2012
 */
@Entity
@Table(name="MEDINA_TIME_PLAN")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_TIME_PLAN_SEQ")
public class TimePlan extends EnableEntity {
	
	private static final long serialVersionUID = 3308464990654625792L;

	//input
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	
	@Column(name="PRIORITY")
	private Integer priority;
	
	@Column(name = "DISCRIMINATOR_CODE", length = 50)
    private String discriminatorCode;
    
    @Enumerated(EnumType.STRING)
	TimePlanDaysEnum days;

	@Column(name="TIME_FROM")
	Long from;

	@Column(name="TIME_TO")
	Long to;
	
	//output
	@Column(name="TIME_CODE")
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
