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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.catalog.RoundingModeEnum;

/**
 * Round a number depending on the number of decimal's digits and the rounding mode
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 7.0
 */
@FacesConverter(value = "bigDecimalXDigitsConverter", managed = true)
public class BigDecimalXDigitsConverter extends BigDecimalConverter {

    private static final long serialVersionUID = -4022913574038089922L;

    /**
     * Number of digits in decimal part for a number
     */
    private Integer nbDecimal = NumberUtils.DEFAULT_NUMBER_DIGITS_DECIMAL_UI;
    /**
     * The rounding Mode.
     */
    private RoundingMode roundingMode = RoundingModeEnum.NEAREST.getRoundingMode();

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object obj) {
        if (obj == null || obj.toString().length() == 0) {
            return "";
        }

        String value = obj.toString();
        value = value.replace(" ", "");
        value = value.replace("\u00a0", "");
        return value;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String str) {
        RoundingModeEnum roundingModeAttribute = (RoundingModeEnum) uIComponent.getAttributes().get("roundingMode");
        Integer nbDecimalAttribute = (Integer) uIComponent.getAttributes().get("nbDecimal");
        if (nbDecimalAttribute != null && nbDecimalAttribute != 0) {
            nbDecimal = nbDecimalAttribute;
        }
        if (roundingModeAttribute != null) {
            roundingMode = roundingModeAttribute.getRoundingMode();
        }
        BigDecimal number = (BigDecimal) super.getAsObject(facesContext, uIComponent, str);
        number = NumberUtils.round(number, nbDecimal, roundingMode);
        return number;
    }

    @Override
    protected DecimalFormat getDecimalFormat() {
        DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols(FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String suffixPattern = StringUtils.repeat("0", nbDecimal);
        return new DecimalFormat("#,##0." + suffixPattern, decimalFormatSymbol);
    }
}
