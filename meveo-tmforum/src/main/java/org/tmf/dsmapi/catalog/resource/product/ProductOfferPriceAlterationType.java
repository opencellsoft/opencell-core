package org.tmf.dsmapi.catalog.resource.product;

import java.util.EnumSet;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
//import org.tmf.dsmapi.commons.exceptions.InvalidEnumeratedValueException;

/**
 *
 * @author bahman.barzideh
 *
 */
public enum ProductOfferPriceAlterationType {
    RECURRING ("recurring"),
    ONE_TIME  ("one time"),
    USAGE     ("usage");

    private String value;

    private ProductOfferPriceAlterationType(String value) {
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

    public static ProductOfferPriceAlterationType find(String value) {
        for (ProductOfferPriceAlterationType productOfferingPriceAlterationType : values()) {
            if (productOfferingPriceAlterationType.value.equals(value)) {
                return productOfferingPriceAlterationType;
            }
        }

        return null;
    }
//
//    @JsonCreator
//    public static ProductOfferPriceAlterationType fromJson(String value) throws InvalidEnumeratedValueException {
//        if (value == null) {
//            return null;
//        }
//
//        ProductOfferPriceAlterationType enumeratedValue = ProductOfferPriceAlterationType.find(value);
//        if (enumeratedValue != null) {
//            return enumeratedValue;
//        }
//
//        throw new InvalidEnumeratedValueException(value, EnumSet.allOf(ProductOfferPriceAlterationType.class));
//    }
}
