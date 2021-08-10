package org.meveo.apiv2.generic.exception;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class ConflictException extends ClientErrorException {

    private static final long serialVersionUID = 7999343638105044751L;

    /**
     * Construct a new "Conflict" exception.
     */
    public ConflictException() {
        super(Response.Status.CONFLICT);
    }

    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
