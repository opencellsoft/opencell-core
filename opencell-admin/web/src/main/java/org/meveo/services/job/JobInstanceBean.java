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

package org.meveo.services.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobExecutionStatus;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.EnumBuilder;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionError;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.service.base.IEntityService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionErrorService;
import org.meveo.service.job.JobExecutionResultService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.util.view.ServiceBasedLazyDataModel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author Edward P. Legaspi
 * @author phung
 * @lastModifiedVersion 5.2
 */
@Named
@ViewScoped
public class JobInstanceBean extends CustomFieldBean<JobInstance> {

    private static final long serialVersionUID = 1L;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobExecutionResultService jobExecutionResultService;

    @Inject
    private JobExecutionErrorService jobExecutionErrorService;

    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    @Inject
    private IEntityService iEntityService;

    private TreeNode executionHistoryRoot;

    private ServiceBasedLazyDataModel<JobExecutionError> executionErrorDM = null;

    private Class entityClassForErrorLog;

    public JobInstanceBean() {
        super(JobInstance.class);
    }

    @Override
    public JobInstance initEntity() {
        super.initEntity();

        try {
            refreshCustomFieldsAndActions();
        } catch (BusinessException e) {
        }

        return entity;
    }

    @Override
    protected IPersistenceService<JobInstance> getPersistenceService() {
        return jobInstanceService;
    }

