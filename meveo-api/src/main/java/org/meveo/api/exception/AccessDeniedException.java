package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class AccessDeniedException extends MeveoApiException {

	private static final long serialVersionUID = 8602421582759722126L;

	public AccessDeniedException() {
		super();
		setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
	}

	public AccessDeniedException(String errorMessage) {
		super(errorMessage);
		setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
	}
}
