package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class SellerAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -3091956687573710598L;

	public SellerAlreadyExistsException(String code) {
		super("Seller with id=" + code + " already exists.");
	}

}
