package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface InvoiceCategoryWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(InvoiceCategoryDto postData);

	@WebMethod
	public ActionStatus update(InvoiceCategoryDto postData);

	@WebMethod
	public GetInvoiceCategoryResponse find(String invoiceCategoryCode);

	@WebMethod
	public ActionStatus remove(String invoiceCategoryCode);

}
