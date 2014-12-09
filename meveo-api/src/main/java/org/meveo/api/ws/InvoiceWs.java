package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface InvoiceWs extends IBaseWs {

	@WebMethod
	public InvoiceCreationResponse create(InvoiceDto invoiceDto);

	@WebMethod
	public CustomerInvoicesResponse find(String customerAccountCode);

}
