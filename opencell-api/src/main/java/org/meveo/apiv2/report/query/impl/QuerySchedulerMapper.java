package org.meveo.apiv2.report.query.impl;

import static java.util.Optional.ofNullable;
import static org.meveo.apiv2.report.ImmutableQueryScheduler.builder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.report.QuerySchedulerInput;
import org.meveo.apiv2.report.ImmutableQueryScheduler.Builder;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.ReportQuery;

public class QuerySchedulerMapper extends ResourceMapper<org.meveo.apiv2.report.QueryScheduler, QueryScheduler> {

    @Override
    protected org.meveo.apiv2.report.QueryScheduler toResource(QueryScheduler entity) {
        try {
            Builder builder = builder()
            		.id(entity.getId())
                    .code(entity.getCode())
                    .fileFormat(entity.getFileFormat())
            		.year(entity.getQueryTimer().getYear())
            		.month(entity.getQueryTimer().getMonth())
            		.dayOfMonth(entity.getQueryTimer().getDayOfMonth())
            		.dayOfWeek(entity.getQueryTimer().getDayOfWeek())
            		.hour(entity.getQueryTimer().getHour())
            		.minute(entity.getQueryTimer().getMinute())
            		.second(entity.getQueryTimer().getSecond())
            		.everyMonth(entity.getQueryTimer().isEveryMonth())
            		.everyDayOfMonth(entity.getQueryTimer().isEveryDayOfMonth())
            		.everyDayOfWeek(entity.getQueryTimer().isEveryDayOfWeek())
            		.everyHour(entity.getQueryTimer().isEveryHour())
            		.everyMinute(entity.getQueryTimer().isEveryMinute())
            		.everySecond(entity.getQueryTimer().isEverySecond());
            ofNullable(entity.getUsersToNotify()).ifPresent(usersToNotify -> builder.usersToNotify(usersToNotify));
            ofNullable(entity.getEmailsToNotify()).ifPresent(emailsToNotify -> builder.emailsToNotify(emailsToNotify));
            		
            return builder()
                    .from(builder.build())
                    .id(entity.getId())
                    .build();
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    @Override
    protected QueryScheduler toEntity(org.meveo.apiv2.report.QueryScheduler resource) {
        return null;
    }

    public QueryScheduler toEntity(ReportQuery reportQuery, QuerySchedulerInput resource) {
        QueryScheduler queryScheduler = new QueryScheduler();
        queryScheduler.setReportQuery(reportQuery);
        queryScheduler.setFileFormat(resource.getFileFormat());
        queryScheduler.setUsersToNotify(resource.getUsersToNotify());
        queryScheduler.setEmailsToNotify(resource.getEmailsToNotify());
        queryScheduler.setIsQueryScheduler(Boolean.TRUE.equals(resource.getIsQueryScheduler()));
        queryScheduler.getQueryTimer().setDayOfMonth(resource.getDayOfMonth());
        queryScheduler.getQueryTimer().setDayOfWeek(resource.getDayOfWeek());
        queryScheduler.getQueryTimer().setEveryDayOfMonth(resource.getEveryDayOfMonth());
        queryScheduler.getQueryTimer().setEveryDayOfWeek(resource.getEveryDayOfWeek());
        queryScheduler.getQueryTimer().setEveryHour(resource.getEveryHour());
        queryScheduler.getQueryTimer().setEveryMinute(resource.getEveryMinute());
        queryScheduler.getQueryTimer().setEveryMonth(resource.getEveryMonth());
        queryScheduler.getQueryTimer().setEverySecond(resource.getEverySecond());
        queryScheduler.getQueryTimer().setHour(resource.getHour());
        queryScheduler.getQueryTimer().setMinute(resource.getMinute());
        queryScheduler.getQueryTimer().setSecond(resource.getSecond());
        queryScheduler.getQueryTimer().setMonth(resource.getMonth());
        queryScheduler.getQueryTimer().setYear(resource.getYear());
        return queryScheduler;
    }


}