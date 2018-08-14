package org.meveo.api.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

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
import org.meveo.api.ws.Invoice4_2Ws;
import org.meveo.service.billing.impl.InvoiceTypeService;

@Deprecated
@WebService(serviceName = "Invoice4_2Ws", endpointInterface = "org.meveo.api.ws.Invoice4_2Ws")
@Interceptors({ WsRestApiInterceptor.class })
public class Invoice4_2WsImpl extends BaseWs implements Invoice4_2Ws {

    @Inject
    Invoice4_2Api invoiceApi;

    @Inject
    InvoiceTypeService invoiceTypeService;

    @Override
    public InvoiceCreationResponse createInvoice(Invoice4_2Dto invoiceDto) {
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
    public CustomerInvoices4_2Response findInvoice(String customerAccountCode) {
        CustomerInvoices4_2Response result = new CustomerInvoices4_2Response();

        try {
            result.setCustomerInvoiceDtoList(invoiceApi.list(customerAccountCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoiceData(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
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
    public GetPdfInvoiceResponseDto findPdfInvoice(String invoiceNumber) {
        return findPdfInvoiceWithType(invoiceNumber, invoiceTypeService.getCommercialCode());
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(String invoiceNumber, String invoiceType) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        try {

            result.setPdfContent(invoiceApi.getPdfInvoince(invoiceNumber, invoiceType));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getPdfInvoice Response={}", result);
        return result;
    }
}