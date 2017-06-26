package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionsDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<PermissionDto> permission = new ArrayList<PermissionDto>();

	public List<PermissionDto> getPermission() {
		return permission;
	}

	public void setPermission(List<PermissionDto> permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return "PermissionsDto [permission=" + permission + "]";
	}

	

}
