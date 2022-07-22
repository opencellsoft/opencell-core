package org.meveo.apiv2.report.query.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.meveo.model.report.query.QueryVisibilityEnum.PUBLIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.Auditable;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.security.MeveoUser;
import org.meveo.service.report.ReportQueryService;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReportQueryApiServiceTest {

    @Spy
    @InjectMocks
    private ReportQueryApiService reportQueryApiService;

    @Mock
    private ReportQueryService reportQueryService;

    @Mock
    private MeveoUser currentUser;


    @Before
    public void setup() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setId(1L);
        reportQuery.setVisibility(PUBLIC);
        reportQuery.setDescription("description");
        reportQuery.setCode("code");
        reportQuery.setGeneratedQuery("select name, code, description from Product");
        reportQuery.setFields(List.of("description", "name", "code"));
        Auditable auditable = new Auditable();
        auditable.setCreator("opencell.admin");
        reportQuery.setAuditable(auditable);
        when(currentUser.hasRole(eq("queryManagement"))).thenReturn(true);
        when(reportQueryService.findById(any(), any())).thenReturn(reportQuery);
        when(currentUser.getUserName()).thenReturn("opencell.admin");
        when(reportQueryService.findById(any())).thenReturn(reportQuery);
    }

    @Test
    public void shouldReturnEntity() {
        Optional<ReportQuery> optionalCustomQuery = reportQueryApiService.findById(1L);
        assertTrue(optionalCustomQuery.isPresent());
        ReportQuery reportQuery = optionalCustomQuery.get();
        assertEquals("code", reportQuery.getCode());
        assertEquals(PUBLIC, reportQuery.getVisibility());
    }

    @Test
    public void shouldDeleteCustomQuery() {
        Optional<ReportQuery> deletedEntity = reportQueryApiService.delete(1L);
        assertNotNull(deletedEntity);
    }
}