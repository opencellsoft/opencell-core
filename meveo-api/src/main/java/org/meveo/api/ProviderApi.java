package org.meveo.api;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.response.CustomerBrandDto;
import org.meveo.api.dto.response.CustomerCategoryDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ProviderApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	@Inject
	private CountryService countryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private LanguageService languageService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	@Inject
	private CalendarService calendarService;

	@Inject
	private TaxService taxService;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	private TerminationReasonService terminationReasonService;

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private TitleService titleService;

	public void create(ProviderDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			Provider provider = new Provider();
			provider.setCode(postData.getCode().toUpperCase());
			provider.setDescription(postData.getDescription());

			provider.setMulticountryFlag(postData.isMultiCountry());
			provider.setMulticurrencyFlag(postData.isMultiCurrency());
			provider.setMultilanguageFlag(postData.isMultiLanguage());

			// search for country
			if (!StringUtils.isBlank(postData.getCountry())) {
				Country country = countryService.findByCode(postData.getCountry());
				if (country == null) {
					throw new EntityDoesNotExistsException(Country.class.getName(), postData.getCountry());
				}

				provider.setCountry(country);
			}

			// search for currency
			if (!StringUtils.isBlank(postData.getCurrency())) {
				Currency currency = currencyService.findByCode(postData.getCurrency());
				if (currency == null) {
					throw new EntityDoesNotExistsException(Currency.class.getName(), postData.getCurrency());
				}

				provider.setCurrency(currency);
			}

			// search for language
			if (!StringUtils.isBlank(postData.getLanguage())) {
				Language language = languageService.findByCode(postData.getLanguage());
				if (language == null) {
					throw new EntityDoesNotExistsException(Language.class.getName(), postData.getLanguage());
				}

				provider.setLanguage(language);
			}

			if (!StringUtils.isBlank(postData.getUserAccount())) {
				UserAccount ua = userAccountService.findByCode(postData.getUserAccount(), currentUser.getProvider());
				provider.setUserAccount(ua);
			}

			// check if provider already exists
			if (providerService.findByCode(postData.getCode()) != null) {
				throw new EntityAlreadyExistsException(Provider.class, postData.getCode());
			}

			providerService.create(provider, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public ProviderDto find(String providerCode) throws MeveoApiException {
		if (!StringUtils.isBlank(providerCode)) {
			Provider provider = providerService.findByCodeWithFetch(providerCode,
					Arrays.asList("currency", "country", "language"));
			if (provider != null) {
				return new ProviderDto(provider);
			}

			throw new EntityDoesNotExistsException(Country.class, providerCode);
		} else {
			if (StringUtils.isBlank(providerCode)) {
				missingParameters.add("providerCode");
			}

			throw new MeveoApiException(getMissingParametersExceptionMessage());
		}
	}

	public void update(ProviderDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			// search for provider
			Provider provider = providerService.findByCode(postData.getCode());

			provider.setDescription(postData.getDescription());

			provider.setMulticountryFlag(postData.isMultiCountry());
			provider.setMulticurrencyFlag(postData.isMultiCurrency());
			provider.setMultilanguageFlag(postData.isMultiLanguage());

			// search for country
			if (!StringUtils.isBlank(postData.getCountry())) {
				Country country = countryService.findByCode(postData.getCountry());
				if (country == null) {
					throw new EntityDoesNotExistsException(Country.class.getName(), postData.getCountry());
				}

				provider.setCountry(country);
			}

			// search for currency
			if (!StringUtils.isBlank(postData.getCurrency())) {
				Currency currency = currencyService.findByCode(postData.getCurrency());
				if (currency == null) {
					throw new EntityDoesNotExistsException(Currency.class.getName(), postData.getCurrency());
				}

				provider.setCurrency(currency);
			}

			// search for language
			if (!StringUtils.isBlank(postData.getLanguage())) {
				Language language = languageService.findByCode(postData.getLanguage());
				if (language == null) {
					throw new EntityDoesNotExistsException(Language.class.getName(), postData.getLanguage());
				}

				provider.setLanguage(language);
			}

			if (!StringUtils.isBlank(postData.getUserAccount())) {
				UserAccount ua = userAccountService.findByCode(postData.getUserAccount(), currentUser.getProvider());
				provider.setUserAccount(ua);
			}

			providerService.update(provider, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	/**
	 * Return a list of all the countryCode, currencyCode and languageCode of
	 * the provider.
	 * 
	 * @param currentUser
	 * @return
	 * @throws MeveoApiException
	 */
	public GetTradingConfigurationResponseDto getTradingConfiguration(User currentUser) throws MeveoApiException {
		GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

		List<TradingLanguage> tradingLanguages = tradingLanguageService.list(currentUser.getProvider());
		if (tradingLanguages != null) {
			for (TradingLanguage tradingLanguage : tradingLanguages) {
				result.getLanguages().getLanguage().add(new LanguageDto(tradingLanguage));
			}
		}

		List<TradingCurrency> tradingCurrencies = tradingCurrencyService.list(currentUser.getProvider());
		if (tradingCurrencies != null) {
			for (TradingCurrency tradingCurrency : tradingCurrencies) {
				result.getCurrencies().getCurrency().add(new CurrencyDto(tradingCurrency));
			}
		}

		List<TradingCountry> tradingCountries = tradingCountryService.list(currentUser.getProvider());
		if (tradingCountries != null) {
			for (TradingCountry tradingCountry : tradingCountries) {
				result.getCountries().getCountry().add(new CountryDto(tradingCountry));
			}
		}

		return result;
	}

	/**
	 * Return a list of all the calendar, tax, invoice categories, invoice
	 * subcategories, billingCycle and termination reason of the provider.
	 * 
	 * @param currentUser
	 * @return
	 */
	public GetInvoicingConfigurationResponseDto getInvoicingConfiguration(User currentUser) throws MeveoApiException {
		GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

		// calendar
		List<Calendar> calendars = calendarService.list(currentUser.getProvider());
		if (calendars != null) {
			for (Calendar calendar : calendars) {
				result.getCalendars().getCalendar().add(new CalendarDto(calendar));
			}
		}

		// tax
		List<Tax> taxes = taxService.list(currentUser.getProvider());
		if (taxes != null) {
			for (Tax tax : taxes) {
				result.getTaxes().getTax().add(new TaxDto(tax));
			}
		}

		// invoice categories
		List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list(currentUser.getProvider());
		if (invoiceCategories != null) {
			for (InvoiceCategory invoiceCategory : invoiceCategories) {
				result.getInvoiceCategories().getInvoiceCategory().add(new InvoiceCategoryDto(invoiceCategory));
			}
		}

		// invoice sub-categories
		List<InvoiceSubCategory> invoiceSubCategories = invoiceSubCategoryService.list(currentUser.getProvider());
		if (invoiceSubCategories != null) {
			for (InvoiceSubCategory invoiceSubCategory : invoiceSubCategories) {
				result.getInvoiceSubCategories().getInvoiceSubCategory()
						.add(new InvoiceSubCategoryDto(invoiceSubCategory));
			}
		}

		// billingCycle
		List<BillingCycle> billingCycles = billingCycleService.list(currentUser.getProvider());
		if (billingCycles != null) {
			for (BillingCycle billingCycle : billingCycles) {
				result.getBillingCycles().getBillingCycle().add(new BillingCycleDto(billingCycle));
			}
		}

		// terminationReasons
		List<SubscriptionTerminationReason> terminationReasons = terminationReasonService.list(currentUser
				.getProvider());
		if (terminationReasons != null) {
			for (SubscriptionTerminationReason terminationReason : terminationReasons) {
				result.getTerminationReasons().getTerminationReason().add(new TerminationReasonDto(terminationReason));
			}
		}

		return result;
	}

	public GetCustomerConfigurationResponseDto getCustomerConfigurationResponse(User currentUser)
			throws MeveoApiException {
		GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

		// customerBrands
		List<CustomerBrand> customerBrands = customerBrandService.list(currentUser.getProvider());
		if (customerBrands != null) {
			for (CustomerBrand customerBrand : customerBrands) {
				result.getCustomerBrands().getCustomerBrand().add(new CustomerBrandDto(customerBrand));
			}
		}

		// customerCategories
		List<CustomerCategory> customerCategories = customerCategoryService.list(currentUser.getProvider());
		if (customerCategories != null) {
			for (CustomerCategory customerCategory : customerCategories) {
				result.getCustomerCategories().getCustomerCategory().add(new CustomerCategoryDto(customerCategory));
			}
		}

		// titles
		List<Title> titles = titleService.list(currentUser.getProvider());
		if (titles != null) {
			for (Title title : titles) {
				result.getTitles().getTitle().add(new TitleDto(title));
			}
		}

		return result;
	}

}
