package org.meveo.api.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.AccountHierarchyDto;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.account.CRMAccountHierarchyDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.account.CustomerAccountsDto;
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
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.Auditable;
import org.meveo.model.CustomFieldEntity;
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
import org.meveo.model.crm.AccountHierarchyTypeEnum;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
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
import org.meveo.service.crm.impl.AccountModelScriptService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.util.MeveoParamBean;

/**
 * 
 * Creates the customer hierarchy including : - Trading Country - Trading
 * Currency - Trading Language - Customer Brand - Customer Category - Seller -
 * Customer - Customer Account - Billing Account - User Account
 * 
 * Required Parameters :customerId, customerCategoryCode, sellerCode
 * ,currencyCode,countryCode,lastname if title provided,
 * languageCode,billingCycleCode
 * 
 */

@Stateless
public class AccountHierarchyApi extends BaseApi {

	@Inject
	private AccountModelScriptService accountModelScriptService;

	@Inject
	private CustomFieldTemplateService customFieldTemplateService;

	@Inject
	private CustomerApi customerApi;

	@Inject
	private CustomerAccountApi customerAccountApi;

	@Inject
	private BillingAccountApi billingAccountApi;

	@Inject
	private UserAccountApi userAccountApi;

	@Inject
	private SellerApi sellerApi;

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
	protected CustomFieldInstanceService customFieldInstanceService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

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

	/**
	 * 
	 * Creates the customer heirarchy including : - Trading Country - Trading
	 * Currency - Trading Language - Customer Brand - Customer Category - Seller
	 * - Customer - Customer Account - Billing Account - User Account
	 * 
	 * Required Parameters :customerId, customerCategoryCode, sellerCode
	 * ,currencyCode,countryCode,lastName if title
	 * provided,languageCode,billingCycleCode
	 * 
	 * @throws BusinessException
	 */
	public void create(AccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {
		Provider provider = currentUser.getProvider();
		
		if (StringUtils.isBlank(postData.getCustomerId()) && StringUtils.isBlank(postData.getCustomerCode())) {
			missingParameters.add("customerCode");
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
		if (!StringUtils.isBlank(postData.getTitleCode()) && StringUtils.isBlank(postData.getLastName())) {
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

		handleMissingParameters();
		
		String customerCodeOrId = null;
		if (!StringUtils.isBlank(postData.getCustomerId())) {
			customerCodeOrId = postData.getCustomerId();
		}
		if (!StringUtils.isBlank(postData.getCustomerCode())) {
			customerCodeOrId = postData.getCustomerCode();
		}

		if (customerService.findByCode(CUSTOMER_PREFIX + customerCodeOrId, provider) != null) {
			throw new EntityAlreadyExistsException(Customer.class, customerCodeOrId);
		}

		Seller seller = sellerService.findByCode(postData.getSellerCode(), provider);

		Auditable auditableTrading = new Auditable();
		auditableTrading.setCreated(new Date());
		auditableTrading.setCreator(currentUser);

		TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), provider);

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
				tradingCountryService.create(tradingCountry, currentUser);
			}
		}

		TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode(), provider);
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
				tradingCurrencyService.create(tradingCurrency, currentUser);
			}
		}

		TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguageCode(), provider);
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
				tradingLanguageService.create(tradingLanguage, currentUser);
			}
		}

		CustomerBrand customerBrand = null;
		if (!StringUtils.isBlank(postData.getCustomerBrandCode())) {
			customerBrand = customerBrandService.findByCode(postData.getCustomerBrandCode(), currentUser.getProvider());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
				customerBrand.setCode(StringUtils.normalizeHierarchyCode(postData.getCustomerBrandCode()));
				customerBrand.setDescription(postData.getCustomerBrandCode());
				customerBrandService.create(customerBrand, currentUser);
			}
		}

		CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategoryCode(), currentUser.getProvider());

		if (customerCategory == null) {
			customerCategory = new CustomerCategory();
			customerCategory.setCode(StringUtils.normalizeHierarchyCode(postData.getCustomerCategoryCode()));
			customerCategory.setDescription(postData.getCustomerCategoryCode());
			customerCategoryService.create(customerCategory, currentUser);
		}

		int caPaymentMethod = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.paymentMethod", "1"));
		String creditCategory = paramBean.getProperty("api.default.customerAccount.creditCategory", "NEWCUSTOMER");
		int baPaymentMethod = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.paymentMethod", "1"));

		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(currentUser);

		if (seller == null) {
			seller = new Seller();
			seller.setActive(true);
			seller.setCode(StringUtils.normalizeHierarchyCode(postData.getSellerCode()));
			seller.setAuditable(auditable);
			seller.setProvider(provider);
			seller.setTradingCountry(tradingCountry);
			seller.setTradingCurrency(tradingCurrency);

			sellerService.create(seller, currentUser);
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
			title = titleService.findByCode(StringUtils.normalizeHierarchyCode(postData.getTitleCode()), provider);
		}

		String customerCode = CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
		Customer customer = customerService.findByCode(customerCode, provider);
		if (customer != null) {
			throw new EntityAlreadyExistsException(Customer.class, customerCode);
		}
		
		Name name = new Name();
		name.setTitle(title);
		name.setFirstName(postData.getFirstName());
		name.setLastName(postData.getLastName());

		customer = new Customer();
		customer.setName(name);
		customer.setContactInformation(contactInformation);
		customer.setAddress(address);
		customer.setCode(CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId));
		customer.setCustomerBrand(customerBrand);
		customer.setCustomerCategory(customerCategory);
		customer.setSeller(seller);
		customerService.create(customer, currentUser);

		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setCustomer(customer);
		customerAccount.setAddress(address);
		customerAccount.setContactInformation(contactInformation);
		customerAccount.setName(name);
		customerAccount.setCode(CUSTOMER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId));
		customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
		customerAccount.setPaymentMethod(PaymentMethodEnum.getValue(caPaymentMethod));
		if (!StringUtils.isBlank(creditCategory)) {
			customerAccount.setCreditCategory(creditCategoryService.findByCode(creditCategory, provider));
		}
		customerAccount.setTradingCurrency(tradingCurrency);
		customerAccount.setTradingLanguage(tradingLanguage);
		customerAccount.setDateDunningLevel(new Date());

		customerAccountService.create(customerAccount, currentUser);

		String billingCycleCode = StringUtils.normalizeHierarchyCode(postData.getBillingCycleCode());
		BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, currentUser, provider);
		if (billingCycle == null) {
			throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
		}

		BillingAccount billingAccount = new BillingAccount();
		billingAccount.setName(name);
		billingAccount.setEmail(postData.getEmail());
		billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(postData.getPaymentMethod()));
		billingAccount.setCode(BILLING_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId));
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setCustomerAccount(customerAccount);
		billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(baPaymentMethod));
		billingAccount.setElectronicBilling(Boolean.valueOf(paramBean.getProperty("api.customerHeirarchy.billingAccount.electronicBilling", "true")));
		billingAccount.setTradingCountry(tradingCountry);
		billingAccount.setTradingLanguage(tradingLanguage);
		billingAccount.setBillingCycle(billingCycle);
		billingAccount.setProvider(provider);
		billingAccount.setAddress(address);

		billingAccountService.createBillingAccount(billingAccount, currentUser);

		String userAccountCode = USER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
		UserAccount userAccount = new UserAccount();
		userAccount.setName(name);
		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setBillingAccount(billingAccount);
		userAccount.setCode(userAccountCode);
		userAccount.setAddress(address);
		try {
			userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
		} catch (AccountAlreadyExistsException e) {
			throw new EntityAlreadyExistsException(UserAccount.class, userAccountCode);
		}
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void update(AccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {

		Provider provider = currentUser.getProvider();

		if (StringUtils.isBlank(postData.getCustomerId()) && StringUtils.isBlank(postData.getCustomerCode())) {
			missingParameters.add("customerCode");
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
		if (!StringUtils.isBlank(postData.getTitleCode()) && StringUtils.isBlank(postData.getLastName())) {
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

		handleMissingParameters();
		
		String customerCodeOrId = null;
		if (!StringUtils.isBlank(postData.getCustomerId())) {
			customerCodeOrId = postData.getCustomerId();
		}
		if (!StringUtils.isBlank(postData.getCustomerCode())) {
			customerCodeOrId = postData.getCustomerCode();
		}		

		String customerCode = CUSTOMER_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
		Customer customer = customerService.findByCode(customerCode, provider);

		if (customer == null) {
			throw new EntityDoesNotExistsException(Customer.class, customerCodeOrId);
		}

		Seller seller = sellerService.findByCode(postData.getSellerCode(), provider);

		Country country = countryService.findByCode(postData.getCountryCode());

		if (country == null) {
			throw new EntityDoesNotExistsException(Country.class, postData.getCountryCode());
		}

		TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), provider);

        if (tradingCountry == null) {
            tradingCountry = new TradingCountry();
            tradingCountry.setCountry(country);
            tradingCountry.setProvider(provider);
            tradingCountry.setActive(true);
            tradingCountry.setPrDescription(country.getDescriptionEn());

            tradingCountryService.create(tradingCountry, currentUser);
        }

		Currency currency = currencyService.findByCode(postData.getCurrencyCode());

		if (currency == null) {
			throw new EntityDoesNotExistsException(Currency.class, postData.getCurrencyCode());
		}

		TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCurrencyCode(), provider);

        if (tradingCurrency == null) {
            // create tradingCountry
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setProvider(provider);
            tradingCurrency.setActive(true);
            tradingCurrency.setPrDescription(currency.getDescriptionEn());

            tradingCurrencyService.create(tradingCurrency, currentUser);
        }

		Language language = languageService.findByCode(postData.getLanguageCode());

		if (language == null) {
			throw new EntityDoesNotExistsException(Language.class, postData.getLanguageCode());
		}

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguageCode(), provider);

        if (tradingLanguage == null) {
            tradingLanguage = new TradingLanguage();
            tradingLanguage.setLanguageCode(postData.getLanguageCode());
            tradingLanguage.setLanguage(language);
            tradingLanguage.setProvider(provider);
            tradingLanguage.setActive(true);
            tradingLanguage.setPrDescription(language.getDescriptionEn());

            tradingLanguageService.create(tradingLanguage, currentUser);
        }

		CustomerBrand customerBrand = null;
		if (!StringUtils.isBlank(postData.getCustomerBrandCode())) {
			customerBrand = customerBrandService.findByCode(postData.getCustomerBrandCode(), currentUser.getProvider());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
			    customerBrand.setCode(StringUtils.normalizeHierarchyCode(postData.getCustomerBrandCode()));
			    customerBrand.setDescription(postData.getCustomerBrandCode());	            
			    customerBrandService.create(customerBrand, currentUser);
			}
		}

		CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategoryCode(), currentUser.getProvider());
		if (customerCategory == null) {
			customerCategory = new CustomerCategory();
			customerCategory.setCode(StringUtils.normalizeHierarchyCode(postData.getCustomerCategoryCode()));
	        customerCategory.setDescription(postData.getCustomerCategoryCode());
	        customerCategoryService.create(customerCategory, currentUser);
		} 

		int caPaymentMethod = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.paymentMethod", "1"));
		String creditCategory = paramBean.getProperty("api.default.customerAccount.creditCategory", "NEWCUSTOMER");

		int baPaymentMethod = Integer.parseInt(paramBean.getProperty("api.default.customerAccount.paymentMethod", "1"));

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
			sellerService.create(seller, currentUser);
		}

		Address address = new Address();
		address.setAddress1(postData.getAddress1());
		address.setAddress2(postData.getAddress2());
		address.setAddress3(postData.getAddress3());
		address.setZipCode(postData.getZipCode());
		address.setCity(postData.getCity());
		address.setCountry(postData.getCountryCode());
		
		ContactInformation contactInformation = new ContactInformation();
		contactInformation.setEmail(postData.getEmail());
		contactInformation.setPhone(postData.getPhoneNumber());

		Title title = null;
		if (!StringUtils.isBlank(postData.getTitleCode())) {
			title = titleService.findByCode(StringUtils.normalizeHierarchyCode(postData.getTitleCode()), provider);
		}
		
		Name name = new Name();
		name.setTitle(title);
		name.setFirstName(postData.getFirstName());
		name.setLastName(postData.getLastName());

		customer.setName(name);
		customer.setAddress(address);
		customer.setCustomerBrand(customerBrand);
		customer.setCustomerCategory(customerCategory);
		customer.setContactInformation(contactInformation);
		customer.setSeller(seller);

		customerService.update(customer, currentUser);

		CustomerAccount customerAccount = customerAccountService.findByCode(
				CUSTOMER_ACCOUNT_PREFIX + customerCodeOrId, provider);
		if (customerAccount == null) {
			customerAccount = new CustomerAccount();
			customerAccount.setCode(CUSTOMER_ACCOUNT_PREFIX
					+ StringUtils.normalizeHierarchyCode(customerCodeOrId));
		}
		customerAccount.setCustomer(customer);

		customerAccount.setAddress(address);
		customerAccount.setContactInformation(contactInformation);

		customerAccount.setName(name);
		customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
		customerAccount.setPaymentMethod(PaymentMethodEnum.getValue(caPaymentMethod));
		if (!StringUtils.isBlank(creditCategory)) {
			customerAccount.setCreditCategory(creditCategoryService.findByCode(creditCategory, provider));
		}
		customerAccount.setTradingCurrency(tradingCurrency);
		customerAccount.setTradingLanguage(tradingLanguage);

		if (customerAccount.isTransient()) {
			customerAccountService.create(customerAccount, currentUser);
		} else {
			customerAccountService.update(customerAccount, currentUser);
		}

		String billingCycleCode = StringUtils.normalizeHierarchyCode(postData.getBillingCycleCode());
		BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, currentUser, provider);
		if (billingCycle == null) {
			throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
		}

		BillingAccount billingAccount = billingAccountService.findByCode(
				BILLING_ACCOUNT_PREFIX + customerCodeOrId, provider);
		if (billingAccount == null) {
			billingAccount = new BillingAccount();
			billingAccount.setCode(BILLING_ACCOUNT_PREFIX
					+ StringUtils.normalizeHierarchyCode(customerCodeOrId));
		}

		billingAccount.setEmail(postData.getEmail());
		billingAccount.setName(name);
		billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(postData.getPaymentMethod()));
		billingAccount.setStatus(AccountStatusEnum.ACTIVE);
		billingAccount.setCustomerAccount(customerAccount);
		billingAccount.setPaymentMethod(PaymentMethodEnum.getValue(baPaymentMethod));
		billingAccount.setElectronicBilling(Boolean.valueOf(paramBean.getProperty("api.customerHeirarchy.billingAccount.electronicBilling", "true")));
		billingAccount.setTradingCountry(tradingCountry);
		billingAccount.setTradingLanguage(tradingLanguage);
		billingAccount.setBillingCycle(billingCycle);
		billingAccount.setAddress(address);

		if (billingAccount.isTransient()) {
			billingAccountService.createBillingAccount(billingAccount, currentUser);
		} else {
			billingAccountService.update(billingAccount, currentUser);
		}

		UserAccount userAccount = userAccountService.findByCode(USER_ACCOUNT_PREFIX + customerCodeOrId,
				provider);
		if (userAccount == null) {
			userAccount = new UserAccount();
		}

		userAccount.setName(name);
		userAccount.setStatus(AccountStatusEnum.ACTIVE);
		userAccount.setBillingAccount(billingAccount);
		userAccount.setAddress(address);

		if (userAccount.isTransient()) {
			String userAccountCode = USER_ACCOUNT_PREFIX + StringUtils.normalizeHierarchyCode(customerCodeOrId);
			try {
				userAccount.setCode(userAccountCode);
				userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
			} catch (AccountAlreadyExistsException e) {
				throw new EntityAlreadyExistsException(UserAccount.class, userAccountCode);
			}
		} else {
			userAccountService.update(userAccount, currentUser);
		}
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @return
	 * @throws MeveoApiException
	 */
	public CustomersDto find(AccountHierarchyDto postData, User currentUser) throws MeveoApiException {

		CustomersDto result = new CustomersDto();

		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(postData.getIndex(), postData.getLimit(), null, null,
				postData.getSortField(), null);
		QueryBuilder qb = new QueryBuilder(Customer.class, "c", null, currentUser.getProvider());

		String customerCodeOrId = null;
		if (!StringUtils.isBlank(postData.getCustomerCode())) {
			customerCodeOrId = CUSTOMER_PREFIX + postData.getCustomerCode();
		}
		if (!StringUtils.isBlank(postData.getCustomerId())) {
			customerCodeOrId = CUSTOMER_PREFIX + postData.getCustomerId();
		}

		if (!StringUtils.isBlank(customerCodeOrId)) {
			qb.addCriterion("c.code", "=", customerCodeOrId, true);
		}
		if (!StringUtils.isBlank(postData.getSellerCode())) {
			Seller seller = sellerService.findByCode(postData.getSellerCode(), currentUser.getProvider());
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, postData.getSellerCode());
			}
			qb.addCriterionEntity("c.seller", seller);
		}
		if (!StringUtils.isBlank(postData.getCustomerBrandCode())) {
			CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrandCode(), currentUser.getProvider());
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrandCode());
			}
			qb.addCriterionEntity("c.customerBrand", customerBrand);
		}
		if (!StringUtils.isBlank(postData.getCustomerCategoryCode())) {
			CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategoryCode(), currentUser.getProvider());
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategoryCode());
			}
			qb.addCriterionEntity("c.customerCategory", customerCategory);
		}
		if (!StringUtils.isBlank(postData.getCountryCode())) {
			TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountryCode(), currentUser.getProvider());
			if (tradingCountry == null) {
				throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountryCode());
			}
			qb.addCriterion("c.address.country", "=", postData.getCountryCode(), true);
		}
		if (!StringUtils.isBlank(postData.getFirstName())) {
			qb.addCriterion("c.name.firstName", "=", postData.getFirstName(), true);
		}
		if (!StringUtils.isBlank(postData.getLastName())) {
			qb.addCriterion("c.name.lastName", "=", postData.getLastName(), true);
		}
		if (!StringUtils.isBlank(postData.getAddress1())) {
			qb.addCriterion("c.address.address1", "=", postData.getAddress1(), true);
		}
		if (!StringUtils.isBlank(postData.getAddress2())) {
			qb.addCriterion("c.address.address2", "=", postData.getAddress2(), true);
		}
		if (!StringUtils.isBlank(postData.getAddress3())) {
			qb.addCriterion("c.address.address3", "=", postData.getAddress3(), true);
		}
		if (!StringUtils.isBlank(postData.getCity())) {
			qb.addCriterion("c.address.city", "=", postData.getCity(), true);
		}
		if (!StringUtils.isBlank(postData.getState())) {
			qb.addCriterion("c.address.state", "=", postData.getState(), true);
		}
		if (!StringUtils.isBlank(postData.getZipCode())) {
			qb.addCriterion("c.address.zipCode", "=", postData.getZipCode(), true);
		}

		// custom fields
		if (postData.getCustomFields() != null) {
			for (@SuppressWarnings("unused")
			CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
				// qb.addCriterion("KEY(c.customFields)", "=", cfDto.getCode(),
				// true); // TODO FIX me - custom fields are no longer tied to
				// entity
			}
		}

		qb.addPaginationConfiguration(paginationConfiguration);
		@SuppressWarnings("unchecked")
		List<Customer> customers = qb.getQuery(customerService.getEntityManager()).getResultList();

		if (customers != null) {
			for (Customer cust : customers) {
				if (postData.getCustomFields() == null || postData.getCustomFields().getCustomField() == null) {
					result.getCustomer().add(customerToDto(cust));
				} else {
					for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {

						if (!cfDto.isEmpty()) {
							Object cfValue = customFieldInstanceService.getCFValue(cust, cfDto.getCode(), currentUser);
							if (getValueConverted(cfDto).equals(cfValue)) {
								result.getCustomer().add(customerToDto(cust));
							}
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void customerHierarchyUpdate(CustomerHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (postData.getSellers() == null || postData.getSellers().getSeller().isEmpty()) {
			missingParameters.add("sellers");
			handleMissingParameters();
		}

		for (SellerDto sellerDto : postData.getSellers().getSeller()) {
			if (StringUtils.isBlank(sellerDto.getCode())) {
				missingParameters.add("seller.code");
				handleMissingParameters();
			}
			Provider provider = currentUser.getProvider();

            sellerApi.createOrUpdate(sellerDto, currentUser);

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

						if (!StringUtils.isBlank(customerDto.getCustomerBrand())) {
							CustomerBrand customerBrand = customerBrandService.findByCode(customerDto.getCustomerBrand(), provider);
							customer.setCustomerBrand(customerBrand);
						} else {
							customer.setCustomerBrand(null);
						}

						if (!StringUtils.isBlank(customerDto.getCustomerCategory())) {
							CustomerCategory customerCategory = customerCategoryService.findByCode(customerDto.getCustomerCategory(), provider);
							if (customerCategory != null) {
								customer.setCustomerCategory(customerCategory);
							}
						} else {
							missingParameters.add("customer.customerCategory");
							handleMissingParameters();
						}

						customer.setMandateDate(customerDto.getMandateDate());
						customer.setMandateIdentification(customerDto.getMandateIdentification());

						customer.setProvider(provider);
					} else {
						if (!StringUtils.isBlank(customerDto.getCustomerBrand())) {
							CustomerBrand customerBrand = customerBrandService.findByCode(customerDto.getCustomerBrand(), provider);
							if (customerBrand != null) {
								customer.setCustomerBrand(customerBrand);
							}
						}

						if (!StringUtils.isBlank(customerDto.getCustomerCategory())) {
							CustomerCategory customerCategory = customerCategoryService.findByCode(customerDto.getCustomerCategory(), provider);
							if (customerCategory != null) {
								customer.setCustomerCategory(customerCategory);
							}
						}

						if (!StringUtils.isBlank(customerDto.getMandateDate())) {
							customer.setMandateDate(customerDto.getMandateDate());
						}
						if (!StringUtils.isBlank(customerDto.getMandateIdentification())) {
							customer.setMandateIdentification(customerDto.getMandateIdentification());
						}
					}

					customer.setSeller(sellerService.findByCode(sellerDto.getCode(), provider));

					if (customerDto.getContactInformation() != null) {
						if (!StringUtils.isBlank(customerDto.getContactInformation().getEmail())) {
							customer.getContactInformation().setEmail(customerDto.getContactInformation().getEmail());
						}
						if (!StringUtils.isBlank(customerDto.getContactInformation().getPhone())) {
							customer.getContactInformation().setPhone(customerDto.getContactInformation().getPhone());
						}
						if (!StringUtils.isBlank(customerDto.getContactInformation().getMobile())) {
							customer.getContactInformation().setMobile(customerDto.getContactInformation().getMobile());
						}
						if (!StringUtils.isBlank(customerDto.getContactInformation().getFax())) {
							customer.getContactInformation().setFax(customerDto.getContactInformation().getFax());
						}
					}

					populateNameAddress(customer, customerDto, currentUser);

					boolean isNewCustomer = customer.isTransient();
					if (isNewCustomer) {
						customerService.create(customer, currentUser);
					} else {
						customerService.update(customer, currentUser);
					}

					// Validate and populate customFields
					try {
						populateCustomFields(customerDto.getCustomFields(), customer, isNewCustomer, currentUser);

					} catch (IllegalArgumentException | IllegalAccessException e) {
						log.error("Failed to associate custom field instance to a customer {}", customerDto.getCode(), e);
						throw new MeveoApiException("Failed to associate custom field instance to a customer " + customerDto.getCode());
					}

					// customerAccounts
					if (customerDto.getCustomerAccounts() != null) {
						for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts().getCustomerAccount()) {
							if (StringUtils.isBlank(customerAccountDto.getCode())) {
								log.warn("code is null={}", customerAccountDto);
								continue;
							}

							CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountDto.getCode(), provider);
							if (customerAccount == null) {

								customerAccount = new CustomerAccount();
								customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
								customerAccount.setCode(customerAccountDto.getCode());

								if (!StringUtils.isBlank(customerAccountDto.getCurrency())) {
									TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(customerAccountDto.getCurrency(),
											provider);
									if (tradingCurrency == null) {
										throw new EntityDoesNotExistsException(TradingCurrency.class, customerAccountDto.getCurrency());
									}

									customerAccount.setTradingCurrency(tradingCurrency);
								} else {
									missingParameters.add("customerAccount.currency");
									handleMissingParameters();
								}

								if (!StringUtils.isBlank(customerAccountDto.getLanguage())) {
									TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(customerAccountDto.getLanguage(),
											provider);
									if (tradingLanguage == null) {
										throw new EntityDoesNotExistsException(TradingLanguage.class, customerAccountDto.getLanguage());
									}

									customerAccount.setTradingLanguage(tradingLanguage);
								} else {
									missingParameters.add("customerAccount.language");
									handleMissingParameters();
								}

								customerAccount.setDateStatus(customerAccountDto.getDateStatus());
								customerAccount.setDateDunningLevel(customerAccountDto.getDateDunningLevel());

								customerAccount.setMandateDate(customerAccountDto.getMandateDate());
								customerAccount.setMandateIdentification(customerAccountDto.getMandateIdentification());

								customerAccount.setProvider(provider);
							} else {
								if (customerAccountDto.getStatus() != null) {
									if (customerAccountDto.getStatus() == CustomerAccountStatusEnum.CLOSE) {
										try {
											customerAccountService.closeCustomerAccount(customerAccount, currentUser);
										} catch (Exception e) {
											throw new MeveoApiException(
													"Failed closing customerAccount with code=" + customerAccountDto.getCode() + ". " + e.getMessage());
										}
									}

								} else {
									if (!StringUtils.isBlank(customerAccountDto.getCurrency())) {
										TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(customerAccountDto.getCurrency(),
												provider);
										if (tradingCurrency == null) {
											throw new EntityDoesNotExistsException(TradingCurrency.class, customerAccountDto.getCurrency());
										}

										customerAccount.setTradingCurrency(tradingCurrency);
									}

									if (!StringUtils.isBlank(customerAccountDto.getLanguage())) {
										TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(customerAccountDto.getLanguage(),
												provider);
										if (tradingLanguage == null) {
											throw new EntityDoesNotExistsException(TradingLanguage.class, customerAccountDto.getLanguage());
										}

										customerAccount.setTradingLanguage(tradingLanguage);
									}

									if (!StringUtils.isBlank(customerAccountDto.getDateStatus())) {
										customerAccount.setDateStatus(customerAccountDto.getDateStatus());
									}
									if (!StringUtils.isBlank(customerAccountDto.getDateDunningLevel())) {
										customerAccount.setDateDunningLevel(customerAccountDto.getDateDunningLevel());
									}
									if (!StringUtils.isBlank(customerAccountDto.getMandateDate())) {
										customerAccount.setMandateDate(customerAccountDto.getMandateDate());
									}
									if (!StringUtils.isBlank(customerAccountDto.getMandateIdentification())) {
										customerAccount.setMandateIdentification(customerAccountDto.getMandateIdentification());
									}
								}
							}

							customerAccount.setCustomer(customer);

							if (customerAccountDto.getStatus() != null) {
								customerAccount.setStatus(customerAccountDto.getStatus());
							}
							if (customerAccountDto.getPaymentMethod() != null) {
								customerAccount.setPaymentMethod(customerAccountDto.getPaymentMethod());
							}
							if (!StringUtils.isBlank(customerAccountDto.getCreditCategory())) {
								customerAccount.setCreditCategory(creditCategoryService.findByCode(customerAccountDto.getCreditCategory(), provider));
							}
							if (customerAccountDto.getDunningLevel() != null) {
								customerAccount.setDunningLevel(customerAccountDto.getDunningLevel());
							}

							if (customerAccountDto.getContactInformation() != null) {
								if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getEmail())) {
									customerAccount.getContactInformation().setEmail(customerAccountDto.getContactInformation().getEmail());
								}
								if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getPhone())) {
									customerAccount.getContactInformation().setPhone(customerAccountDto.getContactInformation().getPhone());
								}
								if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getMobile())) {
									customerAccount.getContactInformation().setMobile(customerAccountDto.getContactInformation().getMobile());
								}
								if (!StringUtils.isBlank(customerAccountDto.getContactInformation().getFax())) {
									customerAccount.getContactInformation().setFax(customerAccountDto.getContactInformation().getFax());
								}
							}

							populateNameAddress(customerAccount, customerAccountDto, currentUser);

							boolean isNewCA = customerAccount.isTransient();
							if (isNewCA) {
								customerAccountService.create(customerAccount, currentUser);
							} else {
								customerAccountService.update(customerAccount, currentUser);
							}

							// Validate and populate customFields
							try {
								populateCustomFields(customerAccountDto.getCustomFields(), customerAccount, isNewCA, currentUser);

							} catch (IllegalArgumentException | IllegalAccessException e) {
								log.error("Failed to associate custom field instance to a customer account {}", customerAccountDto.getCode(), e);
								throw new MeveoApiException("Failed to associate custom field instance to a customer account " + customerAccountDto.getCode());
							}

							// billing accounts
							if (customerAccountDto.getBillingAccounts() != null) {
								for (BillingAccountDto billingAccountDto : customerAccountDto.getBillingAccounts().getBillingAccount()) {
									if (StringUtils.isBlank(billingAccountDto.getCode())) {
										log.warn("code is null={}", billingAccountDto);
										continue;
									}

									BillingAccount billingAccount = billingAccountService.findByCode(billingAccountDto.getCode(), provider);
									if (billingAccount == null) {

										billingAccount = new BillingAccount();
										billingAccount.setStatus(AccountStatusEnum.ACTIVE);
										billingAccount.setCode(billingAccountDto.getCode());

										if (!StringUtils.isBlank(billingAccountDto.getBillingCycle())) {
											BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingAccountDto.getBillingCycle(),
													provider);
											if (billingCycle != null) {
												billingAccount.setBillingCycle(billingCycle);
											} else {
												throw new EntityDoesNotExistsException(BillingCycle.class, billingAccountDto.getBillingCycle());
											}
										} else {
											missingParameters.add("billingAccount.billingCycle");
											handleMissingParameters();
										}

										if (!StringUtils.isBlank(billingAccountDto.getCountry())) {
											TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(billingAccountDto.getCountry(),
													provider);
											if (tradingCountry != null) {
												billingAccount.setTradingCountry(tradingCountry);
											}
										} else {
											missingParameters.add("billingAccount.country");
											handleMissingParameters();
										}

										if (!StringUtils.isBlank(billingAccountDto.getLanguage())) {
											TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(billingAccountDto.getLanguage(),
													provider);
											if (tradingLanguage != null) {
												billingAccount.setTradingLanguage(tradingLanguage);
											}
										} else {
											missingParameters.add("billingAccount.language");
											handleMissingParameters();
										}

										billingAccount.setProvider(provider);
									} else {
										if (billingAccountDto.getTerminationDate() != null) {
											if (StringUtils.isBlank(billingAccountDto.getTerminationReason())) {
												missingParameters.add("billingAccount.terminationReason");
												handleMissingParameters();
											}

											SubscriptionTerminationReason terminationReason = terminationReasonService
													.findByCode(billingAccountDto.getTerminationReason(), provider);
											if (terminationReason == null) {
												throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class,
														billingAccountDto.getTerminationReason());
											}

											try {
												billingAccountService.billingAccountTermination(billingAccount, billingAccountDto.getTerminationDate(),
														terminationReason, currentUser);
												continue;
											} catch (BusinessException e) {
												throw new MeveoApiException("Failed terminating billingAccount. " + e.getMessage());
											}
										} else {
											if (!StringUtils.isBlank(billingAccountDto.getBillingCycle())) {
												BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingAccountDto.getBillingCycle(),
														provider);
												if (billingCycle != null) {
													billingAccount.setBillingCycle(billingCycle);
												}
											}

											if (!StringUtils.isBlank(billingAccountDto.getCountry())) {
												TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(billingAccountDto.getCountry(),
														provider);
												if (tradingCountry != null) {
													billingAccount.setTradingCountry(tradingCountry);
												}
											}

											if (!StringUtils.isBlank(billingAccountDto.getLanguage())) {
												TradingLanguage tradingLanguage = tradingLanguageService
														.findByTradingLanguageCode(billingAccountDto.getLanguage(), provider);
												if (tradingLanguage != null) {
													billingAccount.setTradingLanguage(tradingLanguage);
												}
											}
										}
									}

									billingAccount.setCustomerAccount(customerAccount);

									if (billingAccountDto.getPaymentMethod() != null) {
										billingAccount.setPaymentMethod(billingAccountDto.getPaymentMethod());
									}
									if (billingAccountDto.getPaymentTerms() != null) {
										billingAccount.setPaymentTerm(billingAccountDto.getPaymentTerms());
									}

									if (!StringUtils.isBlank(billingAccountDto.getNextInvoiceDate())) {
										billingAccount.setNextInvoiceDate(billingAccountDto.getNextInvoiceDate());
									}
									if (!StringUtils.isBlank(billingAccountDto.getSubscriptionDate())) {
										billingAccount.setSubscriptionDate(billingAccountDto.getSubscriptionDate());
									}
									if (!StringUtils.isBlank(billingAccountDto.getTerminationDate())) {
										billingAccount.setTerminationDate(billingAccount.getTerminationDate());
									}
									if (!StringUtils.isBlank(billingAccountDto.getElectronicBilling())) {
										billingAccount.setElectronicBilling(billingAccountDto.getElectronicBilling());
									}
									if (!StringUtils.isBlank(billingAccountDto.getEmail())) {
										billingAccount.setEmail(billingAccountDto.getEmail());
									}

									populateNameAddress(billingAccount, billingAccountDto, currentUser);

									boolean isNewBA = billingAccount.isTransient();
									if (isNewBA) {
										billingAccountService.create(billingAccount, currentUser);
									} else {
										billingAccountService.update(billingAccount, currentUser);
									}

									// Validate and populate customFields
									try {
										populateCustomFields(billingAccountDto.getCustomFields(), billingAccount, isNewBA, currentUser);

									} catch (IllegalArgumentException | IllegalAccessException e) {
										log.error("Failed to associate custom field instance to a billing account {}", billingAccountDto.getCode(), e);
										throw new MeveoApiException(
												"Failed to associate custom field instance to a billing account " + billingAccountDto.getCode());
									}

									// user accounts
									if (billingAccountDto.getUserAccounts() != null) {
										for (UserAccountDto userAccountDto : billingAccountDto.getUserAccounts().getUserAccount()) {
											if (StringUtils.isBlank(userAccountDto.getCode())) {
												log.warn("code is null={}", userAccountDto);
												continue;
											}

											UserAccount userAccount = userAccountService.findByCode(userAccountDto.getCode(), provider);
											if (userAccount == null) {

												userAccount = new UserAccount();
												userAccount.setStatus(AccountStatusEnum.ACTIVE);
												userAccount.setCode(userAccountDto.getCode());
												userAccount.setProvider(provider);
											} else {
												if (userAccountDto.getTerminationDate() != null) {
													if (StringUtils.isBlank(userAccountDto.getTerminationReason())) {
														missingParameters.add("userAccount.terminationReason");
														handleMissingParameters();
													}

													SubscriptionTerminationReason terminationReason = terminationReasonService
															.findByCode(userAccountDto.getTerminationReason(), provider);
													if (terminationReason == null) {
														throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class,
																userAccountDto.getTerminationReason());
													}

													try {
														userAccountService.userAccountTermination(userAccount, userAccountDto.getTerminationDate(),
																terminationReason, currentUser);
														continue;
													} catch (BusinessException e) {
														throw new MeveoApiException("Failed terminating billingAccount. " + e.getMessage());
													}
												}
											}

											userAccount.setBillingAccount(billingAccount);

											if (userAccountDto.getStatus() != null) {
												userAccount.setStatus(userAccountDto.getStatus());
											}

											if (!StringUtils.isBlank(userAccountDto.getSubscriptionDate())) {
												userAccount.setSubscriptionDate(userAccountDto.getSubscriptionDate());
											}
											if (!StringUtils.isBlank(userAccountDto.getTerminationDate())) {
												userAccount.setTerminationDate(userAccountDto.getTerminationDate());
											}

											populateNameAddress(userAccount, userAccountDto, currentUser);

											boolean isNewUA = userAccount.isTransient();
											if (isNewUA) {
												try {
													userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
												} catch (AccountAlreadyExistsException e) {
													throw new MeveoApiException(e.getMessage());
												}
											} else {
												userAccountService.update(userAccount, currentUser);
											}

											// Validate and populate
											// customFields
											try {
												populateCustomFields(userAccountDto.getCustomFields(), userAccount, isNewUA, currentUser);

											} catch (IllegalArgumentException | IllegalAccessException e) {
												log.error("Failed to associate custom field instance to a user account {}", userAccountDto.getCode(), e);
												throw new MeveoApiException(
														"Failed to associate custom field instance to a user account " + userAccountDto.getCode());
											}

											// subscriptions
											if (userAccountDto.getSubscriptions() != null) {
												for (SubscriptionDto subscriptionDto : userAccountDto.getSubscriptions().getSubscription()) {
													if (StringUtils.isBlank(subscriptionDto.getCode())) {
														log.warn("code is null={}", subscriptionDto);
														continue;
													}

													Subscription subscription = subscriptionService.findByCode(subscriptionDto.getCode(), provider);
													if (subscription == null) {

														subscription = new Subscription();
														subscription.setCode(subscriptionDto.getCode());

														if (!StringUtils.isBlank(subscriptionDto.getOfferTemplate())) {
															OfferTemplate offerTemplate = offerTemplateService.findByCode(subscriptionDto.getOfferTemplate(),
																	provider);
															if (offerTemplate == null) {
																throw new EntityDoesNotExistsException(OfferTemplate.class, subscriptionDto.getOfferTemplate());
															}

															subscription.setOffer(offerTemplate);
														} else {
															throw new MeveoApiException("Subscription.offerTemplate cannot be null.");
														}

														subscription.setProvider(provider);
													} else {
														if (subscriptionDto.getTerminationDate() != null) {
															if (StringUtils.isBlank(subscriptionDto.getTerminationReason())) {
																missingParameters.add("subscription.terminationReason");
																handleMissingParameters();
															}

															SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService
																	.findByCode(subscriptionDto.getTerminationReason(), provider);

															if (subscriptionTerminationReason == null) {
																throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class,
																		subscriptionDto.getTerminationReason());
															}

															try {
																subscriptionService.terminateSubscription(subscription, subscriptionDto.getTerminationDate(),
																		subscriptionTerminationReason, currentUser);
															} catch (BusinessException e) {
																log.error("Error terminating subscription with code=" + subscriptionDto.getCode());
																throw new MeveoApiException(
																		"Error terminating subscription with code=" + subscriptionDto.getCode());
															}

															continue;
														} else {
															if (!StringUtils.isBlank(subscriptionDto.getOfferTemplate())) {
																OfferTemplate offerTemplate = offerTemplateService
																		.findByCode(subscriptionDto.getOfferTemplate(), provider);
																if (offerTemplate == null) {
																	throw new EntityDoesNotExistsException(OfferTemplate.class,
																			subscriptionDto.getOfferTemplate());
																}

																subscription.setOffer(offerTemplate);
															}
														}
													}

													subscription.setUserAccount(userAccount);

													if (!StringUtils.isBlank(subscriptionDto.getDescription())) {
														subscription.setDescription(subscriptionDto.getDescription());
													}
													if (!StringUtils.isBlank(subscriptionDto.getSubscriptionDate())) {
														subscription.setSubscriptionDate(subscriptionDto.getSubscriptionDate());
													}
													if (!StringUtils.isBlank(subscriptionDto.getTerminationDate())) {
														subscription.setTerminationDate(subscriptionDto.getTerminationDate());
													}
													if (!StringUtils.isBlank(subscriptionDto.getEndAgreementDate())) {
														subscription.setEndAgreementDate(subscriptionDto.getEndAgreementDate());
													}

													boolean isNewSubscription = subscription.isTransient();
													if (isNewSubscription) {
														subscriptionService.create(subscription, currentUser);
													} else {
														subscriptionService.update(subscription, currentUser);
													}

													// populate
													// customFields
													try {
														populateCustomFields(subscriptionDto.getCustomFields(), subscription, isNewSubscription, currentUser);
													} catch (IllegalArgumentException | IllegalAccessException e) {
														log.error("Failed to associate custom field instance to a subscription {}", subscriptionDto.getCode(),
																e);
														throw new MeveoApiException(
																"Failed to associate custom field instance to a subscription " + subscriptionDto.getCode());
													}

													// accesses
													if (subscriptionDto.getAccesses() != null) {
														for (AccessDto accessDto : subscriptionDto.getAccesses().getAccess()) {
															if (StringUtils.isBlank(accessDto.getCode())) {
																log.warn("code is null={}", accessDto);
																continue;
															}

															Access access = accessService.findByUserIdAndSubscription(accessDto.getCode(), subscription);
															if (access == null) {
																access = new Access();
																access.setAccessUserId(accessDto.getCode());

																access.setProvider(provider);
															}

															access.setSubscription(subscription);

															if (!StringUtils.isBlank(accessDto.getStartDate())) {
																access.setStartDate(accessDto.getStartDate());
															}
															if (!StringUtils.isBlank(accessDto.getEndDate())) {
																access.setEndDate(accessDto.getEndDate());
															}

															boolean isNewAccess = access.isTransient();
															if (isNewAccess) {
																accessService.create(access, currentUser);
															} else {
																accessService.update(access, currentUser);
															}

															// populate
															// customFields
															try {
																populateCustomFields(accessDto.getCustomFields(), access, isNewAccess, currentUser);
															} catch (IllegalArgumentException | IllegalAccessException e) {
																log.error("Failed to associate custom field instance to an access {}",
																		subscriptionDto.getCode(), e);
																throw new MeveoApiException(
																		"Failed to associate custom field instance to an access " + subscriptionDto.getCode());
															}
														}
													}

													// service instances
													if (subscriptionDto.getServices() != null) {
														for (ServiceInstanceDto serviceInstanceDto : subscriptionDto.getServices().getServiceInstance()) {
															if (StringUtils.isBlank(serviceInstanceDto.getCode())) {
																log.warn("code is null={}", serviceInstanceDto);
																continue;
															}

															if (serviceInstanceDto.getTerminationDate() != null) {
																// terminate
																ServiceInstance serviceInstance = serviceInstanceService
																		.findActivatedByCodeAndSubscription(serviceInstanceDto.getCode(), subscription);
																if (serviceInstance != null) {
																	if (!StringUtils.isBlank(serviceInstanceDto.getTerminationReason())) {
																		SubscriptionTerminationReason serviceTerminationReason = terminationReasonService
																				.findByCode(serviceInstanceDto.getTerminationReason(), provider);
																		if (serviceTerminationReason == null) {
																			throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class,
																					serviceInstanceDto.getTerminationReason());
																		}
																		try {
																			serviceInstanceService.terminateService(serviceInstance,
																					serviceInstanceDto.getTerminationDate(), serviceTerminationReason,
																					currentUser);
																		} catch (BusinessException e) {
																			log.error("service termination={}", e);
																			throw new MeveoApiException(e.getMessage());
																		}

																	} else {
																		missingParameters.add("serviceInstance.terminationReason");
																		handleMissingParameters();
																	}
																} else {
																	throw new MeveoApiException(
																			"ServiceInstance with code=" + subscriptionDto.getCode() + " must be ACTIVE.");
																}
															} else {
																if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
																	throw new MeveoApiException(
																			"Failed activating a service. Subscription is already RESILIATED.");
																}
																ServiceTemplate serviceTemplate = serviceTemplateService
																		.findByCode(serviceInstanceDto.getCode(), provider);
																if (serviceTemplate == null) {
																	throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceInstanceDto.getCode());
																}
																boolean alreadyActiveOrSuspended = false;
																ServiceInstance serviceInstance = null;
																List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService
																		.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription);

																for (ServiceInstance subscriptionServiceInstance : subscriptionServiceInstances) {
																	if (subscriptionServiceInstance.getStatus() != InstanceStatusEnum.CANCELED
																			&& subscriptionServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED
																			&& subscriptionServiceInstance.getStatus() != InstanceStatusEnum.CLOSED) {
																		if (subscriptionServiceInstance.getStatus().equals(InstanceStatusEnum.INACTIVE)) {
																			alreadyActiveOrSuspended = false;
																		} else {
																			throw new MeveoApiException("ServiceInstance with code="
																					+ serviceInstanceDto.getCode() + " must not be ACTIVE or SUSPENDED.");
																		}
																		break;
																	}
																}

																if (!alreadyActiveOrSuspended) {
																	log.debug("instanciateService id={} checked, quantity={}", serviceTemplate.getId(), 1);
																	serviceInstance = new ServiceInstance();
																	serviceInstance.setProvider(serviceTemplate.getProvider());
																	serviceInstance.setCode(serviceTemplate.getCode());
																	serviceInstance.setDescription(serviceTemplate.getDescription());
																	serviceInstance.setServiceTemplate(serviceTemplate);
																	serviceInstance.setSubscription(subscription);
																	serviceInstance.setSubscriptionDate(serviceInstanceDto.getSubscriptionDate());
																	serviceInstance.setEndAgreementDate(serviceInstanceDto.getEndAgreementDate());
																	serviceInstance.setQuantity(serviceInstanceDto.getQuantity() == null ? BigDecimal.ONE
																			: serviceInstanceDto.getQuantity());
																}

																try {
																	// instantiate
																	serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);
																} catch (BusinessException e) {
																	throw new MeveoApiException(e.getMessage());
																}

																// populate
																// customFields
																try {
																	populateCustomFields(serviceInstanceDto.getCustomFields(), serviceInstance, true,
																			currentUser);
																} catch (IllegalArgumentException | IllegalAccessException e) {
																	log.error("Failed to associate custom field instance to a service instance {}",
																			serviceInstanceDto.getCode(), e);
																	throw new MeveoApiException(
																			"Failed to associate custom field instance to a service instance "
																					+ serviceInstanceDto.getCode());
																}

																if (serviceInstanceDto.getSubscriptionDate() != null) {
																	// activate
																	try {
																		serviceInstanceService.serviceActivation(serviceInstance, null, null, currentUser);
																	} catch (BusinessException e) {
																		throw new MeveoApiException(e.getMessage());
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
		}
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @return
	 * @throws MeveoApiException
	 */
	public GetAccountHierarchyResponseDto findAccountHierarchy2(FindAccountHierachyRequestDto postData, User currentUser) throws MeveoApiException {

		GetAccountHierarchyResponseDto result = new GetAccountHierarchyResponseDto();
		Name name = null;

		if (postData.getName() == null && postData.getAddress() == null) {
			throw new MeveoApiException("At least name or address must not be null.");
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
			List<Customer> customers = customerService.findByNameAndAddress(name, address, currentUser.getProvider());
			if (customers != null) {
				for (Customer customer : customers) {
					result.getCustomers().getCustomer().add(customerToDto(customer));
				}
			}
		}

		if ((postData.getLevel() & CA) != 0) {
			validLevel = true;
			List<CustomerAccount> customerAccounts = customerAccountService.findByNameAndAddress(name, address, currentUser.getProvider());
			if (customerAccounts != null) {
				for (CustomerAccount customerAccount : customerAccounts) {
					addCustomerAccount(result, customerAccount);
				}
			}
		}
		if ((postData.getLevel() & BA) != 0) {
			validLevel = true;
			List<BillingAccount> billingAccounts = billingAccountService.findByNameAndAddress(name, address, currentUser.getProvider());
			if (billingAccounts != null) {
				for (BillingAccount billingAccount : billingAccounts) {
					addBillingAccount(result, billingAccount);
				}
			}
		}
		if ((postData.getLevel() & UA) != 0) {
			validLevel = true;
			List<UserAccount> userAccounts = userAccountService.findByNameAndAddress(name, address, currentUser.getProvider());
			if (userAccounts != null) {
				for (UserAccount userAccount : userAccounts) {
					addUserAccount(result, userAccount);
				}
			}
		}

		if (!validLevel) {
			throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "INVALID_LEVEL_TYPE");
		}

		return result;
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void createCRMAccountHierarchy(CRMAccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (postData.getCrmAccountType() == null) {
			missingParameters.add("crmAccountType");
		}

		handleMissingParameters();

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
			contactInformation.setEmail(postData.getContactInformation().getEmail());
			contactInformation.setFax(postData.getContactInformation().getFax());
			contactInformation.setMobile(postData.getContactInformation().getMobile());
			contactInformation.setPhone(postData.getContactInformation().getPhone());
		}

		String accountType = postData.getCrmAccountType();
		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType, currentUser.getProvider());
		if (businessAccountModel != null) {
			accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
		} else {
			try {
				accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
			} catch (Exception e) {
				throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
			}
		}

		Seller seller = null;
		AccountEntity accountEntity = null;

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

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(Seller.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				sellerDto.setCustomFields(cfsDto);
			}

			seller = sellerApi.create(sellerDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
			// create customer
			log.debug("create cust");

			CustomerDto customerDto = new CustomerDto();
			customerDto.setCode(postData.getCode());
			customerDto.setDescription(postData.getDescription());
			customerDto.setCustomerCategory(postData.getCustomerCategory());
			customerDto.setCustomerBrand(postData.getCustomerBrand());
			if (accountHierarchyTypeEnum.getHighLevel() == 3) {
				customerDto.setSeller(postData.getCrmParentCode());
			} else {
				customerDto.setSeller(postData.getCode());
			}
			customerDto.setMandateDate(postData.getMandateDate());
			customerDto.setMandateIdentification(postData.getMandateIdentification());
			customerDto.setName(name);
			customerDto.setAddress(address);
			customerDto.setContactInformation(contactInformation);
			customerDto.setExternalRef1(postData.getExternalRef1());
			customerDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(Customer.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				customerDto.setCustomFields(cfsDto);
			}

			accountEntity = customerApi.create(customerDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
			// create customer account
			log.debug("create ca");

			CustomerAccountDto customerAccountDto = new CustomerAccountDto();
			customerAccountDto.setCode(postData.getCode());
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
			customerAccountDto.setDateDunningLevel(postData.getDateDunningLevel());
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setDunningLevel(postData.getDunningLevel());
			customerAccountDto.setMandateDate(postData.getMandateDate());
			customerAccountDto.setMandateIdentification(postData.getMandateIdentification());
			customerAccountDto.setName(name);
			customerAccountDto.setAddress(address);
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setExternalRef1(postData.getExternalRef1());
			customerAccountDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(CustomerAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				customerAccountDto.setCustomFields(cfsDto);
			}

			accountEntity = customerAccountApi.create(customerAccountDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
			// create billing account
			log.debug("create ba");

			BillingAccountDto billingAccountDto = new BillingAccountDto();
			billingAccountDto.setCode(postData.getCode());
			billingAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 1) {
				billingAccountDto.setCustomerAccount(postData.getCrmParentCode());
			} else {
				billingAccountDto.setCustomerAccount(postData.getCode());
			}
			billingAccountDto.setBillingCycle(postData.getBillingCycle());
			billingAccountDto.setCountry(postData.getCountry());
			billingAccountDto.setLanguage(postData.getLanguage());
			billingAccountDto.setPaymentMethod(postData.getPaymentMethod());
			billingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
			billingAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
			billingAccountDto.setPaymentTerms(postData.getPaymentTerms());
			billingAccountDto.setElectronicBilling(postData.getElectronicBilling());
			billingAccountDto.setStatus(postData.getBaStatus());
			billingAccountDto.setTerminationReason(postData.getTerminationReason());
			billingAccountDto.setEmail(postData.getEmail());
			if (postData.getBankCoordinates() != null) {
				BankCoordinatesDto bankCoordinatesDto = new BankCoordinatesDto();
				bankCoordinatesDto.setAccountNumber(postData.getBankCoordinates().getAccountNumber());
				bankCoordinatesDto.setAccountOwner(postData.getBankCoordinates().getAccountOwner());
				bankCoordinatesDto.setBankCode(postData.getBankCoordinates().getBankCode());
				bankCoordinatesDto.setBankId(postData.getBankCoordinates().getBankId());
				bankCoordinatesDto.setBankName(postData.getBankCoordinates().getBankName());
				bankCoordinatesDto.setBic(postData.getBankCoordinates().getBic());
				bankCoordinatesDto.setBranchCode(postData.getBankCoordinates().getBranchCode());
				bankCoordinatesDto.setIban(postData.getBankCoordinates().getIban());
				bankCoordinatesDto.setIcs(postData.getBankCoordinates().getIcs());
				bankCoordinatesDto.setIssuerName(postData.getBankCoordinates().getIssuerName());
				bankCoordinatesDto.setIssuerNumber(postData.getBankCoordinates().getIssuerNumber());
				bankCoordinatesDto.setKey(postData.getBankCoordinates().getKey());
				billingAccountDto.setBankCoordinates(bankCoordinatesDto);
			}
			billingAccountDto.setName(name);
			billingAccountDto.setAddress(address);
			billingAccountDto.setExternalRef1(postData.getExternalRef1());
			billingAccountDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(BillingAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				billingAccountDto.setCustomFields(cfsDto);
			}

			accountEntity = billingAccountApi.create(billingAccountDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 0 && accountHierarchyTypeEnum.getLowLevel() <= 0) {
			// create user account
			log.debug("create ua");

			UserAccountDto userAccountDto = new UserAccountDto();
			userAccountDto.setCode(postData.getCode());
			userAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 0) {
				userAccountDto.setBillingAccount(postData.getCrmParentCode());
			} else {
				userAccountDto.setBillingAccount(postData.getCode());
			}
			userAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
			userAccountDto.setTerminationReason(postData.getTerminationReason());
			userAccountDto.setStatus(postData.getUaStatus());
			userAccountDto.setName(name);
			userAccountDto.setAddress(address);
			userAccountDto.setExternalRef1(postData.getExternalRef1());
			userAccountDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(UserAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				userAccountDto.setCustomFields(cfsDto);
			}

			accountEntity = userAccountApi.create(userAccountDto, currentUser, true);
		}

		if (businessAccountModel != null && businessAccountModel.getScript() != null) {
			try {
				accountModelScriptService.createAccount(businessAccountModel.getScript().getCode(), seller, accountEntity, currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
			}
		}
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void updateCRMAccountHierarchy(CRMAccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (postData.getCrmAccountType() == null) {
			missingParameters.add("crmAccountType");
		}

		handleMissingParameters();

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
			contactInformation.setEmail(postData.getContactInformation().getEmail());
			contactInformation.setFax(postData.getContactInformation().getFax());
			contactInformation.setMobile(postData.getContactInformation().getMobile());
			contactInformation.setPhone(postData.getContactInformation().getPhone());
		}

		String accountType = postData.getCrmAccountType();
		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType, currentUser.getProvider());
		if (businessAccountModel != null) {
			accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
		} else {
			try {
				accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
			} catch (Exception e) {
				throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
			}
		}

		Seller seller = null;
		AccountEntity accountEntity = null;

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

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(Seller.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				sellerDto.setCustomFields(cfsDto);
			}

			seller = sellerApi.update(sellerDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
			// update customer
			log.debug("update c");

			CustomerDto customerDto = new CustomerDto();
			customerDto.setCode(postData.getCode());
			customerDto.setDescription(postData.getDescription());
			customerDto.setCustomerCategory(postData.getCustomerCategory());
			customerDto.setCustomerBrand(postData.getCustomerBrand());
			if (accountHierarchyTypeEnum.getHighLevel() == 3) {
				customerDto.setSeller(postData.getCrmParentCode());
			} else {
				customerDto.setSeller(postData.getCode());
			}
			customerDto.setMandateDate(postData.getMandateDate());
			customerDto.setMandateIdentification(postData.getMandateIdentification());
			customerDto.setName(name);
			customerDto.setAddress(address);
			customerDto.setContactInformation(contactInformation);
			customerDto.setExternalRef1(postData.getExternalRef1());
			customerDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(Customer.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				customerDto.setCustomFields(cfsDto);
			}

			accountEntity = customerApi.update(customerDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
			// update customer account
			log.debug("update ca");

			CustomerAccountDto customerAccountDto = new CustomerAccountDto();
			customerAccountDto.setCode(postData.getCode());
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
			customerAccountDto.setDateDunningLevel(postData.getDateDunningLevel());
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setDunningLevel(postData.getDunningLevel());
			customerAccountDto.setMandateDate(postData.getMandateDate());
			customerAccountDto.setMandateIdentification(postData.getMandateIdentification());
			customerAccountDto.setName(name);
			customerAccountDto.setAddress(address);
			customerAccountDto.setContactInformation(contactInformation);
			customerAccountDto.setExternalRef1(postData.getExternalRef1());
			customerAccountDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(CustomerAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				customerAccountDto.setCustomFields(cfsDto);
			}

			accountEntity = customerAccountApi.update(customerAccountDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
			// update billing account
			log.debug("update ba");

			BillingAccountDto billingAccountDto = new BillingAccountDto();
			billingAccountDto.setCode(postData.getCode());
			billingAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 1) {
				billingAccountDto.setCustomerAccount(postData.getCrmParentCode());
			} else {
				billingAccountDto.setCustomerAccount(postData.getCode());
			}
			billingAccountDto.setBillingCycle(postData.getBillingCycle());
			billingAccountDto.setCountry(postData.getCountry());
			billingAccountDto.setLanguage(postData.getLanguage());
			billingAccountDto.setPaymentMethod(postData.getPaymentMethod());
			billingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
			billingAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
			billingAccountDto.setPaymentTerms(postData.getPaymentTerms());
			billingAccountDto.setElectronicBilling(postData.getElectronicBilling());
			billingAccountDto.setStatus(postData.getBaStatus());
			billingAccountDto.setTerminationReason(postData.getTerminationReason());
			billingAccountDto.setEmail(postData.getEmail());
			if (postData.getBankCoordinates() != null) {
				BankCoordinatesDto bankCoordinatesDto = new BankCoordinatesDto();
				bankCoordinatesDto.setAccountNumber(postData.getBankCoordinates().getAccountNumber());
				bankCoordinatesDto.setAccountOwner(postData.getBankCoordinates().getAccountOwner());
				bankCoordinatesDto.setBankCode(postData.getBankCoordinates().getBankCode());
				bankCoordinatesDto.setBankId(postData.getBankCoordinates().getBankId());
				bankCoordinatesDto.setBankName(postData.getBankCoordinates().getBankName());
				bankCoordinatesDto.setBic(postData.getBankCoordinates().getBic());
				bankCoordinatesDto.setBranchCode(postData.getBankCoordinates().getBranchCode());
				bankCoordinatesDto.setIban(postData.getBankCoordinates().getIban());
				bankCoordinatesDto.setIcs(postData.getBankCoordinates().getIcs());
				bankCoordinatesDto.setIssuerName(postData.getBankCoordinates().getIssuerName());
				bankCoordinatesDto.setIssuerNumber(postData.getBankCoordinates().getIssuerNumber());
				bankCoordinatesDto.setKey(postData.getBankCoordinates().getKey());
				billingAccountDto.setBankCoordinates(bankCoordinatesDto);
			}
			billingAccountDto.setName(name);
			billingAccountDto.setAddress(address);
			billingAccountDto.setExternalRef1(postData.getExternalRef1());
			billingAccountDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(BillingAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				billingAccountDto.setCustomFields(cfsDto);
			}

			accountEntity = billingAccountApi.update(billingAccountDto, currentUser, true);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 0 && accountHierarchyTypeEnum.getLowLevel() <= 0) {
			// update user account
			log.debug("update ua");

			UserAccountDto userAccountDto = new UserAccountDto();
			userAccountDto.setCode(postData.getCode());
			userAccountDto.setDescription(postData.getDescription());
			if (accountHierarchyTypeEnum.getHighLevel() == 0) {
				userAccountDto.setBillingAccount(postData.getCrmParentCode());
			} else {
				userAccountDto.setBillingAccount(postData.getCode());
			}
			userAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
			userAccountDto.setTerminationReason(postData.getTerminationReason());
			userAccountDto.setStatus(postData.getUaStatus());
			userAccountDto.setName(name);
			userAccountDto.setAddress(address);
			userAccountDto.setExternalRef1(postData.getExternalRef1());
			userAccountDto.setExternalRef2(postData.getExternalRef2());

			CustomFieldsDto cfsDto = new CustomFieldsDto();
			if (postData.getCustomFields() != null && postData.getCustomFields().getCustomField() != null) {
				Map<String, CustomFieldTemplate> cfts = customFieldTemplateService
						.findByAppliesTo(UserAccount.class.getAnnotation(CustomFieldEntity.class).cftCodePrefix(), currentUser.getProvider());
				for (CustomFieldDto cfDto : postData.getCustomFields().getCustomField()) {
					if (cfts.containsKey(cfDto.getCode())) {
						cfsDto.getCustomField().add(cfDto);
					}
				}

				userAccountDto.setCustomFields(cfsDto);
			}

			accountEntity = userAccountApi.update(userAccountDto, currentUser, true);
		}

		if (businessAccountModel != null && businessAccountModel.getScript() != null) {
			try {
				accountModelScriptService.updateAccount(businessAccountModel.getScript().getCode(), seller, accountEntity, currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
			}
		}
	}

	/**
	 * Create or update Account Hierarchy based on code.
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void createOrUpdate(AccountHierarchyDto postData, User currentUser) throws MeveoApiException,
			BusinessException {		
		String customerCodeOrId = null;
		if (!StringUtils.isBlank(postData.getCustomerId())) {
			customerCodeOrId = postData.getCustomerId();
		}
		if (!StringUtils.isBlank(postData.getCustomerCode())) {
			customerCodeOrId = postData.getCustomerCode();
		}

		if (customerService.findByCode(CUSTOMER_PREFIX + customerCodeOrId, currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}

	/**
	 * 
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 * @throws BusinessException
	 */
	public void createOrUpdateCRMAccountHierarchy(CRMAccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (postData.getCrmAccountType() == null) {
			missingParameters.add("crmAccountType");
		}

		handleMissingParameters();

		String accountType = postData.getCrmAccountType();
		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType, currentUser.getProvider());
		if (businessAccountModel != null) {
			accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
		} else {
			try {
				accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
			} catch (Exception e) {
				throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
			}
		}

		boolean accountExist = false;

		if (accountHierarchyTypeEnum.getHighLevel() == 4) {
			Seller seller = sellerService.findByCode(postData.getCode(), currentUser.getProvider());
			if (seller != null) {
				accountExist = true;
			}
		} else if (accountHierarchyTypeEnum.getHighLevel() >= 3 && accountHierarchyTypeEnum.getLowLevel() <= 3) {
			Customer customer = customerService.findByCode(postData.getCode(), currentUser.getProvider());
			if (customer != null) {
				accountExist = true;
			}
		} else if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
			CustomerAccount customerAccount = customerAccountService.findByCode(postData.getCode(), currentUser.getProvider());
			if (customerAccount != null) {
				accountExist = true;
			}
		} else if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
			BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode(), currentUser.getProvider());
			if (billingAccount != null) {
				accountExist = true;
			}
		} else {
			UserAccount userAccount = userAccountService.findByCode(postData.getCode(), currentUser.getProvider());
			if (userAccount != null) {
				accountExist = true;
			}
		}

		if (accountExist) {
			updateCRMAccountHierarchy(postData, currentUser);
		} else {
			createCRMAccountHierarchy(postData, currentUser);
		}
	}

	private void populateNameAddress(AccountEntity accountEntity, AccountDto accountDto, User currentUser) throws MeveoApiException {

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
				accountEntity.getName().setFirstName(accountDto.getName().getFirstName());
			}
			if (!StringUtils.isBlank(accountDto.getName().getLastName())) {
				accountEntity.getName().setLastName(accountDto.getName().getLastName());
			}
			if (!StringUtils.isBlank(accountDto.getName().getTitle())) {
				Title title = titleService.findByCode(accountDto.getName().getTitle(), currentUser.getProvider());
				if (title != null) {
					accountEntity.getName().setTitle(title);
				}
			}
		}

		if (accountDto.getAddress() != null) {
			if (!StringUtils.isBlank(accountDto.getAddress().getAddress1())) {
				accountEntity.getAddress().setAddress1(accountDto.getAddress().getAddress1());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getAddress2())) {
				accountEntity.getAddress().setAddress2(accountDto.getAddress().getAddress2());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getAddress3())) {
				accountEntity.getAddress().setAddress3(accountDto.getAddress().getAddress3());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getZipCode())) {
				accountEntity.getAddress().setZipCode(accountDto.getAddress().getZipCode());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getCity())) {
				accountEntity.getAddress().setCity(accountDto.getAddress().getCity());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getState())) {
				accountEntity.getAddress().setState(accountDto.getAddress().getState());
			}
			if (!StringUtils.isBlank(accountDto.getAddress().getCountry())) {
				accountEntity.getAddress().setCountry(accountDto.getAddress().getCountry());
			}
		}

	}

	private void addUserAccount(GetAccountHierarchyResponseDto result, UserAccount userAccount) {
		BillingAccount billingAccount = userAccount.getBillingAccount();

		addBillingAccount(result, billingAccount);

		for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
			for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts().getCustomerAccount()) {
				for (BillingAccountDto billingAccountDto : customerAccountDto.getBillingAccounts().getBillingAccount()) {
					if (billingAccountDto.getCode().equals(billingAccount.getCode())) {
						if (billingAccountDto.getUserAccounts() != null && billingAccountDto.getUserAccounts().getUserAccount().size() > 0) {
							UserAccountDto userAccountDto = userAccountToDto(userAccount);
							if (!billingAccountDto.getUserAccounts().getUserAccount().contains(userAccountDto)) {
								billingAccountDto.getUserAccounts().getUserAccount().add(userAccountDto);
							}
						} else {
							billingAccountDto.getUserAccounts().getUserAccount().add(userAccountToDto(userAccount));
						}
					}
				}
			}
		}
	}

	private void addBillingAccount(GetAccountHierarchyResponseDto result, BillingAccount billingAccount) {
		CustomerAccount customerAccount = billingAccount.getCustomerAccount();
		Customer customer = customerAccount.getCustomer();

		addCustomer(result, customer);
		addCustomerAccount(result, customerAccount);

		for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
			for (CustomerAccountDto customerAccountDto : customerDto.getCustomerAccounts().getCustomerAccount()) {
				if (customerAccountDto.getCode().equals(customerAccount.getCode())) {
					if (customerAccountDto.getBillingAccounts() != null && customerAccountDto.getBillingAccounts().getBillingAccount().size() > 0) {
						BillingAccountDto billingAccountDto = billingAccountToDto(billingAccount);
						if (!customerAccountDto.getBillingAccounts().getBillingAccount().contains(billingAccountDto)) {
							customerAccountDto.getBillingAccounts().getBillingAccount().add(billingAccountDto);
						}
					} else {
						customerAccountDto.getBillingAccounts().getBillingAccount().add(billingAccountToDto(billingAccount));
					}
				}
			}
		}
	}

	private void addCustomerAccount(GetAccountHierarchyResponseDto result, CustomerAccount customerAccount) {
		Customer customer = customerAccount.getCustomer();
		CustomerAccountDto customerAccountDto = customerAccountToDto(customerAccount);

		if (result.getCustomers() == null || result.getCustomers().getCustomer().size() == 0) {
			CustomerDto customerDto = customerToDto(customer);
			customerDto.getCustomerAccounts().getCustomerAccount().add(customerAccountDto);
			result.getCustomers().getCustomer().add(customerDto);
		} else {
			for (CustomerDto customerDtoLoop : result.getCustomers().getCustomer()) {
				if (customerDtoLoop.getCode().equals(customer.getCode())) {
					if (!customerDtoLoop.getCustomerAccounts().getCustomerAccount().contains(customerAccountDto)) {
						customerDtoLoop.getCustomerAccounts().getCustomerAccount().add(customerAccountDto);
					}
				}
			}
		}
	}

	private void addCustomer(GetAccountHierarchyResponseDto result, Customer customer) {
		if (result.getCustomers() == null || result.getCustomers().getCustomer().size() == 0) {
			result.getCustomers().getCustomer().add(customerToDto(customer));
		} else {
			boolean found = false;
			for (CustomerDto customerDto : result.getCustomers().getCustomer()) {
				if (customerDto.getCode().equals(customer.getCode())) {
					if (!customerDto.isLoaded()) {
                        customerDto.initFromEntity(customer, entityToDtoConverter.getCustomFieldsDTO(customer));
					}

					found = true;
					break;
				}
			}

			if (!found) {
				result.getCustomers().getCustomer().add(customerToDto(customer));
			}
		}
	}

	public void accountEntityToDto(AccountDto dto, AccountEntity account) {
		dto.setCode(account.getCode());
		dto.setDescription(account.getDescription());
		dto.setExternalRef1(account.getExternalRef1());
		dto.setExternalRef2(account.getExternalRef2());
		dto.setName(new NameDto(account.getName()));
		dto.setAddress(new AddressDto(account.getAddress()));

		dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(account));

	}

	public CustomerDto customerToDto(Customer customer) {
		CustomerDto dto = new CustomerDto();
		accountEntityToDto(dto, customer);

		if (customer.getCustomerCategory() != null) {
			dto.setCustomerCategory(customer.getCustomerCategory().getCode());
		}

		if (customer.getCustomerBrand() != null) {
			dto.setCustomerBrand(customer.getCustomerBrand().getCode());
		}

		if (customer.getSeller() != null) {
			dto.setSeller(customer.getSeller().getCode());
		}

		if (customer.getContactInformation() != null) {
			dto.setContactInformation(new ContactInformationDto(customer.getContactInformation()));
		}

		if (!dto.isLoaded() && customer.getCustomerAccounts() != null) {
			dto.setCustomerAccounts(new CustomerAccountsDto());

			for (CustomerAccount ca : customer.getCustomerAccounts()) {
				dto.getCustomerAccounts().getCustomerAccount().add(customerAccountToDto(ca));
			}
		}

		dto.setLoaded(true);
		return dto;
	}

	public CustomerAccountDto customerAccountToDto(CustomerAccount ca) {
		CustomerAccountDto dto = new CustomerAccountDto();
		accountEntityToDto(dto, ca);

		if (ca.getCustomer() != null) {
			dto.setCustomer(ca.getCustomer().getCode());
		}

		if (ca.getTradingCurrency() != null) {
			dto.setCurrency(ca.getTradingCurrency().getCurrencyCode());
		}

		if (ca.getTradingLanguage() != null) {
			dto.setLanguage(ca.getTradingLanguage().getLanguageCode());
		}

		dto.setStatus(ca.getStatus());
		dto.setDateStatus(ca.getDateStatus());
		dto.setPaymentMethod(ca.getPaymentMethod());
		try {
			dto.setCreditCategory(ca.getCreditCategory().getCode());
		} catch (NullPointerException ex) {
		}
		dto.setDunningLevel(ca.getDunningLevel());
		dto.setDateStatus(ca.getDateStatus());
		dto.setDateDunningLevel(ca.getDateDunningLevel());
		if (ca.getContactInformation() != null) {
			dto.setContactInformation(new ContactInformationDto(ca.getContactInformation()));
		}

		dto.setMandateIdentification(ca.getMandateIdentification());
		dto.setMandateDate(ca.getMandateDate());

		if (!dto.isLoaded() && ca.getBillingAccounts() != null) {
			dto.setBillingAccounts(new BillingAccountsDto());

			for (BillingAccount ba : ca.getBillingAccounts()) {
				dto.getBillingAccounts().getBillingAccount().add(billingAccountToDto(ba));
			}
		}

		dto.setLoaded(true);
		return dto;
	}

	public BillingAccountDto billingAccountToDto(BillingAccount ba) {

		BillingAccountDto dto = new BillingAccountDto();
		accountEntityToDto(dto, ba);

		if (ba.getCustomerAccount() != null) {
			dto.setCustomerAccount(ba.getCustomerAccount().getCode());
		}
		if (ba.getBillingCycle() != null) {
			dto.setBillingCycle(ba.getBillingCycle().getCode());
		}
		if (ba.getTradingCountry() != null) {
			dto.setCountry(ba.getTradingCountry().getCountryCode());
		}
		if (ba.getTradingLanguage() != null) {
			dto.setLanguage(ba.getTradingLanguage().getLanguageCode());
		}
		dto.setPaymentMethod(ba.getPaymentMethod());
		dto.setNextInvoiceDate(ba.getNextInvoiceDate());
		dto.setSubscriptionDate(ba.getSubscriptionDate());
		dto.setTerminationDate(ba.getTerminationDate());
		dto.setPaymentTerms(ba.getPaymentTerm());
		dto.setElectronicBilling(ba.getElectronicBilling());
		dto.setStatus(ba.getStatus());
		dto.setStatusDate(ba.getStatusDate());
		if (ba.getTerminationReason() != null) {
			dto.setTerminationReason(ba.getTerminationReason().getCode());
		}
		dto.setEmail(ba.getEmail());

		if (ba.getBankCoordinates() != null) {
			dto.setBankCoordinates(new BankCoordinatesDto(ba.getBankCoordinates()));
		}

		if (!dto.isLoaded() && ba.getUsersAccounts() != null) {
			for (UserAccount userAccount : ba.getUsersAccounts()) {
				dto.getUserAccounts().getUserAccount().add(userAccountToDto(userAccount));
			}
		}

		dto.setLoaded(true);

		return dto;

	}

	public UserAccountDto userAccountToDto(UserAccount ua) {

		UserAccountDto dto = new UserAccountDto();
		accountEntityToDto(dto, ua);

		if (ua.getBillingAccount() != null) {
			dto.setBillingAccount(ua.getBillingAccount().getCode());
		}

		dto.setSubscriptionDate(ua.getSubscriptionDate());
		dto.setTerminationDate(ua.getTerminationDate());
		dto.setStatus(ua.getStatus());
		dto.setStatusDate(ua.getStatusDate());
		dto.setLoaded(true);

		return dto;
	}

	public void terminateCRMAccountHierarchy(CRMAccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {
		String accountType = postData.getCrmAccountType();
		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType, currentUser.getProvider());
		if (businessAccountModel != null) {
			accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
		} else {
			try {
				accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
			} catch (Exception e) {
				throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
			}
		}

		AccountEntity accountEntity1 = null;
		AccountEntity accountEntity2 = null;
		if (accountHierarchyTypeEnum.getHighLevel() >= 0 && accountHierarchyTypeEnum.getLowLevel() <= 0) {
			UserAccountDto userAccountDto = new UserAccountDto();
			userAccountDto.setCode(postData.getCode());
			userAccountDto.setTerminationDate(postData.getTerminationDate());
			userAccountDto.setTerminationReason(postData.getTerminationReason());
			accountEntity1 = userAccountApi.terminate(userAccountDto, currentUser);
		}

		if (accountHierarchyTypeEnum.getHighLevel() >= 1 && accountHierarchyTypeEnum.getLowLevel() <= 1) {
			// terminate ba
			BillingAccountDto billingAccountDto = new BillingAccountDto();
			billingAccountDto.setCode(postData.getCode());
			billingAccountDto.setTerminationDate(postData.getTerminationDate());
			billingAccountDto.setTerminationReason(postData.getTerminationReason());
			accountEntity2 = billingAccountApi.terminate(billingAccountDto, currentUser);
		}

		if (businessAccountModel != null && businessAccountModel.getScript() != null) {
			try {
				accountModelScriptService.terminateAccount(businessAccountModel.getScript().getCode(), null,
						(accountEntity1 != null ? accountEntity1 : accountEntity2), currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
			}
		}
	}

	public void closeCRMAccountHierarchy(CRMAccountHierarchyDto postData, User currentUser) throws MeveoApiException, BusinessException {
		String accountType = postData.getCrmAccountType();
		AccountHierarchyTypeEnum accountHierarchyTypeEnum = null;
		BusinessAccountModel businessAccountModel = businessAccountModelService.findByCode(accountType, currentUser.getProvider());
		if (businessAccountModel != null) {
			accountHierarchyTypeEnum = businessAccountModel.getHierarchyType();
		} else {
			try {
				accountHierarchyTypeEnum = AccountHierarchyTypeEnum.valueOf(accountType);
			} catch (Exception e) {
				throw new MeveoApiException("Account type does not match any BAM or AccountHierarchyTypeEnum");
			}
		}

		CustomerAccount customerAccount = null;
		if (accountHierarchyTypeEnum.getHighLevel() >= 2 && accountHierarchyTypeEnum.getLowLevel() <= 2) {
			// close customer account
			CustomerAccountDto customerAccountDto = new CustomerAccountDto();
			customerAccountDto.setCode(postData.getCode());
			customerAccount = customerAccountApi.closeAccount(customerAccountDto, currentUser);
		}

		if (businessAccountModel != null && businessAccountModel.getScript() != null && customerAccount != null) {
			try {
				accountModelScriptService.closeAccount(businessAccountModel.getScript().getCode(), null, customerAccount, currentUser);
			} catch (BusinessException e) {
				log.error("Failed to execute a script {}. {}", businessAccountModel.getScript().getCode(), e);
			}
		}
	}

}
