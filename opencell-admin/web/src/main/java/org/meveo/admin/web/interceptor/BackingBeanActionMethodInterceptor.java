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

package org.meveo.admin.web.interceptor;

import java.io.Serializable;
import java.sql.SQLException;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.TransactionRequiredException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.encryption.EncyptionException;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.service.audit.AuditOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles exceptions of backing bean action methods
 *
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@ActionMethod
@Interceptor
public class BackingBeanActionMethodInterceptor implements Serializable {

    private static final long serialVersionUID = -8361765042326423662L;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    protected Messages messages;

    @Inject
    private AuditOrigin auditOrigin;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {

        Object result = null;
        try {

            auditOrigin.setAuditOrigin(ChangeOriginEnum.GUI);
            auditOrigin.setAuditOriginName(FacesContext.getCurrentInstance().getViewRoot().getViewId());

            // Call a backing bean method
            result = invocationContext.proceed();
            return result;

        } catch (TransactionRequiredException e) {
            log.error("Transaction must have been rollbacked already (probably by exception thown in service and caught in backing bean): {}", e.getMessage());
            return result;
        } catch (Exception e) {

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
                        builder.append(String.format("    %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(),
                            violation.getInvalidValue(), violation.getMessage()));
                    }
                    message = builder.toString();
                    break;

                } else if (cause instanceof org.hibernate.exception.ConstraintViolationException) {

                	message = ((org.hibernate.exception.ConstraintViolationException)cause).getSQLException().getMessage();
                    log.error("Database operation was unsuccessful because of constraint violation: "+message);
                    messageKey = "error.database.constraint.violation";
                    break;
                    
                } else if (cause instanceof EncyptionException) {
                    message = cause.getMessage();
                    log.error("Error while de/encrypting: " + cause.getMessage(), cause);
                    break;
                }
                cause = cause.getCause();
            }

            messages.clear();

            if (validation && messageKey != null) {
                messages.error(new BundleKey("messages", messageKey));
            } else if (validation && message != null) {
                messages.error(message);

            } else {
                log.error("Failed to execute {}.{} method due to errors ", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext.getMethod().getName(), e);
                if (message != null) {
                    message = StringEscapeUtils.escapeJava(message);
                    message = message.replace("$", "\\$");
                }
                messages.error(new BundleKey("messages", messageKey != null ? messageKey : "error.action.failed"), message == null ? e.getClass().getSimpleName() : message);
            }
            FacesContext.getCurrentInstance().validationFailed();
        }

        return null;
    }
}
