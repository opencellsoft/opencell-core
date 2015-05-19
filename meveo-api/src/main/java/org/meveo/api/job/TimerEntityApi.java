package org.meveo.api.job;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;

@Stateless
public class TimerEntityApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private TimerEntityService timerEntityService;
	
	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	public void create(TimerEntityDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getJobCategory()) && !StringUtils.isBlank(postData.getJobName())
				&& !StringUtils.isBlank(postData.getName())
				&& !StringUtils.isBlank(postData.getHour()) && !StringUtils.isBlank(postData.getMinute())
				&& !StringUtils.isBlank(postData.getSecond()) && !StringUtils.isBlank(postData.getYear())
				&& !StringUtils.isBlank(postData.getMonth()) && !StringUtils.isBlank(postData.getDayOfMonth())
				&& !StringUtils.isBlank(postData.getDayOfWeek())) {
			Provider provider = currentUser.getProvider();
 
			JobCategoryEnum jobCategory = null;
			try {
				jobCategory = JobCategoryEnum.valueOf(postData.getJobCategory().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job category=" + postData.getJobCategory());
			}
		   if (jobCategory!= null) {
			   HashMap<String, String> jobs = new HashMap<String, String>();
		        jobs = timerEntityService.jobEntries.get(jobCategory);
			
			if(!jobs.containsKey(postData.getJobName())){
				throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job name=" + postData.getJobName());
				}}
		   
			TimerEntity timerEntity = new TimerEntity(); 
			timerEntity.getTimerInfo().setUserId(currentUser.getId());
			timerEntity.getTimerInfo().setActive(postData.isActive());
			timerEntity.getTimerInfo().setParametres(postData.getParametres());
			 if (!StringUtils.isBlank(postData.getFollowingTimer())) {
			 TimerEntity nextJob = timerEntityService.findByName(postData.getFollowingTimer(),provider);
			 timerEntity.setFollowingTimer(timerEntityService.findByName(postData.getFollowingTimer(),provider));
				if(nextJob==null ){
				throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid next job=" + postData.getFollowingTimer());
				}
			   }
			timerEntity.setJobCategoryEnum(jobCategory);
			timerEntity.setJobName(postData.getJobName());
			timerEntity.setName(postData.getName());
			timerEntity.setHour(postData.getHour());
			timerEntity.setMinute(postData.getMinute());
			timerEntity.setSecond(postData.getSecond());
			timerEntity.setYear(postData.getYear());
			timerEntity.setMonth(postData.getMonth());
			timerEntity.setDayOfMonth(postData.getDayOfMonth());
			timerEntity.setDayOfWeek(postData.getDayOfWeek());
			 
			if (postData.getCustomFields() != null) {
				for (CustomFieldDto cf : postData.getCustomFields().getCustomField()) { 
					List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAccountLevel(AccountLevelEnum.TIMER, provider);
			if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
				for (CustomFieldTemplate cft : customFieldTemplates) {
				if (cf.getCode().equals(cft.getCode())) { 
					CustomFieldInstance cfiNew = new CustomFieldInstance();
					cfiNew.setTimerEntity(timerEntity);
					cfiNew.setActive(true);
					cfiNew.setCode(cf.getCode());
					cfiNew.setDateValue(cf.getDateValue());
					cfiNew.setDescription(cf.getDescription());
					cfiNew.setDoubleValue(cf.getDoubleValue());
					cfiNew.setLongValue(cf.getLongValue());
					cfiNew.setProvider(currentUser.getProvider());
					cfiNew.setStringValue(cf.getStringValue());
					cfiNew.updateAudit(currentUser);
					timerEntity.getCustomFields().put(cfiNew.getCode(), cfiNew);
				}
				}
			} else {
				log.warn("No custom field template defined.");
			}
				}
			}
			timerEntityService.create(timerEntity, currentUser, provider);
		} else {
				
			if (StringUtils.isBlank(postData.getJobName())) {
				missingParameters.add("jobName");
			}
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getHour())) {
				missingParameters.add("hour");
			}
			if (StringUtils.isBlank(postData.getMinute())) {
				missingParameters.add("minute");
			}
			if (StringUtils.isBlank(postData.getSecond())) {
				missingParameters.add("second");
			}
			if (StringUtils.isBlank(postData.getYear())) {
				missingParameters.add("year");
			}
			if (StringUtils.isBlank(postData.getMonth())) {
				missingParameters.add("month");
			}
			if (StringUtils.isBlank(postData.getDayOfMonth())) {
				missingParameters.add("dayOfMonth");
			}
			if (StringUtils.isBlank(postData.getDayOfWeek())) {
				missingParameters.add("dayOfWeek");
			}
			
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
