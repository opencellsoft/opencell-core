package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class RolesDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Roles")
@XmlAccessorType(XmlAccessType.FIELD)
public class RolesDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1893591052731642142L;

    /** The roles. */
    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<RoleDto> roles = new ArrayList<>();

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public List<RoleDto> getRoles() {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles the new roles
     */
    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}