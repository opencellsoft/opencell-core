package org.meveo.api.rest.invoice.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.Invoice4_2Dto;
import org.meveo.api.dto.response.CustomerInvoices4_2Response;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.invoice.Invoice4_2Api;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.Invoice4_2Rs;
import org.meveo.service.billing.impl.InvoiceTypeService;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class Invoice4_2RsImpl extends BaseRs implements Invoice4_2Rs {

    @Inject
    private Invoice4_2Api invoiceApi;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Override
    public InvoiceCreationResponse create(Invoice4_2Dto invoiceDto) {
        InvoiceCreationResponse result = new InvoiceCreationResponse();

        try {
            String invoiceNumber = invoiceApi.create(invoiceDto);
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
            result.setInvoiceNumber(invoiceNumber);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public CustomerInvoices4_2Response find(@QueryParam("customerAccountCode") String customerAccountCode) {
        CustomerInvoices4_2Response result = new CustomerInvoices4_2Response();

        try {
            result.setCustomerInvoiceDtoList(invoiceApi.list(customerAccountCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {
            GenerateInvoiceResultDto generateInvoiceResultDto = invoiceApi.generateInvoice(generateInvoiceRequestDto);
            List<GenerateInvoiceResultDto> generateInvoiceResultDtoList = new ArrayList<GenerateInvoiceResultDto>();
            generateInvoiceResultDtoList.add(generateInvoiceResultDto);
            result.setGenerateInvoiceResultDto(generateInvoiceResultDtoList);
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(String invoiceNumber) {
        return findXMLInvoiceWithType(invoiceNumber, invoiceTypeService.getCommercialCode());
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceNumber, invoiceType));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
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

            result.setPdfContent(invoiceApi.getPdfInvoince(InvoiceNumber, invoiceType));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getPdfInvoice Response={}", result);
        return result;
    }

}
