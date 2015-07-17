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
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel = AccountLevelEnum.TIMER)
public class TimerEntityBean extends BaseBean<TimerEntity> {

    private static final long serialVersionUID = 1L;

    @Inject
    TimerEntityService timerEntityservice;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    private List<JobExecutionResultImpl> jobExecutionList;

    public TimerEntityBean() {
        super(TimerEntity.class);
    }

    public List<JobExecutionResultImpl> getJobExecutionList() {
        if (jobExecutionList == null && entity != null && entity.getJobName() != null) {
            jobExecutionList = jobExecutionService.find(entity.getJobName(), null);
        }
        return jobExecutionList;
    }

    @Override
    protected IPersistenceService<TimerEntity> getPersistenceService() {
        return timerEntityservice;
    }

    public List<JobCategoryEnum> getJobCategoryEnumValues() {
        return Arrays.asList(JobCategoryEnum.values());
    }

    public List<TimerEntity> getTimerEntityList() {
        return timerEntityservice.find(null);
    }

    public Set<String> getJobNames() {
        HashMap<String, String> jobs = new HashMap<String, String>();
        if (entity.getJobCategoryEnum() != null) {
            jobs = TimerEntityService.jobEntries.get(entity.getJobCategoryEnum());
            return jobs.keySet();
        }
        return null;
    }

    public String executeTimer() {
        try {
            timerEntityservice.manualExecute(entity);
            messages.info(new BundleKey("messages", "info.entity.executed"), entity.getJobName());
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.execution"));
            return null;
        }

        return "jobTimers";
    }

    protected String getListViewName() {
        return "jobTimers";
    }

    
    /**
     * Get timer name from a timer id
     * 
     * @param timerId
     * @return
     */
    public String translateToTimerName(Long timerId) {
        if (timerId != null) {
            TimerEntity timerEntity = timerEntityservice.findById(timerId);
            if (timerEntity != null) {
                return timerEntity.getName();
            }
        }
        return null;
    }
    
    public void initCustomFields() {
        customFieldTemplates.clear();
        if (entity.getJobName() != null) {
            Job job = timerEntityservice.getJobByName(entity.getJobName());
            if (job.getCustomFields(getCurrentUser()) != null) {
                customFieldTemplates = customFieldTemplateService.findByJobName(entity.getJobName());

                if (customFieldTemplates != null && customFieldTemplates.size() != job.getCustomFields(getCurrentUser()).size()) {
                    for (CustomFieldTemplate cf : job.getCustomFields(getCurrentUser())) {
                        if (!customFieldTemplates.contains(cf)) {
                            try {
                                customFieldTemplateService.create(cf);
                                customFieldTemplates.add(cf);
                            } catch (BusinessException e) {
                                e.printStackTrace();
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
    public boolean isTimerRunning(TimerEntity timerEntity) {
        // Check a timerEntityservice's job cache only when timerEntity itself does not have that info - cases when executing an inactive job manually.
        if (!timerEntity.isRunning()) {
            return timerEntityservice.isTimerRunning(timerEntity.getId());
        } else {
            return true;
        }
    }

}