package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.shared.DateUtils;

@Entity
@ModuleItem
@ExportIdentifier({ "code", "appliesTo" })
@Table(name = "crm_custom_field_tmpl", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "applies_to" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_custom_fld_tmp_seq"), })
@NamedQueries({
        @NamedQuery(name = "CustomFieldTemplate.getCFTForCache", query = "SELECT cft from CustomFieldTemplate cft left join fetch cft.calendar where cft.disabled=false order by cft.appliesTo"),
        @NamedQuery(name = "CustomFieldTemplate.getCFTForIndex", query = "SELECT cft from CustomFieldTemplate cft where cft.disabled=false and cft.indexType is not null ") })
public class CustomFieldTemplate extends BusinessEntity implements Comparable<CustomFieldTemplate> {

    private static final long serialVersionUID = -1403961759495272885L;

    public static String POSITION_TAB = "tab";
    public static String POSITION_FIELD_GROUP = "fieldGroup";
    public static String POSITION_FIELD = "field";

    public static long DEFAULT_MAX_LENGTH_STRING = 50L;

    public static String ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR = " - ";

    @Column(name = "field_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CustomFieldTypeEnum fieldType;

    @Column(name = "applies_to", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String appliesTo;

    @Type(type = "numeric_boolean")
    @Column(name = "value_required")
    private boolean valueRequired;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "crm_custom_field_tmpl_val")
    private Map<String, String> listValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crm_custom_field_tmpl_mcols", joinColumns = { @JoinColumn(name = "cft_id") })
    @AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, length = 20)),
            @AttributeOverride(name = "label", column = @Column(name = "label", nullable = false, length = 50)),
            @AttributeOverride(name = "keyType", column = @Column(name = "key_type", nullable = false, length = 10)) })
    private List<CustomFieldMatrixColumn> matrixColumns = new ArrayList<CustomFieldMatrixColumn>();

    @Transient
    private boolean matrixColumnsSorted;

    @Type(type = "numeric_boolean")
    @Column(name = "versionable")
    private boolean versionable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    // @Column(name = "cache_value_for")
    // private Integer cacheValueTimeperiod;

    @Column(name = "default_value", length = 250)
    @Size(max = 250)
    private String defaultValue;

    @Type(type = "numeric_boolean")
    @Column(name = "inh_as_def_value")
    private boolean useInheritedAsDefaultValue;

    /**
     * Reference to an entity. A classname. In case of CustomEntityTemplate, classname consist of "CustomEntityTemplate - <CustomEntityTemplate code>"
     */
    @Column(name = "entity_clazz", length = 255)
    @Size(max = 255)
    private String entityClazz;

    @Column(name = "storage_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CustomFieldStorageTypeEnum storageType = CustomFieldStorageTypeEnum.SINGLE;

    @Column(name = "mapkey_type")
    @Enumerated(EnumType.STRING)
    private CustomFieldMapKeyEnum mapKeyType;

    @Type(type = "numeric_boolean")
    @Column(name = "trigger_end_period_event", nullable = false)
    private boolean triggerEndPeriodEvent;

    @Column(name = "gui_position", length = 100)
    @Size(max = 100)
    private String guiPosition;

    @Type(type = "numeric_boolean")
    @Column(name = "allow_edit")
    @NotNull
    private boolean allowEdit = true;

    @Type(type = "numeric_boolean")
    @Column(name = "hide_on_new")
    @NotNull
    private boolean hideOnNew;

    @Column(name = "max_value")
    private Long maxValue;

    @Column(name = "min_value")
    private Long minValue;

    @Column(name = "reg_exp", length = 80)
    @Size(max = 80)
    private String regExp;

    @Column(name = "applicable_on_el", length = 2000)
    @Size(max = 2000)
    private String applicableOnEl;

    // @Type(type = "numeric_boolean")
    // @Column(name = "cache_value")
    // @NotNull
    // private boolean cacheValue;

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

    public void addMatrixColumn() {
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setPosition(matrixColumns.size() + 1);
        this.matrixColumns.add(column);
    }

    public void removeMatrixColumn(CustomFieldMatrixColumn columnToRemove) {
        this.matrixColumns.remove(columnToRemove);

        // Reorder position
        for (CustomFieldMatrixColumn column : matrixColumns) {
            if (column.getPosition() > columnToRemove.getPosition()) {
                column.setPosition(column.getPosition() - 1);
            }
        }
    }

    /**
     * Get a sorted list of matrix columns by its index position
     */
    public List<CustomFieldMatrixColumn> getMatrixColumnsSorted() {
        if (!matrixColumnsSorted) {
            Collections.sort(matrixColumns);
        }
        return matrixColumns;
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
        getMatrixColumnsSorted();
        return matrixColumns.get(index);
    }

    /**
     * Extract codes of matrix columns into a sorted list by column index
     * 
     * @return A list of matrix column codes
     */
    public List<String> getMatrixColumnCodes() {

        List<String> matrixColumnNames = null;
        if (storageType == CustomFieldStorageTypeEnum.MATRIX) {
            matrixColumnNames = new ArrayList<>();
            for (CustomFieldMatrixColumn column : getMatrixColumnsSorted()) {
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
     * @param entityClazz
     * @return
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

    public Map<String, String> getGuiPositionParsed() {

        if (guiPosition == null) {
            return null;
        }

        Map<String, String> parsedInfo = new HashMap<String, String>();

        String[] positions = guiPosition.split(";");

        for (String position : positions) {
            String[] positionDetails = position.split(":");
            if (!positionDetails[0].equals(POSITION_FIELD)) {
                parsedInfo.put(positionDetails[0] + "_name", positionDetails[1]);
                if (positionDetails.length == 3) {
                    parsedInfo.put(positionDetails[0] + "_pos", positionDetails[2]);
                }
            } else if (positionDetails[0].equals(POSITION_FIELD) && positionDetails.length == 2) {
                parsedInfo.put(positionDetails[0] + "_pos", positionDetails[1]);
            }
        }

        return parsedInfo;
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
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
            // return true;
        }

        if (code == null && other.getCode() != null) {
            return false;
        } else if (!code.equals(other.getCode())) {
            return false;
        } else if (appliesTo == null && other.getAppliesTo() != null) {
            return false;
        } else if (!appliesTo.equals(other.getAppliesTo())) {
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
     * Instantiate a CustomFieldValue from a template, setting a default value if applicable
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
        if (isVersionable() && getCalendar() != null) {
            return new DatePeriod(getCalendar().previousCalendarDate(date), getCalendar().nextCalendarDate(date));
        }
        return null;
    }
}