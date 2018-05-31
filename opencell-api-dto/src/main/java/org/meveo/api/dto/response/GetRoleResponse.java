package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.RoleDto;

/**
 * The Class GetRoleResponse.
 */
@XmlRootElement(name = "GetRoleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetRoleResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The role dto. */
    private RoleDto roleDto;

    /**
     * Gets the role dto.
     *
     * @return the role dto
     */
    public RoleDto getRoleDto() {
        return roleDto;
    }

    /**
     * Sets the role dto.
     *
     * @param roleDto the new role dto
     */
    public void setRoleDto(RoleDto roleDto) {
        this.roleDto = roleDto;
    }

    @Override
    public String toString() {
        return "GetRoleResponse [roleDto=" + roleDto + "]";
    }
}