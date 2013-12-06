package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class TradingLanguageDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = -1237890840412496368L;

	public TradingLanguageDoesNotExistsException(String code) {
		super("Trading language with code=" + code + " already exists.");
	}

}
