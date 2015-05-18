package org.meveo.admin.job.importexport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.async.ImportSubscriptionsAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class ImportSubscriptionsJob extends Job {

    @Inject
    private ImportSubscriptionsAsync importSubscriptionsAsync;
    
	 @Inject
	 private ResourceBundle resourceMessages;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
       
		Long nbRuns = new Long(1);		
		Long waitingMillis = new Long(0);
		try{
			nbRuns = timerEntity.getLongCustomValue("ImportSubscriptionsJob_nbRuns").longValue();  			
			waitingMillis = timerEntity.getLongCustomValue("ImportSubscriptionsJob_waitingMillis").longValue();
		}catch(Exception e){
			log.warn("Cant get customFields for "+timerEntity.getJobName());
		}
    	
    	for(int i=0; i< nbRuns.intValue();i++){
    		importSubscriptionsAsync.launchAndForget(result, currentUser);
    		 try {
				Thread.sleep(waitingMillis.longValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
    	} 		
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }
    
    @Override
	public List<CustomFieldTemplate> getCustomFields(User currentUser) {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate jobName = new CustomFieldTemplate();
		jobName.setCode("ImportSubscriptionsJob_nbRuns");
		jobName.setAccountLevel(AccountLevelEnum.TIMER);
		jobName.setActive(true);
		Auditable audit = new Auditable();
		audit.setCreated(new Date());
		audit.setCreator(currentUser);
		jobName.setAuditable(audit);
		jobName.setProvider(currentUser.getProvider());
		jobName.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		jobName.setFieldType(CustomFieldTypeEnum.LONG);
		jobName.setValueRequired(false);
		jobName.setLongValue(new Long(1));
		result.add(jobName);

		CustomFieldTemplate nbDays = new CustomFieldTemplate();
		nbDays.setCode("ImportSubscriptionsJob_waitingMillis");
		nbDays.setAccountLevel(AccountLevelEnum.TIMER);
		nbDays.setActive(true);
		Auditable audit2 = new Auditable();
		audit2.setCreated(new Date());
		audit2.setCreator(currentUser);
		nbDays.setAuditable(audit2);
		nbDays.setProvider(currentUser.getProvider());
		nbDays.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		nbDays.setFieldType(CustomFieldTypeEnum.LONG);
		nbDays.setValueRequired(false);
		nbDays.setLongValue(new Long(0));
		result.add(nbDays);

		return result;
	}
}