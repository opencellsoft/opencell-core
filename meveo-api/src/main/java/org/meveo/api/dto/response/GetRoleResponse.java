package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.RoleDto;

@XmlRootElement(name = "GetRoleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetRoleResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private RoleDto roleDto;

	public RoleDto getRoleDto() {
		return roleDto;
	}

	public void setRoleDto(RoleDto roleDto) {
		this.roleDto = roleDto;
	}

	@Override
	public String toString() {
		return "GetRoleResponse [roleDto=" + roleDto + "]";
	}
}
