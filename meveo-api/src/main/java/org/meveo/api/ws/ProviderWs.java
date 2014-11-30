package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.response.GetProviderResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface ProviderWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(ProviderDto postData);

	@WebMethod
	public GetProviderResponse find(String providerCode);

	@WebMethod
	public ActionStatus update(ProviderDto postData);

}
