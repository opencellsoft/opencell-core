package org.meveo.api.ws;

import java.util.Date;
import java.util.List;

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
import org.meveo.api.dto.RolesDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
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
import org.meveo.api.dto.response.PagingAndFiltering;
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

/**
 * @author Edward P. Legaspi
 **/
@SuppressWarnings("deprecation")
@WebService
public interface SettingsWs extends IBaseWs {

    // provider

    @Deprecated
    @WebMethod
    ActionStatus createProvider(@WebParam(name = "provider") ProviderDto postData);

    @Deprecated
    @WebMethod
    GetProviderResponse findProvider(@WebParam(name = "providerCode") String providerCode);

    @Deprecated
    @WebMethod
    ActionStatus updateProvider(@WebParam(name = "provider") ProviderDto postData);

    @Deprecated
    @WebMethod
    ActionStatus createOrUpdateProvider(@WebParam(name = "provider") ProviderDto postData);

    @WebMethod
    ActionStatus updateProviderCF(@WebParam(name = "provider") ProviderDto postData);

    @WebMethod
    GetProviderResponse findProviderCF(@WebParam(name = "providerCode") String providerCode);

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
    ActionStatus createUser(@WebParam(name = "user") UserDto postData);

    @WebMethod
    ActionStatus updateUser(@WebParam(name = "user") UserDto postData);

    @WebMethod
    ActionStatus removeUser(@WebParam(name = "username") String username);

    @WebMethod
    GetUserResponse findUser(@WebParam(name = "username") String username);

    @WebMethod
    ActionStatus createOrUpdateUser(@WebParam(name = "user") UserDto postData);

    /**
     * List users matching a given criteria
     * 
     * @param paging Pagination and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     */
    @WebMethod
    public UsersDto listUsers(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    // seller

    @WebMethod
    ActionStatus createSeller(@WebParam(name = "seller") SellerDto postData);

    @WebMethod
    ActionStatus updateSeller(@WebParam(name = "seller") SellerDto postData);

    @WebMethod
    GetSellerResponse findSeller(@WebParam(name = "sellerCode") String sellerCode);

    @WebMethod
    ActionStatus removeSeller(@WebParam(name = "sellerCode") String sellerCode);

    @WebMethod
    SellerResponseDto listSeller();

    @WebMethod
    SellerCodesResponseDto listSellerCodes();

    @WebMethod
    ActionStatus createOrUpdateSeller(@WebParam(name = "seller") SellerDto postData);

    // tradingLanguage

    @Deprecated
    @WebMethod
    ActionStatus createLanguage(@WebParam(name = "language") LanguageDto postData);

    @Deprecated
    @WebMethod
    GetLanguageResponse findLanguage(@WebParam(name = "languageCode") String languageCode);

    @Deprecated
    @WebMethod
    ActionStatus removeLanguage(@WebParam(name = "languageCode") String languageCode);

    @Deprecated
    @WebMethod
    ActionStatus updateLanguage(@WebParam(name = "language") LanguageDto postData);

    @Deprecated
    @WebMethod
    ActionStatus createOrUpdateLanguage(@WebParam(name = "language") LanguageDto postData);

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
    ActionStatus createTax(@WebParam(name = "tax") TaxDto postData);

    @WebMethod
    ActionStatus updateTax(@WebParam(name = "tax") TaxDto postData);

    @WebMethod
    GetTaxResponse findTax(@WebParam(name = "taxCode") String taxCode);

    @WebMethod
    ActionStatus removeTax(@WebParam(name = "taxCode") String taxCode);

    @WebMethod
    ActionStatus createOrUpdateTax(@WebParam(name = "tax") TaxDto postData);

    @WebMethod
    GetTaxesResponse listTaxes();

    // invoice category

    @WebMethod
    ActionStatus createInvoiceCategory(@WebParam(name = "invoiceCategory") InvoiceCategoryDto postData);

    @WebMethod
    ActionStatus updateInvoiceCategory(@WebParam(name = "invoiceCategory") InvoiceCategoryDto postData);

    @WebMethod
    GetInvoiceCategoryResponse findInvoiceCategory(@WebParam(name = "invoiceCategoryCode") String invoiceCategoryCode);

    @WebMethod
    ActionStatus removeInvoiceCategory(@WebParam(name = "invoiceCategoryCode") String invoiceCategoryCode);

    @WebMethod
    ActionStatus createOrUpdateInvoiceCategory(@WebParam(name = "invoiceCategory") InvoiceCategoryDto postData);

    // invoice sub category

    @WebMethod
    ActionStatus createInvoiceSubCategory(@WebParam(name = "invoiceSubCategory") InvoiceSubCategoryDto postData);

    @WebMethod
    ActionStatus updateInvoiceSubCategory(@WebParam(name = "invoiceSubCategory") InvoiceSubCategoryDto postData);

    @WebMethod
    ActionStatus createOrUpdateInvoiceSubCategory(@WebParam(name = "invoiceSubCategory") InvoiceSubCategoryDto postData);

    @WebMethod
    GetInvoiceSubCategoryResponse findInvoiceSubCategory(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode);

    @WebMethod
    ActionStatus removeInvoiceSubCategory(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode);

    // invoice sub category country

    @WebMethod
    ActionStatus createInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCountry") InvoiceSubCategoryCountryDto postData);

    @WebMethod
    ActionStatus updateInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCountry") InvoiceSubCategoryCountryDto postData);

    @WebMethod
    GetInvoiceSubCategoryCountryResponse findInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode,
            @WebParam(name = "country") String country);

