package org.meveo.api.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class MeveoApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private int errorCode;

	public MeveoApiException() {

	}

	public MeveoApiException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public MeveoApiException(String message) {
		super(message);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
