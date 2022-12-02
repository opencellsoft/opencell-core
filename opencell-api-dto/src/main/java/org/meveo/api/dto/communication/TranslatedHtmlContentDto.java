package org.meveo.api.dto.communication;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "translatedHtmlContent")
@XmlAccessorType(XmlAccessType.FIELD)
public class TranslatedHtmlContentDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4686792860854718893L;

    /** The language code. */
    @Schema(description = "The language code")
    private String languageCode;

    /** The description. */
    @Schema(description = "Subject Translation")
    private String htmlContent;

    public TranslatedHtmlContentDto() {

    }

    public TranslatedHtmlContentDto(String languageCode, String htmlContent) {
        this.languageCode = languageCode;
        this.htmlContent = htmlContent;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
