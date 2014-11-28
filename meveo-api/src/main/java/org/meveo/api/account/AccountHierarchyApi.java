package org.meveo.api.account;

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
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.util.CustomerUtil;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AccountStatusEnum;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CreditCategoryEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
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
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;

@Stateless
public class AccountHierarchyApi extends BaseApi {

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
	private CalendarService calendarService;

	@Inject
	private TitleService titleService;

	@Inject
	private CustomerUtil customerUtil;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

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

		String newValue = Normalizer.normalize(value, Normalizer.Form.NFD)
				.replaceAll("[\u0300-\u036F]", "_");

		newValue = newValue.replaceAll("[^A-Za-z0-9]", "_");
		return newValue;
	}

	public void create(AccountHierarchyDto postData, User currentUser)
			throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		if (customerService.findByCode(em, postData.getCustomerId(),
				provider) != null) {
			throw new EntityAlreadyExistsException(Customer.class,
					postData.getCustomerId());
		} else {
			if (!StringUtils.isEmpty(postData.getCustomerId())
					&& !StringUtils.isEmpty(postData
							.getCustomerBrandCode())
					&& !StringUtils.isEmpty(postData
							.getCustomerCategoryCode())
					&& !StringUtils.isEmpty(postData.getSellerCode())
					&& !StringUtils.isEmpty(postData.getCurrencyCode())
					&& !StringUtils.isEmpty(postData.getCountryCode())
					&& !StringUtils.isEmpty(postData.getLastName())
					&& !StringUtils.isEmpty(postData.getLanguageCode())
					&& !StringUtils.isEmpty(postData
							.getBillingCycleCode())
					&& !StringUtils.isEmpty(postData.getEmail())) {

				Seller seller = sellerService.findByCode(em,
						postData.getSellerCode(), provider);

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				TradingCountry tradingCountry = tradingCountryService
						.findByTradingCountryCode(em,
								postData.getCountryCode(), provider);

				if (tradingCountry == null) {
					Country country = countryService.findByCode(em,
							postData.getCountryCode());
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
						tradingCountryService.create(em, tradingCountry,
								currentUser, provider);
					}
				}

				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(em,
								postData.getCurrencyCode(), provider);
				if (tradingCurrency == null) {
					Currency currency = currencyService.findByCode(em,
							postData.getCurrencyCode());

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
						tradingCurrencyService.create(em, tradingCurrency,
								currentUser, provider);
					}
				}

				TradingLanguage tradingLanguage = tradingLanguageService
						.findByTradingLanguageCode(em,
								postData.getLanguageCode(), provider);
				if (tradingLanguage == null) {
					Language language = languageService.findByCode(em,
							postData.getLanguageCode());

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
						tradingLanguageService.create(em, tradingLanguage,
								currentUser, provider);
					}
				}

				CustomerBrand customerBrand = customerBrandService.findByCode(
						em, postData.getCustomerBrandCode());

				if (customerBrand == null) {
					customerBrand = new CustomerBrand();
					customerBrand.setCode(enleverAccent(postData
							.getCustomerBrandCode()));
					customerBrand.setDescription(postData
							.getCustomerBrandCode());
					customerBrandService.create(em, customerBrand, currentUser,
							provider);
				}

				CustomerCategory customerCategory = customerCategoryService
						.findByCode(em,
								postData.getCustomerCategoryCode());

				if (customerCategory == null) {
					customerCategory = new CustomerCategory();
					customerCategory.setCode(enleverAccent(postData
							.getCustomerCategoryCode()));
					customerCategory.setDescription(postData
							.getCustomerCategoryCode());
					customerCategoryService.create(em, customerCategory,
							currentUser, provider);
				}

				int caPaymentMethod = Integer.parseInt(paramBean.getProperty(
						"asp.api.default.customerAccount.paymentMethod", "1"));
				int creditCategory = Integer.parseInt(paramBean.getProperty(
						"asp.api.default.customerAccount.creditCategory", "5"));
				int baPaymentMethod = Integer.parseInt(paramBean.getProperty(
						"asp.api.default.customerAccount.paymentMethod", "1"));

				Auditable auditable = new Auditable();
				auditable.setCreated(new Date());
				auditable.setCreator(currentUser);

				if (seller == null) {
					seller = new Seller();
					seller.setActive(true);
					seller.setCode(enleverAccent(postData
							.getSellerCode()));
					seller.setAuditable(auditable);
					seller.setProvider(provider);
					seller.setTradingCountry(tradingCountry);
					seller.setTradingCurrency(tradingCurrency);

					sellerService.create(em, seller, currentUser, provider);
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

				Title title = titleService.findByCode(em, provider,
						enleverAccent(postData.getTitleCode()));

				Customer customer = new Customer();
				customer.getName().setLastName(postData.getLastName());
				customer.getName()
						.setFirstName(postData.getFirstName());
				customer.getName().setTitle(title);
				customer.setContactInformation(contactInformation);
				customer.setAddress(address);
				customer.setCode(enleverAccent(postData.getCustomerId()));
				customer.setCustomerBrand(customerBrand);
				customer.setCustomerCategory(customerCategory);
				customer.setSeller(seller);
				customerService.create(em, customer, currentUser, provider);

				CustomerAccount customerAccount = new CustomerAccount();
				customerAccount.setCustomer(customer);
				customerAccount.setAddress(address);
				customerAccount.setContactInformation(contactInformation);
				customerAccount.getName().setFirstName(
						postData.getFirstName());
				customerAccount.getName().setLastName(
						postData.getLastName());
				customerAccount.getName().setTitle(title);
				customerAccount.setCode(enleverAccent(postData
						.getCustomerId()));
				customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
				customerAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(caPaymentMethod));
				customerAccount.setCreditCategory(CreditCategoryEnum
						.getValue(creditCategory));
				customerAccount.setTradingCurrency(tradingCurrency);
				customerAccountService.create(em, customerAccount, currentUser,
						provider);

				BillingCycle billingCycle = billingCycleService
						.findByBillingCycleCode(em,
								enleverAccent(postData
										.getBillingCycleCode()), currentUser,
								provider);

				if (billingCycle == null) {
					billingCycle = billingCycleService.findByBillingCycleCode(
							em, paramBean.getProperty(
									"default.billingCycleCode", "DEFAULT"),
							provider);
					if (billingCycle == null) {
						String imputationCalendarCode = paramBean.getProperty(
								"default.imputationCalendar.Name",
								"DEF_IMP_CAL");
						Calendar imputationCalendar = calendarService
								.findByName(em, imputationCalendarCode);

						String cycleCalendarCode = paramBean.getProperty(
								"default.cycleCalendar.Name", "DEF_CYC_CAL");
						Calendar cycleCalendar = calendarService.findByName(em,
								cycleCalendarCode);

						if (imputationCalendar == null) {
							throw new EntityDoesNotExistsException(
									Calendar.class, imputationCalendarCode);
						}
						if (cycleCalendar == null) {
							throw new EntityDoesNotExistsException(
									Calendar.class, cycleCalendarCode);
						}

						billingCycle = new BillingCycle();
						billingCycle.setCode(paramBean.getProperty(
								"default.billingCycleCode", "DEFAULT"));
						billingCycle.setActive(true);
						billingCycle.setBillingTemplateName(paramBean
								.getProperty("default.billingTemplateName",
										"DEFAULT"));
						billingCycle.setInvoiceDateDelay(0);
						billingCycle.setCalendar(cycleCalendar);

						billingCycleService.create(em, billingCycle,
								currentUser, provider);
					}
				}

				BillingAccount billingAccount = new BillingAccount();
				billingAccount.setEmail(postData.getEmail());
				billingAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(postData.getPaymentMethod()));
				billingAccount.setCode(enleverAccent(postData
						.getCustomerId()));
				billingAccount.setStatus(AccountStatusEnum.ACTIVE);
				billingAccount.setCustomerAccount(customerAccount);
				billingAccount.setPaymentMethod(PaymentMethodEnum
						.getValue(baPaymentMethod));
				billingAccount
						.setElectronicBilling(Boolean.valueOf(paramBean
								.getProperty(
										"customerHeirarchy.billingAccount.electronicBilling",
										"true")));
				billingAccount.setTradingCountry(tradingCountry);
				billingAccount.setTradingLanguage(tradingLanguage);
				billingAccount.setBillingCycle(billingCycle);
				billingAccountService.createBillingAccount(em, billingAccount,
						currentUser, provider);

				String userAccountCode = enleverAccent(postData
						.getCustomerId());
				UserAccount userAccount = new UserAccount();
				userAccount.setStatus(AccountStatusEnum.ACTIVE);
				userAccount.setBillingAccount(billingAccount);
				userAccount.setCode(userAccountCode);

				try {
					userAccountService.createUserAccount(em, billingAccount,
							userAccount, currentUser);
				} catch (AccountAlreadyExistsException e) {
					throw new EntityAlreadyExistsException(UserAccount.class,
							userAccountCode);
				}
			} else {
				if (StringUtils.isEmpty(postData.getCustomerId())) {
					missingParameters.add("Customer ID");
				}
				if (StringUtils
						.isEmpty(postData.getCustomerBrandCode())) {
					missingParameters.add("Customer Brand Code");
				}
				if (StringUtils.isEmpty(postData
						.getCustomerCategoryCode())) {
					missingParameters.add("Customer Category Code");
				}
				if (StringUtils.isEmpty(postData.getSellerCode())) {
					missingParameters.add("Seller Code");
				}
				if (StringUtils.isEmpty(postData.getCurrencyCode())) {
					missingParameters.add("Currency Code");
				}
				if (StringUtils.isEmpty(postData.getCountryCode())) {
					missingParameters.add("Country Code");
				}
				if (StringUtils.isEmpty(postData.getLastName())) {
					missingParameters.add("Last Name");
				}
				if (StringUtils.isEmpty(postData.getLanguageCode())) {
					missingParameters.add("Language Code");
				}
				if (StringUtils.isEmpty(postData.getBillingCycleCode())) {
					missingParameters.add("Billing Cycle Code");
				}

				throw new MissingParameterException(
						getMissingParametersExceptionMessage());
			}
		}
	}

	public void update(AccountHierarchyDto postData, User currentUser)
			throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		Customer customer = customerService.findByCode(em,
				postData.getCustomerId(), provider);

		if (customer == null) {
			throw new EntityAlreadyExistsException(Customer.class,
					postData.getCustomerId());
		}

		if (!StringUtils.isEmpty(postData.getCustomerId())
				&& !StringUtils.isEmpty(postData
						.getCustomerBrandCode())
				&& !StringUtils.isEmpty(postData
						.getCustomerCategoryCode())
				&& !StringUtils.isEmpty(postData.getSellerCode())
				&& !StringUtils.isEmpty(postData.getCurrencyCode())
				&& !StringUtils.isEmpty(postData.getCountryCode())
				&& !StringUtils.isEmpty(postData.getLastName())
				&& !StringUtils.isEmpty(postData.getLanguageCode())
				&& !StringUtils.isEmpty(postData
						.getBillingCycleCode())) {

			Seller seller = sellerService.findByCode(em,
					postData.getSellerCode(), provider);

			Auditable auditableTrading = new Auditable();
			auditableTrading.setCreated(new Date());
			auditableTrading.setCreator(currentUser);

			Country country = countryService.findByCode(em,
					postData.getCountryCode());

			if (country == null) {
				throw new EntityDoesNotExistsException(Country.class,
						postData.getCountryCode());
			}

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(em,
							postData.getCountryCode(), provider);

			if (tradingCountry == null) {
				tradingCountry = new TradingCountry();
				tradingCountry.setAuditable(auditableTrading);
			}

			tradingCountry.setCountry(country);
			tradingCountry.setProvider(provider);
			tradingCountry.setActive(true);
			tradingCountry.setPrDescription(country.getDescriptionEn());

			if (tradingCountry.isTransient()) {
				tradingCountryService.create(em, tradingCountry, currentUser,
						provider);
			} else {
				tradingCountryService.update(em, tradingCountry, currentUser);
			}

			Currency currency = currencyService.findByCode(em,
					postData.getCurrencyCode());

			if (currency == null) {
				throw new EntityDoesNotExistsException(Currency.class,
						postData.getCurrencyCode());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(em,
							postData.getCurrencyCode(), provider);

			if (tradingCurrency == null) {
				// create tradingCountry
				tradingCurrency = new TradingCurrency();
				tradingCurrency.setAuditable(auditableTrading);
			}

			tradingCurrency.setCurrencyCode(postData
					.getCurrencyCode());
			tradingCurrency.setCurrency(currency);
			tradingCurrency.setProvider(provider);
			tradingCurrency.setActive(true);
			tradingCurrency.setPrDescription(currency.getDescriptionEn());

			if (tradingCurrency.isTransient()) {
				tradingCurrencyService.create(em, tradingCurrency, currentUser,
						provider);
			} else {
				tradingCurrencyService.update(em, tradingCurrency, currentUser);
			}

			Language language = languageService.findByCode(em,
					postData.getLanguageCode());

			if (language == null) {
				throw new EntityDoesNotExistsException(Language.class,
						postData.getLanguageCode());
			}

			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(em,
							postData.getLanguageCode(), provider);

			if (tradingLanguage == null) {
				tradingLanguage = new TradingLanguage();
				tradingLanguage.setAuditable(auditableTrading);
			}

			tradingLanguage.setLanguageCode(postData
					.getLanguageCode());
			tradingLanguage.setLanguage(language);
			tradingLanguage.setProvider(provider);
			tradingLanguage.setActive(true);
			tradingLanguage.setPrDescription(language.getDescriptionEn());

			if (tradingLanguage.isTransient()) {
				tradingLanguageService.create(em, tradingLanguage, currentUser,
						provider);
			} else {
				tradingLanguageService.update(em, tradingLanguage, currentUser);
			}

			CustomerBrand customerBrand = customerBrandService.findByCode(em,
					postData.getCustomerBrandCode());

			CustomerCategory customerCategory = customerCategoryService
					.findByCode(em,
							postData.getCustomerCategoryCode());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
			}

			customerBrand.setCode(enleverAccent(postData
					.getCustomerBrandCode()));
			customerBrand.setDescription(postData
					.getCustomerBrandCode());

			if (customerBrand.isTransient()) {
				customerBrandService.create(em, customerBrand, currentUser,
						provider);
			} else {
				customerBrandService.update(em, customerBrand, currentUser);
			}

			if (customerCategory == null) {
				customerCategory = new CustomerCategory();
			}

			customerCategory.setCode(enleverAccent(postData
					.getCustomerCategoryCode()));
			customerCategory.setDescription(postData
					.getCustomerCategoryCode());

			if (customerCategory.isTransient()) {
				customerCategoryService.create(em, customerCategory,
						currentUser, provider);
			} else {
				customerCategoryService.update(em, customerCategory,
						currentUser);
			}

			int caPaymentMethod = Integer.parseInt(paramBean.getProperty(
					"asp.api.default.customerAccount.paymentMethod", "1"));
			int creditCategory = Integer.parseInt(paramBean.getProperty(
					"asp.api.default.customerAccount.creditCategory", "5"));

			int baPaymentMethod = Integer.parseInt(paramBean.getProperty(
					"asp.api.default.customerAccount.paymentMethod", "1"));

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
				sellerService.create(em, seller, currentUser, provider);
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

			Title title = titleService.findByCode(em, provider,
					postData.getTitleCode());

			customer.getName().setLastName(postData.getLastName());
			customer.getName().setFirstName(postData.getFirstName());
			customer.getName().setTitle(title);
			customer.setAddress(address);
			customer.setCode(enleverAccent(postData.getCustomerId()));
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);
			customer.setContactInformation(contactInformation);
			customer.setSeller(seller);

			customerService.update(em, customer, currentUser);

			CustomerAccount customerAccount = customerAccountService
					.findByCode(em, postData.getCustomerId(),
							provider);
			if (customerAccount == null) {
				customerAccount = new CustomerAccount();
			}
			customerAccount.setCustomer(customer);

			customerAccount.setAddress(address);
			customerAccount.setContactInformation(contactInformation);

			customerAccount.getName().setFirstName(
					postData.getFirstName());
			customerAccount.getName().setLastName(
					postData.getLastName());
			customerAccount.getName().setTitle(title);
			customerAccount.setCode(enleverAccent(postData
					.getCustomerId()));
			customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
			customerAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(caPaymentMethod));
			customerAccount.setCreditCategory(CreditCategoryEnum
					.getValue(creditCategory));
			customerAccount.setTradingCurrency(tradingCurrency);

			if (customerAccount.isTransient()) {
				customerAccountService.create(em, customerAccount, currentUser,
						provider);
			} else {
				customerAccountService.update(em, customerAccount, currentUser);
			}

			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(em,
							postData.getBillingCycleCode(),
							currentUser, provider);

			if (billingCycle == null) {
				billingCycle = billingCycleService.findByBillingCycleCode(em,
						paramBean.getProperty("default.billingCycleCode",
								"DEFAULT"), provider);
				if (billingCycle == null) {
					String imputationCalendarCode = paramBean.getProperty(
							"default.imputationCalendar.Name", "DEF_IMP_CAL");
					Calendar imputationCalendar = calendarService.findByName(
							em, paramBean.getProperty(
									"default.imputationCalendar.Name",
									"DEF_IMP_CAL"));

					String cycleCalendarCode = paramBean.getProperty(
							"default.cycleCalendar.Name", "DEF_CYC_CAL");
					Calendar cycleCalendar = calendarService.findByName(em,
							paramBean.getProperty("default.cycleCalendar.Name",
									"DEF_CYC_CAL"));

					if (imputationCalendar == null) {
						throw new EntityDoesNotExistsException(Calendar.class,
								imputationCalendarCode);
					}
					if (cycleCalendar == null) {
						throw new EntityDoesNotExistsException(Calendar.class,
								cycleCalendarCode);
					}

					billingCycle = new BillingCycle();
					billingCycle.setCode(paramBean.getProperty(
							"default.billingCycleCode", "DEFAULT"));
					billingCycle.setActive(true);
					billingCycle.setBillingTemplateName(paramBean.getProperty(
							"default.billingTemplateName", "DEFAULT"));
					billingCycle.setInvoiceDateDelay(0);
					billingCycle.setCalendar(cycleCalendar);

					billingCycleService.create(em, billingCycle, currentUser,
							provider);
				}
			}

			BillingAccount billingAccount = billingAccountService.findByCode(
					em, postData.getCustomerId(), provider);
			if (billingAccount == null) {
				billingAccount = new BillingAccount();
			}
			billingAccount.setCode(enleverAccent(postData
					.getCustomerId()));
			billingAccount.setEmail(postData.getEmail());
			billingAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(postData.getPaymentMethod()));
			billingAccount.setStatus(AccountStatusEnum.ACTIVE);
			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(baPaymentMethod));
			billingAccount
					.setElectronicBilling(Boolean.valueOf(paramBean
							.getProperty(
									"customerHeirarchy.billingAccount.electronicBilling",
									"true")));
			billingAccount.setTradingCountry(tradingCountry);
			billingAccount.setTradingLanguage(tradingLanguage);
			billingAccount.setBillingCycle(billingCycle);
			if (billingAccount.isTransient()) {
				billingAccountService.createBillingAccount(em, billingAccount,
						currentUser, provider);
			} else {
				billingAccountService.updateBillingAccount(em, billingAccount,
						currentUser);
			}

			UserAccount userAccount = userAccountService.findByCode(em,
					postData.getCustomerId(), provider);
			if (userAccount == null) {
				userAccount = new UserAccount();
			}
			String userAccountCode = enleverAccent(postData
					.getCustomerId());
			userAccount.setStatus(AccountStatusEnum.ACTIVE);
			userAccount.setBillingAccount(billingAccount);
			userAccount.setCode(userAccountCode);

			if (userAccount.isTransient()) {
				try {
					userAccountService.createUserAccount(em, billingAccount,
							userAccount, currentUser);
				} catch (AccountAlreadyExistsException e) {
					throw new EntityAlreadyExistsException(UserAccount.class,
							userAccountCode);
				}
			} else {
				try {
					userAccountService.updateUserAccount(em, userAccount,
							currentUser);
				} catch (BusinessException e) {
					throw new BusinessApiException();
				}
			}
		} else {
			if (StringUtils.isEmpty(postData.getCustomerId())) {
				missingParameters.add("Customer ID");
			}
			if (StringUtils.isEmpty(postData.getCustomerBrandCode())) {
				missingParameters.add("Customer Brand Code");
			}
			if (StringUtils.isEmpty(postData
					.getCustomerCategoryCode())) {
				missingParameters.add("Customer Category Code");
			}
			if (StringUtils.isEmpty(postData.getSellerCode())) {
				missingParameters.add("Seller Code");
			}
			if (StringUtils.isEmpty(postData.getCurrencyCode())) {
				missingParameters.add("Currency Code");
			}
			if (StringUtils.isEmpty(postData.getCountryCode())) {
				missingParameters.add("Country Code");
			}
			if (StringUtils.isEmpty(postData.getLastName())) {
				missingParameters.add("Last Name");
			}
			if (StringUtils.isEmpty(postData.getLanguageCode())) {
				missingParameters.add("Language Code");
			}
			if (StringUtils.isEmpty(postData.getBillingCycleCode())) {
				missingParameters.add("Billing Cycle Code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public List<AccountHierarchyDto> find(AccountHierarchyDto customerDto,
			User currentUser) throws MeveoApiException {
		List<AccountHierarchyDto> result = new ArrayList<AccountHierarchyDto>();
		Customer customerFilter;

		customerFilter = customerUtil.getCustomer(customerDto,
				currentUser.getProvider());

		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(
				customerDto.getIndex(), customerDto.getLimit(), null, null,
				customerDto.getSortField(), null);

		List<Customer> customers = customerService.findByValues(em,
				customerFilter, paginationConfiguration);
		for (Customer customer : customers) {
			result.add(new AccountHierarchyDto(customer));
		}

		return result;
	}
}
