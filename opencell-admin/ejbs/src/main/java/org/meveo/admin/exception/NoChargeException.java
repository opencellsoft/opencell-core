package org.meveo.admin.exception;

import org.meveo.model.rating.EDRRejectReasonEnum;

/**
 * No active charges are associated to subscription
 * 
 * @author Andrius Karpavicius
 *
 */
public class NoChargeException extends RatingException {

    private static final long serialVersionUID = -727486466593597624L;

    public NoChargeException() {
        super(EDRRejectReasonEnum.SUBSCRIPTION_HAS_NO_CHARGE);
    }

    /**
     * Constructor
     * 
     * @param message Error message
     */
    public NoChargeException(String message) {
        super(EDRRejectReasonEnum.SUBSCRIPTION_HAS_NO_CHARGE, message);
    }

    /**
     * Constructor
     * 
     * @param rejectionReason Rejection reason
     * @param message Error message
     */
    public NoChargeException(EDRRejectReasonEnum rejectionReason, String message) {
        super(rejectionReason, message);
    }
}