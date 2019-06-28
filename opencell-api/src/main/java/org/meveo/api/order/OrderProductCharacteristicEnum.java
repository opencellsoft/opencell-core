package org.meveo.api.order;

import java.math.BigDecimal;
import java.util.Date;

import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;

public enum OrderProductCharacteristicEnum {

    /**
     * Quantity
     */
    SERVICE_PRODUCT_QUANTITY("quantity", BigDecimal.class),

    /**
     * Subscription code
     */
    SUBSCRIPTION_CODE("subscriptionCode", String.class),

    /**
     * Service code
     */
    SERVICE_CODE("serviceCode", String.class),

    /**
     * Service instance id
     */
    SERVICE_ID("serviceId", Long.class),

    /**
     * Product instance code
     */
    PRODUCT_INSTANCE_CODE("productInstanceCode", String.class),

    /**
     * Subscription date
     */
    SUBSCRIPTION_DATE("subscriptionDate", Date.class),

    /**
     * Subscription agreement end date
     */
    SUBSCRIPTION_END_DATE("subscriptionEndDate", Date.class),

    /**
     * Subscription or service termination date
     */
    TERMINATION_DATE("terminationDate", Date.class),

    /**
     * Subscription or service termination reason
     */
    TERMINATION_REASON("terminationReason", String.class),

    /**
     * Quote script executed before creating the quote it is executed with a context containing the productQuote
     */
    PRE_QUOTE_SCRIPT("preQuoteScript", String.class),

    /**
     * Quote script executed after creating the quote but before commiting it it is executed with a context containing the productQuote and the quote
     */
    POST_QUOTE_SCRIPT("postQuoteScript", String.class),

    /**
     * Should subscription be renewed automatically
     */
    SUBSCRIPTION_AUTO_RENEW("autoRenew", Boolean.class),

    /**
     * Number of days before the end of term to trigger notification event
     */
    SUBSCRIPTION_DAYS_NOTIFY_RENEWAL("daysNotifyRenewal", Integer.class),

    /**
     * Whether the Subscription should be suspended or terminated if not renewed
     */
    SUBSCRIPTION_END_OF_TERM_ACTION("endOfTermAction", EndOfTermActionEnum.class),

    /**
     * TerminationReason used when terminating subscription if endOfTermAction is to terminate
     */
    SUBSCRIPTION_RENEW_TERMINATION_REASON("renewTerminationReason", SubscriptionTerminationReason.class),

    /**
     * The initial period for which the subscription will be active - unit
     */
    SUBSCRIPTION_INITIALLY_ACTIVE_FOR_UNIT("initialyActiveForUnit", RenewalPeriodUnitEnum.class),

    /**
     * The initial period for which the subscription will be active - value
     */
    SUBSCRIPTION_INITIALLY_ACTIVE_FOR("initialyActiveFor", Integer.class),

    /**
     * Whether end of agreement date should be matched to the active till date
     */
    SUBSCRIPTION_EXTEND_AGREEMENT_PERIOD("extendAgreementPeriodToSubscribedTillDate", Boolean.class),

    /**
     * The period to renew subscription for - units
     */
    SUBSCRIPTION_RENEW_FOR_UNIT("renewForUnit", RenewalPeriodUnitEnum.class),

    /**
     * The period to renew subscription for - value
     */
    SUBSCRIPTION_RENEW_FOR("renewFor", Integer.class),

    /**
     * Seller code
     */
    SUBSCRIPTION_SELLER("seller", String.class),

    /**
     * The criteria1
     */
    CRITERIA_1("criteria_1", String.class),

    /**
     * The criteria2
     */
    CRITERIA_2("criteria_2", String.class),

    /**
     * The criteria3
     */
    CRITERIA_3("criteria_3", String.class),

    /**
     * The rate_until_date, if set so the recurring charge will be rated on activation service until this date
     */
    RATE_UNTIL_DATE("rate_until_date", Date.class);

    protected String characteristicName;

    @SuppressWarnings("rawtypes")
    private Class clazz;

    @SuppressWarnings("rawtypes")
    private OrderProductCharacteristicEnum(String characteristicName, Class clazz) {
        this.characteristicName = characteristicName;
        this.clazz = clazz;
    }

    public String getCharacteristicName() {
        return characteristicName;
    }

    @SuppressWarnings("rawtypes")
    public Class getClazz() {
        return clazz;
    }

    public static OrderProductCharacteristicEnum getByCharacteristicName(String characteristicName) {
        for (OrderProductCharacteristicEnum enumItem : OrderProductCharacteristicEnum.values()) {
            if (enumItem.characteristicName.equals(characteristicName)) {
                return enumItem;
            }
        }
        return null;
    }
}
