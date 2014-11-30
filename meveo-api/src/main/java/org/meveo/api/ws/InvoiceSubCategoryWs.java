package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface InvoiceSubCategoryWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(InvoiceSubCategoryDto postData);

	@WebMethod
	public ActionStatus update(InvoiceSubCategoryDto postData);

	@WebMethod
	public GetInvoiceSubCategoryResponse find(String invoiceSubCategoryCode);

	@WebMethod
	public ActionStatus remove(String invoiceSubCategoryCode);

}
