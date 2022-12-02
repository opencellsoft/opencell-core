package org.meveo.api.dto.communication;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "translatedSubject")
@XmlAccessorType(XmlAccessType.FIELD)
public class TranslatedSubjectDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4686792860854718893L;

    /** The language code. */
    @Schema(description = "The language code")
    private String languageCode;

    /** The description. */
    @Schema(description = "Subject Translation")
    private String subject;

    public TranslatedSubjectDto(){

    }

    public TranslatedSubjectDto(String languageCode, String subject) {
        this.languageCode = languageCode;
        this.subject = subject;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setTextContent(String subject) {
        this.subject = subject;
    }
}
