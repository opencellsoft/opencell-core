package org.meveo.service.custom;

import java.util.HashMap;

import org.junit.Test;
import org.meveo.model.customEntities.CustomEntityInstance;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomEntityInstanceServiceTest {

    private CustomEntityInstanceService sut = new CustomEntityInstanceService();

    @Test
    public void should_transform_custom_entity_instance_to_map() {
        //Given
        CustomEntityInstance instance = new CustomEntityInstance();
        //When
        HashMap<String, Object> transformedMap = sut.customEntityInstanceAsMap(instance);
        //Then
        assertThat(transformedMap).isNotNull();
        assertThat(transformedMap).hasSize(3);
        assertThat(transformedMap).containsKeys("code", "description", "id");
    }

    @Test
    public void should_transform_custom_entity_instance_to_map_with_cf_values() {
        //Given
        CustomEntityInstance instance = new CustomEntityInstance();
        //When
        HashMap<String, Object> transformedMap = sut.customEntityInstanceAsMapWithCfValues(instance);
        //Then
        assertThat(transformedMap).isNotNull();
        assertThat(transformedMap).hasSize(4);
        assertThat(transformedMap).containsKeys("cfValues");
    }
}