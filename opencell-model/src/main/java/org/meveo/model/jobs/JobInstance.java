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
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * The Class JobInstance.
 * 
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "JOB", cftCodeFields = "jobTemplate")
@ExportIdentifier({ "code" })
@Table(name = "meveo_job_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_job_instance_seq"), })
public class JobInstance extends EnableBusinessCFEntity {

    private static final long serialVersionUID = -5517252645289726288L;

    /**
     * The job template classname
     */
    @Column(name = "job_template", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String jobTemplate;

    /**
     * Execution parametres
     */
    @Column(name = "parametres", length = 255)
    @Size(max = 255)
    private String parametres;

    /**
     * Job category
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "job_category")
    private JobCategoryEnum jobCategoryEnum;

    /**
     * The execution results
     */
    @OneToMany(mappedBy = "jobInstance", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JobExecutionResultImpl> executionResults = new ArrayList<JobExecutionResultImpl>();

    /**
     * Job schedule
     */
    @JoinColumn(name = "timerentity_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TimerEntity timerEntity;

    /**
     * Following job to execute once job is completely finished
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_job_id")
    private JobInstance followingJob;

    /**
     * What cluster nodes job could/should run on. A comma separated list of custer nodes. A job can/will be run on any node if value is null.
     */
    @Column(name = "run_on_nodes", length = 255)
    @Size(max = 255)
    private String runOnNodes;

    /**
     * Can job be run in parallel on several cluster nodes. Value of True indicates that job can be run on a single node at a time.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "single_node", nullable = false)
    @NotNull
    private boolean limitToSingleNode = true;

    /** The include invoices without amount. */
    @Type(type = "numeric_boolean")
    @Column(name = "exclude_inv_without_amount")
    private Boolean excludeInvoicesWithoutAmount;

    /** Code of provider, that job belongs to. */
    @Transient
    private String providerCode;

    /** The run time values. */
    @Transient
    private Map<String, Object> runTimeValues;

    /**
     * Gets the job template.
     *
     * @return the jobTemplate
     */
    public String getJobTemplate() {
        return jobTemplate;
    }

    /**
     * Sets the job template.
     *
     * @param jobTemplate the jobTemplate to set
     */
    public void setJobTemplate(String jobTemplate) {
        this.jobTemplate = jobTemplate;
    }

    /**
     * Gets the parametres.
     *
     * @return the parametres
     */
    public String getParametres() {
        Object value = this.getParamValue("parameters");
        return value != null ? String.valueOf(value) : parametres;
    }

    /**
     * @return the parametres
     */
    public String getRunTimeParametres() {
        Object value = this.getParamValue("parameters");
        return value != null ? String.valueOf(value) : parametres;
    }

    /**
     * Sets the parametres.
     *
     * @param parametres the parametres to set
     */
    public void setParametres(String parametres) {
        this.parametres = parametres;
    }

    /**
     * Gets the timer entity.
     *
     * @return the timerEntity
     */
    public TimerEntity getTimerEntity() {
        return timerEntity;
    }

    /**
     * Sets the timer entity.
     *
     * @param timerEntity the timerEntity to set
     */
    public void setTimerEntity(TimerEntity timerEntity) {
        this.timerEntity = timerEntity;
    }

    /**
     * Gets the following job.
     *
     * @return the followingJob
     */
    public JobInstance getFollowingJob() {
        return followingJob;
    }

    /**
     * Sets the following job.
     *
     * @param followingJob the followingJob to set
     */
    public void setFollowingJob(JobInstance followingJob) {
        this.followingJob = followingJob;
    }

    /**
     * Gets the job category enum.
     *
     * @return the job category enum
     */
    public JobCategoryEnum getJobCategoryEnum() {
        return jobCategoryEnum;
    }

    /**
     * Sets the job category enum.
     *
     * @param jobCategoryEnum the new job category enum
     */
    public void setJobCategoryEnum(JobCategoryEnum jobCategoryEnum) {
        this.jobCategoryEnum = jobCategoryEnum;
    }

    /**
     * Gets the execution results.
     *
     * @return the execution results
     */
    public List<JobExecutionResultImpl> getExecutionResults() {
        return executionResults;
    }

    /**
     * Sets the execution results.
     *
     * @param executionResults the new execution results
     */
    public void setExecutionResults(List<JobExecutionResultImpl> executionResults) {
        this.executionResults = executionResults;
    }

    /**
     * Gets the run on nodes.
     *
     * @return the run on nodes
     */
    public String getRunOnNodes() {
        Object value = this.getParamValue("runOnNodes");
        return value != null ? String.valueOf(value) : runOnNodes;
    }

    /**
     * Sets the run on nodes.
     *
     * @param runOnNodes the new run on nodes
     */
    public void setRunOnNodes(String runOnNodes) {
        this.runOnNodes = runOnNodes;
    }

    /**
     * Checks if is limit to single node.
     *
     * @return true, if is limit to single node
     */
    public boolean isLimitToSingleNode() {
        return limitToSingleNode;
    }

    /**
     * Sets the limit to single node.
     *
     * @param limitToSingleNode the new limit to single node
     */
    public void setLimitToSingleNode(boolean limitToSingleNode) {
        this.limitToSingleNode = limitToSingleNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.model.BusinessEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof JobInstance)) {
            return false;
        }

        JobInstance other = (JobInstance) obj;

        if (this.getId() == other.getId()) {
            return true;
        }
        return false;
    }

    /**
     * Check if job instance is runnable on a current cluster node.
     *
     * @param currentNode Current cluster node
     * @return True if either current cluster node is unknown (non-clustered mode), runOnNodes is not specified or current cluster node matches any node in a list of nodes
     */
    public boolean isRunnableOnNode(String currentNode) {

        String runOnNodesValue = this.getRunOnNodes();

        if (currentNode == null || runOnNodesValue == null) {
            return true;
        }
        String[] nodes = runOnNodesValue.split(",");
        for (String node : nodes) {
            if (node.trim().equals(currentNode)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.model.BusinessEntity#toString()
     */
    @Override
    public String toString() {
        return String.format("JobInstance [%s, jobTemplate=%s, parametres=%s, jobCategoryEnum=%s, timerEntity=%s,  followingJob=%s]", super.toString(), jobTemplate, parametres,
            jobCategoryEnum, timerEntity, followingJob != null ? followingJob.getCode() : null);
    }

    /**
     * Gets the provider code.
     *
     * @return the provider code
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * Sets the provider code.
     *
     * @param providerCode the new provider code
     */
    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    /**
     * @return the excludeInvoicesWithoutAmount
     */
    public Boolean getExcludeInvoicesWithoutAmount() {
        return excludeInvoicesWithoutAmount;
    }

    /**
     * @param excludeInvoicesWithoutAmount the excludeInvoicesWithoutAmount to set
     */
    public void setExcludeInvoicesWithoutAmount(Boolean excludeInvoicesWithoutAmount) {
        this.excludeInvoicesWithoutAmount = excludeInvoicesWithoutAmount;
    }

    /**
     * @param runTimeValues the runTimeValues to set
     */
    public void setRunTimeValues(Map<String, Object> runTimeValues) {
        this.runTimeValues = runTimeValues;
    }

    /**
     * @return the runTimeValues
     */
    public Map<String, Object> getRunTimeValues() {
        return runTimeValues;
    }

    /**
     * Gets the runtime value.
     *
     * @param key the key
     * @return the runtime value
     */
    public Object getParamValue(String key) {
        if (this.runTimeValues == null) {
            return null;
        }
        return this.runTimeValues.get(key);
    }
}