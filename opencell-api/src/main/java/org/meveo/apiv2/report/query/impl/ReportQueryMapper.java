package org.meveo.apiv2.report.query.impl;

import static java.util.Optional.ofNullable;
import static org.meveo.apiv2.report.ImmutableReportQuery.builder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.apiv2.report.ImmutableReportQuery;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.report.query.ReportQuery;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportQueryMapper extends ResourceMapper<org.meveo.apiv2.report.ReportQuery, ReportQuery> {

    @Override
    protected org.meveo.apiv2.report.ReportQuery toResource(ReportQuery entity) {
        try {

            ImmutableReportQuery.Builder builder = builder()
                    .code(entity.getCode())
                    .description(entity.getDescription())
                    .targetEntity(entity.getTargetEntity())
                    .visibility(entity.getVisibility())
                    .generatedQuery(entity.getGeneratedQuery())
                    .sortOrder(entity.getSortOrder())
                    .sortBy(entity.getSortBy())
                    .ownerName(entity.getAuditable().getCreator());
            ofNullable(entity.getFields()).ifPresent(fields -> builder.fields(fields));
            ofNullable(entity.getFilters()).ifPresent(filters -> builder.filters(filters));
            ofNullable(entity.getAdvancedQuery()).ifPresent(aq -> builder.advancedQuery(aq));
            return builder()
                    .from(builder.build())
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
        reportQuery.setFields(resource.getGenericFields());
        reportQuery.setFilters(resource.getFilters());
        reportQuery.setSortBy(resource.getSortBy());
        reportQuery.setSortOrder(resource.getSortOrder());
        reportQuery.setAdvancedQuery(resource.getAdvancedQuery());
        return reportQuery;
    }

    public org.meveo.apiv2.report.ReportQuery toResource(ReportQuery entity, String fieldToFetch) {
        try {
            ImmutableReportQuery.Builder builder = builder();
            List<String> fields = Arrays.stream(fieldToFetch.split(",")).collect(Collectors.toList());
                Class<?> rsClass = Class.forName(ImmutableReportQuery.Builder.class.getName());
                Class<?> entityClass = Class.forName(ReportQuery.class.getName());
                Method method = null;
                for (String field : fields) {
                    while (entityClass != Object.class && method == null) {
                        try {
                            method = entityClass.getDeclaredMethod("get" + StringUtils.capitalizeFirstLetter(field));
                        } catch (NoSuchMethodException exception) {
                            entityClass = entityClass.getSuperclass();
                        }
                    }
                    if(method == null) {
                        throw new BusinessException("Field " + field + " does not exists");
                    }
                    Object value = method.invoke(entity);
                    Class<?> type = ImmutableReportQuery.Builder.class.getDeclaredField(field).getType();
                    method = rsClass.getDeclaredMethod(field, type.isAssignableFrom(List.class) ? Iterable.class : type);
                    if(value != null) {
                        method.invoke(builder, value);
                    }
                    entityClass = Class.forName(ReportQuery.class.getName());
                    method = null;
                }
            return builder()
                    .from(builder.build())
                    .id(entity.getId())
                    .build();
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }
}