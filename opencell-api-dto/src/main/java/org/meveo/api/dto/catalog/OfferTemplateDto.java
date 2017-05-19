package org.meveo.api.dto.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferTemplateDto extends ProductOfferingDto {

    private static final long serialVersionUID = 9156372453581362595L;

    private String longDescription;

    private String bomCode;

    @Deprecated
    private String offerTemplateCategoryCode;

    @XmlElementWrapper(name = "offerServiceTemplates")
    @XmlElement(name = "offerServiceTemplate")
    private List<OfferServiceTemplateDto> offerServiceTemplates;

    @XmlElementWrapper(name = "offerProductTemplates")
    @XmlElement(name = "offerProductTemplate")
    private List<OfferProductTemplateDto> offerProductTemplates;

    public OfferTemplateDto() {

    }

    public String getBomCode() {
        return bomCode;
    }

    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    public String getOfferTemplateCategoryCode() {
        return offerTemplateCategoryCode;
    }

    public void setOfferTemplateCategoryCode(String offerTemplateCategoryCode) {
        this.offerTemplateCategoryCode = offerTemplateCategoryCode;
    }

    public List<OfferServiceTemplateDto> getOfferServiceTemplates() {
        return offerServiceTemplates;
    }

    public void setOfferServiceTemplates(List<OfferServiceTemplateDto> offerServiceTemplates) {
        this.offerServiceTemplates = offerServiceTemplates;
    }

    public List<OfferProductTemplateDto> getOfferProductTemplates() {
        return offerProductTemplates;
    }

    public void setOfferProductTemplates(List<OfferProductTemplateDto> offerProductTemplates) {
        this.offerProductTemplates = offerProductTemplates;
    }

    public boolean isCodeOnly() {
        return StringUtils.isBlank(description) && StringUtils.isBlank(bomCode) && StringUtils.isBlank(offerTemplateCategoryCode)
                && (offerServiceTemplates == null || offerServiceTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}