package org.meveo.admin.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.admin.custom.GroupedCustomField;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldMapKeyEnum;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.CustomFieldValue;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.EntityActionScriptService;

/**
 * Backing bean for support custom field instances value data entry
 * 
 * @param <T>
 */
public abstract class CustomFieldBean<T extends IEntity> extends BaseBean<T> {

    private static final long serialVersionUID = 1L;

    private CustomFieldTemplate customFieldSelectedTemplate;

    private CustomFieldInstance customFieldSelectedPeriod;

    private String customFieldSelectedPeriodId;

    private boolean customFieldPeriodMatched;

    private Map<String, Object> customFieldNewValue = new HashMap<String, Object>();

    private Map<String, Object> customFieldValues = new HashMap<String, Object>();

    private Map<String, Set<String>> matrixColumns = new HashMap<String, Set<String>>();

    /**
     * Custom field templates grouped into tabs and field groups
     */
    private GroupedCustomField groupedCustomField = null;

    private List<EntityActionScript> customActions = new ArrayList<EntityActionScript>();

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private EntityActionScriptService entityActionScriptService;

    public CustomFieldBean() {
    }

    public CustomFieldBean(Class<T> clazz) {
        super(clazz);
    }

    protected abstract IPersistenceService<T> getPersistenceService();

    @Override
    public T initEntity() {
        T result = super.initEntity();
        initCustomFields();
        initCustomActions();
        return result;
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        boolean isNew = entity.isTransient();
        String outcome = super.saveOrUpdate(killConversation);
        updateCustomFieldsInEntity(isNew);
        return outcome;
    }

    /**
     * Load available custom actions
     */
    private void initCustomActions() {
        customActions.clear();
        Map<String, EntityActionScript> actions = entityActionScriptService.findByAppliesTo((ICustomFieldEntity) entity, getCurrentProvider());
        if (actions != null) {
            customActions.addAll(actions.values());
        }
    }

