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

package org.meveo.admin.action.admin.custom;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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

    @Mock
    private PersistenceService<BusinessEntity> persistenceService;

    @Before
    public void init() {
        doReturn(persistenceService).when(sut).getPersistenceServiceByClass(((Class<?>) any(Class.class)));
    }

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
        verify(customTableService).selectAllRecordsOfATableAsRecord(eq("TABLE_1"), any());
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

    @Test
    public void should_return_business_entity_records_when_associated_entity_is_business() {
        //Given
        CustomFieldTemplate field = new CustomFieldTemplate();
        field.setEntityClazz("org.meveo.model.customEntities.CustomEntityTemplate");
        CustomEntityTemplate relatedEntity = mock(CustomEntityTemplate.class);
        //When
        sut.getFromCustomEntity(field);
        //Then
        verify(sut).loadFromBusinessEntity(eq(field));

    }

    @Test
    public void should_transform_business_entity_to_map() {
        //Given
        BusinessEntity businessEntity = mock(BusinessEntity.class);
        //When
        HashMap<String, Object> result = sut.mapToMap(businessEntity);
        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsKeys("id", "code", "description");
    }

    @Test
    public void should_return_export_identifier_when_entity_is_not_business_entity() {
        //Given
        User user = new User();
        user.setId(15L);
        user.setUserName("flirtikit");
        //When
        HashMap<String, Object> convertedValues = sut.mapToMap(user);
        //Then
        assertThat(convertedValues).isNotNull();
        assertThat(convertedValues).hasSize(2);
        assertThat(convertedValues.get("id")).isEqualTo(15L);
        assertThat(convertedValues.get("userName")).isEqualTo("flirtikit");
    }

}