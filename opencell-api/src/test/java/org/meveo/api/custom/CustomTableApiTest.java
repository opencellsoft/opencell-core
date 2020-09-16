package org.meveo.api.custom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomTableApiTest {
    @InjectMocks
    private CustomTableApi sut;

    @Spy
    @InjectMocks
    private CustomTableService customTableService;

    @Mock
    private CustomEntityTemplateService customEntityTemplateService;

    @Mock
    private CustomFieldTemplateService customFieldTemplateService;

    @Mock
    private CustomEntityInstanceService customEntityInstanceService;


    @Test
    public void should_convert_All_map_keys_to_lower_case() {
        //Given
        Map<String, CustomFieldTemplate> map = new HashMap<String, CustomFieldTemplate>() {{
            put("FLIRTIKIT", mock(CustomFieldTemplate.class));
            put("BIDLIDEZ", mock(CustomFieldTemplate.class));
            put("GNINENDIDEN", mock(CustomFieldTemplate.class));
        }};
        //When
        Map<String, CustomFieldTemplate> lowerCaseKeysMap = customTableService.toLowerCaseKeys(map);
        //Then
        assertThat(lowerCaseKeysMap.keySet()).contains("flirtikit", "bidlidez", "gninendiden");
        assertThat(lowerCaseKeysMap.keySet()).doesNotContain("FLIRTIKIT", "BIDLIDEZ", "GNINENDIDEN");
    }

    @Test
    public void should_not_replace_value_when_reference_not_contains_custom_table_field() {
        //Given
        Map<String, CustomFieldTemplate> reference = new HashMap<>();
        Map.Entry<String, Object> entry = new TestEntry("flirtikit", null);
        //When
        customTableService.replaceIdValueByItsRepresentation(reference, entry, 0, 0);
        //Then
        assertThat(entry.getValue()).isNull();
    }

    @Test
    public void should_not_replace_value_if_field_its_not_a_custom_table_field() {
        //Given
        Map<String, CustomFieldTemplate> reference = new HashMap<String, CustomFieldTemplate>() {{
            put("flirtikit", mock(CustomFieldTemplate.class));
        }};
        Map.Entry<String, Object> entry = new TestEntry("flirtikit", null);
        //When
        customTableService.replaceIdValueByItsRepresentation(reference, entry, 0, 0);
        //Then
        assertThat(entry.getValue()).isNull();
    }

    @Test
    public void should_not_replace_value_if_there_is_no_records_in_database() {
        //Given
        CustomFieldTemplate customTableField = mock(CustomFieldTemplate.class);
        when(customTableField.getEntityClazz()).thenReturn("org.meveo.model.customEntities.CustomEntityTemplate - TABLE_2");
        Map<String, CustomFieldTemplate> reference = new HashMap<String, CustomFieldTemplate>() {{
            put("flirtikit", customTableField);
        }};
        Map.Entry<String, Object> entry = new TestEntry("flirtikit", 55L);
        //When
        customTableService.replaceIdValueByItsRepresentation(reference, entry, 0, 0);
        //Then
        assertThat(entry.getValue()).isEqualTo(55L);
    }

    @Test
    public void should_replace_value_if_all_is_ok() {
        //Given
        CustomFieldTemplate customTableField = mock(CustomFieldTemplate.class);
        when(customTableField.getEntityClazz()).thenReturn("org.meveo.model.customEntities.CustomEntityTemplate");
        CustomEntityTemplate relatedEntity = mock(CustomEntityTemplate.class);
        when(relatedEntity.isStoreAsTable()).thenReturn(true);
        when(customEntityTemplateService.findByCode(anyString())).thenReturn(relatedEntity);
        Map<String, CustomFieldTemplate> reference = new HashMap<String, CustomFieldTemplate>() {{
            put("flirtikit", customTableField);
        }};
        Map.Entry<String, Object> entry = new TestEntry("flirtikit", 55L);
        //When
        customTableService.replaceIdValueByItsRepresentation(reference, entry, 0, 0);
        //Then
        assertThat(entry.getValue()).isNotNull();
    }

    @Test
    public void should_not_replace_value_if_entry_value_is_null() {
        //Given
        Map<String, CustomFieldTemplate> reference = new HashMap<>();
        Map.Entry<String, Object> entry = new TestEntry("flirtikit", null);
        //When
        customTableService.replaceIdValueByItsRepresentation(reference, entry, 0, 0);
        //Then
        assertThat(entry.getValue()).isNull();
    }

    @Test
    public void should_not_replace_value_if_entry_value_is_not_a_number() {
        //Given
        Map<String, CustomFieldTemplate> reference = new HashMap<>();
        Map.Entry<String, Object> entry = new TestEntry("flirtikit", "bidlidez");
        //When
        customTableService.replaceIdValueByItsRepresentation(reference, entry, 0, 0);
        //Then
        assertThat(entry.getValue()).isEqualTo("bidlidez");
    }

    @Test
    public void should_load_data_from_custom_table_service_when_related_entity_is_custom_table() {
        //Given
        CustomFieldTemplate field = mock(CustomFieldTemplate.class);
        when(field.tableName()).thenReturn("flirtikit");
        CustomEntityTemplate relatedEntity = mock(CustomEntityTemplate.class);
        when(relatedEntity.isStoreAsTable()).thenReturn(true);
        when(customEntityTemplateService.findByCode(anyString())).thenReturn(relatedEntity);
        //When
        customTableService.getEitherTableOrEntityValue(field, 15L);
        //Then
        verify(customTableService).findRecordOfTableById(eq(field), eq(15L));
    }

   @Test
    public void should_load_data_from_custom_tamplate_service_when_related_entity_is_custom_entity() {
        //Given
        CustomFieldTemplate field = mock(CustomFieldTemplate.class);
        when(field.tableName()).thenReturn("flirtikit");
        CustomEntityTemplate relatedEntity = mock(CustomEntityTemplate.class);
        when(relatedEntity.isStoreAsTable()).thenReturn(false);
        when(customEntityTemplateService.findByCode(anyString())).thenReturn(relatedEntity);
        //When
        customTableService.getEitherTableOrEntityValue(field, 15L);
        //Then
        verify(customEntityInstanceService).findById(eq(15L));
    }

    private class TestEntry implements Map.Entry<String, Object> {
        private String key;
        private Object value;

        public TestEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public Object setValue(Object value) {
            return this.value = value;
        }
    }
}
