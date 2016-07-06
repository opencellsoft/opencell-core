package org.meveo.api.security.parameter;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.slf4j.Logger;

@Singleton
public class SecureMethodParameterHandler {

	@Any
	@Inject
	private Instance<SecureMethodParameterParser<?>> parsers;

	@Inject
	protected Logger log;

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends SecureMethodParameterParser>, SecureMethodParameterParser<?>> parserMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getParameterValue(SecureMethodParameter parameter, Object[] values, Class<T> resultClass, User user) throws MeveoApiException {
		SecureMethodParameterParser<?> parser = getParser(parameter);
		Object parameterValue = parser.getParameterValue(parameter, values, user);
		return (T) parameterValue;
	}

	private SecureMethodParameterParser<?> getParser(SecureMethodParameter parameter) {
		initialize();
		SecureMethodParameterParser<?> parser = parserMap.get(parameter.parser());
		if (parser == null) {
			log.warn("No SecureMethodParameterParser instance of type {} found.", parameter.parser().getTypeName());
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
