package org.meveo.api.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
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

	public void create(JobInstanceDto postData, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(postData.getJobTemplate()) || StringUtils.isBlank(postData.getCode())) {

			if ( StringUtils.isBlank(postData.getJobTemplate())) {
				missingParameters.add("JobTemplate");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("Code");
			}			
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		Provider provider = currentUser.getProvider();

		if (jobInstanceService.findByCode(postData.getCode(), provider) != null) {
			throw new EntityAlreadyExistsException(JobInstance.class, postData.getCode());
		}
		
		List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();
		JobCategoryEnum jobCategory = null;
		for (Map.Entry<JobCategoryEnum, HashMap<String, String>> jobCategoryEnum : JobInstanceService.jobEntries
				.entrySet()) {
			HashMap<String, String> jobs = new HashMap<String, String>();
			jobs = JobInstanceService.jobEntries.get(jobCategoryEnum.getKey());
			// Create or add missing custom field templates for a job
			if (jobs.containsKey(postData.getJobTemplate())) {
				jobCategory = jobCategoryEnum.getKey();
				customFieldTemplates.clear();
				Job job = jobInstanceService.getJobByName(postData.getJobTemplate());
				Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();
				if (jobCustomFields != null) {
					customFieldTemplates = customFieldTemplateService.findByJobName(postData.getJobTemplate());
					if (customFieldTemplates != null && customFieldTemplates.size() != jobCustomFields.size()) {
						for (CustomFieldTemplate cf : jobCustomFields.values()) {
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
		}
		 
		JobInstance jobInstance = new JobInstance();
		jobInstance.setUserId(currentUser.getId());
		jobInstance.setActive(postData.isActive());
		jobInstance.setParametres(postData.getParameter());  
		jobInstance.setJobCategoryEnum(jobCategory);
		jobInstance.setJobTemplate(postData.getJobTemplate());
		jobInstance.setCode(postData.getCode());
		jobInstance.setDescription(postData.getDescription());
		
		 if (!StringUtils.isBlank(postData.getTimerCode())) {
			 TimerEntity timerEntity = timerEntityService.findByCode(postData.getTimerCode(),provider); 
			 jobInstance.setTimerEntity(timerEntity);
			  if(timerEntity==null ){
			 throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid timer entity=" + postData.getTimerCode());
			 }}
		
		 if (!StringUtils.isBlank(postData.getFollowingJob())) {
			JobInstance nextJob = jobInstanceService.findByCode(postData.getFollowingJob(),provider);
			jobInstance.setFollowingJob(nextJob);
			if(nextJob==null ){
			  throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid next job=" + postData.getFollowingJob());
			}
		} 
		 
		 
		// populate customFields
		if (postData.getCustomFields() != null) {
			try {
				populateCustomFields(customFieldTemplates, postData.getCustomFields().getCustomField(), jobInstance, AccountLevelEnum.TIMER, currentUser);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Failed to associate custom field instance to an entity", e);
				throw new MeveoApiException("Failed to associate custom field instance to an entity");
			}
		}
		

		jobInstanceService.create(jobInstance, currentUser, provider);
	}
	
	/**
	 * Updates JobInstance based on Code
	 * @param jobInstanceDto
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void update(JobInstanceDto postData, User currentUser) throws MeveoApiException {
		
		String jobInstanceCode = postData.getCode(); 
		Provider provider = currentUser.getProvider();
		
		if (StringUtils.isBlank(jobInstanceCode)) {
			missingParameters.add("Code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		} else {
			
			JobInstance jobInstance = jobInstanceService.findByCode(jobInstanceCode, provider); 
			
			if (jobInstance == null ) {
				throw new EntityDoesNotExistsException(JobInstance.class, jobInstanceCode);
			}
			
			List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();
			JobCategoryEnum jobCategory = null;
			for (Map.Entry<JobCategoryEnum, HashMap<String, String>> jobCategoryEnum : JobInstanceService.jobEntries
					.entrySet()) {
				HashMap<String, String> jobs = new HashMap<String, String>();
				jobs = JobInstanceService.jobEntries.get(jobCategoryEnum.getKey());
				// Create or add missing custom field templates for a job
				if (jobs.containsKey(postData.getJobTemplate())) {
					jobCategory = jobCategoryEnum.getKey();
					customFieldTemplates.clear();
					Job job = jobInstanceService.getJobByName(postData.getJobTemplate());
					Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();
					if (jobCustomFields != null) {
						customFieldTemplates = customFieldTemplateService.findByJobName(postData.getJobTemplate());
						if (customFieldTemplates != null && customFieldTemplates.size() != jobCustomFields.size()) {
							for (CustomFieldTemplate cf : jobCustomFields.values()) {
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
			}
			
			// populate customFields
			if (postData.getCustomFields() != null) {
				try {
					populateCustomFields(customFieldTemplates, postData.getCustomFields().getCustomField(), jobInstance, AccountLevelEnum.TIMER, currentUser);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("Failed to associate custom field instance to an entity", e);
					throw new MeveoApiException("Failed to associate custom field instance to an entity");
				}
			}
			
			jobInstance.setJobTemplate(postData.getJobTemplate());
			jobInstance.setParametres(postData.getParameter()); //TODO setParametres should be renamed
			jobInstance.setActive(postData.isActive());
			jobInstance.setJobCategoryEnum(jobCategory);
			
			if (!StringUtils.isBlank(postData.getTimerCode())) {
				TimerEntity timerEntity = timerEntityService.findByCode(
						postData.getTimerCode(), provider);
				jobInstance.setTimerEntity(timerEntity);
				if (timerEntity == null) {
					throw new MeveoApiException(
							MeveoApiErrorCode.BUSINESS_API_EXCEPTION,
							"Invalid timer entity=" + postData.getTimerCode());
				}
			}

			if (!StringUtils.isBlank(postData.getFollowingJob())) {
				JobInstance nextJob = jobInstanceService.findByCode(
						postData.getFollowingJob(), provider);
				jobInstance.setFollowingJob(nextJob);
				if (nextJob == null) {
					throw new MeveoApiException(
							MeveoApiErrorCode.BUSINESS_API_EXCEPTION,
							"Invalid next job=" + postData.getFollowingJob());
				}
			}
			
			jobInstanceService.update(jobInstance, currentUser);
		}
	}
	
	/**
	 * Create or update Job Instance based on code.
	 * @param jobInstanceDto
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(JobInstanceDto jobInstanceDto, User currentUser) throws MeveoApiException {
		if (jobInstanceService.findByCode(jobInstanceDto.getCode(), currentUser.getProvider()) == null) {
			create(jobInstanceDto, currentUser);
		} else {
			update(jobInstanceDto, currentUser);
		}
	}
	
	/**
	 * Retrieves a Job Instance base on the code if it is existing.
	 * @param code
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public JobInstanceDto find(String code, Provider provider) throws MeveoApiException {
		
		if (!StringUtils.isBlank(code)) {
			JobInstance jobInstance = jobInstanceService.findByCode(code, provider);
			if (jobInstance != null) {
				JobInstanceDto jobInstanceDto = new JobInstanceDto();
				
				jobInstanceDto.setJobCategory(jobInstance.getJobCategoryEnum().toString()); //TODO please review if correct
				jobInstanceDto.setJobTemplate(jobInstance.getJobTemplate());
				jobInstanceDto.setCode(jobInstance.getCode());
				jobInstanceDto.setDescription(jobInstance.getDescription());
				
				if (jobInstance.getFollowingJob() != null) {
					jobInstanceDto.setFollowingJob(jobInstance.getFollowingJob().getCode());
				}
				
				jobInstanceDto.setParameter(jobInstance.getParametres());
				jobInstanceDto.setActive(jobInstance.isActive());
				jobInstanceDto.setUserId(jobInstance.getUserId());
				
				//TODO please review if correct
				Map<String, CustomFieldInstance> customFields = jobInstance.getCustomFields();
				CustomFieldsDto customFieldsDto = new CustomFieldsDto();
				if (customFields != null) {
					for (CustomFieldInstance cfi : customFields.values()) {
						customFieldsDto.getCustomField().addAll(CustomFieldDto.toDTO(cfi));
					}
				}
				
				jobInstanceDto.setCustomFields(customFieldsDto);
				jobInstanceDto.setTimerCode(jobInstance.getTimerEntity().toString());
				
				return jobInstanceDto;
			} 
			
			throw new EntityDoesNotExistsException(JobInstance.class, code);
			
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("code");
			}
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
		
	}
	
	/**
	 * 
	 * Removes a Job Instance base on a code.
	 * 
	 * @param code
	 * @param provider
	 * @throws MeveoApiException
	 */
	public void remove(String code, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			JobInstance jobInstance = jobInstanceService.findByCode(code, provider);
			
			if (jobInstance == null) {
				throw new EntityDoesNotExistsException(JobInstance.class, code);
			}
			jobInstanceService.remove(jobInstance);
			
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("code");
			}
			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}
	
}