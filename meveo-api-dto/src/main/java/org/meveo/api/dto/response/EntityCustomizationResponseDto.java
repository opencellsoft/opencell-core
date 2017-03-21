package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EntityCustomizationDto;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "EntityCustomizationResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomizationResponseDto extends BaseResponse {

    private static final long serialVersionUID = -1871967200014440842L;

    private EntityCustomizationDto entityCustomization;

    public EntityCustomizationDto getEntityCustomization() {
        return entityCustomization;
    }

    public void setEntityCustomization(EntityCustomizationDto entityCustomization) {
        this.entityCustomization = entityCustomization;
    }
}