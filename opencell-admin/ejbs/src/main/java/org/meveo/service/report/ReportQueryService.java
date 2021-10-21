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
import static org.meveo.model.report.query.QueryVisibilityEnum.PRIVATE;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.report.query.QueryExecutionModeEnum;
import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.QueryStatusEnum;
import org.meveo.model.report.query.QueryVisibilityEnum;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.util.ApplicationProvider;

@Stateless
public class ReportQueryService extends BusinessService<ReportQuery> {

    @Inject
    private QueryExecutionResultService queryExecutionResultService;
    @Inject
    private QuerySchedulerService querySchedulerService;

    @Inject
    private EmailSender emailSender;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    private static final String ROOT_DIR = "reports" + File.separator;
    private static final String DEFAULT_EMAIL_ADDRESS = "no-reply@opencellsoft.com";
    private static final String DELIMITER = ";";
    private static final String REPORT_EXECUTION_FILE_SUFFIX = "YYYYMMdd-HHmmss";
    private static final String SUCCESS_TEMPLATE_CODE = "REPORT_QUERY_RESULT_SUCCESS";
    private static final String FAILURE_TEMPLATE_CODE = "REPORT_QUERY_RESULT_FAILURE";
    private static final String RESULT_EMPTY_MSG = "Execution of the query doesn't return any data";

    /**
     * List of report queries allowed for the current user.
     * return all PUBLIC/ PROTECTED and only PRIVATE created by the user queries
     * If the current user has query_manager all report queries are returned
     *
     * @param configuration : filtering & pagination configuration used by the query
     * @param currentUser      : current user
     * @return list of ReportQueries
     */
    public List<ReportQuery> reportQueriesAllowedForUser(PaginationConfiguration configuration, MeveoUser currentUser) {
        Map<String, Object> filters = ofNullable(configuration.getFilters()).orElse(new HashMap<>());
        if(!currentUser.getRoles().contains("query_manager")) {
            configuration.setFilters(createQueryFilters(currentUser.getUserName(), filters));
        }
        return list(configuration);
    }

    private Map<String, Object> createQueryFilters(String userName, Map<String, Object> filters) {
        if(filters.containsKey("visibility")) {
            if(filters.get("visibility").equals(PRIVATE)) {
                filters.put("auditable.creator", userName);
            }
        } else {
            filters.put("SQL", "((a.auditable.creator = '"
                    + userName + "' AND a.visibility = 'PRIVATE') OR ( a.visibility = 'PUBLIC' OR a.visibility = 'PROTECTED'))");
        }
        return filters;
    }

    @Transactional
	@SuppressWarnings("unchecked")
	private List<String> executeQuery(ReportQuery reportQuery, Class<?> targetEntity) {
    	List<Object> result = execute(reportQuery, targetEntity, false);
    	List<String> response = new ArrayList<>();
		for(Object object : result) {
    		var line = "";
				Map<String, Object> entries = (Map<String, Object>)object;
				for (Object entry : entries.values()) {
					if(entry != null)
						line += entry.toString() +";"; 
					else
						line += ";";
				}
    		response.add(line);
		}
    	return response;
    }
    

	public byte[] generateCsvFromResultReportQuery(ReportQuery reportQuery, String fileName, Class<?> targetEntity) throws IOException, BusinessException {
    	return generateFileByExtension(reportQuery, fileName, QueryExecutionResultFormatEnum.CSV, targetEntity);
    }

	public byte[] generateExcelFromResultReportQuery(ReportQuery reportQuery, String fileName, Class<?> targetEntity) throws IOException, BusinessException {
    	return generateFileByExtension(reportQuery, fileName, QueryExecutionResultFormatEnum.EXCEL, targetEntity);
    }
	
	@Transactional
	private byte[] generateFileByExtension(ReportQuery reportQuery, String fileName, QueryExecutionResultFormatEnum format, Class<?> targetEntity) throws IOException, BusinessException  {
		var columnnHeader = findColumnHeaderForReportQuery(reportQuery);
    	List<String> selectResult = executeQuery(reportQuery, targetEntity);
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
                    XSSFSheet sheet = wb.createSheet(reportQuery.getTargetEntity());
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
            Map<String, Class> entitiesByName  = ReflectionUtils.getClassesAnnotatedWith(Entity.class).stream().collect(Collectors.toMap(clazz -> clazz.getSimpleName().toLowerCase(), clazz -> clazz));
            selectResult = executeQuery(reportQuery, entitiesByName.get(reportQuery.getTargetEntity().toLowerCase()));
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
        queryResult.setFilePath(outputFile.getParentFile().getName() +  File.separator + outputFile.getName());
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
        return execute(reportQuery, targetEntity, true);
    }


