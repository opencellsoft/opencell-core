package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;

/**
 * The Class LanguageDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Language")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguageDto extends AuditableEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
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

    /**
     * Instantiates a new language dto.
     */
    public LanguageDto() {

    }

    /**
     * Instantiates a new language dto.
     *
     * @param tradingLanguage the trading language
     */
    public LanguageDto(TradingLanguage tradingLanguage) {
        super(tradingLanguage);
        code = tradingLanguage.getLanguageCode();
        description = tradingLanguage.getPrDescription();
        disabled = tradingLanguage.isDisabled();
    }

    /**
     * Instantiates a new language dto.
     *
     * @param language the language
     */
    public LanguageDto(Language language) {
        super(language);
        code = language.getLanguageCode();
        description = language.getDescriptionEn();
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

    @Override
    public String toString() {
        return "LanguageDto [code=" + code + ", description=" + description + ", disabled=" + disabled + "]";
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }
}