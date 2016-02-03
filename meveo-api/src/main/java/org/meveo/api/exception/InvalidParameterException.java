package org.meveo.api.exception;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class InvalidParameterException extends MeveoApiException {

    private static final long serialVersionUID = -3436733471648721659L;

    public InvalidParameterException() {
    }

    public InvalidParameterException(String field, String value) {
        super("Invalid value '" + value + "' for field " + field);

        setErrorCode(MeveoApiErrorCodeEnum.INVALID_PARAMETER);
    }
}
