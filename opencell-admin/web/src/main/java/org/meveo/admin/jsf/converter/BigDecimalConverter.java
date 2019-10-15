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

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "bigDecimalConverter", managed = true)
public class BigDecimalConverter implements Converter<BigDecimal>, Serializable {

    private static final long serialVersionUID = 7431045453306531451L;

    private DecimalFormat format = new DecimalFormat("#,##0.00");

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, BigDecimal obj) {
        if (obj == null || obj.toString().length() == 0) {
            return "";
        }

        String value = getDecimalFormat().format(obj);
        value = value.replace(" ", "");
        value = value.replace("\u00a0", "");
        return value;
    }

    @Override
    public BigDecimal getAsObject(FacesContext facesContext, UIComponent uIComponent, String str) {
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