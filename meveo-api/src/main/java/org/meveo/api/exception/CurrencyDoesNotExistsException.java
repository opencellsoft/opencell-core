package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 22, 2013
 **/
public class CurrencyDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = -1816138230886745876L;

	public CurrencyDoesNotExistsException(String code) {
		super("Currency with code=" + code + " does not exists.");
	}

}
