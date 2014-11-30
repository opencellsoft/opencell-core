package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.response.GetUserResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface UserWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(UserDto postData);

	@WebMethod
	public ActionStatus update(UserDto postData);

	@WebMethod
	public ActionStatus remove(String username);

	@WebMethod
	public GetUserResponse find(String username);

}