    @WebMethod
    ActionStatus removeInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCode") String invoiceSubCategoryCode, @WebParam(name = "country") String country);

    @WebMethod
    ActionStatus createOrUpdateInvoiceSubCategoryCountry(@WebParam(name = "invoiceSubCategoryCountry") InvoiceSubCategoryCountryDto postData);

    // calendar

    @WebMethod
    ActionStatus createCalendar(@WebParam(name = "calendar") CalendarDto postData);

    @WebMethod
    ActionStatus updateCalendar(@WebParam(name = "calendar") CalendarDto postData);

    @WebMethod
    GetCalendarResponse findCalendar(@WebParam(name = "calendarCode") String calendarCode);

    @WebMethod
    ActionStatus removeCalendar(@WebParam(name = "calendarCode") String calendarCode);

    @WebMethod
    ActionStatus createOrUpdateCalendar(@WebParam(name = "calendar") CalendarDto postData);

    @WebMethod
    ListCalendarResponse listCalendars();

    // billing cycle

    @WebMethod
    ActionStatus createBillingCycle(@WebParam(name = "billingCycle") BillingCycleDto postData);

    @WebMethod
    ActionStatus updateBillingCycle(@WebParam(name = "billingCycle") BillingCycleDto postData);

    @WebMethod
    GetBillingCycleResponse findBillingCycle(@WebParam(name = "billingCycleCode") String billingCycleCode);

    @WebMethod
    ActionStatus removeBillingCycle(@WebParam(name = "billingCycleCode") String billingCycleCode);

    @WebMethod
    ActionStatus createOrUpdateBillingCycle(@WebParam(name = "billingCycle") BillingCycleDto postData);

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
    ActionStatus createRole(@WebParam(name = "role") RoleDto postData);

    @WebMethod
    ActionStatus updateRole(@WebParam(name = "role") RoleDto postData);

    @WebMethod
    ActionStatus removeRole(@WebParam(name = "role") String name);

    @WebMethod
    GetRoleResponse findRole(@WebParam(name = "roleName") String name);

    @WebMethod
    ActionStatus createOrUpdateRole(@WebParam(name = "role") RoleDto postData);

    /**
     * List roles matching a given criteria
     * 
     * @param paging Pagination and filtering criteria. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @return A list of roles
     */
    @WebMethod
    public RolesDto listRoles(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    // Multi Language field value translations

    /**
     * 
     * Provide translation of multi language field values. Deprecated in v.4.7. Use updateTranslations instead
     * 
     * @param postData Translated field values
     * @return
     */
    @WebMethod
    @Deprecated
    public ActionStatus createDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

    /**
     * Provide translation of multi language field value. Deprecated in v.4.7. Use updateTranslations instead
     * 
     * @param postData Translated field values
     * @return
     */
    @WebMethod
    @Deprecated
    public ActionStatus updateDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

    /**
     * Provide translation of multi language field values
     * 
     * @param postData Translated field values
     * @return
     */
    @WebMethod
    public ActionStatus updateTranslations(@WebParam(name = "translations") List<CatMessagesDto> translations);

    /**
     * Find entity field translations for a particular entity, field (optional) and a language (optional). Deprecated in v.4.7. Use findTranslations instead
     * 
     * @param entityClass Entity class name
     * @param code Entity code
     * @param languageCode 3 letter language code
     * @return A list of field value translations
     */
    @WebMethod
    @Deprecated
    GetDescriptionsResponse findDescriptions(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code,
            @WebParam(name = "languageCode") String languageCode);

    /**
     * Find entity field translations for a particular entity, field (optional) and a language (optional)
     * 
     * @param entityClass Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name
     * @param languageCode 3 letter language code
     * @return A list of field value translations
     */
    @WebMethod
    public DescriptionsResponseDto findTranslations(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code,
            @WebParam(name = "validFrom") Date validFrom, @WebParam(name = "validTo") Date validTo, @WebParam(name = "fieldName") String fieldname,
            @WebParam(name = "languageCode") String languageCode);

    /**
     * Remove field value translation for a given entity and language (optional). Deprecated in v.4.7. Use removeTranslations instead.
     * 
     * @param entityClass Entity class name
     * @param code Entity code
     * @param languageCode 3 letter language code. Optional
     * @return
     */
    @WebMethod
    @Deprecated
    public ActionStatus removeDescriptions(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code,
            @WebParam(name = "languageCode") String languageCode);

    /**
     * Remove field value translation for a given entity, field (optional) and language (optional)
     * 
     * @param entityClass Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @return
     */
    @WebMethod
    public ActionStatus removeTranslations(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo, @WebParam(name = "fieldName") String fieldname, @WebParam(name = "languageCode") String languageCode);

    /**
     * Provide translation of multi language field values. Deprecated in v.4.7. Use updateTranslations instead
     * 
     * @param postData Translated field values
     * @return
     */
    @WebMethod
    @Deprecated
    public ActionStatus createOrUpdateDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

    /**
     * List entity field value translations. Deprecated in v.4.7. Use listTranslations instead.
     * 
     * @return A list of entity field value translations
     */
    @WebMethod
    @Deprecated
    public DescriptionsResponseDto listDescriptions();

    /**
     * List entity field value translations for a given entity type (optional), field (optional) and language (optional). Note: will provide ONLY those entities that have at least
     * one of multilanguage fields translated.
     * 
     * @param entityClass Entity class name
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @return A list of entity field value translations
     */
    @WebMethod
    public DescriptionsResponseDto listTranslations(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "fieldName") String fieldname,
            @WebParam(name = "languageCode") String languageCode);

