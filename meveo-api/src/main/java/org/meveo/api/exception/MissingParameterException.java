package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class MissingParameterException extends MeveoApiException {

	private static final long serialVersionUID = -7101565234776606126L;

	public MissingParameterException(String message) {
		super(message);
	}

}
