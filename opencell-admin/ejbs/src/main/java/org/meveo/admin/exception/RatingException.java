/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.exception;

import javax.ejb.ApplicationException;

import org.meveo.model.rating.EDRRejectReasonEnum;

/**
 * Groups rating related exceptions
 * 
 * @author Andrius Karpavicius
 */
@ApplicationException(rollback = false)
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