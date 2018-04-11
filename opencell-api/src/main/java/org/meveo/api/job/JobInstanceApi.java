package org.meveo.api.job;

import java.util.Arrays;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.TimerEntityService;

@Stateless
public class JobInstanceApi extends BaseCrudApi<JobInstance, JobInstanceDto> {

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private TimerEntityService timerEntityService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    public JobInstance create(JobInstanceDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getJobTemplate()) || StringUtils.isBlank(postData.getCode())) {

            if (StringUtils.isBlank(postData.getJobTemplate())) {
                missingParameters.add("jobTemplate");
            }
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            handleMissingParametersAndValidate(postData);
        }

        if (jobInstanceService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(JobInstance.class, postData.getCode());
        }

        Job job = jobInstanceService.getJobByName(postData.getJobTemplate());
        JobCategoryEnum jobCategory = job.getJobCategory();

        JobInstance jobInstance = new JobInstance();

        // Use Active or Disabled field
        if (postData.isActive() != null) {
            jobInstance.setActive(postData.isActive());
        } else if (postData.isDisabled() != null) {
            jobInstance.setDisabled(postData.isDisabled());
        }
        jobInstance.setParametres(postData.getParameter());
        jobInstance.setJobCategoryEnum(jobCategory);
        jobInstance.setJobTemplate(postData.getJobTemplate());
        jobInstance.setCode(postData.getCode());
        jobInstance.setDescription(postData.getDescription());
        jobInstance.setRunOnNodes(postData.getRunOnNodes());
        if (postData.getLimitToSingleNode() != null) {
            jobInstance.setLimitToSingleNode(postData.getLimitToSingleNode());
        }

        if (!StringUtils.isBlank(postData.getTimerCode())) {
            TimerEntity timerEntity = timerEntityService.findByCode(postData.getTimerCode());
            jobInstance.setTimerEntity(timerEntity);
            if (timerEntity == null) {
                throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Invalid timer entity=" + postData.getTimerCode());
            }
        }

        if (!StringUtils.isBlank(postData.getFollowingJob())) {
            JobInstance nextJob = jobInstanceService.findByCode(postData.getFollowingJob());
            jobInstance.setFollowingJob(nextJob);
            if (nextJob == null) {
                throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Invalid next job=" + postData.getFollowingJob());
            }
        }

        // Create any missing CFT for a given provider and job
        Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();
        if (jobCustomFields != null) {
            customFieldTemplateService.createMissingTemplates(jobInstance, jobCustomFields.values());
        }

        // Populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), jobInstance, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        try {
            jobInstanceService.create(jobInstance);
        } catch (BusinessException e1) {
            throw new MeveoApiException(e1.getMessage());
        }

        return jobInstance;
    }

    /**
     * Updates JobInstance based on Code.
     * 
     * @param postData posted data to API.
     * @return job instance
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public JobInstance update(JobInstanceDto postData) throws MeveoApiException, BusinessException {

        String jobInstanceCode = postData.getCode();

        if (StringUtils.isBlank(jobInstanceCode)) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        JobInstance jobInstance = jobInstanceService.findByCode(jobInstanceCode);

        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, jobInstanceCode);
        }

        Job job = jobInstanceService.getJobByName(postData.getJobTemplate());
        JobCategoryEnum jobCategory = job.getJobCategory();

        jobInstance.setJobTemplate(postData.getJobTemplate());
        jobInstance.setParametres(postData.getParameter()); // TODO setParametres should be renamed
        // Use Active or Disabled field
        // if (postData.isActive() != null) {
        // jobInstance.setActive(postData.isActive());
        // } else if (postData.isDisabled() != null) {
        // jobInstance.setDisabled(postData.isDisabled());
        // }
        jobInstance.setJobCategoryEnum(jobCategory);
        jobInstance.setDescription(postData.getDescription());
        jobInstance.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        if (postData.getRunOnNodes() != null) {
            jobInstance.setRunOnNodes(postData.getRunOnNodes());
        }
        if (postData.getLimitToSingleNode() != null) {
            jobInstance.setLimitToSingleNode(postData.getLimitToSingleNode());
        }

        if (!StringUtils.isBlank(postData.getTimerCode())) {
            TimerEntity timerEntity = timerEntityService.findByCode(postData.getTimerCode());
            jobInstance.setTimerEntity(timerEntity);
            if (timerEntity == null) {
                throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Invalid timer entity=" + postData.getTimerCode());
            }
        }

        if (!StringUtils.isBlank(postData.getFollowingJob())) {
            JobInstance nextJob = jobInstanceService.findByCode(postData.getFollowingJob());
            jobInstance.setFollowingJob(nextJob);
            if (nextJob == null) {
                throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Invalid next job=" + postData.getFollowingJob());
            }
        }

        // Create any missing CFT for a given provider and job
        Map<String, CustomFieldTemplate> jobCustomFields = job.getCustomFields();
        if (jobCustomFields != null) {
            customFieldTemplateService.createMissingTemplates(jobInstance, jobCustomFields.values());
        }

        // Populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), jobInstance, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        jobInstance = jobInstanceService.update(jobInstance);

        return jobInstance;
    }

    /**
     * Create or update Job Instance based on code.
     * 
     * @param jobInstanceDto job instance dto
     * 
     * @throws MeveoApiException meveo api excepion
     * @throws BusinessException business exception.
     */
    public JobInstance createOrUpdate(JobInstanceDto jobInstanceDto) throws MeveoApiException, BusinessException {
        if (jobInstanceService.findByCode(jobInstanceDto.getCode()) == null) {
            return create(jobInstanceDto);
        } else {
            return update(jobInstanceDto);
        }
    }

    /**
     * Retrieves a Job Instance base on the code if it is existing.
     * 
     * @param code job instance code
     * @return job instance dto
     * @throws MeveoApiException meveo api exception.
     */
    public JobInstanceDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        JobInstance jobInstance = jobInstanceService.findByCode(code, Arrays.asList("timerEntity"));
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }

        JobInstanceDto jobInstanceDto = new JobInstanceDto(jobInstance, entityToDtoConverter.getCustomFieldsDTO(jobInstance, true));
        return jobInstanceDto;
    }

    /**
     * 
     * Removes a Job Instance base on a code.
     * 
     * @param code job instance code
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void remove(String code) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        JobInstance jobInstance = jobInstanceService.findByCode(code);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }
        jobInstanceService.remove(jobInstance);
    }
}