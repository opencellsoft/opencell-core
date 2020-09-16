/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.custom;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.customEntities.CustomTableRecord;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

@SuppressWarnings("deprecation")
@Stateless
public class CustomTableService extends NativePersistenceService {

    /**
     * File prefix indicating that imported data should be appended to exiting data
     */
    public static final String FILE_APPEND = "_append";

    public static final String ONLY_DIGIT_REGEX = "^-{0,1}[0-9]*$";

    private static final int MAX_DISPLAY_COLUMNS = 5;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @EJB
    private CustomTableService customTableService;

    @Inject
    protected ParamBeanFactory paramBeanFactory;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Override
    public Long create(String tableName, Map<String, Object> values) throws BusinessException {

        Long id = super.create(tableName, values, true, true); // Force to return ID as we need it to retrieve data for Elastic Search population
        values.put("id", id);
        elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, values, false, true);

        return id;
    }

    /**
     * Insert multiple values into table
     *
     * @param tableName Table name to insert values to
     * @param values A list of values to insert
     * @param fireNotifications Should notifications be fired upon record creation
     * @throws BusinessException General exception
     */
    public void create(String tableName, List<Map<String, Object>> values, boolean fireNotifications) throws BusinessException {

        for (Map<String, Object> value : values) {
            Long id = super.create(tableName, value, true, fireNotifications); // Force to return ID as we need it to retrieve data for Elastic Search population
            value.put("id", id);
            elasticClient.createOrUpdate(CustomTableRecord.class, tableName, id, value, false, false);
        }

        elasticClient.flushChanges();
    }

    /**
     * Insert multiple values into table with optionally not updating ES nor triggering notifications. Will execute in a new transaction
     *
     * @param tableName Table name to insert values to
     * @param customEntityTemplateCode Custom entity template, corresponding to a custom table, code
     * @param values Values to insert
     * @param updateESAndTriggerNotifications Should Elastic search be updated during record creation and notifications be fired for each record. If false, ES population must be
     *        done outside this call and Notifications have to be handled by some other means
     * @throws BusinessException General exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createInNewTx(String tableName, String customEntityTemplateCode, List<Map<String, Object>> values, boolean updateESAndTriggerNotifications) throws BusinessException {

        // Insert record to db, with ID returned, but flush to ES after the values are processed
        if (updateESAndTriggerNotifications) {

            create(tableName, values, true);

        } else {
            super.create(tableName, customEntityTemplateCode, values);
        }
    }

    public void update(String tableName, Map<String, Object> values) throws BusinessException {
        super.update(tableName, values, true);
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
            super.update(tableName, value, true);
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
    public int remove(String tableName, Long id) throws BusinessException {
        // validateExistance(tableName, Arrays.asList(id));
        int nrDeleted = super.remove(tableName, id);
        elasticClient.remove(CustomTableRecord.class, tableName, id, true);
        return nrDeleted;
    }

    @Override
    public int remove(String tableName, Set<Long> ids) throws BusinessException {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int nrDeleted = super.remove(tableName, ids);
        elasticClient.remove(CustomTableRecord.class, tableName, ids, true);
        return nrDeleted;
    }

    /**
     * Delete multiple records checking first if ID exists
     *
     * @param tableName Table name to delete from
     * @param ids A set of record identifiers
     * @return Number of records deleted
     * @throws BusinessException General exception
     */
    public int removeWithCheck(String tableName, Set<Long> ids) {
        validateExistance(tableName, new ArrayList<Long>(ids));
        return remove(tableName, ids);
    }

    @Override
    public int remove(String tableName) throws BusinessException {
        int nrDeleted = super.remove(tableName);
        elasticClient.remove(CustomTableRecord.class, tableName);
        return nrDeleted;
    }

    /**
     * Export data into a file into exports directory. Filename is in the following format: &lt;db table name&gt;_id_&lt;formated date&gt;.csv
     *
     * @param customEntityTemplate Custom table definition
     * @param config Pagination and search criteria
     * @return A future with a file name where the data will be exported to or an exception occurred
     * @throws BusinessException General exception
     */
    @Asynchronous
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    public Future<DataImportExportStatistics> exportData(CustomEntityTemplate customEntityTemplate, PaginationConfiguration config) throws BusinessException {

        try {
            QueryBuilder queryBuilder = getQuery(customEntityTemplate.getDbTablename(), config);

            SQLQuery query = queryBuilder.getNativeQuery(getEntityManager(), true);

            int firstRow = 0;
            int nrItemsFound = 0;

            ParamBean parambean = paramBeanFactory.getInstance();
            String providerRoot = parambean.getChrootDir(currentUser.getProviderCode());
            String exportDir = providerRoot + File.separator + "exports" + File.separator;

            File exportsDirFile = new File(exportDir);

            File exportFile = new File(exportDir + customEntityTemplate.getDbTablename() + DateUtils.formatDateWithPattern(new Date(), "_yyyy-MM-dd_HH-mm-ss") + ".csv");

            if (!exportsDirFile.exists()) {
                exportsDirFile.mkdirs();
            }

            Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());

            if (cfts == null || cfts.isEmpty()) {
                throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
            }

            List<CustomFieldTemplate> fields = new ArrayList<>(cfts.values());

            Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

                @Override
                public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                    int pos1 = cft1.getGUIFieldPosition();
                    int pos2 = cft2.getGUIFieldPosition();

                    return pos1 - pos2;
                }
            });

            ObjectWriter oWriter = getCSVWriter(fields);

            try (FileWriter fileWriter = new FileWriter(exportFile)) {

                SequenceWriter sWriter = oWriter.writeValues(fileWriter);

                do {
                    queryBuilder.applyPagination(query, firstRow, 500);
                    List<Map<String, Object>> values = query.list();
                    nrItemsFound = values.size();
                    firstRow = firstRow + 500;

                    sWriter.writeAll(values);

                } while (nrItemsFound == 500);

            } catch (IOException e) {
                log.error("Failed to write {} table data to a file {}", customEntityTemplate.getDbTablename(), exportFile.getAbsolutePath(), e);
                throw new BusinessException(e);
            }

            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(exportFile.getAbsolutePath().substring(providerRoot.length())));

        } catch (Exception e) {
            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(e));
        }
    }

    /**
     * Import data into custom table
     *
     * @param customEntityTemplate Custom table definition
     * @param file Data file
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int importData(CustomEntityTemplate customEntityTemplate, File file, boolean append) throws BusinessException {

        try (FileInputStream inputStream = new FileInputStream(file)) {
            return importData(customEntityTemplate, inputStream, append);

        } catch (IOException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Import data into custom table in asynchronous mode
     *
     * @param customEntityTemplate Custom table definition
     * @param inputStream Data stream
     * @param append True if data should be appended to the existing data
     * @return A future with a number of records imported or exception occurred
     * @throws BusinessException General business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<DataImportExportStatistics> importDataAsync(CustomEntityTemplate customEntityTemplate, InputStream inputStream, boolean append) throws BusinessException {

        try {
            int itemsImported = importData(customEntityTemplate, inputStream, append);
            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(itemsImported));

        } catch (Exception e) {
            return new AsyncResult<DataImportExportStatistics>(new DataImportExportStatistics(e));
        }
    }

    /**
     * Import data into custom table from a file
     *
     * @param customEntityTemplate Custom table definition
     * @param inputStream Data stream
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int importData(CustomEntityTemplate customEntityTemplate, InputStream inputStream, boolean append) throws BusinessException {

        // Custom table fields. Fields will be sorted by their GUI 'field' position.
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }
        List<CustomFieldTemplate> fields = new ArrayList<>(cfts.values());

        Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

            @Override
            public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                int pos1 = cft1.getGUIFieldPosition();
                int pos2 = cft2.getGUIFieldPosition();

                return pos1 - pos2;
            }
        });

        Map<String, CustomFieldTemplate> cftsMap = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            cftsMap.put(cft.getCode(), cft);
            cftsMap.put(cft.getDbFieldname(), cft);
        }

        String tableName = customEntityTemplate.getDbTablename();
        int importedLines = 0;
        int importedLinesTotal = 0;
        List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();

        ObjectReader oReader = getCSVReader(fields);

        // Delete current data first if in override mode
        if (!append) {
            customTableService.remove(tableName);
        }

        // Update ES in batch way might be faster - reconstructed from a table
        boolean updateESImediately = false;

        try (Reader reader = new InputStreamReader(inputStream)) {

            MappingIterator<Map<String, Object>> mappingIterator = oReader.readValues(reader);

            while (mappingIterator.hasNext()) {

                // Save to DB every 500 records
                if (importedLines >= 500) {

                    values = convertValues(values, cftsMap, false);
                    customTableService.createInNewTx(tableName, customEntityTemplate.getCode(), values, updateESImediately);

                    values.clear();
                    importedLines = 0;
                }

                Map<String, Object> lineValues = mappingIterator.next();
                values.add(lineValues);

                importedLines++;
                importedLinesTotal++;

                if (importedLinesTotal % 30000 == 0) {
                    log.trace("Imported {} lines to {} table", importedLinesTotal, tableName);
                }
            }

            // Save to DB remaining records
            values = convertValues(values, cftsMap, false);
            customTableService.createInNewTx(tableName, customEntityTemplate.getCode(), values, updateESImediately);

            // Re-populate ES index
            if (!updateESImediately) {
                elasticClient.populateAll(currentUser, CustomTableRecord.class, customEntityTemplate.getCode());
            }

            log.info("Imported {} lines to {} table", importedLinesTotal, tableName);

        } catch (RuntimeJsonMappingException e) {
            throw new ValidationException("Invalid file format", "message.upload.fail.invalidFormat", e);

        } catch (IOException e) {
            throw new BusinessException(e);
        }

        return importedLinesTotal;
    }

    /**
     * Import data into custom table from a list of values (API). Note that notifications are fired only when submitting a small amount of records at once - upto 1K of records
     *
     * @param customEntityTemplate Custom table definition
     * @param values A list of records to import. Each record is a map of values with field name as a map key and field value as a value.
     * @param append True if data should be appended to the existing data
     * @return Number of records imported
     * @throws BusinessException General business exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int importData(CustomEntityTemplate customEntityTemplate, List<Map<String, Object>> values, boolean append) throws BusinessException {

        // Custom table fields. Fields will be sorted by their GUI 'field' position.
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(customEntityTemplate.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table " + customEntityTemplate.getDbTablename(), "customTable.noFields");
        }
        List<CustomFieldTemplate> fields = new ArrayList<>(cfts.values());

        Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

            @Override
            public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                int pos1 = cft1.getGUIFieldPosition();
                int pos2 = cft2.getGUIFieldPosition();

                return pos1 - pos2;
            }
        });

        Map<String, CustomFieldTemplate> cftsMap = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            cftsMap.put(cft.getCode(), cft);
            cftsMap.put(cft.getDbFieldname(), cft);
        }

        String tableName = customEntityTemplate.getDbTablename();
        int importedLines = 0;
        int importedLinesTotal = 0;
        List<Map<String, Object>> valuesPartial = new ArrayList<Map<String, Object>>();

        // Delete current data first if in override mode
        if (!append) {
            customTableService.remove(tableName);
        }

        // By default will update ES immediately. If more than 1000 records are being updated, ES will be updated in batch way - reconstructed from a table
        boolean updateESImediately = append;
        if (values.size() > 1000) {
            updateESImediately = false;
        }

        for (Map<String, Object> value : values) {

            // Save to DB every 1000 records
            if (importedLines >= 1000) {
                valuesPartial = convertValues(valuesPartial, cftsMap, false);
                customTableService.createInNewTx(tableName, customEntityTemplate.getCode(), valuesPartial, updateESImediately);

                valuesPartial.clear();
                importedLines = 0;
            }

            valuesPartial.add(value);

            importedLines++;
            importedLinesTotal++;
        }

        // Save to DB remaining records
        valuesPartial = convertValues(valuesPartial, cftsMap, false);
        customTableService.createInNewTx(tableName, customEntityTemplate.getCode(), valuesPartial, updateESImediately);

        // Repopulate ES index
        if (!updateESImediately) {
            elasticClient.populateAll(currentUser, CustomTableRecord.class, customEntityTemplate.getCode());
        }

        return importedLinesTotal;
    }

    /**
     * Get the CSV file reader. Schema is created from field's dbFieldname values.
     *
     * @param fields Custom table fields definition
     * @return The CSV file reader
     */
    private ObjectReader getCSVReader(Collection<CustomFieldTemplate> fields) {
        CsvSchema.Builder builder = CsvSchema.builder();

        builder.addColumn(NativePersistenceService.FIELD_ID, ColumnType.NUMBER);

        for (CustomFieldTemplate cft : fields) {
            builder.addColumn(cft.getDbFieldname(), cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE ? ColumnType.NUMBER : ColumnType.STRING);
        }

        CsvSchema schema = builder.setUseHeader(true).setStrictHeaders(true).setReorderColumns(true).build();
        CsvMapper mapper = new CsvMapper();
        return mapper.readerFor(Map.class).with(schema);
    }

    /**
     * Get the CSV file writer. Schema is created from field's dbFieldname values.
     *
     * @param fields Custom table fields definition
     * @return The CSV file reader
     */
    private ObjectWriter getCSVWriter(Collection<CustomFieldTemplate> fields) {
        CsvSchema.Builder builder = CsvSchema.builder();

        builder.addColumn(NativePersistenceService.FIELD_ID, ColumnType.NUMBER);

        for (CustomFieldTemplate cft : fields) {
            builder.addColumn(cft.getDbFieldname(), cft.getFieldType() == CustomFieldTypeEnum.LONG || cft.getFieldType() == CustomFieldTypeEnum.DOUBLE ? ColumnType.NUMBER : ColumnType.STRING);
        }

        CsvSchema schema = builder.setUseHeader(true).build();
        CsvMapper mapper = new CsvMapper();

        return mapper.writerFor(Map.class).with(schema).with(new SimpleDateFormat(ParamBean.getInstance().getDateTimeFormat(appProvider.getCode()))).with(Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

    /**
     * Execute a search on given fields for given query values. See ElasticClient.search() for a query format.
     *
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
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
    public List<Map<String, Object>> search(String cetCodeOrTablename, Map<String, Object> queryValues, Integer from, Integer size, String[] sortFields, SortOrder[] sortOrders, String[] returnFields)
            throws BusinessException {

        ElasticSearchClassInfo classInfo = new ElasticSearchClassInfo(CustomTableRecord.class, cetCodeOrTablename);
        SearchResponse searchResult = elasticClient.search(queryValues, from, size, sortFields, sortOrders, returnFields, Arrays.asList(classInfo));

        if (searchResult == null) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> responseValues = new ArrayList<>();

        searchResult.getHits().forEach(hit -> {
            Map<String, Object> values = new HashMap<>();
            responseValues.add(values);

            if (hit.getFields() != null && hit.getFields().values() != null && !hit.getFields().values().isEmpty()) {
                for (DocumentField field : hit.getFields().values()) {
                    if (field.getValues() != null) {
                        if (field.getValues().size() > 1) {
                            values.put(field.getName(), field.getValues());
                        } else {
                            values.put(field.getName(), field.getValue());
                        }
                    }
                }

            } else if (hit.getSourceAsMap() != null) {
                values.putAll(hit.getSourceAsMap());
            }
        });

        // log.debug("AKK ES search result values are {}", responseValues);
        return responseValues;
    }

    /**
     * Get field value of the first record matching search criteria
     *
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldToReturn Field value to return
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A field value
     * @throws BusinessException General exception
     */
    public Object getValue(String cetCodeOrTablename, String fieldToReturn, Map<String, Object> queryValues) throws BusinessException {
        removeEmptyKeys(queryValues);
        Map<String, Object> values = new HashMap<>(queryValues);
        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.DESC }, new String[] { fieldToReturn });

        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return results.get(0).get(fieldToReturn);
        }
    }

    void removeEmptyKeys(Map<String, Object> queryValues) {
        queryValues.entrySet().removeIf(e -> e.getKey().isEmpty());
    }

    /**
     * Get field value of the first record matching search criteria for a given date. Applicable to custom tables that contain 'valid_from' and 'valid_to' fields
     *
     * @param cetCodeOrTablename Custom entity template code, or custom table name to query
     * @param fieldToReturn Field value to return
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A field value
     * @throws BusinessException General exception
     */
    public Object getValue(String cetCodeOrTablename, String fieldToReturn, Date date, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);
        values.put("minmaxRange valid_from valid_to", date);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_VALID_PRIORITY, FIELD_VALID_FROM, FIELD_ID }, new SortOrder[] { SortOrder.DESC, SortOrder.DESC, SortOrder.DESC },
            new String[] { fieldToReturn });

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
     * @param fieldsToReturn Field values to return. Optional. If not provided all fields will be returned.
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A map of values with field name as a key and field value as a value. Note field value is always of String data type.
     * @throws BusinessException General exception
     */
    public Map<String, Object> getValues(String cetCodeOrTablename, String[] fieldsToReturn, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_ID }, new SortOrder[] { SortOrder.DESC }, fieldsToReturn);

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
     * @param fieldsToReturn Field values to return. Optional. If not provided all fields will be returned.
     * @param date Record validity date, as expressed by 'valid_from' and 'valid_to' fields, to match
     * @param queryValues Search criteria with condition/field name as a key and field value as a value. See ElasticClient.search() for a query format.
     * @return A map of values with field name as a key and field value as a value. Note field value is always of String data type.
     * @throws BusinessException General exception
     */
    public Map<String, Object> getValues(String cetCodeOrTablename, String[] fieldsToReturn, Date date, Map<String, Object> queryValues) throws BusinessException {

        Map<String, Object> values = new HashMap<>(queryValues);
        values.put("minmaxRange valid_from valid_to", date);

        List<Map<String, Object>> results = search(cetCodeOrTablename, values, 0, 1, new String[] { FIELD_VALID_PRIORITY, FIELD_VALID_FROM, FIELD_ID }, new SortOrder[] { SortOrder.DESC, SortOrder.DESC, SortOrder.DESC },
            fieldsToReturn);

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
    public List<Map<String, Object>> convertValues(List<Map<String, Object>> values, Collection<CustomFieldTemplate> fields, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, CustomFieldTemplate> cftsMap = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            cftsMap.put(cft.getCode(), cft);
            cftsMap.put(cft.getDbFieldname(), cft);
        }

        return convertValues(values, cftsMap, discardNull);

    }

    /**
     * Convert values to a data type matching field definition
     *
     * @param values A map of values with field name of customFieldTemplate code as a key and field value as a value
     * @param fields Field definitions with field name or field code as a key and data class as a value
     * @param discardNull If True, null values will be discarded
     * @param regexpFields
     * @param classNames
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    public List<Map<String, Object>> convertValues(List<Map<String, Object>> values, Map<String, CustomFieldTemplate> cftsMap, boolean discardNull) throws ValidationException {

        if (values == null) {
            return null;
        }
        List<Map<String, Object>> convertedValues = new LinkedList<>();

        String[] datePatterns = new String[] { DateUtils.DATE_TIME_PATTERN, paramBean.getDateTimeFormat(), DateUtils.DATE_PATTERN, paramBean.getDateFormat() };

        for (Map<String, Object> value : values) {
            convertedValues.add(convertValue(value, cftsMap, discardNull, datePatterns));
        }

        return convertedValues;
    }

    /**
     * Convert values to a data type matching field definition
     *
     * @param values A map of values with customFieldTemplate code or db field name as a key and field value as a value.
     * @param fields Field definitions
     * @param discardNull If True, null values will be discarded
     * @param datePatterns Optional. Date patterns to apply to a date type field. Conversion is attempted in that order until a valid date is matched.If no values are provided, a
     *        standard date and time and then date only patterns will be applied.
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    public Map<String, Object> convertValue(Map<String, Object> values, Collection<CustomFieldTemplate> fields, boolean discardNull, String[] datePatterns) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, CustomFieldTemplate> cftsMap = new HashMap<>();
        for (CustomFieldTemplate cft : fields) {
            cftsMap.put(cft.getCode(), cft);
            cftsMap.put(cft.getDbFieldname(), cft);
        }

        return convertValue(values, cftsMap, discardNull, datePatterns);
    }

    /**
     * Convert single record values to a data type matching field definition
     *
     * @param values A map of values with customFieldTemplate code or db field name as a key and field value as a value.
     * @param fields Field definitions with field name or field code as a key and data class as a value
     * @param discardNull If True, null values will be discarded
     * @param datePatterns Optional. Date patterns to apply to a date type field. Conversion is attempted in that order until a valid date is matched.If no values are provided, a
     *        standard date and time and then date only patterns will be applied.
     * @param classNames
     * @param regexpFields
     * @return Converted values with db field name as a key and field value as value.
     * @throws ValidationException
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Object> convertValue(Map<String, Object> values, Map<String, CustomFieldTemplate> cftsMap, boolean discardNull, String[] datePatterns) throws ValidationException {

        if (values == null) {
            return null;
        }

        Map<String, Object> valuesConverted = new HashMap<>();

        // Handle ID field
        Object id = values.get(FIELD_ID);
        if (id != null) {
            valuesConverted.put(FIELD_ID, castValue(id, Long.class, false, datePatterns, null));
        }

        // Convert field based on data type
        if (cftsMap != null) {
            for (Entry<String, Object> valueEntry : values.entrySet()) {

                String key = valueEntry.getKey();
                String[] fieldInfo = key.split(" ");
                String fieldName = fieldInfo.length == 1 ? fieldInfo[0] : fieldInfo[1]; // field name here can be a db field name or a custom field code
                Object value = valueEntry.getValue();

                if (fieldName.equals(FIELD_ID)) {
                    continue; // Was handled before already
                }

                if (PersistenceService.SEARCH_ATTR_TYPE_CLASS.equals(fieldName) || PersistenceService.SEARCH_SQL.equals(key)
                        || (value != null && value instanceof String && (PersistenceService.SEARCH_IS_NOT_NULL.equals((String) value) || PersistenceService.SEARCH_IS_NULL.equals((String) value)))) {
                    valuesConverted.put(key, value);
                    continue;
                }

                CustomFieldTemplate cft = cftsMap.get(fieldName);
                if (cft == null) {
                    throw new ValidationException("No field definition " + fieldName + " was found");
                }
                if (value == null && !discardNull) {
                    // must check the default value
                    valuesConverted.put(key, cft.getDefaultValueConverted());

                } else if (FIELD_DISABLED.equals(key) && (BigDecimal) value != BigDecimal.ZERO) {

                    valuesConverted.put(key, 1);

                } else if (value != null) {

                    Class dataClass = cft.getFieldType().getDataClass();
                    if (dataClass == null) {
                        throw new ValidationException("No field definition " + fieldName + " was found");
                    }
                    value = castValue(value, dataClass, false, datePatterns, cft);

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

    public Map<String, Object> findRecordOfTableById(CustomFieldTemplate field, Long id) {
        try {
            return Optional.ofNullable(field).map(CustomFieldTemplate::tableName)
                    .map(tableName -> findRecordByIdAndTableName(id, tableName)).orElse(new HashMap<>());
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
    private Map<String, Object> findRecordByIdAndTableName(Long id, String tableName) {
        QueryBuilder queryBuilder = getQuery(tableName, null);
        queryBuilder.addCriterion("id", "=", id, true);
        Query query = queryBuilder.getNativeQuery(getEntityManager(), true);
        return (Map<String, Object>) query.uniqueResult();
    }

    public List<CustomTableRecordDto> selectAllRecordsOfATableAsRecord(String tableName, String wildCode) {

        try {
            String endOfLine = "}";
            Map<String, CustomFieldTemplate> cftl = customFieldTemplateService.findCFTsByDbTbleName(tableName);
            List<String> fields = cftl.values().stream().filter(x -> x.isUniqueConstraint()).map(x -> x.getDbFieldname()).collect(Collectors.toList());
            if (fields.isEmpty()) {
                fields = cftl.values().stream().filter(x -> x.getGUIFieldPosition() < MAX_DISPLAY_COLUMNS).map(x -> x.getDbFieldname()).collect(Collectors.toList());
                if (cftl.size() > MAX_DISPLAY_COLUMNS) {
                    endOfLine = ", ... }";
                }
            }
            String eol = endOfLine;
            List<Map<String, Object>> mapList = extractMapListByFields(tableName, wildCode, fields);
            return mapList.stream().map(x -> new CustomTableRecordDto(getDisplay(tableName, x, eol), x)).collect(Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private String getDisplay(String tableName, Map<String, Object> line, String endOfLine) {
        String vals = line.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
        return tableName + " {" + vals + endOfLine;
    }

    public List<Map<String, Object>> selectAllRecordsOfATableAsMap(String tableName, String wildCode) {
        try {
            Map<String, CustomFieldTemplate> cftl = customFieldTemplateService.findCFTsByDbTbleName(tableName);
            List<String> fields = cftl.values().stream().filter(x -> x.isUniqueConstraint()).map(x -> x.getDbFieldname()).collect(Collectors.toList());
            if (fields.isEmpty()) {
                fields = cftl.values().stream().filter(x -> x.getGUIFieldPosition() < MAX_DISPLAY_COLUMNS).map(x -> x.getDbFieldname()).collect(Collectors.toList());
            }
            return extractMapListByFields(tableName, wildCode, fields);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
    public List<Map<String, Object>> extractMapListByFields(String tableName, String wildCode, List<String> fields) {
        List<String> fetchFields = new ArrayList<>();
        fetchFields.add(FIELD_ID);
        fetchFields.addAll(fields);
        PaginationConfiguration pc = new PaginationConfiguration(null);
        pc.setFetchFields(fetchFields);
        QueryBuilder qb = getQuery(tableName, pc);
        if (!StringUtils.isEmpty(wildCode)) {
            qb.addSql(" cast(" + FIELD_ID + " as varchar(100)) like :id");
        }
        Query query = qb.getNativeQuery(getEntityManager(), true);
        if (!StringUtils.isEmpty(wildCode)) {
            query.setParameter("id", "%" + wildCode.toLowerCase() + "%");
        }
        return query.list();
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    public boolean containsRecordOfTableByColumn(String tableName, String columnName, Long id) {
        QueryBuilder queryBuilder = getQuery(tableName, null);
        queryBuilder.addCriterion(columnName, "=", id, true);
        Query query = queryBuilder.getNativeQuery(getEntityManager(), true);
        return !query.list().isEmpty();
    }

    /**
     * Fetch entity from a referenceWrapper. This method return the wrapped object if it's an entity managed or a CustomEntity, but return a map of values if the wrapped object is
     * a reference to a custom table
     * 
     * @param referenceWrapper
     * @return
     */
    public Object findEntityFromReference(EntityReferenceWrapper referenceWrapper) {
        String classname = referenceWrapper.getClassname();
        String code = referenceWrapper.getCode();
        String classnameCode = referenceWrapper.getClassnameCode();
        return findEntityByClassNameAndKey(classname, code, classnameCode);
    }

    private Object findEntityByClassNameAndKey(String classname, String code, String classnameCode) {
        if (classname.equals(CustomEntityInstance.class.getName())) {
            CustomEntityTemplate cet = customEntityTemplateService.findByCode(classnameCode);
            if (cet.isStoreAsTable()) {
                return findRecordByIdAndTableName(Long.parseLong(code), cet.getDbTablename());
            } else {
                return customEntityInstanceService.findByCodeByCet(cet.getDbTablename(), code);
            }
        } else {
            try {
                return customEntityInstanceService.findByEntityClassAndCode(Class.forName(classname), code);
            } catch (ClassNotFoundException e) {
                log.error("class in the ClassRefWrapper " + classname + " not found ", e);
            }
        }
        return null;
    }

    public List<Map<String, Object>> completeWithEntities(List<Map<String, Object>> list, Map<String, CustomFieldTemplate> cfts, int loadReferenceDepth) {
        list.forEach(map -> completeWithEntities(cfts, map, 0, loadReferenceDepth));
        return list;
    }

    public void completeWithEntities(Map<String, CustomFieldTemplate> cfts, Map<String, Object> map, int currentDepth, int maxDepth) {
        if (currentDepth < maxDepth) {
            Map<String, CustomFieldTemplate> reference = toLowerCaseKeys(cfts);
            map.entrySet().stream().filter(entry -> reference.containsKey(entry.getKey().toLowerCase())).forEach(entry -> replaceIdValueByItsRepresentation(reference, entry, currentDepth, maxDepth));
        }
    }

    public void replaceIdValueByItsRepresentation(Map<String, CustomFieldTemplate> reference, Map.Entry<String, Object> entry, int currentDepth, int maxDepth) {
        if (entry.getValue() != null && entry.getValue().toString().matches(ONLY_DIGIT_REGEX)) {
            CustomFieldTemplate customFieldTemplate = reference.get(entry.getKey().toLowerCase());
            Optional.ofNullable(customFieldTemplate).filter(field -> Objects.nonNull(field.getEntityClazz()))
                    .map(field -> getEitherTableOrEntityValue(field, Long.valueOf(entry.getValue().toString())))
                .filter(values -> values.size() > 0).ifPresent(values -> replaceValue(entry, customFieldTemplate, values, currentDepth, maxDepth));
        }
    }

    public Map<String, Object> getEitherTableOrEntityValue(CustomFieldTemplate field, Long id) {
        CustomEntityTemplate relatedEntity = customEntityTemplateService.findByCode(field.tableName());
        if (relatedEntity != null) {
            if (relatedEntity.isStoreAsTable()) {
                return findRecordOfTableById(field, id);
            }
            return Optional.ofNullable(customEntityInstanceService.findById(id)).map(customEntityInstanceService::customEntityInstanceAsMapWithCfValues).orElse(new HashMap<>());
        }
        return findByClassAndId(field.getEntityClazz(), id);
    }

    public void replaceValue(Map.Entry<String, Object> entry, CustomFieldTemplate customFieldTemplate, Map<String, Object> values, int currentDepth, int maxDepth) {
        entry.setValue(values);
        final int depth = ++currentDepth;
        Optional.ofNullable(customEntityTemplateService.findByCodeOrDbTablename(customFieldTemplate.tableName()))
            .ifPresent(cet -> completeWithEntities(customFieldTemplateService.findByAppliesTo(cet.getAppliesTo()), values, depth, maxDepth));
    }

    public Map<String, CustomFieldTemplate> toLowerCaseKeys(Map<String, CustomFieldTemplate> cfts) {
        return cfts.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue));
    }

    public Map<String, CustomFieldTemplate> retrieveAndValidateCfts(CustomEntityTemplate cet, boolean checkDisabledField) {
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table", "customTable.noFields");
        }
        if (checkDisabledField && !cfts.containsKey(NativePersistenceService.FIELD_DISABLED)) {
            throw new ValidationException("Custom table does not contain a field 'disabled'", "customTable.noDisabledField");
        }
        return cfts;
    }

    public CustomEntityTemplate getCET(String customTableCode) {
        CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(customTableCode);
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, customTableCode);
        }
        return cet;
    }

    public void updateRecords(String dbTablename, Collection<CustomFieldTemplate> cftsValues, List<CustomTableRecordDto> valuesWithIds) {
        List<Long> inputIds = valuesWithIds.stream().map(x -> (castToLong(x.getValues().get(FIELD_ID)))).collect(toList());
        validateExistance(dbTablename, inputIds);
        SubListCreator<CustomTableRecordDto> subListCreator = new SubListCreator<CustomTableRecordDto>(valuesWithIds, 500);
        while (subListCreator.isHasNext()) {
            List<Map<String, Object>> values = subListCreator.getNextWorkSet().stream().map(x -> x.getValues()).collect(toList());
            values = convertValues(values, cftsValues, false);
            update(dbTablename, values);
        }
    }

    private void validateExistance(String dbTablename, List<Long> inputIds) {
        List<BigInteger> existingRecords = filterExistingRecordsOnTable(dbTablename, inputIds);

        Map<Boolean, List<Long>> partitioned = inputIds.stream().collect(Collectors.partitioningBy(x -> existingRecords.stream().anyMatch(y -> x.equals(y.longValue()))));

        List<Long> invalidList = partitioned.get(false);
        if (!invalidList.isEmpty()) {
            throw new EntityDoesNotExistsException(dbTablename, invalidList);
        }
    }

    private Long castToLong(Object id) {
        if (id instanceof String) {
            return Long.parseLong((String) id);
        } else if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        throw new InvalidParameterException("Invalid id value found: " + id);
    }

}