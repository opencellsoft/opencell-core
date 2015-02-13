package org.meveo.api.account;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.CustomerHierarchyDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.util.CustomerUtil;
import org.meveo.commons.utils.ParamBean;
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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentTermEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.TradingCurrencyService;
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
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;

@Stateless
public class AccountHierarchyApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

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
	private CustomerUtil customerUtil;

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
	@MeveoParamBean
	private ParamBean paramBean;

	public final String CUSTOMER_PREFIX = "CUST_";
	public final String CUSTOMER_ACCOUNT_PREFIX = "CA_";
	public final String BILLING_ACCOUNT_PREFIX = "BA_";
	public final String USER_ACCOUNT_PREFIX = "UA_";

	/*
	 * Creates the customer heirarchy including : - Trading Country - Trading
	 * Currency - Trading Language - Customer Brand - Customer Category - Seller
	 * - Customer - Customer Account - Billing Account - User Account
	 * 
	 * Required Parameters :customerId, customerBrandCode,customerCategoryCode,
	 * sellerCode
	 * ,currencyCode,countryCode,lastName,languageCode,billingCycleCode
	 */

	public static String enleverAccent(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}

		String newValue = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "_");

		newValue = newValue.replaceAll("[^A-Za-z0-9]", "_");
		return newValue;
	}

	public void create(AccountHierarchyDto postData, User currentUser) throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		if (customerService.findByCode(postData.getCustomerId(), provider) != null) {
			throw new EntityAlreadyExistsException(Customer.class, postData.getCustomerId());
		} else {
			if (!StringUtils.isEmpty(postData.getCustomerId()) && !StringUtils.isEmpty(postData.getCustomerBrandCode())
					&& !StringUtils.isEmpty(postData.getCustomerCategoryCode())
					&& !StringUtils.isEmpty(postData.getSellerCode())
					&& !StringUtils.isEmpty(postData.getCurrencyCode())
					&& !StringUtils.isEmpty(postData.getBillingCycleCode())
					&& !StringUtils.isEmpty(postData.getCountryCode()) && !StringUtils.isEmpty(postData.getLastName())
					&& !StringUtils.isEmpty(postData.getLanguageCode()) && !StringUtils.isEmpty(postData.getEmail())) {

				Seller seller = sellerService.findByCode(postData.getSellerCode(), provider);

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(
						postData.getCountryCode(), provider);

				if (tradingCountry == null) {
					Country country = countryService.findByCode(postData.getCountryCode());
					if (country == null) {
						throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
					} else {
						// create tradingCountry
						tradingCountry = new TradingCountry();
						tradingCountry.setCountry(country);
						tradingCountry.setProvider(provider);
						tradingCountry.setActive(true);
						tradingCountry.setPrDescription(country.getDescriptionEn());
						tradingCountry.setAuditable(auditableTrading);
						tradingCountryService.create(tradingCountry, currentUser, provider);
					}
				}

				TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
						postData.getCurrencyCode(), provider);
				if (tradingCurrency == null) {
					Currency currency = currencyService.findByCode(postData.getCurrencyCode());

					if (currency == null) {
						throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
					} else {
						// create tradingCountry
						tradingCurrency = new TradingCurrency();
						tradingCurrency.setCurrencyCode(postData.getCurrencyCode());
						tradingCurrency.setCurrency(currency);
						tradingCurrency.setProvider(provider);
						tradingCurrency.setActive(true);
						tradingCurrency.setPrDescription(currency.getDescriptionEn());
						tradingCurrency.setAuditable(auditableTrading);
						tradingCurrencyService.create(tradingCurrency, currentUser, provider);
					}
				}

				TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(
						postData.getLanguageCode(), provider);
				if (tradingLanguage == null) {
					Language language = languageService.findByCode(postData.getLanguageCode());

					if (language == null) {
						throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
					} else {
						// create tradingCountry
						tradingLanguage = new TradingLanguage();
						tradingLanguage.setLanguageCode(postData.getLanguageCode());
						tradingLanguage.setLanguage(language);
						tradingLanguage.setProvider(provider);
						tradingLanguage.setActive(true);
						tradingLanguage.setPrDescription(language.getDescriptionEn());
						tradingLanguage.setAuditable(auditableTrading);
						tradingLanguageService.create(tradingLanguage, currentUser, provider);
					}
				}

				CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrandCode(),
						currentUser.getProvider());

				if (customerBrand == null) {
					customerBrand = new CustomerBrand();
					customerBrand.setCode(enleverAccent(postData.getCustomerBrandCode()));
					customerBrand.setDescription(postData.getCustomerBrandCode());
					customerBrandService.create(customerBrand, currentUser, provider);
				}

				CustomerCategory customerCategory = customerCategoryService.findByCode(
						postData.getCustomerCategoryCode(), currentUser.getProvider());

				if (customerCategory == null) {
					customerCategory = new CustomerCategory();
					customerCategory.setCode(enleverAccent(postData.getCustomerCategoryCode()));
					customerCategory.setDescription(postData.getCustomerCategoryCode());
					customerCategoryService.create(customerCategory, currentUser, provider);
				}

				int caPaymentMethod = Integer.parseInt(paramBean.getProperty(
						"api.default.customerAccount.paymentMethod", "1"));
				int creditCategory = Integer.parseInt(paramBean.getProperty(
						"api.default.customerAccount.creditCategory", "5"));
				int baPaymentMethod = Integer.parseInt(paramBean.getProperty(
						"api.default.customerAccount.paymentMethod", "1"));

				Auditable auditable = new Auditable();
				auditable.setCreated(new Date());
				auditable.setCreator(currentUser);

				if (seller == null) {
					seller = new Seller();
					seller.setActive(true);
					seller.setCode(enleverAccent(postData.getSellerCode()));
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

				Title title = titleService.findByCode(provider, enleverAccent(postData.getTitleCode()));

				String customerCode = CUSTOMER_PREFIX + enleverAccent(postData.getCustomerId());
				Customer customer = customerService.findByCode(customerCode, provider);
				if (customer != null) {
					throw new EntityAlreadyExistsException(Customer.class, customerCode);
				}

				customer = new Customer();
				customer.getName().setLastName(postData.getLastName());
				customer.getName().setFirstName(postData.getFirstName());
				customer.getName().setTitle(title);
				customer.setContactInformation(contactInformation);
				customer.setAddress(address);
				customer.setCode(CUSTOMER_PREFIX + enleverAccent(postData.getCustomerId()));
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
				customerAccount.setCode(CUSTOMER_ACCOUNT_PREFIX + enleverAccent(postData.getCustomerId()));
				customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
				customerAccount.setPaymentMethod(PaymentMethodEnum.getValue(caPaymentMethod));
				customerAccount.setCreditCategory(CreditCategoryEnum.getValue(creditCategory));
				customerAccount.setTradingCurrency(tradingCurrency);
				customerAccountService.create(customerAccount, currentUser, provider);

				String billingCycleCode = enleverAccent(postData.getBillingCycleCode());
				BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, currentUser,
						provider);
				if (billingCycle == null) {
					throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
				}

				BillingAccount billingAccount = new BillingAccount();
				billingAccount.setEmail(postData.getEmail());
				billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(postData.getPaymentMethod()));
				billingAccount.setCode(BILLING_ACCOUNT_PREFIX + enleverAccent(postData.getCustomerId()));
				billingAccount.setStatus(AccountStatusEnum.ACTIVE);
				billingAccount.setCustomerAccount(customerAccount);
				billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(baPaymentMethod));
				billingAccount.setElectronicBilling(Boolean.valueOf(paramBean.getProperty(
						"api.customerHeirarchy.billingAccount.electronicBilling", "true")));
				billingAccount.setTradingCountry(tradingCountry);
				billingAccount.setTradingLanguage(tradingLanguage);
				billingAccount.setBillingCycle(billingCycle);
				billingAccount.setProvider(provider);
				billingAccountService.createBillingAccount(billingAccount, currentUser, provider);

				String userAccountCode = USER_ACCOUNT_PREFIX + enleverAccent(postData.getCustomerId());
				UserAccount userAccount = new UserAccount();
				userAccount.setStatus(AccountStatusEnum.ACTIVE);
				userAccount.setBillingAccount(billingAccount);
				userAccount.setCode(userAccountCode);

				try {
					userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
				} catch (AccountAlreadyExistsException e) {
					throw new EntityAlreadyExistsException(UserAccount.class, userAccountCode);
				}
			} else {
				if (StringUtils.isEmpty(postData.getCustomerId())) {
					missingParameters.add("customerId");
				}
				if (StringUtils.isEmpty(postData.getCustomerBrandCode())) {
					missingParameters.add("customerBrandCode");
				}
				if (StringUtils.isEmpty(postData.getCustomerCategoryCode())) {
					missingParameters.add("customerCategoryCode");
				}
				if (StringUtils.isEmpty(postData.getSellerCode())) {
					missingParameters.add("sellerCode");
				}
				if (StringUtils.isEmpty(postData.getCurrencyCode())) {
					missingParameters.add("currencyCode");
				}
				if (StringUtils.isEmpty(postData.getCountryCode())) {
					missingParameters.add("countryCode");
				}
				if (StringUtils.isEmpty(postData.getLastName())) {
					missingParameters.add("lastName");
				}
				if (StringUtils.isEmpty(postData.getBillingCycleCode())) {
					missingParameters.add("billingCycleCode");
				}
				if (StringUtils.isEmpty(postData.getLanguageCode())) {
					missingParameters.add("languageCode");
				}
				if (StringUtils.isEmpty(postData.getEmail())) {
					missingParameters.add("email");
				}

				throw new MissingParameterException(getMissingParametersExceptionMessage());
			}
		}
	}

	public void update(AccountHierarchyDto postData, User currentUser) throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		Customer customer = customerService.findByCode(postData.getCustomerId(), provider);

		if (customer == null) {
			throw new EntityDoesNotExistsException(Customer.class, postData.getCustomerId());
		}

		if (!StringUtils.isEmpty(postData.getCustomerId()) && !StringUtils.isEmpty(postData.getCustomerBrandCode())
				&& !StringUtils.isEmpty(postData.getCustomerCategoryCode())
				&& !StringUtils.isEmpty(postData.getSellerCode()) && !StringUtils.isEmpty(postData.getCurrencyCode())
				&& !StringUtils.isEmpty(postData.getBillingCycleCode())
				&& !StringUtils.isEmpty(postData.getCountryCode()) && !StringUtils.isEmpty(postData.getLastName())
				&& !StringUtils.isEmpty(postData.getLanguageCode()) && !StringUtils.isEmpty(postData.getEmail())) {

			Seller seller = sellerService.findByCode(postData.getSellerCode(), provider);

			Auditable auditableTrading = new Auditable();
			auditableTrading.setCreated(new Date());
			auditableTrading.setCreator(currentUser);

			Country country = countryService.findByCode(postData.getCountryCode());

			if (country == null) {
				throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
			}

			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(),
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
				tradingCountryService.create(tradingCountry, currentUser, provider);
			} else {
				tradingCountryService.update(tradingCountry, currentUser);
			}

			Currency currency = currencyService.findByCode(postData.getCurrencyCode());

			if (currency == null) {
				throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
					postData.getCurrencyCode(), provider);

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
				tradingCurrencyService.create(tradingCurrency, currentUser, provider);
			} else {
				tradingCurrencyService.update(tradingCurrency, currentUser);
			}

			Language language = languageService.findByCode(postData.getLanguageCode());

			if (language == null) {
				throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
			}

			TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(
					postData.getLanguageCode(), provider);

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
				tradingLanguageService.create(tradingLanguage, currentUser, provider);
			} else {
				tradingLanguageService.update(tradingLanguage, currentUser);
			}

			CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrandCode(),
					currentUser.getProvider());

			CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategoryCode(),
					currentUser.getProvider());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
			}

			customerBrand.setCode(enleverAccent(postData.getCustomerBrandCode()));
			customerBrand.setDescription(postData.getCustomerBrandCode());

			if (customerBrand.isTransient()) {
				customerBrandService.create(customerBrand, currentUser, provider);
			} else {
				customerBrandService.update(customerBrand, currentUser);
			}

			if (customerCategory == null) {
				customerCategory = new CustomerCategory();
			}

			customerCategory.setCode(enleverAccent(postData.getCustomerCategoryCode()));
			customerCategory.setDescription(postData.getCustomerCategoryCode());

			if (customerCategory.isTransient()) {
				customerCategoryService.create(customerCategory, currentUser, provider);
			} else {
				customerCategoryService.update(customerCategory, currentUser);
			}

			int caPaymentMethod = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.paymentMethod",
					"1"));
			int creditCategory = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.creditCategory",
					"5"));

			int baPaymentMethod = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.paymentMethod",
					"1"));

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

			Title title = titleService.findByCode(provider, postData.getTitleCode());

			customer.getName().setLastName(postData.getLastName());
			customer.getName().setFirstName(postData.getFirstName());
			customer.getName().setTitle(title);
			customer.setAddress(address);
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);
			customer.setContactInformation(contactInformation);
			customer.setSeller(seller);

			customerService.update(customer, currentUser);

			CustomerAccount customerAccount = customerAccountService.findByCode(
					CUSTOMER_ACCOUNT_PREFIX + postData.getCustomerId(), provider);
			if (customerAccount == null) {
				customerAccount = new CustomerAccount();
				customerAccount.setCode(CUSTOMER_ACCOUNT_PREFIX + enleverAccent(postData.getCustomerId()));
			}
			customerAccount.setCustomer(customer);

			customerAccount.setAddress(address);
			customerAccount.setContactInformation(contactInformation);

			customerAccount.getName().setFirstName(postData.getFirstName());
			customerAccount.getName().setLastName(postData.getLastName());
			customerAccount.getName().setTitle(title);
			customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
			customerAccount.setPaymentMethod(PaymentMethodEnum.getValue(caPaymentMethod));
			customerAccount.setCreditCategory(CreditCategoryEnum.getValue(creditCategory));
			customerAccount.setTradingCurrency(tradingCurrency);

			if (customerAccount.isTransient()) {
				customerAccountService.create(customerAccount, currentUser, provider);
			} else {
				customerAccountService.update(customerAccount, currentUser);
			}

			String billingCycleCode = enleverAccent(postData.getBillingCycleCode());
			BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, currentUser,
					provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
			}

			BillingAccount billingAccount = billingAccountService.findByCode(
					BILLING_ACCOUNT_PREFIX + postData.getCustomerId(), provider);

			if (billingAccount == null) {
				billingAccount = new BillingAccount();
				billingAccount.setCode(BILLING_ACCOUNT_PREFIX + enleverAccent(postData.getCustomerId()));
			}

			billingAccount.setEmail(postData.getEmail());
			billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(postData.getPaymentMethod()));
			billingAccount.setStatus(AccountStatusEnum.ACTIVE);
			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(baPaymentMethod));
			billingAccount.setElectronicBilling(Boolean.valueOf(paramBean.getProperty(
					"api.customerHeirarchy.billingAccount.electronicBilling", "true")));
			billingAccount.setTradingCountry(tradingCountry);
			billingAccount.setTradingLanguage(tradingLanguage);
			billingAccount.setBillingCycle(billingCycle);

			if (billingAccount.isTransient()) {
				billingAccountService.createBillingAccount(billingAccount, currentUser, provider);
			} else {
				billingAccountService.update(billingAccount, currentUser);
			}

			UserAccount userAccount = userAccountService.findByCode(USER_ACCOUNT_PREFIX + postData.getCustomerId(),
					provider);
			if (userAccount == null) {
				userAccount = new UserAccount();
			}

			userAccount.setStatus(AccountStatusEnum.ACTIVE);
			userAccount.setBillingAccount(billingAccount);

			if (userAccount.isTransient()) {
				String userAccountCode = USER_ACCOUNT_PREFIX + enleverAccent(postData.getCustomerId());
				try {
					userAccount.setCode(userAccountCode);
					userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
				} catch (AccountAlreadyExistsException e) {
					throw new EntityAlreadyExistsException(UserAccount.class, userAccountCode);
				}
			} else {
				userAccountService.update(userAccount, currentUser);
			}
		} else {
			if (StringUtils.isEmpty(postData.getCustomerId())) {
				missingParameters.add("customerId");
			}
			if (StringUtils.isEmpty(postData.getCustomerBrandCode())) {
				missingParameters.add("customerBrandCode");
			}
			if (StringUtils.isEmpty(postData.getCustomerCategoryCode())) {
				missingParameters.add("customerCategoryCode");
			}
			if (StringUtils.isEmpty(postData.getSellerCode())) {
				missingParameters.add("sellerCode");
			}
			if (StringUtils.isEmpty(postData.getCurrencyCode())) {
				missingParameters.add("currencyCode");
			}
			if (StringUtils.isEmpty(postData.getCountryCode())) {
				missingParameters.add("countryCode");
			}
			if (StringUtils.isEmpty(postData.getLastName())) {
				missingParameters.add("lastName");
			}
			if (StringUtils.isEmpty(postData.getBillingCycleCode())) {
				missingParameters.add("billingCycleCode");
			}
			if (StringUtils.isEmpty(postData.getLanguageCode())) {
				missingParameters.add("languageCode");
			}
			if (StringUtils.isEmpty(postData.getEmail())) {
				missingParameters.add("email");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public List<AccountHierarchyDto> find(AccountHierarchyDto customerDto, User currentUser) throws MeveoApiException {
		List<AccountHierarchyDto> result = new ArrayList<AccountHierarchyDto>();
		Customer customerFilter;

		customerFilter = customerUtil.getCustomer(customerDto, currentUser.getProvider());

		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(customerDto.getIndex(),
				customerDto.getLimit(), null, null, customerDto.getSortField(), null);

		List<Customer> customers = customerService.findByValues(customerFilter, paginationConfiguration);
		for (Customer customer : customers) {
			result.add(new AccountHierarchyDto(customer));
		}

		return result;
	}

	public void customerHierarchyUpdate(CustomerHierarchyDto postData, User currentUser) throws MeveoApiException {
		if (postData.getSellers() != null && postData.getSellers().getSeller().size() > 0) {
			for (SellerDto sellerDto : postData.getSellers().getSeller()) {
				if (!StringUtils.isBlank(sellerDto.getCode())) {
					Provider provider = currentUser.getProvider();

					Seller seller = sellerService.findByCode(sellerDto.getCode(), provider);
					if (seller == null) {
						seller = new Seller();
						seller.setCode(sellerDto.getCode());
					}
					seller.setDescription(sellerDto.getDescription());
					seller.setInvoicePrefix(sellerDto.getInvoicePrefix());

					if (!StringUtils.isBlank(sellerDto.getCurrencyCode())) {
						TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(
								sellerDto.getCurrencyCode(), provider);
						if (tradingCurrency != null) {
							seller.setTradingCurrency(tradingCurrency);
						}
					}

					if (!StringUtils.isBlank(sellerDto.getCountryCode())) {
						TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(
								sellerDto.getCountryCode(), provider);
						if (tradingCountry != null) {
							seller.setTradingCountry(tradingCountry);
						}
					}

					if (!StringUtils.isBlank(sellerDto.getLanguageCode())) {
						TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(
								sellerDto.getLanguageCode(), provider);
						if (tradingLanguage != null) {
							seller.setTradingLanguage(tradingLanguage);
						}
					}

					seller.setProvider(provider);
					if (seller.isTransient()) {
						sellerService.create(seller, currentUser, provider);
					} else {
						sellerService.update(seller, currentUser);
					}

					// customers
					if (sellerDto.getCustomers() != null) {
						for (CustomerDto customerDto : sellerDto.getCustomers().getCustomer()) {
							if (StringUtils.isBlank(customerDto.getCode())) {
								log.warn("code is null={}", customerDto);
								continue;
							}

							Customer customer = customerService.findByCode(customerDto.getCode(), provider);
							if (customer == null) {
								customer = new Customer();
								customer.setCode(customerDto.getCode());
							}

							populateNameAndAddress(customer, customerDto, AccountLevelEnum.CUST, currentUser);
							customer.setSeller(seller);
							customer.setDescription(customerDto.getDescription());

							if (!StringUtils.isBlank(customerDto.getCustomerBrand())) {
								CustomerBrand customerBrand = customerBrandService.findByCode(customerDto
										.getCustomerBrand());
								if (customerBrand != null) {
									customer.setCustomerBrand(customerBrand);
								}
							} else {
								missingParameters.add("customer.customerBrand");
								throw new MissingParameterException(getMissingParametersExceptionMessage());
							}

							if (!StringUtils.isBlank(customerDto.getCustomerCategory())) {
								CustomerCategory customerCategory = customerCategoryService.findByCode(customerDto
										.getCustomerCategory());
								if (customerCategory != null) {
									customer.setCustomerCategory(customerCategory);
								}
							} else {
								missingParameters.add("customer.customerCategory");
								throw new MissingParameterException(getMissingParametersExceptionMessage());
							}

							customer.setProvider(provider);
							if (customer.isTransient()) {
								customerService.create(customer, currentUser, provider);
							} else {
								customerService.update(customer, currentUser);
							}

							// customerAccounts
							if (customerDto.getCustomerAccounts() != null) {
								for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts()
										.getCustomerAccount()) {
									if (StringUtils.isBlank(customerAccountDto.getCode())) {
										log.warn("code is null={}", customerAccountDto);
										continue;
									}

									CustomerAccount customerAccount = customerAccountService.findByCode(
											customerAccountDto.getCode(), provider);
									if (customerAccount == null) {
										customerAccount = new CustomerAccount();
										customerAccount.setCode(customerAccountDto.getCode());
									} else {
										if (!StringUtils.isBlank(customerAccountDto.getStatus())) {
											try {
												CustomerAccountStatusEnum customerAccountStatusEnum = CustomerAccountStatusEnum
														.valueOf(customerAccountDto.getStatus());
												if (customerAccountStatusEnum == CustomerAccountStatusEnum.CLOSE) {
													try {
														customerAccountService.closeCustomerAccount(customerAccount,
																currentUser);
													} catch (Exception e) {
														throw new MeveoApiException(
																"Failed closing customerAccount with code="
																		+ customerAccountDto.getCode() + ". "
																		+ e.getMessage());
													}
												}
											} catch (IllegalStateException e) {
												log.warn("customerAccountStatus={}", e.getMessage());
											}
										}
									}

									populateNameAndAddress(customerAccount, customerAccountDto, AccountLevelEnum.CA,
											currentUser);
									customerAccount.setCustomer(customer);
									customerAccount.setDescription(customerAccount.getDescription());

									if (!StringUtils.isBlank(customerAccountDto.getCurrency())) {
										TradingCurrency tradingCurrency = tradingCurrencyService
												.findByTradingCurrencyCode(customerAccountDto.getCurrency(), provider);
										if (tradingCurrency == null) {
											throw new EntityDoesNotExistsException(TradingCurrency.class,
													customerAccountDto.getCurrency());
										}

										customerAccount.setTradingCurrency(tradingCurrency);
									} else {
										missingParameters.add("customerAccount.currency");
										throw new MissingParameterException(getMissingParametersExceptionMessage());
									}

									try {
										customerAccount.setStatus(CustomerAccountStatusEnum.valueOf(customerAccountDto
												.getStatus()));
									} catch (IllegalArgumentException | NullPointerException e) {
										log.warn("customerAccount.status={}", e.getMessage());
									}
									try {
										customerAccount.setPaymentMethod(PaymentMethodEnum.valueOf(customerAccountDto
												.getPaymentMethod()));
									} catch (IllegalArgumentException | NullPointerException e) {
										log.warn("customerAccount.paymentMethod={}", e.getMessage());
									}
									try {
										customerAccount.setCreditCategory(CreditCategoryEnum.valueOf(customerAccountDto
												.getCreditCategory()));
									} catch (IllegalArgumentException | NullPointerException e) {
										log.warn("customerAccount.creditCategory={}", e.getMessage());
									}
									try {
										customerAccount.setDunningLevel(DunningLevelEnum.valueOf(customerAccountDto
												.getDunningLevel()));
									} catch (IllegalArgumentException | NullPointerException e) {
										log.warn("customerAccount.dunningLevel={}", e.getMessage());
									}

									customerAccount.setDateStatus(customerAccountDto.getDateStatus());
									customerAccount.setDateDunningLevel(customerAccount.getDateDunningLevel());
									customerAccount.getContactInformation().setEmail(customerAccountDto.getEmail());
									customerAccount.getContactInformation().setPhone(customerAccountDto.getPhone());
									customerAccount.getContactInformation().setMobile(customerAccountDto.getMobile());
									customerAccount.getContactInformation().setFax(customerAccountDto.getFax());

									customerAccount.setMandateDate(customerAccountDto.getMandateDate());
									customerAccount.setMandateIdentification(customerAccountDto
											.getMandateIdentification());

									customerAccount.setProvider(provider);
									if (customerAccount.isTransient()) {
										customerAccountService.create(customerAccount, currentUser, provider);
									} else {
										customerAccountService.update(customerAccount, currentUser);
									}

									// billing accounts
									if (customerAccountDto.getBillingAccounts() != null) {
										for (BillingAccountDto billingAccountDto : customerAccountDto
												.getBillingAccounts().getBillingAccount()) {
											if (StringUtils.isBlank(billingAccountDto.getCode())) {
												log.warn("code is null={}", billingAccountDto);
												continue;
											}

											BillingAccount billingAccount = billingAccountService.findByCode(
													billingAccountDto.getCode(), provider);
											if (billingAccount == null) {
												billingAccount = new BillingAccount();
												billingAccount.setCode(billingAccountDto.getCode());
											} else {
												if (billingAccountDto.getTerminationDate() != null) {
													if (!StringUtils.isBlank(billingAccountDto.getTerminationReason())) {
														missingParameters.add("billingAccount.terminationReason");
														throw new MissingParameterException(
																getMissingParametersExceptionMessage());
													}

													SubscriptionTerminationReason terminationReason = terminationReasonService
															.findByCode(billingAccountDto.getTerminationReason(),
																	provider);
													if (terminationReason == null) {
														throw new EntityDoesNotExistsException(
																SubscriptionTerminationReason.class,
																billingAccountDto.getTerminationReason());
													}

													try {
														billingAccountService.billingAccountTermination(billingAccount,
																billingAccountDto.getTerminationDate(),
																terminationReason, currentUser);
														continue;
													} catch (BusinessException e) {
														throw new MeveoApiException(
																"Failed terminating billingAccount. " + e.getMessage());
													}
												}
											}

											populateNameAndAddress(billingAccount, billingAccountDto,
													AccountLevelEnum.BA, currentUser);
											billingAccount.setCustomerAccount(customerAccount);
											billingAccount.setDescription(customerAccount.getDescription());

											if (!StringUtils.isBlank(billingAccountDto.getBillingCycle())) {
												BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(
														billingAccountDto.getBillingCycle(), provider);
												if (billingCycle != null) {
													billingAccount.setBillingCycle(billingCycle);
												}
											} else {
												missingParameters.add("billingAccount.billingCycle");
												throw new MissingParameterException(
														getMissingParametersExceptionMessage());
											}

											if (!StringUtils.isBlank(billingAccountDto.getCountry())) {
												TradingCountry tradingCountry = tradingCountryService
														.findByTradingCountryCode(billingAccountDto.getCountry(),
																provider);
												if (tradingCountry != null) {
													billingAccount.setTradingCountry(tradingCountry);
												}
											} else {
												missingParameters.add("billingAccount.country");
												throw new MissingParameterException(
														getMissingParametersExceptionMessage());
											}

											if (!StringUtils.isBlank(billingAccountDto.getLanguage())) {
												TradingLanguage tradingLanguage = tradingLanguageService
														.findByTradingLanguageCode(billingAccountDto.getLanguage(),
																provider);
												if (tradingLanguage != null) {
													billingAccount.setTradingLanguage(tradingLanguage);
												}
											} else {
												missingParameters.add("billingAccount.language");
												throw new MissingParameterException(
														getMissingParametersExceptionMessage());
											}

											try {
												billingAccount.setPaymentMethod(PaymentMethodEnum
														.valueOf(billingAccountDto.getPaymentMethod()));
											} catch (IllegalArgumentException | NullPointerException e) {
												log.warn("billingAccount.paymentMethod={}", e.getMessage());
											}
											try {
												billingAccount.setPaymentTerm(PaymentTermEnum.valueOf(billingAccountDto
														.getPaymentTerms()));
											} catch (IllegalArgumentException | NullPointerException e) {
												log.warn("billingAccount.paymentTerms={}", e.getMessage());
											}

											billingAccount.setNextInvoiceDate(billingAccountDto.getNextInvoiceDate());
											billingAccount.setSubscriptionDate(billingAccountDto.getSubscriptionDate());
											billingAccount.setTerminationDate(billingAccount.getTerminationDate());
											billingAccount.setElectronicBilling(billingAccountDto
													.getElectronicBilling());

											billingAccount.setProvider(provider);
											if (billingAccount.isTransient()) {
												billingAccountService.create(billingAccount, currentUser, provider);
											} else {
												billingAccountService.update(billingAccount, currentUser);
											}

											// user accounts
											if (billingAccountDto.getUserAccounts() != null) {
												for (UserAccountDto userAccountDto : billingAccountDto
														.getUserAccounts().getUserAccount()) {
													if (StringUtils.isBlank(userAccountDto.getCode())) {
														log.warn("code is null={}", userAccountDto);
														continue;
													}

													UserAccount userAccount = userAccountService.findByCode(
															userAccountDto.getCode(), provider);
													if (userAccount == null) {
														userAccount = new UserAccount();
														userAccount.setCode(userAccountDto.getCode());
													} else {
														if (userAccountDto.getTerminationDate() != null) {
															if (!StringUtils.isBlank(userAccountDto
																	.getTerminationReason())) {
																missingParameters.add("userAccount.terminationReason");
																throw new MissingParameterException(
																		getMissingParametersExceptionMessage());
															}

															SubscriptionTerminationReason terminationReason = terminationReasonService
																	.findByCode(userAccountDto.getTerminationReason(),
																			provider);
															if (terminationReason == null) {
																throw new EntityDoesNotExistsException(
																		SubscriptionTerminationReason.class,
																		userAccountDto.getTerminationReason());
															}

															try {
																userAccountService.userAccountTermination(userAccount,
																		userAccountDto.getTerminationDate(),
																		terminationReason, currentUser);
																continue;
															} catch (BusinessException e) {
																throw new MeveoApiException(
																		"Failed terminating billingAccount. "
																				+ e.getMessage());
															}
														}
													}

													populateNameAndAddress(userAccount, userAccountDto,
															AccountLevelEnum.UA, currentUser);
													userAccount.setBillingAccount(billingAccount);
													userAccount.setDescription(userAccountDto.getDescription());

													try {
														userAccount.setStatus(AccountStatusEnum.valueOf(userAccountDto
																.getStatus()));
													} catch (IllegalArgumentException | NullPointerException e) {
														log.warn("userAccountDto.status={}", e.getMessage());
													}

													userAccount.setSubscriptionDate(userAccountDto
															.getSubscriptionDate());
													userAccount.setTerminationDate(userAccountDto.getTerminationDate());

													userAccount.setProvider(provider);
													if (userAccount.isTransient()) {
														userAccountService.create(userAccount, currentUser, provider);
													} else {
														userAccountService.update(userAccount, currentUser);
													}

													// subscriptions
													if (userAccountDto.getSubscriptions() != null) {
														for (SubscriptionDto subscriptionDto : userAccountDto
																.getSubscriptions().getSubscription()) {
															if (StringUtils.isBlank(subscriptionDto.getCode())) {
																log.warn("code is null={}", subscriptionDto);
																continue;
															}

															Subscription subscription = subscriptionService.findByCode(
																	subscriptionDto.getCode(), provider);
															if (subscription == null) {
																subscription = new Subscription();
																subscription.setCode(subscriptionDto.getCode());
															} else {
																if (subscriptionDto.getTerminationDate() != null) {
																	// TODO
																	// [delete
																	// or
																	// update
																	// status?]
																}
															}

															if (!StringUtils
																	.isBlank(subscriptionDto.getOfferTemplate())) {
																OfferTemplate offerTemplate = offerTemplateService
																		.findByCode(subscriptionDto.getOfferTemplate(),
																				provider);
																if (offerTemplate == null) {
																	throw new EntityDoesNotExistsException(
																			OfferTemplate.class,
																			subscriptionDto.getOfferTemplate());
																}

																subscription.setOffer(offerTemplate);
															} else {
																throw new MeveoApiException(
																		"Subscription.offerTemplate cannot be null.");
															}

															subscription.setUserAccount(userAccount);
															subscription.setDescription(subscriptionDto
																	.getDescription());
															subscription.setSubscriptionDate(subscriptionDto
																	.getSubscriptionDate());
															subscription.setTerminationDate(subscriptionDto
																	.getTerminationDate());

															subscription.setProvider(provider);
															if (subscription.isTransient()) {
																subscriptionService.create(subscription, currentUser,
																		provider);
															} else {
																subscriptionService.update(subscription, currentUser);
															}

															// accesses
															if (subscriptionDto.getAccesses() != null) {
																for (AccessDto accessDto : subscriptionDto
																		.getAccesses().getAccess()) {
																	if (StringUtils.isBlank(accessDto.getCode())) {
																		log.warn("code is null={}", accessDto);
																		continue;
																	}

																	Access access = accessService
																			.findByUserIdAndSubscription(
																					accessDto.getCode(), subscription);
																	if (access == null) {
																		access = new Access();
																		access.setAccessUserId(accessDto.getCode());
																	}

																	access.setSubscription(subscription);
																	access.setStartDate(accessDto.getStartDate());
																	access.setEndDate(accessDto.getEndDate());

																	access.setProvider(provider);
																	if (access.isTransient()) {
																		accessService.create(access, currentUser,
																				provider);
																	} else {
																		accessService.update(access, currentUser);
																	}
																}
															}

															// service instances
															if (subscriptionDto.getServices() != null) {
																for (ServiceInstanceDto serviceInstanceDto : subscriptionDto
																		.getServices().getServiceInstance()) {
																	if (StringUtils.isBlank(serviceInstanceDto
																			.getCode())) {
																		log.warn("code is null={}", serviceInstanceDto);
																		continue;
																	}

																	if (serviceInstanceDto.getTerminationDate() != null) {
																		// terminate
																		ServiceInstance serviceInstance = serviceInstanceService
																				.findByCodeAndSubscription(
																						serviceInstanceDto.getCode(),
																						subscription);
																		if (serviceInstance == null) {
																			throw new EntityDoesNotExistsException(
																					ServiceInstance.class,
																					serviceInstanceDto.getCode());
																		}

																		if (serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
																			log.info(
																					"serviceInstance with code={} is already TERMINATED",
																					serviceInstance.getCode());
																			continue;
																		}

																		if (!StringUtils.isBlank(serviceInstanceDto
																				.getTerminationReason())) {
																			SubscriptionTerminationReason serviceTerminationReason = terminationReasonService
																					.findByCode(serviceInstanceDto
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
																				log.error("service termination={}",
																						e.getMessage());
																				throw new MeveoApiException(
																						e.getMessage());
																			}
																		} else {
																			throw new MeveoApiException(
																					"TerminationReason is required when terminating a subscription.");
																		}
																	} else {
																		if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
																			throw new MeveoApiException(
																					"Failed activating a service. Subscription is already RESILIATED.");
																		}

																		// check
																		// if
																		// already
																		// exists
																		ServiceInstance serviceInstance = serviceInstanceService
																				.findByCodeAndSubscription(
																						serviceInstanceDto.getCode(),
																						subscription);
																		if (serviceInstance != null) {
																			// update
																			log.debug(
																					"update service instance with code={}",
																					serviceInstanceDto.getCode());
																			serviceInstance
																					.setSubscriptionDate(serviceInstanceDto
																							.getSubscriptionDate());
																			serviceInstance
																					.setQuantity(serviceInstanceDto
																							.getQuantity() == null ? BigDecimal.ONE
																							: serviceInstanceDto
																									.getQuantity());
																		} else {
																			ServiceTemplate serviceTemplate = serviceTemplateService
																					.findByCode(serviceInstanceDto
																							.getCode(), provider);
																			if (serviceTemplate == null) {
																				throw new EntityDoesNotExistsException(
																						ServiceTemplate.class,
																						serviceInstanceDto.getCode());
																			}

																			// instantiate
																			log.debug(
																					"instanciateService id={} checked, quantity={}",
																					serviceTemplate.getId(), 1);

																			serviceInstance = new ServiceInstance();
																			serviceInstance.setProvider(serviceTemplate
																					.getProvider());
																			serviceInstance.setCode(serviceTemplate
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
																			try {
																				serviceInstanceService
																						.serviceInstanciation(
																								serviceInstance,
																								currentUser);
																			} catch (BusinessException e) {
																				throw new MeveoApiException(
																						e.getMessage());
																			}

																			// activate
																			try {
																				serviceInstanceService
																						.serviceActivation(
																								serviceInstance, null,
																								null, currentUser);
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

					throw new MissingParameterException(getMissingParametersExceptionMessage());
				}
			}
		} else {
			missingParameters.add("sellers");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	private void populateNameAndAddress(AccountEntity accountEntity, AccountDto accountDto,
			AccountLevelEnum accountLevel, User currentUser) {
		if (accountDto.getName() != null) {
			accountEntity.getName().setFirstName(accountDto.getName().getFirstName());
			accountEntity.getName().setLastName(accountDto.getName().getLastName());
			if (!StringUtils.isBlank(accountDto.getName().getTitle())) {
				Title title = titleService.findByCode(currentUser.getProvider(), accountDto.getName().getTitle());
				if (title != null) {
					accountEntity.getName().setTitle(title);
				}
			}
		}

		if (accountDto.getAddress() != null) {
			accountEntity.getAddress().setAddress1(accountDto.getAddress().getAddress1());
			accountEntity.getAddress().setAddress2(accountDto.getAddress().getAddress2());
			accountEntity.getAddress().setAddress3(accountDto.getAddress().getAddress3());
			accountEntity.getAddress().setZipCode(accountDto.getAddress().getZipCode());
			accountEntity.getAddress().setCity(accountDto.getAddress().getCity());
			accountEntity.getAddress().setState(accountDto.getAddress().getState());
			accountEntity.getAddress().setCountry(accountDto.getAddress().getCountry());
		}

		if (accountDto.getCustomFields() != null) {
			for (CustomFieldDto cf : accountDto.getCustomFields().getCustomField()) {
				// check if custom field exists has a template
				List<CustomFieldTemplate> customFieldTemplates = customFieldTemplateService
						.findByAccountLevel(accountLevel);
				boolean found = false;
				if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
					for (CustomFieldTemplate cft : customFieldTemplates) {
						if (cf.getCode().equals(cft.getCode())) {
							found = true;
							CustomFieldInstance cfi = customFieldInstanceService.findByCodeAndAccount(cf.getCode(),
									accountEntity);
							if (cfi != null) {
								// update
								cfi.setActive(true);
								cfi.setDateValue(cf.getDateValue());
								cfi.setDescription(cf.getDescription());
								cfi.setDoubleValue(cf.getDoubleValue());
								cfi.setLongValue(cf.getLongValue());
								cfi.setStringValue(cf.getStringValue());
								cfi.updateAudit(currentUser);
							} else {
								// create
								CustomFieldInstance cfiNew = new CustomFieldInstance();
								cfiNew.setAccount(accountEntity);
								cfiNew.setActive(true);
								cfiNew.setCode(cf.getCode());
								cfiNew.setDateValue(cf.getDateValue());
								cfiNew.setDescription(cf.getDescription());
								cfiNew.setDoubleValue(cf.getDoubleValue());
								cfiNew.setLongValue(cf.getLongValue());
								cfiNew.setProvider(currentUser.getProvider());
								cfiNew.setStringValue(cf.getStringValue());
								cfiNew.updateAudit(currentUser);
								accountEntity.getCustomFields().put(cfiNew.getCode(), cfiNew);
							}
						}
					}
				} else {
					log.warn("No custom field template defined.");
				}

				if (!found) {
					log.warn("No custom field template with code={}", cf.getCode());
				}
			}
		}
	}
}
