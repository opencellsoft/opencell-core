package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface SellerRs extends IBaseWs {

	@WebMethod
	public ActionStatus create(SellerDto postData);

	@WebMethod
	public ActionStatus update(SellerDto postData);

	@WebMethod
	public GetSellerResponse find(String sellerCode);

	@WebMethod
	public ActionStatus remove(String sellerCode);

}
