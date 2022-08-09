package org.meveo.api.dto;


/**
 * Tells which type is the csv file to import.
 *
 * @author Ilham Chafik
 **/
public enum ImportTypesEnum {
    CUSTOMER("imports/massImport/customer/input"),
    CUSTOMER_ACCOUNT("imports/massImport/customerAccount/input"),
    PAYMENT_METHOD("imports/massImport/paymentMethod/input"),
    BILLING_ACCOUNT("imports/massImport/billingAccount/input"),
    USER_ACCOUNT("imports/massImport/userAccount/input"),
    SUBSCRIPTION("imports/massImport/subscription/input"),
    SERVICE_INSTANCE("imports/massImport/serviceInstance/input"),
    ATTRIBUTE_INSTANCE("imports/massImport/attributeInstance/input"),
    ACCESS_POINT("imports/massImport/accessPoint/input"),
    COUNTER("imports/massImport/counter/input"),
    UNKNOWN("");

    public final String path;

    ImportTypesEnum(String path) {
        this.path = path;
    }
}
