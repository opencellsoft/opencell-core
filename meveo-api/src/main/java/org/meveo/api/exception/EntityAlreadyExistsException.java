package org.meveo.api.exception;

import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
public class EntityAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -979336515558555662L;

	public EntityAlreadyExistsException(String entityName, String code) {
		super(entityName + " with code=" + code + " already exists.");
	}

	public EntityAlreadyExistsException(Class<User> clazz, String value,
			String field) {
		super(clazz.getSimpleName() + " with " + field + "=" + value
				+ " already exists.");
	}

}
