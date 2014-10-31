package org.meveo.api.response;

import java.io.Serializable;

import org.meveo.api.ActionStatus;

/**
 * <p>
 * The <code>BaseResponse</code> class is the base class of all the response of
 * MEVEO API.
 * </p>
 * 
 * @author Edward P. Legaspi
 **/
public abstract class BaseResponse implements Serializable {

	private static final long serialVersionUID = -4985814323159091933L;

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
