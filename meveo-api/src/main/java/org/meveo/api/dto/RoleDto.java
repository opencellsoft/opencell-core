package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;

@XmlRootElement(name = "Role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleDto extends BaseDto{

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required = true)
	private String name;
	
	private String description;
	
	private List<PermissionDto> permission = new ArrayList<PermissionDto>();
	
	
	public RoleDto() {
		
	}
	
	public RoleDto(Role role) {
		this.setName(role.getName());
		this.setDescription(role.getDescription());
		
		Set<Permission> permissions = role.getPermissions();
		
		if (permissions!=null && !permissions.isEmpty()) {
			List<PermissionDto> permissionDtos = new ArrayList<PermissionDto>();
			for (Permission p : permissions) {
				PermissionDto pd = new PermissionDto();
				pd.setName(p.getName());
				pd.setPermission(p.getPermission());
				pd.setResource(p.getResource());
				permissionDtos.add(pd);
			}
			this.setPermission(permissionDtos);
		}
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "RoleDto [name=" + name + ", description=" + description + "]";
	}

	public List<PermissionDto> getPermission() {
		return permission;
	}

	public void setPermission(List<PermissionDto> permission) {
		this.permission = permission;
	}

	
}
