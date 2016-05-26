package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.CalendarDto;
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
import org.meveo.api.dto.User4_2Dto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
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
import org.meveo.api.dto.response.GetUser4_2Response;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.dto.response.PermissionResponseDto;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@SuppressWarnings("deprecation")
@WebService
public interface SettingsWs extends IBaseWs {

	// provider

	@Deprecated
	@WebMethod
	public ActionStatus createProvider(@WebParam(name = "provider") ProviderDto postData);

	@Deprecated
	@WebMethod
	public GetProviderResponse findProvider(@WebParam(name = "providerCode") String providerCode);

	@Deprecated
	@WebMethod
	public ActionStatus updateProvider(@WebParam(name = "provider") ProviderDto postData);

	@Deprecated
	@WebMethod
	public ActionStatus createOrUpdateProvider(@WebParam(name = "provider") ProviderDto postData);

	// configuration

	@WebMethod
	GetTradingConfigurationResponseDto findTradingConfiguration(@WebParam(name = "providerCode") String providerCode);

	@WebMethod
	GetInvoicingConfigurationResponseDto findInvoicingConfiguration(@WebParam(name = "providerCode") String providerCode);

	@WebMethod
	GetCustomerConfigurationResponseDto findCustomerConfiguration(@WebParam(name = "providerCode") String providerCode);

	@WebMethod
	GetCustomerAccountConfigurationResponseDto findCustomerAccountConfiguration(@WebParam(name = "providerCode") String providerCode);

	// user

	@WebMethod
	public ActionStatus createUser4_2(@WebParam(name = "user") User4_2Dto postData);
	
	@WebMethod
	public ActionStatus createUser(@WebParam(name = "user") UserDto postData);

	@WebMethod
	public ActionStatus updateUser4_2(@WebParam(name = "user") User4_2Dto postData);
	
	@WebMethod
	public ActionStatus updateUser(@WebParam(name = "user") UserDto postData);

	@WebMethod
	public ActionStatus removeUser(@WebParam(name = "username") String username);

	@WebMethod
	public GetUserResponse findUser(@WebParam(name = "username") String username);
	
	@WebMethod
	public GetUser4_2Response findUser4_2(@WebParam(name = "username") String username);

	@WebMethod
	public ActionStatus createOrUpdateUser4_2(@WebParam(name = "user") User4_2Dto postData);
	
	@WebMethod
	public ActionStatus createOrUpdateUser(@WebParam(name = "user") UserDto postData);

	// seller

	@WebMethod
	public ActionStatus createSeller(@WebParam(name = "seller") SellerDto postData);

	@WebMethod
	public ActionStatus updateSeller(@WebParam(name = "seller") SellerDto postData);

	@WebMethod
	public GetSellerResponse findSeller(@WebParam(name = "sellerCode") String sellerCode);

	@WebMethod
	public ActionStatus removeSeller(@WebParam(name = "sellerCode") String sellerCode);

	@WebMethod
	public SellerResponseDto listSeller();

	@WebMethod
	SellerCodesResponseDto listSellerCodes();

	@WebMethod
	public ActionStatus createOrUpdateSeller(@WebParam(name = "seller") SellerDto postData);

	// tradingLanguage

	@Deprecated
	@WebMethod
	public ActionStatus createLanguage(@WebParam(name = "language") LanguageDto postData);

	@Deprecated
	@WebMethod
	public GetLanguageResponse findLanguage(@WebParam(name = "languageCode") String languageCode);

	@Deprecated
	@WebMethod
	public ActionStatus removeLanguage(@WebParam(name = "languageCode") String languageCode);

	@Deprecated
	@WebMethod
	public ActionStatus updateLanguage(@WebParam(name = "language") LanguageDto postData);

	@Deprecated
	@WebMethod
	public ActionStatus createOrUpdateLanguage(@WebParam(name = "language") LanguageDto postData);

	// tradingCountry
	@Deprecated
	@WebMethod
	ActionStatus createCountry(@WebParam(name = "country") CountryDto countryDto);

	@Deprecated
	@WebMethod
	GetCountryResponse findCountry(@WebParam(name = "countryCode") String countryCode);

	@Deprecated
	@WebMethod
	ActionStatus removeCountry(@WebParam(name = "countryCode") String countryCode, @WebParam(name = "currencyCode") String currencyCode);

	@Deprecated
	@WebMethod
	ActionStatus updateCountry(@WebParam(name = "country") CountryDto countryDto);

