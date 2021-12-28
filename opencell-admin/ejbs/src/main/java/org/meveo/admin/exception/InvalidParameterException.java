package org.meveo.admin.exception;

/**
 * Parameter not supplied or contains an invalid value
 * 
 * @author Andrius Karpavicius
 */
public class InvalidParameterException extends ValidationException {

    private static final long serialVersionUID = 1L;

    public InvalidParameterException(String message) {
        super(message);
    }
}