package org.meveo.admin.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CounterInstantiationException extends BusinessException {

    private static final long serialVersionUID = -350790415602443269L;

    /**
     * Constructs a new exception with the specified cause
     * 
     * @param cause the cause
     */
    public CounterInstantiationException(Throwable cause) {
        super(cause);
    }
}