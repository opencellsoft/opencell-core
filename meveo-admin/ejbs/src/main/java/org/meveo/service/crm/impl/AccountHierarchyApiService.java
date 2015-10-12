package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.AccountHierarchyTypeEnum;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.account.FindAccountHierachyRequestDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.response.account.GetAccountHierarchyResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentTermEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.BillingAccountApiService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;

@Stateless
public class AccountHierarchyApiService extends BaseApi {

	@Inject
	private CustomerApiService customerApi;

	@Inject
	private CustomerAccountApiService customerAccountApi;

	@Inject
	private BillingAccountApiService billingAccountApi;

	@Inject
	private UserAccountApiService userAccountApi;

	@Inject
	private SellerApiService sellerApi;

	@Inject
	private CreditCategoryService creditCategoryService;

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private CountryService countryService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private SellerService sellerService;

	@Inject
	private LanguageService languageService;

	@Inject
	private CustomerService customerService;

	@Inject
	private TitleService titleService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private AccessService accessService;

	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private TerminationReasonService terminationReasonService;

	@Inject
	private ProviderService providerService;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	public static final String CUSTOMER_PREFIX = "CUST_";
	public static final String CUSTOMER_ACCOUNT_PREFIX = "CA_";
	public static final String BILLING_ACCOUNT_PREFIX = "BA_";
	public static final String USER_ACCOUNT_PREFIX = "UA_";

	public static final int CUST = 1;
	public static final int CA = 2;
	public static final int BA = 4;
	public static final int UA = 8;

	public static final String CRM_ACCOUNT_HIERARCHY_PARENT = "CRM_ACCOUNT_PARENT";
	public static final String CRM_ACCOUNT_HIERARCHY_TYPE = "CRM_ACCOUNT_TYPE";

	/**
	 * 
	 * Creates the customer heirarchy including : - Trading Country - Trading
	 * Currency - Trading Language - Customer Brand - Customer Category - Seller
	 * - Customer - Customer Account - Billing Account - User Account
	 * 
	 * Required Parameters :customerId, customerBrandCode,customerCategoryCode,
	 * sellerCode
	 * ,currencyCode,countryCode,lastName,languageCode,billingCycleCode
	 */

	public void create(AccountHierarchyDto postData, User currentUser)
			throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		if (customerService.findByCode(postData.getCustomerId(), provider) != null) {
			throw new EntityAlreadyExistsException(Customer.class,
					postData.getCustomerId());
		} else {
			if (!StringUtils.isBlank(postData.getCustomerId())
					&& !StringUtils.isBlank(postData.getCustomerBrandCode())
					&& !StringUtils.isBlank(postData.getCustomerCategoryCode())
					&& !StringUtils.isBlank(postData.getSellerCode())
					&& !StringUtils.isBlank(postData.getCurrencyCode())
					&& !StringUtils.isBlank(postData.getBillingCycleCode())
					&& !StringUtils.isBlank(postData.getCountryCode())
					&& !StringUtils.isBlank(postData.getLastName())
					&& !StringUtils.isBlank(postData.getLanguageCode())
					&& !StringUtils.isBlank(postData.getEmail())) {

				Seller seller = sellerService.findByCode(
						postData.getSellerCode(), provider);

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				TradingCountry tradingCountry = tradingCountryService
						.findByTradingCountryCode(postData.getCountryCode(),
								provider);

				if (tradingCountry == null) {
					Country country = countryService.findByCode(postData
							.getCountryCode());
					if (country == null) {
						throw new EntityDoesNotExistsException(Country.class,
								postData.getCountryCode());
					} else {
						// create tradingCountry
						tradingCountry = new TradingCountry();
						tradingCountry.setCountry(country);
						tradingCountry.setProvider(provider);
						tradingCountry.setActive(true);
						tradingCountry.setPrDescription(country
								.getDescriptionEn());
						tradingCountry.setAuditable(auditableTrading);
						tradingCountryService.create(tradingCountry,
								currentUser, provider);
					}
				}

				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(postData.getCurrencyCode(),
								provider);
				if (tradingCurrency == null) {
					Currency currency = currencyService.findByCode(postData
							.getCurrencyCode());

					if (currency == null) {
						throw new EntityDoesNotExistsException(Currency.class,
								postData.getCurrencyCode());
					} else {
						// create tradingCountry
						tradingCurrency = new TradingCurrency();
						tradingCurrency.setCurrencyCode(postData
								.getCurrencyCode());
						tradingCurrency.setCurrency(currency);
						tradingCurrency.setProvider(provider);
						tradingCurrency.setActive(true);
						tradingCurrency.setPrDescription(currency
								.getDescriptionEn());
						tradingCurrency.setAuditable(auditableTrading);
						tradingCurrencyService.create(tradingCurrency,
								currentUser, provider);
					}
				}

				TradingLanguage tradingLanguage = tradingLanguageService
						.findByTradingLanguageCode(postData.getLanguageCode(),
								provider);
				if (tradingLanguage == null) {
					Language language = languageService.findByCode(postData
							.getLanguageCode());

					if (language == null) {
						throw new EntityDoesNotExistsException(Language.class,
								postData.getLanguageCode());
					} else {
						// create tradingCountry
						tradingLanguage = new TradingLanguage();
						tradingLanguage.setLanguageCode(postData
								.getLanguageCode());
						tradingLanguage.setLanguage(language);
						tradingLanguage.setProvider(provider);
						tradingLanguage.setActive(true);
						tradingLanguage.setPrDescription(language
								.getDescriptionEn());
						tradingLanguage.setAuditable(auditableTrading);
						tradingLanguageService.create(tradingLanguage,
								currentUser, provider);
					}
				}

				CustomerBrand customerBrand = customerBrandService.findByCode(
						postData.getCustomerBrandCode(),
						currentUser.getProvider());

				if (customerBrand == null) {
					customerBrand = new CustomerBrand();
					customerBrand.setCode(StringUtils
							.normalizeHierarchyCode(postData
									.getCustomerBrandCode()));
					customerBrand.setDescription(postData
							.getCustomerBrandCode());
					customerBrandService.create(customerBrand, currentUser,
							provider);
				}

				CustomerCategory customerCategory = customerCategoryService
						.findByCode(postData.getCustomerCategoryCode(),
								currentUser.getProvider());

				if (customerCategory == null) {
					customerCategory = new CustomerCategory();
					customerCategory.setCode(StringUtils
							.normalizeHierarchyCode(postData
									.getCustomerCategoryCode()));
					customerCategory.setDescription(postData
							.getCustomerCategoryCode());
					customerCategoryService.create(customerCategory,
							currentUser, provider);
				}

				CustomFieldInstance cfInstancePaymentMethond = (CustomFieldInstance) providerService
						.getCustomFieldOrProperty(
								"api.default.customerAccount.paymentMethod",
								"1", provider, true, AccountLevelEnum.PROVIDER,
								currentUser);

				CustomFieldInstance cfInstanceCreditCategory = (CustomFieldInstance) providerService
						.getCustomFieldOrProperty(
								"api.default.customerAccount.creditCategory",
								"NEWCUSTOMER", provider, true,
								AccountLevelEnum.PROVIDER, currentUser);

				int caPaymentMethod = Integer.parseInt(cfInstancePaymentMethond
						.getValueAsString());
				String creditCategory = cfInstanceCreditCategory
						.getValueAsString();
				int baPaymentMethod = Integer.parseInt(cfInstancePaymentMethond
						.getValueAsString());

				Auditable auditable = new Auditable();
				auditable.setCreated(new Date());
				auditable.setCreator(currentUser);

				if (seller == null) {
					seller = new Seller();
					seller.setActive(true);
					seller.setCode(StringUtils.normalizeHierarchyCode(postData
							.getSellerCode()));
					seller.setAuditable(auditable);
					seller.setProvider(provider);
					seller.setTradingCountry(tradingCountry);
					seller.setTradingCurrency(tradingCurrency);

					sellerService.create(seller, currentUser, provider);
				}

				Address address = new Address();
				address.setAddress1(postData.getAddress1());
				address.setAddress2(postData.getAddress2());
				address.setZipCode(postData.getZipCode());
				address.setCity(postData.getCity());
				address.setCountry(postData.getCountryCode());

				ContactInformation contactInformation = new ContactInformation();
				contactInformation.setEmail(postData.getEmail());
				contactInformation.setPhone(postData.getPhoneNumber());

				Title title = null;
				if (!StringUtils.isBlank(postData.getTitleCode())) {
					title = titleService.findByCode(provider, StringUtils
							.normalizeHierarchyCode(postData.getTitleCode()));
				}

				String customerCode = CUSTOMER_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId());
				Customer customer = customerService.findByCode(customerCode,
						provider);
				if (customer != null) {
					throw new EntityAlreadyExistsException(Customer.class,
							customerCode);
				}

				customer = new Customer();
				customer.getName().setLastName(postData.getLastName());
				customer.getName().setFirstName(postData.getFirstName());
				customer.getName().setTitle(title);
				customer.setContactInformation(contactInformation);
				customer.setAddress(address);
				customer.setCode(CUSTOMER_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId()));
				customer.setCustomerBrand(customerBrand);
				customer.setCustomerCategory(customerCategory);
				customer.setSeller(seller);
				customerService.create(customer, currentUser, provider);

