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

package org.meveo.admin.action.admin.custom;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.model.ReferenceIdentifierCode;
import org.meveo.model.ReferenceIdentifierDescription;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.custom.DataImportExportStatistics;
import org.meveo.util.view.NativeTableBasedDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;

@Named
@ViewScoped
public class CustomTableBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = -2748591950645172132L;

    private static final String FIELD_CODE = "code";

    private static final String FIELD_DESCRIPTION = "description";

    private static final String FIELD_ENTITY_TXT_SUFFIX = "_TXTlabel";

    @Inject
    private CustomTableService customTableService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    /**
     * Custom table name. Determined from customEntityTemplate.code value.
     */
    private String customTableName;

    /**
     * Custom table fields. Come from custom fields defined in custom entity.
     */
    private List<CustomFieldTemplate> fields;

    private LazyDataModel<Map<String, Object>> customTableBasedDataModel;

    /**
     * Information about entity that entity type field reference to. DB fieldname is a key.
     */
    private Map<String, CETEntityTypeFieldInfo> entityReferences = new HashMap<>();

    private Map<String, Object> rowValues = new HashMap<>();

    private boolean appendImportedData;

    private List<Map<String, Object>> selectedValues;

    private Future<DataImportExportStatistics> exportFuture;

    private Future<DataImportExportStatistics> importFuture;

    private boolean newEntity = false;

    public CustomTableBean() {
        super(CustomEntityTemplate.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public CustomEntityTemplate initEntity() {
        super.initEntity();

        customTableName = entity.getDbTablename();

        // Get fields and sort them by GUI order
        fields = getCETFields(entity, null);

        // Load fields of entities that entity type field points to
        for (CustomFieldTemplate cetField : fields) {
            if (cetField.getFieldType() == CustomFieldTypeEnum.ENTITY) {

                CETEntityTypeFieldInfo cetFieldInfo = new CETEntityTypeFieldInfo();

                cetFieldInfo.fields = new ArrayList<CustomFieldTemplate>();

                String customEntityTemplateCode = cetField.getEntityClazzCetCode();
                if (customEntityTemplateCode != null) {
                    CustomEntityTemplate fieldCet = customEntityTemplateService.findByCode(customEntityTemplateCode);

                    if (fieldCet != null && fieldCet.isStoreAsTable()) {
                        cetFieldInfo.referenceType = EntityFieldTypeReference.CT;
                        cetFieldInfo.tableName = fieldCet.getDbTablename();

                        // Fetch only first 5 fields plus ID field
                        cetFieldInfo.fields.add(new CustomFieldTemplate("id", "Id", CustomFieldTypeEnum.LONG));
                        cetFieldInfo.fields.addAll(getCETFields(fieldCet, 5));

                    } else {

                        cetFieldInfo.referenceType = EntityFieldTypeReference.CI;
                        cetFieldInfo.tableName = CustomEntityInstance.class.getAnnotation(Table.class).name();

                        cetFieldInfo.fields.add(new CustomFieldTemplate(FIELD_CODE, "Code", CustomFieldTypeEnum.STRING));
                        cetFieldInfo.fields.add(new CustomFieldTemplate(FIELD_DESCRIPTION, "Description", CustomFieldTypeEnum.STRING));
                    }

                } else {
                    cetFieldInfo.referenceType = EntityFieldTypeReference.BusinessEntity;
                    try {

                        Class entityClazz = Class.forName(cetField.getEntityClazz());
                        if (entityClazz.isAnnotationPresent(Table.class)) {
                            cetFieldInfo.tableName = ((Table) entityClazz.getAnnotation(Table.class)).name();
                        } else {

                            Class superClass = entityClazz.getSuperclass();
                            while (superClass != null) {
                                if (superClass.isAnnotationPresent(Table.class)) {
                                    cetFieldInfo.tableName = ((Table) superClass.getAnnotation(Table.class)).name();
                                    break;
                                }
                            }
                        }

                        if (entityClazz.isAnnotationPresent(ReferenceIdentifierCode.class)) {
                            String entityFieldName = ((ReferenceIdentifierCode) entityClazz.getAnnotation(ReferenceIdentifierCode.class)).value();
                            Field entityField = FieldUtils.getField(entityClazz, entityFieldName, true);
                            String dbFieldName = entityFieldName;
                            if (entityField.isAnnotationPresent(Column.class)) {
                                dbFieldName = entityField.getAnnotation(Column.class).name();
                            }

                            cetFieldInfo.fields.add(new CustomFieldTemplate(dbFieldName, StringUtils.capitalize(entityFieldName), CustomFieldTypeEnum.STRING));
                        }
                        if (entityClazz.isAnnotationPresent(ReferenceIdentifierDescription.class)) {
                            String entityFieldName = ((ReferenceIdentifierDescription) entityClazz.getAnnotation(ReferenceIdentifierDescription.class)).value();
                            Field entityField = FieldUtils.getField(entityClazz, entityFieldName, true);
                            String dbFieldName = entityFieldName;
                            if (entityField.isAnnotationPresent(Column.class)) {
                                dbFieldName = entityField.getAnnotation(Column.class).name();
                            }

                            cetFieldInfo.fields.add(new CustomFieldTemplate(dbFieldName, StringUtils.capitalize(entityFieldName), CustomFieldTypeEnum.STRING));
                        }

                    } catch (ClassNotFoundException e) {
                        log.error("Class {} referenced from a Custom field not found", cetField.getEntityClazz());
                    }

                }

                entityReferences.put(cetField.getDbFieldname(), cetFieldInfo);
            }
        }

        return entity;

    }

    /**
     * Get a data model for primefaces lazy loading datatable component for a given entity reference type field
     * 
     * @param field Entity reference type field
     * @return Data model
     */
    public LazyDataModel<Map<String, Object>> getFieldDataModel(CustomFieldTemplate field) {

        if (field == null) {
            return null;
        }
        CETEntityTypeFieldInfo entityReferenceInfo = entityReferences.get(field.getDbFieldname());

        if (entityReferenceInfo.dataModel == null) {

            final Map<String, Object> filters = entityReferenceInfo.dataModelFilter;

            final List<String> fieldsToFetch = entityReferenceInfo.getFieldDBfieldnames();
            final String tableName = entityReferenceInfo.tableName;

            if (entityReferenceInfo.referenceType == EntityFieldTypeReference.CI) {
                filters.put("cet_code", entityReferenceInfo.cetCode);
                fieldsToFetch.add(NativePersistenceService.FIELD_ID);

            } else if (entityReferenceInfo.referenceType == EntityFieldTypeReference.BusinessEntity) {
                fieldsToFetch.add(NativePersistenceService.FIELD_ID);
            }

            if (tableName == null) {
                log.error("Class {} referenced from a Custom field not found", field.getEntityClazz());
                return null;
            }

            entityReferenceInfo.dataModel = new NativeTableBasedDataModel() {

                private static final long serialVersionUID = 6682319740448829853L;

                @Override
                protected Map<String, Object> getSearchCriteria() {
                    return filters;
                }

                @Override
                protected CustomTableService getPersistenceServiceImpl() {
                    return customTableService;
                }

                @Override
                protected String getTableName() {
                    return tableName;
                }

                @Override
                protected List<String> getListFieldsToFetchImpl() {
                    return fieldsToFetch;
                }

                @Override
                public Map<String, Object> getRowData(String rowKey) {
                    for (Map<String, Object> row : getWrappedData()) {
                        if (rowKey.equals(row.get(NativePersistenceService.FIELD_ID).toString())) {
                            return row;
                        }
                    }
                    return null;
                }
            };
        }

        return entityReferenceInfo.dataModel;

    }

    /**
     * DataModel for primefaces lazy loading datatable component.
     *
     * @return LazyDataModel implementation.
     */
    public LazyDataModel<Map<String, Object>> getDataModel() {
        return getDataModel(filters);
    }

    /**
     * DataModel for primefaces lazy loading datatable component.
     *
     * @param inputFilters Search criteria
     * @return LazyDataModel implementation.
     */
    public LazyDataModel<Map<String, Object>> getDataModel(Map<String, Object> inputFilters) {
        if (customTableBasedDataModel == null && customTableName != null) {

            final Map<String, Object> filters = inputFilters;

            customTableBasedDataModel = new NativeTableBasedDataModel() {

                private static final long serialVersionUID = 6682319740448829853L;

                @Override
                protected Map<String, Object> getSearchCriteria() {
                    return filters;
                }

                @Override
                protected CustomTableService getPersistenceServiceImpl() {
                    return customTableService;
                }

                @Override
                protected String getTableName() {
                    return CustomTableBean.this.getCustomTableName();
                }

                @Override
                protected List<Map<String, Object>> loadData(PaginationConfiguration paginationConfig) {
                    List<Map<String, Object>> data = super.loadData(paginationConfig);

                    // Translate IDs of referenced entities to a Code/description representation
                    for (Entry<String, CETEntityTypeFieldInfo> referenceInfo : entityReferences.entrySet()) {

                        String dbFieldname = referenceInfo.getKey();
                        CETEntityTypeFieldInfo reference = referenceInfo.getValue();

                        // Nothing else to retrieve for Custom table - there is no clear code/description field equivalent
                        if (reference.referenceType == EntityFieldTypeReference.CT) {

                            for (Map<String, Object> dataRow : data) {
                                if (dataRow.get(dbFieldname) != null) {
                                    dataRow.put(dbFieldname + FIELD_ENTITY_TXT_SUFFIX, dataRow.get(dbFieldname));
                                }
                            }
                            continue;
                        }

                        List<Long> ids = data.stream().filter(values -> values.get(dbFieldname) != null).map(values -> Long.valueOf(values.get(dbFieldname).toString())).collect(Collectors.toList());

                        if (!ids.isEmpty()) {

                            PaginationConfiguration criteria = new PaginationConfiguration(Map.of("inList " + NativePersistenceService.FIELD_ID, ids));

                            List<String> fetchFields = new ArrayList<String>();
                            fetchFields.add(NativePersistenceService.FIELD_ID);
                            fetchFields.addAll(reference.getFieldDBfieldnames());

                            criteria.setFetchFields(fetchFields);

                            List<Map<String, Object>> values = customTableService.list(referenceInfo.getValue().tableName, criteria);
                            Map<String, String> valuesById = values.stream().collect(Collectors.toMap(map -> map.get(NativePersistenceService.FIELD_ID).toString(), map -> {

                                List<String> refValues = new ArrayList<String>();
                                for (String field : reference.getFieldDBfieldnames()) {
                                    if (map.get(field) != null) {
                                        refValues.add(map.get(field).toString());
                                    }
                                }
                                return org.meveo.commons.utils.StringUtils.concatenate(" / ", refValues);

                            })

                            );

                            for (Map<String, Object> dataRow : data) {
                                if (dataRow.get(dbFieldname) != null && valuesById.containsKey(dataRow.get(dbFieldname).toString())) {
                                    dataRow.put(dbFieldname + FIELD_ENTITY_TXT_SUFFIX, valuesById.get(dataRow.get(dbFieldname).toString()));
                                }
                            }
                        }
                    }

                    return data;
                }
            };
        }

        return customTableBasedDataModel;
    }

    /**
     * Clean search fields in datatable.
     */
    public void clean() {
        customTableBasedDataModel = null;
        filters = new HashMap<>();
    }

    /**
     * Clean entity field search criteria and data model.
     * 
     * @param field Custom field template definition of entity field
     */
    public void cleanFieldSearch(CustomFieldTemplate field) {

        CETEntityTypeFieldInfo entityReferenceInfo = entityReferences.get(field.getDbFieldname());
        entityReferenceInfo.dataModel = null;
        entityReferenceInfo.dataModelFilter = new HashMap<>();
    }

    /**
     * Clean entity field search data model.
     * 
     * @param field Custom field template definition of entity field
     */
    public void cleanFieldDataModel(CustomFieldTemplate field) {
        CETEntityTypeFieldInfo entityReferenceInfo = entityReferences.get(field.getDbFieldname());
        entityReferenceInfo.dataModel = null;
    }

    /**
     * Clean table row values data entry
     */
    public void cleanRowValues() {
        rowValues.clear();
    }

    /**
     * @return Custom table name
     */
    public String getCustomTableName() {
        if (customTableName == null) {
            initEntity();
        }
        return customTableName;
    }

    /**
     * @param customTableName Custom table name
     */
    public void setCustomTableName(String customTableName) {
        this.customTableName = customTableName;
    }

    /**
     * @return Custom table fields
     */
    public List<CustomFieldTemplate> getFields() {
        if (entity == null) {
            initEntity();
        }
        return fields;
    }

    /**
     * @param field Custom table field definition
     * @return Fields
     */
    public List<CustomFieldTemplate> getEntityTypeFields(CustomFieldTemplate field) {
        if (entity == null) {
            initEntity();
        }
        return entityReferences.get(field.getDbFieldname()).fields;
    }

    @Override
    protected IPersistenceService<CustomEntityTemplate> getPersistenceService() {
        return customEntityTemplateService;
    }

    /**
     * @param event the Value in Datatable edit event
     * @throws BusinessException General exception
     */
    @SuppressWarnings("unchecked")
    @ActionMethod
    public void onCellEdit(CellEditEvent event) throws BusinessException {
        DataTable o = (DataTable) event.getSource();
        Map<String, Object> mapValue = (Map<String, Object>) o.getRowData();
        customTableService.update(customTableName, mapValue);
        messages.info(new BundleKey("messages", "customTable.valuesSaved"));
    }

    /**
     * @return Selected or new data row field values
     */
    public Map<String, Object> getRowValues() {
        return rowValues;
    }

    /**
     * @param rowValues Selected or new data row field values
     */
    public void setRowValues(Map<String, Object> rowValues) {
        this.rowValues = rowValues;
    }

    /**
     * Add new values to a map of values, setting a default value if applicable
     *
     * @throws BusinessException General exception
     */
    @ActionMethod
    public void addUpdateRowValues() throws BusinessException {

        // Remove any entity id to label translation fields before saving
        Map<String, Object> rowValuesCopy = new HashMap<String, Object>(rowValues);
        for (String fieldname : rowValues.keySet()) {
            if (fieldname.endsWith(FIELD_ENTITY_TXT_SUFFIX)) {
                rowValuesCopy.remove(fieldname);
            }
        }

        Map<String, Object> convertedValues = customTableService.convertValue(rowValuesCopy, fields, false, null);

        if (isUpdate()) {
            customTableService.update(customTableName, convertedValues);
        } else {
            customTableService.create(customTableName, convertedValues);
        }

        messages.info(new BundleKey("messages", "customTable.valuesSaved"));
        rowValues = new HashMap<>();
        customTableBasedDataModel = null;
    }

    /**
     * Handle a file upload and import the file
     *
     * @param event File upload event
     * @throws BusinessException
     * @throws IOException
     */
    @ActionMethod
    public void handleFileUpload(FileUploadEvent event) throws BusinessException, IOException {
        UploadedFile file = event.getFile();

        if (file == null) {
            messages.warn(new BundleKey("messages", "customTable.importFile.fileRequired"));
            return;
        }

        try {
            importFuture = customTableService.importDataAsync(entity, file.getInputstream(), appendImportedData);
            messages.info(new BundleKey("messages", "customTable.importFile.started"));

        } catch (Exception e) {
            log.error("Failed to initialize custom table data import", e);
            messages.info(new BundleKey("messages", "customTable.importFile.startFailed"), e.getMessage());
        }

    }

    /**
     * A current values refer to new values or an update
     * 
     * @return True if row values contain a field "id"
     */
    public boolean isUpdate() {
        return rowValues.containsKey(NativePersistenceService.FIELD_ID);
    }

    public boolean isAppendImportedData() {
        return appendImportedData;
    }

    public void setAppendImportedData(boolean appendImportedData) {
        this.appendImportedData = appendImportedData;
    }

    public List<Map<String, Object>> getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(List<Map<String, Object>> selectedValues) {
        this.selectedValues = selectedValues;
    }

    /**
     * Construct a CSV file format (header) for file import
     *
     * @return CSV file field order
     */
    public String getCsvFileFormat() {
        StringBuffer format = new StringBuffer();

        format.append(NativePersistenceService.FIELD_ID).append("(optional)");

        for (CustomFieldTemplate field : fields) {
            format.append(",");
            format.append(field.getDbFieldname());
        }

        return format.toString();
    }

    @Override
    @ActionMethod
    public void delete(Long id) throws BusinessException {
        customTableService.remove(customTableName, id);
        customTableBasedDataModel = null;
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    @Override
    @ActionMethod
    public void deleteMany() throws BusinessException {

        if (selectedValues == null || selectedValues.isEmpty()) {
            messages.info(new BundleKey("messages", "delete.entitities.noSelection"));
            return;
        }
        Set<Long> ids = new HashSet<>();

        for (Map<String, Object> values : selectedValues) {

            Object id = values.get(NativePersistenceService.FIELD_ID);
            if (id instanceof String) {
                id = Long.parseLong((String) id);
            } else if (id instanceof Number) {
                id = ((Number) id).longValue();
            }
            ids.add((long) id);

        }

        customTableService.remove(customTableName, ids);
        customTableBasedDataModel = null;
        messages.info(new BundleKey("messages", "delete.entitities.successful"));
    }

    @ActionMethod
    public void exportData() {
        exportFuture = null;

        PaginationConfiguration config = new PaginationConfiguration(filters, "id", PagingAndFiltering.SortOrder.ASCENDING);

        try {
            exportFuture = customTableService.exportData(entity, config);
            messages.info(new BundleKey("messages", "customTable.exportFile.started"));

        } catch (Exception e) {
            log.error("Failed to initialize custom table data export", e);
            messages.info(new BundleKey("messages", "customTable.exportFile.startFailed"), e.getMessage());
        }
    }

    public Future<DataImportExportStatistics> getExportFuture() {
        return exportFuture;
    }

    public Future<DataImportExportStatistics> getImportFuture() {
        return importFuture;
    }

    /**
     * Entity field value selection from a datalist event
     * 
     * @param selectEvent
     */
    public void onEntitySelect(SelectEvent selectEvent) {

        @SuppressWarnings("unchecked")
        Map<String, Object> valueSelected = (Map<String, Object>) selectEvent.getObject();
        Long id = Long.valueOf(valueSelected.get(NativePersistenceService.FIELD_ID).toString());

        // Data table component id is a <dbFieldname>_datatable
        String dbFieldname = ((DataTable) selectEvent.getSource()).getId().substring(0, ((DataTable) selectEvent.getSource()).getId().indexOf("_datatable"));
        rowValues.put(dbFieldname, id);

        CETEntityTypeFieldInfo reference = entityReferences.get(dbFieldname);

        String idPlusDescriptionOrCode = id.toString();

        if (reference.referenceType != EntityFieldTypeReference.CT) {

            List<String> values = new ArrayList<String>();
            for (String field : reference.getFieldDBfieldnames()) {
                if (valueSelected.get(field) != null) {
                    values.add(valueSelected.get(field).toString());
                }
            }
            idPlusDescriptionOrCode = org.meveo.commons.utils.StringUtils.concatenate(" / ", values);

        }
        rowValues.put(dbFieldname + FIELD_ENTITY_TXT_SUFFIX, idPlusDescriptionOrCode);
    }

    /**
     * Get entity reference data model filter
     * 
     * @param field Entity type field
     * @return A map with data model filter values
     */
    public Map<String, Object> getFieldFilters(CustomFieldTemplate field) {
        CETEntityTypeFieldInfo entityReferenceInfo = entityReferences.get(field.getDbFieldname());
        return entityReferenceInfo.dataModelFilter;
    }

//    public void setFieldFilters(Map<String, Object>> fieldFilters) {
//        this.fieldFilters = fieldFilters;
//    }

    /**
     * Get a list of custom entity template fields. Fields are sorted by GUI order.
     * 
     * @param cet Custom entity template
     * @param limit Number of fields to return
     * @return A list of custom entity templates
     */
    private List<CustomFieldTemplate> getCETFields(CustomEntityTemplate cet, Integer limit) {

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts != null) {
            List<CustomFieldTemplate> cetFields = new ArrayList<>(cfts.values());

            Collections.sort(cetFields, new Comparator<CustomFieldTemplate>() {

                @Override
                public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                    int pos1 = cft1.getGUIFieldPosition();
                    int pos2 = cft2.getGUIFieldPosition();

                    return pos1 - pos2;
                }
            });

            if (limit == null) {
                return cetFields;
            } else {
                return cetFields.subList(0, cetFields.size() < limit ? cetFields.size() : limit);
            }
        }
        return new ArrayList<CustomFieldTemplate>();
    }

    enum EntityFieldTypeReference {
        CT, CI, BusinessEntity;
    }

    /**
     * Information about entity that entity type field reference to
     * 
     * @author Andrius Karpavicius
     */
    private class CETEntityTypeFieldInfo {
        /**
         * Entity reference type
         */
        EntityFieldTypeReference referenceType;

        /**
         * Custom entity template code when reference type is Custom entity instance
         */
        String cetCode;

        /**
         * A corresponding table name
         */
        String tableName;

        /**
         * Fields to retrieve when presenting reference information
         */
        List<CustomFieldTemplate> fields;

        /**
         * Filter for data model
         */
        Map<String, Object> dataModelFilter = new HashMap<String, Object>();

        /**
         * Data model
         */
        LazyDataModel<Map<String, Object>> dataModel;

        /**
         * Get a list of DB field names corresponding to the fields
         * 
         * @return A list of DB field names
         */
        List<String> getFieldDBfieldnames() {

            List<String> dbFieldnames = new ArrayList<String>();
            for (CustomFieldTemplate cft : fields) {
                dbFieldnames.add(cft.getDbFieldname());
            }
            return dbFieldnames;
        }
    }

    public boolean isNewEntity() {
        return newEntity;
    }

    public void setNewEntity(boolean newEntity) {
        this.newEntity = newEntity;
    }
}