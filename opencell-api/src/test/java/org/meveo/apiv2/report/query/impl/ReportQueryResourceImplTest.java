package org.meveo.apiv2.report.query.impl;

import static java.util.Arrays.asList;
import static java.util.Map.of;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.meveo.apiv2.report.ImmutableReportQueryInput.builder;
import static org.meveo.model.report.query.QueryVisibilityEnum.PRIVATE;
import static org.meveo.model.report.query.QueryVisibilityEnum.PUBLIC;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.report.ExecutionResult;
import org.meveo.apiv2.report.ReportQueries;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.apiv2.report.ImmutableReportQuery;
import org.meveo.apiv2.report.query.service.ReportQueryApiService;
import org.meveo.model.report.query.ReportQuery;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ReportQueryResourceImplTest {

    @Spy
    @InjectMocks
    private ReportQueryResourceImpl reportQueryResource;

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
        reportQuery.setTargetEntity("BillingRun");

        Optional<ReportQuery> optionalCustomQuery = of(reportQuery);
        when(reportQueryApiService.findById(anyLong())).thenReturn(optionalCustomQuery);
        when(reportQueryApiService.create(any())).thenReturn(reportQuery);
        when(reportQueryApiService.delete(1L)).thenReturn(empty());
        when(reportQueryApiService.delete(2L)).thenReturn(of(reportQuery));
    }

    @Test
    public void shouldReturnCustomQuery() {
        Response response = reportQueryResource.find(1L);
        org.meveo.apiv2.report.ReportQuery reportQuery = (ImmutableReportQuery) response.getEntity();

        assertEquals(200, response.getStatus());
        assertEquals(PUBLIC, reportQuery.getVisibility());
        assertEquals(2, reportQuery.getFields().size());
        assertEquals("BillingRun", reportQuery.getTargetEntity());
    }

    @Test
    public void shouldSaveCustomQuery() {
        ReportQueryInput input = builder()
                .queryName("code")
                .queryDescription("description")
                .targetEntity("BillingRun")
                .fields(asList("code", "description"))
                .visibility(PUBLIC)
                .build();
        Response response = reportQueryResource.createReportQuery(input);
        assertEquals(200, response.getStatus());
        assertThat(response.getEntity(), instanceOf(ImmutableReportQuery.class));
        org.meveo.apiv2.report.ReportQuery reportQuery = (ImmutableReportQuery) response.getEntity();
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
        reportQueryResource.createReportQuery(input);
    }

    @Test
    public void shouldDeleteCustomQuery() {
        Response response = reportQueryResource.delete(2L);
        assertEquals(200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundException() {
        reportQueryResource.delete(1L);
    }

    @Test
    public void shouldReturnCustomQueryList() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setId(1L);
        reportQuery.setVisibility(PUBLIC);
        reportQuery.setDescription("description");
        reportQuery.setCode("code");
        reportQuery.setFields(asList("code", "description"));
        reportQuery.setTargetEntity("BillingRun");

        ReportQuery reportQuery1 = new ReportQuery();
        reportQuery1.setId(2L);
        reportQuery1.setVisibility(PRIVATE);
        reportQuery1.setDescription("description");
        reportQuery1.setCode("code");
        reportQuery1.setFields(asList("code", "description"));
        reportQuery1.setTargetEntity("BillingAccount");
        List<ReportQuery> customQueries = asList(reportQuery, reportQuery);
        when(request.evaluatePreconditions(new EntityTag(Integer.toString(customQueries.hashCode())))).thenReturn(null);
        when(reportQueryApiService.list(0L, 10L, null, null, null)).thenReturn(customQueries);
        Response response = reportQueryResource
                .getReportQueries(0L, 10L, null, null, null, request);
        assertEquals(200, response.getStatus());
        assertThat(response.getEntity(), instanceOf(ReportQueries.class));
        assertEquals(2, ((ReportQueries) response.getEntity()).getData().size());
    }

    @Test
    public void shouldReturnExecutionResult() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setTargetEntity("Invoice");
        reportQuery.setFields(Arrays.asList("invoiceNumber","amountWithTax", "status"));
        reportQuery.setVisibility(PRIVATE);
        reportQuery.setGeneratedQuery("SELECT a.invoiceNumber, a.amountWithTax, a.status FROM Invoice a");
        Map<String, Object> item = of("invoiceNumber", "INV_001",
                "amountWithTax", 10.0,
                "status", "VALIDATED");
        Map<String, Object> item2 = of("invoiceNumber", "INV_002",
                "amountWithTax", 20.0,
                "status", "NEW");
        Map<String, Object> item3 = of("invoiceNumber", "INV_003",
                "amountWithTax", 30.0,
                "status", "VALIDATED");
        List<Object> executionResult = Arrays.asList(item, item2, item3);

        when(reportQueryApiService.execute(1L, false)).thenReturn(of(executionResult));
        Response response = reportQueryResource.execute(1L, false);

        Object responseEntity = response.getEntity();
        assertEquals(3, ((ExecutionResult)responseEntity).getTotal());
        assertEquals(200, response.getStatus());

        List<Object> result =  ((ExecutionResult) responseEntity).getExecutionResults();
        assertEquals(reportQuery.getFields().size(), ((Map<String, Object>) result.get(2)).size());
        assertEquals("INV_002", ((Map<String, Object>) result.get(1)).get("invoiceNumber"));
        assertEquals("NEW", ((Map<String, Object>) result.get(1)).get("status"));
    }

    @Test
    public void shouldReturnAsyncExecutionResult() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setTargetEntity("Invoice");
        reportQuery.setFields(Arrays.asList("invoiceNumber","amountWithTax", "status"));
        reportQuery.setVisibility(PRIVATE);
        reportQuery.setGeneratedQuery("SELECT a.invoiceNumber, a.amountWithTax, a.status FROM Invoice a");

        when(reportQueryApiService.execute(1L, true)).thenReturn(of("Accepted"));
        Response response = reportQueryResource.execute(1L, true);

        assertEquals(202, response.getStatus());
        assertEquals("Execution request accepted", response.getEntity());
    }
}