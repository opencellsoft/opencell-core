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
package org.meveo.model.jobs;

import java.util.Date;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerHandle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "MEVEO_TIMER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_TIMER_SEQ")
public class TimerEntity extends BaseEntity {

	private static final long serialVersionUID = -3764934334462355788L;

	@Column(name = "JOB_NAME")
	private String jobName;

	@Column(name = "TIMER_HANDLE")
	private TimerHandle timerHandle;

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
			ScheduleExpression expression = timerHandle.getTimer().getSchedule();
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
}
