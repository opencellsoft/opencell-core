package org.meveo.service.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.SQLQuery;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.customEntities.CustomTableRecord;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

@Stateless
public class CustomTableService extends NativePersistenceService {

    /**
     * File prefix indicating that imported data should be appended to exiting data
     */
    public static final String FILE_APPEND = "_append";

    /**
     * File prefix indicating that file contains 'id' field
     */
    public static final CharSequence FILE_INCLUDES_ID = "_id";

    @Inject
    private ElasticClient elasticClient;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    // public void createClass(String customTableName) {
    //
    // ClassPool pool = ClassPool.getDefault();
    // ClassClassPath classPath = new ClassClassPath(this.getClass());
    // pool.insertClassPath(classPath);
    // log.error("AKK inserted classpath {}", classPath);
    // CtClass cc = pool.makeClass("org.meveo." + customTableName);
    //
    // try {
    // CtField f = new CtField(CtClass.intType, "z", cc);
    // cc.addField(f);
    //
    // cc.addMethod(CtNewMethod.getter("getZ", f));
    // cc.addMethod(CtNewMethod.setter("setZ", f));
    // cc.addMethod(CtNewMethod.make("public String toString() {return \" \"+z;}", cc));
    //
    // cc.writeFile("C:\\andrius\\programs\\wildfly-10.1.0.Final\\standalone\\deployments\\opencell.war\\WEB-INF\\classes");
    //
    // Class clazz = cc.toClass();
    // Object instance = clazz.newInstance();
    // Field field = ReflectionUtils.getField(clazz, "z");
    // field.setAccessible(true);
    // field.set(instance, 10);
    // log.error("AKK field value is {}", field.get(instance));
    //
    // Object value = getEntityManager().createNativeQuery("select id from cust_cet", CustomTableRecord.class).getSingleResult();
    //
    // log.error("AKK Value from DB is {} {}", value);// , field.get(value));
    //
    // } catch (
    //
    // Exception e) {
    // log.error("AKK Failed to create a new Class {}", customTableName, e);
    // }
    //
    // }

    @Override
    public Long create(String tableName, Map<String, Object> values) throws BusinessException {

        Long id = super.create(tableName, values, true); // Force to return ID as we need it to retrieve data for Elastic Search population
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, true);

