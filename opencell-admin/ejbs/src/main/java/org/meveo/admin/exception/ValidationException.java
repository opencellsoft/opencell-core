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
 * A type of Business exception that denotes some data validation issue. Primarily used to show less "tech" stuff in error display to end user in GUI or API.
 * 
 * @author Andrius Karpavicius
 *
 */
public class ValidationException extends BusinessException {

    private static final long serialVersionUID = 4921614951372762464L;

    private String messageKey;

    public ValidationException() {
        super();
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    /**
     * Exception constructor
     * 
     * @param message Message to log or display in GUI if message key is not provided
     * @param messageKey An optional message key to be displayed in GUI
     */
    public ValidationException(String message, String messageKey) {
        super(message);
        this.messageKey = messageKey;
    }

    /**
     * Exception constructor
     * 
     * @param message Message to log or display in GUI if message key is not provided
     * @param messageKey An optional message key to be displayed in GUI
     * @param cause Original exception
     */
    public ValidationException(String message, String messageKey, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Stacktrace is not of interest here
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}