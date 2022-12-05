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
package org.meveo.admin.jsf.converter;

import java.math.BigDecimal;

import jakarta.faces.convert.ByteConverter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.DoubleConverter;
import jakarta.faces.convert.FloatConverter;
import jakarta.faces.convert.IntegerConverter;
import jakarta.faces.convert.LongConverter;
import jakarta.faces.convert.ShortConverter;
import jakarta.inject.Named;

import org.meveo.commons.utils.StringUtils;

@Named
public class GetConverter {

    /**
     * Gets converter for type and by parameter.
     *
     * @param obj Obj for which converter is searched.
     * @return Converter.
     */
    public Converter forType(Object obj) {
        return forType(obj, null);
    }

    /**
     * Gets converter for type and by parameter.
     *
     * @param obj   Obj for which converter is searched.
     * @param param Parameter that can be used for finding out converter.
     * @return Converter.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Converter forType(Object obj, String param) {

        if (obj == null) {
            return null;
        }

        if (StringUtils.isBlank(param)) {

            Class type = obj.getClass();

            if (type == BigDecimal.class) {
                return new BigDecimalConverter();

            } else if (type == Integer.class || (type.isPrimitive() && type.getName().equals("int"))) {
                return new IntegerConverter();

            } else if (type == Long.class || (type.isPrimitive() && type.getName().equals("long"))) {
                return new LongConverter();

            } else if (type == Byte.class || (type.isPrimitive() && type.getName().equals("byte"))) {
                return new ByteConverter();

            } else if (type == Short.class || (type.isPrimitive() && type.getName().equals("short"))) {
                return new ShortConverter();

            } else if (type == Double.class || (type.isPrimitive() && type.getName().equals("double"))) {
                return new DoubleConverter();

            } else if (type == Float.class || (type.isPrimitive() && type.getName().equals("float"))) {
                return new FloatConverter();
            }

        } else if (obj.getClass() == BigDecimal.class) {
            if ("4digits".equals(param)) {
                return new BigDecimal4DigitsConverter();

            } else if ("10digits".equals(param)) {
                return new BigDecimal10DigitsConverter();

            } else if ("12digits".equals(param)) {
                return new BigDecimal12DigitsConverter();

            }
        } else if ("date".equals(param)) {
            return new DateConverter();

        } else if ("customFieldAppliesTo".equals(param)) {
            return new CustomFieldAppliesToConverter();

        }
        return null;
    }
}
