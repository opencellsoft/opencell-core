package org.meveo.api.security.parameter;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;

public class ObjectPropertyParser extends SecureMethodParameterParser<BusinessEntity> {

	private static final String FAILED_TO_RETRIEVE_PROPERTY = "Failed to retrieve property %s.%s.";

	@Override
	public BusinessEntity getParameterValue(SecureMethodParameter parameter, Object[] values, User user) throws MeveoApiException {
		if (parameter == null) {
			return null;
		}
		String code = extractCode(parameter, values);
		BusinessEntity entity = extractBusinessEntity(parameter, code);
		return entity;
	}

	private String extractCode(SecureMethodParameter parameter, Object[] values) throws MeveoApiException {
		Object dto = values[parameter.index()];
		String property = parameter.property();
		String code = null;
		try {
			code = (String) getPropertyValue(dto, property);
		} catch (IllegalAccessException e) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_RETRIEVE_PROPERTY, dto.getClass().getTypeName(), property), e);
		}
		if (StringUtils.isBlank(code)) {
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, CODE_REQUIRED);
		}
		return code;
	}

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
			throwErrorMessage(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION, String.format(FAILED_TO_INSTANTIATE_ENTITY, entityClass.getSimpleName()), e);
		}
		return entity;
	}

}
