package org.meveo.admin.exception;

public class UncheckedThreadingException extends RuntimeException{
    public UncheckedThreadingException(Throwable e) {
        super(e);
    }
}
