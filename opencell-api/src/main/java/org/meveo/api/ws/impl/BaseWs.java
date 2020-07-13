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

package org.meveo.api.ws.impl;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.service.admin.impl.UserService;
import org.meveo.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseWs {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected WebServiceContext webServiceContext;

    @Inject
    protected UserService userService;

    @Inject
    private ResourceBundle resourceMessages;

    /**
     * Unique constraint exception parsing to retrieve fields and values affected by the constraint. Might be Postgres specific.
     */
    private static Pattern uniqueConstraintMsgPattern = Pattern.compile(".*violates unique constraint.*\\R*.*: Key \\((.*)\\)=\\((.*)\\).*");

    /**
     * Check constraint exception parsing to retrieve a constraint name. Might be Postgres specific.
     */
    private static Pattern checkConstraintMsgPattern = Pattern.compile(".*violates check constraint \"(\\w*)\".*\\R*.*");

    @WebMethod
    public ActionStatus index() {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "MEVEO API Web Service V" + Version.appVersion);
        try {
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Process exception and update status of response
     * 
     * @param e Exception
     * @param status Status dto to update
     */
    protected void processException(Exception e, ActionStatus status) {

        if (e instanceof MeveoApiException) {
            status.setErrorCode(((MeveoApiException) e).getErrorCode());
            status.setStatus(ActionStatusEnum.FAIL);
            status.setMessage(e.getMessage());

        } else {
            if (e instanceof ValidationException) {
                log.error("Failed to execute API: {}", e.getMessage());
            } else {
                log.error("Failed to execute API", e);
            }

            MeveoApiErrorCodeEnum errorCode = e instanceof InsufficientBalanceException ? MeveoApiErrorCodeEnum.INSUFFICIENT_BALANCE
                    : e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;

            // See if can get to the root of the exception cause
            String message = e.getMessage();
            String messageKey = null;
            boolean validation = false;
            Throwable cause = e;
            while (cause != null) {

                if (cause instanceof SQLException || cause instanceof BusinessException) {
                    message = cause.getMessage();
                    if (cause instanceof ValidationException) {
                        validation = true;
                        messageKey = ((ValidationException) cause).getMessageKey();
                    }
                    break;

                } else if (cause instanceof ConstraintViolationException) {

                    StringBuilder builder = new StringBuilder();
                    builder.append("Invalid values passed: ");
                    for (ConstraintViolation<?> violation : ((ConstraintViolationException) cause).getConstraintViolations()) {
                        builder.append(
                            String.format("    %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                    }
                    message = builder.toString();
                    errorCode = MeveoApiErrorCodeEnum.INVALID_PARAMETER;
                    break;

                } else if (cause instanceof org.hibernate.exception.ConstraintViolationException) {

                    message = ((org.hibernate.exception.ConstraintViolationException) cause).getSQLException().getMessage();

                    log.error("Database operation was unsuccessful because of constraint violation: " + message);

                    Matcher matcherUnique = uniqueConstraintMsgPattern.matcher(message);
                    Matcher matcherCheck = checkConstraintMsgPattern.matcher(message);
                    if (matcherUnique.matches() && matcherUnique.groupCount() == 2) {
                        message = resourceMessages.getString("commons.unqueFieldWithValue", matcherUnique.group(1), matcherUnique.group(2));
                        errorCode = MeveoApiErrorCodeEnum.ENTITY_ALREADY_EXISTS_EXCEPTION;

                    } else if (matcherCheck.matches() && matcherCheck.groupCount() == 1) {
                        message = resourceMessages.getString("error.database.constraint.violationWName", matcherCheck.group(1));
                        errorCode = MeveoApiErrorCodeEnum.INVALID_PARAMETER;

                    } else {
                        errorCode = MeveoApiErrorCodeEnum.INVALID_PARAMETER;
                    }
                    break;
                }
                cause = cause.getCause();
            }

            if (validation && messageKey != null) {
                message = resourceMessages.getString(messageKey);

            } else if (message == null) {
                message = e.getClass().getSimpleName();
            }

            status.setErrorCode(errorCode);
            status.setStatus(ActionStatusEnum.FAIL);
            status.setMessage(message);

        }
    }

    protected HttpServletRequest getHttpServletRequest() {
        MessageContext mc = webServiceContext.getMessageContext();
        return (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
    }
}