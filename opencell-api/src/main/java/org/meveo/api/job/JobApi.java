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
     * Execute job
     * 
     * @param jobInstanceDTO Job instance DTO
     * @return Job execution result identifier
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public JobExecutionResultDto executeJob(JobInstanceInfoDto jobInstanceDTO) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(jobInstanceDTO.getCode()) && StringUtils.isBlank(jobInstanceDTO.getTimerName())) {
            missingParameters.add("timerName or code");
        }
        handleMissingParameters();

        String code = jobInstanceDTO.getCode() != null ? jobInstanceDTO.getCode() : jobInstanceDTO.getTimerName();

        org.meveo.model.jobs.JobInstance jobInstance = jobInstanceService.findByCode(code);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }

        Long executionId = jobExecutionService.executeJobWithResultId(jobInstance, null);

        return findJobExecutionResult(executionId);
    }

    /**
     * Retrieve job execution result
     * 
     * @param id Job execution result identifier
     * @return Job execution result DTO
     * @throws MeveoApiException
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

        if (jobExecutionResult.getEndDate() != null) {
            List<String> nodeNames = jobCacheContainerProvider.getNodesJobIsRuningOn(jobExecutionResult.getJobInstance().getId());
            if (nodeNames != null && !nodeNames.isEmpty()) {
                jobExecutionResultDto.setRunningOnNodes(StringUtils.concatenate(",", nodeNames));
            }
        }

        return jobExecutionResultDto;
    }
}