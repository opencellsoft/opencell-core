package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.model.shared.Title;

/**
 * The Class TitleDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Title")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1332916104721562522L;

    /** The is company. */
    private Boolean isCompany = Boolean.FALSE;

    /** The language descriptions. */
    private List<LanguageDescriptionDto> languageDescriptions;

    /**
     * Instantiates a new title dto.
     */
    public TitleDto() {

    }

    /**
     * Instantiates a new title dto.
     *
     * @param title the title entity
     */
    public TitleDto(Title title) {
        super(title);
        isCompany = title.getIsCompany();
    }

    /**
     * Gets the checks if is company.
     *
     * @return the checks if is company
     */
    public Boolean getIsCompany() {
        return isCompany;
    }

    /**
     * Sets the checks if is company.
     *
     * @param isCompany the new checks if is company
     */
    public void setIsCompany(Boolean isCompany) {
        this.isCompany = isCompany;
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

    @Override
    public String toString() {
        return "TitleDto [code=" + getCode() + ", description=" + getDescription() + ", isCompany=" + isCompany + "]";
    }
}