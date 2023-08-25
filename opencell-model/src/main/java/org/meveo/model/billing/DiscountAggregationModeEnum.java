package org.meveo.model.billing;

/**
 * Discount type Rated transaction aggregation mode
 */
public enum DiscountAggregationModeEnum {
    /**
     * Each discount rated item will generate its own invoice line.
     */
    NO_AGGREGATION,

    /**
     * All rated items will be aggregated regardless of discount details
     */
    FULL_AGGREGATION
}