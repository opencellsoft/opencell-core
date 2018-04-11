package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetTradingLanguageResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTradingLanguageResponse extends BaseResponse {

    private static final long serialVersionUID = -1697478352703038101L;

    private LanguageDto language;

    public GetTradingLanguageResponse() {
        super();
    }

    public LanguageDto getLanguage() {
        return language;
    }

    public void setLanguage(LanguageDto language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "GetTradingLanguageResponse [language=" + language + "]";
    }

}
