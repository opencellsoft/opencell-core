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

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.meveo.model.admin.User;

import java.util.Collections;
import java.util.List;

/**
 * This parser is used to retrieve the {@link User} object from the method parameters.
 * 
 * @author Tony Alejandro
 *
 */
public class UserParser extends SecureMethodParameterParser<User> {

    @Override
    public List<User> getParameterValue(SecureMethodParameterConfig parameterConfig, Object[] values) throws InvalidParameterException, MissingParameterException {
        if (parameterConfig == null) {
            return null;
        }

        Object parameterValue = values[parameterConfig.getIndex()];
        if (!(parameterValue instanceof User)) {
            throw new InvalidParameterException("Parameter received at index: " + parameterConfig.getIndex() + " is not a User instance.");
        }

        return Collections.singletonList((User) parameterValue);
    }
}
