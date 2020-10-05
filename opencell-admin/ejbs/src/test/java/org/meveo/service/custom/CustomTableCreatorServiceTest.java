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