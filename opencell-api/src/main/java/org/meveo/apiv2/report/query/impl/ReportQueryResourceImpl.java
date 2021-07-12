package org.meveo.apiv2.report.query.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.query.DownloadReportQueryResponseDto;
import org.meveo.apiv2.report.ImmutableReportQueries;
import org.meveo.apiv2.report.ImmutableReportQuery;
import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.query.execution.QueryExecutionResultApiService;
import org.meveo.apiv2.report.ReportQueries;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.apiv2.report.query.resource.ReportQueryResource;
import org.meveo.apiv2.report.query.service.ReportQueryApiService;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.ReportQuery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javaparser.utils.Log;

public class ReportQueryResourceImpl implements ReportQueryResource {

    @Inject
    private ReportQueryApiService reportQueryApiService;
    @Inject
    private QueryExecutionResultApiService queryExecutionResultApiService;

    private ReportQueryMapper mapper = new ReportQueryMapper();

    @Override
    public Response find(Long id) {
        ReportQuery reportQuery = reportQueryApiService.findById(id)
                .orElseThrow(() -> new NotFoundException("The query with {" + id + "} does not exists"));
        return Response.ok().entity(mapper.toResource(reportQuery)).build();
    }

    @Override
    public Response delete(Long id) {
        if (reportQueryApiService.delete(id).isEmpty()) {
            throw new NotFoundException("The query with {" + id + "} does not exists");
        }
        return null;
    }

    @Override
    public Response getReportQueries(Long offset, Long limit, String sort, String orderBy,
                                     String filter, Request request) {
        List<ReportQuery> reportQueryEntities = reportQueryApiService.list(offset, limit, sort, orderBy, filter);
        EntityTag etag = new EntityTag(Integer.toString(reportQueryEntities.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableReportQuery[] reportQueriesList = reportQueryEntities
                .stream()
                .map(customQuery -> mapper.toResource(customQuery))
                .toArray(ImmutableReportQuery[]::new);
        Long count = Long.valueOf(reportQueryEntities.size());
        ReportQueries reportQueries = ImmutableReportQueries.builder()
                .addData(reportQueriesList)
                .offset(offset)
                .limit(limit)
                .total(count)
                .build()
                .withLinks(new LinkGenerator.PaginationLinkGenerator(ReportQueryResource.class)
                        .offset(offset).limit(limit).total(count).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(reportQueries).build();
    }

    @Override
    public Response createReportQuery(ReportQueryInput resource) {
        ReportQuery entity = reportQueryApiService.create(mapper.toEntity(resource));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ReportQueryResource.class, entity.getId()).build())
                .entity(mapper.toResource(entity))
                .build();
    }

	@Override
	public Response findQueryResult(Long queryexecutionResultId) {
		var queryExecutionResult = queryExecutionResultApiService.findById(queryexecutionResultId)
															.orElseThrow(() -> new NotFoundException("The query execution result with {" + queryexecutionResultId + "} does not exists"));
		var result = queryExecutionResultApiService.convertQueryExectionResultToJson(queryExecutionResult);
		return Response.ok(result != null ? result : "").build();
	}

	@Override
	public Response downloadQueryExecutionResult(Long queryExecutionResultId, QueryExecutionResultFormatEnum format) {
		try {
			
			var queryExecutionResult = reportQueryApiService.findById(queryExecutionResultId)
					.orElseThrow(() -> new NotFoundException("The query execution result with {" + queryExecutionResultId + "} does not exists"));
			DownloadReportQueryResponseDto response = new DownloadReportQueryResponseDto();
				var dateNow = new Date();
				var dateFormat = new SimpleDateFormat("YYMMDD");
				var houreFormat = new SimpleDateFormat("HHmmss");
				var fileName = new StringBuilder(dateFormat.format(dateNow))
													.append("_")
													.append(houreFormat.format(dateNow)).append("_")
													.append(queryExecutionResult.getCode());
				var content = reportQueryApiService.donwloadQueryExecutionResult(queryExecutionResult, format, fileName.toString());
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
}