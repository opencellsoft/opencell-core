package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class InvalidEnumValueException extends MeveoApiException {

	private static final long serialVersionUID = 6948986026477833086L;

	public InvalidEnumValueException() {
	}

	public InvalidEnumValueException(String enumType, String value) {
		super("Enum of type=" + enumType + " doesn't have a value=" + value);
		setErrorCode(MeveoApiErrorCodeEnum.INVALID_ENUM_VALUE);
	}
}
