package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class TradingCurrencyAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = 1070665354662332960L;

	public TradingCurrencyAlreadyExistsException(String code) {
		super("Trading currency with code=" + code + " does not exists.");
	}

}
