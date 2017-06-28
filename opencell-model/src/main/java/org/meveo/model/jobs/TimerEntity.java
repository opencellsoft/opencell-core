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
package org.meveo.model.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

@Entity
@ModuleItem
@ExportIdentifier({ "code"})
@Table(name = "meveo_timer", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "meveo_timer_seq"), })
public class TimerEntity extends BusinessEntity{

	private static final long serialVersionUID = -3764934334462355788L;

	@Column(name = "sc_year", nullable = false, length = 255)
	@Size(max = 255)
	@NotNull
	private String year = "*";

	@Column(name = "sc_month", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
	private String month = "*";

	@Column(name = "sc_d_o_month", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
	private String dayOfMonth = "*";

	@Column(name = "sc_d_o_week", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
	private String dayOfWeek = "*";

	@Column(name = "sc_hour", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
	private String hour = "*";

	@Column(name = "sc_min", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
	private String minute = "0";

	@Column(name = "sc_sec", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
	private String second = "0";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sc_start", nullable = true)
	private Date start;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sc_end", nullable = true)
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

	public String getTimerSchedule() {
		return String.format("Hour %s Minute %s Second %s Year %s Month %s Day of month %s Day of week %s", hour, minute, second, year, month, dayOfMonth, dayOfWeek);    
	}

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TimerEntity)) {
            return false;
        }

        TimerEntity other = (TimerEntity) obj;

        if (this.getId() == other.getId()) {
            return true;
        }

        if (this.getCode() == other.getCode()) {
            return true;
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