    /* termination reasons */

    @WebMethod
    ActionStatus createTerminationReason(@WebParam(name = "terminationReason") TerminationReasonDto postData);

    @WebMethod
    ActionStatus updateTerminationReason(@WebParam(name = "terminationReason") TerminationReasonDto postData);

    @WebMethod
    ActionStatus createOrUpdateTerminationReason(@WebParam(name = "terminationReason") TerminationReasonDto postData);

    @WebMethod
    ActionStatus removeTerminationReason(@WebParam(name = "terminationReasonCode") String code);

    @WebMethod
    GetTerminationReasonResponse findTerminationReason(@WebParam(name = "terminationReasonCode") String code);

    @WebMethod
    GetTerminationReasonResponse listTerminationReason();

    // InvoiceType
    @WebMethod
    ActionStatus createInvoiceType(@WebParam(name = "invoiceType") InvoiceTypeDto invoiceTypeDto);

    @WebMethod
    ActionStatus updateInvoiceType(@WebParam(name = "invoiceType") InvoiceTypeDto invoiceTypeDto);

    @WebMethod
    GetInvoiceTypeResponse findInvoiceType(@WebParam(name = "invoiceTypeCode") String invoiceTypeCode);

    @WebMethod
    ActionStatus removeInvoiceType(@WebParam(name = "invoiceTypeCode") String invoiceTypeCode);

    @WebMethod
    ActionStatus createOrUpdateInvoiceType(@WebParam(name = "invoiceType") InvoiceTypeDto invoiceTypeDto);

    @WebMethod
    GetInvoiceTypesResponse listInvoiceTypes();

    /**
     * create a providerContact by dto
     * 
     * @param providerContactDto
     * @return
     */
    @WebMethod
    ActionStatus createProviderContact(@WebParam(name = "providerContact") ProviderContactDto providerContactDto);

    /**
     * update a providerContact by dto
     * 
     * @param providerContactDto
     * @return
     */
    @WebMethod
    ActionStatus updateProviderContact(@WebParam(name = "providerContact") ProviderContactDto providerContactDto);

    /**
     * find a providerContact by code
     * 
     * @param providerContactCode
     * @return
     */
    @WebMethod
    ProviderContactResponseDto findProviderContact(@WebParam(name = "providerContactCode") String providerContactCode);

    /**
     * remove a providerContact by code
     * 
     * @param providerContactCode
     * @return
     */
    @WebMethod
    ActionStatus removeProviderContact(@WebParam(name = "providerContactCode") String providerContactCode);

    /**
     * list all providerContacts
     * 
     * @return
     */
    @WebMethod
    ProviderContactsResponseDto listProviderContacts();

    /**
     * createOrUpdate a providerContact by dto
     * 
     * @param providerContactDto
     * @return
     */
    @WebMethod
    ActionStatus createOrUpdateProviderContact(@WebParam(name = "providerContact") ProviderContactDto providerContactDto);

    /**
     * create an emailTemplate by dto
     * 
     * @param emailTemplateDto
     * @return
     */
    @WebMethod
    ActionStatus createEmailTemplate(@WebParam(name = "emailTemplate") EmailTemplateDto emailTemplateDto);

