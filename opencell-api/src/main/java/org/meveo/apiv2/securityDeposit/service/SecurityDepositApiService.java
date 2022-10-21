package org.meveo.apiv2.securityDeposit.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.ImmutableBasicInvoice;
import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.billing.ImmutableInvoiceLinesInput;
import org.meveo.apiv2.billing.service.InvoiceApiService;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;
import org.meveo.service.securityDeposit.impl.SecurityDepositTemplateService;

public class SecurityDepositApiService implements ApiService<SecurityDeposit> {

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private SecurityDepositService securityDepositService;

    @Inject
    private SecurityDepositTemplateService securityDepositTemplateService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private ProviderService providerService;

	@Inject
    private BillingAccountService billingAccountService;
        
    @Inject
    private InvoiceLineService invoiceLineService;

    @Inject
    private InvoiceApiService invoiceApiService;

    @Inject
    private ServiceSingleton serviceSingleton;
    
    @Override
    public List<SecurityDeposit> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<SecurityDeposit> findById(Long id) {
        return Optional.ofNullable(securityDepositService.findById(id));
    }

    @Override
    public SecurityDeposit create(SecurityDeposit baseEntity) {
        return null;
    }

    @Override
    public Optional<SecurityDeposit> update(Long id, SecurityDeposit baseEntity) {
        return empty();
    }

    @Override
    public Optional<SecurityDeposit> patch(Long id, SecurityDeposit baseEntity) {
        return empty();
    }

    @Override
    public Optional<SecurityDeposit> delete(Long id) {
        return empty();
    }

    @Override
    public Optional<SecurityDeposit> findByCode(String code) {
        return empty();
    }

    @Transactional
    public Optional<SecurityDeposit> instantiate(SecurityDeposit securityDepositInput, 
            SecurityDepositStatusEnum status, boolean validate) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, ImportInvoiceException, InvoiceExistException {
        return createOrinstantiate(securityDepositInput, status, validate, true);
    }
    
    @Transactional
    public Optional<SecurityDeposit> create(SecurityDeposit securityDepositInput, 
            SecurityDepositStatusEnum status, boolean validate) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, ImportInvoiceException, InvoiceExistException {
        return createOrinstantiate(securityDepositInput, status, validate, false);
    }
    
