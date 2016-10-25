package org.meveo.api.account;

import java.math.BigDecimal;
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
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.UserParser;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class CustomerAccountApi extends AccountApi {

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

	public void create(CustomerAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {
		create(postData, currentUser, true);
	}

	public CustomerAccount create(CustomerAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, BusinessException {
		return create(postData, currentUser, true, null);
	}

	public CustomerAccount create(CustomerAccountDto postData, User currentUser, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

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

		handleMissingParameters();

		Provider provider = currentUser.getProvider();
		// check if already exists
		if (customerAccountService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(CustomerAccount.class, postData.getCode());
		}

		Customer customer = customerService.findByCode(postData.getCustomer(), provider);
		if (customer == null) {
			throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
		}

		TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency(), provider);
		if (tradingCurrency == null) {
			throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
		}

		TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage(), provider);
		if (tradingLanguage == null) {
			throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
		}

		CustomerAccount customerAccount = new CustomerAccount();
		populate(postData, customerAccount, currentUser);
		customerAccount.setDateDunningLevel(new Date());
		customerAccount.setCustomer(customer);
		customerAccount.setTradingCurrency(tradingCurrency);
		customerAccount.setTradingLanguage(tradingLanguage);
		customerAccount.setPaymentMethod(postData.getPaymentMethod());
		if (!StringUtils.isBlank(postData.getCreditCategory())) {
			customerAccount.setCreditCategory(creditCategoryService.findByCode(postData.getCreditCategory(), provider));
		}
		customerAccount.setMandateDate(postData.getMandateDate());
		customerAccount.setMandateIdentification(postData.getMandateIdentification());
		customerAccount.setExternalRef1(postData.getExternalRef1());
		customerAccount.setExternalRef2(postData.getExternalRef2());

		if (postData.getContactInformation() != null) {
			customerAccount.getContactInformation().setEmail(postData.getContactInformation().getEmail());
			customerAccount.getContactInformation().setPhone(postData.getContactInformation().getPhone());
			customerAccount.getContactInformation().setMobile(postData.getContactInformation().getMobile());
			customerAccount.getContactInformation().setFax(postData.getContactInformation().getFax());
		}

		if(businessAccountModel != null){
			customerAccount.setBusinessAccountModel(businessAccountModel);
		}

		customerAccountService.create(customerAccount, currentUser);

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), customerAccount, true, currentUser, checkCustomFields);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

		return customerAccount;
	}

	public void update(CustomerAccountDto postData, User currentUser) throws MeveoApiException, DuplicateDefaultAccountException {
		update(postData, currentUser, true);
	}

	public CustomerAccount update(CustomerAccountDto postData, User currentUser, boolean checkCustomFields) throws MeveoApiException, DuplicateDefaultAccountException {
		return update(postData, currentUser, true, null);
	}

	public CustomerAccount update(CustomerAccountDto postData, User currentUser, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, DuplicateDefaultAccountException {

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

		handleMissingParameters();

		Provider provider = currentUser.getProvider();
		// check if already exists
		CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode(), currentUser.getProvider());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCode());
		}

		if (!StringUtils.isBlank(postData.getCustomer())) {
			Customer customer = customerService.findByCode(postData.getCustomer(), provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, postData.getCustomer());
			}
			customerAccount.setCustomer(customer);
		}

		if (!StringUtils.isBlank(postData.getCurrency())) {
			TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrency(), provider);
			if (tradingCurrency == null) {
				throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCurrency());
			}
			customerAccount.setTradingCurrency(tradingCurrency);
		}

		if (!StringUtils.isBlank(postData.getLanguage())) {
			TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage(), provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
			}
			customerAccount.setTradingLanguage(tradingLanguage);
		}

		if (postData.getContactInformation() != null) {
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

		updateAccount(customerAccount, postData, currentUser, checkCustomFields);

		if (postData.getPaymentMethod() != null) {
			customerAccount.setPaymentMethod(postData.getPaymentMethod());
		}
		if (!StringUtils.isBlank(postData.getCreditCategory())) {
			customerAccount.setCreditCategory(creditCategoryService.findByCode(postData.getCreditCategory(), provider));
		}
		if (!StringUtils.isBlank(postData.getMandateDate())) {
			customerAccount.setMandateDate(postData.getMandateDate());
		}
		if (!StringUtils.isBlank(postData.getMandateIdentification())) {
			customerAccount.setMandateIdentification(postData.getMandateIdentification());
		}

		if (!StringUtils.isBlank(postData.getExternalRef1())) {
			customerAccount.setExternalRef1(postData.getExternalRef1());
		}
		if (!StringUtils.isBlank(postData.getExternalRef2())) {
			customerAccount.setExternalRef2(postData.getExternalRef2());
		}

		if(businessAccountModel != null) {
			customerAccount.setBusinessAccountModel(businessAccountModel);
		}

		try {
			customerAccount = customerAccountService.update(customerAccount, currentUser);
		} catch (BusinessException e1) {
			throw new MeveoApiException(e1.getMessage());
		}

		// Validate and populate customFields
		try {
			populateCustomFields(postData.getCustomFields(), customerAccount, false, currentUser, checkCustomFields);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

		return customerAccount;
	}

	@SecuredBusinessEntityMethod(
			validate = @SecureMethodParameter(entity = CustomerAccount.class), 
			user = @SecureMethodParameter(index = 1, parser = UserParser.class))
	public CustomerAccountDto find(String customerAccountCode, User currentUser) throws Exception {

		if (StringUtils.isBlank(customerAccountCode)) {
			missingParameters.add("customerAccountCode");
			handleMissingParameters();
		}

		Provider provider = currentUser.getProvider();
		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
		if (customerAccount == null) {
			throw new BusinessException("Cannot find customer account with code=" + customerAccountCode);
		}

		CustomerAccountDto customerAccountDto = accountHierarchyApi.customerAccountToDto(customerAccount);

		BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, customerAccount.getCode(), new Date(), customerAccount.getProvider());

		if (balance == null) {
			throw new BusinessException("account balance calculation failed");
		}

		customerAccountDto.setBalance(balance);

		return customerAccountDto;
	}

	public void remove(String customerAccountCode, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(customerAccountCode)) {
			missingParameters.add("customerAccountCode");
			handleMissingParameters();
		}

		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, currentUser.getProvider());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
		}
		try {
			customerAccountService.remove(customerAccount, currentUser);
			customerAccountService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(CustomerAccount.class, customerAccountCode);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}

	}

	public CustomerAccountsDto listByCustomer(String customerCode, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(customerCode)) {
			missingParameters.add("customerCode");
			handleMissingParameters();
		}
		Customer customer = customerService.findByCode(customerCode, provider);
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

	public void dunningExclusionInclusion(DunningInclusionExclusionDto dunningDto, User currentUser) throws EntityDoesNotExistsException, BusinessApiException {
		try {
			for (String ref : dunningDto.getInvoiceReferences()) {
				AccountOperation accountOp = accountOperationService.findByReference(ref, currentUser.getProvider());
				if (accountOp == null) {
					throw new EntityDoesNotExistsException(AccountOperation.class, "no account operation with this reference " + ref);
				}
				if (accountOp instanceof RecordedInvoice) {
					accountOp.setExcludedFromDunning(dunningDto.getExclude());
					accountOperationService.update(accountOp, currentUser);
				} else {
					throw new BusinessEntityException(accountOp.getReference() + " is not an invoice account operation");
				}
				if (accountOp.getMatchingStatus() == MatchingStatusEnum.P) {
					for (MatchingAmount matchingAmount : accountOp.getMatchingAmounts()) {
						MatchingCode matchingCode = matchingAmount.getMatchingCode();
						for (MatchingAmount ma : matchingCode.getMatchingAmounts()) {
							AccountOperation accountoperation = ma.getAccountOperation();
							accountoperation.setExcludedFromDunning(dunningDto.getExclude());
							accountOperationService.update(accountoperation, currentUser);
						}
					}
				}
			}
		} catch (BusinessException e) {
			throw new BusinessApiException(e);
		}
	}

	public void createCreditCategory(CreditCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			handleMissingParameters();
		}

		if (creditCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(CreditCategory.class, postData.getCode());
		}

		CreditCategory creditCategory = new CreditCategory();
		creditCategory.setCode(postData.getCode());
		creditCategory.setDescription(postData.getDescription());

		creditCategoryService.create(creditCategory, currentUser);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void updateCreditCategory(CreditCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			handleMissingParameters();
		}

		CreditCategory creditCategory = creditCategoryService.findByCode(postData.getCode(), currentUser.getProvider());

		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, postData.getCode());
		}

		creditCategory.setCode(postData.getCode());
		creditCategory.setDescription(postData.getDescription());

		creditCategoryService.update(creditCategory, currentUser);
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void createOrUpdateCreditCategory(CreditCategoryDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
			handleMissingParameters();
		}

		if (creditCategoryService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			createCreditCategory(postData, currentUser);
		} else {
			updateCreditCategory(postData, currentUser);
		}
	}

	public void removeCreditCategory(String code, User currentUser) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("creditCategoryCode");
			handleMissingParameters();
		}
		CreditCategory creditCategory = creditCategoryService.findByCode(code, currentUser.getProvider());
		if (creditCategory == null) {
			throw new EntityDoesNotExistsException(CreditCategory.class, code);
		}
		try {
			creditCategoryService.remove(creditCategory, currentUser);
			creditCategoryService.commit();
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(CreditCategory.class, code);
			}
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	public void createOrUpdate(CustomerAccountDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (customerAccountService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}

	public CustomerAccount closeAccount(CustomerAccountDto postData, User currentUser) throws EntityDoesNotExistsException, BusinessException {
		CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode(), currentUser.getProvider());
		if (customerAccount == null) {
			throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCode());
		}

		customerAccountService.closeCustomerAccount(customerAccount, currentUser);

		return customerAccount;
	}

	public void createOrUpdatePartial(CustomerAccountDto customerAccountDto, User currentUser) throws MeveoApiException, BusinessException {
		CustomerAccountDto existedCustomerAccountDto = null;
		try {
			existedCustomerAccountDto = find(customerAccountDto.getCode(), currentUser);
		} catch (Exception e) {
			existedCustomerAccountDto = null;
		}
		log.debug("createOrUpdate customerAccount {}", customerAccountDto);
		if (existedCustomerAccountDto == null) {// create
			create(customerAccountDto, currentUser);
		} else {// update

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
			if (!StringUtils.isBlank(customerAccountDto.getMandateDate())) {
				existedCustomerAccountDto.setMandateDate(customerAccountDto.getMandateDate());
			}
			if (!StringUtils.isBlank(customerAccountDto.getMandateIdentification())) {
				existedCustomerAccountDto.setMandateIdentification(customerAccountDto.getMandateIdentification());
			}

			if (customerAccountDto.getPaymentMethod() != null) {
				existedCustomerAccountDto.setPaymentMethod(customerAccountDto.getPaymentMethod());
			}
			if (!StringUtils.isBlank(customerAccountDto.getCreditCategory())) {
				existedCustomerAccountDto.setCreditCategory(customerAccountDto.getCreditCategory());
			}
			if (customerAccountDto.getDunningLevel() != null) {
				existedCustomerAccountDto.setDunningLevel(customerAccountDto.getDunningLevel());
			}
			//
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
			accountHierarchyApi.populateNameAddress(existedCustomerAccountDto, customerAccountDto, currentUser);
			if (StringUtils.isBlank(customerAccountDto.getCustomFields())) {
				existedCustomerAccountDto.setCustomFields(customerAccountDto.getCustomFields());
			}
			update(existedCustomerAccountDto, currentUser);
		}
	}
}