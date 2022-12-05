package org.meveo.apiv2.generic.exception;

import jakarta.ws.rs.ClientErrorException;

public class UnprocessableEntityException extends ClientErrorException {

    private static final long serialVersionUID = 7999343638105044751L;

    /**
     * Construct a new "Unprocessable" exception.
     */
    public UnprocessableEntityException() {
        super(422);
    }

    public UnprocessableEntityException(String message) {
        super(message, 422);
    }
}
