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

import javax.inject.Inject;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.slf4j.Logger;

/**
 * This is the base class of parser implementations that can be used with methods annotated with {@link SecureMethodParameter}. A parser is used to to retrieve the value from a
 * method parameter.
 * 
 * @author tonys
 *
 * @param <T> the type of the entity
 */
public abstract class SecureMethodParameterParser<T> {

    @Inject
    protected Logger log;

    /**
     * This method implements the algorithm for parsing method parameters from {@link SecuredBusinessEntityMethod} annotated methods.
     * 
     * @param parameter The {@link SecureMethodParameter} instance that describe the parameter that will be evaluated.
     * @param values The method parameters received by the method that was annotated with {@link SecuredBusinessEntityMethod}
     * @return The resulting object that was retrieved by the parser.
     *
     * @throws InvalidParameterException Parameter value was not resolved because of wrong path, or other parsing errors
     * @throws MissingParameterException Parameter value was null
     */
    public abstract T getParameterValue(SecureMethodParameterConfig parameter, Object[] values) throws InvalidParameterException, MissingParameterException;

}
