package org.meveo.api.dto.hierarchy;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.hierarchy.HierarchyLevel;

/**
 * The Class UserHierarchyLevelDto.
 *
 * @author Phu Bach
 */
@XmlRootElement(name = "UserHierarchyLevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1332916104721562522L;

    /** The parent level. */
    private String parentLevel;

    /** The child levels. */
    @XmlElementWrapper(name = "childLevels")
    @XmlElement(name = "userHierarchyLevel")
    private List<UserHierarchyLevelDto> childLevels;

    /** The order level. */
    protected Long orderLevel = 0L;

    /**
     * Instantiates a new user hierarchy level dto.
     */
    public UserHierarchyLevelDto() {

    }

    /**
     * Instantiates a new user hierarchy level dto.
     *
     * @param level the HierarchyLevel
     */
    public UserHierarchyLevelDto(@SuppressWarnings("rawtypes") HierarchyLevel level) {
        super(level);
        orderLevel = level.getOrderLevel();
        if (level.getParentLevel() != null) {
            parentLevel = level.getParentLevel().getCode();
        }
    }

    /**
     * Gets the parent level.
     *
     * @return the parent level
     */
    public String getParentLevel() {
        return parentLevel;
    }

    /**
     * Sets the parent level.
     *
     * @param parentLevel the new parent level
     */
    public void setParentLevel(String parentLevel) {
        this.parentLevel = parentLevel;
    }

    /**
     * Gets the child levels.
     *
     * @return the child levels
     */
    public List<UserHierarchyLevelDto> getChildLevels() {
        return childLevels;
    }

    /**
     * Sets the child levels.
     *
     * @param childLevels the new child levels
     */
    public void setChildLevels(List<UserHierarchyLevelDto> childLevels) {
        this.childLevels = childLevels;
    }

    /**
     * Gets the order level.
     *
     * @return the order level
     */
    public Long getOrderLevel() {
        return orderLevel;
    }

    /**
     * Sets the order level.
     *
     * @param orderLevel the new order level
     */
    public void setOrderLevel(Long orderLevel) {
        this.orderLevel = orderLevel;
    }
}