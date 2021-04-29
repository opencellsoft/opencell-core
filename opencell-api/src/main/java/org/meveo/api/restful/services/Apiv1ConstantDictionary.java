package org.meveo.api.restful.services;

import org.meveo.api.restful.annotation.GetAllEntities;

/**
 * @author Thang Nguyen
 */
public class Apiv1ConstantDictionary {

    // business final string variables :
    // variables used in endpoint /listGetAll declared with @GetAllEntities annotation
    @GetAllEntities
    public static final String WALLET_OPERATION = "/billing/wallet/operation";

    @GetAllEntities
    public static final String PRICE_PLAN = "/catalog/pricePlan";

    @GetAllEntities
    public static final String COUNTRY_ISO = "/countryIso";

    @GetAllEntities
    public static final String CURRENCY_ISO = "/currencyIso";

    @GetAllEntities
    public static final String LANGUAGE_ISO = "/languageIso";

    @GetAllEntities
    public static final String CUSTOMER = "/account/customer";

    @GetAllEntities
    public static final String USER = "/user";

    @GetAllEntities
    public static final String INVOICE = "/invoice";

    @GetAllEntities
    public static final String ACCOUNTING_CODE = "/billing/accountingCode";

    @GetAllEntities
    public static final String CONTACT = "/contact";

    @GetAllEntities
    public static final String TAX_CATEGORY = "/taxCategory";

    @GetAllEntities
    public static final String TAX_CLASS = "/taxClass";

    @GetAllEntities
    public static final String TAX_MAPPING = "/taxMapping";

    @GetAllEntities
    public static final String PAYMENT_METHOD = "/payment/paymentMethod";

    @GetAllEntities
    public static final String FILE_FORMAT = "/admin/fileFormat";

    @GetAllEntities
    public static final String OSC_TEMPLATE = "/catalog/oneShotChargeTemplate";

    @GetAllEntities
    public static final String RC_TEMPLATE = "/catalog/recurringChargeTemplate";

    @GetAllEntities
    public static final String UC_TEMPLATE = "/catalog/usageChargeTemplate";

    @GetAllEntities
    public static final String SUBSCRIPTION = "/billing/subscription";

    @GetAllEntities
    public static final String RATED_TRANSACTION = "/billing/ratedTransaction";

    @GetAllEntities
    public static final String WALLET = "/billing/wallet";

    @GetAllEntities
    public static final String OFFER_TEMPLATE = "/catalog/offerTemplate";

    @GetAllEntities
    public static final String CALENDAR = "/calendar";

    @GetAllEntities
    public static final String UNIT_MEASURE = "/catalog/unitOfMeasure";

    @GetAllEntities
    public static final String TAX = "/tax";

    @GetAllEntities
    public static final String CREDIT_CATEGORY = "/payment/creditCategory";

    @GetAllEntities
    public static final String CUSTOMER_ACCOUNT = "/account/customerAccount";

    @GetAllEntities
    public static final String TITLE = "/account/title";

    @GetAllEntities
    public static final String BILLING_ACCOUNT = "/account/billingAccount";

    @GetAllEntities
    public static final String USER_ACCOUNT = "/account/userAccount";

    @GetAllEntities
    public static final String SERVICE_TEMPLATE = "/catalog/serviceTemplate";

    @GetAllEntities
    public static final String BUSINESS_ACCOUNT_MODEL = "/account/businessAccountModel";

    @GetAllEntities
    public static final String BUSINESS_PRODUCT_MODEL = "/catalog/businessProductModel";

    @GetAllEntities
    public static final String BUSINESS_SERVICE_MODEL = "/catalog/businessServiceModel";

    @GetAllEntities
    public static final String COUNTER_TEMPLATE = "/catalog/counterTemplate";

    @GetAllEntities
    public static final String DISCOUNT_PLAN = "/catalog/discountPlan";

    @GetAllEntities
    public static final String DISCOUNT_PLAN_ITEM = "/catalog/discountPlanItem";

    @GetAllEntities
    public static final String OFFER_TEMPLATE_CATEGORY = "/catalog/offerTemplateCategory";

    @GetAllEntities
    public static final String TRIGGERED_EDR = "/catalog/triggeredEdr";

    @GetAllEntities
    public static final String PRODUCT_CHARGE_TEMPLATE = "/catalogManagement/productChargeTemplate";

    @GetAllEntities
    public static final String PRODUCT_TEMPLATE = "/catalogManagement/productTemplate";

    @GetAllEntities
    public static final String CHART = "/chart";


    public static final String CUSTOMER_CATEGORY = "/account/customer/category";
    public static final String EMAIL_TEMPLATE = "/communication/emailTemplate";
    public static final String MEVEO_INSTANCE = "/communication/meveoInstance";
    public static final String CUSTOM_ENTITY_TEMPLATE = "/entityCustomization/entity";
    public static final String CUSTOM_ENTITY_INSTANCE = "/customEntityInstance";

}
