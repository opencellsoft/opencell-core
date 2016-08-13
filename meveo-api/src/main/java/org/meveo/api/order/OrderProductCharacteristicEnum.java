package org.meveo.api.order;

import java.util.Date;

public enum OrderProductCharacteristicEnum {

    SERVICE_PRODUCT_QUANTITY("quantity", Integer.class), SUBSCRIPTION_CODE("subscriptionCode", String.class), SERVICE_CODE("serviceCode", String.class), PRODUCT_INSTANCE_CODE(
            "productInstanceCode", String.class), SUBSCRIPTION_DATE("subscriptionDate", Date.class), TERMINATION_DATE("terminationDate", Date.class), TERMINATION_REASON(
            "terminationReason", String.class);

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
