package org.meveo.admin.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.meveo.model.catalog.RoundingModeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class NumberUtil {

	public static BigDecimal getInChargeUnit(BigDecimal unitValue, BigDecimal unitMultiplicator, Integer unitNbDecimal, RoundingModeEnum roundingModeEnum) {
		if (unitMultiplicator == null){
			unitMultiplicator = BigDecimal.ONE;
		}	

		if (unitNbDecimal == null){
			unitNbDecimal = new Integer(2);
		}
		if (roundingModeEnum == null){
			roundingModeEnum = RoundingModeEnum.NEAREST;
		}

		BigDecimal result = unitValue.multiply(unitMultiplicator);
		
		if (RoundingModeEnum.DOWN == roundingModeEnum) {
			result = result.setScale(unitNbDecimal, RoundingMode.FLOOR);
		} else if (RoundingModeEnum.UP == roundingModeEnum) {
			result = result.setScale(unitNbDecimal, RoundingMode.CEILING);
		} else {
			result = result.setScale(unitNbDecimal, RoundingMode.HALF_UP);
		}

		return result;
	}
}
