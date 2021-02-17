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

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang3.math.NumberUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IReferenceEntity;
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
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptUtils;
import org.meveo.util.EntityCustomizationUtils;
import org.meveo.util.view.LazyDataModelWSize;
import org.meveo.util.view.NativeTableBasedDataModel;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Provides support for custom field value data entry
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Said Ramli
 * @author Abdellatif BARI
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
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
    private CustomTableService customTableService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * paramBeanFactory
     */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    protected Messages messages;

    @Inject
    private FacesContext facesContext;

    /**
     * Selected item in dataTable.
     */
    private Map<String, Object> selectedItem;
    /**
     * Custom table data model
     */
    private LazyDataModel<Map<String, Object>> customTableBasedDataModel;

    private List<Map<String, Object>> selectedValues;

    private String customTableName;

    private Map<String, Object> newValues = new HashMap<>();

    private List<CustomFieldTemplate> fields;

    /**
     * Logger.
     */
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

        List<EntityCustomAction> actionList = new ArrayList<>(actions.values());
        customActions.put(entity.getUuid(), actionList);
    }

    private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @Override
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

        // Get custom field instances mapped by a CFT code if entity has any field defined
        if (customFieldTemplates != null && customFieldTemplates.size() > 0) {

            Map<String, List<CustomFieldValue>> cfValuesByCode = new HashMap<>();
            if (entity.getCfValues() != null && entity.getCfValues().getValuesByCode() != null) {
                cfValuesByCode = entity.getCfValues().getValuesByCode();
            }

            cfValuesByCode = prepareCFIForGUI(customFieldTemplates, cfValuesByCode, entity);

            CustomFieldValueHolder entityFieldsValues = new CustomFieldValueHolder(customFieldTemplates, cfValuesByCode, entity);
            fieldsValues.put(entity.getUuid(), entityFieldsValues);
        }
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

        // Populate new value defaults for map, list and matrix fields
        entityFieldsValues.populateNewValueDefaults(customFieldTemplates.values(), null);

        // Populate new value defaults for simple fields
        for (CustomFieldTemplate cft : customFieldTemplates.values()) {
            if (entityFieldsValues.getValues(cft) == null && !cft.isVersionable()) {
                entityFieldsValues.getValuesByCode().put(cft.getCode(), Arrays.asList(cft.toDefaultCFValue()));
            }
        }
    }

    /**
     * Prepare custom field values for GUI - instantiate fields with default values, deserialize values for GUI.
     * 
     * @param customFieldTemplates Custom field templates applicable for the entity, mapped by a custom CFT code
     * @param cfValuesByCode Custom field values mapped by a CFT code
     * @param entity Entity containing custom field values
     * @return Prepared for GUI custom fields instances
     */
    private Map<String, List<CustomFieldValue>> prepareCFIForGUI(Map<String, CustomFieldTemplate> customFieldTemplates, Map<String, List<CustomFieldValue>> cfValuesByCode,
            ICustomFieldEntity entity) {

        Map<String, List<CustomFieldValue>> cfisPrepared = new HashMap<>();

        // For each template, check if custom field value exists, and instantiate one if needed with a default value
        cftLoop: for (CustomFieldTemplate cft : customFieldTemplates.values()) {

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

            // Mark field as not visible in GUI if its a List/Map/Matrix and contains a large amount of data and ignore its value, as exiting value in entity will be used during
            // saving process
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST || cft.getStorageType() == CustomFieldStorageTypeEnum.MAP
                    || cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
                cft.setHideInGUI(false);

                for (CustomFieldValue cfValue : cfValuesByTemplate) {
                    if (cfValue.isExcessiveInSize()) {
                        cft.setHideInGUI(true);
                        continue cftLoop;
                    }
                }
            }

            // Clone values and deserialize values for GUI if applicable
            List<CustomFieldValue> cfValuesByTemplateCloned = new ArrayList<>();
            for (CustomFieldValue cfValue : cfValuesByTemplate) {
                cfValue = cfValue.clone(); // SerializationUtils.clone(cfValue);
                deserializeForGUI(cfValue, cft);
                cfValuesByTemplateCloned.add(cfValue);
            }
            cfValuesByTemplate = cfValuesByTemplateCloned;

            // Make sure that only one value is retrieved
            if (!cft.isVersionable()) {
                cfValuesByTemplate = new ArrayList<>(cfValuesByTemplate.subList(0, 1));
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
     * Increase priority of a custom field value period
     * 
     * @param entityValueHolder custom field value holder
     * @param cft Custom field definition
     * @param valuePeriodToChange Custom field value period to change
     */
    public void increasePriority(CustomFieldValueHolder entityValueHolder, CustomFieldTemplate cft, CustomFieldValue valuePeriodToChange) {

        boolean changed = entityValueHolder.changePriority(cft, valuePeriodToChange, true);

        if (changed) {
            messages.info(new BundleKey("messages", "customFieldTemplate.periodValue.priorityIncreased"));
        }
    }

    /**
     * Decrease priority of a custom field value period
     * 
     * @param entityValueHolder custom field value holder
     * @param cft Custom field definition
     * @param valuePeriodToChange Custom field value period to change
     */
    public void decreasePriority(CustomFieldValueHolder entityValueHolder, CustomFieldTemplate cft, CustomFieldValue valuePeriodToChange) {

        boolean changed = entityValueHolder.changePriority(cft, valuePeriodToChange, false);

        if (changed) {
            messages.info(new BundleKey("messages", "customFieldTemplate.periodValue.priorityDecreased"));
        }
    }

    /**
     * Remove a customField period
     * 
     * @param entityValueHolder Entity custom field value holder
     * @param cft Custom field definition
     * @param valuePeriodToRemove Custom field value period to remove
     */
    public void removePeriod(CustomFieldValueHolder entityValueHolder, CustomFieldTemplate cft, CustomFieldValue valuePeriodToRemove) {

        boolean removed = entityValueHolder.removeValuePeriod(cft, valuePeriodToRemove);

        if (removed) {
            messages.info(new BundleKey("messages", "customFieldTemplate.periodValue.removedPeriod"));
        }
    }

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
            facesContext.validationFailed();
            return;
        }

        // Validate that value is set
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.SINGLE && value == null) {
            messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
            facesContext.validationFailed();
            return;
        }

        CustomFieldValue cfValue = null;
        // First check if any period matches the dates
        if (entityValueHolder.getValuePeriodMatched() == null || !entityValueHolder.getValuePeriodMatched()) {
            if (periodStartDate == null && periodEndDate == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.periodDatesBothNull"));
                entityValueHolder.setValuePeriodMatched(true);
                facesContext.validationFailed();
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
                ParamBean paramBean = paramBeanFactory.getInstance();
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
                facesContext.validationFailed();
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
                    facesContext.validationFailed();
                    return;

                } else if (from != null && to != null && from.compareTo(to) >= 0) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                    facesContext.validationFailed();
                    return;
                }
                newKey = (from == null ? "" : from) + CustomFieldValue.RON_VALUE_SEPARATOR + (to == null ? "" : to);
            }

            if (newKey == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyNotSpecified"));
                facesContext.validationFailed();
                return;
            }
        }

        Object newValue = entityValueHolder.getNewValue(cft.getCode() + "_value");
        if (newValue == null) {
            messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
            facesContext.validationFailed();
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
                facesContext.validationFailed();
                return;
            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST && mapItem.get(CustomFieldValue.MAP_VALUE).equals(newValue)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.listValueExists"));
                facesContext.validationFailed();
                return;
            }
        }

        cfv.getMapValuesForGUI().add(value);
        cfv.setDatasetForGUI(null);

        entityValueHolder.clearNewValues();
    }

    /**
     * Autocomplete method for listing entities for "Reference to entity" type custom field values
     * 
     * @param wildcode A partial entity code match
     * @return A list of entities [partially] matching code
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public List<BusinessEntity> autocompleteEntityForCFV(String wildcode) throws ClassNotFoundException {
        String classname = (String) UIComponent.getCurrentComponent(facesContext).getAttributes().get("classname");
        int limit =  paramBeanFactory.getInstance().getPropertyAsInteger("autocomplete.limit", 50);
		return customFieldInstanceService.findBusinessEntityForCFVByCode(classname, wildcode, limit);
    }

    /**
     * Validate complex custom fields.
     * 
     * @param entity Entity, to which custom fields are related to
     * @return Are custom fields valid or not
     */
    public boolean validateCustomFields(ICustomFieldEntity entity) {
        boolean valid = true;
        boolean isNewEntity = ((IEntity) entity).isTransient();

        FacesContext fc = facesContext;
        for (CustomFieldTemplate cft : groupedFieldTemplates.get(entity.getUuid()).getFields()) {

            // Ignore the validation on a field when creating entity and CFT.hideOnNew=true or editing entity and CFT.allowEdit=false or when CFT.applicableOnEL expression
            // evaluates to false or when field is hidden in GUI CFT.hiddenInGUI=true
            if (cft.isDisabled() || !cft.isValueRequired() || cft.isHideInGUI() || (isNewEntity && cft.isHideOnNew()) || (!isNewEntity && !cft.isAllowEdit())
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
     * @param entity to get the inherited value for
     * @param cfCode Custom field code
     * @return Custom field value
     */
    public Object getInheritedCFValue(ICustomFieldEntity entity, String cfCode) {
        return customFieldInstanceService.getInheritedOnlyCFValue(entity, cfCode);
    }

    /**
     * Get a a list of custom field CFvalues for a given entity's parent's hierarchy up. (DOES NOT include a given entity)
     * 
     * @param entity Entity
     * @param cft Custom field definition
     * @return A list of Custom field CFvalues. From all the entities CF entity hierarchy up.
     */
    public List<CustomFieldValue> getInheritedVersionableCFValue(ICustomFieldEntity entity, CustomFieldTemplate cft) {
        List<CustomFieldValue> values = new ArrayList<>();
        if (cft != null) {
            values.addAll(customFieldInstanceService.getInheritedOnlyAllCFValues(entity, cft.getCode()));

            for (CustomFieldValue cfv : values) {
                deserializeForGUI(cfv, cft);
            }
        }

        return values;
    }

    /**
     * Get inherited custom field value for a given entity. A cumulative custom field value is calculated for Map(Matrix) type fields
     * 
     * @param entity to get the inherited value for
     * @param cft Custom field definition
     * @return Custom field value
     */
    public CustomFieldValue getInheritedCumulativeCFValue(ICustomFieldEntity entity, CustomFieldTemplate cft) {
        if (cft == null) {
            return null;
        }

        Object inheritedValue = customFieldInstanceService.getInheritedOnlyCFValueCumulative(entity, cft.getCode());
        if (inheritedValue == null) {
            return null;
        }

        CustomFieldValue cfv = new CustomFieldValue();
        cfv.setValue(inheritedValue);
        deserializeForGUI(cfv, cft);

        return cfv;
    }

    /**
     * Add row to a matrix. v5.0: Fix for save values on a multi values CF type problem
     *
     * @param entityValueHolder Entity custom field value holder
     * @param cfValue Map value holder
     * @param cft Custom field definition
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public void addMatrixRow(CustomFieldValueHolder entityValueHolder, CustomFieldValue cfValue, CustomFieldTemplate cft) {

        Map<String, Object> rowKeysAndValues = new HashMap<String, Object>();

        // Process keys
        for (CustomFieldMatrixColumn column : cft.getMatrixKeyColumns()) {

            Object newKey = null;

            if (column.getKeyType() == CustomFieldMapKeyEnum.STRING) {
                newKey = (String) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

                // No reason to support Long and Double as key values as it us covered by a range, but - why not??
            } else if (column.getKeyType() == CustomFieldMapKeyEnum.LONG) {
                newKey = (Long) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

            } else if (column.getKeyType() == CustomFieldMapKeyEnum.DOUBLE) {
                newKey = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

            } else if (column.getKeyType() == CustomFieldMapKeyEnum.RON) {
                // Validate that at least one value is provided and in correct order
                Double from = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode() + "_from");
                Double to = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode() + "_to");

                if (from == null && to == null) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                    facesContext.validationFailed();
                    return;

                } else if (from != null && to != null && from.compareTo(to) >= 0) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                    facesContext.validationFailed();
                    return;
                }
                newKey = (from == null ? "" : from) + CustomFieldValue.RON_VALUE_SEPARATOR + (to == null ? "" : to);
            }

            if (newKey != null) {
                rowKeysAndValues.put(column.getCode(), newKey);
            }
        }

        if (rowKeysAndValues.isEmpty()) {
            messages.error(new BundleKey("messages", "customFieldTemplate.matrixKeyNotSpecified"));
            facesContext.validationFailed();
            return;
        }

        // Process values

        // Multiple value columns
        if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {

            Map<String, Object> rowValues = new HashMap<String, Object>();

            for (CustomFieldMatrixColumn column : cft.getMatrixValueColumns()) {

                Object newValue = null;

                if (column.getKeyType() == CustomFieldMapKeyEnum.STRING) {
                    newValue = (String) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

                } else if (column.getKeyType() == CustomFieldMapKeyEnum.LONG) {
                    newValue = (Long) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

                } else if (column.getKeyType() == CustomFieldMapKeyEnum.DOUBLE) {
                    newValue = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode());

                    // No reason to support RON as value data type - but code is copied - so why not
                } else if (column.getKeyType() == CustomFieldMapKeyEnum.RON) {
                    // Validate that at least one value is provided and in correct order
                    Double from = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode() + "_from");
                    Double to = (Double) entityValueHolder.getNewValue(cft.getCode() + "_" + column.getCode() + "_to");

                    if (from == null && to == null) {
                        messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                        facesContext.validationFailed();
                        return;

                    } else if (from != null && to != null && from.compareTo(to) >= 0) {
                        messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                        facesContext.validationFailed();
                        return;
                    }
                    newValue = (from == null ? "" : from) + CustomFieldValue.RON_VALUE_SEPARATOR + (to == null ? "" : to);
                }

                if (newValue != null) {
                    rowValues.put(column.getCode(), newValue);
                }
            }

            if (rowValues.isEmpty()) {
                messages.error(new BundleKey("messages", "customFieldTemplate.valuesNotSpecified"));
                facesContext.validationFailed();
                return;
            }

            rowKeysAndValues.putAll(rowValues);

            // Single value column
        } else {
            Object newValue = entityValueHolder.getNewValue(cft.getCode() + "_value");
            if (newValue == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
                facesContext.validationFailed();
                return;
            }

            rowKeysAndValues.put(CustomFieldValue.MAP_VALUE, newValue);
        }

        // Validate that key or value is not duplicate
        for (Map<String, Object> mapItem : cfValue.getMatrixValuesForGUI()) {
            boolean allMatch = true;
            for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {
                if (mapItem.get(column.getCode()) == null && rowKeysAndValues.get(column.getCode()) == null) {

                } else if (mapItem.get(column.getCode()) != null && !mapItem.get(column.getCode()).equals(rowKeysAndValues.get(column.getCode()))) {
                    allMatch = false;
                    break;
                } else if (rowKeysAndValues.get(column.getCode()) != null && !rowKeysAndValues.get(column.getCode()).equals(mapItem.get(column.getCode()))) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                messages.error(new BundleKey("messages", "customFieldTemplate.matrixKeyExists"));
                facesContext.validationFailed();
                return;
            }
        }

        cfValue.getMatrixValuesForGUI().add(rowKeysAndValues);
        cfValue.setDatasetForGUI(null);

        entityValueHolder.clearNewValues();
    }

    /**
     * Execute custom action on an entity
     * 
     * @param entity Entity to execute action on
     * @param action Action to execute
     * @param encodedParameters Additional parameters encoded in URL like style param=value&amp;param=value
     * @return A script execution result value from Script.RESULT_GUI_OUTCOME variable
     */
    public String executeCustomAction(ICustomFieldEntity entity, EntityCustomAction action, String encodedParameters) {

        try {

            action = entityActionScriptService.retrieveIfNotManaged(action);

            Map<String, Object> context = ScriptUtils.parseParameters(encodedParameters);
            context.put(Script.CONTEXT_ACTION, action.getCode());
            Map<String, Object> result = scriptInstanceService.execute((IEntity) entity, action.getScript().getCode(), context);

            // Display a message accordingly on what is set in result
            if (result.containsKey(Script.RESULT_GUI_MESSAGE_KEY)) {
                messages.info(new BundleKey("messages", (String) result.get(Script.RESULT_GUI_MESSAGE_KEY)));
                log.info("A key message to show after entity custom action execution (RESULT_GUI_MESSAGE_KEY) is : {}", (String) result.get(Script.RESULT_GUI_MESSAGE_KEY));

            } else if (result.containsKey(Script.RESULT_GUI_MESSAGE)) {
                String message = (String) result.get(Script.RESULT_GUI_MESSAGE);
                messages.info(message);
                log.info("A message to show after entity custom action execution (RESULT_GUI_MESSAGE) is : {}", message);

            } else {
                messages.info(new BundleKey("messages", "scriptInstance.actionExecutionSuccessfull"), action.getLabel());
                log.info("A message to show after entity custom action execution is : scriptInstance.actionExecutionSuccessfull");
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
     * @param encodedParameters Additional parameters encoded in URL like style param=value&amp;param=value
     * @return A script execution result value from Script.RESULT_GUI_OUTCOME variable
     */
    public String executeCustomActionOnChildEntity(ICustomFieldEntity parentEntity, ICustomFieldEntity childEntity, EntityCustomAction action, String encodedParameters) {

        try {

            Map<String, Object> context = ScriptUtils.parseParameters(encodedParameters);
            context.put(Script.CONTEXT_PARENT_ENTITY, parentEntity);
            context.put(Script.CONTEXT_ACTION, action.getCode());

            Map<String, Object> result = scriptInstanceService.execute((IEntity) childEntity, action.getScript().getCode(), context);

            // Display a message accordingly on what is set in result
            if (result.containsKey(Script.RESULT_GUI_MESSAGE_KEY)) {
                messages.info(new BundleKey("messages", (String) result.get(Script.RESULT_GUI_MESSAGE_KEY)));

            } else if (result.containsKey(Script.RESULT_GUI_MESSAGE)) {
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
     * @return CustomFieldValue Map
     * @throws BusinessException General business exception
     */
    public Map<String, List<CustomFieldValue>> saveCustomFieldsToEntity(ICustomFieldEntity entity, boolean isNewEntity) throws BusinessException {
        String uuid = entity.getUuid();
        return saveCustomFieldsToEntity(entity, uuid, false, isNewEntity, false);
    }

    /**
     * Save custom fields for a given entity.
     * 
     * @param entity Entity, the fields relate to
     * @param uuid Unique uuid of field value holder
     * @param duplicateCFI Should custom field value be duplicated.
     * @param isNewEntity Is it a new entity
     * @param removedOriginalCFI - When duplicating a CFI, this boolean is true when we want to remove the original CFI. Use specially in offer instantiation where we assigned CFT
     *        values on entity a but then save it on entity b. Entity a is then reverted. This flag is needed because on some part CFI is duplicated first, but is not updated,
     *        instead we duplicate again.
     * @return CustomFieldValue Map
     * @throws BusinessException General business exception
     */
    public Map<String, List<CustomFieldValue>> saveCustomFieldsToEntity(ICustomFieldEntity entity, String uuid, boolean duplicateCFI, boolean isNewEntity,
            boolean removedOriginalCFI) throws BusinessException {

        Map<String, List<CustomFieldValue>> newValuesByCode = new HashMap<>();

        CustomFieldValueHolder entityFieldsValues = getFieldValueHolderByUUID(uuid);
        GroupedCustomField groupedCustomFields = groupedFieldTemplates.get(uuid);
        if (groupedCustomFields != null) {
            for (CustomFieldTemplate cft : groupedCustomFields.getFields()) {

                // Do not update existing CF value if it is not updatable or was hidden in GUI
                if ((!isNewEntity && !cft.isAllowEdit()) || cft.isHideInGUI()) {

                    if (entity != null && entity.getCfValues() != null) {
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
                    // escape this control when the CF is Multi CHECKBOX MENU
                    if (!CustomFieldTypeEnum.CHECKBOX_LIST.name().equals(cft.getFieldType().name()) && (
                            (cfValue.isValueEmptyForGui() && (cft.getDefaultValue() == null || cft.getStorageType() != CustomFieldStorageTypeEnum.SINGLE) && !cft.isVersionable())
                                    || ((isNewEntity && cft.isHideOnNew()) || (entity != null && !ValueExpressionWrapper
                                    .evaluateToBooleanOneVariable(cft.getApplicableOnEl(), "entity", entity))))) {
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
        if (entity != null) {
            if (newValuesByCode.isEmpty()) {
                entity.clearCfValues();
            } else {
                entity.getCfValuesNullSafe().setValues(newValuesByCode);
            }
        }

        return newValuesByCode;
    }

    /**
     * Get a child entity column corresponding to a given code
     * 
     * @param childEntityTypeFieldDefinition Child entity type field definition
     * @param childFieldCode Child entity field code
     * @return customFieldTemplate
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
     * Save child entity record.
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
        if ((cei.isTransient() && ceiSameCode != null) || (!cei.isTransient() && ceiSameCode != null && cei.getId().longValue() != ceiSameCode.getId().longValue())) {
            messages.error(new BundleKey("messages", "commons.uniqueField.code"));
            facesContext.validationFailed();
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
     * Prepare to edit child entity.
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
     * @param selectedChildEntity Child entity record to remove
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
     * @throws BusinessException General business exception
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
                listValue.add(new EntityReferenceWrapper((IReferenceEntity) childEntityValueHolder.getEntity()));
            }
            customFieldValue.setListValue(listValue);

            // Populate customFieldValue.listValue from mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {

            List<Object> listValue = new ArrayList<>();
            if (CustomFieldTypeEnum.CHECKBOX_LIST.name().equalsIgnoreCase(cft.getFieldType().name())) {
            	if(customFieldValue != null && customFieldValue.getListValue() != null) {
            		listValue.addAll(customFieldValue.getListValue());
            	}
            } else {
            for (Map<String, Object> listItem : customFieldValue.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    listValue.add(new EntityReferenceWrapper((IReferenceEntity) listItem.get(CustomFieldValue.MAP_VALUE)));

                } else {
                    listValue.add(listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            }
            customFieldValue.setListValue(listValue);

            // Populate customFieldValue.mapValue from mapValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {

            Map<String, Object> mapValue = new LinkedHashMap<String, Object>();

            for (Map<String, Object> listItem : customFieldValue.getMapValuesForGUI()) {
                if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), new EntityReferenceWrapper((IReferenceEntity) listItem.get(CustomFieldValue.MAP_VALUE)));

                } else {
                    mapValue.put((String) listItem.get(CustomFieldValue.MAP_KEY), listItem.get(CustomFieldValue.MAP_VALUE));
                }
            }
            customFieldValue.setMapValue(mapValue);

            // Populate customFieldValue.mapValue from matrixValuesForGUI field
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {

            Map<String, Object> mapValue = new LinkedHashMap<String, Object>();

            List<String> keyColumns = cft.getMatrixKeyColumnCodes();

            for (Map<String, Object> mapItem : customFieldValue.getMatrixValuesForGUI()) {

                Object value = null;

                // Multi-value values need to be concatenated and stored as string
                if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
                	
                    value = cft.serializeMultiValue(mapItem);
                    if (value == null) {
                    	java.util.ResourceBundle bundle = facesContext.getApplication().getResourceBundle(facesContext, "messages");
                    	String message = bundle.getString("customFieldTemplate.matrix.key.valuesNotSpecified");
                        //facesContext.validationFailed();
                    	throw new BusinessException(MessageFormat.format(message, new Object [] {mapItem.get(cft.getMatrixColumnByIndex(0).getLabel())}));
                    }

                } else {
                    value = mapItem.get(CustomFieldValue.MAP_VALUE);
                    if (StringUtils.isBlank(value)) {
                    	continue;
                    }

                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        value = new EntityReferenceWrapper((IReferenceEntity) value);
                    }
                }

                StringBuilder keyBuilder = new StringBuilder();
                for (String column : keyColumns) {
                    keyBuilder.append(keyColumns.indexOf(column) == 0 ? "" : CustomFieldValue.MATRIX_KEY_SEPARATOR);
                    keyBuilder.append(mapItem.get(column) != null ? mapItem.get(column) : "");
                }

                mapValue.put(keyBuilder.toString(), value);

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
     * @throws BusinessException General business exception
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

                List<String> keyColumnCodes = cft.getMatrixKeyColumnCodes();

                for (Entry<String, Object> mapItem : ((Map<String, Object>) customFieldValue.getMapValue()).entrySet()) {
                    if (mapItem.getKey().equals(CustomFieldValue.MAP_KEY)) {
                        continue;
                    }

                    Map<String, Object> mapItemKeysAndValues = new HashMap<String, Object>();
                    if (cft.getFieldType() == CustomFieldTypeEnum.ENTITY) {
                        mapItemKeysAndValues.put(CustomFieldValue.MAP_VALUE, deserializeEntityReferenceForGUI((EntityReferenceWrapper) mapItem.getValue()));

                    } else if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
                        cft.deserializeMultiValue((String) mapItem.getValue(), mapItemKeysAndValues);

                    } else {
                        mapItemKeysAndValues.put(CustomFieldValue.MAP_VALUE, mapItem.getValue());
                    }

                    // Matrix keys are concatenated when stored - split them and set as separate map key/values
                    String[] keys = mapItem.getKey().split("\\" + CustomFieldValue.MATRIX_KEY_SEPARATOR);
                    for (int i = 0; i < keys.length; i++) {
                        mapItemKeysAndValues.put(keyColumnCodes.get(i), keys[i]);
                    }

                    mapValues.add(mapItemKeysAndValues);
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
    private IReferenceEntity deserializeEntityReferenceForGUI(EntityReferenceWrapper entityReferenceValue) {
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
            IReferenceEntity convertedEntity = (IReferenceEntity) ReflectionUtils.createObject(entityReferenceValue.getClassname());
            if (convertedEntity != null) {
                if (convertedEntity instanceof CustomEntityInstance) {
                    ((CustomEntityInstance) convertedEntity).setCetCode(entityReferenceValue.getClassnameCode());
                }

                convertedEntity.setReferenceCode(entityReferenceValue.getCode());
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
     * Save custom fields for a given entity.
     * 
     * @param entity Entity, the fields relate to
     * @return CustomFieldTemplate and Value Map
     * @throws BusinessException General business exception
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
     * Get custom field values for a given entity - in case of versioned custom fields, retrieve the latest value.
     * 
     * @param entity Entity, the fields relate to
     * @return CustomFieldTemplate and Value Map
     * @throws BusinessException General business exception
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

    /**
     * Get currently active locale
     * 
     * @return Currently active locale
     */
    public Locale getCurrentLocale() {
        return facesContext.getViewRoot().getLocale();
    }

    /**
     * Calculate a parent JSF component id based on a given component id
     * 
     * @param componentId Component identifier
     * @return A parent JSF component id
     */
    public String getParentComponentId(String componentId) {

        int index = componentId.lastIndexOf(':');
        if (index > 0) {
            return componentId.substring(0, index);
        }
        return componentId;
    }

    public boolean hasVisibleTabs(List<GroupedCustomField> children, ICustomFieldEntity entity, CustomFieldValueHolder cfValueHolder) {
        for (GroupedCustomField cfTab : children) {
            if (cfTab.hasVisibleCustomFields(entity, cfValueHolder)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get key of ron type.
     * 
     * @param key custom field key
     * @return the custom field key or null in the error case
     */
    private Object getRonKey(Object key) {
        Object ronkey = key;
        if (ronkey != null) {
            String[] ron = ((String) ronkey).split(CustomFieldValue.RON_VALUE_SEPARATOR);

            if(ron.length > 0 && !((String) ronkey).isEmpty()) {

                for(String valueOfRON : ron) {
                    if(!valueOfRON.isEmpty() && !NumberUtils.isParsable(valueOfRON)) {
                        messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                        facesContext.validationFailed();
                        return null;
                    }
                }

                if (ron[0] == null && ron.length > 1 && ron[1] == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                facesContext.validationFailed();
                return null;

                } else if (ron[0] != null  && !ron[0].isEmpty() && ron.length > 1 && ron[1] != null) {
                try {
                    if (Double.valueOf(ron[0]).compareTo(Double.valueOf(ron[1])) >= 0) {
                        messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                        facesContext.validationFailed();
                        return null;
                    }

                } catch (NumberFormatException e) {
                    messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                    facesContext.validationFailed();
                    return null;
                }
                } else if (ron[0] != null && ron.length == 1) {
                    try {
                        Double.parseDouble(ron[0]);
                    } catch (NumberFormatException e) {
                        messages.error(new BundleKey("messages", "customFieldTemplate.fromOrToOrder"));
                        facesContext.validationFailed();
                        return null;
            }
                }
        } else {
                messages.error(new BundleKey("messages", "customFieldTemplate.eitherFromOrToRequired"));
                facesContext.validationFailed();
                return null;
            }
        } else {
            messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyNotSpecified"));
            facesContext.validationFailed();
            return null;
        }
        return ronkey;
    }

    /**
     * Get the key of custom field
     * 
     * @param cft the custom field
     * @param csvLine the csv line
     * @return the custom field key or null in the error case
     */
    private String getMapKey(CustomFieldTemplate cft, Map<String, Object> csvLine) {
        String key = null;
        if (cft.getMapKeyType() == CustomFieldMapKeyEnum.STRING) {
            key = (String) csvLine.get(CustomFieldValue.MAP_KEY);
        }
        if (cft.getMapKeyType() == CustomFieldMapKeyEnum.RON) {
            key = (String) getRonKey(csvLine.get(CustomFieldValue.MAP_KEY));
            if (key == null) {
                return null;
            }
        }
        if (key == null) {
            messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyNotSpecified"));
            facesContext.validationFailed();
            return null;
        }
        return key;
    }

    /**
     * Get the value of custom field
     * 
     * @param cft the custom field
     * @param csvLine the csv line
     * @return the custom field value or null in the error case
     */
    private Object getMapValue(CustomFieldTemplate cft, Map<String, Object> csvLine) {
        switch (cft.getFieldType()){
            case DOUBLE:
                return Double.parseDouble((String) csvLine.get(CustomFieldValue.MAP_VALUE));
            case LONG:
                return Long.parseLong((String) csvLine.get(CustomFieldValue.MAP_VALUE));
            case STRING:
            case DATE:
            case TEXT_AREA:
            case ENTITY:
            case CHILD_ENTITY:
            case LIST:
            case MULTI_VALUE:
                return csvLine.get(CustomFieldValue.MAP_VALUE);
            default:
                messages.error(new BundleKey("messages", "customFieldTemplate.valueNotSpecified"));
                facesContext.validationFailed();
                return null;
        }
    }

    /**
     * Validate keys and map values
     * 
     * @param cft the custom field
     * @param mapValuesForGUI the map values for GUI
     * @param key the map key
     * @param value the map value
     * @return true is the map is valid or false in the error case.
     */
    private boolean validateMapKeysValues(CustomFieldTemplate cft, List<Map<String, Object>> mapValuesForGUI, String key, Object value) {

        for (Map<String, Object> mapItem : mapValuesForGUI) {
            if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP && mapItem.get(CustomFieldValue.MAP_KEY).equals(key)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyExists"));
                facesContext.validationFailed();
                return false;
            } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST && mapItem.get(CustomFieldValue.MAP_VALUE).equals(value)) {
                messages.error(new BundleKey("messages", "customFieldTemplate.listValueExists"));
                facesContext.validationFailed();
                return false;
            }
        }
        return true;
    }

    /**
     * Add csv line to map GUI
     * 
     * @param cft the custom field
     * @param mapValuesForGUI the map values for GUI
     * @param csvLine the csv line
     * @return true is the map is valid or false in the error case.
     */
    private boolean addMapValuesItem(CustomFieldTemplate cft, List<Map<String, Object>> mapValuesForGUI, Map<String, Object> csvLine) {
        Map<String, Object> mapValuesItem = new HashMap<String, Object>();

        String key = null;
        if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            // get the key
            key = getMapKey(cft, csvLine);
            if (key == null) {
                return false;
            }
            mapValuesItem.put(CustomFieldValue.MAP_KEY, key);
        }

        // get the value
        Object value = getMapValue(cft, csvLine);
        if (value == null) {
            return false;
        }
        mapValuesItem.put(CustomFieldValue.MAP_VALUE, value);

        // Validate that key or value is not duplicate
        if (!validateMapKeysValues(cft, mapValuesForGUI, key, value)) {
            return false;
        }

        mapValuesForGUI.add(mapValuesItem);
        return true;
    }

    /**
     * Get the matrix key
     * 
     * @param cft the custom field
     * @param column the custom field column
     * @param csvLine csv line
     * @return the matrix key if the matrix is valid or null in the error case.
     */
    private Object getMatrixKey(CustomFieldTemplate cft, CustomFieldMatrixColumn column, Map<String, Object> csvLine) {
        Object key = null;
        try {
            if (column.getKeyType() == CustomFieldMapKeyEnum.STRING) {
                key = (String) csvLine.get(column.getCode());
            } else if (column.getKeyType() == CustomFieldMapKeyEnum.LONG) {
                key = Long.valueOf((String) csvLine.get(column.getCode()));
            } else if (column.getKeyType() == CustomFieldMapKeyEnum.DOUBLE) {
                key = Double.valueOf((String) csvLine.get(column.getCode()));
            } else if (column.getKeyType() == CustomFieldMapKeyEnum.RON) {
                key = (String) getRonKey(csvLine.get(column.getCode()));
                if (key == null) {
                    return null;
                }
            }
            if (key == null) {
                messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyNotSpecified"));
                facesContext.validationFailed();
                return null;
            }
        } catch (ClassCastException | NumberFormatException e) {
            messages.error(new BundleKey("messages", "customFieldTemplate.mapKeyValueIsInvalid"), e.getMessage());
            facesContext.validationFailed();
            key = null;
        }
        return key;
    }

    /**
     * Validate the keys and values matrix.
     * 
     * @param cft the custom field
     * @param matrixValuesForGUI the matrix values for GUI
     * @param matrixKeysValuesItem the matrix keys and values
     * @return true is the matrix is valid or false in the error case.
     */
    private boolean validateMatrixKeysValues(CustomFieldTemplate cft, List<Map<String, Object>> matrixValuesForGUI, Map<String, Object> matrixKeysValuesItem) {

        for (Map<String, Object> mapItem : matrixValuesForGUI) {
            boolean allMatch = true;
            for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {
                if (mapItem.get(column.getCode()) == null && matrixKeysValuesItem.get(column.getCode()) == null) {

                } else if (mapItem.get(column.getCode()) != null && !mapItem.get(column.getCode()).equals(matrixKeysValuesItem.get(column.getCode()))) {
                    allMatch = false;
                    break;
                } else if (matrixKeysValuesItem.get(column.getCode()) != null && !matrixKeysValuesItem.get(column.getCode()).equals(mapItem.get(column.getCode()))) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                messages.error(new BundleKey("messages", "customFieldTemplate.matrixKeyExists"));
                facesContext.validationFailed();
                return false;
            }
        }
        return true;
    }

    /**
     * Get the keys matrix
     * 
     * @param cft the custom field
     * @param csvLine the csv line.
     * @return the keys matrix is the matrix is valid or null in the error case.
     */
    private Map<String, Object> getMatrixKeysItem(CustomFieldTemplate cft, Map<String, Object> csvLine) {
        Map<String, Object> matrixKeysItem = new HashMap<String, Object>();
        for (CustomFieldMatrixColumn column : cft.getMatrixKeyColumns()) {

            // get the key
            Object key = getMatrixKey(cft, column, csvLine);
            if (key == null) {
                return null;
            }
            matrixKeysItem.put(column.getCode(), key);
        }

        if (matrixKeysItem.isEmpty()) {
            messages.error(new BundleKey("messages", "customFieldTemplate.matrixKeyNotSpecified"));
            facesContext.validationFailed();
            return null;
        }
        return matrixKeysItem;
    }

    /**
     * Get the values matrix
     * 
     * @param cft the custom field
     * @param csvLine the csv line.
     * @return the values matrix is the matrix is valid or null in the error case.
     */
    private Map<String, Object> getMatrixValuesItem(CustomFieldTemplate cft, Map<String, Object> csvLine) {
        Map<String, Object> matrixValuesItem = new HashMap<String, Object>();
        if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
            for (CustomFieldMatrixColumn column : cft.getMatrixValueColumns()) {
                // get the key
                Object key = getMatrixKey(cft, column, csvLine);
                if (key == null) {
                    return null;
                }
                matrixValuesItem.put(column.getCode(), key);
            }

            if (matrixValuesItem.isEmpty()) {
                messages.error(new BundleKey("messages", "customFieldTemplate.valuesNotSpecified"));
                facesContext.validationFailed();
                return null;
            }
            // Single value column
        } else {
            // get the value
            Object value = getMapValue(cft, csvLine);
            if (value == null) {
                return null;
            }
            matrixValuesItem.put(CustomFieldValue.MAP_VALUE, value);
        }
        return matrixValuesItem;
    }

    /**
     * Get the values matrix
     * 
     * @param cft the custom field
     * @param matrixValuesForGUI the matrix values for GUI
     * @param csvLine the csv line
     * @return the values matrix is the matrix is valid or false in the error case.
     */
    private boolean addMatrixValuesItem(CustomFieldTemplate cft, List<Map<String, Object>> matrixValuesForGUI, Map<String, Object> csvLine) {
        Map<String, Object> matrixKeysValuesItem = new HashMap<String, Object>();

        // Process keys
        Map<String, Object> matrixKeysItem = getMatrixKeysItem(cft, csvLine);
        if (matrixKeysItem == null) {
            return false;
        }
        matrixKeysValuesItem.putAll(matrixKeysItem);

        // Process values
        Map<String, Object> matrixValuesItem = getMatrixValuesItem(cft, csvLine);
        if (matrixValuesItem == null) {
            return false;
        }
        matrixKeysValuesItem.putAll(matrixValuesItem);

        // Validate that key or value is not duplicate
        if (!validateMatrixKeysValues(cft, matrixValuesForGUI, matrixKeysValuesItem)) {
            return false;
        }

        matrixValuesForGUI.add(matrixKeysValuesItem);
        return true;
    }

    /**
     * Get the file reader
     * 
     * @param cft the custom field
     * @return the file reader
     */
    private ObjectReader getReader(CustomFieldTemplate cft) {
        CsvSchema.Builder builder = CsvSchema.builder();

        if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST) {
            builder.addColumn(CustomFieldValue.MAP_VALUE).build();
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
            builder.addColumn(CustomFieldValue.MAP_KEY).addColumn(CustomFieldValue.MAP_VALUE).build();
        } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
            for (CustomFieldMatrixColumn column : cft.getMatrixColumns()) {
                builder.addColumn(column.getCode());
            }
            if (cft.getFieldType() != CustomFieldTypeEnum.MULTI_VALUE) {
                builder.addColumn(CustomFieldValue.MAP_VALUE);
            }
        }

        CsvSchema schema = builder.build();
        CsvMapper mapper = new CsvMapper();
        return mapper.readerFor(Map.class).with(schema);
    }

    /**
     * Handle a file upload and import the file
     * 
     * @param event File upload event
     */
    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();

        if (file != null) {

            CustomFieldValue cfv = (CustomFieldValue) event.getComponent().getAttributes().get("cfv");
            CustomFieldTemplate cft = (CustomFieldTemplate) event.getComponent().getAttributes().get("cft");

            int importedLines = 0;
            List<Map<String, Object>> mapValuesForGUI = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> matrixValuesForGUI = new ArrayList<Map<String, Object>>();

            // read from file
            ObjectReader oReader = getReader(cft);
            try (Reader reader = new InputStreamReader(file.getInputstream())) {
                MappingIterator<Map<String, Object>> mappingIterator = oReader.readValues(reader);
                while (mappingIterator.hasNext()) {

                    Map<String, Object> csvLine = mappingIterator.next();

                    if (cft.getStorageType() == CustomFieldStorageTypeEnum.LIST || cft.getStorageType() == CustomFieldStorageTypeEnum.MAP) {
                        if (!addMapValuesItem(cft, mapValuesForGUI, csvLine)) {
                            messages.error(new BundleKey("messages", "customFieldTemplate.importFile.fail"), importedLines + 1);
                            return;
                        }
                    } else if (cft.getStorageType() == CustomFieldStorageTypeEnum.MATRIX) {
                        if (!addMatrixValuesItem(cft, matrixValuesForGUI, csvLine)) {
                            messages.error(new BundleKey("messages", "customFieldTemplate.importFile.fail"), importedLines + 1);
                            return;
                        }
                    }
                    importedLines++;
                }
                if (!mapValuesForGUI.isEmpty()) {
                    cfv.setMapValuesForGUI(mapValuesForGUI);
                }
                if (!matrixValuesForGUI.isEmpty()) {
                    cfv.setMatrixValuesForGUI(matrixValuesForGUI);
                }
            } catch (RuntimeJsonMappingException e1) {
                messages.error(new BundleKey("messages", "message.upload.fail.invalidFormat"), e1.getMessage());
                return;
            } catch (IOException e) {
                messages.error(new BundleKey("messages", "message.upload.fail"), e.getMessage());
                return;
            }
            messages.info(new BundleKey("messages", "customFieldTemplate.importFile.importedLines"), importedLines);
        } else {
            messages.warn(new BundleKey("messages", "customFieldTemplate.importFile.fileRequired"));
        }

    }

    public LazyDataModel getValueDataset(CustomFieldValue cfv, CustomFieldStorageTypeEnum storageType) {
        return getValueDataset(cfv, storageType, null, false);
    }

    public LazyDataModel getValueDataset(CustomFieldValue cfv, CustomFieldStorageTypeEnum storageType, Map<String, Object> inputFilters, boolean forceReload) {
        if (cfv == null) {
            return null;
        }

        if (cfv.getDatasetForGUI() == null || forceReload) {

            LazyDataModel dataset = new LazyDataModelWSize() {

                private static final long serialVersionUID = -5796910936316457322L;

                @Override
                public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                    List valueList = storageType == CustomFieldStorageTypeEnum.MATRIX ? cfv.getMatrixValuesForGUI() : cfv.getMapValuesForGUI();
                    setRowCount(valueList.size());

                    if (getRowCount() > 0) {
                        int toNr = first + pageSize;
                        return new ArrayList(valueList.subList(first, getRowCount() <= toNr ? getRowCount() : toNr));

                    } else {
                        return new ArrayList();
                    }
                }
            };
            cfv.setDatasetForGUI(dataset);
        }
        return (LazyDataModel) cfv.getDatasetForGUI();
    }


    /**
     * Gets the selectedItem
     *
     * @return the selectedItem
     */
    public Map<String, Object> getSelectedItem() {
        return selectedItem;
    }

    /**
     * Sets the selectedItem.
     *
     * @param selectedItem the new selectedItem
     */
    public void setSelectedItem(Map<String, Object> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Remove value from a map of values.
     *
     * @param cfv Map value holder
     * @param storageType storage ype.
     * @param mapValues map of values
     */
    public void removeValue(CustomFieldValue cfv, CustomFieldStorageTypeEnum storageType, Map<String, Object> mapValues) {
        List valueList = storageType == CustomFieldStorageTypeEnum.MATRIX ? cfv.getMatrixValuesForGUI() : cfv.getMapValuesForGUI();
        valueList.remove(mapValues);
        cfv.setDatasetForGUI(null);
    }

    public LazyDataModel<Map<String, Object>> getCustomTableWrapperValues(ICustomFieldEntity entity, CustomFieldTemplate cft) {
        if (cft == null) {
            return null;
        }
        setCustomTableName(entity, cft);
        PagingAndFiltering pagingAndFiltering = getPagingAndFiltering(entity, cft);
        if (customTableBasedDataModel == null && customTableName != null) {

            customTableBasedDataModel = new NativeTableBasedDataModel() {

                @Override
                protected Map<String, Object> getSearchCriteria() {
                    return pagingAndFiltering.getFilters();
                }

                @Override
                protected List<String> getListFieldsToFetchImpl() {
                    return (pagingAndFiltering.getFields() != null) ? Arrays.asList(pagingAndFiltering.getFields().split(",")) : null;

                }

                @Override
                protected CustomTableService getPersistenceServiceImpl() {
                    return customTableService;
                }

                @Override
                protected String getTableName() {
                    return customTableName;
                }
            };
        }

        return customTableBasedDataModel;
    }

    private PagingAndFiltering getPagingAndFiltering(ICustomFieldEntity entity, CustomFieldTemplate cft) {
        String filterString = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getDataFilterEL(), "entity", entity);
        String fieldsString = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getFieldsEL(), "entity", entity);
        if (!StringUtils.isBlank(fieldsString) && !org.apache.commons.lang3.StringUtils.contains(fieldsString, "id")) {
            fieldsString = "id," + fieldsString;
        }
        if (StringUtils.isBlank(filterString)) {
            return new PagingAndFiltering();
        }
        String jsonFilter = "{\"filters\": {" + filterString + "},\"fields\":\"" + fieldsString + "\"}";
        PagingAndFiltering pagingAndFiltering = JsonUtils.toObject(jsonFilter, PagingAndFiltering.class);
        return pagingAndFiltering;
    }

    public List<CustomFieldTemplate> getFields(ICustomFieldEntity entity, CustomFieldTemplate cft) {

        String customTableCode = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getCustomTableCodeEL(), "entity", entity);
        String fieldsString = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getFieldsEL(), "entity", entity);
        List<String> includedFields = new ArrayList<>();
        if (!StringUtils.isBlank(fieldsString)) {
            fieldsString = fieldsString.replaceAll("[\\s\"]+", "");
            includedFields.addAll(Arrays.asList(fieldsString.split(",")));
        }

        CustomEntityTemplate cet = customTableService.getCET(customTableCode);
        if (cet == null) {
            throw new BusinessException("Custom Entity Template not found for the Custom Table:" + customTableCode);
        }

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts != null) {
            fields = new ArrayList<>(cfts.values());
            //Exclude fields not included in fieldsEl
            if (!includedFields.isEmpty()) {
                List<CustomFieldTemplate> toBeRemoved = new ArrayList<>();
                for (CustomFieldTemplate cf : fields) {
                    if (!includedFields.contains(cf.getCode())) {
                        toBeRemoved.add(cf);
                    }
                }
                fields.removeAll(toBeRemoved);
            }
            Collections.sort(fields, new Comparator<CustomFieldTemplate>() {

                @Override
                public int compare(CustomFieldTemplate cft1, CustomFieldTemplate cft2) {
                    int pos1 = cft1.getGUIFieldPosition();
                    int pos2 = cft2.getGUIFieldPosition();

                    return pos1 - pos2;
                }
            });
        }
        return fields;
    }

    @ActionMethod
    public void delete(Long id, ICustomFieldEntity entity, CustomFieldTemplate cft) throws BusinessException {
        String customTableCode = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getCustomTableCodeEL(), "entity", entity);
        CustomEntityTemplate cet = customTableService.getCET(customTableCode);
        if (cet == null) {
            throw new BusinessException("Custom Entity Template not found for the Custom Table:" + customTableCode);
        }
        customTableService.remove(cet.getDbTablename(), id);
        customTableBasedDataModel = null;
        messages.info(new BundleKey("messages", "delete.successful"));

    }

    public List<Map<String, Object>> getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(List<Map<String, Object>> selectedValues) {
        this.selectedValues = selectedValues;
    }

    public Map<String, Object> getNewValues() {
        return newValues;
    }

    public void setNewValues(Map<String, Object> newValues) {
        this.newValues = newValues;
    }

    @ActionMethod
    public void onCellEdit(CellEditEvent event) throws BusinessException {

        DataTable o = (DataTable) event.getSource();
        Map<String, Object> mapValue = (Map<String, Object>) o.getRowData();
        customTableService.update(customTableName, mapValue);
        messages.info(new BundleKey("messages", "customTable.valuesSaved"));
    }

    private String setCustomTableName(ICustomFieldEntity entity, CustomFieldTemplate cft) {

        String customTableCode = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getCustomTableCodeEL(), "entity", entity);
            CustomEntityTemplate cet = customTableService.getCET(customTableCode);
            if (cet == null) {
                throw new BusinessException("Custom Entity Template not found for the Custom Table:" + customTableCode);
            }
            customTableName = cet.getDbTablename();
        return customTableName;
    }

    /**
     * Add new values to a map of values, setting a default value if applicable
     *
     * @throws BusinessException General exception
     */
    @ActionMethod
    public void addNewValues(BusinessEntity entity) throws BusinessException {

        Map<String, Object> convertedValues = customTableService.convertValue(newValues, fields, false, null);
        if (convertedValues != null && convertedValues.containsValue(entity.getCode())) {
            customTableService.create(customTableName, convertedValues);
            messages.info(new BundleKey("messages", "customTable.valuesSaved"));
            newValues = new HashMap<>();
            customTableBasedDataModel = null;
        } else {
            messages.error(new BundleKey("messages", "customTable.invalidValues"));
        }
    }

    @ActionMethod
    public void deleteMany() throws BusinessException {

        if (selectedValues == null || selectedValues.isEmpty()) {
            messages.warn(new BundleKey("messages", "delete.entitities.noSelection"));
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
    
    /**
	 * Check if field is still encrypted
	 * @param field
	 * @return boolean 
	 */
    public Boolean isEncrypted(ICustomFieldEntity entity, CustomFieldTemplate cft) {
    	
    	if(entity != null && entity.getCfValues() != null && entity.getCfValues().getValuesByCode() != null && entity.getCfValues().getValuesByCode().get("AES") != null) {
    		return true;
    	}
    	return false;
    }

}