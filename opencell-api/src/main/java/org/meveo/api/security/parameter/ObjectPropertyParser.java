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
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;

/**
 * This parser retrieves the entity class that will be checked for authorization by looking up a property value from the given parameter of a {@link SecuredBusinessEntityMethod}
 * annotated method.
 * 
 * @author Tony Alejandro
 *
 */
public class ObjectPropertyParser extends SecureMethodParameterParser<BusinessEntity> {

    @Override
    public BusinessEntity getParameterValue(SecureMethodParameterConfig parameterConfig, Object[] values) throws InvalidParameterException, MissingParameterException {
        if (parameterConfig == null) {
            return null;
        }
        // get the code
        try {
            String code = extractPropertyValue(parameterConfig, values);
            // retrieve the entity
            BusinessEntity entity = extractBusinessEntity(parameterConfig, code);
            return entity;
        } catch (MissingParameterException e) {
            // TODO how to handle when entity to filter can not be resolved because it is null - is it an error?
            return null;
            // throw e;
        }
    }

    /**
     * The value is determined by getting the parameterConfig object and returning the value of the property.
     * 
     * @param parameterConfig {@link SecureMethodParameter} instance that has the entity, index, and property attributes set.
     * @param values The method parameters.
     * @return The value retrieved from the object.
     * @throws InvalidParameterException Parameter value was not resolved because of wrong path, or other parsing errors
     * @throws MissingParameterException Parameter value was null
     */
    private String extractPropertyValue(SecureMethodParameterConfig parameterConfig, Object[] values) throws InvalidParameterException, MissingParameterException {

        // retrieve the dto and property based on the parameterConfig annotation
        Object dto = values[parameterConfig.getIndex()];
        String property = parameterConfig.getProperty();
        String propertyValue = null;
        try {
            propertyValue = (String) ReflectionUtils.getPropertyValue(dto, property);
        } catch (IllegalAccessException e) {
            String message = String.format("Failed to retrieve property %s.%s.", dto.getClass().getName(), property);
            log.error(message, e);
            throw new InvalidParameterException(message);
        }

        if (StringUtils.isBlank(propertyValue)) {
            throw new MissingParameterException(String.format("%s.%s returned an empty value.", dto.getClass().getName(), property));
        }
        return propertyValue;
    }

    private BusinessEntity extractBusinessEntity(SecureMethodParameterConfig parameterConfig, String code) throws InvalidParameterException {
        Class<? extends BusinessEntity> entityClass = parameterConfig.getEntityClass();
        BusinessEntity entity = null;
        try {
            entity = entityClass.newInstance();
            entity.setCode(code);
        } catch (InstantiationException | IllegalAccessException e) {
            String message = String.format("Failed to create new %s instance.", entityClass.getName());
            log.error(message, e);
            throw new InvalidParameterException(message);
        }
        return entity;
    }

}
