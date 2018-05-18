package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageIsoDto;

/**
 * The Class GetLanguagesIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 */
@XmlRootElement(name = "GetLanguagesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLanguagesIsoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1697478352703038101L;

    /** The languages. */
    private List<LanguageIsoDto> languages;

    /**
     * Instantiates a new gets the languages iso response.
     */
    public GetLanguagesIsoResponse() {
        super();
    }

    /**
     * Gets the languages.
     *
     * @return the languages
     */
    public List<LanguageIsoDto> getLanguages() {
        return languages;
    }

    /**
     * Sets the languages.
     *
     * @param languages the new languages
     */
    public void setLanguages(List<LanguageIsoDto> languages) {
        this.languages = languages;
    }

    @Override
    public String toString() {
        return "GetLanguagesIsoResponse [languages=" + languages + "]";
    }
}