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

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ProviderlessEntity;

/**
 * Data transformation Job
 */
@Entity
@ExportIdentifier("name")
@Table(name = "BI_JOB")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BI_JOB_SEQ")
public class Job extends ProviderlessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "NAME", unique = true, length = 50)
	@Size(max = 50)
	private String name;

	@Column(name = "LAST_EXECUTION_DATE")
	private Date lastExecutionDate;

	@Column(name = "NEXT_EXECUTION_DATE")
	private Date nextExecutionDate;

	@Type(type="numeric_boolean")
    @Column(name = "ACTIVE")
	private boolean active;

	@Column(name = "JOB_FREQUENCY")
	private Integer jobFrequencyId;

	@Column(name = "JOB_REPOSITORY_ID")
	private Integer jobRepositoryId;

	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((jobRepositoryId == null) ? 0 : jobRepositoryId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
