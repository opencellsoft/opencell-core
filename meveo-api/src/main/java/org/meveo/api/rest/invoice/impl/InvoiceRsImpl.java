package org.meveo.api.rest.invoice.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.InvoiceRs;
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

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {

            result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());

        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(String invoiceNumber) {
        return findXMLInvoiceWithType(invoiceNumber, invoiceTypeService.getCommercialCode() );
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceNumber, invoiceType, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());

        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }
        log.info("getXMLInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(String InvoiceNumber) {
        return findPdfInvoiceWithType(InvoiceNumber, invoiceTypeService.getCommercialCode());
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(String InvoiceNumber, String invoiceType) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoince(InvoiceNumber, invoiceType, getCurrentUser()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());

        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
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
	public GetInvoiceResponseDto findInvoiceByIdOrType(Long id, String invoiceNumber, String invoiceType) {
		GetInvoiceResponseDto result = new GetInvoiceResponseDto();
		try {
            result.setInvoice(invoiceApi.find(id, invoiceNumber, invoiceType, getCurrentUser().getProvider()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
	}
		@Override
		public CustomerInvoicesResponse invoicesWithAccountOperation(@QueryParam("customerAccountCode") String customerAccountCode) {
			CustomerInvoicesResponse result = new CustomerInvoicesResponse();
			try {
				result.setCustomerInvoiceDtoList(invoiceApi.invoicesWithAccountOperation(customerAccountCode, getCurrentUser().getProvider()));
			} catch (MeveoApiException e) {
				result.getActionStatus().setErrorCode(e.getErrorCode());
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
			} catch (Exception e) {
				log.error("Failed to execute API", e);
				result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
				result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
				result.getActionStatus().setMessage(e.getMessage());
			}
			return result;
		}
	
}
