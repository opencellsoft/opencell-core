package org.meveo.apiv2.custom.query.impl;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.meveo.apiv2.custom.ImmutableReportQueryInput.builder;
import static org.meveo.model.custom.query.QueryVisibilityEnum.PRIVATE;
import static org.meveo.model.custom.query.QueryVisibilityEnum.PUBLIC;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.custom.ReportQueries;
import org.meveo.apiv2.custom.ReportQueryInput;
import org.meveo.apiv2.custom.ImmutableReportQuery;
import org.meveo.apiv2.custom.query.service.ReportQueryApiService;
import org.meveo.model.custom.query.ReportQuery;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ReportQueryResourceImplTest {

    @Spy
    @InjectMocks
    private ReportQueryResourceImpl customQueryResource;

    @Mock
    private ReportQueryApiService reportQueryApiService;

    @Mock
    private Request request;

    @Before
    public void setUp() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setId(1L);
        reportQuery.setVisibility(PUBLIC);
        reportQuery.setDescription("description");
        reportQuery.setCode("code");
        reportQuery.setFields(asList("code", "description"));
        reportQuery.setTargetEntity("org.meveo.model.billing.BillingRun");

        Optional<ReportQuery> optionalCustomQuery = of(reportQuery);
        when(reportQueryApiService.findById(anyLong())).thenReturn(optionalCustomQuery);
        when(reportQueryApiService.create(any())).thenReturn(reportQuery);
        when(reportQueryApiService.delete(1L)).thenReturn(empty());
        when(reportQueryApiService.delete(2L)).thenReturn(of(reportQuery));
    }

    @Test
    public void shouldReturnCustomQuery() {
        Response response = customQueryResource.find(1L);
        org.meveo.apiv2.custom.ReportQuery reportQuery = (ImmutableReportQuery) response.getEntity();

        assertEquals(200, response.getStatus());
        assertEquals(PUBLIC, reportQuery.getVisibility());
        assertEquals(2, reportQuery.getFields().size());
        assertEquals("org.meveo.model.billing.BillingRun", reportQuery.getTargetEntity());
    }

    @Test
    public void shouldSaveCustomQuery() {
        ReportQueryInput input = builder()
                .queryName("code")
                .queryDescription("description")
                .targetEntity("org.meveo.model.billing.BillingRun")
                .fields(asList("code", "description"))
                .visibility(PUBLIC)
                .build();
        Response response = customQueryResource.createReportQuery(input);
        assertEquals(201, response.getStatus());
        assertThat(response.getEntity(), instanceOf(ImmutableReportQuery.class));
        org.meveo.apiv2.custom.ReportQuery reportQuery = (ImmutableReportQuery) response.getEntity();
        assertEquals(2, reportQuery.getFields().size());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionInCaseOfMissingAttributes() {
        ReportQueryInput input = builder()
                .queryName("code")
                .queryDescription("description")
                .fields(asList("code", "description"))
                .visibility(PUBLIC)
                .build();
        customQueryResource.createReportQuery(input);
    }

    @Test
    public void shouldDeleteCustomQuery() {
        Response response = customQueryResource.delete(2L);
        assertEquals(null, response);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundException() {
        customQueryResource.delete(1L);
    }

    @Test
    public void shouldReturnCustomQueryList() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setId(1L);
        reportQuery.setVisibility(PUBLIC);
        reportQuery.setDescription("description");
        reportQuery.setCode("code");
        reportQuery.setFields(asList("code", "description"));
        reportQuery.setTargetEntity("org.meveo.model.billing.BillingRun");

        ReportQuery reportQuery1 = new ReportQuery();
        reportQuery1.setId(2L);
        reportQuery1.setVisibility(PRIVATE);
        reportQuery1.setDescription("description");
        reportQuery1.setCode("code");
        reportQuery1.setFields(asList("code", "description"));
        reportQuery1.setTargetEntity("org.meveo.model.billing.BillingAccount");
        List<ReportQuery> customQueries = asList(reportQuery, reportQuery);
        when(request.evaluatePreconditions(new EntityTag(Integer.toString(customQueries.hashCode())))).thenReturn(null);
        when(reportQueryApiService.list(0L, 10L, null, null, null)).thenReturn(customQueries);
        Response response = customQueryResource
                .getReportQueries(0L, 10L, null, null, null, request);
        assertEquals(200, response.getStatus());
        assertThat(response.getEntity(), instanceOf(ReportQueries.class));
        assertEquals(2, ((ReportQueries) response.getEntity()).getData().size());
    }
}