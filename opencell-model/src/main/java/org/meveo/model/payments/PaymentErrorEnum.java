package org.meveo.model.payments;

/**
 * Payment error codes enum. 
 * @author Said Ramli
 */
public enum PaymentErrorEnum {
    PAY_CB_INVALID, NO_PAY_GATEWAY_FOR_CA, NO_PAY_METHOD_FOR_CA, PAY_GATEWAY_NOT_COMPATIBLE_FOR_CA, PAY_CARD_CANNOT_BE_PREFERED, PAY_SEPA_MANDATE_BLANK, PAY_METHOD_IS_NOT_DD;
}
