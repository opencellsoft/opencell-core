package org.meveo.service.crm.impl;

import org.junit.Test;
import org.meveo.model.customEntities.CustomEntityTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomFieldInstanceServiceTest {
    private CustomFieldInstanceService sut = new CustomFieldInstanceService();

    @Test
    public void should_return_class_if_className_contains_custom_table_ref() throws ClassNotFoundException {
        //Given
        String className = "org.meveo.model.customEntities.CustomEntityTemplate - TABLE_1";
        //When
        Class<?> clazz = sut.trimTableNameAndGetClass(className);
        //Then
        assertThat(clazz).isEqualTo(CustomEntityTemplate.class);
    }

    @Test
    public void should_return_class_if_className_is_right() throws ClassNotFoundException {
        //Given
        String className = "org.meveo.model.customEntities.CustomEntityTemplate";
        //When
        Class<?> clazz = sut.trimTableNameAndGetClass(className);
        //Then
        assertThat(clazz).isEqualTo(CustomEntityTemplate.class);
    }
}