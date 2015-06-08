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
package org.meveo.model.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.ScheduleExpression;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "MEVEO_TIMER", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_TIMER_SEQ")
public class TimerEntity extends BusinessEntity{

	private static final long serialVersionUID = -3764934334462355788L;

	@Column(name = "SC_YEAR", nullable = false)
	private String year = "*";

	@Column(name = "SC_MONTH", nullable = false)
	private String month = "*";

	@Column(name = "SC_D_O_MONTH", nullable = false)
	private String dayOfMonth = "*";

	@Column(name = "SC_D_O_WEEK", nullable = false)
	private String dayOfWeek = "*";

	@Column(name = "SC_HOUR", nullable = false)
	private String hour = "*";

	@Column(name = "SC_MIN", nullable = false)
	private String minute = "0";

	@Column(name = "SC_SEC", nullable = false)
	private String second = "0";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SC_START", nullable = true)
	private Date start;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SC_END", nullable = true)
	private Date end;
	
	@OneToMany(mappedBy = "timerEntity", fetch = FetchType.LAZY)
	private List<JobInstance> jobInstances = new ArrayList<JobInstance>();

	public TimerEntity(){

	}


	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
	

	/**
	 * @return the jobInstances
	 */
	public List<JobInstance> getJobInstances() {
		return jobInstances;
	}


	/**
	 * @param jobInstances the jobInstances to set
	 */
	public void setJobInstances(List<JobInstance> jobInstances) {
		this.jobInstances = jobInstances;
	}


	public ScheduleExpression getScheduleExpression() {
		ScheduleExpression expression = new ScheduleExpression();
		expression.dayOfMonth(dayOfMonth);
		expression.dayOfWeek(dayOfWeek);
		expression.end(end);
		expression.hour(hour);
		expression.minute(minute);
		expression.month(month);
		expression.second(second);
		expression.start(start);
		expression.year(year);
		return expression;
	}

	public String getTimerSchedule() {
		return String.format("Hour %s Minute %s Second %s Year %s Month %s Day of month %s Day of week %s", hour, minute, second, year, month, dayOfMonth, dayOfWeek);    
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof TimerEntity) {
			if (this == other) {
				return true;
			}
			TimerEntity timer = (TimerEntity) other;

			if (this.getId() == timer.getId()) {
				return true;
			}
			
			if (this.getCode() == timer.getCode()) {
				return true;
			}
			
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimerEntity [year=" + year + ", month=" + month
				+ ", dayOfMonth=" + dayOfMonth + ", dayOfWeek=" + dayOfWeek
				+ ", hour=" + hour + ", minute=" + minute + ", second="
				+ second + ", start=" + start + ", end=" + end
				+ "]";
	}
	
	
}