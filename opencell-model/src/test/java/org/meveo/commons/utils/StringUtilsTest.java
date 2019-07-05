package org.meveo.commons.utils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class StringUtilsTest {

    @Test
    public void should_return_false_when_value_is_null() {
        //Given
        String value = null;
        //When
        boolean notBlank = StringUtils.isNotBlank(value);
        //Then
        assertThat(notBlank).isFalse();

    }

    @Test
    public void should_return_false_when_value_is_empty() {
        //Given
        String value = "";
        //When
        boolean notBlank = StringUtils.isNotBlank(value);
        //Then
        assertThat(notBlank).isFalse();

    }

    @Test
    public void should_return_false_when_value_is_white_space() {
        //Given
        String value = " ";
        //When
        boolean notBlank = StringUtils.isNotBlank(value);
        //Then
        assertThat(notBlank).isFalse();

    }

    @Test
    public void should_return_true_when_value_is_not_empty_and_not_white_space() {
        //Given
        String value = " value ";
        //When
        boolean notBlank = StringUtils.isNotBlank(value);
        //Then
        assertThat(notBlank).isTrue();

    }
}