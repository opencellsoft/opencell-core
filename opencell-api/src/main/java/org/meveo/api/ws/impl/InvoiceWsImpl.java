package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

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
import org.meveo.api.dto.response.InvoicesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
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
            result = invoiceApi.create(invoiceDto);
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    @Deprecated
    public CustomerInvoicesResponse findInvoice(String customerAccountCode) {
        CustomerInvoicesResponse result = new CustomerInvoicesResponse();

        try {
            result.setCustomerInvoiceDtoList(invoiceApi.listByPresentInAR(customerAccountCode, false));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoiceData(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {

            result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(Long invoiceId, String invoiceNumber) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceId, invoiceNumber));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        log.info("getXMLInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(Long invoiceId, String invoiceNumber, String invoiceType) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceId, invoiceNumber, invoiceType));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        log.info("getXMLInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(Long invoiceId, String invoiceNumber) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoice(invoiceId, invoiceNumber));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        log.info("getPdfInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(Long invoiceId, String invoiceNumber, String invoiceType) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoice(invoiceId, invoiceNumber, invoiceType));
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
            invoiceApi.cancelInvoice(invoiceId);
        } catch (Exception e) {
            super.processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus validateInvoice(Long invoiceId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            result.setMessage(invoiceApi.validateInvoice(invoiceId));
        } catch (Exception e) {
            super.processException(e, result);
        }
        return result;
    }

    @Override
    public GetInvoiceResponseDto findInvoiceByIdOrType(Long id, String invoiceNumber, String invoiceType, boolean includeTransactions) {
        GetInvoiceResponseDto result = new GetInvoiceResponseDto();
        try {
            result.setInvoice(invoiceApi.find(id, invoiceNumber, invoiceType, includeTransactions));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    @Deprecated
    public CustomerInvoicesResponse listPresentInAR(String customerAccountCode) {
        CustomerInvoicesResponse result = new CustomerInvoicesResponse();
        try {
            result.setCustomerInvoiceDtoList(invoiceApi.listByPresentInAR(customerAccountCode, true));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateDraftInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {

            result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto, true));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    public InvoicesDto list(PagingAndFiltering pagingAndFiltering) {

        InvoicesDto result = new InvoicesDto();

        try {
            result = invoiceApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}