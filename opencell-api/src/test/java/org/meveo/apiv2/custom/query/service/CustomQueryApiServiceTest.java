package org.meveo.apiv2.custom.query.service;

import static org.junit.Assert.*;
import static org.meveo.model.custom.query.QueryVisibilityEnum.PUBLIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.custom.query.CustomQuery;
import org.meveo.service.custom.CustomQueryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CustomQueryApiServiceTest {

    @Spy
    @InjectMocks
    private CustomQueryApiService customQueryApiService;

    @Mock
    private CustomQueryService customQueryService;

    @Before
    public void setup() {
        CustomQuery customQuery = new CustomQuery();
        customQuery.setId(1L);
        customQuery.setVisibility(PUBLIC);
        customQuery.setDescription("description");
        customQuery.setCode("code");

        when(customQueryService.findById(any(), any())).thenReturn(customQuery);
    }

    @Test
    public void shouldReturnEntity() {
        Optional<CustomQuery> optionalCustomQuery = customQueryApiService.findById(1L);
        assertTrue(optionalCustomQuery.isPresent());
        CustomQuery customQuery = optionalCustomQuery.get();
        assertEquals(customQuery.getCode(), "code");
        assertEquals(customQuery.getVisibility(), PUBLIC);
    }

    @Test
    public void shouldDeleteCustomQuery() {
        Optional<CustomQuery> deletedEntity = customQueryApiService.delete(1L);
        assertNotNull(deletedEntity);
    }
}