package org.meveo.apiv2.report.query.impl;

import static java.util.Collections.EMPTY_LIST;
import static org.meveo.apiv2.report.ImmutableExecutionResult.builder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.query.DownloadReportQueryResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.query.execution.QueryExecutionResultApiService;
import org.meveo.apiv2.report.*;
import org.meveo.apiv2.report.query.resource.ReportQueryResource;
import org.meveo.apiv2.report.query.service.QuerySchedulerApiService;
import org.meveo.apiv2.report.query.service.ReportQueryApiService;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.report.ReportQueryService;

@Interceptors({ WsRestApiInterceptor.class })
public class ReportQueryResourceImpl implements ReportQueryResource {

    @Inject
    private ReportQueryApiService reportQueryApiService;
    
    @Inject
    private ReportQueryService reportQueryService;

    @Inject
    private QueryExecutionResultApiService queryExecutionResultApiService;

    @Inject
    private QuerySchedulerApiService querySchedulerApiService;

    private ReportQueryMapper mapper = new ReportQueryMapper();

    private QuerySchedulerMapper queryScheduleMapper = new QuerySchedulerMapper();
    @Inject
    private GenericPagingAndFilteringUtils genericPagingAndFilteringUtils;

    @Override
    public Response find(Long id) {
        ReportQuery reportQuery = reportQueryApiService.findById(id)
                .orElseThrow(() -> new NotFoundException("The query with " + id + " does not exists"));
        return Response.ok().entity(mapper.toResource(reportQuery)).build();
    }

    @Override
    public Response delete(Long id) {
        ReportQuery reportQuery = reportQueryApiService.delete(id)
                .orElseThrow(() -> new NotFoundException("The query with id " + id + " does not exists"));
        return Response.ok(ImmutableSuccessResponse.builder()
                .status("SUCCESS")
                .message("The query with name " + reportQuery.getCode() + " is successfully deleted")
                .build()).build();
    }

