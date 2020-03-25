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