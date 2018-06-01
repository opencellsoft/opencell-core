package org.meveo.api.invoice;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.meveo.model.billing.BillingAccount;
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
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;
import org.primefaces.model.SortOrder;

/**
 * CRUD API for managing {@link Invoice}.
 * 
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@Stateless
public class InvoiceApi extends BaseApi {

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private BillingAccountService billingAccountService;

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
        Auditable auditable = new Auditable(currentUser);
        Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

        BillingAccount billingAccount = billingAccountService.findByCode(invoiceDTO.getBillingAccountCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, invoiceDTO.getBillingAccountCode());
        }
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceDTO.getInvoiceType());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceDTO.getInvoiceType());
        }

        BigDecimal invoiceAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal invoiceAmountTax = BigDecimal.ZERO;
        BigDecimal invoiceAmountWithTax = BigDecimal.ZERO;
        Invoice invoice = new Invoice();
        invoice.setBillingAccount(billingAccount);

        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setDueDate(invoiceDTO.getDueDate());

        PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            invoice.setPaymentMethodType(preferedPaymentMethod.getPaymentType());
        }

        invoice.setInvoiceType(invoiceType);
        invoiceService.create(invoice);
        if (invoiceDTO.getListInvoiceIdToLink() != null) {
            for (Long invoiceId : invoiceDTO.getListInvoiceIdToLink()) {
                Invoice invoiceTmp = invoiceService.findById(invoiceId);
                if (invoiceTmp == null) {
                    throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
                }
                if (!invoiceType.getAppliesTo().contains(invoiceTmp.getInvoiceType())) {
                    throw new BusinessApiException("InvoiceId " + invoiceId + " cant be linked");
                }
                invoice.getLinkedInvoices().add(invoiceTmp);
                invoiceTmp.getLinkedInvoices().add(invoice);
                invoiceService.update(invoiceTmp);
            }
        }
        invoice = invoiceService.update(invoice);
        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
        if (userAccounts == null || userAccounts.isEmpty()) {
            throw new BusinessApiException("BillingAccount " + invoiceDTO.getBillingAccountCode() + " has no userAccount");
        }
        // TODO : userAccount on dto ?
        UserAccount userAccount = userAccounts.get(0);
        for (CategoryInvoiceAgregateDto catInvAgrDto : invoiceDTO.getCategoryInvoiceAgregates()) {
            BigDecimal catAmountWithoutTax = BigDecimal.ZERO;
            BigDecimal catAmountTax = BigDecimal.ZERO;
            BigDecimal catAmountWithTax = BigDecimal.ZERO;
            CategoryInvoiceAgregate invoiceAgregateCat = new CategoryInvoiceAgregate();
            invoiceAgregateCat.setAuditable(auditable);
            invoiceAgregateCat.setInvoice(invoice);
            invoiceAgregateCat.setBillingRun(null);
            invoiceAgregateCat.setDescription(catInvAgrDto.getDescription());
            invoiceAgregateCat.setItemNumber(catInvAgrDto.getListSubCategoryInvoiceAgregateDto().size());
            invoiceAgregateCat.setUserAccount(userAccount);
            invoiceAgregateCat.setBillingAccount(billingAccount);
            invoiceAgregateCat.setInvoiceCategory(invoiceCategoryService.findByCode(catInvAgrDto.getCategoryInvoiceCode()));
            invoiceAgregateCat.setUserAccount(userAccount);
            invoiceAgregateService.create(invoiceAgregateCat);

            for (SubCategoryInvoiceAgregateDto subCatInvAgrDTO : catInvAgrDto.getListSubCategoryInvoiceAgregateDto()) {
                BigDecimal subCatAmountWithoutTax = BigDecimal.ZERO;
                BigDecimal subCatAmountTax = BigDecimal.ZERO;
                BigDecimal subCatAmountWithTax = BigDecimal.ZERO;
                Tax currentTax = null;
                List<Tax> taxes = new ArrayList<Tax>();
                InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(subCatInvAgrDTO.getInvoiceSubCategoryCode());
                for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
                    if ((invoicesubcatCountry.getSellingCountry() == null
                            || (billingAccount.getCustomerAccount().getCustomer().getSeller().getTradingCountry() != null && invoicesubcatCountry.getSellingCountry()
                                .getCountryCode().equalsIgnoreCase(billingAccount.getCustomerAccount().getCustomer().getSeller().getTradingCountry().getCountryCode())))
                            && (invoicesubcatCountry.getTradingCountry() == null
                                    || invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode()))
                            && invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, invoice)) {
                        if (!taxes.contains(invoicesubcatCountry.getTax())) {
                            if (StringUtils.isBlank(invoicesubcatCountry.getTaxCodeEL())) {
                                taxes.add(invoicesubcatCountry.getTax());
                            } else {
                                taxes.add(invoiceSubCategoryService.evaluateTaxCodeEL(invoicesubcatCountry.getTaxCodeEL(), userAccount, billingAccount, invoice));
                            }
                        }
                        if (currentTax == null) {                           
                            if (StringUtils.isBlank(invoicesubcatCountry.getTaxCodeEL())) {
                                currentTax = invoicesubcatCountry.getTax();
                            } else {
                                currentTax = invoiceSubCategoryService.evaluateTaxCodeEL(invoicesubcatCountry.getTaxCodeEL(), userAccount, billingAccount, invoice);
                            }
                        }
                    }
                }
                if (currentTax == null) {
                    throw new BusinessApiException("Cant find tax for InvoiceSubCategory:" + subCatInvAgrDTO.getInvoiceSubCategoryCode());
                }
                
                boolean isDetailledInvoiceMode = InvoiceModeEnum.DETAILLED.name().equals(invoiceDTO.getInvoiceMode().name());
                
                for (RatedTransactionDto ratedTransactionDto : subCatInvAgrDTO.getRatedTransactions()) {

                    BigDecimal amountWithoutTax = ratedTransactionDto.getUnitAmountWithoutTax().multiply(ratedTransactionDto.getQuantity());
                    BigDecimal amountWithTax = getAmountWithTax(currentTax, amountWithoutTax);
                    BigDecimal amountTax = getAmountTax(amountWithTax, amountWithoutTax);

                    RatedTransaction meveoRatedTransaction = new RatedTransaction(null, ratedTransactionDto.getUsageDate(), ratedTransactionDto.getUnitAmountWithoutTax(),
                        ratedTransactionDto.getUnitAmountWithTax(), ratedTransactionDto.getUnitAmountTax(), ratedTransactionDto.getQuantity(), amountWithoutTax, amountWithTax,
                        amountTax, RatedTransactionStatusEnum.BILLED, userAccount.getWallet(), billingAccount, invoiceSubCategory, null, null, null, null, null,
                        ratedTransactionDto.getUnityDescription(), null, null, null, null, ratedTransactionDto.getCode(), ratedTransactionDto.getDescription(),
                        ratedTransactionDto.getStartDate(), ratedTransactionDto.getEndDate());

                    meveoRatedTransaction.setInvoice(invoice);
                    meveoRatedTransaction.setWallet(userAccount.getWallet());
                    // #3355 : setting params 1,2,3
                    if (isDetailledInvoiceMode) {
                        meveoRatedTransaction.setParameter1(ratedTransactionDto.getParameter1());
                        meveoRatedTransaction.setParameter2(ratedTransactionDto.getParameter2());
                        meveoRatedTransaction.setParameter3(ratedTransactionDto.getParameter3());
                    }

                    ratedTransactionService.create(meveoRatedTransaction);

                    subCatAmountWithoutTax = subCatAmountWithoutTax.add(amountWithoutTax);
                    subCatAmountTax = subCatAmountTax.add(amountTax);
                    subCatAmountWithTax = subCatAmountWithTax.add(amountWithTax);
                }
                if (invoiceDTO.getInvoiceType().equals(invoiceTypeService.getCommercialCode())) {
                    List<RatedTransaction> openedRT = ratedTransactionService.openRTbySubCat(userAccount.getWallet(), invoiceSubCategory);
                    for (RatedTransaction ratedTransaction : openedRT) {
                        subCatAmountWithoutTax = subCatAmountWithoutTax.add(ratedTransaction.getAmountWithoutTax());
                        subCatAmountTax = subCatAmountTax.add(ratedTransaction.getAmountTax());
                        subCatAmountWithTax = subCatAmountWithTax.add(ratedTransaction.getAmountWithTax());
                        ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
                        ratedTransaction.setInvoice(invoice);
                        ratedTransactionService.update(ratedTransaction);
                    }
                }

                SubCategoryInvoiceAgregate invoiceAgregateSubcat = new SubCategoryInvoiceAgregate();
                invoiceAgregateSubcat.setCategoryInvoiceAgregate(invoiceAgregateCat);
                invoiceAgregateSubcat.setInvoiceSubCategory(invoiceSubCategory);
                invoiceAgregateSubcat.setInvoice(invoice);
                invoiceAgregateSubcat.setDescription(subCatInvAgrDTO.getDescription());
                invoiceAgregateSubcat.setBillingRun(null);
                invoiceAgregateSubcat.setWallet(userAccount.getWallet());
                invoiceAgregateSubcat.setUserAccount(userAccount);
                invoiceAgregateSubcat.setAccountingCode(invoiceSubCategory.getAccountingCode());
                invoiceAgregateSubcat.setAuditable(auditable);
                invoiceAgregateSubcat.setQuantity(BigDecimal.ONE);
                invoiceAgregateSubcat.setTaxPercent(currentTax.getPercent());
                invoiceAgregateSubcat.setSubCategoryTaxes(new HashSet<Tax>(Arrays.asList(currentTax)));
                
                if (isDetailledInvoiceMode) {
                    invoiceAgregateSubcat.setItemNumber(subCatInvAgrDTO.getRatedTransactions().size());
                    invoiceAgregateSubcat.setAmountWithoutTax(subCatAmountWithoutTax);
                    invoiceAgregateSubcat.setAmountTax(subCatAmountTax);
                    invoiceAgregateSubcat.setAmountWithTax(subCatAmountWithTax);
                } else {
                    // we add subCatAmountWithoutTax, in the case if there any opened RT to includ
                    invoiceAgregateSubcat.setAmountWithoutTax(subCatAmountWithoutTax.add(subCatInvAgrDTO.getAmountWithoutTax()));
                    invoiceAgregateSubcat.setAmountWithTax(subCatAmountWithTax.add(getAmountWithTax(currentTax, subCatInvAgrDTO.getAmountWithoutTax())));
                    invoiceAgregateSubcat.setAmountTax(getAmountTax(invoiceAgregateSubcat.getAmountWithTax(), invoiceAgregateSubcat.getAmountWithoutTax()));
                }

                invoiceAgregateService.create(invoiceAgregateSubcat);
                for (Tax tax : taxes) {
                    TaxInvoiceAgregate invoiceAgregateTax = null;
                    Long taxId = tax.getId();

                    if (taxInvoiceAgregateMap.containsKey(taxId)) {
                        invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
                    } else {
                        invoiceAgregateTax = new TaxInvoiceAgregate();
                        invoiceAgregateTax.setInvoice(invoice);
                        invoiceAgregateTax.setBillingRun(null);
                        invoiceAgregateTax.setTax(tax);
                        invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
                        invoiceAgregateTax.setTaxPercent(tax.getPercent());
                        invoiceAgregateTax.setUserAccount(userAccount);
                        invoiceAgregateTax.setAmountWithoutTax(BigDecimal.ZERO);
                        invoiceAgregateTax.setAmountWithTax(BigDecimal.ZERO);
                        invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
                        invoiceAgregateTax.setBillingAccount(billingAccount);
                        invoiceAgregateTax.setUserAccount(userAccount);
                        invoiceAgregateTax.setAuditable(auditable);
                    }
                    invoiceAgregateTax.setAmountWithoutTax(invoiceAgregateTax.getAmountWithoutTax().add(invoiceAgregateSubcat.getAmountWithoutTax()));
                    invoiceAgregateTax.setAmountTax(invoiceAgregateTax.getAmountTax().add(invoiceAgregateSubcat.getAmountTax()));
                    invoiceAgregateTax.setAmountWithTax(invoiceAgregateTax.getAmountWithTax().add(invoiceAgregateSubcat.getAmountWithTax()));

                    taxInvoiceAgregateMap.put(taxId, invoiceAgregateTax);
                }
                catAmountWithoutTax = catAmountWithoutTax.add(invoiceAgregateSubcat.getAmountWithoutTax());
                catAmountTax = catAmountTax.add(invoiceAgregateSubcat.getAmountTax());
                catAmountWithTax = catAmountWithTax.add(invoiceAgregateSubcat.getAmountWithTax());
            }

            invoiceAgregateCat.setAmountWithoutTax(catAmountWithoutTax);
            invoiceAgregateCat.setAmountTax(catAmountTax);
            invoiceAgregateCat.setAmountWithTax(catAmountWithTax);

            invoiceAmountWithoutTax = invoiceAmountWithoutTax.add(invoiceAgregateCat.getAmountWithoutTax());
            invoiceAmountTax = invoiceAmountTax.add(invoiceAgregateCat.getAmountTax());
            invoiceAmountWithTax = invoiceAmountWithTax.add(invoiceAgregateCat.getAmountWithTax());
        }

        for (Entry<Long, TaxInvoiceAgregate> entry : taxInvoiceAgregateMap.entrySet()) {
            invoiceAgregateService.create(entry.getValue());
        }

        invoice.setAmountWithoutTax(invoiceAmountWithoutTax);
        invoice.setAmountTax(invoiceAmountTax);
        invoice.setAmountWithTax(invoiceAmountWithTax);

        BigDecimal netToPay = invoice.getAmountWithTax();
        if (!appProvider.isEntreprise() && invoiceDTO.isIncludeBalance()) {
            BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, invoice.getBillingAccount().getCustomerAccount().getCode(), invoice.getDueDate());

            if (balance == null) {
                throw new BusinessException("account balance calculation failed");
            }
            netToPay = invoice.getAmountWithTax().add(balance);
        }
        invoice.setNetToPay(netToPay);

        try {
            populateCustomFields(invoiceDTO.getCustomFields(), invoice, true, true);

        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        invoice = invoiceService.update(invoice);

        CreateInvoiceResponseDto response = new CreateInvoiceResponseDto();
        response.setInvoiceId(invoice.getId());
        response.setAmountWithoutTax(invoice.getAmountWithoutTax());
        response.setAmountTax(invoice.getAmountTax());
        response.setAmountWithTax(invoice.getAmountWithTax());
        response.setDueDate(invoice.getDueDate());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setNetToPay(invoice.getNetToPay());

        // pdf and xml are added to response if requested
        if (invoiceDTO.isAutoValidation()) {
            response.setInvoiceNumber(validateInvoice(invoice.getId()));
            if (invoiceDTO.isReturnXml() || invoiceDTO.isReturnPdf()) {
                invoiceService.produceInvoiceXml(invoice);
                String invoiceXml = invoiceService.getInvoiceXml(invoice);
                response.setXmlInvoice(invoiceXml);
                response.setXmlFilename(invoice.getXmlFilename());
            }
            if (invoiceDTO.isReturnPdf()) {
                invoice = invoiceService.produceInvoicePdf(invoice);
                byte[] invoicePdf = invoiceService.getInvoicePdf(invoice);
                response.setPdfInvoice(invoicePdf);
                response.setPdfFilename(invoice.getPdfFilename());
            }
        }
        return response;
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
    public GenerateInvoiceResultDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto)
            throws BusinessException, MeveoApiException, FileNotFoundException, InvoiceExistException, ImportInvoiceException {
        return generateInvoice(generateInvoiceRequestDto, false);
    }

    public GenerateInvoiceResultDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto, boolean isDraft)
            throws BusinessException, MeveoApiException, FileNotFoundException, InvoiceExistException, ImportInvoiceException {

        if (generateInvoiceRequestDto == null) {
            missingParameters.add("generateInvoiceRequest");
            handleMissingParameters();
            return null;
        }

        String billingAccountCode = generateInvoiceRequestDto.getBillingAccountCode();
        if (StringUtils.isBlank(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
        }

        if (generateInvoiceRequestDto.getInvoicingDate() == null) {
            missingParameters.add("invoicingDate");
        }
        if (generateInvoiceRequestDto.getLastTransactionDate() == null && StringUtils.isBlank(generateInvoiceRequestDto.getFilter())
                && StringUtils.isBlank(generateInvoiceRequestDto.getOrderNumber())) {
            missingParameters.add("lastTransactionDate or filter or orderNumber");
        }


        handleMissingParameters();

        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode, Arrays.asList("billingRun"));
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
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

        Invoice invoice = invoiceService.generateInvoice(billingAccount, generateInvoiceRequestDto.getInvoicingDate(), generateInvoiceRequestDto.getFirstTransactionDate(),
            generateInvoiceRequestDto.getLastTransactionDate(), ratedTransactionFilter, generateInvoiceRequestDto.getOrderNumber(), isDraft, produceXml, producePdf, generateAO);

        GenerateInvoiceResultDto generateInvoiceResultDto = createGenerateInvoiceResultDto(invoice, produceXml, producePdf);
        if (isDraft) {
            invoiceService.cancelInvoice(invoice);
        }

        return generateInvoiceResultDto;
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
            throws MissingParameterException, EntityDoesNotExistsException, MeveoApiException, BusinessException {
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
            throws MissingParameterException, EntityDoesNotExistsException, MeveoApiException, BusinessException {
        Invoice invoice = find(id, invoiceNumber, invoiceTypeCode);
        if (invoice == null) {
            if (id != null)
                throw new EntityDoesNotExistsException(Invoice.class, id);
            else
                throw new EntityDoesNotExistsException(Invoice.class, "invoiceNumber", invoiceNumber, "invoiceType", invoiceTypeCode);
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
        BigDecimal ttc = amountWithoutTax.add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100), rounding, RoundingMode.HALF_UP));
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

        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
            if (invoiceAgregate instanceof SubCategoryInvoiceAgregate || invoiceAgregate instanceof TaxInvoiceAgregate) {
                continue;

            } else if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                CategoryInvoiceAgregateDto categoryInvoiceAgregateDto = new CategoryInvoiceAgregateDto();
                categoryInvoiceAgregateDto.setCategoryInvoiceCode(((CategoryInvoiceAgregate) invoiceAgregate).getInvoiceCategory().getCode());

                SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();
                subCategoryInvoiceAgregateDto.setType("R");
                subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
                if(invoiceAgregate.getAccountingCode() != null) {
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
                    if(invoiceAgregate.getAccountingCode() != null) {
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

        return invoiceDto;
    }

    /**
     * Setting invoice pdf contents
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
