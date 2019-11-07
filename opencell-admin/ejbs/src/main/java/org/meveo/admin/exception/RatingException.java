package org.meveo.admin.exception;

import javax.ejb.ApplicationException;

import org.meveo.model.rating.EDRRejectReasonEnum;

/**
 * Groups rating related exceptions
 * 
 * @author Andrius Karpavicius
 */
@ApplicationException(rollback = true)
public class RatingException extends BusinessException {

    private static final long serialVersionUID = -4006839791846079300L;

    protected EDRRejectReasonEnum rejectionReason;

    public RatingException() {
        super();
    }

    public RatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RatingException(String message) {
        super(message);
    }

    public RatingException(Throwable cause) {
        super(cause);
    }

    public RatingException(EDRRejectReasonEnum rejectionReason, String message, Throwable cause) {
        super(message, cause);
        this.rejectionReason = rejectionReason;
    }

    public RatingException(EDRRejectReasonEnum rejectionReason, String message) {
        super(message);
        this.rejectionReason = rejectionReason;
    }

    public RatingException(EDRRejectReasonEnum rejectionReason) {
        super(rejectionReason.getCode());
        this.rejectionReason = rejectionReason;
    }

    public EDRRejectReasonEnum getRejectionReason() {
        return rejectionReason;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (!(this instanceof RatingScriptExecutionErrorException)) {
            return null;
        } else {
            return super.fillInStackTrace();
        }
    }

    /**
     * Provide a business exception without a stack trace
     * 
     * @return Business exception
     */
    public BusinessException getBusinessException() {
        return new BusinessException(this) {

            private static final long serialVersionUID = -3378218824502032575L;

            @Override
            public synchronized Throwable fillInStackTrace() {
                return null;
            }
        };
    }
}