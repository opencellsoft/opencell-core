package org.meveo.model.catalog;

/**
 * Type of discounts applied to BillingAccount.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public enum DiscountPlanItemTypeEnum {

	PERCENTAGE("PERCENTAGE", "DiscountPlanItemTypeEnum.Percentage"), //
	FIXED("PERCENTAGE", "DiscountPlanItemTypeEnum.Fixed");

	private String id;
	private String label;

	private DiscountPlanItemTypeEnum(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

}
