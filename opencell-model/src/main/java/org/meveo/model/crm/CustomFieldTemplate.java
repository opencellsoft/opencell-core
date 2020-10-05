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

package org.meveo.model.crm;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn.CustomFieldColumnUseEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.shared.DateUtils;

/**
 * Custom field template
 *
 * @author Andrius Karpavicius
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 10.0
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code", "appliesTo" })
@Table(name = "crm_custom_field_tmpl", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "applies_to" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "crm_custom_fld_tmp_seq"), })
@NamedQueries({ @NamedQuery(name = "CustomFieldTemplate.getCFTForCache", query = "SELECT cft from CustomFieldTemplate cft left join fetch cft.calendar where cft.disabled=false order by cft.appliesTo"),
        @NamedQuery(name = "CustomFieldTemplate.getCFTForIndex", query = "SELECT cft from CustomFieldTemplate cft where cft.disabled=false and cft.indexType is not null "),
        @NamedQuery(name = "CustomFieldTemplate.getCFTByCodeAndAppliesTo", query = "SELECT cft from CustomFieldTemplate cft where cft.code=:code and cft.appliesTo=:appliesTo", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "CustomFieldTemplate.getCFTByAppliesTo", query = "SELECT cft from CustomFieldTemplate cft where cft.appliesTo=:appliesTo order by cft.code", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "CustomFieldTemplate.getReferencedCFTByEntity", query = "SELECT cft from CustomFieldTemplate cft where cft.fieldType='ENTITY' and upper(cft.entityClazz)=:referencedEntity order by cft.code", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "CustomFieldTemplate.getCFTsForAccumulation", query = "SELECT cft from CustomFieldTemplate cft where cft.appliesTo='Seller' or cft.code in (SELECT cftu.code from CustomFieldTemplate cftu where cftu.appliesTo in :appliesTo group by cftu.code having count(cftu.code)>1) order by cft.code"),
        @NamedQuery(name = "CustomFieldTemplate.getUniqueFromTable", query = "SELECT cft from CustomFieldTemplate cft where cft.uniqueConstraint = true and lower(cft.appliesTo) = :appliesTo") })
public class CustomFieldTemplate extends EnableBusinessEntity implements Comparable<CustomFieldTemplate> {

    private static final long serialVersionUID = -1403961759495272885L;

    public static final long DEFAULT_MAX_LENGTH_STRING = 50L;

    public static final String ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR = " - ";
    private static final String CUSTOM_TABLE_STRUCTURE_REGEX = "org.meveo.model.customEntities.CustomEntityTemplate - [a-zA-Z\\S]{1,}$";

    public enum GroupedCustomFieldTreeItemType {

        root(null), tab("tab"), fieldGroup("fieldGroup"), field("field"), action("action");

        private String positionTag;

        public String getPositionTag() {
            return positionTag;
        }

        private GroupedCustomFieldTreeItemType(String tag) {
            this.positionTag = tag;
        }
    }

    /**
     * Field data type
     */
    @Column(name = "field_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CustomFieldTypeEnum fieldType;

    /**
     * Entity type that field applies to
     */
    @Column(name = "applies_to", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String appliesTo;

    /**
     * Is value mandatory
     */
    @Type(type = "numeric_boolean")
    @Column(name = "value_required")
    private boolean valueRequired;
    /**
     * Is value part of unique constraint
     */
    @Type(type = "numeric_boolean")
    @Column(name = "unique_constraint")
    private boolean uniqueConstraint;

    /**
     * Values for selection from a picklist
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crm_custom_field_tmpl_val")
    private Map<String, String> listValues;

    /**
     * Matrix columns. Contains both key and value columns.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderBy("columnUse ASC, position ASC")
    @CollectionTable(name = "crm_custom_field_tmpl_mcols", joinColumns = { @JoinColumn(name = "cft_id") })
    @AttributeOverrides(value = { @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 20)),
            @AttributeOverride(name = "label", column = @Column(name = "label", nullable = false, length = 50)), @AttributeOverride(name = "keyType", column = @Column(name = "key_type", nullable = false, length = 10)),
            @AttributeOverride(name = "position", column = @Column(name = "position", nullable = false)), @AttributeOverride(name = "columnUse", column = @Column(name = "column_use", nullable = false)) })
    private List<CustomFieldMatrixColumn> matrixColumns = new ArrayList<CustomFieldMatrixColumn>();

    /**
     * Matrix key type columns
     */
    @Transient
    private List<CustomFieldMatrixColumn> matrixKeyColumns;

    /**
     * Matrix value type columns
     */
    @Transient
    private List<CustomFieldMatrixColumn> matrixValueColumns;

    /**
     * Is field value versionable
     */
    @Type(type = "numeric_boolean")
    @Column(name = "versionable")
    private boolean versionable;

    /**
     * Calendar to calculate period date range
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    /**
     * Default value
     */
    @Column(name = "default_value", length = 250)
    @Size(max = 250)
    private String defaultValue;

    /**
     * Use parent's entities field value as a default value
     */
    @Type(type = "numeric_boolean")
    @Column(name = "inh_as_def_value")
    private boolean useInheritedAsDefaultValue;

    /**
     * Should value not be updated once entered
     */
    @Type(type = "numeric_boolean")
    @Column(name = "cf_protectable")
    protected boolean protectable;

    /**
     * Reference to an entity. A classname. In case of CustomEntityTemplate, classname consist of "CustomEntityTemplate - &lt;CustomEntityTemplate code&gt;"
     */
    @Column(name = "entity_clazz", length = 255, updatable = false)
    @Size(max = 255)
    private String entityClazz;

    /**
     * Field data grouping type
     */
    @Column(name = "storage_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CustomFieldStorageTypeEnum storageType = CustomFieldStorageTypeEnum.SINGLE;

    /**
     * Key data type for Map type field
     */
    @Column(name = "mapkey_type")
    @Enumerated(EnumType.STRING)
    private CustomFieldMapKeyEnum mapKeyType;

    /**
     * Should event be fired when end period is reached for versioned values
     */
    @Type(type = "numeric_boolean")
    @Column(name = "trigger_end_period_event", nullable = false)
    private boolean triggerEndPeriodEvent;

    /**
     * Where field should be displayed. Format: tab:&lt;tab name&gt;:&lt;tab relative position&gt;;fieldGroup:&lt;fieldgroup name&gt;:&lt;fieldgroup relative
     * position&gt;;field:&lt;field relative position in fieldgroup/tab&gt;
     *
     * Tab and field group names support translation in the following format: &lt;default value&gt;|&lt;language3 letter key=translated value&gt;
     *
     * e.g. tab:Tab default title|FRA=Title in french|ENG=Title in english:0;fieldGroup:Field group default label|FRA=Field group label in french|ENG=Field group label in
     * english:0;field:0 OR tab:Second tab:1;field:1
     */
    @Column(name = "gui_position", length = 2000)
    @Size(max = 2000)
    private String guiPosition;

    /**
     * Allow to edit value
     */
    @Type(type = "numeric_boolean")
    @Column(name = "allow_edit")
    @NotNull
    private boolean allowEdit = true;

    /**
     * Allow to edit value
     */
    @Type(type = "numeric_boolean")
    @Column(name = "anonymize_gdpr")
    @NotNull
    private boolean anonymizeGdpr = false;

    /**
     * If true, field wont be visible/appicable on new entity entry
     */
    @Type(type = "numeric_boolean")
    @Column(name = "hide_on_new")
    @NotNull
    private boolean hideOnNew;

    /**
     * Validation - maximum value
     */
    @Column(name = "max_value")
    private Long maxValue;

    /**
     * Validation - minimum value
     */
    @Column(name = "min_value")
    private Long minValue;

    /**
     * Validation - regular expression
     */
    @Column(name = "reg_exp", length = 80)
    @Size(max = 80)
    private String regExp;

    /**
     * Expression to determine if field is applies
     */
    @Column(name = "applicable_on_el", length = 2000)
    @Size(max = 2000)
    private String applicableOnEl;

    /**
     * Child entity fields to display as summary. Field names are separated by a comma.
     */
    @Column(name = "che_fields", length = 500)
    @Size(max = 500)
    private String childEntityFields;

    /**
     * If and how custom field value should be indexed in Elastic Search
     */
    @Column(name = "index_type", length = 10)
    @Enumerated(EnumType.STRING)
    private CustomFieldIndexTypeEnum indexType;

    /**
     * Tags assigned to custom field template
     */
    @Column(name = "tags", length = 2000)
    @Size(max = 2000)
    private String tags;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    /**
     * Value display format - pattern
     */
    @Column(name = "display_format", length = 80)
    @Size(max = 80)
    private String displayFormat;
    /**
     * Number of digits in decimal part, if the fieldType is double.
     */
    @Column(name = "nb_decimal")
    private Integer nbDecimal;

    /**
     * Rounding mode, Possible values {@link RoundingModeEnum}.
     */
    @Column(name = "rounding_mode", length = 50)
    @Enumerated(EnumType.STRING)
    private RoundingModeEnum roundingMode;

    /**
     * Should field be not manageable in GUI, irrelevant of any other settings
     */
    @Transient
    private boolean hideInGUI;

    /**
     * Database field name - derived from code
     */
    @Transient
    private String dbFieldname;

    /**
     * An EL expression that should resolve into the code of a valid CustomTable.
     */
    @Column(name = "custom_table_code_el", length = 2000)
    @Size(max = 2000)
    private String customTableCodeEL;

    /**
     * An EL expression that should resolve into a list of filters as defined by the Search API.
     */
    @Column(name = "data_filter_el", length = 2000)
    @Size(max = 2000)
    private String dataFilterEL;

    /**
     * Should resolve into a list of CT fields to be shown for CTW (displayed in GUI, returned by API). It's the "fields" parameter in the CT list API.
     */
    @Column(name = "fields_el", length = 2000)
    @Size(max = 2000)
    private String fieldsEL;

    public CustomFieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(CustomFieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public boolean isValueRequired() {
        return valueRequired;
    }

    public void setValueRequired(boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    public Map<String, String> getListValues() {
        return listValues;
    }

    /**
     * create a Map of attribute from sorted List
     *
     * @return a sorted LinkedHashMap values
     */
    public Map<String, String> getListValuesSorted() {
        if (listValues != null && !listValues.isEmpty()) {
            Comparator<String> dropdownListComparator = new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    try {
                        return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
                    } catch (NumberFormatException e) {
                        return s1.compareTo(s2);
                    }
                }
            };

            Map<String, String> newList = new TreeMap<>(dropdownListComparator);
            newList.putAll(listValues);
            return newList;
        }

        return listValues;
    }

    public void setListValues(Map<String, String> listValues) {
        this.listValues = listValues;
    }

    public List<CustomFieldMatrixColumn> getMatrixColumns() {
        return matrixColumns;
    }

    public void setMatrixColumns(List<CustomFieldMatrixColumn> matrixColumns) {
        this.matrixColumns = matrixColumns;
    }

    public void addMatrixKeyColumn() {
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setColumnUse(CustomFieldColumnUseEnum.USE_KEY);
        column.setPosition(getMatrixKeyColumns().size() + 1);
        this.matrixColumns.add(column);
        matrixKeyColumns = null;
    }

    public void addMatrixValueColumn() {
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        column.setPosition(getMatrixValueColumns().size() + 1);
        this.matrixColumns.add(column);
        matrixValueColumns = null;
    }

    public void removeMatrixColumn(CustomFieldMatrixColumn columnToRemove) {
        this.matrixColumns.remove(columnToRemove);
        matrixKeyColumns = null;
        matrixValueColumns = null;

        // Reorder position
        int i = 0;
        for (CustomFieldMatrixColumn column : getMatrixKeyColumns()) {
            i++;
            column.setPosition(i);
        }
        i = 0;
        for (CustomFieldMatrixColumn column : getMatrixValueColumns()) {
            i++;
            column.setPosition(i);
        }
    }

    /**
     * Find a corresponding matrix column by its index (position). Note: result might differ if matrix column was added and value was not updated
     *
     * @param index Index to return the column for
     * @return Matched matrix column
     */
    public CustomFieldMatrixColumn getMatrixColumnByIndex(int index) {
        if (index >= matrixColumns.size()) {
            return null;
        }
        return matrixColumns.get(index);
    }

    /**
     * Extract codes of matrix columns into a sorted list by column index.
     *
     * @return A list of matrix column codes
     */
    public List<String> getMatrixColumnCodes() {

        List<String> matrixColumnNames = null;
        if (storageType == CustomFieldStorageTypeEnum.MATRIX) {
            matrixColumnNames = new ArrayList<>();
            for (CustomFieldMatrixColumn column : matrixColumns) {
                matrixColumnNames.add(column.getCode());
            }
        }
        return matrixColumnNames;
    }

    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    public boolean isVersionable() {
        return versionable;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isUseInheritedAsDefaultValue() {
        return useInheritedAsDefaultValue;
    }

    public void setUseInheritedAsDefaultValue(boolean useInheritedAsDefaultValue) {
        this.useInheritedAsDefaultValue = useInheritedAsDefaultValue;
    }

    public String getEntityClazz() {
        return entityClazz;
    }

    public void setEntityClazz(String entityClazz) {
        this.entityClazz = entityClazz;
    }

    public String getEntityClazzCetCode() {
        return CustomFieldTemplate.retrieveCetCode(entityClazz);
    }

    /**
     * Retrieve a cet code from classname and code as it is stored in entityClazz field.
     *
     * @param entityClazz entity class
     * @return code
     */
    public static String retrieveCetCode(String entityClazz) {
        if (entityClazz == null) {
            return null;
        }
        if (entityClazz.startsWith(CustomEntityTemplate.class.getName())) {
            String cetCode = entityClazz.substring(entityClazz.indexOf(ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR) + ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR.length());
            return cetCode;
        }
        return null;
    }

    public Object getDefaultValueConverted() {
        if (defaultValue == null) {
            return null;
        }
        try {
            if (fieldType == CustomFieldTypeEnum.DOUBLE) {
                return Double.parseDouble(defaultValue);
            } else if (fieldType == CustomFieldTypeEnum.LONG) {
                return Long.parseLong(defaultValue);
            } else if (fieldType == CustomFieldTypeEnum.STRING || fieldType == CustomFieldTypeEnum.LIST || fieldType == CustomFieldTypeEnum.TEXT_AREA) {
                return defaultValue;
            } else if (fieldType == CustomFieldTypeEnum.DATE) {
                return DateUtils.parseDateWithPattern(defaultValue, DateUtils.DATE_TIME_PATTERN);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public CustomFieldStorageTypeEnum getStorageType() {
        return storageType;
    }

    public void setStorageType(CustomFieldStorageTypeEnum storageType) {
        this.storageType = storageType;
    }

    public CustomFieldMapKeyEnum getMapKeyType() {
        return mapKeyType;
    }

    public void setMapKeyType(CustomFieldMapKeyEnum mapKeyType) {
        this.mapKeyType = mapKeyType;
    }

    public boolean isTriggerEndPeriodEvent() {
        return triggerEndPeriodEvent;
    }

    public void setTriggerEndPeriodEvent(boolean triggerEndPeriodEvent) {
        this.triggerEndPeriodEvent = triggerEndPeriodEvent;
    }

    // public Integer getCacheValueTimeperiod() {
    // return cacheValueTimeperiod;
    // }
    //
    // public void setCacheValueTimeperiod(Integer cacheValueTimeperiod) {
    // this.cacheValueTimeperiod = cacheValueTimeperiod;
    // }

    public String getGuiPosition() {
        return guiPosition;
    }

    public void setGuiPosition(String guiPosition) {
        this.guiPosition = guiPosition;
    }

    /**
     * Parse GUIPosition field value e.g. 'tab:Configuration:0;fieldGroup:Price:5;field:0' and return 'tab', 'fieldGroup' and 'field' item values as a map
     *
     * @return A map with 'tab_pos', 'tab_name', 'fieldGroup_pos', 'fieldGroup_name' and 'field_pos' as keys
     */
    public Map<String, String> getGuiPositionParsed() {

        if (guiPosition == null) {
            return null;
        }

        Map<String, String> parsedInfo = new HashMap<String, String>();

        String[] positions = guiPosition.split(";");

        for (String position : positions) {
            String[] positionDetails = position.split(":");
            if (!positionDetails[0].equals(GroupedCustomFieldTreeItemType.field.positionTag)) {
                parsedInfo.put(positionDetails[0] + "_name", positionDetails[1]);
                if (positionDetails.length == 3) {
                    parsedInfo.put(positionDetails[0] + "_pos", positionDetails[2]);
                }
            } else if (positionDetails[0].equals(GroupedCustomFieldTreeItemType.field.positionTag) && positionDetails.length == 2) {
                parsedInfo.put(positionDetails[0] + "_pos", positionDetails[1]);
            }
        }

        return parsedInfo;
    }

    /**
     * Get GUI 'field' position value in a GUIPosition value as in e.g. "tab:Configuration:0;fieldGroup:Purge counter periods:1;field:0"
     *
     * @return GUI 'field' position value
     */
    public int getGUIFieldPosition() {
        if (guiPosition != null) {
            String position = getGuiPositionParsed().get(GroupedCustomFieldTreeItemType.field.positionTag + "_pos");
            if (position != null) {
                try {
                    return Integer.parseInt(position);
                } catch (NumberFormatException e) {
                }
            }
        }
        return 0;
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    public boolean isAnonymizeGdpr() {
        return anonymizeGdpr;
    }

    public void setAnonymizeGdpr(boolean anonymizeGdpr) {
        this.anonymizeGdpr = anonymizeGdpr;
    }

    public boolean isHideOnNew() {
        return hideOnNew;
    }

    public void setHideOnNew(boolean hideOnNew) {
        this.hideOnNew = hideOnNew;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public Long getMaxValueOrDefault(Long defaultValue) {
        return Optional.ofNullable(maxValue).orElse(defaultValue);
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public String getRegExp() {
        return regExp;
    }

    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    // public boolean isCacheValue() {
    // return cacheValue;
    // }
    //
    // public void setCacheValue(boolean cacheValue) {
    // this.cacheValue = cacheValue;
    // }

    public String getApplicableOnEl() {
        return applicableOnEl;
    }

    public void setApplicableOnEl(String applicableOnEl) {
        this.applicableOnEl = applicableOnEl;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomFieldTemplate)) {
            return false;
        }

        CustomFieldTemplate other = (CustomFieldTemplate) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (code == null && other.getCode() != null) {
            return false;
        } else if (code != null && !code.equals(other.getCode())) {
            return false;
        } else if (appliesTo == null && other.getAppliesTo() != null) {
            return false;
        } else if (appliesTo != null && !appliesTo.equals(other.getAppliesTo())) {
            return false;
        }
        return true;
    }

    public String getChildEntityFields() {
        return childEntityFields;
    }

    public String[] getChildEntityFieldsAsList() {
        if (childEntityFields != null) {
            return childEntityFields.split("\\|");
        }
        return new String[0];
    }

    public void setChildEntityFields(String childEntityFields) {
        this.childEntityFields = childEntityFields;
    }

    public void setChildEntityFieldsAsList(List<String> cheFields) {
        this.childEntityFields = StringUtils.concatenate("|", cheFields);
    }

    public CustomFieldIndexTypeEnum getIndexType() {
        return indexType;
    }

    public void setIndexType(CustomFieldIndexTypeEnum indexType) {
        this.indexType = indexType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return String.format("CustomFieldTemplate [id=%s, appliesTo=%s, code=%s]", id, appliesTo, code);
    }

    @Override
    public int compareTo(CustomFieldTemplate o) {
        return o.getCode().compareTo(getCode());
    }

    /**
     * Instantiate a CustomFieldValue from a template, setting a default value if applicable.
     *
     * @return CustomFieldValue object
     */
    public CustomFieldValue toDefaultCFValue() {
        CustomFieldValue cfValue = new CustomFieldValue();

        // Set a default value
        if (getStorageType() == CustomFieldStorageTypeEnum.SINGLE) {
            cfValue.setValue(getDefaultValueConverted());
        }

        return cfValue;
    }

    /**
     * Get a date period for a given date. Applies only to CFT versionable by a calendar.
     *
     * @param date Date
     * @return Date period matching calendar's dates
     */
    public DatePeriod getDatePeriod(Date date) {
        if (isVersionable() && getCalendar() != null && date != null) {
            return new DatePeriod(getCalendar().previousCalendarDate(date), getCalendar().nextCalendarDate(date));
        }
        return null;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    /**
     * Instantiate descriptionI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
     *
     * @return descriptionI18n value or instantiated descriptionI18n field value
     */
    public Map<String, String> getDescriptionI18nNullSafe() {
        if (descriptionI18n == null) {
            descriptionI18n = new HashMap<>();
        }
        return descriptionI18n;
    }

    /**
     * Get description in a given language. Will return default description if not found for the language
     *
     * @param language language code
     * @return descriptionI18n value or instantiated descriptionI18n field value
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public String getDescription(String language) {

        if (language == null || descriptionI18n == null || descriptionI18n.isEmpty()) {
            return description;
        }

        language = language.toUpperCase();
        if (StringUtils.isBlank(descriptionI18n.get(language))) {
            return description;
        } else {
            return descriptionI18n.get(language);
        }
    }

    /**
     * Get a list of matrix columns used as key columns
     *
     * @return A list of matrix columns where isKeyColumn = true
     */
    public List<CustomFieldMatrixColumn> getMatrixKeyColumns() {

        if (matrixKeyColumns != null) {
            return matrixKeyColumns;
        }
        matrixKeyColumns = matrixColumns.stream().filter(elem -> elem.isColumnForKey()).collect(Collectors.toList());
        return matrixKeyColumns;
    }

    /**
     * Extract codes of matrix columns used as key columns into a sorted list by column index
     *
     * @return A list of matrix column codes
     */
    public List<String> getMatrixKeyColumnCodes() {

        List<String> matrixColumnNames = null;
        if (storageType == CustomFieldStorageTypeEnum.MATRIX) {
            matrixColumnNames = new ArrayList<>();
            for (CustomFieldMatrixColumn column : getMatrixKeyColumns()) {
                matrixColumnNames.add(column.getCode());
            }
        }
        return matrixColumnNames;
    }

    /**
     * Get a list of matrix columns used as value columns
     *
     * @return A list of matrix columns where isKeyColumn = false
     */
    public List<CustomFieldMatrixColumn> getMatrixValueColumns() {

        if (matrixValueColumns != null) {
            return matrixValueColumns;
        }

        matrixValueColumns = matrixColumns.stream().filter(elem -> !elem.isColumnForKey()).collect(Collectors.toList());

        return matrixValueColumns;
    }

    /**
     * Extract codes of matrix columns used as value columns into a sorted list by column index
     *
     * @return A list of matrix column codes
     */
    public List<String> getMatrixValueColumnCodes() {

        List<String> matrixColumnNames = null;
        if (storageType == CustomFieldStorageTypeEnum.MATRIX) {
            matrixColumnNames = new ArrayList<>();
            for (CustomFieldMatrixColumn column : getMatrixValueColumns()) {
                matrixColumnNames.add(column.getCode());
            }
        }
        return matrixColumnNames;
    }

    /**
     * Parse multi-value value from string to a map of values
     *
     * @param multiValue Multi-value value as string
     * @param appendToMap Map to append values to. If not provided a new map will be instantiated.
     * @return Map of values (or same as appendToMap if provided)
     */
    public Map<String, Object> deserializeMultiValue(String multiValue, Map<String, Object> appendToMap) {

        // DO NOT REMOVE - Initialize matrixValueColumns field
        getMatrixValueColumns();

        Map<String, Object> values = appendToMap;
        if (values == null) {
            values = new HashMap<>();
        }

        // Multi-value values are concatenated when stored - split them and set as separate map key/values.
        // Unescape a | character inside a string value that was saved as html &#124 value
        String[] splitValues = multiValue.split("\\" + CustomFieldValue.MATRIX_KEY_SEPARATOR);
        for (int i = 0; i < splitValues.length && i < matrixValueColumns.size(); i++) {
            CustomFieldMapKeyEnum dataType = matrixValueColumns.get(i).getKeyType();
            if (!StringUtils.isBlank(splitValues[i])) {
                if (dataType == CustomFieldMapKeyEnum.STRING) {
                    String splitValue = splitValues[i];
                    if (!StringUtils.isBlank(splitValue)) {
                        splitValue = splitValue.replaceAll(Pattern.quote("&#124;"), "|");
                    }
                    values.put(matrixValueColumns.get(i).getCode(), splitValue);

                } else {
                    try {
                        if (dataType == CustomFieldMapKeyEnum.LONG) {
                            values.put(matrixValueColumns.get(i).getCode(), Long.parseLong(splitValues[i]));
                        } else if (dataType == CustomFieldMapKeyEnum.DOUBLE) {
                            values.put(matrixValueColumns.get(i).getCode(), Double.parseDouble(splitValues[i]));
                        }
                    } catch (Exception e) {
                        // Was not a number - ignore
                    }
                }
            }
        }

        return values;
    }

    /**
     * Serialize multi-value from a map of values to a string
     *
     * @param mapValues Map of values
     * @return A string with concatenated values
     */
    public String serializeMultiValue(Map<String, Object> mapValues) {

        // DO NOT REMOVE - Initialize matrixValueColumns field
        getMatrixValueColumns();

        boolean valueSet = false;
        StringBuilder valBuilder = new StringBuilder();
        for (CustomFieldMatrixColumn column : matrixValueColumns) {
            valBuilder.append(matrixValueColumns.indexOf(column) == 0 ? "" : CustomFieldValue.MATRIX_KEY_SEPARATOR);
            Object columnValue = mapValues.get(column.getCode());
            if (StringUtils.isBlank(columnValue)) {
                continue;
            }
            valueSet = true;
            CustomFieldMapKeyEnum dataType = column.getKeyType();
            if (dataType == CustomFieldMapKeyEnum.STRING) {

                if (!StringUtils.isBlank((String) columnValue)) {
                    columnValue = ((String) columnValue).replaceAll(Pattern.quote("|"), "&#124;");
                }
                valBuilder.append((String) columnValue);
            } else if (dataType == CustomFieldMapKeyEnum.LONG || dataType == CustomFieldMapKeyEnum.DOUBLE) {
                // Case of String value
                if (dataType == CustomFieldMapKeyEnum.LONG) {
                    columnValue = Long.valueOf(columnValue.toString());
                } else if (dataType == CustomFieldMapKeyEnum.DOUBLE) {
                    columnValue = Double.valueOf(columnValue.toString());
                }
                DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                df.setMaximumFractionDigits(340);
                valBuilder.append(df.format(columnValue));
            }
        }

        if (!valueSet) {
            return null;
        }

        return valBuilder.toString();
    }

    /**
     * @return the protectable
     */
    public boolean isProtectable() {
        return protectable;
    }

    /**
     * @param protectable the protectable to set
     */
    public void setProtectable(boolean protectable) {
        this.protectable = protectable;
    }

    /**
     * @return the displayFormat
     */
    public String getDisplayFormat() {
        return displayFormat;
    }

    /**
     * @param displayFormat the displayFormat to set
     */
    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }

    /**
     * @return Should field be not manageable in GUI, irrelevant of any other settings
     */
    public boolean isHideInGUI() {
        return hideInGUI;
    }

    /**
     * @param hideInGUI Should field be not manageable in GUI, irrelevant of any other settings
     */
    public void setHideInGUI(boolean hideInGUI) {
        this.hideInGUI = hideInGUI;
    }

    public boolean isUniqueConstraint() {
        return uniqueConstraint;
    }

    public void setUniqueConstraint(boolean uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
    }

    /**
     * Get a database field name derived from a code value. Lowercase and spaces replaced by "_".
     *
     * @return Database field name
     */
    public String getDbFieldname() {
        if (dbFieldname == null && code != null) {
            dbFieldname = CustomFieldTemplate.getDbFieldname(code);
        }
        return dbFieldname;
    }

    /**
     * Get a database field name derived from a code value. Lowercase and spaces replaced by "_".
     *
     * @param code Field code
     * @return Database field name
     */
    public static String getDbFieldname(String code) {
        return BaseEntity.cleanUpAndLowercaseCodeOrId(code);
    }

    /**
     * Gets the number of digits in decimal part.
     * 
     * @return number of digits.
     */
    public Integer getNbDecimal() {
        return nbDecimal;
    }

    /**
     * Sets number of digits in decimal part.
     * 
     * @param nbDecimal the number of digits.
     */
    public void setNbDecimal(Integer nbDecimal) {
        this.nbDecimal = nbDecimal;
    }

    /**
     * Gets the rounding mode.
     * 
     * @return the rounding mode.
     */
    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    /**
     * Sets the rounding mode.
     * 
     * @param roundingMode rounding mode.
     */
    public void setRoundingMode(RoundingModeEnum roundingMode) {
        this.roundingMode = roundingMode;
    }

    public String tableName() {
        return Optional.ofNullable(this.entityClazz).filter(entityClazz -> entityClazz.matches(CUSTOM_TABLE_STRUCTURE_REGEX)).map(tableName -> tableName.split(ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR)[1])
            .orElse(null);
    }

    /**
     * Gets the customTableCodeEL expression.
     *
     * @return the customTableCodeEL expression.
     */
    public String getCustomTableCodeEL() {
        return customTableCodeEL;
    }

    /**
     * Sets the customTableCodeEL expression.
     *
     * @param customTableCodeEL the customTableCodeEL expression.
     */
    public void setCustomTableCodeEL(String customTableCodeEL) {
        this.customTableCodeEL = customTableCodeEL;
    }

    /**
     * Gets the dataFilterEL expression.
     *
     * @return the dataFilterEL expression.
     */
    public String getDataFilterEL() {
        return dataFilterEL;
    }

    /**
     * Sets the dataFilterEL expression.
     *
     * @param dataFilterEL the dataFilterEL expression.
     */
    public void setDataFilterEL(String dataFilterEL) {
        this.dataFilterEL = dataFilterEL;
    }

    /**
     * Gets the fieldsEL expression.
     *
     * @return the fieldsEL expression.
     */
    public String getFieldsEL() {
        return fieldsEL;
    }

    /**
     * Sets the fieldsEL expression.
     *
     * @param fieldsEL the fieldsEL expression.
     */
    public void setFieldsEL(String fieldsEL) {
        this.fieldsEL = fieldsEL;
    }

}