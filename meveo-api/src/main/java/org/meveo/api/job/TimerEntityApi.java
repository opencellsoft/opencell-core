package org.meveo.api.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import liquibase.exception.DateParseException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;

@Stateless
public class TimerEntityApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private TimerEntityService timerEntityService;
	
	@Inject
	private CustomFieldInstanceService customFieldInstanceService;
	
	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	private List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();

	public void create(TimerEntityDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getJobCategory()) && !StringUtils.isBlank(postData.getJobName())
				&& !StringUtils.isBlank(postData.getName())
				&& !StringUtils.isBlank(postData.getHour()) && !StringUtils.isBlank(postData.getMinute())
				&& !StringUtils.isBlank(postData.getSecond()) && !StringUtils.isBlank(postData.getYear())
				&& !StringUtils.isBlank(postData.getMonth()) && !StringUtils.isBlank(postData.getDayOfMonth())
				&& !StringUtils.isBlank(postData.getDayOfWeek())) {
			Provider provider = currentUser.getProvider();

			if (timerEntityService.findByName(postData.getName(), provider) != null) {
				throw new EntityAlreadyExistsException(TimerEntity.class, postData.getName());
			}
			JobCategoryEnum jobCategory = null;
			try {
				jobCategory = JobCategoryEnum.valueOf(postData.getJobCategory().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job category=" + postData.getJobCategory());
			}
		   if (jobCategory!= null) {
			   HashMap<String, String> jobs = new HashMap<String, String>();
		        jobs = timerEntityService.jobEntries.get(jobCategory);
			
			if(jobs.containsKey(postData.getJobName())){
			   customFieldTemplates.clear();
	            Job job = timerEntityService.getJobByName(postData.getJobName());
	            if (job.getCustomFields(currentUser) != null) {
	              customFieldTemplates = customFieldTemplateService.findByJobName(postData.getJobName());
	              if (customFieldTemplates != null && customFieldTemplates.size() != job.getCustomFields(currentUser).size()) {
                    for (CustomFieldTemplate cf : job.getCustomFields(currentUser)) {
                        if (!customFieldTemplates.contains(cf)) {
                            try {
                                customFieldTemplateService.create(cf);
                                customFieldTemplates.add(cf);
                            } catch (BusinessException e) {
                                log.error("Failed  to init custom fields",e);
                            }}
		                    }}
	            			}
						   }
						else{   
						throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job name=" + postData.getJobName());
		   				}
						  }	
			TimerEntity timerEntity = new TimerEntity(); 
			
			
			timerEntity.getTimerInfo().setUserId(currentUser.getId());
			timerEntity.getTimerInfo().setActive(postData.isActive());
			timerEntity.getTimerInfo().setParametres(postData.getParameter());
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
			timerEntityService.create(timerEntity, currentUser, provider);
			  if (postData.getCustomFields() != null) {
				  Job job = timerEntityService.getJobByName(postData.getJobName());
				  if(job!=null){
				  for (Map.Entry<String, String> entry : postData.getCustomFields().entrySet()) {
					  if(!StringUtils.isBlank(entry.getKey()) &&!StringUtils.isBlank(entry.getValue())) {
                            CustomFieldTemplate cf = customFieldTemplateService.findByCode(entry.getKey(), currentUser.getProvider());
							if(job.getCustomFields(currentUser).contains(cf)){
						    CustomFieldInstance cfi = new CustomFieldInstance();
							cfi.setTimerEntity(timerEntity);
							cfi.setActive(true);
							cfi.setCode(cf.getCode()); 
							cfi.setProvider(currentUser.getProvider());
							try{
						    if (cf.getFieldType() == CustomFieldTypeEnum.DATE) {
						   SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
							try { 
							 Date dateValue= format.parse(entry.getValue());
							 cfi.setDateValue(dateValue); 
							} catch (ParseException e) {
							  throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, " Custom field "+entry.getKey()+" must be have the pattern dd-MM-yyyy");
					          } 
						  } else if (cf.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
								  cfi.setDoubleValue(Double.valueOf(entry.getValue()));
						  } else if (cf.getFieldType() == CustomFieldTypeEnum.LONG) {
								  cfi.setLongValue(Long.valueOf(entry.getValue()));
						  } else if (cf.getFieldType() == CustomFieldTypeEnum.STRING || cf.getFieldType() == CustomFieldTypeEnum.LIST) {
								  cfi.setStringValue(entry.getValue());
						  }
						}catch (NumberFormatException e ){
								 throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, " Custom field "+entry.getKey()+" must be a number");   
						} 
							
					      customFieldInstanceService.create(cfi, currentUser); 
						}
						else{
						throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "No custom field template with code="+entry.getKey());
						}
							}
						  }
						  }
								}
			
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

	public List<CustomFieldTemplate> getCustomFieldTemplates() {
		return customFieldTemplates;
	}

	public void setCustomFieldTemplates(
			List<CustomFieldTemplate> customFieldTemplates) {
		this.customFieldTemplates = customFieldTemplates;
	}
	
	
}

		        