package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public class MeveoApiException extends Exception {

	private static final long serialVersionUID = 1L;

	public MeveoApiException() {

	}

	public MeveoApiException(String message) {
		super(message);
	}

}
