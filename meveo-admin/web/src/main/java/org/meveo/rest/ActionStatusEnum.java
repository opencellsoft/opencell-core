package org.meveo.rest;

/**
 * @author Edward P. Legaspi
 * @since Oct 3, 2013
 **/
public enum ActionStatusEnum {
	SUCCESS(1), FAIL(0);

	private int status;

	private ActionStatusEnum(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
