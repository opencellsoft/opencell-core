package org.meveo.api.security.parameter;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
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
    public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values) throws InvalidParameterException, MissingParameterException {
        if (parameter == null) {
            return null;
        }
        // get the code
        try {
            String code = extractPropertyValue(parameter, values);
            // retrieve the entity
            BusinessEntity entity = extractBusinessEntity(parameter, code);
            return entity;
        } catch (MissingParameterException e) {
            // TODO how to handle when entity to filter can not be resolved because it is null - is it an error?
            return null;
            // throw e;
        }
    }

    /**
     * The value is determined by getting the parameter object and returning the value of the property.
     * 
     * @param parameter {@link SecureMethodParameter} instance that has the entity, index, and property attributes set.
     * @param values The method parameters.
     * @return The value retrieved from the object.
     * @throws InvalidParameterException Parameter value was not resolved because of wrong path, or other parsing errors
     * @throws MissingParameterException Parameter value was null
     */
    private String extractPropertyValue(SecureMethodParameter parameter, Object[] values) throws InvalidParameterException, MissingParameterException {

        // retrieve the dto and property based on the parameter annotation
        Object dto = values[parameter.index()];
        String property = parameter.property();
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

    private BusinessEntity extractBusinessEntity(SecureMethodParameter parameter, String code) throws InvalidParameterException {
        Class<? extends BusinessEntity> entityClass = parameter.entityClass();
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
