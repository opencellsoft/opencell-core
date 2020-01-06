package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class ConstraintViolationApiException extends MeveoApiException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ConstraintViolationApiException(String dbMessage) {
		super(dbMessage);
		setErrorCode(MeveoApiErrorCodeEnum.CONSTRAINT_VIOLATION_EXCEPTION);
	}
	
}