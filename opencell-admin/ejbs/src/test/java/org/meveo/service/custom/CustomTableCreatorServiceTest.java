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

package org.meveo.service.custom;

import liquibase.change.AddColumnConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CustomTableCreatorServiceTest {
    
    @InjectMocks
    private CustomTableCreatorService sut;
    
    
    @Test
    public void given_string_column_type_with_null_max_value_then_should_set_column_data_type_as_varchar_with_default_length() {
        //Given
        CustomFieldTemplate customFieldTemplate = new CustomFieldTemplate();
        customFieldTemplate.setFieldType(CustomFieldTypeEnum.STRING);
        AddColumnConfig addColumnConfig = new AddColumnConfig();
        //When
        sut.setColumnType(customFieldTemplate, addColumnConfig);
        //Then
        assertThat(addColumnConfig.getType()).isEqualTo("varchar(50)");
    }
    
    @Test
    public void given_string_column_type_with_valid_max_value_then_should_set_column_data_type_as_varchar_with_defined_length() {
        //Given
        CustomFieldTemplate customFieldTemplate = new CustomFieldTemplate();
        customFieldTemplate.setFieldType(CustomFieldTypeEnum.STRING);
        customFieldTemplate.setMaxValue(30L);
        AddColumnConfig addColumnConfig = new AddColumnConfig();
        //When
        sut.setColumnType(customFieldTemplate, addColumnConfig);
        //Then
        assertThat(addColumnConfig.getType()).isEqualTo("varchar(30)");
    }
    @Test
    public void given_boolean_column_type_then_should_set_column_data_type_as_boolean() {
        //Given
        CustomFieldTemplate customFieldTemplate = new CustomFieldTemplate();
        customFieldTemplate.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        AddColumnConfig addColumnConfig = new AddColumnConfig();
        //When
        sut.setColumnType(customFieldTemplate, addColumnConfig);
        //Then
        assertThat(addColumnConfig.getType()).isEqualTo("boolean ");
    }
}