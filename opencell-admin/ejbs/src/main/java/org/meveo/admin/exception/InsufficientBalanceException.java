package org.meveo.admin.exception;

import org.meveo.model.rating.EDRRejectReasonEnum;

/**
 * Insufficient prepaid balance
 * 
 * @author Andrius Karpavicius
 *
 */
public class InsufficientBalanceException extends RatingException {

    private static final long serialVersionUID = 8152437282691490974L;

    /**
     * Constructor
     */
    public InsufficientBalanceException() {
        super(EDRRejectReasonEnum.INSUFFICIENT_BALANCE);
    }

    /**
     * Constructor
     * 
     * @param message Error message
     */
    public InsufficientBalanceException(String message) {
        super(EDRRejectReasonEnum.INSUFFICIENT_BALANCE, message);
    }
}