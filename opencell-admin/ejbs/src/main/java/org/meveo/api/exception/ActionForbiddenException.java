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

package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * Action execution was not allowed
 * 
 * @author Adrius Karpavicius
 * 
 **/
public class ActionForbiddenException extends MeveoApiException {

    private static final long serialVersionUID = -3436733471648721659L;

    private String reason;

    public ActionForbiddenException() {
    }

    public ActionForbiddenException(String message) {
        super(message);
        setErrorCode(MeveoApiErrorCodeEnum.ACTION_FORBIDDEN);
    }

    @SuppressWarnings("rawtypes")
    public ActionForbiddenException(Class entityClass, String entityCode, String action, String reason) {
        super("Action '" + action + "' on entity '" + entityClass.getName() + "' with code '" + entityCode + "' is not allowed  for reason: " + reason + "'");

        this.reason = reason;

        setErrorCode(MeveoApiErrorCodeEnum.ACTION_FORBIDDEN);
    }

    public String getReason() {
        return reason;
    }

    /**
     * Stacktrace is not of interest here
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}