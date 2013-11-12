package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class ParentSellerDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 4230713424171403800L;

	public ParentSellerDoesNotExistsException(String code) {
		super("Parent seller with code=" + code + " does not exists.");
	}

}
