package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.TimerInfoDto;

@WebService
public interface JobWs extends IBaseWs {

	@WebMethod
	ActionStatus executeTimer(@WebParam(name = "timer") TimerInfoDto postData);

}
