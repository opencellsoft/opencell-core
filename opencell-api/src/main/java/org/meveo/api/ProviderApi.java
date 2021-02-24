/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.ProvidersDto;
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
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
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
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
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
import org.primefaces.model.SortOrder;

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

    public ProviderDto find() throws MeveoApiException {

        Provider provider = providerService.findById(appProvider.getId(), Arrays.asList("currency", "country", "language"));
        if (currentUser.hasRole("apiAccess") || currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationVisualization"))) {
            return new ProviderDto(provider, entityToDtoConverter.getCustomFieldsDTO(provider));
        } else {
            throw new ActionForbiddenException("User has no permission to access provider");
        }
    }

    public void update(ProviderDto postData) throws MeveoApiException, BusinessException {

        // search for provider
        Provider provider = providerService.findById(appProvider.getId(), Arrays.asList("currency", "country", "language"));

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage provider " + provider.getCode());
        }

        provider = fromDto(postData, provider);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        provider = providerService.update(provider);
    }

    /**
     * Return a list of all the countryCode, currencyCode and languageCode of the provider.
     * 
     * 
     * @return GetTradingConfigurationResponseDto
     * @throws MeveoApiException meveo api exception
     */
    public GetTradingConfigurationResponseDto getTradingConfiguration() throws MeveoApiException {

        if (!(currentUser.hasRole("apiAccess") || currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationVisualization")))) {
            throw new ActionForbiddenException("User has no permission to access provider");
        }

        GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

        List<TradingLanguage> tradingLanguages = tradingLanguageService.list();
        if (tradingLanguages != null) {
            for (TradingLanguage tradingLanguage : tradingLanguages) {
                result.getLanguages().getLanguage().add(new LanguageDto(tradingLanguage));
            }
        }

        List<TradingCurrency> tradingCurrencies = tradingCurrencyService.list();
        if (tradingCurrencies != null) {
            for (TradingCurrency tradingCurrency : tradingCurrencies) {
                result.getCurrencies().getCurrency().add(new CurrencyDto(tradingCurrency));
            }
        }

        List<TradingCountry> tradingCountries = tradingCountryService.list();
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
     * 
     * @return instance of GetInvoicingConfigurationResponseDto
     * @throws MeveoApiException meveo exception.
     */
    public GetInvoicingConfigurationResponseDto getInvoicingConfiguration() throws MeveoApiException {

        if (!(currentUser.hasRole("apiAccess") || currentUser.hasRole("superAdminManagement")
                || ((currentUser.hasRole("administrationVisualization") || currentUser.hasRole("billingVisualization") || currentUser.hasRole("catalogVisualization"))))) {
            throw new ActionForbiddenException("User has no permission to access provider");
        }

        GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

        // calendar
        List<Calendar> calendars = calendarService.list();
        if (calendars != null) {
            for (Calendar calendar : calendars) {
                result.getCalendars().getCalendar().add(new CalendarDto(calendar));
            }
        }

        // tax
        List<Tax> taxes = taxService.list();
        if (taxes != null) {
            for (Tax tax : taxes) {
                result.getTaxes().getTax().add(new TaxDto(tax, entityToDtoConverter.getCustomFieldsDTO(tax, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), false));
            }
        }

        // invoice categories
        List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list();
        if (invoiceCategories != null) {
            for (InvoiceCategory invoiceCategory : invoiceCategories) {
                result.getInvoiceCategories().getInvoiceCategory().add(new InvoiceCategoryDto(invoiceCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        // invoice sub-categories
        List<InvoiceSubCategory> invoiceSubCategories = invoiceSubCategoryService.list();
        if (invoiceSubCategories != null) {
            for (InvoiceSubCategory invoiceSubCategory : invoiceSubCategories) {
                result.getInvoiceSubCategories().getInvoiceSubCategory()
                    .add(new InvoiceSubCategoryDto(invoiceSubCategory, entityToDtoConverter.getCustomFieldsDTO(invoiceSubCategory, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        // billingCycle
        List<BillingCycle> billingCycles = billingCycleService.list();
        if (billingCycles != null) {
            for (BillingCycle billingCycle : billingCycles) {
                result.getBillingCycles().getBillingCycle().add(new BillingCycleDto(billingCycle, entityToDtoConverter.getCustomFieldsDTO(billingCycle, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        // terminationReasons
        List<SubscriptionTerminationReason> terminationReasons = terminationReasonService.list();
        if (terminationReasons != null) {
            for (SubscriptionTerminationReason terminationReason : terminationReasons) {
                result.getTerminationReasons().getTerminationReason().add(new TerminationReasonDto(terminationReason));
            }
        }

        return result;
    }

    public GetCustomerConfigurationResponseDto getCustomerConfiguration() throws MeveoApiException {

        GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

        // customerBrands
        List<CustomerBrand> customerBrands = customerBrandService.list();
        if (customerBrands != null) {
            for (CustomerBrand customerBrand : customerBrands) {
                result.getCustomerBrands().getCustomerBrand().add(new CustomerBrandDto(customerBrand));
            }
        }

        // customerCategories
        List<CustomerCategory> customerCategories = customerCategoryService.list();
        if (customerCategories != null) {
            for (CustomerCategory customerCategory : customerCategories) {
                result.getCustomerCategories().getCustomerCategory().add(new CustomerCategoryDto(customerCategory));
            }
        }

        // titles
        List<Title> titles = titleService.list();
        if (titles != null) {
            for (Title title : titles) {
                result.getTitles().getTitle().add(new TitleDto(title, null));
            }
        }

        return result;
    }

    public GetCustomerAccountConfigurationResponseDto getCustomerAccountConfiguration() throws MeveoApiException {

        GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

        List<CreditCategory> creditCategories = creditCategoryService.list();
        for (CreditCategory cc : creditCategories) {
            result.getCreditCategories().getCreditCategory().add(new CreditCategoryDto(cc));
        }

        return result;
    }

    public void updateProviderCF(ProviderDto postData) throws MeveoApiException, BusinessException {

        if (!(currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationManagement")))) {
            throw new ActionForbiddenException("User has no permission to manage provider ");
        }

        Provider provider = providerService.findById(appProvider.getId());
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), provider, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        provider = providerService.update(provider);
    }

    public ProviderDto findProviderCF() throws MeveoApiException {

        Provider provider = providerService.findById(appProvider.getId());
        if (currentUser.hasRole("apiAccess") || currentUser.hasRole("superAdminManagement") || (currentUser.hasRole("administrationVisualization"))) {
            return new ProviderDto(provider, entityToDtoConverter.getCustomFieldsDTO(provider, CustomFieldInheritanceEnum.INHERIT_NO_MERGE), false);
        } else {
            throw new ActionForbiddenException("User has no permission to access provider");
        }
    }

    public Provider fromDto(ProviderDto postData, Provider entity) throws MeveoApiException {

        Provider provider = null;
        if (entity == null) {
            provider = new Provider();
        } else {
            provider = entity;
        }
        if (!StringUtils.isBlank(postData.getCode())) {
            provider.setCode(postData.getCode().toUpperCase());
        }

        if (!StringUtils.isBlank(postData.getDescription())) {
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
        if (postData.isMultiCurrency() != null) {
            provider.setMulticurrencyFlag(postData.isMultiCurrency());
        }
        if (postData.isMultiCountry() != null) {
            provider.setMulticountryFlag(postData.isMultiCountry());
        }
        if (postData.isMultiLanguage() != null) {
            provider.setMultilanguageFlag(postData.isMultiLanguage());
        }
        if (!StringUtils.isBlank(postData.getUserAccount())) {
            UserAccount ua = userAccountService.findByCode(postData.getUserAccount());
            provider.setUserAccount(ua);
        }
        if (postData.isEnterprise() != null) {
            provider.setEntreprise(postData.isEnterprise());
        }
        if (postData.isLevelDuplication() != null) {
            provider.setLevelDuplication(postData.isLevelDuplication());
        }
        if (postData.getRounding() != null) {
            provider.setRounding(postData.getRounding());
        }
        if (postData.getRoundingMode() != null) {
            provider.setRoundingMode(postData.getRoundingMode());
        }
        if (postData.getInvoiceRounding() != null) {
            provider.setInvoiceRounding(postData.getInvoiceRounding());
        }
        if (postData.getInvoiceRoundingMode() != null) {
            provider.setInvoiceRoundingMode(postData.getInvoiceRoundingMode());
        }
        if (postData.getPrepaidReservationExpirationDelayinMillisec() != null) {
            provider.setPrepaidReservationExpirationDelayinMillisec(postData.getPrepaidReservationExpirationDelayinMillisec());
        }
        if (!StringUtils.isBlank(postData.getDiscountAccountingCode())) {
            provider.setDiscountAccountingCode(postData.getDiscountAccountingCode());
        }
        if (!StringUtils.isBlank(postData.getEmail())) {
            provider.setEmail(postData.getEmail());
        }
        if (postData.getBankCoordinates() != null) {
            if (provider.getBankCoordinates() == null) {
                provider.setBankCoordinates(new BankCoordinates());
            }
            BankCoordinates bankCoordinates = provider.getBankCoordinates();
            if (postData.getBankCoordinates().getBankCode() != null) {
                bankCoordinates.setBankCode(postData.getBankCoordinates().getBankCode());
            }
            if (postData.getBankCoordinates().getBranchCode() != null) {
                bankCoordinates.setBranchCode(postData.getBankCoordinates().getBranchCode());
            }
            if (postData.getBankCoordinates().getAccountNumber() != null) {
                bankCoordinates.setAccountNumber(postData.getBankCoordinates().getAccountNumber());
            }
            if (postData.getBankCoordinates().getKey() != null) {
                bankCoordinates.setKey(postData.getBankCoordinates().getKey());
            }
            if (postData.getBankCoordinates().getIban() != null) {
                bankCoordinates.setIban(postData.getBankCoordinates().getIban());
            }
            if (postData.getBankCoordinates().getBic() != null) {
                bankCoordinates.setBic(postData.getBankCoordinates().getBic());
            }
            if (postData.getBankCoordinates().getAccountOwner() != null) {
                bankCoordinates.setAccountOwner(postData.getBankCoordinates().getAccountOwner());
            }
            if (postData.getBankCoordinates().getBankName() != null) {
                bankCoordinates.setBankName(postData.getBankCoordinates().getBankName());
            }
            if (postData.getBankCoordinates().getBankId() != null) {
                bankCoordinates.setBankId(postData.getBankCoordinates().getBankId());
            }
            if (postData.getBankCoordinates().getIssuerNumber() != null) {
                bankCoordinates.setIssuerNumber(postData.getBankCoordinates().getIssuerNumber());
            }
            if (postData.getBankCoordinates().getIssuerName() != null) {
                bankCoordinates.setIssuerName(postData.getBankCoordinates().getIssuerName());
            }
            if (postData.getBankCoordinates().getIcs() != null) {
                bankCoordinates.setIcs(postData.getBankCoordinates().getIcs());
            }
        }
        if (postData.isRecognizeRevenue() != null) {
            provider.setRecognizeRevenue(postData.isRecognizeRevenue());
        }

        InvoiceConfigurationDto invoiceConfigurationDto = postData.getInvoiceConfiguration();
        if (invoiceConfigurationDto != null) {
            InvoiceConfiguration invoiceConfiguration = provider.getInvoiceConfiguration();
            if (invoiceConfiguration == null) {
                invoiceConfiguration = new InvoiceConfiguration();
                provider.setInvoiceConfiguration(invoiceConfiguration);
            }
            if (invoiceConfigurationDto.getDisplaySubscriptions() != null) {
                invoiceConfiguration.setDisplaySubscriptions(invoiceConfigurationDto.getDisplaySubscriptions());
            }
            if (invoiceConfigurationDto.getDisplayServices() != null) {
                invoiceConfiguration.setDisplayServices(invoiceConfigurationDto.getDisplayServices());
            }
            if (invoiceConfigurationDto.getDisplayOffers() != null) {
                invoiceConfiguration.setDisplayOffers(invoiceConfigurationDto.getDisplayOffers());
            }
            if (invoiceConfigurationDto.getDisplayEdrs() != null) {
                invoiceConfiguration.setDisplayEdrs(invoiceConfigurationDto.getDisplayEdrs());
            }
            if (invoiceConfigurationDto.getDisplayProvider() != null) {
                invoiceConfiguration.setDisplayProvider(invoiceConfigurationDto.getDisplayProvider());
            }
            if (invoiceConfigurationDto.getDisplayCfAsXML() != null) {
                invoiceConfiguration.setDisplayCfAsXML(invoiceConfigurationDto.getDisplayCfAsXML());
            }
            if (invoiceConfigurationDto.getDisplayPricePlans() != null) {
                invoiceConfiguration.setDisplayPricePlans(invoiceConfigurationDto.getDisplayPricePlans());
            }
            if (invoiceConfigurationDto.getDisplayDetail() != null) {
                invoiceConfiguration.setDisplayDetail(invoiceConfigurationDto.getDisplayDetail());
            }
            if (invoiceConfigurationDto.getDisplayFreeTransacInInvoice() != null) {
                provider.setDisplayFreeTransacInInvoice(invoiceConfigurationDto.getDisplayFreeTransacInInvoice());
            }
            if (invoiceConfigurationDto.getDisplayBillingCycle() != null) {
                invoiceConfiguration.setDisplayBillingCycle(invoiceConfigurationDto.getDisplayBillingCycle());
            }
            if (invoiceConfigurationDto.getDisplayOrders() != null) {
                invoiceConfiguration.setDisplayOrders(invoiceConfigurationDto.getDisplayOrders());
            }
            if (invoiceConfigurationDto.getCurrentInvoiceNb() != null) {
                invoiceConfiguration.setCurrentInvoiceNb(invoiceConfigurationDto.getCurrentInvoiceNb());
            }
            if (invoiceConfigurationDto.getDisplayWalletOperations() != null) {
                invoiceConfiguration.setDisplayWalletOperations(invoiceConfigurationDto.getDisplayWalletOperations());
            }
        }
        return provider;
    }

    /**
     * New tenant/provider creation
     * 
     * @param postData postData Provider Dto
     * @throws MeveoApiException MeveoApiException
     * @throws BusinessException BusinessException
     */
    public void createTenant(ProviderDto postData) throws MeveoApiException, BusinessException {

        // Tenant/provider management is available for superadmin user only of main provider
        if (!ParamBean.isMultitenancyEnabled()) {
            throw new ActionForbiddenException("Multitenancy is not enabled");

        } else if (currentUser.getProviderCode() != null) {
            throw new ActionForbiddenException("Tenants should be managed by a main tenant's super administrator");

        } else if (!currentUser.hasRole("superAdminManagement")) {
            throw new ActionForbiddenException("User has no permission to manage tenants ");
        }

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        // check if provider already exists
        if (providerService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Provider.class, postData.getCode());
        }

        Provider provider = new Provider();
        provider.setCode(postData.getCode());
        provider.setDescription(postData.getDescription());

        providerService.create(provider);
    }

    /**
     * List tenants/providers
     * 
     * @return A list of tenants/providers
     * @throws ActionForbiddenException action forbidden exception
     * @throws InvalidParameterException invalid parameter exception.
     */
    public ProvidersDto listTenants() throws ActionForbiddenException, InvalidParameterException {

        // Tenant/provider management is available for superadmin user only of main provider
        if (!ParamBean.isMultitenancyEnabled()) {
            throw new ActionForbiddenException("Multitenancy is not enabled");

        } else if (currentUser.getProviderCode() != null) {
            throw new ActionForbiddenException("Tenants should be managed by a main tenant's super administrator");

        } else if (!currentUser.hasRole("superAdminManagement")) {
            throw new ActionForbiddenException("User has no permission to manage tenants ");
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, null, Provider.class);

        Long totalCount = providerService.count(paginationConfig);

        ProvidersDto result = new ProvidersDto();
        result.setPaging(new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<Provider> providers = providerService.list(paginationConfig);
            for (Provider provider : providers) {
                result.getProviders().add(new ProviderDto(provider, appProvider.getId().equals(provider.getId()) ? entityToDtoConverter.getCustomFieldsDTO(provider, CustomFieldInheritanceEnum.INHERIT_NO_MERGE) : null));
            }
        }

        return result;
    }

    /**
     * Remove tenant/provider
     * 
     * @param providerCode providerCode
     * @throws MeveoApiException MeveoApiException
     */
    public void removeTenant(String providerCode) throws MeveoApiException {

        // Tenant/provider management is available for superadmin user only of main provider
        if (!ParamBean.isMultitenancyEnabled()) {
            throw new ActionForbiddenException("Multitenancy is not enabled");

        } else if (currentUser.getProviderCode() != null) {
            throw new ActionForbiddenException("Tenants should be managed by a main tenant's super administrator");

        } else if (!currentUser.hasRole("superAdminManagement")) {
            throw new ActionForbiddenException("User has no permission to manage tenants ");
        }

        if (StringUtils.isBlank(providerCode)) {
            missingParameters.add("providerCode");
            handleMissingParameters();
        }

        if (appProvider.getCode().equalsIgnoreCase(providerCode)) {
            throw new BusinessApiException("Can not remove a main provider");
        }

        Provider provider = providerService.findByCode(providerCode);
        if (provider == null) {
            throw new EntityDoesNotExistsException(Provider.class, providerCode);
        }

        try {
            providerService.remove(provider);
            providerService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(Provider.class, providerCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }
}