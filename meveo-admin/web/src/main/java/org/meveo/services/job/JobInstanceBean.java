package org.meveo.services.job;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldEnabledBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel = AccountLevelEnum.TIMER)
public class JobInstanceBean extends BaseBean<JobInstance> {

    private static final long serialVersionUID = 1L;

    @Inject
    JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    private List<JobExecutionResultImpl> jobExecutionList;

    public JobInstanceBean() {
        super(JobInstance.class);
    }

    public List<JobExecutionResultImpl> getJobExecutionList() {
        if (jobExecutionList == null && entity != null && entity.getJobTemplate() != null) {
            jobExecutionList = jobExecutionService.find(entity.getJobTemplate(), null);
        }
        return jobExecutionList;
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

    public String executeTimer() {
        try {
        	jobInstanceService.manualExecute(entity);
            messages.info(new BundleKey("messages", "info.entity.executed"), entity.getJobTemplate());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.execution"));
            return null;
        }

        return  getListViewName();
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
    
    public void initCustomFields() {
        customFieldTemplates.clear();
        if (entity.getJobTemplate() != null) {
            Job job = jobInstanceService.getJobByName(entity.getJobTemplate());
            if (job.getCustomFields(getCurrentUser()) != null) {
                customFieldTemplates = customFieldTemplateService.findByJobName(entity.getJobTemplate());

                if (customFieldTemplates != null && customFieldTemplates.size() != job.getCustomFields(getCurrentUser()).size()) {
                    for (CustomFieldTemplate cf : job.getCustomFields(getCurrentUser())) {
                        if (!customFieldTemplates.contains(cf)) {
                            try {
                                customFieldTemplateService.create(cf);
                                customFieldTemplates.add(cf);
                            } catch (BusinessException e) {
                                log.error("Failed  to init custom fields",e);
                            }
                        }
                    }
                }
                if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
                    for (CustomFieldTemplate cf : customFieldTemplates) {
                        CustomFieldInstance cfi = ((ICustomFieldEntity) entity).getCustomFields().get(cf.getCode());
                        if (cfi != null) {
                            if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
                                cf.setDateValue(cfi.getDateValue());
                            } else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                                cf.setDoubleValue(cfi.getDoubleValue());
                            } else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
                                cf.setLongValue(cfi.getLongValue());
                            } else if (cf.getFieldType() == CustomFieldTypeEnum.STRING || cf.getFieldType() == CustomFieldTypeEnum.LIST) {
                                cf.setStringValue(cfi.getStringValue());
                            }
                            // Clear existing transient values
                        } else {
                            if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
                                cf.setDateValue(null);
                            } else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                                cf.setDoubleValue(null);
                            } else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
                                cf.setLongValue(null);
                            } else if (cf.getFieldType() == CustomFieldTypeEnum.STRING || cf.getFieldType() == CustomFieldTypeEnum.LIST) {
                                cf.setStringValue(null);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Check if a timer is running.
     * 
     * @param timerEntity Timer entity
     * @return True if running
     */
    public boolean isTimerRunning(JobInstance jobInstance) {
        // Check a timerEntityservice's job cache only when timerEntity itself does not have that info - cases when executing an inactive job manually.
        if (!jobInstance.isRunning()) {
            return jobInstanceService.isTimerRunning(jobInstance.getId());
        } else {
            return true;
        }
    }

}