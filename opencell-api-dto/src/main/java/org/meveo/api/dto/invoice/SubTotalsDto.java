package org.meveo.api.dto.invoice;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
    @XmlElement(required = true)
    @Schema(description = "The invoice type")
    private String el;
    
    /** Label translations. */
    @XmlElement(required = true)
    @Schema(description = "The invoice type")
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
