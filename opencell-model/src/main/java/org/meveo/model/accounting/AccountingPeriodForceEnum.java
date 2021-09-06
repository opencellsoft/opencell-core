package org.meveo.model.accounting;

public enum AccountingPeriodForceEnum {
	/**
	 * The first day of the opened sub period
	 */
    FIRST_DAY,
    /**
     * the first sunday of the opened sub-period
     */
    FIRST_SUNDAY,
    /**
     * the custom day of the month (1,2,3,…), stored in forceCustomDay of accounting period table 
     */
    CUSTOM_DAY
}
