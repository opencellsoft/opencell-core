package org.meveo.api.rest.impl;

import static java.util.stream.Collectors.joining;

import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.exception.AlreadyExistException;
import org.meveo.api.rest.exception.BadRequestException;
import org.meveo.api.rest.exception.ForbiddenException;
import org.meveo.api.rest.exception.InternalServerErrorException;
import org.meveo.api.rest.exception.NotAuthorizedException;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionProcessorRs {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Unique constraint exception parsing to retrieve fields and values affected by the constraint. Might be Postgres specific.
     */
    private static Pattern uniqueConstraintMsgPattern = Pattern.compile(".*violates unique constraint.*\\R*.*: Key \\((.*)\\)=\\((.*)\\).*");

    /**
     * Check constraint exception parsing to retrieve a constraint name. Might be Postgres specific.
     */
    private static Pattern checkConstraintMsgPattern = Pattern.compile(".*violates check constraint \"(\\w*)\".*\\R*.*");

    private ResourceBundle resourceMessages;

    /**
     * @param resourceMessages
     */
    public ExceptionProcessorRs(ResourceBundle resourceMessages) {
        this.resourceMessages = resourceMessages;
    }

    public void process(Exception e, ActionStatus status) {
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

                    Set<ConstraintViolation<?>> constraintsViolation = ((ConstraintViolationException) cause).getConstraintViolations();
                    message = buildErrorMessage(constraintsViolation);
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

    public String buildErrorMessage(Set<ConstraintViolation<?>> constraintsViolation) {
        return constraintsViolation.stream()
            .map(violation -> String.format("    %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()))
            .sorted().collect(joining("", "Invalid values passed: ", ""));
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
            } else if ("DELETE_REFERENCED_ENTITY_EXCEPTION".equals(str) //
                    || "ACTION_FORBIDDEN".equals(str)//
                    || "INSUFFICIENT_BALANCE".equals(str)) {
                throw new ForbiddenException(status);
            } else if ("ENTITY_DOES_NOT_EXISTS_EXCEPTION".equals(str)) {
                throw new NotFoundException(status);
            } else if ("ENTITY_ALREADY_EXISTS_EXCEPTION".equals(str)) {
                throw new AlreadyExistException(status);
            } else {
                throw new InternalServerErrorException(status);
            }
        }
    }
}
