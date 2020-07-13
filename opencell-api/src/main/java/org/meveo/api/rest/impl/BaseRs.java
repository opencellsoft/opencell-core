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

package org.meveo.api.rest.impl;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.exception.BadRequestException;
import org.meveo.api.rest.exception.ForbiddenException;
import org.meveo.api.rest.exception.InternalServerErrorException;
import org.meveo.api.rest.exception.NotAuthorizedException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.util.ApplicationProvider;
import org.meveo.util.MeveoParamBean;
import org.meveo.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseRs implements IBaseRs {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    @MeveoParamBean
    protected ParamBean paramBean;

    @Context
    protected HttpServletRequest httpServletRequest;

    // one way to get HttpServletResponse
    @Context
    protected HttpServletResponse httpServletResponse;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    protected final String RESPONSE_DELIMITER = " - ";

    /**
     * Unique constraint exception parsing to retrieve fields and values affected by the constraint. Might be Postgres specific.
     */
    private static Pattern uniqueConstraintMsgPattern = Pattern.compile(".*violates unique constraint.*\\R*.*: Key \\((.*)\\)=\\((.*)\\).*");

    /**
     * Check constraint exception parsing to retrieve a constraint name. Might be Postgres specific.
     */
    private static Pattern checkConstraintMsgPattern = Pattern.compile(".*violates check constraint \"(\\w*)\".*\\R*.*");

    public ActionStatus index() {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "Opencell Rest API version " + Version.appVersion + " commit " + Version.buildNumber);
        return result;
    }

    /**
     * Returns the authenticated user.
     * 
     * @return action status.
     */
    @GET
    @Path("/user")
    public ActionStatus user() {
        ActionStatus result = new ActionStatus();

        result = new ActionStatus(ActionStatusEnum.SUCCESS, "WS User is=" + currentUser.getUserName());

        return result;
    }

    protected Response.ResponseBuilder createResponseFromMeveoApiException(MeveoApiException e, ActionStatus result) {
        Response.ResponseBuilder responseBuilder = null;

        if (e instanceof EntityDoesNotExistsException) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND).entity(result);
        } else if (e instanceof EntityAlreadyExistsException) {
            responseBuilder = Response.status(Response.Status.FOUND).entity(result);
        } else if (e instanceof MissingParameterException) {
            responseBuilder = Response.status(Response.Status.PRECONDITION_FAILED).entity(result);
        } else {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
        }

        return responseBuilder;
    }

    protected ResponseBuilder createResponseFromMeveoApiException(MeveoApiException e, BaseResponse result) {
        Response.ResponseBuilder responseBuilder = null;

        if (e instanceof EntityDoesNotExistsException) {
            responseBuilder = Response.status(Response.Status.NOT_FOUND).entity(result);
        } else if (e instanceof EntityAlreadyExistsException) {
            responseBuilder = Response.status(Response.Status.FOUND).entity(result);
        } else if (e instanceof MissingParameterException) {
            responseBuilder = Response.status(Response.Status.PRECONDITION_FAILED).entity(result);
        } else {
            responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(result);
        }

        return responseBuilder;
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

        handleErrorStatus(status);
    }

    /**
     * @param status action status.
     */
    private void handleErrorStatus(ActionStatus status) {
        if (StringUtils.isBlank(status.getErrorCode())) {
            throw new InternalServerErrorException(status);
        } else {
            String str = status.getErrorCode().toString();
            if ("MISSING_PARAMETER".equals(str)//
                    || "INVALID_PARAMETER".equals(str)//
                    || "INVALID_ENUM_VALUE".equals(str)//
                    || "INVALID_IMAGE_DATA".equals(str)) {
                throw new BadRequestException(status);
            } else if ("UNAUTHORIZED".equals(str) //
                    || "AUTHENTICATION_AUTHORIZATION_EXCEPTION".equals(str)) {
                throw new NotAuthorizedException(status);
            } else if ("ENTITY_ALREADY_EXISTS_EXCEPTION".equals(str) //
                    || "DELETE_REFERENCED_ENTITY_EXCEPTION".equals(str) //
                    || "DUPLICATE_ACCESS".equals(str) || "ACTION_FORBIDDEN".equals(str)//
                    || "INSUFFICIENT_BALANCE".equals(str)) {
                throw new ForbiddenException(status);
            } else if ("ENTITY_DOES_NOT_EXISTS_EXCEPTION".equals(str)) {
                throw new NotFoundException(status);
            } else {
                throw new InternalServerErrorException(status);
            }
        }
    }
}