package org.meveo.api;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
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
import org.meveo.api.dto.invoice.InvoiceConfigurationDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BankCoordinates;
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
import org.meveo.service.payments.impl.CreditCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@SuppressWarnings("deprecation")
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

    public void create(ProviderDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();
        if (!currentUser.hasPermission("superAdmin", "superAdminManagement")) {
            throw new LoginException("User has no permission to create new providers");
        }

        Provider provider = providerService.findByCode(postData.getCode());
        if (provider != null) {
            throw new EntityAlreadyExistsException(Provider.class, postData.getCode());
        }

        provider=fromDto(postData,currentUser.getProvider(),provider);
        providerService.create(provider, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, true, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

    }

    public ProviderDto find(String providerCode, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCodeWithFetch(providerCode, Arrays.asList("currency", "country", "language"));
        if (provider != null) {
            if (currentUser.hasPermission("superAdmin", "superAdminManagement")
                    || (currentUser.hasPermission("administration", "administrationVisualization") && provider.getId().equals(currentUser.getProvider().getId()))) {
                return new ProviderDto(provider, entityToDtoConverter.getCustomFieldsDTO(provider));
            } else {
                throw new LoginException("User has no permission to access provider " + provider.getCode());
            }
        }

        throw new EntityDoesNotExistsException(Provider.class, providerCode);

    }

    public void update(ProviderDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        // search for provider
        Provider provider = providerService.findByCodeWithFetch(postData.getCode(), Arrays.asList("currency", "country", "language"));
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getCode());
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider.getId()
            .equals(currentUser.getProvider().getId())))) {
            throw new LoginException("User has no permission to manage provider " + provider.getCode());
        }

        provider=fromDto(postData,currentUser.getProvider(),provider);
        provider = providerService.update(provider, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, false, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
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

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationVisualization") && provider.getId()
            .equals(currentUser.getProvider().getId())))) {
            throw new LoginException("User has no permission to access provider " + provider.getCode());
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

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || ((currentUser.hasPermission("administration", "administrationVisualization")
                || currentUser.hasPermission("billing", "billingVisualization") || currentUser.hasPermission("catalog", "catalogVisualization")) && provider.getId().equals(
            currentUser.getProvider().getId())))) {
            throw new LoginException("User has no permission to access provider " + provider.getCode());
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
                result.getTaxes().getTax().add(new TaxDto(tax,entityToDtoConverter.getCustomFieldsDTO(tax)));
            }
        }

        // invoice categories
        List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list(provider);
        if (invoiceCategories != null) {
            for (InvoiceCategory invoiceCategory : invoiceCategories) {
                result.getInvoiceCategories().getInvoiceCategory().add(new InvoiceCategoryDto(invoiceCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceCategory)));
            }
        }

        // invoice sub-categories
        List<InvoiceSubCategory> invoiceSubCategories = invoiceSubCategoryService.list(provider);
        if (invoiceSubCategories != null) {
            for (InvoiceSubCategory invoiceSubCategory : invoiceSubCategories) {
                result.getInvoiceSubCategories().getInvoiceSubCategory()
                    .add(new InvoiceSubCategoryDto(invoiceSubCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceSubCategory)));
            }
        }

        // billingCycle
        List<BillingCycle> billingCycles = billingCycleService.list(provider);
        if (billingCycles != null) {
            for (BillingCycle billingCycle : billingCycles) {
                result.getBillingCycles().getBillingCycle().add(new BillingCycleDto(billingCycle,entityToDtoConverter.getCustomFieldsDTO(billingCycle)));
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

        if (!currentUser.hasPermission("superAdmin", "superAdminManagement") && !provider.getId().equals(currentUser.getProvider().getId())) {
            throw new LoginException("User has no permission to access provider " + provider.getCode());
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

        if (!currentUser.hasPermission("superAdmin", "superAdminManagement") && !provider.getId().equals(currentUser.getProvider().getId())) {
            throw new LoginException("User has no permission to access provider " + provider.getCode());
        }

        GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

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
     * @throws BusinessException
     */
    public void createOrUpdate(ProviderDto postData, User currentUser) throws MeveoApiException, BusinessException {
        Provider provider = providerService.findByCode(postData.getCode());

        if (provider == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    public void updateProviderCF(ProviderDto postData, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        // search for provider
        Provider provider = providerService.findByCodeWithFetch(postData.getCode(), Arrays.asList("currency", "country", "language"));
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, postData.getCode());
        }

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider.getId()
            .equals(currentUser.getProvider().getId())))) {
            throw new LoginException("User has no permission to manage provider " + provider.getCode());
        }

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, false, currentUser);
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
    }

    public ProviderDto findProviderCF(String providerCode, User currentUser) throws MeveoApiException {
        if (StringUtils.isBlank(providerCode)) {
            providerCode = currentUser.getProvider().getCode();
        }

        Provider provider = providerService.findByCode(providerCode);
        if (provider != null) {
            if (currentUser.hasPermission("superAdmin", "superAdminManagement")
                    || (currentUser.hasPermission("administration", "administrationVisualization") && provider.getId().equals(currentUser.getProvider().getId()))) {
                return new ProviderDto(provider, entityToDtoConverter.getCustomFieldsDTO(provider), false);
            } else {
                throw new LoginException("User has no permission to access provider " + provider.getCode());
            }
        }

        throw new EntityDoesNotExistsException(Provider.class, providerCode);
    }
    public Provider fromDto(ProviderDto postData,Provider currentProvider,Provider entity)throws MeveoApiException{

    	Provider provider=null;
    	if(entity==null){
    		provider=new Provider();
    		provider.setCode(postData.getCode().toUpperCase());
    	}else{
    		provider=entity;
    	}
    	if(!StringUtils.isBlank(postData.getDescription())){
    		provider.setDescription(postData.getDescription());
    	}
    	// search for currency
        if (!StringUtils.isBlank(postData.getCurrency())) {
            Currency currency = currencyService.findByCode(postData.getCurrency());
            if (currency == null) {
                throw new EntityDoesNotExistsException(Currency.class.getName(), postData.getCurrency());
            }
            provider.setCurrency(currency);
        }
        // search for country
        if (!StringUtils.isBlank(postData.getCountry())) {
            Country country = countryService.findByCode(postData.getCountry());
            if (country == null) {
                throw new EntityDoesNotExistsException(Country.class.getName(), postData.getCountry());
            }
            provider.setCountry(country);
        }
        // search for language
        if (!StringUtils.isBlank(postData.getLanguage())) {
            Language language = languageService.findByCode(postData.getLanguage());
            if (language == null) {
                throw new EntityDoesNotExistsException(Language.class.getName(), postData.getLanguage());
            }
            provider.setLanguage(language);
        }
    	if(postData.isMultiCurrency()!=null){
    		provider.setMulticurrencyFlag(postData.isMultiCurrency());
    	}
    	if(postData.isMultiCountry()!=null){
    		provider.setMulticountryFlag(postData.isMultiCountry());
    	}
    	if(postData.isMultiLanguage()!=null){
    		provider.setMultilanguageFlag(postData.isMultiLanguage());
    	}
    	if (!StringUtils.isBlank(postData.getUserAccount())) {
            UserAccount ua = userAccountService.findByCode(postData.getUserAccount(),currentProvider);
            provider.setUserAccount(ua);
        }
    	if(postData.isEnterprise()!=null){
        	provider.setEntreprise(postData.isEnterprise());
        }
        if(postData.isLevelDuplication()!=null){
        	provider.setLevelDuplication(postData.isLevelDuplication());
        }
    	if(postData.getRounding()!=null){
    		provider.setRounding(postData.getRounding());
    	}
    	if(postData.getPrepaidReservationExpirationDelayinMillisec()!=null){
    		provider.setPrepaidReservationExpirationDelayinMillisec(postData.getPrepaidReservationExpirationDelayinMillisec());
    	}
    	if(!StringUtils.isBlank(postData.getDiscountAccountingCode())){
    		provider.setDiscountAccountingCode(postData.getDiscountAccountingCode());
    	}
    	if(!StringUtils.isBlank(postData.getEmail())){
    		provider.setEmail(postData.getEmail());
    	}
        BankCoordinates bankCoordinates = provider.getBankCoordinates() == null ? new BankCoordinates() : provider.getBankCoordinates();
        if (!StringUtils.isBlank(postData.getBankCoordinates().getBankCode())) {
            bankCoordinates.setBankCode(postData.getBankCoordinates().getBankCode());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getBranchCode())) {
            bankCoordinates.setBranchCode(postData.getBankCoordinates().getBranchCode());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getAccountNumber())) {
            bankCoordinates.setAccountNumber(postData.getBankCoordinates().getAccountNumber());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getKey())) {
            bankCoordinates.setKey(postData.getBankCoordinates().getKey());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getIban())) {
            bankCoordinates.setIban(postData.getBankCoordinates().getIban());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getBic())) {
            bankCoordinates.setBic(postData.getBankCoordinates().getBic());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getAccountOwner())) {
            bankCoordinates.setAccountOwner(postData.getBankCoordinates().getAccountOwner());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getBankName())) {
            bankCoordinates.setBankName(postData.getBankCoordinates().getBankName());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getBankId())) {
            bankCoordinates.setBankId(postData.getBankCoordinates().getBankId());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getIssuerNumber())) {
            bankCoordinates.setIssuerNumber(postData.getBankCoordinates().getIssuerNumber());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getIssuerName())) {
            bankCoordinates.setIssuerName(postData.getBankCoordinates().getIssuerName());
        }
        if (!StringUtils.isBlank(postData.getBankCoordinates().getIcs())) {
            bankCoordinates.setIcs(postData.getBankCoordinates().getIcs());
        }
        if(provider.getBankCoordinates()==null){
        	provider.setBankCoordinates(bankCoordinates);
        }
        if(postData.isRecognizeRevenue()!=null){
        	provider.setRecognizeRevenue(postData.isRecognizeRevenue());
        }
        InvoiceConfiguration invoiceConfiguration = (provider.getInvoiceConfiguration() == null) ? new InvoiceConfiguration() : provider.getInvoiceConfiguration();
        InvoiceConfigurationDto invoiceConfigurationDto=postData.getInvoiceConfiguration();
        if(invoiceConfigurationDto!=null){
        	if (invoiceConfigurationDto.getDisplaySubscriptions()!=null) {
                invoiceConfiguration.setDisplaySubscriptions(invoiceConfigurationDto.getDisplaySubscriptions());
            }
            if (invoiceConfigurationDto.getDisplayServices()!=null) {
                invoiceConfiguration.setDisplayServices(invoiceConfigurationDto.getDisplayServices());
            }
            if (invoiceConfigurationDto.getDisplayOffers()!=null) {
                invoiceConfiguration.setDisplayOffers(invoiceConfigurationDto.getDisplayOffers());
            }
            if (invoiceConfigurationDto.getDisplayEdrs()!=null) {
                invoiceConfiguration.setDisplayEdrs(invoiceConfigurationDto.getDisplayEdrs());
            }
            if (invoiceConfigurationDto.getDisplayProvider()!=null) {
                invoiceConfiguration.setDisplayProvider(invoiceConfigurationDto.getDisplayProvider());
            }
            if (invoiceConfigurationDto.getDisplayCfAsXML()!=null) {
                invoiceConfiguration.setDisplayCfAsXML(invoiceConfigurationDto.getDisplayCfAsXML());
            }
            if (invoiceConfigurationDto.getDisplayPricePlans()!=null) {
                invoiceConfiguration.setDisplayPricePlans(invoiceConfigurationDto.getDisplayPricePlans());
            }
            if (invoiceConfigurationDto.getDisplayDetail()!=null) {
                invoiceConfiguration.setDisplayDetail(invoiceConfigurationDto.getDisplayDetail());
            }
            if (invoiceConfigurationDto.getDisplayChargesPeriods()!=null) {
                invoiceConfiguration.setDisplayChargesPeriods(invoiceConfigurationDto.getDisplayChargesPeriods());
            }
            if(provider.getInvoiceConfiguration()==null||provider.getInvoiceConfiguration().isTransient()){
            	provider.setInvoiceConfiguration(invoiceConfiguration);
            	provider.getInvoiceConfiguration().setProvider(provider);
            }
            if(invoiceConfigurationDto.getDisplayFreeTransacInInvoice()!=null){
            	provider.setDisplayFreeTransacInInvoice(invoiceConfigurationDto.getDisplayFreeTransacInInvoice());
            }
            if(invoiceConfigurationDto.getDisplayBillingCycle()!=null){
                provider.getInvoiceConfiguration().setDisplayBillingCycle(invoiceConfigurationDto.getDisplayBillingCycle());
            }
        }
        return provider;
    }
}
