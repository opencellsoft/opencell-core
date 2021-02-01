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
package org.meveo.model.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.JobCategoryEnumCoverter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * The Class JobInstance.
 * 
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "JobInstance", cftCodeFields = "jobTemplate")
@ExportIdentifier({ "code" })
@Table(name = "meveo_job_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "meveo_job_instance_seq"), })
@NamedQueries({ @NamedQuery(name = "JobInstance.listByTemplate", query = "SELECT ji FROM JobInstance ji where ji.jobTemplate=:jobTemplate order by ji.code") })
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
    @Convert(converter = JobCategoryEnumCoverter.class)
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
    private boolean excludeInvoicesWithoutAmount = false;

    /**
     * Whether a verbose error log will be kept.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "verbose_report")
    private boolean verboseReport = true;

    /**
     * Whether a verbose error log will be kept.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "stop_on_error")
    private boolean stopOnError = false;

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

        if (id != null && other.getId() != null && id.equals(other.getId())) {
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
        return String.format("JobInstance [%s, jobTemplate=%s, parametres=%s, jobCategoryEnum=%s, timerEntity=%s,  followingJob=%s]", super.toString(), jobTemplate, parametres, jobCategoryEnum, timerEntity,
            followingJob != null ? followingJob.getCode() : null);
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
    public boolean isExcludeInvoicesWithoutAmount() {
        return excludeInvoicesWithoutAmount;
    }

    /**
     * @param excludeInvoicesWithoutAmount the excludeInvoicesWithoutAmount to set
     */
    public void setExcludeInvoicesWithoutAmount(boolean excludeInvoicesWithoutAmount) {
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

    /**
     * Are error logs recorded?
     * 
     * @return boolean value
     */
    public boolean isVerboseReport() {
        return verboseReport;
    }

    /**
     * Sets whether error logs are recorded
     * 
     * @param verboseReport boolean value
     */
    public void setVerboseReport(boolean verboseReport) {
        this.verboseReport = verboseReport;
    }

    /**
     * @return the stopOnError
     */
    public boolean isStopOnError() {
        return stopOnError;
    }

    /**
     * @param stopOnError the stopOnError to set
     */
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }
}