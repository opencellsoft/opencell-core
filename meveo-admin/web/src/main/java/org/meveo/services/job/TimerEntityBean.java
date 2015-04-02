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
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.local.IPersistenceService;
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

    
    private List<JobExecutionResultImpl> jobExecutionList;
    
    public TimerEntityBean(){
        super(TimerEntity.class);
    }
    
    public List<JobExecutionResultImpl> getJobExecutionList(){
        if(jobExecutionList==null && entity!=null && entity.getJobName()!=null){
            jobExecutionList= jobExecutionService.find(entity.getJobName(), null);
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

}
