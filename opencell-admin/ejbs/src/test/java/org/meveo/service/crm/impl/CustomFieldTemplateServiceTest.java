package org.meveo.service.crm.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomFieldTemplateServiceTest {
    @InjectMocks
    private CustomFieldTemplateService sut;

    @Mock
    private CustomEntityTemplateService customEntityTemplateService;

    @Test
    public void should_not_do_anything_if_cft_is_null() {
        //Given
        CustomFieldTemplate cft = null;
        //When
        sut.checkAndUpdateUniqueConstraint(cft);
        //Then
        verify(customEntityTemplateService, never()).findByCode(anyString());
    }

    @Test
    public void should_not_do_anything_if_cft_is_not_custom_table() {
        //Given
        CustomFieldTemplate cft = mock(CustomFieldTemplate.class);
        when(cft.tableName()).thenReturn(StringUtils.EMPTY);
        //When
        sut.checkAndUpdateUniqueConstraint(cft);
        //Then
        verify(customEntityTemplateService, never()).findByCode(anyString());
    }

    @Test
    public void should_not_do_anything_if_custom_entity_is_not_store_as_table() {
        //Given
        CustomFieldTemplate cft = mock(CustomFieldTemplate.class);
        when(cft.tableName()).thenReturn("TABLE_1");
        CustomEntityTemplate customEntityTemplate = mock(CustomEntityTemplate.class);
        when(customEntityTemplateService.findByCode(cft.tableName())).thenReturn(customEntityTemplate);

        //When
        sut.checkAndUpdateUniqueConstraint(cft);
        //Then
        verify(customEntityTemplate, never()).setUniqueContraintName(anyString());
    }

    @Test
    public void should_removePrefixe() {
        //Given
        String table1 = "CE_TABLE_1";
        String table2 = "CE_CE_TABLE_2";
        //When
        String removePrefixeFromTableName1 = sut.removePrefixeFromTableName(table1);
        String removePrefixeFromTableName2 = sut.removePrefixeFromTableName(table2);
        //Then
        assertThat(removePrefixeFromTableName1).isEqualTo("TABLE_1");
        assertThat(removePrefixeFromTableName2).isEqualTo("CE_TABLE_2");
    }
}
