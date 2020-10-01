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

package org.meveo.service.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeletionServiceTest {

    @Spy
    @InjectMocks
    private DeletionService sut;

    @Mock
    private CustomEntityTemplateService customEntityTemplateService;

    @Mock
    private CustomFieldTemplateService customFieldTemplateService;

    @Mock
    private CustomEntityInstanceService customEntityInstanceService;

    @Test
    public void should_remove_prefix_when_string_starts_whith_prefix() {
        //Given
        String code = "CE_TABLE_1";
        //When
        String removedPrefix = sut.removePrefix(code);
        //Then
        assertThat(removedPrefix).isEqualTo("TABLE_1");
    }

    @Test
    public void should_create_CEI_on_delete_table() {
        doNothing().when(sut).checkEntityIsNotreferenced(any(IEntity.class));
        //Given
        String tableNAme = "AAA";
        //When
        sut.checkTableNotreferenced(tableNAme, 5L);
        //Then
        ArgumentCaptor<IEntity> captor = ArgumentCaptor.forClass(IEntity.class);
        verify(sut).checkEntityIsNotreferenced(captor.capture());
        IEntity value = captor.getValue();
        assertThat(value).isInstanceOf(CustomEntityInstance.class);
        CustomEntityInstance customEntityInstance = (CustomEntityInstance) value;
        assertThat(customEntityInstance.getCetCode()).isEqualTo(tableNAme);
        assertThat(customEntityInstance.getCode()).isEqualTo("5");


    }

    @Test
    public void should_not_delete_CE_IN_BE() {
        //Given
        CustomEntityInstance CE = new CustomEntityInstance();
        CE.setCetCode("BE_CE");
        CE.setCode("AAA");
        CE.setDescription("AAA");
        CE.setId(8L);

        CustomFieldTemplate template = new CustomFieldTemplate();
        template.setEntityClazz("org.meveo.model.customEntities.CustomEntityTemplate - BE_CE");
        template.setCode("abcd");
        template.setDescription("bcda");
        template.setAppliesTo("Customer");

    }
}