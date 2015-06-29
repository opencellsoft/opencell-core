package org.meveo.api.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.TimerEntityService;

@Stateless
public class JobInstanceApi extends BaseApi {

	@Inject
	private JobInstanceService jobInstanceService;
	
	@Inject
	private TimerEntityService timerEntityService;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	public void create(JobInstanceDto jobInstanceDto, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(jobInstanceDto.getJobCategory()) || StringUtils.isBlank(jobInstanceDto.getJobTemplate()) || StringUtils.isBlank(jobInstanceDto.getCode())) {
			if (StringUtils.isBlank(jobInstanceDto.getJobCategory())) {
				missingParameters.add("JobCategory");
			}
			if ( StringUtils.isBlank(jobInstanceDto.getJobTemplate())) {
				missingParameters.add("JobTemplate");
			}
			if (StringUtils.isBlank(jobInstanceDto.getCode())) {
				missingParameters.add("Code");
			}			
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		Provider provider = currentUser.getProvider();

		if (jobInstanceService.findByCode(jobInstanceDto.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(JobInstance.class, jobInstanceDto.getCode());
		}
		JobCategoryEnum jobCategory = null;
		try {
			jobCategory = JobCategoryEnum.valueOf(jobInstanceDto.getJobCategory().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job category=" + jobInstanceDto.getJobCategory());
		}
		if (jobCategory == null) {
			throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job name=" + jobInstanceDto.getJobTemplate());
		}
		
		 if (jobCategory!= null) {
			 HashMap<String, String> jobs = new HashMap<String, String>(); 
			 jobs = jobInstanceService.jobEntries.get(jobCategory);
			 if (!jobs.containsKey(jobInstanceDto.getJobTemplate())) {
				 throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job template=" + jobInstanceDto.getJobTemplate());
			 }}
		 
		List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();
		HashMap<String, String> jobs = new HashMap<String, String>();
		jobs = JobInstanceService.jobEntries.get(jobCategory);

		// Create or add missing custom field templates for a job
		if (jobs.containsKey(jobInstanceDto.getJobTemplate())) {
			customFieldTemplates.clear();
			Job job = jobInstanceService.getJobByName(jobInstanceDto.getJobTemplate());
			List<CustomFieldTemplate> jobCustomFields = job.getCustomFields();
			if (jobCustomFields != null) {
				customFieldTemplates = customFieldTemplateService.findByJobName(jobInstanceDto.getJobTemplate());
				if (customFieldTemplates != null && customFieldTemplates.size() != jobCustomFields.size()) {
					for (CustomFieldTemplate cf : jobCustomFields) {
						if (!customFieldTemplates.contains(cf)) {
							try {
								customFieldTemplateService.create(cf, currentUser);
								customFieldTemplates.add(cf);
							} catch (Exception e) {
								log.error("Failed  to init custom fields", e);
							}
						}
					}
				}
			}
		}
		 
		JobInstance jobInstance = new JobInstance();
		jobInstance.setUserId(currentUser.getId());
		jobInstance.setActive(jobInstanceDto.isActive());
		jobInstance.setParametres(jobInstanceDto.getParameter());  
		jobInstance.setJobCategoryEnum(jobCategory);
		jobInstance.setJobTemplate(jobInstanceDto.getJobTemplate());
		jobInstance.setCode(jobInstanceDto.getCode());
		jobInstance.setDescription(jobInstanceDto.getDescription());
		
		 if (!StringUtils.isBlank(jobInstanceDto.getTimerCode())) {
			 TimerEntity timerEntity = timerEntityService.findByCode(jobInstanceDto.getTimerCode(),provider); 
			 jobInstance.setTimerEntity(timerEntity);
			  if(timerEntity==null ){
			 throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid timer entity=" + jobInstanceDto.getTimerCode());
			 }}
		
		 if (!StringUtils.isBlank(jobInstanceDto.getFollowingJob())) {
			 JobInstance nextJob = jobInstanceService.findByCode(jobInstanceDto.getFollowingJob(),provider);
			 jobInstance.setFollowingJob(nextJob);
			  if(nextJob==null ){
			 throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid next job=" + jobInstanceDto.getFollowingJob());
			 }
			 } 

		if (jobInstanceDto.getCustomFields() != null) {
			// populate customFields
			if (jobInstanceDto.getCustomFields() != null) {
				try {
					populateCustomFields(customFieldTemplates, jobInstanceDto.getCustomFields().getCustomField(), jobInstance, "jobInstance", currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("Failed to associate custom field instance to an entity", e);
					throw new MeveoApiException("Failed to associate custom field instance to an entity");
				}
			}
		}

		jobInstanceService.create(jobInstance, currentUser, provider);
	}
}