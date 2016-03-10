package org.meveo.api.logging;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * 
 *         Logs the calls to the REST and WS interfaces, handles DB level errors by forcing commit
 **/
public class WsRestApiInterceptor {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    @MeveoJpaForJobs
    private EntityManager emfForJobs;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {
        log.debug("\r\n\r\n===========================================================");
        log.debug("Entering method {}.{}", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext.getMethod().getName());

        if (invocationContext.getParameters() != null) {
            for (Object obj : invocationContext.getParameters()) {
                log.debug("Parameter {}", obj == null ? null : obj.toString());
            }
        }

        // Call the actual REST/WS method
        Object apiResult = invocationContext.proceed();

        // Try committing if status is SUCCESS
        ActionStatus actionStatus = null;

        if (apiResult instanceof BaseResponse) {
            actionStatus = ((BaseResponse) apiResult).getActionStatus();
        } else if (apiResult instanceof ActionStatus) {
            actionStatus = (ActionStatus) apiResult;
        }

        if (actionStatus != null && actionStatus.getStatus() == ActionStatusEnum.SUCCESS) {
            try {
                emfForJobs.flush();
            } catch (ConstraintViolationException e) {
                log.error("Failed to execute {}.{} method due to DTO validation errors ", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext
                    .getMethod().getName(), e);
                actionStatus.setStatus(ActionStatusEnum.FAIL);
                actionStatus.setErrorCode(MeveoApiErrorCodeEnum.INVALID_PARAMETER);
                StringBuilder builder = new StringBuilder();
                builder.append("Invalid values passed: ");
                for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                    builder.append(String.format("    %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(),
                        violation.getInvalidValue(), violation.getMessage()));
                }

                actionStatus.setMessage(builder.toString());

            } catch (Exception e) {
                log.error("Failed to execute {}.{} method due to DB level errors ", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext.getMethod()
                    .getName(), e);
                actionStatus.setStatus(ActionStatusEnum.FAIL);
                actionStatus.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
                actionStatus.setMessage(e.getMessage());
            }
        }

        log.debug("Finished method {}.{}", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext.getMethod().getName());

        return apiResult;
    }

}
