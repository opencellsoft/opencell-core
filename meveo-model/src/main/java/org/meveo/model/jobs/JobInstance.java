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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "JOB", cftCodeFields = "jobTemplate")
@ExportIdentifier({ "code", "provider" })
@Table(name = "MEVEO_JOB_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_JOB_INSTANCE_SEQ")
public class JobInstance extends BusinessCFEntity {

    private static final long serialVersionUID = -5517252645289726288L;

    @Column(name = "JOB_TEMPLATE", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String jobTemplate;

    @Column(name = "PARAMETRES", length = 255)
    @Size(max = 255)
    private String parametres;

    @Enumerated(EnumType.STRING)
    @Column(name = "JOB_CATEGORY")
    private JobCategoryEnum jobCategoryEnum;

    @OneToMany(mappedBy = "jobInstance", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JobExecutionResultImpl> executionResults = new ArrayList<JobExecutionResultImpl>();

    @JoinColumn(name = "TIMERENTITY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private TimerEntity timerEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWING_JOB_ID")
    private JobInstance followingJob;

    public JobInstance() {

    }

    /**
     * @return the jobTemplate
     */
    public String getJobTemplate() {
        return jobTemplate;
    }

    /**
     * @param jobTemplate the jobTemplate to set
     */
    public void setJobTemplate(String jobTemplate) {
        this.jobTemplate = jobTemplate;
    }

    /**
     * @return the parametres
     */
    public String getParametres() {
        return parametres;
    }

    /**
     * @param parametres the parametres to set
     */
    public void setParametres(String parametres) {
        this.parametres = parametres;
    }

    /**
     * @return the timerEntity
     */
    public TimerEntity getTimerEntity() {
        return timerEntity;
    }

    /**
     * @param timerEntity the timerEntity to set
     */
    public void setTimerEntity(TimerEntity timerEntity) {
        this.timerEntity = timerEntity;
    }

    /**
     * @return the followingJob
     */
    public JobInstance getFollowingJob() {
        return followingJob;
    }

    /**
     * @param followingJob the followingJob to set
     */
    public void setFollowingJob(JobInstance followingJob) {
        this.followingJob = followingJob;
    }

    public JobCategoryEnum getJobCategoryEnum() {
        return jobCategoryEnum;
    }

    public void setJobCategoryEnum(JobCategoryEnum jobCategoryEnum) {
        this.jobCategoryEnum = jobCategoryEnum;
    }

    public List<JobExecutionResultImpl> getExecutionResults() {
        return executionResults;
    }

    public void setExecutionResults(List<JobExecutionResultImpl> executionResults) {
        this.executionResults = executionResults;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof JobInstance) {
            if (this == other) {
                return true;
            }
            JobInstance job = (JobInstance) other;

            if (this.getId() == job.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("JobInstance [%s, jobTemplate=%s, parametres=%s, jobCategoryEnum=%s, timerEntity=%s,  followingJob=%s]", super.toString(), jobTemplate,
            parametres, jobCategoryEnum, timerEntity, followingJob);
    }
}