    @Override
    public Response getReportQueries(Long offset, Long limit, String sort, String orderBy, String filter,
                                     String query, String fields, Request request) {

        long apiLimit = genericPagingAndFilteringUtils.getLimit(limit != null ? limit.intValue() : null);
        List<ReportQuery> reportQueryEntities =
                reportQueryApiService.list(offset, apiLimit, sort, orderBy, filter, query);
        EntityTag etag = new EntityTag(Integer.toString(reportQueryEntities.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        Long count = reportQueryApiService.countAllowedQueriesForUserWithFilters(query);
        ImmutableReportQuery[] reportQueriesList = reportQueryEntities
                .stream()
                .map(reportQuery -> {
                	reportQuery = reportQueryService.findById(reportQuery.getId(),Arrays.asList("fields"));
                    if(fields != null && !fields.isEmpty()) {
                        return mapper.toResource(reportQuery, fields);
                    } else {
                        return mapper.toResource(reportQuery);
                    }
                })
                .toArray(ImmutableReportQuery[]::new);
        ImmutableReportQueries reportQueries = ImmutableReportQueries.builder()
                .offset(offset)
                .addData(reportQueriesList)
                .limit(apiLimit)
                .total(count)
                .build()
                .withLinks(new LinkGenerator.PaginationLinkGenerator(ReportQueryResource.class)
                .offset(offset).limit(apiLimit).total(count).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(reportQueries).build();
    }

    @Override
    public Response createReportQuery(ReportQueryInput resource) {
        validateResource(resource);
        ReportQuery entity = reportQueryApiService.create(mapper.toEntity(resource));
        return Response
                .ok(LinkGenerator.getUriBuilderFromResource(ReportQueryResource.class, entity.getId()).build())
                .entity(mapper.toResource(entity))
                .build();
    }

    private void validateResource(ReportQueryInput resource) {
        if (resource.getQueryName() == null) {
            throw new BadRequestException("Report query name is missing");
        }
        if (resource.getTargetEntity() == null) {
            throw new BadRequestException("Target entity is missing");
        }
        if (resource.getVisibility() == null) {
            throw new BadRequestException("Report query visibility is missing");
        }
    }

	@Override
	public Response findQueryResult(Long queryExecutionResultId) {
		var queryExecutionResult = queryExecutionResultApiService.findById(queryExecutionResultId)
															.orElseThrow(() -> new NotFoundException("The query execution result with {" + queryExecutionResultId + "} does not exists"));
		var result = queryExecutionResultApiService.convertQueryExecutionResultToJson(queryExecutionResult);
		return Response.ok(result != null ? result : "").build();
	}

	@Override
	public Response downloadQueryExecutionResult(Long queryExecutionResultId, QueryExecutionResultFormatEnum format) {
		try {
			
			var queryExecutionResult = reportQueryApiService.findById(queryExecutionResultId)
					.orElseThrow(() -> new NotFoundException("The query execution result with {" + queryExecutionResultId + "} does not exists"));
			DownloadReportQueryResponseDto response = new DownloadReportQueryResponseDto();
				var dateNow = new Date();
				var dateFormat = new SimpleDateFormat("yyyyMMdd");
				var houreFormat = new SimpleDateFormat("HHmmss");
				var fileName = new StringBuilder(dateFormat.format(dateNow))
													.append("_")
													.append(houreFormat.format(dateNow)).append("_")
													.append(queryExecutionResult.getCode());
				var content = reportQueryApiService.downloadQueryExecutionResult(queryExecutionResult, format, fileName.toString());
				fileName.append(format.getExtension());
				if(content != null) {
					response.setReportContent(content);
				}
				if(format == QueryExecutionResultFormatEnum.CSV)
					response.setFileName(fileName.toString());
				else if (format == QueryExecutionResultFormatEnum.EXCEL)
					response.setFileName(fileName.toString());
			return Response.ok(response).build();
		}catch(IOException e) {
			throw new BadRequestException(e.getMessage());
		}catch(BusinessException e) {
			throw new BadRequestException(e.getMessage());
		}
	}
	
	@Transactional
	@Override
	public Response createQueryScheduler(Long reportId, QuerySchedulerInput queryScheduler) {
		ReportQuery reportQuery = reportQueryApiService.findById(reportId)
                .orElseThrow(() -> new NotFoundException("The query with {" + reportId + "} does not exists"));
		QueryScheduler entity = querySchedulerApiService.create(queryScheduleMapper.toEntity(reportQuery, queryScheduler));

        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ReportQueryResource.class, entity.getId()).build())
                .entity(queryScheduleMapper.toResource(entity))
                .build();
	}

    @Override
    public Response execute(Long id, boolean async, boolean sendNotification, ReportQueryInput resource, UriInfo uriInfo) {
        List<String> emails = new ArrayList<String>();
        if(resource != null) {
            emails = resource.getEmails();
        } 
        if(async) {
            reportQueryApiService.execute(id, async, sendNotification, emails, uriInfo);
            return Response.ok().entity(ImmutableSuccessResponse.builder()
                    .status("ACCEPTED")
                    .message("Execution request accepted")
                    .build()).build();
        } else {
            List<Object> result = (List<Object>) reportQueryApiService.execute(id, async, sendNotification, emails, uriInfo).orElse(EMPTY_LIST);
            ExecutionResult executionResult = builder()
                    .executionResults(result)
                    .total(result.size())
                    .build();
            return Response.ok().entity(executionResult).build();
        }
    }

    @Override
    public Response verifyReportQuery(VerifyQueryInput verifyQueryInput) {
    	 ActionStatus result=reportQueryApiService.verifyReportQuery(verifyQueryInput);
        return Response.ok(result).build();
    }

    @Override
    public Response update(Long id, ReportQueryInput resource) {
        validateResource(resource);
        ReportQuery entity = reportQueryApiService.update(id, mapper.toEntity(resource))
                .orElseThrow(() -> new NotFoundException("The query with id " + id + " does not exists"));
        return Response
                .ok(LinkGenerator.getUriBuilderFromResource(ReportQueryResource.class, entity.getId()).build())
                .entity(mapper.toResource(entity))
                .build();
    }
}