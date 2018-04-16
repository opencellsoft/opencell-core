package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.job.JobExecutionResultResponseDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.dto.response.job.TimerEntityResponseDto;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@WebService
public interface JobWs extends IBaseWs {

    // job instance

    @WebMethod
    JobExecutionResultResponseDto execute(@WebParam(name = "jobInstanceInfo") JobInstanceInfoDto postData);
    
    @WebMethod
    ActionStatus stop(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    @WebMethod
    ActionStatus create(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    ActionStatus update(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    ActionStatus createOrUpdateJobInstance(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    JobInstanceResponseDto findJobInstance(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    @WebMethod
    ActionStatus removeJobInstance(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    // timer

    @WebMethod
    ActionStatus createTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    ActionStatus updateTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    ActionStatus createOrUpdateTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    TimerEntityResponseDto findTimer(@WebParam(name = "timerCode") String timerCode);

    @WebMethod
    ActionStatus removeTimer(@WebParam(name = "timerCode") String timerCode);
    
    @WebMethod
    JobExecutionResultResponseDto findJobExecutionResult(@WebParam(name="code") String code, @WebParam(name="jobExecutionResultId") Long jobExecutionResultId);
    
}
