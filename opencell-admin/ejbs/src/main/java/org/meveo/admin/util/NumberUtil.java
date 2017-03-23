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

		BigDecimal result = unitValue.multiply(unitMultiplicator);			
		result = result.setScale(unitNbDecimal, getRoundingMode(roundingModeEnum));
		return result;
	}
	
	public static RoundingMode getRoundingMode(RoundingModeEnum roundingModeEnum){
		if (roundingModeEnum == null){
			return RoundingMode.HALF_UP;
		}
		
		if (RoundingModeEnum.DOWN.name().equals(roundingModeEnum.name())) {
			return RoundingMode.FLOOR;
		} 
		
		if (RoundingModeEnum.UP.name().equals(roundingModeEnum.name())) {
			return RoundingMode.CEILING;
		} 
			
		return RoundingMode.HALF_UP;		
	}
}
