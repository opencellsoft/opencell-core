package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CustomFieldTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldTemplateDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    /**
     * Field code
     */
    @XmlAttribute(required = true)
    protected String code;

    /**
     * Field label
     */
    @XmlAttribute(required = true)
    protected String description;

    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Value type
     */
    @XmlElement(required = true)
    protected CustomFieldTypeEnum fieldType;

    @XmlElement(required = false)
    @Deprecated
    protected String accountLevel;

    /**
     * PROVIDER - Provider, SELLER - Seller, CUST - customer, CA - Customer account, BA - Billing Account, UA - User account, SUB - subscription, ACC - access, CHARGE - charge
     * template, SERVICE - service template or service instance, OFFER_CATEGORY - Offer template category, OFFER - Offer template, JOB_XX - Job instance, CE_ - Custom entity
     * instance
     */
    @XmlElement(required = false)
    protected String appliesTo;

    /**
     * Default value
     */
    @XmlElement
    protected String defaultValue;

    /**
     * Shall inherited value be used as default value instead if available
     */
    @XmlElement
    protected Boolean useInheritedAsDefaultValue;

    /**
     * Value storage type
     */
    @XmlElement(required = true)
    protected CustomFieldStorageTypeEnum storageType;

    /**
     * Is value required
     */
    @XmlElement
    protected Boolean valueRequired;

    /**
     * Is value versionable
     */
    @XmlElement
    protected Boolean versionable;

    /**
     * Should Period end event be fired when value period is over
     */
    @XmlElement
    protected Boolean triggerEndPeriodEvent;

    /**
     * Calendar associated to value versioning periods
     */
    @XmlElement
    protected String calendar;

    /**
     * How long versionable values be cached past the period end date. As of v.4.7 not used anymore
     */
    @XmlElement
    @Deprecated
    protected Integer cacheValueTimeperiod;

    /**
     * Entity class and CET code for a reference to entity or child entity type fields
     */
    @XmlElement
    protected String entityClazz;

    /**
     * List of values for LIST type field
     */
    @XmlElement
    protected Map<String, String> listValues;

    /**
     * Can value be changed when editing a previously saved entity
     */
    @XmlElement
    protected Boolean allowEdit = true;

    /**
     * Do not show/apply field on new entity creation
     */
    @XmlElement
    protected Boolean hideOnNew;

    /**
     * Maximum value to validate long and double values OR maximum length of string value
     */
    @XmlElement
    protected Long maxValue;

    /**
     * Minimum value to validate long and double values
     */
    @XmlElement
    protected Long minValue;

    /**
     * Regular expression to validate string values
     */
    @XmlElement
    protected String regExp;

    /**
     * Should value be cached. As of v.4.7 not used anymore
     */
    @XmlElement
    @Deprecated
    protected Boolean cacheValue;

    /**
     * Where field should be displayed. Format: tab:&lt;tab name&gt;:&lt;tab relative position&gt;;fieldGroup:&lt;fieldgroup name&gt;:&lt;fieldgroup relative
     * position&gt;;field:&lt;field relative position in fieldgroup/tab&gt;<br/>
     * <br/>
     * 
     * Tab and field group names support translation in the following format: &lt;default value&gt;|&lt;language3 letter key=translated value&gt;<br/>
     * 
     * e.g. tab:Tab default title|FRA=Title in french|ENG=Title in english:0;fieldGroup:Field group default label|FRA=Field group label in french|ENG=Field group label in
     * english:0;field:0 OR tab:Second tab:1;field:1
     */
    @XmlElement
    protected String guiPosition;

    /**
     * Key format of a map for map type fields
     */
    @XmlElement()
    protected CustomFieldMapKeyEnum mapKeyType;

    /**
     * EL expression (including #{}) to evaluate when field is applicable.
     */
    @XmlElement()
    protected String applicableOnEl;

    /**
     * A list of columns matrix consists of
     */
    @XmlElementWrapper(name = "matrixColumns")
    @XmlElement(name = "matrixColumn")
    private List<CustomFieldMatrixColumnDto> matrixColumns;

    /**
     * A list of child entity fields to be displayed in a summary table of child entities
     */
    @XmlElementWrapper(name = "childEntityFieldsForSummary")
    @XmlElement(name = "fieldCode")
    private List<String> childEntityFieldsForSummary;

    /**
     * If and how custom field values should be indexed in Elastic Search
     */
    private CustomFieldIndexTypeEnum indexType;

    /**
     * Tags assigned to custom field template
     */
    private String tags;

    public CustomFieldTemplateDto() {

    }

    public CustomFieldTemplateDto(CustomFieldTemplate cf) {
        code = cf.getCode();
        description = cf.getDescription();
        languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(cf.getDescriptionI18n());
        fieldType = cf.getFieldType();
        accountLevel = cf.getAppliesTo();
        appliesTo = cf.getAppliesTo();
        defaultValue = cf.getDefaultValue();
        useInheritedAsDefaultValue = cf.isUseInheritedAsDefaultValue();
        storageType = cf.getStorageType();
        valueRequired = cf.isValueRequired();
        versionable = cf.isVersionable();
        triggerEndPeriodEvent = cf.isTriggerEndPeriodEvent();
        entityClazz = cf.getEntityClazz();
        if (cf.getCalendar() != null) {
            calendar = cf.getCalendar().getCode();
        }
        allowEdit = cf.isAllowEdit();
        hideOnNew = cf.isHideOnNew();
        minValue = cf.getMinValue();
        maxValue = cf.getMaxValue();
        regExp = cf.getRegExp();
        // cacheValue = cf.isCacheValue();
        // cacheValueTimeperiod = cf.getCacheValueTimeperiod();
        guiPosition = cf.getGuiPosition();
        if (cf.getFieldType() == CustomFieldTypeEnum.LIST) {
            listValues = cf.getListValuesSorted();
        }
        applicableOnEl = cf.getApplicableOnEl();
        mapKeyType = cf.getMapKeyType();
        indexType = cf.getIndexType();
        tags = cf.getTags();

        if (cf.getStorageType() == CustomFieldStorageTypeEnum.MATRIX && cf.getMatrixColumns() != null) {
            matrixColumns = new ArrayList<>();
            for (CustomFieldMatrixColumn column : cf.getMatrixColumns()) {
                matrixColumns.add(new CustomFieldMatrixColumnDto(column));
            }
        }

        if (cf.getFieldType() == CustomFieldTypeEnum.CHILD_ENTITY && cf.getChildEntityFields() != null) {
            childEntityFieldsForSummary = Arrays.asList(cf.getChildEntityFieldsAsList());
        }

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CustomFieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(CustomFieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public String getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(String accountLevel) {
        this.accountLevel = accountLevel;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean isUseInheritedAsDefaultValue() {
        return useInheritedAsDefaultValue;
    }

    public void setUseInheritedAsDefaultValue(Boolean useInheritedAsDefaultValue) {
        this.useInheritedAsDefaultValue = useInheritedAsDefaultValue;
    }

    public CustomFieldStorageTypeEnum getStorageType() {
        return storageType;
    }

    public void setStorageType(CustomFieldStorageTypeEnum storageType) {
        this.storageType = storageType;
    }

    public Boolean isVersionable() {
        return versionable;
    }

    public void setVersionable(Boolean versionable) {
        this.versionable = versionable;
    }

    public Boolean isTriggerEndPeriodEvent() {
        return triggerEndPeriodEvent;
    }

    public void setTriggerEndPeriodEvent(Boolean triggerEndPeriodEvent) {
        this.triggerEndPeriodEvent = triggerEndPeriodEvent;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public Boolean isValueRequired() {
        return valueRequired;
    }

    public void setValueRequired(Boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    @Override
    public String toString() {
        return "CustomFieldTemplateDto [code=" + code + ", description=" + description + ", fieldType=" + fieldType + ", accountLevel=" + accountLevel + ", appliesTo=" + appliesTo
                + ", defaultValue=" + defaultValue + ", storageType=" + storageType + ", mapKeyType=" + mapKeyType + ", valueRequired=" + valueRequired + ", versionable="
                + versionable + ", triggerEndPeriodEvent=" + triggerEndPeriodEvent + ", calendar=" + calendar + ", entityClazz=" + entityClazz + ", indexType=" + indexType + "]";
    }

    public String getEntityClazz() {
        return entityClazz;
    }

    public void setEntityClazz(String entityClazz) {
        this.entityClazz = entityClazz;
    }

    /**
     * @return the listValues
     */
    public Map<String, String> getListValues() {
        if (listValues == null) {
            listValues = new HashMap<String, String>();
        }
        return listValues;
    }

    /**
     * @param listValues the listValues to set
     */
    public void setListValues(Map<String, String> listValues) {
        this.listValues = listValues;
    }

    public Boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(Boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    public Boolean isHideOnNew() {
        return hideOnNew;
    }

    public void setHideOnNew(Boolean hideOnNew) {
        this.hideOnNew = hideOnNew;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public String getRegExp() {
        return regExp;
    }

    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    public Boolean isCacheValue() {
        return cacheValue;
    }

    public void setCacheValue(Boolean cacheValue) {
        this.cacheValue = cacheValue;
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

    public CustomFieldMapKeyEnum getMapKeyType() {
        return mapKeyType;
    }

    public void setMapKeyType(CustomFieldMapKeyEnum mapKeyType) {
        this.mapKeyType = mapKeyType;
    }

    public String getApplicableOnEl() {
        return applicableOnEl;
    }

    public void setApplicableOnEl(String applicableOnEl) {
        this.applicableOnEl = applicableOnEl;
    }

    public List<CustomFieldMatrixColumnDto> getMatrixColumns() {
        if (matrixColumns == null) {
            matrixColumns = new ArrayList<CustomFieldMatrixColumnDto>();
        }
        return matrixColumns;
    }

    public void setMatrixColumns(List<CustomFieldMatrixColumnDto> matrixColumns) {
        this.matrixColumns = matrixColumns;
    }

    public List<String> getChildEntityFieldsForSummary() {
        return childEntityFieldsForSummary;
    }

    public void setChildEntityFieldsForSummary(List<String> childEntityFieldsForSummary) {
        this.childEntityFieldsForSummary = childEntityFieldsForSummary;
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

    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }
}