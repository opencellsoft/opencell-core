package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.ExecuteJobDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface JobWs extends IBaseWs {

	@WebMethod
	ActionStatus executeJob(@WebParam(name = "executeJob") ExecuteJobDto postData);

}
