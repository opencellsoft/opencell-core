package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CdrListDto;

@WebService
public interface MediationWs extends IBaseWs {

	@WebMethod
	ActionStatus registerCdrList(@WebParam(name = "cdrList") CdrListDto postData);
	
	@WebMethod
	ActionStatus chargeCdr(@WebParam(name = "cdr") String cdr);
}
