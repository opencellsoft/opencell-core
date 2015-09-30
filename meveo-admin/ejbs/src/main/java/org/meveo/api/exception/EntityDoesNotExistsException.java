package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCode;

/**
 * @author Edward P. Legaspi
 **/
public class EntityDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 4814463369593237028L;

	public EntityDoesNotExistsException(String entityName, String code) {
		super(entityName + " with code=" + code + " does not exists.");
		setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String code) {
		super(clazz.getSimpleName() + " with code=" + code
				+ " does not exists.");
		setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, Long id) {
		super(clazz.getSimpleName() + " with id=" + id + " does not exists.");
		setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String value,
			String field) {
		super(clazz.getSimpleName() + " with " + field + "=" + value
				+ " does not exists.");
		setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

}
