package org.meveo.model.billing;

/**
 * Rated transaction type
 */
public enum RatedTransactionTypeEnum {

    /**
     * A regular, created from a single Wallet operation
     */
    REGULAR,

    /**
     * A rated transaction created to reach a minimum invoiceable amount
     */
    MINIMUM,

    /**
     * An aggregation of multiple Wallet operations
     */
    AGGREGATED,

    /**
     * Does not have a corresponding Wallet operation
     */
    MANUAL;
}