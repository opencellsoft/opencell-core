package org.meveo.api.logging;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Logs the calls to the REST and WS interfaces. Sets up logging MDC context.
 * 
 * @author Edward P. Legaspi
 * 
 **/
public class WsRestApiInterceptor {

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {

        if (currentUser.getProviderCode() == null) {
            MDC.remove("providerCode");
        } else {
            MDC.put("providerCode", currentUser.getProviderCode());
        }

        if (log.isDebugEnabled()) {
            log.debug("\r\n\r\n===========================================================");
            log.debug("Entering method {}.{}", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext.getMethod().getName());

            if (invocationContext.getParameters() != null) {
                for (Object obj : invocationContext.getParameters()) {
                    log.debug("Parameter {}", obj == null ? null : obj.toString());
                }
            }
        }

        // Call the actual REST/WS method
        // ActionStatus actionStatus = null;
        Object apiResult = null;

        // try {
        apiResult = invocationContext.proceed();
        //
        //
        // } catch (TransactionRequiredException e) {
        // log.error("Transaction must have been rollbacked already (probably by exception thown in service and caught in backing bean): {}", e.getMessage());
        //
        // } catch (ConstraintViolationException e) {
        // log.error("Failed to execute {}.{} method due to DTO validation errors ", invocationContext.getMethod().getDeclaringClass().getName(),
        // invocationContext.getMethod().getName(), e);
        //
        // // Need to create a result, if it is the invocationContext.proceed() method that caused the error
        // if (actionStatus == null) {
        // apiResult = invocationContext.getMethod().getReturnType().newInstance();
        // if (apiResult instanceof BaseResponse) {
        // actionStatus = ((BaseResponse) apiResult).getActionStatus();
        // } else if (apiResult instanceof ActionStatus) {
        // actionStatus = (ActionStatus) apiResult;
        // }
        // }
        //
        // actionStatus.setStatus(ActionStatusEnum.FAIL);
        // actionStatus.setErrorCode(MeveoApiErrorCodeEnum.INVALID_PARAMETER);
        // StringBuilder builder = new StringBuilder();
        // builder.append("Invalid values passed: ");
        // for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
        // builder.append(String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(),
        // violation.getInvalidValue(), violation.getMessage()));
        // }
        //
        // actionStatus.setMessage(builder.toString());
        //
        // } catch (BusinessException e) {
        // log.error("Failed to execute {}.{} method due to DB level errors ", invocationContext.getMethod().getDeclaringClass().getName(),
        // invocationContext.getMethod().getName(), e);
        //
        // // Need to create a result, if it is the invocationContext.proceed() method that caused the error
        // if (actionStatus == null) {
        // apiResult = invocationContext.getMethod().getReturnType().newInstance();
        // if (apiResult instanceof BaseResponse) {
        // actionStatus = ((BaseResponse) apiResult).getActionStatus();
        // } else if (apiResult instanceof ActionStatus) {
        // actionStatus = (ActionStatus) apiResult;
        // }
        // }
        // actionStatus.setStatus(ActionStatusEnum.FAIL);
        // actionStatus.setErrorCode(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION);
        // actionStatus.setMessage(e.getMessage());
        //
        // } catch (Exception e) {
        // log.error("Failed to execute {}.{} method due to DB level errors ", invocationContext.getMethod().getDeclaringClass().getName(),
        // invocationContext.getMethod().getName(), e);
        //
        // // Need to create a result, if it is the invocationContext.proceed() method that caused the error
        // if (actionStatus == null) {
        // apiResult = invocationContext.getMethod().getReturnType().newInstance();
        // if (apiResult instanceof BaseResponse) {
        // actionStatus = ((BaseResponse) apiResult).getActionStatus();
        // } else if (apiResult instanceof ActionStatus) {
        // actionStatus = (ActionStatus) apiResult;
        // }
        // }
        //
        // actionStatus.setStatus(ActionStatusEnum.FAIL);
        // actionStatus.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
        // actionStatus.setMessage(e.getMessage());
        // }

        log.debug("Finished method {}.{}", invocationContext.getMethod().getDeclaringClass().getName(), invocationContext.getMethod().getName());

        MDC.remove("providerCode");

        return apiResult;
    }

}
