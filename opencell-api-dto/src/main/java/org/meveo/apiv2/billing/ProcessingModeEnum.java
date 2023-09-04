package org.meveo.apiv2.billing;

/**
 * How processing of CDRs should behave in the event of an error while processing a list of CDRs
 */
public enum ProcessingModeEnum {

    /**
     * Do not process remaining CDRs after the first CDR fails
     */
    STOP_ON_FIRST_FAIL,

    /**
     * Continue processing remaining CDRs
     */
    PROCESS_ALL,

    /**
     * Rollback in case of failure to process any CDR
     */
    ROLLBACK_ON_ERROR
}
