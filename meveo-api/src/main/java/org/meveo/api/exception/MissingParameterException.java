package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCode;

public class MissingParameterException extends MeveoApiException {

	private static final long serialVersionUID = -7101565234776606126L;

	public MissingParameterException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
	}

}
