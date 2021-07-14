package org.meveo.service.report;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.base.BusinessService;

@Stateless
public class ReportQueryService extends BusinessService<ReportQuery> {

    /**
     *
     * @param configuration : filtering & pagination configuration used by the query
     * @param userName : current user
     * @return list of ReportQueries
     */
    public List<ReportQuery> reportQueriesAllowedForUser(PaginationConfiguration configuration, String userName) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("SQL", "visibility = 'PRIVATE' OR visibility = 'PUBLIC' OR visibility = 'PROTECTED'");
        filters.put("auditable.creator", userName);
        configuration.setFilters(filters);
        return list(configuration);
    }
    
    @SuppressWarnings("unchecked")
	private List<String> executeQuery(ReportQuery reportQuery) {
    	QueryBuilder queryBuilder = new QueryBuilder(reportQuery.getGeneratedQuery());
    	var query = queryBuilder.getQuery(this.getEntityManager());
    	List<Object[]> result = query.getResultList();
    	List<String> response = new ArrayList<String>();
    	
    	for (Object[] object : result) {
    		var line = "";
    		for (Object value : object) {
				line += value.toString() +";"; 
			}
    		response.add(line);
		}
    	return response;
    }
    
    private static final String RESULT_EMPTY_MSG = "Execution of the query doesn't return any data";

	public byte[] generateCsvFromResultReportQuery(ReportQuery reportQuery, String fileName) throws IOException, BusinessException {
    	return generateFileByExtension(reportQuery, fileName, QueryExecutionResultFormatEnum.CSV);
    }

	public byte[] generateExcelFromResultReportQuery(ReportQuery reportQuery, String fileName) throws IOException, BusinessException {
    	return generateFileByExtension(reportQuery, fileName, QueryExecutionResultFormatEnum.EXCEL);
    }
	
	private byte[] generateFileByExtension(ReportQuery reportQuery, String fileName, QueryExecutionResultFormatEnum format) throws IOException, BusinessException  {
		var columnnHeader = findColumnHeaderForReportQuery(reportQuery);
    	List<String> selectResult = executeQuery(reportQuery);
    	if(selectResult == null || selectResult.isEmpty())
    		throw new BusinessException(RESULT_EMPTY_MSG);
    	Path tempFile = Files.createTempFile(fileName, format.getExtension());
    	try(FileWriter fw = new FileWriter(tempFile.toFile(), true); BufferedWriter bw = new BufferedWriter(fw)){
    		if(format == QueryExecutionResultFormatEnum.CSV) {
	    		bw.write(String.join(";", columnnHeader));
		    	for (String line : selectResult) {
		    		bw.newLine();
		    		bw.write(line);
				}
		    	bw.close();
		    	fw.close();
    		}else if (format == QueryExecutionResultFormatEnum.EXCEL) {
    			var wb = new XSSFWorkbook();
    			XSSFSheet  sheet = wb.createSheet(reportQuery.getTargetEntity());
    			int i = 0;
    			int j = 0;
				var rowHeader = sheet.createRow(i++);
    			for (String header : columnnHeader) {
    				Cell cell = rowHeader.createCell(j++);
    				cell.setCellValue(header);
				}
    			for (String rowSelect : selectResult) {
    				rowHeader = sheet.createRow(i++);
    				j = 0;
    				var splitLine = rowSelect.split(";");
    				for (String field : splitLine) {
    					Cell cell = rowHeader.createCell(j++);
        				cell.setCellValue(field);
					}
				}

    			FileOutputStream fileOut = new FileOutputStream(tempFile.toFile());
    			wb.write(fileOut);
    			fileOut.close();
    			wb.close();
    		}
    	}
    	return Files.readAllBytes(tempFile);
	}
    
    
    private Set<String> findColumnHeaderForReportQuery(ReportQuery reportQuery){
    	return mappingColumn(reportQuery.getGeneratedQuery(), reportQuery.getFields()).keySet();
    }
    
    private Map<String, Integer> mappingColumn(String query, List<String> fields){
    	Map<String, Integer> result = new HashMap<String, Integer>();
    	for (String col : fields) {
    		result.put(col, query.indexOf(col));
		}
    	result =
    			result.entrySet().stream()
     			  .sorted(Map.Entry.comparingByValue())
     			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                      (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    	return result;
    }

	public ReportQuery create(ReportQuery reportQuery, String creator) {
		try {
			ReportQuery entity = (ReportQuery) getEntityManager()
					.createNamedQuery("ReportQuery.ReportQueryByCreatorVisibilityCode")
					.setParameter("code", reportQuery.getCode())
					.setParameter("visibility", reportQuery.getVisibility())
					.getSingleResult();
			if (entity != null) {
				if(entity.getAuditable().getCreator().equals(creator)) {
					throw new BusinessException("Query Already exists and belong to you");
				} else {
					throw new BusinessException("Query Already exists and belong to other user");
				}
			}
		} catch (NoResultException noResultException) {
			super.create(reportQuery);
		}
		return reportQuery;
	}
}