package org.meveo.api.exception;

/**
 * @author Edward P. Legaspi
 **/
public class SubscriptionDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = -7153341923036857081L;

	public SubscriptionDoesNotExistsException() {

	}

	public SubscriptionDoesNotExistsException(String subscriptionCode) {
		super("Subscription with code=" + subscriptionCode
				+ " does not exists.");
	}

}
