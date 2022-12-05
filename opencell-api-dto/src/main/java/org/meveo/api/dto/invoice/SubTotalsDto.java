package org.meveo.api.dto.invoice;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageDescriptionDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "SubTotals")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubTotalsDto {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    @Schema(description = "The Sub Totals id")
    protected Long id;
    
    @Schema(description = "The Sub Totals code")
    private String code;
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /** EL expression. */
    @XmlElement()
    @Schema(description = "The EL Expression")
    private String el;
    
    /** Label translations. */
    @XmlElement()
    @Schema(description = "The Label Translations")
    private String label;
    
    /** Label translations. */
    private List<LanguageDescriptionDto> languageLabels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEl() {
        return el;
    }

    public void setEl(String el) {
        this.el = el;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<LanguageDescriptionDto> getLanguageLabels() {
        return languageLabels;
    }

    public void setLanguageLabels(List<LanguageDescriptionDto> languageLabels) {
        this.languageLabels = languageLabels;
    }
    
}
