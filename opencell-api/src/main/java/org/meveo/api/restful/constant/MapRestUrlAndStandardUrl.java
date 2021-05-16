package org.meveo.api.restful.constant;

import org.meveo.api.restful.GenericOpencellRestfulAPIv1;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapRestUrlAndStandardUrl {

    public static final String SEPARATOR = ",";

    public static final String GET = "GET";

    public static final String POST = "POST";

    public static final String PUT = "PUT";

    public static final String DELETE = "DELETE";

    // Here is the syntax to declare a mapping between a RESTful URL and a standard URL
    // (K, V) = ("{METHOD}{SEPARATOR}{standardURL}", "{restfulURL}")
    public static final Map<String, String> MAP_RESTFUL_URL_AND_STANDARD_URL = new LinkedHashMap<>() {
        {

            // for entity Seller
            put( POST + SEPARATOR + "/seller", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/sellers" );
            put( GET + SEPARATOR + "/seller/list", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/sellers" );
            put( GET + SEPARATOR + "/seller", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/sellers/{sellerCode}" );
            put( PUT + SEPARATOR + "/seller", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/sellers/{sellerCode}" );
            put( DELETE + SEPARATOR + "/seller/{sellerCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/sellers/{sellerCode}" );

            // for entity BillingCycle
            put( POST + SEPARATOR + "/billingCycle", GenericOpencellRestfulAPIv1.API_VERSION + "/billingCycles" );
            put( GET + SEPARATOR + "/billingCycle/list", GenericOpencellRestfulAPIv1.API_VERSION + "/billingCycles" );
            put( GET + SEPARATOR + "/billingCycle", GenericOpencellRestfulAPIv1.API_VERSION + "/billingCycles/{billingCycleCode}" );
            put( PUT + SEPARATOR + "/billingCycle", GenericOpencellRestfulAPIv1.API_VERSION + "/billingCycles/{billingCycleCode}" );
            put( DELETE + SEPARATOR + "/billingCycle/{billingCycleCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/billingCycles/{billingCycleCode}" );

            // for entity InvoiceCategory
            put( POST + SEPARATOR + "/invoiceCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceCategories" );
            put( GET + SEPARATOR + "/invoiceCategory/list", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceCategories" );
            put( GET + SEPARATOR + "/invoiceCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceCategories/{invoiceCategoryCode}" );
            put( PUT + SEPARATOR + "/invoiceCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceCategories/{invoiceCategoryCode}" );
            put( DELETE + SEPARATOR + "/invoiceCategory/{invoiceCategoryCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceCategories/{invoiceCategoryCode}" );

            // for entity InvoiceSequence
            put( POST + SEPARATOR + "/invoiceSequence", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSequences" );
            put( GET + SEPARATOR + "/invoiceSequence/list", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSequences" );
            put( GET + SEPARATOR + "/invoiceSequence", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSequences/{invoiceSequenceCode}" );
            put( PUT + SEPARATOR + "/invoiceSequence", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSequences/{invoiceSequenceCode}" );

            // for entity InvoiceSubCategory
            put( POST + SEPARATOR + "/invoiceSubCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSubCategories" );
            put( GET + SEPARATOR + "/invoiceSubCategory/list", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSubCategories" );
            put( GET + SEPARATOR + "/invoiceSubCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSubCategories/{invoiceSubCategoryCode}" );
            put( PUT + SEPARATOR + "/invoiceSubCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSubCategories/{invoiceSubCategoryCode}" );
            put( DELETE + SEPARATOR + "/invoiceSubCategory/{invoiceSubCategoryCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceSubCategories/{invoiceSubCategoryCode}" );

            // for entity InvoiceType
            put( POST + SEPARATOR + "/invoiceType", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceTypes" );
            put( GET + SEPARATOR + "/invoiceType/list", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceTypes" );
            put( GET + SEPARATOR + "/invoiceType", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceTypes/{invoiceTypeCode}" );
            put( PUT + SEPARATOR + "/invoiceType", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceTypes/{invoiceTypeCode}" );
            put( DELETE + SEPARATOR + "/invoiceType/{invoiceTypeCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/invoiceTypes/{invoiceTypeCode}" );

            // for entity User
            put( POST + SEPARATOR + "/user", GenericOpencellRestfulAPIv1.API_VERSION + "/users" );
            put( GET + SEPARATOR + "/user/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/users" );
            put( GET + SEPARATOR + "/user", GenericOpencellRestfulAPIv1.API_VERSION + "/users/{userCode}" );
            put( PUT + SEPARATOR + "/user", GenericOpencellRestfulAPIv1.API_VERSION + "/users/{userCode}" );
            put( DELETE + SEPARATOR + "/user/{userName}", GenericOpencellRestfulAPIv1.API_VERSION + "/users/{userCode}" );

            // for entity Calendar
            put( POST + SEPARATOR + "/calendar", GenericOpencellRestfulAPIv1.API_VERSION + "/calendars" );
            put( GET + SEPARATOR + "/calendar/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/calendars" );
            put( GET + SEPARATOR + "/calendar", GenericOpencellRestfulAPIv1.API_VERSION + "/calendars/{calendarCode}" );
            put( PUT + SEPARATOR + "/calendar", GenericOpencellRestfulAPIv1.API_VERSION + "/calendars/{calendarCode}" );
            put( DELETE + SEPARATOR + "/calendar/{calendarCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/calendars/{calendarCode}" );

            // for entity UnitOfMeasure
            put( POST + SEPARATOR + "/catalog/unitOfMeasure", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/unitOfMeasures" );
            put( GET + SEPARATOR + "/catalog/unitOfMeasure/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/unitOfMeasures" );
            put( GET + SEPARATOR + "/catalog/unitOfMeasure", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/unitOfMeasures/{unitOfMeasureCode}" );
            put( PUT + SEPARATOR + "/catalog/unitOfMeasure", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/unitOfMeasures/{unitOfMeasureCode}" );
            put( DELETE + SEPARATOR + "/catalog/unitOfMeasure/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/unitOfMeasures/{unitOfMeasureCode}" );

            // for entity Contact
            put( POST + SEPARATOR + "/contact", GenericOpencellRestfulAPIv1.API_VERSION + "/contacts" );
            put( GET + SEPARATOR + "/contact/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/contacts" );
            put( GET + SEPARATOR + "/contact", GenericOpencellRestfulAPIv1.API_VERSION + "/contacts/{contactCode}" );
            put( PUT + SEPARATOR + "/contact", GenericOpencellRestfulAPIv1.API_VERSION + "/contacts/{contactCode}" );
            put( DELETE + SEPARATOR + "/contact/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/contacts/{contactCode}" );

            // for entity Tax
            put( POST + SEPARATOR + "/tax", GenericOpencellRestfulAPIv1.API_VERSION + "/taxes" );
            put( GET + SEPARATOR + "/tax/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/taxes" );
            put( GET + SEPARATOR + "/tax", GenericOpencellRestfulAPIv1.API_VERSION + "/taxes/{taxCode}" );
            put( PUT + SEPARATOR + "/tax", GenericOpencellRestfulAPIv1.API_VERSION + "/taxes/{taxCode}" );
            put( DELETE + SEPARATOR + "/tax/{taxCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/taxes/{taxCode}" );

            // for entity TaxCategory
            put( POST + SEPARATOR + "/taxCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/taxCategories" );
            put( GET + SEPARATOR + "/taxCategory/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/taxCategories" );
            put( GET + SEPARATOR + "/taxCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/taxCategories/{taxCategoryCode}" );
            put( PUT + SEPARATOR + "/taxCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/taxCategories/{taxCategoryCode}" );
            put( DELETE + SEPARATOR + "/taxCategory/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/taxCategories/{taxCategoryCode}" );

            // for entity TaxClass
            put( POST + SEPARATOR + "/taxClass", GenericOpencellRestfulAPIv1.API_VERSION + "/taxClasses" );
            put( GET + SEPARATOR + "/taxClass/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/taxClasses" );
            put( GET + SEPARATOR + "/taxClass", GenericOpencellRestfulAPIv1.API_VERSION + "/taxClasses/{taxClassCode}" );
            put( PUT + SEPARATOR + "/taxClass", GenericOpencellRestfulAPIv1.API_VERSION + "/taxClasses/{taxClassCode}" );
            put( DELETE + SEPARATOR + "/taxClass/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/taxClasses/{taxClassCode}" );

            // for entity TaxMapping
            put( POST + SEPARATOR + "/taxMapping", GenericOpencellRestfulAPIv1.API_VERSION + "/taxMappings" );
            put( GET + SEPARATOR + "/taxMapping/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/taxMappings" );
            put( GET + SEPARATOR + "/taxMapping", GenericOpencellRestfulAPIv1.API_VERSION + "/taxMappings/{taxMappingCode}" );
            put( PUT + SEPARATOR + "/taxMapping", GenericOpencellRestfulAPIv1.API_VERSION + "/taxMappings/{taxMappingCode}" );
            put( DELETE + SEPARATOR + "/taxMapping/{id}", GenericOpencellRestfulAPIv1.API_VERSION + "/taxMappings/{taxMappingCode}" );

            // for entity CreditCategory
            put( POST + SEPARATOR + "/payment/creditCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/creditCategories" );
            put( GET + SEPARATOR + "/payment/creditCategory/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/creditCategories" );
            put( GET + SEPARATOR + "/payment/creditCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/creditCategories/{creditCategoryCode}" );
            put( PUT + SEPARATOR + "/payment/creditCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/creditCategories/{creditCategoryCode}" );
            put( DELETE + SEPARATOR + "/payment/creditCategory/{creditCategoryCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/creditCategories/{creditCategoryCode}" );

            // for entity BusinessProductModel
            put( POST + SEPARATOR + "/catalog/businessProductModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessProductModels" );
            put( GET + SEPARATOR + "/catalog/businessProductModel/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessProductModels" );
            put( GET + SEPARATOR + "/catalog/businessProductModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessProductModels/{businessProductModelCode}" );
            put( PUT + SEPARATOR + "/catalog/businessProductModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessProductModels/{businessProductModelCode}" );
            put( DELETE + SEPARATOR + "/catalog/businessProductModel/{businessProductModelCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessProductModels/{businessProductModelCode}" );

            // for entity BusinessOfferModel
            put( POST + SEPARATOR + "/catalog/businessOfferModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessOfferModels" );
            put( GET + SEPARATOR + "/catalog/businessOfferModel/list", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessOfferModels" );
            put( GET + SEPARATOR + "/catalog/businessOfferModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessOfferModels/{businessOfferModelCode}" );
            put( PUT + SEPARATOR + "/catalog/businessOfferModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessOfferModels/{businessOfferModelCode}" );
            put( DELETE + SEPARATOR + "/catalog/businessOfferModel/{businessOfferModelCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessOfferModels/{businessOfferModelCode}" );

            // for entity BusinessServiceModel
            put( POST + SEPARATOR + "/catalog/businessServiceModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessServiceModels" );
            put( GET + SEPARATOR + "/catalog/businessServiceModel/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessServiceModels" );
            put( GET + SEPARATOR + "/catalog/businessServiceModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessServiceModels/{businessServiceModelCode}" );
            put( PUT + SEPARATOR + "/catalog/businessServiceModel", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessServiceModels/{businessServiceModelCode}" );
            put( DELETE + SEPARATOR + "/catalog/businessServiceModel/{businessServiceModelCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/businessServiceModels/{businessServiceModelCode}" );

            // for entity TriggeredEdr
            put( POST + SEPARATOR + "/catalog/triggeredEdr", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/triggeredEdrs" );
            put( GET + SEPARATOR + "/catalog/triggeredEdr/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/triggeredEdrs" );
            put( GET + SEPARATOR + "/catalog/triggeredEdr", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/triggeredEdrs/{triggeredEdrCode}" );
            put( PUT + SEPARATOR + "/catalog/triggeredEdr", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/triggeredEdrs/{triggeredEdrCode}" );
            put( DELETE + SEPARATOR + "/catalog/triggeredEdr/{triggeredEdrCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/triggeredEdrs/{triggeredEdrCode}" );

            // for entity EmailTemplate
            put( POST + SEPARATOR + "/communication/emailTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/emailTemplates" );
            put( GET + SEPARATOR + "/communication/emailTemplate/list", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/emailTemplates" );
            put( GET + SEPARATOR + "/communication/emailTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/emailTemplates/{emailTemplateCode}" );
            put( PUT + SEPARATOR + "/communication/emailTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/emailTemplates/{emailTemplateCode}" );
            put( DELETE + SEPARATOR + "/communication/emailTemplate/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/emailTemplates/{emailTemplateCode}" );

            // for entity MeveoInstance
            put( POST + SEPARATOR + "/communication/meveoInstance", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/meveoInstances" );
            put( GET + SEPARATOR + "/communication/meveoInstance/list", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/meveoInstances" );
            put( GET + SEPARATOR + "/communication/meveoInstance", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/meveoInstances/{meveoInstanceCode}" );
            put( PUT + SEPARATOR + "/communication/meveoInstance", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/meveoInstances/{meveoInstanceCode}" );
            put( DELETE + SEPARATOR + "/communication/meveoInstance/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/communication/meveoInstances/{meveoInstanceCode}" );

            // for entity AccountingCode
            put( POST + SEPARATOR + "/billing/accountingCode", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes" );
            put( GET + SEPARATOR + "/billing/accountingCode/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes" );
            put( GET + SEPARATOR + "/billing/accountingCode", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes/{accountingCodeCode}" );
            put( PUT + SEPARATOR + "/billing/accountingCode", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes/{accountingCodeCode}" );
            put( DELETE + SEPARATOR + "/billing/accountingCode/{accountingCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes/{accountingCodeCode}" );
            put( POST + SEPARATOR + "/billing/accountingCode/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes/{accountingCodeCode}/enable" );
            put( POST + SEPARATOR + "/billing/accountingCode/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/accountingCodes/{accountingCodeCode}/disable" );

            // for entity CounterTemplate
            put( POST + SEPARATOR + "/catalog/counterTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates" );
            put( GET + SEPARATOR + "/catalog/counterTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates" );
            put( GET + SEPARATOR + "/catalog/counterTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates/{counterTemplateCode}" );
            put( PUT + SEPARATOR + "/catalog/counterTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates/{counterTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalog/counterTemplate/{counterTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates/{counterTemplateCode}" );
            put( POST + SEPARATOR + "/catalog/counterTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates/{counterTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalog/counterTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/counterTemplates/{counterTemplateCode}/disable" );

            // for entity RecurringChargeTemplate
            put( POST + SEPARATOR + "/catalog/recurringChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates" );
            put( GET + SEPARATOR + "/catalog/recurringChargeTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates" );
            put( GET + SEPARATOR + "/catalog/recurringChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates/{recurringChargeTemplateCode}" );
            put( PUT + SEPARATOR + "/catalog/recurringChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates/{recurringChargeTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalog/recurringChargeTemplate/{recurringChargeTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates/{recurringChargeTemplateCode}" );
            put( POST + SEPARATOR + "/catalog/recurringChargeTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates/{recurringChargeTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalog/recurringChargeTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/recurringChargeTemplates/{recurringChargeTemplateCode}/disable" );

            // for entity ServiceTemplate
            put( POST + SEPARATOR + "/catalog/serviceTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates" );
            put( GET + SEPARATOR + "/catalog/serviceTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates" );
            put( GET + SEPARATOR + "/catalog/serviceTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates/{serviceTemplateCode}" );
            put( PUT + SEPARATOR + "/catalog/serviceTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates/{serviceTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalog/serviceTemplate/{serviceTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates/{serviceTemplateCode}" );
            put( POST + SEPARATOR + "/catalog/serviceTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates/{serviceTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalog/serviceTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/serviceTemplates/{serviceTemplateCode}/disable" );

            // for entity Country
            put( POST + SEPARATOR + "/country", GenericOpencellRestfulAPIv1.API_VERSION + "/countries" );
            put( GET + SEPARATOR + "/country/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/countries" );
            put( GET + SEPARATOR + "/country", GenericOpencellRestfulAPIv1.API_VERSION + "/countries/{countryCode}" );
            put( PUT + SEPARATOR + "/country", GenericOpencellRestfulAPIv1.API_VERSION + "/countries/{countryCode}" );
            put( DELETE + SEPARATOR + "/country/{countryCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/countries/{countryCode}" );
            put( POST + SEPARATOR + "/country/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/countries/{countryCode}/enable" );
            put( POST + SEPARATOR + "/country/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/countries/{countryCode}/disable" );

            // for entity PaymentMethod
            put( POST + SEPARATOR + "/payment/paymentMethod", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods" );
            put( GET + SEPARATOR + "/payment/paymentMethod/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods" );
            put( GET + SEPARATOR + "/payment/paymentMethod", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods/{paymentMethodCode}" );
            put( PUT + SEPARATOR + "/payment/paymentMethod", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods/{paymentMethodCode}" );
            put( DELETE + SEPARATOR + "/payment/paymentMethod", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods/{paymentMethodCode}" );
            put( POST + SEPARATOR + "/payment/paymentMethod/{id}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods/{paymentMethodId}/enable" );
            put( POST + SEPARATOR + "/payment/paymentMethod/{id}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/payment/paymentMethods/{paymentMethodId}/disable" );

            // for entity RatedTransaction
            put( GET + SEPARATOR + "/billing/ratedTransaction/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/ratedTransactions" );

            // for entity CurrencyIso
            put( POST + SEPARATOR + "/currencyIso", GenericOpencellRestfulAPIv1.API_VERSION + "/currenciesIso" );
            put( GET + SEPARATOR + "/currencyIso/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/currenciesIso" );
            put( GET + SEPARATOR + "/currencyIso", GenericOpencellRestfulAPIv1.API_VERSION + "/currenciesIso/{currencyIsoCode}" );
            put( PUT + SEPARATOR + "/currencyIso", GenericOpencellRestfulAPIv1.API_VERSION + "/currenciesIso/{currencyIsoCode}" );
            put( DELETE + SEPARATOR + "/currencyIso/{currencyCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/currenciesIso/{currencyIsoCode}" );

            // for entity BusinessAccountModel
            put( POST + SEPARATOR + "/account/businessAccountModel", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/businessAccountModels" );
            put( GET + SEPARATOR + "/account/businessAccountModel/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/businessAccountModels" );
            put( GET + SEPARATOR + "/account/businessAccountModel", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/businessAccountModels/{businessAccountModelCode}" );
            put( PUT + SEPARATOR + "/account/businessAccountModel", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/businessAccountModels/{businessAccountModelCode}" );
            put( DELETE + SEPARATOR + "/account/businessAccountModel/{businessAccountModelCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/businessAccountModels/{businessAccountModelCode}" );

            // for entity Currency
            put( POST + SEPARATOR + "/currency", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies" );
            put( GET + SEPARATOR + "/currency/list", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies" );
            put( GET + SEPARATOR + "/currency", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies/{currencyCode}" );
            put( PUT + SEPARATOR + "/currency", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies/{currencyCode}" );
            put( DELETE + SEPARATOR + "/currency/{currencyCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies/{currencyCode}" );
            put( POST + SEPARATOR + "/currency/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies/{currencyCode}/enable" );
            put( POST + SEPARATOR + "/currency/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/currencies/{currencyCode}/disable" );

            // for entity OneShotChargeTemplate
            put( POST + SEPARATOR + "/catalog/oneShotChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates" );
            put( GET + SEPARATOR + "/catalog/oneShotChargeTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates" );
            put( GET + SEPARATOR + "/catalog/oneShotChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates/{oneShotChargeTemplateCode}" );
            put( PUT + SEPARATOR + "/catalog/oneShotChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates/{oneShotChargeTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalog/oneShotChargeTemplate/{oneShotChargeTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates/{oneShotChargeTemplateCode}" );
            put( POST + SEPARATOR + "/catalog/oneShotChargeTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates/{oneShotChargeTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalog/oneShotChargeTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/oneShotChargeTemplates/{oneShotChargeTemplateCode}/disable" );

            // for entity Invoice
            put( GET + SEPARATOR + "/invoice", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices" );
            put( GET + SEPARATOR + "/invoice/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices" );
            put( GET + SEPARATOR + "/invoice/getPdfInvoice", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices/pdfInvoices/{invoiceId}" );
            put( GET + SEPARATOR + "/invoice/getXMLInvoice", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices/xmlInvoices/{invoiceId}" );
            put( PUT + SEPARATOR + "/invoice/validate", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices/{invoiceId}/validation" );
            put( POST + SEPARATOR + "/invoice/generateInvoice", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices/generation" );
            put( POST + SEPARATOR + "/invoice/fetchPdfInvoice", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices/pdfInvoices" );
            put( POST + SEPARATOR + "/invoice/fetchXMLInvoice", GenericOpencellRestfulAPIv1.API_VERSION + "/invoices/xmlInvoices" );

            // for entity JobInstance
            put( POST + SEPARATOR + "/jobInstance/create", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances" );
            put( GET + SEPARATOR + "/jobInstance/list", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances" );
            put( GET + SEPARATOR + "/jobInstance", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances/{jobInstanceCode}" );
            put( PUT + SEPARATOR + "/jobInstance", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances/{jobInstanceCode}" );
            put( DELETE + SEPARATOR + "/jobInstance/{jobInstanceCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances/{jobInstanceCode}" );
            put( POST + SEPARATOR + "/jobInstance/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances/{jobInstanceCode}/enable" );
            put( POST + SEPARATOR + "/jobInstance/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/jobInstances/{jobInstanceCode}/disable" );

            // for entity OfferTemplate
            put( POST + SEPARATOR + "/catalog/offerTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates" );
            put( GET + SEPARATOR + "/catalog/offerTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates" );
            put( GET + SEPARATOR + "/catalog/offerTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates/{offerTemplateCode}" );
            put( PUT + SEPARATOR + "/catalog/offerTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates/{offerTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalog/offerTemplate/{offerTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates/{offerTemplateCode}" );
            put( POST + SEPARATOR + "/catalog/offerTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates/{offerTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalog/offerTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplates/{offerTemplateCode}/disable" );

            // for entity Access
            put( POST + SEPARATOR + "/account/access", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/accesses" );
            put( POST + SEPARATOR + "/account/access/createOrUpdate", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/accesses/{accessCode}/creationOrUpdate" );
            put( PUT + SEPARATOR + "/account/access", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/accesses/{accessCode}" );
            put( GET + SEPARATOR + "/account/access/list", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}/accesses" );
            put( GET + SEPARATOR + "/account/access", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}/accesses/{accessCode}" );

            // for entity CountryIso
            put( POST + SEPARATOR + "/countryIso", GenericOpencellRestfulAPIv1.API_VERSION + "/countriesIso" );
            put( GET + SEPARATOR + "/countryIso/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/countriesIso" );
            put( GET + SEPARATOR + "/countryIso", GenericOpencellRestfulAPIv1.API_VERSION + "/countriesIso/{countryIsoCode}" );
            put( PUT + SEPARATOR + "/countryIso", GenericOpencellRestfulAPIv1.API_VERSION + "/countriesIso/{countryIsoCode}" );
            put( DELETE + SEPARATOR + "/countryIso/{countryCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/countriesIso/{countryIsoCode}" );

            // for entity AccountHierarchy
            put( POST + SEPARATOR + "/account/accountHierarchy", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/accountHierarchies/{accountHierarchyCode}" );
            put( PUT + SEPARATOR + "/account/accountHierarchy", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/accountHierarchies/{accountHierarchyCode}" );

            // for entity Wallet
            put( POST + SEPARATOR + "/billing/wallet/operation", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/wallets" );
            put( GET + SEPARATOR + "/billing/wallet/operation/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/billing/wallets/operation" );

            // for entity UserAccount
            put( POST + SEPARATOR + "/account/userAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/userAccounts" );
            put( GET + SEPARATOR + "/account/userAccount/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/userAccounts" );
            put( GET + SEPARATOR + "/account/userAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/userAccounts/{userAccountCode}" );
            put( PUT + SEPARATOR + "/account/userAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/userAccounts/{userAccountCode}" );
            put( DELETE + SEPARATOR + "/account/userAccount/{userAccountCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/userAccounts/{userAccountCode}" );
            put( GET + SEPARATOR + "/account/userAccount/list", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts/{billingAccountCode}/userAccounts" );

            // for entity DiscountPlanItem
            put( POST + SEPARATOR + "/catalog/discountPlanItem", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems" );
            put( GET + SEPARATOR + "/catalog/discountPlanItem/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems" );
            put( GET + SEPARATOR + "/catalog/discountPlanItem", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems/{discountPlanItemCode}" );
            put( PUT + SEPARATOR + "/catalog/discountPlanItem", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems/{discountPlanItemCode}" );
            put( DELETE + SEPARATOR + "/catalog/discountPlanItem/{discountPlanItemCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems/{discountPlanItemCode}" );
            put( POST + SEPARATOR + "/catalog/discountPlanItem/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems/{discountPlanItemCode}/enable" );
            put( POST + SEPARATOR + "/catalog/discountPlanItem/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlanItems/{discountPlanItemCode}/disable" );

            // for entity CustomerAccount
            put( POST + SEPARATOR + "/account/customerAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customerAccounts" );
            put( GET + SEPARATOR + "/account/customerAccount/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customerAccounts" );
            put( GET + SEPARATOR + "/account/customerAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customerAccounts/{customerAccountCode}" );
            put( PUT + SEPARATOR + "/account/customerAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customerAccounts/{customerAccountCode}" );
            put( DELETE + SEPARATOR + "/account/customerAccount/{customerAccountCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customerAccounts/{customerAccountCode}" );
            put( GET + SEPARATOR + "/account/customerAccount/list", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts/{billingAccountCode}/userAccounts" );

            // for entity Chart
            put( POST + SEPARATOR + "/chart", GenericOpencellRestfulAPIv1.API_VERSION + "/charts" );
            put( GET + SEPARATOR + "/chart/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/charts" );
            put( GET + SEPARATOR + "/chart", GenericOpencellRestfulAPIv1.API_VERSION + "/charts/{chartCode}" );
            put( PUT + SEPARATOR + "/chart", GenericOpencellRestfulAPIv1.API_VERSION + "/charts/{chartCode}" );
            put( DELETE + SEPARATOR + "/chart", GenericOpencellRestfulAPIv1.API_VERSION + "/charts/{chartCode}" );
            put( POST + SEPARATOR + "/chart/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/charts/{chartCode}/enable" );
            put( POST + SEPARATOR + "/chart/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/charts/{chartCode}/disable" );

            // for entity PdfInvoice
            put( GET + SEPARATOR + "/PdfInvoice", GenericOpencellRestfulAPIv1.API_VERSION + "/pdfInvoices" );

            // for entity ProviderContact
            put( POST + SEPARATOR + "/account/providerContact", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/providerContacts" );
            put( GET + SEPARATOR + "/account/providerContact/list", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/providerContacts" );
            put( GET + SEPARATOR + "/account/providerContact", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/providerContacts/{providerContactCode}" );
            put( PUT + SEPARATOR + "/account/providerContact", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/providerContacts/{providerContactCode}" );
            put( DELETE + SEPARATOR + "/account/providerContact/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/providerContacts/{providerContactCode}" );

            // for entity Customer
            put( POST + SEPARATOR + "/account/customer", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customers" );
            put( GET + SEPARATOR + "/account/customer/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customers" );
            put( GET + SEPARATOR + "/account/customer", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customers/{customerCode}" );
            put( PUT + SEPARATOR + "/account/customer", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customers/{customerCode}" );
            put( DELETE + SEPARATOR + "/account/customer/{customerCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customers/{customerCode}" );

            // for entity Title
            put( POST + SEPARATOR + "/account/title", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/titles" );
            put( GET + SEPARATOR + "/account/title/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/titles" );
            put( GET + SEPARATOR + "/account/title", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/titles/{titleCode}" );
            put( PUT + SEPARATOR + "/account/title", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/titles/{titleCode}" );
            put( DELETE + SEPARATOR + "/account/title/{titleCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/titles/{titleCode}" );

            // for entity ProductChargeTemplate
            put( POST + SEPARATOR + "/catalogManagement/productChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates" );
            put( GET + SEPARATOR + "/catalogManagement/productChargeTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates" );
            put( GET + SEPARATOR + "/catalogManagement/productChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates/{productChargeTemplateCode}" );
            put( PUT + SEPARATOR + "/catalogManagement/productChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates/{productChargeTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalogManagement/productChargeTemplate/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates/{productChargeTemplateCode}" );
            put( POST + SEPARATOR + "/catalogManagement/productChargeTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates/{productChargeTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalogManagement/productChargeTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productChargeTemplates/{productChargeTemplateCode}/disable" );

            // for entity ProductTemplate
            put( POST + SEPARATOR + "/catalogManagement/productTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates" );
            put( GET + SEPARATOR + "/catalogManagement/productTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates" );
            put( GET + SEPARATOR + "/catalogManagement/productTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates/{productTemplateCode}" );
            put( PUT + SEPARATOR + "/catalogManagement/productTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates/{productTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalogManagement/productTemplate/{code}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates/{productTemplateCode}" );
            put( POST + SEPARATOR + "/catalogManagement/productTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates/{productTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalogManagement/productTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/productTemplates/{productTemplateCode}/disable" );

            // for entity Language
            put( POST + SEPARATOR + "/language", GenericOpencellRestfulAPIv1.API_VERSION + "/languages" );
            put( GET + SEPARATOR + "/language/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/languages" );
            put( GET + SEPARATOR + "/language", GenericOpencellRestfulAPIv1.API_VERSION + "/languages/{languageCode}" );
            put( PUT + SEPARATOR + "/language", GenericOpencellRestfulAPIv1.API_VERSION + "/languages/{languageCode}" );
            put( DELETE + SEPARATOR + "/language/{languageCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/languages/{languageCode}" );
            put( POST + SEPARATOR + "/language/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/languages/{languageCode}/enable" );
            put( POST + SEPARATOR + "/language/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/languages/{languageCode}/disable" );

            // for entity BillingAccount
            put( POST + SEPARATOR + "/account/billingAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts" );
            put( GET + SEPARATOR + "/account/billingAccount/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts" );
            put( GET + SEPARATOR + "/account/billingAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts/{billingAccountCode}" );
            put( PUT + SEPARATOR + "/account/billingAccount", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts/{billingAccountCode}" );
            put( DELETE + SEPARATOR + "/account/billingAccount/{billingAccountCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/billingAccounts/{billingAccountCode}" );
            put( GET + SEPARATOR + "/account/billingAccount/list", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/customerAccounts/{customerAccountCode}/billingAccounts" );

            // for entity PricePlan
            put( POST + SEPARATOR + "/catalog/pricePlan", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans" );
            put( GET + SEPARATOR + "/catalog/pricePlan/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans" );
            put( GET + SEPARATOR + "/catalog/pricePlan", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans/{pricePlanCode}" );
            put( PUT + SEPARATOR + "/catalog/pricePlan", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans/{pricePlanCode}" );
            put( DELETE + SEPARATOR + "/catalog/pricePlan/{pricePlanCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans/{pricePlanCode}" );
            put( POST + SEPARATOR + "/catalog/pricePlan/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans/{pricePlanCode}/enable" );
            put( POST + SEPARATOR + "/catalog/pricePlan/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/pricePlans/{pricePlanCode}/disable" );

            // for entity OfferTemplateCategory
            put( POST + SEPARATOR + "/catalog/offerTemplateCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories" );
            put( GET + SEPARATOR + "/catalog/offerTemplateCategory/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories" );
            put( GET + SEPARATOR + "/catalog/offerTemplateCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories/{offerTemplateCategoryCode}" );
            put( PUT + SEPARATOR + "/catalog/offerTemplateCategory", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories/{offerTemplateCategoryCode}" );
            put( DELETE + SEPARATOR + "/catalog/offerTemplateCategory/{offerTemplateCategoryCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories/{offerTemplateCategoryCode}" );
            put( POST + SEPARATOR + "/catalog/offerTemplateCategory/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories/{offerTemplateCategoryCode}/enable" );
            put( POST + SEPARATOR + "/catalog/offerTemplateCategory/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/offerTemplateCategories/{offerTemplateCategoryCode}/disable" );

            // for entity DiscountPlan
            put( POST + SEPARATOR + "/catalog/discountPlan", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans" );
            put( GET + SEPARATOR + "/catalog/discountPlan/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans" );
            put( GET + SEPARATOR + "/catalog/discountPlan", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans/{discountPlanCode}" );
            put( PUT + SEPARATOR + "/catalog/discountPlan", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans/{discountPlanCode}" );
            put( DELETE + SEPARATOR + "/catalog/discountPlan/{discountPlanCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans/{discountPlanCode}" );
            put( POST + SEPARATOR + "/catalog/discountPlan/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans/{discountPlanCode}/enable" );
            put( POST + SEPARATOR + "/catalog/discountPlan/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/discountPlans/{discountPlanCode}/disable" );

            // for entity Subscription
            put( POST + SEPARATOR + "/billing/subscription", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions" );
            put( GET + SEPARATOR + "/billing/subscription/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions" );
            put( GET + SEPARATOR + "/billing/subscription", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}" );
            put( PUT + SEPARATOR + "/billing/subscription", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}" );
            put( PUT + SEPARATOR + "/billing/subscription/activate", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}/activation" );
            put( PUT + SEPARATOR + "/billing/subscription/suspend", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}/suspension" );
            put( PUT + SEPARATOR + "/billing/subscription/updateServices", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}/services" );
            put( PUT + SEPARATOR + "/billing/subscription/terminate", GenericOpencellRestfulAPIv1.API_VERSION + "/accountManagement/subscriptions/{subscriptionCode}/termination" );

            // for entity UsageChargeTemplate
            put( POST + SEPARATOR + "/catalog/usageChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates" );
            put( GET + SEPARATOR + "/catalog/usageChargeTemplate/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates" );
            put( GET + SEPARATOR + "/catalog/usageChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates/{usageChargeTemplateCode}" );
            put( PUT + SEPARATOR + "/catalog/usageChargeTemplate", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates/{usageChargeTemplateCode}" );
            put( DELETE + SEPARATOR + "/catalog/usageChargeTemplate/{usageChargeTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates/{usageChargeTemplateCode}" );
            put( POST + SEPARATOR + "/catalog/usageChargeTemplate/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates/{usageChargeTemplateCode}/enable" );
            put( POST + SEPARATOR + "/catalog/usageChargeTemplate/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/catalog/usageChargeTemplates/{usageChargeTemplateCode}/disable" );

            // for entity Entity
            put( POST + SEPARATOR + "/entityCustomization/entity", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities" );
            put( GET + SEPARATOR + "/entityCustomization/entity/list", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities" );
            put( GET + SEPARATOR + "/entityCustomization/entity", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities/{entityCode}" );
            put( PUT + SEPARATOR + "/entityCustomization/entity", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities/{entityCode}" );
            put( DELETE + SEPARATOR + "/entityCustomization/entity/{customEntityTemplateCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities/{entityCode}" );
            put( POST + SEPARATOR + "/entityCustomization/entity/{code}/enable", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities/{entityCode}/enable" );
            put( POST + SEPARATOR + "/entityCustomization/entity/{code}/disable", GenericOpencellRestfulAPIv1.API_VERSION + "/entityCustomization/entities/{entityCode}/disable" );

            // for entity LanguageIso
            put( POST + SEPARATOR + "/languageIso", GenericOpencellRestfulAPIv1.API_VERSION + "/languagesIso" );
            put( GET + SEPARATOR + "/languageIso/listGetAll", GenericOpencellRestfulAPIv1.API_VERSION + "/languagesIso" );
            put( GET + SEPARATOR + "/languageIso", GenericOpencellRestfulAPIv1.API_VERSION + "/languagesIso/{languageCode}" );
            put( PUT + SEPARATOR + "/languageIso", GenericOpencellRestfulAPIv1.API_VERSION + "/languagesIso/{languageCode}" );
            put( DELETE + SEPARATOR + "/languageIso/{languageCode}", GenericOpencellRestfulAPIv1.API_VERSION + "/languagesIso/{languageCode}" );


        }
    };

}
