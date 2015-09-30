package org.meveo.api.rest.invoice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.Invoice}.
 *  
 * @author Edward P. Legaspi
 **/
@Path("/invoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface InvoiceRs extends IBaseRs {

	/**
	 * Create invoice.
	 * 
	 * @param invoiceDto
	 * @return
	 */
	@POST
	@Path("/")
	public InvoiceCreationResponse create(InvoiceDto invoiceDto);

	/**
	 * Search for a list of invoice given a customer account code.
	 * @param customerAccountCode
	 * @return
	 */
	@GET
	@Path("/")
	public CustomerInvoicesResponse find(
			@QueryParam("customerAccountCode") String customerAccountCode);
	
	
	@POST
	@Path("/generateInvoice")
	public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);
	
	
	@POST
	@Path("/getXMLInvoice")
	public GetXmlInvoiceResponseDto getXMLInvoice(String invoiceNumber);
	
	@POST
	@Path("/getPdfInvoice")
	public GetPdfInvoiceResponseDto getPdfInvoice(String invoiceNumber);


}
