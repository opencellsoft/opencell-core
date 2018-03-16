package org.meveo.admin.exception;

/**
 * @author Edward P. Legaspi
 **/
public class FilterException extends RuntimeException {

    private static final long serialVersionUID = -2672823995730125755L;

    public FilterException() {

    }

    public FilterException(String message) {
        super(message);
    }

    public FilterException(Exception e) {
        super(e);
    }
}