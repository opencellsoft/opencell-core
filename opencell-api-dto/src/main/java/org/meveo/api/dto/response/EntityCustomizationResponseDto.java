package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EntityCustomizationDto;

/**
 * The Class EntityCustomizationResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "EntityCustomizationResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomizationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1871967200014440842L;

    /** The entity customization. */
    private EntityCustomizationDto entityCustomization;

    /**
     * Gets the entity customization.
     *
     * @return the entity customization
     */
    public EntityCustomizationDto getEntityCustomization() {
        return entityCustomization;
    }

    /**
     * Sets the entity customization.
     *
     * @param entityCustomization the new entity customization
     */
    public void setEntityCustomization(EntityCustomizationDto entityCustomization) {
        this.entityCustomization = entityCustomization;
    }
}