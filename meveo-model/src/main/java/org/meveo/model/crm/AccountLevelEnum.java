package org.meveo.model.crm;

public enum AccountLevelEnum {
	CUST, CA, BA, UA;

	public String getLabel() {
		return "enum.accountLevel." + this.name();
	}

}
