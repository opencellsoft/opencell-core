package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;

/**
 * @author Edward P. Legaspi
 * 
 **/
@XmlRootElement(name = "Language")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDto extends BaseDto implements IEnableDto {

    private static final long serialVersionUID = 725968016559888810L;

    /**
     * Language code
     */
    @XmlAttribute(required = true)
    private String code;

    /**
     * Description
     */
    private String description;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

    public LanguageDto() {

    }

    public LanguageDto(TradingLanguage e) {
        code = e.getLanguageCode();
        description = e.getPrDescription();
        disabled = e.isDisabled();
    }

    public LanguageDto(Language e) {
        code = e.getLanguageCode();
        description = e.getDescriptionEn();
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

    @Override
    public String toString() {
        return "LanguageDto [code=" + code + ", description=" + description + ", disabled=" + disabled + "]";
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isDisabled() {
        return disabled;
    }
}