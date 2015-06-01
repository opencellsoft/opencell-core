package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CommDto;


/**
 * @author Nasseh
 **/
@WebService
public interface CommunicationWs extends IBaseWs {

	@WebMethod
	public ActionStatus communicate(@WebParam(name = "communicate") CommDto commDto);
}
