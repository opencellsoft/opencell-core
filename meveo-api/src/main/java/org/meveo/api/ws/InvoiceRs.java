package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface InvoiceRs extends IBaseWs {

	@WebMethod
	public ActionStatus create(InvoiceDto invoiceDto);

	@WebMethod
	public CustomerInvoicesResponse find(String customerAccountCode);

}
