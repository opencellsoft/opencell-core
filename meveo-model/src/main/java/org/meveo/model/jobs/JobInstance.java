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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;

@Entity
@CustomFieldEntity(accountLevel = AccountLevelEnum.TIMER)
@ExportIdentifier({ "code", "provider" })
@Table(name = "MEVEO_JOB_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_JOB_INSTANCE_SEQ")
public class JobInstance extends BusinessCFEntity {

    private static final long serialVersionUID = -5517252645289726288L;

    @Column(name = "JOB_TEMPLATE", nullable = false)
    private String jobTemplate;

    @Column(name = "PARAMETRES", nullable = true)
    private String parametres;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "JOB_CATEGORY")
    JobCategoryEnum jobCategoryEnum;

    @OneToMany(mappedBy = "jobInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKeyColumn(name = "code")
    private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

    @OneToMany(mappedBy = "jobInstance", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JobExecutionResultImpl> executionResults = new ArrayList<JobExecutionResultImpl>();

    @ManyToOne(fetch = FetchType.LAZY)
    private TimerEntity timerEntity;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Transient
    private boolean running;

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
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
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

    public Map<String, CustomFieldInstance> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
        this.customFields = customFields;
    }

     public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JobInstance [jobTemplate=" + jobTemplate + ", parametres=" + parametres + ", active=" + active + ", jobCategoryEnum=" + jobCategoryEnum + ", customFields="
                + "customFields" + ", timerEntity=" + "timerEntity" + ", running=" + running + ", followingJobs=" + "followingJobs" + "]";
    }

    @Override
    public ICustomFieldEntity getParentCFEntity() {
        return null;
    }
}