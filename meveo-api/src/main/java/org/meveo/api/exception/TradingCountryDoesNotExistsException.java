package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class TradingCountryDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 1070665354662332960L;

	public TradingCountryDoesNotExistsException(String code) {
		super("Trading country with code=" + code + " does not exists.");
	}

}
