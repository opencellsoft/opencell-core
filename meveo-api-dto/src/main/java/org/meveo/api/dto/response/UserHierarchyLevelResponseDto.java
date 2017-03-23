package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;

/**
 * @author Phu Bach
 **/
@XmlRootElement(name = "UserHierarchyLevelResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelResponseDto extends BaseResponse {

	private static final long serialVersionUID = -1125948385137327401L;

	private UserHierarchyLevelDto userHierarchyLevel;

    public UserHierarchyLevelDto getUserHierarchyLevel() {
        return userHierarchyLevel;
    }

    public void setUserHierarchyLevel(UserHierarchyLevelDto userHierarchyLevel) {
        this.userHierarchyLevel = userHierarchyLevel;
    }
}
