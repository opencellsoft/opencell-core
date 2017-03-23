package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EntityCustomActionDto;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "EntityCustomActionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomActionResponseDto extends BaseResponse {

    private static final long serialVersionUID = -3631110189107702332L;

    private EntityCustomActionDto entityAction;

    public EntityCustomActionDto getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(EntityCustomActionDto entityAction) {
        this.entityAction = entityAction;
    }
}