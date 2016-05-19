package org.tmf.dsmapi.catalog.resource.specification;

import java.util.EnumSet;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
//import org.tmf.dsmapi.commons.exceptions.InvalidEnumeratedValueException;

/**
 *
 * @author bahman.barzideh
 *
 */
public enum SpecificationCharacteristicRelationshipType {
    AGGREGATION ("aggregation"),
    MIGRATION   ("migration"),
    SUBSTITUTION("substitution"),
    DEPENDENCY  ("dependency"),
    EXCLUSIVITY ("exclusivity");

    private String value;

    private SpecificationCharacteristicRelationshipType(String value) {
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

    public static SpecificationCharacteristicRelationshipType find(String value) {
        for (SpecificationCharacteristicRelationshipType specificationCharacteristicRelationshipType : values()) {
            if (specificationCharacteristicRelationshipType.value.equals(value)) {
                return specificationCharacteristicRelationshipType;
            }
        }

        return null;
    }

//    @JsonCreator
//    public static SpecificationCharacteristicRelationshipType fromJson(String value) throws InvalidEnumeratedValueException {
//        if (value == null) {
//            return null;
//        }
//
//        SpecificationCharacteristicRelationshipType enumeratedValue = SpecificationCharacteristicRelationshipType.find(value);
//        if (enumeratedValue != null) {
//            return enumeratedValue;
//        }
//
//        throw new InvalidEnumeratedValueException(value, EnumSet.allOf(SpecificationCharacteristicRelationshipType.class));
//    }
}
