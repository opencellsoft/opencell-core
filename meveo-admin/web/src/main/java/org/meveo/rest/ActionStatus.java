package org.meveo.rest;

/**
 * @author Edward P. Legaspi
 * @since Oct 3, 2013
 **/
public class ActionStatus {
	private int status;
	private String message;

	public ActionStatus() {
	}
	
	public ActionStatus(ActionStatusEnum status, String message) {
		this.status = status.getStatus();
		this.message = message;
	}

	public ActionStatus(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
