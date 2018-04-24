package org.meveo.api.dto.hierarchy;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.hierarchy.HierarchyLevel;

/**
 * The Class UserHierarchyLevelDto.
 *
 * @author Phu Bach
 */
@XmlRootElement(name = "UserHierarchyLevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1332916104721562522L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    @XmlAttribute(required = false)
    private String description;

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
        code = level.getCode();
        description = level.getDescription();
        orderLevel = level.getOrderLevel();
        if (level.getParentLevel() != null) {
            parentLevel = level.getParentLevel().getCode();
        }
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
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