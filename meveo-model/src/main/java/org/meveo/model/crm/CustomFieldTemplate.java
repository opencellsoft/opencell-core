package org.meveo.model.crm;

import java.util.HashMap;
import java.util.Map;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.Calendar;

@Entity
@ExportIdentifier({ "code", "appliesTo", "provider" })
@Table(name = "CRM_CUSTOM_FIELD_TMPL", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "APPLIES_TO", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FLD_TMP_SEQ")
@NamedQueries({ @NamedQuery(name = "CustomFieldTemplate.getCFTForCache", query = "SELECT cft from CustomFieldTemplate cft left join fetch cft.calendar where cft.disabled=false  ") })
public class CustomFieldTemplate extends BusinessEntity {

    private static final long serialVersionUID = -1403961759495272885L;

    public static String POSITION_TAB = "tab";
    public static String POSITION_FIELD_GROUP = "fieldGroup";
    public static String POSITION_FIELD = "field";

    public static long DEFAULT_MAX_LENGTH_STRING = 50L;

    @Column(name = "FIELD_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomFieldTypeEnum fieldType;

    @Column(name = "APPLIES_TO", nullable = false, length = 100)
    private String appliesTo;

    @Column(name = "VALUE_REQUIRED")
    private boolean valueRequired;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CRM_CUSTOM_FIELD_TMPL_VAL")
    private Map<String, String> listValues = new HashMap<String, String>();

    @Column(name = "VERSIONABLE")
    private boolean versionable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;

    @Column(name = "CACHE_VALUE_FOR")
    private Integer cacheValueTimeperiod;

    @Column(name = "DEFAULT_VALUE", length = 50)
    private String defaultValue;

    @Column(name = "ENTITY_CLAZZ")
    private String entityClazz;

    @Column(name = "STORAGE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomFieldStorageTypeEnum storageType = CustomFieldStorageTypeEnum.SINGLE;

    @Column(name = "MAPKEY_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomFieldMapKeyEnum mapKeyType;

    @Column(name = "TRIGGER_END_PERIOD_EVENT", nullable = false)
    private boolean triggerEndPeriodEvent;

    @Column(name = "GUI_POSITION", length = 100)
    private String guiPosition;

    @Column(name = "ALLOW_EDIT")
    private boolean allowEdit = true;

    @Column(name = "HIDE_ON_NEW")
    private boolean hideOnNew;

    @Column(name = "MAX_VALUE")
    private Long maxValue;

    @Column(name = "MIN_VALUE")
    private Long minValue;

    @Column(name = "REG_EXP", length = 80)
    private String regExp;

    @Column(name = "APPLICABLE_ON_EL", length = 150)
    @Size(max = 150)
    private String applicableOnEl;

    @Column(name = "CACHE_VALUE")
    private boolean cacheValue;

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

    public Object getDefaultValueConverted() {
        if (defaultValue != null) {
            try {
                if (fieldType == CustomFieldTypeEnum.DOUBLE) {
                    return Double.parseDouble(defaultValue);
                } else if (fieldType == CustomFieldTypeEnum.LONG) {
                    return Long.parseLong(defaultValue);
                } else if (fieldType == CustomFieldTypeEnum.STRING || fieldType == CustomFieldTypeEnum.LIST || fieldType == CustomFieldTypeEnum.TEXT_AREA) {
                    return defaultValue;
                } else if (fieldType == CustomFieldTypeEnum.DATE) {
                    return null; // TODO implement deserialization from a date
                }
            } catch (Exception e) {
                return null;
            }
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
        }

        if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomFieldTemplate)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CustomFieldTemplate other = (CustomFieldTemplate) obj;

        if (getId() != null && other.getId() != null && getId() == other.getId()) {
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

    @Override
    public String toString() {
        return String.format("CustomFieldTemplate [id=%s, appliesTo=%s, code=%s]", id, appliesTo, code);
    }
}