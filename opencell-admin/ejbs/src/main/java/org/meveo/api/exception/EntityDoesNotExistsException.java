package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class EntityDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 4814463369593237028L;

	public EntityDoesNotExistsException(String entityName, String code) {
		super(entityName + " with code=" + code + " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String code) {
		super(clazz.getSimpleName() + " with code=" + code
				+ " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, Long id) {
		super(clazz.getSimpleName() + " with id=" + id + " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String value,
			String field) {
		super(clazz.getSimpleName() + " with " + field + "=" + value
				+ " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}
	
	public EntityDoesNotExistsException(Class<?> clazz, String value1,
			String field1,String value2,String field2) {
		super(clazz.getSimpleName() + " with " + field1 + "=" + value1 +" and/or " + field2 + "=" + value2+ " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

}
