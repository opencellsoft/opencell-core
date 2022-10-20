/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

    @Test
    public void should_compute_next_alphabet_sequence() {
        String current;
        CharSequence nextSequence;
        //Given
        current = "AZ";
        //When
        nextSequence = StringUtils.computeNextAlphabetSequence(current);
        //Then
        assertThat(nextSequence).isEqualTo("BA");

        //Given
        current = "AA";
        //When
        nextSequence = StringUtils.computeNextAlphabetSequence(current);
        //Then
        assertThat(nextSequence).isEqualTo("AB");

        //Given
        current = "AZZ";
        //When
        nextSequence = StringUtils.computeNextAlphabetSequence(current);
        //Then
        assertThat(nextSequence).isEqualTo("BAA");

        //Given
        current = "BBA";
        //When
        nextSequence = StringUtils.computeNextAlphabetSequence(current);
        //Then
        assertThat(nextSequence).isEqualTo("BBB");

        //Given
        current = "BZA";
        //When
        nextSequence = StringUtils.computeNextAlphabetSequence(current);
        //Then
        assertThat(nextSequence).isEqualTo("BZB");

        //Given
        current = "ZZZ";
        //When
        nextSequence = StringUtils.computeNextAlphabetSequence(current);
        //Then
        assertThat(nextSequence).isEqualTo("AAAA");
    }
}