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
package org.meveo.model.bi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;
import org.meveo.model.crm.Email;

/**
 * Report entity.
 * 
 * @author Gediminas Ubartas
 * @created 2009.10.05
 */
@Entity
@Table(name = "BI_REPORT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BI_REPORT_SEQ")
public class Report extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "DESCRIPTION", nullable = true, length = 50)
    @Size(max = 50)
    protected String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BI_REPORT_EMAILS", joinColumns = @JoinColumn(name = "REPORT_ID"), inverseJoinColumns = @JoinColumn(name = "EMAIL_ID"))
    private List<Email> emails;

    @Column(name = "SCHEDULE")
    private Date schedule;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "REPORT_FILE_NAME")
    private String fileName;

    @Column(name = "PRODUCER_CLASS_NAME")
    private String producerClassName;

    @Column(name = "DS_RECORD_PATH")
    private String recordPath;

    @Column(name = "REPORT_FREQUENCY", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionFrequencyEnum frequency;

    @Column(name = "EXECUTION_HOUR")
    private Integer executionHour;

    @Column(name = "EXECUTION_MINUTES")
    private Integer executionMinutes;

    @Column(name = "EXECUTION_INTERVAL_MINUTES")
    private Integer executionIntervalMinutes;

    @Column(name = "EXECUTION_INTERVAL_SECONDS")
    private Integer executionIntervalSeconds;

    @Column(name = "EXECUTION_DAY_OF_WEEK")
    private Integer executionDayOfWeek;

    @Column(name = "EXECUTION_DAY_OF_MONTH")
    private Integer executionDayOfMonth;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ACTION_NAME")
    private JobNameEnum actionName;

    @Column(name = "OUTPUT_FORMAT", nullable = false)
    @Enumerated(EnumType.STRING)
    private OutputFormatEnum outputFormat;

    public void computeNextExecutionDate() {
        Calendar calendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        switch (frequency) {
        case INTERVAL:
            int delay = executionIntervalMinutes * 60 + executionIntervalSeconds;
            startDate.setTime(startDate.getTime() + delay);
            endDate.setTime(endDate.getTime() + delay);
            schedule.setTime(schedule.getTime() + delay);
            break;
        case DAILY:
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (executionHour != null) {
                calendar.set(Calendar.HOUR_OF_DAY, executionHour);
            }
            if (executionMinutes != null) {
                calendar.set(Calendar.MINUTE, executionMinutes);
            }
            schedule = calendar.getTime();
            startCalendar.setTime(new Date(schedule.getTime()));
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);
            startDate = startCalendar.getTime();
            endCalendar.setTime(new Date(startDate.getTime()));
            endCalendar.add(Calendar.DAY_OF_MONTH, 1);
            endDate = new Date(endCalendar.getTime().getTime() - 1);
            break;
        case WEEKLY:
            if (executionDayOfWeek != null) {
                calendar.set(Calendar.DAY_OF_WEEK, executionDayOfWeek);
            }
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, executionHour);
            calendar.set(Calendar.MINUTE, executionMinutes);
            calendar.set(Calendar.SECOND, 0);
            schedule = calendar.getTime();
            startCalendar.setTime(new Date(endDate.getTime() + 1000L * 3600 * 24));
            startCalendar.set(Calendar.DAY_OF_WEEK, 0);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);
            endCalendar.setTime(new Date(startDate.getTime()));
            endCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            endDate = new Date(endCalendar.getTime().getTime() - 1);
            break;
        case MONTHLY:
            startCalendar.setTime(new Date(endDate.getTime() + 1000L * 3600 * 24));
            startCalendar.set(Calendar.DAY_OF_MONTH, 1);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);
            startDate = startCalendar.getTime();
            endCalendar.setTime(new Date(startDate.getTime()));
            endCalendar.add(Calendar.MONTH, 1);
            endDate = new Date(endCalendar.getTime().getTime() - 1);
            calendar.setTime(new Date(getEndDate().getTime() + 1000L * 3600 * 24 * 3));
            calendar.set(Calendar.DAY_OF_MONTH, executionDayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, executionHour);
            calendar.set(Calendar.MINUTE, executionMinutes);
            calendar.set(Calendar.SECOND, 0);
            schedule = calendar.getTime();
            break;
        default:
            break;
        }
    }

    public ExecutionFrequencyEnum getFrequency() {
        return frequency;
    }

    public void setFrequency(ExecutionFrequencyEnum frequency) {
        this.frequency = frequency;
    }

    public Integer getExecutionHour() {
        return executionHour;
    }

    public void setExecutionHour(Integer executionHour) {
        this.executionHour = executionHour;
    }

    public Integer getExecutionMinutes() {
        return executionMinutes;
    }

    public void setExecutionMinutes(Integer executionMinutes) {
        this.executionMinutes = executionMinutes;
    }

    public Integer getExecutionIntervalMinutes() {
        return executionIntervalMinutes;
    }

    public void setExecutionIntervalMinutes(Integer executionIntervalMinutes) {
        this.executionIntervalMinutes = executionIntervalMinutes;
    }

    public Integer getExecutionIntervalSeconds() {
        return executionIntervalSeconds;
    }

    public void setExecutionIntervalSeconds(Integer executionIntervalSeconds) {
        this.executionIntervalSeconds = executionIntervalSeconds;
    }

    public Integer getExecutionDayOfWeek() {
        return executionDayOfWeek;
    }

    public void setExecutionDayOfWeek(Integer executionDayOfWeek) {
        this.executionDayOfWeek = executionDayOfWeek;
    }

    public Integer getExecutionDayOfMonth() {
        return executionDayOfMonth;
    }

    public void setExecutionDayOfMonth(Integer executionDayOfMonth) {
        this.executionDayOfMonth = executionDayOfMonth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getProducerClassName() {
        return producerClassName;
    }

    public void setProducerClassName(String producerClassName) {
        this.producerClassName = producerClassName;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public JobNameEnum getActionName() {
        return actionName;
    }

    public void setActionName(JobNameEnum actionName) {
        this.actionName = actionName;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OutputFormatEnum getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormatEnum outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Report other = (Report) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
