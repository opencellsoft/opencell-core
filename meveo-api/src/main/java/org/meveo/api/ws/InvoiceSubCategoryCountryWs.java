package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.InvoiceSubCategoryCountryDto;
import org.meveo.api.dto.response.GetInvoiceSubCategoryCountryResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface InvoiceSubCategoryCountryWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(InvoiceSubCategoryCountryDto postData);

	@WebMethod
	public ActionStatus update(InvoiceSubCategoryCountryDto postData);

	@WebMethod
	public GetInvoiceSubCategoryCountryResponse find(
			String invoiceSubCategoryCode, String country);

	@WebMethod
	public ActionStatus remove(String invoiceSubCategoryCode, String country);

}
