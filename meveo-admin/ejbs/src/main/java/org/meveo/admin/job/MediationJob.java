package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.async.MediationAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class MediationJob extends Job {

	@Inject
	private MediationAsync mediationAsync;

	@Inject
	private ResourceBundle resourceMessages;

	@Override
	protected void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException {
		Long nbRuns = new Long(1);		
		Long waitingMillis = new Long(0);
		try{
			nbRuns = timerEntity.getLongCustomValue("MediationJob_nbRuns").longValue();  			
			waitingMillis = timerEntity.getLongCustomValue("MediationJob_waitingMillis").longValue();
			if(nbRuns == -1){
				nbRuns  = (long) Runtime.getRuntime().availableProcessors();
			}
		}catch(Exception e){
			log.warn("Cant get customFields for "+timerEntity.getJobName());
		}

		Provider provider = currentUser.getProvider();

		ParamBean parambean = ParamBean.getInstance();
		String meteringDir = parambean.getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + provider.getCode() + File.separator + "imports" + File.separator
				+ "metering" + File.separator;

		String inputDir = meteringDir + "input";
		String cdrExtension = parambean.getProperty("mediation.extensions", "csv");
		ArrayList<String> cdrExtensions = new ArrayList<String>();
		cdrExtensions.add(cdrExtension);

		File f = new File(inputDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		File[] files = FileUtils.getFilesForParsing(inputDir, cdrExtensions);
		SubListCreator subListCreator =null;

		try {
			subListCreator = new SubListCreator(Arrays.asList(files),nbRuns.intValue());
		} catch (Exception e1) {			
			e1.printStackTrace();
		}

		while (subListCreator.isHasNext()) {	
			mediationAsync.launchAndForget((List<File>) subListCreator.getNextWorkSet(),result, timerEntity.getTimerInfo().getParametres(), currentUser);
			try {
				Thread.sleep(waitingMillis.longValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}

		for(int i=0; i< nbRuns.intValue();i++){
			try {
				Thread.sleep(waitingMillis.longValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		} 
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.MEDIATION;
	}


	@Override
	public List<CustomFieldTemplate> getCustomFields(User currentUser) {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate nbRuns = new CustomFieldTemplate();
		nbRuns.setCode("MediationJob_nbRuns");
		nbRuns.setAccountLevel(AccountLevelEnum.TIMER);
		nbRuns.setActive(true);
		Auditable audit = new Auditable();
		audit.setCreated(new Date());
		audit.setCreator(currentUser);
		nbRuns.setAuditable(audit);
		nbRuns.setProvider(currentUser.getProvider());
		nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		nbRuns.setLongValue(new Long(1));
		nbRuns.setValueRequired(false);
		result.add(nbRuns);

		CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
		waitingMillis.setCode("MediationJob_waitingMillis");
		waitingMillis.setAccountLevel(AccountLevelEnum.TIMER);
		waitingMillis.setActive(true);
		Auditable audit2 = new Auditable();
		audit2.setCreated(new Date());
		audit2.setCreator(currentUser);
		waitingMillis.setAuditable(audit2);
		waitingMillis.setProvider(currentUser.getProvider());
		waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
		waitingMillis.setLongValue(new Long(0));
		waitingMillis.setValueRequired(false);
		result.add(waitingMillis);

		return result;
	}
}