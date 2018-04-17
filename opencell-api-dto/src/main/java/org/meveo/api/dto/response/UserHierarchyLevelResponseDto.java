package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;

/**
 * The Class UserHierarchyLevelResponseDto.
 *
 * @author Phu Bach
 */
@XmlRootElement(name = "UserHierarchyLevelResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1125948385137327401L;

    /** The user hierarchy level. */
    private UserHierarchyLevelDto userHierarchyLevel;

    /**
     * Gets the user hierarchy level.
     *
     * @return the user hierarchy level
     */
    public UserHierarchyLevelDto getUserHierarchyLevel() {
        return userHierarchyLevel;
    }

    /**
     * Sets the user hierarchy level.
     *
     * @param userHierarchyLevel the new user hierarchy level
     */
    public void setUserHierarchyLevel(UserHierarchyLevelDto userHierarchyLevel) {
        this.userHierarchyLevel = userHierarchyLevel;
    }
}