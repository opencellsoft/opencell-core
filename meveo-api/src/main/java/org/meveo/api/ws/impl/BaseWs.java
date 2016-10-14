package org.meveo.api.ws.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.SuperAdminPermission;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
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
            getCurrentUser();
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Authenticate a user requiring a particular permission <permissionResource>/<permissionAction> apart of a standard permission required to access WS
     * 
     * @param permissionResource Resource of permission
     * @param permissionAction Permission action of permission
     * @return Authenticated user
     * @throws LoginException In case a user does not have a required permission
     */
    protected User getCurrentUser(String permissionResource, String permissionAction) throws LoginException {
        User user = getCurrentUser();
        if (user.hasPermission(permissionResource, permissionAction)) {
            return user;
        }
        throw new LoginException("User does not have permission '" + permissionAction + "' on resource '" + permissionResource + "'");
    }

    /**
     * Authenticate a user requiring a standard permission user/apiAccess to access WS
     * 
     * @return Authenticated user
     * @throws LoginException In case a user does not have a required permission
     */
    @SuppressWarnings("unchecked")
    protected User getCurrentUser() throws LoginException {
        MessageContext messageContext = webServiceContext.getMessageContext();

        // get request headers
        Map<?, ?> requestHeaders = (Map<?, ?>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);

        List<Object> authorizationHeader = (List<Object>) requestHeaders.get("Authorization");
        if (authorizationHeader == null || authorizationHeader.size() == 0) {
            throw new LoginException("Authentication failed! This WS needs BASIC Authentication!");
        }

        String userpass = (String) authorizationHeader.get(0);
        userpass = userpass.substring(5);
        byte[] buf = Base64.decodeBase64(userpass.getBytes());
        String credentials = new String(buf);

        String username = null;
        String password = null;
        int p = credentials.indexOf(":");
        if (p > -1) {
            username = credentials.substring(0, p);
            password = credentials.substring(p + 1);
        } else {
            throw new RuntimeException("There was an error while decoding the Authentication!");
        }

        if (StringUtils.isBlank(username) | StringUtils.isBlank(password)) {
            throw new LoginException("Username and password are required.");
        }

        User user = null;
        try {
            user = userService.loginChecks(username, password);
        } catch (org.meveo.admin.exception.LoginException e) {
            throw new LoginException("Authentication failed!");
        }

        if (user == null) {
            throw new LoginException("Authentication failed!");
        }        
        boolean isAllowed = false;

        if( this.getClass().isAnnotationPresent(SuperAdminPermission.class)){
        	  isAllowed = user.hasPermission("superAdmin", "superAdminManagement");
        }else{
        	isAllowed = user.hasPermission("user", "apiAccess");
        }
        // check if has api permission
       
        if (!isAllowed) {
            throw new LoginException(user.getUserName(), "Authentication failed! Insufficient privilege to access API services!");
        }

        return user;
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
            log.error("Failed to execute API", e);
            status.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            status.setStatus(ActionStatusEnum.FAIL);
            status.setMessage(e.getMessage());
        }
    }
}