package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class LanguagesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Languages")
@XmlAccessorType(XmlAccessType.FIELD)
public class LanguagesDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4455041168159380792L;

    /** The language. */
    private List<LanguageDto> language;

    /**
     * Gets the language.
     *
     * @return the language
     */
    public List<LanguageDto> getLanguage() {
        if (language == null)
            language = new ArrayList<LanguageDto>();
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(List<LanguageDto> language) {
        this.language = language;
    }

}
