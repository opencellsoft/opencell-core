package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.PermissionsDto;

/**
 * The Class PermissionResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "SellerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The permissions dto. */
    private PermissionsDto permissionsDto = new PermissionsDto();

    /**
     * Gets the permissions dto.
     *
     * @return the permissions dto
     */
    public PermissionsDto getPermissionsDto() {
        return permissionsDto;
    }

    /**
     * Sets the permissions dto.
     *
     * @param permissionsDto the new permissions dto
     */
    public void setPermissionsDto(PermissionsDto permissionsDto) {
        this.permissionsDto = permissionsDto;
    }

    @Override
    public String toString() {
        return "PermissionResponseDto [permissionsDto=" + permissionsDto + "]";
    }
}