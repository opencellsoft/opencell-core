package org.meveo.api.exception;

import java.util.Map;

import org.meveo.admin.exception.ValidationException;

/**
 * Failed to evaluate EL expression or EL expression is incorrect
 * 
 * @author Andrius Karpavicius
 *
 */
public class InvalidELException extends ValidationException {

    private static final long serialVersionUID = 337716117840025810L;

    /**
     * Constructs a new exception with the specified detail message
     * 
     * @param elExpression The EL expression to evaluate
     * @param contextMap EL parameters
     * @param exception Exception occurred
     */
    public InvalidELException(String elExpression, Map<Object, Object> contextMap, Throwable exception) {
        super("Error while evaluating expression " + elExpression + " with parameters " + contextMap + ": " + exception.getMessage(), exception);
    }

    /**
     * Constructs a new exception with the specified detail message
     * 
     * @param message Error message
     */
    public InvalidELException(String message) {
        super(message);
    }
}