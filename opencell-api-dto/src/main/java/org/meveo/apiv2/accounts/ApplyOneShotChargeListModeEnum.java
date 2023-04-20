package org.meveo.apiv2.accounts;

/**
 * How processing of Applying oneshot Charge List should behave in the event of an error while processing a list of Charges
 */
public enum ApplyOneShotChargeListModeEnum {

    /**
     * Do not process remaining Charges after the first Charge fails
     */
    STOP_ON_FIRST_FAIL,

    /**
     * Continue processing remaining Charges
     */
    PROCESS_ALL,

    /**
     * Rollback in case of failure to process any Charge
     */
    ROLLBACK_ON_ERROR
}
