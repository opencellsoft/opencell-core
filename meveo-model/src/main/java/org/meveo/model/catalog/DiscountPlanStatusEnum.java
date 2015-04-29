package org.meveo.model.catalog;

/**
 * @author Edward P. Legaspi
 **/
public enum DiscountPlanStatusEnum {
	ACTIVE, INACTIVE;

	public String getLabel() {
		return "enum.DiscountPlanStatusEnum." + name();
	}
}
