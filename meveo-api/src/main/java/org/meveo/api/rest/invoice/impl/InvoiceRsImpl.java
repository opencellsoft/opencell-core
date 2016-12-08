package org.meveo.api.rest.invoice.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceRequestDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceRequestDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.InvoiceRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.billing.impl.InvoiceTypeService;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceRsImpl extends BaseRs implements InvoiceRs {

    @Inject
    private InvoiceApi invoiceApi;
    
    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Override
    public CreateInvoiceResponseDto create(InvoiceDto invoiceDto) {
    	CreateInvoiceResponseDto result = new CreateInvoiceResponseDto();

        try {
        	result = invoiceApi.create(invoiceDto, getCurrentUser());
        	if(invoiceDto.isAutoValidation()){
        		String invoiceXml = invoiceApi.getXMLInvoice(result.getInvoiceNumber(), invoiceDto.getInvoiceType(), getCurrentUser());
        		byte[] invoicePdf = invoiceApi.getPdfInvoince(result.getInvoiceNumber(), invoiceDto.getInvoiceType(), getCurrentUser());
        		if(invoiceDto.isReturnXml()){
        			result.setXmlInvoice(invoiceXml);
        		}
            	if(invoiceDto.isReturnPdf()){
            		result.setPdfInvoice(invoicePdf);
            	}
        	}
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		} catch (Exception e) {
			super.processException(e, result.getActionStatus());
		}

        return result;
    }

    @Override
    public CustomerInvoicesResponse find(@QueryParam("customerAccountCode") String customerAccountCode) {
    	CustomerInvoicesResponse result = new CustomerInvoicesResponse();

        try {
            result.setCustomerInvoiceDtoList(invoiceApi.list(customerAccountCode, getCurrentUser().getProvider()));

        } catch (Exception e) {
			processException(e, result.getActionStatus());
		}

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {
            result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
        	processException(e, result.getActionStatus());            
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(String invoiceNumber) {
        return findXMLInvoiceWithType(invoiceNumber, invoiceTypeService.getCommercialCode() );
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(GetXmlInvoiceRequestDto xmlInvoiceRequestDto) {
        String invoiceNumber = xmlInvoiceRequestDto.getInvoiceNumber();
        String invoiceType = xmlInvoiceRequestDto.getInvoiceType();
        if(StringUtils.isBlank(invoiceType)){
            invoiceType = invoiceTypeService.getCommercialCode();
        }
        return findXMLInvoiceWithType(invoiceNumber, invoiceType);
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceNumber, invoiceType, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
			processException(e, result.getActionStatus());
		}
        log.info("getXMLInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(String invoiceNumber) {
        return findPdfInvoiceWithType(invoiceNumber, invoiceTypeService.getCommercialCode());
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(GetPdfInvoiceRequestDto pdfInvoiceRequestDto) {
        String invoiceNumber = pdfInvoiceRequestDto.getInvoiceNumber();
        String invoiceType = pdfInvoiceRequestDto.getInvoiceType();
        if(StringUtils.isBlank(invoiceType)){
            invoiceType = invoiceTypeService.getCommercialCode();
        }
        return findPdfInvoiceWithType(invoiceNumber, invoiceType);
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoince(invoiceNumber, invoiceType, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
			processException(e, result.getActionStatus());
		}
        log.info("getPdfInvoice Response={}", result);
        return result;
    }


	@Override
	public ActionStatus cancel(Long invoiceId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			invoiceApi.cancelInvoice(invoiceId, getCurrentUser());
		} catch (Exception e) {
			super.processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus validate(Long invoiceId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			result.setMessage(invoiceApi.validateInvoice(invoiceId, getCurrentUser()));
		} catch (Exception e) {
			super.processException(e, result);
		}
		return result;
	}
	
	@Override
	public GetInvoiceResponseDto findInvoiceByIdOrType(Long id, String invoiceNumber, String invoiceType, boolean includeTransactions) {
		GetInvoiceResponseDto result = new GetInvoiceResponseDto();
		try {
            result.setInvoice(invoiceApi.find(id, invoiceNumber, invoiceType, includeTransactions, getCurrentUser().getProvider()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
			processException(e, result.getActionStatus());
		}

        return result;
	}
	@Override
	public CustomerInvoicesResponse listPresentInAR(@QueryParam("customerAccountCode") String customerAccountCode) {
		CustomerInvoicesResponse result = new CustomerInvoicesResponse();
		try {
			result.setCustomerInvoiceDtoList(invoiceApi.listPresentInAR(customerAccountCode, getCurrentUser().getProvider()));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public GenerateInvoiceResponseDto generateDraftInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
		GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
		try {
			result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto, true,getCurrentUser()));
			result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		log.info("generateInvoice Response={}", result);
		return result;
	}
	
}
