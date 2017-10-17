package org.meveo.api.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.payment.DunningInclusionExclusionDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CustomerAccountApi extends AccountEntityApi {

    @Inject
    private CreditCategoryService creditCategoryService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private CustomerService customerService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @EJB
    private AccountHierarchyApi accountHierarchyApi;

    public void create(CustomerAccountDto postData) throws MeveoApiException, BusinessException {
        create(postData, true);
    }

    public CustomerAccount create(CustomerAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public CustomerAccount create(CustomerAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCustomer())) {
            missingParameters.add("customer");
        }
        if (StringUtils.isBlank(postData.getCurrency())) {
            missingParameters.add("currency");
        }
        if (StringUtils.isBlank(postData.getLanguage())) {
            missingParameters.add("language");
        }
        if (postData.getName() != null && !StringUtils.isBlank(postData.getName().getTitle()) && StringUtils.isBlank(postData.getName().getLastName())) {
            missingParameters.add("name.lastName");
        }

        handleMissingParametersAndValidate(postData);

        if (postData.getPaymentMethods() != null) {
            for (PaymentMethodDto paymentMethodDto : postData.getPaymentMethods()) {
                paymentMethodDto.validate();
            }
        }

        // check if already exists
        if (customerAccountService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomerAccount.class, postData.getCode());
        }

        Customer customer = customerService.findByCode(postData.getCustomer());
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
        }

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency());
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage());
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
        }
        // Start compatibility with pre-4.6 versions
        if (postData.getPaymentMethods() == null || postData.getPaymentMethods().isEmpty()) {
            postData.setPaymentMethods(new ArrayList<>());
            PaymentMethodDto paymentMethodDto = null;
            if (postData.getPaymentMethod() != null) {
            	paymentMethodDto = new PaymentMethodDto(postData.getPaymentMethod(), null, postData.getMandateIdentification(), postData.getMandateDate());               
            } else if (!StringUtils.isBlank(postData.getMandateIdentification())) {
            	paymentMethodDto = new PaymentMethodDto(PaymentMethodEnum.DIRECTDEBIT, null, postData.getMandateIdentification(), postData.getMandateDate());
            } else {
            	paymentMethodDto = new PaymentMethodDto(PaymentMethodEnum.CHECK);
            }
            paymentMethodDto.validate();
            postData.getPaymentMethods().add(paymentMethodDto);
            
        }

        CustomerAccount customerAccount = new CustomerAccount();
        populate(postData, customerAccount);
        customerAccount.setDateDunningLevel(new Date());
        customerAccount.setCustomer(customer);
        customerAccount.setTradingCurrency(tradingCurrency);
        customerAccount.setTradingLanguage(tradingLanguage);
        if (!StringUtils.isBlank(postData.getCreditCategory())) {
            customerAccount.setCreditCategory(creditCategoryService.findByCode(postData.getCreditCategory()));
        }
        customerAccount.setExternalRef1(postData.getExternalRef1());
        customerAccount.setExternalRef2(postData.getExternalRef2());
        customerAccount.setDueDateDelayEL(postData.getDueDateDelayEL());

        if (postData.getContactInformation() != null) {
            if (customerAccount.getContactInformation() == null) {
                customerAccount.setContactInformation(new ContactInformation());
            }
            customerAccount.getContactInformation().setEmail(postData.getContactInformation().getEmail());
            customerAccount.getContactInformation().setPhone(postData.getContactInformation().getPhone());
            customerAccount.getContactInformation().setMobile(postData.getContactInformation().getMobile());
            customerAccount.getContactInformation().setFax(postData.getContactInformation().getFax());
        }

        if (postData.getPaymentMethods() != null) {
            for (PaymentMethodDto paymentMethodDto : postData.getPaymentMethods()) {
            	paymentMethodDto.validate();
                customerAccount.addPaymentMethod(paymentMethodDto.fromDto(customerAccount));
            }
        }

        if (businessAccountModel != null) {
            customerAccount.setBusinessAccountModel(businessAccountModel);
        }
        customerAccount.setExcludedFromPayment(postData.isExcludedFromPayment());
        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customerAccount, true, checkCustomFields);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        customerAccountService.create(customerAccount);

        return customerAccount;
    }

    public void update(CustomerAccountDto postData) throws MeveoApiException, DuplicateDefaultAccountException {
        update(postData, true);
    }

    public CustomerAccount update(CustomerAccountDto postData, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {
        return update(postData, true, null);
    }

    public CustomerAccount update(CustomerAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel)
            throws MeveoApiException, DuplicateDefaultAccountException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getCurrency())) {
            missingParameters.add("currency");
        }
        if (StringUtils.isBlank(postData.getLanguage())) {
            missingParameters.add("language");
        }
        if (postData.getName() != null && !StringUtils.isBlank(postData.getName().getTitle()) && StringUtils.isBlank(postData.getName().getLastName())) {
            missingParameters.add("name.lastName");
        }

        handleMissingParametersAndValidate(postData);

        // check if already exists
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCode());
        }
        customerAccount.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());

        if (!StringUtils.isBlank(postData.getCustomer())) {
            Customer customer = customerService.findByCode(postData.getCustomer());
            if (customer == null) {
                throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
            } else if (!customerAccount.getCustomer().equals(customer)) {
                throw new InvalidParameterException(
                    "Can not change the parent account. Customer account's current parent account (customer) is " + customerAccount.getCustomer().getCode());
            }
            customerAccount.setCustomer(customer);
        }

        if (!StringUtils.isBlank(postData.getCurrency())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
            }
            customerAccount.setTradingCurrency(tradingCurrency);
        }

        if (!StringUtils.isBlank(postData.getLanguage())) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage());
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
            }
            customerAccount.setTradingLanguage(tradingLanguage);
        }

        if (postData.getContactInformation() != null) {
            if (customerAccount.getContactInformation() == null) {
                customerAccount.setContactInformation(new ContactInformation());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getEmail())) {
                customerAccount.getContactInformation().setEmail(postData.getContactInformation().getEmail());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getPhone())) {
                customerAccount.getContactInformation().setPhone(postData.getContactInformation().getPhone());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getMobile())) {
                customerAccount.getContactInformation().setMobile(postData.getContactInformation().getMobile());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getFax())) {
                customerAccount.getContactInformation().setFax(postData.getContactInformation().getFax());
            }
        }

        updateAccount(customerAccount, postData, checkCustomFields);

        if (!StringUtils.isBlank(postData.getCreditCategory())) {
            customerAccount.setCreditCategory(creditCategoryService.findByCode(postData.getCreditCategory()));
        }

        if (!StringUtils.isBlank(postData.getExternalRef1())) {
            customerAccount.setExternalRef1(postData.getExternalRef1());
        }
        if (!StringUtils.isBlank(postData.getExternalRef2())) {
            customerAccount.setExternalRef2(postData.getExternalRef2());
        }
        if (!StringUtils.isBlank(postData.getDueDateDelayEL())) {
            customerAccount.setDueDateDelayEL(postData.getDueDateDelayEL());
        }
        
        if (!StringUtils.isBlank(postData.isExcludedFromPayment())) {
            customerAccount.setExcludedFromPayment(postData.isExcludedFromPayment());
        }
        
        if (!StringUtils.isBlank(postData.getDunningLevel())) {
        	customerAccount.setDunningLevel(postData.getDunningLevel());
        }
        
        // Synchronize payment methods
        if (postData.getPaymentMethods() != null && !postData.getPaymentMethods().isEmpty()) {
            if (customerAccount.getPaymentMethods() == null) {
                customerAccount.setPaymentMethods(new ArrayList<PaymentMethod>());
            }

            List<PaymentMethod> paymentMethodsFromDto = new ArrayList<PaymentMethod>();

            for (PaymentMethodDto paymentMethodDto : postData.getPaymentMethods()) {
                PaymentMethod paymentMethodFromDto = paymentMethodDto.fromDto(customerAccount);

                int index = customerAccount.getPaymentMethods().indexOf(paymentMethodFromDto);
                if (index < 0) {
                    customerAccount.addPaymentMethod(paymentMethodFromDto);
                    paymentMethodsFromDto.add(paymentMethodFromDto);
                } else {
                    PaymentMethod paymentMethod = customerAccount.getPaymentMethods().get(index);
                    paymentMethod.updateWith(paymentMethodFromDto);
                    paymentMethodsFromDto.add(paymentMethod);
                }

            }
            customerAccount.getPaymentMethods().retainAll(paymentMethodsFromDto);
        }

        // Create a default payment method if non was specified
        if (customerAccount.getPaymentMethods() == null || customerAccount.getPaymentMethods().isEmpty()) {

            // Start compatibility with pre-4.6 versions
            PaymentMethodEnum defaultPaymentMethod = postData.getPaymentMethod();

            if (defaultPaymentMethod != null && !defaultPaymentMethod.isSimple()) {
                throw new InvalidParameterException(
                    "Please specify payment method via 'paymentMethods' attribute, as currently specified payment method requires additional information");
            } else if (defaultPaymentMethod == null) {
                // End of compatibility with pre-4.6 versions

                defaultPaymentMethod = PaymentMethodEnum.valueOf(ParamBean.getInstance().getProperty("api.default.customerAccount.paymentMethodType", "CHECK"));
            }

            if (defaultPaymentMethod == null || !defaultPaymentMethod.isSimple()) {
                throw new InvalidParameterException(
                    "Please specify payment method, as currently specified default payment method (in api.default.customerAccount.paymentMethodType) is invalid or requires additional information");
            }

            PaymentMethod paymentMethodFromDto = (new PaymentMethodDto(defaultPaymentMethod)).fromDto(customerAccount);
            customerAccount.addPaymentMethod(paymentMethodFromDto);
        }

        if (businessAccountModel != null) {
            customerAccount.setBusinessAccountModel(businessAccountModel);
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customerAccount, false, checkCustomFields);
        } catch (MissingParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        try {
            customerAccount = customerAccountService.update(customerAccount);
        } catch (BusinessException e1) {
            throw new MeveoApiException(e1.getMessage());
        }

        return customerAccount;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entity = CustomerAccount.class))
    public CustomerAccountDto find(String customerAccountCode, Boolean calculateBalances) throws Exception {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        if (calculateBalances == null) {
            calculateBalances = Boolean.TRUE;
        }

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        CustomerAccountDto customerAccountDto = accountHierarchyApi.customerAccountToDto(customerAccount);

        if (calculateBalances) {
            BigDecimal balanceDue = customerAccountService.customerAccountBalanceDue(customerAccount, new Date());
            BigDecimal totalInvoiceBalance = customerAccountService.customerAccountBalanceExigibleWithoutLitigation(customerAccount, new Date());

            if (balanceDue == null) {
                throw new BusinessException("Balance due calculation failed.");
            }

            if (totalInvoiceBalance == null) {
                throw new BusinessException("Total invoice balance calculation failed.");
            }
            customerAccountDto.setBalance(balanceDue);
            customerAccountDto.setTotalInvoiceBalance(totalInvoiceBalance);
        }

        return customerAccountDto;
    }

    public void remove(String customerAccountCode) throws MeveoApiException {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }
        try {
            customerAccountService.remove(customerAccount);
            customerAccountService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(CustomerAccount.class, customerAccountCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }

    }

    public CustomerAccountsDto listByCustomer(String customerCode) throws MeveoApiException {

        if (StringUtils.isBlank(customerCode)) {
            missingParameters.add("customerCode");
            handleMissingParameters();
        }
        Customer customer = customerService.findByCode(customerCode);
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, customerCode);
        }

        CustomerAccountsDto result = new CustomerAccountsDto();
        List<CustomerAccount> customerAccounts = customerAccountService.listByCustomer(customer);
        if (customerAccounts != null) {
            for (CustomerAccount ca : customerAccounts) {
                result.getCustomerAccount().add(accountHierarchyApi.customerAccountToDto(ca));
            }
        }

        return result;
    }

    public void dunningExclusionInclusion(DunningInclusionExclusionDto dunningDto) throws EntityDoesNotExistsException, BusinessException {

        for (String ref : dunningDto.getInvoiceReferences()) {
            AccountOperation accountOp = accountOperationService.findByReference(ref);
            if (accountOp == null) {
                throw new EntityDoesNotExistsException(AccountOperation.class, "no account operation with this reference " + ref);
            }
            if (accountOp instanceof RecordedInvoice) {
                accountOp.setExcludedFromDunning(dunningDto.getExclude());
                accountOperationService.update(accountOp);
            } else {
                throw new BusinessEntityException(accountOp.getReference() + " is not an invoice account operation");
            }
            if (accountOp.getMatchingStatus() == MatchingStatusEnum.P) {
                for (MatchingAmount matchingAmount : accountOp.getMatchingAmounts()) {
                    MatchingCode matchingCode = matchingAmount.getMatchingCode();
                    for (MatchingAmount ma : matchingCode.getMatchingAmounts()) {
                        AccountOperation accountoperation = ma.getAccountOperation();
                        accountoperation.setExcludedFromDunning(dunningDto.getExclude());
                        accountOperationService.update(accountoperation);
                    }
                }
            }
        }
    }

    public void createCreditCategory(CreditCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        if (creditCategoryService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CreditCategory.class, postData.getCode());
        }

        CreditCategory creditCategory = new CreditCategory();
        creditCategory.setCode(postData.getCode());
        creditCategory.setDescription(postData.getDescription());

        creditCategoryService.create(creditCategory);
    }

    /**
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void updateCreditCategory(CreditCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCode());

        if (creditCategory == null) {
            throw new EntityDoesNotExistsException(CreditCategory.class, postData.getCode());
        }

        creditCategory.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        creditCategory.setDescription(postData.getDescription());

        creditCategoryService.update(creditCategory);
    }

    /**
     * 
     * @param postData
     * 
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdateCreditCategory(CreditCategoryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParametersAndValidate(postData);
        }

        if (creditCategoryService.findByCode(postData.getCode()) == null) {
            createCreditCategory(postData);
        } else {
            updateCreditCategory(postData);
        }
    }

    public void removeCreditCategory(String code) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("creditCategoryCode");
            handleMissingParameters();
        }
        CreditCategory creditCategory = creditCategoryService.findByCode(code);
        if (creditCategory == null) {
            throw new EntityDoesNotExistsException(CreditCategory.class, code);
        }
        try {
            creditCategoryService.remove(creditCategory);
            creditCategoryService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(CreditCategory.class, code);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    public void createOrUpdate(CustomerAccountDto postData) throws MeveoApiException, BusinessException {

        if (customerAccountService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    public CustomerAccount closeAccount(CustomerAccountDto postData) throws EntityDoesNotExistsException, BusinessException {
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCode());
        }

        customerAccountService.closeCustomerAccount(customerAccount);

        return customerAccount;
    }

    public void createOrUpdatePartial(CustomerAccountDto customerAccountDto) throws MeveoApiException, BusinessException {
        CustomerAccountDto existedCustomerAccountDto = null;
        try {
            existedCustomerAccountDto = find(customerAccountDto.getCode(), false);
        } catch (Exception e) {
            existedCustomerAccountDto = null;
        }
        log.debug("createOrUpdate customerAccount {}", customerAccountDto);
        if (existedCustomerAccountDto == null) {// create
            create(customerAccountDto);
        } else {// update

            if (!StringUtils.isBlank(customerAccountDto.getCustomer())) {
                existedCustomerAccountDto.setCustomer(customerAccountDto.getCustomer());
            }

            if (!StringUtils.isBlank(customerAccountDto.getCurrency())) {
                existedCustomerAccountDto.setCurrency(customerAccountDto.getCurrency());
            }
            if (!StringUtils.isBlank(customerAccountDto.getLanguage())) {
                existedCustomerAccountDto.setLanguage(customerAccountDto.getLanguage());
            }
            if (!StringUtils.isBlank(customerAccountDto.getStatus())) {
                existedCustomerAccountDto.setStatus(customerAccountDto.getStatus());
            }
            if (!StringUtils.isBlank(customerAccountDto.getDateStatus())) {
                existedCustomerAccountDto.setDateStatus(customerAccountDto.getDateStatus());
            }
            if (!StringUtils.isBlank(customerAccountDto.getDateDunningLevel())) {
                existedCustomerAccountDto.setDateDunningLevel(customerAccountDto.getDateDunningLevel());
            }

            if (!StringUtils.isBlank(customerAccountDto.getCreditCategory())) {
                existedCustomerAccountDto.setCreditCategory(customerAccountDto.getCreditCategory());
            }
            if (customerAccountDto.getDunningLevel() != null) {
                existedCustomerAccountDto.setDunningLevel(customerAccountDto.getDunningLevel());
            }

            if (customerAccountDto.getContactInformation() != null) {
                if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getEmail())) {
                    existedCustomerAccountDto.getContactInformation().setEmail(customerAccountDto.getContactInformation().getEmail());
                }
                if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getPhone())) {
                    existedCustomerAccountDto.getContactInformation().setPhone(customerAccountDto.getContactInformation().getPhone());
                }
                if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getMobile())) {
                    existedCustomerAccountDto.getContactInformation().setMobile(customerAccountDto.getContactInformation().getMobile());
                }
                if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getFax())) {
                    existedCustomerAccountDto.getContactInformation().setFax(customerAccountDto.getContactInformation().getFax());
                }
            }
            accountHierarchyApi.populateNameAddress(existedCustomerAccountDto, customerAccountDto);
            if (customerAccountDto.getCustomFields() != null && !customerAccountDto.getCustomFields().isEmpty()) {
                existedCustomerAccountDto.setCustomFields(customerAccountDto.getCustomFields());
            }
            update(existedCustomerAccountDto);
        }
    }

}