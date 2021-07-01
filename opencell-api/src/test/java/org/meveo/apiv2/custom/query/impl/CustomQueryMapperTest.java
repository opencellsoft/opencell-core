package org.meveo.apiv2.custom.query.impl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.meveo.apiv2.custom.ImmutableCustomQueryInput.builder;

import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.custom.CustomQueryInput;
import org.meveo.model.custom.query.CustomQuery;
import org.meveo.model.custom.query.QueryVisibilityEnum;

import java.util.Arrays;

public class CustomQueryMapperTest {

    private CustomQueryMapper mapper;

    @Before
    public void setUp() {
        mapper = new CustomQueryMapper();
    }

    @Test
    public void shouldConvertResourceToEntity() {
        CustomQueryInput resource = builder()
                .queryName("code")
                .queryDescription("description")
                .visibility(QueryVisibilityEnum.PUBLIC)
                .targetEntity("org.meveo.model.billing.BillingAccount")
                .fields(Arrays.asList("code", "description"))
                .build();
        org.meveo.model.custom.query.CustomQuery entity = mapper.toEntity(resource);
        assertThat(entity, instanceOf(org.meveo.model.custom.query.CustomQuery.class));
        assertEquals(entity.getCode(), resource.getQueryName());
        assertEquals(entity.getDescription(), resource.getQueryDescription());
    }

    @Test
    public void shouldConvertEntityToResource() {
        CustomQuery entity = new CustomQuery();
        entity.setCode("code");
        entity.setDescription("Custom query");
        entity.setVisibility(QueryVisibilityEnum.PRIVATE);
        entity.setTargetEntity("org.meveo.model.billing.BillingAccount");
        entity.setFields(Arrays.asList("code", "description"));

        org.meveo.apiv2.custom.CustomQuery resource = mapper.toResource(entity);
        assertThat(resource, instanceOf(org.meveo.apiv2.custom.CustomQuery.class));
        assertEquals(resource.getCode(), entity.getCode());
    }
}