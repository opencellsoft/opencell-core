package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 11, 2013
 **/
public class OrganizationAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -3091956687573710598L;

	public OrganizationAlreadyExistsException(String code) {
		super("Organization with id=" + code + " already exists.");
	}

}
