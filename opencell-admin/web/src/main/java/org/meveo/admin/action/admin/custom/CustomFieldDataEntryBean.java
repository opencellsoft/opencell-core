package org.meveo.admin.action.admin.custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValueHolder;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.util.EntityCustomizationUtils;
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
     * Custom field templates grouped into tabs and field groups
     */
    private Map<String, GroupedCustomField> groupedFieldTemplates = new HashMap<String, GroupedCustomField>();

    /**
     * Custom actions applicable to the entity
     */
    private Map<String, List<EntityCustomAction>> customActions = new HashMap<String, List<EntityCustomAction>>();

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
    private EntityCustomActionService entityActionScriptService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    protected Messages messages;

    /** Logger. */
    private Logger log = LoggerFactory.getLogger(this.getClass());

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
     * Explicitly refresh fields and action definitions while preserving field values. Should be used when entity customization is managed as part of some page that contains CF
     * data entry and CF fields should be refreshed when entity customization is finished. Job template.
     * 
     * @param entity Entity to [re]load definitions and field values for
     */
    public void refreshFieldsAndActionsWhilePreserveValues(ICustomFieldEntity entity) {

        refreshFieldsWhilePreservingValues(entity);
        initCustomActions(entity);
    }

    /**
     * Get a grouped list of custom field definitions. If needed, load applicable custom fields (templates) and their values for a given entity
     * 
     * @param entity Entity to load definitions and field values for
     * @return Custom field information
     */
    public GroupedCustomField getGroupedFieldTemplates(ICustomFieldEntity entity) {

        if (entity == null) {
            return null;
        }
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
    public List<EntityCustomAction> getCustomActions(IEntity entity) {

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
    public CustomFieldValueHolder getFieldValueHolderByUUID(String entityUuid) {
        return fieldsValues.get(entityUuid);
    }

    /**
     * Load applicable custom actions for a given entity
     * 
     * @param entity Entity to load action definitions
     */
    private void initCustomActions(ICustomFieldEntity entity) {

        Map<String, EntityCustomAction> actions = entityActionScriptService.findByAppliesTo(entity);

        List<EntityCustomAction> actionList = new ArrayList<EntityCustomAction>(actions.values());
        customActions.put(entity.getUuid(), actionList);
    }

    private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Load available custom fields (templates) and their values for a given entity
     * 
     * @param entity Entity to load definitions and field values for
     */
    private void initFields(ICustomFieldEntity entity) {

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity);
        log.trace("Found {} custom field templates for entity {}", customFieldTemplates.size(), entity.getClass());

        customFieldTemplates = sortByValue(customFieldTemplates);

        GroupedCustomField groupedCustomField = new GroupedCustomField(customFieldTemplates.values(), "Custom fields", false);
        groupedFieldTemplates.put(entity.getUuid(), groupedCustomField);

        Map<String, List<CustomFieldValue>> cfValuesByCode = null;
        // Get custom field instances mapped by a CFT code if entity has any field defined
        // if (!((IEntity) entity).isTransient() && customFieldTemplates != null && customFieldTemplates.size() > 0) {
        // No longer checking for isTransient as for offer new version creation, CFs are duplicated, but entity is not persisted, offering to review it in GUI before saving it.
        if (customFieldTemplates != null && customFieldTemplates.size() > 0 && ((ICustomFieldEntity) entity).getCfValues() != null) {
            cfValuesByCode = ((ICustomFieldEntity) entity).getCfValues().getValuesByCode(); // TODO need to close the values
        }
        cfValuesByCode = prepareCFIForGUI(customFieldTemplates, cfValuesByCode, entity);
        CustomFieldValueHolder entityFieldsValues = new CustomFieldValueHolder(customFieldTemplates, cfValuesByCode, entity);
        fieldsValues.put(entity.getUuid(), entityFieldsValues);
    }

    /**
     * Load available custom fields (templates) while preserving their values for a given entity
     * 
     * @param entity Entity to load definitions and field values for
     */
    private void refreshFieldsWhilePreservingValues(ICustomFieldEntity entity) {

        Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity);
        log.trace("Refreshing CFTS while preserving values. Found {} custom field templates for entity {}", customFieldTemplates.size(), entity.getClass());

        GroupedCustomField groupedCustomField = new GroupedCustomField(customFieldTemplates.values(), "Custom fields", false);
        groupedFieldTemplates.put(entity.getUuid(), groupedCustomField);

        CustomFieldValueHolder entityFieldsValues = fieldsValues.get(entity.getUuid());

        // Populate new value defaults formap, list and matrix fields
        entityFieldsValues.populateNewValueDefaults(customFieldTemplates.values(), null);

        // Populate new value defaults for simple fields
        for (CustomFieldTemplate cft : customFieldTemplates.values()) {
            if (entityFieldsValues.getValues(cft) == null && !cft.isVersionable()) {
                entityFieldsValues.getValuesByCode().put(cft.getCode(), Arrays.asList(cft.toDefaultCFValue()));
            }
        }
    }

    /**
     * Prepare custom field values for GUI - instantiate fields with default values, deserialize values for GUI
     * 
     * @param customFieldTemplates Custom field templates applicable for the entity, mapped by a custom CFT code
     * @param cfValuesByCode Custom field values mapped by a CFT code
     * @param entity Entity containing custom field values
     * 
     * @return Prepared for GUI custom fields instances
     */
    private Map<String, List<CustomFieldValue>> prepareCFIForGUI(Map<String, CustomFieldTemplate> customFieldTemplates, Map<String, List<CustomFieldValue>> cfValuesByCode,
            ICustomFieldEntity entity) {

        Map<String, List<CustomFieldValue>> cfisPrepared = new HashMap<>();

        // For each template, check if custom field value exists, and instantiate one if needed with a default value
        for (CustomFieldTemplate cft : customFieldTemplates.values()) {

            List<CustomFieldValue> cfValuesByTemplate = null;
            if (cfValuesByCode != null) {
                cfValuesByTemplate = cfValuesByCode.get(cft.getCode());
            }
            if (cfValuesByTemplate == null) {
                cfValuesByTemplate = new ArrayList<>();
            }

            // Instantiate with a default value if no value found
            if (cfValuesByTemplate.isEmpty() && !cft.isVersionable()) {
                CustomFieldValue cfValue = cft.toDefaultCFValue();

                // Overwrite with inherited value if needed
                if (cft.isUseInheritedAsDefaultValue()) {
                    Object inheritedValue = customFieldInstanceService.getInheritedOnlyCFValue(entity, cft.getCode());
                    if (inheritedValue != null) {
                        cfValue.setValue(inheritedValue);
                    }
                }

                cfValuesByTemplate.add(cfValue);
            }

            // Deserialize values if applicable
            for (CustomFieldValue cfValue : cfValuesByTemplate) {
                deserializeForGUI(cfValue, cft);
            }

            // Make sure that only one value is retrieved
            if (!cft.isVersionable()) {
                cfValuesByTemplate = new ArrayList<CustomFieldValue>(cfValuesByTemplate.subList(0, 1));
            }
            cfisPrepared.put(cft.getCode(), cfValuesByTemplate);
        }

        return cfisPrepared;
    }

    //
    // /**
    // * Load available custom fields (templates) and their values for a given entity
    // *
    // * @param entity Entity to load definitions and field values for
    // * @param cfisAsMap Custom field instances mapped by a CFT code
    // */
    // private void initFields(ICustomFieldEntity entity, Map<String, List<CustomFieldInstance>> cfisAsMap) {
    //
    // Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(entity);
    // log.debug("Found {} custom field templates for entity {}", customFieldTemplates.size(), entity.getClass());
    //
    // GroupedCustomField groupedCustomField = new GroupedCustomField(customFieldTemplates.values(), "Custom fields", false);
    // groupedFieldTemplates.put(entity.getUuid(), groupedCustomField);
    //
    // CustomFieldValueHolder entityFieldsValues = new CustomFieldValueHolder(customFieldTemplates, cfisAsMap, entity);
    // fieldsValues.put(entity.getUuid(), entityFieldsValues);
    // }

    // /**
    // * Load available custom fields (templates) for a given child entity field definition
    // *
    // * @param childEntityFieldDefinition Custom field template of child entity type, definition
    // */
    // private void initGroupedCustomFieldsForChildEntity(CustomFieldTemplate childEntityFieldDefinition) {
    //
    // Map<String, CustomFieldTemplate> customFieldTemplates = customFieldTemplateService.findByAppliesTo(
    // EntityCustomizationUtils.getAppliesTo(CustomEntityTemplate.class, CustomFieldTemplate.retrieveCetCode(childEntityFieldDefinition.getEntityClazz())));
    //
    // log.debug("Found {} custom field templates for entity {}", customFieldTemplates.size(), childEntityFieldDefinition.getEntityClazz());
    //
    // GroupedCustomField groupedCustomField = new GroupedCustomField(customFieldTemplates.values(), "Custom fields", false);
    // groupedFieldTemplates.put(childEntityFieldDefinition.getEntityClazz(), groupedCustomField);
    // }

    /**
     * Add a new customField period with a previous validation that matching period does not exists
     * 
     * @param entityValueHolder Entity custom field value holder
     * @param cft Custom field definition
     */
    public void addNewValuePeriod(CustomFieldValueHolder entityValueHolder, CustomFieldTemplate cft) {

        Date periodStartDate = (Date) entityValueHolder.getNewValue(cft.getCode() + "_periodStartDate");
        Date periodEndDate = (Date) entityValueHolder.getNewValue(cft.getCode() + "_periodEndDate");
        Object value = entityValueHolder.getNewValue(cft.getCode() + "_value");

        // Check that two dates are one after another
        if (periodStartDate != null && periodEndDate != null && periodStartDate.compareTo(periodEndDate) >= 0) {
            messages.error(new BundleKey("messages", "customFieldTemplate.periodIntervalIncorrect"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        // Validate that value is set
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && value == null) {
            messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        CustomFieldValue cfValue = null;
        // First check if any period matches the dates
        if (entityValueHolder.getValuePeriodMatched() == null || !entityValueHolder.getValuePeriodMatched()) {
            if (periodStartDate == null && periodEndDate == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.periodDatesBothNull"));
                entityValueHolder.setValuePeriodMatched(true);
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }

            boolean strictMatch = false;
            if (cft.getCalendar() != null) {
                cfValue = entityValueHolder.getValuePeriod(cft, periodStartDate, false);
                strictMatch = true;
            } else {
                cfValue = entityValueHolder.getValuePeriod(cft, periodStartDate, periodEndDate, false, false);
                if (cfValue != null && cfValue.getPeriod() != null) {
                    strictMatch = cfValue.getPeriod().isCorrespondsToPeriod(periodStartDate, periodEndDate, true);
                }
            }

            if (cfValue != null) {
                entityValueHolder.setValuePeriodMatched(true);
                ParamBean paramBean = ParamBean.getInstance();
                String datePattern = paramBean.getDateFormat();

                // For a strict match need to edit an existing period
                if (strictMatch) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound.noNew"),
                        cfValue.getPeriod() == null ? "" : DateUtils.formatDateWithPattern(cfValue.getPeriod().getFrom(), datePattern),
                        cfValue.getPeriod() == null ? "" : DateUtils.formatDateWithPattern(cfValue.getPeriod().getTo(), datePattern));
                    entityValueHolder.setValuePeriodMatched(false);

                    // For a non-strict match user has an option to create a period with a higher priority
                } else {
                    messages.warn(new BundleKey("messages", "customFieldTemplate.matchingPeriodFound"),
                        cfValue.getPeriod() == null ? "" : DateUtils.formatDateWithPattern(cfValue.getPeriod().getFrom(), datePattern),
                        cfValue.getPeriod() == null ? "" : DateUtils.formatDateWithPattern(cfValue.getPeriod().getTo(), datePattern));
                    entityValueHolder.setValuePeriodMatched(true);
                }
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        // Create period if passed a period check or if user decided to create it anyway
        if (cft.getCalendar() != null) {
            cfValue = entityValueHolder.addValuePeriod(cft, periodStartDate);

        } else {
            cfValue = entityValueHolder.addValuePeriod(cft, periodStartDate, periodEndDate);
        }

        // Set value
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            cfValue.setSingleValue(value, cft.getFieldType());
            if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                cfValue.setEntityReferenceValueForGUI((BusinessEntity) value);
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

        entityValueHolder.populateNewValueDefaults(null, cft);
        entityValueHolder.setValuePeriodMatched(false);
        entityValueHolder.setSelectedFieldTemplate(cft);
        entityValueHolder.setSelectedValuePeriod(cfValue);
    }

    /**
     * Add value to a map of values, setting a default value if applicable
     * 
     * @param entityValueHolder Entity custom field value holder
     * @param cfv Map value holder
     * @param cft Custom field definition
     */
    public void addValueToMap(CustomFieldValueHolder entityValueHolder, CustomFieldValue cfv, CustomFieldTemplate cft) {

        String newKey = null;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
                newKey = (String) entityValueHolder.getNewValue(cft.getCode() + "_key");

            } else if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
                // Validate that at least one value is provided and in correct order
                Double from = (Double) entityValueHolder.getNewValue(cft.getCode() + "_key_one_from");
                Double to = (Double) entityValueHolder.getNewValue(cft.getCode() + "_key_one_to");

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

            if (newKey == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyNotSpecified"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        Object newValue = entityValueHolder.getNewValue(cft.getCode() + "_value");
        if (newValue == null) {
            messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

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

        entityValueHolder.clearNewValues();
    }

    /**
     * Autocomplete method for listing entities for "Reference to entity" type custom field values
     * 
     * @param wildcode A partial entity code match
     * @return A list of entities [partially] matching code
     */
    public List<BusinessEntity> autocompleteEntityForCFV(String wildcode) {
        String classname = (String) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get("classname");
        return customFieldInstanceService.findBusinessEntityForCFVByCode(classname, wildcode);
    }

    /**
     * Validate complex custom fields
     * 
     * @param entity Entity, to which custom fields are related to
     */
    public boolean validateCustomFields(ICustomFieldEntity entity) {
        boolean valid = true;
        boolean isNewEntity = ((IEntity) entity).isTransient();

        FacesContext fc = FacesContext.getCurrentInstance();
        for (CustomFieldTemplate cft : groupedFieldTemplates.get(entity.getUuid()).getFields()) {

            // Ignore the validation on a field when creating entity and CFT.hideOnNew=true or editing entity and CFT.allowEdit=false or when CFT.applicableOnEL expression
            // evaluates to false
            if (cft.isDisabled() || !cft.isValueRequired() || (isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit())
                    || !ValueExpressionWrapper.evaluateToBooleanIgnoreErrors(cft.getApplicableOnEl(), "entity", entity)) {
                continue;

                // Single field's mandatory requirement are taken care in GUI level, new values are not available yet here at validation stage
            } else if ((cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE || cft.isVersionable())) {

                List<CustomFieldValue> cfValues = getFieldValueHolderByUUID(entity.getUuid()).getValues(cft);

                // Fail validation on non empty values only if it does not have inherited value
                if (cfValues == null || cfValues.isEmpty()) {
                    if (!customFieldInstanceService.hasInheritedOnlyCFValue(entity, cft.getCode())) {
                        FacesMessage msg = new FacesMessage(resourceMessages.getString("javax.faces.component.UIInput.REQUIRED", cft.getDescription()));
                        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                        fc.addMessage(null, msg);
                        valid = false;
                    }
                } else {
                    for (CustomFieldValue cfValue : cfValues) {
                        if (cfValue.isValueEmptyForGui()) {
                            if (customFieldInstanceService.hasInheritedOnlyCFValue(entity, cft.getCode())) {
                                break;
                            }
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
        return valid;
    }

    /**
     * Get inherited custom field value for a given entity
     * 
     * @param Entity to get the inherited value for
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object getInheritedCFValue(ICustomFieldEntity entity, String cfCode) {
        return customFieldInstanceService.getInheritedOnlyCFValue(entity, cfCode);
    }

    public List<CustomFieldValue> getInheritedVersionableCFValue(ICustomFieldEntity entity, String cfCode) {
        return customFieldInstanceService.getInheritedVersionableOnlyCFValue(entity, cfCode);
    }

    /**
     * Get inherited custom field value for a given entity. A cumulative custom field value is calculated for Map(Matrix) type fields
     * 
     * @param Entity to get the inherited value for
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public CustomFieldValue getInheritedCFValueAsCFValue(ICustomFieldEntity entity, CustomFieldTemplate cft, String cfCode) {

        Object inheritedValue = customFieldInstanceService.getInheritedOnlyCFValueCumulative(entity, cfCode);
        if (inheritedValue == null) {
            return null;
        }

        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setValue(inheritedValue);
        deserializeForGUI(cfv, cft);

        return cfv;
    }

    /**
     * Add row to a matrix.
     * 
     * @param entityValueHolder Entity custom field value holder
     * @param cfValue Map value holder
     * @param cft Custom field definition
     */
    public void addMatrixRow(CustomFieldValueHolder entityValueHolder, CustomFieldValue cfValue, CustomFieldTemplate cft) {
        Map<String, Object> rowValues = new HashMap<String, Object>();

        for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {

            String newKey = null;

            if (column.getKeyType() == CustomFieldMapKeyEnum.STRING) {
                newKey = (String) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

            } else if (column.getKeyType() == CustomFieldMapKeyEnum.RON) {
                // Validate that at least one value is provided and in correct order
                Double from = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode() + "_from");
                Double to = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode() + "_to");

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

            if (newKey != null) {
                rowValues.put(column.getCode(), newKey);
            }
        }

        if (rowValues.isEmpty()) {
            messages.error(new BundleKey("messages", "customFieldTemplate.matrixKeyNotSpecified"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        Object newValue = entityValueHolder.getNewValue(cft.getCode() + "_value");
        if (newValue == null) {
            messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        rowValues.put(CustomFieldValue.MAP_VALUE, newValue);

        // Validate that key or value is not duplicate
        for (Map<String, Object> mapItem : cfValue.getMatrixValuesForGUI()) {
            boolean allMatch = true;
            for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {
                if (mapItem.get(column.getCode()) == null && rowValues.get(column.getCode()) == null) {

                } else if (mapItem.get(column.getCode()) != null && !mapItem.get(column.getCode()).equals(rowValues.get(column.getCode()))) {
                    allMatch = false;
                    break;
                } else if (rowValues.get(column.getCode()) != null && !rowValues.get(column.getCode()).equals(mapItem.get(column.getCode()))) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                messages.error(new BundleKey("messages", "customFieldTemplate.matrixKeyExists"));
                FacesContext.getCurrentInstance().validationFailed();
                return;
            }
        }

        cfValue.getMatrixValuesForGUI().add(rowValues);

        entityValueHolder.clearNewValues();
    }

    /**
     * Execute custom action on an entity
     * 
     * @param entity Entity to execute action on
     * @param action Action to execute
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @return A script execution result value from Script.RESULT_GUI_OUTCOME variable
     */
    public String executeCustomAction(ICustomFieldEntity entity, EntityCustomAction action, String encodedParameters) {

        try {

            Map<String, Object> context = CustomScriptService.parseParameters(encodedParameters);
            context.put(Script.CONTEXT_ACTION, action.getCode());

            Map<String, Object> result = scriptInstanceService.execute((IEntity) entity, action.getScript().getCode(), context);

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
     * Execute custom action on a child entity
     * 
     * @param parentEntity Parent entity, entity is related to
     * @param childEntity Entity to execute action on
     * @param action Action to execute
     * @param encodedParameters Additional parameters encoded in URL like style param=value&param=value
     * @return A script execution result value from Script.RESULT_GUI_OUTCOME variable
     */
    public String executeCustomActionOnChildEntity(ICustomFieldEntity parentEntity, ICustomFieldEntity childEntity, EntityCustomAction action, String encodedParameters) {

        try {

            Map<String, Object> context = CustomScriptService.parseParameters(encodedParameters);
            context.put(Script.CONTEXT_PARENT_ENTITY, parentEntity);
            context.put(Script.CONTEXT_ACTION, action.getCode());

            Map<String, Object> result = scriptInstanceService.execute((IEntity) childEntity, action.getScript().getCode(), context);

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
            log.error("Failed to execute a script {} on entity {}", action.getCode(), childEntity, e);
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
    public void saveCustomFieldsToEntity(ICustomFieldEntity entity, boolean isNewEntity) throws BusinessException {
        String uuid = entity.getUuid();
        saveCustomFieldsToEntity(entity, uuid, false, isNewEntity);
    }

    public void saveCustomFieldsToEntity(ICustomFieldEntity entity, String uuid, boolean duplicateCFI, boolean isNewEntity) throws BusinessException {
        saveCustomFieldsToEntity(entity, uuid, duplicateCFI, isNewEntity, false);
    }

    /**
     * Save custom fields for a given entity
     * 
     * @param entity Entity, the fields relate to
     * @param isNewEntity Is it a new entity
     * @param removedOriginalCFI - When duplicating a CFI, this boolean is true when we want to remove the original CFI. Use specially in offer instantiation where we assigned CFT
     *        values on entity a but then save it on entity b. Entity a is then reverted. This flag is needed because on some part CFI is duplicated first, but is not updated,
     *        instead we duplicate again.
     * @throws BusinessException
     */
    public void saveCustomFieldsToEntity(ICustomFieldEntity entity, String uuid, boolean duplicateCFI, boolean isNewEntity, boolean removedOriginalCFI) throws BusinessException {

        Map<String, List<CustomFieldValue>> newValuesByCode = new HashMap<>();

        CustomFieldValueHolder entityFieldsValues = getFieldValueHolderByUUID(uuid);
        GroupedCustomField groupedCustomFields = groupedFieldTemplates.get(uuid);
        if (groupedCustomFields != null) {
            for (CustomFieldTemplate cft : groupedCustomFields.getFields()) {

                // Do not update existing CF value if it is not updatable
                if (!isNewEntity && !cft.isAllowEdit()) {

                    if (entity.getCfValues() != null) {
                        List<CustomFieldValue> previousCfValues = entity.getCfValues().getValuesByCode().get(cft.getCode());
                        if (previousCfValues != null && !previousCfValues.isEmpty()) {
                            newValuesByCode.put(cft.getCode(), previousCfValues);
                        }
                    }
                    continue;
                }

                for (CustomFieldValue cfValue : entityFieldsValues.getValues(cft)) {

                    // TODO not sure what this code is about - need to check its use AK

                    // if (duplicateCFI) {
                    // if (removedOriginalCFI) {
                    // List<CustomFieldInstance> cfisToBeRemove = customFieldInstanceService.getCustomFieldInstances(entity, cfValue.getCode());
                    // if (cfisToBeRemove != null) {
                    // for (CustomFieldInstance cfiToBeRemove : cfisToBeRemove) {
                    // customFieldInstanceService.remove(cfiToBeRemove);
                    // }
                    // }
                    // }
                    //
                    // customFieldInstanceService.detach(cfValue);
                    // cfValue.setId(null);
                    // cfValue.setAppliesToEntity(entity.getUuid());
                    // }

                    // Not saving empty values unless template has a default value or is versionable (to prevent that for SINGLE type CFT with a default value, value is
                    // instantiates automatically)
                    // Also don't save if CFT does not apply in a given entity lifecycle or because cft.applicableOnEL evaluates to false
                    if ((cfValue.isValueEmptyForGui() && (cft.getDefaultValue() == null || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE) && !cft.isVersionable())
                            || ((isNewEntity && cft.isHideOnNew()) || !ValueExpressionWrapper.evaluateToBoolean(cft.getApplicableOnEl(), "entity", entity))) {
                        log.trace("Will ommit from saving cfi {}", cfValue);

                        // Existing value update
                    } else {
                        serializeFromGUI(cfValue, cft);

                        if (!newValuesByCode.containsKey(cft.getCode())) {
                            newValuesByCode.put(cft.getCode(), new ArrayList<>());
                        }
                        newValuesByCode.get(cft.getCode()).add(cfValue);

                        saveChildEntities(entity, cfValue, cft);
                    }
                }
            }
        }
        // Update entity custom values field
        if (newValuesByCode.isEmpty()) {
            entity.clearCfValues();
        } else {
            entity.getCfValuesNullSafe().setValuesByCode(newValuesByCode);
        }
    }

    /**
     * Get a child entity column corresponding to a given code
     * 
     * @param childEntityTypeFieldDefinition Child entity type field definition
     * @param childFieldCode Child entity field code
     * @return
     */
    public CustomFieldTemplate getChildEntityField(CustomFieldTemplate childEntityTypeFieldDefinition, String childFieldCode) {

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(
            EntityCustomizationUtils.getAppliesTo(CustomEntityTemplate.class, CustomFieldTemplate.retrieveCetCode(childEntityTypeFieldDefinition.getEntityClazz())));

        return cfts.get(childFieldCode);
    }

    /**
     * Prepare new child entity record for data entry
     * 
     * @param mainEntityValueHolder Entity custom field value holder
     * @param mainEntityCfv Main entity's custom field value containing child entities
     * @param childEntityFieldDefinition Custom field template of child entity type, definition, corresponding to cfv
     */
    public void newChildEntity(CustomFieldValueHolder mainEntityValueHolder, CustomFieldValue mainEntityCfv, CustomFieldTemplate childEntityFieldDefinition) {

        CustomEntityInstance cei = new CustomEntityInstance();
        cei.setCetCode(CustomFieldTemplate.retrieveCetCode(childEntityFieldDefinition.getEntityClazz()));
        cei.setParentEntityUuid(mainEntityValueHolder.getEntityUuid());

        initFields(cei);

        CustomFieldValueHolder childEntityValueHolder = getFieldValueHolderByUUID(cei.getUuid());

        mainEntityValueHolder.setSelectedChildEntity(childEntityValueHolder);
    }

    /**
     * Save child entity record
     * 
     * @param mainEntityValueHolder Main entity custom field value holder
     * @param mainEntityCfv Main entity's custom field value containing child entities
     * @param childEntityFieldDefinition Custom field template of child entity type, definition, corresponding to cfv
     */
    public void saveChildEntity(CustomFieldValueHolder mainEntityValueHolder, CustomFieldValue mainEntityCfv, CustomFieldTemplate childEntityFieldDefinition) {

        CustomEntityInstance cei = (CustomEntityInstance) mainEntityValueHolder.getSelectedChildEntity().getEntity();
        if (!validateCustomFields(cei)) {
            return;
        }

        // check that CEI code is unique
        CustomEntityInstance ceiSameCode = customEntityInstanceService.findByCodeByCet(cei.getCetCode(), cei.getCode());
        if ((cei.isTransient() && ceiSameCode != null) || (!cei.isTransient() && cei.getId().longValue() != ceiSameCode.getId().longValue())) {
            messages.error(new BundleKey("messages", "commons.uniqueField.code"));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        // try {
        String message = "customFieldInstance.childEntity.save.successful";

        CustomFieldValueHolder childEntityValueHolder = mainEntityValueHolder.getSelectedChildEntity();
        childEntityValueHolder.setUpdated(true);

        if (mainEntityCfv.getChildEntityValuesForGUI().contains(childEntityValueHolder)) {
            mainEntityCfv.getChildEntityValuesForGUI().set(mainEntityCfv.getChildEntityValuesForGUI().indexOf(childEntityValueHolder), childEntityValueHolder);
            message = "customFieldInstance.childEntity.update.successful";

        } else {
            mainEntityCfv.getChildEntityValuesForGUI().add(childEntityValueHolder);
        }
        messages.info(new BundleKey("messages", message));

        // } catch (BusinessException e) {
        // log.error("Failed to save child entity {} {}", childEntityFieldDefinition.getCode(), mainEntityValueHolder, e);
        // messages.error(new BundleKey("messages", "error.action.failed"), e.getMessage());
        // }
    }

    /**
     * Prepare to edit child entity
     * 
     * @param mainEntityValueHolder Main entity custom field value holder
     * @param selectedChildEntity Child entity custom field value holder
     */
    public void editChildEntity(CustomFieldValueHolder mainEntityValueHolder, CustomFieldValueHolder selectedChildEntity) {
        mainEntityValueHolder.setSelectedChildEntity(selectedChildEntity);
        fieldsValues.put(selectedChildEntity.getEntityUuid(), selectedChildEntity);
    }

    /**
     * Remove child entity record from a given field
     * 
     * @param mainEntityCfv Main entity's custom field value containing child entities
     * @param childEntity Child entity record to remove
     */
    public void removeChildEntity(CustomFieldValue mainEntityCfv, CustomFieldValueHolder selectedChildEntity) {

        mainEntityCfv.getChildEntityValuesForGUI().remove(selectedChildEntity);
        fieldsValues.remove(selectedChildEntity.getEntityUuid());
        messages.info(new BundleKey("messages", "customFieldInstance.childEntity.delete.successful"));
    }

    /**
     * Serialize map, list and entity reference values that were adapted for GUI data entry. See CustomFieldValue.xxxGUI fields for transformation description
     * 
     * @param customFieldValue Value to serialize
     * @param cft Custom field template
     * @throws BusinessException
     */
    private void serializeFromGUI(CustomFieldValue customFieldValue, CustomFieldTemplate cft) {

        // Convert JPA object to Entity reference - just Single storage fields
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
            if (customFieldValue.getEntityReferenceValueForGUI() == null) {
                customFieldValue.setEntityReferenceValue(null);
            } else {
                customFieldValue.setEntityReferenceValue(new EntityReferenceWrapper(customFieldValue.getEntityReferenceValueForGUI()));
            }

            // Convert CustomFieldValueHolder object to EntityReferenceWrapper- ONLY LIST storage type field
        } else if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {

            List<Object> listValue = new ArrayList<Object>();
            for (CustomFieldValueHolder childEntityValueHolder : customFieldValue.getChildEntityValuesForGUI()) {
                listValue.add(new EntityReferenceWrapper((BusinessEntity) childEntityValueHolder.getEntity()));
            }
            customFieldValue.setListValue(listValue);

            // Populate customFieldValue.listValue from mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Object> listValue = new ArrayList<Object>();
            for (Map<String, Object> listItem : customFieldValue.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    listValue.add(new EntityReferenceWrapper((BusinessEntity) listItem.get(CustomFieldValue.MAP_VALUE)));

                } else {
                    listValue.add(listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            customFieldValue.setListValue(listValue);

            // Populate customFieldValue.mapValue from mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            Map<String, Object> mapValue = new LinkedHashMap<String, Object>();

            for (Map<String, Object> listItem : customFieldValue.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), new EntityReferenceWrapper((BusinessEntity) listItem.get(CustomFieldValue.MAP_VALUE)));

                } else {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            customFieldValue.setMapValue(mapValue);

            // Populate customFieldValue.mapValue from matrixValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

            Map<String, Object> mapValue = new LinkedHashMap<String, Object>();

            List<String> columnKeys = new ArrayList<String>();
            for (CustomFieldMatrixColumn column : cft.getMatrixColumnsSorted()) {
                columnKeys.add(column.getCode());
            }
            mapValue.put(CustomFieldValue.MAP_KEY, columnKeys);

            for (Map<String, Object> mapItem : customFieldValue.getMatrixValuesForGUI()) {
                Object value = mapItem.get(CustomFieldValue.MAP_VALUE);
                if (StringUtils.isBlank(value)) {
                    continue;
                }

                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    value = new EntityReferenceWrapper((BusinessEntity) value);
                }

                StringBuilder valBuilder = new StringBuilder();
                for (String column : columnKeys) {
                    valBuilder.append(valBuilder.length() == 0 ? "" : CustomFieldValue.MATRIX_KEY_SEPARATOR);
                    valBuilder.append(mapItem.get(column));
                }
                mapValue.put(valBuilder.toString(), value);
            }

            customFieldValue.setMapValue(mapValue);
        }
    }

    /**
     * Save child entities to DB as Custom entity instance object along with its custom fields.
     * 
     * @param mainEntity Entity of which child entity type field is being saved
     * @param customFieldValue Value to serialize
     * @param childEntityFieldDefinition Custom field template
     * @throws BusinessException
     */
    private void saveChildEntities(ICustomFieldEntity mainEntity, CustomFieldValue customFieldValue, CustomFieldTemplate childEntityFieldDefinition) throws BusinessException {
        if (childEntityFieldDefinition.getFieldType() != CustomFieldTypeEnum.CHILD_ENTITY) {
            return;
        }

        // Find current child entities, so the ones no longer referenced shall be removed
        List<CustomEntityInstance> previousChildEntities = customEntityInstanceService
            .findChildEntities(CustomFieldTemplate.retrieveCetCode(childEntityFieldDefinition.getEntityClazz()), mainEntity.getUuid());

        for (CustomFieldValueHolder childEntityValueHolder : customFieldValue.getChildEntityValuesForGUI()) {

            CustomEntityInstance cei = (CustomEntityInstance) childEntityValueHolder.getEntity();
            boolean isNewEntity = cei.isTransient();
            if (isNewEntity) {
                customEntityInstanceService.create(cei);
                saveCustomFieldsToEntity(cei, isNewEntity);

            } else {
                if (childEntityValueHolder.isUpdated()) {
                    cei = customEntityInstanceService.update(cei);
                    saveCustomFieldsToEntity(cei, isNewEntity);
                }
                previousChildEntities.remove(cei);
            }
        }

        // Remove child entities that are no longer referenced along with its custom field values
        for (CustomEntityInstance ceiNolongerReferenced : previousChildEntities) {
            customEntityInstanceService.remove(ceiNolongerReferenced);
        }
    }

    /**
     * Deserialize map, list and entity reference values to adapt them for GUI data entry. See CustomFieldValue.xxxGUI fields for transformation description
     * 
     * @param cft Custom field template
     */
    @SuppressWarnings("unchecked")
    private void deserializeForGUI(CustomFieldValue customFieldValue, CustomFieldTemplate cft) {

        // Convert just Entity type field to a JPA object - just Single storage fields
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
            customFieldValue.setEntityReferenceValueForGUI(deserializeEntityReferenceForGUI(customFieldValue.getEntityReferenceValue()));

            // Populate childEntityValuesForGUI field - ONLY LIST storage is supported
        } else if (cft.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY) {
            List<CustomFieldValueHolder> cheHolderList = new ArrayList<>();
            if (customFieldValue.getListValue() != null) {
                for (Object listItem : customFieldValue.getListValue()) {
                    CustomFieldValueHolder childEntityValueHolder = loadChildEntityForGUI((EntityReferenceWrapper) listItem);
                    if (childEntityValueHolder != null) {
                        cheHolderList.add(childEntityValueHolder);
                    }
                }
            }
            customFieldValue.setChildEntityValuesForGUI(cheHolderList);

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Map<String, Object>> listOfMapValues = new ArrayList<Map<String, Object>>();
            if (customFieldValue.getListValue() != null) {
                for (Object listItem : customFieldValue.getListValue()) {
                    Map<String, Object> listEntry = new HashMap<String, Object>();
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        listEntry.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) listItem));
                    } else {
                        listEntry.put(CustomFieldValue.MAP_VALUE, listItem);
                    }
                    listOfMapValues.add(listEntry);
                }
            }
            customFieldValue.setMapValuesForGUI(listOfMapValues);

            // Populate mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            List<Map<String, Object>> listOfMapValues = new ArrayList<Map<String, Object>>();

            if (customFieldValue.getMapValue() != null) {
                for (Entry<String, Object> mapInfo : ((Map<String, Object>) customFieldValue.getMapValue()).entrySet()) {
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
            customFieldValue.setMapValuesForGUI(listOfMapValues);

            // Populate matrixValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

            List<Map<String, Object>> mapValues = new ArrayList<Map<String, Object>>();
            customFieldValue.setMatrixValuesForGUI(mapValues);

            if (customFieldValue.getMapValue() != null) {

                Object columns = customFieldValue.getMapValue().get(CustomFieldValue.MAP_KEY);
                String[] columnArray = null;
                if (columns instanceof String) {
                    columnArray = ((String) columns).split(CustomFieldValue.MATRIX_COLUMN_NAME_SEPARATOR);
                } else if (columns instanceof Collection) {
                    columnArray = new String[((Collection<String>) columns).size()];
                    int i = 0;
                    for (String column : (Collection<String>) columns) {
                        columnArray[i] = column;
                        i++;
                    }
                }

                for (Entry<String, Object> mapItem : ((Map<String, Object>) customFieldValue.getMapValue()).entrySet()) {
                    if (mapItem.getKey().equals(CustomFieldValue.MAP_KEY)) {
                        continue;
                    }

                    Map<String, Object> mapValuesItem = new HashMap<String, Object>();
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        mapValuesItem.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) mapItem.getValue()));
                    } else {
                        mapValuesItem.put(CustomFieldValue.MAP_VALUE, mapItem.getValue());
                    }

                    String[] keys = mapItem.getKey().split("\\" + CustomFieldValue.MATRIX_KEY_SEPARATOR);
                    for (int i = 0; i < keys.length; i++) {
                        mapValuesItem.put(columnArray[i], keys[i]);
                    }
                    mapValues.add(mapValuesItem);
                }
            }
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

        // BusinessEntity convertedEntity = customFieldInstanceService.convertToBusinessEntityFromCfV(entityReferenceValue);
        // if (convertedEntity == null) {
        // convertedEntity = (BusinessEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
        // if (convertedEntity != null) {
        // convertedEntity.setCode("NOT FOUND: " + entityReferenceValue.getCode());
        // }
        // } else {

        try {
            BusinessEntity convertedEntity = (BusinessEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
            if (convertedEntity != null) {
                if (convertedEntity instanceof CustomEntityInstance) {
                    ((CustomEntityInstance) convertedEntity).setCetCode(entityReferenceValue.getClassnameCode());
                }

                convertedEntity.setCode(entityReferenceValue.getCode());
            } else {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.error("Unknown entity class specified " + entityReferenceValue.getClassname() + "in a custom field value {} ", entityReferenceValue);
            }
            // }
            return convertedEntity;

        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.error("Unknown entity class specified in a custom field value {} ", entityReferenceValue);
            return null;
        }
    }

    /**
     * Convert childEntity field type value of EntityReferenceWrapper type to GUI suitable format - CustomFieldValueHolder. Entity is loaded from db with all related custom fields.
     * 
     * @param childEntityWrapper EntityReferenceWrapper value to convert
     * @return CustomFieldValueHolder instance
     */
    private CustomFieldValueHolder loadChildEntityForGUI(EntityReferenceWrapper childEntityWrapper) {
        if (childEntityWrapper == null) {
            return null;
        }

        CustomEntityInstance cei = customEntityInstanceService.findByCodeByCet(childEntityWrapper.getClassnameCode(), childEntityWrapper.getCode());
        if (cei == null) {
            return null;
        }
        initFields(cei);
        return fieldsValues.get(cei.getUuid());
    }

    /**
     * Save custom fields for a given entity
     * 
     * @param entity Entity, the fields relate to
     * @throws BusinessException
     */
    public Map<CustomFieldTemplate, Object> loadCustomFieldsFromGUI(ICustomFieldEntity entity) throws BusinessException {
        Map<CustomFieldTemplate, Object> fieldMap = new HashMap<>();
        String uuid = entity.getUuid();
        CustomFieldValueHolder entityFieldsValues = getFieldValueHolderByUUID(uuid);
        GroupedCustomField groupedCustomFields = groupedFieldTemplates.get(uuid);
        if (groupedCustomFields != null) {
            for (CustomFieldTemplate cft : groupedCustomFields.getFields()) {
                for (CustomFieldValue cfValue : entityFieldsValues.getValues(cft)) {
                    serializeFromGUI(cfValue, cft);
                    if (CustomFieldTypeEnum.ENTITY.equals(cft.getFieldType())) {
                        fieldMap.put(cft, cfValue.getEntityReferenceValueForGUI());
                    } else {
                        fieldMap.put(cft, cfValue.getValue());
                    }
                }
            }
        }
        return fieldMap;
    }

    /**
     * Get custom field values for a given entity - in case of versioned custom fields, retrieve the latest value
     * 
     * @param entity Entity, the fields relate to
     * @throws BusinessException
     */
    public Map<CustomFieldTemplate, Object> getFieldValuesLatestValue(ICustomFieldEntity entity) throws BusinessException {
        Map<CustomFieldTemplate, Object> fieldMap = new HashMap<>();
        String uuid = entity.getUuid();
        CustomFieldValueHolder entityFieldsValues = getFieldValueHolderByUUID(uuid);
        GroupedCustomField groupedCustomFields = groupedFieldTemplates.get(uuid);
        if (groupedCustomFields != null) {
            for (CustomFieldTemplate cft : groupedCustomFields.getFields()) {

                // TODO instead of looping an preserving the last value only, could figure the latest value right away
                for (CustomFieldValue cfValue : entityFieldsValues.getValues(cft)) {

                    try {
                        serializeFromGUI(cfValue, cft);
                        fieldMap.put(cft, cfValue.getValue());

                    } catch (Exception e) {
                        log.error("Failed to convert custom field to product characteristic {} {}", cft.getCode(), cfValue);
                    }
                }
            }
        }
        return fieldMap;
    }

    /**
     * Set values of custom fields
     * 
     * @param cfValues A map of custom field values with CFT as a key and CF value as a value
     * @param entity Entity custom field values apply to
     */
    public void setCustomFieldValues(Map<CustomFieldTemplate, Object> cfValues, BusinessCFEntity entity) {

        if (entity == null) {
            return;
        }

        if (!groupedFieldTemplates.containsKey(entity.getUuid())) {
            initFields(entity);
        }

        CustomFieldValueHolder entityFieldsValues = getFieldValueHolderByUUID(entity.getUuid());

        for (Entry<CustomFieldTemplate, Object> cfValueInfo : cfValues.entrySet()) {
            CustomFieldValue cfValue = entityFieldsValues.getFirstValue(cfValueInfo.getKey().getCode());
            if (cfValue == null) {
                // log.error("AKK not CFI found in holder for {}", cfValueInfo.getKey().getCode());
                continue; // TODO - maybe we should add??
            }
            cfValue.setValue(cfValueInfo.getValue());
            deserializeForGUI(cfValue, cfValueInfo.getKey());
        }
    }

    /**
     * Get names of repeated custom field component forms and tabs ids
     * 
     * @param prefix prefix to apply
     * @param suffix suffix to apply
     * @param length Number of repeated items
     * @return A concatenated string of component ID values
     */
    public static String getCFComponentIds(String prefix, String suffix, int length) {
        if (length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(prefix + i + (suffix != null ? suffix : "") + " ");
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public int getActiveGroupedFieldTemplatesCount(ICustomFieldEntity entity) {

        if (entity == null) {
            return 0;
        }
        if (!groupedFieldTemplates.containsKey(entity.getUuid())) {
            initFields(entity);
        }

        GroupedCustomField groupCF = groupedFieldTemplates.get(entity.getUuid());
        CustomFieldValueHolder cfValueHolder = getFieldValueHolderByUUID(entity.getUuid());
        int ctr = 0;
        for (GroupedCustomField groupCFChild : groupCF.getChildren()) {
            if (groupCFChild.hasVisibleCustomFields(entity, cfValueHolder)) {
                ctr++;
            }
        }

        return ctr;
    }
}