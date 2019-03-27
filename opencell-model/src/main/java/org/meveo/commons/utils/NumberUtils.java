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

import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.RoundingModeEnum;

/**
 * @author Edward P. Legaspi
 * @author R.AITYAAZZA
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
public class NumberUtils {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public static final Integer DEFAULT_NUMBER_DIGITS_DECIMAL = 2;

    public static BigDecimal round(BigDecimal what, int howmuch, RoundingModeEnum roundingModeEnum) {
        if (what == null) {
            return null;
        }

        what = what.setScale(howmuch, roundingModeEnum.getRoundingMode());
        return what;
    }

    public static BigDecimal round(BigDecimal what, int howmuch, RoundingMode roundingMode) {
        if (what == null) {
            return null;
        }

        what = what.setScale(howmuch, roundingMode);
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

    public static BigDecimal getInChargeUnit(BigDecimal unitValue, BigDecimal unitMultiplicator, Integer unitNbDecimal, RoundingModeEnum roundingMode) {
        if (unitMultiplicator == null) {
            unitMultiplicator = BigDecimal.ONE;
        }
        if (unitNbDecimal == null) {
            unitNbDecimal = new Integer(2);
        }

        BigDecimal result = unitValue.multiply(unitMultiplicator);
        result = result.setScale(unitNbDecimal, roundingMode.getRoundingMode());
        return result;
    }

    /**
     * Round to string.
     *
     * @param amount the amount
     * @param scale the scale
     * @param roundingMode the rounding mode
     * @return the string
     */
    public static String roundToString(BigDecimal amount, Integer scale, RoundingModeEnum roundingMode) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (scale == null) {
            scale = 2;
        }
        amount = amount.setScale(scale, roundingMode.getRoundingMode());
        return amount.toPlainString();
    }

    /**
     * Compute derived amounts amountWithoutTax/amountWithTax/amountTax
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param taxPercent Tax percent
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     * @return Calculated amount values as array [amountWithoutTax, amountWithTax, amountTax]
     */
    public static BigDecimal[] computeDerivedAmounts(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal taxPercent, boolean isEnterprise, int rounding,
            RoundingMode roundingMode) {

        if (taxPercent == null || taxPercent.compareTo(BigDecimal.ZERO) == 0) {
            if (isEnterprise) {
                amountWithoutTax = amountWithoutTax.setScale(rounding, roundingMode);
            } else {
                amountWithTax = amountWithTax.setScale(rounding, roundingMode);
            }
            return new BigDecimal[] { isEnterprise ? amountWithoutTax : amountWithTax, isEnterprise ? amountWithoutTax : amountWithTax, BigDecimal.ZERO };
        }

        if (isEnterprise) {
            amountWithoutTax = amountWithoutTax.setScale(rounding, roundingMode);
            amountWithTax = amountWithoutTax.add(amountWithoutTax.multiply(taxPercent).divide(new BigDecimal(100), rounding, roundingMode));

        } else {
            amountWithTax = amountWithTax.setScale(rounding, roundingMode);
            BigDecimal percentPlusOne = BigDecimal.ONE.add(taxPercent.divide(NumberUtils.HUNDRED, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
            amountWithoutTax = amountWithTax.divide(percentPlusOne, rounding, roundingMode);
        }

        BigDecimal amountTax = amountWithTax.subtract(amountWithoutTax);

        return new BigDecimal[] { amountWithoutTax, amountWithTax, amountTax };
    }
}