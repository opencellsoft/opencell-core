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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles exceptions of backing bean action methods
 *
 * @author Andrius Karpavicius
 */
@ActionMethod
@Interceptor
public class BackingBeanActionMethodInterceptor implements Serializable {

    private static final long serialVersionUID = -8361765042326423662L;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    protected Messages messages;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {

        Object result = null;
        try {
            // Call a backing bean method
            result = invocationContext.proceed();
            return result;

        } catch (TransactionRequiredException e) {
            log.error("Transaction must have been rollbacked already (probably by exception thown in service and caught in backing bean): {}", e.getMessage());
            if (result != null) {
                return result;
            }

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
                    log.error("Delete was unsuccessful because entity is already in use.");
                    messageKey = "error.delete.entityUsed";
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
