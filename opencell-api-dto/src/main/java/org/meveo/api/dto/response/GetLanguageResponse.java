package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageDto;

/**
 * The Class GetLanguageResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 * @deprecated will be renammed to GetTradingLanguageResponse
 */
@XmlRootElement(name = "GetLanguageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetLanguageResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1697478352703038101L;

    /** The language. */
    private LanguageDto language;

    /**
     * Instantiates a new gets the language response.
     */
    public GetLanguageResponse() {
        super();
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public LanguageDto getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    public void setLanguage(LanguageDto language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "GetLanguageResponse [language=" + language + "]";
    }
}