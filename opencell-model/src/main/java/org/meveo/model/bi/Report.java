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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.crm.Email;

/**
 * Report entity.
 */
@Entity
@ExportIdentifier({ "name" })
@Table(name = "bi_report")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "bi_report_seq"), })
public class Report extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 50)
    @Size(max = 50)
    private String name;

    @Column(name = "description", nullable = true, length = 255)
    @Size(max = 255)
    protected String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bi_report_emails", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "email_id"))
    private List<Email> emails;

    @Column(name = "schedule")
    private Date schedule;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "report_file_name", length = 255)
    @Size(max = 255)
    private String fileName;

    @Column(name = "producer_class_name", length = 255)
    @Size(max = 255)
    private String producerClassName;

    @Column(name = "ds_record_path", length = 255)
    @Size(max = 255)
    private String recordPath;

    @Column(name = "report_frequency", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ExecutionFrequencyEnum frequency;

    @Column(name = "execution_hour")
    private Integer executionHour;

    @Column(name = "execution_minutes")
    private Integer executionMinutes;

    @Column(name = "execution_interval_minutes")
    private Integer executionIntervalMinutes;

    @Column(name = "execution_interval_seconds")
    private Integer executionIntervalSeconds;

    @Column(name = "execution_day_of_week")
    private Integer executionDayOfWeek;

    @Column(name = "execution_day_of_month")
    private Integer executionDayOfMonth;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "action_name")
    private JobNameEnum actionName;

    @Column(name = "output_format", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
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
        return 961 + (("Report" + (name == null ? "" : name)).hashCode());
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Report)) {
            return false;
        }

        Report other = (Report) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
