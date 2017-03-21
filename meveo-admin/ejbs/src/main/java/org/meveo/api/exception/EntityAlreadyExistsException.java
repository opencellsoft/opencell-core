package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class EntityAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -979336515558555662L;

	public EntityAlreadyExistsException(String entityName, String code) {
		super(entityName + " with code=" + code + " already exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_ALREADY_EXISTS_EXCEPTION);
	}

	public EntityAlreadyExistsException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_ALREADY_EXISTS_EXCEPTION);
	}

	public EntityAlreadyExistsException(Class<?> clazz, String code) {
		super(clazz.getSimpleName() + " with code=" + code + " already exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_ALREADY_EXISTS_EXCEPTION);
	}

	public EntityAlreadyExistsException(Class<?> clazz, String value,
			String field) {
		super(clazz.getSimpleName() + " with " + field + "=" + value
				+ " already exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_ALREADY_EXISTS_EXCEPTION);
	}

}