				CustomerAccount customerAccount = new CustomerAccount();
				customerAccount.setCustomer(customer);
				customerAccount.setAddress(address);
				customerAccount.setContactInformation(contactInformation);
				customerAccount.getName().setFirstName(postData.getFirstName());
				customerAccount.getName().setLastName(postData.getLastName());
				customerAccount.getName().setTitle(title);
				customerAccount.setCode(CUSTOMER_ACCOUNT_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId()));
				customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
				customerAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(caPaymentMethod));
				if (!StringUtils.isBlank(creditCategory)) {
					customerAccount.setCreditCategory(creditCategoryService
							.findByCode(creditCategory, provider));
				}
				customerAccount.setTradingCurrency(tradingCurrency);
				customerAccount.setTradingLanguage(tradingLanguage);
				customerAccount.setDateDunningLevel(new Date());
				customerAccountService.create(customerAccount, currentUser,
						provider);

				String billingCycleCode = StringUtils
						.normalizeHierarchyCode(postData.getBillingCycleCode());
				BillingCycle billingCycle = billingCycleService
						.findByBillingCycleCode(billingCycleCode, currentUser,
								provider);
				if (billingCycle == null) {
					throw new EntityDoesNotExistsException(BillingCycle.class,
							billingCycleCode);
				}

				BillingAccount billingAccount = new BillingAccount();
				billingAccount.setEmail(postData.getEmail());
				billingAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(postData.getPaymentMethod()));
				billingAccount.setCode(BILLING_ACCOUNT_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId()));
				billingAccount.setStatus(AccountStatusEnum.ACTIVE);
				billingAccount.setCustomerAccount(customerAccount);
				billingAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(baPaymentMethod));

				CustomFieldInstance cfInstance = (CustomFieldInstance) providerService
						.getCustomFieldOrProperty(
								"api.customerHeirarchy.billingAccount.electronicBilling",
								"true", customerAccount.getProvider(), true,
								AccountLevelEnum.PROVIDER, currentUser);

				billingAccount.setElectronicBilling(Boolean.valueOf(cfInstance
						.getValueAsString()));
				billingAccount.setTradingCountry(tradingCountry);
				billingAccount.setTradingLanguage(tradingLanguage);
				billingAccount.setBillingCycle(billingCycle);
				billingAccount.setProvider(provider);
				billingAccountService.createBillingAccount(billingAccount,
						currentUser, provider);

				String userAccountCode = USER_ACCOUNT_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId());
				UserAccount userAccount = new UserAccount();
				userAccount.setStatus(AccountStatusEnum.ACTIVE);
				userAccount.setBillingAccount(billingAccount);
				userAccount.setCode(userAccountCode);

				try {
					userAccountService.createUserAccount(billingAccount,
							userAccount, currentUser);
				} catch (AccountAlreadyExistsException e) {
					throw new EntityAlreadyExistsException(UserAccount.class,
							userAccountCode);
				}
			} else {
				if (StringUtils.isBlank(postData.getCustomerId())) {
					missingParameters.add("customerId");
				}
				if (StringUtils.isBlank(postData.getCustomerBrandCode())) {
					missingParameters.add("customerBrandCode");
				}
				if (StringUtils.isBlank(postData.getCustomerCategoryCode())) {
					missingParameters.add("customerCategoryCode");
				}
				if (StringUtils.isBlank(postData.getSellerCode())) {
					missingParameters.add("sellerCode");
				}
				if (StringUtils.isBlank(postData.getCurrencyCode())) {
					missingParameters.add("currencyCode");
				}
				if (StringUtils.isBlank(postData.getCountryCode())) {
					missingParameters.add("countryCode");
				}
				if (StringUtils.isBlank(postData.getLastName())) {
					missingParameters.add("lastName");
				}
				if (StringUtils.isBlank(postData.getBillingCycleCode())) {
					missingParameters.add("billingCycleCode");
				}
				if (StringUtils.isBlank(postData.getLanguageCode())) {
					missingParameters.add("languageCode");
				}
				if (StringUtils.isBlank(postData.getEmail())) {
					missingParameters.add("email");
				}

				throw new MissingParameterException(
						getMissingParametersExceptionMessage());
			}
		}
	}

	public void update(AccountHierarchyDto postData, User currentUser)
			throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		Customer customer = customerService.findByCode(
				postData.getCustomerId(), provider);

		if (customer == null) {
			throw new EntityDoesNotExistsException(Customer.class,
					postData.getCustomerId());
		}

		if (!StringUtils.isBlank(postData.getCustomerId())
				&& !StringUtils.isBlank(postData.getCustomerBrandCode())
				&& !StringUtils.isBlank(postData.getCustomerCategoryCode())
				&& !StringUtils.isBlank(postData.getSellerCode())
				&& !StringUtils.isBlank(postData.getCurrencyCode())
				&& !StringUtils.isBlank(postData.getBillingCycleCode())
				&& !StringUtils.isBlank(postData.getCountryCode())
				&& !StringUtils.isBlank(postData.getLastName())
				&& !StringUtils.isBlank(postData.getLanguageCode())
				&& !StringUtils.isBlank(postData.getEmail())) {

			Seller seller = sellerService.findByCode(postData.getSellerCode(),
					provider);

			Auditable auditableTrading = new Auditable();
			auditableTrading.setCreated(new Date());
			auditableTrading.setCreator(currentUser);

			Country country = countryService.findByCode(postData
					.getCountryCode());

			if (country == null) {
				throw new EntityDoesNotExistsException(Country.class,
						postData.getCountryCode());
			}

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(postData.getCountryCode(),
							provider);

			if (tradingCountry == null) {
				tradingCountry = new TradingCountry();
				tradingCountry.setAuditable(auditableTrading);
			}

			tradingCountry.setCountry(country);
			tradingCountry.setProvider(provider);
			tradingCountry.setActive(true);
			tradingCountry.setPrDescription(country.getDescriptionEn());

			if (tradingCountry.isTransient()) {
				tradingCountryService.create(tradingCountry, currentUser,
						provider);
			} else {
				tradingCountryService.update(tradingCountry, currentUser);
			}

			Currency currency = currencyService.findByCode(postData
					.getCurrencyCode());

			if (currency == null) {
				throw new EntityDoesNotExistsException(Currency.class,
						postData.getCurrencyCode());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(postData.getCurrencyCode(),
							provider);

			if (tradingCurrency == null) {
				// create tradingCountry
				tradingCurrency = new TradingCurrency();
				tradingCurrency.setAuditable(auditableTrading);
			}

			tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
			tradingCurrency.setCurrency(currency);
			tradingCurrency.setProvider(provider);
			tradingCurrency.setActive(true);
			tradingCurrency.setPrDescription(currency.getDescriptionEn());

			if (tradingCurrency.isTransient()) {
				tradingCurrencyService.create(tradingCurrency, currentUser,
						provider);
			} else {
				tradingCurrencyService.update(tradingCurrency, currentUser);
			}

			Language language = languageService.findByCode(postData
					.getLanguageCode());

			if (language == null) {
				throw new EntityDoesNotExistsException(Language.class,
						postData.getLanguageCode());
			}

			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(postData.getLanguageCode(),
							provider);

			if (tradingLanguage == null) {
				tradingLanguage = new TradingLanguage();
				tradingLanguage.setAuditable(auditableTrading);
			}

			tradingLanguage.setLanguageCode(postData.getLanguageCode());
			tradingLanguage.setLanguage(language);
			tradingLanguage.setProvider(provider);
			tradingLanguage.setActive(true);
			tradingLanguage.setPrDescription(language.getDescriptionEn());

			if (tradingLanguage.isTransient()) {
				tradingLanguageService.create(tradingLanguage, currentUser,
						provider);
			} else {
				tradingLanguageService.update(tradingLanguage, currentUser);
			}

			CustomerBrand customerBrand = customerBrandService.findByCode(
					postData.getCustomerBrandCode(), currentUser.getProvider());

			CustomerCategory customerCategory = customerCategoryService
					.findByCode(postData.getCustomerCategoryCode(),
							currentUser.getProvider());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
			}

			customerBrand.setCode(StringUtils.normalizeHierarchyCode(postData
					.getCustomerBrandCode()));
			customerBrand.setDescription(postData.getCustomerBrandCode());

			if (customerBrand.isTransient()) {
				customerBrandService.create(customerBrand, currentUser,
						provider);
			} else {
				customerBrandService.update(customerBrand, currentUser);
			}

			if (customerCategory == null) {
				customerCategory = new CustomerCategory();
			}

			customerCategory
					.setCode(StringUtils.normalizeHierarchyCode(postData
							.getCustomerCategoryCode()));
			customerCategory.setDescription(postData.getCustomerCategoryCode());

			if (customerCategory.isTransient()) {
				customerCategoryService.create(customerCategory, currentUser,
						provider);
			} else {
				customerCategoryService.update(customerCategory, currentUser);
			}

			CustomFieldInstance cfInstancePaymentMethond = (CustomFieldInstance) providerService
					.getCustomFieldOrProperty(
							"api.default.customerAccount.paymentMethod", "1",
							provider, true, AccountLevelEnum.PROVIDER,
							currentUser);

			CustomFieldInstance cfInstanceCreditCategory = (CustomFieldInstance) providerService
					.getCustomFieldOrProperty(
							"api.default.customerAccount.creditCategory",
							"NEWCUSTOMER", provider, true,
							AccountLevelEnum.PROVIDER, currentUser);

			int caPaymentMethod = Integer.parseInt(cfInstancePaymentMethond
					.getValueAsString());
			String creditCategory = cfInstanceCreditCategory.getValueAsString();

			int baPaymentMethod = Integer.parseInt(cfInstancePaymentMethond
					.getValueAsString());

			Auditable auditable = new Auditable();
			auditable.setCreated(new Date());
			auditable.setCreator(currentUser);

			if (seller == null) {
				seller = new Seller();
				seller.setAuditable(auditable);
				seller.setActive(true);
				seller.setCode(postData.getSellerCode());
				seller.setProvider(provider);
				seller.setTradingCountry(tradingCountry);
				seller.setTradingCurrency(tradingCurrency);
				sellerService.create(seller, currentUser, provider);
			}

			Address address = new Address();
			address.setAddress1(postData.getAddress1());
			address.setAddress2(postData.getAddress2());
			address.setZipCode(postData.getZipCode());
			address.setCity(postData.getCity());
			address.setCountry(postData.getCountryCode());

			ContactInformation contactInformation = new ContactInformation();
			contactInformation.setEmail(postData.getEmail());
			contactInformation.setPhone(postData.getPhoneNumber());

			Title title = null;
			if (!StringUtils.isBlank(postData.getTitleCode())) {
				title = titleService.findByCode(provider, StringUtils
						.normalizeHierarchyCode(postData.getTitleCode()));
			}

			customer.getName().setLastName(postData.getLastName());
			customer.getName().setFirstName(postData.getFirstName());
			customer.getName().setTitle(title);
			customer.setAddress(address);
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);
			customer.setContactInformation(contactInformation);
			customer.setSeller(seller);

			customerService.update(customer, currentUser);

			CustomerAccount customerAccount = customerAccountService
					.findByCode(
							CUSTOMER_ACCOUNT_PREFIX + postData.getCustomerId(),
							provider);
			if (customerAccount == null) {
				customerAccount = new CustomerAccount();
				customerAccount.setCode(CUSTOMER_ACCOUNT_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId()));
			}
			customerAccount.setCustomer(customer);

			customerAccount.setAddress(address);
			customerAccount.setContactInformation(contactInformation);

			customerAccount.getName().setFirstName(postData.getFirstName());
			customerAccount.getName().setLastName(postData.getLastName());
			customerAccount.getName().setTitle(title);
			customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
			customerAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(caPaymentMethod));
			if (!StringUtils.isBlank(creditCategory)) {
				customerAccount.setCreditCategory(creditCategoryService
						.findByCode(creditCategory, provider));
			}
			customerAccount.setTradingCurrency(tradingCurrency);
			customerAccount.setTradingLanguage(tradingLanguage);

			if (customerAccount.isTransient()) {
				customerAccountService.create(customerAccount, currentUser,
						provider);
			} else {
				customerAccountService.update(customerAccount, currentUser);
			}

			String billingCycleCode = StringUtils
					.normalizeHierarchyCode(postData.getBillingCycleCode());
			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(billingCycleCode, currentUser,
							provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class,
						billingCycleCode);
			}

			BillingAccount billingAccount = billingAccountService
					.findByCode(
							BILLING_ACCOUNT_PREFIX + postData.getCustomerId(),
							provider);

			if (billingAccount == null) {
				billingAccount = new BillingAccount();
				billingAccount.setCode(BILLING_ACCOUNT_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId()));
			}

			billingAccount.setEmail(postData.getEmail());
			billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(postData
					.getPaymentMethod()));
			billingAccount.setStatus(AccountStatusEnum.ACTIVE);
			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(baPaymentMethod));

			CustomFieldInstance cfInstance = (CustomFieldInstance) providerService
					.getCustomFieldOrProperty(
							"api.customerHeirarchy.billingAccount.electronicBilling",
							"true", customerAccount.getProvider(), true,
							AccountLevelEnum.PROVIDER, currentUser);
			billingAccount.setElectronicBilling(Boolean.valueOf(cfInstance
					.getValueAsString()));
			billingAccount.setTradingCountry(tradingCountry);
			billingAccount.setTradingLanguage(tradingLanguage);
			billingAccount.setBillingCycle(billingCycle);

			if (billingAccount.isTransient()) {
				billingAccountService.createBillingAccount(billingAccount,
						currentUser, provider);
			} else {
				billingAccountService.update(billingAccount, currentUser);
			}

			UserAccount userAccount = userAccountService.findByCode(
					USER_ACCOUNT_PREFIX + postData.getCustomerId(), provider);
			if (userAccount == null) {
				userAccount = new UserAccount();
			}

			userAccount.setStatus(AccountStatusEnum.ACTIVE);
			userAccount.setBillingAccount(billingAccount);

			if (userAccount.isTransient()) {
				String userAccountCode = USER_ACCOUNT_PREFIX
						+ StringUtils.normalizeHierarchyCode(postData
								.getCustomerId());
				try {
					userAccount.setCode(userAccountCode);
					userAccountService.createUserAccount(billingAccount,
							userAccount, currentUser);
				} catch (AccountAlreadyExistsException e) {
					throw new EntityAlreadyExistsException(UserAccount.class,
							userAccountCode);
				}
			} else {
				userAccountService.update(userAccount, currentUser);
			}
		} else {
			if (StringUtils.isBlank(postData.getCustomerId())) {
				missingParameters.add("customerId");
			}
			if (StringUtils.isBlank(postData.getCustomerBrandCode())) {
				missingParameters.add("customerBrandCode");
			}
			if (StringUtils.isBlank(postData.getCustomerCategoryCode())) {
				missingParameters.add("customerCategoryCode");
			}
			if (StringUtils.isBlank(postData.getSellerCode())) {
				missingParameters.add("sellerCode");
			}
			if (StringUtils.isBlank(postData.getCurrencyCode())) {
				missingParameters.add("currencyCode");
			}
			if (StringUtils.isBlank(postData.getCountryCode())) {
				missingParameters.add("countryCode");
			}
			if (StringUtils.isBlank(postData.getLastName())) {
				missingParameters.add("lastName");
			}
			if (StringUtils.isBlank(postData.getBillingCycleCode())) {
				missingParameters.add("billingCycleCode");
			}
			if (StringUtils.isBlank(postData.getLanguageCode())) {
				missingParameters.add("languageCode");
			}
			if (StringUtils.isBlank(postData.getEmail())) {
				missingParameters.add("email");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public CustomersDto find(AccountHierarchyDto postData, User currentUser)
			throws MeveoApiException {
		CustomersDto result = new CustomersDto();

		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(
				postData.getIndex(), postData.getLimit(), null, null,
				postData.getSortField(), null);
		QueryBuilder qb = new QueryBuilder(Customer.class, "c", null,
				currentUser.getProvider());

		if (!StringUtils.isBlank(postData.getCustomerId())) {
			qb.addCriterion("c.code", "=", postData.getCustomerId(), true);
		}
		if (!StringUtils.isBlank(postData.getSellerCode())) {
			Seller seller = sellerService.findByCode(postData.getSellerCode(),
					currentUser.getProvider());
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class,
						postData.getSellerCode());
			}
			qb.addCriterionEntity("c.seller", seller);
		}
		if (!StringUtils.isBlank(postData.getCustomerBrandCode())) {
			CustomerBrand customerBrand = customerBrandService.findByCode(
					postData.getCustomerBrandCode(), currentUser.getProvider());
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class,
						postData.getCustomerBrandCode());
			}
			qb.addCriterionEntity("c.customerBrand", customerBrand);
		}
		if (!StringUtils.isBlank(postData.getCustomerCategoryCode())) {
			CustomerCategory customerCategory = customerCategoryService
					.findByCode(postData.getCustomerCategoryCode(),
							currentUser.getProvider());
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class,
						postData.getCustomerCategoryCode());
			}
			qb.addCriterionEntity("c.customerCategory", customerCategory);
		}
		if (!StringUtils.isBlank(postData.getCountryCode())) {
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(postData.getCountryCode(),
							currentUser.getProvider());
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class,
						postData.getCountryCode());
			}
			qb.addCriterion("c.address.country", "=",
					postData.getCountryCode(), true);
		}
		if (!StringUtils.isBlank(postData.getFirstName())) {
			qb.addCriterion("c.name.firstName", "=", postData.getFirstName(),
					true);
		}
		if (!StringUtils.isBlank(postData.getLastName())) {
			qb.addCriterion("c.name.lastName", "=", postData.getLastName(),
					true);
		}
		if (!StringUtils.isBlank(postData.getAddress1())) {
			qb.addCriterion("c.address.address1", "=", postData.getAddress1(),
					true);
		}
		if (!StringUtils.isBlank(postData.getAddress2())) {
			qb.addCriterion("c.address.address2", "=", postData.getAddress2(),
					true);
		}
		if (!StringUtils.isBlank(postData.getCity())) {
			qb.addCriterion("c.address.city", "=", postData.getCity(), true);
		}
		if (!StringUtils.isBlank(postData.getZipCode())) {
			qb.addCriterion("c.address.zipCode", "=", postData.getZipCode(),
					true);
		}

		// custom fields
		if (postData.getCustomFields() != null) {
			for (CustomFieldDto cfDto : postData.getCustomFields()
					.getCustomField()) {
				qb.addCriterion("KEY(c.customFields)", "=", cfDto.getCode(),
						true);
				// problem with multiple result
				// List<CustomFieldInstance> cfis =
				// customFieldInstanceService.findByCodeAndAccountAndValue(
				// cfDto.getCode(), Customer.ACCOUNT_TYPE,
				// cfDto.getStringValue(), cfDto.getDateValue(),
				// cfDto.getLongValue(), cfDto.getDoubleValue(),
				// currentUser.getProvider());
				// if (cfis != null) {
				// for (CustomFieldInstance cfi : cfis) {
				// qb.addCriterion("VALUE(c.customFields)", "=", cfi, true);
				// }
				// }
			}
		}

		qb.addPaginationConfiguration(paginationConfiguration);
		List<Customer> customers = qb.getQuery(
				customerService.getEntityManager()).getResultList();

		if (customers != null) {
			for (Customer cust : customers) {
				if (postData.getCustomFields() == null
						|| postData.getCustomFields().getCustomField() == null) {
					result.getCustomer().add(new CustomerDto(cust));
				} else {
					for (CustomFieldDto cfDto : postData.getCustomFields()
							.getCustomField()) {

						if (!cfDto.isEmpty()) {
							if (cfDto.getValueConverted().equals(
									cust.getCFValue(cfDto.getCode()))) {
								result.getCustomer().add(new CustomerDto(cust));
							}
						}
					}
				}
			}
		}

		return result;
	}

	public void customerHierarchyUpdate(CustomerHierarchyDto postData,
			User currentUser) throws MeveoApiException {
		if (postData.getSellers() != null
				&& postData.getSellers().getSeller().size() > 0) {
			for (SellerDto sellerDto : postData.getSellers().getSeller()) {
				if (!StringUtils.isBlank(sellerDto.getCode())) {
					Provider provider = currentUser.getProvider();

					Seller seller = sellerService.findByCode(
							sellerDto.getCode(), provider);
					if (seller == null) {
						seller = new Seller();
						seller.setCode(sellerDto.getCode());

						seller.setDescription(sellerDto.getDescription());
						seller.setInvoicePrefix(sellerDto.getInvoicePrefix());

						seller.setProvider(provider);
					} else {
						if (!StringUtils.isBlank(sellerDto.getDescription())) {
							seller.setDescription(sellerDto.getDescription());
						}
						if (!StringUtils.isBlank(sellerDto.getInvoicePrefix())) {
							seller.setInvoicePrefix(sellerDto
									.getInvoicePrefix());
						}
					}

					if (!StringUtils.isBlank(sellerDto.getCurrencyCode())) {
						TradingCurrency tradingCurrency = tradingCurrencyService
								.findByTradingCurrencyCode(
										sellerDto.getCurrencyCode(), provider);
						if (tradingCurrency != null) {
							seller.setTradingCurrency(tradingCurrency);
						}
					}

					if (!StringUtils.isBlank(sellerDto.getCountryCode())) {
						TradingCountry tradingCountry = tradingCountryService
								.findByTradingCountryCode(
										sellerDto.getCountryCode(), provider);
						if (tradingCountry != null) {
							seller.setTradingCountry(tradingCountry);
						}
					}

					if (!StringUtils.isBlank(sellerDto.getLanguageCode())) {
						TradingLanguage tradingLanguage = tradingLanguageService
								.findByTradingLanguageCode(
										sellerDto.getLanguageCode(), provider);
						if (tradingLanguage != null) {
							seller.setTradingLanguage(tradingLanguage);
						}
					}

					if (seller.isTransient()) {
						sellerService.create(seller, currentUser, provider);
					} else {
						sellerService.update(seller, currentUser);
					}

					// customers
					if (sellerDto.getCustomers() != null) {
						for (CustomerDto customerDto : sellerDto.getCustomers()
								.getCustomer()) {
							if (StringUtils.isBlank(customerDto.getCode())) {
								log.warn("code is null={}", customerDto);
								continue;
							}

							Customer customer = customerService.findByCode(
									customerDto.getCode(), provider);
							if (customer == null) {
								if (StringUtils.isBlank(customerDto
										.getDescription())) {
									missingParameters
											.add("customer.description");
									throw new MissingParameterException(
											getMissingParametersExceptionMessage());
								}

								customer = new Customer();
								customer.setCode(customerDto.getCode());

								if (!StringUtils.isBlank(customerDto
										.getCustomerBrand())) {
									CustomerBrand customerBrand = customerBrandService
											.findByCode(customerDto
													.getCustomerBrand(),
													provider);
									if (customerBrand != null) {
										customer.setCustomerBrand(customerBrand);
									}
								} else {
									missingParameters
											.add("customer.customerBrand");
									throw new MissingParameterException(
											getMissingParametersExceptionMessage());
								}

								if (!StringUtils.isBlank(customerDto
										.getCustomerCategory())) {
									CustomerCategory customerCategory = customerCategoryService
											.findByCode(customerDto
													.getCustomerCategory(),
													provider);
									if (customerCategory != null) {
										customer.setCustomerCategory(customerCategory);
									}
								} else {
									missingParameters
											.add("customer.customerCategory");
									throw new MissingParameterException(
											getMissingParametersExceptionMessage());
								}

								customer.setMandateDate(customerDto
										.getMandateDate());
								customer.setMandateIdentification(customerDto
										.getMandateIdentification());

								customer.setProvider(provider);
							} else {
								if (!StringUtils.isBlank(customerDto
										.getCustomerBrand())) {
									CustomerBrand customerBrand = customerBrandService
											.findByCode(customerDto
													.getCustomerBrand(),
													provider);
									if (customerBrand != null) {
										customer.setCustomerBrand(customerBrand);
									}
								}

								if (!StringUtils.isBlank(customerDto
										.getCustomerCategory())) {
									CustomerCategory customerCategory = customerCategoryService
											.findByCode(customerDto
													.getCustomerCategory(),
													provider);
									if (customerCategory != null) {
										customer.setCustomerCategory(customerCategory);
									}
								}

								if (!StringUtils.isBlank(customerDto
										.getMandateDate())) {
									customer.setMandateDate(customerDto
											.getMandateDate());
								}
								if (!StringUtils.isBlank(customerDto
										.getMandateIdentification())) {
									customer.setMandateIdentification(customerDto
											.getMandateIdentification());
								}
							}

							customer.setSeller(seller);

							if (customerDto.getContactInformation() != null) {
								if (!StringUtils.isBlank(customerDto
										.getContactInformation().getEmail())) {
									customer.getContactInformation().setEmail(
											customerDto.getContactInformation()
													.getEmail());
								}
								if (!StringUtils.isBlank(customerDto
										.getContactInformation().getPhone())) {
									customer.getContactInformation().setPhone(
											customerDto.getContactInformation()
													.getPhone());
								}
								if (!StringUtils.isBlank(customerDto
										.getContactInformation().getMobile())) {
									customer.getContactInformation().setMobile(
											customerDto.getContactInformation()
													.getMobile());
								}
								if (!StringUtils.isBlank(customerDto
										.getContactInformation().getFax())) {
									customer.getContactInformation().setFax(
											customerDto.getContactInformation()
													.getFax());
								}
							}

							populateNameAddressAndCustomFields(customer,
									customerDto, AccountLevelEnum.CUST,
									currentUser);

							if (customer.isTransient()) {
								customerService.create(customer, currentUser,
										provider);
							} else {
								customerService.update(customer, currentUser);
							}

							// customerAccounts
							if (customerDto.getCustomerAccounts() != null) {
								for (CustomerAccountDto customerAccountDto : customerDto
										.getCustomerAccounts()
										.getCustomerAccount()) {
									if (StringUtils.isBlank(customerAccountDto
											.getCode())) {
										log.warn("code is null={}",
												customerAccountDto);
										continue;
									}

									CustomerAccount customerAccount = customerAccountService
											.findByCode(customerAccountDto
													.getCode(), provider);
									if (customerAccount == null) {
										if (StringUtils
												.isBlank(customerAccountDto
														.getDescription())) {
											missingParameters
													.add("customerAccountDto.description");
											throw new MissingParameterException(
													getMissingParametersExceptionMessage());
										}

										customerAccount = new CustomerAccount();
										customerAccount
												.setStatus(CustomerAccountStatusEnum.ACTIVE);
										customerAccount
												.setCode(customerAccountDto
														.getCode());

										if (!StringUtils
												.isBlank(customerAccountDto
														.getCurrency())) {
											TradingCurrency tradingCurrency = tradingCurrencyService
													.findByTradingCurrencyCode(
															customerAccountDto
																	.getCurrency(),
															provider);
											if (tradingCurrency == null) {
												throw new EntityDoesNotExistsException(
														TradingCurrency.class,
														customerAccountDto
																.getCurrency());
											}

											customerAccount
													.setTradingCurrency(tradingCurrency);
										} else {
											missingParameters
													.add("customerAccount.currency");
											throw new MissingParameterException(
													getMissingParametersExceptionMessage());
										}

										if (!StringUtils
												.isBlank(customerAccountDto
														.getLanguage())) {
											TradingLanguage tradingLanguage = tradingLanguageService
													.findByTradingLanguageCode(
															customerAccountDto
																	.getLanguage(),
															provider);
											if (tradingLanguage == null) {
												throw new EntityDoesNotExistsException(
														TradingLanguage.class,
														customerAccountDto
																.getLanguage());
											}

											customerAccount
													.setTradingLanguage(tradingLanguage);
										} else {
											missingParameters
													.add("customerAccount.language");
											throw new MissingParameterException(
													getMissingParametersExceptionMessage());
										}

										customerAccount
												.setDateStatus(customerAccountDto
														.getDateStatus());
										customerAccount
												.setDateDunningLevel(customerAccount
														.getDateDunningLevel());

										customerAccount
												.setMandateDate(customerAccountDto
														.getMandateDate());
										customerAccount
												.setMandateIdentification(customerAccountDto
														.getMandateIdentification());

										customerAccount.setProvider(provider);
									} else {
										if (!StringUtils
												.isBlank(customerAccountDto
														.getStatus())) {
											try {
												CustomerAccountStatusEnum customerAccountStatusEnum = CustomerAccountStatusEnum
														.valueOf(customerAccountDto
																.getStatus());
												if (customerAccountStatusEnum == CustomerAccountStatusEnum.CLOSE) {
													try {
														customerAccountService
																.closeCustomerAccount(
																		customerAccount,
																		currentUser);
													} catch (Exception e) {
														throw new MeveoApiException(
																"Failed closing customerAccount with code="
																		+ customerAccountDto
																				.getCode()
																		+ ". "
																		+ e.getMessage());
													}
												}
											} catch (IllegalStateException e) {
												log.warn(
														"error generated while getting customer account status ",
														e);
											}
										} else {
											if (!StringUtils
													.isBlank(customerAccountDto
															.getCurrency())) {
												TradingCurrency tradingCurrency = tradingCurrencyService
														.findByTradingCurrencyCode(
																customerAccountDto
																		.getCurrency(),
																provider);
												if (tradingCurrency == null) {
													throw new EntityDoesNotExistsException(
															TradingCurrency.class,
															customerAccountDto
																	.getCurrency());
												}

												customerAccount
														.setTradingCurrency(tradingCurrency);
											}

											if (!StringUtils
													.isBlank(customerAccountDto
															.getLanguage())) {
												TradingLanguage tradingLanguage = tradingLanguageService
														.findByTradingLanguageCode(
																customerAccountDto
																		.getLanguage(),
																provider);
												if (tradingLanguage == null) {
													throw new EntityDoesNotExistsException(
															TradingLanguage.class,
															customerAccountDto
																	.getLanguage());
												}

												customerAccount
														.setTradingLanguage(tradingLanguage);
											}

											if (!StringUtils
													.isBlank(customerAccountDto
															.getDateStatus())) {
												customerAccount
														.setDateStatus(customerAccountDto
																.getDateStatus());
											}
											if (!StringUtils
													.isBlank(customerAccountDto
															.getDateDunningLevel())) {
												customerAccount
														.setDateDunningLevel(customerAccount
																.getDateDunningLevel());
											}
											if (!StringUtils
													.isBlank(customerAccountDto
															.getMandateDate())) {
												customerAccount
														.setMandateDate(customerAccountDto
																.getMandateDate());
											}
											if (!StringUtils
													.isBlank(customerAccountDto
															.getMandateIdentification())) {
												customerAccount
														.setMandateIdentification(customerAccountDto
																.getMandateIdentification());
											}
										}
									}

									customerAccount.setCustomer(customer);

									if (!StringUtils.isBlank(customerAccountDto
											.getStatus())) {
										try {
											customerAccount
													.setStatus(CustomerAccountStatusEnum
															.valueOf(customerAccountDto
																	.getStatus()));
										} catch (IllegalArgumentException
												| NullPointerException e) {
											log.warn(
													"error while setting customer account status",
													e);
										}
									}
									if (!StringUtils.isBlank(customerAccountDto
											.getPaymentMethod())) {
										try {
											customerAccount
													.setPaymentMethod(PaymentMethodEnum
															.valueOf(customerAccountDto
																	.getPaymentMethod()));
										} catch (IllegalArgumentException
												| NullPointerException e) {
											log.warn(
													"error while setting customerAccount.paymentMethod",
													e);
										}
									}
									if (!StringUtils.isBlank(customerAccountDto
											.getCreditCategory())) {
										customerAccount
												.setCreditCategory(creditCategoryService.findByCode(
														customerAccountDto
																.getCreditCategory(),
														provider));
									}
									if (!StringUtils.isBlank(customerAccountDto
											.getDunningLevel())) {
										try {
											customerAccount
													.setDunningLevel(DunningLevelEnum
															.valueOf(customerAccountDto
																	.getDunningLevel()));
										} catch (IllegalArgumentException
												| NullPointerException e) {
											log.warn(
													"error while setting customerAccount.dunningLevel ",
													e);
										}
									}

									if (customerAccountDto
											.getContactInformation() != null) {
										if (!StringUtils
												.isBlank(customerAccountDto
														.getContactInformation()
														.getEmail())) {
											customerAccount
													.getContactInformation()
													.setEmail(
															customerAccountDto
																	.getContactInformation()
																	.getEmail());
										}
										if (!StringUtils
												.isBlank(customerAccountDto
														.getContactInformation()
														.getPhone())) {
											customerAccount
													.getContactInformation()
													.setPhone(
															customerAccountDto
																	.getContactInformation()
																	.getPhone());
										}
										if (!StringUtils
												.isBlank(customerAccountDto
														.getContactInformation()
														.getMobile())) {
											customerAccount
													.getContactInformation()
													.setMobile(
															customerAccountDto
																	.getContactInformation()
																	.getMobile());
										}
										if (!StringUtils
												.isBlank(customerAccountDto
														.getContactInformation()
														.getFax())) {
											customerAccount
													.getContactInformation()
													.setFax(customerAccountDto
															.getContactInformation()
															.getFax());
										}
									}

									populateNameAddressAndCustomFields(
											customerAccount,
											customerAccountDto,
											AccountLevelEnum.CA, currentUser);

									if (customerAccount.isTransient()) {
										customerAccountService.create(
												customerAccount, currentUser,
												provider);
									} else {
										customerAccountService.update(
												customerAccount, currentUser);
									}

									// billing accounts
									if (customerAccountDto.getBillingAccounts() != null) {
										for (BillingAccountDto billingAccountDto : customerAccountDto
												.getBillingAccounts()
												.getBillingAccount()) {
											if (StringUtils
													.isBlank(billingAccountDto
															.getCode())) {
												log.warn("code is null={}",
														billingAccountDto);
												continue;
											}

											BillingAccount billingAccount = billingAccountService
													.findByCode(
															billingAccountDto
																	.getCode(),
															provider);
											if (billingAccount == null) {
												if (StringUtils
														.isBlank(billingAccountDto
																.getDescription())) {
													missingParameters
															.add("billingAccountDto.description");
													throw new MissingParameterException(
															getMissingParametersExceptionMessage());
												}

												billingAccount = new BillingAccount();
												billingAccount
														.setStatus(AccountStatusEnum.ACTIVE);
												billingAccount
														.setCode(billingAccountDto
																.getCode());

												if (!StringUtils
														.isBlank(billingAccountDto
																.getBillingCycle())) {
													BillingCycle billingCycle = billingCycleService
															.findByBillingCycleCode(
																	billingAccountDto
																			.getBillingCycle(),
																	provider);
													if (billingCycle != null) {
														billingAccount
																.setBillingCycle(billingCycle);
													}
												} else {
													missingParameters
															.add("billingAccount.billingCycle");
													throw new MissingParameterException(
															getMissingParametersExceptionMessage());
												}

												if (!StringUtils
														.isBlank(billingAccountDto
																.getCountry())) {
													TradingCountry tradingCountry = tradingCountryService
															.findByTradingCountryCode(
																	billingAccountDto
																			.getCountry(),
																	provider);
													if (tradingCountry != null) {
														billingAccount
																.setTradingCountry(tradingCountry);
													}
												} else {
													missingParameters
															.add("billingAccount.country");
													throw new MissingParameterException(
															getMissingParametersExceptionMessage());
												}

												if (!StringUtils
														.isBlank(billingAccountDto
																.getLanguage())) {
													TradingLanguage tradingLanguage = tradingLanguageService
															.findByTradingLanguageCode(
																	billingAccountDto
																			.getLanguage(),
																	provider);
													if (tradingLanguage != null) {
														billingAccount
																.setTradingLanguage(tradingLanguage);
													}
												} else {
													missingParameters
															.add("billingAccount.language");
													throw new MissingParameterException(
															getMissingParametersExceptionMessage());
												}

												billingAccount
														.setProvider(provider);
											} else {
												if (billingAccountDto
														.getTerminationDate() != null) {
													if (StringUtils
															.isBlank(billingAccountDto
																	.getTerminationReason())) {
														missingParameters
																.add("billingAccount.terminationReason");
														throw new MissingParameterException(
																getMissingParametersExceptionMessage());
													}

													SubscriptionTerminationReason terminationReason = terminationReasonService
															.findByCode(
																	billingAccountDto
																			.getTerminationReason(),
																	provider);
													if (terminationReason == null) {
														throw new EntityDoesNotExistsException(
																SubscriptionTerminationReason.class,
																billingAccountDto
																		.getTerminationReason());
													}

													try {
														billingAccountService
																.billingAccountTermination(
																		billingAccount,
																		billingAccountDto
																				.getTerminationDate(),
																		terminationReason,
																		currentUser);
														continue;
													} catch (BusinessException e) {
														throw new MeveoApiException(
																"Failed terminating billingAccount. "
																		+ e.getMessage());
													}
												} else {
													if (!StringUtils
															.isBlank(billingAccountDto
																	.getBillingCycle())) {
														BillingCycle billingCycle = billingCycleService
																.findByBillingCycleCode(
																		billingAccountDto
																				.getBillingCycle(),
																		provider);
														if (billingCycle != null) {
															billingAccount
																	.setBillingCycle(billingCycle);
														}
													}

													if (!StringUtils
															.isBlank(billingAccountDto
																	.getCountry())) {
														TradingCountry tradingCountry = tradingCountryService
																.findByTradingCountryCode(
																		billingAccountDto
																				.getCountry(),
																		provider);
														if (tradingCountry != null) {
															billingAccount
																	.setTradingCountry(tradingCountry);
														}
													}

													if (!StringUtils
															.isBlank(billingAccountDto
																	.getLanguage())) {
														TradingLanguage tradingLanguage = tradingLanguageService
																.findByTradingLanguageCode(
																		billingAccountDto
																				.getLanguage(),
																		provider);
														if (tradingLanguage != null) {
															billingAccount
																	.setTradingLanguage(tradingLanguage);
														}
													}
												}
											}

											billingAccount
													.setCustomerAccount(customerAccount);

											if (!StringUtils
													.isBlank(billingAccountDto
															.getPaymentMethod())) {
												try {
													billingAccount
															.setPaymentMethod(PaymentMethodEnum
																	.valueOf(billingAccountDto
																			.getPaymentMethod()));
												} catch (
														IllegalArgumentException
														| NullPointerException e) {
													log.warn(
															"error while setting billingAccount.paymentMethod",
															e);
												}
											}
											if (!StringUtils
													.isBlank(billingAccountDto
															.getPaymentTerms())) {
												try {
													billingAccount
															.setPaymentTerm(PaymentTermEnum
																	.valueOf(billingAccountDto
																			.getPaymentTerms()));
												} catch (
														IllegalArgumentException
														| NullPointerException e) {
													log.warn(
															"error while setting billingAccount.paymentTerms ",
															e);
												}
											}

											if (!StringUtils
													.isBlank(billingAccountDto
															.getNextInvoiceDate())) {
												billingAccount
														.setNextInvoiceDate(billingAccountDto
																.getNextInvoiceDate());
											}
											if (!StringUtils
													.isBlank(billingAccountDto
															.getSubscriptionDate())) {
												billingAccount
														.setSubscriptionDate(billingAccountDto
																.getSubscriptionDate());
											}
											if (!StringUtils
													.isBlank(billingAccountDto
															.getTerminationDate())) {
												billingAccount
														.setTerminationDate(billingAccount
																.getTerminationDate());
											}
											if (!StringUtils
													.isBlank(billingAccountDto
															.getElectronicBilling())) {
												billingAccount
														.setElectronicBilling(billingAccountDto
																.getElectronicBilling());
											}
											if (!StringUtils
													.isBlank(billingAccountDto
															.getEmail())) {
												billingAccount
														.setEmail(billingAccountDto
																.getEmail());
											}

											populateNameAddressAndCustomFields(
													billingAccount,
													billingAccountDto,
													AccountLevelEnum.BA,
													currentUser);

											if (billingAccount.isTransient()) {
												billingAccountService.create(
														billingAccount,
														currentUser, provider);
											} else {
												billingAccountService.update(
														billingAccount,
														currentUser);
											}

											// user accounts
											if (billingAccountDto
													.getUserAccounts() != null) {
												for (UserAccountDto userAccountDto : billingAccountDto
														.getUserAccounts()
														.getUserAccount()) {
													if (StringUtils
															.isBlank(userAccountDto
																	.getCode())) {
														log.warn(
																"code is null={}",
																userAccountDto);
														continue;
													}

													UserAccount userAccount = userAccountService
															.findByCode(
																	userAccountDto
																			.getCode(),
																	provider);
													if (userAccount == null) {
														if (StringUtils
																.isBlank(userAccountDto
																		.getDescription())) {
															missingParameters
																	.add("userAccountDto.description");
															throw new MissingParameterException(
																	getMissingParametersExceptionMessage());
														}

														userAccount = new UserAccount();
														userAccount
																.setStatus(AccountStatusEnum.ACTIVE);
														userAccount
																.setCode(userAccountDto
																		.getCode());
														userAccount
																.setProvider(provider);
													} else {
														if (userAccountDto
																.getTerminationDate() != null) {
															if (StringUtils
																	.isBlank(userAccountDto
																			.getTerminationReason())) {
																missingParameters
																		.add("userAccount.terminationReason");
																throw new MissingParameterException(
																		getMissingParametersExceptionMessage());
															}

															SubscriptionTerminationReason terminationReason = terminationReasonService
																	.findByCode(
																			userAccountDto
																					.getTerminationReason(),
																			provider);
															if (terminationReason == null) {
																throw new EntityDoesNotExistsException(
																		SubscriptionTerminationReason.class,
																		userAccountDto
																				.getTerminationReason());
															}

															try {
																userAccountService
																		.userAccountTermination(
																				userAccount,
																				userAccountDto
																						.getTerminationDate(),
																				terminationReason,
																				currentUser);
																continue;
															} catch (BusinessException e) {
																throw new MeveoApiException(
																		"Failed terminating billingAccount. "
																				+ e.getMessage());
															}
														}
													}

													userAccount
															.setBillingAccount(billingAccount);

													if (!StringUtils
															.isBlank(userAccountDto
																	.getStatus())) {
														try {
															userAccount
																	.setStatus(AccountStatusEnum
																			.valueOf(userAccountDto
																					.getStatus()));
														} catch (
																IllegalArgumentException
																| NullPointerException e) {
															log.warn(
																	"error while setting userAccountDto.status ",
																	e);
														}
													}

													if (!StringUtils
															.isBlank(userAccountDto
																	.getSubscriptionDate())) {
														userAccount
																.setSubscriptionDate(userAccountDto
																		.getSubscriptionDate());
													}
													if (!StringUtils
															.isBlank(userAccountDto
																	.getTerminationDate())) {
														userAccount
																.setTerminationDate(userAccountDto
																		.getTerminationDate());
													}

													populateNameAddressAndCustomFields(
															userAccount,
															userAccountDto,
															AccountLevelEnum.UA,
															currentUser);

													if (userAccount
															.isTransient()) {
														try {
															userAccountService
																	.createUserAccount(
																			billingAccount,
																			userAccount,
																			currentUser);
														} catch (AccountAlreadyExistsException e) {
															throw new MeveoApiException(
																	e.getMessage());
														}
													} else {
														userAccountService
																.update(userAccount,
																		currentUser);
													}

													// subscriptions
													if (userAccountDto
															.getSubscriptions() != null) {
														for (SubscriptionDto subscriptionDto : userAccountDto
																.getSubscriptions()
																.getSubscription()) {
															if (StringUtils
																	.isBlank(subscriptionDto
																			.getCode())) {
																log.warn(
																		"code is null={}",
																		subscriptionDto);
																continue;
															}

															Subscription subscription = subscriptionService
																	.findByCode(
																			subscriptionDto
																					.getCode(),
																			provider);
															if (subscription == null) {
																if (StringUtils
																		.isBlank(subscriptionDto
																				.getDescription())) {
																	missingParameters
																			.add("subscriptionDto.description");
																	throw new MissingParameterException(
																			getMissingParametersExceptionMessage());
																}

																subscription = new Subscription();
																subscription
																		.setCode(subscriptionDto
																				.getCode());

																if (!StringUtils
																		.isBlank(subscriptionDto
																				.getOfferTemplate())) {
																	OfferTemplate offerTemplate = offerTemplateService
																			.findByCode(
																					subscriptionDto
																							.getOfferTemplate(),
																					provider);
																	if (offerTemplate == null) {
																		throw new EntityDoesNotExistsException(
																				OfferTemplate.class,
																				subscriptionDto
																						.getOfferTemplate());
																	}

																	subscription
																			.setOffer(offerTemplate);
																} else {
																	throw new MeveoApiException(
																			"Subscription.offerTemplate cannot be null.");
																}

																subscription
																		.setProvider(provider);
															} else {
																if (subscriptionDto
																		.getTerminationDate() != null) {
																	if (StringUtils
																			.isBlank(subscriptionDto
																					.getTerminationReason())) {
																		missingParameters
																				.add("subscription.terminationReason");
																		throw new MissingParameterException(
																				getMissingParametersExceptionMessage());
																	}

																	SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService
																			.findByCode(
																					subscriptionDto
																							.getTerminationReason(),
																					provider);

																	if (subscriptionTerminationReason == null) {
																		throw new EntityDoesNotExistsException(
																				SubscriptionTerminationReason.class,
																				subscriptionDto
																						.getTerminationReason());
																	}

																	try {
																		subscriptionService
																				.terminateSubscription(
																						subscription,
																						subscriptionDto
																								.getTerminationDate(),
																						subscriptionTerminationReason,
																						currentUser);
																	} catch (BusinessException e) {
																		log.error("Error terminating subscription with code="
																				+ subscriptionDto
																						.getCode());
																		throw new MeveoApiException(
																				"Error terminating subscription with code="
																						+ subscriptionDto
																								.getCode());
																	}

																	continue;
																} else {
																	if (!StringUtils
																			.isBlank(subscriptionDto
																					.getOfferTemplate())) {
																		OfferTemplate offerTemplate = offerTemplateService
																				.findByCode(
																						subscriptionDto
																								.getOfferTemplate(),
																						provider);
																		if (offerTemplate == null) {
																			throw new EntityDoesNotExistsException(
																					OfferTemplate.class,
																					subscriptionDto
																							.getOfferTemplate());
																		}

																		subscription
																				.setOffer(offerTemplate);
																	}
																}
															}

															subscription
																	.setUserAccount(userAccount);

															if (!StringUtils
																	.isBlank(subscriptionDto
																			.getDescription())) {
																subscription
																		.setDescription(subscriptionDto
																				.getDescription());
															}
															if (!StringUtils
																	.isBlank(subscriptionDto
																			.getSubscriptionDate())) {
																subscription
																		.setSubscriptionDate(subscriptionDto
																				.getSubscriptionDate());
															}
															if (!StringUtils
																	.isBlank(subscriptionDto
																			.getTerminationDate())) {
																subscription
																		.setTerminationDate(subscriptionDto
																				.getTerminationDate());
															}

															// populate
															// customFields
															if (subscriptionDto
																	.getCustomFields() != null) {
																try {
																	populateCustomFields(
																			AccountLevelEnum.SUB,
																			subscriptionDto
																					.getCustomFields()
																					.getCustomField(),
																			subscription,
																			currentUser);
																} catch (
																		IllegalArgumentException
																		| IllegalAccessException e) {
																	log.error(
																			"Failed to associate custom field instance to a subscription {}",
																			subscriptionDto
																					.getCode(),
																			e);
																	throw new MeveoApiException(
																			"Failed to associate custom field instance to a subscription "
																					+ subscriptionDto
																							.getCode());
																}
															}

															if (subscription
																	.isTransient()) {
																subscriptionService
																		.create(subscription,
																				currentUser,
																				provider);
															} else {
																subscriptionService
																		.update(subscription,
																				currentUser);
															}

															// accesses
															if (subscriptionDto
																	.getAccesses() != null) {
																for (AccessDto accessDto : subscriptionDto
																		.getAccesses()
																		.getAccess()) {
																	if (StringUtils
																			.isBlank(accessDto
																					.getCode())) {
																		log.warn(
																				"code is null={}",
																				accessDto);
																		continue;
																	}

																	Access access = accessService
																			.findByUserIdAndSubscription(
																					accessDto
																							.getCode(),
																					subscription);
																	if (access == null) {
																		access = new Access();
																		access.setAccessUserId(accessDto
																				.getCode());

																		access.setProvider(provider);
																	}

																	access.setSubscription(subscription);

																	if (!StringUtils
																			.isBlank(accessDto
																					.getStartDate())) {
																		access.setStartDate(accessDto
																				.getStartDate());
																	}
																	if (!StringUtils
																			.isBlank(accessDto
																					.getEndDate())) {
																		access.setEndDate(accessDto
																				.getEndDate());
																	}

																	// populate
																	// customFields
																	if (accessDto
																			.getCustomFields() != null) {
																		try {
																			populateCustomFields(
																					AccountLevelEnum.ACC,
																					accessDto
																							.getCustomFields()
																							.getCustomField(),
																					access,
																					currentUser);
																		} catch (
																				IllegalArgumentException
																				| IllegalAccessException e) {
																			log.error(
																					"Failed to associate custom field instance to an access {}",
																					subscriptionDto
																							.getCode(),
																					e);
																			throw new MeveoApiException(
																					"Failed to associate custom field instance to an access "
																							+ subscriptionDto
																									.getCode());
																		}
																	}

																	if (access
																			.isTransient()) {
																		accessService
																				.create(access,
																						currentUser,
																						provider);
																	} else {
																		accessService
																				.update(access,
																						currentUser);
																	}
																}
															}

															// service instances
															if (subscriptionDto
																	.getServices() != null) {
																for (ServiceInstanceDto serviceInstanceDto : subscriptionDto
																		.getServices()
																		.getServiceInstance()) {
																	if (StringUtils
																			.isBlank(serviceInstanceDto
																					.getCode())) {
																		log.warn(
																				"code is null={}",
																				serviceInstanceDto);
																		continue;
																	}

																	if (serviceInstanceDto
																			.getTerminationDate() != null) {
																		// terminate
																		ServiceInstance serviceInstance = serviceInstanceService
																				.findActivatedByCodeAndSubscription(
																						serviceInstanceDto
																								.getCode(),
																						subscription);
																		if (serviceInstance != null) {
																			if (!StringUtils
																					.isBlank(serviceInstanceDto
																							.getTerminationReason())) {
																				SubscriptionTerminationReason serviceTerminationReason = terminationReasonService
																						.findByCode(
																								serviceInstanceDto
																										.getTerminationReason(),
																								provider);
																				if (serviceTerminationReason == null) {
																					throw new EntityDoesNotExistsException(
																							SubscriptionTerminationReason.class,
																							serviceInstanceDto
																									.getTerminationReason());
																				}
																				try {
																					serviceInstanceService
																							.terminateService(
																									serviceInstance,
																									serviceInstanceDto
																											.getTerminationDate(),
																									serviceTerminationReason,
																									currentUser);
																				} catch (BusinessException e) {
																					log.error(
																							"service termination={}",
																							e);
																					throw new MeveoApiException(
																							e.getMessage());
																				}

																			} else {
																				missingParameters
																						.add("serviceInstance.terminationReason");
																				throw new MissingParameterException(
																						getMissingParametersExceptionMessage());
																			}
																		} else {
																			throw new MeveoApiException(
																					"ServiceInstance with code="
																							+ subscriptionDto
																									.getCode()
																							+ " must be ACTIVE.");
																		}
																	} else {
																		if (subscription
																				.getStatus() == SubscriptionStatusEnum.RESILIATED) {
																			throw new MeveoApiException(
																					"Failed activating a service. Subscription is already RESILIATED.");
																		}
																		ServiceTemplate serviceTemplate = serviceTemplateService
																				.findByCode(
																						serviceInstanceDto
																								.getCode(),
																						provider);
																		if (serviceTemplate == null) {
																			throw new EntityDoesNotExistsException(
																					ServiceTemplate.class,
																					serviceInstanceDto
																							.getCode());
																		}
																		boolean alreadyActiveOrSuspended = false;
																		ServiceInstance serviceInstance = null;
																		List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService
																				.findByCodeSubscriptionAndStatus(
																						serviceTemplate
																								.getCode(),
																						subscription);

																		for (ServiceInstance subscriptionServiceInstance : subscriptionServiceInstances) {
																			if (subscriptionServiceInstance
																					.getStatus() != InstanceStatusEnum.CANCELED
																					&& subscriptionServiceInstance
																							.getStatus() != InstanceStatusEnum.TERMINATED
																					&& subscriptionServiceInstance
																							.getStatus() != InstanceStatusEnum.CLOSED) {
																				if (subscriptionServiceInstance
																						.getStatus()
																						.equals(InstanceStatusEnum.INACTIVE)) {
																					alreadyActiveOrSuspended = false;
																				} else {
																					throw new MeveoApiException(
																							"ServiceInstance with code="
																									+ serviceInstanceDto
																											.getCode()
																									+ " must not be ACTIVE or SUSPENDED.");
																				}
																				break;
																			}
																		}

																		if (!alreadyActiveOrSuspended) {
																			log.debug(
																					"instanciateService id={} checked, quantity={}",
																					serviceTemplate
																							.getId(),
																					1);
																			serviceInstance = new ServiceInstance();
																			serviceInstance
																					.setProvider(serviceTemplate
																							.getProvider());
																			serviceInstance
																					.setCode(serviceTemplate
																							.getCode());
																			serviceInstance
																					.setDescription(serviceTemplate
																							.getDescription());
																			serviceInstance
																					.setServiceTemplate(serviceTemplate);
																			serviceInstance
																					.setSubscription(subscription);
																			serviceInstance
																					.setSubscriptionDate(serviceInstanceDto
																							.getSubscriptionDate());
																			serviceInstance
																					.setQuantity(serviceInstanceDto
																							.getQuantity() == null ? BigDecimal.ONE
																							: serviceInstanceDto
																									.getQuantity());
																		}

																		try {
																			// instantiate
																			serviceInstanceService
																					.serviceInstanciation(
																							serviceInstance,
																							currentUser);
																		} catch (BusinessException e) {
																			throw new MeveoApiException(
																					e.getMessage());
																		}

																		if (serviceInstanceDto
																				.getSubscriptionDate() != null) {
																			// activate
																			try {
																				serviceInstanceService
																						.serviceActivation(
																								serviceInstance,
																								null,
																								null,
																								currentUser);
																			} catch (BusinessException e) {
																				throw new MeveoApiException(
																						e.getMessage());
																			}
																		}

																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				} else {
					missingParameters.add("seller.code");

					throw new MissingParameterException(
							getMissingParametersExceptionMessage());
				}
			}
		} else {
			missingParameters.add("sellers");

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	private void populateNameAddressAndCustomFields(
			AccountEntity accountEntity, AccountDto accountDto,
			AccountLevelEnum accountLevel, User currentUser)
			throws MeveoApiException {

		if (!StringUtils.isBlank(accountDto.getDescription())) {
			accountEntity.setDescription(accountDto.getDescription());
		}
		if (!StringUtils.isBlank(accountDto.getExternalRef1())) {
			accountEntity.setExternalRef1(accountDto.getExternalRef1());
		}
		if (!StringUtils.isBlank(accountDto.getExternalRef2())) {
			accountEntity.setExternalRef2(accountDto.getExternalRef2());
		}

		if (accountDto.getName() != null) {
			if (!StringUtils.isBlank(accountDto.getName().getFirstName())) {
				accountEntity.getName().setFirstName(
						accountDto.getName().getFirstName());
			}
			if (!StringUtils.isBlank(accountDto.getName().getLastName())) {
				accountEntity.getName().setLastName(
						accountDto.getName().getLastName());
			}
			if (!StringUtils.isBlank(accountDto.getName().getTitle())) {
				Title title = titleService.findByCode(
						currentUser.getProvider(), accountDto.getName()
								.getTitle());
				if (title != null) {
					accountEntity.getName().setTitle(title);
				}
			}
		}

		if (accountDto.getAddress() != null) {
			if (!StringUtils.isBlank(accountDto.getAddress().getAddress1())) {
				accountEntity.getAddress().setAddress1(
						accountDto.getAddress().getAddress1());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getAddress2())) {
				accountEntity.getAddress().setAddress2(
						accountDto.getAddress().getAddress2());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getAddress3())) {
				accountEntity.getAddress().setAddress3(
						accountDto.getAddress().getAddress3());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getZipCode())) {
				accountEntity.getAddress().setZipCode(
						accountDto.getAddress().getZipCode());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getCity())) {
				accountEntity.getAddress().setCity(
						accountDto.getAddress().getCity());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getState())) {
				accountEntity.getAddress().setState(
						accountDto.getAddress().getState());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getCountry())) {
				accountEntity.getAddress().setCountry(
						accountDto.getAddress().getCountry());
			}
		}

		// populate customFields
		if (accountDto.getCustomFields() != null) {
			try {
				populateCustomFields(accountLevel, accountDto.getCustomFields()
						.getCustomField(), accountEntity, currentUser);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				log.error(
						"Failed to associate custom field instance to an entity {}",
						accountDto.getCode(), e);
				throw new MeveoApiException(
						"Failed to associate custom field instance to an entity "
								+ accountDto.getCode());
			}
		}
	}

	public GetAccountHierarchyResponseDto findAccountHierarchy2(
			FindAccountHierachyRequestDto postData, User currentUser)
			throws MeveoApiException {
		GetAccountHierarchyResponseDto result = new GetAccountHierarchyResponseDto();
		Name name = null;

		if (postData.getName() == null && postData.getAddress() == null) {
			throw new MeveoApiException(
					"At least name or address must not be null.");
		}

		if (postData.getName() != null) {
			name = new Name();
			name.setFirstName(postData.getName().getFirstName());
			name.setLastName(postData.getName().getLastName());
		}

		Address address = null;
		if (postData.getAddress() != null) {
			address = new Address();
			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress3(postData.getAddress().getAddress3());
			address.setCity(postData.getAddress().getCity());
			address.setCountry(postData.getAddress().getCountry());
			address.setState(postData.getAddress().getState());
			address.setZipCode(postData.getAddress().getZipCode());
		}

		boolean validLevel = false;

		// check each level
		if ((postData.getLevel() & CUST) != 0) {
			validLevel = true;
			List<Customer> customers = customerService.findByNameAndAddress(
					name, address, currentUser.getProvider());
			if (customers != null) {
				for (Customer customer : customers) {
					result.getCustomers().getCustomer()
							.add(new CustomerDto(customer));
				}
			}
		}

		if ((postData.getLevel() & CA) != 0) {
			validLevel = true;
			List<CustomerAccount> customerAccounts = customerAccountService
					.findByNameAndAddress(name, address,
							currentUser.getProvider());
			if (customerAccounts != null) {
				for (CustomerAccount customerAccount : customerAccounts) {
					addCustomerAccount(result, customerAccount);
				}
			}
		}
		if ((postData.getLevel() & BA) != 0) {
			validLevel = true;
			List<BillingAccount> billingAccounts = billingAccountService
					.findByNameAndAddress(name, address,
							currentUser.getProvider());
			if (billingAccounts != null) {
				for (BillingAccount billingAccount : billingAccounts) {
					addBillingAccount(result, billingAccount);
				}
			}
		}
		if ((postData.getLevel() & UA) != 0) {
			validLevel = true;
			List<UserAccount> userAccounts = userAccountService
					.findByNameAndAddress(name, address,
							currentUser.getProvider());
			if (userAccounts != null) {
				for (UserAccount userAccount : userAccounts) {
					addUserAccount(result, userAccount);
				}
			}
		}

		if (!validLevel) {
			throw new MeveoApiException(
					MeveoApiErrorCode.BUSINESS_API_EXCEPTION,
					"INVALID_LEVEL_TYPE");
		}

		return result;
	}

	private void addUserAccount(GetAccountHierarchyResponseDto result,
			UserAccount userAccount) {
		BillingAccount billingAccount = userAccount.getBillingAccount();

		addBillingAccount(result, billingAccount);

		for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
			for (CustomerAccountDto customerAccountDto : customerDto
					.getCustomerAccounts().getCustomerAccount()) {
				for (BillingAccountDto billingAccountDto : customerAccountDto
						.getBillingAccounts().getBillingAccount()) {
					if (billingAccountDto.getCode().equals(
							billingAccount.getCode())) {
						if (billingAccountDto.getUserAccounts() != null
								&& billingAccountDto.getUserAccounts()
										.getUserAccount().size() > 0) {
							UserAccountDto userAccountDto = new UserAccountDto(
									userAccount);
							if (!billingAccountDto.getUserAccounts()
									.getUserAccount().contains(userAccountDto)) {
								billingAccountDto.getUserAccounts()
										.getUserAccount().add(userAccountDto);
							}
						} else {
							billingAccountDto.getUserAccounts()
									.getUserAccount()
									.add(new UserAccountDto(userAccount));
						}
					}
				}
			}
		}
	}

	private void addBillingAccount(GetAccountHierarchyResponseDto result,
			BillingAccount billingAccount) {
		CustomerAccount customerAccount = billingAccount.getCustomerAccount();
		Customer customer = customerAccount.getCustomer();

		addCustomer(result, customer);
		addCustomerAccount(result, customerAccount);

		for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
			for (CustomerAccountDto customerAccountDto : customerDto
					.getCustomerAccounts().getCustomerAccount()) {
				if (customerAccountDto.getCode().equals(
						customerAccount.getCode())) {
					if (customerAccountDto.getBillingAccounts() != null
							&& customerAccountDto.getBillingAccounts()
									.getBillingAccount().size() > 0) {
						BillingAccountDto billingAccountDto = new BillingAccountDto(
								billingAccount);
						if (!customerAccountDto.getBillingAccounts()
								.getBillingAccount()
								.contains(billingAccountDto)) {
							customerAccountDto.getBillingAccounts()
									.getBillingAccount().add(billingAccountDto);
						}
					} else {
						customerAccountDto.getBillingAccounts()
								.getBillingAccount()
								.add(new BillingAccountDto(billingAccount));
					}
				}
			}
		}
	}

	private void addCustomerAccount(GetAccountHierarchyResponseDto result,
			CustomerAccount customerAccount) {
		Customer customer = customerAccount.getCustomer();
		CustomerAccountDto customerAccountDto = new CustomerAccountDto(
				customerAccount);

		if (result.getCustomers() == null
				|| result.getCustomers().getCustomer().size() == 0) {
			CustomerDto customerDto = new CustomerDto(customer);
			customerDto.getCustomerAccounts().getCustomerAccount()
					.add(customerAccountDto);
			result.getCustomers().getCustomer().add(customerDto);
		} else {
			for (CustomerDto customerDtoLoop : result.getCustomers()
					.getCustomer()) {
				if (customerDtoLoop.getCode().equals(customer.getCode())) {
					if (!customerDtoLoop.getCustomerAccounts()
							.getCustomerAccount().contains(customerAccountDto)) {
						customerDtoLoop.getCustomerAccounts()
								.getCustomerAccount().add(customerAccountDto);
					}
				}
			}
		}
	}

	private void addCustomer(GetAccountHierarchyResponseDto result,
			Customer customer) {
		if (result.getCustomers() == null
				|| result.getCustomers().getCustomer().size() == 0) {
			result.getCustomers().getCustomer().add(new CustomerDto(customer));
		} else {
			boolean found = false;
			for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
				if (customerDto.getCode().equals(customer.getCode())) {
					if (!customerDto.isLoaded()) {
						customerDto.initFromEntity(customer);
					}

					found = true;
					break;
				}
			}

			if (!found) {
				result.getCustomers().getCustomer()
						.add(new CustomerDto(customer));
			}
		}
	}

	public void createCRMAccountHierarchy(CRMAccountHierarchyDto postData,
			User currentUser) throws MeveoApiException {
		NameDto name = new NameDto();
		name.setFirstName(postData.getName().getFirstName());
		name.setLastName(postData.getName().getLastName());
		name.setTitle(postData.getName().getTitle());

		AddressDto address = new AddressDto();
		if (postData.getAddress() != null) {
			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress3(postData.getAddress().getAddress3());
			address.setCity(postData.getAddress().getCity());
			address.setCountry(postData.getAddress().getCountry());
			address.setState(postData.getAddress().getState());
			address.setZipCode(postData.getAddress().getZipCode());
		}

		ContactInformationDto contactInformation = new ContactInformationDto();
		if (postData.getContactInformation() != null) {
			contactInformation.setEmail(postData.getContactInformation()
					.getEmail());
			contactInformation
					.setFax(postData.getContactInformation().getFax());
			contactInformation.setMobile(postData.getContactInformation()
					.getMobile());
			contactInformation.setPhone(postData.getContactInformation()
					.getPhone());
		}

		Map<String, Boolean> cfSet = new HashMap<>();
		if (postData.getCustomFields() != null
				&& postData.getCustomFields().getCustomField().size() > 0) {
			for (CustomFieldDto cfDto : postData.getCustomFields()
					.getCustomField()) {
				cfSet.put(cfDto.getCode(), Boolean.FALSE);
			}
		}

		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		try {
			accountHierarchyTypeEnum = AccountHierarchyTypeEnum
					.valueOf(postData.getCrmAccountType());
		} catch (IllegalArgumentException e) {
			throw new InvalidEnumValue(
					AccountHierarchyTypeEnum.class.getName(),
					postData.getCrmAccountType());
		}

		if (accountHierarchyTypeEnum.getHighLevel() == 4) {
			// create seller
			log.debug("create seller");

			if (StringUtils.isBlank(postData.getSeller())) {
				postData.setSeller(postData.getCode());
			}

			SellerDto sellerDto = new SellerDto();
			sellerDto.setCode(postData.getSeller());
			sellerDto.setDescription(postData.getDescription());
			sellerDto.setCountryCode(postData.getCountry());
			sellerDto.setCurrencyCode(postData.getCurrency());
			sellerDto.setLanguageCode(postData.getLanguage());

			sellerApi.create(sellerDto, currentUser);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 3
				&& accountHierarchyTypeEnum.getLowLevel() <= 3) {
			// create customer
			log.debug("create cust");

			CustomerDto customerDto = new CustomerDto();
			customerDto.setCode(postData.getCode());
			customerDto.setExternalRef1(postData.getExternalRef1());
			customerDto.setDescription(postData.getDescription());
			customerDto.setCustomerCategory(postData.getCustomerCategory());
			customerDto.setCustomerBrand(postData.getCustomerBrand());
			if (accountHierarchyTypeEnum.getHighLevel() == 3) {
				customerDto.setSeller(postData.getCrmParentCode());
			} else {
				customerDto.setSeller(postData.getCode());
			}
			customerDto.setMandateDate(postData.getMandateDate());
			customerDto.setMandateIdentification(postData
					.getMandateIdentification());
			customerDto.setName(name);
			customerDto.setAddress(address);
			customerDto.setContactInformation(contactInformation);
			customerDto.setCustomFields(postData.getCustomFields());

			customerApi.create(customerDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				Customer customer = customerService.findByCode(
						postData.getCode(), currentUser.getProvider());
				if (customer.getCustomFields() != null
						&& customer.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (customer.getCustomFields().containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 2
				&& accountHierarchyTypeEnum.getLowLevel() <= 2) {
			// create customer account
			log.debug("create ca");

			CustomerAccountDto customerAccountDto = new CustomerAccountDto();
			customerAccountDto.setCode(postData.getCode());
			customerAccountDto.setExternalRef1(postData.getExternalRef1());

			customerAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 2) {
				customerAccountDto.setCustomer(postData.getCrmParentCode());
			} else {
				customerAccountDto.setCustomer(postData.getCode());
			}
			customerAccountDto.setCurrency(postData.getCurrency());
			customerAccountDto.setLanguage(postData.getLanguage());
			customerAccountDto.setStatus(postData.getCaStatus());
			customerAccountDto.setPaymentMethod(postData.getPaymentMethod());
			customerAccountDto.setCreditCategory(postData.getCreditCategory());
			customerAccountDto.setDateStatus(postData.getDateStatus());
			customerAccountDto.setDateDunningLevel(postData
					.getDateDunningLevel());
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setDunningLevel(postData.getDunningLevel());
			customerAccountDto.setMandateDate(postData.getMandateDate());
			customerAccountDto.setMandateIdentification(postData
					.getMandateIdentification());
			customerAccountDto.setName(name);
			customerAccountDto.setAddress(address);
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setCustomFields(postData.getCustomFields());

			customerAccountApi.create(customerAccountDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				CustomerAccount customerAccount = customerAccountService
						.findByCode(postData.getCode(),
								currentUser.getProvider());
				if (customerAccount.getCustomFields() != null
						&& customerAccount.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (customerAccount.getCustomFields()
								.containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 1
				&& accountHierarchyTypeEnum.getLowLevel() <= 1) {
			// create billing account
			log.debug("create ba");

			BillingAccountDto billingAccountDto = new BillingAccountDto();
			billingAccountDto.setCode(postData.getCode());
			billingAccountDto.setExternalRef1(postData.getExternalRef1());
			billingAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 1) {
				billingAccountDto.setCustomerAccount(postData
						.getCrmParentCode());
			} else {
				billingAccountDto.setCustomerAccount(postData.getCode());
			}
			billingAccountDto.setBillingCycle(postData.getBillingCycle());
			billingAccountDto.setCountry(postData.getCountry());
			billingAccountDto.setLanguage(postData.getLanguage());
			billingAccountDto.setPaymentMethod(postData.getPaymentMethod());
			billingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
			billingAccountDto.setSubscriptionDate(postData
					.getSubscriptionDate());
			billingAccountDto.setTerminationDate(postData.getTerminationDate());
			billingAccountDto.setPaymentTerms(postData.getPaymentTerms());
			billingAccountDto.setElectronicBilling(postData
					.getElectronicBilling());
			billingAccountDto.setStatus(postData.getBaStatus());
			billingAccountDto.setTerminationReason(postData
					.getTerminationReason());
			billingAccountDto.setEmail(postData.getEmail());
			if (postData.getBankCoordinates() != null) {
				BankCoordinatesDto bankCoordinatesDto = new BankCoordinatesDto();
				bankCoordinatesDto.setAccountNumber(postData
						.getBankCoordinates().getAccountNumber());
				bankCoordinatesDto.setAccountOwner(postData
						.getBankCoordinates().getAccountOwner());
				bankCoordinatesDto.setBankCode(postData.getBankCoordinates()
						.getBankCode());
				bankCoordinatesDto.setBankId(postData.getBankCoordinates()
						.getBankId());
				bankCoordinatesDto.setBankName(postData.getBankCoordinates()
						.getBankName());
				bankCoordinatesDto.setBic(postData.getBankCoordinates()
						.getBic());
				bankCoordinatesDto.setBranchCode(postData.getBankCoordinates()
						.getBranchCode());
				bankCoordinatesDto.setIban(postData.getBankCoordinates()
						.getIban());
				bankCoordinatesDto.setIcs(postData.getBankCoordinates()
						.getIcs());
				bankCoordinatesDto.setIssuerName(postData.getBankCoordinates()
						.getIssuerName());
				bankCoordinatesDto.setIssuerNumber(postData
						.getBankCoordinates().getIssuerNumber());
				bankCoordinatesDto.setKey(postData.getBankCoordinates()
						.getKey());
				billingAccountDto.setBankCoordinates(bankCoordinatesDto);
			}
			billingAccountDto.setName(name);
			billingAccountDto.setAddress(address);
			billingAccountDto.setCustomFields(postData.getCustomFields());

			billingAccountApi.create(billingAccountDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				BillingAccount billingAccount = billingAccountService
						.findByCode(postData.getCode(),
								currentUser.getProvider());
				if (billingAccount.getCustomFields() != null
						&& billingAccount.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (billingAccount.getCustomFields().containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 0
				&& accountHierarchyTypeEnum.getLowLevel() <= 0) {
			// create user account
			log.debug("create ua");

			UserAccountDto userAccountDto = new UserAccountDto();
			userAccountDto.setCode(postData.getCode());
			userAccountDto.setExternalRef1(postData.getExternalRef1());
			userAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 0) {
				userAccountDto.setBillingAccount(postData.getCrmParentCode());
			} else {
				userAccountDto.setBillingAccount(postData.getCode());
			}
			userAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
			userAccountDto.setTerminationDate(postData.getTerminationDate());
			userAccountDto
					.setTerminationReason(postData.getTerminationReason());
			userAccountDto.setStatus(postData.getUaStatus());
			userAccountDto.setName(name);
			userAccountDto.setAddress(address);
			userAccountDto.setCustomFields(postData.getCustomFields());

			userAccountApi.create(userAccountDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				UserAccount userAccount = userAccountService.findByCode(
						postData.getCode(), currentUser.getProvider());
				if (userAccount.getCustomFields() != null
						&& userAccount.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (userAccount.getCustomFields().containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (postData.getCustomFields() != null
				&& postData.getCustomFields().getCustomField().size() > 0) {
			if (cfSet.size() > 0) {
				for (Map.Entry<String, Boolean> entry : cfSet.entrySet()) {
					if (entry.getValue().equals(false)) {
						throw new MeveoApiException("CUSTOM_FIELD_NOT_FOUND");
					}
				}
			}
		}
	}

	public void updateCRMAccountHierarchy(CRMAccountHierarchyDto postData,
			User currentUser) throws MeveoApiException {
		NameDto name = new NameDto();
		name.setFirstName(postData.getName().getFirstName());
		name.setLastName(postData.getName().getLastName());
		name.setTitle(postData.getName().getTitle());

		AddressDto address = new AddressDto();
		if (postData.getAddress() != null) {
			address.setAddress1(postData.getAddress().getAddress1());
			address.setAddress2(postData.getAddress().getAddress2());
			address.setAddress3(postData.getAddress().getAddress3());
			address.setCity(postData.getAddress().getCity());
			address.setCountry(postData.getAddress().getCountry());
			address.setState(postData.getAddress().getState());
			address.setZipCode(postData.getAddress().getZipCode());
		}

		ContactInformationDto contactInformation = new ContactInformationDto();
		if (postData.getContactInformation() != null) {
			contactInformation.setEmail(postData.getContactInformation()
					.getEmail());
			contactInformation
					.setFax(postData.getContactInformation().getFax());
			contactInformation.setMobile(postData.getContactInformation()
					.getMobile());
			contactInformation.setPhone(postData.getContactInformation()
					.getPhone());
		}

		Map<String, Boolean> cfSet = new HashMap<>();
		if (postData.getCustomFields() != null
				&& postData.getCustomFields().getCustomField().size() > 0) {
			for (CustomFieldDto cfDto : postData.getCustomFields()
					.getCustomField()) {
				cfSet.put(cfDto.getCode(), Boolean.FALSE);
			}
		}

		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		try {
			accountHierarchyTypeEnum = AccountHierarchyTypeEnum
					.valueOf(postData.getCrmAccountType());
		} catch (IllegalArgumentException e) {
			throw new InvalidEnumValue(
					AccountHierarchyTypeEnum.class.getName(),
					postData.getCrmAccountType());
		}

		if (accountHierarchyTypeEnum.getHighLevel() == 4) {
			// update seller
			log.debug("update seller");

			if (StringUtils.isBlank(postData.getSeller())) {
				postData.setSeller(postData.getCode());
			}

			SellerDto sellerDto = new SellerDto();
			sellerDto.setCode(postData.getSeller());
			sellerDto.setDescription(postData.getDescription());
			sellerDto.setCountryCode(postData.getCountry());
			sellerDto.setCurrencyCode(postData.getCurrency());
			sellerDto.setLanguageCode(postData.getLanguage());

			sellerApi.update(sellerDto, currentUser);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 3
				&& accountHierarchyTypeEnum.getLowLevel() <= 3) {
			// update customer
			log.debug("update");

			CustomerDto customerDto = new CustomerDto();
			customerDto.setCode(postData.getCode());
			customerDto.setExternalRef1(postData.getExternalRef1());
			customerDto.setDescription(postData.getDescription());
			customerDto.setCustomerCategory(postData.getCustomerCategory());
			customerDto.setCustomerBrand(postData.getCustomerBrand());
			if (accountHierarchyTypeEnum.getHighLevel() == 3) {
				customerDto.setSeller(postData.getCrmParentCode());
			} else {
				customerDto.setSeller(postData.getCode());
			}
			customerDto.setMandateDate(postData.getMandateDate());
			customerDto.setMandateIdentification(postData
					.getMandateIdentification());
			customerDto.setName(name);
			customerDto.setAddress(address);
			customerDto.setContactInformation(contactInformation);
			customerDto.setCustomFields(postData.getCustomFields());

			customerApi.update(customerDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				Customer customer = customerService.findByCode(
						postData.getCode(), currentUser.getProvider());
				if (customer.getCustomFields() != null
						&& customer.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (customer.getCustomFields().containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 2
				&& accountHierarchyTypeEnum.getLowLevel() <= 2) {
			// update customer account
			log.debug("update ca");

			CustomerAccountDto customerAccountDto = new CustomerAccountDto();
			customerAccountDto.setCode(postData.getCode());
			customerAccountDto.setExternalRef1(postData.getExternalRef1());
			customerAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 2) {
				customerAccountDto.setCustomer(postData.getCrmParentCode());
			} else {
				customerAccountDto.setCustomer(postData.getCode());
			}
			customerAccountDto.setCurrency(postData.getCurrency());
			customerAccountDto.setLanguage(postData.getLanguage());
			customerAccountDto.setStatus(postData.getCaStatus());
			customerAccountDto.setPaymentMethod(postData.getPaymentMethod());
			customerAccountDto.setCreditCategory(postData.getCreditCategory());
			customerAccountDto.setDateStatus(postData.getDateStatus());
			customerAccountDto.setDateDunningLevel(postData
					.getDateDunningLevel());
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setDunningLevel(postData.getDunningLevel());
			customerAccountDto.setMandateDate(postData.getMandateDate());
			customerAccountDto.setMandateIdentification(postData
					.getMandateIdentification());
			customerAccountDto.setName(name);
			customerAccountDto.setAddress(address);
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setCustomFields(postData.getCustomFields());

			customerAccountApi.update(customerAccountDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				CustomerAccount customerAccount = customerAccountService
						.findByCode(postData.getCode(),
								currentUser.getProvider());
				if (customerAccount.getCustomFields() != null
						&& customerAccount.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (customerAccount.getCustomFields()
								.containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 1
				&& accountHierarchyTypeEnum.getLowLevel() <= 1) {
			// update billing account
			log.debug("update ba");

			BillingAccountDto billingAccountDto = new BillingAccountDto();
			billingAccountDto.setCode(postData.getCode());
			billingAccountDto.setExternalRef1(postData.getExternalRef1());
			billingAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 1) {
				billingAccountDto.setCustomerAccount(postData
						.getCrmParentCode());
			} else {
				billingAccountDto.setCustomerAccount(postData.getCode());
			}
			billingAccountDto.setBillingCycle(postData.getBillingCycle());
			billingAccountDto.setCountry(postData.getCountry());
			billingAccountDto.setLanguage(postData.getLanguage());
			billingAccountDto.setPaymentMethod(postData.getPaymentMethod());
			billingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
			billingAccountDto.setSubscriptionDate(postData
					.getSubscriptionDate());
			billingAccountDto.setTerminationDate(postData.getTerminationDate());
			billingAccountDto.setPaymentTerms(postData.getPaymentTerms());
			billingAccountDto.setElectronicBilling(postData
					.getElectronicBilling());
			billingAccountDto.setStatus(postData.getBaStatus());
			billingAccountDto.setTerminationReason(postData
					.getTerminationReason());
			billingAccountDto.setEmail(postData.getEmail());
			if (postData.getBankCoordinates() != null) {
				BankCoordinatesDto bankCoordinatesDto = new BankCoordinatesDto();
				bankCoordinatesDto.setAccountNumber(postData
						.getBankCoordinates().getAccountNumber());
				bankCoordinatesDto.setAccountOwner(postData
						.getBankCoordinates().getAccountOwner());
				bankCoordinatesDto.setBankCode(postData.getBankCoordinates()
						.getBankCode());
				bankCoordinatesDto.setBankId(postData.getBankCoordinates()
						.getBankId());
				bankCoordinatesDto.setBankName(postData.getBankCoordinates()
						.getBankName());
				bankCoordinatesDto.setBic(postData.getBankCoordinates()
						.getBic());
				bankCoordinatesDto.setBranchCode(postData.getBankCoordinates()
						.getBranchCode());
				bankCoordinatesDto.setIban(postData.getBankCoordinates()
						.getIban());
				bankCoordinatesDto.setIcs(postData.getBankCoordinates()
						.getIcs());
				bankCoordinatesDto.setIssuerName(postData.getBankCoordinates()
						.getIssuerName());
				bankCoordinatesDto.setIssuerNumber(postData
						.getBankCoordinates().getIssuerNumber());
				bankCoordinatesDto.setKey(postData.getBankCoordinates()
						.getKey());
				billingAccountDto.setBankCoordinates(bankCoordinatesDto);
			}
			billingAccountDto.setName(name);
			billingAccountDto.setAddress(address);
			billingAccountDto.setCustomFields(postData.getCustomFields());

			billingAccountApi.update(billingAccountDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				BillingAccount billingAccount = billingAccountService
						.findByCode(postData.getCode(),
								currentUser.getProvider());
				if (billingAccount.getCustomFields() != null
						&& billingAccount.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (billingAccount.getCustomFields().containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 0
				&& accountHierarchyTypeEnum.getLowLevel() <= 0) {
			// update user account
			log.debug("update ua");

			UserAccountDto userAccountDto = new UserAccountDto();
			userAccountDto.setCode(postData.getCode());
			userAccountDto.setExternalRef1(postData.getExternalRef1());
			userAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 0) {
				userAccountDto.setBillingAccount(postData.getCrmParentCode());
			} else {
				userAccountDto.setBillingAccount(postData.getCode());
			}
			userAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
			userAccountDto.setTerminationDate(postData.getTerminationDate());
			userAccountDto
					.setTerminationReason(postData.getTerminationReason());
			userAccountDto.setStatus(postData.getUaStatus());
			userAccountDto.setName(name);
			userAccountDto.setAddress(address);
			userAccountDto.setCustomFields(postData.getCustomFields());

			userAccountApi.update(userAccountDto, currentUser, false);

			if (postData.getCustomFields() != null
					&& postData.getCustomFields().getCustomField().size() > 0) {
				// check if created
				UserAccount userAccount = userAccountService.findByCode(
						postData.getCode(), currentUser.getProvider());
				if (userAccount.getCustomFields() != null
						&& userAccount.getCustomFields().size() > 0) {
					for (String cfKey : cfSet.keySet()) {
						if (userAccount.getCustomFields().containsKey(cfKey)) {
							cfSet.put(cfKey, true);
						}
					}
				}
			}
		}

		if (postData.getCustomFields() != null
				&& postData.getCustomFields().getCustomField().size() > 0) {
			if (cfSet.size() > 0) {
				for (Map.Entry<String, Boolean> entry : cfSet.entrySet()) {
					if (entry.getValue().equals(false)) {
						throw new MeveoApiException("CUSTOM_FIELD_NOT_FOUND");
					}
				}
			}
		}
	}

	/**
	 * Create or update Account Hierarchy based on code.
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(AccountHierarchyDto postData, User currentUser)
			throws MeveoApiException {
		if (customerService.findByCode(postData.getCustomerId(),
				currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
