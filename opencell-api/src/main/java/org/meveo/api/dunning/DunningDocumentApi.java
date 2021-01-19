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

package org.meveo.api.dunning;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.dunning.DunningDocumentDto;
import org.meveo.api.dto.dunning.DunningDocumentResponseDto;
import org.meveo.api.dto.dunning.DunningDocumentsListResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningDocumentService;

/**
 * @author akadid abdelmounaim
 * @lastModifiedVersion 7.3
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class DunningDocumentApi extends BaseApi {

    /**
     * Default sort for list call.
     */
    private static final String DEFAULT_SORT_ORDER_ID = "id";

    @Inject
    private DunningDocumentService dunningDocumentService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoiceApi invoiceApi;

    @Inject
    private AccountOperationService accountOperationService;

    public DunningDocument create(DunningDocumentDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCustomerAccountCode())) {
            missingParameters.add("customerAccount");
        }
        handleMissingParameters(postData);

        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccountCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccountCode());
        }

        DunningDocument dunningDocument = new DunningDocument();
        dunningDocument.setCustomerAccount(customerAccount);

        if (postData.getDueInvoices() != null) {
            List<RecordedInvoice> ris = new ArrayList<>();
            for (InvoiceDto invoiceDto : postData.getDueInvoices()) {
                Invoice invoice = invoiceService.getInvoice(invoiceDto.getInvoiceNumber(), customerAccount);
                if (invoice == null) {
                    throw new EntityDoesNotExistsException(Invoice.class, invoiceDto.getInvoiceNumber(), "invoiceNumber");
                }
                if (invoice.getRecordedInvoice() == null) {
                    throw new EntityDoesNotExistsException(RecordedInvoice.class, invoiceDto.getInvoiceNumber(), "invoiceNumber");
                }
                invoice.setPaymentStatus(InvoicePaymentStatusEnum.DISPUTED);
                RecordedInvoice ri = invoice.getRecordedInvoice();
                ri.setDunningDocument(dunningDocument);
                ris.add(invoice.getRecordedInvoice());
            }
            dunningDocument.setDueInvoices(ris);
        }

        dunningDocumentService.create(dunningDocument);

        return dunningDocument;
    }

    public DunningDocument addPayments(String dunningDocumentId, List<PaymentDto> paymentDtos) throws MeveoApiException, BusinessException {

        DunningDocument dunningDocument = dunningDocumentService.findByCode(dunningDocumentId);
        if (dunningDocument == null) {
            throw new EntityDoesNotExistsException(DunningDocument.class, dunningDocumentId);
        }

        for (PaymentDto paymentDto : paymentDtos) {
            AccountOperation accountOperation = accountOperationService.findByReference(paymentDto.getReference());
            if (accountOperation == null) {
                throw new EntityDoesNotExistsException(Payment.class, paymentDto.getReference(), "reference");
            }
            if (!(accountOperation instanceof Payment)) {
                throw new EntityDoesNotExistsException(Payment.class, paymentDto.getReference(), "reference");
            }

            Payment payment = (Payment) accountOperation;
            payment.setDunningDocument(dunningDocument);
            dunningDocument.addPayment(payment);
        }

        dunningDocumentService.update(dunningDocument);

        return dunningDocument;
    }

    /**
     * Find DunningDocument
     *
     * @param dunningDocumentCode Dunning Document Code
     * @return instance of DunningDocumentResponseDto which contains the DunningDocument DTO
     * @throws MeveoApiException meveo api exception
     */
    public DunningDocumentResponseDto find(String dunningDocumentCode) throws MeveoApiException {
        DunningDocumentResponseDto result = new DunningDocumentResponseDto();

        DunningDocument dunningDocument = dunningDocumentService.findByCode(dunningDocumentCode);
        if (dunningDocument == null) {
            throw new EntityDoesNotExistsException(DunningDocument.class, dunningDocumentCode);
        }

        result.setDunningDocument(dunningDocumentToDto(dunningDocument));
        return result;

    }

    /**
     * List DunningDocuments
     *
     * @param mergedCF true if merging inherited CF
     * @param pagingAndFiltering paging and filtering.
     * @return instance of DunningDocumentsListDto which contains list of DunningDocument DTO
     * @throws MeveoApiException meveo api exception
     */
    public DunningDocumentsListResponseDto list(Boolean mergedCF, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        boolean merge = mergedCF != null && mergedCF;
        return list(pagingAndFiltering, CustomFieldInheritanceEnum.getInheritCF(true, merge));
    }

    public DunningDocumentsListResponseDto list(PagingAndFiltering pagingAndFiltering, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }

        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, org.primefaces.model.SortOrder.ASCENDING, null, pagingAndFiltering,
            DunningDocument.class);

        Long totalCount = dunningDocumentService.count(paginationConfiguration);

        DunningDocumentsListResponseDto result = new DunningDocumentsListResponseDto();

        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<DunningDocument> dunningDocuments = dunningDocumentService.list(paginationConfiguration);
            if (dunningDocuments != null) {
                for (DunningDocument dunningDocument : dunningDocuments) {
                    result.getDunningDocuments().getDunningDocuments().add(dunningDocumentToDto(dunningDocument));
                }
            }
        }

        return result;

    }

    /**
     * Convert dunningDocument dto to entity
     *
     * @param dunningDocument instance of DunningDocument to be mapped
     * @return instance of DunningDocumentDto
     */
    public DunningDocumentDto dunningDocumentToDto(DunningDocument dunningDocument) {
       
        DunningDocumentDto dto = new DunningDocumentDto();
        dto.setAuditable(dunningDocument);

        if (dunningDocument.getCustomerAccount() != null) {
            dto.setCustomerAccountCode(dunningDocument.getCustomerAccount().getCode());
        }
        if (dunningDocument.getSubscription() != null) {
            dto.setSubscriptionCode(dunningDocument.getSubscription().getCode());
        }
        if (dunningDocument.getDueInvoices() != null) {
            List<InvoiceDto> dueInvoices = new ArrayList<InvoiceDto>();
            for (RecordedInvoice recordedInvoice : dunningDocument.getDueInvoices()) {
                for (Invoice invoice : recordedInvoice.getInvoices()) {
                    dueInvoices.add(invoiceApi.invoiceToDto(invoice, false, null));
                }

            }
            dto.setDueInvoices(dueInvoices);
        }

        if (dunningDocument.getPayments() != null) {
            List<PaymentDto> paymentDtos = new ArrayList<PaymentDto>();
            for (Payment payment : dunningDocument.getPayments()) {
                paymentDtos.add(new PaymentDto(payment));
            }
            dto.setPayments(paymentDtos);
        }

        return dto;
    }
}