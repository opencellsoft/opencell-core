package org.meveo.api.invoice;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.Invoice4_2Dto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.MatchingAmountDto;
import org.meveo.api.dto.payment.MatchingAmountsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValueException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.util.MeveoParamBean;

@Deprecated
@Stateless
public class Invoice4_2Api extends BaseApi {

    @Inject
    RecordedInvoiceService recordedInvoiceService;

    @Inject
    ProviderService providerService;

    @Inject
    CustomerAccountService customerAccountService;

    @Inject
    BillingAccountService billingAccountService;

    @Inject
    BillingRunService billingRunService;

    @Inject
    InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    RatedTransactionService ratedTransactionService;

    @Inject
    OCCTemplateService oCCTemplateService;

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    InvoiceService invoiceService;

    @Inject
    TaxService taxService;

    @Inject
    XMLInvoiceCreator xmlInvoiceCreator;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    @MeveoParamBean
    private ParamBean paramBean;
    
    @Inject
    private AccountingCodeService accountingCodeService;

    public String create(Invoice4_2Dto invoiceDTO) throws MeveoApiException, BusinessException {

        if (invoiceDTO.getSubCategoryInvoiceAgregates().size() <= 0) {
            missingParameters.add("subCategoryInvoiceAgregates");
        }
        if (StringUtils.isBlank(invoiceDTO.getBillingAccountCode())) {
            missingParameters.add("billingAccountCode");
        }
        if (StringUtils.isBlank(invoiceDTO.getDueDate())) {
            missingParameters.add("dueDate");
        }
        if (StringUtils.isBlank(invoiceDTO.getAmountTax())) {
            missingParameters.add("amountTax");
        }
        if (StringUtils.isBlank(invoiceDTO.getAmountWithoutTax())) {
            missingParameters.add("amountWithoutTax");
        }
        if (StringUtils.isBlank(invoiceDTO.getAmountWithTax())) {
            missingParameters.add("amountWithTax");
        }
        if (StringUtils.isBlank(invoiceDTO.getInvoiceType())) {
            missingParameters.add("invoiceType");
        }

        handleMissingParameters();

        BillingAccount billingAccount = billingAccountService.findByCode(invoiceDTO.getBillingAccountCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, invoiceDTO.getBillingAccountCode());
        }
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceDTO.getInvoiceType());
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceDTO.getInvoiceType());
        }

        // FIXME : store that in SubCategoryInvoiceAgregateDto

        // FIXME : store that in SubCategoryInvoiceAgregateDto

        Invoice invoice = new Invoice();
        invoice.setBillingAccount(billingAccount);

        // no billing run here, use auditable.created as xml dir
        invoice.setInvoiceDate(new Date());
        invoice.setDueDate(invoiceDTO.getDueDate());

        PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            invoice.setPaymentMethodType(preferedPaymentMethod.getPaymentType());
        }
        invoice.setAmountTax(invoiceDTO.getAmountTax());
        invoice.setAmountWithoutTax(invoiceDTO.getAmountWithoutTax());
        invoice.setAmountWithTax(invoiceDTO.getAmountWithTax());
        invoice.setDiscount(invoiceDTO.getDiscount());
        invoice.setInvoiceType(invoiceType);

        if (invoice.getInvoiceType().equals(invoiceTypeService.getDefaultAdjustement())) {
            String invoiceNumber = invoiceDTO.getInvoiceNumber();
            if (invoiceNumber == null) {
                missingParameters.add("invoiceNumber");
                handleMissingParameters();
            }
            Invoice commercialInvoice = invoiceService.getInvoiceByNumber(invoiceNumber);
            if (commercialInvoice == null) {
                throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber);
            }
            invoice.setAdjustedInvoice(commercialInvoice);

        }
        invoiceService.assignInvoiceNumber(invoice);

        invoiceService.create(invoice);

        List<UserAccount> userAccounts = billingAccount.getUsersAccounts();

        for (SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDTO : invoiceDTO.getSubCategoryInvoiceAgregates()) {
            String invoiceSubCategoryCode = subCategoryInvoiceAgregateDTO.getInvoiceSubCategoryCode();
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(invoiceSubCategoryCode);
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, invoiceSubCategoryCode);
            }

            if (subCategoryInvoiceAgregateDTO.getRatedTransactions().size() <= 0) {
                missingParameters.add("ratedTransactions");
            }
            if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getItemNumber())) {
                missingParameters.add("itemNumber");
            }
            if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountTax())) {
                missingParameters.add("amountTax");
            }
            if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountWithoutTax())) {
                missingParameters.add("amountWithoutTax");
            }
            if (StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAmountWithTax())) {
                missingParameters.add("amountWithTax");
            }

            handleMissingParameters();

            SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
            String sciaDTOUserAccountCode = subCategoryInvoiceAgregateDTO.getUserAccountCode();
            UserAccount billingAccountUserAccount = null;
            if (sciaDTOUserAccountCode != null) {
                for (UserAccount ua : userAccounts) {
                    if (sciaDTOUserAccountCode.equals(ua.getCode())) {
                        billingAccountUserAccount = ua;
                        break;
                    }
                }
                if (billingAccountUserAccount == null) {
                    throw new BusinessException("Incorrect userAccountCode in subCategoryInvoiceAgregateDTO " + subCategoryInvoiceAgregateDTO.getDescription());
                }
            } else {
                throw new BusinessException("Missing userAccountCode in subCategoryInvoiceAgregateDTO " + subCategoryInvoiceAgregateDTO.getDescription());
            }

            for (String taxCode : subCategoryInvoiceAgregateDTO.getTaxesCodes()) {

                Tax tax = taxService.findByCode(taxCode);
                if (tax == null) {
                    throw new EntityDoesNotExistsException(Tax.class, taxCode);
                }

                TaxInvoiceAgregate taxInvoiceAgregate = new TaxInvoiceAgregate();
                taxInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax());
                taxInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax().multiply(tax.getPercent()).divide(new BigDecimal("100")));

                taxInvoiceAgregate.setTaxPercent(tax.getPercent());
                taxInvoiceAgregate.setBillingAccount(billingAccount);
                taxInvoiceAgregate.setInvoice(invoice);
                taxInvoiceAgregate.setUserAccount(billingAccountUserAccount);
                taxInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregateDTO.getItemNumber());
                taxInvoiceAgregate.setTax(tax);
                invoiceAgregateService.create(taxInvoiceAgregate);
                subCategoryInvoiceAgregate.addSubCategoryTax(tax);
            }

            subCategoryInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax());
            subCategoryInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregateDTO.getAmountWithTax());
            subCategoryInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregateDTO.getAmountTax());
            
            if (!StringUtils.isBlank(subCategoryInvoiceAgregateDTO.getAccountingCode())) {
                AccountingCode accountingCode = accountingCodeService.findByCode(subCategoryInvoiceAgregateDTO.getAccountingCode());
                if (accountingCode == null) {
                    throw new EntityDoesNotExistsException(AccountingCode.class, subCategoryInvoiceAgregateDTO.getAccountingCode());
                }
                subCategoryInvoiceAgregate.setAccountingCode(accountingCode);
            }
            
            subCategoryInvoiceAgregate.setBillingAccount(billingAccount);
            subCategoryInvoiceAgregate.setUserAccount(billingAccountUserAccount);
            subCategoryInvoiceAgregate.setInvoice(invoice);
            subCategoryInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregateDTO.getItemNumber());
            subCategoryInvoiceAgregate.setInvoiceSubCategory(invoiceSubCategory);
            subCategoryInvoiceAgregate.setWallet(billingAccountUserAccount.getWallet());

            CategoryInvoiceAgregate categoryInvoiceAgregate = new CategoryInvoiceAgregate();
            categoryInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregateDTO.getAmountWithTax());
            categoryInvoiceAgregate.setAmountWithoutTax(subCategoryInvoiceAgregateDTO.getAmountWithoutTax());
            categoryInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregateDTO.getAmountTax());
            categoryInvoiceAgregate.setBillingAccount(billingAccount);
            categoryInvoiceAgregate.setInvoice(invoice);
            categoryInvoiceAgregate.setItemNumber(subCategoryInvoiceAgregateDTO.getItemNumber());
            categoryInvoiceAgregate.setUserAccount(billingAccountUserAccount);
            categoryInvoiceAgregate.setInvoiceCategory(invoiceSubCategory.getInvoiceCategory());
            invoiceAgregateService.create(categoryInvoiceAgregate);

            subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(categoryInvoiceAgregate);
            invoiceAgregateService.create(subCategoryInvoiceAgregate);

            for (RatedTransactionDto ratedTransaction : subCategoryInvoiceAgregateDTO.getRatedTransactions()) {
                RatedTransaction meveoRatedTransaction = new RatedTransaction(null, ratedTransaction.getUsageDate(), ratedTransaction.getUnitAmountWithoutTax(),
                    ratedTransaction.getUnitAmountWithTax(), ratedTransaction.getUnitAmountTax(), ratedTransaction.getQuantity(), ratedTransaction.getAmountWithoutTax(),
                    ratedTransaction.getAmountWithTax(), ratedTransaction.getAmountTax(), RatedTransactionStatusEnum.BILLED, null, billingAccount, invoiceSubCategory, null, null,
                    null, null, null, ratedTransaction.getUnityDescription(), null, null, null, null, ratedTransaction.getCode(), ratedTransaction.getDescription());

                meveoRatedTransaction.setInvoice(invoice);
                meveoRatedTransaction.setWallet(billingAccountUserAccount.getWallet());
                ratedTransactionService.create(meveoRatedTransaction);

            }

        }

        invoiceService.update(invoice);

        return invoice.getInvoiceNumber();
    }

    public List<Invoice4_2Dto> list(String customerAccountCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        List<Invoice4_2Dto> customerInvoiceDtos = new ArrayList<Invoice4_2Dto>();

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        for (BillingAccount billingAccount : customerAccount.getBillingAccounts()) {
            List<Invoice> invoiceList = billingAccount.getInvoices();

            for (Invoice invoice : invoiceList) {
                Invoice4_2Dto customerInvoiceDto = new Invoice4_2Dto();
                customerInvoiceDto.setBillingAccountCode(billingAccount.getCode());
                customerInvoiceDto.setInvoiceDate(invoice.getInvoiceDate());
                customerInvoiceDto.setDueDate(invoice.getDueDate());

                customerInvoiceDto.setAmountWithoutTax(invoice.getAmountWithoutTax());
                customerInvoiceDto.setAmountTax(invoice.getAmountTax());
                customerInvoiceDto.setAmountWithTax(invoice.getAmountWithTax());
                customerInvoiceDto.setInvoiceNumber(invoice.getInvoiceNumber());
                customerInvoiceDto.setPaymentMethod(invoice.getPaymentMethodType());
                customerInvoiceDto.setInvoiceType(invoice.getInvoiceType().getCode());

                if (invoiceService.isInvoicePdfExist(invoice)) {
                    customerInvoiceDto.setPdfPresent(true);
                    customerInvoiceDto.setPdf(invoiceService.getInvoicePdf(invoice));
                }

                SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = null;

                for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {

                    subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();

                    if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                        subCategoryInvoiceAgregateDto.setType("R");
                    } else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                        subCategoryInvoiceAgregateDto.setType("F");
                    } else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                        subCategoryInvoiceAgregateDto.setType("T");
                    }

                    subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
                    
                    if (!StringUtils.isBlank(subCategoryInvoiceAgregateDto.getAccountingCode())) {
                        AccountingCode accountingCode = accountingCodeService.findByCode(subCategoryInvoiceAgregateDto.getAccountingCode());
                        if (accountingCode == null) {
                            throw new EntityDoesNotExistsException(AccountingCode.class, subCategoryInvoiceAgregateDto.getAccountingCode());
                        }
                        invoiceAgregate.setAccountingCode(accountingCode);
                    }
                    
                    subCategoryInvoiceAgregateDto.setDescription(invoiceAgregate.getDescription());
                    subCategoryInvoiceAgregateDto.setQuantity(invoiceAgregate.getQuantity());
                    subCategoryInvoiceAgregateDto.setDiscount(invoiceAgregate.getDiscount());
                    subCategoryInvoiceAgregateDto.setAmountWithoutTax(invoiceAgregate.getAmountWithoutTax());
                    subCategoryInvoiceAgregateDto.setAmountTax(invoiceAgregate.getAmountTax());
                    subCategoryInvoiceAgregateDto.setAmountWithTax(invoiceAgregate.getAmountWithTax());
                    customerInvoiceDto.getSubCategoryInvoiceAgregates().add(subCategoryInvoiceAgregateDto);
                }

                // customerInvoiceDtos.add(customerInvoiceDto);
                customerInvoiceDtos.add(invoice4_2ToDto(invoice, billingAccount.getCode()));
            }
        }

        return customerInvoiceDtos;
    }

    public BillingRun launchExceptionalInvoicing(GenerateInvoiceRequestDto generateInvoiceRequestDto, List<Long> BAids)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException, BusinessApiException, Exception {
        return billingRunService.launchExceptionalInvoicing(BAids, generateInvoiceRequestDto.getInvoicingDate(), generateInvoiceRequestDto.getLastTransactionDate(),
            BillingProcessTypesEnum.AUTOMATIC);
    }

    private void updateBAtotalAmount(Long billingAccountId, BillingRun billingRun) throws BusinessException {
        billingAccountService.updateBillingAccountTotalAmounts(billingAccountId, billingRun);
        log.debug("updateBillingAccountTotalAmounts ok");
    }

    public void createRatedTransaction(Long billingAccountId, Date invoicingDate) throws Exception {
        ratedTransactionService.createRatedTransaction(billingAccountId, invoicingDate);
    }

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

    public void validateBR(BillingRun billingRun) throws BusinessException {
        billingRunService.forceValidate(billingRun.getId());
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GenerateInvoiceResultDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto)
            throws MissingParameterException, EntityDoesNotExistsException, BusinessException, BusinessApiException, Exception {

        if (generateInvoiceRequestDto == null) {
            missingParameters.add("generateInvoiceRequest");
            handleMissingParameters();
        }
        if (StringUtils.isBlank(generateInvoiceRequestDto.getBillingAccountCode())) {
            missingParameters.add("billingAccountCode");
        }

        if (generateInvoiceRequestDto.getInvoicingDate() == null) {
            missingParameters.add("invoicingDate");
        }
        if (generateInvoiceRequestDto.getLastTransactionDate() == null) {
            missingParameters.add("lastTransactionDate");
        }

        handleMissingParameters();

        BillingAccount billingAccount = billingAccountService.findByCode(generateInvoiceRequestDto.getBillingAccountCode(), Arrays.asList("billingRun"));
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, generateInvoiceRequestDto.getBillingAccountCode());
        }

        if (billingAccount.getBillingRun() != null && (billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.NEW)
                || billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.PREVALIDATED)
                || billingAccount.getBillingRun().getStatus().equals(BillingRunStatusEnum.POSTVALIDATED))) {

            throw new BusinessApiException("The billingAccount is already in an billing run with status " + billingAccount.getBillingRun().getStatus());
        }

        List<Long> baIds = new ArrayList<Long>();
        baIds.add(billingAccount.getId());

        createRatedTransaction(billingAccount.getId(), generateInvoiceRequestDto.getInvoicingDate());
        log.info("createRatedTransaction ok");

        BillingRun billingRun = launchExceptionalInvoicing(generateInvoiceRequestDto, baIds);
        Long billingRunId = billingRun.getId();
        log.info("launchExceptionalInvoicing ok , billingRun.id:" + billingRunId);

        updateBAtotalAmount(billingAccount.getId(), billingRun);
        log.info("updateBillingAccountTotalAmounts ok");

        billingRun = updateBR(billingRun, BillingRunStatusEnum.PREVALIDATED, 1, 1);
        log.info("update billingRun ON_GOING");

        billingRunService.createAgregatesAndInvoice(billingRun, 1, 0, null, null);
        log.info("createAgregatesAndInvoice ok");

        billingRun = updateBR(billingRun, BillingRunStatusEnum.POSTINVOICED, null, null);
        log.info("update billingRun POSTINVOICED");

        validateBR(billingRun);
        log.info("billingRunService.validate ok");

        List<Invoice> invoices = invoiceService.getInvoices(billingRun);
        log.info((invoices == null) ? "getInvoice is null" : "size=" + invoices.size());
        if (invoices == null || invoices.isEmpty()) {
            throw new BusinessApiException("Can't find invoice");
        }

        GenerateInvoiceResultDto generateInvoiceResultDto = new GenerateInvoiceResultDto();
        generateInvoiceResultDto.setInvoiceId(invoices.get(0).getId());
        generateInvoiceResultDto.setInvoiceNumber(invoices.get(0).getInvoiceNumber());
        return generateInvoiceResultDto;
    }

    public String getXMLInvoice(String invoiceNumber, String invoiceTypeCode)
            throws FileNotFoundException, MissingParameterException, EntityDoesNotExistsException, BusinessException, InvalidEnumValueException {
        log.debug("getXMLInvoice  invoiceNumber:{}", invoiceNumber);
        if (StringUtils.isBlank(invoiceNumber)) {
            missingParameters.add("invoiceNumber");
        }
        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("invoiceTypeCode");
        }
        handleMissingParameters();

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
        }

        Invoice invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber);
        }

        String xmlContent = invoiceService.getInvoiceXml(invoice);
        log.debug("getXMLInvoice  invoiceNumber:{} done.", invoiceNumber);
        return xmlContent;
    }

    public byte[] getPdfInvoince(String invoiceNumber, String invoiceTypeCode) throws MissingParameterException, EntityDoesNotExistsException, Exception {
        log.debug("getPdfInvoince  invoiceNumber:{}", invoiceNumber);
        if (StringUtils.isBlank(invoiceNumber)) {
            missingParameters.add("invoiceNumber");
        }
        if (StringUtils.isBlank(invoiceTypeCode)) {
            missingParameters.add("invoiceTypeCode");
        }
        handleMissingParameters();

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException(InvoiceType.class, invoiceTypeCode);
        }

        Invoice invoice = invoiceService.findByInvoiceNumberAndType(invoiceNumber, invoiceType);
        if (invoice == null) {
            throw new EntityDoesNotExistsException(Invoice.class, invoiceNumber);
        }

        log.debug("getXMLInvoice invoiceNumber:{} done.", invoiceNumber);
        return invoiceService.getInvoicePdf(invoice);
    }

    private Invoice4_2Dto invoice4_2ToDto(Invoice invoice, String billingAccountCode) throws BusinessException {
        Invoice4_2Dto invoiceDto = new Invoice4_2Dto();

        invoiceDto.setBillingAccountCode(billingAccountCode);
        invoiceDto.setInvoiceDate(invoice.getInvoiceDate());
        invoiceDto.setDueDate(invoice.getDueDate());

        invoiceDto.setAmountWithoutTax(invoice.getAmountWithoutTax());
        invoiceDto.setAmountTax(invoice.getAmountTax());
        invoiceDto.setAmountWithTax(invoice.getAmountWithTax());
        invoiceDto.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDto.setPaymentMethod(invoice.getPaymentMethodType());
        invoiceDto.setInvoiceType(invoice.getInvoiceType().getCode());

        if (invoiceService.isInvoicePdfExist(invoice)) {
            invoiceDto.setPdfPresent(true);
            invoiceDto.setPdf(invoiceService.getInvoicePdf(invoice));
        }

        SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = null;

        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {

            subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();

            if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                subCategoryInvoiceAgregateDto.setType("R");
            } else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                subCategoryInvoiceAgregateDto.setType("F");
            } else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                subCategoryInvoiceAgregateDto.setType("T");
            }
            
            if (invoiceAgregate.getAccountingCode() != null) {
                subCategoryInvoiceAgregateDto.setAccountingCode(invoiceAgregate.getAccountingCode().getCode());
            }
            
            subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
            subCategoryInvoiceAgregateDto.setDescription(invoiceAgregate.getDescription());
            subCategoryInvoiceAgregateDto.setQuantity(invoiceAgregate.getQuantity());
            subCategoryInvoiceAgregateDto.setDiscount(invoiceAgregate.getDiscount());
            subCategoryInvoiceAgregateDto.setAmountWithoutTax(invoiceAgregate.getAmountWithoutTax());
            subCategoryInvoiceAgregateDto.setAmountTax(invoiceAgregate.getAmountTax());
            subCategoryInvoiceAgregateDto.setAmountWithTax(invoiceAgregate.getAmountWithTax());
            invoiceDto.getSubCategoryInvoiceAgregates().add(subCategoryInvoiceAgregateDto);
        }

        CustomerAccount ca = invoice.getBillingAccount().getCustomerAccount();
        AccountOperationDto accountOperationDto = null;
        for (AccountOperation accountOp : ca.getAccountOperations()) {
            accountOperationDto = new AccountOperationDto();
            accountOperationDto.setId(accountOp.getId());
            accountOperationDto.setDueDate(accountOp.getDueDate());
            accountOperationDto.setType(accountOp.getType());
            accountOperationDto.setTransactionDate(accountOp.getTransactionDate());
            accountOperationDto.setTransactionCategory(accountOp.getTransactionCategory());
            accountOperationDto.setReference(accountOp.getReference());
            accountOperationDto.setAccountCode(accountOp.getAccountCode());
            accountOperationDto.setAccountCodeClientSide(accountOp.getAccountCodeClientSide());
            accountOperationDto.setAmount(accountOp.getAmount());
            accountOperationDto.setMatchingAmount(accountOp.getMatchingAmount());
            accountOperationDto.setUnMatchingAmount(accountOp.getUnMatchingAmount());
            accountOperationDto.setMatchingStatus(accountOp.getMatchingStatus());
            accountOperationDto.setOccCode(accountOp.getOccCode());
            accountOperationDto.setOccDescription(accountOp.getOccDescription());

            List<MatchingAmount> matchingAmounts = accountOp.getMatchingAmounts();
            if (matchingAmounts != null && !matchingAmounts.isEmpty()) {
                MatchingAmountDto matchingAmountDto = null;
                MatchingAmountsDto matchingAmountsDto = new MatchingAmountsDto();
                matchingAmountsDto.setMatchingAmount(new ArrayList<>());
                if (matchingAmounts != null && matchingAmounts.size() > 0) {
                    for (MatchingAmount matchingAmount : matchingAmounts) {
                        matchingAmountDto = new MatchingAmountDto();
                        matchingAmountDto.setMatchingCode(matchingAmount.getMatchingCode().getCode());
                        matchingAmountDto.setMatchingAmount(matchingAmount.getMatchingAmount());
                        matchingAmountsDto.getMatchingAmount().add(matchingAmountDto);
                    }
                    accountOperationDto.setMatchingAmounts(matchingAmountsDto);
                }
            }

            invoiceDto.getAccountOperations().add(accountOperationDto);
        }

        return invoiceDto;
    }

}