        return id;
    }

    /**
     * Insert multiple values into table
     * 
     * @param tableName Table name to insert values to
     * @param values A list of values to insert
     * @throws BusinessException General exception
     */
    public void create(String tableName, List<Map<String, Object>> values) throws BusinessException {

        create(tableName, values, true);
    }

    /**
     * Insert multiple values into table
     * 
     * @param tableName Table name to insert values to
     * @param values Values to insert
     * @param updateES Should Elastic search be updated during record creation. If false, ES population must be done outside this call.
     * @throws BusinessException General exception
     */
    private void create(String tableName, List<Map<String, Object>> values, boolean updateES) throws BusinessException {

        for (Map<String, Object> value : values) {

            // Return ID, but postpone ES flushing until all the values are processed
            if (updateES) {
                Long id = super.create(tableName, value, true); // Force to return ID as we need it to retrieve data for Elastic Search population
                elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, value, false, false);

            } else {
                super.create(tableName, value, false);
            }
        }

        if (updateES) {
            elasticClient.flushChanges();
        }
    }

    @Override
    public void update(String tableName, Map<String, Object> values) throws BusinessException {
        super.update(tableName, values);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, values.get(NativePersistenceService.FIELD_ID), values, false, true);
    }

    /**
     * Update multiple values in a table. Record is identified by an "id" field value.
     * 
     * @param tableName Table name to update values
     * @param values Values to update. Must contain an 'id' field.
     * @throws BusinessException General exception
     */
    public void update(String tableName, List<Map<String, Object>> values) throws BusinessException {

        for (Map<String, Object> value : values) {
            super.update(tableName, value);
            elasticClient.createOrUpdate(CustomTableRecord.class, tableName, value.get(NativePersistenceService.FIELD_ID), value, false, false);
        }
        elasticClient.flushChanges();
    }

    @Override
    public void updateValue(String tableName, Long id, String fieldName, Object value) throws BusinessException {
        super.updateValue(tableName, id, fieldName, value);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, MapUtils.putAll(new HashMap<String, Object>(), new Object[] { fieldName, value }), true, true);
    }

    @Override
    public void disable(String tableName, Long id) throws BusinessException {
        super.disable(tableName, id);
        elasticClient.remove(CustomTableRecord.class, tableName, id, true);
    }

    @Override
    public void disable(String tableName, Set<Long> ids) throws BusinessException {

        super.disable(tableName, ids);
        elasticClient.remove(CustomTableRecord.class, tableName, ids, true);
    }

    @Override
    public void enable(String tableName, Long id) throws BusinessException {
        super.enable(tableName, id);
        Map<String, Object> values = findById(tableName, id);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, true);
    }

    @Override
    public void enable(String tableName, Set<Long> ids) throws BusinessException {
        super.enable(tableName, ids);
        for (Long id : ids) {
            Map<String, Object> values = findById(tableName, id);
            elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, false);
        }
        elasticClient.flushChanges();
    }

    @Override
    public void remove(String tableName, Long id) throws BusinessException {
        super.remove(tableName, id);
        elasticClient.remove(CustomTableRecord.class, tableName, id, true);
    }

    @Override
    public void remove(String tableName, Set<Long> ids) throws BusinessException {
        super.remove(tableName, ids);
        elasticClient.remove(CustomTableRecord.class, tableName, ids, true);
    }

    @Override
    public void remove(String tableName) throws BusinessException {
        super.remove(tableName);
        elasticClient.remove(CustomTableRecord.class, tableName, (Long) null, true);
    }

    /**
     * Export data into a file into exports directory. Filename is in the following format: <db table name>_id_<formated date>.csv
     * 
     * @param customEntityTemplate Custom table definition
     * @param config Pagination and search criteria
     * @return A file name where the data will be exported to
     * @throws BusinessException General exception
     */
    @Asynchronous
    @SuppressWarnings("unchecked")
    public Future<String> exportData(CustomEntityTemplate customEntityTemplate, PaginationConfiguration config) throws BusinessException {

        QueryBuilder queryBuilder = getQuery(customEntityTemplate.getDbTablename(), config);

        SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), true);

        int firstRow = 0;
        int nrItemsFound = 0;

        ParamBean parambean = paramBeanFactory.getInstance();
        String exportDir = parambean.getChrootDir(currentUser.getProviderCode()) + File.separator + "exports" + File.separator;

        File exportsDirFile = new File(exportDir);

        File exportFile = new File(exportDir + customEntityTemplate.getDbTablename() + "_id" + DateUtils.formatDateWithPattern(new Date(), "_yyyy-MM-dd_HH-mm-ss") + ".csv");

        if (!exportsDirFile.exists()) {
            exportsDirFile.mkdirs();
        }

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());

        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }
        Collection<CustomFieldTemplate> fields = cfts.values();

        ObjectWriter oWriter = getCSVWriter(fields, true);

        try (FileWriter fileWriter = new FileWriter(exportFile)) {
            do {
                queryBuilder.applyPagination(query, firstRow, 500);
                List<Map<String, Object>> values = query.list();
                nrItemsFound = values.size();

                oWriter.writeValues(fileWriter).writeAll(values);

            } while (nrItemsFound == 500);

        } catch (IOException e) {
            log.error("Failed to write {} table data to a file {}", customEntityTemplate.getDbTablename(), exportFile.getAbsolutePath(), e);
            throw new BusinessException(e);
        }

        return new AsyncResult<String>(exportFile.getAbsolutePath());

    }

    /**
     * Import data into custom table
     * 
     * @param customEntityTemplate Custom table definition
     * @param file Data file
     * @param includeIdField True if file includes 'id' field
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    public int importData(CustomEntityTemplate customEntityTemplate, File file, boolean includeIdField, boolean append) throws BusinessException {

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());

        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            return importData(customEntityTemplate, cfts.values(), inputStream, includeIdField, append);

        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Import data into custom table
     * 
     * @param customEntityTemplate Custom table definition
     * @param fields Custom table fields
     * @param inputStream Data stream
     * @param includeIdField True if file includes 'id' field
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    public int importData(CustomEntityTemplate customEntityTemplate, Collection<CustomFieldTemplate> fields, InputStream inputStream, boolean includeIdField, boolean append)
            throws BusinessException {

        if (fields == null || fields.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }

        String tableName = customEntityTemplate.getDbTablename();
        int importedLines = 0;
        int importedLinesTotal = 0;
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();

        ObjectReader oReader = getCSVReader(fields, includeIdField);

        // Delete current data first if in override mode
        if (!append) {
            remove(tableName);
        }

        try (Reader reader = new InputStreamReader(inputStream)) {

            MappingIterator<Map<String, Object>> mappingIterator = oReader.readValues(reader);

            while (mappingIterator.hasNext()) {

                // Save to DB every 500 records
                if (importedLines >= 500) {
                    create(tableName, values, append);

                    values.clear();
                    importedLines = 0;
                }

                Map<String, Object> lineValues = mappingIterator.next();
                values.add(lineValues);

                importedLines++;
                importedLinesTotal++;
            }

            // Save to DB remaining records
            create(tableName, values, append);

            // Repopulate ES index
            if (!append) {
                elasticClient.repopulate(currentUser, CustomTableRecord.class, customEntityTemplate.getCode());
            }

        } catch (RuntimeJsonMappingException e) {
            throw new ValidationException("Invalid file format", "message.upload.fail.invalidFormat", e);

        } catch (IOException e) {
            throw new BusinessException(e);
        }

        return importedLinesTotal;
    }

    /**
     * Get the CSV file reader. Schema is created from field's dbFieldname values.
     * 
     * @param fields Custom table fields definition
     * @param includeIdField True if data includes ID field, which is not present in custom table field definitions
     * @return The CSV file reader
     */
    private ObjectReader getCSVReader(Collection<CustomFieldTemplate> fields, boolean includeIdField) {
        CsvSchema.Builder builder = CsvSchema.builder();

        if (includeIdField) {
            builder.addColumn(NativePersistenceService.FIELD_ID, ColumnType.NUMBER);
        }

        for (CustomFieldTemplate cft : fields) {
            builder.addColumn(cft.getDbFieldname(),
                cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE ? ColumnType.NUMBER : ColumnType.STRING);
        }

        CsvSchema schema = builder.build();
        CsvMapper mapper = new CsvMapper();
        return mapper.readerFor(Map.class).with(schema);
    }

    /**
     * Get the CSV file writer. Schema is created from field's dbFieldname values.
     * 
     * @param fields Custom table fields definition
     * @param includeIdField True if data includes ID field, which is not present in custom table field definitions
     * @return The CSV file reader
     */
    private ObjectWriter getCSVWriter(Collection<CustomFieldTemplate> fields, boolean includeIdField) {
        CsvSchema.Builder builder = CsvSchema.builder();

        if (includeIdField) {
            builder.addColumn(NativePersistenceService.FIELD_ID, ColumnType.NUMBER);
        }

        for (CustomFieldTemplate cft : fields) {
            builder.addColumn(cft.getDbFieldname(),
                cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE ? ColumnType.NUMBER : ColumnType.STRING);
        }

        CsvSchema schema = builder.build();
        CsvMapper mapper = new CsvMapper();
        return mapper.writerFor(Map.class).with(schema);
    }

    /**
     * Execute a search on given fields for given query values. See ElasticClient.search() for a query format.
     *
     * @cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param queryValues Fields and values to match
     * @param from Pagination - starting record. Defaults to 0.
     * @param size Pagination - number of records per page. Defaults to ElasticClient.DEFAULT_SEARCH_PAGE_SIZE.
     * @param sortFields - Fields to sort by. If omitted, will sort by score. If search query contains a 'closestMatch' expression, sortFields and sortOrder will be overwritten
     *        with a corresponding field and descending order.
     * @param sortOrders Sorting orders
     * @param returnFields Return only certain fields - see Elastic Search documentation for details
     * @return Search result
     * @throws BusinessException General business exception
     */
    public List<Map<String, Object>> search(String cetCodeOrTablename, Map<String, Object> queryValues, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders,
            String[] returnFields) throws BusinessException {

        ElasticSearchClassInfo classInfo = new ElasticSearchClassInfo(CustomTableRecord.class, cetCodeOrTablename);
        SearchResponse searchResult = elasticClient.search(queryValues, from, size, sortFields, sortOrders, returnFields, Arrays.asList(classInfo));

        if (searchResult == null) {
            return new ArrayList<>();
        }

        log.error("AKK search result is {}", searchResult.toString());

        List<Map<String, Object>> responseValues = new ArrayList<>();

        searchResult.getHits().forEach(hit -> {
            Map<String, Object> values = new HashMap<>();
            responseValues.add(values);

            values.put(NativePersistenceService.FIELD_ID, hit.getId());

            if (hit.getFields() != null) {
                for (SearchHitField field : hit.getFields().values()) {
                    if (field.getValues() != null) {
                        if (field.getValues().size() > 1) {
                            values.put(field.getName(), field.getValues());
                        } else {
                            values.put(field.getName(), field.getValue());
                        }
                    }
                }
            
            } else if (hit.getSource() != null) {
                values.putAll(hit.getSource());
            }
        });

        log.debug("AKK ES search result values are {}", responseValues);
        return responseValues;
    }

    /**
     * Get field value of the first record matching search criteria
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldToReturn Field value to return
     * @param queryValues Search criteria. A list of alternating field name (or condition) and field value.
     * @return A field value
     * @throws BusinessException General exception
     */
    public Object getValue(String cetCodeOrTablename, String fieldToReturn, Object... queryValues) throws BusinessException {

        Map<String, Object> values = MapUtils.putAll(new HashMap<String, Object>(), queryValues);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.ASC }, new String[] { fieldToReturn });

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0).get(fieldToReturn);
        }
    }

    /**
     * Get field value of the first record matching search criteria for a given date. Applicable to custom tables that contain 'valid_from' and 'valid_to' fields
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldToReturn Field value to return
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param queryValues Search criteria. A list of alternating field name (or condition) and field value.
     * @return A field value
     * @throws BusinessException General exception
     */
    public Object getValue(String cetCodeOrTablename, String fieldToReturn, Date date, Object... queryValues) throws BusinessException {

        Map<String, Object> values = MapUtils.putAll(new HashMap<String, Object>(), queryValues);
        values.put("minmaxRange valid_from valid_to", date);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.ASC }, new String[] { fieldToReturn });

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0).get(fieldToReturn);
        }
    }

    /**
     * Get field values of the first record matching search criteria
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param queryValues Search criteria. A list of alternating field name (or condition) and field value.
     * @return A map of values with field name as a key and field value as a value
     * @throws BusinessException General exception
     */
    public Map<String, Object> getValues(String cetCodeOrTablename, Object... queryValues) throws BusinessException {

        Map<String, Object> values = MapUtils.putAll(new HashMap<String, Object>(), queryValues);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.ASC }, null);

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * Get field values of the first record matching search criteria for a given date. Applicable to custom tables that contain 'valid_from' and 'valid_to' fields
     * 
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param queryValues Search criteria. A list of alternating field name (or condition) and field value.
     * @return A map of values with field name as a key and field value as a value
     * @throws BusinessException General exception
     */
    public Map<String, Object> getValues(String cetCodeOrTablename, Date date, Object... queryValues) throws BusinessException {

        Map<String, Object> values = MapUtils.putAll(new HashMap<String, Object>(), queryValues);
        values.put("minmaxRange valid_from valid_to", date);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.ASC }, null);

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with field name of customFieldTemplate code as a key and field value as a value
     * @param fields Field definitions
     * @param discardNull If True, null values will be discarded
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public static List<Map<String, Object>> convertValues(List<Map<String, Object>> values, Collection<CustomFieldTemplate> fields, boolean discardNull)
            throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Class> fieldTypes = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            fieldTypes.put(cft.getCode(), cft.getFieldType().getDataClass());
            fieldTypes.put(cft.getDbFieldname(), cft.getFieldType().getDataClass());
        }

        return convertValues(values, fieldTypes, discardNull);

    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with field name of customFieldTemplate code as a key and field value as a value
     * @param fields Field definitions with field name or field code as a key and data class as a value
     * @param discardNull If True, null values will be discarded
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public static List<Map<String, Object>> convertValues(List<Map<String, Object>> values, Map<String, Class> fields, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }
        List<Map<String, Object>> convertedValues = new LinkedList<>();

        for (Map<String, Object> value : values) {
            convertedValues.add(convertValues(value, fields, discardNull));
        }

        return convertedValues;
    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with customFieldTemplate code or db field name as a key and field value as a value.
     * @param fields Field definitions
     * @param discardNull If True, null values will be discarded
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Object> convertValues(Map<String, Object> values, Collection<CustomFieldTemplate> fields, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Class> fieldTypes = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            fieldTypes.put(cft.getCode(), cft.getFieldType().getDataClass());
            fieldTypes.put(cft.getDbFieldname(), cft.getFieldType().getDataClass());
        }

        return convertValues(values, fieldTypes, discardNull);
    }

    /**
     * Convert values to a data type matching field definition
     * 
     * @param values A map of values with customFieldTemplate code or db field name as a key and field value as a value.
     * @param fields Field definitions with field name or field code as a key and data class as a value
     * @param discardNull If True, null values will be discarded
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Object> convertValues(Map<String, Object> values, Map<String, Class> fields, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Object> valuesConverted = new HashMap<>();

        // Handle ID field
        Object id = values.get(FIELD_ID);
        if (id != null) {
            valuesConverted.put(FIELD_ID, castValue(id, Long.class, false));
        }

        // Convert field based on data type
        if (fields != null) {
            for (Entry<String, Object> valueEntry : values.entrySet()) {

                String key = valueEntry.getKey();
                if (key.equals(FIELD_ID)) {
                    continue;
                }
                if (valueEntry.getValue() == null && !discardNull) {
                    valuesConverted.put(key, null);

                } else if (valueEntry.getValue() != null) {

                    String[] fieldInfo = key.split(" ");
                    // String condition = fieldInfo.length == 1 ? null : fieldInfo[0];
                    String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1]; // field name here can be a db field name or a custom field code

                    Class dataClass = fields.get(fieldName);
                    if (dataClass == null) {
                        throw new ValidationException("No field definition " + fieldName + " was found");
                    }
                    Object value = castValue(valueEntry.getValue(), dataClass, false);

                    // Replace cft code with db field name if needed
                    String dbFieldname = CustomFieldTemplate.getDbFieldname(fieldName);
                    if (!fieldName.equals(dbFieldname)) {
                        key = key.replaceAll(fieldName, dbFieldname);
                    }
                    valuesConverted.put(key, value);
                }
            }

        }
        return valuesConverted;
    }

}