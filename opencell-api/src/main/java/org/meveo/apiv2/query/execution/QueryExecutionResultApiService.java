package org.meveo.apiv2.query.execution;

import static org.meveo.model.report.query.QueryExecutionModeEnum.IMMEDIATE;
import static org.meveo.model.report.query.QueryStatusEnum.SUCCESS;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.report.QueryExecutionResultService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class QueryExecutionResultApiService implements ApiService<QueryExecutionResult> {

	@Inject
	private QueryExecutionResultService queryExecutionResultService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    @Inject
    protected ParamBeanFactory paramBeanFactory;

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

	public String convertQueryExecutionResultToJson(QueryExecutionResult queryExecutionResult) {
		if (queryExecutionResult.getQueryExecutionMode() == IMMEDIATE) {
			throw new BadRequestException("Result file generation not supported for execution mode IMMEDIATE");
		}
		if(queryExecutionResult.getQueryStatus() == SUCCESS) {
			if (queryExecutionResult.getFilePath() != null && !queryExecutionResult.getFilePath().isEmpty()) {
				var filePath = new File(paramBeanFactory.getDefaultChrootDir() + File.separator + queryExecutionResult.getFilePath());
				if(!filePath.exists())
					throw new BadRequestException("File Path not exist");
				if(!queryExecutionResult.getFilePath().toLowerCase().endsWith("csv"))
					throw new BadRequestException("Only File CSV format is accepted");
				var result = convertCsvReportToJson(filePath, ";", true);
				return result;
			} else {
				if(queryExecutionResult.getLineCount() == 0) {
					throw new BadRequestException("Execution result is available but no file was generated records count = 0");
				}
				if(queryExecutionResult.getLineCount() != 0) {
					throw new BadRequestException("Missing file path");
				}
			}
		}
		return null;
	}
	
	private String convertCsvReportToJson(File input, String separator,boolean hasHeader) {
		try {	  
		 	CsvSchema csvSchema = CsvSchema.builder().setUseHeader(hasHeader).setColumnSeparator(separator.charAt(0)).build();
		    CsvMapper csvMapper = new CsvMapper();
		    csvMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		    List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
		    ObjectMapper mapper = new ObjectMapper();
		    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
		return null;

	}

}