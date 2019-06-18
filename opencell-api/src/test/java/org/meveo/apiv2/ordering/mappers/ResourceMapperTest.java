package org.meveo.apiv2.ordering.mappers;

import org.junit.runner.RunWith;
import org.meveo.model.BaseEntity;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public interface ResourceMapperTest {
    void mapDtoToEntityAndEntityToDtoTest();
    BaseEntity generateEntity();
}
