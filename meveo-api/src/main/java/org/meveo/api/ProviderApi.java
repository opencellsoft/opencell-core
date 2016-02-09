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
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceConfiguration;
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
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.PaymentMethodEnum;
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
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CreditCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ProviderApi extends BaseApi {

    @Inject
    private ProviderService providerService;

    @Inject
    private CreditCategoryService creditCategoryService;

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

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    public void create(ProviderDto postData, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        Provider provider = new Provider();
        provider.setCode(postData.getCode().toUpperCase());
        provider.setDescription(postData.getDescription());
        provider.setInvoiceSequenceSize(postData.getInvoiceSequenceSize());

        provider.setMulticountryFlag(postData.isMultiCountry());
        provider.setMulticurrencyFlag(postData.isMultiCurrency());
        provider.setMultilanguageFlag(postData.isMultiLanguage());

        provider.setEntreprise(postData.isEnterprise());
        provider.setInvoicePrefix(postData.getInvoicePrefix());
        provider.setCurrentInvoiceNb(postData.getCurrentInvoiceNb());
        provider.setDisplayFreeTransacInInvoice(postData.isDisplayFreeTransacInInvoice());

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

        InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration();
        invoiceConfiguration.setCode(provider.getCode());
        invoiceConfiguration.setDescription(provider.getDescription());
        invoiceConfiguration.setDisplayEdrs(false);
        invoiceConfiguration.setDisplayOffers(true);
        invoiceConfiguration.setDisplayServices(true);
        invoiceConfiguration.setDisplaySubscriptions(true);
        invoiceConfiguration.setDisplayProvider(postData.getDisplayProvider());
        invoiceConfiguration.setDisplayDetail(postData.getDisplayDetail());

        Auditable auditable = new Auditable(currentUser);
        invoiceConfiguration.setAuditable(auditable);

        provider.setInvoiceConfiguration(invoiceConfiguration);

        invoiceConfiguration.setProvider(provider);

        provider.setInvoiceAdjustmentPrefix(postData.getInvoiceAdjustmentPrefix());
        provider.setCurrentInvoiceAdjustmentNb(postData.getCurrentInvoiceAdjustmentNb());

        if (postData.getInvoiceAdjustmentSequenceSize() != null) {
            provider.setInvoiceAdjustmentSequenceSize(postData.getInvoiceAdjustmentSequenceSize());
        }

        providerService.create(provider, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, true, currentUser);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }

    }

    public ProviderDto find(String providerCode, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCodeWithFetch(providerCode, Arrays.asList("currency", "country", "language"));
        if (provider != null) {
            return new ProviderDto(provider, customFieldInstanceService.getCustomFieldInstances(provider));
        }

        throw new EntityDoesNotExistsException(Provider.class, providerCode);

    }

    public void update(ProviderDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }

        // search for provider
        Provider provider = providerService.findByCode(postData.getCode());

        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getCode());
        }

        provider.setDescription(postData.getDescription());
        provider.setInvoiceSequenceSize(postData.getInvoiceSequenceSize());
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

        provider.setDisplayFreeTransacInInvoice(postData.isDisplayFreeTransacInInvoice());
        provider.setEntreprise(postData.isEnterprise());
        provider.setInvoicePrefix(postData.getInvoicePrefix());
        provider.setCurrentInvoiceNb(postData.getCurrentInvoiceNb());

        InvoiceConfiguration invoiceConfiguration = provider.getInvoiceConfiguration();
        if (invoiceConfiguration != null) {
            invoiceConfiguration.setDisplaySubscriptions(postData.getDisplaySubscriptions());
            invoiceConfiguration.setDisplayServices(postData.getDisplayServices());
            invoiceConfiguration.setDisplayOffers(postData.getDisplayOffers());
            invoiceConfiguration.setDisplayEdrs(postData.getDisplayEdrs());
            invoiceConfiguration.setDisplayProvider(postData.getDisplayProvider());
            invoiceConfiguration.setDisplayDetail(postData.getDisplayDetail());
        }

        if (postData.getInvoiceSequenceSize() != null) {
            provider.setInvoiceSequenceSize(postData.getInvoiceSequenceSize());
        }

        if (postData.getInvoiceAdjustmentPrefix() != null) {
            provider.setInvoiceAdjustmentPrefix(postData.getInvoiceAdjustmentPrefix());
        }

        if (postData.getCurrentInvoiceAdjustmentNb() != null) {
            provider.setCurrentInvoiceAdjustmentNb(postData.getCurrentInvoiceAdjustmentNb());
        }

        if (postData.getInvoiceAdjustmentSequenceSize() != null) {
            provider.setInvoiceAdjustmentSequenceSize(postData.getInvoiceAdjustmentSequenceSize());
        }

        provider = providerService.update(provider, currentUser);
        providerService.refresh(provider);
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, false, currentUser);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }
    }

    /**
     * Return a list of all the countryCode, currencyCode and languageCode of the provider.
     * 
     * @param currentUser
     * @return
     * @throws MeveoApiException
     */
    public GetTradingConfigurationResponseDto getTradingConfiguration(String providerCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCode(providerCode);
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, providerCode);
        }

        GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

        List<TradingLanguage> tradingLanguages = tradingLanguageService.list(provider);
        if (tradingLanguages != null) {
            for (TradingLanguage tradingLanguage : tradingLanguages) {
                result.getLanguages().getLanguage().add(new LanguageDto(tradingLanguage));
            }
        }

        List<TradingCurrency> tradingCurrencies = tradingCurrencyService.list(provider);
        if (tradingCurrencies != null) {
            for (TradingCurrency tradingCurrency : tradingCurrencies) {
                result.getCurrencies().getCurrency().add(new CurrencyDto(tradingCurrency));
            }
        }

        List<TradingCountry> tradingCountries = tradingCountryService.list(provider);
        if (tradingCountries != null) {
            for (TradingCountry tradingCountry : tradingCountries) {
                result.getCountries().getCountry().add(new CountryDto(tradingCountry));
            }
        }

        return result;
    }

    /**
     * Return a list of all the calendar, tax, invoice categories, invoice subcategories, billingCycle and termination reason of the provider.
     * 
     * @param currentUser
     * @return
     */
    public GetInvoicingConfigurationResponseDto getInvoicingConfiguration(String providerCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCode(providerCode);
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, providerCode);
        }

        GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

        // calendar
        List<Calendar> calendars = calendarService.list(provider);
        if (calendars != null) {
            for (Calendar calendar : calendars) {
                result.getCalendars().getCalendar().add(new CalendarDto(calendar));
            }
        }

        // tax
        List<Tax> taxes = taxService.list(provider);
        if (taxes != null) {
            for (Tax tax : taxes) {
                result.getTaxes().getTax().add(new TaxDto(tax));
            }
        }

        // invoice categories
        List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list(provider);
        if (invoiceCategories != null) {
            for (InvoiceCategory invoiceCategory : invoiceCategories) {
                result.getInvoiceCategories().getInvoiceCategory().add(new InvoiceCategoryDto(invoiceCategory));
            }
        }

        // invoice sub-categories
        List<InvoiceSubCategory> invoiceSubCategories = invoiceSubCategoryService.list(provider);
        if (invoiceSubCategories != null) {
            for (InvoiceSubCategory invoiceSubCategory : invoiceSubCategories) {
                result.getInvoiceSubCategories().getInvoiceSubCategory().add(new InvoiceSubCategoryDto(invoiceSubCategory));
            }
        }

        // billingCycle
        List<BillingCycle> billingCycles = billingCycleService.list(provider);
        if (billingCycles != null) {
            for (BillingCycle billingCycle : billingCycles) {
                result.getBillingCycles().getBillingCycle().add(new BillingCycleDto(billingCycle));
            }
        }

        // terminationReasons
        List<SubscriptionTerminationReason> terminationReasons = terminationReasonService.list(provider);
        if (terminationReasons != null) {
            for (SubscriptionTerminationReason terminationReason : terminationReasons) {
                result.getTerminationReasons().getTerminationReason().add(new TerminationReasonDto(terminationReason));
            }
        }

        return result;
    }

    public GetCustomerConfigurationResponseDto getCustomerConfiguration(String providerCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCode(providerCode);
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, providerCode);
        }

        GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

        // customerBrands
        List<CustomerBrand> customerBrands = customerBrandService.list(provider);
        if (customerBrands != null) {
            for (CustomerBrand customerBrand : customerBrands) {
                result.getCustomerBrands().getCustomerBrand().add(new CustomerBrandDto(customerBrand));
            }
        }

        // customerCategories
        List<CustomerCategory> customerCategories = customerCategoryService.list(provider);
        if (customerCategories != null) {
            for (CustomerCategory customerCategory : customerCategories) {
                result.getCustomerCategories().getCustomerCategory().add(new CustomerCategoryDto(customerCategory));
            }
        }

        // titles
        List<Title> titles = titleService.list(provider);
        if (titles != null) {
            for (Title title : titles) {
                result.getTitles().getTitle().add(new TitleDto(title));
            }
        }

        return result;
    }

    public GetCustomerAccountConfigurationResponseDto getCustomerAccountConfiguration(String providerCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCode(providerCode);
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, providerCode);
        }

        GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

        for (PaymentMethodEnum e : PaymentMethodEnum.values()) {
            result.getPaymentMethods().add(e.name());
        }

        List<CreditCategory> creditCategories = creditCategoryService.list(provider);
        for (CreditCategory cc : creditCategories) {
            result.getCreditCategories().getCreditCategory().add(new CreditCategoryDto(cc));
        }

        return result;
    }

    /**
     * Create or update Provider based on provider code
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     */
    public void createOrUpdate(ProviderDto postData, User currentUser) throws MeveoApiException {
        Provider provider = providerService.findByCode(postData.getCode());

        if (provider == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }

    }
}