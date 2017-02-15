package org.meveo.api.ws.impl;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.xml.ws.WebServiceContext;

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
            status.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            status.setStatus(ActionStatusEnum.FAIL);
            status.setMessage(e.getMessage());
        }
    }
}