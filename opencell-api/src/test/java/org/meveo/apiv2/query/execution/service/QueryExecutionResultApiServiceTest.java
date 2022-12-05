package org.meveo.apiv2.query.execution.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import jakarta.ws.rs.BadRequestException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.apiv2.query.execution.QueryExecutionResultApiService;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.QueryStatusEnum;
import org.meveo.service.report.QueryExecutionResultService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueryExecutionResultApiServiceTest {

    @Spy
    @InjectMocks
    private QueryExecutionResultApiService queryExecutionResultApiService;

    @Mock
    private QueryExecutionResultService queryExecutionResultService;
    
    QueryExecutionResult queryExecutionResult;

    @Mock
    protected ParamBeanFactory paramBeanFactory;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
    	queryExecutionResult = new QueryExecutionResult();
    	queryExecutionResult.setId(1L);
    	queryExecutionResult.setStartDate(new Date());
    	queryExecutionResult.setEndDate(new Date());
    	queryExecutionResult.setFilePath("src/test/resources/query/result_20210707.csv");
    	queryExecutionResult.setLineCount(5);
    	queryExecutionResult.setQueryStatus(QueryStatusEnum.ERROR);
        when(queryExecutionResultService.findById(1L)).thenReturn(queryExecutionResult);
        
    }

    @Test
    public void shouldHaveTheSameFilePath() {
        var optionalCustomQuery = queryExecutionResultApiService.findById(1L);
        assertTrue(optionalCustomQuery.isPresent());
        QueryExecutionResult queryExecutionResult = optionalCustomQuery.get();
        assertEquals("file path not correct", "src/test/resources/query/result_20210707.csv", queryExecutionResult.getFilePath());
    }

    @Test
    public void shouldReturnNullWhenQueryStatusIsFailure() {
        var json = queryExecutionResultApiService.convertQueryExecutionResultToJson(queryExecutionResult);
        assertEquals(null, json);
    }

    @Test
    public void shouldReturnBadRequestWhenFilePathIsEmpty() {
    	queryExecutionResult.setFilePath(null);
    	queryExecutionResult.setQueryStatus(QueryStatusEnum.SUCCESS);
    	queryExecutionResult.setLineCount(100);
    	thrown.expect(BadRequestException.class);
    	thrown.expectMessage("Missing file path");
        queryExecutionResultApiService.convertQueryExecutionResultToJson(queryExecutionResult);
    }

    @Test
    public void shouldReturnBadRequestWhenFilePathDoesntExist() {
    	queryExecutionResult.setFilePath("src/test/resources/query/noFile.csv");
    	queryExecutionResult.setQueryStatus(QueryStatusEnum.SUCCESS);
    	thrown.expect(BadRequestException.class);
    	thrown.expectMessage("File Path not exist");
        queryExecutionResultApiService.convertQueryExecutionResultToJson(queryExecutionResult);
    }

    @Ignore
    @Test
    public void shouldReturnBadRequestWhenFilePathIsNotCSVFile() {
    	queryExecutionResult.setFilePath("src/test/resources/query/result_20210708.txt");
    	queryExecutionResult.setQueryStatus(QueryStatusEnum.SUCCESS);
    	thrown.expect(BadRequestException.class);
    	thrown.expectMessage("Only File CSV format is accepted");
        queryExecutionResultApiService.convertQueryExecutionResultToJson(queryExecutionResult);
    }

    @Ignore
    @Test
    public void shouldReturnJsonResponse() {
    	queryExecutionResult.setFilePath("src/test/resources/query/result_20210707.csv");
    	queryExecutionResult.setQueryStatus(QueryStatusEnum.SUCCESS);
        var json = queryExecutionResultApiService.convertQueryExecutionResultToJson(queryExecutionResult);
        assertEquals(true, json.contains("PDT_1"));
    }
}