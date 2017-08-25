package org.meveo.api.ws.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.BillingCycleApi;
import org.meveo.api.CalendarApi;
import org.meveo.api.CountryApi;
import org.meveo.api.CurrencyApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.InvoiceCategoryApi;
import org.meveo.api.InvoiceSubCategoryApi;
import org.meveo.api.InvoiceSubCategoryCountryApi;
import org.meveo.api.InvoiceTypeApi;
import org.meveo.api.LanguageApi;
import org.meveo.api.MultiLanguageFieldApi;
import org.meveo.api.OccTemplateApi;
import org.meveo.api.PermissionApi;
import org.meveo.api.ProviderApi;
import org.meveo.api.RoleApi;
import org.meveo.api.TaxApi;
import org.meveo.api.TerminationReasonApi;
import org.meveo.api.UserApi;
import org.meveo.api.account.ProviderContactApi;
import org.meveo.api.account.SellerApi;
import org.meveo.api.communication.EmailTemplateApi;
import org.meveo.api.communication.MeveoInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.CalendarsDto;
import org.meveo.api.dto.CatMessagesDto;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.InvoiceCategoryDto;
import org.meveo.api.dto.InvoiceSubCategoryCountryDto;
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.response.DescriptionsResponseDto;
import org.meveo.api.dto.response.GetBillingCycleResponse;
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.GetCountryResponse;
import org.meveo.api.dto.response.GetCurrencyResponse;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetDescriptionsResponse;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.dto.response.GetInvoiceSubCategoryCountryResponse;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import org.meveo.api.dto.response.GetInvoiceTypeResponse;
import org.meveo.api.dto.response.GetInvoiceTypesResponse;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetLanguageResponse;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetRoleResponse;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;
import org.meveo.api.dto.response.GetTerminationReasonResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.dto.response.PermissionResponseDto;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.api.dto.response.UserHierarchyLevelResponseDto;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;
import org.meveo.api.dto.response.account.ProviderContactsResponseDto;
import org.meveo.api.dto.response.communication.EmailTemplateResponseDto;
import org.meveo.api.dto.response.communication.EmailTemplatesResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstanceResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstancesResponseDto;
import org.meveo.api.hierarchy.UserHierarchyLevelApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.SettingsWs;

/**
 * @author Edward P. Legaspi
 **/
@SuppressWarnings("deprecation")
@WebService(serviceName = "SettingsWs", endpointInterface = "org.meveo.api.ws.SettingsWs")
@Interceptors({ WsRestApiInterceptor.class })
public class SettingsWsImpl extends BaseWs implements SettingsWs {

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CountryApi countryApi;

    @Inject
    private CurrencyApi currencyApi;

    @Inject
    private LanguageApi languageApi;

    @Inject
    private InvoiceCategoryApi invoiceCategoryApi;

    @Inject
    private InvoiceSubCategoryCountryApi invoiceSubCategoryCountryApi;

    @Inject
    private InvoiceSubCategoryApi invoiceSubCategoryApi;

    @Inject
    private ProviderApi providerApi;

    @Inject
    private SellerApi sellerApi;

    @Inject
    private TaxApi taxApi;

    @Inject
    private UserApi userApi;

    @Inject
    private BillingCycleApi billingCycleApi;

    @Inject
    private CalendarApi calendarApi;

    @Inject
    private OccTemplateApi occTemplateApi;

    @Inject
    private PermissionApi permissionApi;

    @Inject
    private RoleApi roleApi;

    @Inject
    private MultiLanguageFieldApi multilanguageFieldApi;

    @Inject
    private TerminationReasonApi terminationReasonApi;

    @Inject
    private InvoiceTypeApi invoiceTypeApi;

    @Inject
    private ProviderContactApi providerContactApi;

    @Inject
    private EmailTemplateApi emailTemplateApi;

    @Inject
    private MeveoInstanceApi meveoInstanceApi;

    @Inject
    private UserHierarchyLevelApi userHierarchyLevelApi;

