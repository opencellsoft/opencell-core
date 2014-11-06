package org.meveo.api.exception;

import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
public class EntityDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 4814463369593237028L;

	public EntityDoesNotExistsException(String entityName, String code) {
		super(entityName + " with code=" + code + " does not exists.");
	}

	public EntityDoesNotExistsException(Class<?> clazz, String code) {
		super(clazz.getSimpleName() + " with code=" + code
				+ " does not exists.");
	}

	public EntityDoesNotExistsException(Class<User> clazz, String value,
			String field) {
		super(clazz.getSimpleName() + " with " + field + "=" + value
				+ " does not exists.");
	}

}
