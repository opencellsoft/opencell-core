package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomerHeirarchyDto;
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
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CustomerHeirarchyApi extends BaseApi {

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

	ParamBean paramBean = ParamBean.getInstance();

	public void createCustomerHeirarchy(
			CustomerHeirarchyDto customerHeirarchyDto) throws BusinessException {

		log.info("Creating Customer Heirarchy...");
		Provider provider = em.find(Provider.class,
				customerHeirarchyDto.getProviderId());
		User currentUser = em.find(User.class,
				customerHeirarchyDto.getCurrentUserId());

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

			if (seller != null) {
				throw new BusinessException("Seller with code "
						+ customerHeirarchyDto.getSellerCode()
						+ " already exists.");
			}

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
					throw new BusinessException("Invalid country code "
							+ customerHeirarchyDto.getCountryCode());
				} else {
					// create tradingCountry
					tradingCountry = new TradingCountry();
					tradingCountry.setCountry(country);
					tradingCountry.setProvider(provider);
					tradingCountry.setActive(true);
					tradingCountry.setPrDescription(country.getDescriptionEn());
					tradingCountry.setAuditable(auditableTrading);
					tradingCountryService.create(em, tradingCountry,
							currentUser, provider);
				}
			}

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(em,
							customerHeirarchyDto.getCurrencyCode(), provider);

			if (tradingCurrency == null) {
				Currency currency = currencyService.findByCode(em,
						customerHeirarchyDto.getCurrencyCode());

				if (currency == null) {
					throw new BusinessException("Currency with code "
							+ customerHeirarchyDto.getCurrencyCode()
							+ " does not exist.");
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
							customerHeirarchyDto.getLanguageCode(), provider);

			if (tradingLanguage == null) {
				Language language = languageService.findByCode(em,
						customerHeirarchyDto.getLanguageCode());

				if (language == null) {
					throw new BusinessException("Language with code "
							+ customerHeirarchyDto.getLanguageCode()
							+ " does not exist.");
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

			CustomerBrand customerBrand = customerBrandService.findByCode(em,
					customerHeirarchyDto.getCustomerBrandCode());

			if (StringUtils.isEmpty(customerHeirarchyDto
					.getCustomerCategoryCode())) {
				throw new BusinessException("Missing Customer Category Code "
						+ customerHeirarchyDto.getCustomerCategoryCode());
			}

			CustomerCategory customerCategory = customerCategoryService
					.findByCode(em,
							customerHeirarchyDto.getCustomerCategoryCode());

			if (customerBrand == null) {
				customerBrand = new CustomerBrand();
				customerBrand.setCode(customerHeirarchyDto
						.getCustomerBrandCode());
				customerBrand.setDescription(customerHeirarchyDto
						.getCustomerBrandCode());
				customerBrandService.create(em, customerBrand, currentUser,
						provider);
			}

			if (customerCategory == null) {
				customerCategory = new CustomerCategory();
				customerCategory.setCode(customerHeirarchyDto
						.getCustomerCategoryCode());
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

			Seller newSeller = new Seller();
			newSeller.setActive(true);
			newSeller.setCode(customerHeirarchyDto.getSellerCode());
			newSeller.setAuditable(auditable);
			newSeller.setProvider(provider);
			newSeller.setTradingCountry(tradingCountry);
			newSeller.setTradingCurrency(tradingCurrency);

			sellerService.create(em, newSeller, currentUser, provider);

			Customer customer = new Customer();
			customer.setCode(customerHeirarchyDto.getCustomerId());
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);
			customer.setSeller(newSeller);

			customerService.create(em, customer, currentUser, provider);

			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setCustomer(customer);
			customerAccount.setCode(customerHeirarchyDto.getCustomerId());
			customerAccount.setStatus(CustomerAccountStatusEnum.ACTIVE);
			customerAccount.setPaymentMethod(PaymentMethodEnum
					.getValue(caPaymentMethod));
			customerAccount.setCreditCategory(CreditCategoryEnum
					.getValue(creditCategory));
			customerAccount.setTradingCurrency(tradingCurrency);
			customerAccountService.create(em, customerAccount, currentUser,
					provider);

			customerAccountService.create(em, customerAccount, currentUser,
					provider);

			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(em,
							customerHeirarchyDto.getBillingCycleCode(),
							currentUser, provider);

			if (billingCycle == null) {
				billingCycle = billingCycleService.findByBillingCycleCode(em,
						paramBean.getProperty("default.billingCycleCode",
								"DEFAULT"), provider);
				if (billingCycle == null) {
					Calendar imputationCalendar = calendarService.findByName(
							em, paramBean.getProperty(
									"default.imputationCalendar.Name",
									"DEF_IMP_CAL"));

					Calendar cycleCalendar = calendarService.findByName(em,
							paramBean.getProperty("default.cycleCalendar.Name",
									"DEF_CYC_CAL"));

					if (imputationCalendar == null) {
						throw new BusinessException(
								"Cannot find calendar with name "
										+ paramBean
												.getProperty(
														"default.imputationCalendar.Name",
														"DEF_IMP_CAL"));
					}
					if (cycleCalendar == null) {
						throw new BusinessException(
								"Cannot find calendar with name "
										+ paramBean.getProperty(
												"default.cycleCalendar.Name",
												"DEF_CYC_CAL"));

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

			BillingAccount billingAccount = new BillingAccount();
			billingAccount.setCode(customerHeirarchyDto.getCustomerId());
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

			UserAccount userAccount = new UserAccount();
			userAccount.setStatus(AccountStatusEnum.ACTIVE);
			userAccount.setBillingAccount(billingAccount);
			userAccount.setCode(customerHeirarchyDto.getCustomerId());
			userAccountService.createUserAccount(em, billingAccount,
					userAccount, currentUser);

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

			throw new BusinessException(sb.toString());

		}

	}
}
