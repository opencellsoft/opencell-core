package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

@XmlRootElement(name = "Roles")
@XmlAccessorType(XmlAccessType.FIELD)
public class RolesDto extends SearchResponse {

    private static final long serialVersionUID = 1893591052731642142L;

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<RoleDto> roles = new ArrayList<>();

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}