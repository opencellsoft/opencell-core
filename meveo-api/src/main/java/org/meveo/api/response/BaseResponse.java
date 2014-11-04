package org.meveo.api.response;

import java.io.Serializable;

import org.meveo.api.ActionStatus;

/**
 * All the MEVEO API web service response must extend this class.
 * 
 * @author Edward P. Legaspi
 **/
public abstract class BaseResponse implements Serializable {

	private static final long serialVersionUID = -4985814323159091933L;

	/**
	 * The status response of the web service response.
	 */
	private ActionStatus actionStatus;

	public BaseResponse() {
		actionStatus = new ActionStatus();
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

}
