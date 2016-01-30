package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.PermissionsDto;

@XmlRootElement(name = "SellerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private PermissionsDto permissionsDto = new PermissionsDto();

	public PermissionsDto getPermissionsDto() {
		return permissionsDto;
	}

	public void setPermissionsDto(PermissionsDto permissionsDto) {
		this.permissionsDto = permissionsDto;
	}

	@Override
	public String toString() {
		return "PermissionResponseDto [permissionsDto=" + permissionsDto + "]";
	}
}
