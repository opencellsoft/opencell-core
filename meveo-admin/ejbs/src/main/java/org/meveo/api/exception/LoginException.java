package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class LoginException extends MeveoApiException {

	private static final long serialVersionUID = -1218204618504364423L;

	public LoginException() {
	}
	
	public LoginException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
	}

	public LoginException(String user, String message) {
		super(message);
		setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
	}

}
