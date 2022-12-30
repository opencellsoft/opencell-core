package org.meveo.api.dto.cpq;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BaseEntityDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OfferContextDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferContextConfigDTO extends BaseEntityDto {

    @Schema(description = "Load related Tag of Attribute")
    private boolean loadAttributeTags = true;

    @Schema(description = "Load related GroupedAttribute of Attribute")
    private boolean loadAttributeGroupedAttribute = true;

    @Schema(description = "Load related ChargeTemplate of Attribute")
    private boolean loadAttributeChargeTemplates = false;

    @Schema(description = "Load related AssignedAttribute of Attribute")
    private boolean loadAttributeAssignedAttr = false;

    @Schema(description = "Load related Media of Attribute")
    private boolean loadAttributeMedia = false;

    public boolean isLoadAttributeTags() {
        return loadAttributeTags;
    }

    public void setLoadAttributeTags(boolean loadAttributeTags) {
        this.loadAttributeTags = loadAttributeTags;
    }

    public boolean isLoadAttributeGroupedAttribute() {
        return loadAttributeGroupedAttribute;
    }

    public void setLoadAttributeGroupedAttribute(boolean loadAttributeGroupedAttribute) {
        this.loadAttributeGroupedAttribute = loadAttributeGroupedAttribute;
    }

    public boolean isLoadAttributeChargeTemplates() {
        return loadAttributeChargeTemplates;
    }

    public void setLoadAttributeChargeTemplates(boolean loadAttributeChargeTemplates) {
        this.loadAttributeChargeTemplates = loadAttributeChargeTemplates;
    }

    public boolean isLoadAttributeAssignedAttr() {
        return loadAttributeAssignedAttr;
    }

    public void setLoadAttributeAssignedAttr(boolean loadAttributeAssignedAttr) {
        this.loadAttributeAssignedAttr = loadAttributeAssignedAttr;
    }

    public boolean isLoadAttributeMedia() {
        return loadAttributeMedia;
    }

    public void setLoadAttributeMedia(boolean loadAttributeMedia) {
        this.loadAttributeMedia = loadAttributeMedia;
    }
}
