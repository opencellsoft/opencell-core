package org.meveo.service.report;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.reverse;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;
import static org.meveo.model.report.query.QueryExecutionModeEnum.BACKGROUND;
import static org.meveo.model.report.query.QueryExecutionModeEnum.IMMEDIATE;
import static org.meveo.model.report.query.QueryStatusEnum.SUCCESS;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.report.query.QueryExecutionModeEnum;
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryStatusEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.util.ApplicationProvider;

@Stateless
public class ReportQueryService extends BusinessService<ReportQuery> {

    @Inject
    private QueryExecutionResultService queryExecutionResultService;

    @Inject
    private EmailSender emailSender;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    private static final String DEFAULT_EMAIL_ADDRESS = "no-reply@opencellsoft.com";
    private static final String DELIMITER = ";";
    private static final String REPORT_EXECUTION_FILE_SUFFIX = "YYYYMMdd-HHmmss";
    private static final String SUCCESS_TEMPLATE_CODE = "REPORT_QUERY_RESULT_SUCCESS";
    private static final String FAILURE_TEMPLATE_CODE = "REPORT_QUERY_RESULT_FAILURE";
    private static final String RESULT_EMPTY_MSG = "Execution of the query doesn't return any data";

    /**
     * List of report queries allowed for the current user.
     * return all PUBLIC/ PROTECTED and only PRIVATE created by the user queries
     *
     * @param configuration : filtering & pagination configuration used by the query
     * @param userName      : current user
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

    /**
     * Synchronous execution for a specific report query
     *
     * @param reportQuery query to execute
     * @param targetEntity target entity
     * @return query result
     */
    public List<Object> execute(ReportQuery reportQuery, Class<?> targetEntity) {
        Date startDate = new Date();
        List<Object> reportResult = prepareQueryToExecute(reportQuery, targetEntity).getResultList();
        Date endDate = new Date();
        saveQueryResult(reportQuery, startDate, endDate, IMMEDIATE, null, reportResult.size());
        return toExecutionResult(reportQuery.getFields(), reportResult);
    }

    /**
     * Asynchronous execution for a specific report query
     *
     * @param reportQuery report query to execute
     * @param targetEntity target entity
     * @param currentUser current user
     */
    public void executeAsync(ReportQuery reportQuery, Class<?> targetEntity, MeveoUser currentUser) {
        launchAndForget(reportQuery, targetEntity, currentUser);
    }

    /**
     * Asynchronous execution for a specific report query
     *
     * @param reportQuery query to execute
     * @param targetEntity target entity
     * @param currentUser current user email to be used for notification
     */
    @Asynchronous
    public void launchAndForget(ReportQuery reportQuery, Class<?> targetEntity, MeveoUser currentUser) {
        Date startDate = new Date();
        try {
            Future<QueryExecutionResult> asyncResult = executeReportQueryAndSaveResult(reportQuery, targetEntity, startDate);
            QueryExecutionResult executionResult = asyncResult.get();
            if(executionResult != null) {
                notifyUser(reportQuery.getCode(), currentUser.getEmail(), currentUser.getFullNameOrUserName(), true,
                        executionResult.getStartDate(), executionResult.getExecutionDuration(),
                        executionResult.getLineCount(), null);
            }
        } catch (InterruptedException | CancellationException e) {
        } catch (Exception exception) {
            long duration = new Date().getTime() - startDate.getTime();
            notifyUser(reportQuery.getCode(), currentUser.getEmail(), currentUser.getFullNameOrUserName(), false,
                    startDate, duration, null, exception.getMessage());
            log.error("Failed to execute async report query", exception);
        }
    }

    private void notifyUser(String reportQueryName, String userEmail, String userName, boolean success,
                            Date startDate, long duration, Integer lineCount, String error) {
        EmailTemplate emailTemplate;
        String content;
        String subject;
        Map<Object, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("reportQueryName", reportQueryName);
        params.put("startDate", startDate);
        Format format = new SimpleDateFormat("HH:mm:ss");
        params.put("duration", format.format(duration));
        if(success) {
            emailTemplate = emailTemplateService.findByCode(SUCCESS_TEMPLATE_CODE);
            params.put("lineCount", lineCount);
            content = evaluateExpression(emailTemplate.getTextContent(), params, String.class);
            subject = emailTemplate.getSubject();

        } else {
            emailTemplate = emailTemplateService.findByCode(FAILURE_TEMPLATE_CODE);
            params.put("error", error);
            content = evaluateExpression(emailTemplate.getTextContent(), params, String.class);
            subject = emailTemplate.getSubject();
        }
        emailSender.send(ofNullable(appProvider.getEmail()).orElse(DEFAULT_EMAIL_ADDRESS),
                null, asList(userEmail), subject, content, null);
    }

