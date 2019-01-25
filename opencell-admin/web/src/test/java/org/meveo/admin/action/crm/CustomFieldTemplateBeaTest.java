package org.meveo.admin.action.crm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.BusinessEntity;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CustomFieldTemplateBeaTest {
    @Mock
    private CustomizedEntityService customizedEntityService;

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

    @Before
    public void setup() {
        when(customizedEntityService.getCustomizedEntities("", false, true, false, null, null)).thenReturn(JOBS_CUSTOMIZED_ENTITY);
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
        return reflections.getSubTypesOf(BusinessEntity.class).stream()
                .filter(businessEntity -> !Modifier.isAbstract(businessEntity.getModifiers()))
                .map(businessEntity -> new CustomizedEntity(businessEntity))
                .map(customizedEntity -> customizedEntity.getClassnameToDisplay())
                .collect(Collectors.toList());
    }

    private static List<CustomizedEntity> getJobsCustomizedEntity() {
        Reflections reflections = new Reflections("org.meveo.admin.job");
        return reflections.getSubTypesOf(BusinessEntity.class).stream()
                .filter(businessEntity -> !Modifier.isAbstract(businessEntity.getModifiers()))
                .map(businessEntity -> new CustomizedEntity(businessEntity))
                .collect(Collectors.toList());
    }
}
