package org.meveo.api.dto.response;

import java.io.Serializable;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * All the Opencell API web service response must extend this class.
 * 
 * @author Edward P. Legaspi
 **/
@JsonInclude(Include.NON_NULL)
public abstract class BaseResponse implements Serializable {

    private static final long serialVersionUID = -4985814323159091933L;

    /**
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();

    public BaseResponse() {
        actionStatus = new ActionStatus();
    }

    public BaseResponse(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        actionStatus = new ActionStatus(status, errorCode, message);
    }

    public ActionStatus getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    @Override
    public String toString() {
        return "BaseResponse [actionStatus=" + actionStatus + "]";
    }
}