package org.meveo.api.dto.generic;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericRequestDtoTest {

    @Test
    public void should_get_fields_remove_all_blank_fields() {
        //Given
        GenericRequestDto dto = new GenericRequestDto();
        dto.setFields(Arrays.asList("value", "", " ", null, "other value"));
        //When
        List<String> formattedFields = dto.getFields();
        //Then
        assertThat(formattedFields.size()).isEqualTo(2);
        assertThat(formattedFields).isEqualTo(Arrays.asList("value", "other value"));
    }

   @Test
    public void should_get_fields_map_to_lower_case() {
        //Given
        GenericRequestDto dto = new GenericRequestDto();
        dto.setFields(Arrays.asList("Value", "", " ", null, "Other value"));
        //When
        List<String> formattedFields = dto.getFields();
        //Then
        assertThat(formattedFields.size()).isEqualTo(2);
        assertThat(formattedFields).isEqualTo(Arrays.asList("value", "other value"));
    }

    @Test
    public void should__get_fields_keep_initial_list_when_its_validated() {
        //Given
        GenericRequestDto dto = new GenericRequestDto();
        dto.setFields(Arrays.asList("Value 1", "Value 2", "Value 3"));
        //When
        List<String> formattedFields = dto.getFields();
        //Then
        assertThat(formattedFields.size()).isEqualTo(3);
        assertThat(formattedFields).isEqualTo(Arrays.asList("value 1", "value 2", "value 3"));
    }
}