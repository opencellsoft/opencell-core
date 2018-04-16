package org.meveo.api.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CatMessagesDto.
 */
@XmlRootElement(name = "CatMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatMessagesDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The entity class. */
    @XmlAttribute(required = true)
    private String entityClass;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The valid from. */
    @XmlAttribute()
    protected Date validFrom;

    /** The valid to. */
    @XmlAttribute()
    protected Date validTo;

    /** The field name. */
    @XmlAttribute(required = true)
    private String fieldName;

    /** Use defaultValue instead. */
    @Deprecated
    private String defaultDescription;

    /** The default value. */
    @XmlElement(required = true)
    private String defaultValue;

    /** The translated descriptions. */
    @XmlElementWrapper(name = "translatedDescriptions")
    @XmlElement(name = "translatedDescription")
    @Deprecated
    private List<LanguageDescriptionDto> translatedDescriptions;

    /** The translated values. */
    @XmlElementWrapper(name = "translatedValues")
    @XmlElement(name = "translatedValue")
    private List<LanguageDescriptionDto> translatedValues;

    /**
     * Gets the entity class.
     *
     * @return the entity class
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * Sets the entity class.
     *
     * @param entityClass the new entity class
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the valid from.
     *
     * @return the valid from
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the valid from.
     *
     * @param validFrom the new valid from
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Gets the valid to.
     *
     * @return the valid to
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Sets the valid to.
     *
     * @param validTo the new valid to
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Gets the field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the field name.
     *
     * @param fieldName the new field name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the default description.
     *
     * @return the default description
     */
    public String getDefaultDescription() {
        return defaultDescription;
    }

    /**
     * Sets the default description.
     *
     * @param defaultDescription the new default description
     */
    public void setDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    /**
     * Gets the translated descriptions.
     *
     * @return the translated descriptions
     */
    public List<LanguageDescriptionDto> getTranslatedDescriptions() {
        return translatedDescriptions;
    }

    /**
     * Sets the translated descriptions.
     *
     * @param translatedDescriptions the new translated descriptions
     */
    public void setTranslatedDescriptions(List<LanguageDescriptionDto> translatedDescriptions) {
        this.translatedDescriptions = translatedDescriptions;
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
     * Gets the translated values.
     *
     * @return the translated values
     */
    public List<LanguageDescriptionDto> getTranslatedValues() {
        return translatedValues;
    }

    /**
     * Sets the translated values.
     *
     * @param translatedValues the new translated values
     */
    public void setTranslatedValues(List<LanguageDescriptionDto> translatedValues) {
        this.translatedValues = translatedValues;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CatMessagesDto [entityClass=" + entityClass + ", code=" + code + ", fieldName=" + fieldName + ", defaultDescription=" + defaultDescription + ", defaultValue="
                + defaultValue + ", translatedDescriptions=" + translatedDescriptions + ", translatedValues=" + translatedValues + "]";
    }
}