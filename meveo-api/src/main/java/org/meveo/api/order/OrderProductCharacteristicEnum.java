package org.meveo.api.order;

import java.math.BigDecimal;
import java.util.Date;

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
     * Product instance code
     */
    PRODUCT_INSTANCE_CODE("productInstanceCode", String.class),

    /**
     * Subscription date
     */
    SUBSCRIPTION_DATE("subscriptionDate", Date.class),

    /**
     * Subscription end date
     */
    SUBSCRIPTION_END_DATE("subscriptionEndDate", Date.class),

    /**
     * Subscription or service termination date
     */
    TERMINATION_DATE("terminationDate", Date.class),

    /**
     * Subscription or service termination reason
     */
    TERMINATION_REASON("terminationReason", String.class);

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
