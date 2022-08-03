package org.meveo.api.dto;


/**
 * Tells which type is the csv file to import.
 *
 * @author Ilham Chafik
 **/
public enum ImportTypesEnum {
    CUSTOMER("imports/customers/inputs"),
    CUSTOMER_ACCOUNT("imports/customerAccount/inputs"),
    PAYMENT_METHOD("imports/paymentMethod/inputs"),
    BILLING_ACCOUNT("imports/billingAccount/inputs"),
    USER_ACCOUNT("imports/userAccount/inputs"),
    SUBSCRIPTION("imports/subscription/inputs"),
    SERVICE_INSTANCE("imports/serviceInstance/inputs"),
    ATTRIBUTE_INSTANCE("imports/attributeInstance/inputs"),
    ACCESS_POINT("imports/accessPoint/inputs"),
    COUNTER("imports/counter/inputs"),
    UNKNOWN("");

    public final String path;

    ImportTypesEnum(String path) {
        this.path = path;
    }
}
