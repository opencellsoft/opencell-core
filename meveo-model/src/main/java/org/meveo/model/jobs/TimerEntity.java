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

import java.util.Date;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerHandle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "MEVEO_TIMER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_TIMER_SEQ")
public class TimerEntity extends BaseEntity {

	private static final long serialVersionUID = -3764934334462355788L;

	@Column(name = "NAME", unique = true, nullable = false)
	private String name;

	@Column(name = "JOB_NAME", nullable = false)
	private String jobName;

	@Column(name = "TIMER_HANDLE", nullable = false, length = 1000)
	private TimerHandle timerHandle;

	@JoinColumn(name = "FOLLOWING_TIMER_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private TimerEntity followingTimer;

	@Transient
	private String year = "*";

	@Transient
	private String month = "*";

	@Transient
	private String dayOfMonth = "*";

	@Transient
	private String dayOfWeek = "*";

	@Transient
	private String hour = "*";

	@Transient
	private String minute = "0";

	@Transient
	private String second = "0";

	@Transient
	private Date start;

	@Transient
	private Date end;

	@Transient
	private TimerInfo info = new TimerInfo();

	public String getName() {
		return (name == null) ? (getId() == null ? null : jobName + "_"
				+ getId()) : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		System.out.println("setJobName(" + jobName + ")");
		this.jobName = jobName;
	}

	public TimerHandle getTimerHandle() {
		return timerHandle;
	}

	public void setTimerHandle(TimerHandle timerHandle) {
		this.timerHandle = timerHandle;
	}

	public TimerEntity getFollowingTimer() {
		return followingTimer;
	}

	public void setFollowingTimer(TimerEntity followingTimer) {
		this.followingTimer = followingTimer;
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

	public TimerInfo getInfo() {
		return info;
	}

	public void setInfo(TimerInfo info) {
		this.info = info;
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

	public void setFieldsFromTimerHandler() {
		try {
			ScheduleExpression expression = timerHandle.getTimer()
					.getSchedule();
			setDayOfMonth(expression.getDayOfMonth());
			setDayOfWeek(expression.getDayOfWeek());
			setEnd(expression.getEnd());
			setHour(expression.getHour());
			setMinute(expression.getMinute());
			setMonth(expression.getMonth());
			setSecond(expression.getSecond());
			setStart(expression.getStart());
			setYear(expression.getYear());
			setInfo((TimerInfo) timerHandle.getTimer().getInfo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTimerSchedule() {
		String result = "";
		try {
			result = timerHandle.getTimer().getSchedule().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof TimerEntity) {
			if (this == other) {
				return true;
			}
			TimerEntity timer = (TimerEntity) other;
			if (timerHandle != null
					&& timerHandle.equals(timer.getTimerHandle())) {
				return true;
			}
		}
		return false;
	}

	public int hashcode() {
		int result = super.hashCode();
		if (timerHandle != null) {
			return timerHandle.hashCode();
		}
		return result;
	}
}
