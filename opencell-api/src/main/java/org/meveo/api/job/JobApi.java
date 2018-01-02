package org.meveo.api.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;

/**
 * @author Edward P. Legaspi
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

        if (jobExecution.isForceExecution()) {
            jobCacheContainerProvider.resetJobRunningStatus(jobInstance.getId());
        }

        Long executionId = jobExecutionService.executeJobWithResultId(jobInstance, null);

        return findJobExecutionResult(executionId);
    }
    
    /**
     * Stop running job
     * @param jobInstanceCode job instance code to stop
     * @throws MeveoApiException
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
     * @param id Job execution result identifier
     * @return Job execution result DTO
     * @throws MeveoApiException meveo api exception
     */
    public JobExecutionResultDto findJobExecutionResult(Long id) throws MeveoApiException {
        JobExecutionResultDto jobExecutionResultDto = new JobExecutionResultDto();
        if (StringUtils.isBlank(id)) {
            missingParameters.add("id");
            handleMissingParameters();
        }

        JobExecutionResultImpl jobExecutionResult = jobExecutionService.findById(id);
        if (jobExecutionResult == null) {
            throw new EntityDoesNotExistsException(JobExecutionResultImpl.class, id);
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