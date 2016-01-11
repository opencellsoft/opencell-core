package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
import org.meveo.api.dto.payment.DunningInclusionExclusionDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
public class CustomerAccountApiService extends AccountApiService {

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
	
	@Inject
	private CustomFieldInstanceService customFieldInstanceService;
	
	@EJB
	private AccountHierarchyApiService accountHierarchyApiService;

	public void create(CustomerAccountDto postData, User currentUser)
			throws MeveoApiException, DuplicateDefaultAccountException {
		create(postData, currentUser, true);
	}

	public void create(CustomerAccountDto postData, User currentUser,
			boolean checkCustomFields) throws MeveoApiException,
			DuplicateDefaultAccountException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomer())
				&& !StringUtils.isBlank(postData.getCurrency())
				&& !StringUtils.isBlank(postData.getLanguage())
				&& !StringUtils.isBlank(postData.getName())
				&& !StringUtils.isBlank(postData.getName().getLastName())) {
			Provider provider = currentUser.getProvider();
			// check if already exists
			if (customerAccountService.findByCode(postData.getCode(),
					currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(CustomerAccount.class,
						postData.getCode());
			}

			Customer customer = customerService.findByCode(
					postData.getCustomer(), provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class,
						postData.getCustomer());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(postData.getCurrency(), provider);
			if (tradingCurrency == null) {
				throw new EntityDoesNotExistsException(TradingCurrency.class,
						postData.getCurrency());
			}

			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(postData.getLanguage(), provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class,
						postData.getLanguage());
			}

			CustomerAccount customerAccount = new CustomerAccount();
			populate(postData, customerAccount, currentUser);
			customerAccount.setDateDunningLevel(new Date());
			customerAccount.setCustomer(customer);
			customerAccount.setTradingCurrency(tradingCurrency);
			customerAccount.setTradingLanguage(tradingLanguage);
			try {
				customerAccount.setPaymentMethod(PaymentMethodEnum
						.valueOf(postData.getPaymentMethod()));
			} catch (IllegalArgumentException | NullPointerException e) {
				log.warn("error generated while setting payment method", e);
			}
			if (!StringUtils.isBlank(postData.getCreditCategory())) {
				customerAccount.setCreditCategory(creditCategoryService
						.findByCode(postData.getCreditCategory(), provider));
			}
			customerAccount.setMandateDate(postData.getMandateDate());
			customerAccount.setMandateIdentification(postData
					.getMandateIdentification());

			if (postData.getContactInformation() != null) {
				customerAccount.getContactInformation().setEmail(
						postData.getContactInformation().getEmail());
				customerAccount.getContactInformation().setPhone(
						postData.getContactInformation().getPhone());
				customerAccount.getContactInformation().setMobile(
						postData.getContactInformation().getMobile());
				customerAccount.getContactInformation().setFax(
						postData.getContactInformation().getFax());
			}
			
			
			customerAccountService.create(customerAccount, currentUser,
					provider);
			
            // Validate and populate customFields
            try {
                populateCustomFields(postData.getCustomFields(), customerAccount, true, currentUser, checkCustomFields);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new MeveoApiException("Failed to associate custom field instance to an entity");
            }
            
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
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
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getName().getLastName())) {
				missingParameters.add("name.lastName");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public void update(CustomerAccountDto postData, User currentUser)
			throws MeveoApiException, DuplicateDefaultAccountException {
		update(postData, currentUser, true);
	}

	public void update(CustomerAccountDto postData, User currentUser,
			boolean checkCustomFields) throws MeveoApiException,
			DuplicateDefaultAccountException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomer())
				&& !StringUtils.isBlank(postData.getCurrency())
				&& !StringUtils.isBlank(postData.getLanguage())
				&& !StringUtils.isBlank(postData.getName())
				&& !StringUtils.isBlank(postData.getName().getLastName())) {
			Provider provider = currentUser.getProvider();
			// check if already exists
			CustomerAccount customerAccount = customerAccountService
					.findByCode(postData.getCode(), currentUser.getProvider());
			if (customerAccount == null) {
				throw new EntityDoesNotExistsException(CustomerAccount.class,
						postData.getCode());
			}

			if (!StringUtils.isBlank(postData.getCustomer())) {
				Customer customer = customerService.findByCode(
						postData.getCustomer(), provider);
				if (customer == null) {
					throw new EntityDoesNotExistsException(Customer.class,
							postData.getCustomer());
				}
				customerAccount.setCustomer(customer);
			}

			if (!StringUtils.isBlank(postData.getCurrency())) {
				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(postData.getCurrency(),
								provider);
				if (tradingCurrency == null) {
					throw new EntityDoesNotExistsException(
							TradingCurrency.class, postData.getCurrency());
				}
				customerAccount.setTradingCurrency(tradingCurrency);
			}

			if (!StringUtils.isBlank(postData.getLanguage())) {
				TradingLanguage tradingLanguage = tradingLanguageService
						.findByTradingLanguageCode(postData.getLanguage(),
								provider);
				if (tradingLanguage == null) {
					throw new EntityDoesNotExistsException(
							TradingLanguage.class, postData.getLanguage());
				}
				customerAccount.setTradingLanguage(tradingLanguage);
			}

			if (postData.getContactInformation() != null) {
				if (!StringUtils.isBlank(postData.getContactInformation()
						.getEmail())) {
					customerAccount.getContactInformation().setEmail(
							postData.getContactInformation().getEmail());
				}
				if (!StringUtils.isBlank(postData.getContactInformation()
						.getPhone())) {
					customerAccount.getContactInformation().setPhone(
							postData.getContactInformation().getPhone());
				}
				if (!StringUtils.isBlank(postData.getContactInformation()
						.getMobile())) {
					customerAccount.getContactInformation().setMobile(
							postData.getContactInformation().getMobile());
				}
				if (!StringUtils.isBlank(postData.getContactInformation()
						.getFax())) {
					customerAccount.getContactInformation().setFax(
							postData.getContactInformation().getFax());
				}
			}

			updateAccount(customerAccount, postData, currentUser, checkCustomFields);

			if (!StringUtils.isBlank(postData.getPaymentMethod())) {
				try {
					customerAccount.setPaymentMethod(PaymentMethodEnum
							.valueOf(postData.getPaymentMethod()));
				} catch (IllegalArgumentException e) {
					log.warn("PaymentMethodEnum={}",
							MeveoApiErrorCode.INVALID_ENUM_VALUE);
				}
			}
			if (!StringUtils.isBlank(postData.getCreditCategory())) {
				customerAccount.setCreditCategory(creditCategoryService
						.findByCode(postData.getCreditCategory(), provider));
			}
			if (!StringUtils.isBlank(postData.getMandateDate())) {
				customerAccount.setMandateDate(postData.getMandateDate());
			}
			if (!StringUtils.isBlank(postData.getMandateIdentification())) {
				customerAccount.setMandateIdentification(postData
						.getMandateIdentification());
			}

			customerAccountService.updateAudit(customerAccount, currentUser);
			
	         // Validate and populate customFields
            try {
                populateCustomFields(postData.getCustomFields(), customerAccount, false, currentUser, checkCustomFields);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("Failed to associate custom field instance to an entity", e);
                throw new MeveoApiException("Failed to associate custom field instance to an entity");
            }
            
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
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
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getName().getLastName())) {
				missingParameters.add("name.lastName");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

    public CustomerAccountDto find(String customerAccountCode, User currentUser) throws Exception {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");

            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        Provider provider = currentUser.getProvider();
        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
        if (customerAccount == null) {
            throw new BusinessException("Cannot find customer account with code=" + customerAccountCode);
        }

        CustomerAccountDto customerAccountDto = accountHierarchyApiService.customerAccountToDto(customerAccount);
       
        BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, customerAccount.getCode(), new Date(), customerAccount.getProvider());

        if (balance == null) {
            throw new BusinessException("account balance calculation failed");
        }

        customerAccountDto.setBalance(balance);

        return customerAccountDto;
	}

	public void remove(String customerAccountCode, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(customerAccountCode)) {
			missingParameters.add("customerAccountCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode, provider);
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
			throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}

	}

	public CustomerAccountsDto listByCustomer(String customerCode,
			Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerCode)) {
			Customer customer = customerService.findByCode(customerCode,
					provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class,
						customerCode);
			}

			CustomerAccountsDto result = new CustomerAccountsDto();
			List<CustomerAccount> customerAccounts = customerAccountService
					.listByCustomer(customer);
			if (customerAccounts != null) {
				for (CustomerAccount ca : customerAccounts) {
					result.getCustomerAccount().add(accountHierarchyApiService.customerAccountToDto(ca));
				}
			}

			return result;
		} else {
			missingParameters.add("customerCode");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void dunningExclusionInclusion(
			DunningInclusionExclusionDto dunningDto, Provider provider)
			throws MeveoApiException {
		try {
			for (String ref : dunningDto.getInvoiceReferences()) {
				AccountOperation accountOp = accountOperationService
						.findByReference(ref, provider);
				if (accountOp == null) {
					throw new EntityDoesNotExistsException(
							AccountOperation.class,
							"no account operation with this reference " + ref);
				}
				if (accountOp instanceof RecordedInvoice) {
					accountOp.setExcludedFromDunning(dunningDto.getExclude());
					accountOperationService.update(accountOp);
				} else {
					throw new BusinessEntityException(accountOp.getReference()
							+ " is not an invoice account operation");
				}
				if (accountOp.getMatchingStatus() == MatchingStatusEnum.P) {
					for (MatchingAmount matchingAmount : accountOp
							.getMatchingAmounts()) {
						MatchingCode matchingCode = matchingAmount
								.getMatchingCode();
						for (MatchingAmount ma : matchingCode
								.getMatchingAmounts()) {
							AccountOperation accountoperation = ma
									.getAccountOperation();
							accountoperation.setExcludedFromDunning(dunningDto
									.getExclude());
							accountOperationService.update(accountoperation);
						}
					}
				}
			}
		} catch (EntityDoesNotExistsException | BusinessEntityException e) {
			throw new MeveoApiException(e.getMessage());
		}
	}

	public void createCreditCategory(CreditCategoryDto postData,
			User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {
			if (creditCategoryService.findByCode(postData.getCode(),
					currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(CreditCategory.class,
						postData.getCode());
			}

			CreditCategory creditCategory = new CreditCategory();
			creditCategory.setCode(postData.getCode());
			creditCategory.setDescription(postData.getDescription());

			creditCategoryService.create(creditCategory, currentUser,
					currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void updateCreditCategory(CreditCategoryDto postData,
			User currentUser) throws MeveoApiException {

		if (!StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getDescription())) {

			CreditCategory creditCategory = creditCategoryService.findByCode(
					postData.getCode(), currentUser.getProvider());

			if (creditCategory == null) {
				throw new EntityDoesNotExistsException(CreditCategory.class,
						postData.getCode());
			}

			creditCategory.setCode(postData.getCode());
			creditCategory.setDescription(postData.getDescription());

			creditCategoryService.update(creditCategory, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdateCreditCategory(CreditCategoryDto postData,
			User currentUser) throws MeveoApiException {

		if (!StringUtils.isBlank(postData.getCode())) {
			if (creditCategoryService.findByCode(postData.getCode(),
					currentUser.getProvider()) == null) {
				createCreditCategory(postData, currentUser);
			} else {
				updateCreditCategory(postData, currentUser);
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

	}

	public void removeCreditCategory(String code, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("creditCategoryCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		CreditCategory creditCategory = creditCategoryService.findByCode(code, provider);
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
			throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Cannot delete entity");
		}
	}

	public void createOrUpdate(CustomerAccountDto postData, User currentUser)
			throws MeveoApiException, DuplicateDefaultAccountException {

		if (customerAccountService.findByCode(postData.getCode(),
				currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
