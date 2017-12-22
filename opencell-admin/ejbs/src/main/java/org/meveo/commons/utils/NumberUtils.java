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
package org.meveo.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * @author R.AITYAAZZA
 */
public class NumberUtils {

    public static BigDecimal round(BigDecimal what, int howmuch) {
        if (what == null) {
            return null;
        }

        what = what.setScale(howmuch, RoundingMode.HALF_UP);
        return what;
    }

    public static String format(BigDecimal amount, String format) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        DecimalFormat decimalFormat = new DecimalFormat();
        Locale lcl = Locale.FRENCH;
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance(lcl);
        decimalFormat.applyPattern(format);
        String value = decimalFormat.format(amount);
        return value;
    }

    public static BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {
        if (minuend == null) {
            return new BigDecimal(0);
        }

        if (subtrahend == null) {
            return minuend;
        }

        return minuend.subtract(subtrahend);
    }

}
