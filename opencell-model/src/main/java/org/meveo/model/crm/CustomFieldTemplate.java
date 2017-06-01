package org.meveo.model.crm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.shared.DateUtils;

@Entity
@ModuleItem
@ExportIdentifier({ "code", "appliesTo" })
@Table(name = "CRM_CUSTOM_FIELD_TMPL", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "APPLIES_TO" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "CRM_CUSTOM_FLD_TMP_SEQ"), })
@NamedQueries({
        @NamedQuery(name = "CustomFieldTemplate.getCFTForCache", query = "SELECT cft from CustomFieldTemplate cft left join fetch cft.calendar where cft.disabled=false order by cft.appliesTo"),
        @NamedQuery(name = "CustomFieldTemplate.getCFTForIndex", query = "SELECT cft from CustomFieldTemplate cft where cft.disabled=false and cft.indexType is not null ") })
public class CustomFieldTemplate extends BusinessEntity {

    private static final long serialVersionUID = -1403961759495272885L;

    public static String POSITION_TAB = "tab";
    public static String POSITION_FIELD_GROUP = "fieldGroup";
    public static String POSITION_FIELD = "field";

    public static long DEFAULT_MAX_LENGTH_STRING = 50L;

    public static String ENTITY_REFERENCE_CLASSNAME_CETCODE_SEPARATOR = " - ";

    @Column(name = "FIELD_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CustomFieldTypeEnum fieldType;

    @Column(name = "APPLIES_TO", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String appliesTo;

    @Type(type = "numeric_boolean")
    @Column(name = "VALUE_REQUIRED")
    private boolean valueRequired;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_TMPL_VAL")
    private Map<String, String> listValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_TMPL_MCOLS", joinColumns = { @JoinColumn(name = "CFT_ID") })
    @AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "CODE", nullable = false, length = 20)),
            @AttributeOverride(name = "label", column = @Column(name = "LABEL", nullable = false, length = 50)),
            @AttributeOverride(name = "keyType", column = @Column(name = "KEY_TYPE", nullable = false, length = 10)) })
    private List<CustomFieldMatrixColumn> matrixColumns = new ArrayList<CustomFieldMatrixColumn>();

    @Transient
    private boolean matrixColumnsSorted;

    @Type(type = "numeric_boolean")
    @Column(name = "VERSIONABLE")
    private boolean versionable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    @Column(name = "CACHE_VALUE_FOR")
    private Integer cacheValueTimeperiod;

    @Column(name = "DEFAULT_VALUE", length = 250)
    @Size(max = 250)
    private String defaultValue;

    /**
     * Reference to an entity. A classname. In case of CustomEntityTemplate, classname consist of "CustomEntityTemplate - <CustomEntityTemplate code>"
     */
    @Column(name = "ENTITY_CLAZZ", length = 255)
    @Size(max = 255)
    private String entityClazz;

    @Column(name = "STORAGE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CustomFieldStorageTypeEnum storageType = CustomFieldStorageTypeEnum.SINGLE;

    @Column(name = "MAPKEY_TYPE")
    @Enumerated(EnumType.STRING)
    private CustomFieldMapKeyEnum mapKeyType;

    @Type(type = "numeric_boolean")
    @Column(name = "TRIGGER_END_PERIOD_EVENT", nullable = false)
    private boolean triggerEndPeriodEvent;

    @Column(name = "GUI_POSITION", length = 100)
    @Size(max = 100)
    private String guiPosition;

    @Type(type = "numeric_boolean")
    @Column(name = "ALLOW_EDIT")
    @NotNull
    private boolean allowEdit = true;

    @Type(type = "numeric_boolean")
    @Column(name = "HIDE_ON_NEW")
    @NotNull
    private boolean hideOnNew;

    @Column(name = "MAX_VALUE")
    private Long maxValue;

    @Column(name = "MIN_VALUE")
    private Long minValue;

    @Column(name = "REG_EXP", length = 80)
    @Size(max = 80)
    private String regExp;

    @Column(name = "APPLICABLE_ON_EL", length = 2000)
    @Size(max = 2000)
    private String applicableOnEl;

    @Type(type = "numeric_boolean")
    @Column(name = "CACHE_VALUE")
    @NotNull
    private boolean cacheValue;

    /**
     * Child entity fields to display as summary. Field names are separated by a comma.
     */
    @Column(name = "CHE_FIELDS", length = 500)
    @Size(max = 500)
    private String childEntityFields;

    /**
     * If and how custom field value should be indexed in Elastic Search
     */
    @Column(name = "INDEX_TYPE", length = 10)
    @Enumerated(EnumType.STRING)
    private CustomFieldIndexTypeEnum indexType;

    /**
     * Tags assigned to custom field template
     */
    @Column(name = "TAGS", length = 2000)
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

    public Integer getCacheValueTimeperiod() {
        return cacheValueTimeperiod;
    }

    public void setCacheValueTimeperiod(Integer cacheValueTimeperiod) {
        this.cacheValueTimeperiod = cacheValueTimeperiod;
    }

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

    public boolean isCacheValue() {
        return cacheValue;
    }

    public void setCacheValue(boolean cacheValue) {
        this.cacheValue = cacheValue;
    }

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
}