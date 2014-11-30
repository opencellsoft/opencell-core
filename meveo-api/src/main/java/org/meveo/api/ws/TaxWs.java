package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface TaxWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(TaxDto postData);

	@WebMethod
	public ActionStatus update(TaxDto postData);

	@WebMethod
	public GetTaxResponse find(String taxCode);

	@WebMethod
	public ActionStatus remove(String taxCode);

}