    public List<JobCategoryEnum> getJobCategoryEnumValues() {
        List<Object> categories = EnumBuilder.values(JobCategoryEnum.class);
        if (categories != null) {
            return categories.stream().map(cat -> (JobCategoryEnum) cat).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * Get a list of jobs suitable as a next job to execute (all jobs, minus a current one)
     * 
     * @return A list of jobs minus a current one
     */
    public List<JobInstance> getFollowingJobList() {
        List<JobInstance> jobs = jobInstanceService.list();
        jobs.remove(entity);
        return jobs;
    }

    public List<String> getJobNames() {
        if (entity.getJobCategoryEnum() == null) {
            return null;
        }
        return jobInstanceService.getJobNames(entity.getJobCategoryEnum());
    }

    @ActionMethod
    public String execute() {

        jobExecutionService.executeJob(entity, null, JobLauncherEnum.GUI);
        messages.info(new BundleKey("messages", "jobInstance.job.laucnhed"), entity.getJobTemplate());

        return getEditViewName();
    }

    @ActionMethod
    public String stop() {

        jobExecutionService.stopJob(entity);
        messages.info(new BundleKey("messages", "jobInstance.job.requestedToStop"), entity.getJobTemplate());

        return getEditViewName();
    }

    @ActionMethod
    public String stopByForce() {

        jobExecutionService.stopJobByForce(entity);
        messages.info(new BundleKey("messages", "jobInstance.job.requestedToStopByForce"), entity.getJobTemplate());

        return getEditViewName();
    }

    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return getEditViewName();
    }

    /**
     * Get JobInstance name from a jobId
     * 
     * @param jobId job identifier
     * @return timer name
     */
    public String translateToTimerName(Long jobId) {
        if (jobId != null) {
            JobInstance jobInstance = jobInstanceService.findById(jobId);
            if (jobInstance != null) {
                return jobInstance.getCode();
            }
        }
        return null;
    }

    /**
     * Synchronize definition of custom field templates specified in Job class to those found in DB. Register in DB if was missing.
     */
    private void createMissingCustomFieldTemplates() {

        if (entity.getJobTemplate() == null) {
            return;
        }

        // Get job definition and custom field templates defined in a job
        Job job = jobInstanceService.getJobByName(entity.getJobTemplate());
        Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();

        // Create missing custom field templates if needed
        Collection<CustomFieldTemplate> jobTemplatesFromJob = null;
        if (jobCustomFields == null) {
            jobTemplatesFromJob = new ArrayList<CustomFieldTemplate>();
        } else {
            jobTemplatesFromJob = jobCustomFields.values();
        }

        try {
            customFieldTemplateService.createMissingTemplates((ICustomFieldEntity) entity, jobTemplatesFromJob);
        } catch (BusinessException e) {
            log.error("Failed to create missing custom field templates", e);
        }
    }

    /**
     * Check if a job is running on this node
     * 
     * @param jobInstance JobInstance entity
     * @return True if job is running on this node
     */
    public boolean isJobRunningOnThisNode(JobInstance jobInstance) {
        JobRunningStatusEnum status = jobCacheContainerProvider.isJobRunning(jobInstance.getId());
        return status == JobRunningStatusEnum.LOCKED_THIS || status == JobRunningStatusEnum.REQUEST_TO_STOP || status == JobRunningStatusEnum.RUNNING_THIS;
    }

    /**
     * Check if a job is running on another node
     * 
     * @param jobInstance JobInstance entity
     * @return True if job is running on another node
     */
    public boolean isJobRunningOnAnotherNode(JobInstance jobInstance) {
        JobRunningStatusEnum status = jobCacheContainerProvider.isJobRunning(jobInstance.getId());
        return status == JobRunningStatusEnum.LOCKED_OTHER || status == JobRunningStatusEnum.REQUEST_TO_STOP || status == JobRunningStatusEnum.RUNNING_OTHER;
    }

    /**
     * Check if a job is running on any node
     * 
     * @param jobInstance JobInstance entity
     * @return True if job is running on any node
     */
    public boolean isJobRunning(JobInstance jobInstance) {
        JobRunningStatusEnum status = jobCacheContainerProvider.isJobRunning(jobInstance.getId());
        return status != JobRunningStatusEnum.NOT_RUNNING;
    }

    /**
     * Check if a job was requested to stop
     * 
     * @param jobInstance JobInstance entity
     * @return True if job was requested to stop
     */
    public boolean isJobPausing(JobInstance jobInstance) {
        JobRunningStatusEnum status = jobCacheContainerProvider.isJobRunning(jobInstance.getId());
        return status == JobRunningStatusEnum.REQUEST_TO_STOP;
    }

    /**
     * Check if job can be run on a current server or cluster node if deployed in cluster environment
     * 
     * @param jobInstance JobInstance entity
     * @return True if it can be executed locally
     */
    public boolean isAllowedToExecute(JobInstance jobInstance) {
        return jobExecutionService.isAllowedToExecute(jobInstance);
    }

    /**
     * Return job execution status details from cache
     * 
     * @return Job execution status
     */
    public JobExecutionStatus getJobExecutionStatusDetails() {
        return jobCacheContainerProvider.getJobStatus(entity.getId());
    }

    /**
     * Return job execution status from cache
     * 
     * @return Job execution status
     */
    public JobRunningStatusEnum getJobExecutionStatus() {
        return jobCacheContainerProvider.isJobRunning(entity.getId());
    }

    /**
     * Explicitly refresh custom fields and action definitions. Should be used when job template change, as on it depends what fields and actions apply
     * 
     * @throws BusinessException General business exception
     */
    public void refreshCustomFieldsAndActions() throws BusinessException {

        createMissingCustomFieldTemplates();
        customFieldDataEntryBean.refreshFieldsAndActions(entity);
    }

    @Override
    @ActionMethod
    public void enable() {
        super.enable();
        initEntity();
    }

    @Override
    @ActionMethod
    public void disable() {
        super.disable();
        initEntity();
    }

    /**
     * Get execution error for other job types
     *
     * @return Job execution error lazy data model
     */
    public ServiceBasedLazyDataModel<JobExecutionError> getJobExecutionErrors() {

        if (executionErrorDM == null) {

            executionErrorDM = new ServiceBasedLazyDataModel<JobExecutionError>() {

                private static final long serialVersionUID = 87900L;

                @Override
                protected Map<String, Object> getSearchCriteria() {

                    Map<String, Object> filters = new HashMap<>();
                    filters.put("jobInstance", entity);
                    return filters;
                }

                @Override
                protected String getDefaultSortImpl() {
                    return "id";
                }

                @Override
                protected IPersistenceService<JobExecutionError> getPersistenceServiceImpl() {
                    return jobExecutionErrorService;
                }

                @Override
                protected List<JobExecutionError> loadData(PaginationConfiguration paginationConfig) {

                    List<JobExecutionError> errorList = super.loadData(paginationConfig);

                    Class entityClassForErrorLog = getTargetEntityClassForErrorLog();
                    iEntityService.setEntityClass(entityClassForErrorLog);

                    // Supplement error information - convert entity id to an entity
                    errorList.stream().forEach(execError -> {
                        execError.setEntity((IEntity) iEntityService.findById(execError.getEntityId()));
                    });

                    return errorList;
                }
            };

        }

        return executionErrorDM;
    }

    public TreeNode getExecutionHistoryRoot() {

        if (executionHistoryRoot == null) {

            executionHistoryRoot = new DefaultTreeNode(new JobExecutionResultImpl());

            Map<String, Object> filters = new HashMap<>();
            filters.put("jobInstance", entity);
            filters.put("parentJobExecutionResult", PersistenceService.SEARCH_IS_NULL);

            PaginationConfiguration paginationFilter = new PaginationConfiguration(filters, "id", SortOrder.DESCENDING, 500);
            List<JobExecutionResultImpl> jobExecutions = jobExecutionResultService.list(paginationFilter);

            for (JobExecutionResultImpl jobExecutionResultParent : jobExecutions) {

                TreeNode executionHistoryNode = new DefaultTreeNode(jobExecutionResultParent, executionHistoryRoot);
                if (!jobExecutionResultParent.getWorkerJobExecutionResults().isEmpty()) {
                    for (JobExecutionResultImpl jobExecutionResultChild : jobExecutionResultParent.getCumulativeJobExecutionResults()) {
                        new DefaultTreeNode("child", jobExecutionResultChild, executionHistoryNode);
                    }
                }
            }
        }

        return executionHistoryRoot;
    }

    /**
     * Get a corresponding job implementation to a job template
     * 
     * @return Job implementation
     */
    public Job getJob() {
        return jobInstanceService.getJobByName(entity.getJobTemplate());
    }

    public Class getTargetEntityClassForErrorLog() {
        if (entityClassForErrorLog == null) {
            entityClassForErrorLog = getJob().getTargetEntityClass(entity);
        }

        return entityClassForErrorLog;
    }
}