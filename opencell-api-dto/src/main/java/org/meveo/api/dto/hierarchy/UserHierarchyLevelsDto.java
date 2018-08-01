package org.meveo.api.dto.hierarchy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class UserHierarchyLevelsDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "UserHierarchyLevels")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelsDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8948684323709076291L;

    /** The user hierarchy levels. */
    @XmlElementWrapper(name = "userHierarchyLevels")
    @XmlElement(name = "userHierarchyLevel")
    private List<UserHierarchyLevelDto> userHierarchyLevels = new ArrayList<>();

    /**
     * Gets the user hierarchy levels.
     *
     * @return the user hierarchy levels
     */
    public List<UserHierarchyLevelDto> getUserHierarchyLevels() {
        return userHierarchyLevels;
    }

    /**
     * Sets the user hierarchy levels.
     *
     * @param userHierarchyLevels the new user hierarchy levels
     */
    public void setUserHierarchyLevels(List<UserHierarchyLevelDto> userHierarchyLevels) {
        this.userHierarchyLevels = userHierarchyLevels;
    }
}