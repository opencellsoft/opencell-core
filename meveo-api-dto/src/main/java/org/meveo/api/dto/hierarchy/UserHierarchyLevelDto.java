package org.meveo.api.dto.hierarchy;

import org.meveo.model.hierarchy.UserHierarchyLevel;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Phu Bach
 **/
@XmlRootElement(name = "UserHierarchyLevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHierarchyLevelDto implements Serializable {

	private static final long serialVersionUID = -1332916104721562522L;

	@XmlAttribute(required = true)
	private String code;

    @XmlElement(required = true)
	private String description;

    private String parentLevel;

    private List<UserHierarchyLevelDto> childLevels;

    protected Long orderLevel = 0L;

	public UserHierarchyLevelDto() {

	}

	public UserHierarchyLevelDto(UserHierarchyLevel e) {
		code = e.getCode();
		description = e.getDescription();
        orderLevel = e.getOrderLevel();
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
