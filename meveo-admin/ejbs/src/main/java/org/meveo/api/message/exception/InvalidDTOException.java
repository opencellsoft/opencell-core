package org.meveo.api.message.exception;

/**
 * @author Edward P. Legaspi
 * @since Oct 29, 2013
 **/
public class InvalidDTOException extends RuntimeException {

	private static final long serialVersionUID = -3620904898122661664L;

	public InvalidDTOException() {
	}

	public InvalidDTOException(String message) {
		super(message);
	}

}
