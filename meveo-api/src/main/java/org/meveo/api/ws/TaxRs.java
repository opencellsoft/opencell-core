package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.response.GetTaxResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface TaxRs extends IBaseWs {

	@WebMethod
	public ActionStatus create(TaxDto postData);

	@WebMethod
	public ActionStatus update(TaxDto postData);

	@WebMethod
	public GetTaxResponse find(@QueryParam("taxCode") String taxCode);

	@WebMethod
	public ActionStatus remove(@PathParam("taxCode") String taxCode);

}