    @Deprecated
    @Override
    public ActionStatus createCountry(CountryDto countryDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.create(countryDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public GetCountryResponse findCountry(String countryCode) {
        GetCountryResponse result = new GetCountryResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCountry(countryApi.find(countryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus removeCountry(String countryCode, String currencyCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.remove(countryCode, currencyCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus updateCountry(CountryDto countryDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.update(countryDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus createCurrency(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public GetCurrencyResponse findCurrency(String currencyCode) {
        GetCurrencyResponse result = new GetCurrencyResponse();

        try {
            result.setCurrency(currencyApi.find(currencyCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus removeCurrency(String currencyCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.remove(currencyCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus updateCurrency(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus createOrUpdateCurrency(CurrencyDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            currencyApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createInvoiceCategory(InvoiceCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateInvoiceCategory(InvoiceCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetInvoiceCategoryResponse findInvoiceCategory(String invoiceCategoryCode) {
        GetInvoiceCategoryResponse result = new GetInvoiceCategoryResponse();

        try {
            result.setInvoiceCategory(invoiceCategoryApi.find(invoiceCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeInvoiceCategory(String invoiceCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.remove(invoiceCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createInvoiceSubCategoryCountry(InvoiceSubCategoryCountryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryCountryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateInvoiceSubCategoryCountry(InvoiceSubCategoryCountryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryCountryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetInvoiceSubCategoryCountryResponse findInvoiceSubCategoryCountry(String invoiceSubCategoryCode, String country) {
        GetInvoiceSubCategoryCountryResponse result = new GetInvoiceSubCategoryCountryResponse();

        try {
            result.setInvoiceSubCategoryCountryDto(invoiceSubCategoryCountryApi.find(invoiceSubCategoryCode, country));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeInvoiceSubCategoryCountry(String invoiceSubCategoryCode, String country) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryCountryApi.remove(invoiceSubCategoryCode, country);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createInvoiceSubCategory(InvoiceSubCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateInvoiceSubCategory(InvoiceSubCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetInvoiceSubCategoryResponse findInvoiceSubCategory(String invoiceSubCategoryCode) {
        GetInvoiceSubCategoryResponse result = new GetInvoiceSubCategoryResponse();

        try {
            result.setInvoiceSubCategory(invoiceSubCategoryApi.find(invoiceSubCategoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeInvoiceSubCategory(String invoiceSubCategoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryApi.remove(invoiceSubCategoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createLanguage(LanguageDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetLanguageResponse findLanguage(String languageCode) {
        GetLanguageResponse result = new GetLanguageResponse();

        try {
            result.setLanguage(languageApi.find(languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeLanguage(String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.remove(languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateLanguage(LanguageDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus createProvider(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public GetProviderResponse findProvider(String providerCode) {
        GetProviderResponse result = new GetProviderResponse();

        try {
            result.setProvider(providerApi.find());

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus updateProvider(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createSeller(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateSeller(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetSellerResponse findSeller(String sellerCode) {
        GetSellerResponse result = new GetSellerResponse();

        try {
            result.setSeller(sellerApi.find(sellerCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeSeller(String sellerCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            sellerApi.remove(sellerCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public SellerCodesResponseDto listSellerCodes() {
        SellerCodesResponseDto result = new SellerCodesResponseDto();

        try {
            result = sellerApi.listSellerCodes();
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createTax(TaxDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateTax(TaxDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTaxResponse findTax(String taxCode) {
        GetTaxResponse result = new GetTaxResponse();

        try {
            result.setTax(taxApi.find(taxCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeTax(String taxCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            taxApi.remove(taxCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTaxesResponse listTaxes() {
        GetTaxesResponse result = new GetTaxesResponse();

        try {
            result.setTaxesDto(taxApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createUser(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateUser(UserDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeUser(String username) {
        ActionStatus result = new ActionStatus();

        try {
            userApi.remove(username);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUserResponse findUser(String username) {
        GetUserResponse result = new GetUserResponse();

        try {
            result.setUser(userApi.find(username));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createBillingCycle(BillingCycleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            billingCycleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBillingCycle(BillingCycleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            billingCycleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBillingCycleResponse findBillingCycle(String billingCycleCode) {
        GetBillingCycleResponse result = new GetBillingCycleResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setBillingCycle(billingCycleApi.find(billingCycleCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeBillingCycle(String billingCycleCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            billingCycleApi.remove(billingCycleCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCalendar(CalendarDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCalendar(CalendarDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCalendarResponse findCalendar(String calendarCode) {
        GetCalendarResponse result = new GetCalendarResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCalendar(calendarApi.find(calendarCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ListCalendarResponse listCalendars() {
        ListCalendarResponse result = new ListCalendarResponse();
        CalendarsDto calendarsDto = new CalendarsDto();

        try {
            calendarsDto.setCalendar(calendarApi.list());
            result.setCalendars(calendarsDto);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeCalendar(String calendarCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.remove(calendarCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public SellerResponseDto listSeller() {
        SellerResponseDto result = new SellerResponseDto();

        try {
            result.setSellers(sellerApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetTradingConfigurationResponseDto findTradingConfiguration(String providerCode) {
        GetTradingConfigurationResponseDto result = new GetTradingConfigurationResponseDto();

        try {
            result = providerApi.getTradingConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetInvoicingConfigurationResponseDto findInvoicingConfiguration(String providerCode) {
        GetInvoicingConfigurationResponseDto result = new GetInvoicingConfigurationResponseDto();

        try {
            result = providerApi.getInvoicingConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCustomerConfigurationResponseDto findCustomerConfiguration(String providerCode) {
        GetCustomerConfigurationResponseDto result = new GetCustomerConfigurationResponseDto();

        try {
            result = providerApi.getCustomerConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration(String providerCode) {
        GetCustomerAccountConfigurationResponseDto result = new GetCustomerAccountConfigurationResponseDto();

        try {
            result = providerApi.getCustomerAccountConfiguration();

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOccTemplate(OccTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateOccTemplate(OccTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetOccTemplateResponseDto findOccTemplate(String occTemplateCode) {
        GetOccTemplateResponseDto result = new GetOccTemplateResponseDto();

        try {
            result.setOccTemplate(occTemplateApi.find(occTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeOccTemplate(String occTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.remove(occTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCustomFieldTemplate(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.create(postData, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCustomFieldTemplate(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.update(postData, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeCustomFieldTemplate(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.remove(customFieldTemplateCode, appliesTo);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomFieldTemplateReponseDto findCustomFieldTemplate(String customFieldTemplateCode, String appliesTo) {
        GetCustomFieldTemplateReponseDto result = new GetCustomFieldTemplateReponseDto();

        try {
            result.setCustomFieldTemplate(customFieldTemplateApi.find(customFieldTemplateCode, appliesTo));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCountry(CountryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            countryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateBillingCycle(BillingCycleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            billingCycleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCalendar(CalendarDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            calendarApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateTax(TaxDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            taxApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateSeller(SellerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            sellerApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateCustomFieldTemplate(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customFieldTemplateApi.createOrUpdate(postData, null);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateInvoiceCategory(InvoiceCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateInvoiceSubCategoryCountry(InvoiceSubCategoryCountryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryCountryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateOccTemplate(OccTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            occTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus createOrUpdateProvider(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateUser(UserDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    public ActionStatus createOrUpdateInvoiceSubCategory(InvoiceSubCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            invoiceSubCategoryApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;

    }

    @Override
    public PermissionResponseDto listPermissions() {
        PermissionResponseDto result = new PermissionResponseDto();
        try {
            result.setPermissionsDto(permissionApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createRole(RoleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateRole(RoleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeRole(String name, String provider) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.remove(name, provider);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetRoleResponse findRole(String name, String provider) {
        GetRoleResponse result = new GetRoleResponse();
        try {
            result.setRoleDto(roleApi.find(name, provider));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetRoleResponse findRole4_2(String name) {
        GetRoleResponse result = new GetRoleResponse();
        try {
            result.setRoleDto(roleApi.find(name, null));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateRole(RoleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            roleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createDescriptions(CatMessagesDto postData) {
        return updateTranslations(postData);
    }

    @Override
    public ActionStatus updateDescriptions(CatMessagesDto postData) {
        return updateTranslations(postData);
    }

    @Override
    public ActionStatus updateTranslations(CatMessagesDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            multilanguageFieldApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetDescriptionsResponse findDescriptions(String entityClass, String code, String languageCode) {
        return findTranslations(entityClass, languageCode, null, null, null, languageCode);
    }

    @Override
    public GetDescriptionsResponse findTranslations(String entityClass, String code, Date validFrom, Date validTo, String fieldname, String languageCode) {

        GetDescriptionsResponse result = new GetDescriptionsResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCatMessagesDto(multilanguageFieldApi.find(entityClass, code, validFrom, validTo, fieldname, languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeDescriptions(String entityClass, String code, String languageCode) {
        return removeTranslations(entityClass, code, null, null, null, languageCode);
    }

    @Override
    public ActionStatus removeTranslations(String entityClass, String code, Date validFrom, Date validTo, String fieldname, String languageCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            multilanguageFieldApi.remove(entityClass, code, validFrom, validTo, fieldname, languageCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateDescriptions(CatMessagesDto postData) {
        return updateTranslations(postData);
    }

    @Override
    public DescriptionsResponseDto listDescriptions() {
        return listTranslations(null, null, null);
    }

    @Override
    public DescriptionsResponseDto listTranslations(String entityClass, String fieldname, String languageCode) {

        DescriptionsResponseDto result = new DescriptionsResponseDto();

        try {
            result.setCatMessages(multilanguageFieldApi.list(entityClass, fieldname, languageCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createTerminationReason(TerminationReasonDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateTerminationReason(TerminationReasonDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateTerminationReason(TerminationReasonDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeTerminationReason(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            terminationReasonApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetTerminationReasonResponse findTerminationReason(String code) {
        GetTerminationReasonResponse result = new GetTerminationReasonResponse();

        try {
            result.getTerminationReason().add(terminationReasonApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetTerminationReasonResponse listTerminationReason() {
        GetTerminationReasonResponse result = new GetTerminationReasonResponse();

        try {
            result.getTerminationReason().addAll(terminationReasonApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Deprecated
    @Override
    public ActionStatus createOrUpdateLanguage(LanguageDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            languageApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createInvoiceType(InvoiceTypeDto invoiceTypeDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            invoiceTypeApi.create(invoiceTypeDto);
        } catch (Exception e) {
            super.processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus updateInvoiceType(InvoiceTypeDto invoiceTypeDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            invoiceTypeApi.update(invoiceTypeDto);
        } catch (Exception e) {
            super.processException(e, result);
        }
        return result;
    }

    @Override
    public GetInvoiceTypeResponse findInvoiceType(String invoiceTypeCode) {
        GetInvoiceTypeResponse result = new GetInvoiceTypeResponse();
        result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
        try {
            result.setInvoiceTypeDto(invoiceTypeApi.find(invoiceTypeCode));
        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus removeInvoiceType(String invoiceTypeCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            invoiceTypeApi.remove(invoiceTypeCode);
        } catch (Exception e) {
            super.processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdateInvoiceType(InvoiceTypeDto invoiceTypeDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            invoiceTypeApi.createOrUpdate(invoiceTypeDto);
        } catch (Exception e) {
            super.processException(e, result);
        }
        return result;
    }

    @Override
    public GetInvoiceTypesResponse listInvoiceTypes() {
        GetInvoiceTypesResponse result = new GetInvoiceTypesResponse();
        result.setActionStatus(new ActionStatus(ActionStatusEnum.SUCCESS, ""));
        try {
            result.setInvoiceTypesDto(invoiceTypeApi.list());
        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus updateProviderCF(ProviderDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerApi.updateProviderCF(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetProviderResponse findProviderCF(String providerCode) {
        GetProviderResponse result = new GetProviderResponse();

        try {
            result.setProvider(providerApi.findProviderCF());

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createProviderContact(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.create(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateProviderContact(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.update(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProviderContactResponseDto findProviderContact(String providerContactCode) {
        ProviderContactResponseDto result = new ProviderContactResponseDto();

        try {
            result.setProviderContact(providerContactApi.find(providerContactCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeProviderContact(String providerContactCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.remove(providerContactCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ProviderContactsResponseDto listProviderContacts() {
        ProviderContactsResponseDto result = new ProviderContactsResponseDto();

        try {
            result.setProviderContacts(providerContactApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdateProviderContact(ProviderContactDto providerContactDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            providerContactApi.createOrUpdate(providerContactDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus createEmailTemplate(EmailTemplateDto emailTemplateDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailTemplateApi.create(emailTemplateDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateEmailTemplate(EmailTemplateDto emailTemplateDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailTemplateApi.update(emailTemplateDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public EmailTemplateResponseDto findEmailTemplate(String emailTemplateCode) {
        EmailTemplateResponseDto result = new EmailTemplateResponseDto();

        try {
            result.setEmailTemplate(emailTemplateApi.find(emailTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeEmailTemplate(String emailTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailTemplateApi.remove(emailTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public EmailTemplatesResponseDto listEmailTemplates() {
        EmailTemplatesResponseDto result = new EmailTemplatesResponseDto();

        try {
            result.setEmailTemplates(emailTemplateApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateEmailTemplate(EmailTemplateDto emailTemplateDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            emailTemplateApi.createOrUpdate(emailTemplateDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createMeveoInstance(MeveoInstanceDto meveoInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            log.debug("start to create in meveoInstanceApi");
            meveoInstanceApi.create(meveoInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus updateMeveoInstance(MeveoInstanceDto meveoInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.update(meveoInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoInstanceResponseDto findMeveoInstance(String meveoInstanceCode) {
        MeveoInstanceResponseDto result = new MeveoInstanceResponseDto();
        try {
            result.setMeveoInstance(meveoInstanceApi.find(meveoInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeMeveoInstance(String meveoInstanceCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.remove(meveoInstanceCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoInstancesResponseDto listMeveoInstances() {
        MeveoInstancesResponseDto result = new MeveoInstancesResponseDto();

        try {
            result.setMeveoInstances(meveoInstanceApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateMeveoInstance(MeveoInstanceDto meveoInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            meveoInstanceApi.createOrUpdate(meveoInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createUserHierarchyLevel(UserHierarchyLevelDto userHierarchyLevelDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.create(userHierarchyLevelDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateUserHierarchyLevel(UserHierarchyLevelDto userHierarchyLevelDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.update(userHierarchyLevelDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus removeUserHierarchyLevel(String hierarchyLevelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.remove(hierarchyLevelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public UserHierarchyLevelResponseDto findUserHierarchyLevel(String hierarchyLevelCode) {
        UserHierarchyLevelResponseDto result = new UserHierarchyLevelResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setUserHierarchyLevel(userHierarchyLevelApi.find(hierarchyLevelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateUserHierarchyLevel(UserHierarchyLevelDto userHierarchyLevelDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            userHierarchyLevelApi.createOrUpdate(userHierarchyLevelDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }
}