package org.meveo.apiv2;

import org.junit.Test;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.api.MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION;
import static org.meveo.api.MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION;

public class ValidationUtilsTest {

    @Test
    public void given_null_object_when_require_non_null_then_throw_meveo_api_exception_and_expect_default_message() {
        try {
            ValidationUtils.requireNonNull(null, BUSINESS_API_EXCEPTION, "The required parameter must not be null");
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The required parameter must not be null");
            assertThat(((MeveoApiException) ex).getErrorCode()).isEqualTo(BUSINESS_API_EXCEPTION);

        }
    }

    @Test
    public void given_emty_string_when_require_not_empty_then_should_throw_meveo_api_exception() {
        try {
            ValidationUtils.requireNonEmpty(StringUtils.EMPTY, ENTITY_DOES_NOT_EXISTS_EXCEPTION, "The param is empty");
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The param is empty");
            assertThat(((MeveoApiException) ex).getErrorCode()).isEqualTo(ENTITY_DOES_NOT_EXISTS_EXCEPTION);
        }

    }

}