    public Optional<SecurityDeposit> createOrinstantiate(SecurityDeposit securityDepositInput, 
            SecurityDepositStatusEnum status, boolean validate, boolean isInstantiate) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, ImportInvoiceException, InvoiceExistException {
        // Check FinanceSettings.useSecurityDeposit
        FinanceSettings financeSettings = financeSettingsService.findLastOne();
        if (financeSettings == null || !financeSettings.isUseSecurityDeposit()) {
            throw new BadRequestException("instantiation is not allowed in general settings");
        }
        BigDecimal securityDepositAmount = securityDepositInput.getAmount();
        if (securityDepositAmount == null) {
            throw new EntityDoesNotExistsException("The Amount == null.");
        }
        if (securityDepositInput.getId() != null) {
            Optional<SecurityDeposit> sd = findById(securityDepositInput.getId());
            if (sd.isPresent()) {
                if (SecurityDepositStatusEnum.VALIDATED.equals(sd.get().getStatus())) {
                    throw new BusinessApiException("Modification of the security deposit is not allowed for Validated status.");
                } 
            }
        }        
                
        linkRealEntities(securityDepositInput);        
        
        org.meveo.model.billing.InvoiceLine invoiceLine = new org.meveo.model.billing.InvoiceLine();
        boolean updateComment = false;
        Invoice invoice = null;
        if(securityDepositInput.getSecurityDepositInvoice() != null) {
            invoice = invoiceService.findById(securityDepositInput.getSecurityDepositInvoice().getId());
            if(invoice == null) {
                throw new EntityDoesNotExistsException(Invoice.class, securityDepositInput.getSecurityDepositInvoice().getId());
            }               
            else {
                if(!"SECURITY_DEPOSIT".equals(invoice.getInvoiceType().getCode())) {
                    throw new BusinessApiException("Linked invoice should be a SECURITY_DEPOSIT");
                }

                if(invoice.getStatus() != InvoiceStatusEnum.NEW && invoice.getStatus() != InvoiceStatusEnum.DRAFT) {
                    throw new BusinessApiException("Linked invoice status should be NEW or DRAFT");
                }

                if(!invoice.getBillingAccount().getCustomerAccount().getCustomer().getId().equals(securityDepositInput.getCustomerAccount().getCustomer().getId())) {
                    throw new BusinessApiException("Linked invoice should have the same Customer as the Security Deposit");
                }

                if(ListUtils.isEmtyCollection(invoice.getInvoiceLines())) {
                    throw new BusinessApiException("Linked invoice should have invoice lines");
                }

                if(invoice.getAmountWithoutTax() == null) {
                    throw new BusinessApiException("Linked invoice cannot have amountWithoutTax null");
                }
            }
        }
        else {
            if (isInstantiate) {
                invoice = invoiceService.createBasicInvoiceFromSD(securityDepositInput);
                invoiceLine = invoiceLineService.createInvoiceLineWithInvoiceAndSD(securityDepositInput, invoice, invoiceLine);
                updateComment = true;
            }            
        }
        securityDepositInput.setSecurityDepositInvoice(invoice);
        
        // Check Maximum amount per Security deposit
        BigDecimal maxAmountPerSecurityDeposit = financeSettings.getMaxAmountPerSecurityDeposit();
        if (maxAmountPerSecurityDeposit != null && securityDepositAmount.compareTo(maxAmountPerSecurityDeposit) > 0) {
            throw new BadRequestException("The amount is greater than the maximum per security deposit : " + maxAmountPerSecurityDeposit);
        }

        // Check Maximum Security deposit amount per Client
        BigDecimal maxAmountPerCustomer = financeSettings.getMaxAmountPerCustomer();
        if (maxAmountPerCustomer != null) {
            BigDecimal sumAmountPerCustomer = securityDepositService.sumAmountPerCustomer(securityDepositInput.getCustomerAccount());
            if (sumAmountPerCustomer != null) {
                BigDecimal totalAmount = securityDepositAmount.add(sumAmountPerCustomer);
                if (totalAmount.compareTo(maxAmountPerCustomer) > 0) {
                    throw new BadRequestException("Security deposit amount is greater than the maximum per customer");
                }
            }
        }

        SecurityDepositTemplate template = securityDepositInput.getTemplate();
        // UNIQUE Security deposit Name
        Long count = securityDepositService.countPerTemplate(template);
        String securityDepositName = securityDepositInput.getCode();
        if (StringUtils.isBlank(securityDepositName)) {
            securityDepositName = template.getTemplateName();
        }
        securityDepositInput.setCode(securityDepositName + "-" + count);
        securityDepositInput.setStatus(status);
        
        // Check validity dates
        if (financeSettings.isAutoRefund() && template.isAllowValidityDate() && template.isAllowValidityPeriod()) {
            if (securityDepositInput.getValidityDate() == null && securityDepositInput.getValidityPeriod() == null) {
                throw new BadRequestException("At least one of the two options (SD.validityDate or SD.validityPeriod) should be filled");
            }
        }
        if (securityDepositInput.getValidityDate() != null && ((isSameDay(securityDepositInput.getValidityDate(), new Date()))
                || securityDepositInput.getValidityDate().before(new Date()))) {
            throw new BadRequestException("Validity must be in the future");
        }
        if (securityDepositInput.getValidityPeriod() != null && securityDepositInput.getValidityPeriod() <= 0) {
            throw new BadRequestException("0 and negative values not allowed");
        }
        if (securityDepositInput.getValidityPeriod() != null && securityDepositInput.getValidityPeriodUnit() == null) {
            throw new BadRequestException("The validity period unit must be not null or empty");
        }

        // Check The min and max amount
        if (template.getMinAmount() != null && template.getMinAmount().compareTo(securityDepositAmount) > 0) {
            throw new BadRequestException("The amount should be greater or equal to minimum amount (of SD template) : " + template.getMinAmount());
        }
        if (template.getMaxAmount() != null && template.getMaxAmount().compareTo(securityDepositAmount) < 0) {
            throw new BadRequestException("The amount should be lesser or equal to maximum amount (of SD template) : " + template.getMaxAmount());
        }

        // Check The if subscription not null the serviceInstance cannot be null 
        if(securityDepositInput.getSubscription() != null && securityDepositInput.getServiceInstance() == null) {
            throw new BadRequestException("The service instance is mandatory if subscription is set");
        }

        if (securityDepositInput.getId() != null) {
            securityDepositService.update(securityDepositInput);    
        }
        else {
            securityDepositService.create(securityDepositInput);
        }
                
        if (updateComment) {
            invoiceLine.setLabel("Generated invoice for Security Deposit {" + securityDepositInput.getId() + "}");
            invoiceLineService.update(invoiceLine);
        }

        // Increment template.NumberOfInstantiation after each instantiation
        Integer numberOfInstantiation = template.getNumberOfInstantiation() != null ? template.getNumberOfInstantiation() : 0;
        template.setNumberOfInstantiation(++numberOfInstantiation);
        securityDepositTemplateService.update(template);

        return of(securityDepositInput);
    }

