package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCode;

/**
 * @author Edward P. Legaspi
 **/
public class BusinessApiException extends MeveoApiException {

	private static final long serialVersionUID = -5546608621039046117L;

	public BusinessApiException() {
		super("Business exception");
		setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
	}
	
	public BusinessApiException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCode.BUSINESS_API_EXCEPTION);
	}

}
