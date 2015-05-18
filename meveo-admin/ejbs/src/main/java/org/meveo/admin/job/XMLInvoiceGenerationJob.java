package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

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
public class XMLInvoiceGenerationJob extends Job {

    @Inject
    private XMLInvoiceGenerationJobBean xmlInvoiceGenerationJobBean;
    
	 @Inject
	 private ResourceBundle resourceMessages;

    @Override
    protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
        xmlInvoiceGenerationJobBean.execute(result, timerEntity.getTimerInfo().getParametres(), currentUser, timerEntity);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.INVOICING;
    }
    
    @Override
   	public List<CustomFieldTemplate> getCustomFields(User currentUser) {
   		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

   		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
   		customFieldNbRuns.setCode("XMLInvoiceGenerationJob_nbRuns");
   		customFieldNbRuns.setAccountLevel(AccountLevelEnum.TIMER);
   		customFieldNbRuns.setActive(true);
   		Auditable audit = new Auditable();
   		audit.setCreated(new Date());
   		audit.setCreator(currentUser);
   		customFieldNbRuns.setAuditable(audit);
   		customFieldNbRuns.setProvider(currentUser.getProvider());
   		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
   		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
   		customFieldNbRuns.setValueRequired(false);
   		customFieldNbRuns.setLongValue(new Long(1));
   		result.add(customFieldNbRuns);

   		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
   		customFieldNbWaiting.setCode("XMLInvoiceGenerationJob_waitingMillis");
   		customFieldNbWaiting.setAccountLevel(AccountLevelEnum.TIMER);
   		customFieldNbWaiting.setActive(true);
   		Auditable audit2 = new Auditable();
   		audit2.setCreated(new Date());
   		audit2.setCreator(currentUser);
   		customFieldNbWaiting.setAuditable(audit2);
   		customFieldNbWaiting.setProvider(currentUser.getProvider());
   		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
   		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
   		customFieldNbWaiting.setValueRequired(false);
   		customFieldNbWaiting.setLongValue(new Long(0));
   		result.add(customFieldNbWaiting);

   		return result;
   	}
}