    @Asynchronous
    public Future<QueryExecutionResult> executeReportQueryAndSaveResult(ReportQuery reportQuery,
                                                                             Class<?> targetEntity, Date startDate) {
        List<Object> reportResult = toExecutionResult((reportQuery.getFields() != null && !reportQuery.getFields().isEmpty())
                        ? reportQuery.getFields() : joinEntityFields(targetEntity),
                prepareQueryToExecute(reportQuery, targetEntity).getResultList());
        if (!reportResult.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(REPORT_EXECUTION_FILE_SUFFIX);
            StringBuilder fileName = new StringBuilder(dateFormat.format(startDate))
                    .append("_")
                    .append(reportQuery.getCode());
            String fileHeader = ((Map<String, Object>) reportResult.get(0))
                    .entrySet()
                    .stream()
                    .map(Entry::getKey)
                    .collect(joining(DELIMITER));
            List<String> data = reportResult.stream()
                    .map(item -> ((Map<String, Object>) item).entrySet()
                            .stream()
                            .map(Entry::getValue)
                            .map(Object::toString)
                            .collect(joining(DELIMITER)))
                    .collect(toList());
            try {
                createResultFile(data, fileHeader, fileName.toString(), ".csv");
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }
            Date endDate = new Date();
            return new AsyncResult<>(saveQueryResult(reportQuery, startDate, endDate, BACKGROUND, fileName.toString(), data.size()));
        }
        return new AsyncResult<>(null);
    }

    public List<Object> toExecutionResult(List<String> fields, List<Object> executionResult) {
        if(fields != null) {
            List<Object>response = new ArrayList<>();
            int size = fields.size();
            Map<String, Object> item = new HashMap<>();
            for (Object result : executionResult) {
                for (int index = 0; index < size; index++) {
                    item.put(fields.get(index), ((Object[]) result)[index]);
                }
                response.add(item);
            }
            return response;
        } else {
            return executionResult;
        }
    }

    private List<String> joinEntityFields(Class<?> targetEntity) {
        Class<?> entity = targetEntity;
        List<String> fields = new ArrayList<>();
        do {
            fields.add(stream(entity.getDeclaredFields())
                    .filter(field -> (!Modifier.isStatic(field.getModifiers())
                            && field.getDeclaredAnnotation(Transient.class) == null))
                    .map(Field::getName).collect(joining(";")));
            entity = entity.getSuperclass();
        } while (entity != Object.class);
        reverse(fields);
        return fields;
    }

    private Query prepareQueryToExecute(ReportQuery reportQuery, Class<?> targetEntity) {
        PersistenceService persistenceService = (PersistenceService)
                getServiceInterface(targetEntity.getSimpleName() + "Service");
        Query result = persistenceService.getEntityManager().createQuery(reportQuery.getGeneratedQuery());
        FilterConverter converter = new FilterConverter(targetEntity);
        Map<String, Object> filters = converter.convertFilters(reportQuery.getFilters());
        for (Entry<String, Object> entry : filters.entrySet()) {
            if(!(entry.getValue() instanceof Boolean)) {
                result.setParameter("a_" + entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private void createResultFile(List<String> data, String header, String fileName, String extension)
            throws IOException {
        File dir = new File(paramBeanFactory.getChrootDir() + File.separator + "reports" + File.separator);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try(PrintWriter pw = new PrintWriter(dir + File.separator + fileName + extension)) {
            pw.println(header);
            data.stream()
                    .forEach(pw::println);
        }
    }

    private QueryExecutionResult saveQueryResult(ReportQuery reportQuery, Date startDate, Date endDate,
												 QueryExecutionModeEnum executionMode, String filePath, int count) {
        QueryExecutionResult queryExecutionResult = new QueryExecutionResult();
        queryExecutionResult.setReportQuery(reportQuery);
        queryExecutionResult.setStartDate(startDate);
        queryExecutionResult.setEndDate(endDate);
        queryExecutionResult.setExecutionDuration(endDate.getTime() - startDate.getTime());
        queryExecutionResult.setQueryExecutionMode(executionMode);
        queryExecutionResult.setLineCount(count);
        ofNullable(filePath).ifPresent(path -> queryExecutionResult.setFilePath(path));
        queryExecutionResult.setQueryStatus(SUCCESS);
        queryExecutionResultService.create(queryExecutionResult);
        return queryExecutionResult;
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