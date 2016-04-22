package org.tmf.dsmapi.catalog.resource.catalog;

import java.util.EnumSet;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
//import org.tmf.dsmapi.commons.exceptions.InvalidEnumeratedValueException;

/**
 *
 * @author bahman.barzideh
 *
 */
public enum CatalogType {
    PRODUCT_CATALOG   ("Product Catalog"),
    SERVICE_CATALOG   ("Service Catalog"),
    RESOOURCE_CATALOG ("Resource Catalog");

    private String value;

    private CatalogType(String value) {
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

    public static CatalogType find(String value) {
        for (CatalogType catalogType : values()) {
            if (catalogType.value.equals(value)) {
                return catalogType;
            }
        }

        return null;
    }

//    @JsonCreator
//    public static CatalogType fromJson(String value) throws InvalidEnumeratedValueException {
//        if (value == null) {
//            return null;
//        }
//
//        CatalogType enumeratedValue = CatalogType.find(value);
//        if (enumeratedValue != null) {
//        return enumeratedValue;
//        }
//
//        throw new InvalidEnumeratedValueException(value, EnumSet.allOf(CatalogType.class));
//    }
}
