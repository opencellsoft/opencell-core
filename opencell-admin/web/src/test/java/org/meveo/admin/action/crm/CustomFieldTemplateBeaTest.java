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

package org.meveo.admin.action.crm;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reflections.Reflections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomFieldTemplateBeaTest {
    @Mock
    private CustomizedEntityService customizedEntityService;
    
    @Mock
    private CustomEntityTemplateService customEntityTemplateService;

    @InjectMocks
    private CustomFieldTemplateBean CustomFieldTemplateBean;

    static List<String> CUSTOMIZED_BUSINESS_ENTITIES_CLASS_NAMES;
    static List<String> JOBS_CUSTOMIZED_ENTITY_CLASS_NAMES;
    static List<CustomizedEntity> JOBS_CUSTOMIZED_ENTITY;

    @BeforeClass
    public static void setupHeavyInitialization(){
        CUSTOMIZED_BUSINESS_ENTITIES_CLASS_NAMES = getAllBusinessEntityClassNames();
        JOBS_CUSTOMIZED_ENTITY = getJobsCustomizedEntity();
        JOBS_CUSTOMIZED_ENTITY_CLASS_NAMES = JOBS_CUSTOMIZED_ENTITY.stream()
                .map(customizedEntity -> customizedEntity.getClassnameToDisplay())
                .collect(Collectors.toList());
    }

    @Test
    public void retrieveAllModelClassAndCustomizedEntities(){
        List<String> classNames = CustomFieldTemplateBean.autocompleteClassNames("");
        for(String className : classNames){
            assertTrue(className.startsWith("org.meveo.model") || className.startsWith("org.meveo.admin.job"));
            assertTrue(CUSTOMIZED_BUSINESS_ENTITIES_CLASS_NAMES.contains(className) || className.equals("org.meveo.admin.job.BaseJobBean"));
        }
    }

    @Test
    public void retrieveAllModelClassAndCustomizedEntitiesByName(){
        List<String> classNames = CustomFieldTemplateBean.autocompleteClassNames("AccountingCode");
        assertEquals(classNames.size(),1);
        assertTrue(classNames.get(0).equalsIgnoreCase("org.meveo.model.billing.AccountingCode"));
    }

    private static List<String> getAllBusinessEntityClassNames() {
        Reflections reflections = new Reflections("org.meveo.model");
        List<String> list = reflections.getSubTypesOf(BusinessEntity.class).stream()
		                .filter(businessEntity -> !Modifier.isAbstract(businessEntity.getModifiers()))
		                .map(businessEntity -> new CustomizedEntity(businessEntity))
		                .map(customizedEntity -> customizedEntity.getClassnameToDisplay())
		                .collect(Collectors.toList());
        list.addAll(List.of(User.class.getName(), Role.class.getName()));
        return list;
    }

    private static List<CustomizedEntity> getJobsCustomizedEntity() {
        Reflections reflections = new Reflections("org.meveo.admin.job");
        return reflections.getSubTypesOf(BusinessEntity.class).stream()
                .filter(businessEntity -> !Modifier.isAbstract(businessEntity.getModifiers()))
                .map(businessEntity -> new CustomizedEntity(businessEntity))
                .collect(Collectors.toList());
    }
}
