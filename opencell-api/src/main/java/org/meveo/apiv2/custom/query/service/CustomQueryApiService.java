package org.meveo.apiv2.custom.query.service;

import static java.lang.Class.forName;
import static java.util.Arrays.asList;
import static java.util.Optional.*;
import static java.util.stream.Collectors.joining;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.custom.query.CustomQuery;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.custom.CustomQueryService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomQueryApiService implements ApiService<CustomQuery> {

    @Inject
    private CustomQueryService customQueryService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    private List<String> fetchFields = asList("fields");

    @Override
    public List<CustomQuery> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(),
                limit.intValue(), null, filter, fetchFields, null, null);
        return customQueryService.customQueriesAllowedForUser(paginationConfiguration, currentUser.getUserName());
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null,
                null, filter, null, null, null);
        return customQueryService.count(paginationConfiguration);
    }

    @Override
    public Optional<CustomQuery> findById(Long id) {
        return ofNullable(customQueryService.findById(id, fetchFields));
    }

    @Override
    public CustomQuery create(CustomQuery entity) {
        Class<?> targetEntity = validateTargetEntity(entity.getTargetEntity());
        try {
            entity.setGeneratedQuery(generateQuery(entity, targetEntity.getSimpleName()));
            customQueryService.create(entity);
            return entity;
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }
    }

    private Class<?> validateTargetEntity(String targetEntity) {
        try {
            return forName(targetEntity);
        } catch (ClassNotFoundException exception) {
            throw new NotFoundException("Target entity does not exist");
        }
    }

    private String generateQuery(CustomQuery entity, String targetEntity) {
        PersistenceService persistenceService =
                (PersistenceService) getServiceInterface(targetEntity + "Service");
        PaginationConfiguration configuration = new PaginationConfiguration(null);
        QueryBuilder queryBuilder = persistenceService.getQuery(configuration);
        for (Map.Entry<String, String> entry : entity.getFilters().entrySet()) {
            queryBuilder.addCriterion("a." + entry.getKey(), "=", entry.getValue(), false);
        }
        String generatedQuery;
        if (entity.getFields() != null && !entity.getFields().isEmpty()) {
            generatedQuery = addFields(queryBuilder.getSqlString(), entity.getFields());
        } else {
            generatedQuery = queryBuilder.getSqlString();
        }
        if (queryBuilder.getParams() != null) {
            generatedQuery = setParametersValues(new HashMap<>(entity.getFilters()), generatedQuery);
        }
        return generatedQuery;
    }

    private String addFields(String query, List<String> fields) {
        String generatedQuery;
        generatedQuery = new StringBuilder("select ")
                .append(fields.stream().map(field -> "a." + field).collect(joining(", ")))
                .append(" ")
                .append(query)
                .toString();
        return generatedQuery;
    }

    private String setParametersValues(Map<String, Object> params, String query) {
        for (Map.Entry<String, Object> param : params.entrySet()) {
            query = query.replace(":a_" + param.getKey(), "\'" + param.getValue() + "\'");
        }
        return query;
    }

    @Override
    public Optional<CustomQuery> update(Long id, CustomQuery baseEntity) {
        return empty();
    }

    @Override
    public Optional<CustomQuery> patch(Long id, CustomQuery baseEntity) {
        return empty();
    }

    @Override
    public Optional<CustomQuery> delete(Long id) {
        CustomQuery customQuery = customQueryService.findById(id);
        if (customQuery != null) {
            customQueryService.remove(customQuery);
            return of(customQuery);
        }
        return empty();
    }

    @Override
    public Optional<CustomQuery> findByCode(String code) {
        return of(customQueryService.findByCode(code, fetchFields));
    }
}