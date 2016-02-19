package org.meveo.admin.action.admin.custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.action.admin.CurrentUser;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValueHolder;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.EntityActionScriptService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides support for custom field value data entry
 * 
 */
@Named
@ViewScoped
public class CustomFieldDataEntryBean implements Serializable {

    private static final long serialVersionUID = 2587695185934268809L;

    /**
     * Field used to show detail values of a single value period
     */
    private Map<String, CustomFieldTemplate> selectedFieldTemplate = new HashMap<String, CustomFieldTemplate>();

    /**
     * Field used to show detail values of a single value period
     */
    private Map<String, CustomFieldInstance> selectedValuePeriod = new HashMap<String, CustomFieldInstance>();

    /**
     * Field used to show detail values of a single value period
     */
    private Map<String, String> selectedValuePeriodId = new HashMap<String, String>();

    /**
     * Was value period found with identical/overlapping dates
     */
    private Map<String, Boolean> valuePeriodMatched = new HashMap<String, Boolean>();

    /**
     * Custom field templates grouped into tabs and field groups
     */
    private Map<String, GroupedCustomField> groupedFieldTemplates = new HashMap<String, GroupedCustomField>();

    /**
     * Custom actions applicable to the entity
     */
    private Map<String, List<EntityActionScript>> customActions = new HashMap<String, List<EntityActionScript>>();

    /**
     * Custom field values and new value GUI data entry values
     */
    private Map<String, CustomFieldValueHolder> fieldsValues = new HashMap<String, CustomFieldValueHolder>();

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private EntityActionScriptService entityActionScriptService;

    @Inject
    @CurrentProvider
    protected Provider currentProvider;

    @Inject
    @CurrentUser
    protected User currentUser;

    @Inject
    protected Messages messages;

    /** Logger. */
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public Map<String, CustomFieldInstance> getSelectedValuePeriod() {
        return selectedValuePeriod;
    }

    public void setSelectedValuePeriod(Map<String, CustomFieldInstance> selectedValuePeriod) {
        this.selectedValuePeriod = selectedValuePeriod;
    }

    public Map<String, CustomFieldTemplate> getSelectedFieldTemplate() {
        return selectedFieldTemplate;
    }

    public void setSelectedFieldTemplate(Map<String, CustomFieldTemplate> selectedFieldTemplate) {
        this.selectedFieldTemplate = selectedFieldTemplate;
    }

    public Map<String, String> getSelectedValuePeriodId() {
        return selectedValuePeriodId;
    }

    public void setSelectedValuePeriodId(Map<String, String> selectedValuePeriodId) {
        this.selectedValuePeriodId = selectedValuePeriodId;
    }

    /**
     * Explicitly refresh fields and action definitions. Should be used on some field value change event when that field is used to determine what fields and actions apply. E.g.
     * Job template.
     * 
     * @param entity Entity to [re]load definitions and field values for
     */
    public void refreshFieldsAndActions(ICustomFieldEntity entity) {

        initFields(entity);
        initCustomActions(entity);
    }

    /**
     * Get a grouped list of custom field definitions. If needed, load applicable custom fields (templates) and their values for a given entity
     * 
     * @param entity Entity to load definitions and field values for
     * @return Custom field information
     */
    public GroupedCustomField getGroupedFieldTemplates(ICustomFieldEntity entity) {

        if (!groupedFieldTemplates.containsKey(entity.getUuid())) {
            initFields(entity);
        }
        return groupedFieldTemplates.get(entity.getUuid());
    }

    /**
     * Get a list of actions applicable for an entity. If needed, load them.
     * 
     * @param entity Entity to load action definitions
     * @return A list of actions
     */
    public List<EntityActionScript> getCustomActions(IEntity entity) {

        if (!(entity instanceof ICustomFieldEntity)) {
            return null;
        }

        if (!customActions.containsKey(((ICustomFieldEntity) entity).getUuid())) {
            initCustomActions((ICustomFieldEntity) entity);
        }
        return customActions.get(((ICustomFieldEntity) entity).getUuid());
    }

    /**
     * Get a custom field value holder for a given entity
     * 
     * @param entityUuid Entity uuid identifier
     * @return Custom field value holder
     */
    public CustomFieldValueHolder getFieldsValues(String entityUuid) {
        return fieldsValues.get(entityUuid);
    }

    /**
     * Load applicable custom actions for a given entity
     * 
     * @param entity Entity to load action definitions
     */
    private void initCustomActions(ICustomFieldEntity entity) {

        Map<String, EntityActionScript> actions = entityActionScriptService.findByAppliesTo(entity, currentProvider);

        List<EntityActionScript> actionList = new ArrayList<EntityActionScript>(actions.values());
        customActions.put(entity.getUuid(), actionList);
    }

