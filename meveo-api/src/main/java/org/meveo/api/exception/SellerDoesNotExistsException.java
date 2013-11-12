package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 12, 2013
 **/
public class SellerDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = -6159383154906455450L;

	public SellerDoesNotExistsException(String code) {
		super("Seller with id=" + code + " does not exists.");
	}

}
