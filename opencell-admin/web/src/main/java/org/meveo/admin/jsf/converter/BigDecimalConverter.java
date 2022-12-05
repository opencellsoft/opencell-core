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

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "bigDecimalConverter", managed = true)
public class BigDecimalConverter implements Converter, Serializable {

    private static final long serialVersionUID = 7431045453306531451L;

    private DecimalFormat format = new DecimalFormat("#,##0.00");

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object obj) {
        if (obj == null || obj.toString().length() == 0) {
            return "";
        }

        String value = getDecimalFormat().format(obj);
        value = value.replace(" ", "");
        value = value.replace("\u00a0", "");
        return value;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        str = str.replace(" ", "");
        str = str.replace("\u00a0", "");
        int commaPos = str.indexOf(',');
        int dotPos = str.indexOf('.');
        if (commaPos > 0 && dotPos > 0) {
            // Get rid of comma when value was entered in 2,500.89 format (EN locale)
            if (commaPos < dotPos) {
                str = str.replace(",", "");
                // Handle when value was entered in 2,500.89 format (FR locale)
            } else {
                str = str.replace(".", "");
                str = str.replace(",", ".");
            }
            // Replace comma with period when entered in 21,89 format
        } else {
            str = str.replace(",", ".");
        }

        return new BigDecimal(str);
    }

    protected DecimalFormat getDecimalFormat() {
        return format;
    }
}