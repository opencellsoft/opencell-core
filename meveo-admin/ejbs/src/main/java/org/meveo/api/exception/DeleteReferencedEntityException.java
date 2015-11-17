package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCode;

/**
 * @author Edward P. Legaspi
 **/
public class DeleteReferencedEntityException extends MeveoApiException {

	private static final long serialVersionUID = -979336515558551662L;

	public DeleteReferencedEntityException(String entityName, String code) {
		super(entityName + " with code=" + code + " is refenced");
		setErrorCode(MeveoApiErrorCode.DELETE_REFERENCED_ENTITY_EXCEPTION);
	}

	public DeleteReferencedEntityException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCode.DELETE_REFERENCED_ENTITY_EXCEPTION);
	}

	public DeleteReferencedEntityException(Class<?> clazz, String code) {
		super(clazz.getSimpleName() + " with code=" + code + " is refenced.");
		setErrorCode(MeveoApiErrorCode.DELETE_REFERENCED_ENTITY_EXCEPTION);
	}


}
