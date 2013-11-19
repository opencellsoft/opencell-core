package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 18, 2013
 **/
public class OfferTemplateDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = -6698904723213260820L;

	public OfferTemplateDoesNotExistsException(String code) {
		super("OfferTemplate with code=" + code + " does not exists.");
	}

}
