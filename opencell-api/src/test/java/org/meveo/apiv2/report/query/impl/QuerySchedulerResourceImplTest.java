package org.meveo.apiv2.report.query.impl;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.meveo.apiv2.report.ImmutableQuerySchedulerInput.builder;
import static org.meveo.model.report.query.QueryVisibilityEnum.PUBLIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.report.ImmutableQueryScheduler;
import org.meveo.apiv2.report.QuerySchedulerInput;
import org.meveo.apiv2.report.query.service.QuerySchedulerApiService;
import org.meveo.apiv2.report.query.service.ReportQueryApiService;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.report.QuerySchedulerService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuerySchedulerResourceImplTest {

    @Spy
    @InjectMocks
    private ReportQueryResourceImpl reportQueryResource;

    @Mock
    private QuerySchedulerApiService querySchedulerApiService;
    
    @Mock
    private ReportQueryApiService reportQueryApiService;

    @Mock
    private Request request;

    @Mock
    private JobInstanceService jobInstanceService;

    @Mock
    private QuerySchedulerService querySchedulerService;

    @Before
    public void setUp() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setId(1L);
        reportQuery.setVisibility(PUBLIC);
        reportQuery.setDescription("description");
        reportQuery.setCode("code");
        reportQuery.setFields(asList("code", "description"));
        reportQuery.setTargetEntity("org.meveo.model.billing.BillingRun");
        
        QueryScheduler queryScheduler = new QueryScheduler();
        queryScheduler.setReportQuery(reportQuery);
        queryScheduler.setFileFormat("csv");
        queryScheduler.setIsQueryScheduler(true);
        queryScheduler.getQueryTimer().setDayOfMonth("1");
        queryScheduler.getQueryTimer().setDayOfWeek("Sun");
        queryScheduler.getQueryTimer().setEveryDayOfMonth(false);
        queryScheduler.getQueryTimer().setEveryDayOfWeek(true);
        queryScheduler.getQueryTimer().setEveryHour(true);
        queryScheduler.getQueryTimer().setEveryMinute(true);
        queryScheduler.getQueryTimer().setEveryMonth(false);
        queryScheduler.getQueryTimer().setEverySecond(true);
        queryScheduler.getQueryTimer().setHour("0");
        queryScheduler.getQueryTimer().setMinute("0");
        queryScheduler.getQueryTimer().setSecond("0");
        queryScheduler.getQueryTimer().setMonth("07");
        queryScheduler.getQueryTimer().setYear("2021");

        Optional<ReportQuery> optionalCustomQuery = of(reportQuery);
        when(reportQueryApiService.findById(anyLong())).thenReturn(optionalCustomQuery);
        when(querySchedulerApiService.create(any())).thenReturn(queryScheduler);
    }

    @Test
    public void shouldSaveQueryScheduler() {
        
        QuerySchedulerInput input = builder()
                .fileFormat("csv")
        		.year("2021")
        		.month("07")
        		.dayOfMonth("1")
        		.dayOfWeek("Sun")
        		.hour("0")
        		.minute("0")
        		.second("0")
        		.everyMonth(false)
        		.everyDayOfMonth(false)
        		.everyDayOfWeek(true)
        		.everyHour(true)
        		.everyMinute(false)
        		.everySecond(false)
        		.isQueryScheduler(true)
        		.build();
        
        Response response = reportQueryResource.createQueryScheduler(Long.valueOf(1), input);
        assertEquals(201, response.getStatus());
        assertThat(response.getEntity(), instanceOf(ImmutableQueryScheduler.class));
        org.meveo.apiv2.report.QueryScheduler queryScheduler = (ImmutableQueryScheduler) response.getEntity();
        assertEquals(0, queryScheduler.getUsersToNotify().size());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionInCaseOfMissingAttributes() {
    	QuerySchedulerInput input = builder()
        		.year("2021")
        		.month("07")
        		.dayOfMonth("1")
        		.dayOfWeek("Sun")
        		.hour("0")
        		.minute("0")
        		.second("0")
        		.everyMonth(false)
        		.everyDayOfMonth(false)
        		.everyDayOfWeek(true)
        		.everyHour(true)
        		.everyMinute(false)
        		.everySecond(false)
        		.build();
    	Response response = reportQueryResource.createQueryScheduler(Long.valueOf(1), input);
    }

}