package org.meveo.api.job;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

/**
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class JobApi extends BaseApi {

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    /**
     * Execute job.
     * 
     * @param jobExecution Job execution info
     * @return Job execution result identifier
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public JobExecutionResultDto executeJob(JobInstanceInfoDto jobExecution) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(jobExecution.getCode()) && StringUtils.isBlank(jobExecution.getTimerName())) {
            missingParameters.add("timerName or code");
        }
        handleMissingParameters();

        String code = jobExecution.getCode() != null ? jobExecution.getCode() : jobExecution.getTimerName();

        org.meveo.model.jobs.JobInstance jobInstance = jobInstanceService.findByCode(code);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }
        // #3063 Ability to pass parameters when running job instance                                                                                  
        this.setRunTimeJobValues(jobExecution, jobInstance);

        if (jobExecution.isForceExecution()) {
            jobCacheContainerProvider.resetJobRunningStatus(jobInstance.getId());
        }

        Long executionId = jobExecutionService.executeJobWithResultId(jobInstance, null);

        return findJobExecutionResult(null, executionId);
    }

    
    /**
     * Sets the run time job values.
     *
     * @param jobExecution the job execution
     * @param jobInstance the job instance
     * @throws MeveoApiException 
     */
    private void setRunTimeJobValues(JobInstanceInfoDto jobExecution, JobInstance jobInstance) throws MeveoApiException {
        
        final String  runOnNodes = jobExecution.getRunOnNodes();
        if (isNotEmpty(runOnNodes)) {
            jobInstance.setRunOnNodes(runOnNodes);    
        }
        
        final String parametres = jobExecution.getParameters();
        if (isNotEmpty(parametres)) {
            jobInstance.setParametres(parametres);    
        }
        
        CustomFieldsDto customFieldsDto = jobExecution.getCustomFields();
        if (customFieldsDto != null && CollectionUtils.isNotEmpty(customFieldsDto.getCustomField())) {
            List<CustomFieldDto> cfDtos = customFieldsDto.getCustomField();
            this.validateAndConvertCustomFields(cfDtos, jobInstance);
            Map<String , Object> runTimeCFValues = new HashMap<>();
            for (CustomFieldDto cfDto : cfDtos) {
                runTimeCFValues.put(cfDto.getCode(), cfDto.getValueConverted());
            }
            jobInstance.setRunTimeCFValues(runTimeCFValues);
        }
    }

    /**
     * Stop running job
     * 
     * @param jobInstanceCode job instance code to stop
     * @throws MeveoApiException Meveo api exception
     */
    public void stopJob(String jobInstanceCode) throws MeveoApiException {
        if (StringUtils.isBlank(jobInstanceCode)) {
            missingParameters.add("jobInstanceCode");
        }
        handleMissingParameters();
        org.meveo.model.jobs.JobInstance jobInstance = jobInstanceService.findByCode(jobInstanceCode);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, jobInstanceCode);
        }

        try {
            jobExecutionService.stopJob(jobInstance);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }

    /**
     * Retrieve job execution result.
     * 
     * @param code The job instance code
     * @param id Job execution result identifier
     * @return Job execution result DTO
     * @throws MeveoApiException meveo api exception
     */
    public JobExecutionResultDto findJobExecutionResult(String code, Long id) throws MeveoApiException {
        JobExecutionResultDto jobExecutionResultDto;
        if (StringUtils.isBlank(code) && StringUtils.isBlank(id)) {
            missingParameters.add("id or code");
            handleMissingParameters();
        }

        JobExecutionResultImpl jobExecutionResult = new JobExecutionResultImpl();
        if (!StringUtils.isBlank(code)) {
            JobInstance jobInstance = jobInstanceService.findByCode(code);
            if (jobInstance == null) {
                throw new EntityDoesNotExistsException(JobInstance.class, code);
            }

            jobExecutionResult = jobExecutionService.findLastExecutionByInstance(jobInstance);
            if (jobExecutionResult == null) {
                throw new EntityDoesNotExistsException(JobExecutionResultImpl.class, code);
            }
        } else if (!StringUtils.isBlank(id)) {
            jobExecutionResult = jobExecutionService.findById(id);
            if (jobExecutionResult == null) {
                throw new EntityDoesNotExistsException(JobExecutionResultImpl.class, id);
            }
        }

        jobExecutionResultDto = new JobExecutionResultDto(jobExecutionResult);

        if (jobExecutionResult.getEndDate() == null) {
            List<String> nodeNames = jobCacheContainerProvider.getNodesJobIsRuningOn(jobExecutionResult.getJobInstance().getId());
            if (nodeNames != null && !nodeNames.isEmpty()) {
                jobExecutionResultDto.setRunningOnNodes(StringUtils.concatenate(",", nodeNames));
            }
        }

        return jobExecutionResultDto;
    }
}