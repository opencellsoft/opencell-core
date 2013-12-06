package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class TradingLanguageAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -1237890840412496368L;

	public TradingLanguageAlreadyExistsException(String code) {
		super("Trading language with code=" + code + " does not exists.");
	}

}
