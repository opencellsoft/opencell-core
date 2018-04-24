package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EntityCustomActionDto;

/**
 * The Class EntityCustomActionResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "EntityCustomActionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomActionResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3631110189107702332L;

    /** The entity action. */
    private EntityCustomActionDto entityAction;

    /**
     * Gets the entity action.
     *
     * @return the entity action
     */
    public EntityCustomActionDto getEntityAction() {
        return entityAction;
    }

    /**
     * Sets the entity action.
     *
     * @param entityAction the new entity action
     */
    public void setEntityAction(EntityCustomActionDto entityAction) {
        this.entityAction = entityAction;
    }
}