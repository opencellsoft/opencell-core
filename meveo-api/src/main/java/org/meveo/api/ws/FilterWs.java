package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface FilterWs extends IBaseWs {

	@WebMethod
	ActionStatus createOrUpdateFilter(@WebParam(name = "filter") FilterDto postData);

}
