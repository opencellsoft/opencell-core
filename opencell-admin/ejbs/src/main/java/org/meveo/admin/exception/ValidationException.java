/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
}