	@Deprecated
	@WebMethod
	ActionStatus createOrUpdateCountry(@WebParam(name = "country") CountryDto countryDto);

	// traingCurrency
	@Deprecated
	@WebMethod
	ActionStatus createCurrency(@WebParam(name = "currency") CurrencyDto postData);

	@Deprecated
	@WebMethod
	GetCurrencyResponse findCurrency(@WebParam(name = "currencyCode") String currencyCode);

	@Deprecated
	@WebMethod
	ActionStatus removeCurrency(@WebParam(name = "currencyCode") String currencyCode);

	@Deprecated
	@WebMethod
	ActionStatus updateCurrency(@WebParam(name = "currency") CurrencyDto postData);

	@Deprecated
	@WebMethod
	ActionStatus createOrUpdateCurrency(@WebParam(name = "currency") CurrencyDto postData);

	// tax

	@WebMethod
	public ActionStatus createTax(@WebParam(name = "tax") TaxDto postData);

	@WebMethod
	public ActionStatus updateTax(@WebParam(name = "tax") TaxDto postData);

	@WebMethod
	public GetTaxResponse findTax(@WebParam(name = "taxCode") String taxCode);

	@WebMethod
	public ActionStatus removeTax(@WebParam(name = "taxCode") String taxCode);

	@WebMethod
	public ActionStatus createOrUpdateTax(@WebParam(name = "tax") TaxDto postData);

	@WebMethod
	public GetTaxesResponse listTaxes();

	// invoice category

	@WebMethod
	public ActionStatus createInvoiceCategory(@WebParam(name = "invoiceCategory") InvoiceCategoryDto postData);

	@WebMethod
	public ActionStatus updateInvoiceCategory(@WebParam(name = "invoiceCategory") InvoiceCategoryDto postData);

	@WebMethod
	public GetInvoiceCategoryResponse findInvoiceCategory(@WebParam(name = "invoiceCategoryCode") String invoiceCategoryCode);

	@WebMethod
	public ActionStatus removeInvoiceCategory(@WebParam(name = "invoiceCategoryCode") String invoiceCategoryCode);

	@WebMethod
	public ActionStatus createOrUpdateInvoiceCategory(@WebParam(name = "invoiceCategory") InvoiceCategoryDto postData);

	// invoice sub category

	@WebMethod
	public ActionStatus createInvoiceSubCategory(@WebParam(name = "invoiceSubCategory") InvoiceSubCategoryDto postData);

	@WebMethod
	public ActionStatus updateInvoiceSubCategory(@WebParam(name = "invoiceSubCategory") InvoiceSubCategoryDto postData);

	@WebMethod
	ActionStatus createOrUpdateInvoiceSubCategory(@WebParam(name = "invoiceSubCategory") InvoiceSubCategoryDto postData);

	@WebMethod
	public GetInvoiceSubCategoryResponse findInvoiceSubCategory(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode);

	@WebMethod
	public ActionStatus removeInvoiceSubCategory(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode);

	// invoice sub category country

	@WebMethod
	public ActionStatus createInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCountry") InvoiceSubCategoryCountryDto postData);

	@WebMethod
	public ActionStatus updateInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCountry") InvoiceSubCategoryCountryDto postData);

