package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 22, 2013
 **/
public class CountryAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -6308327785234265841L;

	public CountryAlreadyExistsException(String code) {
		super("Country with code=" + code + " already exists.");
	}

}
