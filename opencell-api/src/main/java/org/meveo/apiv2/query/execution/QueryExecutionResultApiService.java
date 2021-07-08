package org.meveo.apiv2.query.execution;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.elasticsearch.common.Strings;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.apiv2.query.commons.utils;
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.QueryStatusEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.report.QueryExecutionResultService;

public class QueryExecutionResultApiService implements ApiService<QueryExecutionResult> {

	@Inject
	private QueryExecutionResultService queryExecutionResultService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

	@Override
	public List<QueryExecutionResult> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
	}

	@Override
	public Long getCount(String filter) {
        throw new BadRequestException("Not yet implemented");
	}

	@Override
	public Optional<QueryExecutionResult> findById(Long id) {
		return Optional.ofNullable(queryExecutionResultService.findById(id));
	}

	@Override
	public QueryExecutionResult create(QueryExecutionResult baseEntity) {
        throw new BadRequestException("Not yet implemented");
	}

	@Override
	public Optional<QueryExecutionResult> update(Long id, QueryExecutionResult baseEntity) {
        throw new BadRequestException("Not yet implemented");
	}

	@Override
	public Optional<QueryExecutionResult> patch(Long id, QueryExecutionResult baseEntity) {
        throw new BadRequestException("Not yet implemented");
	}

	@Override
	public Optional<QueryExecutionResult> delete(Long id) {
        throw new BadRequestException("Not yet implemented");
	}

	@Override
	public Optional<QueryExecutionResult> findByCode(String code) {
        throw new BadRequestException("Doesnt have code, please try with id");
	}

	public String convertQueryExectionResultToJson(QueryExecutionResult queryExecutionResult) {
		if(queryExecutionResult.getQueryStatus() == QueryStatusEnum.SUCCESS) {
			if(Strings.isEmpty(queryExecutionResult.getFilePath()))
				throw new BadRequestException("Missing file path");
			var filePath = new File(queryExecutionResult.getFilePath());
			if(!filePath.exists()) 
				throw new BadRequestException("File Path not exist");
			if(!queryExecutionResult.getFilePath().toLowerCase().endsWith("csv")) 
				throw new BadRequestException("Only File CSV format is accepted");
			var result = utils.convertCsvReportToJson(filePath, ";", true);
			return result;
		}
		return null;
	}

}