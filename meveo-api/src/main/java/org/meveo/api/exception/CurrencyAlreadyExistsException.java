package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 22, 2013
 **/
public class CurrencyAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = 801577032960916436L;

	public CurrencyAlreadyExistsException(String code) {
		super("Currency with code=" + code + " already exists.");
	}

}
