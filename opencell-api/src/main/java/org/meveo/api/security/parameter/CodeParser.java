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
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.config.SecureMethodParameterConfig;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;

import java.util.Collections;
import java.util.List;

/**
 * This is the default parser for {@link SecuredBusinessEntityMethod} annotated methods. It simply retrieves the parameter value and assigns it to the instance of the entity
 * described in the entity attribute of the corresponding {@link SecureMethodParameter} annotation.
 * 
 * @author Tony Alejandro
 * @author Mounir Boukayoua
 */
public class CodeParser extends SecureMethodParameterParser<BusinessEntity> {

    @Override
    public List<BusinessEntity> getParameterValue(SecureMethodParameterConfig parameterConfig, Object[] values)
            throws InvalidParameterException, MissingParameterException {
        if (parameterConfig == null) {
            return null;
        }

        // retrieve the code from the parameterConfig
        String code = (String) values[parameterConfig.getIndex()];
        if (StringUtils.isBlank(code)) {
            // TODO how to handle when entity to filter can not be resolved because it is null - is it an error?
            return null;
            // throw new MissingParameterException("code parameterConfig is an empty value");
        }

        // instantiate a new entity.
        Class<? extends BusinessEntity> entityClass = parameterConfig.getEntityClass();

        BusinessEntity entity = null;
        try {
            entity = entityClass.newInstance();
            entity.setCode(code);
        } catch (InstantiationException | IllegalAccessException e) {
            String message = String.format("Failed to create new %s instance.", entityClass.getSimpleName());
            log.error(message, e);
            throw new InvalidParameterException(message);
        }

        return Collections.singletonList(entity);
    }

}
