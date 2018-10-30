package org.meveo.api.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.BeanUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0.1
 **/

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class BillingAccountApi extends AccountEntityApi {

    @Inject
    private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private CustomerAccountService customerAccountService;

    @EJB
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    private InvoiceApi invoiceApi;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private DiscountPlanService discountPlanService;
    
    @Inject
    private WalletOperationService walletOperationService;
    
    @Inject
    private RatedTransactionService ratedTransactionService;
    
    @Inject
    private UserAccountApi userAccountApi;

    public void create(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public BillingAccount create(BillingAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public BillingAccount create(BillingAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCustomerAccount())) {
            missingParameters.add("customerAccount");
        }
        if (StringUtils.isBlank(postData.getBillingCycle())) {
            missingParameters.add("billingCycle");
        }
        if (StringUtils.isBlank(postData.getCountry())) {
            missingParameters.add("country");
        }
        if (StringUtils.isBlank(postData.getLanguage())) {
            missingParameters.add("language");
        }
        if (postData.getElectronicBilling() != null && postData.getElectronicBilling()) {
            if (StringUtils.isBlank(postData.getEmail())) {
                missingParameters.add("email");
            }
        }

        handleMissingParametersAndValidate(postData);

        if (billingAccountService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(BillingAccount.class, postData.getCode());
        }

        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
        }

        BillingCycle billingCycle = billingCycleService.findByCode(postData.getBillingCycle());
        if (billingCycle == null) {
            throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
        }

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage());
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
        }

        BillingAccount billingAccount = new BillingAccount();
        
        if (!StringUtils.isBlank(postData.getPhone())) {
        	postData.getContactInformation().setPhone(postData.getPhone());
		}

        if (!StringUtils.isBlank(postData.getEmail())) {
        	postData.getContactInformation().setEmail(postData.getEmail());
        }
        
        populate(postData, billingAccount);

        billingAccount.setCustomerAccount(customerAccount);
        billingAccount.setBillingCycle(billingCycle);
        billingAccount.setTradingCountry(tradingCountry);
        billingAccount.setTradingLanguage(tradingLanguage);
        billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
        billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
        billingAccount.setTerminationDate(postData.getTerminationDate());
        billingAccount.setInvoicingThreshold(postData.getInvoicingThreshold());
        billingAccount.setMinimumAmountEl(postData.getMinimumAmountEl());
        billingAccount.setMinimumLabelEl(postData.getMinimumLabelEl());

		if (!StringUtils.isBlank(postData.getDiscountPlan())) {
			postData.addDiscountPlan(postData.getDiscountPlan());
		} else {
			billingAccount.setDiscountPlan(null);
		}
        
		if (postData.getDiscountPlans() != null && !postData.getDiscountPlans().isEmpty()) {
			for (String discountPlanCode : postData.getDiscountPlans()) {
				DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanCode);
				billingAccount.addDiscountPlanNullSafe(discountPlan);
			}
		}
        
        if (postData.getElectronicBilling() == null) {
            billingAccount.setElectronicBilling(false);
        } else {
            billingAccount.setElectronicBilling(postData.getElectronicBilling());
        }
        billingAccount.setExternalRef1(postData.getExternalRef1());
        billingAccount.setExternalRef2(postData.getExternalRef2());

        if (businessAccountModel != null) {
            billingAccount.setBusinessAccountModel(businessAccountModel);
        }

        // Update payment method information in a customer account.
        // ONLY used to handle deprecated billingAccountDto.paymentMethod and billingAccountDto.bankCoordinates fields. Use
        createOrUpdatePaymentMethodInCA(postData, billingAccount);

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingAccount, true, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        billingAccountService.createBillingAccount(billingAccount);

        return billingAccount;
    }

    public void update(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        update(postData, true);
    }

    public BillingAccount update(BillingAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return update(postData, true, null);
    }

    public BillingAccount update(BillingAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getBillingCycle())) {
            missingParameters.add("billingCycle");
        }
        if (StringUtils.isBlank(postData.getCountry())) {
            missingParameters.add("country");
        }
        if (StringUtils.isBlank(postData.getLanguage())) {
            missingParameters.add("language");
        }
        if (postData.getElectronicBilling() != null && postData.getElectronicBilling()) {
            if (StringUtils.isBlank(postData.getEmail())) {
                missingParameters.add("email");
            }
        }

        handleMissingParametersAndValidate(postData);

        BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
        }

        if (!StringUtils.isBlank(postData.getCustomerAccount())) {
            CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCustomerAccount());
            if (customerAccount == null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());
            } else if (!billingAccount.getCustomerAccount().equals(customerAccount)) {
                // a safeguard to allow this only if all the WO/RT have been invoiced.
                Long countNonTreatedWO = walletOperationService.countNonTreatedWOByBA(billingAccount);
                if(countNonTreatedWO > 0) {
                    throw new BusinessApiException("Can not change the parent account. Billing account have non treated WO");
                }
                Long countNonInvoicedRT = ratedTransactionService.countNotInvoicedRTByBA(billingAccount);
                if(countNonInvoicedRT > 0) {
                    throw new BusinessApiException("Can not change the parent account. Billing account have non invoiced RT");
                }
            }
            billingAccount.setCustomerAccount(customerAccount);
        }

        if (!StringUtils.isBlank(postData.getBillingCycle())) {
            BillingCycle billingCycle = billingCycleService.findByCode(postData.getBillingCycle());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
            }
            billingAccount.setBillingCycle(billingCycle);
        }

        if (!StringUtils.isBlank(postData.getCountry())) {
            TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
            }
            billingAccount.setTradingCountry(tradingCountry);
        }

        if (!StringUtils.isBlank(postData.getLanguage())) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage());
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
            }
            billingAccount.setTradingLanguage(tradingLanguage);
        }

        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            billingAccount.setExternalRef1(postData.getExternalRef1());
        }
        if (!StringUtils.isBlank(postData.getExternalRef2())) {
            billingAccount.setExternalRef2(postData.getExternalRef2());
        }
        
        if (!StringUtils.isBlank(postData.getPhone())) {
            postData.getContactInformation().setPhone(postData.getPhone());
        }
        if (!StringUtils.isBlank(postData.getEmail())) {
        	postData.getContactInformation().setEmail(postData.getEmail());
        }

        updateAccount(billingAccount, postData, checkCustomFields);

        if (!StringUtils.isBlank(postData.getNextInvoiceDate())) {
            billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
        }
        if (!StringUtils.isBlank(postData.getSubscriptionDate())) {
            billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
        }
        if (!StringUtils.isBlank(postData.getTerminationDate())) {
            billingAccount.setTerminationDate(postData.getTerminationDate());
        }
        if (postData.getElectronicBilling() != null) {
            billingAccount.setElectronicBilling(postData.getElectronicBilling());
        }
        if (postData.getInvoicingThreshold() != null) {
            billingAccount.setInvoicingThreshold(postData.getInvoicingThreshold());
        }
        if (!StringUtils.isBlank(postData.getMinimumAmountEl())) {
            billingAccount.setMinimumAmountEl(postData.getMinimumAmountEl());
        }
        if (!StringUtils.isBlank(postData.getMinimumLabelEl())) {
            billingAccount.setMinimumLabelEl(postData.getMinimumLabelEl());
        }
        if (!StringUtils.isBlank(postData.getDiscountPlan())) {
			postData.addDiscountPlan(postData.getDiscountPlan());
        } else if (postData.getDiscountPlan() != null) {
            billingAccount.setDiscountPlan(null);
        }
        
		if (postData.getDiscountPlans() != null && !postData.getDiscountPlans().isEmpty()) {
			for (String discountPlanCode : postData.getDiscountPlans()) {
				DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanCode);
				billingAccount.addDiscountPlanNullSafe(discountPlan);
			}
		}

        if (businessAccountModel != null) {
            billingAccount.setBusinessAccountModel(businessAccountModel);
        }

        // Update payment method information in a customer account.
        // ONLY used to handle deprecated billingAccountDto.paymentMethod and billingAccountDto.bankCoordinates fields. Use
        createOrUpdatePaymentMethodInCA(postData, billingAccount);

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingAccount, false, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        try {
            billingAccount = billingAccountService.update(billingAccount);
        } catch (BusinessException e1) {
            throw new MeveoApiException(e1.getMessage());
        }

        return billingAccount;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = BillingAccount.class))
    public BillingAccountDto find(String billingAccountCode) throws MeveoApiException {
        return find(billingAccountCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = BillingAccount.class))
    public BillingAccountDto find(String billingAccountCode, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {
        if (StringUtils.isBlank(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
            handleMissingParameters();
        }
        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }

        return accountHierarchyApi.billingAccountToDto(billingAccount, inheritCF);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = BillingAccount.class))
    public void remove(String billingAccountCode) throws MeveoApiException {
        if (StringUtils.isBlank(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
            handleMissingParameters();
        }
        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }
        try {
            billingAccountService.remove(billingAccount);
            billingAccountService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(BillingAccount.class, billingAccountCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    public BillingAccountsDto listByCustomerAccount(String customerAccountCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        BillingAccountsDto result = new BillingAccountsDto();
        List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount);
        if (billingAccounts != null) {
            for (BillingAccount ba : billingAccounts) {
                BillingAccountDto billingAccountDto = accountHierarchyApi.billingAccountToDto(ba);

                List<Invoice> invoices = ba.getInvoices();
                if (invoices != null && invoices.size() > 0) {
                    List<InvoiceDto> invoicesDto = new ArrayList<InvoiceDto>();
                    if (invoices != null && invoices.size() > 0) {
                        for (Invoice invoice : invoices) {
                            if (invoiceTypeService.getAdjustementCode().equals(invoice.getInvoiceType().getCode())) {
                                InvoiceDto invoiceDto = invoiceApi.invoiceToDto(invoice, false, false);
                                invoicesDto.add(invoiceDto);
                            }
                        }
                        billingAccountDto.setInvoices(invoicesDto);
                    }
                }

                result.getBillingAccount().add(billingAccountDto);
            }
        }

        return result;
    }

    /**
     * Create or update Billing Account based on Billing Account Code
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        if (billingAccountService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    public BillingAccount terminate(BillingAccountDto postData) throws MeveoApiException {
        SubscriptionTerminationReason terminationReason = null;
        try {
            terminationReason = subscriptionTerminationReasonService.findByCodeReason(postData.getTerminationReason());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (terminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
        }

        BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
        }

        try {
            billingAccountService.billingAccountTermination(billingAccount, postData.getTerminationDate(), terminationReason);
        } catch (BusinessException e) {
            log.error("Failed terminating a billingAccount with code={}. {}", postData.getCode(), e.getMessage());
            throw new MeveoApiException("Failed terminating billingAccount with code=" + postData.getCode());
        }

        return billingAccount;
    }

    public List<CounterInstance> filterCountersByPeriod(String billingAccountCode, Date date) throws MeveoApiException, BusinessException {

        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);

        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }

        if (StringUtils.isBlank(date)) {
            throw new MeveoApiException("date is null");
        }

        return new ArrayList<>(billingAccountService.filterCountersByPeriod(billingAccount.getCounters(), date).values());
    }

    public void createOrUpdatePartial(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        BillingAccountDto existedBillingAccountDto = null;
        try {
            existedBillingAccountDto = find(postData.getCode());
        } catch (Exception e) {
            existedBillingAccountDto = null;
        }
        log.debug("createOrUpdate billingAccount {}", postData);
        if (existedBillingAccountDto == null) {// create
            create(postData);
        } else {// update
            if (postData.getTerminationDate() != null) {
                if (StringUtils.isBlank(postData.getTerminationReason())) {
                    missingParameters.add("billingAccount.terminationReason");
                    handleMissingParametersAndValidate(postData);
                }
                terminate(postData);
            } else {

                if (!StringUtils.isBlank(postData.getCustomerAccount())) {
                    existedBillingAccountDto.setCustomerAccount(postData.getCustomerAccount());
                }

                if (!StringUtils.isBlank(postData.getBillingCycle())) {
                    existedBillingAccountDto.setBillingCycle(postData.getBillingCycle());
                }
                if (!StringUtils.isBlank(postData.getCountry())) {
                    existedBillingAccountDto.setCountry(postData.getCountry());
                }
                if (!StringUtils.isBlank(postData.getLanguage())) {
                    existedBillingAccountDto.setLanguage(postData.getLanguage());
                }

                //
                if (!StringUtils.isBlank(postData.getNextInvoiceDate())) {
                    existedBillingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
                }
                if (!StringUtils.isBlank(postData.getSubscriptionDate())) {
                    existedBillingAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
                }
                if (!StringUtils.isBlank(postData.getTerminationDate())) {
                    existedBillingAccountDto.setTerminationDate(postData.getTerminationDate());
                }
                if (!StringUtils.isBlank(postData.getElectronicBilling())) {
                    existedBillingAccountDto.setElectronicBilling(postData.getElectronicBilling());
                }
                if (!StringUtils.isBlank(postData.getEmail())) {
                    existedBillingAccountDto.setEmail(postData.getEmail());
                }
                if (!StringUtils.isBlank(postData.getMinimumAmountEl())) {
                    existedBillingAccountDto.setMinimumAmountEl(postData.getMinimumAmountEl());
                }
                if (!StringUtils.isBlank(postData.getMinimumLabelEl())) {
                    existedBillingAccountDto.setMinimumLabelEl(postData.getMinimumLabelEl());
                }
                if (postData.getInvoicingThreshold() != null) {
                    existedBillingAccountDto.setInvoicingThreshold(postData.getInvoicingThreshold());
                }
                //
                accountHierarchyApi.populateNameAddress(existedBillingAccountDto, postData);
                if (postData.getCustomFields() != null && !postData.getCustomFields().isEmpty()) {
                    existedBillingAccountDto.setCustomFields(postData.getCustomFields());
                }
                update(existedBillingAccountDto);
            }
        }
    }

    /**
     * Update payment method information in a customer account. ONLY used to handle deprecated billingAccountDto.paymentMethod and billingAccountDto.bankCoordinates fields. Use
     * CustomerAccounDto.paymentMethods instead.
     * 
     * @param postData Billing account DTO
     * @param billingAccount Billing account to update if necessary
     * @throws MeveoApiException
     * @throws BusinessException General business exception
     */
    private void createOrUpdatePaymentMethodInCA(BillingAccountDto postData, BillingAccount billingAccount) throws MeveoApiException, BusinessException {

        if (postData.getPaymentMethod() == null) {
            return;
        }

        CustomerAccount customerAccount = billingAccount.getCustomerAccount();

        if (postData.getPaymentMethod() == PaymentMethodEnum.CARD) {
            throw new InvalidParameterException("paymentMethod", "Card");
        } else if (postData.getPaymentMethod() == PaymentMethodEnum.DIRECTDEBIT) {
            if (postData.getBankCoordinates() == null) {
                throw new MissingParameterException("bankCoordinates");
            }

            if (StringUtils.isBlank(postData.getBankCoordinates().getIban())) {
                throw new MissingParameterException("iban");
            }
        }

        boolean found = false;
        boolean updateCA = false;

        if (customerAccount.getPaymentMethods() != null) {
            for (PaymentMethod paymentMethod : customerAccount.getPaymentMethods()) {
                if (postData.getPaymentMethod() == paymentMethod.getPaymentType()) {

                    if (postData.getPaymentMethod().isSimple()) {
                        found = true;
                        break;

                    } else if (postData.getPaymentMethod() == PaymentMethodEnum.DIRECTDEBIT) {

                        if (postData.getBankCoordinates().getIban() != null && ((DDPaymentMethod) paymentMethod).getBankCoordinates() != null
                                && postData.getBankCoordinates().getIban().equals(((DDPaymentMethod) paymentMethod).getBankCoordinates().getIban())) {
                            found = true;
                            BankCoordinates bankCoordinatesFromDto = postData.getBankCoordinates().fromDto();

                            if (!BeanUtils.isIdentical(bankCoordinatesFromDto, ((DDPaymentMethod) paymentMethod).getBankCoordinates())) {
                                ((DDPaymentMethod) paymentMethod).setBankCoordinates(bankCoordinatesFromDto);
                                updateCA = true;
                            }
                            break;
                        }

                    }
                }
            }
        }

        if (!found) {
            PaymentMethod paymentMethodFromDto = null;
            if (postData.getPaymentMethod() == PaymentMethodEnum.CHECK || postData.getPaymentMethod() == PaymentMethodEnum.WIRETRANSFER) {
                paymentMethodFromDto = (new PaymentMethodDto(postData.getPaymentMethod())).fromDto(customerAccount, currentUser);
            } else if (postData.getPaymentMethod() == PaymentMethodEnum.DIRECTDEBIT) {
                paymentMethodFromDto = (new PaymentMethodDto(postData.getPaymentMethod(), postData.getBankCoordinates(), null, null)).fromDto(customerAccount, currentUser);
            }

            if (customerAccount.getPaymentMethods() == null) {
                customerAccount.setPaymentMethods(new ArrayList<>());
            }
            customerAccount.getPaymentMethods().add(paymentMethodFromDto);

            updateCA = true;
        }

        if (updateCA) {
            customerAccount = customerAccountService.update(customerAccount);
            billingAccount.setCustomerAccount(customerAccount);
        }
    }

    /**
     * Exports a json representation of the BillingAcount hierarchy. It include subscription, accountOperations and invoices.
     * 
     * @param ba the selected BillingAccount
     * @return DTO representation of BillingAccount
     */
	public BillingAccountDto exportBillingAccountHierarchy(BillingAccount ba) {
		BillingAccountDto result = new BillingAccountDto(ba);

		if (ba.getInvoices() != null && !ba.getInvoices().isEmpty()) {
			for (Invoice invoice : ba.getInvoices()) {
				result.getInvoices().add(invoiceApi.invoiceToDto(invoice, true, false));
			}
		}

		if (ba.getUsersAccounts() != null && !ba.getUsersAccounts().isEmpty()) {
			for (UserAccount ua : ba.getUsersAccounts()) {
				result.getUserAccounts().getUserAccount().add(userAccountApi.exportUserAccountHierarchy(ua));
			}
		}

		return result;
	}

}