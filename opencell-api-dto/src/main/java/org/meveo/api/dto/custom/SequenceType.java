package org.meveo.api.dto.custom;

import static java.util.Arrays.stream;

public enum SequenceType {
    SEQUENCE, NUMERIC, ALPHA_UP, UUID, REGEXP;

    public static SequenceType fromValue(String value) {
        return stream(SequenceType.values())
                .filter(sequenceType -> sequenceType.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No sequence type for value : " + value));
    }
}
