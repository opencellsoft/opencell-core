package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.Invoice4_2Dto;
import org.meveo.api.dto.response.CustomerInvoices4_2Response;
import org.meveo.api.dto.response.InvoiceCreationResponse;

/**
 * @author Edward P. Legaspi
 **/

@Deprecated
@WebService
public interface Invoice4_2Ws extends IBaseWs {

	@WebMethod
	public InvoiceCreationResponse createInvoice(@WebParam(name = "invoice") Invoice4_2Dto invoiceDto);

	@WebMethod
	public CustomerInvoices4_2Response findInvoice(@WebParam(name = "customerAccountCode") String customerAccountCode);

	@WebMethod
	public GenerateInvoiceResponseDto generateInvoiceData(
			@WebParam(name = "generateInvoiceRequest") GenerateInvoiceRequestDto generateInvoiceRequestDto);

	@WebMethod
	public GetXmlInvoiceResponseDto findXMLInvoice(@WebParam(name = "invoiceNumber") String invoiceNumber);
	
	@WebMethod
	public GetXmlInvoiceResponseDto findXMLInvoiceWithType(@WebParam(name = "invoiceNumber") String invoiceNumber,
			@WebParam(name = "invoiceType") String invoiceType);

	@WebMethod
	public GetPdfInvoiceResponseDto findPdfInvoice(@WebParam(name = "invoiceNumber") String invoiceNumber);
	
	@WebMethod
	public GetPdfInvoiceResponseDto findPdfInvoiceWithType(@WebParam(name = "invoiceNumber") String invoiceNumber,
			@WebParam(name = "invoiceType") String invoiceType);

}
