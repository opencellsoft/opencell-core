package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 25, 2013
 **/
public class ServiceTemplateDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = -4982671084041103527L;

	public ServiceTemplateDoesNotExistsException(String code) {
		super("ServiceTemplate with code=" + code + " does not exists.");
	}
	
}