    /**
     * Load available custom fields (templates) and their values for a given entity
     * 
     * @param entity Entity to load definitions and field values for
     */
    private void initFields(ICustomFieldEntity entity) {

        Map<String, CustomFieldTemplate> customFieldTemplates = getApplicableCustomFieldTemplates(entity);

        GroupedCustomField groupedCustomField = new GroupedCustomField(customFieldTemplates.values(), "Custom fields", false);
        groupedFieldTemplates.put(entity.getUuid(), groupedCustomField);

        Map<String, List<CustomFieldInstance>> cfisAsMap = null;

        // Get custom field instances mapped by a CFT code if entity has any field defined
        if (customFieldTemplates != null && customFieldTemplates.size() > 0) {
            cfisAsMap = customFieldInstanceService.getCustomFieldInstances((ICustomFieldEntity) entity);
        }
        CustomFieldValueHolder entityFieldsValues = new CustomFieldValueHolder(customFieldTemplates, cfisAsMap, entity);
        fieldsValues.put(entity.getUuid(), entityFieldsValues);
    }

    /**
     * Add a new customField period with a previous validation that matching period does not exists
     * 
     * @param entityUuid Entity uuid identifier
     * @param cft Custom field definition
     */
    public void addNewValuePeriod(String entityUuid, CustomFieldTemplate cft) {
        CustomFieldValueHolder entityFieldsValues = getFieldsValues(entityUuid);

        Date periodStartDate = (Date) entityFieldsValues.getNewValue(cft.getCode() + "_periodStartDate");
        Date periodEndDate = (Date) entityFieldsValues.getNewValue(cft.getCode() + "_periodEndDate");
        Object value = entityFieldsValues.getNewValue(cft.getCode() + "_value");

        // Check that two dates are one after another
        if (periodStartDate != null && periodEndDate != null && periodStartDate.compareTo(periodEndDate) >= 0) {
            messages.error(new BundleKey("messages", "customFieldTemplate.periodIntervalIncorrect"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        CustomFieldInstance period = null;
        // First check if any period matches the dates
        if (!valuePeriodMatched.containsKey(entityUuid) || !valuePeriodMatched.get(entityUuid)) {
            if (periodStartDate == null && periodEndDate == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.periodDatesBothNull"));
                valuePeriodMatched.put(entityUuid, true);
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            boolean strictMatch = false;
            if (cft.getCalendar() != null) {
                period = entityFieldsValues.getValuePeriod(cft, periodStartDate, false);
                strictMatch = true;
            } else {
                period = entityFieldsValues.getValuePeriod(cft, periodStartDate, periodEndDate, false, false);
                if (period != null) {
                    strictMatch = period.isCorrespondsToPeriod(periodStartDate, periodEndDate, true);
                }
            }

            if (period != null) {
                valuePeriodMatched.put(entityUuid, true);
                ParamBean paramBean = ParamBean.getInstance();
                String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");

                // For a strict match need to edit an existing period
                if (strictMatch) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound.noNew"),
                        DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern), DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                    valuePeriodMatched.put(entityUuid, false);

                    // For a non-strict match user has an option to create a period with a higher priority
                } else {
                    messages.warn(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound"), DateUtils.formatDateWithPattern(period.getPeriodStartDate(), datePattern),
                        DateUtils.formatDateWithPattern(period.getPeriodEndDate(), datePattern));
                    valuePeriodMatched.put(entityUuid, true);
                }
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        // Create period if passed a period check or if user decided to create it anyway
        if (cft.getCalendar() != null) {
            period = entityFieldsValues.addValuePeriod(cft, periodStartDate);

        } else {
            period = entityFieldsValues.addValuePeriod(cft, periodStartDate, periodEndDate);
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

        entityFieldsValues.populateNewValueDefaults(null, cft);
        valuePeriodMatched.put(entityUuid, false);
        selectedFieldTemplate.put(entityUuid, cft);
        selectedValuePeriod.put(entityUuid, period);
    }

    /**
     * Add value to a map of values, setting a default value if applicable
     * 
     * @param entityUuid Entity uuid identifier
     * @param cfv Map value holder
     * @param cft Custom field definition
     */
    public void addValueToMap(String entityUuid, CustomFieldValue cfv, CustomFieldTemplate cft) {

        CustomFieldValueHolder entityFieldValues = getFieldsValues(entityUuid);

        String newKey = null;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                newKey = (String) entityFieldValues.getNewValue(cft.getCode() + "_key");

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                // Validate that at least one value is provided and in correct order
                Double from = (Double) entityFieldValues.getNewValue(cft.getCode() + "_key_one_from");
                Double to = (Double) entityFieldValues.getNewValue(cft.getCode() + "_key_one_to");

                if (from == null && to == null) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;

                } else if (from != null && to != null && from.compareTo(to) >= 0) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                    FacesContext.getCurrentInstance().validationFailed();
                    return;
                }
                newKey = (from == null ? "" : from) + CustomFieldValue.RON_VALUE_SEPARATOR + (to == null ? "" : to);
            }
        }

        Object newValue = entityFieldValues.getNewValue(cft.getCode() + "_value");

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

        entityFieldValues.clearNewValues();
    }

    /**
     * Autocomplete method for listing entities for "Reference to entity" type custom field values
     * 
     * @param wildcode A partial entity code match
     * @return A list of entities [partially] matching code
     */
    public List<BusinessEntity> autocompleteEntityForCFV(String wildcode) {
        String classname = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("classname");
        return customFieldInstanceService.findBusinessEntityForCFVByCode(classname, wildcode, this.currentProvider);
    }

    /**
     * Validate complex custom fields
     * 
     * @param entity Entity, to which custom fields are related to
     */
    public void validateCustomFields(ICustomFieldEntity entity) {
        boolean valid = true;
        boolean isNewEntity = ((IEntity) entity).isTransient();

        FacesContext fc = FacesContext.getCurrentInstance();
        for (CustomFieldTemplate cft : groupedFieldTemplates.get(entity.getUuid()).getFields()) {

            // Ignore the validation on a field when creating entity and CFT.hideOnNew=true or editing entity and CFT.allowEdit=false or when CFT.applicableOnEL expression
            // evaluates to false
            if ((isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit())
                    || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                continue;

            } else if (cft.isActive() && cft.isValueRequired() && (cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE || cft.isVersionable())) {

                for (CustomFieldInstance cfi : getFieldsValues(entity.getUuid()).getValues(cft)) {

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

        if (!valid) {

            fc.validationFailed();
            fc.renderResponse();
        }
    }

    /**
     * Get inherited custom field value for a given entity
     * 
     * @param Entity to get the inherited value for
     * @param code Custom field code
     * @return Custom field value
     */
    public Object getInheritedCFValue(ICustomFieldEntity entity, String code) {
        return customFieldInstanceService.getInheritedOnlyCFValue(entity, code, currentUser);
    }

    /**
     * Add row to a matrix.
     * 
     * @param cft Custom field template
     * @param matrixValues Matrix values
     */
    public void addMatrixRow(CustomFieldTemplate cft, List<Map<String, Object>> matrixValues) {
        Map<String, Object> rowValues = new HashMap<String, Object>();
        matrixValues.add(rowValues);
    }

    /**
     * Execute custom action on an entity
     * 
     * @param entity Entity to execute action on
     * @param action Action to execute
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @return A script execution result value from Script.RESULT_GUI_OUTCOME variable
     */
    public String executeCustomAction(ICustomFieldEntity entity, EntityActionScript action, String encodedParameters) {

        try {
            Map<String, Object> result = entityActionScriptService.execute((IEntity) entity, action.getCode(), encodedParameters, currentUser, currentProvider);

            // Display a message accordingly on what is set in result
            if (result.containsKey(Script.RESULT_GUI_MESSAGE_KEY)) {
                messages.info(new BundleKey("messages", (String) result.get(Script.RESULT_GUI_MESSAGE_KEY)));

            } else if (result.containsKey(Script.RESULT_GUI_MESSAGE_KEY)) {
                messages.info((String) result.get(Script.RESULT_GUI_MESSAGE));

            } else {
                messages.info(new BundleKey("messages", "scriptInstance.actionExecutionSuccessfull"), action.getLabel());
            }

            if (result.containsKey(Script.RESULT_GUI_OUTCOME)) {
                return (String) result.get(Script.RESULT_GUI_OUTCOME);
            }

        } catch (BusinessException e) {
            log.error("Failed to execute a script {} on entity {}", action.getCode(), entity, e);
            messages.error(new BundleKey("messages", "scriptInstance.actionExecutionFailed"), action.getLabel(), e.getMessage());
        }

        return null;
    }

    /**
     * Save custom fields for a given entity
     * 
     * @param entity Entity, the fields relate to
     * @param isNewEntity Is it a new entity
     * @throws BusinessException
     */
    public void updateCustomFieldsInEntity(ICustomFieldEntity entity, boolean isNewEntity) throws BusinessException {

        CustomFieldValueHolder entityFieldsValues = getFieldsValues(entity.getUuid());
        for (CustomFieldTemplate cft : groupedFieldTemplates.get(entity.getUuid()).getFields()) {
            List<CustomFieldInstance> cfis = entityFieldsValues.getValues(cft);

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
                    cfi.getCfValue().serializeForGUI(cft);
                    if (cfi.isTransient()) {
                        customFieldInstanceService.create(cfi, (ICustomFieldEntity) entity, currentUser, currentProvider);
                    } else {
                        customFieldInstanceService.update(cfi, currentUser);
                    }
                }
            }
        }
    }

    /**
     * Get a list of custom field templates applicable to an entity.
     * 
     * @param entity Entity to retrieve custom field templates for
     * @return A map of custom field templates with template code as a key
     */
    private Map<String, CustomFieldTemplate> getApplicableCustomFieldTemplates(ICustomFieldEntity entity) {
        Map<String, CustomFieldTemplate> result = customFieldTemplateService.findByAppliesTo(entity, currentProvider);
        log.debug("Found {} custom field templates for entity {}", result.size(), entity.getClass());
        return result;
    }
}