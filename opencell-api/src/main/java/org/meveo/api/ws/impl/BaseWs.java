package org.meveo.api.ws.impl;

import java.sql.SQLException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.meveo.admin.exception.BusinessException;
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
            log.warn("Failed to execute API", e);

            String message = e.getMessage();
            MeveoApiErrorCodeEnum errorCode = e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
            Throwable cause = e;

            // See if can get to the root of the exception cause
            if (!(e instanceof BusinessException)) {
                cause = e.getCause();
                while (cause != null) {

                    if (cause instanceof SQLException || cause instanceof BusinessException || cause instanceof ConstraintViolationException) {

                        if (cause instanceof ConstraintViolationException) {
                            ConstraintViolationException cve = (ConstraintViolationException) (cause);
                            Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
                            message = "";
                            for (ConstraintViolation<?> cv : violations) {
                                message += cv.getPropertyPath() + " " + cv.getMessage() + ",";
                            }
                            message = message.substring(0, message.length() - 1);
                            errorCode = MeveoApiErrorCodeEnum.INVALID_PARAMETER;
                        } else {
                            message = cause.getMessage();
                            errorCode = cause instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
                        }
                        break;
                    }
                    cause = cause.getCause();
                }
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