    /**
     * update an emailTemplate by dto
     * 
     * @param emailTemplateDto
     * @return
     */
    @WebMethod
    ActionStatus updateEmailTemplate(@WebParam(name = "emailTemplate") EmailTemplateDto emailTemplateDto);

    /**
     * find an emailTemplate by code
     * 
     * @param emailTemplateCode
     * @return
     */
    @WebMethod
    EmailTemplateResponseDto findEmailTemplate(@WebParam(name = "emailTemplateCode") String emailTemplateCode);

    /**
     * remove an emailTemplate by code
     * 
     * @param emailTemplateCode
     * @return
     */
    @WebMethod
    ActionStatus removeEmailTemplate(@WebParam(name = "emailTemplateCode") String emailTemplateCode);

    /**
     * list emailTemplates
     * 
     * @return
     */
    @WebMethod
    EmailTemplatesResponseDto listEmailTemplates();

    /**
     * createOrUpdate an emailTemplate by dto
     * 
     * @param emailTemplateDto
     * @return
     */
    @WebMethod
    ActionStatus createOrUpdateEmailTemplate(@WebParam(name = "emailTemplate") EmailTemplateDto emailTemplateDto);

    /**
     * create a meveoInstance by dto
     * 
     * @param meveoInstanceDto
     * @return
     */
    @WebMethod
    ActionStatus createMeveoInstance(@WebParam(name = "meveoInstance") MeveoInstanceDto meveoInstanceDto);

    /**
     * update a mveoInstance by dto
     * 
     * @param meveoInstanceDto
     * @return
     */
    @WebMethod
    ActionStatus updateMeveoInstance(@WebParam(name = "meveoInstance") MeveoInstanceDto meveoInstanceDto);

    /**
     * find a meveoInstance by code
     * 
     * @param meveoInstanceCode
     * @return
     */
    @WebMethod
    MeveoInstanceResponseDto findMeveoInstance(@WebParam(name = "meveoInstanceCode") String meveoInstanceCode);

    /**
     * remove a meveoInstance by code
     * 
     * @param meveoInstanceCode
     * @return
     */
    @WebMethod
    ActionStatus removeMeveoInstance(@WebParam(name = "meveoInstanceCode") String meveoInstanceCode);

    /**
     * list meveoInstances
     * 
     * @return
     */
    @WebMethod
    MeveoInstancesResponseDto listMeveoInstances();

    /**
     * createOrUpdate meveoInstance by dto
     * 
     * @param meveoInstanceDto
     * @return
     */
    @WebMethod
    ActionStatus createOrUpdateMeveoInstance(@WebParam(name = "meveoInstance") MeveoInstanceDto meveoInstanceDto);

    /**
     * create userHierarchyLevel by dto
     * 
     * @param userHierarchyLevelDto
     * @return
     */
    @WebMethod
    ActionStatus createUserHierarchyLevel(@WebParam(name = "userHierarchyLevel") UserHierarchyLevelDto userHierarchyLevelDto);

    /**
     * update userHierarchyLevel by dto
     * 
     * @param userHierarchyLevelDto
     * @return
     */
    @WebMethod
    ActionStatus updateUserHierarchyLevel(@WebParam(name = "userHierarchyLevel") UserHierarchyLevelDto userHierarchyLevelDto);

    /**
     * remove a userHierarchyCode by code
     * 
     * @param hierarchyLevelCode
     * @return
     */
    @WebMethod
    ActionStatus removeUserHierarchyLevel(@WebParam(name = "hierarchyLevelCode") String hierarchyLevelCode);

    /**
     * find a userHierarchyCode by code
     * 
     * @param hierarchyLevelCode
     * @return
     */
    @WebMethod
    UserHierarchyLevelResponseDto findUserHierarchyLevel(@WebParam(name = "hierarchyLevelCode") String hierarchyLevelCode);

    /**
     * createOrUpdate userHierarchyLevel by dto
     * 
     * @param userHierarchyLevelDto
     * @return
     */
    @WebMethod
    ActionStatus createOrUpdateUserHierarchyLevel(@WebParam(name = "userHierarchyLevel") UserHierarchyLevelDto userHierarchyLevelDto);

    /**
     * List user hierarchy levels matching a given criteria
     * 
     * @param paging Pagination and filtering criteria. Specify "childLevels" in fields to include the child levels of user hierarchy level.
     * @return A list of user hierarchy levels
     */
    @WebMethod
    public UserHierarchyLevelsDto listUserHierarchyLevels(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    /**
     * Set configuration (stored in meveo-admin.properties file) property
     * 
     * @param property Property key
     * @param value Value to set
     * @return
     */
    @WebMethod
    public ActionStatus setConfigurationProperty(@WebParam(name = "property") String property, @WebParam(name = "value") String value);

}