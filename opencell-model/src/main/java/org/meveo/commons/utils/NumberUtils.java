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
package org.meveo.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;

/**
 * @author Edward P. Legaspi
 * @author R.AITYAAZZA
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
public class NumberUtils {

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    public static final Integer DEFAULT_NUMBER_DIGITS_DECIMAL_UI = 2;

    public static final Integer DEFAULT_NUMBER_DIGITS_DECIMAL = 12;

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

    public static BigDecimal getInChargeUnit(BigDecimal unitValue,ChargeTemplate chargeTemplate) {
    	
        BigDecimal unitMultiplicator=chargeTemplate.getUnitMultiplicator();
		if (unitMultiplicator == null) {
            unitMultiplicator = BigDecimal.ONE;
        }
        int unitNbDecimal=chargeTemplate.getUnitNbDecimal();
		if (unitNbDecimal == 0) {
            unitNbDecimal = 2;
        }

        BigDecimal result = unitValue.multiply(unitMultiplicator);
        RoundingModeEnum roundingMode = chargeTemplate.getRoundingMode();
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
     * Get BigDecimal as a string
     * 
     * @param bigDecimal
     * @return A null-safe Plain String value of the bigDecimal
     */
    public static String toPlainString(BigDecimal bigDecimal) {
        return bigDecimal != null ? bigDecimal.toPlainString() : BigDecimal.ZERO.toPlainString();
    }

    /**
     * Compute derived amounts: amountWithoutTax/amountWithTax/amountTax, taking amountWithoutTax or amountWithTax as a base and use tax percent to calculate the rest. <br/>
     * If taxPercent is null, or ZERO returned amountWithoutTax and amountWithTax values will be the same (which one, depending on isEnterprise value)
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param taxPercent Tax percent
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     * @return Calculated amount values as array [amountWithoutTax, amountWithTax, amountTax]
     */
    public static BigDecimal[] computeDerivedAmounts(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal taxPercent, boolean isEnterprise, int rounding, RoundingMode roundingMode) {

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
            amountWithTax = amountWithTax.compareTo(amountWithoutTax) < 0 ? amountWithoutTax.add(amountWithoutTax.multiply(taxPercent).divide(new BigDecimal(100), rounding, roundingMode)) : amountWithTax;

        } else {
            amountWithTax = amountWithTax.setScale(rounding, roundingMode);
            BigDecimal percentPlusOne = BigDecimal.ONE.add(taxPercent.divide(NumberUtils.HUNDRED, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
            amountWithoutTax = amountWithoutTax.compareTo(amountWithTax) > 0 ? amountWithTax.divide(percentPlusOne, rounding, roundingMode) : amountWithoutTax;
        }

        BigDecimal amountTax = amountWithTax.subtract(amountWithoutTax);

        return new BigDecimal[] { amountWithoutTax, amountWithTax, amountTax };
    }

    /**
     * Compute derived amounts: amountWithoutTax/amountWithTax with rounding applied.
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param amountTax Tax amount
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     * @return Calculated amount values as array [amountWithoutTax, amountWithTax, amountTax]
     */
    public static BigDecimal[] computeDerivedAmountsWoutTaxPercent(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, boolean isEnterprise, int rounding, RoundingMode roundingMode) {

        if (isEnterprise) {
            amountWithoutTax = amountWithoutTax.setScale(rounding, roundingMode);
            amountTax = amountTax.setScale(rounding, roundingMode);
            amountWithTax = amountWithoutTax.add(amountTax);

        } else {
            amountWithTax = amountWithTax.setScale(rounding, roundingMode);
            amountTax = amountTax.setScale(rounding, roundingMode);
            amountWithoutTax = amountWithTax.subtract(amountTax);
        }

        return new BigDecimal[] { amountWithoutTax, amountWithTax, amountTax };
    }
    
	public static BigDecimal computeTax(BigDecimal amountWithoutTax, BigDecimal taxPercent, int rounding,
			RoundingMode roundingMode) {
		taxPercent = taxPercent != null ? taxPercent : BigDecimal.ZERO;
		amountWithoutTax.setScale(rounding, roundingMode);
		BigDecimal tax = amountWithoutTax.multiply(taxPercent).divide(new BigDecimal(100), rounding, roundingMode);
		return tax;
	}

    public static long parseLongDefault(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}