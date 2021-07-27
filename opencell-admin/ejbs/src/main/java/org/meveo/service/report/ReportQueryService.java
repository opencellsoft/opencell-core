package org.meveo.service.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
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
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryStatusEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.base.BusinessService;

@Stateless
public class ReportQueryService extends BusinessService<ReportQuery> {

    private static final String RESULT_EMPTY_MSG = "Execution of the query doesn't return any data";

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

    private List<String> executeQuery(ReportQuery reportQuery) {
        QueryBuilder queryBuilder = new QueryBuilder(reportQuery.getGeneratedQuery());
        var query = queryBuilder.getQuery(this.getEntityManager());
        List<?> result = query.getResultList();
        List<String> response = new ArrayList<>();

        for (Object object : result) {
            var line = new StringBuilder();

            if (object.getClass().isArray()) {
                for (Object value : (Object[]) object) {
                    line.append(value.toString()).append(";");
                }
            } else {
                line.append(object.toString());
            }

            response.add(line.toString());
        }
        return response;
    }

    /**
     * Execute report query from ReportQueryJob
     * 
     * @param queryResult
     * @param reportQuery
     * @param outputFile
     * @param format
     * @throws IOException
     * @throws BusinessException
     */
    public void executeQuery(QueryExecutionResult queryResult, ReportQuery reportQuery, File outputFile, QueryExecutionResultFormatEnum format)
            throws IOException, BusinessException {

        if (queryResult == null) {
            queryResult = new QueryExecutionResult();
        }

        queryResult.setStartDate(new Date());
        List<String> selectResult = null;
        try {
            selectResult = executeQuery(reportQuery);
        } catch (Exception e) {
            queryResult.setQueryStatus(QueryStatusEnum.ERROR);
            queryResult.setErrorMessage(e.getClass().getSimpleName() + " : " + e.getMessage());
            queryResult.setEndDate(new Date());
            queryResult.setExecutionDuration(queryResult.getEndDate().getTime() - queryResult.getStartDate().getTime());
            return;
        }

        var columnnHeader = findColumnHeaderForReportQuery(reportQuery);
        // Store results in a a report file (excel, csv)
        writeOutputFile(outputFile, format, columnnHeader, selectResult);

        queryResult.setQueryStatus(QueryStatusEnum.SUCCESS);
        queryResult.setEndDate(new Date());
        queryResult.setExecutionDuration(queryResult.getEndDate().getTime() - queryResult.getStartDate().getTime());
        queryResult.setLineCount(selectResult.size());
        queryResult.setFilePath(outputFile.getAbsolutePath());
    }

    public byte[] generateCsvFromResultReportQuery(ReportQuery reportQuery, String fileName) throws IOException, BusinessException {
        return generateFileByExtension(reportQuery, fileName, QueryExecutionResultFormatEnum.CSV);
    }

    public byte[] generateExcelFromResultReportQuery(ReportQuery reportQuery, String fileName) throws IOException, BusinessException {
        return generateFileByExtension(reportQuery, fileName, QueryExecutionResultFormatEnum.EXCEL);
    }

    private byte[] generateFileByExtension(ReportQuery reportQuery, String fileName, QueryExecutionResultFormatEnum format) throws IOException, BusinessException {
        var columnnHeader = findColumnHeaderForReportQuery(reportQuery);
        List<String> selectResult = executeQuery(reportQuery);
        if (selectResult == null || selectResult.isEmpty()) {
            throw new BusinessException(RESULT_EMPTY_MSG);
        }
        Path tempFile = Files.createTempFile(fileName, format.getExtension());
        writeOutputFile(tempFile.toFile(), format, columnnHeader, selectResult);
        return Files.readAllBytes(tempFile);
    }

    private void writeOutputFile(File file, QueryExecutionResultFormatEnum format, Set<String> columnnHeader, List<String> selectResult) throws IOException {

        try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw)) {
            if (format == QueryExecutionResultFormatEnum.CSV) {
                bw.write(String.join(";", columnnHeader));
                for (String line : selectResult) {
                    bw.newLine();
                    bw.write(line);
                }
            } else if (format == QueryExecutionResultFormatEnum.EXCEL) {
                var wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet();
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
                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.close();
                wb.close();
            }
        }
    }

    private Set<String> findColumnHeaderForReportQuery(ReportQuery reportQuery) {
        return mappingColumn(reportQuery.getGeneratedQuery(), reportQuery.getFields()).keySet();
    }

    private Map<String, Integer> mappingColumn(String query, List<String> fields) {
        Map<String, Integer> result = new HashMap<>();
        for (String col : fields) {
            result.put(col, query.indexOf(col));
        }
        result = result.entrySet().stream().sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    public ReportQuery create(ReportQuery reportQuery, String creator) {
        try {
            ReportQuery entity = (ReportQuery) getEntityManager().createNamedQuery("ReportQuery.ReportQueryByCreatorVisibilityCode").setParameter("code", reportQuery.getCode())
                .setParameter("visibility", reportQuery.getVisibility()).getSingleResult();
            if (entity != null) {
                if (entity.getAuditable().getCreator().equals(creator)) {
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