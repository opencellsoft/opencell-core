package org.meveo.model.catalog;

/**
 * Type of discounts applied to BillingAccount.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public enum DiscountPlanItemTypeEnum {

    /**
     * Percentage type of discount
     */
    PERCENTAGE,

    /**
     * Fixed amount type of discount
     */
    FIXED;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}