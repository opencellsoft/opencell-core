package org.meveo.api.job;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.AuditableDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.job.JobInstanceListResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.TimerEntityService;

@Stateless
public class JobInstanceApi extends BaseCrudApi<JobInstance, JobInstanceDto> {
	
	/**
     * Default sort for list call.
     */
    private static final String DEFAULT_SORT_ORDER_ID = "id";
    
    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private TimerEntityService timerEntityService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;
    
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Override
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

        if (job == null) {
            throw new EntityDoesNotExistsException("JobTemplate with code '" + postData.getJobTemplate() + "' doesn't exist.");
        }

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

    @Override
    public JobInstance update(JobInstanceDto postData) throws MeveoApiException, BusinessException {

        String jobInstanceCode = postData.getCode();
        
        if (StringUtils.isBlank(postData.getJobTemplate())) {
            missingParameters.add("jobTemplate");
        }

        if (StringUtils.isBlank(jobInstanceCode)) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        JobInstance jobInstance = jobInstanceService.findByCode(jobInstanceCode);

        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, jobInstanceCode);
        }

        Job job = jobInstanceService.getJobByName(postData.getJobTemplate());
        
        if (job == null) {
            throw new EntityDoesNotExistsException("JobTemplate with code '" + postData.getJobTemplate() + "' doesn't exist." );
        }
        
        JobCategoryEnum jobCategory = job.getJobCategory();

        jobInstance.setJobTemplate(postData.getJobTemplate());
        jobInstance.setParametres(postData.getParameter()); // TODO setParametres should be renamed
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

    @Override
    public JobInstanceDto find(String code) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        JobInstance jobInstance = jobInstanceService.findByCode(code, Arrays.asList("timerEntity"));
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }
        
        customFieldInstanceService.instantiateCFWithDefaultValue(jobInstance);
        
        JobInstanceDto jobInstanceDto = new JobInstanceDto(jobInstance, entityToDtoConverter.getCustomFieldsDTO(jobInstance, CustomFieldInheritanceEnum.INHERIT_NONE));
        return jobInstanceDto;
    }
    
    
    /**
     * List job instances
     * 
     * @param pagingAndFiltering paging and filtering.
     * @return instance of JobInstanceListDto which contains list of Job Instance DTO
     * @throws MeveoApiException meveo api exception
     */
    public JobInstanceListResponseDto list(Boolean mergedCF, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return list(pagingAndFiltering, CustomFieldInheritanceEnum.getInheritCF(true, Boolean.valueOf(mergedCF)));
    }

    public JobInstanceListResponseDto list(PagingAndFiltering pagingAndFiltering, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering, JobInstance.class);

        Long totalCount = jobInstanceService.count(paginationConfiguration);

        JobInstanceListResponseDto result = new JobInstanceListResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<JobInstance> jobInstances = jobInstanceService.list(paginationConfiguration);
            if (jobInstances != null) {
                for (JobInstance jobInstance : jobInstances) {
                    result.getJobInstances().getJobInstances().add(JobInstanceToDto(jobInstance, inheritCF));
                }
            }
        }

        return result;

    }
    
    /**
     * Convert jobInstance entity to dto
     * 
     * @param jobInstance of JobInstance to be mapped
     * @return instance of JobInstanceDto.
     */
    public JobInstanceDto JobInstanceToDto(JobInstance jobInstance) {
        return this.JobInstanceToDto(jobInstance, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    /**
     * Convert jobInstance dto to entity
     *
     * @param jobInstance instance of JobInstance to be mapped
     * @param inheritCF the inherit CF
     * @return instance of JobInstanceDto
     */
    public JobInstanceDto JobInstanceToDto(JobInstance jobInstance, CustomFieldInheritanceEnum inheritCF) {
    	JobInstanceDto dto = new JobInstanceDto();

    	dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(jobInstance, inheritCF));
        dto.setAuditable(new AuditableDto(jobInstance.getAuditable()));
        dto.setJobTemplate(jobInstance.getJobTemplate());
        dto.setJobTemplate(jobInstance.getJobTemplate());
        dto.setJobCategory(jobInstance.getJobCategoryEnum());
        dto.setParameter(jobInstance.getParametres());

        return dto;
    }
}