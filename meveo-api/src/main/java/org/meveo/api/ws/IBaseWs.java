package org.meveo.api.ws;

import javax.jws.WebMethod;

import org.meveo.api.dto.ActionStatus;

/**
 * @author Edward P. Legaspi
 **/
public interface IBaseWs {

	@WebMethod
	public ActionStatus index();

}
