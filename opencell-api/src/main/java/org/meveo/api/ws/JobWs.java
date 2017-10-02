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

@WebService
public interface JobWs extends IBaseWs {

    // job instance

    @WebMethod
    JobExecutionResultResponseDto execute(@WebParam(name = "jobInstanceInfo") JobInstanceInfoDto postData);

    @WebMethod
    public ActionStatus create(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    public ActionStatus update(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    public ActionStatus createOrUpdateJobInstance(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    public JobInstanceResponseDto findJobInstance(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    @WebMethod
    public ActionStatus removeJobInstance(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    // timer

    @WebMethod
    public ActionStatus createTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    public ActionStatus updateTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    public ActionStatus createOrUpdateTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    public TimerEntityResponseDto findTimer(@WebParam(name = "timerCode") String timerCode);

    @WebMethod
    public ActionStatus removeTimer(@WebParam(name = "timerCode") String timerCode);
    
    @WebMethod
    public JobExecutionResultResponseDto findJobExecutionResult(@WebParam(name="jobExecutionResultId") Long jobExecutionResultId);
    
}
