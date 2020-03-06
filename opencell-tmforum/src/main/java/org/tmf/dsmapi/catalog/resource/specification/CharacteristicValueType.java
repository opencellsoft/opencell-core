/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.tmf.dsmapi.catalog.resource.specification;

import com.fasterxml.jackson.annotation.JsonValue;

//import org.tmf.dsmapi.commons.exceptions.InvalidEnumeratedValueException;

/**
 * 
 * @author bahman.barzideh
 * 
 */
public enum CharacteristicValueType {
    STRING("string"), NUMBER("number");

    private String value;

    private CharacteristicValueType(String value) {
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

    public static CharacteristicValueType find(String value) {
        for (CharacteristicValueType characteristicValueType : values()) {
            if (characteristicValueType.value.equals(value)) {
                return characteristicValueType;
            }
        }

        return null;
    }

    // @JsonCreator
    // public static CharacteristicValueType fromJson(String value) throws InvalidEnumeratedValueException {
    // if (value == null) {
    // return null;
    // }
    //
    // CharacteristicValueType enumeratedValue = CharacteristicValueType.find(value);
    // if (enumeratedValue != null) {
    // return enumeratedValue;
    // }
    //
    // throw new InvalidEnumeratedValueException(value, EnumSet.allOf(CharacteristicValueType.class));
    // }
}
