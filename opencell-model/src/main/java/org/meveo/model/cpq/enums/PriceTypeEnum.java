package org.meveo.model.cpq.enums;

import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.TerminationChargeInstance;
import org.meveo.model.billing.UsageChargeInstance;

public enum PriceTypeEnum {

	 RECURRING("recurring"), ONE_SHOT_SUBSCRIPTION("oneShot_subscription"),ONE_SHOT_TERMINATION("oneShot_termination"), ONE_SHOT_OTHER("oneShot_other"), 
	 USAGE("usage"), FIXED_DISCOUNT("fixed discount");
	
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
			if(chargeInstance instanceof SubscriptionChargeInstance) {
				return ONE_SHOT_SUBSCRIPTION;
			}else if(chargeInstance instanceof TerminationChargeInstance)
				return ONE_SHOT_TERMINATION;
			return ONE_SHOT_OTHER;
		}else if(chargeInstance instanceof UsageChargeInstance) {
			return USAGE;
		}
		return null;
	}
}
