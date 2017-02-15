package org.meveo.api.security.parameter;

import javax.inject.Inject;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
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
	public abstract T getParameterValue(SecureMethodParameter parameter, Object[] values) throws MeveoApiException;

}
