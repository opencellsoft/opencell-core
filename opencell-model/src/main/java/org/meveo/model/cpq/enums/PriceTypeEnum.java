package org.meveo.model.cpq.enums;

import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.UsageChargeInstance;

public enum PriceTypeEnum {

	 RECURRING("recurring"), ONE_SHOT("oneShot"), USAGE("usage");
	
	private String value;
	
	private PriceTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static PriceTypeEnum getPriceTypeEnum(ChargeInstance chargeInstance) {
		if(chargeInstance instanceof RecurringChargeInstance) {
			return RECURRING;
		}else if(chargeInstance instanceof OneShotChargeInstance) {
			return ONE_SHOT;
		}else if(chargeInstance instanceof UsageChargeInstance) {
			return USAGE;
		}
		return null;
	}
}
