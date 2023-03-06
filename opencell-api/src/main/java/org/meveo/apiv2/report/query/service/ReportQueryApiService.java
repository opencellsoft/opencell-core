package org.meveo.apiv2.report.query.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.meveo.apiv2.generic.core.GenericHelper.getEntityClass;
import static org.meveo.model.report.query.QueryVisibilityEnum.PRIVATE;
import static org.meveo.model.report.query.QueryVisibilityEnum.PROTECTED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;

import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.apiv2.report.VerifyQueryInput;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryVisibilityEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.model.report.query.SortOrderEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.report.ReportQueryService;

public class ReportQueryApiService implements ApiService<ReportQuery> {

    private static final String QUERY_MANAGEMENT_ROLE = "queryManagement";
    private static final String QUERY_USER_ROLE = "queryUser";
    @Inject
    private ReportQueryService reportQueryService;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    private List<String> fetchFields = asList("fields");

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+\\((.*?)\\)");
    
    private static final int EXECUTE = 1;
    private static final int READ = 2;
    private static final int UPDATE = 3;

    @Override
    public List<ReportQuery> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(),
                limit.intValue(), null, filter, fetchFields, orderBy, sort != null ? SortOrder.valueOf(sort) : null);
        return reportQueryService.reportQueriesAllowedForUser(paginationConfiguration, currentUser);
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration =
                new PaginationConfiguration(null, null, null, filter, null, null, null);
        return reportQueryService.count(paginationConfiguration);
    }

    @Override
    public Optional<ReportQuery> findById(Long id) {
    	var reportQuery = reportQueryService.findById(id, fetchFields);
    	if(reportQuery != null)
    		checkPermissionByAction(reportQuery, READ);
        return ofNullable(reportQuery);
    }

    
    private void checkPermissionExist() {
        if (!currentUser.hasRole(QUERY_MANAGEMENT_ROLE) && !currentUser.hasRole(QUERY_USER_ROLE)) {
            throw new BadRequestException("You don't have permission to access report queries");
        }
    }
    private void checkPermissionByAction(ReportQuery report, int action) {
    	checkPermissionExist();
    	if(currentUser.hasRole(QUERY_USER_ROLE)) {
    		if(report.getVisibility() == PRIVATE && !report.getAuditable().getCreator().equals(currentUser.getUserName())) {
	    		if(action == READ) {
	        		throw new BadRequestException("Only Public and Protected query can you see");
	    		}
	    		if(action == EXECUTE) {
	        		throw new BadRequestException("Only Public and Protected query can you execute");
	    		}
    		}
    		if(report.getVisibility() == PROTECTED && !report.getAuditable().getCreator().equals(currentUser.getUserName())) {
    			if(action == UPDATE)
	        		throw new BadRequestException("You don't have permission to update query that belongs to another user.");
    		}
    	}
    }

    @Override
    public ReportQuery create(ReportQuery entity) {
    	checkPermissionExist();
        Class<?> targetEntity = getEntityClass(entity.getTargetEntity());
        try {
        	String generatedQuery;
        	if(entity.getAdvancedQuery() != null) {
        		generatedQuery = nativePersistenceService.generatedAdvancedQuery(entity).getSqlString();
        	} else {
        		generatedQuery = generateQuery(entity, targetEntity);
        	}
            entity.setGeneratedQuery(generatedQuery);
            reportQueryService.create(entity, currentUser.getUserName());
            return entity;
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }
    }

    @SuppressWarnings("rawtypes")
    private String generateQuery(ReportQuery entity, Class<?> targetEntity) {
        PersistenceService persistenceService =
                (PersistenceService) getServiceInterface(targetEntity.getSimpleName());
        QueryBuilder queryBuilder;
        List<String> nestedEntities = entity.getFields().stream().filter(e -> e.contains(".")).map(e -> e.substring(0, e.lastIndexOf("."))).distinct().collect(Collectors.toList());
        if (entity.getFilters() != null) {
            Map<String, Object> filters = new FilterConverter(targetEntity).convertFilters(entity.getFilters());
            queryBuilder = persistenceService.getQuery(new PaginationConfiguration(null, null, filters, null, false, nestedEntities));
        } else {
            queryBuilder = persistenceService.getQuery(new PaginationConfiguration(null, null, null, null, false, nestedEntities));
        }
        String generatedQuery;
        if (entity.getFields() != null && !entity.getFields().isEmpty()) {
            generatedQuery = addFields(queryBuilder.getSqlString(false), entity.getFields(), entity.getSortBy(), entity.getAliases());
        } else {
            generatedQuery = queryBuilder.getSqlString();
        }
//        entity.setFilters(queryBuilder.getParams());
        entity.setQueryParameters(queryBuilder.getParams());
        if(entity.getSortBy() != null) {
            StringBuilder sortOptions = new StringBuilder(" order by ")
                    .append(!entity.getSortBy().isBlank() ? ("a." + entity.getSortBy()) : "a.id")
                    .append(" ")
                    .append(entity.getSortOrder() != null ? entity.getSortOrder().getLabel()
                            : SortOrderEnum.ASCENDING.getLabel());
            return generatedQuery + sortOptions;
        }
        return generatedQuery;
    }
    
    @SuppressWarnings("rawtypes")
    private PersistenceService getServiceInterface(String targetEntityName) {
        if ("EDR".equals(targetEntityName)) {
            targetEntityName = "Edr";
        }
        PersistenceService persistenceService =
                (PersistenceService) EjbUtils.getServiceInterface(targetEntityName + "Service");
        
        if (persistenceService == null) {
            throw new BusinessApiException("Failed to obtain service interface for" + targetEntityName + "Service");
        }

        return persistenceService;
    }

    private String addFields(String query, List<String> fields, String sortBy, Map<String, String> aliases) {
        Set<String> groupByField = new TreeSet<>();
        List<String> aggFields = new ArrayList<>();
        Set<String> fetchJoins = new HashSet<>();
        StringBuilder queryField = new StringBuilder();
        if(aliases == null) {
        	aliases = new HashMap<>();
        }
        for (String field : fields) {
            Matcher matcher = pattern.matcher(field);
            if(matcher.find()) {
                queryField.append(field);
                if(aliases.containsKey(field)) {
                	queryField.append(" as ").append(aliases.get(field));
                }
                aggFields.add(field);
                if (sortBy != null && sortBy.isBlank()) {
                    groupByField.add("id");
                }
            } else {
                queryField.append("a." + field);
                if(aliases.containsKey(field)) {
                	queryField.append(" as ").append(aliases.get(field));
                }
                if(field.contains(".")) {
                	fetchJoins.add(field.substring(0, field.indexOf(".")));
                }
            }
            queryField.append(" ,");
        }
        if (!aggFields.isEmpty()) {
            groupByField.addAll(fields.stream()
                    .filter(element -> !aggFields.contains(element))
                    .collect(Collectors.toSet()));
        }
        StringBuilder generatedQuery = new StringBuilder("select ")
                .append(queryField.deleteCharAt(queryField.length() - 1))
                .append(" ")
                .append(query);
        if(!groupByField.isEmpty()) {
            generatedQuery.append(" group by ");
            generatedQuery.append(groupByField.stream().map(field -> "a." + field).collect(joining(", ")));
        }
        return generatedQuery.toString();
    }

    @Override
    public Optional<ReportQuery> update(Long id, ReportQuery toUpdate) {
        Optional<ReportQuery> reportQuery = findById(id);
        if (!reportQuery.isPresent()) {
            return empty();
        }
        ReportQuery entity = reportQuery.get();
        checkPermissionByAction(reportQuery.get(), UPDATE);

        Class<?> targetEntity = getEntityClass(toUpdate.getTargetEntity());
        ofNullable(toUpdate.getCode()).ifPresent(entity::setCode);
        ofNullable(toUpdate.getVisibility()).ifPresent(entity::setVisibility);
        ofNullable(toUpdate.getTargetEntity()).ifPresent(entity::setTargetEntity);
        entity.setDescription(toUpdate.getDescription());
        entity.setFields(toUpdate.getFields());
        entity.setFilters(toUpdate.getFilters());
        entity.setSortBy(toUpdate.getSortBy());
        entity.setSortOrder(toUpdate.getSortOrder());
        entity.setAdvancedQuery(toUpdate.getAdvancedQuery());
        entity.setAliases(toUpdate.getAliases());
        try {
        	String generatedQuery;
        	if(entity.getAdvancedQuery() != null) {
        		generatedQuery = nativePersistenceService.generatedAdvancedQuery(entity).getSqlString();
        	} else {
        		generatedQuery = generateQuery(entity, targetEntity);
        	}
            entity.setGeneratedQuery(generatedQuery);
            return of(reportQueryService.update(entity));
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage(), exception.getCause());
        }
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
                if(!reportQuery.getAuditable().getCreator().equals(currentUser.getUserName())
                        && reportQuery.getVisibility() == PRIVATE
                        && !currentUser.hasRole(QUERY_MANAGEMENT_ROLE)) {
                    throw new BadRequestException("You don't have permission to delete query that belongs to another user.");
                }
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
    	var reportQuery = reportQueryService.findByCode(code, fetchFields);
    	if(reportQuery != null)
    		checkPermissionByAction(reportQuery, READ);
        return of(reportQuery);
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
     * @param emails 
     */
    public Optional<Object> execute(Long queryId, boolean async, boolean sendNotification, List<String> emails, UriInfo uriInfo) {
    	checkPermissionExist();
        ReportQuery query = findById(queryId).orElseThrow(() ->
                new NotFoundException("Query with id " + queryId + " does not exists"));
        if(!query.getAuditable().getCreator().equals(currentUser.getUserName()) && query.getVisibility() == PRIVATE
                && !currentUser.getRoles().contains(QUERY_MANAGEMENT_ROLE)) {
            throw new BadRequestException("You don't have permission to execute query that belongs to another user.");
        }

        Class<?> targetEntity = getEntityClass(query.getTargetEntity());
        Optional<Object> result;
        if (async) {
            reportQueryService.executeAsync(query, targetEntity, currentUser, sendNotification, emails, uriInfo);
            result = of("Accepted");
        } else {
            result = of(reportQueryService.execute(query, targetEntity));
        }
        return result;
    }

    public ActionStatus verifyReportQuery(VerifyQueryInput verifyQueryInput) {
    	checkPermissionExist();
    	ActionStatus result = new ActionStatus();
        result.setStatus(ActionStatusEnum.SUCCESS);
        result.setMessage("New query");

        if (verifyQueryInput == null) {
            throw new ForbiddenException("The queryName and visibility must be non-null");
        }
        if (StringUtils.isBlank(verifyQueryInput.getQueryName())) {
            throw new ForbiddenException("The queryName parameter is missing.");
        }
        if (StringUtils.isBlank(verifyQueryInput.getQueryName()) || verifyQueryInput.getVisibility() == null) {
            throw new ForbiddenException("The visibility parameter is missing.");
        }

        ReportQuery reportQuery = reportQueryService.findByCodeAndVisibility(verifyQueryInput.getQueryName(), verifyQueryInput.getVisibility());

        if (reportQuery != null) {

            // a query with that name already exists and belongs to user (regardless of visibility)
            if (currentUser.getUserName().equalsIgnoreCase(reportQuery.getAuditable().getCreator())) {
            	result.setStatus(ActionStatusEnum.WARNING);
                result.setMessage("The query already exists and belongs to you");
                return result;
            }
            
            // a public query with that name already exists and belongs to another user
            if (reportQuery.getVisibility() == QueryVisibilityEnum.PUBLIC && !currentUser.getUserName().equalsIgnoreCase(reportQuery.getAuditable().getCreator())) {
                result.setStatus(ActionStatusEnum.WARNING);
                result.setMessage("The query already exists and belongs to another user");
                return result;
            }

            // a protected query with that name already exists and belongs to another user
            if (reportQuery.getVisibility() == QueryVisibilityEnum.PROTECTED && !currentUser.getUserName().equalsIgnoreCase(reportQuery.getAuditable().getCreator())) {
                result.setStatus(ActionStatusEnum.FAIL);
                result.setMessage("The query already exists and belongs to another user");
                return result;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Long countAllowedQueriesForUser() {
        return reportQueryService.countAllowedQueriesForUser(currentUser, Collections.EMPTY_MAP);
    }

    public List<ReportQuery> list(Long offset, Long limit, String sort, String orderBy, String filter, String query) {
        checkPermissionExist();
        Map<String, Object> filters = query != null ? buildFilters(query) : new HashMap<>();
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(),
                limit.intValue(), filters, filter, fetchFields, orderBy, sort != null ? SortOrder.valueOf(sort) : null);
        return reportQueryService.reportQueriesAllowedForUser(paginationConfiguration, currentUser);
    }

    private Map<String, Object> buildFilters(String query) {
        Map<String, Object> filters = new HashMap<>();
        String[] inputFilters = query.split(";");
        for (String input : inputFilters) {
            String[] entry = input.split(":");
            if(entry[0].equals("visibility")) {
                if(entry[1] != null) {
                    filters.put("visibility", QueryVisibilityEnum.valueOf(entry[1]));
                }
            } else {
                filters.put(entry[0], entry[1]);
            }
        }
        return filters;
    }

    public Long countAllowedQueriesForUserWithFilters(String query) {
        Map<String, Object> filters = (query != null && !query.isBlank()) ?
                buildFilters(query) : new HashMap<>();
        return reportQueryService.countAllowedQueriesForUser(currentUser, filters);
    }
}
