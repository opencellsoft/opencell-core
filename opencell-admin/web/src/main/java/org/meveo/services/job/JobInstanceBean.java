package org.meveo.services.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

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
    private JobCacheContainerProvider jobCacheContainerProvider;

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
        return Arrays.asList(JobCategoryEnum.values());
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

    public String execute() {
        try {
            jobExecutionService.manualExecute(entity);
            messages.info(new BundleKey("messages", "info.entity.executed"), entity.getJobTemplate());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.execution"));
            return null;
        }

        return getEditViewName();
    }
    
    public String stop() {
        try {
            jobExecutionService.stopJob(entity);
            messages.info(new BundleKey("messages", "info.entity.stopped"), entity.getJobTemplate());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.execution"));
            return null;
        }

        return getEditViewName();
    }

    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return getEditViewName();
    }

    protected String getListViewName() {
        return "jobInstances";
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
     * Check if a job is running and where
     * 
     * @param jobInstance JobInstance entity
     * @return Running status
     */
    public JobRunningStatusEnum isTimerRunning(JobInstance jobInstance) {
        return jobCacheContainerProvider.isJobRunning(jobInstance.getId());
    }

    /**
     * Check if job can be run on a current server or cluster node if deployed in cluster environment
     * 
     * @param jobInstance JobInstance entity
     * @return True if it can be executed locally
     */
    public boolean isAllowedToExecute(JobInstance jobInstance) {
		if (jobInstance == null || jobInstance.getId() == null) {
			return false;
		}
    	
        JobRunningStatusEnum isRunning = jobCacheContainerProvider.isJobRunning(jobInstance.getId());
        if (isRunning == JobRunningStatusEnum.NOT_RUNNING) {
            return true;
        } else if (isRunning == JobRunningStatusEnum.RUNNING_THIS) {
            return false;
        } else {

            String nodeToCheck = EjbUtils.getCurrentClusterNode();
            return jobInstance.isRunnableOnNode(nodeToCheck);
        }
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
    
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("executionResults");
    }
    
    @Override
    public void enable() {    	
    	super.enable();
    	initEntity();
    }
    
    @Override
    public void disable() {
    	super.disable();
    	initEntity();
    }
    
}