package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.InvoiceWs;
import org.meveo.model.billing.InvoiceTypeEnum;

@WebService(serviceName = "InvoiceWs", endpointInterface = "org.meveo.api.ws.InvoiceWs")
@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceWsImpl extends BaseWs implements InvoiceWs {

    @Inject
    InvoiceApi invoiceApi;

    @Override
    public InvoiceCreationResponse createInvoice(InvoiceDto invoiceDto) {
        InvoiceCreationResponse result = new InvoiceCreationResponse();

        try {
            String invoiceNumber = invoiceApi.create(invoiceDto, getCurrentUser());
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
            result.setInvoiceNumber(invoiceNumber);

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
    public CustomerInvoicesResponse findInvoice(String customerAccountCode) {
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
    public GenerateInvoiceResponseDto generateInvoiceData(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
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
        return findXMLInvoiceWithType(invoiceNumber, InvoiceTypeEnum.COMMERCIAL.name());
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
    public GetPdfInvoiceResponseDto findPdfInvoice(String invoiceNumber) {
        return findPdfInvoiceWithType(invoiceNumber, InvoiceTypeEnum.COMMERCIAL.name());
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoince(invoiceNumber, invoiceType, getCurrentUser()));
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

}
