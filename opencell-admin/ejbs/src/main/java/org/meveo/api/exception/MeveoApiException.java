package org.meveo.api.exception;

import javax.ejb.ApplicationException;

import org.meveo.api.MeveoApiErrorCodeEnum;

@ApplicationException(rollback = true)
public class MeveoApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private MeveoApiErrorCodeEnum errorCode;

	public MeveoApiException() {
		errorCode = MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
	}

	public MeveoApiException(Throwable e) {
		super(e);
		errorCode = MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
	}

	public MeveoApiException(MeveoApiErrorCodeEnum errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public MeveoApiException(String message) {
		super(message);
	}

	public MeveoApiErrorCodeEnum getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(MeveoApiErrorCodeEnum errorCode) {
		this.errorCode = errorCode;
	}
}