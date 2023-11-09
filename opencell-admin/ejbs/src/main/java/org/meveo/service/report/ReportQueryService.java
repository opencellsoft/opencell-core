package org.meveo.service.report;

import static java.lang.Double.valueOf;
import static java.lang.Enum.valueOf;
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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.*;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.generics.PersistenceServiceHelper;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
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
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.FilterConverter;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.communication.impl.InternationalSettingsService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.util.ApplicationProvider;

@Stateless
public class ReportQueryService extends BusinessService<ReportQuery> {

    private static final String SEPARATOR_SELECTED_FIELDS = "\\.";
    
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
    private InternationalSettingsService internationalSettingsService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;
    
    private static final String ROOT_DIR = "reports" + File.separator;
    private static final String DEFAULT_EMAIL_ADDRESS = "no-reply@opencellsoft.com";
    private static final String DELIMITER = ";";
    private static final String REPORT_EXECUTION_FILE_SUFFIX = "YYYYMMdd-HHmmss";
    private static final String SUCCESS_TEMPLATE_CODE = "REPORT_QUERY_RESULT_SUCCESS";
    private static final String FAILURE_TEMPLATE_CODE = "REPORT_QUERY_RESULT_FAILURE";
    private static final String RESULT_EMPTY_MSG = "Execution of the query doesn't return any data";
    private static final String DEFAULT_URI_PATH = "/opencell/frontend/DEMO/portal/operation/query-tool/query-runs-results/";

