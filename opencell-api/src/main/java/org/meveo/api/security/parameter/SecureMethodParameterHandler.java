/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.security.parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.config.SecureMethodParameterConfig;
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
	 * @param <T> The result class.
	 * @param parameterConfig the {@link SecureMethodParameter} describing which parameter is going to be evaluated and what parser to use to extract the data.
	 * @param values The array of parameters that was passed into the method.
	 * @param resultClass The class of the value that will be extracted from the parameter.
	 * @return The parameter value
	 * @throws MeveoApiException Meveo api exception
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getParameterValue(SecureMethodParameterConfig parameterConfig, Object[] values , Class<T> resultClass) throws MeveoApiException {
		SecureMethodParameterParser<?> parser = getParser(parameterConfig);
		if (parser == null) {
		    return null;
		}
		Object parameterValue = parser.getParameterValue(parameterConfig, values);
		return (List<T>) parameterValue;
	}

	private SecureMethodParameterParser<?> getParser(SecureMethodParameterConfig parameterConfig) {
		initialize();
		SecureMethodParameterParser<?> parser = parserMap.get(parameterConfig.getParser());
		if (parser == null) {
			log.warn("No SecureMethodParameterParser instance of type {} found.", parameterConfig.getParser().getName());
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
