package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.InvoiceWs;

@WebService(serviceName = "InvoiceWs", endpointInterface = "org.meveo.api.ws.InvoiceWs")
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceWsImpl extends BaseWs implements InvoiceWs {

    @Inject
    InvoiceApi invoiceApi;

    @Override
    public CreateInvoiceResponseDto createInvoice(InvoiceDto invoiceDto) {
    	CreateInvoiceResponseDto result = new CreateInvoiceResponseDto();

        try {
        	result = invoiceApi.create(invoiceDto, getCurrentUser());  
        	if(invoiceDto.isAutoValidation() && invoiceDto.isReturnXml()){
        		result.setXmlInvoice(invoiceApi.getXMLInvoice(result.getInvoiceNumber(), invoiceDto.getInvoiceType(), getCurrentUser()));
        	}
        	if(invoiceDto.isAutoValidation() && invoiceDto.isReturnXml() && invoiceDto.isReturnPdf()){
        		result.setPdfInvoice(invoiceApi.getPdfInvoince(result.getInvoiceNumber(), invoiceDto.getInvoiceType(), getCurrentUser()));
        	}
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}

        return result;
    }

    @Override
    public CustomerInvoicesResponse findInvoice(String customerAccountCode) {
        CustomerInvoicesResponse result = new CustomerInvoicesResponse();

        try {
            result.setCustomerInvoiceDtoList(invoiceApi.list(customerAccountCode, getCurrentUser().getProvider()));

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoiceData(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {

            result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(String invoiceNumber) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceNumber, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}
        log.info("getXMLInvoice Response={}", result);
        return result;
    }
   

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceNumber, invoiceType, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}
        log.info("getXMLInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(String invoiceNumber) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoince(invoiceNumber, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}
        log.info("getPdfInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoince(invoiceNumber, invoiceType, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}
        log.info("getPdfInvoice Response={}", result);
        return result;
    }

	@Override
	public ActionStatus cancelInvoice(Long invoiceId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			invoiceApi.cancelInvoice(invoiceId, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus validateInvoice(Long invoiceId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			result.setMessage(invoiceApi.validateInvoice(invoiceId, getCurrentUser()));
		} catch (Exception e) {
			super.processException(e, result);
		}
		return result;
	}

}
