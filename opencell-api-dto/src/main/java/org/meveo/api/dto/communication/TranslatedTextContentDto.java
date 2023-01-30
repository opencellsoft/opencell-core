package org.meveo.api.dto.communication;


import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public class TranslatedTextContentDto implements Serializable {

    /** The language code. */
    @Schema(description = "The language code")
    private String languageCode;

    /** The description. */
    @Schema(description = "Text content translation")
    private String textContent;

    public TranslatedTextContentDto(){

    }

    public TranslatedTextContentDto(String languageCode, String textContent) {
        this.languageCode = languageCode;
        this.textContent = textContent;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
