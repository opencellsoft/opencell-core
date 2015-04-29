package org.meveo.api.exception;

import javax.ejb.ApplicationException;

import org.meveo.api.MeveoApiErrorCode;

@ApplicationException(rollback = true)
public class MeveoApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorCode;

	public MeveoApiException() {
		errorCode = MeveoApiErrorCode.GENERIC_API_EXCEPTION;
	}

	public MeveoApiException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public MeveoApiException(String message) {
		super(message);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
