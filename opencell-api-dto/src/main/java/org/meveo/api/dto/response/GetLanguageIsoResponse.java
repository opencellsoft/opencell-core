package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageIsoDto;

/**
 * The Class GetLanguageIsoResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 */
@XmlRootElement(name = "GetLanguageIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLanguageIsoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1697478352703038101L;

    /** The language. */
    private LanguageIsoDto language;

    /**
     * Instantiates a new gets the language iso response.
     */
    public GetLanguageIsoResponse() {
        super();
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public LanguageIsoDto getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(LanguageIsoDto language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "GetLanguageIsoResponse [language=" + language + "]";
    }
}