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

package org.meveo.api.invoice;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.DiscountInvoiceAggregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.SubcategoryInvoiceAgregateAmountDto;
import org.meveo.api.dto.TaxInvoiceAggregateDto;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstanceDto;
import org.meveo.api.dto.payment.PaymentScheduleInstancesDto;
import org.meveo.api.dto.payment.RecordedInvoiceDto;
import org.meveo.api.dto.response.InvoicesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.payment.PaymentApi;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.SubcategoryInvoiceAgregateAmount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.filter.Filter;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.WriteOff;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.meveo.service.order.OrderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;
import org.primefaces.model.SortOrder;

/**
 * CRUD API for managing {@link Invoice}.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdelmounaim Akadid
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@Stateless
public class InvoiceApi extends BaseApi {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private SellerService sellerService;

    @Inject
    private OrderService orderService;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private FilteredListApi filteredListApi;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private PaymentApi paymentApi;

    @Inject
    @MeveoParamBean
    private ParamBean paramBean;

    @Inject
    protected ResourceBundle resourceMessages;

    @Inject
    protected WorkflowInstanceService workflowInstanceService;

    /**
     * Create an invoice based on the DTO object data and current user
     *
     * @param invoiceDTO invoice DTO
     * @return CreateInvoiceResponseDto
     * @throws MeveoApiException Meveo Api exception
     * @throws BusinessException Business exception
     * @throws Exception exception
     */
    public CreateInvoiceResponseDto create(InvoiceDto invoiceDTO) throws MeveoApiException, BusinessException, Exception {

        if (invoiceDTO.getSendByEmail() == null) {
            invoiceDTO.setSendByEmail(true);
        }
        log.debug("InvoiceDto:" + JsonUtils.toJson(invoiceDTO, true));
        validateInvoiceDto(invoiceDTO);

        BillingAccount billingAccount = billingAccountService.findByCode(invoiceDTO.getBillingAccountCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, invoiceDTO.getBillingAccountCode());
        }
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceDTO.getInvoiceType());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceDTO.getInvoiceType());
        }

        Seller seller = this.getSeller(invoiceDTO, billingAccount);
        Invoice invoice = invoiceService.createInvoice(invoiceDTO, seller, billingAccount, invoiceType);

        CreateInvoiceResponseDto response = new CreateInvoiceResponseDto();
        response.setInvoiceId(invoice.getId());
        response.setAmountWithoutTax(invoice.getAmountWithoutTax());
        response.setAmountTax(invoice.getAmountTax());
        response.setAmountWithTax(invoice.getAmountWithTax());
        response.setDueDate(invoice.getDueDate());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setNetToPay(invoice.getNetToPay());
        response.setInvoiceNumber(invoice.getInvoiceNumber());

        if (invoice.isDraft()) {
            this.setDraftSetting(invoiceDTO, seller, invoice);
        }
        // pdf and xml are added to response if requested
        if ((invoiceDTO.isReturnXml() != null && invoiceDTO.isReturnXml()) || (invoiceDTO.isReturnPdf() != null && invoiceDTO.isReturnPdf())) {
            invoice = invoiceService.produceInvoiceXml(invoice);
            String invoiceXml = invoiceService.getInvoiceXml(invoice);
            if ((invoiceDTO.isReturnXml() != null && invoiceDTO.isReturnXml())) {
                response.setXmlInvoice(invoiceXml);
                response.setXmlFilename(invoice.getXmlFilename());
            }
        }

        if (invoiceDTO.isReturnPdf() != null && invoiceDTO.isReturnPdf()) {
            invoice = invoiceService.produceInvoicePdf(invoice);
            byte[] invoicePdf = invoiceService.getInvoicePdf(invoice);
            response.setPdfInvoice(invoicePdf);
            response.setPdfFilename(invoice.getPdfFilename());
        }

        if (invoice.isDraft()) {
            invoiceService.cancelInvoice(invoice);
        } else {
            invoiceService.update(invoice);
        }

        return response;
    }

    private void setDraftSetting(InvoiceDto invoiceDTO, Seller seller, Invoice invoice) throws BusinessException {
        if (invoice.getInvoiceNumber() == null) {
            invoice = serviceSingleton.assignInvoiceNumber(invoice);
        }
        InvoiceType draftInvoiceType = invoiceTypeService.getDefaultDraft();
        InvoiceTypeSellerSequence invoiceTypeSellerSequence = draftInvoiceType.getSellerSequenceByType(seller);
        String prefix = (invoiceTypeSellerSequence != null) ? invoiceTypeSellerSequence.getPrefixEL() : "DRAFT_";
        invoice.setInvoiceNumber(prefix + invoice.getInvoiceNumber());
        invoice.assignTemporaryInvoiceNumber();
        invoiceDTO.setReturnPdf(Boolean.FALSE);
        invoiceDTO.setReturnXml(Boolean.FALSE);
    }

    private Seller getSeller(InvoiceDto invoiceDTO, BillingAccount billingAccount) throws EntityDoesNotExistsException {
        Seller seller = null;
        if (invoiceDTO.getSellerCode() != null) {
            seller = sellerService.findByCode(invoiceDTO.getSellerCode());
            if (seller == null) {
                throw new EntityDoesNotExistsException(Seller.class, invoiceDTO.getSellerCode());
            }
        } else {
            seller = billingAccount.getCustomerAccount().getCustomer().getSeller();
        }
        return seller;
    }

    public List<InvoiceDto> listByPresentInAR(String customerAccountCode, boolean isPresentInAR, boolean includePdf) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        List<InvoiceDto> customerInvoiceDtos = new ArrayList<InvoiceDto>();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        for (BillingAccount billingAccount : customerAccount.getBillingAccounts()) {
            List<Invoice> invoiceList = new ArrayList<Invoice>();
            if (isPresentInAR) {
                invoiceList = invoiceService.getInvoicesWithAccountOperation(billingAccount);
            } else {
                invoiceList = billingAccount.getInvoices();
            }
            for (Invoice invoice : invoiceList) {
                InvoiceDto customerInvoiceDto = invoiceToDto(invoice, false, includePdf);
                customerInvoiceDtos.add(customerInvoiceDto);
            }
        }

        return customerInvoiceDtos;
    }

    /**
     * Update the billing run
     * 
     * @param billingRun billing run
     * @param status status of billing run
     * @param billingAccountNumber billing account number
     * @param billableBillingAcountNumber billable Billing account number
     * 
     * @return the billing run
     * @throws BusinessException Business exception
     */
    public BillingRun updateBR(BillingRun billingRun, BillingRunStatusEnum status, Integer billingAccountNumber, Integer billableBillingAcountNumber) throws BusinessException {
        billingRun.setStatus(status);
        if (billingAccountNumber != null) {
            billingRun.setBillingAccountNumber(billingAccountNumber);
        }
        if (billableBillingAcountNumber != null) {
            billingRun.setBillableBillingAcountNumber(billableBillingAcountNumber);
        }
        return billingRunService.update(billingRun);
    }

    /**
     * Validate the Billing run.
     * 
     * @param billingRun billing run to validate
     * @throws BusinessException business exception
     */
    public void validateBR(BillingRun billingRun) throws BusinessException {
        billingRunService.forceValidate(billingRun.getId());
    }

    /**
     * Launch all the invoicing process for a given billingAccount, that's mean :
     * <ul>
     * <li>Create an exeptionnal billingRun with given dates
     * <li>Validate the preinvoicing resport
     * <li>Validate the postinvoicing report
     * <li>Vaidate the BillingRun
     * </ul>
     * 
     * @param generateInvoiceRequestDto generate invoice request
     * 
     * @return The invoiceNumber invoice number.
     * @throws BusinessException business exception
     * @throws MeveoApiException meveo api exception
     * @throws FileNotFoundException file not found exception
     * @throws ImportInvoiceException import invoice exception
     * @throws InvoiceExistException invoice exist exception
     */
    public List<GenerateInvoiceResultDto> generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto)
            throws BusinessException, MeveoApiException, FileNotFoundException, InvoiceExistException, ImportInvoiceException {
        return generateInvoice(generateInvoiceRequestDto, false);
    }

    public List<GenerateInvoiceResultDto> generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto, boolean isDraft)
            throws BusinessException, MeveoApiException, FileNotFoundException, InvoiceExistException, ImportInvoiceException {

        if (generateInvoiceRequestDto == null) {
            missingParameters.add("generateInvoiceRequest");
            handleMissingParameters();
            return null;
        } else {
            if (StringUtils.isBlank(generateInvoiceRequestDto.getBillingAccountCode())) {
                if (StringUtils.isBlank(generateInvoiceRequestDto.getTargetCode())) {
                    missingParameters.add("targetCode");
                }
                if (StringUtils.isBlank(generateInvoiceRequestDto.getTargetType())) {
                    missingParameters.add("targetType");
                }

                if (generateInvoiceRequestDto.getInvoicingDate() == null) {
                    missingParameters.add("invoicingDate");
                }
            }
        }

        handleMissingParameters();

        IBillableEntity entity = null;
        if (StringUtils.isBlank(generateInvoiceRequestDto.getBillingAccountCode())) {
            if (BillingEntityTypeEnum.BILLINGACCOUNT.toString().equalsIgnoreCase(generateInvoiceRequestDto.getTargetType())) {
                entity = billingAccountService.findByCode(generateInvoiceRequestDto.getTargetCode(), Arrays.asList("billingRun"));
            } else if (BillingEntityTypeEnum.SUBSCRIPTION.toString().equalsIgnoreCase(generateInvoiceRequestDto.getTargetType())) {
                entity = subscriptionService.findByCode(generateInvoiceRequestDto.getTargetCode(), Arrays.asList("billingRun"));
            } else if (BillingEntityTypeEnum.ORDER.toString().equalsIgnoreCase(generateInvoiceRequestDto.getTargetType())) {
                entity = orderService.findByCodeOrExternalId(generateInvoiceRequestDto.getTargetCode());
            }
        } else {
            if (!StringUtils.isBlank(generateInvoiceRequestDto.getOrderNumber())) {
                entity = orderService.findByCodeOrExternalId(generateInvoiceRequestDto.getOrderNumber());
            } else {
                entity = billingAccountService.findByCode(generateInvoiceRequestDto.getBillingAccountCode(), Arrays.asList("billingRun"));
            }
        }

        if (entity == null) {
            throw new EntityDoesNotExistsException(IBillableEntity.class, generateInvoiceRequestDto.getTargetCode());
        }

        Filter ratedTransactionFilter = null;
        if (generateInvoiceRequestDto.getFilter() != null) {
            ratedTransactionFilter = filteredListApi.getFilterFromDto(generateInvoiceRequestDto.getFilter());
            if (ratedTransactionFilter == null) {
                throw new EntityDoesNotExistsException(Filter.class, generateInvoiceRequestDto.getFilter().getCode());
            }
        }

        if (isDraft) {
            if (generateInvoiceRequestDto.getGeneratePDF() == null) {
                generateInvoiceRequestDto.setGeneratePDF(Boolean.TRUE);
            }
            if (generateInvoiceRequestDto.getGenerateAO() != null) {
                generateInvoiceRequestDto.setGenerateAO(Boolean.FALSE);
            }
        }

        boolean produceXml = (generateInvoiceRequestDto.getGenerateXML() != null && generateInvoiceRequestDto.getGenerateXML())
                || (generateInvoiceRequestDto.getGeneratePDF() != null && generateInvoiceRequestDto.getGeneratePDF());
        boolean producePdf = (generateInvoiceRequestDto.getGeneratePDF() != null && generateInvoiceRequestDto.getGeneratePDF());

        List<GenerateInvoiceResultDto> invoicesDtos = new ArrayList<>();
        ICustomFieldEntity customFieldEntity = new Invoice();
        customFieldEntity = this.populateCustomFields(generateInvoiceRequestDto.getCustomFields(), customFieldEntity, false);
        List<Invoice> invoices = invoiceService.generateInvoice(entity, generateInvoiceRequestDto, ratedTransactionFilter, isDraft, customFieldEntity.getCfValues());

        // For backward compatibility with API
        if (invoices == null || invoices.isEmpty()) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));
        }
        for (Invoice invoice : invoices) {
            if (isDraft && invoice.isPrepaid()) {
                invoiceService.cancelInvoice(invoice);
                continue;
            }
            invoice = invoiceService.retrieveIfNotManaged(invoice);

            // TODO AKK need to extract custom fields and use them inside the generateInvoice()
            // this.populateCustomFields(generateInvoiceRequestDto.getCustomFields(), invoice, false);

            GenerateInvoiceResultDto generateInvoiceResultDto = createGenerateInvoiceResultDto(invoice, produceXml, producePdf, generateInvoiceRequestDto.isIncludeRatedTransactions());
            invoicesDtos.add(generateInvoiceResultDto);
            if (isDraft) {
                invoiceService.cancelInvoice(invoice);
            }
        }

        return invoicesDtos;
    }

    public GenerateInvoiceResultDto createGenerateInvoiceResultDto(Invoice invoice, boolean includeXml, boolean includePdf, Boolean includeRatedTransactions) throws BusinessException {
        GenerateInvoiceResultDto dto = generateInvoiceResultToDto(invoice, includeRatedTransactions);

        if (invoiceService.isInvoicePdfExist(invoice)) {
            dto.setPdfFilename(invoice.getPdfFilename());
            if (includePdf) {
                dto.setPdf(invoiceService.getInvoicePdf(invoice));
            }
        }

        if (invoiceService.isInvoiceXmlExist(invoice)) {
            dto.setXmlFilename(invoice.getXmlFilename());
        }

        return dto;
    }

    public String getXMLInvoice(Long invoiceId, String invoiceNumber) throws FileNotFoundException, MissingParameterException, EntityDoesNotExistsException, BusinessException {
        return getXMLInvoice(invoiceId, invoiceNumber, invoiceTypeService.getDefaultCommertial().getCode());
    }

    public String getXMLInvoice(Long invoiceId, String invoiceNumber, String invoiceTypeCode) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("invoiceTypeCode");
        }
        handleMissingParameters();

        Invoice invoice = find(invoiceId, invoiceNumber, invoiceTypeCode);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber, "invoiceNumber", invoiceTypeCode, "invoiceTypeCode");
        }

        return invoiceService.getInvoiceXml(invoice);
    }

    /**
     * 
     * @param invoiceNumber invoice number
     * @param invoiceId invoice' id
     * @return invoice's pdf as bytes
     * @throws MissingParameterException missing pararmeter exception
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws Exception exception.
     */
    public byte[] getPdfInvoice(Long invoiceId, String invoiceNumber) throws MissingParameterException, EntityDoesNotExistsException, Exception {
        return getPdfInvoice(invoiceId, invoiceNumber, invoiceTypeService.getDefaultCommertial().getCode());
    }

    /**
     * Gets the pdf invoice.
     *
     * @param invoiceId the invoice id
     * @param invoiceNumber the invoice number
     * @param invoiceTypeCode the invoice type code
     * @param generatePdfIfNoExist the generate pdf if no exist
     * @return the pdf invoice
     * @throws MissingParameterException the missing parameter exception
     * @throws EntityDoesNotExistsException the entity does not exists exception
     * @throws Exception the exception
     */
    public byte[] getPdfInvoice(Long invoiceId, String invoiceNumber, String invoiceTypeCode, boolean generatePdfIfNoExist) throws MissingParameterException, EntityDoesNotExistsException, Exception {
        log.debug("getPdfInvoince  invoiceNumber:{}", invoiceNumber);

        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("invoiceTypeCode");
        }
        handleMissingParameters();
        Invoice invoice = find(invoiceId, invoiceNumber, invoiceTypeCode);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber, "invoiceNumber", invoiceTypeCode, "invoiceTypeCode");
        }
        if (invoice.isPrepaid()) {
            throw new BusinessException("Invoice PDF is disabled for prepaid invoice: " + invoice.getInvoiceNumber());
        }
        if (!invoiceService.isInvoicePdfExist(invoice)) {
            if (generatePdfIfNoExist) {
                invoiceService.produceInvoicePdf(invoice);
            }
        }
        return invoiceService.getInvoicePdf(invoice);

    }

    /**
     * @param invoiceId invoice's id.
     * @param invoiceNumber invoice number
     * @param invoiceTypeCode invoice type code
     * 
     * @return invoice pdf as bytes
     * @throws MissingParameterException missing parameter exception
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws Exception general exception.
     */
    public byte[] getPdfInvoice(Long invoiceId, String invoiceNumber, String invoiceTypeCode) throws MissingParameterException, EntityDoesNotExistsException, Exception {
        return getPdfInvoice(invoiceId, invoiceNumber, invoiceTypeCode, false);
    }

    /**
     * 
     * @param invoiceId invoice id
     * 
     * @return invoice number.
     * @throws MissingParameterException missing parameter exception
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws BusinessException business exception
     */
    public String validateInvoice(Long invoiceId) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(invoiceId)) {
            missingParameters.add("invoiceId");
        }
        handleMissingParameters();

        Invoice invoice = invoiceService.findById(invoiceId);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
        }
        invoice = serviceSingleton.assignInvoiceNumber(invoice);
        return invoice.getInvoiceNumber();
    }

    /**
     * 
     * @param invoiceId invoice id
     * 
     * @throws MissingParameterException issing parameter exception
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void cancelInvoice(Long invoiceId) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(invoiceId)) {
            missingParameters.add("invoiceId");
        }
        handleMissingParameters();
        Invoice invoice = invoiceService.findById(invoiceId);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
        }
        invoiceService.cancelInvoice(invoice);
    }

    /**
     * 
     * @param invoiceDTO invoie dto
     * 
     * @throws MissingParameterException missing parametter exception
     * @throws EntityDoesNotExistsException enity does not exist exception.
     */
    private void validateInvoiceDto(InvoiceDto invoiceDTO) throws MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(invoiceDTO.getInvoiceMode())) {
            missingParameters.add("invoiceMode");
        }
        if (StringUtils.isBlank(invoiceDTO.getBillingAccountCode())) {
            missingParameters.add("billingAccountCode");
        }
        if (StringUtils.isBlank(invoiceDTO.getDueDate())) {
            missingParameters.add("dueDate");
        }
        if (StringUtils.isBlank(invoiceDTO.getInvoiceDate())) {
            missingParameters.add("invoiceDate");
        }
        if (StringUtils.isBlank(invoiceDTO.getInvoiceType())) {
            missingParameters.add("invoiceType");
        }

        if ((invoiceDTO.getRatedTransactionsTolink() == null || invoiceDTO.getRatedTransactionsTolink().isEmpty()) && (StringUtils.isBlank(invoiceDTO.getCategoryInvoiceAgregates()) || invoiceDTO.getCategoryInvoiceAgregates().isEmpty())) {
            missingParameters.add("categoryInvoiceAgregates");
        }

        handleMissingParameters();

        for (CategoryInvoiceAgregateDto catInvAgrDto : invoiceDTO.getCategoryInvoiceAgregates()) {
            if (StringUtils.isBlank(catInvAgrDto.getCategoryInvoiceCode())) {
                missingParameters.add("categoryInvoiceAgregateDto.categoryInvoiceCode");
            }
            if (invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode()) == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, catInvAgrDto.getCategoryInvoiceCode());
            }
            if (catInvAgrDto.getListSubCategoryInvoiceAgregateDto() == null || catInvAgrDto.getListSubCategoryInvoiceAgregateDto().isEmpty()) {
                missingParameters.add("categoryInvoiceAgregateDto.listSubCategoryInvoiceAgregateDto");
            }
            handleMissingParameters();
            for (SubCategoryInvoiceAgregateDto subCatInvAgrDto : catInvAgrDto.getListSubCategoryInvoiceAgregateDto()) {
                if (StringUtils.isBlank(subCatInvAgrDto.getInvoiceSubCategoryCode())) {
                    missingParameters.add("subCategoryInvoiceAgregateDto.invoiceSubCategoryCode");
                }
                if (invoiceSubCategoryService.findByCode(subCatInvAgrDto.getInvoiceSubCategoryCode()) == null) {
                    throw new EntityDoesNotExistsException(InvoiceSubCategory.class, subCatInvAgrDto.getInvoiceSubCategoryCode());
                }

                if (InvoiceModeEnum.DETAILLED.name().equals(invoiceDTO.getInvoiceMode().name())) {
                    if (subCatInvAgrDto.getRatedTransactions() == null || subCatInvAgrDto.getRatedTransactions().isEmpty()) {
                        missingParameters.add("ratedTransactions");
                    }
                    handleMissingParameters();
                    for (RatedTransactionDto ratedTransactionDto : subCatInvAgrDto.getRatedTransactions()) {
                        if (StringUtils.isBlank(ratedTransactionDto.getCode())) {
                            missingParameters.add("ratedTransactions.code");
                        }
                        if (StringUtils.isBlank(ratedTransactionDto.getUsageDate())) {
                            missingParameters.add("ratedTransactions.usageDate");
                        }
                        if (StringUtils.isBlank(ratedTransactionDto.getUnitAmountWithoutTax())) {
                            missingParameters.add("ratedTransactions.unitAmountWithout");
                        }
                        if (StringUtils.isBlank(ratedTransactionDto.getQuantity())) {
                            missingParameters.add("ratedTransactions.quantity");
                        }
                    }
                } else {
                    if (StringUtils.isBlank(subCatInvAgrDto.getAmountWithoutTax())) {
                        missingParameters.add("subCategoryInvoiceAgregateDto.amountWithoutTax");
                    }
                }
            }
        }

        handleMissingParameters();

    }

    /**
     * 
     * @param id Invoice id. Either id or invoice number and type must be provided
     * @param invoiceNumber Invoice number
     * @param invoiceTypeCode Invoice type code
     * @param includeTransactions Should invoice list associated transactions
     * @return invoice dto
     * @throws MissingParameterException missing parameter exception
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public InvoiceDto find(Long id, String invoiceNumber, String invoiceTypeCode, boolean includeTransactions) throws MeveoApiException, BusinessException {
        return this.find(id, invoiceNumber, invoiceTypeCode, includeTransactions, false, false);
    }

    /**
     * @param includePdf if true return pdf , else if null or false don't return pdf
     * @param includeXml if true return pdf , else if null or false don't return xml
     * @param id Invoice id. Either id or invoice number and type must be provided
     * @param invoiceNumber Invoice number
     * @param invoiceTypeCode Invoice type code
     * @param includeTransactions Should invoice list associated transactions
     * @return invoice dto
     * @throws MissingParameterException missing parameter exception
     * @throws EntityDoesNotExistsException entity does not exist exception
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public InvoiceDto find(Long id, String invoiceNumber, String invoiceTypeCode, boolean includeTransactions, boolean includePdf, boolean includeXml) throws MeveoApiException, BusinessException {
        Invoice invoice = find(id, invoiceNumber, invoiceTypeCode);
        if (invoice == null) {
            if (id != null) {
                throw new EntityDoesNotExistsException(Invoice.class, id);

            } else {
                throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber, "invoiceNumber", invoiceTypeCode, "invoiceType");
            }
        }
        return invoiceToDto(invoice, includeTransactions, includePdf, includeXml);
    }

    private InvoiceDto invoiceToDto(Invoice invoice, boolean includeTransactions, boolean includePdf, boolean includeXml) {

        InvoiceDto invoiceDto = invoiceToDto(invoice, includeTransactions, null);

        this.setInvoicePdf(invoice, includePdf, invoiceDto);
        this.setInvoiceXml(invoice, includeXml, invoiceDto);

        return invoiceDto;
    }

    /**
     * Setting Invoice DTO with invoice pdf file contents
     * 
     * @param invoice Invoice
     * @param includePdf Include PDF file contents
     * @param invoiceDto Invoice DTO to set the PDF value to
     */
    private void setInvoicePdf(Invoice invoice, boolean includePdf, InvoiceDto invoiceDto) {

        if (invoice.isPrepaid()) {
            invoiceDto.setPdfFilename(null);
            return;
        }
        boolean pdfFileExists = invoiceService.isInvoicePdfExist(invoice);
        // Generate PDF file if requested, but not available yet
        if (includePdf && !pdfFileExists) {
            try {
                invoiceService.generateXmlAndPdfInvoice(invoice, false);
            } catch (BusinessException e) {
                log.error("Failed to generate XML and or PDF file for invoice " + invoice.getId());
            }
            pdfFileExists = invoiceService.isInvoicePdfExist(invoice);
        }
        if (pdfFileExists) {
            invoiceDto.setPdfFilename(invoice.getPdfFilename());
            if (includePdf) {
                try {
                    invoiceDto.setPdf(invoiceService.getInvoicePdf(invoice));
                } catch (BusinessException e) {
                    // Should not happen as file was found few lines above
                    log.error(e.getMessage(), e);
                }
            }
        } else {
            invoiceDto.setPdfFilename(null);
        }
    }

    /**
     * #2236 Populate Invoice DTO with invoice xml file infp
     * 
     * @param invoice Invoice
     * @param includeXml Include XML file contents
     * @param invoiceDto Invoice DTO to set the XML value to
     */
    private void setInvoiceXml(Invoice invoice, boolean includeXml, InvoiceDto invoiceDto) {

        boolean xmlExists = invoiceService.isInvoiceXmlExist(invoice);
        // Generate XML file if requested, but not available yet
        if (includeXml && !xmlExists) {
            try {
                invoiceService.produceInvoiceXml(invoice);
            } catch (BusinessException e) {
                log.error("Failed to generate XML file for invoice " + invoice.getId());
            }
            xmlExists = invoiceService.isInvoiceXmlExist(invoice);
        }
        if (xmlExists) {
            invoiceDto.setXmlFilename(invoice.getXmlFilename());
            if (includeXml) {
                try {
                    String xml = invoiceService.getInvoiceXml(invoice);
                    invoiceDto.setXml(xml != null ? Base64.encodeBase64String(xml.getBytes()) : null);

                } catch (BusinessException e) {
                    // Should not happen as file was found few lines above
                    log.error(e.getMessage(), e);
                }
            }
        } else {
            invoiceDto.setXmlFilename(null);
        }
    }

    public InvoiceDto invoiceToDto(Invoice invoice, boolean includeTransactions, boolean includePdf) {
        return this.invoiceToDto(invoice, includeTransactions, includePdf, false);
    }

    private Invoice find(Long id, String invoiceNumber, String invoiceTypeCode) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(invoiceNumber) && id == null) {
            missingParameters.add("id ,or");
            missingParameters.add("invoiceNumber");
            handleMissingParameters();
        }
        if (id != null) {
            return invoiceService.findById(id);
        }
        InvoiceType invoiceType = null;
        if (!StringUtils.isBlank(invoiceTypeCode)) {
            invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
            if (invoiceType == null) {
                throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
            }
        }
        if (invoiceType != null) {
            return invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType);
        }
        return invoiceService.getInvoiceByNumber(invoiceNumber);
    }

    /**
     * List invoices matching filtering and query criteria
     * 
     * @param pagingAndFiltering Paging and filtering criteria. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @return A list of invoices
     * @throws InvalidParameterException invalid parameter exception
     */
    public InvoicesDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {

        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, pagingAndFiltering, Invoice.class);

        Long totalCount = invoiceService.count(paginationConfig);

        InvoicesDto result = new InvoicesDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<Invoice> invoices = invoiceService.list(paginationConfig);
            for (Invoice invoice : invoices) {
                result.getInvoices().add(invoiceToDto(invoice, pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("transactions"), pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("pdf")));
            }
        }

        return result;
    }

    /**
     * Send the invoice by Email.
     *
     * @param invoiceDto The invoice DTO
     * @param mailingType The mailing type
     * @return True if sent, false else
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     * @throws BusinessException
     */
    public boolean sendByEmail(InvoiceDto invoiceDto, MailingTypeEnum mailingType) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(invoiceDto.getInvoiceId())) {
            missingParameters.add("invoiceId");
        }
        handleMissingParameters();
        Invoice invoice = invoiceService.findById(invoiceDto.getInvoiceId());
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceDto.getInvoiceId());
        }
        if (invoice.isPrepaid()) {
            return false;
        }
        if (MailingTypeEnum.AUTO.equals(mailingType) && invoice.isDontSend()) {
            return false;
        }
        if (MailingTypeEnum.AUTO.equals(mailingType) && invoice.getInvoiceType().equals(invoiceTypeService.getDefaultDraft())) {
            return false;
        }
        if (invoiceDto.isCheckAlreadySent() && !invoice.isAlreadySent()) {
            return invoiceService.sendByEmail(invoice, mailingType, invoiceDto.getOverrideEmail());
        }
        if (!invoiceDto.isCheckAlreadySent() && MailingTypeEnum.MANUAL.equals(mailingType)) {
            return invoiceService.sendByEmail(invoice, mailingType, invoiceDto.getOverrideEmail());
        }
        return false;

    }

    /**
     * Send a list of invoices
     *
     * @param invoicesResult the invoice result
     * @return GenerateInvoiceResultDto
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     * @throws BusinessException
     */
    public List<GenerateInvoiceResultDto> sendByEmail(List<GenerateInvoiceResultDto> invoicesResult) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        for (GenerateInvoiceResultDto invoiceResult : invoicesResult) {
            invoiceResult.setCheckAlreadySent(true);
            invoiceResult.setSentByEmail(false);
            if (sendByEmail(invoiceResult, MailingTypeEnum.AUTO)) {
                invoiceResult.setSentByEmail(true);
            }
        }
        return invoicesResult;
    }

    /**
     * Instantiates a new sub category invoice aggregate dto.
     *
     * @param subCategoryInvoiceAgregate SubCategory invoice aggregate
     * @param includeTransactions Should Rated transactions be detailed
     * @param dtoToUpdate DTO entity to fill information with
     */
    @SuppressWarnings("deprecation")
    private SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateToDto(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate, boolean includeTransactions, SubCategoryInvoiceAgregateDto dtoToUpdate) {

        SubCategoryInvoiceAgregateDto dto = dtoToUpdate == null ? new SubCategoryInvoiceAgregateDto() : dtoToUpdate;
        dto.setItemNumber(subCategoryInvoiceAgregate.getItemNumber());
        if (subCategoryInvoiceAgregate.getAccountingCode() != null) {
            dto.setAccountingCode(subCategoryInvoiceAgregate.getAccountingCode().getCode());
        }
        dto.setDescription(subCategoryInvoiceAgregate.getDescription());
        dto.setQuantity(subCategoryInvoiceAgregate.getQuantity());
        dto.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
        dto.setAmountTax(subCategoryInvoiceAgregate.getAmountTax());
        dto.setAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());

        dto.setInvoiceSubCategoryCode(subCategoryInvoiceAgregate.getInvoiceSubCategory().getCode());

        if (subCategoryInvoiceAgregate.getUserAccount() != null) {
            dto.setUserAccountCode(subCategoryInvoiceAgregate.getUserAccount().getCode());
        }

        if (!subCategoryInvoiceAgregate.getAmountsByTax().isEmpty()) {
            dto.setAmountsByTax(new ArrayList<SubcategoryInvoiceAgregateAmountDto>());

            for (Entry<Tax, SubcategoryInvoiceAgregateAmount> amount : subCategoryInvoiceAgregate.getAmountsByTax().entrySet()) {
                dto.getAmountsByTax().add(new SubcategoryInvoiceAgregateAmountDto(amount.getValue(), amount.getKey()));
            }
            dto.getAmountsByTax().sort(Comparator.comparing(SubcategoryInvoiceAgregateAmountDto::getAmountWithoutTax));
        }
        if (includeTransactions) {

            List<RatedTransaction> ratedTransactions = ratedTransactionService.getRatedTransactionsByInvoiceAggr(subCategoryInvoiceAgregate);

            List<RatedTransactionDto> ratedTransactionDtos = new ArrayList<>();

            for (RatedTransaction ratedTransaction : ratedTransactions) {
                ratedTransactionDtos.add(new RatedTransactionDto(ratedTransaction));
            }

            ratedTransactionDtos.sort(Comparator.comparing(RatedTransactionDto::getUsageDate).thenComparing(RatedTransactionDto::getAmountWithTax).thenComparing(RatedTransactionDto::getCode));

            dto.setRatedTransactions(ratedTransactionDtos);
        }

        return dto;
    }

    /**
     * Instantiates a new sub category invoice aggregate dto.
     *
     * @param subCategoryInvoiceAgregate the SubCategoryInvoiceAgregate entity
     */
    private DiscountInvoiceAggregateDto discountInvoiceAggregateToDto(SubCategoryInvoiceAgregate subCategoryInvoiceAgregate) {

        DiscountInvoiceAggregateDto dto = new DiscountInvoiceAggregateDto();
        subCategoryInvoiceAgregateToDto(subCategoryInvoiceAgregate, false, dto);

        dto.setDiscountPlanItemCode(subCategoryInvoiceAgregate.getDiscountPlanItem().getCode());
        dto.setDiscountPercent(subCategoryInvoiceAgregate.getDiscountPercent());

        if (!subCategoryInvoiceAgregate.getAmountsByTax().isEmpty()) {
            dto.setAmountsByTax(new ArrayList<SubcategoryInvoiceAgregateAmountDto>());

            for (Entry<Tax, SubcategoryInvoiceAgregateAmount> amount : subCategoryInvoiceAgregate.getAmountsByTax().entrySet()) {
                dto.getAmountsByTax().add(new SubcategoryInvoiceAgregateAmountDto(amount.getValue(), amount.getKey()));
            }
            dto.getAmountsByTax().sort(Comparator.comparing(SubcategoryInvoiceAgregateAmountDto::getAmountWithoutTax));
        }

        return dto;
    }

    /**
     * Instantiates a new category invoice aggregate dto
     * 
     * @param categoryAggregate Category invoice aggregate
     * @param includeTransactions Should Rated transactions be detailed in subcategory aggregate level
     */
    private CategoryInvoiceAgregateDto categoryInvoiceAgregateToDto(CategoryInvoiceAgregate categoryAggregate, boolean includeTransactions) {

        CategoryInvoiceAgregateDto dto = new CategoryInvoiceAgregateDto();

        dto.setCategoryInvoiceCode(categoryAggregate.getInvoiceCategory().getCode());
        dto.setDescription(categoryAggregate.getDescription());
        dto.setAmountWithoutTax(categoryAggregate.getAmountWithoutTax());
        dto.setAmountWithTax(categoryAggregate.getAmountWithTax());
        dto.setAmountTax(categoryAggregate.getAmountTax());
        dto.setItemNumber(categoryAggregate.getItemNumber());
        if (categoryAggregate.getUserAccount() != null) {
            dto.setUserAccountCode(categoryAggregate.getUserAccount().getCode());
        }

        List<DiscountInvoiceAggregateDto> discountAggregates = new ArrayList<>();
        List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto = new ArrayList<>();

        for (SubCategoryInvoiceAgregate subCategoryAggregate : categoryAggregate.getSubCategoryInvoiceAgregates()) {

            if (subCategoryAggregate.isDiscountAggregate()) {
                discountAggregates.add(discountInvoiceAggregateToDto(subCategoryAggregate));
            } else {
                listSubCategoryInvoiceAgregateDto.add(subCategoryInvoiceAgregateToDto(subCategoryAggregate, includeTransactions, null));
            }
        }
        if (!listSubCategoryInvoiceAgregateDto.isEmpty()) {
            listSubCategoryInvoiceAgregateDto.sort(Comparator.comparing(SubCategoryInvoiceAgregateDto::getInvoiceSubCategoryCode));
            dto.setListSubCategoryInvoiceAgregateDto(listSubCategoryInvoiceAgregateDto);
        }
        if (!discountAggregates.isEmpty()) {
            discountAggregates.sort(Comparator.comparing(DiscountInvoiceAggregateDto::getDiscountPlanItemCode));
            dto.setDiscountAggregates(discountAggregates);
        }

        return dto;
    }

    /**
     * Instantiates a new invoice dto. Note: does not fill in XML and PDF information
     * 
     * @param invoice Invoice
     * @param includeTransactions Should Rated transactions be detailed in subcategory aggregate level
     * @param dtoToUpdate DTO to fill with invoice information
     */
    public InvoiceDto invoiceToDto(Invoice invoice, boolean includeTransactions, InvoiceDto dtoToUpdate) {

        InvoiceDto dto = dtoToUpdate == null ? new InvoiceDto() : dtoToUpdate;

        dto.setAuditable(invoice);
        dto.setInvoiceId(invoice.getId());
        dto.setBillingAccountCode(invoice.getBillingAccount().getCode());
        Subscription subscription = invoice.getSubscription();
        if (subscription != null) {
            dto.setSubscriptionCode(subscription.getCode());
            dto.setSubscriptionId(subscription.getId());
            List<PaymentScheduleInstanceDto> instances = new ArrayList<PaymentScheduleInstanceDto>();
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                for (PaymentScheduleInstance psInstance : serviceInstance.getPsInstances()) {
                    PaymentScheduleInstanceDto psiDto = new PaymentScheduleInstanceDto(psInstance);
                    instances.add(psiDto);
                }
            }
            PaymentScheduleInstancesDto instancesDto = new PaymentScheduleInstancesDto();
            instancesDto.setInstances(instances);
            dto.setPaymentScheduleInstancesDto(instancesDto);
            BigDecimal writeOffAmount = BigDecimal.ZERO;
            for (AccountOperation ao : subscription.getAccountOperations()) {
                if (ao instanceof WriteOff) {
                    writeOffAmount.add(ao.getAmount());
                }
            }
            dto.setWriteOffAmount(writeOffAmount);
        }
        if (invoice.getOrder() != null) {
            dto.setOrderNumber(invoice.getOrder().getOrderNumber());
        }
        if (invoice.getSeller() != null) {
            dto.setSellerCode(invoice.getSeller().getCode());
        }
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());

        dto.setAmountWithoutTax(invoice.getAmountWithoutTax());
        dto.setAmountTax(invoice.getAmountTax());
        dto.setAmountWithTax(invoice.getAmountWithTax());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setPaymentMethod(invoice.getPaymentMethodType());
        dto.setInvoiceType(invoice.getInvoiceType().getCode());
        dto.setDueBalance(invoice.getDueBalance());
        dto.setXmlFilename(invoice.getXmlFilename());
        dto.setPdfFilename(invoice.getPdfFilename());
        dto.setDiscount(invoice.getDiscount());
        dto.setCheckAlreadySent(invoice.isAlreadySent());
        dto.setSentByEmail(invoice.isDontSend());
        dto.setIntialCollectionDate(invoice.getIntialCollectionDate());

        List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<>();
        List<TaxInvoiceAggregateDto> taxAggregates = new ArrayList<>();
        List<Long> listInvoiceIdToLink = new ArrayList<Long>();

        for (InvoiceAgregate invoiceAggregate : invoice.getInvoiceAgregates()) {
            if (invoiceAggregate instanceof CategoryInvoiceAgregate) {
                categoryInvoiceAgregates.add(categoryInvoiceAgregateToDto((CategoryInvoiceAgregate) invoiceAggregate, includeTransactions));
            } else if (invoiceAggregate instanceof TaxInvoiceAgregate) {
                taxAggregates.add(new TaxInvoiceAggregateDto((TaxInvoiceAgregate) invoiceAggregate));
            }
        }

        categoryInvoiceAgregates.sort(Comparator.comparing(CategoryInvoiceAgregateDto::getCategoryInvoiceCode));
        taxAggregates.sort(Comparator.comparing(TaxInvoiceAggregateDto::getTaxCode));

        for (Invoice inv : invoice.getLinkedInvoices()) {
            listInvoiceIdToLink.add(inv.getId());
        }

        if (!categoryInvoiceAgregates.isEmpty()) {
            dto.setCategoryInvoiceAgregates(categoryInvoiceAgregates);
        }
        if (!taxAggregates.isEmpty()) {
            dto.setTaxAggregates(taxAggregates);
        }
        if (!listInvoiceIdToLink.isEmpty()) {
            dto.setListInvoiceIdToLink(listInvoiceIdToLink);
        }

        RecordedInvoice recordedInvoice = invoice.getRecordedInvoice();
        if (recordedInvoice != null) {
            RecordedInvoiceDto recordedInvoiceDto = new RecordedInvoiceDto(recordedInvoice);
            dto.setRecordedInvoiceDto(recordedInvoiceDto);
            if (invoice.getRecordedInvoice().getPaymentHistories() != null && !invoice.getRecordedInvoice().getPaymentHistories().isEmpty()) {
                for (PaymentHistory ph : invoice.getRecordedInvoice().getPaymentHistories()) {
                    recordedInvoiceDto.getPaymentHistories().add(paymentApi.fromEntity(ph, false));
                }
            }

            DunningDocument dunningDocument = recordedInvoice.getDunningDocument();
            if (dunningDocument != null) {
                dto.setDunningEntryDate(dunningDocument.getAuditable().getCreated());
                dto.setDunningLastModification(dunningDocument.getAuditable().getUpdated());
                List<WorkflowInstance> workflows = workflowInstanceService.findByEntityIdAndClazz(dunningDocument.getId(), DunningDocument.class);
                if (workflows != null && !workflows.isEmpty()) {
                    dto.setDunningStatus(workflows.get(0).getCurrentStatus().getCode());
                }
                List<Payment> payments = dunningDocument.getPayments();
                if (payments != null && !payments.isEmpty()) {
                    List<PaymentHistory> paymentHistory = payments.get(0).getPaymentHistories();
                    if (paymentHistory != null) {
                        dto.setPaymentIncidents(paymentHistory.stream().map(x -> x.getErrorMessage()).collect(Collectors.toList()));
                    }
                    Date paymentDate = new Date(0);
                    for (Payment payment : payments) {
                        if (payment.getAuditable() != null && payment.getAuditable().getCreated().after(paymentDate)) {
                            paymentDate = payment.getAuditable().getCreated();
                        }
                    }
                    dto.setPaymentDate(paymentDate);
                }
            }
        }
        dto.setRealTimeStatus(invoice.getRealTimeStatus());
        dto.setNetToPay(invoice.getNetToPay());
        dto.setStatus(invoice.getStatus());
        dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(invoice));

        return dto;
    }

    /**
     * Instantiates a new generate invoice response dto. Note: does not fill in XML and PDF information
     * 
     * @param invoice Invoice
     * @param includeTransactions Should Rated transactions be detailed in subcategory aggregate level
     */
    public GenerateInvoiceResultDto generateInvoiceResultToDto(Invoice invoice, boolean includeTransactions) {

        GenerateInvoiceResultDto dto = new GenerateInvoiceResultDto();

        invoiceToDto(invoice, includeTransactions, dto);
        dto.setTemporaryInvoiceNumber(invoice.getTemporaryInvoiceNumber());
        dto.setInvoiceTypeCode(invoice.getInvoiceType().getCode());
        dto.setAmount(invoice.getAmount());
        if (invoice.getRecordedInvoice() != null) {
            dto.setAccountOperationId(invoice.getRecordedInvoice().getId());
        }

        return dto;
    }
}