    public void linkRealEntities(SecurityDeposit securityDepositInput) {
        if (securityDepositInput.getTemplate() != null) {
            SecurityDepositTemplate securityDepositTemplate = securityDepositTemplateService.tryToFindByCodeOrId(securityDepositInput.getTemplate());
            securityDepositInput.setTemplate(securityDepositTemplate);
        }

        if (securityDepositInput.getCurrency() != null) {
            Currency currency = currencyService.tryToFindByCodeOrId(securityDepositInput.getCurrency());
            validateNotNull(securityDepositInput.getCurrency(), currency);
            securityDepositInput.setCurrency(currency);
        }

        if(securityDepositInput.getCurrency() == null) {
        	Currency defaultCurrency = Optional.ofNullable(securityDepositInput.getTemplate())
        										.map(SecurityDepositTemplate::getCurrency)
        										.orElse(providerService.getProvider().getCurrency());
        	securityDepositInput.setCurrency(defaultCurrency);
        }

        if (securityDepositInput.getCustomerAccount() != null) {
            CustomerAccount customerAccount = customerAccountService.tryToFindByCodeOrId(securityDepositInput.getCustomerAccount());
            securityDepositInput.setCustomerAccount(customerAccount);
        }

        if (securityDepositInput.getSubscription() != null) {
            Subscription subscription = subscriptionService.tryToFindByCodeOrId(securityDepositInput.getSubscription());
            securityDepositInput.setSubscription(subscription);
        }

        if (securityDepositInput.getServiceInstance() != null) {
            ServiceInstance serviceInstance = serviceInstanceService.tryToFindByCodeOrId(securityDepositInput.getServiceInstance());
            securityDepositInput.setServiceInstance(serviceInstance);
        }
        
        if (securityDepositInput.getSecurityDepositInvoice() != null) {
            Invoice invoice = invoiceService.findById(securityDepositInput.getSecurityDepositInvoice().getId());
            securityDepositInput.setSecurityDepositInvoice(invoice);
        }
        else {
            securityDepositInput.setSecurityDepositInvoice(null);
        }
        
        if (securityDepositInput.getBillingAccount() != null) {
            BillingAccount billingAccount = billingAccountService.tryToFindByCodeOrId(securityDepositInput.getBillingAccount());
            if(billingAccount != null) {
                securityDepositInput.setBillingAccount(billingAccount);
                CustomerAccount customerAccount = billingAccount.getCustomerAccount();
                customerAccount = customerAccountService.refreshOrRetrieve(customerAccount);
                if (customerAccount != null) {
                    if (!securityDepositInput.getCustomerAccount().equals(customerAccount)) {
                        throw new BusinessApiException("Customer Account not equal Customer Account in Billing Account");
                    }
                    securityDepositInput.setCustomerAccount(customerAccount);
                }
            }
        }
    }

    private <B extends BaseEntity> void validateNotNull(B input, B result) {
        if (result == null) {
            StringBuilder sb = new StringBuilder(splitCamelCase(input.getClass().getSimpleName()));
            sb.append(" no found");
            if (input.getId() != null) {
                sb.append(" with id =").append(input.getId());
            }
            throw new EntityDoesNotExistsException(sb.toString());
        }
    }

    private String splitCamelCase(String s) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase("ExampleTest"), ' ');
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    @Transactional
	public void refund(SecurityDeposit securityDepositToUpdate, String reason, SecurityDepositOperationEnum securityDepositOperationEnum, SecurityDepositStatusEnum securityDepositStatusEnum, String operationType) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, ImportInvoiceException, InvoiceExistException, IOException {

		securityDepositToUpdate = securityDepositService.refreshOrRetrieve(securityDepositToUpdate);
		
		Invoice adjustmentInvoice = createAdjustmentInvoice(securityDepositToUpdate);
        securityDepositToUpdate.setSecurityDepositAdjustment(adjustmentInvoice);
        securityDepositService.update(securityDepositToUpdate);
		
		securityDepositService.refund(securityDepositToUpdate, reason, securityDepositOperationEnum, securityDepositStatusEnum, operationType);
	}

	private Invoice createAdjustmentInvoice(SecurityDeposit securityDepositToUpdate) throws MissingParameterException, EntityDoesNotExistsException, BusinessException, ImportInvoiceException, InvoiceExistException, IOException {
		// Create adjustment invoice
        org.meveo.apiv2.billing.BasicInvoice adjInvoice = ImmutableBasicInvoice.builder()
                .invoiceTypeCode("ADJ")
                .billingAccountCode(securityDepositToUpdate.getBillingAccount().getCode())
                .invoiceDate(new Date())
                .amountWithTax(securityDepositToUpdate.getCurrentBalance())
                .build();
        Invoice adjustmentInvoice = invoiceService.createBasicInvoice(adjInvoice);
        invoiceService.getEntityManager().flush();

        // Create Invoice Line
        org.meveo.apiv2.billing.InvoiceLine invoiceLineResource = ImmutableInvoiceLine.builder()
                .accountingArticleCode("ART_SECURITY_DEPOSIT")
                .amountTax(BigDecimal.ZERO)
                .amountWithoutTax(securityDepositToUpdate.getCurrentBalance())
                .amountWithTax(securityDepositToUpdate.getCurrentBalance())
                .unitPrice(securityDepositToUpdate.getCurrentBalance())
                .invoiceId(adjustmentInvoice.getId())
                .label("Security Deposit Adjustment")
                .quantity(BigDecimal.ONE)
                .build();

        org.meveo.apiv2.billing.InvoiceLinesInput input = ImmutableInvoiceLinesInput.builder()
                .addInvoiceLines(invoiceLineResource)
                .build();

        invoiceApiService.createLines(adjustmentInvoice, input);
        
        // Validate ADJ Invoice
        adjustmentInvoice.setStatus(VALIDATED);
        serviceSingleton.assignInvoiceNumber(adjustmentInvoice, true);
        
		return adjustmentInvoice;
	}
}