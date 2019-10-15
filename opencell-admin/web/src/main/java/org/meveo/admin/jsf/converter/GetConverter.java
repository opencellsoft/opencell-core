/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.jsf.converter;

import java.math.BigDecimal;

import javax.faces.convert.ByteConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.DoubleConverter;
import javax.faces.convert.FloatConverter;
import javax.faces.convert.IntegerConverter;
import javax.faces.convert.LongConverter;
import javax.faces.convert.ShortConverter;
import javax.inject.Named;

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
