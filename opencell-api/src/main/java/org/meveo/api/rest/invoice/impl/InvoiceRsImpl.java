/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.invoice.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Optional;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
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
import org.meveo.api.dto.response.InvoicesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.invoice.InvoiceRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.service.billing.impl.InvoiceTypeService;

/**
 * The Class InvoiceRsImpl.
 * 
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
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
            result = invoiceApi.create(invoiceDto);
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
            if (invoiceDto.getSendByEmail()!=null && invoiceDto.getSendByEmail() && invoiceDto.isReturnPdf() != null && invoiceDto.isReturnPdf() && !invoiceDto.isDraft()) {
                invoiceDto.setCheckAlreadySent(true);
                invoiceDto.setInvoiceId(result.getInvoiceId());
                boolean res = invoiceApi.sendByEmail(invoiceDto, MailingTypeEnum.AUTO);
                result.setSentByEmail(res);
            }

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public CustomerInvoicesResponse find(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("returnPdf") Boolean returnPdf) {
        CustomerInvoicesResponse result = new CustomerInvoicesResponse();

        try {
            result.setCustomerInvoiceDtoList(invoiceApi.listByPresentInAR(customerAccountCode, false, (returnPdf != null && returnPdf.booleanValue())));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto) {
        GenerateInvoiceResponseDto result = new GenerateInvoiceResponseDto();
        try {
            result.setGenerateInvoiceResultDto(invoiceApi.generateInvoice(generateInvoiceRequestDto));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
            if (generateInvoiceRequestDto.getGeneratePDF() != null && generateInvoiceRequestDto.getGeneratePDF()) {
                List<GenerateInvoiceResultDto> invoicesResult = result.getGenerateInvoiceResultDto();
                invoicesResult = invoiceApi.sendByEmail(invoicesResult);
                result.setGenerateInvoiceResultDto(invoicesResult);
            }
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(Long invoiceId, String invoiceNumber) {
        return findXMLInvoiceWithType(invoiceId, invoiceNumber, invoiceTypeService.getCommercialCode());
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoice(GetXmlInvoiceRequestDto xmlInvoiceRequestDto) {
        String invoiceNumber = xmlInvoiceRequestDto.getInvoiceNumber();
        String invoiceType = xmlInvoiceRequestDto.getInvoiceType();
        Long invoiceId = Optional.ofNullable(xmlInvoiceRequestDto.getInvoiceId()).orElse(null);
        if (StringUtils.isBlank(invoiceType)) {
            invoiceType = invoiceTypeService.getCommercialCode();
        }
        return findXMLInvoiceWithType(invoiceId, invoiceNumber, invoiceType);
    }

    @Override
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(Long invoiceId, String invoiceNumber, String invoiceType) {
        GetXmlInvoiceResponseDto result = new GetXmlInvoiceResponseDto();
        try {

            result.setXmlContent(invoiceApi.getXMLInvoice(invoiceId, invoiceNumber, invoiceType));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        log.info("getXMLInvoice Response={}", result);
        return result;
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(Long invoiceId, String invoiceNumber) {
        return findPdfInvoiceWithType(invoiceId, invoiceNumber, invoiceTypeService.getCommercialCode());
    }

    @Override
    public GetPdfInvoiceResponseDto findPdfInvoice(GetPdfInvoiceRequestDto pdfInvoiceRequestDto) {
        GetPdfInvoiceResponseDto result = new GetPdfInvoiceResponseDto();
        String invoiceNumber = pdfInvoiceRequestDto.getInvoiceNumber();
        String invoiceType = pdfInvoiceRequestDto.getInvoiceType();
        Long invoiceId = Optional.ofNullable(pdfInvoiceRequestDto.getInvoiceId()).orElse(null);
        if (StringUtils.isBlank(invoiceType)) {
            invoiceType = invoiceTypeService.getCommercialCode();
        }
        try {

            result.setPdfContent(invoiceApi.getPdfInvoice(invoiceId, invoiceNumber, invoiceType, pdfInvoiceRequestDto.getGeneratePdf()));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
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
            processException(e, result.getActionStatus());
        }
        log.info("getPdfInvoice Response={}", result);
        return result;
    }

    @Override
    public ActionStatus cancel(Long invoiceId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            invoiceApi.cancelInvoice(invoiceId);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus validate(Long invoiceId) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            result.setMessage(invoiceApi.validateInvoice(invoiceId));
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public GetInvoiceResponseDto findInvoiceByIdOrType(Long id, String invoiceNumber, String invoiceType, boolean includeTransactions, Boolean includePdf, Boolean includeXml) {
        GetInvoiceResponseDto result = new GetInvoiceResponseDto();
        try {
            // #2236 : includePdf's default value = false.
            boolean isPdfToInclude = includePdf != null ? includePdf.booleanValue() : false;
            // #2236 : includeXml's default value = false
            boolean isXmlToInclude = includeXml != null ? includeXml.booleanValue() : false;

            result.setInvoice(invoiceApi.find(id, invoiceNumber, invoiceType, includeTransactions, isPdfToInclude, isXmlToInclude));
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    @Deprecated
    public CustomerInvoicesResponse listPresentInAR(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("includePdf") boolean includePdf) {
        CustomerInvoicesResponse result = new CustomerInvoicesResponse();
        try {
            result.setCustomerInvoiceDtoList(invoiceApi.listByPresentInAR(customerAccountCode, true, includePdf));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
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
            processException(e, result.getActionStatus());
        }
        log.info("generateInvoice Response={}", result);
        return result;
    }

    @Override
    public InvoicesDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        InvoicesDto result = new InvoicesDto();

        try {
            result = invoiceApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public InvoicesDto listPost(PagingAndFiltering pagingAndFiltering) {
        InvoicesDto result = new InvoicesDto();

        try {
            result = invoiceApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus sendByEmail(InvoiceDto invoiceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            boolean response = invoiceApi.sendByEmail(invoiceDto, MailingTypeEnum.MANUAL);
            result.setMessage("INVOICE_SENT_BY_EMAIL");
            if (!response) {
                result.setStatus(ActionStatusEnum.FAIL);
                throw new MeveoApiException("INVOICE_NOT_SENT_BY_EMAIL");
            }

        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }
}