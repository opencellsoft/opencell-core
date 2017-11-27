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
 * @author Phu Bach
 **/
@XmlRootElement(name = "UserHierarchyLevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelDto implements Serializable {

    private static final long serialVersionUID = -1332916104721562522L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute(required = false)
    private String description;

    private String parentLevel;

    @XmlElementWrapper(name = "childLevels")
    @XmlElement(name = "userHierarchyLevel")
    private List<UserHierarchyLevelDto> childLevels;

    protected Long orderLevel = 0L;

    public UserHierarchyLevelDto() {

    }

    public UserHierarchyLevelDto(@SuppressWarnings("rawtypes") HierarchyLevel level) {
        code = level.getCode();
        description = level.getDescription();
        orderLevel = level.getOrderLevel();
        if (level.getParentLevel() != null) {
            parentLevel = level.getParentLevel().getCode();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentLevel() {
        return parentLevel;
    }

    public void setParentLevel(String parentLevel) {
        this.parentLevel = parentLevel;
    }

    public List<UserHierarchyLevelDto> getChildLevels() {
        return childLevels;
    }

    public void setChildLevels(List<UserHierarchyLevelDto> childLevels) {
        this.childLevels = childLevels;
    }

    public Long getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(Long orderLevel) {
        this.orderLevel = orderLevel;
    }
}