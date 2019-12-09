package org.meveo.api.exception;

import javax.ejb.ApplicationException;

import org.meveo.api.ApiErrorCodeEnum;
import org.meveo.api.MeveoApiErrorCodeEnum;

@ApplicationException(rollback = true)
public class MeveoApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ApiErrorCodeEnum errorCode;

	public MeveoApiException() {
		errorCode = MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
	}

	public MeveoApiException(Throwable e) {
		super(e);
		errorCode = MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION;
	}

	public MeveoApiException(ApiErrorCodeEnum errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public MeveoApiException(String message) {
		super(message);
	}

	public ApiErrorCodeEnum getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ApiErrorCodeEnum errorCode) {
		this.errorCode = errorCode;
	}
}