package org.meveo.apiv2.report.query.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.meveo.apiv2.generic.core.GenericHelper.getEntityClass;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.apiv2.generic.exception.UnprocessableEntityException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.apiv2.report.VerifyQueryInput;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryVisibilityEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.report.ReportQueryService;
import org.primefaces.model.SortOrder;

public class ReportQueryApiService implements ApiService<ReportQuery> {

    @Inject
    private ReportQueryService reportQueryService;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    private List<String> fetchFields = asList("fields");

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+\\((.*?)\\)");

    @Override
    public List<ReportQuery> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(),
                limit.intValue(), null, filter, fetchFields, orderBy, sort != null ? SortOrder.valueOf(sort) : null);
        return reportQueryService.reportQueriesAllowedForUser(paginationConfiguration, currentUser.getUserName());
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration =
                new PaginationConfiguration(null, null, null, filter, null, null, null);
        return reportQueryService.count(paginationConfiguration);
    }

    @Override
    public Optional<ReportQuery> findById(Long id) {
        return ofNullable(reportQueryService.findById(id, fetchFields));
    }

    @Override
    public ReportQuery create(ReportQuery entity) {
        Class<?> targetEntity = getEntityClass(entity.getTargetEntity());
        try {
            entity.setGeneratedQuery(generateQuery(entity, targetEntity));
            reportQueryService.create(entity, currentUser.getUserName());
            return entity;
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }
    }

    private String generateQuery(ReportQuery entity, Class<?> targetEntity) {
        PersistenceService persistenceService = (PersistenceService) getServiceInterface(targetEntity.getSimpleName() + "Service");
        QueryBuilder queryBuilder;
        if (entity.getFilters() != null) {
            Map<String, Object> filters = new FilterConverter(targetEntity).convertFilters(entity.getFilters());
            queryBuilder = persistenceService.getQuery(new PaginationConfiguration(filters));
        } else {
            queryBuilder = persistenceService.getQuery(new PaginationConfiguration(null));
        }
        String generatedQuery;
        if (entity.getFields() != null && !entity.getFields().isEmpty()) {
            generatedQuery = addFields(queryBuilder.getSqlString(), entity.getFields());
        } else {
            generatedQuery = queryBuilder.getSqlString();
        }
        if(entity.getSortBy() != null && entity.getSortOrder() != null) {
            StringBuilder sortOptions = new StringBuilder(" order by ")
                    .append(entity.getSortBy())
                    .append(" ")
                    .append(entity.getSortOrder().getLabel());
            return generatedQuery.replaceAll("\\s*\\blower\\b\\s*", " ") + sortOptions;
        }
        return generatedQuery.replaceAll("\\s*\\blower\\b\\s*", " ");
    }

    private String addFields(String query, List<String> fields) {
        List<String> groupByField = new ArrayList<>();
        StringBuilder queryField = new StringBuilder();
        for (String field : fields) {
            Matcher matcher = pattern.matcher(field);
            if(matcher.find()) {
                queryField.append(field);
            } else {
                queryField.append("a." + field);
                groupByField.add(field);
            }
            queryField.append(" ,");
        }
        StringBuilder generatedQuery = new StringBuilder("select ")
                .append(queryField.deleteCharAt(queryField.length() - 1))
                .append(" ")
                .append(query);
        if(fields.size() != groupByField.size()) {
            generatedQuery
                    .append(" group by ")
                    .append(groupByField.stream().map(field -> "a." + field).collect(joining(", ")));
        }
        return generatedQuery.toString();
    }

    @Override
    public Optional<ReportQuery> update(Long id, ReportQuery baseEntity) {
        return empty();
    }

    @Override
    public Optional<ReportQuery> patch(Long id, ReportQuery baseEntity) {
        return empty();
    }

    @Override
    public Optional<ReportQuery> delete(Long id) {
        ReportQuery reportQuery = reportQueryService.findById(id);
        if (reportQuery != null) {
            try {
                reportQueryService.remove(reportQuery);
                return of(reportQuery);
            } catch (Exception exception) {
                Throwable throwable = exception.getCause();
                while (throwable != null) {
                    if (throwable instanceof ConstraintViolationException) {
                        throw new BusinessException("The query with id "+ id + " is referenced");
                    }
                    throwable = throwable.getCause();
                }
                throw new BusinessException(exception.getMessage());
            }
        }
        return empty();
    }

    @Override
    public Optional<ReportQuery> findByCode(String code) {
        return of(reportQueryService.findByCode(code, fetchFields));
    }

	public byte[] downloadQueryExecutionResult(ReportQuery reportQuery, QueryExecutionResultFormatEnum format, String fileName) throws IOException, BusinessException{
        Class<?> targetEntity = getEntityClass(reportQuery.getTargetEntity());
        if (format == QueryExecutionResultFormatEnum.CSV) {
            return reportQueryService.generateCsvFromResultReportQuery(reportQuery, fileName, targetEntity);
        } else if (format == QueryExecutionResultFormatEnum.EXCEL) {
            return reportQueryService.generateExcelFromResultReportQuery(reportQuery, fileName, targetEntity);
        }
        return null;
    }

    /**
     *
     * @param queryId report query Id
     * @param async execution type; by default false true : asynchronous execution false : synchronous execution
     */
    public Optional<Object> execute(Long queryId, boolean async) {
        ReportQuery query = findById(queryId).orElseThrow(() ->
                new NotFoundException("Query with id " + queryId + " does not exists"));
        Class<?> targetEntity = getEntityClass(query.getTargetEntity());
        Optional<Object> result;
        if (async) {
            reportQueryService.executeAsync(query, targetEntity, currentUser);
            result = of("Accepted");
        } else {
            result = of(reportQueryService.execute(query, targetEntity));
        }
        return result;
    }

    public void verifyReportQuery(VerifyQueryInput verifyQueryInput) {

        if (verifyQueryInput == null) {
            throw new ForbiddenException("The queryName and visibility must be non-null");
        }
        if (StringUtils.isBlank(verifyQueryInput.getQueryName())) {
            throw new ForbiddenException("The queryName parameter is missing.");
        }
        if (verifyQueryInput == null || StringUtils.isBlank(verifyQueryInput.getQueryName()) || verifyQueryInput.getVisibility() == null) {
            throw new ForbiddenException("The visibility parameter is missing.");
        }

        ReportQuery reportQuery = reportQueryService.findByCodeAndVisibility(verifyQueryInput.getQueryName(), verifyQueryInput.getVisibility());

        if (reportQuery != null) {

            // a query with that name already exists and belongs to user (regardless of visibility)
            if (currentUser.getUserName().equalsIgnoreCase(reportQuery.getAuditable().getCreator())) {
                throw new ConflictException("The query already exists and belong you");
            }
            
            // a public query with that name already exists and belongs to another user
            if (reportQuery.getVisibility() == QueryVisibilityEnum.PUBLIC && !currentUser.getUserName().equalsIgnoreCase(reportQuery.getAuditable().getCreator())) {
                throw new ConflictException("The query already exists and belongs to another user");
            }

            // a protected query with that name already exists and belongs to another user
            if (reportQuery.getVisibility() == QueryVisibilityEnum.PROTECTED && !currentUser.getUserName().equalsIgnoreCase(reportQuery.getAuditable().getCreator())) {
                throw new UnprocessableEntityException("The query already exists and belong you");
            }
        }
    }

    public Long countAllowedQueriesForUser() {
        return reportQueryService.countAllowedQueriesForUser(currentUser.getUserName());
    }
}