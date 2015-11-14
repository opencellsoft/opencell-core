package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.security.Permission;

@XmlRootElement(name = "Permission")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionDto extends BaseDto {

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required = true)
	private String permission;
	@XmlAttribute(required = true)
	private String name;
	
	private String resource;
	
	public PermissionDto() {
		
	}
	
	public PermissionDto(Permission p) {
		if (p != null) {
			this.name = p.getName();
			this.permission = p.getPermission();
			this.resource = p.getResource();
		}
	}
	
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "PermissionDto [permission=" + permission + ", name=" + name
				+ ", resource=" + resource + "]";
	}
}
