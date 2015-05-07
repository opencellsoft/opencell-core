package org.meveo.admin.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Edward P. Legaspi
 **/
public class NumberUtil {

	public static BigDecimal getInChargeUnit(BigDecimal unitValue, BigDecimal unitMultiplicator, Integer unitNbDecimal) {
		if (unitMultiplicator == null)
			unitMultiplicator = BigDecimal.ONE;

		if (unitNbDecimal == null)
			unitNbDecimal = new Integer(2);

		BigDecimal result = unitValue.multiply(unitMultiplicator);

		if (unitNbDecimal.compareTo(2) > 0) {
			result = result.setScale(unitNbDecimal, RoundingMode.HALF_UP);
		}

		return result;
	}
}
