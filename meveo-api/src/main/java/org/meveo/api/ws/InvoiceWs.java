package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface InvoiceWs extends IBaseWs {

	@WebMethod
	public InvoiceCreationResponse create(@WebParam(name = "invoice") InvoiceDto invoiceDto);

	@WebMethod
	public CustomerInvoicesResponse find(@WebParam(name = "customerAccountCode") String customerAccountCode);

}