    /**
     * Load available custom fields (templates) and their values
     */
    protected void initCustomFields() {

        Map<String, CustomFieldTemplate> customFieldTemplates = getApplicableCustomFieldTemplates();

        customFieldValues.clear();

        if (customFieldTemplates != null && customFieldTemplates.size() > 0) {

            Map<String, List<CustomFieldInstance>> cfisAsMap = customFieldInstanceService.getCustomFieldInstances((ICustomFieldEntity) entity);

            // For each template, check if custom field value exists, and instantiate one if needed with a default value
            for (CustomFieldTemplate cft : customFieldTemplates.values()) {
                List<CustomFieldInstance> cfisByTemplate = cfisAsMap.get(cft.getCode());

                if (cfisByTemplate == null) {
                    cfisByTemplate = new ArrayList<>();
                }

                if (cfisByTemplate.isEmpty() && !cft.isVersionable()) {
                    cfisByTemplate.add(CustomFieldInstance.fromTemplate(cft, (ICustomFieldEntity) entity));
                }

                for (CustomFieldInstance cfi : cfisByTemplate) {
                    deserializeForGUI(cft, cfi.getCfValue());
                }

                if (cft.isVersionable()) {
                    customFieldValues.put(cft.getCode(), cfisByTemplate);
                } else {
                    customFieldValues.put(cft.getCode(), cfisByTemplate.get(0));
                }

                // Retrieve columns for Matrix data entry
                if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
                    populateMatrixColumns(cft.getCode(), cfisByTemplate);
                }
            }

            populateCustomFieldNewValueDefaults(customFieldTemplates.values());
            groupedCustomField = new GroupedCustomField(customFieldTemplates.values(), "Custom fields", false);
        }

    }

    /**
     * Retrieve matrix columns from custom field instances
     * 
     * @param code Custom field template code
     * @param cfis Custom field instances
     */
    private void populateMatrixColumns(String code, List<CustomFieldInstance> cfis) {

        Set<String> cftColumns = new HashSet<String>();
        matrixColumns.put(code, cftColumns);

        for (CustomFieldInstance cfi : cfis) {
            for (Map<String, Object> columnInfo : cfi.getCfValue().getMatrixValuesForGUI().values()) {
                cftColumns.addAll(columnInfo.keySet());
            }
        }
    }

    /**
     * Load available custom fields (templates) and their values
     * 
     * @return Custom field information
     */
    public GroupedCustomField getGroupedCustomFieldWithInit() {
        if (groupedCustomField == null) {
            if (entity == null) {
                initEntity();
            } else {
                initCustomFields();
            }
        }
        return groupedCustomField;
    }

    /**
     * Save custom fields
     * 
     * @param isNewEntity Is it a new entity
     * @throws BusinessException
     */
    private void updateCustomFieldsInEntity(boolean isNewEntity) throws BusinessException {
        if (groupedCustomField != null && groupedCustomField.getFields().size() > 0) {
            for (CustomFieldTemplate cft : groupedCustomField.getFields()) {
                List<CustomFieldInstance> cfis = getInstancesAsList(cft);

                for (CustomFieldInstance cfi : cfis) {
                    // Not saving empty values unless template has a default value or is versionable (to prevent that for SINGLE type CFT with a default value, value is
                    // instantiates automatically)
                    // Also don't save if CFT does not apply in a given entity lifecycle or because cft.applicableOnEL evaluates to false
                    if ((cfi.isValueEmptyForGui() && (cft.getDefaultValue() == null || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE) && !cft.isVersionable())
                            || ((isNewEntity && cft.isHideOnNew()) || !ValueExpressionWrapper.evaluateToBoolean(cft.getApplicableOnEl(), "entity", entity))) {
                        if (!cfi.isTransient()) {
                            customFieldInstanceService.remove(cfi, (ICustomFieldEntity) entity);
                            log.trace("Remove empty cfi value {}", cfi);
                        } else {
                            log.error("Will ommit from saving cfi {}", cfi);
                        }

                        // Do not update existing CF value if it is not updatable
                    } else if (!isNewEntity && !cft.isAllowEdit()) {
                        continue;

                        // Existing value update
                    } else {
                        serializeForGUI(cft, cfi.getCfValue());
                        if (cfi.isTransient()) {
                            customFieldInstanceService.create(cfi, (ICustomFieldEntity) entity, getCurrentUser(), getCurrentProvider());
                        } else {
                            customFieldInstanceService.update(cfi, getCurrentUser());
                        }
                    }
                }
            }
        }
    }

    private void deserializeForGUI(CustomFieldTemplate cft, CustomFieldValue cfv) {

        // Convert just Entity type field to a JPA object
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                cfv.setEntityReferenceValueForGUI(deserializeEntityReferenceForGUI(cfv.getEntityReferenceValue()));
            }

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Map<String, Object>> listOfMapValues = new ArrayList<Map<String, Object>>();
            if (cfv.getListValue() != null) {
                for (Object listItem : cfv.getListValue()) {
                    Map<String, Object> listEntry = new HashMap<String, Object>();
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        listEntry.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) listItem));
                    } else {
                        listEntry.put(CustomFieldValue.MAP_VALUE, listItem);
                    }
                    listOfMapValues.add(listEntry);
                }
            }
            cfv.setMapValuesForGUI(listOfMapValues);

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            List<Map<String, Object>> listOfMapValues = new ArrayList<Map<String, Object>>();

            if (cfv.getMapValue() != null) {
                for (Entry<String, Object> mapInfo : cfv.getMapValue().entrySet()) {
                    Map<String, Object> listEntry = new HashMap<String, Object>();
                    listEntry.put(CustomFieldValue.MAP_KEY, mapInfo.getKey());
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        listEntry.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) mapInfo.getValue()));
                    } else {
                        listEntry.put(CustomFieldValue.MAP_VALUE, mapInfo.getValue());
                    }
                    listOfMapValues.add(listEntry);
                }
            }
            cfv.setMapValuesForGUI(listOfMapValues);

            // Populate matrixValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

            Map<String, Map<String, Object>> mapValues = new HashMap<String, Map<String, Object>>();

            if (cfv.getMapValue() != null) {
                for (Entry<String, Object> mapInfo : cfv.getMapValue().entrySet()) {
                    String[] fromTo = mapInfo.getKey().split("\\" + CustomFieldInstanceService.MATRIX_VALUE_SEPARATOR);
                    if (!mapValues.containsKey(fromTo[0])) {
                        mapValues.put(fromTo[0], new HashMap<String, Object>());
                    }
                    mapValues.get(fromTo[0]).put(fromTo[1], mapInfo.getValue());
                }
            }
            cfv.setMatrixValuesForGUI(mapValues);
        }
    }

    /**
     * Covert entity reference to a Business entity JPA object.
     * 
     * @param entityReferenceValue Entity reference value
     * @return Business entity JPA object
     */
    private BusinessEntity deserializeEntityReferenceForGUI(EntityReferenceWrapper entityReferenceValue) {
        if (entityReferenceValue == null) {
            return null;
        }
        // NOTE: For PF autocomplete seems that fake BusinessEntity object with code value filled is sufficient - it does not have to be a full loaded JPA object

        // BusinessEntity convertedEntity = customFieldInstanceService.convertToBusinessEntityFromCfV(entityReferenceValue, this.currentProvider);
        // if (convertedEntity == null) {
        // convertedEntity = (BusinessEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
        // if (convertedEntity != null) {
        // convertedEntity.setCode("NOT FOUND: " + entityReferenceValue.getCode());
        // }
        // } else {

        try {
            BusinessEntity convertedEntity = (BusinessEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
            if (convertedEntity != null) {
                convertedEntity.setCode(entityReferenceValue.getCode());
            } else {
                log.error("Unknown entity class specified " + entityReferenceValue.getClassname() + "in a custom field value {} ", entityReferenceValue);
            }
            // }
            return convertedEntity;

        } catch (Exception e) {
            log.error("Unknown entity class specified in a custom field value {} ", entityReferenceValue);
            return null;
        }
    }

    private void serializeForGUI(CustomFieldTemplate cft, CustomFieldValue cfv) {

        // Convert just Entity type field to a JPA object
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                if (cfv.getEntityReferenceValueForGUI() == null) {
                    cfv.setEntityReferenceValue(null);
                } else {
                    cfv.setEntityReferenceValue(new EntityReferenceWrapper(cfv.getEntityReferenceValueForGUI()));
                }
            }

            // Populate customFieldValue.listValue from mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Object> listValue = new ArrayList<Object>();
            for (Map<String, Object> listItem : cfv.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    listValue.add(new EntityReferenceWrapper((BusinessEntity) listItem.get(CustomFieldValue.MAP_VALUE)));
                } else {
                    listValue.add(listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            cfv.setListValue(listValue);

            // Populate customFieldValue.mapValue from mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            Map<String, Object> mapValue = new HashMap<String, Object>();

            for (Map<String, Object> listItem : cfv.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), new EntityReferenceWrapper((BusinessEntity) listItem.get(CustomFieldValue.MAP_VALUE)));
                } else {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            cfv.setMapValue(mapValue);

            // Populate customFieldValue.mapValue from matrixValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

            Map<String, Object> mapValue = new HashMap<String, Object>();

            for (Entry<String, Map<String, Object>> mapInfo : cfv.getMatrixValuesForGUI().entrySet()) {

                for (Entry<String, Object> valueInfo : mapInfo.getValue().entrySet()) {
                    if (!StringUtils.isBlank(valueInfo.getValue())) {
                        mapValue.put(mapInfo.getKey() + CustomFieldInstanceService.MATRIX_VALUE_SEPARATOR + valueInfo.getKey(), valueInfo.getValue());
                    }
                }
            }

            cfv.setMapValue(mapValue);
        }
    }

    public GroupedCustomField getGroupedCustomField() {
        return groupedCustomField;
    }

    public CustomFieldInstance getCustomFieldSelectedPeriod() {
        return customFieldSelectedPeriod;
    }

    public void setCustomFieldSelectedPeriod(CustomFieldInstance customFieldSelectedPeriod) {
        this.customFieldSelectedPeriod = customFieldSelectedPeriod;
    }

    public void setCustomFieldSelectedTemplate(CustomFieldTemplate customFieldSelectedTemplate) {
        this.customFieldSelectedTemplate = customFieldSelectedTemplate;
    }

    public CustomFieldTemplate getCustomFieldSelectedTemplate() {
        return customFieldSelectedTemplate;
    }

    public String getCustomFieldSelectedPeriodId() {
        return customFieldSelectedPeriodId;
    }

    public void setCustomFieldSelectedPeriodId(String customFieldSelectedPeriodId) {
        this.customFieldSelectedPeriodId = customFieldSelectedPeriodId;
    }

    public boolean isCustomFieldPeriodMatched() {
        return customFieldPeriodMatched;
    }

    /**
     * Add a new customField period with a previous validation that matching period does not exists
     */
    public void addNewCustomFieldPeriod(CustomFieldTemplate cft) {

        Date periodStartDate = (Date) customFieldNewValue.get(cft.getCode() + "_periodStartDate");
        Date periodEndDate = (Date) customFieldNewValue.get(cft.getCode() + "_periodEndDate");
        Object value = customFieldNewValue.get(cft.getCode() + "_value");

        // Check that two dates are one after another
        if (periodStartDate != null && periodEndDate != null && periodStartDate.compareTo(periodEndDate) >= 0) {
            messages.error(new BundleKey("messages", "customFieldTemplate.periodIntervalIncorrect"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        CustomFieldInstance period = null;
        // First check if any period matches the dates
        if (!customFieldPeriodMatched) {
            if (periodStartDate == null && periodEndDate == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.periodDatesBothNull"));
                customFieldPeriodMatched = true;
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            boolean strictMatch = false;
            if (cft.getCalendar() != null) {
                period = getValuePeriod(cft, (ICustomFieldEntity) entity, periodStartDate, false);
                strictMatch = true;
            } else {
                period = getValuePeriod(cft, (ICustomFieldEntity) entity, periodStartDate, periodEndDate, false, false);
                if (period != null) {
                    strictMatch = period.isCorrespondsToPeriod(periodStartDate, periodEndDate, true);
                }
            }

            if (period != null) {
                customFieldPeriodMatched = true;
                ParamBean paramBean = ParamBean.getInstance();
                String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");

                // For a strict match need to edit an existing period
                if (strictMatch) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound.noNew"),
                        DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern), DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                    customFieldPeriodMatched = false;

                    // For a non-strict match user has an option to create a period with a higher priority
                } else {
                    messages.warn(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound"), DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern),
                        DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                    customFieldPeriodMatched = true;
                }
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        // Create period if passed a period check or if user decided to create it anyway
        if (cft.getCalendar() != null) {
            period = addValuePeriod(cft, (ICustomFieldEntity) entity, periodStartDate);

        } else {
            period = addValuePeriod(cft, (ICustomFieldEntity) entity, periodStartDate, periodEndDate);
        }

        // Set value
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            period.getCfValue().setSingleValue(value, cft.getFieldType());
            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                period.getCfValue().setEntityReferenceValueForGUI((BusinessEntity) value);
            }
        }

        // } else {
        // Map<String, Object> newValue = new HashMap<String, Object>();
        // if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
        // newValue.put("key", key);
        // }
        // newValue.put("value", value);
        // period.getCfValue().getMapValuesForGUI().add(newValue);
        // }

        populateCustomFieldNewValueDefaults(null);
        customFieldPeriodMatched = false;
        customFieldSelectedTemplate = cft;
        customFieldSelectedPeriod = period;
    }

    /**
     * Add value to a map of values, setting a default value if applicable
     * 
     * @param cft Custom field template corresponding to an instance
     */
    public void addValueToMap(CustomFieldValue cfv, CustomFieldTemplate cft) {

        String newKey = null;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                newKey = (String) customFieldNewValue.get(cft.getCode() + "_key");

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                // Validate that at least one value is provided and in correct order
                Double from = (Double) customFieldNewValue.get(cft.getCode() + "_key_one_from");
                Double to = (Double) customFieldNewValue.get(cft.getCode() + "_key_one_to");

                if (from == null && to == null) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;

                } else if (from != null && to != null && from.compareTo(to) >= 0) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }
                newKey = (from == null ? "" : from) + CustomFieldInstanceService.RON_VALUE_SEPARATOR + (to == null ? "" : to);
            }
        }

        Object newValue = customFieldNewValue.get(cft.getCode() + "_value");

        Map<String, Object> value = new HashMap<String, Object>();
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            value.put(CustomFieldValue.MAP_KEY, newKey);
        }
        value.put(CustomFieldValue.MAP_VALUE, newValue);

        // Validate that key or value is not duplicate
        for (Map<String, Object> mapItem : cfv.getMapValuesForGUI()) {
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && mapItem.get(CustomFieldValue.MAP_KEY).equals(newKey)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyExists"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST && mapItem.get(CustomFieldValue.MAP_VALUE).equals(newValue)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.listValueExists"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        cfv.getMapValuesForGUI().add(value);

        customFieldNewValue.clear();
    }

    public Map<String, Object> getCustomFieldNewValue() {
        return customFieldNewValue;
    }

    public void setCustomFieldNewValue(Map<String, Object> customFieldNewValue) {
        this.customFieldNewValue = customFieldNewValue;
    }

    public Map<String, Object> getCustomFieldValues() {
        return customFieldValues;
    }

    public void setCustomFieldValues(Map<String, Object> customFieldValues) {
        this.customFieldValues = customFieldValues;
    }

    @Override
    public List<EntityActionScript> getCustomActions() {
        return customActions;
    }

    public List<BusinessEntity> autocompleteCustomEntityForCFV(String wildcode) {
        String classname = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("classname");
        return customFieldInstanceService.findBusinessEntityForCFVByCode(classname, wildcode, this.currentProvider);
    }

    /**
     * Get a list of custom field templates applicable to an entity.
     * 
     * @return A map of custom field templates with template code as a key
     */
    protected Map<String, CustomFieldTemplate> getApplicableCustomFieldTemplates() {
        Map<String, CustomFieldTemplate> result = customFieldTemplateService.findByAppliesTo((ICustomFieldEntity) entity, getCurrentProvider());
        log.debug("Found {} custom field templates for entity {}", result.size(), entity.getClass());
        return result;
    }

    /**
     * Validate complex custom fields
     * 
     * @param event
     */
    public void validateCustomFields(ComponentSystemEvent event) {
        boolean valid = true;
        boolean isNewEntity = entity.isTransient();

        FacesContext fc = FacesContext.getCurrentInstance();
        if (groupedCustomField != null && groupedCustomField.getFields().size() > 0) {
            for (CustomFieldTemplate cft : groupedCustomField.getFields()) {

                // Ignore the validation on a field when creating entity and CFT.hideOnNew=true or editing entity and CFT.allowEdit=false or when CFT.applicableOnEL expression
                // evaluates to false
                if ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit())
                        || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                    continue;

                } else if (cft.isActive() && cft.isValueRequired() && (cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE || cft.isVersionable())) {

                    for (CustomFieldInstance cfi : getInstancesAsList(cft)) {

                        // Fail validation on non empty values
                        if (cfi.isValueEmptyForGui()) {

                            FacesMessage msg = new FacesMessage(resourceMessages.getString("javax.faces.component.UIInput.REQUIRED", cft.getDescription()));
                            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                            fc.addMessage(null, msg);
                            valid = false;
                        }
                    }
                }
            }
        }

        if (!valid) {

            fc.validationFailed();
            fc.renderResponse();
        }
    }

    /**
     * Get a custom field instance corresponding to a given date. Calendar is used to determine period start/end dates if requested to create one if not found
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @return Custom field period
     */
    @SuppressWarnings("unchecked")
    private CustomFieldInstance getValuePeriod(CustomFieldTemplate cft, ICustomFieldEntity entity, Date date, Boolean createIfNotFound) {
        CustomFieldInstance periodFound = null;
        for (CustomFieldInstance period : (List<CustomFieldInstance>) customFieldValues.get(cft.getCode())) {
            if (period.isCorrespondsToPeriod(date)) {
                // If calendar is used for versioning, then no periods can overlap
                if (cft.getCalendar() != null) {
                    periodFound = period;
                    break;
                    // Otherwise match the period with highest priority
                } else if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            }
        }

        if (periodFound == null && createIfNotFound && cft.getCalendar() != null) {
            periodFound = CustomFieldInstance.fromTemplate(cft, entity, date);
            ((List<CustomFieldInstance>) customFieldValues.get(cft.getCode())).add(periodFound);

        }
        return periodFound;
    }

    /**
     * Get a custom field instance corresponding to a given start and end date
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param date Date
     * @param createIfNotFound Should period be created if not found
     * @param calendar Calendar to determine period start/end dates when creating a new period
     * @param strictMatch Should a match occur only if start and end dates match. Non-strict match would match when dates overlap
     * @return Custom field period
     */
    @SuppressWarnings("unchecked")
    private CustomFieldInstance getValuePeriod(CustomFieldTemplate cft, ICustomFieldEntity entity, Date startDate, Date endDate, boolean strictMatch, Boolean createIfNotFound) {
        CustomFieldInstance periodFound = null;
        for (CustomFieldInstance period : (List<CustomFieldInstance>) customFieldValues.get(cft.getCode())) {
            if (period.isCorrespondsToPeriod(startDate, endDate, strictMatch)) {
                if (periodFound == null || periodFound.getPriority() < period.getPriority()) {
                    periodFound = period;
                }
            }
        }
        // Create a period if match not found
        if (periodFound == null && createIfNotFound) {
            periodFound = CustomFieldInstance.fromTemplate(cft, entity, startDate, endDate, getNextPriority(cft));
            ((List<CustomFieldInstance>) customFieldValues.get(cft.getCode())).add(periodFound);
        }
        return periodFound;
    }

    /**
     * Calculate the next priority value
     * 
     * @param cft Custom field template
     * @return Integer
     */
    @SuppressWarnings("unchecked")
    private int getNextPriority(CustomFieldTemplate cft) {
        int maxPriority = 0;
        for (CustomFieldInstance period : (List<CustomFieldInstance>) customFieldValues.get(cft.getCode())) {
            maxPriority = (period.getPriority() > maxPriority ? period.getPriority() : maxPriority);
        }
        return maxPriority + 1;
    }

    /**
     * Add a new custom field instance, corresponding to a given date
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param date Value date
     * @return Instantiated custom field instance corresponding to a value date period
     */
    private CustomFieldInstance addValuePeriod(CustomFieldTemplate cft, ICustomFieldEntity entity, Date date) {
        CustomFieldInstance period = getValuePeriod(cft, entity, date, true);
        return period;
    }

    /**
     * Add a new custom field instance, corresponding to a given date range
     * 
     * @param cft Custom field template
     * @param entity Entity
     * @param startDate Period strt date
     * @param endDate Period end date
     * @return Instantiated custom field instance corresponding to a value date period
     */
    private CustomFieldInstance addValuePeriod(CustomFieldTemplate cft, ICustomFieldEntity entity, Date startDate, Date endDate) {
        CustomFieldInstance period = getValuePeriod(cft, entity, startDate, endDate, true, true);
        return period;
    }

    /**
     * Get inherited custom field value
     * 
     * @param code Custom field code
     * @return Custom field value
     */
    public Object getInheritedCFValue(String code) {
        return customFieldInstanceService.getInheritedOnlyCFValue((ICustomFieldEntity) entity, code, getCurrentUser());
    }

    /**
     * Check if any instance value should be considered empty for GUI
     * 
     * @return
     */
    public boolean isAnyInstanceValueEmptyForGui(CustomFieldTemplate cft) {

        for (CustomFieldInstance cfi : getInstancesAsList(cft)) {
            if (!cfi.isValueEmptyForGui()) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldInstance> getInstancesAsList(CustomFieldTemplate cft) {
        List<CustomFieldInstance> cfis = new ArrayList<>();
        if (cft.isVersionable()) {
            cfis = (List<CustomFieldInstance>) customFieldValues.get(cft.getCode());
        } else {
            cfis.add((CustomFieldInstance) customFieldValues.get(cft.getCode()));
        }
        return cfis;
    }

    /**
     * Populate GUI "new value fields" with default values
     * 
     * @param cfts A list of custom field templates. Optional. If not provided - will be retrieved
     */
    private void populateCustomFieldNewValueDefaults(Collection<CustomFieldTemplate> cfts) {

        customFieldNewValue.clear();

        if (cfts == null) {
            cfts = getApplicableCustomFieldTemplates().values();
        }

        for (CustomFieldTemplate cft : cfts) {
            if (cft.isVersionable() && cft.getDefaultValue() != null && !customFieldNewValue.containsKey(cft.getCode() + "_value")) {
                customFieldNewValue.put(cft.getCode() + "_value", cft.getDefaultValueConverted());
            }
        }
    }

    /**
     * Clear value from GUI "new value field" - used when displaying a popup with period values
     * 
     * @param cft Custom field template
     */
    public void clearCustomFieldNewValueDefaults(CustomFieldTemplate cft) {
        customFieldNewValue.remove(cft.getCode() + "_value");
    }

    /**
     * Get columns for matrix data entry
     * 
     * @param matrixValues Matrix values
     * @return A set of column names
     */
    public Set<String> getMatrixColumns(String cftCode) {
        return matrixColumns.get(cftCode);
    }

    /**
     * Add column to a matrix. Also sets null value for new column in every row of matrix
     * 
     * @param cft Custom field template
     * @param matrixValues Matrix values
     */
    public void addMatrixColumn(CustomFieldTemplate cft, Map<String, Map<String, Object>> matrixValues) {

        String columnValue = null;

        if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {

            // Validate that at least one value is provided and in correct order
            Double from = (Double) customFieldNewValue.get(cft.getCode() + "_key_from");
            Double to = (Double) customFieldNewValue.get(cft.getCode() + "_key_to");

            if (from == null && to == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                FacesContext.getCurrentInstance().validationFailed();
                return;

            } else if (from != null && to != null && from.compareTo(to) >= 0) {
                messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
            columnValue = (from == null ? "" : from) + CustomFieldInstanceService.RON_VALUE_SEPARATOR + (to == null ? "" : to);
        } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
            columnValue = (String) customFieldNewValue.get(cft.getCode() + "_key");
        }

        if (matrixColumns.get(cft.getCode()).add(columnValue)) {
            customFieldNewValue.clear();

        } else {
            messages.error(new BundleKey("messages", "customFieldTemplate.columnExists"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }
    }

    /**
     * Add row to a matrix. Also sets null value for every column of matrix in a new row
     * 
     * @param cft Custom field template
     * @param matrixValues Matrix values
     */
    public void addMatrixRow(CustomFieldTemplate cft, Map<String, Map<String, Object>> matrixValues) {

        String rowValue = null;

        if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {

            // Validate that at least one value is provided and in correct order
            Double from = (Double) customFieldNewValue.get(cft.getCode() + "_key_from");
            Double to = (Double) customFieldNewValue.get(cft.getCode() + "_key_to");

            if (from == null && to == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                FacesContext.getCurrentInstance().validationFailed();
                return;

            } else if (from != null && to != null && from.compareTo(to) >= 0) {
                messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
            rowValue = (from == null ? "" : from) + CustomFieldInstanceService.RON_VALUE_SEPARATOR + (to == null ? "" : to);

        } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
            rowValue = (String) customFieldNewValue.get(cft.getCode() + "_key");
        }

        if (matrixValues.containsKey(rowValue)) {
            messages.error(new BundleKey("messages", "customFieldTemplate.rowExists"));
            FacesContext.getCurrentInstance().validationFailed();
            return;

        } else {
            Map<String, Object> rowValues = new HashMap<String, Object>();
            matrixValues.put(rowValue, rowValues);
            customFieldNewValue.clear();
        }

    }

    /**
     * Execute custom action on an entity
     * 
     * @param action Action to execute
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @param currentUser Current user
     * @param currentProvider Current provider
     * @return A script execution result value
     */
    public String executeCustomAction(EntityActionScript action, String encodedParameters) {

        try {
            entityActionScriptService.execute(entity, action.getCode(), encodedParameters, getCurrentUser(), getCurrentProvider());
            messages.info(new BundleKey("messages", "scriptInstance.actionExecutionSuccessfull"), action.getLabel());

        } catch (InstantiationException | IllegalAccessException | BusinessException e) {
            log.error("Failed to execute a script {} on entity {}", action.getCode(), entity);
            messages.error(new BundleKey("messages", "scriptInstance.actionExecutionFailed"), action.getLabel(), e.getMessage());
        }

        return null;
    }
}