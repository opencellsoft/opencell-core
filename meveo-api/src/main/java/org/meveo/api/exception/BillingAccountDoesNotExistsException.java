package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 26, 2013
 **/
public class BillingAccountDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 8413347727346444658L;

	public BillingAccountDoesNotExistsException(String code) {
		super("Billing account with code=" + code + " does not exists.");
	}

}
