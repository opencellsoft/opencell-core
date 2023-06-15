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

package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldIndexTypeEnum;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

/**
 * The Class CustomFieldTemplateDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
@XmlRootElement(name = "CustomFieldTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldTemplateDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /** Value type. */
    @XmlElement(required = true)
    protected CustomFieldTypeEnum fieldType;

    /** The account level. */
    @XmlElement
    @Deprecated
    protected String accountLevel;

    /**
     * PROVIDER - Provider, Seller - Seller, Customer - customer, CustomerAccount - Customer account, BillingAccount - Billing Account, UserAccount - User account, Subscription - subscription, Access - access,
     * ChargeTemplate - charge template, ServiceInstance - service instance, ServiceTemplate - service template, OfferTemplateCategory - Offer template category, OfferTemplate - Offer template,
     * JOB_XX - Job instance, CE_ - Custom entity instance.
     */
    @XmlElement
    protected String appliesTo;

    /** Default value. */
    @XmlElement
    protected String defaultValue;

    /** Shall inherited value be used as default value instead if available. */
    @XmlElement
    protected Boolean useInheritedAsDefaultValue;

    /** Value storage type. */
    @XmlElement(required = true)
    protected CustomFieldStorageTypeEnum storageType;

    /** Is value required. */
    @XmlElement
    protected Boolean valueRequired;

    /** Is value is part of unique constraint. */
    @XmlElement
    protected Boolean uniqueConstraint;

    /** Is value versionable. */
    @XmlElement
    protected Boolean versionable;

    /** Should Period end event be fired when value period is over. */
    @XmlElement
    protected Boolean triggerEndPeriodEvent;

    /** Calendar associated to value versioning periods. */
    @XmlElement
    protected String calendar;

    /**
     * How long versionable values be cached past the period end date. As of v.4.7 not used anymore
     */
    @XmlElement
    @Deprecated
    protected Integer cacheValueTimeperiod;

    /** Entity class and CET code for a reference to entity or child entity type fields. */
    @XmlElement
    protected String entityClazz;

    /** List of values for LIST type field. */
    @XmlElement
    protected Map<String, String> listValues;

    /** Can value be changed when editing a previously saved entity. */
    @XmlElement
    protected Boolean allowEdit = true;

    /** Do not show/apply field on new entity creation. */
    @XmlElement
    protected Boolean hideOnNew;

    /** Maximum value to validate long and double values OR maximum length of string value. */
    @XmlElement
    protected Long maxValue;

    /** Minimum value to validate long and double values. */
    @XmlElement
    protected Long minValue;

    /**
     * Regular expression to validate string values.
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
     * The digit's number of a decimal for a double type.
     */
    @XmlElement
    protected Integer nbDecimal;

    /**
     * The rounding mode.
     */
    @XmlElement
    protected RoundingModeEnum roundingMode;

    /**
     * Where field should be displayed. Format: tab:&lt;tab name&gt;:&lt;tab relative position&gt;;fieldGroup:&lt;fieldgroup name&gt;:&lt;fieldgroup relative
     * position&gt;;field:&lt;field relative position in fieldgroup
     * 
     * 
     * Tab and field group names support translation in the following format: &lt;default value&gt;|&lt;language3 letter key=translated value&gt;
     * 
     * e.g. tab:Tab default title|FRA=Title in french|ENG=Title in english:0;fieldGroup:Field group default label|FRA=Field group label in french|ENG=Field group label in
     * english:0;field:0 OR tab:Second tab:1;field:1
     */
    @XmlElement
    protected String guiPosition;

    /**
     * Key format of a map for map type fields.
     */
    @XmlElement()
    protected CustomFieldMapKeyEnum mapKeyType;

    /**
     * EL expression (including #{}) to evaluate when field is applicable.
     */
    @XmlElement()
    protected String applicableOnEl;

    /**
     * A list of columns matrix consists of.
     */
    @XmlElementWrapper(name = "matrixColumns")
    @XmlElement(name = "matrixColumn")
    private List<CustomFieldMatrixColumnDto> matrixColumns;

    /**
     * A list of child entity fields to be displayed in a summary table of child entities.
     */
    @XmlElementWrapper(name = "childEntityFieldsForSummary")
    @XmlElement(name = "fieldCode")
    private List<String> childEntityFieldsForSummary;

    /**
     * If and how custom field values should be indexed in Elastic Search.
     */
    private CustomFieldIndexTypeEnum indexType;

    /**
     * Tags assigned to custom field template.
     */
    private String tags;

    /**
     * display format.
     */
    @XmlElement
    protected String displayFormat;

    /**
     * An EL expression that should resolve into the code of a valid CustomTable.
     */
    @XmlElement
    private String customTableCodeEL;

    /**
     * An EL expression that should resolve into a list of filters as defined by the Search API.
     */
    @XmlElement
    private String dataFilterEL;

    /**
     * Should resolve into a list of CT fields to be shown for CTW (displayed in GUI, returned by API). It's the "fields" parameter in the CT list API.
     */
    @XmlElement
    private String fieldsEL;

    /**
     * which should resolve into an additional filter to apply to the table search.
     */
    @XmlElement
    private String versionFilterEL;

    /**
     * value indicate if anonymize gdpr is enabled
     */
    private boolean anonymize;

    /** Reference table associated with the entity clazz if informed*/
    @XmlElement
    private String referenceTable;


    /**
     * Instantiates a new custom field template dto.
     */
    public CustomFieldTemplateDto() {

    }

    /**
     * Instantiates a new custom field template dto.
     *
     * @param cf the cf
     */
    public CustomFieldTemplateDto(CustomFieldTemplate cf) {
        super(cf);

        languageDescriptions = LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(cf.getDescriptionI18n());
        fieldType = cf.getFieldType();
        accountLevel = cf.getAppliesTo();
        appliesTo = cf.getAppliesTo();
        defaultValue = cf.getDefaultValue();
        useInheritedAsDefaultValue = cf.isUseInheritedAsDefaultValue();
        storageType = cf.getStorageType();
        displayFormat = cf.getDisplayFormat();
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
        guiPosition = cf.getGuiPosition();
        if (cf.getFieldType() == CustomFieldTypeEnum.LIST 
                || cf.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST) {
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
        this.uniqueConstraint = cf.isUniqueConstraint();
        this.customTableCodeEL = cf.getCustomTableCodeEL();
        this.dataFilterEL = cf.getDataFilterEL();
        this.fieldsEL = cf.getFieldsEL();
        this.anonymize = cf.isAnonymizeGdpr();

    }

    /**
     * Gets the field type.
     *
     * @return the field type
     */
    public CustomFieldTypeEnum getFieldType() {
        return fieldType;
    }

    /**
     * Sets the field type.
     *
     * @param fieldType the new field type
     */
    public void setFieldType(CustomFieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Gets the account level.
     *
     * @return the account level
     */
    public String getAccountLevel() {
        return accountLevel;
    }

    /**
     * Sets the account level.
     *
     * @param accountLevel the new account level
     */
    public void setAccountLevel(String accountLevel) {
        this.accountLevel = accountLevel;
    }

    /**
     * Gets the applies to.
     *
     * @return the applies to
     */
    public String getAppliesTo() {
        return appliesTo;
    }

    /**
     * Sets the applies to.
     *
     * @param appliesTo the new applies to
     */
    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Checks if is use inherited as default value.
     *
     * @return the boolean
     */
    public Boolean isUseInheritedAsDefaultValue() {
        return useInheritedAsDefaultValue;
    }

    /**
     * Sets the use inherited as default value.
     *
     * @param useInheritedAsDefaultValue the new use inherited as default value
     */
    public void setUseInheritedAsDefaultValue(Boolean useInheritedAsDefaultValue) {
        this.useInheritedAsDefaultValue = useInheritedAsDefaultValue;
    }

    /**
     * Gets the storage type.
     *
     * @return the storage type
     */
    public CustomFieldStorageTypeEnum getStorageType() {
        return storageType;
    }

    /**
     * Sets the storage type.
     *
     * @param storageType the new storage type
     */
    public void setStorageType(CustomFieldStorageTypeEnum storageType) {
        this.storageType = storageType;
    }

    /**
     * Checks if is versionable.
     *
     * @return the boolean
     */
    public Boolean isVersionable() {
        return versionable;
    }

    /**
     * Sets the versionable.
     *
     * @param versionable the new versionable
     */
    public void setVersionable(Boolean versionable) {
        this.versionable = versionable;
    }

    /**
     * Checks if is trigger end period event.
     *
     * @return the boolean
     */
    public Boolean isTriggerEndPeriodEvent() {
        return triggerEndPeriodEvent;
    }

    /**
     * Sets the trigger end period event.
     *
     * @param triggerEndPeriodEvent the new trigger end period event
     */
    public void setTriggerEndPeriodEvent(Boolean triggerEndPeriodEvent) {
        this.triggerEndPeriodEvent = triggerEndPeriodEvent;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public String getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    /**
     * Checks if is value required.
     *
     * @return the boolean
     */
    public Boolean isValueRequired() {
        return valueRequired;
    }

    /**
     * Sets the value required.
     *
     * @param valueRequired the new value required
     */
    public void setValueRequired(Boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomFieldTemplateDto [code=" + code + ", description=" + description + ", fieldType=" + fieldType + ", accountLevel=" + accountLevel + ", appliesTo=" + appliesTo
                + ", defaultValue=" + defaultValue + ", storageType=" + storageType + ", displayFormat=" + displayFormat + ", mapKeyType=" + mapKeyType + ", valueRequired=" + valueRequired + ", versionable="
                + versionable + ", triggerEndPeriodEvent=" + triggerEndPeriodEvent + ", calendar=" + calendar + ", entityClazz=" + entityClazz + ", indexType=" + indexType + "]";
    }

    /**
     * Gets the entity clazz.
     *
     * @return the entity clazz
     */
    public String getEntityClazz() {
        return entityClazz;
    }

    /**
     * Sets the entity clazz.
     *
     * @param entityClazz the new entity clazz
     */
    public void setEntityClazz(String entityClazz) {
        this.entityClazz = entityClazz;
    }

    /**
     * Gets the list values.
     *
     * @return the listValues
     */
    public Map<String, String> getListValues() {
        if (listValues == null) {
            listValues = new HashMap<String, String>();
        }
        return listValues;
    }

    /**
     * Sets the list values.
     *
     * @param listValues the listValues to set
     */
    public void setListValues(Map<String, String> listValues) {
        this.listValues = listValues;
    }

    /**
     * Checks if is allow edit.
     *
     * @return the boolean
     */
    public Boolean isAllowEdit() {
        return allowEdit;
    }

    /**
     * Sets the allow edit.
     *
     * @param allowEdit the new allow edit
     */
    public void setAllowEdit(Boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    /**
     * Checks if is hide on new.
     *
     * @return the boolean
     */
    public Boolean isHideOnNew() {
        return hideOnNew;
    }

    /**
     * Sets the hide on new.
     *
     * @param hideOnNew the new hide on new
     */
    public void setHideOnNew(Boolean hideOnNew) {
        this.hideOnNew = hideOnNew;
    }

    /**
     * Gets the min value.
     *
     * @return the min value
     */
    public Long getMinValue() {
        return minValue;
    }

    /**
     * Sets the min value.
     *
     * @param minValue the new min value
     */
    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    /**
     * Gets the max value.
     *
     * @return the max value
     */
    public Long getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the max value.
     *
     * @param maxValue the new max value
     */
    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Gets the reg exp.
     *
     * @return the reg exp
     */
    public String getRegExp() {
        return regExp;
    }

    /**
     * Sets the reg exp.
     *
     * @param regExp the new reg exp
     */
    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    /**
     * Checks if is cache value.
     *
     * @return the boolean
     */
    public Boolean isCacheValue() {
        return cacheValue;
    }

    /**
     * Sets the cache value.
     *
     * @param cacheValue the new cache value
     */
    public void setCacheValue(Boolean cacheValue) {
        this.cacheValue = cacheValue;
    }

    /**
     * Gets the cache value timeperiod.
     *
     * @return the cache value timeperiod
     */
    public Integer getCacheValueTimeperiod() {
        return cacheValueTimeperiod;
    }

    /**
     * Sets the cache value timeperiod.
     *
     * @param cacheValueTimeperiod the new cache value timeperiod
     */
    public void setCacheValueTimeperiod(Integer cacheValueTimeperiod) {
        this.cacheValueTimeperiod = cacheValueTimeperiod;
    }

    /**
     * Gets the gui position.
     *
     * @return the gui position
     */
    public String getGuiPosition() {
        return guiPosition;
    }

    /**
     * Sets the gui position.
     *
     * @param guiPosition the new gui position
     */
    public void setGuiPosition(String guiPosition) {
        this.guiPosition = guiPosition;
    }

    /**
     * Gets the map key type.
     *
     * @return the map key type
     */
    public CustomFieldMapKeyEnum getMapKeyType() {
        return mapKeyType;
    }

    /**
     * Sets the map key type.
     *
     * @param mapKeyType the new map key type
     */
    public void setMapKeyType(CustomFieldMapKeyEnum mapKeyType) {
        this.mapKeyType = mapKeyType;
    }

    /**
     * Gets the applicable on el.
     *
     * @return the applicable on el
     */
    public String getApplicableOnEl() {
        return applicableOnEl;
    }

    /**
     * Sets the applicable on el.
     *
     * @param applicableOnEl the new applicable on el
     */
    public void setApplicableOnEl(String applicableOnEl) {
        this.applicableOnEl = applicableOnEl;
    }

    /**
     * Gets the matrix columns.
     *
     * @return the matrix columns
     */
    public List<CustomFieldMatrixColumnDto> getMatrixColumns() {
        if (matrixColumns == null) {
            matrixColumns = new ArrayList<CustomFieldMatrixColumnDto>();
        }
        return matrixColumns;
    }

    /**
     * Sets the matrix columns.
     *
     * @param matrixColumns the new matrix columns
     */
    public void setMatrixColumns(List<CustomFieldMatrixColumnDto> matrixColumns) {
        this.matrixColumns = matrixColumns;
    }

    /**
     * Gets the child entity fields for summary.
     *
     * @return the child entity fields for summary
     */
    public List<String> getChildEntityFieldsForSummary() {
        return childEntityFieldsForSummary;
    }

    /**
     * Sets the child entity fields for summary.
     *
     * @param childEntityFieldsForSummary the new child entity fields for summary
     */
    public void setChildEntityFieldsForSummary(List<String> childEntityFieldsForSummary) {
        this.childEntityFieldsForSummary = childEntityFieldsForSummary;
    }

    /**
     * Gets the index type.
     *
     * @return the index type
     */
    public CustomFieldIndexTypeEnum getIndexType() {
        return indexType;
    }

    /**
     * Sets the index type.
     *
     * @param indexType the new index type
     */
    public void setIndexType(CustomFieldIndexTypeEnum indexType) {
        this.indexType = indexType;
    }

    /**
     * Gets the tags.
     *
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * Sets the tags.
     *
     * @param tags the new tags
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Gets the language descriptions.
     *
     * @return the language descriptions
     */
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    /**
     * Sets the language descriptions.
     *
     * @param languageDescriptions the new language descriptions
     */
    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
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
     * Gets the number of decimal digits.
     * @return the number of decimal digits.
     */
    public Integer getNbDecimal() {
        return nbDecimal;
    }

    /**
     * Sets the number of decimal digits.
     * @param nbDecimal the number of decimal digits.
     */
    public void setNbDecimal(Integer nbDecimal) {
        this.nbDecimal = nbDecimal;
    }

    /**
     * Gets the rounding Mode
     * @return the rounding Mode
     */
    public RoundingModeEnum getRoundingMode() {
        return roundingMode;
    }

    /**
     * Sets the rounding Mode
     * @param roundingMode the rounding Mode
     */
    public void setRoundingMode(RoundingModeEnum roundingMode) {
        this.roundingMode = roundingMode;
    }

    public Boolean getUniqueConstraint() {
        return uniqueConstraint;
    }

    public void setUniqueConstraint(Boolean uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
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

    /**
     * Gets the versionFilterEL expression.
     *
     * @return the versionFilterEL expression.
     */
    public String getVersionFilterEL() {
        return versionFilterEL;
    }

    /**
     * Sets the versionFilterEL expression.
     *
     * @param versionFilterEL the versionFilterEL expression.
     */
    public void setVersionFilterEL(String versionFilterEL) {
        this.versionFilterEL = versionFilterEL;
    }

    public boolean isAnonymize() {
        return anonymize;
    }

    public void setAnonymize(boolean anonymize) {
        this.anonymize = anonymize;
    }

    /**
     * Gets the reference table.
     *
     * @return the reference table.
     */
    public String getReferenceTable() {
        return referenceTable;
    }

    /**
     * Sets the reference table.
     *
     * @param referenceTable the reference table.
     */
    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }
}