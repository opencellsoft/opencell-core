package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EntityActionScriptDto;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "EntityActionScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityActionScriptResponseDto extends BaseResponse {

    private static final long serialVersionUID = -3631110189107702332L;

    private EntityActionScriptDto entityAction;

    public EntityActionScriptDto getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(EntityActionScriptDto entityAction) {
        this.entityAction = entityAction;
    }
}