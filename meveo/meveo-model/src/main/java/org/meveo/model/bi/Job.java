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

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.meveo.model.ProviderlessEntity;

/**
 * Data transformation Job
 * 
 * @author Gediminas Ubartas
 * @created 2010.09.22
 */
@Entity
@Table(name = "BI_JOB")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BI_JOB_SEQ")
public class Job extends ProviderlessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", unique = true, length = 50)
    private String name;

    @Column(name = "LAST_EXECUTION_DATE")
    private Date lastExecutionDate;

    @Column(name = "NEXT_EXECUTION_DATE")
    private Date nextExecutionDate;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "JOB_FREQUENCY")
    private Integer jobFrequencyId;

    @Column(name = "JOB_REPOSITORY_ID")
    private Integer jobRepositoryId;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
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
