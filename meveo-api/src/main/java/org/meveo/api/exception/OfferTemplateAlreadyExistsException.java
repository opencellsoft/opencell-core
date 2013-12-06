package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 18, 2013
 **/
public class OfferTemplateAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -466769769966819416L;

	public OfferTemplateAlreadyExistsException(String code) {
		super("OfferTemplate with code=" + code + " already exists.");
	}

}
