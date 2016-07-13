package org.meveo.api.security.parameter;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;

/**
 * This parser retrieves the entity class that will be checked for authorization
 * by looking up a property value from the given parameter of a
 * {@link SecuredBusinessEntityMethod} annotated method.
 * 
 * @author Tony Alejandro
 *
 */
public class ObjectPropertyParser extends SecureMethodParameterParser<BusinessEntity> {

	private static final String FAILED_TO_RETRIEVE_PROPERTY = "Failed to retrieve property %s.%s.";

	@Override
	public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values, User user) throws MeveoApiException {
		if (parameter == null) {
			return null;
		}
		// get the code
		String code = extractCode(parameter, values);
		// retrieve the entity
		BusinessEntity entity = extractBusinessEntity(parameter, code);
		return entity;
	}

	/**
	 * The code is determined by getting the parameter object and returning the value of the property.
	 * 
	 * @param parameter {@link SecureMethodParameter} instance that has the entity, index, and property attributes set.
	 * @param values The method parameters.
	 * @return The code retrieved from the object.
	 * @throws MeveoApiException
	 */
	private String extractCode(SecureMethodParameter parameter, Object[] values) throws MeveoApiException {
		
		// retrieve the dto and property based on the parameter annotation
		Object dto = values[parameter.index()];
		String property = parameter.property();
		String code = null;
		try {
			code = (String) getPropertyValue(dto, property);
		} catch (IllegalAccessException e) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_RETRIEVE_PROPERTY, dto.getClass().getName(), property), e);
		}
		if (StringUtils.isBlank(code)) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, CODE_REQUIRED);
		}
		return code;
	}

	/**
	 * This is a recursive function that aims to walk through the properties of an object until it gets the final value.
	 * 
	 * e.g.
	 * If we received an Object named obj and given a string property of code.name, then the value of obj1.code.name will be returned.
	 * 
	 * @param obj The object that contains the property value.
	 * @param property The property of the object that contains the data.
	 * @return The value of the data contained in obj.property
	 * @throws IllegalAccessException
	 */
	private Object getPropertyValue(Object obj, String property) throws IllegalAccessException {
		int fieldIndex = property.indexOf(".");
		if (property.indexOf(".") != -1) {
			String fieldName = property.substring(0, fieldIndex);
			Object fieldValue = FieldUtils.readField(obj, fieldName, true);
			return getPropertyValue(fieldValue, property.substring(fieldIndex + 1));
		} else {
			return FieldUtils.readField(obj, property, true);
		}
	}

	private BusinessEntity extractBusinessEntity(SecureMethodParameter parameter, String code) throws MeveoApiException {
		Class<? extends BusinessEntity> entityClass = parameter.entity();
		BusinessEntity entity = null;
		try {
			entity = entityClass.newInstance();
			entity.setCode(code);
		} catch (InstantiationException | IllegalAccessException e) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_ENTITY, entityClass.getName()), e);
		}
		return entity;
	}

}
