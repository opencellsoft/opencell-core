package org.meveo.api.exception;


/**
 * @author Edward P. Legaspi
 * @since Nov 25, 2013
 **/
public class CreditLimitExceededException extends MeveoApiException {

	private static final long serialVersionUID = -7530794892986996486L;

	public CreditLimitExceededException() {

	}

	public CreditLimitExceededException(String organizationId) {
		super("Credit limit exceeded for organization with code="
				+ organizationId);
	}

}
