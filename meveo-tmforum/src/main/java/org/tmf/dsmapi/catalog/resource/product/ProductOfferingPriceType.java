package org.tmf.dsmapi.catalog.resource.product;

import com.fasterxml.jackson.annotation.JsonValue;

//import org.tmf.dsmapi.commons.exceptions.InvalidEnumeratedValueException;

/**
 * 
 * @author bahman.barzideh
 * 
 */
public enum ProductOfferingPriceType {
    RECURRING("recurring"), ONE_TIME("one time"), USAGE("usage");

    private String value;

    private ProductOfferingPriceType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonValue(true)
    public String getValue() {
        return this.value;
    }

    public static ProductOfferingPriceType find(String value) {
        for (ProductOfferingPriceType productOfferingPriceType : values()) {
            if (productOfferingPriceType.value.equals(value)) {
                return productOfferingPriceType;
            }
        }

        return null;
    }

    // @JsonCreator
    // public static ProductOfferingPriceType fromJson(String value) throws InvalidEnumeratedValueException {
    // if (value == null) {
    // return null;
    // }
    //
    // ProductOfferingPriceType enumeratedValue = ProductOfferingPriceType.find(value);
    // if (enumeratedValue != null) {
    // return enumeratedValue;
    // }
    //
    // throw new InvalidEnumeratedValueException(value, EnumSet.allOf(ProductOfferingPriceType.class));
    // }
}
