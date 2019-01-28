package org.meveo.admin.action.admin.custom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.util.view.NativeTableBasedDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.LazyDataModel;

@Named
@ViewScoped
public class CustomTableBean extends BaseBean<CustomEntityTemplate> {

    private static final long serialVersionUID = -2748591950645172132L;

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
    private Collection<CustomFieldTemplate> fields;

    private LazyDataModel<Map<String, Object>> customTableBasedDataModel;

    private Map<String, Object> newValues = new HashMap<String, Object>();

    public CustomTableBean() {
        super(CustomEntityTemplate.class);
    }

    @Override
    public CustomEntityTemplate initEntity() {
        super.initEntity();

        customTableName = entity.getCode();

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(entity.getAppliesTo());
        if (cfts != null) {
            fields = cfts.values();
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
        filters = new HashMap<String, Object>();
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
    public Collection<CustomFieldTemplate> getFields() {
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
    public void addValueToMap() throws BusinessException {
        customTableService.create(customTableName, newValues);
        messages.info(new BundleKey("messages", "customTable.valuesSaved"));
        newValues = new HashMap<>();
        customTableBasedDataModel = null;
    }

    // Bellow is implementation when value changes are saved in bulk
    //
    //
    // private List<Map<String, Object>> dirtyValues = new ArrayList<>();
    //
    // private Set<Long> dirtyIds = new HashSet<>();
    //
    // /**
    // * @param event the Value in Datatable edit event
    // */
    // @SuppressWarnings("unchecked")
    // @ActionMethod
    // public void onCellEdit(CellEditEvent event) {
    // DataTable o = (DataTable) event.getSource();
    // Map<String, Object> mapValue = (Map<String, Object>) o.getRowData();
    // Long id = (Long) mapValue.get("id");
    // if (!dirtyIds.contains(id)) {
    // dirtyIds.add(id);
    // dirtyValues.add(mapValue);
    // log.debug("Changed custom table value for ID {}", id);
    // }
    // }
    //
    // /**
    // * Update custom table with new or modified values
    // *
    // * @throws BusinessException
    // */
    // @ActionMethod
    // public void save() throws BusinessException {
    //
    // if (dirtyValues.isEmpty()) {
    // return;
    // }
    // customTableService.createOrUpdate(customTableName, dirtyValues);
    //
    // dirtyValues = new ArrayList<>();
    // dirtyIds = new HashSet<>();
    // customTableBasedDataModel = null;
    // messages.info(new BundleKey("messages", "customTable.valuesSaved"));
    // }
    //
    // @ActionMethod
    // public void reset() {
    //
    // dirtyValues = new ArrayList<>();
    // dirtyIds = new HashSet<>();
    // customTableBasedDataModel = null;
    // messages.info(new BundleKey("messages", "customTable.valuesReset"));
    // }
    //
    // /**
    // * Add new values to a map of values, setting a default value if applicable
    // */
    // public void addValueToMap() {
    // dirtyValues.add(newValues);
    // newValues = new HashMap<>();
    // }
}