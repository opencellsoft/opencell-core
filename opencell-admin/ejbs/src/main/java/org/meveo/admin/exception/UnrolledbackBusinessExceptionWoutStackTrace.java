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

/**
 * A business exception that will not rollback the DB transaction and wont print stack trace in the logs
 * 
 * @author Andrius Karpavicius
 *
 */
public class UnrolledbackBusinessExceptionWoutStackTrace extends UnrolledbackBusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception
     */
    public UnrolledbackBusinessExceptionWoutStackTrace() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnrolledbackBusinessExceptionWoutStackTrace(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message
     * 
     * @param message The detail message
     */
    public UnrolledbackBusinessExceptionWoutStackTrace(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause
     * 
     * @param cause the cause
     */
    public UnrolledbackBusinessExceptionWoutStackTrace(Throwable cause) {
        super(cause);
    }

    /**
     * Stacktrace is not of interest here
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}