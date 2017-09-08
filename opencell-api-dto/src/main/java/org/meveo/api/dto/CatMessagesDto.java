package org.meveo.api.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CatMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatMessagesDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(required = true)
    private String entityClass;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    protected Date validFrom;

    @XmlAttribute()
    protected Date validTo;

    @XmlAttribute(required = true)
    private String fieldName;

    /**
     * Use defaultValue instead
     */
    @Deprecated
    private String defaultDescription;

    @XmlElement(required = true)
    private String defaultValue;

    @XmlElementWrapper(name = "translatedDescriptions")
    @XmlElement(name = "translatedDescription")
    @Deprecated
    private List<LanguageDescriptionDto> translatedDescriptions;

    @XmlElementWrapper(name = "translatedValues")
    @XmlElement(name = "translatedValue")
    private List<LanguageDescriptionDto> translatedValues;

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public void setDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public List<LanguageDescriptionDto> getTranslatedDescriptions() {
        return translatedDescriptions;
    }

    public void setTranslatedDescriptions(List<LanguageDescriptionDto> translatedDescriptions) {
        this.translatedDescriptions = translatedDescriptions;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<LanguageDescriptionDto> getTranslatedValues() {
        return translatedValues;
    }

    public void setTranslatedValues(List<LanguageDescriptionDto> translatedValues) {
        this.translatedValues = translatedValues;
    }

    @Override
    public String toString() {
        return "CatMessagesDto [entityClass=" + entityClass + ", code=" + code + ", fieldName=" + fieldName + ", defaultDescription=" + defaultDescription + ", defaultValue="
                + defaultValue + ", translatedDescriptions=" + translatedDescriptions + ", translatedValues=" + translatedValues + "]";
    }
}