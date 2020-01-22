package org.meveo.admin.action.admin.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.custom.DataImportExportStatistics;
import org.meveo.util.view.NativeTableBasedDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

@Named
@ViewScoped
public class CustomTableBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = -2748591950645172132L;
    @Inject
    private CustomTableService customTableService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

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

    private Map<String, Object> newValues = new HashMap<>();

    private boolean appendImportedData;

    private List<Map<String, Object>> selectedValues;

    private Future<DataImportExportStatistics> exportFuture;

    private Future<DataImportExportStatistics> importFuture;

    public CustomTableBean() {
        super(CustomEntityTemplate.class);
    }

    @Override
    public CustomEntityTemplate initEntity() {
        super.initEntity();

        customTableName = entity.getDbTablename();

        // Get fields and sort them by GUI order
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity.getAppliesTo());
        if (cfts != null) {
            fields = new ArrayList<>(cfts.values());

            Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

                @Override
                public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                    int pos1 = cft1.getGUIFieldPosition();
                    int pos2 = cft2.getGUIFieldPosition();

                    return pos1 - pos2;
                }
            });
        }

        return entity;
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

    public Map<String, Object> getNewValues() {
        return newValues;
    }

    public void setNewValues(Map<String, Object> newValues) {
        this.newValues = newValues;
    }

    /**
     * Add new values to a map of values, setting a default value if applicable
     *
     * @throws BusinessException General exception
     */
    @ActionMethod
    public void addNewValues() throws BusinessException {

        Map<String, Object> convertedValues = customTableService.convertValue(newValues, fields, false, null);

        customTableService.create(customTableName, convertedValues);
        messages.info(new BundleKey("messages", "customTable.valuesSaved"));
        newValues = new HashMap<>();
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

        PaginationConfiguration config = new PaginationConfiguration(filters, "id", SortOrder.ASCENDING);

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

    public List<CustomTableRecordDto> entityTypeColumnDatas(CustomFieldTemplate field) {
        CustomEntityTemplate relatedEntity = customEntityTemplateService.findByCode(field.tableName());
        if (relatedEntity != null && relatedEntity.isStoreAsTable()) {
            return customTableService.selectAllRecordsOfATableAsRecord(field.tableName(),null);
        }
        return getFromCustomEntity(field);
    }

    List<CustomTableRecordDto> getFromCustomEntity(CustomFieldTemplate field) {

        return Optional.ofNullable(field.tableName())
                .map(tableName -> customEntityInstanceService.listByCet(field.tableName()).stream().map(customEntityInstanceService::customEntityInstanceAsMap)
                        .map(x-> new CustomTableRecordDto(x,tableName)).collect(Collectors.toList())).orElse(loadFromBusinessEntity(field));
    }

    List<CustomTableRecordDto> loadFromBusinessEntity(CustomFieldTemplate field) {
        try {
            Class entityClass = Class.forName(field.getEntityClazz());
            PersistenceService<BaseEntity> persistenceService = getPersistenceServiceByClass(entityClass);
            return persistenceService.list().stream()
                    .filter(Objects::nonNull)
                    .map(this::mapToMap)
                    .map(x-> new CustomTableRecordDto(x,field.tableName()))
                    .collect(Collectors.toList());
        } catch (ClassNotFoundException e) {
            return Collections.EMPTY_LIST;
        }
    }

    HashMap<String, Object> mapToMap(Object entity) {
        if (entity instanceof BusinessEntity) {
            return convertBusinessEntity((BusinessEntity) entity);
        }
        return convertIdAndIdentifiers((BaseEntity) entity);
    }

    HashMap<String, Object> convertIdAndIdentifiers(BaseEntity baseEntity) {
        HashMap<String, Object> convertedValues = new HashMap<>();
        convertedValues.put("id", baseEntity.getId());
        addExternalIdentifiers(baseEntity, convertedValues);
        return convertedValues;
    }

    private void addExternalIdentifiers(BaseEntity baseEntity, HashMap<String, Object> convertedValues) {
        if (baseEntity.getClass().isAnnotationPresent(ExportIdentifier.class)) {
            Arrays.asList(baseEntity.getClass().getAnnotation(ExportIdentifier.class).value()).forEach(v -> {
                try {
                    convertedValues.put(v, ReflectionUtils.getPropertyValue(baseEntity, v));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private HashMap<String, Object> convertBusinessEntity(BusinessEntity e) {
        return new HashMap<String, Object>() {{
            put("id", e.getId());
            put("code", e.getCode());
            put("description", e.getDescription());
        }};
    }

    PersistenceService getPersistenceServiceByClass(Class entityClass) {
        return (PersistenceService) EjbUtils.getServiceInterface(entityClass.getSimpleName() + "Service");
    }

}