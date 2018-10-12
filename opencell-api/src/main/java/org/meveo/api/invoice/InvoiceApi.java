package org.meveo.api.invoice;

import static org.meveo.commons.utils.NumberUtils.getRoundingMode;
import static org.meveo.commons.utils.NumberUtils.round;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.RecordedInvoiceDto;
import org.meveo.api.dto.response.InvoicesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.IBillableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
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
 * @lastModifiedVersion 5.1 
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
    private OrderService orderService;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private FilteredListApi filteredListApi;

    @Inject
    @MeveoParamBean
    private ParamBean paramBean;

    /**
     * Create an invoice based on the DTO object data and current user
     * 
     * @param invoiceDTO invoice DTO
     * 
     * @return CreateInvoiceResponseDto
     * @throws MeveoApiException Meveo Api exception
     * @throws BusinessException Business exception
     * @throws Exception exception
     */
    public CreateInvoiceResponseDto create(InvoiceDto invoiceDTO) throws MeveoApiException, BusinessException, Exception {
        log.debug("InvoiceDto:" + JsonUtils.toJson(invoiceDTO, true));
        validateInvoiceDto(invoiceDTO);       
        return invoiceService.create(invoiceDTO);
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
        	if(StringUtils.isBlank(generateInvoiceRequestDto.getBillingAccountCode())) {
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
        if(StringUtils.isBlank(generateInvoiceRequestDto.getBillingAccountCode())) {
	        if(BillingEntityTypeEnum.BILLINGACCOUNT.toString().equalsIgnoreCase(generateInvoiceRequestDto.getTargetType())) {
	            entity = billingAccountService.findByCode(generateInvoiceRequestDto.getTargetCode(), Arrays.asList("billingRun"));
	        } else if(BillingEntityTypeEnum.SUBSCRIPTION.toString().equalsIgnoreCase(generateInvoiceRequestDto.getTargetType())) {
	            entity = subscriptionService.findByCode(generateInvoiceRequestDto.getTargetCode(), Arrays.asList("billingRun"));
	        } else if(BillingEntityTypeEnum.ORDER.toString().equalsIgnoreCase(generateInvoiceRequestDto.getTargetType())) {
	            entity = orderService.findByCodeOrExternalId(generateInvoiceRequestDto.getTargetCode());
	        }
        } else {
        	if(!StringUtils.isBlank(generateInvoiceRequestDto.getOrderNumber())) {
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
        boolean generateAO = generateInvoiceRequestDto.getGenerateAO() != null && generateInvoiceRequestDto.getGenerateAO();

        List<GenerateInvoiceResultDto> invoicesDtos = new ArrayList<>();
        List<Invoice> invoices = invoiceService.generateInvoice(entity, generateInvoiceRequestDto, ratedTransactionFilter, isDraft);
        if(invoices != null) {
	        for(Invoice invoice : invoices) {
	            this.populateCustomFields(generateInvoiceRequestDto.getCustomFields(), invoice, false);
	            invoiceService.produceFilesAndAO(produceXml, producePdf, generateAO, invoice, isDraft);
	            
	            GenerateInvoiceResultDto generateInvoiceResultDto = createGenerateInvoiceResultDto(invoice, produceXml, producePdf);
	            invoicesDtos.add(generateInvoiceResultDto);
	            if (isDraft) {
	                invoiceService.cancelInvoice(invoice);
	            }   
	        }
        }
        
        return invoicesDtos;
    }
    
    public GenerateInvoiceResultDto createGenerateInvoiceResultDto(Invoice invoice, boolean includeXml, boolean includePdf) throws BusinessException {
        GenerateInvoiceResultDto dto = new GenerateInvoiceResultDto();
        dto.setInvoiceId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setTemporaryInvoiceNumber(invoice.getTemporaryInvoiceNumber());
        dto.setInvoiceTypeCode(invoice.getInvoiceType().getCode());
        dto.setAmount(invoice.getAmount());
        dto.setAmountWithoutTax(invoice.getAmountWithoutTax());
        dto.setAmountWithTax(invoice.getAmountWithTax());
        dto.setAmountTax(invoice.getAmountTax());
        dto.setDiscount(invoice.getDiscount());
        if (invoice.getSubscription() != null) {
            dto.setSubscriptionCode(invoice.getSubscription().getCode());
        }
        if (invoice.getBillingAccount() != null) {
            dto.setBillingAccountCode(invoice.getBillingAccount().getCode());
        }

        if (invoiceService.isInvoicePdfExist(invoice)) {
            dto.setPdfFilename(invoice.getPdfFilename());
            if (includePdf) {
                dto.setPdf(invoiceService.getInvoicePdf(invoice));
            }
        }

        if (invoiceService.isInvoiceXmlExist(invoice)) {
            dto.setXmlFilename(invoice.getXmlFilename());
        }

        if (invoice.getRecordedInvoice() != null) {
            dto.setAccountOperationId(invoice.getRecordedInvoice().getId());
        }

        List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = new ArrayList<>();
        subCategoryInvoiceAgregates = invoiceAgregateService.findDiscountAggregates(invoice);

        for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : subCategoryInvoiceAgregates) {
            SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto(subCategoryInvoiceAgregate);
            dto.getDiscountAggregates().add(subCategoryInvoiceAgregateDto);
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
    public byte[] getPdfInvoice(Long invoiceId, String invoiceNumber, String invoiceTypeCode, boolean generatePdfIfNoExist)
            throws MissingParameterException, EntityDoesNotExistsException, Exception {
        log.debug("getPdfInvoince  invoiceNumber:{}", invoiceNumber);

        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("invoiceTypeCode");
        }
        handleMissingParameters();
        Invoice invoice = find(invoiceId, invoiceNumber, invoiceTypeCode);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber, "invoiceNumber", invoiceTypeCode, "invoiceTypeCode");
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
        invoiceService.assignInvoiceNumber(invoice);
        invoice = invoiceService.update(invoice);
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

        if (StringUtils.isBlank(invoiceDTO.getCategoryInvoiceAgregates()) || invoiceDTO.getCategoryInvoiceAgregates().isEmpty()) {
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
    public InvoiceDto find(Long id, String invoiceNumber, String invoiceTypeCode, boolean includeTransactions)
            throws MeveoApiException, BusinessException {
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
    public InvoiceDto find(Long id, String invoiceNumber, String invoiceTypeCode, boolean includeTransactions, boolean includePdf, boolean includeXml)
            throws MeveoApiException, BusinessException {
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

    /**
     * 
     * @param tax taxe
     * @param amountWithoutTax amount with tax
     * @return amount with tax.
     */
    private BigDecimal getAmountWithTax(Tax tax, BigDecimal amountWithoutTax) {
        Integer rounding = appProvider.getRounding() == null ? 2 : appProvider.getRounding();
        BigDecimal ttc = amountWithoutTax.add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100), rounding, getRoundingMode(appProvider.getRoundingMode())));
        return ttc;
    }

    /**
     * 
     * @param amountWithTax amount with tax
     * @param amountWithoutTax amount without tax
     * @return tax amount.
     */
    private BigDecimal getAmountTax(BigDecimal amountWithTax, BigDecimal amountWithoutTax) {
        return amountWithTax.subtract(amountWithoutTax);
    }

    private InvoiceDto invoiceToDto(Invoice invoice, boolean includeTransactions, boolean includePdf, boolean includeXml) {

        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setAuditable(invoice);
        invoiceDto.setInvoiceId(invoice.getId());
        invoiceDto.setBillingAccountCode(invoice.getBillingAccount().getCode());
        invoiceDto.setInvoiceDate(invoice.getInvoiceDate());
        invoiceDto.setDueDate(invoice.getDueDate());

        invoiceDto.setAmountWithoutTax(invoice.getAmountWithoutTax());
        invoiceDto.setAmountTax(invoice.getAmountTax());
        invoiceDto.setAmountWithTax(invoice.getAmountWithTax());
        invoiceDto.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDto.setPaymentMethod(invoice.getPaymentMethodType());
        invoiceDto.setInvoiceType(invoice.getInvoiceType().getCode());
        invoiceDto.setDueBalance(invoice.getDueBalance());

        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof SubCategoryInvoiceAgregate || invoiceAgregate instanceof TaxInvoiceAgregate) {
                continue;

            } else if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                CategoryInvoiceAgregateDto categoryInvoiceAgregateDto = new CategoryInvoiceAgregateDto();
                categoryInvoiceAgregateDto.setCategoryInvoiceCode(((CategoryInvoiceAgregate) invoiceAgregate).getInvoiceCategory().getCode());

                SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();
                subCategoryInvoiceAgregateDto.setType("R");
                subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
                if (invoiceAgregate.getAccountingCode() != null) {
                    subCategoryInvoiceAgregateDto.setAccountingCode(invoiceAgregate.getAccountingCode().getCode());
                }
                subCategoryInvoiceAgregateDto.setDescription(invoiceAgregate.getDescription());
                subCategoryInvoiceAgregateDto.setQuantity(invoiceAgregate.getQuantity());
                subCategoryInvoiceAgregateDto.setDiscount(invoiceAgregate.getDiscount());
                subCategoryInvoiceAgregateDto.setAmountWithoutTax(invoiceAgregate.getAmountWithoutTax());
                subCategoryInvoiceAgregateDto.setAmountTax(invoiceAgregate.getAmountTax());
                subCategoryInvoiceAgregateDto.setAmountWithTax(invoiceAgregate.getAmountWithTax());
                subCategoryInvoiceAgregateDto.setUserAccountCode(invoiceAgregate.getUserAccount() == null ? null : invoiceAgregate.getUserAccount().getCode());

                categoryInvoiceAgregateDto.getListSubCategoryInvoiceAgregateDto().add(subCategoryInvoiceAgregateDto);

                invoiceDto.getCategoryInvoiceAgregates().add(categoryInvoiceAgregateDto);

                for (SubCategoryInvoiceAgregate subCategoryAggregate : ((CategoryInvoiceAgregate) invoiceAgregate).getSubCategoryInvoiceAgregates()) {
                    subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();
                    subCategoryInvoiceAgregateDto.setType("F");
                    subCategoryInvoiceAgregateDto.setInvoiceSubCategoryCode(subCategoryAggregate.getInvoiceSubCategory().getCode());
                    subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
                    if (invoiceAgregate.getAccountingCode() != null) {
                        subCategoryInvoiceAgregateDto.setAccountingCode(invoiceAgregate.getAccountingCode().getCode());
                    }
                    subCategoryInvoiceAgregateDto.setDescription(invoiceAgregate.getDescription());
                    subCategoryInvoiceAgregateDto.setQuantity(invoiceAgregate.getQuantity());
                    subCategoryInvoiceAgregateDto.setDiscount(invoiceAgregate.getDiscount());
                    subCategoryInvoiceAgregateDto.setAmountWithoutTax(invoiceAgregate.getAmountWithoutTax());
                    subCategoryInvoiceAgregateDto.setAmountTax(invoiceAgregate.getAmountTax());
                    subCategoryInvoiceAgregateDto.setAmountWithTax(invoiceAgregate.getAmountWithTax());
                    subCategoryInvoiceAgregateDto.setUserAccountCode(invoiceAgregate.getUserAccount() == null ? null : invoiceAgregate.getUserAccount().getCode());

                    if (includeTransactions) {

                        List<RatedTransaction> ratedTransactions = ratedTransactionService.getListByInvoiceAndSubCategory(invoice, subCategoryAggregate.getInvoiceSubCategory());

                        for (RatedTransaction ratedTransaction : ratedTransactions) {
                            subCategoryInvoiceAgregateDto.getRatedTransactions().add(new RatedTransactionDto(ratedTransaction));
                        }
                    }

                    categoryInvoiceAgregateDto.getListSubCategoryInvoiceAgregateDto().add(subCategoryInvoiceAgregateDto);
                }
                // } else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                // SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();
                // subCategoryInvoiceAgregateDto.setType("T");
                // subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
                // subCategoryInvoiceAgregateDto.setAccountingCode(invoiceAgregate.getAccountingCode());
                // subCategoryInvoiceAgregateDto.setDescription(invoiceAgregate.getDescription());
                // subCategoryInvoiceAgregateDto.setQuantity(invoiceAgregate.getQuantity());
                // subCategoryInvoiceAgregateDto.setDiscount(invoiceAgregate.getDiscount());
                // subCategoryInvoiceAgregateDto.setAmountWithoutTax(invoiceAgregate.getAmountWithoutTax());
                // subCategoryInvoiceAgregateDto.setAmountTax(invoiceAgregate.getAmountTax());
                // subCategoryInvoiceAgregateDto.setAmountWithTax(invoiceAgregate.getAmountWithTax());
                // subCategoryInvoiceAgregateDto.setUserAccountCode(invoiceAgregate.getUserAccount() == null ? null : invoiceAgregate.getUserAccount().getCode());
                // CategoryInvoiceAgregateDto categoryInvoiceAgregateDto = null;
                // if (invoiceDto.getCategoryInvoiceAgregates().size() > 0) {
                // categoryInvoiceAgregateDto = invoiceDto.getCategoryInvoiceAgregates().get(0);
                // } else {
                // categoryInvoiceAgregateDto = new CategoryInvoiceAgregateDto();
                // invoiceDto.getCategoryInvoiceAgregates().add(categoryInvoiceAgregateDto);
                // }
                //
                // categoryInvoiceAgregateDto.getListSubCategoryInvoiceAgregateDto().add(subCategoryInvoiceAgregateDto);
            }
        }

        for (Invoice inv : invoice.getLinkedInvoices()) {
            invoiceDto.getListInvoiceIdToLink().add(inv.getId());
        }

        if (invoice.getRecordedInvoice() != null) {
            RecordedInvoiceDto recordedInvoiceDto = new RecordedInvoiceDto(invoice.getRecordedInvoice());
            invoiceDto.setRecordedInvoiceDto(recordedInvoiceDto);
        }

        this.setInvoicePdf(invoice, includePdf, invoiceDto);

        // #2236 if includeXml return xml file content
        if (includeXml) {
            this.setInvoiceXml(invoice, invoiceDto);
        }

        if (invoiceService.isInvoiceXmlExist(invoice)) {
            invoiceDto.setXmlFilename(invoice.getXmlFilename());
        }
        invoiceDto.setNetToPay(invoice.getNetToPay());
        invoiceDto.setReturnPdf(invoiceDto.getPdf() != null);

        return invoiceDto;
    }

    /**
     * Setting invoice pdf contents
     * 
     * @param invoice
     * @param includePdf
     * @param invoiceDto
     */
    private void setInvoicePdf(Invoice invoice, boolean includePdf, InvoiceDto invoiceDto) {
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
            invoiceDto.setPdfPresent(true);
            invoiceDto.setPdfFilename(invoice.getPdfFilename());
            if (includePdf) {
                try {
                    invoiceDto.setPdf(invoiceService.getInvoicePdf(invoice));
                } catch (BusinessException e) {
                    // Should not happen as file was found few lines above
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * #2236 setting invoice xml
     * 
     * @param invoice
     * @param invoiceDto
     * @throws BusinessException
     */
    private void setInvoiceXml(Invoice invoice, InvoiceDto invoiceDto) {
        try {
            if (!invoiceService.isInvoiceXmlExist(invoice)) {
                invoiceService.produceInvoiceXml(invoice);
            }
            String xml = invoiceService.getInvoiceXml(invoice);
            invoiceDto.setXml(xml != null ? Base64.encodeBase64String(xml.getBytes()) : null);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
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
                result.getInvoices().add(invoiceToDto(invoice, pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("transactions"),
                    pagingAndFiltering != null && pagingAndFiltering.hasFieldOption("pdf")));
            }
        }

        return result;
    }
}
