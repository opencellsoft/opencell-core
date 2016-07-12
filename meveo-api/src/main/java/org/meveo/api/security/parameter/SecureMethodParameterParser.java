package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

/**
 * This is the base class of parser implementations that can be used with
 * methods annotated with {@link SecureMethodParameter}. A parser is used to to
 * retrieve the value from a method parameter.
 * 
 * @author tonys
 *
 * @param <T>
 */
public abstract class SecureMethodParameterParser<T> {

	@Inject
	protected Logger log;

	protected static final String INVALID_PARAMETER_TYPE = "Parameter should be of %s type.";
	protected static final String CODE_REQUIRED = "The entity code is required.";
	protected static final String FAILED_TO_INSTANTIATE_ENTITY = "Failed to create new %s instance.";

	/**
	 * This method implements the algorithm for parsing method parameters from
	 * {@link SecuredBusinessEntityMethod} annotated methods.
	 * 
	 * @param parameter
	 *            The {@link SecureMethodParameter} instance that describe the
	 *            parameter that will be evaluated.
	 * @param values
	 *            The method parameters received by the method that was
	 *            annotated with {@link SecuredBusinessEntityMethod}
	 * @param user
	 *            The current user
	 * @return The resulting object that was retrieved by the parser.
	 * @throws MeveoApiException
	 */
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
