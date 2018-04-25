package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class LanguageDescriptionDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "LanguageDescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDescriptionDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4686792860854718893L;

    /** The language code. */
    private String languageCode;
    
    /** The description. */
    private String description;

    /**
     * Instantiates a new language description dto.
     */
    public LanguageDescriptionDto() {

    }

    /**
     * Instantiates a new language description dto.
     *
     * @param languageCode the language code
     * @param description the description
     */
    public LanguageDescriptionDto(String languageCode, String description) {
        this.languageCode = languageCode;
        this.description = description;
    }

    /**
     * Gets the language code.
     *
     * @return the language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the new language code
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LanguageDescriptionDto [languageCode=" + languageCode + ", description=" + description + "]";
    }

    /**
     * Convert multi language field value map into a list of multi language field DTO values.
     *
     * @param multiLanguageMap Map of values with language code as a key
     * @return Multi langauge field DTO values
     */
    public static List<LanguageDescriptionDto> convertMultiLanguageFromMapOfValues(Map<String, String> multiLanguageMap) {

        if (multiLanguageMap == null || multiLanguageMap.isEmpty()) {
            return null;
        }

        List<LanguageDescriptionDto> translationInfos = new ArrayList<>();

        for (Entry<String, String> translationInfo : multiLanguageMap.entrySet()) {
            translationInfos.add(new LanguageDescriptionDto(translationInfo.getKey(), translationInfo.getValue()));
        }

        return translationInfos;
    }
}