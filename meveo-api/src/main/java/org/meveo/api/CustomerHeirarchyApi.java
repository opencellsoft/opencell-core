package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomerHeirarchyDto;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CustomerHeirarchyApi extends BaseApi {

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
	private LanguageService languageService;

	@Inject
	private CustomerService customerService;

	public void createCustomerHeirarchy(
			CustomerHeirarchyDto customerHeirarchyDto) throws BusinessException {

		Provider provider = em.find(Provider.class,
				customerHeirarchyDto.getProviderId());
		User currentUser = em.find(User.class,
				customerHeirarchyDto.getCurrentUserId());

		CustomerBrand customerBrand = customerBrandService.findByCode(em,
				customerHeirarchyDto.getCustomerBrandCode());

		CustomerCategory customerCategory = customerCategoryService.findByCode(
				em, customerHeirarchyDto.getCustomerCategoryCode());

		if (customerBrand == null) {
			customerBrand = new CustomerBrand();
			customerBrand.setCode(customerHeirarchyDto.getCustomerBrandCode());
			customerBrandService.create(em, customerBrand, currentUser,
					provider);
		}

		if (customerCategory == null) {
			customerCategory = new CustomerCategory();
			customerCategory.setCode(customerHeirarchyDto
					.getCustomerCategoryCode());

			customerCategoryService.create(em, customerCategory, currentUser,
					provider);
		}

		if (customerHeirarchyDto != null) {
			Customer customer = new Customer();
			customer.setCode(customerHeirarchyDto.getCustomerId());
			customer.setCustomerBrand(customerBrand);
			customer.setCustomerCategory(customerCategory);

			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setCode(customerHeirarchyDto.getCustomerId());
			Name name = new Name();
			name.setLastName(customerHeirarchyDto.getLastName());
			customerAccount.setName(name);

			customer.getCustomerAccounts().add(customerAccount);

			BillingAccount billingAccount = new BillingAccount();
			billingAccount.setCustomerAccount(customerAccount);
			billingAccount.setCode(customerHeirarchyDto.getCustomerId());

			Auditable auditableTrading = new Auditable();
			auditableTrading.setCreated(new Date());
			auditableTrading.setCreator(currentUser);

			TradingCurrency tradingCurrency = tradingCurrencyService
					.findByTradingCurrencyCode(em,
							customerHeirarchyDto.getCurrencyCode(), provider);

			if (tradingCurrency == null) {
				Currency currency = currencyService.findByCode(em,
						customerHeirarchyDto.getCurrencyCode());

				if (currency == null) {
					throw new BusinessException("Invalid currency code "
							+ customerHeirarchyDto.getCurrencyCode());
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

			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(em,
							customerHeirarchyDto.getLanguageCode(), provider);

			if (tradingLanguage == null) {
				Language language = languageService.findByCode(em,
						customerHeirarchyDto.getLanguageCode());

				if (language == null) {
					throw new BusinessException("Invalid language code "
							+ customerHeirarchyDto.getLanguageCode());
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

			billingAccount.setTradingCountry(tradingCountry);
			billingAccount.setTradingLanguage(tradingLanguage);

			UserAccount userAccount = new UserAccount();
			userAccount.setCode(customerHeirarchyDto.getCustomerId());

			userAccountService.create(em, userAccount, currentUser, provider);

			// TODO: Billing Cycles needed to be added to billing account

			BillingCycle billingCycle = billingCycleService
					.findByBillingCycleCode(em,
							customerHeirarchyDto.getBillingCycleCode(),
							provider);

			
			billingAccount.setBillingCycle(billingCycle);
			billingAccount.getUsersAccounts().add(userAccount);

			billingAccountService.create(em, billingAccount, currentUser,
					provider);

			customerAccount.getBillingAccounts().add(billingAccount);

			customerAccountService.create(em, customerAccount, currentUser,
					provider);

			customerService.create(em, customer, currentUser, provider);
		}
	}
}
