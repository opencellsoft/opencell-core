package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCode;

/**
 * @author Edward P. Legaspi
 **/
public class InvalidEnumValue extends MeveoApiException {

	private static final long serialVersionUID = 6948986026477833086L;

	public InvalidEnumValue() {

	}

	public InvalidEnumValue(String enumType, String value) {
		super("Enum of type=" + enumType + " doesn't have a value=" + value);
		setErrorCode(MeveoApiErrorCode.INVALID_ENUM_VALUE);
	}

}
