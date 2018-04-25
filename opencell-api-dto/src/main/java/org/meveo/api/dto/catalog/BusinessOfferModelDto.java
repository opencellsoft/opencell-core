package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.module.MeveoModule;

/**
 * The Class BusinessOfferModelDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BusinessOfferModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessOfferModelDto extends MeveoModuleDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7023791262640948222L;

    /** The offer template. */
    @NotNull
    @XmlElement(required = true)
    private OfferTemplateDto offerTemplate;

    /**
     * Instantiates a new business offer model dto.
     */
    public BusinessOfferModelDto() {
    }

    /**
     * Instantiates a new business offer model dto.
     *
     * @param module the module
     */
    public BusinessOfferModelDto(MeveoModule module) {
        super(module);
    }

    /**
     * Sets the offer template.
     *
     * @param offerTemplate the new offer template
     */
    public void setOfferTemplate(OfferTemplateDto offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    /**
     * Gets the offer template.
     *
     * @return the offer template
     */
    public OfferTemplateDto getOfferTemplate() {
        return offerTemplate;
    }

    @Override
    public String toString() {
        return "BusinessOfferModelDto [offerTemplate=" + offerTemplate + "]";
    }
}