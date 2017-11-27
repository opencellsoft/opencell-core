package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class AccessDeniedException extends MeveoApiException {

    private static final long serialVersionUID = 8602421582759722126L;

    public AccessDeniedException() {
        super();
        setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
    }

    public AccessDeniedException(String errorMessage) {
        super(errorMessage);
        setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
    }

    public AccessDeniedException(Class<?> clazz, String code) {
        super("Insufficient permissions to access " + clazz.getSimpleName() + " with code=" + code);
        setErrorCode(MeveoApiErrorCodeEnum.AUTHENTICATION_AUTHORIZATION_EXCEPTION);
    }
}
