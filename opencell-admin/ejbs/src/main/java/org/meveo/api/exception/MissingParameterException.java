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

import java.util.List;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class MissingParameterException extends MeveoApiException {

    private static final long serialVersionUID = -7101565234776606126L;

    public static String MESSAGE_TEXT = "The following parameters are required or contain invalid values: ";

    public MissingParameterException(String fieldName) {
        super(MESSAGE_TEXT + fieldName);
        setErrorCode(MeveoApiErrorCodeEnum.MISSING_PARAMETER);
    }

    public MissingParameterException(List<String> missingFields) {
        super(composeMessage(missingFields));
        setErrorCode(MeveoApiErrorCodeEnum.MISSING_PARAMETER);
    }

    private static String composeMessage(List<String> missingFields) {
        StringBuilder sb = new StringBuilder(MESSAGE_TEXT);

        if (!missingFields.isEmpty()) {
            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");
        }

        return sb.toString();
    }

    /**
     * Stacktrace is not of interest here
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}