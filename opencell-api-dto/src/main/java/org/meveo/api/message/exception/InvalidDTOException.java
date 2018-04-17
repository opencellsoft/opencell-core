package org.meveo.api.message.exception;

/**
 * The Class InvalidDTOException.
 *
 * @author Edward P. Legaspi
 * @since Oct 29, 2013
 */
public class InvalidDTOException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3620904898122661664L;

    /**
     * Instantiates a new invalid DTO exception.
     */
    public InvalidDTOException() {
    }

    /**
     * Instantiates a new invalid DTO exception.
     *
     * @param message the message
     */
    public InvalidDTOException(String message) {
        super(message);
    }

}
