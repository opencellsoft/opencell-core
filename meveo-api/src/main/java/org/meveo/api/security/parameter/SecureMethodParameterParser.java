package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

public abstract class SecureMethodParameterParser<T> {
	
	@Inject
	protected Logger log;
	
	protected static final String INVALID_PARAMETER_TYPE = "Parameter should be of %s type.";
	protected static final String CODE_REQUIRED = "The entity code is required.";
	protected static final String FAILED_TO_INSTANTIATE_ENTITY = "Failed to create new %s instance.";
	
	public abstract T getParameterValue(SecureMethodParameter parameter, Object[] values, User user) throws MeveoApiException;
	
	protected void throwErrorMessage(MeveoApiErrorCodeEnum errorCode, String message) throws MeveoApiException {
		throwErrorMessage(errorCode, message, null);
	}

	protected void throwErrorMessage(MeveoApiErrorCodeEnum errorCode, String message, Throwable e) throws MeveoApiException {
		if (e == null) {
			log.error(message);
		} else {
			log.error(message, e);
		}
		throw new MeveoApiException(errorCode, message);
	}
	
}
