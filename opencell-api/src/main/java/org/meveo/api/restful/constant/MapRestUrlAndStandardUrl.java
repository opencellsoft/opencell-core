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


        }
    };

}
