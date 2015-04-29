package org.meveo.model.crm;

public enum AccountLevelEnum {
	CUST, CA, BA, UA, SUB, ACC, CHARGE, OFFER, SERVICE,TIMER;

	public String getLabel() {
		return "enum.accountLevel." + this.name();
	}

}
