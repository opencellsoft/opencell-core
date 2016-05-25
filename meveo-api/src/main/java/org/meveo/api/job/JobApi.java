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
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResult;
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
     * 
     * @param timerInfoDTO , timerInfoDTO.getTimerName() contains the code of JobInstance
     * @param currentUser
     * @throws Exception
     */

    public Long executeJob(JobInstanceInfoDto timerInfoDTO, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(timerInfoDTO.getCode()) && StringUtils.isBlank(timerInfoDTO.getTimerName())) {
            missingParameters.add("timerName or code");
        }
        handleMissingParameters();

        try {
            return jobInstanceService.executeAPITimer(timerInfoDTO, currentUser);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }
    
    public JobExecutionResultDto findJobExecutionResult(Long id) throws MeveoApiException {
    	JobExecutionResultDto jobExecutionResultDto = new JobExecutionResultDto();
    	if(StringUtils.isBlank(id)) {
    		missingParameters.add("id");
    		handleMissingParameters();
    	}
    	
    	JobExecutionResult jobExecutionResult = jobExecutionService.findById(id) ;
    	if (jobExecutionResult == null) {
            throw new EntityDoesNotExistsException(JobExecutionResult.class, id);
        }
    	
    	if(StringUtils.isBlank(jobExecutionResult.getEndDate())) {
            throw new MeveoApiException("Job still running, not yet finished");
		}
    	
    	jobExecutionResultDto = new JobExecutionResultDto(jobExecutionResult);
    	
    	return jobExecutionResultDto;
    	
    }

}
