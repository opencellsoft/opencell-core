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
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.account.AccountHierarchyDto;
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
import org.slf4j.Logger;

@Stateless
public class AccountHierarchyApi extends BaseApi {

	@Inject
	private Logger log;

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

	public void createAccountHierarchy(
			AccountHierarchyDto customerHeirarchyDto, User currentUser)
			throws MeveoApiException {

		Provider provider = currentUser.getProvider();

		if (customerService.findByCode(em,
				customerHeirarchyDto.getCustomerId(), provider) != null) {
			throw new EntityAlreadyExistsException(Customer.class,
					customerHeirarchyDto.getCustomerId());
		} else {

			if (!StringUtils.isEmpty(customerHeirarchyDto.getCustomerId())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getCustomerBrandCode())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getCustomerCategoryCode())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getSellerCode())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getCurrencyCode())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getCountryCode())
					&& !StringUtils.isEmpty(customerHeirarchyDto.getLastName())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getLanguageCode())
					&& !StringUtils.isEmpty(customerHeirarchyDto
							.getBillingCycleCode())) {

				log.info("Creating Customer Heirarchy...");

				Seller seller = sellerService.findByCode(em,
						customerHeirarchyDto.getSellerCode(), provider);

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				TradingCountry tradingCountry = tradingCountryService
						.findByTradingCountryCode(em,
								customerHeirarchyDto.getCountryCode(), provider);

				if (tradingCountry == null) {
					Country country = countryService.findByCode(em,
							customerHeirarchyDto.getCountryCode());

					if (country == null) {
						throw new EntityDoesNotExistsException(Country.class,
								customerHeirarchyDto.getCountryCode());
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
								customerHeirarchyDto.getCurrencyCode(),
								provider);

				if (tradingCurrency == null) {
					Currency currency = currencyService.findByCode(em,
							customerHeirarchyDto.getCurrencyCode());

					if (currency == null) {
						throw new EntityDoesNotExistsException(Currency.class,
								customerHeirarchyDto.getCurrencyCode());
					} else {
						// create tradingCountry
						tradingCurrency = new TradingCurrency();
						tradingCurrency.setCurrencyCode(customerHeirarchyDto
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
								customerHeirarchyDto.getLanguageCode(),
								provider);

				if (tradingLanguage == null) {
					Language language = languageService.findByCode(em,
							customerHeirarchyDto.getLanguageCode());

					if (language == null) {
						throw new EntityDoesNotExistsException(Language.class,
								customerHeirarchyDto.getLanguageCode());
					} else {
						// create tradingCountry
						tradingLanguage = new TradingLanguage();
						tradingLanguage.setLanguageCode(customerHeirarchyDto
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
						em, customerHeirarchyDto.getCustomerBrandCode());

				if (StringUtils.isEmpty(customerHeirarchyDto
						.getCustomerCategoryCode())) {
					throw new MissingParameterException(
							"CustomerCategorCode is required.");
				}

				CustomerCategory customerCategory = customerCategoryService
						.findByCode(em,
								customerHeirarchyDto.getCustomerCategoryCode());

				if (customerBrand == null) {
					customerBrand = new CustomerBrand();
					customerBrand.setCode(enleverAccent(customerHeirarchyDto
							.getCustomerBrandCode()));
					customerBrand.setDescription(customerHeirarchyDto
							.getCustomerBrandCode());
					customerBrandService.create(em, customerBrand, currentUser,
							provider);
				}

				if (customerCategory == null) {
					customerCategory = new CustomerCategory();
					customerCategory.setCode(enleverAccent(customerHeirarchyDto
							.getCustomerCategoryCode()));
					customerCategory.setDescription(customerHeirarchyDto
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
					seller.setCode(enleverAccent(customerHeirarchyDto
							.getSellerCode()));
					seller.setAuditable(auditable);
					seller.setProvider(provider);
					seller.setTradingCountry(tradingCountry);
					seller.setTradingCurrency(tradingCurrency);

					sellerService.create(em, seller, currentUser, provider);
				}

				Address address = new Address();
				address.setAddress1(customerHeirarchyDto.getAddress1());
				address.setAddress2(customerHeirarchyDto.getAddress2());
				address.setZipCode(customerHeirarchyDto.getZipCode());

				ContactInformation contactInformation = new ContactInformation();
				contactInformation.setEmail(customerHeirarchyDto.getEmail());
				contactInformation.setPhone(customerHeirarchyDto
						.getPhoneNumber());

				Title title = titleService.findByCode(em, provider,
						enleverAccent(customerHeirarchyDto.getTitleCode()));

				Customer customer = new Customer();
				customer.getName().setLastName(
						customerHeirarchyDto.getLastName());
				customer.getName().setFirstName(
						customerHeirarchyDto.getFirstName());
				customer.getName().setTitle(title);
				customer.setContactInformation(contactInformation);
				customer.setAddress(address);
				customer.setCode(enleverAccent(customerHeirarchyDto
						.getCustomerId()));
				customer.setCustomerBrand(customerBrand);
				customer.setCustomerCategory(customerCategory);
				customer.setSeller(seller);

				customerService.create(em, customer, currentUser, provider);

				CustomerAccount customerAccount = new CustomerAccount();
				customerAccount.setCustomer(customer);
				customerAccount.setAddress(address);
				customerAccount.setContactInformation(contactInformation);
				customerAccount.getName().setFirstName(
						customerHeirarchyDto.getFirstName());
				customerAccount.getName().setLastName(
						customerHeirarchyDto.getLastName());
				customerAccount.getName().setTitle(title);
				customerAccount.setCode(enleverAccent(customerHeirarchyDto
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
								enleverAccent(customerHeirarchyDto
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
				billingAccount.setCode(enleverAccent(customerHeirarchyDto
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

				String userAccountCode = enleverAccent(customerHeirarchyDto
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
				StringBuilder sb = new StringBuilder(
						"Missing value for the following parameters ");
				List<String> missingFields = new ArrayList<String>();

				if (StringUtils.isEmpty(customerHeirarchyDto.getCustomerId())) {
					missingFields.add("Customer ID");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto
						.getCustomerBrandCode())) {
					missingFields.add("Customer Brand Code");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto
						.getCustomerCategoryCode())) {
					missingFields.add("Customer Category Code");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto.getSellerCode())) {
					missingFields.add("Seller Code");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto.getCurrencyCode())) {
					missingFields.add("Currency Code");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto.getCountryCode())) {
					missingFields.add("Country Code");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto.getLastName())) {
					missingFields.add("Last Name");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto.getLanguageCode())) {
					missingFields.add("Language Code");
				}
				if (StringUtils.isEmpty(customerHeirarchyDto
						.getBillingCycleCode())) {
					missingFields.add("Billing Cycle Code");
				}
				if (missingFields.size() > 1) {
					sb.append(org.apache.commons.lang.StringUtils.join(
							missingFields.toArray(), ", "));
				} else {
					sb.append(missingFields.get(0));
				}
				sb.append(".");

				throw new MissingParameterException(sb.toString());
			}
		}
	}

	public void updateCustomerHeirarchy(
			AccountHierarchyDto customerHeirarchyDto, User currentUser)
			throws MeveoApiException {

		log.info("Updating Customer Heirarchy with code : "
				+ customerHeirarchyDto.getCustomerId());

		Provider provider = currentUser.getProvider();

		Customer customer = customerService.findByCode(em,
				customerHeirarchyDto.getCustomerId(), provider);

		if (customer == null) {
			throw new EntityAlreadyExistsException(Customer.class,
					customerHeirarchyDto.getCustomerId());
		}

		if (!StringUtils.isEmpty(customerHeirarchyDto.getCustomerId())
				&& !StringUtils.isEmpty(customerHeirarchyDto
						.getCustomerBrandCode())
				&& !StringUtils.isEmpty(customerHeirarchyDto
						.getCustomerCategoryCode())
				&& !StringUtils.isEmpty(customerHeirarchyDto.getSellerCode())
				&& !StringUtils.isEmpty(customerHeirarchyDto.getCurrencyCode())
				&& !StringUtils.isEmpty(customerHeirarchyDto.getCountryCode())
				&& !StringUtils.isEmpty(customerHeirarchyDto.getLastName())
				&& !StringUtils.isEmpty(customerHeirarchyDto.getLanguageCode())
				&& !StringUtils.isEmpty(customerHeirarchyDto
						.getBillingCycleCode())) {

			Seller seller = sellerService.findByCode(em,
					customerHeirarchyDto.getSellerCode(), provider);

			Auditable auditableTrading = new Auditable();
			auditableTrading.setCreated(new Date());
			auditableTrading.setCreator(currentUser);

			Country country = countryService.findByCode(em,
					customerHeirarchyDto.getCountryCode());

			if (country == null) {
				throw new EntityDoesNotExistsException(Country.class,
						customerHeirarchyDto.getCountryCode());
			}

			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(em,
							customerHeirarchyDto.getCountryCode(), provider);

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
					customerHeirarchyDto.getCurrencyCode());

			if (currency == null) {
				throw new EntityDoesNotExistsException(Currency.class,
						customerHeirarchyDto.getCurrencyCode());
			}

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(em,
							customerHeirarchyDto.getCurrencyCode(), provider);

			if (tradingCurrency == null) {
				// create tradingCountry
				tradingCurrency = new TradingCurrency();
				tradingCurrency.setAuditable(auditableTrading);
			}

			tradingCurrency.setCurrencyCode(customerHeirarchyDto
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
					customerHeirarchyDto.getLanguageCode());

			if (language == null) {
				throw new EntityDoesNotExistsException(Language.class,
						customerHeirarchyDto.getLanguageCode());
			}

			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(em,
							customerHeirarchyDto.getLanguageCode(), provider);

			if (tradingLanguage == null) {
				tradingLanguage = new TradingLanguage();
				tradingLanguage.setAuditable(auditableTrading);
			}

			tradingLanguage.setLanguageCode(customerHeirarchyDto
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
					customerHeirarchyDto.getCustomerBrandCode());

			CustomerCategory customerCategory = customerCategoryService
					.findByCode(em,
							customerHeirarchyDto.getCustomerCategoryCode());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
			}

			customerBrand.setCode(enleverAccent(customerHeirarchyDto
					.getCustomerBrandCode()));
			customerBrand.setDescription(customerHeirarchyDto
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

			customerCategory.setCode(enleverAccent(customerHeirarchyDto
					.getCustomerCategoryCode()));
			customerCategory.setDescription(customerHeirarchyDto
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
				seller.setCode(customerHeirarchyDto.getSellerCode());
				seller.setProvider(provider);
				seller.setTradingCountry(tradingCountry);
				seller.setTradingCurrency(tradingCurrency);
				sellerService.create(em, seller, currentUser, provider);
			}

			Address address = new Address();
			address.setAddress1(customerHeirarchyDto.getAddress1());
			address.setAddress2(customerHeirarchyDto.getAddress2());
			address.setZipCode(customerHeirarchyDto.getZipCode());

			ContactInformation contactInformation = new ContactInformation();
			contactInformation.setEmail(customerHeirarchyDto.getEmail());
			contactInformation.setPhone(customerHeirarchyDto.getPhoneNumber());

			Title title = titleService.findByCode(em, provider,
					customerHeirarchyDto.getTitleCode());

			customer.getName().setLastName(customerHeirarchyDto.getLastName());
			customer.getName()
					.setFirstName(customerHeirarchyDto.getFirstName());
			customer.getName().setTitle(title);
			customer.setAddress(address);
			customer.setCode(enleverAccent(customerHeirarchyDto.getCustomerId()));
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);
			customer.setContactInformation(contactInformation);
			customer.setSeller(seller);

			customerService.update(em, customer, currentUser);

			CustomerAccount customerAccount = customerAccountService
					.findByCode(em, customerHeirarchyDto.getCustomerId(),
							provider);
			if (customerAccount == null) {
				customerAccount = new CustomerAccount();
			}
			customerAccount.setCustomer(customer);

			customerAccount.setAddress(address);
			customerAccount.setContactInformation(contactInformation);

			customerAccount.getName().setFirstName(
					customerHeirarchyDto.getFirstName());
			customerAccount.getName().setLastName(
					customerHeirarchyDto.getLastName());
			customerAccount.getName().setTitle(title);
			customerAccount.setCode(enleverAccent(customerHeirarchyDto
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
							customerHeirarchyDto.getBillingCycleCode(),
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
					em, customerHeirarchyDto.getCustomerId(), provider);
			if (billingAccount == null) {
				billingAccount = new BillingAccount();
			}
			billingAccount.setCode(enleverAccent(customerHeirarchyDto
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
			if (billingAccount.isTransient()) {
				billingAccountService.createBillingAccount(em, billingAccount,
						currentUser, provider);
			} else {
				billingAccountService.updateBillingAccount(em, billingAccount,
						currentUser);
			}

			UserAccount userAccount = userAccountService.findByCode(em,
					customerHeirarchyDto.getCustomerId(), provider);
			if (userAccount == null) {
				userAccount = new UserAccount();
			}
			String userAccountCode = enleverAccent(customerHeirarchyDto
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
					throw new MeveoApiException(
							MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
				}
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"Missing value for the following parameters ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isEmpty(customerHeirarchyDto.getCustomerId())) {
				missingFields.add("Customer ID");
			}
			if (StringUtils
					.isEmpty(customerHeirarchyDto.getCustomerBrandCode())) {
				missingFields.add("Customer Brand Code");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto
					.getCustomerCategoryCode())) {
				missingFields.add("Customer Category Code");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto.getSellerCode())) {
				missingFields.add("Seller Code");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto.getCurrencyCode())) {
				missingFields.add("Currency Code");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto.getCountryCode())) {
				missingFields.add("Country Code");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto.getLastName())) {
				missingFields.add("Last Name");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto.getLanguageCode())) {
				missingFields.add("Language Code");
			}
			if (StringUtils.isEmpty(customerHeirarchyDto.getBillingCycleCode())) {
				missingFields.add("Billing Cycle Code");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
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
			result.add(customerUtil.getCustomerDTO(customer));
		}

		return null;
	}
}