    /**
     * List of report queries allowed for the current user.
     * return all PUBLIC/ PROTECTED and only PRIVATE created by the user queries
     * If the current user has queryManagement all report queries are returned
     *
     * @param configuration : filtering & pagination configuration used by the query
     * @param currentUser      : current user
     * @return list of ReportQueries
     */
    public List<ReportQuery> reportQueriesAllowedForUser(PaginationConfiguration configuration, MeveoUser currentUser) {
        Map<String, Object> filters = ofNullable(configuration.getFilters()).orElse(new HashMap<>());
        if(!currentUser.hasRole("queryManagement")) {
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
    	Map<String, String> aliases = reportQuery.getAliases() != null ? reportQuery.getAliases() : new HashMap<>();
		for(Object object : result) {
			Map<String, Object> entries = (Map<String, Object>)object;
			List<String> fields = reportQuery.getFields();
			if(reportQuery.getAdvancedQuery() != null && !reportQuery.getAdvancedQuery().isEmpty()) {
				fields = (List<String>) reportQuery.getAdvancedQuery().getOrDefault("genericFields", new ArrayList<String>());
			}
			
			var line = fields.stream().map(f -> aliases.getOrDefault(f, f)).map(e -> entries.getOrDefault((String) e, "") != null ? entries.getOrDefault((String) e, "").toString() : "").collect(Collectors.joining(";"));
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
        Map<String, String> aliases = reportQuery.getAliases() != null ? reportQuery.getAliases() : new HashMap<>();
        List<String> fields;
        if (reportQuery.getAdvancedQuery() != null && !reportQuery.getAdvancedQuery().isEmpty()) {
            fields = (List<String>) reportQuery.getAdvancedQuery().getOrDefault("genericFields", new ArrayList<>());
        } else {
            fields = reportQuery.getFields();
        }
        return mappingColumn(reportQuery.getGeneratedQuery(), fields, aliases).keySet();
    }

    private Map<String, Integer> mappingColumn(String query, List<String> fields, Map<String, String> aliases) {
        Map<String, Integer> result = new HashMap<>();
        for (String col : fields) {
            result.put(col, query.indexOf(col));
        }
        result = result.entrySet().stream().sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(e -> aliases.getOrDefault(e.getKey(), e.getKey()), Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
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

    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;

    @SuppressWarnings("unchecked")
	private List<Object> execute(ReportQuery reportQuery, Class<?> targetEntity, boolean saveQueryResult) {
        Date startDate = new Date();
        List<Object> reportResult;
        List<String> fields;
        if(reportQuery.getAdvancedQuery() != null && !reportQuery.getAdvancedQuery().isEmpty()) {
        	QueryBuilder qb = nativePersistenceService.generatedAdvancedQuery(reportQuery);
    		reportResult = qb.getQuery(PersistenceServiceHelper.getPersistenceService(targetEntity).getEntityManager()).getResultList();
    		fields = (List<String>) reportQuery.getAdvancedQuery().getOrDefault("genericFields", new ArrayList<>());
        } else {
        	reportResult = prepareQueryToExecute(reportQuery, targetEntity).getResultList();
        	fields = reportQuery.getFields();
        }
        
        Date endDate = new Date();
    	Map<String, String> aliases = reportQuery.getAliases() != null ? reportQuery.getAliases() : new HashMap<>();
        if(saveQueryResult)
        	saveQueryResult(reportQuery, startDate, endDate, IMMEDIATE, null, reportResult.size());
		return toExecutionResult(fields, reportResult, targetEntity, aliases);
    }

    /**
     * Asynchronous execution for a specific report query
     *
     * @param reportQuery report query to execute
     * @param targetEntity target entity
     * @param currentUser current user
     * @param emails 
     */
    public void executeAsync(ReportQuery reportQuery, Class<?> targetEntity, MeveoUser currentUser, boolean sendNotification, List<String> emails, UriInfo uriInfo) {
        launchAndForget(reportQuery, targetEntity, currentUser, sendNotification, emails, uriInfo);
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
    public void launchAndForget(ReportQuery reportQuery, Class<?> targetEntity, MeveoUser currentUser, boolean sendNotification, List<String> emails, UriInfo uriInfo) {
        Date startDate = new Date();
        try {
            Future<QueryExecutionResult> asyncResult = executeReportQueryAndSaveResult(reportQuery, targetEntity, startDate);
            QueryExecutionResult executionResult = asyncResult.get();
            if(executionResult != null && sendNotification) {
            	if(currentUser.getEmail() != null) {
                    notifyUser(executionResult.getId(), reportQuery.getCode(), currentUser.getFullNameOrUserName(), currentUser.getEmail(), true,
                            executionResult.getStartDate(), executionResult.getExecutionDuration(),
                            executionResult.getLineCount(), null, uriInfo);
            	}
            	if(emails != null && !emails.isEmpty()) {
                	Set<String> setEmails = new HashSet<String>(emails);
                    for(String email : setEmails) {
                    	if(email != null && !email.equalsIgnoreCase(currentUser.getEmail())) {
                        	notifyUser(executionResult.getId(), reportQuery.getCode(), currentUser.getFullNameOrUserName(), email, true,
                                    executionResult.getStartDate(), executionResult.getExecutionDuration(),
                                    executionResult.getLineCount(), null, uriInfo);
                    	}
                    }
            	}
            }
            
        } catch (InterruptedException | CancellationException e) {
        } catch (Exception exception) {
        	if(sendNotification) {
	            long duration = new Date().getTime() - startDate.getTime();
	            notifyUser(reportQuery.getId(), reportQuery.getCode(), currentUser.getFullNameOrUserName(), currentUser.getEmail(), false,
	                    startDate, duration, null, exception.getMessage(), uriInfo);
        	}
            log.error("Failed to execute async report query", exception);
        }
    }

    private void notifyUser(Long reportQueryId, String reportQueryName, String userName, String userEmail , boolean success,
                            Date startDate, long duration, Integer lineCount, String error, UriInfo uriInfo) {
        String contentHtml = null;
        String content = null;
        String subject;
        String portalResultLink = paramBeanFactory.getInstance().getProperty("portal.host.queryUri", uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), "").concat(DEFAULT_URI_PATH));
        if (portalResultLink.contains(DEFAULT_URI_PATH)) {
        	portalResultLink = portalResultLink.concat(reportQueryId+"/show");
        } else {
        	portalResultLink = portalResultLink.concat(DEFAULT_URI_PATH).concat(reportQueryId+"/show");
        }
       
        Format format = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

        Map<Object, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("reportQueryName", reportQueryName);
        params.put("startDate", format.format(startDate));
        params.put("portalResultLink", portalResultLink);

	    //to arround to the max value : (long) Math.ceil(duration % 1000)
	    long durationSecond = (duration / 1000) + (((long) Math.ceil(duration % 1000)) > 0?1l:0l);
	    
        params.put("duration", String.format("%02d",durationSecond / 3600)+"h "+String.format("%02d",durationSecond / 60 % 60)+"m "+String.format("%02d",durationSecond % 60)+"s");
        try {

            EmailTemplate emailTemplate = success ? emailTemplateService.findByCode(SUCCESS_TEMPLATE_CODE) :
                    emailTemplateService.findByCode(FAILURE_TEMPLATE_CODE);

            String languageCode = appProvider.getLanguage().getLanguageCode();
            String emailSubject = internationalSettingsService.resolveSubject(emailTemplate, languageCode);
            String emailContent = internationalSettingsService.resolveEmailContent(emailTemplate, languageCode);
            String htmlContent = internationalSettingsService.resolveHtmlContent(emailTemplate, languageCode);

            if(success) {
                params.put("lineCount", lineCount);
                contentHtml = evaluateExpression(htmlContent, params, String.class);
                subject = evaluateExpression(emailSubject, params, String.class);
            } else {
                params.put("error", error);
                content = evaluateExpression(emailContent, params, String.class);
                subject = evaluateExpression(emailSubject, params, String.class);
            }
            emailSender.send(ofNullable(appProvider.getEmail()).orElse(DEFAULT_EMAIL_ADDRESS),
                    null, asList(userEmail), subject, content, contentHtml);
        } catch (Exception exception) {
            log.error("Failed to send notification email " + exception.getMessage());
        }
    }

    @Asynchronous
    public Future<QueryExecutionResult> executeReportQueryAndSaveResult(ReportQuery reportQuery,
                                                                             Class<?> targetEntity, Date startDate) {
    	Map<String, String> aliases = reportQuery.getAliases() != null ? reportQuery.getAliases() : new HashMap<>();
        List<String> fields = null;
        List<Object> result = null;
        if(reportQuery.getAdvancedQuery() != null && !reportQuery.getAdvancedQuery().isEmpty()) {
        	QueryBuilder qb = nativePersistenceService.generatedAdvancedQuery(reportQuery);
    		result = qb.getQuery(PersistenceServiceHelper.getPersistenceService(targetEntity).getEntityManager()).getResultList();
    		fields = (List<String>) reportQuery.getAdvancedQuery().getOrDefault("genericFields", new ArrayList<>());
        } else {
        	result = prepareQueryToExecute(reportQuery, targetEntity).getResultList();
        	fields = reportQuery.getFields();
        }
		List<Object> reportResult = toExecutionResult(fields, result, targetEntity, aliases);
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

    public List<Object> toExecutionResult(List<String> fields, List<Object> executionResult, Class<?> targetEntity, Map<String, String> aliases) {
        if(fields != null && !fields.isEmpty()) {
            List<Object> response = new ArrayList<>();
            int size = fields.size();
            if(aliases == null) {
            	aliases = new HashMap<>();
            }
            for (Object result : executionResult) {
                Map<String, Object> item = new LinkedHashMap<>();
                if(fields.size() == 1) {
                    item.put(aliases.getOrDefault(fields.get(0), fields.get(0)), result);
                } else {
                    for (int index = 0; index < size; index++) {
                    	if(result instanceof Object[]) {
                    		item.put(aliases.getOrDefault(fields.get(index), fields.get(index)), ((Object[]) result)[index]);
                    	} else if (targetEntity.isInstance(result)) {
                    	    if(fields.get(index).contains(".") && !nativePersistenceService.isAggregationField(fields.get(index))){
                    	        String[] selected_fields =  fields.get(index).split(SEPARATOR_SELECTED_FIELDS);
                    	        if (selected_fields.length >= 2) {
                    	            processSelectedFields(selected_fields, result, item,  aliases.getOrDefault(fields.get(index), fields.get(index)), targetEntity, 0);
                    	        }
                            }
                    	    else {
                                try {
                                    item.put(aliases.getOrDefault(fields.get(index), fields.get(index)), result);
                                } catch (IllegalArgumentException e) {
                                    log.error("Result construction failed", e);
                                    throw new BusinessException("Result construction failed", e);
                                } 
                            }
                    	}
                    }
                }
                response.add(item);
            }            
            return response;
        } else {
            List<Field> field = getFields(targetEntity);
            if(targetEntity.getSuperclass() != null) {
                field.addAll(getFields(targetEntity.getSuperclass()));
            }
            for (Object item : executionResult) {
                initLazyLoadedValues(field, item);
            }
            return executionResult;
        }
    }
    
    private void processSelectedFields(String[] selectedFields, Object result, Map<String, Object> item, String fieldAlias, Class<?> currentClass, int fieldIndex) {
        if (fieldIndex + 1 == selectedFields.length) {
            item.put(fieldAlias, result != null ? result.toString() : "");
            return;
        }

        Field fieldParent = FieldUtils.getField(currentClass, selectedFields[fieldIndex], true);
        Field field = FieldUtils.getField(fieldParent.getType(), selectedFields[fieldIndex + 1], true);
        if (fieldParent == null) {
            throw new BusinessException("Field Parent not found: " + selectedFields[fieldIndex]);
        }
        if (field == null) {
            throw new BusinessException("Field not found: " + selectedFields[fieldIndex + 1]);
        }
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), fieldParent.getType());
            Object property = null;
            if(fieldIndex == 0){
                result = fieldParent.get(result);
            }            
            if(result == null){
                item.put(fieldAlias, property != null ? property.toString() : "");
            }
            else{
                property = propertyDescriptor.getReadMethod().invoke(result);
                processSelectedFields(selectedFields, property, item, fieldAlias, fieldParent.getType(), fieldIndex + 1);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            log.error("processSelectedFields failed", e);
            throw new BusinessException("processSelectedFields failed", e);
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
                if(property instanceof PersistentSet) {
                    ((PersistentSet) property).removeAll((Collection) property);
                }else if (property instanceof PersistentBag) {
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
        if(reportQuery.getFilters() != null && !reportQuery.getFilters().isEmpty()) {
        	if(reportQuery.getQueryParameters() != null && !reportQuery.getQueryParameters().isEmpty()) {        		
        		result.getParameters().forEach(p -> {
        			result.setParameter(p.getName(), convertParameter(p.getParameterType(), reportQuery.getQueryParameters().get(p.getName())));
        		});
        	} else {
				// For Queries created before INTRD-12720
				FilterConverter converter = new FilterConverter(targetEntity);
				Map<String, Object> filters = converter.convertFilters(reportQuery.getFilters());
				for (Entry<String, Object> entry : filters.entrySet()) {
					if (!(entry.getValue() instanceof Boolean)) {
						if (entry.getKey().length() > 1 && entry.getKey().contains(" ")) {
							String[] compareExpression = entry.getKey().split(" ");
							result.setParameter("a_" + compareExpression[compareExpression.length - 1], entry.getValue());
						} else {
							result.setParameter("a_" + entry.getKey(), entry.getValue());
						}
					}
				}
        	}
        }
        return result;
    }
    
    private Object convertParameter(Class<?> pClazz, Object value) {
    	if(value == null) {
    		return null;
    	}
    	if(Number.class.isAssignableFrom(pClazz)) {
    		return toNumber(pClazz, (String) value);
    	}
    	
    	if (pClazz.isEnum()) {
            return valueOf((Class<Enum>) pClazz, ((String)value).toUpperCase());
        }
    	
    	if(pClazz.isAssignableFrom(Date.class)) {
            try {
				return new SimpleDateFormat("yyyy-MM-dd").parse((String) value);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
        }
        if (Boolean.class.isAssignableFrom(pClazz) || boolean.class.isAssignableFrom(pClazz)) {
            return Boolean.valueOf((String) value);
        }
    	
    	return value;
    }
    
	private Object toNumber(Class<?> pClazz, String value) {
		Method method;
		if (Long.class.isAssignableFrom(pClazz)) {
			return Long.valueOf(value);
		}
		if (BigInteger.class.isAssignableFrom(pClazz)) {
			return BigInteger.valueOf(Long.valueOf(value));
		}
		if (Integer.class.isAssignableFrom(pClazz)) {
			return Integer.valueOf(value);
		}
		Double doubleValue = valueOf(value);
		try {
			method = pClazz.getMethod("valueOf", double.class);
			return method.invoke(pClazz, doubleValue);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new BusinessException(e);
		}
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
        if(currentUser.hasRole("queryManagement")) {
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
