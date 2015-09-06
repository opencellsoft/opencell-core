package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.model.jobs.JobInstanceInfoDto;

@WebService
public interface JobWs extends IBaseWs {

	@WebMethod
	ActionStatus execute(@WebParam(name = "jobInstanceInfo") JobInstanceInfoDto postData);
	
	@WebMethod
	public ActionStatus create(@WebParam(name = "jobInstance") JobInstanceDto postData);
	
	@WebMethod
	public ActionStatus update(@WebParam(name = "jobInstance") JobInstanceDto postData);
	
	@WebMethod
	public ActionStatus createOrUpdate(@WebParam(name = "jobInstance") JobInstanceDto postData);
	
	@WebMethod
	public ActionStatus createTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

}
