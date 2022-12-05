/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.bi;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Data transformation Job
 */
@Entity
@ExportIdentifier("name")
@Table(name = "bi_job")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "bi_job_seq"), })
public class Job extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Job name
     */
    @Column(name = "name", unique = true, length = 50)
    @Size(max = 50)
    private String name;

    /**
     * Last execution timestamp
     */
    @Column(name = "last_execution_date")
    private Date lastExecutionDate;

    /**
     * Next execution date
     */
    @Column(name = "next_execution_date")
    private Date nextExecutionDate;

    /**
     * Is job enabled
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "active")
    private boolean active;

    /**
     * Job frequency identifier
     */
    @Column(name = "job_frequency")
    private Integer jobFrequencyId;

    /**
     * Job repository identifier
     */
    @Column(name = "job_repository_id")
    private Integer jobRepositoryId;

    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)

    /**
     * Job execution histories
     */
    @OneToMany(mappedBy = "job", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JobExecutionHisto> jobHistory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(Date nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getJobFrequencyId() {
        return jobFrequencyId;
    }

    public void setFrequencyId(Integer jobFrequencyId) {
        this.jobFrequencyId = jobFrequencyId;
    }

    public ExecutionFrequencyEnum getFrequency() {
        return ExecutionFrequencyEnum.getValue(jobFrequencyId);
    }

    public void setFrequency(ExecutionFrequencyEnum status) {
        this.jobFrequencyId = status.getId();
    }

    public Integer getJobRepositoryId() {
        return jobRepositoryId;
    }

    public void setJobRepositoryId(Integer jobRepositoryId) {
        this.jobRepositoryId = jobRepositoryId;
    }

    public List<JobExecutionHisto> getJobHistory() {
        return jobHistory;
    }

    public void setJobHistory(List<JobExecutionHisto> jobHistory) {
        this.jobHistory = jobHistory;
    }

    @Override
    public int hashCode() {
        int result = 961 + (("BiJob" + (jobRepositoryId == null ? "" : jobRepositoryId)).hashCode());
        result = 31 * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Job)) {
            return false;
        }

        Job other = (Job) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (jobRepositoryId == null) {
            if (other.jobRepositoryId != null)
                return false;
        } else if (!jobRepositoryId.equals(other.jobRepositoryId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