    @SuppressWarnings("unchecked")
	private List<Object> execute(ReportQuery reportQuery, Class<?> targetEntity, boolean saveQueryResult) {
        Date startDate = new Date();
        List<Object> reportResult = prepareQueryToExecute(reportQuery, targetEntity).getResultList();
        Date endDate = new Date();
        if(saveQueryResult)
        	saveQueryResult(reportQuery, startDate, endDate, IMMEDIATE, null, reportResult.size());
        return toExecutionResult(reportQuery.getFields(), reportResult, targetEntity);
    }

    /**
     * Asynchronous execution for a specific report query
     *
     * @param reportQuery report query to execute
     * @param targetEntity target entity
     * @param currentUser current user
     * @param emails 
     */
    public void executeAsync(ReportQuery reportQuery, Class<?> targetEntity, MeveoUser currentUser, boolean sendNotification, List<String> emails) {
        launchAndForget(reportQuery, targetEntity, currentUser, sendNotification, emails);
    }

    /**
     * Asynchronous execution for a specific report query
     *
     * @param reportQuery query to execute
     * @param targetEntity target entity
     * @param currentUser current user email to be used for notification
     * @param emails 
     */
    @Asynchronous
    public void launchAndForget(ReportQuery reportQuery, Class<?> targetEntity, MeveoUser currentUser, boolean sendNotification, List<String> emails) {
        Date startDate = new Date();
        try {
            Future<QueryExecutionResult> asyncResult = executeReportQueryAndSaveResult(reportQuery, targetEntity, startDate);
            QueryExecutionResult executionResult = asyncResult.get();
            if(executionResult != null && sendNotification) {
                notifyUser(reportQuery.getCode(), currentUser.getEmail(), currentUser.getFullNameOrUserName(), true,
                        executionResult.getStartDate(), executionResult.getExecutionDuration(),
                        executionResult.getLineCount(), null);
            }
            for(String email : emails) {
            	notifyUser(reportQuery.getCode(), email, currentUser.getFullNameOrUserName(), true,
                        executionResult.getStartDate(), executionResult.getExecutionDuration(),
                        executionResult.getLineCount(), null);
            }
        } catch (InterruptedException | CancellationException e) {
        } catch (Exception exception) {
        	if(sendNotification) {
	            long duration = new Date().getTime() - startDate.getTime();
	            notifyUser(reportQuery.getCode(), currentUser.getEmail(), currentUser.getFullNameOrUserName(), false,
	                    startDate, duration, null, exception.getMessage());
        	}
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
        try {
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
        } catch (Exception exception) {
            log.error("Failed to send notification email " + exception.getMessage());
        }
    }

    @Asynchronous
    public Future<QueryExecutionResult> executeReportQueryAndSaveResult(ReportQuery reportQuery,
                                                                             Class<?> targetEntity, Date startDate) {
        List<Object> reportResult = toExecutionResult((reportQuery.getFields() != null && !reportQuery.getFields().isEmpty())
                        ? reportQuery.getFields() : joinEntityFields(targetEntity),
                prepareQueryToExecute(reportQuery, targetEntity).getResultList(), targetEntity);
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
                            .map(String::valueOf)
                            .collect(joining(DELIMITER)))
                    .collect(toList());
            String fullFileName = fileName.toString();
            try {
               createResultFile(data, fileHeader, fullFileName, ".csv");
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }
            return new AsyncResult<>(saveQueryResult(reportQuery, startDate, new Date(), BACKGROUND, ROOT_DIR + fullFileName+".csv", data.size()));
        } else {
            return new AsyncResult<>(saveQueryResult(reportQuery, startDate, new Date(), BACKGROUND, null, 0));
        }
    }

    public List<Object> toExecutionResult(List<String> fields, List<Object> executionResult, Class<?> targetEntity) {
        if(fields != null && !fields.isEmpty()) {
            List<Object> response = new ArrayList<>();
            int size = fields.size();
            for (Object result : executionResult) {
                Map<String, Object> item = new HashMap<>();
                if(fields.size() == 1) {
                    item.put(fields.get(0), result);
                } else {
                    for (int index = 0; index < size; index++) {
                        item.put(fields.get(index), ((Object[]) result)[index]);
                    }
                }
                response.add(item);
            }
            for (Object item : response) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>)item).entrySet()) {
                    if(entry.getValue() != null) {
                        List<Field> field = getFields(entry.getValue().getClass());
                        initLazyLoadedValues(field, entry.getValue());
                    }
                }
            }
            return response;
        } else {
            List<Field> field = getFields(targetEntity);
            for (Object item : executionResult) {
                getEntityManager().detach(item);
                initLazyLoadedValues(field, item);
            }
            return executionResult;
        }
    }

    private List<Field> getFields(Class<?> targetEntity) {
        return Arrays.stream(targetEntity.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toList());
    }

    private void initLazyLoadedValues(List<Field> fields, Object item) {
        for (Field field : fields) {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), item.getClass());
                Object property = propertyDescriptor.getReadMethod().invoke(item);
                if(property instanceof PersistentSet || property instanceof PersistentBag) {
                    ((PersistentBag) property).removeAll((Collection) property);
                }else if (property instanceof HibernateProxy) {
                    Hibernate.initialize(property);
                    Object implementation = ((HibernateProxy)property).getHibernateLazyInitializer().getImplementation();
                    propertyDescriptor.getWriteMethod().invoke(item, implementation);
                }
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
                //e.printStackTrace();
                log.warn("Error initLazyLoadedValues() for field : {}", field.getName());
            }
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
        if (reportQuery.getFilters() != null && !reportQuery.getFilters().isEmpty()) {
            FilterConverter converter = new FilterConverter(targetEntity);
            Map<String, Object> filters = converter.convertFilters(reportQuery.getFilters());
            for (Entry<String, Object> entry : filters.entrySet()) {
                if(!(entry.getValue() instanceof Boolean)) {
                    if(entry.getKey().length()>1 && entry.getKey().contains(" ")){
                        String[] compareExpression = entry.getKey().split(" ");
                        result.setParameter("a_" + compareExpression[compareExpression.length-1], entry.getValue());
                    }else{
                        result.setParameter("a_" + entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return result;
    }

    private String createResultFile(List<String> data, String header, String fileName, String extension)
            throws IOException {
        File dir = new File(paramBeanFactory.getChrootDir() + File.separator + ROOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = dir + File.separator + fileName + extension;
        try(PrintWriter pw = new PrintWriter(filePath)) {
            pw.println(header);
            data.stream()
                    .forEach(pw::println);
        }
        return filePath;
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
            ReportQuery entity = (ReportQuery) getEntityManager()
                    .createNamedQuery("ReportQuery.ReportQueryByCreatorVisibilityCode")
                    .setParameter("code", reportQuery.getCode())
                    .setParameter("visibility", reportQuery.getVisibility())
                    .getSingleResult();
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

    /**
     * find by code and visibility
     * 
     * @param code
     * @param visibility
     * @return
     */
    public ReportQuery findByCodeAndVisibility(String code, QueryVisibilityEnum visibility) {
        if (code == null) {
            return null;
        }

        TypedQuery<ReportQuery> query = getEntityManager().createQuery("select r from ReportQuery r where lower(r.code)=:code and visibility=:visibility", entityClass)
            .setParameter("code", code.toLowerCase())
            .setParameter("visibility", visibility)
            .setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No ReportQuery of code {} and visibility {} found", entityClass.getSimpleName(), code, visibility);
            return null;
        }
    }

    /**
     * Report queries count allowed for current user.
     *
     * @param currentUser   : current user
     * @param filters       : filters
     * @return number of ReportQueries
     */
    public Long countAllowedQueriesForUser(MeveoUser currentUser, Map<String, Object> filters) {
        if(currentUser.getRoles().contains("query_manager")) {
                return count(new PaginationConfiguration(filters));
        } else {
            return count(new PaginationConfiguration(createQueryFilters(currentUser.getUserName(), filters)));
        }
    }
    public void remove(ReportQuery reportQuery) {
        try {
            QueryScheduler queryScheduler = querySchedulerService.findByReportQuery(reportQuery);
            if (queryScheduler != null) {
                Optional.ofNullable(queryScheduler.getJobInstance()).ifPresent(jobInstanceService::remove);
                querySchedulerService.remove(queryScheduler);
            }
            List<Long> resultsIds = queryExecutionResultService.findByReportQuery(reportQuery);
            if (!resultsIds.isEmpty()) {
                queryExecutionResultService.remove(resultsIds.parallelStream().collect(Collectors.toSet()));
            }
            reportQuery.setFields(Collections.emptyList());
            super.remove(reportQuery);
        } catch (Exception e) {
            throw new BusinessException("An exception is happened : " + e.getMessage());
        }
    }
}