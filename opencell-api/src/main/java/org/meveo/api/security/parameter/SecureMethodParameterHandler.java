package org.meveo.api.security.parameter;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.slf4j.Logger;

/**
 * This is a singleton object that takes an annotation and the method parameters
 * of a {@link SecuredBusinessEntityMethod} annotated method and retrieves the
 * value using the given parser defined in the {@link SecureMethodParameter}
 * annotation.
 * 
 * @author Tony Alejandro
 *
 */
@Singleton
public class SecureMethodParameterHandler {

	@Any
	@Inject
	private Instance<SecureMethodParameterParser<?>> parsers;

	@Inject
	protected Logger log;

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends SecureMethodParameterParser>, SecureMethodParameterParser<?>> parserMap = new HashMap<>();

	/**
	 * Retrieves the parser defined in the {@link SecureMethodParameter}
	 * parameter, uses the parser to extract the value from the values array,
	 * then returns it.
	 * 
	 * @param parameter the {@link SecureMethodParameter} describing which parameter is going to be evaluated and what parser to use to extract the data.
	 * @param values The array of parameters that was passed into the method.
	 * @param resultClass The class of the value that will be extracted from the parameter.
	 * @return The parameter value
	 * @throws MeveoApiException Meveo api exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParameterValue(SecureMethodParameter parameter, Object[] values, Class<T> resultClass) throws MeveoApiException {
		SecureMethodParameterParser<?> parser = getParser(parameter);
		Object parameterValue = parser.getParameterValue(parameter, values);
		return (T) parameterValue;
	}

	private SecureMethodParameterParser<?> getParser(SecureMethodParameter parameter) {
		initialize();
		SecureMethodParameterParser<?> parser = parserMap.get(parameter.parser());
		if (parser == null) {
			log.warn("No SecureMethodParameterParser instance of type {} found.", parameter.parser().getName());
		}
		return parser;
	}

	@SuppressWarnings("rawtypes")
	private void initialize() {
		if (parserMap.isEmpty()) {
			log.debug("Initializing SecureMethodParameterParser map.");
			for (SecureMethodParameterParser parser : parsers) {
				parserMap.put(parser.getClass(), parser);
			}
			log.debug("Parser map initialization done.  Found {} SecureMethodParameterParsers.", parserMap.size());
		}
	}

}