	@WebMethod
	public GetInvoiceSubCategoryCountryResponse findInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode,
			@WebParam(name = "country") String country);

	@WebMethod
	public ActionStatus removeInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode, @WebParam(name = "country") String country);

	@WebMethod
	public ActionStatus createOrUpdateInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCountry") InvoiceSubCategoryCountryDto postData);

	// calendar

	@WebMethod
	public ActionStatus createCalendar(@WebParam(name = "calendar") CalendarDto postData);

	@WebMethod
	public ActionStatus updateCalendar(@WebParam(name = "calendar") CalendarDto postData);

	@WebMethod
	public GetCalendarResponse findCalendar(@WebParam(name = "calendarCode") String calendarCode);

	@WebMethod
	public ActionStatus removeCalendar(@WebParam(name = "calendarCode") String calendarCode);

	@WebMethod
	public ActionStatus createOrUpdateCalendar(@WebParam(name = "calendar") CalendarDto postData);

	@WebMethod
	public ListCalendarResponse listCalendars();

	// billing cycle

	@WebMethod
	public ActionStatus createBillingCycle(@WebParam(name = "billingCycle") BillingCycleDto postData);

	@WebMethod
	public ActionStatus updateBillingCycle(@WebParam(name = "billingCycle") BillingCycleDto postData);

	@WebMethod
	public GetBillingCycleResponse findBillingCycle(@WebParam(name = "billingCycleCode") String billingCycleCode);

	@WebMethod
	public ActionStatus removeBillingCycle(@WebParam(name = "billingCycleCode") String billingCycleCode);

	@WebMethod
	public ActionStatus createOrUpdateBillingCycle(@WebParam(name = "billingCycle") BillingCycleDto postData);

	// occ template

	@WebMethod
	ActionStatus createOccTemplate(@WebParam(name = "occTemplate") OccTemplateDto postData);

	@WebMethod
	ActionStatus updateOccTemplate(@WebParam(name = "occTemplate") OccTemplateDto postData);

	@WebMethod
	GetOccTemplateResponseDto findOccTemplate(@WebParam(name = "occTemplateCode") String occTemplateCode);

	@WebMethod
	ActionStatus removeOccTemplate(@WebParam(name = "occTemplateCode") String occTemplateCode);

	@WebMethod
	ActionStatus createOrUpdateOccTemplate(@WebParam(name = "occTemplate") OccTemplateDto postData);

	// custom field

	@WebMethod
	ActionStatus createCustomFieldTemplate(@WebParam(name = "customField") CustomFieldTemplateDto postData);

	@WebMethod
	ActionStatus updateCustomFieldTemplate(@WebParam(name = "customField") CustomFieldTemplateDto postData);

	@WebMethod
	ActionStatus removeCustomFieldTemplate(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

	@WebMethod
	GetCustomFieldTemplateReponseDto findCustomFieldTemplate(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode,
			@WebParam(name = "appliesTo") String appliesTo);

	@WebMethod
	ActionStatus createOrUpdateCustomFieldTemplate(@WebParam(name = "customField") CustomFieldTemplateDto postData);

	// permission

	@WebMethod
	PermissionResponseDto listPermissions();

	// role

	@WebMethod
	public ActionStatus createRole(@WebParam(name = "role") RoleDto postData);

	@WebMethod
	public ActionStatus updateRole(@WebParam(name = "role") RoleDto postData);

	@WebMethod
	public ActionStatus removeRole(@WebParam(name = "role") String name);

	@WebMethod
	public GetRoleResponse findRole(@WebParam(name = "roleName") String name);

	@WebMethod
	public ActionStatus createOrUpdateRole(@WebParam(name = "role") RoleDto postData);

	// descriptions

	@WebMethod
	public ActionStatus createDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

	@WebMethod
	public ActionStatus updateDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

	@WebMethod
	public GetDescriptionsResponse findDescriptions(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code,
			@WebParam(name = "languageCode") String languageCode);

	@WebMethod
	public ActionStatus removeDescriptions(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code,
			@WebParam(name = "languageCode") String languageCode);

	@WebMethod
	public ActionStatus createOrUpdateDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

	@WebMethod
	public DescriptionsResponseDto listDescriptions();

	/* termination reasons */

	@WebMethod
	public ActionStatus createTerminationReason(@WebParam(name = "terminationReason") TerminationReasonDto postData);

	@WebMethod
	public ActionStatus updateTerminationReason(@WebParam(name = "terminationReason") TerminationReasonDto postData);

	@WebMethod
	public ActionStatus createOrUpdateTerminationReason(@WebParam(name = "terminationReason") TerminationReasonDto postData);

	@WebMethod
	public ActionStatus removeTerminationReason(@WebParam(name = "terminationReasonCode") String code);

	@WebMethod
	public GetTerminationReasonResponse findTerminationReason(@WebParam(name = "terminationReasonCode") String code);

	@WebMethod
	public GetTerminationReasonResponse listTerminationReason();

	// InvoiceType
	@WebMethod
	public ActionStatus createInvoiceType(@WebParam(name = "invoiceType") InvoiceTypeDto invoiceTypeDto);

	@WebMethod
	public ActionStatus updateInvoiceType(@WebParam(name = "invoiceType") InvoiceTypeDto invoiceTypeDto);

	@WebMethod
	public GetInvoiceTypeResponse findInvoiceType(@WebParam(name = "invoiceTypeCode") String invoiceTypeCode);

	@WebMethod
	public ActionStatus removeInvoiceType(@WebParam(name = "invoiceTypeCode") String invoiceTypeCode);

	@WebMethod
	public ActionStatus createOrUpdateInvoiceType(@WebParam(name = "invoiceType") InvoiceTypeDto invoiceTypeDto);

	@WebMethod
	public GetInvoiceTypesResponse listInvoiceTypes();
}
