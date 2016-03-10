package org.meveo.api.exception;

import java.util.List;

import org.meveo.api.MeveoApiErrorCodeEnum;

public class MissingParameterException extends MeveoApiException {

    private static final long serialVersionUID = -7101565234776606126L;

    public static String MESSAGE_TEXT = "The following parameters are required or contain invalid values: ";

    public MissingParameterException(String fieldName) {
        super(MESSAGE_TEXT + fieldName);
        setErrorCode(MeveoApiErrorCodeEnum.MISSING_PARAMETER);
    }

    public MissingParameterException(List<String> missingFields) {
        super(composeMessage(missingFields));
        setErrorCode(MeveoApiErrorCodeEnum.MISSING_PARAMETER);
    }

    private static String composeMessage(List<String> missingFields) {
        StringBuilder sb = new StringBuilder(MESSAGE_TEXT);

        if (!missingFields.isEmpty()) {
            if (missingFields.size() > 1) {
                sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
            } else {
                sb.append(missingFields.get(0));
            }
            sb.append(".");
        }

        return sb.toString();
    }
}
