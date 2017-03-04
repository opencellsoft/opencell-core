package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResult;
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

    /**
     * Execute job
     * 
     * @param jobInstanceDTO Job instance DTO
     * @return Job execution result identifier
     * @throws MeveoApiException
     */
    public Long executeJob(JobInstanceInfoDto jobInstanceDTO) throws MeveoApiException {
        if (StringUtils.isBlank(jobInstanceDTO.getCode()) && StringUtils.isBlank(jobInstanceDTO.getTimerName())) {
            missingParameters.add("timerName or code");
        }
        handleMissingParameters();

        String code = jobInstanceDTO.getCode() != null ? jobInstanceDTO.getCode() : jobInstanceDTO.getTimerName();

        org.meveo.model.jobs.JobInstance jobInstance = jobInstanceService.findByCode(code);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }

        try {
            return jobExecutionService.executeJobWithResultId(jobInstance, null);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
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

        JobExecutionResult jobExecutionResult = jobExecutionService.findById(id);
        if (jobExecutionResult == null) {
            throw new EntityDoesNotExistsException(JobExecutionResult.class, id);
        }

        if (StringUtils.isBlank(jobExecutionResult.getEndDate())) {
            throw new MeveoApiException("Job still running, not yet finished");
        }

        jobExecutionResultDto = new JobExecutionResultDto(jobExecutionResult);

        return jobExecutionResultDto;

    }

}
