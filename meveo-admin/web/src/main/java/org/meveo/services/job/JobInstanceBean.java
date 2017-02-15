package org.meveo.services.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.action.admin.custom.CustomFieldDataEntryBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class JobInstanceBean extends CustomFieldBean<JobInstance> {

    private static final long serialVersionUID = 1L;

    @Inject
    JobInstanceService jobInstanceService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private CustomFieldDataEntryBean customFieldDataEntryBean;
    
    public JobInstanceBean() {
        super(JobInstance.class);
    }

    @Override
    public JobInstance initEntity() {
        super.initEntity();
        
        createMissingCustomFieldTemplates();
          
        return entity;
    }

    @Override
    protected IPersistenceService<JobInstance> getPersistenceService() {
        return jobInstanceService;
    }

    public List<JobCategoryEnum> getJobCategoryEnumValues() {
        return Arrays.asList(JobCategoryEnum.values());
    }

    public List<JobInstance> getTimerEntityList() {
        return jobInstanceService.find(null);
    }

    public Set<String> getJobNames() {
        HashMap<String, String> jobs = new HashMap<String, String>();
        if (entity.getJobCategoryEnum() != null) {
            jobs = JobInstanceService.jobEntries.get(entity.getJobCategoryEnum());
            return jobs.keySet();
        }
        return null;
    }

    public String execute() {
        try {
            jobInstanceService.manualExecute(entity);
            messages.info(new BundleKey("messages", "info.entity.executed"), entity.getJobTemplate());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.execution"));
            return null;
        }

        return getEditViewName();
    }
    
    public String saveOrUpdate(boolean killConversation) throws BusinessException{
    	super.saveOrUpdate(killConversation);
    	return getEditViewName();
    }

    protected String getListViewName() {
        return "jobInstances";
    }

    /**
     * Get JobInstance name from a jobId
     * 
     * @param jobId
     * @return
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
     * Get a list of templates matching job template name. Create new ones if job template defines new ones in code.
     * 
     * @return A map of custom field templates with template code as a key
     * @throws BusinessException 
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
     * Check if a job is running.
     * 
     * @param jobInstance JobInstance entity
     * @return True if running
     */
    public boolean isTimerRunning(JobInstance jobInstance) {
        return jobInstanceService.isJobRunning(jobInstance.getId());
    }

    /**
     * Explicitly refresh custom fields and action definitions. Should be used when job template change, as on it depends what fields and actions apply
     * @throws BusinessException 
     */
    public void refreshCustomFieldsAndActions() throws BusinessException {

        createMissingCustomFieldTemplates();
        customFieldDataEntryBean.refreshFieldsAndActions(entity);
    }
}