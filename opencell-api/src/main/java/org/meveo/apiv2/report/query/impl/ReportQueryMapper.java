package org.meveo.apiv2.report.query.impl;

import static org.meveo.apiv2.report.ImmutableReportQuery.builder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.apiv2.report.ImmutableReportQuery;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.report.query.ReportQuery;

public class ReportQueryMapper extends ResourceMapper<org.meveo.apiv2.report.ReportQuery, ReportQuery> {

    @Override
    protected org.meveo.apiv2.report.ReportQuery toResource(ReportQuery entity) {
        try {
            ImmutableReportQuery resource = builder()
                    .code(entity.getCode())
                    .description(entity.getDescription())
                    .targetEntity(entity.getTargetEntity())
                    .visibility(entity.getVisibility())
                    .fields(entity.getFields())
                    .filters(entity.getFilters())
                    .generatedQuery(entity.getGeneratedQuery())
                    .build();
            return builder()
                    .from(resource)
                    .id(entity.getId())
                    .build();
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    @Override
    protected ReportQuery toEntity(org.meveo.apiv2.report.ReportQuery resource) {
        return null;
    }

    public ReportQuery toEntity(ReportQueryInput resource) {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setCode(resource.getQueryName());
        reportQuery.setDescription(resource.getQueryDescription());
        reportQuery.setVisibility(resource.getVisibility());
        reportQuery.setTargetEntity(resource.getTargetEntity());
        reportQuery.setFields(resource.getFields());
        reportQuery.setFilters(resource.getFilters());
        return reportQuery;
    }
}