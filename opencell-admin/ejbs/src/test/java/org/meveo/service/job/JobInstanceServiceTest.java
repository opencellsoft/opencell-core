package org.meveo.service.job;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.QueryTimer;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ejb.ScheduleExpression;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobInstanceServiceTest {

    @Mock
    private JobInstanceService jobInstanceService;

    @Test
    public void computeScheduleExpressionWhenEveryMinutesTest() {
        QueryScheduler queryScheduler = mock(QueryScheduler.class);
        QueryTimer timer = new QueryTimer();
        when(queryScheduler.getQueryTimer()).thenReturn(timer);
        when(jobInstanceService.getScheduleExpression(any())).thenCallRealMethod();
        timer.setEveryMinute(true);
        ScheduleExpression scheduleExpression = jobInstanceService.getScheduleExpression(queryScheduler);
        Assert.assertEquals("ScheduleExpression [second=0;minute=*;hour=*;dayOfMonth=*;month=*;dayOfWeek=*;year=*;timezoneID=null;start=null;end=null]",
                scheduleExpression.toString());
    }

    @Test
    public void computeScheduleExpressionWhenEveryHoursTest() {
        QueryScheduler queryScheduler = mock(QueryScheduler.class);
        QueryTimer timer = new QueryTimer();
        when(queryScheduler.getQueryTimer()).thenReturn(timer);
        when(jobInstanceService.getScheduleExpression(any())).thenCallRealMethod();
        timer.setEveryHour(true);
        ScheduleExpression scheduleExpression = jobInstanceService.getScheduleExpression(queryScheduler);
        Assert.assertEquals("ScheduleExpression [second=0;minute=0;hour=*;dayOfMonth=*;month=*;dayOfWeek=*;year=*;timezoneID=null;start=null;end=null]",
                scheduleExpression.toString());
    }

    @Test
    public void computeScheduleExpressionWhenEverySecondTest() {
        QueryScheduler queryScheduler = mock(QueryScheduler.class);
        QueryTimer timer = new QueryTimer();
        when(queryScheduler.getQueryTimer()).thenReturn(timer);
        when(jobInstanceService.getScheduleExpression(any())).thenCallRealMethod();
        timer.setEverySecond(true);
        ScheduleExpression scheduleExpression = jobInstanceService.getScheduleExpression(queryScheduler);
        Assert.assertEquals("ScheduleExpression [second=*;minute=*;hour=*;dayOfMonth=*;month=*;dayOfWeek=*;year=*;timezoneID=null;start=null;end=null]",
                scheduleExpression.toString());
    }

    @Test
    public void computeScheduleExpressionWhenEveryDayOfWeekTest() {
        QueryScheduler queryScheduler = mock(QueryScheduler.class);
        QueryTimer timer = new QueryTimer();
        when(queryScheduler.getQueryTimer()).thenReturn(timer);
        when(jobInstanceService.getScheduleExpression(any())).thenCallRealMethod();
        timer.setEveryDayOfWeek(true);
        ScheduleExpression scheduleExpression = jobInstanceService.getScheduleExpression(queryScheduler);
        Assert.assertEquals("ScheduleExpression [second=0;minute=0;hour=0;dayOfMonth=*;month=*;dayOfWeek=*;year=*;timezoneID=null;start=null;end=null]",
                scheduleExpression.toString());
    }

    @Test
    public void computeScheduleExpressionWhenEveryDayAt12PMTest() {
        QueryScheduler queryScheduler = mock(QueryScheduler.class);
        QueryTimer timer = new QueryTimer();
        when(queryScheduler.getQueryTimer()).thenReturn(timer);
        when(jobInstanceService.getScheduleExpression(any())).thenCallRealMethod();
        timer.setEveryDayOfWeek(true);
        timer.setHour("12");
        ScheduleExpression scheduleExpression = jobInstanceService.getScheduleExpression(queryScheduler);
        Assert.assertEquals("ScheduleExpression [second=0;minute=0;hour=12;dayOfMonth=*;month=*;dayOfWeek=*;year=*;timezoneID=null;start=null;end=null]",
                scheduleExpression.toString());
    }

    @Test
    public void computeScheduleExpressionWhenEveryDayAt1015DuringTheYear2005Test() {
        QueryScheduler queryScheduler = mock(QueryScheduler.class);
        QueryTimer timer = new QueryTimer();
        when(queryScheduler.getQueryTimer()).thenReturn(timer);
        when(jobInstanceService.getScheduleExpression(any())).thenCallRealMethod();
        timer.setEveryDayOfWeek(true);
        timer.setHour("10");
        timer.setMinute("15");
        timer.setYear("2005");
        ScheduleExpression scheduleExpression = jobInstanceService.getScheduleExpression(queryScheduler);
        Assert.assertEquals("ScheduleExpression [second=0;minute=15;hour=10;dayOfMonth=*;month=*;dayOfWeek=*;year=2005;timezoneID=null;start=null;end=null]",
                scheduleExpression.toString());
    }
}
