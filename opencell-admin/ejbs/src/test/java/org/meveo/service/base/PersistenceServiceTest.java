package org.meveo.service.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceServiceTest {

    @InjectMocks
    private PersistenceServiceStub sut;

    @Test
    public void should_throw_exception_if_entity_contains_custom_table_reference() {

        //Given
        Customer entity = new Customer();
        CustomFieldValues customFieldValues = new CustomFieldValues();
        CustomFieldValue customFieldValue = new CustomFieldValue();
        EntityReferenceWrapper entityReferenceValue = new EntityReferenceWrapper();
        entityReferenceValue.setClassname("org.meveo.model.customEntities.CustomEntityInstance");
        entityReferenceValue.setClassnameCode("Table_1");
        customFieldValue.setEntityReferenceValue(entityReferenceValue);
        HashMap<String, List<CustomFieldValue>> cfValues = new HashMap<String, List<CustomFieldValue>>(){{
            put("T1", Arrays.asList(customFieldValue));
        }};
        customFieldValues.setValuesByCode(cfValues);
        entity.setCfValues(customFieldValues);
        //When
        try {
            sut.checkEntityDoesNotcontainReferenceToCustomTable(entity);
        }catch (Exception ex){
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("Cannot remove entity: CustomTable reference is present");
        }
    }

  static class PersistenceServiceStub extends PersistenceService<Customer> {}

}