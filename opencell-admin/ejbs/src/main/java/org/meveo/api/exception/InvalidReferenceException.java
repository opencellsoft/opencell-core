package org.meveo.api.exception;

public class InvalidReferenceException extends InvalidParameterException {

	private static final long serialVersionUID = -3436733471648721659L;

	public InvalidReferenceException() {
	}

	public InvalidReferenceException(String entity, String value) {
		super("Entity of type " + entity + " with code " + value + " not found");

	}

	public InvalidReferenceException(String entity, String[] valueItems) {
		super("No entity of type " + entity + " with code in list [" + String.join(", ", valueItems) + "] was found");
	}
}
