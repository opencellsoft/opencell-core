package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 * @since Nov 25, 2013
 **/
public class ServiceTemplateAlreadyExistsException extends MeveoApiException {

	private static final long serialVersionUID = -2563131351662794981L;

	public ServiceTemplateAlreadyExistsException(String code) {
		super("ServiceTemplate with code=" + code + " already exists.");
	}

}
