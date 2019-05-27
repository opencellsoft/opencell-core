package org.meveo.apiv2;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;

public class ValidationUtils {


    public static void requireNonNull(Object param, MeveoApiErrorCodeEnum meveoApiErrorCodeEnum, String errorMessage) {
        if (param == null) {
            throw new MeveoApiException(meveoApiErrorCodeEnum, errorMessage);
        }
    }

    public static void requireNonEmpty(String param, MeveoApiErrorCodeEnum meveoApiErrorCodeEnum, String errorMessage) {
        if (StringUtils.isBlank(param)) {
            throw new MeveoApiException(meveoApiErrorCodeEnum, errorMessage);
        }
    }
}
