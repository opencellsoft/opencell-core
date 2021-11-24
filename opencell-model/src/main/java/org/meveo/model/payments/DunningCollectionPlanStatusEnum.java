package org.meveo.model.payments;

public enum DunningCollectionPlanStatusEnum {

	/**
     * For ongoing collection plans that are following the dunning levels defined in the related dunning policy.
     * The status is defined for each dunning level in dunning level configuration.
     */
    ACTIVE,
    
    /**
     * When collection plan is interrupted because invoice is paid
     */
    SUCCESS,

    /**
     * When collection plan reaches the end of dunning level and balance is still positive.
     * The status is defined in the end of dunning level for each policy and can be different for each policy depending on the next step.
     */
    FAILED,
    
    /**
     * When user pauses a collection plan
     */
    PAUSED,
    
    /**
     * When user stop a collection plan
     */
    STOPPED;

}
