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
import org.meveo.api.dto.GDPRInfoDto;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.account.TransferCustomerAccountDto;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * CRUD API for {@link CustomerAccount}.
 *
 * @author Edward P. Legaspi
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
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
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private PaymentMethodApi paymentMethodApi;

    @EJB
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    private BillingAccountApi billingAccountApi;

    @Inject
    private AddressBookService addressBookService;

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    public CustomerAccount create(CustomerAccountDto postData) throws MeveoApiException, BusinessException {
        return create(postData, true);
    }

    public CustomerAccount create(CustomerAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null);
    }

    public CustomerAccount create(CustomerAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

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

        handleMissingParameters(postData);

        if (postData.getPaymentMethods() != null) {
            for (PaymentMethodDto paymentMethodDto : postData.getPaymentMethods()) {
                paymentMethodDto.setCustomerCode(postData.getCustomer());
                paymentMethodApi.validate(paymentMethodDto, false);
            }
        }

        // check if already exists
        if (customerAccountService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(CustomerAccount.class, postData.getCode());
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
            paymentMethodApi.validate(paymentMethodDto, false);
            postData.getPaymentMethods().add(paymentMethodDto);

        }

        CustomerAccount customerAccount = new CustomerAccount();

        dtoToEntity(customerAccount, postData, checkCustomFields, businessAccountModel);

        if (StringUtils.isBlank(postData.getCode())) {
            customerAccount.setCode(customGenericEntityCodeService.getGenericEntityCode(customerAccount));
        }

//        if (postData.getPaymentMethods() != null) {
//            for (PaymentMethodDto paymentMethodDto : postData.getPaymentMethods()) {
//                paymentMethodApi.validate(paymentMethodDto, false);
//                customerAccount.addPaymentMethod(paymentMethodDto.fromDto(customerAccount, currentUser));
//            }
//        }

        customerAccountService.create(customerAccount);

        return customerAccount;
    }

    public CustomerAccount update(CustomerAccountDto postData) throws MeveoApiException, BusinessException {
        return update(postData, true);
    }

    public CustomerAccount update(CustomerAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return update(postData, true, null);
    }

    public CustomerAccount update(CustomerAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
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

        dtoToEntity(customerAccount, postData, checkCustomFields, businessAccountModel);

        customerAccount = customerAccountService.update(customerAccount);

        return customerAccount;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param userAccount Entity to populate
     * @param postData DTO entity object to populate from
     * @param checkCustomField Should a check be made if CF field is required
     * @param businessAccountModel Business account model
     **/
    private void dtoToEntity(CustomerAccount customerAccount, CustomerAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) {

        boolean isNew = customerAccount.getId() == null;

        if (!StringUtils.isBlank(postData.getCustomer())) {
            Customer customer = customerService.findByCode(postData.getCustomer());
            if (customer == null) {
                throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
            } else if (!isNew && !customerAccount.getCustomer().equals(customer)) {
                throw new InvalidParameterException("Can not change the parent account. Customer account's current parent account (customer) is " + customerAccount.getCustomer().getCode());
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

        updateAccount(customerAccount, postData, checkCustomFields);

        if (postData.getCreditCategory() != null) {
            if (StringUtils.isBlank(postData.getCreditCategory())) {
                customerAccount.setCreditCategory(null);
            } else {
                CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCreditCategory());
                if (creditCategory == null) {
                    throw new EntityDoesNotExistsException(CreditCategory.class, postData.getCreditCategory());
                } else {
                    customerAccount.setCreditCategory(creditCategory);
                }
            }
        }

        if (postData.getDueDateDelayEL() != null) {
            customerAccount.setDueDateDelayEL(StringUtils.isBlank(postData.getDueDateDelayEL()) ? null : postData.getDueDateDelayEL());
        }
        if (postData.getDueDateDelayELSpark() != null) {
            customerAccount.setDueDateDelayELSpark(StringUtils.isBlank(postData.getDueDateDelayELSpark()) ? null : postData.getDueDateDelayELSpark());
        }

        if (postData.isExcludedFromPayment() != null) {
            customerAccount.setExcludedFromPayment(postData.isExcludedFromPayment());
        }

        if (postData.getDunningLevel() != null) {
            customerAccount.setDunningLevel(postData.getDunningLevel());
        }

        if (postData.getDateDunningLevel() != null) {
            customerAccount.setDateDunningLevel(postData.getDateDunningLevel());
        } else if (isNew) {
            customerAccount.setDateDunningLevel(new Date());
        }

        if (businessAccountModel != null) {
            customerAccount.setBusinessAccountModel(businessAccountModel);
        }
        if (postData.getInvoicingThreshold() != null) {
            customerAccount.setInvoicingThreshold(postData.getInvoicingThreshold());
        }
        if (postData.isThresholdPerEntity() != null) {
            customerAccount.setThresholdPerEntity(postData.isThresholdPerEntity());
        }
        if (postData.getCheckThreshold() != null) {
            customerAccount.setCheckThreshold(postData.getCheckThreshold());
        }
        // Synchronize payment methods
        updatePaymentMethods(customerAccount, postData);

        // Create a default payment method if non was specified
        if (customerAccount.getPaymentMethods() == null || customerAccount.getPaymentMethods().isEmpty()) {

            // Start compatibility with pre-4.6 versions
            PaymentMethodEnum defaultPaymentMethod = postData.getPaymentMethod();

            if (defaultPaymentMethod != null && !defaultPaymentMethod.isSimple()) {
                throw new InvalidParameterException("Please specify payment method via 'paymentMethods' attribute, as currently specified payment method requires additional information");
            } else if (defaultPaymentMethod == null) {
                // End of compatibility with pre-4.6 versions

                defaultPaymentMethod = PaymentMethodEnum.valueOf(paramBeanFactory.getInstance().getProperty("api.default.customerAccount.paymentMethodType", "CHECK"));
            }

            if (defaultPaymentMethod == null || !defaultPaymentMethod.isSimple()) {
                throw new InvalidParameterException(
                    "Please specify payment method, as currently specified default payment method (in api.default.customerAccount.paymentMethodType) is invalid or requires additional information");
            }

            PaymentMethod paymentMethodFromDto = (new PaymentMethodDto(defaultPaymentMethod)).fromDto(customerAccount, null, currentUser);
            customerAccount.addPaymentMethod(paymentMethodFromDto);
        }

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), customerAccount, isNew, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        if (customerAccount.getAddressbook() == null) {
            AddressBook addressBook = new AddressBook("CA_" + customerAccount.getCode());
            addressBookService.create(addressBook);
            customerAccount.setAddressbook(addressBook);
        }

    }

    private void updatePaymentMethods(CustomerAccount customerAccount, CustomerAccountDto postData) {
        if (postData.getPaymentMethods() != null && !postData.getPaymentMethods().isEmpty()) {
            if (customerAccount.getPaymentMethods() == null) {
                customerAccount.setPaymentMethods(new ArrayList<PaymentMethod>());
            }

            List<PaymentMethod> paymentMethodsFromDto = new ArrayList<PaymentMethod>();

            for (PaymentMethodDto paymentMethodDto : postData.getPaymentMethods()) {
                PaymentMethod paymentMethodFromDto = paymentMethodDto.fromDto(customerAccount, null, currentUser);

                int index = customerAccount.getPaymentMethods().indexOf(paymentMethodFromDto);
                if (index < 0) {
                    customerAccount.addPaymentMethod(paymentMethodFromDto);
                    paymentMethodsFromDto.add(paymentMethodFromDto);
                } else {
                    PaymentMethod paymentMethod = customerAccount.getPaymentMethods().get(index);
                    paymentMethod.updateWith(paymentMethodFromDto);
                    paymentMethodsFromDto.add(paymentMethod);
                    customerAccount.addPaymentMethodToAudit(new Object() {
                    }.getClass().getEnclosingMethod().getName(), paymentMethod);
                }

            }
            customerAccount.getPaymentMethods().retainAll(paymentMethodsFromDto);
        }
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
    public CustomerAccountDto find(String customerAccountCode, boolean calculateBalances) throws Exception {
        return find(customerAccountCode, calculateBalances, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, false, false);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
    public CustomerAccountDto find(String customerAccountCode, boolean calculateBalances, CustomFieldInheritanceEnum inheritCF, boolean withAccountOperations, boolean includeBillingAccounts) {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        CustomerAccountDto customerAccountDto = accountHierarchyApi.customerAccountToDto(customerAccount, inheritCF, calculateBalances, includeBillingAccounts);

        if (withAccountOperations) {
            List<AccountOperation> accountOperations = customerAccount.getAccountOperations();
            if (accountOperations != null && !accountOperations.isEmpty()) {
                List<AccountOperationDto> accountOperationsDto = new ArrayList<>();
                for (AccountOperation accountOperation : accountOperations) {
                    AccountOperationDto accountOperationDto = new AccountOperationDto(accountOperation, entityToDtoConverter.getCustomFieldsDTO(accountOperation, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                    accountOperationsDto.add(accountOperationDto);
                }
                customerAccountDto.setAccountOperations(accountOperationsDto);
            }
        }

        return customerAccountDto;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
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
     * @param postData posted data to API
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
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
     * @param postData posted data to API
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
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

    public CustomerAccount createOrUpdate(CustomerAccountDto postData) throws MeveoApiException, BusinessException {
        CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode());
        if (customerAccount == null) {
            customerAccount = create(postData);
        } else {
            customerAccount = update(postData);
        }
        return customerAccount;
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
            }
            if (!StringUtils.isBlank(customerAccountDto.getVatNo())) {
                existedCustomerAccountDto.setVatNo(customerAccountDto.getVatNo());
            }
            if (!StringUtils.isBlank(customerAccountDto.getRegistrationNo())) {
                existedCustomerAccountDto.setRegistrationNo(customerAccountDto.getRegistrationNo());
            }
            accountHierarchyApi.populateNameAddress(existedCustomerAccountDto, customerAccountDto);
            if (customerAccountDto.getCustomFields() != null && !customerAccountDto.getCustomFields().isEmpty()) {
                existedCustomerAccountDto.setCustomFields(customerAccountDto.getCustomFields());
            }
            update(existedCustomerAccountDto);
        }
    }

    /**
     * Exports a json representation of the CustomerAcount hierarchy. It include subscription, accountOperations and invoices.
     *
     * @param ca the selected CustomerAccount
     * @return DTO representation of a CustomerAccount
     */
    public CustomerAccountDto exportCustomerAccountHierarchy(CustomerAccount ca, List<GDPRInfoDto> customerAccountGdpr) {
        CustomerAccountDto result = new CustomerAccountDto(ca, customerAccountGdpr);

        if (ca.getAccountOperations() != null && !ca.getAccountOperations().isEmpty()) {
            for (AccountOperation ao : ca.getAccountOperations()) {
                result.getAccountOperations().add(new AccountOperationDto(ao));
            }
        }

        if (ca.getBillingAccounts() != null && !ca.getBillingAccounts().isEmpty()) {
            for (BillingAccount ba : ca.getBillingAccounts()) {
                List<GDPRInfoDto> billingAccountGDPR = customFieldTemplateService.findCFMarkAsAnonymize(ba);
                result.getBillingAccounts().getBillingAccount().add(billingAccountApi.exportBillingAccountHierarchy(ba, billingAccountGDPR));
            }
        }

        return result;
    }

    /**
     * Transfer amount from a customer account to an other.
     *
     * @param transferCustomerAccountDto
     */
    public void transferAccount(TransferCustomerAccountDto transferCustomerAccountDto) {

        if (StringUtils.isBlank(transferCustomerAccountDto.getFromCustomerAccountCode())) {
            missingParameters.add("fromCustomerAccountCode");
        }
        if (StringUtils.isBlank(transferCustomerAccountDto.getToCustomerAccountCode())) {
            missingParameters.add("toCustomerAccountCode");
        }
        if (StringUtils.isBlank(transferCustomerAccountDto.getAmount())) {
            missingParameters.add("amount");
        }
        handleMissingParameters();

        customerAccountService.transferAccount(transferCustomerAccountDto.getFromCustomerAccountCode(), transferCustomerAccountDto.getToCustomerAccountCode(), transferCustomerAccountDto.getAmount());
    }

    /**
     * Returns list of counters at a given date for a customer account.
     * 
     * @param customerAccountCode the customer account code
     * @param date the selected date
     * @return a counter instance list
     */
    public List<CounterInstance> filterCountersByPeriod(String customerAccountCode, Date date) {
        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        if (StringUtils.isBlank(date)) {
            throw new BusinessApiException("date is null");
        }

        return new ArrayList<>(customerAccountService.filterCountersByPeriod(customerAccount.getCounters(), date).values());
    }
}