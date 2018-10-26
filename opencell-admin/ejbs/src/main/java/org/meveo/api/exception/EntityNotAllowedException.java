package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 **/
public class EntityNotAllowedException extends MeveoApiException {

	private static final long serialVersionUID = 1L;

	public EntityNotAllowedException(Class<?> clazz, Class<?> clazz2, String code) {
		super(clazz.getSimpleName() + " with code=" + code
				+ " not allowed on "+clazz2.getSimpleName());
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_NOT_ALLOWED_EXCEPTION);
	}

}
