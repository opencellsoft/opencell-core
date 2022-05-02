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
import org.meveo.api.dto.InvoiceSubCategoryDto;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.ProviderDto;
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.billing.InvoiceSequenceDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
import org.meveo.api.dto.response.BankingDateStatusResponse;
import org.meveo.api.dto.response.DescriptionsResponseDto;
import org.meveo.api.dto.response.GetBillingCycleResponse;
import org.meveo.api.dto.response.GetCalendarResponse;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.dto.response.GetCustomerAccountConfigurationResponseDto;
import org.meveo.api.dto.response.GetCustomerConfigurationResponseDto;
import org.meveo.api.dto.response.GetDescriptionsResponse;
import org.meveo.api.dto.response.GetInvoiceCategoryResponse;
import org.meveo.api.dto.response.GetInvoiceSequenceResponse;
import org.meveo.api.dto.response.GetInvoiceSequencesResponse;
import org.meveo.api.dto.response.GetInvoiceSubCategoryResponse;
import org.meveo.api.dto.response.GetInvoiceTypeResponse;
import org.meveo.api.dto.response.GetInvoiceTypesResponse;
import org.meveo.api.dto.response.GetInvoicingConfigurationResponseDto;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.dto.response.GetOccTemplatesResponseDto;
import org.meveo.api.dto.response.GetProviderResponse;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.GetTaxResponse;
import org.meveo.api.dto.response.GetTaxesResponse;
import org.meveo.api.dto.response.GetTerminationReasonResponse;
import org.meveo.api.dto.response.GetTradingConfigurationResponseDto;
import org.meveo.api.dto.response.GetTradingCountryResponse;
import org.meveo.api.dto.response.GetTradingCurrencyResponse;
import org.meveo.api.dto.response.GetTradingLanguageResponse;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.ListCalendarResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.api.dto.response.UserHierarchyLevelResponseDto;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;
import org.meveo.api.dto.response.account.ProviderContactsResponseDto;
import org.meveo.api.dto.response.communication.EmailTemplateResponseDto;
import org.meveo.api.dto.response.communication.EmailTemplatesResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstanceResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstancesResponseDto;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * SOAP endpoints for settings.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@WebService
@Deprecated
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
     * Creates a user in keycloak and core.
     * 
     * @param postData The user dto
     * @return ActionStatus
     */
    @WebMethod
    ActionStatus createExternalUser(@WebParam(name = "user") UserDto postData);

    /**
     * Updates a user in keycloak and core given a username.
     * 
     * @param postData The user dto
     * @return ActionStatus
     */
    @WebMethod
    ActionStatus updateExternalUser(@WebParam(name = "user") UserDto postData);

    /**
     * Deletes a user in keycloak and core given a username.
     * 
     * @param username the username of the user to be deleted.
     * @return ActionStatus
     */
    @WebMethod
    ActionStatus deleteExternalUser(@WebParam(name = "username") String username);

    /**
     * List users matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     */
    @WebMethod
    UsersDto listUsers(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    // seller

    @WebMethod
    ActionStatus createSeller(@WebParam(name = "seller") SellerDto postData);

    @WebMethod
    ActionStatus updateSeller(@WebParam(name = "seller") SellerDto postData);

    /**
     * Find seller by its code
     * 
     * @param sellerCode Seller code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return Seller information
     */
    @WebMethod
    GetSellerResponse findSeller(@WebParam(name = "sellerCode") String sellerCode, @WebParam(name = "inheritCF") CustomFieldInheritanceEnum inheritCF);

    @WebMethod
    ActionStatus removeSeller(@WebParam(name = "sellerCode") String sellerCode);

    @WebMethod
    SellerResponseDto listSeller();

    @WebMethod
    SellerCodesResponseDto listSellerCodes();

    @WebMethod
    ActionStatus createOrUpdateSeller(@WebParam(name = "seller") SellerDto postData);

    // tradingLanguage

    @WebMethod
    ActionStatus createLanguage(@WebParam(name = "language") LanguageDto postData);

    @WebMethod
    GetTradingLanguageResponse findLanguage(@WebParam(name = "languageCode") String languageCode);

    @WebMethod
    ActionStatus removeLanguage(@WebParam(name = "languageCode") String languageCode);

    @WebMethod
    ActionStatus updateLanguage(@WebParam(name = "language") LanguageDto postData);

    @WebMethod
    ActionStatus createOrUpdateLanguage(@WebParam(name = "language") LanguageDto postData);

    /**
     * Enable a Trading language by its language code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @WebMethod
    public ActionStatus enableLanguage(@WebParam(name = "code") String code);

    /**
     * Disable a Trading language by its language code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableLanguage(@WebParam(name = "code") String code);

    // tradingCountry
    @WebMethod
    ActionStatus createCountry(@WebParam(name = "country") CountryDto countryDto);

    @WebMethod
    GetTradingCountryResponse findCountry(@WebParam(name = "countryCode") String countryCode);

    @WebMethod
    ActionStatus removeCountry(@WebParam(name = "countryCode") String countryCode, @WebParam(name = "currencyCode") String currencyCode);

    @WebMethod
    ActionStatus updateCountry(@WebParam(name = "country") CountryDto countryDto);

    @WebMethod
    ActionStatus createOrUpdateCountry(@WebParam(name = "country") CountryDto countryDto);

    /**
     * Enable a Trading country by its country code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @WebMethod
    public ActionStatus enableCountry(@WebParam(name = "code") String code);

    /**
     * Disable a Trading country by its country code
     * 
     * @param code Language code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableCountry(@WebParam(name = "code") String code);

    // traingCurrency
    @WebMethod
    ActionStatus createCurrency(@WebParam(name = "currency") CurrencyDto postData);

    @WebMethod
    GetTradingCurrencyResponse findCurrency(@WebParam(name = "currencyCode") String currencyCode);

    @WebMethod
    ActionStatus removeCurrency(@WebParam(name = "currencyCode") String currencyCode);

    @WebMethod
    ActionStatus updateCurrency(@WebParam(name = "currency") CurrencyDto postData);

    @WebMethod
    ActionStatus createOrUpdateCurrency(@WebParam(name = "currency") CurrencyDto postData);

    /**
     * Enable a Trading currency by its currency code
     * 
     * @param code Currency code
     * @return Request processing status
     */
    @WebMethod
    public ActionStatus enableCurrency(@WebParam(name = "code") String code);

    /**
     * Disable a Trading currency by its currency code
     * 
     * @param code Currency code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableCurrency(@WebParam(name = "code") String code);

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

    // calendar

    @WebMethod
    ActionStatus createCalendar(@WebParam(name = "calendar") CalendarDto postData);

    @WebMethod
    ActionStatus updateCalendar(@WebParam(name = "calendar") CalendarDto postData);

    @WebMethod
    GetCalendarResponse findCalendar(@WebParam(name = "calendarCode") String calendarCode);
    
    @WebMethod
    BankingDateStatusResponse getBankingDateStatus(@WebParam(name = "date") Date date);

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

    /**
     * Create custom field. Deprecated in 5.1. Use {@link EntityCustomizationWs#createField(CustomFieldTemplateDto)}
     * 
     * @param postData Custom field information
     * @return Request processing status
     */
    @Deprecated
    @WebMethod
    ActionStatus createCustomFieldTemplate(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    /**
     * Update custom field definition. Deprecated in 5.1. Use {@link EntityCustomizationWs#updateField(CustomFieldTemplateDto)}
     * 
     * @param postData Custom field information
     * @return Request processing status
     */
    @Deprecated
    @WebMethod
    ActionStatus updateCustomFieldTemplate(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    /**
     * Remove custom field. Deprecated in 5.1. Use {@link EntityCustomizationWs#removeField(String, String)}
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @Deprecated
    @WebMethod
    ActionStatus removeCustomFieldTemplate(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode, @WebParam(name = "appliesTo") String appliesTo);

    /**
     * Find custom field. Deprecated in 5.1. Use {@link EntityCustomizationWs#findField(String,String)}
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @Deprecated
    @WebMethod
    GetCustomFieldTemplateReponseDto findCustomFieldTemplate(@WebParam(name = "customFieldTemplateCode") String customFieldTemplateCode,
            @WebParam(name = "appliesTo") String appliesTo);

    /**
     * Create custom field. Deprecated in 5.1. Use {@link EntityCustomizationWs#createOrUpdateField(CustomFieldTemplateDto)}
     * 
     * @param postData Custom field information
     * @return Request processing status
     */
    @Deprecated
    @WebMethod
    ActionStatus createOrUpdateCustomFieldTemplate(@WebParam(name = "customField") CustomFieldTemplateDto postData);

    // Multi Language field value translations

    /**
     * 
     * Provide translation of multi language field values. Deprecated in v.4.7. Use updateTranslations instead
     * 
     * @param postData Translated field values
     * @return action status.
     */
    @WebMethod
    @Deprecated
    ActionStatus createDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

    /**
     * Provide translation of multi language field value. Deprecated in v.4.7. Use updateTranslations instead
     * 
     * @param postData Translated field values
     * @return action status.
     */
    @WebMethod
    @Deprecated
    ActionStatus updateDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

    /**
     * Provide translation of multi language field values.
     * 
     * @param translations list of category messages.
     * @return action status.
     */
    @WebMethod
    ActionStatus updateTranslations(@WebParam(name = "translations") List<CatMessagesDto> translations);

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
    DescriptionsResponseDto findTranslations(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo, @WebParam(name = "fieldName") String fieldname, @WebParam(name = "languageCode") String languageCode);

    /**
     * Remove field value translation for a given entity and language (optional). Deprecated in v.4.7. Use removeTranslations instead.
     * 
     * @param entityClass Entity class name
     * @param code Entity code
     * @param languageCode 3 letter language code. Optional
     * @return action status.
     */
    @WebMethod
    @Deprecated
    ActionStatus removeDescriptions(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code, @WebParam(name = "languageCode") String languageCode);

    /**
     * Remove field value translation for a given entity, field (optional) and language (optional)
     * 
     * @param entityClass Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @return action status.
     */
    @WebMethod
    ActionStatus removeTranslations(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "code") String code, @WebParam(name = "validFrom") Date validFrom,
            @WebParam(name = "validTo") Date validTo, @WebParam(name = "fieldName") String fieldname, @WebParam(name = "languageCode") String languageCode);

    /**
     * Provide translation of multi language field values. Deprecated in v.4.7. Use updateTranslations instead
     * 
     * @param postData Translated field values
     * @return action status.
     */
    @WebMethod
    @Deprecated
    ActionStatus createOrUpdateDescriptions(@WebParam(name = "descriptions") CatMessagesDto postData);

    /**
     * List entity field value translations. Deprecated in v.4.7. Use listTranslations instead.
     * 
     * @return A list of entity field value translations
     */
    @WebMethod
    @Deprecated
    DescriptionsResponseDto listDescriptions();

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
    DescriptionsResponseDto listTranslations(@WebParam(name = "entityClass") String entityClass, @WebParam(name = "fieldName") String fieldname,
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

    // InvoiceSequence
    @WebMethod
    ActionStatus createInvoiceSequence(@WebParam(name = "invoiceSequence") InvoiceSequenceDto invoiceSequenceDto);

    @WebMethod
    ActionStatus updateInvoiceSequence(@WebParam(name = "invoiceSequence") InvoiceSequenceDto invoiceSequenceDto);

    @WebMethod
    GetInvoiceSequenceResponse findInvoiceSequence(@WebParam(name = "invoiceSequenceCode") String invoiceSequenceCode);

    @WebMethod
    ActionStatus createOrUpdateInvoiceSequence(@WebParam(name = "invoiceSequence") InvoiceSequenceDto invoiceSequenceDto);

    @WebMethod
    GetInvoiceSequencesResponse listInvoiceSequences();

    /**
     * create a providerContact by dto
     * 
     * @param providerContactDto providerContactDto
     * @return action status.
     */
    @WebMethod
    ActionStatus createProviderContact(@WebParam(name = "providerContact") ProviderContactDto providerContactDto);

    /**
     * update a providerContact by dto
     * 
     * @param providerContactDto providerContactDto
     * @return action status.
     */
    @WebMethod
    ActionStatus updateProviderContact(@WebParam(name = "providerContact") ProviderContactDto providerContactDto);

    /**
     * find a providerContact by code
     * 
     * @param providerContactCode providerContactCode
     * @return action status.
     */
    @WebMethod
    ProviderContactResponseDto findProviderContact(@WebParam(name = "providerContactCode") String providerContactCode);

    /**
     * remove a providerContact by code
     * 
     * @param providerContactCode providerContactCode
     * @return action status.
     */
    @WebMethod
    ActionStatus removeProviderContact(@WebParam(name = "providerContactCode") String providerContactCode);

    /**
     * list all providerContacts
     * 
     * @return action status.
     */
    @WebMethod
    ProviderContactsResponseDto listProviderContacts();

    /**
     * createOrUpdate a providerContact by dto
     * 
     * @param providerContactDto providerContactDto
     * @return action status.
     */
    @WebMethod
    ActionStatus createOrUpdateProviderContact(@WebParam(name = "providerContact") ProviderContactDto providerContactDto);

    /**
     * create an emailTemplate by dto
     * 
     * @param emailTemplateDto emailTemplateDto
     * @return action status.
     */
    @WebMethod
    ActionStatus createEmailTemplate(@WebParam(name = "emailTemplate") EmailTemplateDto emailTemplateDto);

    /**
     * update an emailTemplate by dto
     * 
     * @param emailTemplateDto emailTemplateDto
     * @return action status.
     */
    @WebMethod
    ActionStatus updateEmailTemplate(@WebParam(name = "emailTemplate") EmailTemplateDto emailTemplateDto);

    /**
     * find an emailTemplate by code
     * 
     * @param emailTemplateCode emailTemplateCode
     * @return action status.
     */
    @WebMethod
    EmailTemplateResponseDto findEmailTemplate(@WebParam(name = "emailTemplateCode") String emailTemplateCode);

    /**
     * remove an emailTemplate by code
     * 
     * @param emailTemplateCode emailTemplateCode
     * @return action status.
     */
    @WebMethod
    ActionStatus removeEmailTemplate(@WebParam(name = "emailTemplateCode") String emailTemplateCode);

    /**
     * list emailTemplates
     * 
     * @return action status.
     */
    @WebMethod
    EmailTemplatesResponseDto listEmailTemplates();

    /**
     * createOrUpdate an emailTemplate by dto
     * 
     * @param emailTemplateDto emailTemplateDto
     * @return action status.
     */
    @WebMethod
    ActionStatus createOrUpdateEmailTemplate(@WebParam(name = "emailTemplate") EmailTemplateDto emailTemplateDto);

    /**
     * create a meveoInstance by dto
     * 
     * @param meveoInstanceDto meveoInstanceDto
     * @return action status.
     */
    @WebMethod
    ActionStatus createMeveoInstance(@WebParam(name = "meveoInstance") MeveoInstanceDto meveoInstanceDto);

    /**
     * update a mveoInstance by dto
     * 
     * @param meveoInstanceDto meveoInstanceDto
     * @return action status.
     */
    @WebMethod
    ActionStatus updateMeveoInstance(@WebParam(name = "meveoInstance") MeveoInstanceDto meveoInstanceDto);

    /**
     * find a meveoInstance by code
     * 
     * @param meveoInstanceCode meveoInstanceCode
     * @return action status.
     */
    @WebMethod
    MeveoInstanceResponseDto findMeveoInstance(@WebParam(name = "meveoInstanceCode") String meveoInstanceCode);

    /**
     * remove a meveoInstance by code
     * 
     * @param meveoInstanceCode meveoInstanceCode
     * @return action status.
     */
    @WebMethod
    ActionStatus removeMeveoInstance(@WebParam(name = "meveoInstanceCode") String meveoInstanceCode);

    /**
     * list meveoInstances
     * 
     * @return action status.
     */
    @WebMethod
    MeveoInstancesResponseDto listMeveoInstances();

    /**
     * createOrUpdate meveoInstance by dto
     * 
     * @param meveoInstanceDto meveoInstanceDto
     * @return action status.
     */
    @WebMethod
    ActionStatus createOrUpdateMeveoInstance(@WebParam(name = "meveoInstance") MeveoInstanceDto meveoInstanceDto);

    /**
     * Set configuration (stored in opencell-admin.properties file) property
     * 
     * @param property Property key
     * @param value Value to set
     * @return action status.
     */
    @WebMethod
    ActionStatus setConfigurationProperty(@WebParam(name = "property") String property, @WebParam(name = "value") String value);

    /**
     * Returns a list of OCCTemplate.
     * 
     * @param query query
     * @param fields fields
     * @param offset offset
     * @param limit limit
     * @param sortBy sortBy
     * @param sortOrder sortOrder
     * @return GetOccTemplatesResponseDto
     */
    @WebMethod
    GetOccTemplatesResponseDto listOccTemplate(@WebParam(name = "query") String query, @WebParam(name = "fields") String fields, @WebParam(name = "offset") Integer offset,
            @WebParam(name = "limit") Integer limit, @WebParam(name = "sortBy") String sortBy, @WebParam(name = "sortOrder") SortOrder sortOrder);

    /**
     * Returns the system properties as json string.
     * 
     * @return system properties
     */
    @WebMethod
    ActionStatus getSystemProperties();

}