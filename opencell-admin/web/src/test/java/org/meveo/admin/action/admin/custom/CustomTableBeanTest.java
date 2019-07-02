package org.meveo.admin.action.admin.custom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomTableBeanTest {

    @Spy
    @InjectMocks
    private CustomTableBean sut;

    @Mock
    private CustomTableService customTableService;

    @Mock
    private CustomEntityInstanceService customEntityInstanceService;

    @Mock
    private CustomEntityTemplateService customEntityTemplateService;

    @Test
    public void should_return_table_records_when_entity_si_store_as_table() {
        //Given
        CustomFieldTemplate field = new CustomFieldTemplate();
        field.setEntityClazz("org.meveo.model.customEntities.CustomEntityTemplate - TABLE_1");
        CustomEntityTemplate relatedEntity = mock(CustomEntityTemplate.class);
        when(relatedEntity.isStoreAsTable()).thenReturn(true);
        when(customEntityTemplateService.findByCode(anyString())).thenReturn(relatedEntity);
        //When
        sut.entityTypeColumnDatas(field);
        //Then
        verify(customTableService).selectAllRecordsOfATableAsRecord(eq("TABLE_1"));
    }

    @Test
    public void should_return_entity_records_when_entity_is_not_store_as_table() {
        //Given
        CustomFieldTemplate field = new CustomFieldTemplate();
        field.setEntityClazz("org.meveo.model.customEntities.CustomEntityTemplate - TABLE_1");
        CustomEntityTemplate relatedEntity = mock(CustomEntityTemplate.class);
        when(relatedEntity.isStoreAsTable()).thenReturn(false);
        when(customEntityTemplateService.findByCode(anyString())).thenReturn(relatedEntity);
        //When
        sut.entityTypeColumnDatas(field);
        //Then
        verify(sut).getFromCustomEntity(eq(field));

    }
}