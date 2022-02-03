package org.meveo.model.billing.threshold;

public enum ThresholdLevelEnum {
	NONE("ThresholdLevelEnum.none"), INVOICE("ThresholdLevelEnum.invoice"), BILLING_ACCOUNT("ThresholdLevelEnum.billingAccount"),
	CUSTOMER_ACCOUNT("ThresholdLevelEnum.customerAccount"), CUSTOMER("ThresholdLevelEnum.customer");

	private String label;

	/**
	 * Default constructor.
	 *
	 * @param label
	 */
	private ThresholdLevelEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return name();
	}
}
