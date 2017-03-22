package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * Action execution was not allowed
 * 
 * @author Adrius Karpavicius
 * 
 **/
public class ActionForbiddenException extends MeveoApiException {

    private static final long serialVersionUID = -3436733471648721659L;

    private String reason;

    public ActionForbiddenException() {
    }

    public ActionForbiddenException(String message) {
        super(message);
        setErrorCode(MeveoApiErrorCodeEnum.ACTION_FORBIDDEN);
    }

    @SuppressWarnings("rawtypes")
    public ActionForbiddenException(Class entityClass, String entityCode, String action, String reason) {
        super("Action '" + action + "' on entity '" + entityClass.getName() + "' with code '" + entityCode + "' is not allowed  for reason: " + reason + "'");

        this.reason = reason;
        
        setErrorCode(MeveoApiErrorCodeEnum.ACTION_FORBIDDEN);
    }

    public String getReason() {
        return reason;
    }
}