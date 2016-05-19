package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.script.OfferModelScriptDto;
import org.meveo.model.module.MeveoModule;

@XmlRootElement(name = "BusinessOfferModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessOfferModelDto extends ModuleDto {

    private static final long serialVersionUID = -7023791262640948222L;

    @NotNull
    @XmlElement(required = true)
    private OfferTemplateDto offerTemplate;

    private OfferModelScriptDto script;

    public BusinessOfferModelDto() {
    }

    public BusinessOfferModelDto(MeveoModule module) {
        super(module);
    }

    public void setOfferTemplate(OfferTemplateDto offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public OfferTemplateDto getOfferTemplate() {
        return offerTemplate;
    }

    public void setScript(OfferModelScriptDto script) {
        this.script = script;
    }

    public OfferModelScriptDto getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "BusinessOfferModelDto [offerTemplate=" + offerTemplate + ", script=" + script + "]";
    }
}